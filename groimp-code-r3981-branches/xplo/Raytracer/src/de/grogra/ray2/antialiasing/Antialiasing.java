
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

package de.grogra.ray2.antialiasing;

import java.util.Random;

import javax.vecmath.Color4f;

import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.PixelwiseRenderer;

/**
 * An implementation of this interface encapsulates a single antialiasing
 * method that is based on prefiltering.
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public interface Antialiasing
{
	/**
	 * Returns a clone of this <code>Antialiasing</code>.
	 * All constant variables are copied shallowly, state variables
	 * are newly created and copied where necessary.
	 * 
	 * @param scene duplicate of scene 
	 * @return clone of this antialiasing
	 */
	Antialiasing dup (Scene scene);

	/**
	 * Initializes the antialiasing method using the data available
	 * through the <code>renderer</code>.
	 * 
	 * @param renderer the renderer which provides the needed information
	 * @param scene the scene which is rendered
	 */
	void initialize (PixelwiseRenderer renderer, Scene scene);

	/**
	 * This method has to return a color value determined for a given
	 * rectangular region on the image plane. The coordinates are understood
	 * as {@link de.grogra.ray.physics.Environment#uv uv} coordinates for the
	 * {@link de.grogra.ray.physics.Sensor} which represents the camera. 
	 * 
	 * @param x Describes the x position of the lower left corner of 
	 * the rectangle.
	 * @param y Describes the y position of the lower left corner of 
	 * the rectangle.
	 * @param width width of the rectangle 
	 * @param height height of the of the rectangle 
	 * @param color The determined color of the rectangle will be
	 * stored in this parameter.
	 * @param random pseudorandom generator 
	 */
	void getColorOfRectangle (double x, double y, double width, double height,
			Color4f color, Random random);

	/**
	 * Appends some statistics information about the antialiasing to
	 * <code>stats</code>. This method will be invoked after the whole
	 * rendering process has completed. It should also invoke
	 * <code>appendStatistics</code> on the ray processor.
	 * 
	 * @param stats buffer for statistics information
	 */
	void appendStatistics (StringBuffer stats);

	
	public void setPixelXY(float x, float y);
	
}
