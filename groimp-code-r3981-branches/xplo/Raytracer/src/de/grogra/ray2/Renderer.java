
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

package de.grogra.ray2;

import java.awt.image.ImageObserver;

import javax.vecmath.Matrix4d;

import de.grogra.ray.physics.Sensor;

/**
 * This interface specifies the basic operations for a
 * renderer. 
 * 
 * @author Ole Kniemeyer
 */
public interface Renderer
{
	/**
	 * Initializes the renderer. This method has to be invoked
	 * at first.
	 * 
	 * @param opts options to use (may be <code>null</code>)
	 * @param progress monitor to display rendering progress
	 * (may be <code>null</code>)
	 */
	void initialize (Options opts, ProgressMonitor progress);

	/**
	 * Renders an image of a <code>scene</code>.
	 * 
	 * @param scene scene to render
	 * @param camera camera to use
	 * @param cameraTransformation transformation from camera coordinates
	 * to world coordinates
	 * @param width width of image
	 * @param height height of image
	 * @param obs the observer receives the image data
	 */
	void render (Scene scene, Sensor camera, Matrix4d cameraTransformation,
			int width, int height, ImageObserver obs);

	/**
	 * This method is invoked from another thread to stop a running
	 * renderer. The renderer should stop rendering as early as possible.
	 * However, there is no guarantee on the time span between
	 * <code>stop</code> and the return from the invocation
	 * of <code>render</code>.
	 */
	void stop ();
}
