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
 * This abstract class is the base class for unions of volumes.
 * 
 * @author Ole Kniemeyer
 */
public abstract class UnionBase extends CompoundVolume
{

	public boolean contains (Tuple3d point, boolean open)
	{
		for (int i = 0; i < volumes.size (); i++)
		{
			if (volumes.get (i).contains (point, open))
			{
				return true;
			}
		}
		return false;
	}


	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;
		for (int i = volumes.size () - 1; i >= 0; i--)
		{
			volumes.get (i).getExtent (min, max, temp);
			if (minX > min.x)
			{
				minX = min.x;
			}
			if (minY > min.y)
			{
				minY = min.y;
			}
			if (minZ > min.z)
			{
				minZ = min.z;
			}
			if (maxX < max.x)
			{
				maxX = max.x;
			}
			if (maxY < max.y)
			{
				maxY = max.y;
			}
			if (maxZ < max.z)
			{
				maxZ = max.z;
			}
		}
		min.set (minX, minY, minZ);
		max.set (maxX, maxY, maxZ);
	}


}
