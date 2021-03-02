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

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.light.DefaultLightProcessor;
import de.grogra.ray2.photonmap.OptionReader;
import de.grogra.ray2.photonmap.PhotonMap;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.xl.util.ObjectList;

/**
 * This class renders a scene with the photon map rendering algorithm.
 * @author Ralf Kopsch
 */
public class PhotonMapRayProcessor extends RayProcessorBase {
	
	private static int SHOWPHOTONMAPWITHCOLOREDTYPE_MODE = 1;
	/** direct visualize the photon map */
	private static int SHOWPHOTONMAP_MODE = 2;
	
	private int debug_mode = 0;
	
	
	/** the photon map */
	private static PhotonMap photonMap = null;

	/** Light properties */
	private float[] lightProp = null;
	private Color3f[] photonColor = null;
	/** current selected light */
	private int selectedLight = 0;
	
	private int globalPhotonCount = 10000;
	private int causticPhotonCount = 50000;
	private int photonDepth = 5;

	private Environment[] lightEnvironments = null;
	/** a random number */
	private MTRandom random = new MTRandom();
	private final Line line = new Line ();
	
	/** True, if a new Photon map was created */
	boolean newMapCreated = false;
		
	private Spectrum tmpSpectrum0;
	private Environment env;
	private final Vector3f out = new Vector3f ();
	private ObjectList<Spectrum> radiantPower = new ObjectList<Spectrum> ();
	
	private static long pmTime = 0;

	/**
	 * Creates a new Photon Map Processor.
	 */
	public PhotonMapRayProcessor() {
		DefaultLightProcessor p = new DefaultLightProcessor ();
		p.useGeometryTerm (true);
		p.useOneSamplePerLight (true);
		setLightProcessor (p);
	}
	
	
	@Override
	public void initialize(PixelwiseRenderer renderer, Scene scene) {
		super.initialize(renderer, scene);
		OptionReader reader = new OptionReader(renderer);
		if (PhotonMapRayProcessor.photonMap == null || reader.isPhotonMapCalcNeeded(scene)) {
			long startTime = System.currentTimeMillis();
			this.globalPhotonCount  = reader.getGlobalPhotonCount();
			this.causticPhotonCount = reader.getCausticPhotonCount();
			this.photonDepth = reader.getPhotonDepth();
			Spectrum spectrumFactory = scene.createSpectrum ();
			env = new Environment (scene.getBoundingBox (), spectrumFactory,
					Environment.RADIATION_MODEL);
			tmpSpectrum0 = spectrumFactory.newInstance ();
			PhotonMapRayProcessor.photonMap = new PhotonMap(reader.getPhotonArea());
			createPhotonMap(renderer);
			this.newMapCreated = true;
			pmTime = System.currentTimeMillis() - startTime;
		}
		reader.calcFinished(scene);
	}

	private RayList rays;
	private Vector3f view;
	
	
	@Override
	protected void initLocals() {
		super.initLocals();
		rays = new RayList (scene.createSpectrum ());
		view = new Vector3f ();		
	}
	
	/**
	 * Compute the light probabilities for all lights
	 * @param lights array of all lights.
	 * @param photonCount the photon count.
	 */
	private void computeLightProbs(ArrayList<Light> lights, int photonCount) {
		this.lightProp = new float[lights.size()];
		this.photonColor = new Color3f[lights.size()];
		
		RayList list = new RayList(1);
		float max = 0;
		this.lightEnvironments = new Environment[lights.size()];
		for(int i = 0; i < lights.size(); i++) {
			this.lightEnvironments[i] = new Environment (scene.getBoundingBox (), scene.createSpectrum(), getEnvironmentType ());
			this.lightEnvironments[i].localToGlobal.set(scene.getLightTransformation(i));
			lights.get(i).generateRandomOrigins(this.lightEnvironments[i], list, this.random);
			this.photonColor[i] = new Color3f(list.rays[0].color); 
			max += this.photonColor[i].x + this.photonColor[i].y + this.photonColor[i].z;
		}
		float photonEnergy = max / (float) photonCount;
		
		for(int i = 0; i < lights.size(); i++) {
			this.lightProp[i] = (this.photonColor[i].x + this.photonColor[i].y + this.photonColor[i].z) / photonEnergy;
			this.photonColor[i].scale(1.0f / this.lightProp[i]);
		}
	}

