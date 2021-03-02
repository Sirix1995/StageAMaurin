package de.grogra.ray.tracing;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTFakeObject;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.intersection.Intersections;
import de.grogra.ray.light.DefaultLightProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.shader.RTShader.TransparencyInput;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class PathTracerHS implements RayProcessor {

	private final static int LISTSIZE = 1;
	boolean test_Started = false;
	
	
	private int depthLimit = 0;
	
	private final static float LIGHT_WEIGHT_MIN   = 0.02f;
	
	private IntersectionProcessor m_intersectionProcessor = null;
	private LightProcessor            m_lightModel            = null;
	

	private MemoryPool m_memoryPool;
	private IntersectionDescription desc  = new IntersectionDescription();
	
	private final Color3f m_rayColor = new Color3f();

	private float m_lastIOR = 1.0f;
	
	
	private char seed;
	private float totalDensity =0f;
	private int count=0;	
	private RayList tempRayList = new RayList(LISTSIZE);
	private boolean physValid = false;
	
	private int rayCount =0;
	
	
	
	private char tempChar;
	private float tempFloat;
	private int tempInt;
	private Vector3f tempVec = new Vector3f();
	private boolean tempBool = true;
	
	private float searchedDensity;
	private float tempDensity;
	
	
	
	private final static int INTERSECTION_EVALUATION_PARAMS = 
		Intersections.EVALUATE_POINT | 
		Intersections.EVALUATE_NORMAL |
		Intersections.EVALUATE_SHADER |
		Intersections.EVALUATE_TANGET_VECTORS |
		Intersections.EVALUATE_UV_COORDINATES;
	
	
	public PathTracerHS() {
		super();
		setLightProcessor(new DefaultLightProcessor());
	}
	
	public PathTracerHS(int depth) {
		super();
		setLightProcessor(new DefaultLightProcessor());
		
		depthLimit = depth;
	}
	
	
	public void setIntersectionProcessor(IntersectionProcessor processor) {
		m_intersectionProcessor = processor;
		
	}
	
	
	public void prepareRayProcessor(RTScene scene, IntersectionProcessor intersectionProcessor) {
		m_memoryPool = MemoryPool.getPool();
		setIntersectionProcessor(intersectionProcessor);
		m_lightModel.prepareLightProcessor(scene,intersectionProcessor);
		
		seed = Math2.random((char)0);
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
	
	
	
	public void getColorFromRay(Ray ray,Color4f color) {
		
		if (m_intersectionProcessor==null) { 
			color.set(0.0f,0.0f,0.0f,0.0f);
			
			return ;
		}	
		
//		m_intersectionProcessor.setEvaluationParameters(INTERSECTION_EVALUATION_PARAMS);
		
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray,null,desc)) {
			// no intersection for this ray -> return transparency
			color.set(0.0f,0.0f,0.0f,0.0f);
			
			return ;
		}
		
		//initial Setting of the PDF-Probalitity of this ray
		
		
		float rayProbability = traceImportanceRay(1, ray, desc,null, m_rayColor);
		

		color.x = m_rayColor.x;
		color.y = m_rayColor.y;
		color.z = m_rayColor.z;
		color.w = 1.0f;
		
		return;
	}
	

	
	
	private float traceImportanceRay(int depth, Ray ray, 
			IntersectionDescription desc, RTObject exclude, Color3f color) {
		
		
//		m_intersectionProcessor.setEvaluationParameters(INTERSECTION_EVALUATION_PARAMS);
		
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray,/*exclude*/null,desc)) {
			// no intersection for this ray -> return transparency
			color.set(0.0f,0.0f,0.0f);

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
		
		float rayProbability=1.0f;

		if(!physValid) {
			//find out diffuse-color-rays 
			m_lightModel.getLightRays(ray,desc,env.rays);
			//Calculate all lightshares for this Intersectionpoint
			cur_shader.getShadingColor(env,color);	
		}

	

		
		// recursively trace importance ray
		if (depth<depthLimit) {

			//set outgoing Importance Ray
			tempVec.set(env.view);
			
			//generate a List of randomly reflected or refracted Rays
			cur_shader.generateRandomRays(env, tempVec,tempRayList,true,Math2.random(++seed));
			//choose randomly one Ray of this List (the most probable is chosen at most)
			Ray nextRay = getMostProbableRay(tempRayList,Math2.random(++seed));
			
			
			
			//if there was no ray chosen (List empty?) return
			if(nextRay == null) {
				//color.set(0.0f,0.0f,0.0f);

				return 1.0f;
			}
			
			rayCount++;
			

			rayProbability = nextRay.directionDensity;

			
			if(nextRay.directionDensity < 0.0f ) {
				System.err.println("rayProb: " +nextRay.directionDensity);
				rayProbability = 0.0f;
			}
			
			Color3f oldColWeight = new Color3f(nextRay.getColor());
			Color3f newCol = new Color3f();
	
			//Calculate the Colorweight
			tempFloat   = oldColWeight.x + oldColWeight.y + oldColWeight.z;
			
			//If the weight still high enough
			if (tempFloat>LIGHT_WEIGHT_MIN) {
				
				//if Ray was reflected...
				if((desc.getNormal().dot(nextRay.direction) > 0.0f)) {

					
					//trace lightray recursivly excluding the intersected object
					rayProbability *= traceImportanceRay(depth+1, nextRay, desc,desc.getRTObject(),newCol);
					

					nextRay.getColor().x = newCol.x* oldColWeight.x;
					nextRay.getColor().y = newCol.y * oldColWeight.y;
					nextRay.getColor().z = newCol.z * oldColWeight.z;
					
					//Insert reflected or refracted ray into Enviroment-List
					color.add(nextRay.getColor());
					
				}else{
					
					//if ray was refracted und the Object is transparent
					if(/*cur_shader.getShaderFlags()>31){//*/cur_shader.isTransparent()) {
						rayProbability *= traceImportanceRay(depth+1, nextRay, desc,null,newCol);
						
						nextRay.getColor().x = newCol.x * oldColWeight.x;
						nextRay.getColor().y = newCol.y * oldColWeight.y;
						nextRay.getColor().z = newCol.z * oldColWeight.z;
						
						//Insert reflected or refracted ray into Enviroment-List
						color.add(nextRay.getColor());
						
					}

				}
				
				
			} 

		}
		
		return rayProbability;
		
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
		depthLimit = value;
		
	}

	public int getRecursionDepth() {

		return depthLimit;
	}
	
	public void setPhysicalValid(boolean valid){
		
	}
	
	public boolean isPhysicalValid(){
		return physValid;
	}

}
