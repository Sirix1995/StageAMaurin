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

/**
 * An instance of this class represents the union of a list of volumes
 * as defined by Constructive Solid Geometry.
 * 
 * @author Ole Kniemeyer
 */
public class CSGUnion extends UnionBase
{

	/**
	 * Creates a new <code>CSGUnion</code> whose list of volumes
	 * is empty.
	 */
	public CSGUnion ()
	{
	}

	/**
	 * Creates a new <code>CSGUnion</code> whose list of volumes
	 * is set to <code>[a, b]</code>.
	 */
	public CSGUnion (Volume a, Volume b)
	{
		volumes.add (a);
		volumes.add (b);
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		return computeIntersections (line, which == Intersection.ALL, list,
			excludeStart, excludeEnd, 1);
	}

	@Override
	public Volume operator$or (Volume v)
	{
		CSGUnion c = new CSGUnion ();
		c.volumes.addAll (volumes);
		c.volumes.add (v);
		return c;
	}

}
