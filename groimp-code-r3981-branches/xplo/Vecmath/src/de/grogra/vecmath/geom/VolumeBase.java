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

import javax.vecmath.Tuple3d;

/**
 * This abstract class implements the operator and id methods of the
 * interface <code>Volume</code>. It can be used as base class
 * for volumes. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class VolumeBase implements Volume
{
	/**
	 * The id of this volume.
	 */
	private int id;

	public void setId (int id)
	{
		this.id = id;
	}

	public int getId ()
	{
		return id;
	}

	public Volume operator$com ()
	{
		return new CSGComplement (this);
	}

	public Volume operator$or (Volume v)
	{
		return new CSGUnion (this, v);
	}

	public Volume operator$and (Volume v)
	{
		return new CSGIntersection (this, v);
	}

	public Volume operator$sub (Volume v)
	{
		return new CSGDifference (this, v);
	}

	static boolean boxContainsTriangle (Tuple3d min, Tuple3d max, Tuple3d a,
			Tuple3d b, Tuple3d c)
	{
		return false;
	}

	protected boolean addConvexIntersections (double u0, int f0, double u1,
			int f1, boolean solid, Line line, int which, IntersectionList list,
			Intersection excludeStart, Intersection excludeEnd)
	{
		if ((excludeStart != null) && (excludeStart.volume == this))
		{
			// line.start is on the surface of this volume
			// so either u0 or u1 equals line.start
			if (line.start < 0.5 * (u0 + u1))
			{
				// u0 equals line.start
				if ((u1 <= line.end) && (f1 >= 0))
				{
					// line leaves this volume at u1
					list.add (this, line, u1, solid ? Intersection.LEAVING
							: Intersection.PASSING, f1);
				}
				return solid;
			}
			else
			{
				// u1 equals line.start
				return false;
			}
		}

		if ((excludeEnd != null) && (excludeEnd.volume == this))
		{
			// line.end is on the surface of this volume
			// so either u0 or u1 equals line.end
			if (line.end > 0.5 * (u0 + u1))
			{
				// u1 equals line.end
				if (u0 >= line.start)
				{
					// line enters this volume at u0
					if (f0 >= 0)
					{
						list.add (this, line, u0, solid ? Intersection.ENTERING
								: Intersection.PASSING, f0);
					}
					return false;
				}
				else
				{
					// line starts within volume
					return solid;
				}
			}
			else
			{
				// u0 equals line.end
				return false;
			}
		}

		if ((u0 > line.end) || (u1 < line.start))
		{
			return false;
		}

		boolean enter0 = u0 >= line.start;
		if (enter0 && (f0 >= 0))
		{
			list.add (this, line, u0, solid ? Intersection.ENTERING
					: Intersection.PASSING, f0);
			if (which != Intersection.ALL)
			{
				return false;
			}
		}
		if ((u1 <= line.end) && (f1 >= 0))
		{
			list.add (this, line, u1, solid ? Intersection.LEAVING
					: Intersection.PASSING, f1);
		}

		return !enter0;

	}
}
