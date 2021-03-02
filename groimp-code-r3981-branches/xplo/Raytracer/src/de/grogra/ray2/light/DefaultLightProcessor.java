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

package de.grogra.ray2.light;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.ProcessorBase;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.Volume;

/**
 * This class implements a standard <code>LightProcessor</code>.
 * For lights which are not shadowless, it shoots a shadow ray
 * in order to check if the light source is visible at a specific
 * point. If this ray hits any object, the point is considered
 * to be in the shadow of the light.
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public class DefaultLightProcessor extends ProcessorBase implements
		LightProcessor
{
	private static class LightEntry
	{
		double power;

		/**
		 * The light
		 */
		Light light;

		/**
		 * This is used as parameter to the <code>Light</code> methods
		 * which are invoked on {@link #light}.
		 */
		Environment env;

		LightSample[] samples;

		LightEntry dup ()
		{
			LightEntry e = new LightEntry ();
			e.power = power;
			e.light = light;
			e.samples = samples;
			e.env = new Environment (env.bounds, env.tmpSpectrum0, env.type);
			e.env.globalToLocal.set (env.globalToLocal);
			e.env.localToGlobal.set (env.localToGlobal);
			return e;
		}
	}

	/**
	 * A <code>LightSample</code> stores information about a single
	 * sample of a light source.
	 * 
	 * @author Ole Kniemeyer
	 */
	private static class LightSample
	{
		int lightEntry;

		/**
		 * The location of the light in global coordinates
		 */
		Point3d origin;

		/**
		 * The color of the light
		 */
		Spectrum color;

		/**
		 * Only for directional lights: The light direction
		 */
		Vector3d direction;
	}

	private static class CacheEntry
	{
		Volume shading;

		Environment shadingEnvironment;

		ArrayList<Environment> environments = new ArrayList<Environment> ();
	}

	/**
	 * Contains the list of {@link LightEntry} instances.
	 */
	private LightEntry[] lightEntries;

	/**
	 * Defines the number of samples which should be created for an area light.
	 */
	private int samplesForAreaLight;

	/**
	 * Defines the number of samples which should be created for a sky light.
	 */
	private int samplesForSkyLight;

	/**
	 * The scene which defines the geometry to use for shadow rays
	 */
	private Scene scene;

	/**
	 * This instance is used as parameter to intersection computations.
	 */
	private IntersectionList list;

	/**
	 * This instance is used as parameter to intersection computations.
	 */
	private Line line;

	/**
	 * Counts the total number of shadow tests
	 */
	private long shadowTestCount = 0;

	/**
	 * Counts the number of succeeded shadow tests (i.e., where the point
	 * is in shadow of a light)
	 */
	private long shadowedCount = 0;

	/**
	 * Counts the number of shadow tests which succeeded by the usage
	 * of the cache
	 */
	private long shadowCacheSuccessCount = 0;

	private Vector3f negatedDirection;

	private Spectrum tmpSpectrum;
	
	private Spectrum id;

	private boolean geometryTerm = false;

	private boolean alwaysOneSample = false;
	
	private RayList singleRay;
	private LightSample singleSample;

	private int raytracerType;

	private double totalPower;

	public void useGeometryTerm (boolean use)
	{
		geometryTerm = use;
	}

	public void useOneSamplePerLight (boolean one)
	{
		alwaysOneSample = one;
	}

	public LightProcessor dup (Scene scene)
	{
		DefaultLightProcessor p = (DefaultLightProcessor) clone ();
		p.scene = scene;
		p.initLocals ();

		LightEntry[] a = new LightEntry[lightEntries.length];
		for (int i = 0; i < a.length; i++)
		{
			a[i] = lightEntries[i].dup ();
		}
		p.lightEntries = a;
		return p;
	}

	public void initialize (Scene scene, int raytracerType, Random rnd)
	{
		this.scene = scene;
		id = scene.createSpectrum ();
		id.setIdentity ();
		initLocals ();
		this.raytracerType = raytracerType;
		if (raytracerType == Environment.STANDARD_RAY_TRACER)
		{
			samplesForAreaLight = 50;
			samplesForSkyLight = 100;
		}
		else
		{
			samplesForAreaLight = 4;
			samplesForSkyLight = 8;
		}
		line.start = 0;
		totalPower = 0;
		Light[] lights = scene.getLights ();
		RayList rays = new RayList (id);
		ArrayList<LightSample> samples = new ArrayList<LightSample> ();
		Vector3f sum = new Vector3f ();
		Vector3f squaredSum = new Vector3f ();
		Vector3f tmpVector = new Vector3f ();
		ArrayList<LightEntry> lightEntries = new ArrayList<LightEntry> ();
		for (int i = 0; i < lights.length; i++)
		{
			int type = lights[i].getLightType ();
			if ((type == Light.AMBIENT) || (type == Light.NO_LIGHT))
			{
				continue;
			}

			Environment e = new Environment (scene.getBoundingBox (), id, raytracerType);
			e.localToGlobal.set (scene.getLightTransformation (i));
			e.globalToLocal.set (scene.getInverseLightTransformation (i));

			samples.clear ();
			double power = lights[i].getTotalPower (e);
			if (!(power > 0))
			{
				continue;
			}
			totalPower += power;
			
			LightEntry le = new LightEntry ();
			le.light = lights[i];
			le.env = e;

			sum.set (0, 0, 0);
			squaredSum.set (sum);
			boolean onlyOne = (type == Light.DIRECTIONAL) || (type == Light.POINT);
			boolean genRays = (type == Light.DIRECTIONAL) || (type == Light.SKY);
			double delta = (genRays ? 3 : Math.PI * e.boundsRadius * e.boundsRadius) * 1e-5;
			if (!onlyOne && alwaysOneSample)
			{
				LightSample ls = new LightSample ();
				ls.lightEntry = lightEntries.size ();
				samples.add (ls);
			}
			else
			{
				int n = onlyOne ? 1 : 50;
				int count;
				do
				{
					rays.setSize (n);
		
					if (genRays)
					{
						lights[i].generateRandomRays (e, null, id, rays, true, rnd);
					}
					else
					{
						lights[i].generateRandomOrigins (e, rays, rnd);
					}
		
					// now add the generated rays to samples 
					for (int j = 0; j < n; j++)
					{
						LightSample ls = new LightSample ();
						ls.lightEntry = lightEntries.size ();
						ls.origin = new Point3d (rays.rays[j].origin);
						ls.color = rays.rays[j].spectrum.clone ();
						Tuple3f t;
						if (genRays)
						{
							ls.direction = new Vector3d (rays.rays[j].direction);
							t = rays.rays[j].direction;
						}
						else
						{
							t = rays.rays[j].origin;
						}
						sum.add (t);
						Math2.mul (tmpVector, t, t);
						squaredSum.add (tmpVector);
						samples.add (ls);
					}
					count = samples.size ();
					Math2.mul (tmpVector, sum, sum);
					tmpVector.scaleAdd (-1f / count, squaredSum);
					tmpVector.scale (1f / count);
					float variance = tmpVector.x + tmpVector.y + tmpVector.z;
					n = (int) Math.min (variance / delta, 500000) - count;
				} while ((n > count >> 2) && !onlyOne);
			}

			le.power = power;
			le.samples = samples.toArray (new LightSample[samples.size ()]);
			lightEntries.add (le);
		}
		this.lightEntries = lightEntries.toArray (new LightEntry[lightEntries.size ()]);
	}

	protected void initLocals ()
	{
		super.initLocals ();
		list = new IntersectionList ();
		line = new Line ();
		negatedDirection = new Vector3f ();
		tmpSpectrum = scene.createSpectrum ();
		singleRay = new RayList (id);
		singleRay.setSize (1);
		singleSample = new LightSample ();
		singleSample.direction = new Vector3d ();
		singleSample.origin = new Point3d ();
		singleSample.color = id.newInstance ();
	}

	public void getLightRays (boolean frontFace, Intersection desc,
			RayList rays, ArrayList cache, Random rnd)
	{
		int rayIndex = rays.size ();
		int index = scene.getLight (desc.solid);
		Light exclude = (index >= 0) ? scene.getLights ()[index] : null;
		
		for (int i = 0; i < lightEntries.length; i++)
		{
			LightEntry e = lightEntries[i];
			if (e.light == exclude)
			{
				continue;
			}
			int n;
			if (alwaysOneSample)
			{
				n = 1;
			}
			else
			{
				switch (e.light.getLightType ())
				{
					case Light.DIRECTIONAL:
					case Light.POINT:
						n = 1;
						break;
					case Light.AREA:
						n = Math.max ((int) (samplesForAreaLight * e.power / totalPower), 1);
						break;
					case Light.SKY:
						n = Math.max ((int) (samplesForSkyLight * e.power / totalPower), 1);
						break;
					default:
						throw new AssertionError ();
				}
				if ((n < 1) || (e.samples.length == 1))
				{
					n = 1;
				}
			}
			boolean dir = (e.light.getLightType () == Light.DIRECTIONAL) || (e.light.getLightType () == Light.SKY);
		scanLights:
			for (int j = 0; j < n; j++)
			{
				LightSample ls;
				if (e.samples.length == 1)
				{
					ls = e.samples[0];
				}
				else if (raytracerType == Environment.STANDARD_RAY_TRACER)
				{
					ls = e.samples[j % e.samples.length];
				}
				else
				{
					ls = e.samples[rnd.nextInt (e.samples.length)];
				}
				LightEntry le = lightEntries[ls.lightEntry];
				if (ls.color == null)
				{
					singleSample.lightEntry = ls.lightEntry;
					ls = singleSample;
					if (dir)
					{
						le.light.generateRandomRays (le.env, null, id, singleRay, true, rnd);
					}
					else
					{
						le.light.generateRandomOrigins (le.env, singleRay, rnd);
					}
					ls.origin.set (singleRay.rays[0].origin);
					ls.color.set (singleRay.rays[0].spectrum);
					ls.direction.set (singleRay.rays[0].direction);
				}
				rays.setSize (rayIndex + 1);
				Ray r = rays.rays[rayIndex];
	
				line.start = 0;
				line.origin.set (desc.getPoint ());
	
				if (dir)
				{
					line.direction.set (ls.direction);
					line.direction.negate ();
					line.end = Double.MAX_VALUE;
				}
				else
				{
					line.direction.set (ls.origin);
					line.direction.sub (line.origin);
					line.end = 1;
				}
	
				if (frontFace != (desc.getNormal ().dot (line.direction) > 0))
				{
					continue;
				}
	
				r.direction.set (line.direction);
				r.direction.normalize ();
				negatedDirection.negate (r.direction);
				if (dir)
				{
					le.light.computeExitance (le.env, r.spectrum);
					r.spectrum.mul (ls.color);
				}
				else
				{
					le.light
						.computeBSDF (le.env, null, ls.color, negatedDirection, false, r.spectrum);
					if (geometryTerm)
					{
						r.spectrum.scale (1 / (float) line.lengthSquared ());
					}
				}
				r.spectrum.scale (1d / n);
	
				if (r.spectrum.integrate () <= 1e-10f)
				{
					continue;
				}
	
				if (!le.light.isShadowless ())
				{
					shadowTestCount++;
					while (cache.size () <= i)
					{
						cache.add (new CacheEntry ());
					}
					list.clear ();
					CacheEntry ce = (CacheEntry) cache.get (i);
					if (ce.shading != null)
					{
						ce.shading.computeIntersections (line, Intersection.ANY, list,
							desc, null);
						if (list.size > 0)
						{
							Shader sh = scene.getShader (ce.shading);
							Environment env = ce.shadingEnvironment;
							env.set (list.elements[0], sh.getFlags (), scene);
							sh.computeBSDF (env, r.direction, r.spectrum, negatedDirection, false, tmpSpectrum);
							if (tmpSpectrum.integrate () <= 1e-10 * Shader.DELTA_FACTOR)
							{
								shadowedCount++;
								shadowCacheSuccessCount++;
								continue scanLights;
							}
						}
						ce.shading = null;
					}
	
					scene.computeIntersections (line, Intersection.ALL, list, desc,
						null);
					for (int k = list.size - 1; k >= 0; k--)
					{
						Intersection is = list.elements[k];
						int li = scene.getLight (is.solid);
						if ((li >= 0) && (scene.getLights ()[li] == le.light))
						{
							list.remove (k, k + 1);
						}
					}
					while (ce.environments.size () < list.size)
					{
						ce.environments.add (new Environment (scene.getBoundingBox (), scene.createSpectrum (), Environment.STANDARD_RAY_TRACER));
					}
					for (int k = 0; k < list.size; k++)
					{
						Shader sh = scene.getShader (list.elements[k].solid);
						Environment env = ce.environments.get (k);
						env.set (list.elements[k], sh.getFlags (), scene);
						sh.computeBSDF (env, r.direction, r.spectrum, negatedDirection, false, tmpSpectrum);
						if (tmpSpectrum.integrate () <= 1e-10 * Shader.DELTA_FACTOR)
						{
							shadowedCount++;
							ce.shadingEnvironment = env;
							ce.shading = list.elements[k].solid;
							continue scanLights;
						}
						tmpSpectrum.scale (1 / Shader.DELTA_FACTOR);
						r.spectrum.set (tmpSpectrum);
					}
				}
	
				rayIndex++;
			}
		}
		rays.setSize (rayIndex);
	}

	@Override
	protected void mergeStatistics (ProcessorBase src)
	{
		super.mergeStatistics (src);
		DefaultLightProcessor lp = (DefaultLightProcessor) src;
		shadowCacheSuccessCount += lp.shadowCacheSuccessCount;
		shadowedCount += lp.shadowedCount;
		shadowTestCount += lp.shadowTestCount;
	}

	@Override
	protected void appendStatisticsImpl (StringBuffer stats)
	{
		int samples = 0;
		for (int i = 0; i < lightEntries.length; i++)
		{
			samples += lightEntries[i].samples.length;
		}
		stats
			.append (Resources
				.msg (
					"lightprocessor.default.statistics",
					new Object[] {
							Integer.valueOf (lightEntries.length),
							Integer.valueOf (samples),
							new Long (shadowTestCount),
							new Long (shadowedCount),
							new Float (
								(shadowedCount == 0) ? 0
										: ((float) shadowCacheSuccessCount / shadowedCount))}));
	}

}
