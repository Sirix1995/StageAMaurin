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

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.light.DefaultLightProcessor;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;

/**
 * This class implements a physically correct path tracer.
 * The computation is done using the formulas for
 * bidirectional path tracing in Eric Veach's PhD thesis
 * "Robust Monte Carlo Methods for Light Transport Simulation". However
 * note that the implemented path tracer is a standard path tracer
 * which creates random paths starting at the eye and directly connects
 * each created vertex with all lights in the scene. So in the
 * terminology of bidirectional path tracing, only light subpaths
 * consisting of a single light vertex are considered (or zero light
 * vertices in case of a ray which hits a light surface).
 *  
 * @author Ole Kniemeyer
 */
public class PathTracer extends RayProcessorBase
{
	public static final String BRIGHTNESS = "pathtracer.brightness";

	private static final float MIN_WEIGHT = 0.01f;

	private long secondaryCount = 0;

	public PathTracer ()
	{
		DefaultLightProcessor p = new DefaultLightProcessor ();

		// make light processor physically correct 
		p.useGeometryTerm (true);
		
		p.useOneSamplePerLight (true);

		setLightProcessor (p);
	}

	private RayList rays;
	private Vector3f view;

	private float brightness;

	@Override
	protected void initLocals ()
	{
		super.initLocals ();
		rays = new RayList (scene.createSpectrum ());
		view = new Vector3f ();
	}

