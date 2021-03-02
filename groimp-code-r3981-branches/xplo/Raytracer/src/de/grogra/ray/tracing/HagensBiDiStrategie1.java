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
import de.grogra.ray.RTSceneVisitor;
import de.grogra.ray.Raytracer;
import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.light.NoShadows;
import de.grogra.ray.light.ShadowProcessor;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.shader.RTMedium;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.tracing.BidirectionalPathTracer.PathingStrategies;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class HagensBiDiStrategie1 implements PathingStrategies {
	
	private final static boolean SHOW_ADDITION_COLOR = false;
	private final static float LIGHT_VARIANCE_MAX = RTShader.LAMBERTIAN_VARIANCE*3.0f*0.8f; 
	private final static float LIGHT_WEIGHT_MIN   = 0.02f;
	
	private final Color3f m_rayColor = new Color3f();
	
	private final Color4f m_testColor = new Color4f();
	private float m_varianceSum;
	private float m_weightSum;
	
	private float m_lastIOR = 1.0f;
	
	
	
	
	private RayProcessor eyePathRayProcessor  = null;
	
	private int lightPathLength = 5;
	private int eyePathLength = 5;
	
	private int pathLength=10;
	
	private final static int LISTSIZE = 1;
	
	private MemoryPool m_memoryPool;
	private RTScene scene = null;
	private char seed;
	
	private IntersectionProcessor intersectionProcessor = null;
//	private PseudoLightModel            eyePathLightModel            = null;
	
	private IntersectionDescription desc  = new IntersectionDescription();
	
	
	
	
	private float totalDensity =0f;
	private int count=0;
	private RayList tempRayList = new RayList(LISTSIZE); 
	
	private char tempChar;
	private float tempFloat;
	private int tempInt;
	private Vector3f tempVec = new Vector3f();
	private boolean tempBool = true;
	
	private float searchedDensity;
	private float tempDensity;
	
	

	
	
	
	public HagensBiDiStrategie1(){
		eyePathRayProcessor = new PathTracerHS(pathLength);
//		eyePathLightModel = new PseudoLightModel();
	}
	
	
	public Ray getMostProbableRay(RayList rays, int seed){
		
		
		if (rays.getSize()==0) return null;
		if (rays.getSize()==1) return rays.rays[0];
		
		totalDensity = 0f;
		tempDensity=0f;
		tempInt = rays.getSize();
		
		//calculate the sum of all directionDensities
		for(count=0;count<tempInt;count++){
			//if(rays.rays[count].directionDensity > 0.2f) {
				totalDensity += rays.rays[count].directionDensity;
			//}
		}
		
		//choose randomly one value inside this intervall
		tempChar = Math2.random((char)seed);
		tempFloat = ((float)tempChar)/((float) Character.MAX_VALUE);
		searchedDensity = tempFloat*totalDensity;
		
		//calculate and return the chosen ray
		for(count=0; count<tempInt;count++){
			//if(rays.rays[count].directionDensity > 0.2f) {
				tempDensity += rays.rays[count].directionDensity;
				if(tempDensity >= searchedDensity) return rays.rays[count];
			//}
		}
		
		//List is empty
		return null;
	}
	
	
	

	public void getColorFromRay(Ray ray, Color4f color) {
		//System.err.println("BirdirectionalRayprocessor uebernimmt!!!");
		
		if (intersectionProcessor==null) { 
			color.set(0.0f,0.0f,0.0f,0.0f);
			return ;
		}

		
//		RTLight[] lights = getLights();
		RTLight[] lights = null;

		float lightRayDensity=1.0f;
		float importanceRayDensity=1.0f;
		float completePathDensitySquared=0.0f;

		
		Color4f unweightedContributionArray[] = new Color4f[pathLength+1];
		float densitySquaredArray[] = new float[pathLength+1];
		
		for(int i=0; i<(pathLength+1); i++) {
			
			//trace the light subpath
			lightRayDensity = startLightRay(i,lights);
			
			//trace the eye subpath
			unweightedContributionArray[i] = new Color4f();
			startImportanceRay((pathLength-i),ray,unweightedContributionArray[i]);
			
			//collecting the squared Density of this path
			densitySquaredArray[i] = importanceRayDensity*lightRayDensity * importanceRayDensity*lightRayDensity;
			
			//adding to the complete density of all paths
			completePathDensitySquared += densitySquaredArray[i];
			

		}
		
		calculateWeightedContribution(unweightedContributionArray, densitySquaredArray, completePathDensitySquared, color);


//		startLightRay(5,lights,lightRayProb);
//		startImportanceRay(5,ray,color,importanceRayProb);
		
		return ;
	}
	
	
	private void calculateWeightedContribution(Color4f unweightedContArray[],float densityArray[],float completePathDensity, Color4f color ){
		
		color.set(0.0f, 0.0f, 0.0f, 0.0f);
		for(int i=0; i<unweightedContArray.length; i++){
			
			//weighting of the unweighted Colors by factor p² / P  with p = density of path i and P = squared Density of all Paths 0<= i <= s+t+1
			unweightedContArray[i].scale( densityArray[i]/completePathDensity);
			
			//adding the weighted Color
			color.add( unweightedContArray[i]);
		}
		
	}
	
	
	private float startLightRay(int depthLimit, RTLight[] lights) {
		
		
		if(depthLimit==0) {
			
			//egde 0 := p_L0 = 1.0
			return 1.0f;
		}
		
		
		lightPathLength = depthLimit;
		
		
		RayList list = new RayList(1);
		
		float rayDensity = 1.0f;
		
		Vector3f vec = new Vector3f();
		
		//TODO Fragen was passiert mit der Dichte bei meheren Lichtquellen? Wird sie addiert oder gmittelt?
		for(int count = 0; count < lights.length; count++){
			

			lights[count].generateRandomRays(vec, list, true, Math2.random(++seed));
			lights[count].generateRandomOrigins(list, Math2.random(++seed));
			
			Ray lightRay = getMostProbableRay(list,Math2.random(++seed));
			
			if (lightRay==null) break;
			
			//First edge on Lightsource surface:=  p_L1 = P_A(y0)
			rayDensity = lightRay.originDensity;
			
			Color3f newCol = lightRay.getColor();
			
			//all next edges :=  p_Li = Po(y_i-2 -> y_i-1) * p_Li-1
			rayDensity *= traceLightRay(1,lightRay,desc,null,newCol);
			
		}
		
		return rayDensity;
	}
	
	
	private void startImportanceRay(int depthlimit, Ray ray, Color4f color) {
		
		eyePathRayProcessor.setRecursionDepth(depthlimit);
		
		eyePathRayProcessor.getColorFromRay(ray, color);
//		eyePathLightModel.removePseudoLight();

	}
	

	private float traceLightRay(int depth, Ray ray, IntersectionDescription desc, RTObject exclude,Color3f color) {

		//If there was no Intersection...
		if (!intersectionProcessor.getFirstIntersectionDescription(ray,/*exclude*/null,desc)) {
			
			
			return 1.0f;
		}
		
		if (desc.getRTObject() instanceof RTFakeObject) {
			((RTFakeObject)desc.getRTObject()).getColor(ray,desc,color);
			return 1.0f;
		}
		

		// TODO get input from memory pool
		ShadingEnvironment env = new ShadingEnvironment();
	
		refreshInput(env,ray,desc);

		// TODO get shader instance from memory pool
//		RTShader cur_shader = desc.getShader();
		RTShader cur_shader = null;
		
		float rayDensity=1.0f;

		
		// recursively trace lightray
		if (depth<lightPathLength) {
			
			//set outgoing light Ray	
			Vector3f in = new Vector3f(env.view);
			in.normalize();
			
			//generate a List of randomly reflected or refracted Rays
			cur_shader.generateRandomRays(env, in,tempRayList,false,Math2.random(++seed));

			//choose randomly one Ray of this List (the most probable is chosen at most)			
			Ray nextRay = getMostProbableRay(tempRayList,Math2.random(++seed));
			
			//if there was no ray chosen (List empty?) return
			if (nextRay==null) {
				return 1.0f;
			}
			

			rayDensity = nextRay.directionDensity;

			
			
			Color3f newCol = new Color3f(nextRay.getColor());
			newCol.x *= color.x;
			newCol.y *= color.y;
			newCol.z *= color.z;
			
			//Calculate the Colorweight
			float colorWeightSum   = newCol.x +newCol.y + newCol.z;

			//If the weight still high enough		
			if (colorWeightSum>LIGHT_WEIGHT_MIN) {

				//if Ray was reflected...
				if((desc.getNormal().dot(nextRay.direction) > 0.0f)) {
				
					//generate a new Pseudolight (only for Lightmodel not for scenegraph) placed at intersection point
					RTPseudoLight newPseudo = generatePseudoLight(ray,nextRay,env,cur_shader,newCol);
					
					//add Pseudolight to the lightmodel of the eyepathprocessor
//					eyePathLightModel.addPseudoLight(newPseudo);
					
					
					
					//trace lightray recursivly excluding the intersected object
					rayDensity *= traceLightRay(depth+1, nextRay, desc,desc.getRTObject(),newPseudo.color);
					
				}else{
					//if Ray was refracted and Object is transparent...
					if(/*cur_shader.getShaderFlags()>31) { //*/cur_shader.isTransparent()) {
						

						
						//trace lightray recursivly
						rayDensity *= traceLightRay(depth+1, nextRay, desc,null,newCol);
						
					}
				}
				
			}
			
		}

		return rayDensity;
	}		
	
	public RTPseudoLight generatePseudoLight(Ray rayIn,Ray rayOut, ShadingEnvironment env, RTShader shader, Color3f color){
		
		Color3f newCol = new Color3f(color);
		
//		Vector3f dif = new Vector3f(rayIn.origin);
//		float r_2 = dif.lengthSquared();
//		if(r_2!=0) {
//			float correction = 1.0f/(r_2*4*Math2.M_PI);
//			newCol.scale(correction);
//		}
	
//		float f = Math.abs(env.normal.dot(rayIn.direction));
//		newCol.scale(f);
		
		Vector3f newOriginVec = new Vector3f(env.point);
		
		Color3f bsdf = new Color3f(newCol);
		Vector3f in = new Vector3f(rayIn.direction);
		in.normalize();
		in.negate();
		Vector3f out = new Vector3f(rayOut.direction);
		out.normalize();
		
		shader.computeBSDF(env,in,out,false,bsdf);
		newCol.x *= bsdf.x;
		newCol.y *= bsdf.y;
		newCol.z *= bsdf.z;
		
		return new RTPseudoLight(newOriginVec, newCol);
	}
	
	
	
	
	private void refreshInput(ShadingEnvironment input, Ray ray, IntersectionDescription desc) {
		if (desc==null) {
			return;
		}
		input.localPoint.set(desc.getLocalPoint());
		input.point.set(desc.getPoint());
		input.normal.set(desc.getNormal());
		input.view.set(ray.getDirection());
		input.view.negate();
		input.view.normalize();
		input.photonDirection = false;
		input.solid = true;
//		if (desc.getMedium()==null) {
//			input.iorRatio = m_lastIOR/1.0f;
//			m_lastIOR = 1.0f;
//		} else {
//			input.iorRatio = m_lastIOR/desc.getMedium().getIndexOfRefraction();
//			m_lastIOR = desc.getMedium().getIndexOfRefraction();
//		}
		input.uv.set(desc.getUVCoordinate());
		input.dpdu.set(desc.getTangenteU());
		input.dpdv.set(desc.getTangenteV());
	}
	

	
	
	public static RTLight[] getLights(RTScene scene) {
		RTLight[] lights = new RTLight[scene.getLightsCount()];
		GetLightsVisitor visitor = new GetLightsVisitor(lights);
		scene.traversSceneLights(visitor);
		return lights;
	}
	
	

	


	public void prepareRayProcessor(RTScene scene,IntersectionProcessor intersectionProcessor) {
		this.scene = scene;
		
		m_memoryPool = MemoryPool.getPool();

//		eyePathLightModel.prepareLightModel(scene,intersectionProcessor);
//		
//		eyePathRayProcessor.setLightProcessor(eyePathLightProcessor);
//		eyePathRayProcessor.prepareRayProcessor(scene);
		
		seed = Math2.random((char)0);
		
	}

	public void setIntersectionProcessor(IntersectionProcessor processor) {
		intersectionProcessor = processor;
//		eyePathRayProcessor.setIntersectionProcessor(processor);
		
	}

	public void setLightProcessor(LightProcessor model) {
//		eyePathRayProcessor.setLightProcessor(eyePathLightModel);
		
	}
	
	public LightProcessor getLightProcessor() {
//		return eyePathLightProcessor;//lightPathLightModel;
		return null;
	}
	
	
	public int getRecursionDepth() {
		// TODO Auto-generated method stub
		return pathLength;
	}


	public void setRecursionDepth(int value) {
		pathLength = value;
		
	}
	
	
	
	
	
	
	
	private static class GetLightsVisitor implements RTSceneVisitor {

		private RTLight[] m_lights       = null;
		private int       m_lightsOffset = 0;
		
		public GetLightsVisitor(RTLight[] lights) {
			initialize(lights);
		}
		
		public void initialize(RTLight[] lights) {
			m_lights = lights;
			m_lightsOffset = 0;
		}
		
		public void visitObject(RTObject object) {
			if (!(object instanceof RTLight)) { return; }
			
			m_lights[m_lightsOffset++] = (RTLight)object;
		}
		
	}	
	
	
//	public class PseudoLightModel extends DirectLight{
//		
//		RTPseudoLight[] pseudoLights = null;
//		int pseudoCount=0;
//
//		public void addPseudoLight(RTPseudoLight pseudoLight){
//			pseudoLights[pseudoCount++] = pseudoLight;
//			//System.err.println(pseudoCount +". PseudoLight geadded!");
//		}
//		
//		public void removePseudoLight(){
//
//			pseudoCount=0;
//			//System.err.println("PseudoLichter geloescht!");
//		}
//		
//		
//		public void prepareLightModel(RTSceneGraph scene,IntersectionProcessor processor) {
//			//System.err.println("PseudoLightModel prepareLightModel()::");
//			super.prepareLightModel(scene, processor);
//			pseudoLights = new RTPseudoLight[m_lights.length*lightPathLength];
//			
//			//System.err.println("m_lights.length*lightPathLength=" +m_lights.length*lightPathLength);
//			
//		} 
//		
//		public int getLightRays(IntersectionDescription desc, RayList rays) {
//			int added = super.getLightRays(desc, rays);
//			for (int i=0; i<pseudoCount;i++) {
//				added += pseudoLights[i].getLightRays(desc, rays, m_shadowModel);
//			}
//			return (added);
//		}
//		
//		
//	}
	
	
	public class RTPseudoLight implements RTLight {
		
		
		private boolean shadowless = false;
		
//		private ShadowModel noShadows = new NoShadows();
		private final Ray tmpRay1   = new Ray();
		private Ray tmpRay2         = null;
		private float rayLength;
		
		private final Point3f globalOrigin = new Point3f();
		
		public Color3f color = new Color3f();
		

		
		public RTPseudoLight(Tuple3f newOrigin, Color3f newColor){
				globalOrigin.set(newOrigin);
				color.set(newColor);
				tmpRay1.getColor().set(newColor);
				//System.err.println("PseudoLight " +newOrigin +" - " +newColor +" erzeugt");
				
		}
		
		
		public boolean isShadowless() {
			return shadowless;
		}

		public int getLightRays(IntersectionDescription desc, RayList rays/*, ShadowModel shadowModel*/) {
//			ShadowModel cur_shadow_model;
//			if (shadowless) {
//				cur_shadow_model = noShadows;
//			} else {
//				cur_shadow_model = shadowModel;
//			}
//			
//			cur_shadow_model.setNormal(desc.getNormal());
			tmpRay1.getOrigin().set(globalOrigin);
			tmpRay1.getDirection().set(
					desc.getPoint().x-globalOrigin.x,
					desc.getPoint().y-globalOrigin.y,
					desc.getPoint().z-globalOrigin.z);
			tmpRay1.getDirection().normalize();
			
			rayLength = Raytracer.getT(tmpRay1,desc.getPoint());
			
//			if (!cur_shadow_model.shadowRay(tmpRay1,rayLength,null)){//desc.getRTObject())) {
//				tmpRay2 = rays.nextRay();
//				tmpRay2.getDirection().set(
//						-tmpRay1.getDirection().x,
//						-tmpRay1.getDirection().y,
//						-tmpRay1.getDirection().z);
//				tmpRay2.getOrigin().set(desc.getPoint());
//				tmpRay2.getColor().set(tmpRay1.getColor());
//				return 1;
//			}
			
			
			
			return 0;
		}

		public void generateRandomOrigins(RayList out, int seed) {	
//			
//			for(int i=0; i<out.getSize(); i++){
//				out.rays[i].origin.set(globalOrigin);
//			}
		
		}

		public void generateRandomRays(Vector3f out, RayList rays, boolean adjoint, int seed) {
			
//			char c = Math2.random ((char) seed);
//			for (int i = rays.getSize() - 1; i >= 0; i--)
//			{
//				float cost = 1 - (2f / 0x10000) * Math2.random (++c),
//					sint = (float) Math.sqrt (1 - cost * cost);
//				char phi = Math2.random (++c);
//				Ray r = rays.rays[i];
//				r.direction.set (Math2.ccos (phi) * sint,
//								 Math2.csin (phi) * sint, cost);
//				r.direction.normalize ();
//				r.color.set (1, 1, 1);
//				r.directionDensity = Math2.M_1_2PI / 2;
//				
//			}

		}

		public float getDistance(Ray ray) {return 0;}

		public void getIntersectionDescription(int params, IntersectionDescription desc) {	}

		public boolean isShadeable() {	return false;}

		public BoundingVolume getBoundingVolume() {	return null;}


		public float computeBSDF(ShadingEnvironment env, Vector3f in, Vector3f out, boolean adjoint, Color3f bsdf) { return 0;	}


		public boolean isSolid() {
			
			return false;
		}


		public void getIntersectionDescription(IntersectionDescription desc) {
			desc = null;
			
		}


		public Point3f getGloabalOrigin() {
			
			return globalOrigin;
		}


		public Point3f getGlobalOrigin() {
			// TODO Auto-generated method stub
			return null;
		}


		public int getLightRays(Ray view, IntersectionDescription desc, ShadowProcessor shadowProcessor, RayList rays) {
			// TODO Auto-generated method stub
			return 0;
		}


		public float getDistance(Ray ray, RayContext context) {
			// TODO Auto-generated method stub
			return 0;
		}


		public RTMedium getMedium() {
			// TODO Auto-generated method stub
			return null;
		}


		public RTShader getShader() {
			// TODO Auto-generated method stub
			return null;
		}


		public RTObjectUserData getUserData() {
			// TODO Auto-generated method stub
			return null;
		}


		public boolean isConvex() {
			// TODO Auto-generated method stub
			return false;
		}
	}







}


