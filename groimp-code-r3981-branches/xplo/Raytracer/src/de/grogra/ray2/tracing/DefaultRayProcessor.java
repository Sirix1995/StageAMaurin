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
import de.grogra.ray2.light.DefaultLightProcessor;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;

public class DefaultRayProcessor extends RayProcessorBase
{
	private static final float MAX_VARIANCE = Shader.LAMBERTIAN_VARIANCE * 3 * 0.8f;
	private static final float MIN_WEIGHT = 0.03f;

	private long reflectedCount = 0;
	private long transmittedCount = 0;

	public DefaultRayProcessor ()
	{
		setLightProcessor (new DefaultLightProcessor ());
	}


	private Vector3f reflectedVariance;
	private Vector3f refractedVariance;
	private RayList rays;
	private Vector3f view;

	@Override
	protected void initLocals ()
	{
		super.initLocals ();
		reflectedVariance = new Vector3f ();
		refractedVariance = new Vector3f ();
		rays = new RayList (scene.createSpectrum ());
		view = new Vector3f ();
	}

	@Override
	int getEnvironmentType ()
	{
		return Environment.STANDARD_RAY_TRACER;
	}

	@Override
	float traceRay (int depth, Intersection desc,
			Spectrum weight, Tuple3d color, Locals loc, Random random)
	{
		Spectrum newWeight = loc.newWeight;
		Ray reflected_ray = loc.reflected;
		Ray refracted_ray = loc.transmitted;
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
			r.direction.negate ();
			r.direction.normalize ();
			loc.env.localToGlobal.set (scene.getLightTransformation (light));
			loc.env.globalToLocal.set (scene.getInverseLightTransformation (light));
			lt.computeExitance (loc.env, r.spectrum);
			lt.computeBSDF (loc.env, r.direction, r.spectrum, view, false, tmpSpectrum);

			tmpSpectrum.dot (weight, tmpColor);
			r.spectrum.set (tmpSpectrum);
			color.add (tmpColor);
		}

		rays.clear ();
		if (finite && (sh != null))
		{
			// direct illumination: for all light sources, choose a vertex on
			// light source and connect it with the current eye subpath
			lightProcessor.getLightRays (desc.line.direction.dot (desc
				.getNormal ()) < 0, desc, rays, loc.lightCache, random);
			// calculate color
			if (rays.size () > 0)
			{
				sh.shade (loc.env, rays, view, weight, tmpColor);
				color.add (tmpColor);
			}
		}

		//--- recursively trace reflected and refracted rays -------------------

		if (finite && (depth <= maxDepth))
		{
			tmpRay.origin.set (desc.getPoint ());

			if (sh != null)
			{
				sh.computeMaxRays (loc.env, view, weight, reflected_ray, reflectedVariance,
					refracted_ray, refractedVariance);
			}
			else
			{
				reflectedVariance.set (MAX_VARIANCE, MAX_VARIANCE, MAX_VARIANCE);
				reflected_ray.spectrum.setZero ();
				refractedVariance.set (0, 0, 0);;
				refracted_ray.direction.set (desc.line.direction);
				refracted_ray.spectrum.set (weight);
			}
			// [...]_ray.color represents the weight of this ray

			//-----------------------------------------------------------------
			// trace REFLECTED ray
			//-----------------------------------------------------------------
			float reflVarSum = reflectedVariance.x + reflectedVariance.y
				+ reflectedVariance.z;
			float refrVarSum = refractedVariance.x + refractedVariance.y
				+ refractedVariance.z;

			if ((reflVarSum < MAX_VARIANCE)
				&& (reflected_ray.spectrum.integrate () > MIN_WEIGHT))
			{
				reflectedCount++;

				newWeight.set (reflected_ray.spectrum);

				int ilistSize = ilist.size;

				tmpRay.direction.set (reflected_ray.direction);
				scene.computeIntersections (tmpRay, Intersection.CLOSEST,
					ilist, desc, null);
				if (ilist.size > ilistSize)
				{
					// intersection -> calculate color recursively

					int i = record (desc, true);
					traceRay (depth + 1, ilist.elements[ilistSize],
						newWeight, color, loc.nextReflected (), random);
					unrecord (desc, i);

					ilist.setSize (ilistSize);
				}

			}

			//-----------------------------------------------------------------
			// trace REFRACTED ray 
			//-----------------------------------------------------------------

			// weight is not too low and the variance is not too high 
			// -> calculate refracted ray
			if ((refrVarSum < MAX_VARIANCE)
				&& (refracted_ray.spectrum.integrate () > MIN_WEIGHT))
			{
				transmittedCount++;

				newWeight.set (refracted_ray.spectrum);

				int ilistSize = ilist.size;
				tmpRay.direction.set (refracted_ray.direction);
				scene.computeIntersections (tmpRay, Intersection.CLOSEST,
					ilist, desc, null);
				if (ilist.size > ilistSize)
				{
					// intersection -> calculate color recursively

					int i = record (desc, false);
					result = traceRay (depth + 1,
						ilist.elements[ilistSize], newWeight, color, loc
							.nextTransmitted (), random);
					unrecord (desc, i);
					ilist.setSize (ilistSize);
				}
				else
				{
					result = (float) newWeight.integrate ();
				}

			}
		}

		return result;
	}

	@Override
	protected void mergeStatistics (ProcessorBase src)
	{
		super.mergeStatistics (src);
		reflectedCount += ((DefaultRayProcessor) src).reflectedCount;
		transmittedCount += ((DefaultRayProcessor) src).transmittedCount;
	}

	@Override
	protected void appendStatisticsImpl (StringBuffer stats)
	{
		stats.append (Resources.msg ("rayprocessor.default.statistics",
			new Object[] {new Long (primaryCount), new Long (reflectedCount),
					new Long (transmittedCount)}));
		lightProcessor.appendStatistics (stats);
	}

}