	@Override
	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		super.initialize (renderer, scene);
		brightness = renderer.getNumericOption (BRIGHTNESS, new Float (200))
			.floatValue ();
	}

	@Override
	float getBrightness ()
	{
		return brightness;
	}

	@Override
	int getEnvironmentType ()
	{
		return Environment.PATH_TRACER;
	}

	/* (non-Javadoc)
	 * 
	 * In this implementation, weight contains the values alpha_i^E
	 * for the current eye subpath (i = depth + 1), see formula 10.7
	 * in Veach's thesis.
	 */
	@Override
	float traceRay (int depth, Intersection desc, Spectrum weight,
			Tuple3d color, Locals loc, Random random)
	{
		if (PixelwiseRenderer.DEBUG_SUBPIXEL)
		{
			System.err.println ("traceRay " + depth + ' ' + desc + ' ' + weight);
		}
		Spectrum newWeight = loc.newWeight;
		Spectrum tmpSpectrum = loc.tmpSpectrum;
		Line tmpRay = loc.tmpRay;

		float result = 0;

		boolean finite = desc.parameter < Double.POSITIVE_INFINITY;
		
		view.set (desc.line.direction);
		view.negate ();
		assert Math.abs (view.lengthSquared () - 1) < 1e-4f;

		Shader sh = scene.getShader (desc.volume);
		int light = scene.getLight (desc.volume);
		Light lt = (light >= 0) ? scene.getLights ()[light] : null;

		loc.env.set (desc, ((sh != null) ? sh.getFlags () : 0) | ((lt != null) ? lt.getFlags () : 0), scene);

		loc.env.iorRatio = (float) getIOR (desc, weight);

		if ((lt != null) && !lt.isIgnoredWhenHit ())
		{
			// ray hits light source
			rays.setSize (1);
			Ray r = rays.rays[0];
			r.direction.set (desc.getNormal ());
			loc.env.localToGlobal.set (scene.getLightTransformation (light));
			loc.env.globalToLocal.set (scene.getInverseLightTransformation (light));
			lt.computeExitance (loc.env, r.spectrum);
			// In terms of Veach's thesis, the computed r.spectrum is the
			// product alpha_0^L c_0t, divided by the BSDF-factor of the light source.

			// multiply r.spectrum with the BSDF (=L^1). The
			// resulting tmpSpectrum is the value alpha_0^L c_0t
			lt.computeBSDF (loc.env, view, r.spectrum, r.direction, true, tmpSpectrum);

			tmpSpectrum.dot (weight, tmpColor);

			if (PixelwiseRenderer.DEBUG_SUBPIXEL)
			{
				System.err.println ("hit light source " + lt + " " + tmpColor);
			}
			
			// Now in terms of Veach's thesis, the computed dot product is the
			// product alpha_0^L c_0t alpha_t^E in formula 10.8 with t = depth + 1
			// (length of current eye subpath)

			// add contribution to color
			color.add (tmpColor);
		}

		if (finite && (sh != null))
		{
			rays.clear ();
	
			// direct illumination: for all light sources, choose a vertex on
			// light source and connect it with the current eye subpath
			lightProcessor.getLightRays (desc.line.direction.dot (desc
				.getNormal ()) < 0, desc, rays, loc.lightCache, random);
			// In terms of Veach's thesis, the computed spectra are the
			// values alpha_1^L c_1t, divided by the BSDF-factors and cosines of the surface.
			// These factors are considered below. 

			// calculate color
			for (int i = rays.size () - 1; i >= 0; i--)
			{
				// multiply ray spectra with the BSDF and cos(theta)-factor. The
				// resulting tmpSpectrum is the value alpha_1^L c_1t
				sh.computeBSDF (loc.env, view, rays.rays[i].spectrum, rays.rays[i].direction, true, tmpSpectrum);
				
				tmpSpectrum.dot (weight, tmpColor);
	
				// Now in terms of Veach's thesis, the computed dot product is the
				// product alpha_1^L c_1t alpha_t^E in formula 10.8 with t = depth + 1
				// (length of current eye subpath)
	
				if (PixelwiseRenderer.DEBUG_SUBPIXEL)
				{
					System.err.println ("contribution of light " + i + " " + tmpColor);
					System.err.println ("    " + rays.rays[i] + " " + loc.env);
				}

				// add contribution to color
				color.add (tmpColor);
			}
		}

		//--- recursively trace reflected and refracted rays -------------------

		if (finite && (depth <= maxDepth))
		{
			tmpRay.origin.set (desc.getPoint ());

			rays.setSize (1);
			if (sh != null)
			{
				sh.generateRandomRays (loc.env, view, weight, rays, false, random);
			}
			else
			{
				rays.rays[0].direction.set (desc.line.direction);
				rays.rays[0].spectrum.set (weight);
			}

			// generateRandomRay multiplied weight by the factor given
			// in formula 10.7 of Veach's thesis, newWeight is the new
			// alpha value
			newWeight.set (rays.rays[0].spectrum);

			if (newWeight.integrate () > MIN_WEIGHT)
			{
				boolean reflected = rays.rays[0].reflected;
				secondaryCount++;

				int ilistSize = ilist.size;

				tmpRay.direction.set (rays.rays[0].direction);
				scene.computeIntersections (tmpRay, Intersection.CLOSEST,
					ilist, desc, null);
				if (ilist.size > ilistSize)
				{
					// intersection -> calculate color recursively

					int i = record (desc, reflected);
					result = traceRay (depth + 1, ilist.elements[ilistSize],
						newWeight, color, loc.nextReflected (), random);
					unrecord (desc, i);

					ilist.setSize (ilistSize);
				}
				else
				{
					result = (float) newWeight.integrate ();
				}
				if (reflected)
				{
					result = 0;
				}
			}
		}

		if (PixelwiseRenderer.DEBUG_SUBPIXEL)
		{
			System.err.println ("traceRay " + depth + ' ' + color);
		}

		return result;
	}

	@Override
	protected void mergeStatistics (ProcessorBase src)
	{
		super.mergeStatistics (src);
		secondaryCount += ((PathTracer) src).secondaryCount;
	}

	@Override
	protected void appendStatisticsImpl (StringBuffer stats)
	{
		stats.append (Resources.msg ("rayprocessor.pathtracer.statistics",
			new Object[] {new Long (primaryCount), new Long (secondaryCount)}));
		lightProcessor.appendStatistics (stats);
	}

}
