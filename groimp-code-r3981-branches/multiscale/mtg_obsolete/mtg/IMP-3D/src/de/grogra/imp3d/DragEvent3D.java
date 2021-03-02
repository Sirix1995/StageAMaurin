
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

package de.grogra.imp3d;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A <code>DragEvent3D</code> represents a drag event in a 3D view.
 * 
 * @author Ole Kniemeyer
 */
public class DragEvent3D extends de.grogra.pf.ui.event.DragEvent
{
	/**
	 * The origin of the mouse ray. <code>origin</code>
	 * and <code>direction</code> describe a mouse ray in world
	 * coordinates emanating
	 * at the camera and going through the point to which the mouse points.
	 * <code>position</code> is not the position on the surface of
	 * an intersected object. 
	 */
	public final Point3d origin;

	/**
	 * The direction of the mouse ray.
	 * 
	 * @see #origin
	 */
	public final Vector3d direction;


	public DragEvent3D (Point3d origin, Vector3d direction)
	{
		this.origin = new Point3d (origin);
		this.direction = new Vector3d (direction);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString ()
			+ ",origin=" + origin + ",direction=" + direction;
	}

}