	/**
	 * Returns the number of the next light.
	 * @return Returns the number of the next light.
	 */
	private int selectLight() {
		if(lightProp[selectedLight] > 0) {
			lightProp[selectedLight]--;
			return selectedLight;
		} else {
			selectedLight++;
			lightProp[selectedLight]--;
			return selectedLight;
		}
	}	
	
	private Ray generatePhoton(int lightNumber, ArrayList<Light> lights) {
		RayList list = new RayList(1);
		Vector3f vec = new Vector3f();
		Light light = lights.get(lightNumber);
		light.generateRandomOrigins(this.lightEnvironments[lightNumber], list, this.random);
		light.generateRandomRays(this.lightEnvironments[lightNumber], vec, scene.createSpectrum(), list, true, this.random);
		Ray temp = new Ray(list.rays[0]);
		temp.color.set(photonColor[selectedLight]);
		return temp;
	}	

	/**
	 * Create a new photon map.
	 * @param renderer the renderer which provides the needed information
	 */
	private void createPhotonMap(PixelwiseRenderer renderer) {
//		Light[] lights = super.scene.getLights();
		ArrayList<Light> lights = new ArrayList<Light>();
		// check if light is a light :-)
		for (Light light: super.scene.getLights()) {
			if (light.getLightType() != Light.NO_LIGHT) {
				lights.add(light);
			}
		}
		if (lights.size() == 0) {
			System.err.println("Warning: Scene contains no Lights. No Photon Map created.\n");
			return;
		}
		
		// calculate light properties
		computeLightProbs(lights, this.globalPhotonCount);

		// create the global photon map
		int currentPhoton = this.globalPhotonCount;
		while((currentPhoton != 0) && !isStopped(renderer)) {
			this.renderer.setMessage("Creating Photonmap.", 1 - (1/(this.globalPhotonCount / (currentPhoton + 1f))));
			
			// select a light
			int lightNumber = selectLight();
			// create a photon
			Ray ray = generatePhoton(lightNumber, lights);
			// trace the photon throw the scene
			shootPhoton(this.photonDepth, ray, null, false);
			currentPhoton--;
		}
		
		// create the caustic photon map
		selectedLight = 0;
		computeLightProbs(lights, this.causticPhotonCount);
		currentPhoton = this.causticPhotonCount;
		while((currentPhoton != 0) && !isStopped(renderer)) {
			this.renderer.setMessage("Creating Caustic Photonmap.", 1 - (1/(this.causticPhotonCount / (currentPhoton + 1f))));
			
			// select a light
			int lightNumber = selectLight();
			// create a photon
			Ray ray = generatePhoton(lightNumber, lights);
			// trace the photon throw the scene
			shootPhoton(this.photonDepth, ray, null, true);
			currentPhoton--;
		}
	}
	
