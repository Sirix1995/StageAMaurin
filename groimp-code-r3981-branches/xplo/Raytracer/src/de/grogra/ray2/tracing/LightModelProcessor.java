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

import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Collector;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Scene;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.xl.util.ObjectList;

/**
 * Perform light model calculation for a single ray and collect
 * the energy that was absorbed by the volumes. Color computations
 * are performed with instances of Spectrum. 
 * 
 * @author Reinhard Hemmerling
 *
 */
public class LightModelProcessor extends RadiationModel implements Cloneable
{
	private Scene scene;


	private RayList rays;
	private Environment env;
	private Vector3f out;
	private Line line;

	private ObjectList<Spectrum> radiantPower;
	private ObjectList<Spectrum> sensedIrradiance;

	private Spectrum tmpSpectrum0;
	private Spectrum tmpSpectrum1;
	private Spectrum tmpSpectrum2;
	private Spectrum tmpSpectrum3;
	
	private IntersectionList ilist;
	
	private double minPower;
	private int depth;

	/**
	 * Create a new light model processor that uses the given
	 * spectrum factory.
	 * 
	 * @param scene
	 */
	public LightModelProcessor (Scene scene, int[] idToGroup)
	{
		this (scene, new ObjectList<Spectrum> (), new ObjectList<Spectrum> (), idToGroup);
	}

	/**
	 * Create a new light model processor that uses the given
	 * spectrum factory and adds collected radiation values to the lists.
	 * 
	 * @param scene a scene
	 * @param radiantPowerSum
	 * @param sensedIrradianceSum
	 */
	public LightModelProcessor (Scene scene, ObjectList<Spectrum> radiantPowerSum,
		 ObjectList<Spectrum> sensedIrridiancSum, int[] idToGroup)
	{
		super (scene.createSpectrum (), radiantPowerSum, sensedIrridiancSum, idToGroup);
		this.scene = scene;
		initLocals ();
	}

	protected void initLocals ()
	{
		rays = new RayList (black);
		tmpSpectrum0 = black.newInstance ();
		tmpSpectrum1 = black.newInstance ();
		tmpSpectrum2 = black.newInstance ();
		tmpSpectrum3 = black.newInstance ();
		out = new Vector3f ();
		line = new Line ();
		env = new Environment (scene.getBoundingBox (), black,
			Environment.RADIATION_MODEL);
		radiantPower = new ObjectList<Spectrum> ();
		sensedIrradiance = new ObjectList<Spectrum> ();
		
		ilist = new IntersectionList ();
	}

