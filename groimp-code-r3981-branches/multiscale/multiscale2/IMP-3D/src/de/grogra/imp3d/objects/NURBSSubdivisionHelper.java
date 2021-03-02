
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

import de.grogra.xl.util.FloatList;

public abstract class NURBSSubdivisionHelper extends de.grogra.math.SubdivisionHelper
{
	final FloatList vertices;


	NURBSSubdivisionHelper (FloatList vertices, boolean rational)
	{
		super (rational);
		this.vertices = vertices;
	}


	int addVertex (float[] v, int i)
	{
		float x, y, z;
		int d = dimension;
		if (rational)
		{
			float w1 = 1 / v[i + d - 1];
			x = v[i] * w1;
			y = (d > 2) ? v[i + 1] * w1 : 0;
			z = (d > 3) ? v[i + 2] * w1 : 0;
		}
		else
		{
			x = v[i];
			y = (d > 1) ? v[i + 1] : 0;
			z = (d > 2) ? v[i + 2] : 0;
		}
		i = vertices.size;
		vertices.push (x).push (y).push (z);
		return i / 3;
	}

			
	public void setFlatness (float f)
	{
		flatness = f;
	}

}
