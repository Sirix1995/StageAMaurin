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

package de.grogra.ray2.tracing;

import java.util.Random;

import javax.vecmath.Color4f;

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Scene;
import de.grogra.vecmath.geom.Line;

/**
 * An implementation of this interface encapsulates the raytracing algorithm
 * for a single ray. Such an algorithm needs information about the whole scene,
 * this information is obtained through the
 * {@link de.grogra.ray2.tracing.PixelwiseRenderer} which is passed
 * to the <code>initialize</code> method. 
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public interface RayProcessor
{
	/**
	 * Returns a clone of this <code>RayProcessor</code>.
	 * All constant variables are copied shallowly, state variables
	 * are newly created and copied where necessary.
	 * 
	 * @param scene duplicate of scene 
	 * @return clone of this ray processor
	 */
	RayProcessor dup (Scene scene);

	/**
	 * With this method the processor is initialized with the scene
	 * and other information of a <code>PixelwiseRenderer</code>.
	 * 
	 * @param renderer the renderer which provides the needed information
	 * @param scene the scene which is rendered
	 */
	void initialize (PixelwiseRenderer renderer, Scene scene);

	
	/**
	 * With this method the processor is initialized with the Randomizer
	 * actually before starting the ray tracing.
	 *
	 * @param random pseudorandom generator 
	 */	
	public void initializeBeforeTracing (Random random);
	
	
	/**
	 * The main method of a ray processor. This method computes 
	 * a <code>color</code> for the specified <code>ray</code>.
	 * <code>color.w</code> contains
	 * the alpha value for the ray, the other components are
	 * premultiplied with this alpha value.
	 * 
	 * @param ray input - calculate for this ray
	 * @param resp responsivity of camera
	 * @param color output - the calculated color
	 * @param random pseudorandom generator 
	 */
	void getColorFromRay (Line ray, Spectrum resp, Color4f color, Random random);

	/**
	 * Appends some statistics information about the ray processing to
	 * <code>stats</code>. This method will be invoked after the whole
	 * rendering process has completed.
	 * 
	 * @param stats buffer for statistics information
	 */
	void appendStatistics (StringBuffer stats);
}
