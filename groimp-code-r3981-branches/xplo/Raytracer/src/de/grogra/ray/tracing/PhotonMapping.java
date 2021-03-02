package de.grogra.ray.tracing;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTFakeObject;
import de.grogra.ray.RTLight;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.Raytracer;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.light.DefaultLightProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;
import edu.wlu.cs.levy.CG.*;

/**
 * Funktioniert momentan nur mit Punktlichtquellen und Arealight
 * Funktioniert nicht mit directional Light
 */
public class PhotonMapping implements RayProcessor {

	private int nr_photons     = 0;
	private int recursionDepth = 5;
	private int range          = 50; 
	
	private int recursionDepth_eye = 5;
	
//	private final static float MIN_ENERGY   = 0.02f;
//	private final static float LIGHT_VARIANCE_MAX = RTShader.LAMBERTIAN_VARIANCE*3.0f*0.8f; 
	private final static float LIGHT_WEIGHT_MIN   = 0.02f;

	private RTScene scene = null;
	private static KDTree photonMap = null;
	private char seed;
	private Color3f[] photonColor = null;
	private RTLight[] lights = null;
	private float lightProp[] = null;
	private int selectedLight = 0;
	private boolean createMap = true;

	private IntersectionDescription m_desc  = new IntersectionDescription();
	private IntersectionProcessor m_intersectionProcessor = null;
	private float m_lastIOR = 1.0f;	
	private LightProcessor            m_lightModel            = null;
	
	private RayList tempRayList = new RayList(1); 
	
	private static float sum (Tuple3f v)
	{
		return v.x + v.y + v.z;
	}

	public PhotonMapping(){
		super();
		if(photonMap == null) {
			photonMap = new KDTree(3);
		}
		seed = Math2.random((char)0);
		setLightProcessor(new DefaultLightProcessor());
	}

	public void createPhotonMap(){
		createMap = true;
		if(scene.getLightsCount() == 0) return;

		m_lastIOR = 1.0f;
//		while(nr_photons != 0) {
			// global machen um zeit zu sparen
			RayContext context = new RayContext();
			context.initializeContext();
		
		
			RTLight light = selectLight(lights); 
			Ray ray = generatePhoton(light);
			shootPhoton(1, ray, context, m_desc, null);
//			nr_photons--;
//		}
	}
	
	
	// berechnet den Lichtanteil der einzelnen Lichtquellen
	private void compluteLightProbs(RTLight[] lights){
		lightProp = new float[lights.length];
		photonColor = new Color3f[lights.length];
		
		RayList list = new RayList(1);
		float max = 0;
		for(int i = 0; i < lights.length; i++){
			lights[i].generateRandomOrigins(list, Math2.random(++seed));
			photonColor[i] = new Color3f(list.rays[0].color); 
			max += sum(photonColor[i]);
		}
		float photonEnergy = max / (float)nr_photons;
		for(int i = 0; i < lights.length; i++) {
			lightProp[i] = sum(photonColor[i]) / photonEnergy;
			photonColor[i].scale(1.0f / lightProp[i]);
		}
	}
	
	
	// Lichtquelle mithilfe der Hellichkeit ausw�hlen
	private RTLight selectLight(RTLight[] lights){
		if(lightProp[selectedLight] > 0){
			lightProp[selectedLight]--;
			//System.out.println("  w�hle Licht: " + selectedLight);
			return lights[selectedLight];
		} else {
			selectedLight++;
			lightProp[selectedLight]--;
			//System.out.println("  w�hle Licht: " + selectedLight);
			return lights[selectedLight];
		}
	}
	
	
	private Ray generatePhoton(RTLight light){
		RayList list = new RayList(1);
		Vector3f vec = new Vector3f();
		light.generateRandomOrigins(list, Math2.random(++seed));
//		Color3f col = list.rays[0].color;
		light.generateRandomRays(vec, list, true, Math2.random(++seed));
//		list.rays[0].color.x *= col.x;
//		list.rays[0].color.y *= col.y;
//		list.rays[0].color.z *= col.z;
		Ray temp = new Ray(list.rays[0]);
//		list.rays[0].color.x = photonColor[selectedLight].x;
//		list.rays[0].color.y = photonColor[selectedLight].y;
//		list.rays[0].color.z = photonColor[selectedLight].z;
//		return list.rays[0];
		temp.color.set(photonColor[selectedLight]);
		return temp;
	}
	
/*
	private void shootPhoton(Ray ray){
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray, null, m_desc)) {
			//System.out.println("kein Schnitt");
			return;
		}
		// Schnittpunkt in photonMap eintragen (oder reflektion)?
		// neue Energie berechen
		// neue Richtung berechnen
		//if(energie > min) shootPhoton(newRay); 


//		Ray reflected_ray = new Ray();
//		Ray refracted_ray = new Ray();
		float energie = ray.color.sum();
		if (energie >= 0) {
			insertPhoton(ray, m_desc.getPoint());




//			calcNewPhoton(ray, reflected_ray, refracted_ray, m_desc);
//			if(reflected_ray != null) shootPhoton(reflected_ray);
//			if(refracted_ray != null) shootPhoton(refracted_ray);

//			calcEnergy(m_desc, ray);

		}
	}
*/
	
