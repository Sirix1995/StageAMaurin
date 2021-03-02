package de.grogra.ray2.metropolis;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.light.DefaultLightProcessor;
import de.grogra.ray2.light.LightProcessor;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.vecmath.geom.Intersection;

public class DirectLightingCalculator {

	LightProcessor lightProcessor;
	Tuple3d  color;
	RayList rays;
	Tuple3d tmpColor = new Point3d ();
	Spectrum3d tmpSpectrum = new Spectrum3d();
	Vector3f view = new Vector3f();
	
	public DirectLightingCalculator() {
		
		rays = new RayList();
		color = new Point3d();
	}
	

	public void setLightProccessor(LightProcessor lightProc){
		this.lightProcessor = lightProc;
	}
	
	
	public Tuple3d calculateDirectLight(CombinedPathValues path, int index, Random random){
		
		Scattering sh = path.shaderList.get(index);
		Intersection desc = path.intersecList.get(index);
		Environment env = path.envList.get(index);
		Spectrum weight = path.weightListEB.get(index);
		
		boolean finite = (desc!=null) ? desc.parameter < Double.POSITIVE_INFINITY:false;
		color.set(0,0, 0);
		
		if((sh!=null)&& finite) {
			rays.clear ();
	
			
			view.set(desc.line.direction);
			view.negate();
			// direct illumination: for all light sources, choose a vertex on
			// light source and connect it with the current eye subpath
			lightProcessor.getLightRays (desc.line.direction.dot (desc
				.getNormal ()) < 0, desc, rays, new ArrayList(), random);
			// In terms of Veach's thesis, the computed spectra are the
			// values alpha_1^L c_1t, divided by the BSDF-factors and cosines of the surface.
			// These factors are considered below. 
	
			// calculate color
			for (int i = rays.size () - 1; i >= 0; i--)
			{
				// multiply ray spectra with the BSDF and cos(theta)-factor. The
				// resulting tmpSpectrum is the value alpha_1^L c_1t
				sh.computeBSDF (env, view, rays.rays[i].spectrum, rays.rays[i].direction, true, tmpSpectrum);
				
				tmpSpectrum.dot (weight, tmpColor);
	
				// Now in terms of Veach's thesis, the computed dot product is the
				// product alpha_1^L c_1t alpha_t^E in formula 10.8 with t = depth + 1
				// (length of current eye subpath)
	
	//			if (PixelwiseRenderer.DEBUG_SUBPIXEL)
	//			{
	//				System.err.println ("contribution of light " + i + " " + tmpColor);
	//				System.err.println ("    " + rays.rays[i] + " " + env);
	//			}
	
				// add contribution to color
				color.add (tmpColor);
			}
		}
		return color;
		
	}
	
}
