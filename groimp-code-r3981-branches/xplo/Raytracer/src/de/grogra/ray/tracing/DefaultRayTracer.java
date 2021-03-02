package de.grogra.ray.tracing;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTFakeObject;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.debug3d.Debug3d;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.light.DefaultLightProcessor;
import de.grogra.ray.light.LightProcessor;
import de.grogra.ray.memory.MemoryPool;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class DefaultRayTracer implements RayProcessor {

	private final static boolean USE_MEMORY_POOL = true;
	
	private int m_depthLimit = 8; // this will be overwritten from Raytracer
	
	private final static float LIGHT_VARIANCE_MAX = RTShader.LAMBERTIAN_VARIANCE*3.0f*0.8f; 
	private final static float LIGHT_WEIGHT_MIN   = 0.03f;
	
	private IntersectionProcessor m_intersectionProcessor = null;
	private LightProcessor            m_lightProcessor            = null;
	
	private final RayContext m_rayContext = new RayContext();
	
	private MemoryPool m_memoryPool;

	private IntersectionDescription m_desc  = new IntersectionDescription();
	
	private final Color3f m_rayColor = new Color3f();
	
	private final Vector3f m_weight = new Vector3f();
	private float          m_weightAverage;
	
	private float m_varianceSum;
	private float m_weightSum;
	
	private ShadingEnvironment cur_env = new ShadingEnvironment();
	
	
	public DefaultRayTracer() {
		super();
		setLightProcessor(new DefaultLightProcessor());
	}

	
	public boolean hasFixedLightProcessor() {
		return false;		
	}


	public void setLightProcessor(LightProcessor lightProcessor) {
		m_lightProcessor = lightProcessor;		
	}


	public LightProcessor getLightProcessor() {
		return m_lightProcessor;
	}


	public void setRecursionDepth(int value) {
		if (value>=0) { m_depthLimit = value; }
	}


	public int getRecursionDepth() {
		return m_depthLimit;
	}
	
	
	public void prepareRayProcessor(RTScene scene,
			IntersectionProcessor intersectionProcessor) {
		m_memoryPool = MemoryPool.getPool();
		m_intersectionProcessor = intersectionProcessor;
		m_lightProcessor.prepareLightProcessor(scene,m_intersectionProcessor);
	}
	
	
	public void getColorFromRay(Ray ray,Color4f color) {
		
		// test if intersection processor is defined
		if (m_intersectionProcessor==null) {
			System.err.println("ERROR in Raytracer: " +
					"no intersection processor defined for DefaultRayTracer");
			// no intersection processor set -> return transparency
			color.set(0.0f,0.0f,0.0f,0.0f);
			return;
		}

		// reset		
		m_rayContext.initializeContext();
		m_weight.set(1.0f,1.0f,1.0f);
			
		// test for transparency		
		if (!m_intersectionProcessor.getFirstIntersectionDescription(ray,m_rayContext,m_desc)) {
			// no intersection for this ray -> return transparency
			color.set(0.0f,0.0f,0.0f,0.0f);
			return;
		}
		
		// debugging log
		// TODO remove this
		Debug3d.logFiniteRay(ray,m_desc.getT(),1.0f);
		Debug3d.logNormal(m_desc.getPoint(),m_desc.getNormal());
			
		// calculate color recursively
		recursivelyTraceRay(1,ray,m_rayContext,m_desc,m_weight,m_rayColor);
		color.x = m_rayColor.x;
		color.y = m_rayColor.y;
		color.z = m_rayColor.z;
		color.w = 1.0f;
				
	}
	

	private void recursivelyTraceRay(int depth, Ray ray, RayContext context,
			IntersectionDescription desc, Vector3f weight, Color3f color) {
		
		// test if the intersected object is a fake object e.g. sky object then ...
		if (desc.getRTObject() instanceof RTFakeObject) {
			// if intersected object is a unphysical fake object
			// -> return object color and abort recursion
			((RTFakeObject)desc.getRTObject()).getColor(ray,desc,color);
			return;
		}
		
		//--- calculate diffuse color -----------------------------------------
		
		// determine direct light illumination
		cur_env.rays.clear();
		if (!desc.getRTObject().getUserData().isInside) {
			m_lightProcessor.getLightRays(ray,desc,cur_env.rays);
		}
		refreshEnvironment(cur_env,ray,context,desc);
		
		// debug
		// log light rays
		for (int i=0;i<cur_env.rays.size();i++) {
			Debug3d.logDirectLightRay(cur_env.rays.rays[i]);
		}
				
		// calculate color
		desc.getRTObject().getShader().getShadingColor(cur_env,color);
	
		
		//--- recursively trace relected and refracted rays -------------------
		
		if (depth<=m_depthLimit) {
			
			Ray reflected_ray;
			Color3f reflected_weight;
			Vector3f reflected_variance;
			Ray refracted_ray;
			Color3f refracted_weight;
			Vector3f refracted_variance;
			Vector3f weight_memo;
			
			if (USE_MEMORY_POOL) {
				// allocate variables with memory pool
				// variables for reflection
				reflected_ray      = m_memoryPool.newRay(); 
				reflected_weight   = m_memoryPool.newColor3f(); 
				reflected_variance = m_memoryPool.newVector3f();
				// variables for refraction			
				refracted_ray      = m_memoryPool.newRay();
				refracted_weight   = m_memoryPool.newColor3f(); 
				refracted_variance = m_memoryPool.newVector3f();

				weight_memo        = m_memoryPool.newVector3f();
			} else {
				// variables for reflection
				reflected_ray      = new Ray();
				reflected_weight   = new Color3f();
				reflected_variance = new Vector3f();
				// variables for refraction			
				refracted_ray      = new Ray();
				refracted_weight   = new Color3f();
				refracted_variance = new Vector3f();
				
				weight_memo        = new Vector3f();
			}
			
			desc.getRTObject().getShader().computeMaxRays(cur_env,
					reflected_ray,reflected_variance,
					refracted_ray,refracted_variance);
			// [...]_ray.color represents the weight of this ray
			reflected_weight.set(reflected_ray.color);
			refracted_weight.set(refracted_ray.color);
			weight_memo.set(weight);
			
			RTObject obj_memo = desc.getRTObject();
			
			//-----------------------------------------------------------------
			// trace REFLECTED ray
			//-----------------------------------------------------------------
			m_varianceSum = reflected_variance.x+
						    reflected_variance.y+
						    reflected_variance.z;
			m_weightSum   = reflected_weight.x*weight.x+
							reflected_weight.y*weight.y+
							reflected_weight.z*weight.z;

			if ((m_varianceSum<LIGHT_VARIANCE_MAX) &&
				(m_weightSum>LIGHT_WEIGHT_MIN)) {
								
				//--- 1 set ray context and weight ----------------------------
				// this settings have to be removed after recursion 
				// ... done at 3 
				weight.x *= reflected_weight.x;
				weight.y *= reflected_weight.y;
				weight.z *= reflected_weight.z;
				if (!obj_memo.getUserData().isInside) {
					// if ray is leaving -> ignore object in intersection calculation
					context.excludeObject = obj_memo;
				} else {
					context.excludeObject = null;
				}
				
				//--- 2 get intersection --------------------------------------
				if (!m_intersectionProcessor.
						getFirstIntersectionDescription(reflected_ray,context,
							 desc)) {
					// no intersection for this ray -> return transparency
					
					//debug
					m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
					Debug3d.logInfiniteRay(reflected_ray,m_weightAverage);
					
					reflected_ray.getColor().set(0.0f,0.0f,0.0f);
				} else {		
					// intersection -> calculate color recursively
					
					// debug
					m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
					Debug3d.logFiniteRay(reflected_ray,desc.getT(),m_weightAverage);
					Debug3d.logNormal(desc.getPoint(),desc.getNormal());
					
					recursivelyTraceRay(depth+1,reflected_ray,context,desc,
							weight,reflected_ray.getColor());
				}
				
				//--- 3 reset ray context and weight --------------------------
				weight.set(weight_memo);
				
				// add weighted reflection color to result color
				reflected_ray.getColor().x *= reflected_weight.x;
				reflected_ray.getColor().y *= reflected_weight.y;
				reflected_ray.getColor().z *= reflected_weight.z;
				color.add(reflected_ray.getColor());
				
			}
			
			
			//-----------------------------------------------------------------
			// trace REFRACTED ray 
			//-----------------------------------------------------------------
			m_varianceSum = refracted_variance.x+
							refracted_variance.y+
							refracted_variance.z;
			m_weightSum   = refracted_weight.x*weight.x+
							refracted_weight.y*weight.y+
							refracted_weight.z*weight.z;

			// weight is not too low and the variance is not too height 
			// -> calculate refracted ray
			if ((m_varianceSum<LIGHT_VARIANCE_MAX) &&
				(m_weightSum>LIGHT_WEIGHT_MIN)) {
				
				int stackIndex = -1;

				//--- 1 set ray context and weight ----------------------------
				// this settings have to be removed after recursion 
				// ... done at 3 
				weight.x *= refracted_weight.x;
				weight.y *= refracted_weight.y;
				weight.z *= refracted_weight.z;
				if (obj_memo.isSolid()) {
					obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
					if (!obj_memo.getUserData().isInside) {
						// ray is leaving 
						// -> ignore object in intersection calculation
						context.excludeObject = obj_memo;
						// -> test if object is last in material stack 
						//    ... if not set ior ration to 1
						if (context.isLastMaterial(obj_memo)) {
							context.popMaterial();
						} else {
							// TODO not last
							stackIndex = context.deleteMaterial(obj_memo);
						}
					} else {
						// ray enters object 
						// -> ignore no object
						context.excludeObject = null;
						// -> add object material to material stack
						context.pushMaterial(obj_memo);
					}
				} else {
					context.excludeObject = obj_memo;
				}				
				
				//--- 2 get intersection --------------------------------------
				if (!m_intersectionProcessor.getFirstIntersectionDescription(refracted_ray,context,desc)) {
					// no intersection for this ray -> return transparency
					
					//debug
					m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
					Debug3d.logInfiniteRay(refracted_ray,m_weightAverage);
					
					refracted_ray.getColor().set(0.0f,0.0f,0.0f);
				} else {		
					// intersection -> calculate color recursively
					
					// debug
					m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
					Debug3d.logFiniteRay(refracted_ray,desc.getT(),m_weightAverage);
					Debug3d.logNormal(desc.getPoint(),desc.getNormal());
					
					recursivelyTraceRay(depth+1,refracted_ray,context,desc,weight,refracted_ray.getColor());
				}
								
				//--- 3 reset ray context and weight --------------------------
				
				if (obj_memo.isSolid()) {
					if (!obj_memo.getUserData().isInside) {
						// ray is leaving 
						// -> test if object was (former line commented with 1) 
						//    last in material stack 
						//    ... if not insert material into stack
						if (stackIndex==-1) {
							context.pushMaterial(obj_memo);
						} else {
							context.insertMaterial(obj_memo,stackIndex);
						}


					} else {
						// ray enters object 
						// -> pop object material from material stack
						context.popMaterial();
					}
					obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
				}
				weight.set(weight_memo);
				
				// add weighted reflection color to result color
				refracted_ray.getColor().x *= refracted_weight.x;
				refracted_ray.getColor().y *= refracted_weight.y;
				refracted_ray.getColor().z *= refracted_weight.z;
				color.add(refracted_ray.getColor());
				
			}
			
			if (USE_MEMORY_POOL) {
				m_memoryPool.freeVector3f(weight_memo);
				// free refracted variables
				m_memoryPool.freeVector3f(refracted_variance);
				m_memoryPool.freeColor3f(refracted_weight);
				m_memoryPool.freeRay(refracted_ray);
				// free reflected variables
				m_memoryPool.freeVector3f(reflected_variance);
				m_memoryPool.freeColor3f(reflected_weight);
				m_memoryPool.freeRay(reflected_ray);
			}
			
			
		}
	}
	
	
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
		
		if (desc.getRTObject().getUserData().isInside) {
			if (context.isLastMaterial(desc.getRTObject())) {
				env.iorRatio = context.getExitingIORRation();
			} else {
				
			}
		} else {
			if (desc.getRTObject().getMedium()==null) {
				env.iorRatio = 1.0f;
			} else {
				env.iorRatio = context.getCurrentIOR()/
					desc.getRTObject().getMedium().getIndexOfRefraction();
			}
		}
		
		env.uv.set(desc.getUVCoordinate());
		env.dpdu.set(desc.getTangenteU());
		env.dpdv.set(desc.getTangenteV());
	}
	
}