	/*
	// mit random Ray
	private void shootPhoton(int depth, Ray ray, IntersectionDescription desc, RTObject exclude) {
		//If there was no Intersection...
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray, exclude, desc)) {
			return;
		}
		
		if (desc.getRTObject() instanceof RTFakeObject) {
			//((RTFakeObject)desc.getRTObject()).getColor(ray,desc,color);
			return;
		}
		
		if(createMap) {
			insertPhoton(ray, m_desc.getPoint());
		}
		
		// TODO get input from memory pool
		ShadingEnvironment env = new ShadingEnvironment();
		refreshInput(env,ray,desc);

		// TODO get shader instance from memory pool
		RTShader cur_shader = desc.getShader();

		// recursively trace lightray
		if (depth < recursionDepth) {
			
			//set outgoing light Ray	
			Vector3f in = new Vector3f(env.view);
			in.normalize();
			
			//generate a List of randomly reflected or refracted Rays
			//TODO: �berpr�fen ob gewicht = 0. Es kann vorkommen das transparente Strahlen bei nicht transparenten
			//      Objekten erzeugt werden
			cur_shader.generateRandomRays(env, in, tempRayList, false, Math2.random(++seed));

			//choose randomly one Ray of this List (the most probable is chosen at most)			
			Ray nextRay = new Ray(tempRayList.rays[0]);
			
			//if there was no ray chosen (List empty?) return
			if (nextRay == null) return;
			
			Color3f newCol = new Color3f(nextRay.getColor());
			newCol.x *= ray.getColor().x;
			newCol.y *= ray.getColor().y;
			newCol.z *= ray.getColor().z;
			
			nextRay.getColor().set(newCol);
			
			//Calculate the Colorweight
			float colorWeightSum = newCol.sum();

			//If the weight still high enough		
			if (colorWeightSum > 0.0f) {

				//if Ray was reflected...
				if((desc.getNormal().dot(nextRay.direction) > 0.0f)) {

					//trace lightray recursivly excluding the intersected object
					shootPhoton(depth+1, nextRay, desc, desc.getRTObject());
					
				}else{
					//if Ray was refracted and Object is transparent...
					//if(cur_shader.getShaderFlags()>31) {
					if( cur_shader.isTransparent()) {
						//trace lightray recursivly
						shootPhoton(depth+1, nextRay, desc, null);
					}
				}
				
			}
		}
	}		
*/
	
	
	// mit compluteMayRay
	private void shootPhoton(int depth, Ray ray, RayContext context, IntersectionDescription desc, RTObject exclude) {
		
		//If there was no Intersection...
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray, context, desc)) {
			return;
		}
		
		if (desc.getRTObject() instanceof RTFakeObject) {
			//((RTFakeObject)desc.getRTObject()).getColor(ray,desc,color);
			return;
		}
		
		if(createMap) {
			insertPhoton(ray, m_desc.getPoint());
		}
		
		// TODO get input from memory pool
		ShadingEnvironment env = new ShadingEnvironment();
		refreshEnvironment(env, ray, context, desc);
		
		if (depth < recursionDepth) {
			Ray reflectedRay = new Ray();
			Ray refractedRay = new Ray();
			Vector3f reflectedVariance = new Vector3f();
			Vector3f refractedVariance = new Vector3f();
			
			desc.getRTObject().getShader().computeMaxRays(env, reflectedRay, reflectedVariance, refractedRay, refractedVariance);
			
			if(sum(refractedRay.color) > 0) {
				Color3f newrefCol = new Color3f(refractedRay.getColor());
				newrefCol.x *= ray.getColor().x;
				newrefCol.y *= ray.getColor().y;
				newrefCol.z *= ray.getColor().z;
				refractedRay.getColor().set(newrefCol);
				
				//shootPhoton(depth+1, refractedRay, desc, desc.getRTObject());
				shootPhoton(depth+1, refractedRay, context, desc, null);
			} else {
			
				Color3f newCol = new Color3f(reflectedRay.getColor());
				newCol.x *= ray.getColor().x;
				newCol.y *= ray.getColor().y;
				newCol.z *= ray.getColor().z;
				reflectedRay.getColor().set(newCol);

				float colorWeightSum = sum(newCol);

				if (colorWeightSum > 0.0f) {
					shootPhoton(depth+1, reflectedRay, context, desc, desc.getRTObject());
				}
			}
		}
	}
	
	
	
	private void insertPhoton(Ray ray, Point3f pos){
		double posa[] = {pos.x, pos.y, pos.z};
		Vector3f dir = new Vector3f(ray.direction);
		dir.negate();
		Node newNode = new Node(new Color3f(ray.color), new Point3f(pos), dir);
		try {
			photonMap.insert(posa, newNode);//ray.color);
		} catch (KeySizeException e) {
			e.printStackTrace();
		} catch (KeyDuplicateException e) {
			try {
				Node temp = (Node)photonMap.search(posa);
				photonMap.delete(posa);
				temp.col.add(ray.color);
				photonMap.insert(posa, temp);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

/*	
	private void refreshInput(ShadingEnvironment input, Ray ray, IntersectionDescription desc) {
		if (desc==null) {
			return;
		}
		input.localPoint.set(desc.getLocalPoint());
		input.point.set(desc.getPoint());
		input.normal.set(desc.getNormal());
		input.view.set(ray.getDirection());
		input.view.negate();
//		input.view.x = ray.getOrigin().x - desc.getPoint().x;
//		input.view.y = ray.getOrigin().y - desc.getPoint().y;
//		input.view.z = ray.getOrigin().z - desc.getPoint().z;
		input.view.normalize();
		input.photonDirection = false;
		input.solid = true;
		if (desc.getMedium()==null) {
			input.iorRatio = m_lastIOR/1.0f;
			m_lastIOR = 1.0f;
		} else {
			input.iorRatio = m_lastIOR/desc.getMedium().getIndexOfRefraction();
			m_lastIOR = desc.getMedium().getIndexOfRefraction();
		}
		input.uv.set(desc.getUVCoordinate());
		input.dpdu.set(desc.getTangenteU());
		input.dpdv.set(desc.getTangenteV());
	}
*/	
	
	
	private void refreshEnvironment(ShadingEnvironment env, Ray ray, 
			RayContext context, IntersectionDescription desc) {
		if (desc==null) {
			return;
		}
		env.localPoint.set(desc.getLocalPoint());
		env.point.set(desc.getPoint());
		env.normal.set(desc.getNormal());
		env.view.set(ray.getDirection());
		env.view.negate();
		env.photonDirection = false;
		env.solid = desc.getRTObject().isSolid();
		
//		System.out.println("#env ior ration:"+context.iorRatio);
//		env.iorRatio = context.iorRatio;
		
		if (desc.getRTObject().getUserData().isInside) {
//			System.out.println("isInside cur ior:"+context.getCurrentIOR());
			if (context.isLastMaterial(desc.getRTObject())) {
				env.iorRatio = context.getExitingIORRation();
			} else {
//				System.out.println("special case: material not last on stack");
			}
		} else {
//			System.out.println("next ior:"+desc.getMedium().getIndexOfRefraction()+" cur ior:"+context.getCurrentIOR());
//			System.err.println("object:"+desc.getRTObject());
			if (desc.getRTObject().getMedium()==null) {
				env.iorRatio = 1.0f;
			} else {
				env.iorRatio = context.getCurrentIOR()/
					desc.getRTObject().getMedium().getIndexOfRefraction();
			}
		}
//		System.out.println("ratio:"+env.iorRatio);
		
		
//		if (desc.getMedium()==null) {
//			env.iorRatio = m_lastIOR/1.0f;
//			m_lastIOR = 1.0f;
//		} else {
//			env.iorRatio = m_lastIOR/desc.getMedium().getIndexOfRefraction();
//			m_lastIOR = desc.getMedium().getIndexOfRefraction();
//		}
		
		
		
		env.uv.set(desc.getUVCoordinate());
		env.dpdu.set(desc.getTangenteU());
		env.dpdv.set(desc.getTangenteV());
	}
	

	
	public boolean hasFixedLightProcessor() {
		return false;
	}

	public void setLightProcessor(LightProcessor model) {
		m_lightModel = model;
	}

	public LightProcessor getLightProcessor() {
		return m_lightModel;
	}

	public void setRecursionDepth(int value) {
		recursionDepth = value;
	}

	public int getRecursionDepth() {
		return recursionDepth;
	}
	
	public void setPhotonCount(int photonCount) {
		nr_photons = photonCount;
	}
	
	public void setRang(int range) {
		this.range = range;
	}
	
	public void clear() {
		photonMap = new KDTree(3);
	}
/*
	public void setIntersectionProcessor(IntersectionProcessor processor) {
		m_intersectionProcessor = processor;
	}
*/
	public void prepareRayProcessor(RTScene scene, IntersectionProcessor m_intersectionProcessor) {
		this.scene = scene;
		this.m_intersectionProcessor = m_intersectionProcessor; 
		m_lightModel.prepareLightProcessor(scene, m_intersectionProcessor);
		lights = Raytracer.getLights(scene);
		compluteLightProbs(lights);
	}

	public void getColorFromRay(Ray ray, Color4f color4) {
		IntersectionDescription desc = new IntersectionDescription();
		Color3f m_rayColor = new Color3f();
		m_lastIOR = 1.0f;
		
		
		// global machen um zeit zu sparen
		RayContext context = new RayContext();
		context.initializeContext();
		
		if (m_intersectionProcessor==null) { 
			color4.set(0.0f,0.0f,0.0f,0.0f);
			return;
		}	

		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray,context,desc)) {
			// no intersection for this ray -> return transparency
			color4.set(0.0f,0.0f,0.0f,0.0f);
			return;
		}
		
		// Farbe des 1. SchnittPunktes berechnen
		if (desc.getRTObject() instanceof RTFakeObject) {
			((RTFakeObject)desc.getRTObject()).getColor(ray, desc, m_rayColor);
			return; // nicht return sondern weiter???
		}
	
		ShadingEnvironment env = new ShadingEnvironment();
		getRegionLight(desc, m_rayColor, ray, context, env);

		traceEyeRay(1, ray, context, desc, env, /*null*/desc.getRTObject() , m_rayColor);
		//traceImportanceRay(1, ray, desc, null, m_rayColor);
		
		color4.x = m_rayColor.x;
		color4.y = m_rayColor.y;
		color4.z = m_rayColor.z;
		color4.w = 1.0f;
	}
	
	
	private void getRegionLight(IntersectionDescription desc, Color3f color, Ray ray, RayContext context, ShadingEnvironment env) {
		Object[] photons;
		float r2 = 0;
		Node n = null;
		
		Point3f p = desc.getPoint();
		double[] pos = {p.x, p.y, p.z};
		
		try {
			photons = photonMap.nearest(pos, range);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (KeySizeException e) {
			e.printStackTrace();
			return;
		}
		
		for(int i = 0; i < photons.length; i++){
			//Color3f farbe = (Color3f)photons[i];
			n = (Node)photons[i];
			//System.out.println("summiere Farbe " + i + " " + farbe);

			//check mit punktprodukt
			if(n.dir.dot(desc.getNormal()) >= 0) {
				color.add(n.col);
			} else {
				//System.out.println("�berspringe");
			}
			
			//schnittpunkttest
/*			Shadows shadows = new Shadows(m_intersectionProcessor);
  			float length = n.pos.distance(desc.getPoint());
			Ray tempRay = new Ray();
			Vector3f dir = new Vector3f();
			dir.sub(n.pos, desc.getPoint());
			dir.normalize();
			tempRay.direction.set(dir);
			tempRay.origin.set(desc.getPoint());
			if(n.dir.dot(desc.getNormal()) >= 0) {
				if(!shadows.shadowRay(tempRay, length, null)) {
					color.add(n.col);
				}
			} else {
				//System.out.println("�berspringe");
			}
*/			
		}
		if(n != null){
			r2 = n.pos.distanceSquared(p);
		}
		color.scale(1 / (r2 * Math2.M_PI));
		
		
//		ShadingEnvironment env = new ShadingEnvironment();
		refreshEnvironment(env, ray, context, desc);

		Vector3f in = new Vector3f(ray.direction);
		in.normalize();
		in.negate();

		Color3f bsdf = new Color3f();
		desc.getRTObject().getShader().computeBSDF(env, in, env.normal, true, bsdf);
		color.x *= bsdf.x;
		color.y *= bsdf.y;
		color.z *= bsdf.z;
	}
	
	
	private Vector3f reflectedVariance = new Vector3f();
	private Vector3f refractedVariance = new Vector3f();
	private Ray reflectedRay = new Ray();
	private Ray refractedRay = new Ray();
	
	private void traceEyeRay(int depth, Ray ray, RayContext context, IntersectionDescription desc,
			ShadingEnvironment env, RTObject exclude, Color3f color) {
		
		if (desc.getRTObject() instanceof RTFakeObject) {
			((RTFakeObject)desc.getRTObject()).getColor(ray, desc, color);
			return; // nicht return sondern weiter???
		}
	
//		getRegionLight(desc, color, ray);
		
		
//		ShadingEnvironment env = new ShadingEnvironment();
//		refreshInput(env, ray, desc);
		
		desc.getRTObject().getShader().computeMaxRays(env, reflectedRay, reflectedVariance, refractedRay, refractedVariance);
		
		if(sum(reflectedVariance) < 0.02f) {
			// specular
			
			IntersectionDescription newdesc = new IntersectionDescription();
			Color3f refl_rayColor = new Color3f();
			if (!m_intersectionProcessor.getFirstIntersectionDescription(reflectedRay, /*null*/ context, newdesc)) {
				// no intersection for this ray -> return transparency
				refl_rayColor.set(0.0f,0.0f,0.0f);
				return;
			}
//			ShadingEnvironment newenv = new ShadingEnvironment();
//			refreshInput(newenv, reflectedRay, newdesc);
//
//			Vector3f in = new Vector3f(reflectedRay.direction);
//			in.normalize();
//			in.negate();
//
//			Color3f bsdf = new Color3f();
//			newdesc.getShader().computeBSDF(newenv, in, newenv.normal, true, bsdf);
			ShadingEnvironment newenv = new ShadingEnvironment(); 
			getRegionLight(newdesc, refl_rayColor, reflectedRay, context, newenv);
			
			refl_rayColor.x *= reflectedRay.color.x;
			refl_rayColor.y *= reflectedRay.color.y;
			refl_rayColor.z *= reflectedRay.color.z;				
			
			color.add(refl_rayColor);
			if(depth < recursionDepth_eye){
				traceEyeRay(depth+1, reflectedRay, context, newdesc, newenv, newdesc.getRTObject(), color);
			}
		} else
		
		if(sum(refractedRay.color) > 0) {
//			tracetrans(depth+1, refractedRay, color);
			IntersectionDescription newdesc = new IntersectionDescription();
			Color3f refr_rayColor = new Color3f();
			if (!m_intersectionProcessor.getFirstIntersectionDescription(refractedRay, /*null*/context, newdesc)) {
				// no intersection for this ray -> return transparency
				refr_rayColor.set(0.0f,0.0f,0.0f);
				return;
			}
			ShadingEnvironment newenv = new ShadingEnvironment();
			getRegionLight(newdesc, refr_rayColor, refractedRay, context, newenv);
			
			refr_rayColor.x *= refractedRay.color.x;
			refr_rayColor.y *= refractedRay.color.y;
			refr_rayColor.z *= refractedRay.color.z;
			
			color.add(refr_rayColor);
			
			if(depth < recursionDepth_eye){
				traceEyeRay(depth+1, refractedRay, context, newdesc, newenv, /*null*/ newdesc.getRTObject(), color);
			}
			
		}
		
		
/*		Point3f p = desc.getPoint();
		Object[] photons;

		double[] pos = {p.x, p.y, p.z};
		try {
			photons = photonMap.nearest(pos, (int)range);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (KeySizeException e) {
			e.printStackTrace();
			return;
		}
		
		float r2 = 0;
		int max = 0;
		Node n = null;
		for(int i = 0; i < photons.length; i++){
			//Color3f farbe = (Color3f)photons[i];
			n = (Node)photons[i];
			//System.out.println("summiere Farbe " + i + " " + farbe);

			if(n.dir.dot(desc.getNormal()) >= 0) {
				color.add(n.col);
			} else {
				//System.out.println("�berspringe");
			}
		}
		if(n != null){
			r2 = n.pos.distanceSquared(p);
		}
		color.scale(1 / (r2 * Math2.M_PI));
		//System.out.println("max bei: "+ max + " " + photons.length);
		
		
		ShadingEnvironment env = new ShadingEnvironment();
		refreshInput(env, ray, desc);

		Vector3f in = new Vector3f(ray.direction);
		in.normalize();
		in.negate();

		Color3f bsdf = new Color3f();
		desc.getShader().computeBSDF(env, in, env.normal, true, bsdf);
		color.x *= bsdf.x;
		color.y *= bsdf.y;
		color.z *= bsdf.z;
*/		
		
//		Color3f nextcol = new Color3f();
//		nextref(ray, desc, nextcol);
//		color.add(nextcol);
		
	}

	private class Node {
		public Color3f  col;
		public Point3f  pos;
		public Vector3f dir;
		
		public Node(Color3f col, Point3f pos, Vector3f dir) {
			this.col = col;
			this.pos = pos;
			this.dir = dir;
		}
	}
}
