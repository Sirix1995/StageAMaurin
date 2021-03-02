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

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.DefaultRayProcessor;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.ProcessorBase;
import de.grogra.ray2.tracing.RayProcessor;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Line;

/**
 * Although this class is implemented as antialiasing method it will not
 * perform any aliasing. Contrary it is used if no antialiasing is needed.
 * 
 * The method <code>getColorOfRectangle</code> will only return the color
 * of a single ray that is located at the center of the rectangle.
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public class NoAntialiasing extends ProcessorBase implements Antialiasing
{

	/**
	 * The renderer for which antialiasing is used.
	 */
	PixelwiseRenderer renderer;

	/**
	 * The scene which is rendered.
	 */
	Scene scene;

	/**
	 * The processor on which antialiasing operates.
	 */
	public RayProcessor processor;

	/**
	 * May be used internally.
	 */
	Environment env;

	/**
	 * This list may be used internally, it is initialized
	 * with length 1 in {@link #initialize}. 
	 */
	RayList list;

	/**
	 * May be used internally.
	 */
	Line line;

	/**
	 * May be used internally.
	 */
	Spectrum tmpSpectrum;

	float xPixel,yPixel;
	
	@Override
	protected void initLocals ()
	{
		line = new Line ();
		tmpSpectrum = scene.createSpectrum ();
		list = new RayList (tmpSpectrum);
		list.setSize (1);
		env = new Environment (scene.getBoundingBox (), tmpSpectrum, Environment.STANDARD_RAY_TRACER);
		env.localToGlobal.set (renderer.getCameraTransformation ());
		env.globalToLocal.m33 = 1;
		Math2.invertAffine (env.localToGlobal, env.globalToLocal);
	}

	public Antialiasing dup (Scene scene)
	{
		NoAntialiasing a = (NoAntialiasing) clone ();
		a.processor = processor.dup (scene);
		a.scene = scene;
		a.initLocals ();
		return a;
	}

	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		this.renderer = renderer;
		this.scene = scene;
		initLocals ();
		processor = (RayProcessor) renderer.getClassOption (
			PixelwiseRenderer.RAYPROCESSOR, new DefaultRayProcessor ());
		processor.initialize (renderer, scene);
	}
	

	

	public void getColorOfRectangle (double x, double y, double width,
			double height, Color4f color, Random random)
	{

		Ray ray = list.rays[0];
		env.uv.set ((float) (x + width * 0.5), (float) (y + height * 0.5));

		// generate a ray origin for env.uv
		renderer.getCamera ().generateRandomOrigins (env, list, random);
		tmpSpectrum.set (ray.spectrum);

		// copy generated ray origin to env 
		env.point.set (ray.origin);
		env.globalToLocal.transform (env.point, env.localPoint);

		// generate ray direction
		renderer.getCamera ().generateRandomRays (env, null, tmpSpectrum, list, false, random);
		
		line.x = xPixel;
		line.y = yPixel;
		
		line.start = 0;
		line.end = Double.POSITIVE_INFINITY;
		line.origin.set (ray.origin);
		line.direction.set (ray.direction);

		processor.initializeBeforeTracing(random);
		
		// trace the ray
		processor.getColorFromRay (line, ray.spectrum, color, random);
	}

	public void setPixelXY(float x, float y){
		xPixel = x;
		yPixel =y;
	}
	
	
	@Override
	protected void appendStatisticsImpl (StringBuffer stats)
	{
		processor.appendStatistics (stats);
	}

	
}
