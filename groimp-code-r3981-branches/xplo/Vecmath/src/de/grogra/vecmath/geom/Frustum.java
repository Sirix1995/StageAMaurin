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
 * This class represents the geometry of a frustum. It inherits
 * all properties from the superclass <code>Cone</code>. However,
 * the geometry is modified such that the part from the
 * cone's tip up to the plane at z=1 is removed. I.e., the frustum
 * is the intersection of the cone and the half-space above
 * the plane z=1.
 * 
 * @author Ole Kniemeyer
 */
public class Frustum extends Cone
{
	public boolean topOpen;
	
	@Override
	public double getTop ()
	{
		return 1;
	}

	@Override
	public boolean isTopOpen ()
	{
		return topOpen;
	}
	
}
