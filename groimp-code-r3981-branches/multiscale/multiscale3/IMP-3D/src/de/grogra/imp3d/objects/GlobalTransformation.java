
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

package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import de.grogra.graph.*;
import de.grogra.imp.objects.Matrix4dAttribute;
import de.grogra.vecmath.Matrix34d;

public final class GlobalTransformation extends ObjectTreeAttribute<Matrix34dPair>
{
	public static final GlobalTransformation ATTRIBUTE
		= new GlobalTransformation ();

	private static final class IdM34dPair extends Matrix34d implements Matrix34dPair
	{
		IdM34dPair ()
		{
		}

		public Matrix34d get (boolean second)
		{
			return this;
		}
	}

	private static final class M34dPair extends Matrix34d implements Matrix34dPair
	{
		final IdM34dPair second = new IdM34dPair ();

		M34dPair ()
		{
		}

		public Matrix34d get (boolean second)
		{
			return second ? this.second : this;
		}
	}

	private static final IdM34dPair INITIAL_VALUE;
	
	static
	{
		INITIAL_VALUE = new IdM34dPair ();
		INITIAL_VALUE.setIdentity ();
	}

	private static final int MATRIX_POOL = GraphState.allocatePropertyId ();


	private GlobalTransformation ()
	{
		super (Matrix34dPair.class, false, null);
		initializeName ("de.grogra.imp3d.globalTransformation");
	}


	@Override
	protected Matrix34dPair derive (Object object, boolean asNode, Matrix34dPair parentValue,
			Matrix34dPair placeIn, GraphState gs)
	{
		Object t;
		if ((object != null)
			&& ((t = gs.getObjectDefault
				 (object, asNode, Attributes.TRANSFORMATION, this)) != this))
		{
			M34dPair pair = (M34dPair) placeIn;
			if (pair == null)
			{
				pair = new M34dPair ();
				pair.setIdentity ();
				pair.second.setIdentity ();
			}
			int i = gs.getInstancingPathIndex ();
			Matrix34d p = parentValue.get ((i <= 0) || asNode || !gs.getInstancingPath ().isInstancingEdge (i));
			Matrix4d[] m = (Matrix4d[]) gs.getUserProperty (MATRIX_POOL);
			if (m == null)
			{
				m = new Matrix4d[3];
				gs.setUserProperty (MATRIX_POOL, m);
				m[0] = new Matrix4d ();
				m[0].setIdentity ();
				m[1] = new Matrix4d ();
				m[1].setIdentity ();
				m[2] = new Matrix4d ();
				m[2].setIdentity ();
			}
			p.get (m[0]);
			((Transformation) t).preTransform (object, asNode, m[0], m[1], gs);
			pair.set (m[1]);
			((Transformation) t).postTransform (object, asNode, m[1], m[2], m[0], gs);
			pair.second.set (m[2]);
			return pair;
		}
		else
		{
			return (IdM34dPair) parentValue.get (true);
		}
	}


	@Override
	public boolean dependsOn (Attribute[] a)
	{
		return Attributes.TRANSFORMATION.isContained (a);
	}


	@Override
	protected Matrix34dPair getInitialValue (GraphState gs)
	{
		return INITIAL_VALUE;
	}


	/**
	 * Returns the global coordinate system of <code>object</code>.
	 * If <code>post</code> is <code>true</code>, the coordinate system
	 * behind the object is returned, which is the basis for the coordinate
	 * system of children, otherwise the method returns the coordinate system
	 * in which the shape of the object is defined.
	 * <p>
	 * The returned matrix must not be modified. 
	 * 
	 * @param object an object in the scene graph
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param gs the current graph state
	 * @param post include the post-transformation (behind the object)?
	 * @return global coordinate system, matrix must not be modified
	 */
	public static Matrix34d get (Object object, boolean asNode, GraphState gs, boolean post)
	{
		return ATTRIBUTE.getDerived (object, asNode, null, gs).get (post);
	}


	/**
	 * Returns the global coordinate system of the parent of <code>object</code>.
	 * If <code>post</code> is <code>true</code>, the coordinate system
	 * behind the parent is returned, which is the basis for the coordinate
	 * system of <code>object</code>, otherwise the method returns the coordinate system
	 * in which the shape of the parent is defined.
	 * <p>
	 * The returned matrix must not be modified. 
	 * 
	 * @param object an object in the scene graph
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param gs the current graph state
	 * @param post include the post-transformation (behind the parent)?
	 * @return global coordinate system of parent, matrix must not be modified
	 */
	public static Matrix34d getParentValue (Object object, boolean asNode, GraphState gs, boolean post)
	{
		return ATTRIBUTE.getParentValue (object, asNode, null, gs).get (post);
	}
}
