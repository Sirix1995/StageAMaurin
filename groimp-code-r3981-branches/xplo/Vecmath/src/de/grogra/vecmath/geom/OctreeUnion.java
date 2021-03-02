/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.vecmath.geom;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import de.grogra.vecmath.Math2;

/**
 * An <code>OctreeUnion</code> behaves like a
 * {@link de.grogra.vecmath.geom.SimpleUnion}, but has a better performance
 * due to the intersection acceleration by an octree. After all
 * component volumes have been added to the union,
 * {@link #initialize} has to be invoked in order to construct the
 * octree.
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public class OctreeUnion extends UnionBase
{
	/**
	 * List of all infinite volumes. These cannot be included
	 * in the octree.
	 */
	final ArrayList<Volume> infiniteObjects = new ArrayList<Volume> ();

	/**
	 * The cell iterator which is used to traverse the octree
	 * along a line.
	 */
	private CellIterator cellIterator;

	/**
	 * The maximum of the ids of the finite volumes. 
	 */
	private int maxId;

	static final class StateImpl extends Octree.State
	{
		/**
		 * This array keeps track for which volume intersections
		 * have been computed in {@link OctreeUnion#computeIntersections}.
		 * The indices are the ids of the volumes.
		 */
		boolean[] intersectedVolumes;

		@Override
		public boolean mark (int id)
		{
			if (intersectedVolumes[id])
			{
				return false;
			}
			else
			{
				intersectedVolumes[id] = true;
				return true;
			}
		}

		@Override
		public void clear (int id)
		{
			intersectedVolumes[id] = false;
		}
	}

	static final class CellImpl extends Octree.Cell
	{
		ArrayList<Volume> volumes = new ArrayList<Volume> ();

		CellImpl (int position)
		{
			super (position);
		}

		@Override
		protected Octree.Cell createChild (int position)
		{
			return new CellImpl (position);
		}

		@Override
		public int getVolumeCount ()
		{
			return (volumes != null) ? volumes.size () : 0;
		}

		@Override
		public void clearVolumes ()
		{
			volumes = null;
		}

		@Override
		public Volume getVolume (int index, Octree.State state)
		{
			return volumes.get (index);
		}

		@Override
		public void addVolume (Volume v)
		{
			volumes.add (v);
		}

		@Override
		void finish (Octree tree)
		{
			super.finish (tree);
			if (volumes != null)
			{
				volumes.trimToSize ();
			}
		}
	}

	private final Octree octree = new Octree ()
	{
		@Override
		public State createState ()
		{
			StateImpl state = new StateImpl ();
			state.intersectedVolumes = new boolean[maxId + 1];
			state.cellIterator = cellIterator.dup ();
			return state;
		}

		@Override
		protected ArrayList getInfiniteVolumes ()
		{
			return infiniteObjects;
		}
	};

	
	public Octree getOctree ()
	{
		return octree;
	}

	/**
	 * Initializes the octree. This method has to be invoked after all
	 * volumes have been added to this union, but before any of the
	 * other methods are invoked.
	 * 
	 * @param maxDepth maximum allowed depth of octree
	 * @param minObjects minimum volumes per cell. Only cells having
	 * more than <code>minObjects</code> volumes are checked for
	 * subdivision
	 * @param iterator the cell iterator to use
	 */
	public void initialize (int maxDepth, int minObjects, CellIterator iterator)
	{
		infiniteObjects.clear ();
		this.cellIterator = iterator;

		Point3d min = new Point3d (Double.MAX_VALUE, Double.MAX_VALUE,
			Double.MAX_VALUE);
		Point3d max = new Point3d (-Double.MAX_VALUE, -Double.MAX_VALUE,
			-Double.MAX_VALUE);
		Point3d minTmp = new Point3d ();
		Point3d maxTmp = new Point3d ();

		Variables temp = new Variables ();

		ArrayList finiteObjects = new ArrayList ();

		maxId = 0;
		for (int i = 0; i < volumes.size (); i++)
		{
			Volume v = volumes.get (i);
			v.getExtent (minTmp, maxTmp, temp);
			if ((minTmp.x == Double.NEGATIVE_INFINITY)
				|| (minTmp.y == Double.NEGATIVE_INFINITY)
				|| (minTmp.z == Double.NEGATIVE_INFINITY)
				|| (maxTmp.x == Double.POSITIVE_INFINITY)
				|| (maxTmp.y == Double.POSITIVE_INFINITY)
				|| (maxTmp.z == Double.POSITIVE_INFINITY))
			{
				infiniteObjects.add (v);
			}
			else
			{
				maxId = Math.max (maxId, v.getId ());
				finiteObjects.add (v);
				Math2.min (min, minTmp);
				Math2.max (max, maxTmp);
			}
		}
		
		if (finiteObjects.isEmpty ())
		{
			min.set (0, 0, 0);
			max.set (1, 1, 1);
		}
//*
		// make cubic scene bounding
		double cubeLength = max.distanceLinf (min);

		minTmp.add (min, max);
		minTmp.scale (0.5);

		maxTmp.set (cubeLength, cubeLength, cubeLength);

		min.scaleAdd (-0.5001, maxTmp, minTmp);
		max.scaleAdd (0.5001, maxTmp, minTmp);
//*/
		CellImpl root = new CellImpl (Octree.Cell.ROOT_POSITION);
		root.volumes = finiteObjects;

		octree.initialize (maxDepth, minObjects, min, max, root);
		
		cellIterator.initialize (octree);
	}

	
	public void addInfiniteVolume (Volume v)
	{
		infiniteObjects.add (v);
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		Octree.State s = (Octree.State) list.cache.get (this);
		if (s == null)
		{
			s = octree.createState ();
			list.cache.put (this, s);
		}
		return octree.computeIntersections (line, which, list, excludeStart,
			excludeEnd, s);
	}


	@Override
	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (octree.getMin ());
		max.set (octree.getMax ());
	}

}