	/**
	 * Shoot a photon from the selected light source into the scene.
	 * 
	 * @param depth reflection depth
	 * @param ray a light ray
	 * @param is properties of an intersection point
	 */
	private void shootPhoton(int depth, Ray ray, Intersection is, boolean isCausticMap) {
		// convert the ray into a line for intersection calculation
		// conversion between Tuple3d and Tuple3f is needed
		line.origin.set (ray.getOrigin ());
		line.direction.set (ray.getDirection ());
		line.start = 0.00001; //  TODO 
		line.end = java.lang.Double.POSITIVE_INFINITY;
		Spectrum spectrum = tmpSpectrum0;
		spectrum.set (ray.spectrum);
		
		// create an empty list where the found intersections will be stored
		IntersectionList ilist = new IntersectionList ();

		// compute first intersection between ray and scene
		scene.computeIntersections (line, Intersection.CLOSEST, ilist, /*is,*/null, null);
		
		// check if an intersection was found
		if (ilist.size > 0) {
			// there should be just one intersection
			Intersection intersection = ilist.elements[0];			
			
			// obtain the volume id
			int volumeID = intersection.volume.getId ();
			
			// prepare the direction of the outgoing ray
			out.set (line.direction);
			out.negate ();
			
			// ior test
//			env.iorRatio = (float) record(intersection, true, spectrum);
			env.iorRatio = (float) getIOR (intersection, spectrum);
			
			
			// Sensor ....
			/*
			int sensorID = scene.getSensor (intersection.volume);
			if (sensorID >= 0)
			{
				Sensor s = scene.getSensors ()[sensorID];
				rays.setSize (1);
				env.set (intersection, s.getFlags (), scene);
				s.computeExitance (env, tmpSpectrum1);
				s.computeBSDF (env, null, tmpSpectrum1, out, true, tmpSpectrum2);
				tmpSpectrum2.mul (spectrum);
				Spectrum sp = sensedPower.get (volumeID);
				if (sp == null)
				{
					sensedPower.set (volumeID, tmpSpectrum2.clone ());
				}
				else
				{
					sp.add (tmpSpectrum2);
				}
			}*/
			
			
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
				setEnvironment (env, intersection, shader.getFlags (), scene);

				// calculate the new ray direction
				rays.setSize (1);
				shader.generateRandomRays (env, out, spectrum, rays, true, random);
			}
			
			// obtain the new ray and set its origin (not done by generateRandomRays)
			Ray newRay = rays.lastRay ();
			newRay.origin.set (intersection.getPoint ());

			spectrum.sub (newRay.spectrum);
			spectrum.clampMinZero ();

			// add the absorbed part of the light to the volume that was hit
			Spectrum pwr = radiantPower.get (volumeID);
			if (pwr == null)
			{
				radiantPower.set (volumeID, spectrum.clone ());
			}
			else
			{
				pwr.add (spectrum);
			}
			
			boolean isSpecular = Math.abs (intersection.getNormal().dot(new Vector3d(out)) - intersection.getNormal().dot(new Vector3d(newRay.direction))) < 0.001;
			boolean isReflected = intersection.getNormal().dot(new Vector3d(out)) * intersection.getNormal().dot(new Vector3d(newRay.direction)) <= 0;
			boolean isTrans = out.dot(new Vector3f(intersection.getNormal())) <= 0;
			
			boolean isDefuse = !isTrans && !isSpecular && !isReflected;
			
			if (this.debug_mode == PhotonMapRayProcessor.SHOWPHOTONMAPWITHCOLOREDTYPE_MODE) {
				if (isTrans) {
					photonMap.insertPhoton(new Color3f(1,0,0), intersection.getPoint(), out);
				} else if (isSpecular){
					photonMap.insertPhoton(new Color3f(0,1,0), intersection.getPoint(), out);
				} else if (isReflected) {
					photonMap.insertPhoton(new Color3f(0,0,1), intersection.getPoint(), out);
				} else {
					photonMap.insertPhoton(new Color3f(1,1,1), intersection.getPoint(), out);
				}
			} else {
				Color3f testCol = new Color3f();
				newRay.spectrum.get(testCol);
				if (isCausticMap) {
					if ((depth != this.photonDepth) && isDefuse) {
						photonMap.insertPhoton(testCol, intersection.getPoint(), new Vector3f(/*intersection.getNormal()*/ out));
					}
				} else {
					if (!isTrans && !isSpecular && !isReflected) {
						photonMap.insertPhoton(testCol, intersection.getPoint(), new Vector3f(/*intersection.getNormal()*/ out));
					}
				}
			}

			// check if radiance power is big enough for recursion
			// and no sky object was hit (parameter < INF)
			if ((depth > 0) && intersection.parameter < Double.POSITIVE_INFINITY)
			{
				// prevent the ray from staying at the same position
				// e.g. if two spheres of the same size are at the same position
				//				intersection.line.start += 0.00001;

				// recursive descent
				if (isCausticMap) {
					if (!isDefuse) {
						int i = record (intersection, newRay.reflected);//false);
						shootPhoton (depth - 1, newRay, intersection, isCausticMap);
						unrecord (intersection, i);
					}
				} else {
					int i = record (intersection, newRay.reflected);//false);
					shootPhoton (depth - 1, newRay, intersection, isCausticMap);
					unrecord (intersection, i);
				}
			}
		}
	}
	
	private Vector3f reflectedVariance = new Vector3f();
	private Vector3f refractedVariance = new Vector3f();
	private static final float MAX_VARIANCE = Shader.LAMBERTIAN_VARIANCE * 3 * 0.8f;
	private static final float MIN_WEIGHT = 0.03f;
	
	float traceRay (int depth, Intersection desc,
			Spectrum weight, Tuple3d color, Locals loc, Random random) {
		
		if (debug_mode == PhotonMapRayProcessor.SHOWPHOTONMAP_MODE ||
				debug_mode == PhotonMapRayProcessor.SHOWPHOTONMAPWITHCOLOREDTYPE_MODE) {
			return PhotonMapRayProcessor.photonMap.traceRay(desc, color);
		}
		
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
			photonMap.sumPhotons(desc.getPoint(), new Vector3f(desc.getNormal()), color);
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
	
	private final Vector3d tmpVector = new Vector3d ();
	private void setEnvironment (Environment env, Intersection desc, int flags,
			Scene scene)
	{
		if ((flags & Shader.NEEDS_POINT) != 0)
		{
			if (desc.parameter == java.lang.Double.POSITIVE_INFINITY)
			{
				tmpVector.normalize (desc.line.direction);
				env.point.set (tmpVector);
				tmpVector.scale (1e100);
				scene.transform (desc.volume, tmpVector, tmpVector);
				tmpVector.normalize ();
				env.localPoint.set (tmpVector);
			}
			else
			{
				Point3d p = desc.getPoint ();
				env.point.set (p);
				scene.transform (desc.volume, p, tmpVector);
				env.localPoint.set (tmpVector);
			}
		}
		if ((flags & Shader.NEEDS_NORMAL) != 0)
		{
			env.normal.set (desc.getNormal ());
		}
		if ((flags & Shader.NEEDS_UV) != 0)
		{
			env.uv.set (desc.getUV ());
		}
		if ((flags & Shader.NEEDS_TANGENTS) != 0)
		{
			env.dpdu.set (desc.getUTangent ());
			env.dpdv.set (desc.getVTangent ());
		}
		env.solid = desc.type != Intersection.PASSING;
	}

	@Override
	int getEnvironmentType() {
		return Environment.PATH_TRACER;
	}

	/**
	 * Returns true, if stop is been invoked.
	 * @param render the renderer which provides the needed information
	 * @return returns true, if stop is been invoked.
	 */
	private boolean isStopped(PixelwiseRenderer render) {
		boolean ret = renderer.isStopped();
		if (ret) {
			// clear photon map
			PhotonMapRayProcessor.photonMap = null;
		}
		return ret;
	}
	
	
	@Override
	protected void appendStatisticsImpl(StringBuffer stats) {
		stats.append("Photon Map Statistics\n");
		stats.append("    New Photon Map created  : " + (this.newMapCreated ? "yes" : "no") + "\n");
		stats.append("    Photon Map creation time: " + ((int) (pmTime / 60000)) + " minutes " + 
				((pmTime % 60000) * 0.001f) + " seconds\n");
		stats.append("    Number of Map entries   : " + PhotonMapRayProcessor.photonMap.getEntryCount());
		if (this.debug_mode != 0) {
			stats.append("    DEBUG MODE              : " + this.debug_mode);
		}
	}

}