	public LightModelProcessor dup (Scene scene)
	{
		try
		{
			LightModelProcessor p = (LightModelProcessor) super.clone ();
			p.scene = scene;
			p.initLocals ();
			return p;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}


	public void compute (long rayCount, long seed, ProgressMonitor progress, int depth, double minPower)
	{
		final Light[] lights = scene.getLights ();
		final int lightCount = lights.length;


		if (lightCount == 0)
		{
			return;
		}
		
		this.depth = depth;

		// continue if there is light in the scene
		Environment e = new Environment (scene.getBoundingBox (),
			black, Environment.RADIATION_MODEL);

		// collect the power of the light sources
		final double[] powerOfLight = new double[lightCount];
		double totalPower = 0;
		for (int i = 0; i < lightCount; i++)
		{
			powerOfLight[i] = lights[i].getTotalPower (e);
			totalPower += powerOfLight[i];
		}
		if (!(totalPower > 0))
		{
			return;
		}

		rayCount = (rayCount + BUNDLE_SIZE - 1) / BUNDLE_SIZE;

		// calculate how many rays originate from each light source
		final long[] raysFromLight = new long[lightCount];
		long totalRays = 0;
		for (int i = 0; i < lightCount; i++)
		{
			raysFromLight[i] = (long) (rayCount * powerOfLight[i] / totalPower);
			totalRays += raysFromLight[i];
			//					System.err.println ("light: " + i + "   rays: "
			//						+ raysFromLight[i]);
		}
		//				System.err.println ("totalRays: " + totalRays + "   rayCount: "
		//					+ rayCount);

		MTRandom rnd = new MTRandom (seed);
		// randomly distribute remaining rays (because of rounding)
		while (rayCount > totalRays)
		{
			int light = rnd.nextInt (lightCount);
			if (powerOfLight[light] > 0)
			{
				raysFromLight[light]++;
				totalRays++;
			}
		}
		//				System.err.println ("totalRays: " + totalRays + "   rayCount: "
		//					+ rayCount);
		
		double[] lightFactors = new double[lightCount];
		long[] seedOffsets = new long[lightCount];
		for (int i = 0; i < lightCount; i++)
		{
			lightFactors[i] = 1.0 / raysFromLight[i];
			seedOffsets[i] = (seed * lightCount + i) * rayCount;
		}
		computeImpl (raysFromLight, seedOffsets, lightFactors, rnd, progress, depth, minPower);
	}


	void computeImpl (long[] raysFromLight, long[] seedOffsets, double[] lightFactors,
			Random rnd, ProgressMonitor progress, int depth, double minPower)
	{
		this.minPower = minPower;
		this.depth = depth;

		final Light[] lights = scene.getLights ();
		final int lightCount = lights.length;

		// these rays will be created by light.generateRandomOrigins
		final RayList orays = new RayList (black);
		orays.setSize (BUNDLE_SIZE);

		// these rays (in fact a single ray) will be created by light.generateRandomRays
		final RayList drays = new RayList (black);
		drays.setSize (1);

		Environment e = new Environment (scene.getBoundingBox (),
			black, Environment.RADIATION_MODEL);

		long rayCount = 0;
		for (int l = 0; l < lightCount; l++)
		{
			rayCount += raysFromLight[l];
		}

		// create lots of rays through the scene
		long time0 = System.currentTimeMillis ();
		long r = 0;
		for (int l = 0; l < lightCount; l++)
		{
			// number of pending rays for light source #l 
			long raysToCreate = raysFromLight[l];
			// create rays for light source #l
			for (long lr = 0; lr < raysToCreate; lr++)
			{
				r++;

				rnd.setSeed (seedOffsets[l] + lr);

				// generate a random ray from that light source
				e.localToGlobal.set (scene.getLightTransformation (l));
				e.globalToLocal.set (scene
					.getInverseLightTransformation (l));
				Light light = lights[l];
				
				// create the set of initial rays (in fact just their origins)
				light.generateRandomOrigins (e, orays, rnd);
				
				// for each initial origin, complete the ray by choosing a direction
				for (int j = 0; j < BUNDLE_SIZE; j++)
				{
					drays.rays[0].origin.set (orays.rays[j].origin);
					light.generateRandomRays (e, null, orays.rays[j].spectrum, drays,
						true, rnd);

					// trace where the ray goes and update lighting conditions
					// for each object it hits on its way
					ilist.clear ();
					traceRay (depth, drays.rays[0], null, rnd, lightFactors[l]);
				}
				
				if (progress != null)
				{
					// display the progress if:
					//  - it was the last ray
					//  - 500ms passed since the last display
					long time1 = System.currentTimeMillis ();
					if (r >= rayCount || (time1 - time0 > 500))
					{
						progress.setProgress ("Ray " + (r * BUNDLE_SIZE) + " of " + (rayCount * BUNDLE_SIZE),
							(float) r / (float) rayCount);
						time0 = time1;
					}
				}
				
			}

			if (raysFromLight[l] > 0)
			{
				// collect the light for light source #l
				addAndClear (lightFactors[l] * (1.0 / BUNDLE_SIZE), radiantPower, radiantPowerSum);
				addAndClear (lightFactors[l] * (1.0 / BUNDLE_SIZE), sensedIrradiance, sensedIrradianceSum);
			}
		}
	}


	
	/**
	 * Perform recursive trace of the path of the light ray.
	 * The recursion ends when depth reaches zero or when the light energy
	 * of the ray falls below a given threshold.
	 * @param depth
	 * @param ray
	 * @param is
	 */
	void traceRay (int depth, Ray ray, Intersection is, Random rnd, double lightFactor)
	{
		//				System.err.println ("origin: " + ray.origin + "   direction: "
		//					+ ray.direction + "   spectrum: " + ray.spectrum);

		// convert the ray into a line for intersection calculation
		// conversion between Tuple3d and Tuple3f is needed
		line.origin.set (ray.getOrigin ());
		line.direction.set (ray.getDirection ());
		line.start = 0.00001; //  TODO 
		line.end = java.lang.Double.POSITIVE_INFINITY;
		Spectrum spectrum = tmpSpectrum0;
		spectrum.set (ray.spectrum);

		tmpSpectrum3.set (ray.spectrum);
		tmpSpectrum3.clampMinZero();
		
		int ilistSize = ilist.size;
		// compute first intersection between ray and scene
		scene
			.computeIntersections (line, Intersection.CLOSEST, ilist, is, null);

		// check if an intersection was found
		if (ilist.size > ilistSize)
		{
			// there should be just one intersection
			Intersection intersection = ilist.elements[ilistSize];
			// check if no sky object was hit (parameter < INF)
			if (intersection.parameter < Double.POSITIVE_INFINITY)
			{				
				// obtain the volume index
				int volumeIndex = idToGroup[intersection.volume.getId ()];
		
				// prepare the direction of the outgoing ray
				out.set (line.direction);
				out.negate ();
		
				int sensorID = scene.getSensor (intersection.volume);
				if (sensorID >= 0)
				{
					Sensor s = scene.getSensors ()[sensorID];
					rays.setSize (1);
					env.set (intersection, s.getFlags (), scene);
					s.computeExitance (env, tmpSpectrum1);
					s.computeBSDF (env, null, tmpSpectrum1, out, true, tmpSpectrum2);
					tmpSpectrum2.mul (spectrum);
					Spectrum sCol = sensedIrradiance.get (volumeIndex);					
					if (sCol == null)
					{
						// Create a Collector-instance for a sensor
						sCol = tmpSpectrum2.clone () ;
						sensedIrradiance.set (volumeIndex, sCol);
					}
					else
					{						
						// Add the spectrum to an already existing spectrum
						sCol.add(tmpSpectrum2);
					}
					
					if(sCol instanceof Collector)
					{
						((Collector) sCol).setAsCollector();
						((Collector) sCol).addToStatistic(line.direction, tmpSpectrum2, lightFactor, this.depth == depth);
					}
				}
						
				// obtain the shader of the volume that was intersected
				Shader shader = scene.getShader (intersection.volume);
		
				if (shader == null)
				{
					// no shader: continue ray without change 
					rays.setSize (1);
					Ray newRay = rays.rays[0];
					newRay.direction.set (line.direction);
					newRay.spectrum.set (spectrum);
				}
				else
				{
					// set the local environment
					env.set (intersection, shader.getFlags (), scene);
		
					// calculate the new ray direction
					rays.setSize (1);
					shader.generateRandomRays (env, out, spectrum, rays, true, rnd);
				}
		
				// obtain the new ray and set its origin (not done by generateRandomRays)
				Ray newRay = rays.lastRay ();
				newRay.origin.set (intersection.getPoint ());
		
				spectrum.sub (newRay.spectrum);
				spectrum.clampMinZero ();
		
				// add the absorbed part of the light to the volume that was hit
				Spectrum col = radiantPower.get (volumeIndex); 
				if(col == null)
				{
					// Creates a new Collector-instance for a usual object.
					col = spectrum.clone ();
					radiantPower.set (volumeIndex, col);
					
				} else {			
					
					// Add the spectrum to an already existing spectrum
					col.add(spectrum);
				}				
				//tmpSpectrum0.integrate()
				if(col instanceof Collector)
				{
					((Collector) col).setAsCollector();
					((Collector) col).addToStatistic(line.direction, tmpSpectrum3, lightFactor, this.depth == depth);
				}
				
				// check if radiance power is big enough for recursion
				if ((depth > 0) && newRay.spectrum.integrate () > minPower)
				{
					// prevent the ray from staying at the same position
					// e.g. if two spheres of the same size are at the same position
					//				intersection.line.start += 0.00001;
		
					// recursive descent
					traceRay (depth - 1, newRay, intersection, rnd, lightFactor);
				}
			}
			ilist.setSize (ilistSize);
		}
	}

}
