
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

package de.grogra.ray.physics;

import javax.vecmath.*;


import de.grogra.ray.util.Ray;

/**
 * An <code>Interior</code> instance represents the interior of a solid
 * object. The interior defines the index of refraction and is
 * responsible for light ray attenuation.
 * 
 * @author Ole Kniemeyer
 */
public interface Interior
{
	/**
	 * This class serves as input to
	 * {@link Interior#attenuate(Input, Color3f)}.
	 */
	final class Input
	{
		/**
		 * The light ray.
		 */
		public final Ray ray;
		
		/**
		 * The end point of the light ray in global coordinates.
		 */
		public final Point3f globalEnd = new Point3f ();
		
		/**
		 * The origin of the light ray in local coordinates.
		 */
		public final Point3f localOrigin = new Point3f ();
		
		/**
		 * The end point of the light ray in local coordinates.
		 */
		public final Point3f localEnd = new Point3f ();
		
		public Input (Spectrum factory)
		{
			ray = new Ray (factory);
		}
		
	}


	/**
	 * Returns the index of refraction of the interior. The computed
	 * index of refraction has to be averaged over the <code>spectrum</code>.
	 * 
	 * @param spectrum spectrum for which the average IOR has to be computed
	 * 
	 * @return index of refraction
	 */
	float getIndexOfRefraction (Spectrum spectrum);


	/**
	 * Calculates the attenuation of a light ray as it travels through this
	 * interior.
	 * @param in the input data
	 * @param outColor the outgoing color of the light ray will be placed in here
	 */
	void attenuate (Input in, Color3f outColor);
}
