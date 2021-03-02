
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

import javax.vecmath.*;

public abstract class CameraBase
{

	/**
	 * Computes an estimate for the scaling factor from 3D
	 * world coordinates at <code>(x, y, z)</code> to 2D coordinates
	 * on the camera plane.
	 * 
	 * @param x x world coordinate
	 * @param y y world coordinate
	 * @param z z world coordinate
	 * @return scaling factor of camera projection at location
	 */
	public abstract float getScaleAt (double x, double y, double z);


	/**
	 * Computes an estimate for the scaling factor from view coordinates
	 * around a depth of <code>z</code> to 2D coordinates
	 * on the camera plane.
	 * 
	 * @param z z view coordinate
	 * @return scaling factor of camera projection at location
	 */
	public abstract float getScaleAt (float z);


	/**
	 * Determines the ray which starts at the camera and goes through
	 * the specified point on the image plane of the camera.
	 * 
	 * @param x the x-coordinate on the image plane
	 * @param y the y-coordinate on the image plane
	 * @param origin the origin of the ray, in world coordinates
	 * @param direction the direction of the ray, in world coordinates
	 */
	public abstract void getRay (float x, float y,
								 Point3d origin, Vector3d direction);


	public abstract float getZNear ();


	public abstract float getZFar ();


	/**
	 * Returns the world-to-view transformation of this camera. It transforms
	 * world coordinates to view coordinates (= camera coordinates).  
	 * 
	 * @return world-to-view transformation
	 */
	public abstract Matrix4d getWorldToViewTransformation ();

}
