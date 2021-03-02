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
import de.grogra.ray.shader.ShadingEnvironment;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public class PathTracerMT implements RayProcessor {

	private final static boolean USE_MEMORY_POOL = true;
	
	private int m_depthLimit = 4; // this will be overwritten from Raytracer
	
	private IntersectionProcessor m_intersectionProcessor = null;
	private LightProcessor        m_lightProcessor        = null;
	
	private MemoryPool m_memoryPool;

	private IntersectionDescription m_desc  = new IntersectionDescription();
	
	private final Color3f m_rayColor = new Color3f();
	
	private PathtracingStrategy m_strategy = new PathtracingStrategy5();
	
	private final RayContext   m_rayContext     = new RayContext();
	private ShadingEnvironment m_curEnvironment = new ShadingEnvironment();
	
	private int m_pathCount = 20;
	
	
	public PathTracerMT() {
		super();
		setLightProcessor(new DefaultLightProcessor());
	}
	


	
	
	public boolean hasFixedLightProcessor() {
		return false;		
	}


	public void setLightProcessor(LightProcessor model) {
		m_lightProcessor = model;		
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
	
	
	public void setPathCount(int value) {
		if ((value>=1)&&(value<10000)) {
			m_pathCount = value;
		}
	}
	
	
	public int getPathCount() {
		return m_pathCount;
	}
	
	
	public void prepareRayProcessor(RTScene scene,IntersectionProcessor processor) {
		m_memoryPool = MemoryPool.getPool();
		m_intersectionProcessor = processor;
		m_lightProcessor.prepareLightProcessor(scene,m_intersectionProcessor);
	}

	
	public void getColorFromRay(Ray ray,Color4f color) {
		
		m_strategy.getColorFromRay(ray,color);

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


	private interface PathtracingStrategy {
		public void getColorFromRay(Ray ray,Color4f color);		
	}
	
	
	private class PathtracingStrategy5 implements PathtracingStrategy {

		private final static float WEIGHT_SUM_MIN = 0.0f;
		
		private float m_scalar1;
		private float m_scalar2;
		private Vector3f m_weight = new Vector3f();
		
		private final Vector3f m_density = new Vector3f();
		private final Vector3f m_densitySum = new Vector3f();
		
		
		private final Ray     m_randomRay    = new Ray();
		private final Color3f m_pathColor    = new Color3f();
		private final Color3f m_diffuseColor = new Color3f();
		
		private final IntersectionDescription m_localDesc = new IntersectionDescription();
		
		private ShadingEnvironment m_firstEnvironment = new ShadingEnvironment();
		
		private float m_weightAverage;
		
		
		public void getColorFromRay(Ray ray, Color4f color) {
			
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
			
			// test for intersection		
			if (!m_intersectionProcessor.getFirstIntersectionDescription(ray,m_rayContext,m_desc)) {
				// no intersection for this ray -> return transparency
				color.set(0.0f,0.0f,0.0f,0.0f);
				return;
			}
			
			// test if the intersected object is a fake object e.g. sky object then ...
			if (m_desc.getRTObject() instanceof RTFakeObject) {
				// if intersected object is a unphysical fake object
				// -> return object color and abort recursion
				((RTFakeObject)m_desc.getRTObject()).getColor(ray,m_desc,m_diffuseColor);
				color.x = m_diffuseColor.x;
				color.y = m_diffuseColor.y;
				color.z = m_diffuseColor.z;
				color.w = 1.0f;
				return;
			}
			
			// debugging log
			// TODO remove this
			Debug3d.logFiniteRay(ray,m_desc.getT(),1.0f);
			Debug3d.logNormal(m_desc.getPoint(),m_desc.getNormal());
						
			//--- calculate diffuse color of first ray ------------------------
			
			// determine direct light illumination
			// TODO get input from memory pool
			m_firstEnvironment.rays.clear();
			if (!m_desc.getRTObject().getUserData().isInside) {
				m_lightProcessor.getLightRays(ray,m_desc,m_firstEnvironment.rays);
			}
			refreshEnvironment(m_firstEnvironment,ray,m_rayContext,m_desc);
			// debug
			// log light rays
			// TODO remove this
			for (int i=0;i<m_firstEnvironment.rays.size();i++) {
				Debug3d.logDirectLightRay(m_firstEnvironment.rays.rays[i]);
			}
			// calculate color
			m_desc.getRTObject().getShader().getShadingColor(m_firstEnvironment,m_diffuseColor);
			m_rayColor.set(0.0f,0.0f,0.0f);
			m_densitySum.set(1.0f,1.0f,1.0f);
			int path_count = getPathCount();
			
			if (getRecursionDepth()>0) {
				
				m_densitySum.set(0.0f,0.0f,0.0f);
				
				// get first random rays
				for (int i=0;i<path_count;i++) {
					
					// get reflected or refracted random ray
					m_desc.getRTObject().getShader().generateRandomRay(
							m_firstEnvironment,m_randomRay);
					
					// reset attributes
					m_weight.set(m_randomRay.color);
					m_density.set(m_randomRay.directionDensity,
							m_randomRay.directionDensity,m_randomRay.directionDensity);
					m_pathColor.set(0.0f,0.0f,0.0f);
					
					if  ((m_weight.x+m_weight.y+m_weight.z)<WEIGHT_SUM_MIN) {
						// TODO
						// is this mathematical correct?
						i--;
						continue;
					}
				
					m_scalar1 = 
						m_desc.getNormal().x*ray.getDirection().x+
						m_desc.getNormal().y*ray.getDirection().y+
						m_desc.getNormal().z*ray.getDirection().z;
					m_scalar2 = 
						m_desc.getNormal().x*m_randomRay.getDirection().x+
						m_desc.getNormal().y*m_randomRay.getDirection().y+
						m_desc.getNormal().z*m_randomRay.getDirection().z;
					
					if ((m_scalar1*m_scalar2)<=0) {

						m_rayContext.excludeObject = m_desc.getRTObject();
						if (!m_intersectionProcessor.getFirstIntersectionDescription(m_randomRay,m_rayContext,m_localDesc)) {
							// debugging log
							// TODO remove this
							m_weightAverage = (m_weight.x+m_weight.y+m_weight.z)/3.0f;
							Debug3d.logInfiniteRay(m_randomRay,m_weightAverage);
						} else {
							tracePath(2,m_randomRay,m_rayContext,m_localDesc,m_pathColor,m_weight);
						}

					} else {
							
						RTObject obj_memo = m_desc.getRTObject();
						int stackIndex = -1;
						if (obj_memo.isSolid()) {
							obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
							if (!obj_memo.getUserData().isInside) {
								// ray is leaving 
								// -> ignore object in intersection calculation
								m_rayContext.excludeObject = obj_memo;
								// -> test if object is last in material stack 
								//    ... if not set ior ration to 1
								if (m_rayContext.isLastMaterial(obj_memo)) {
									m_rayContext.popMaterial();
								} else {
									// not last
									stackIndex = m_rayContext.deleteMaterial(obj_memo);
								}
							} else {
								// ray enters object 
								// -> ignore no object
								m_rayContext.excludeObject = null;
								// -> add object material to material stack
								m_rayContext.pushMaterial(obj_memo);
							}
						} else {
							m_rayContext.excludeObject = obj_memo;
						}
						
						if (!m_intersectionProcessor.getFirstIntersectionDescription(m_randomRay,m_rayContext,m_localDesc)) {
							// debugging log
							// TODO remove this
							m_weightAverage = (m_weight.x+m_weight.y+m_weight.z)/3.0f;
							Debug3d.logInfiniteRay(m_randomRay,m_weightAverage);
						} else {
							tracePath(2,m_randomRay,m_rayContext,m_localDesc,m_pathColor,m_weight);
						}
						
						if (obj_memo.isSolid()) {
							if (!obj_memo.getUserData().isInside) {
								// ray is leaving 
								// -> test if object was (former line commented with 1) 
								//    last in material stack 
								//    ... if not insert material into stack
								if (stackIndex==-1) {
									m_rayContext.pushMaterial(obj_memo);
								} else {
									m_rayContext.insertMaterial(obj_memo,stackIndex);
								}


							} else {
								// ray enters object 
								// -> pop object material from material stack
								m_rayContext.popMaterial();
							}
							obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
						}
						
					}
					
					m_densitySum.add(m_density);
					m_rayColor.x += m_pathColor.x*m_weight.x;
					m_rayColor.y += m_pathColor.y*m_weight.y;
					m_rayColor.z += m_pathColor.z*m_weight.z;
					
				}
			}
			
			m_rayColor.x /= path_count;
			m_rayColor.y /= path_count;
			m_rayColor.z /= path_count;
			
			color.x = m_diffuseColor.x+m_rayColor.x;
			color.y = m_diffuseColor.y+m_rayColor.y;
			color.z = m_diffuseColor.z+m_rayColor.z;
			color.w = 1.0f;
			
		}
		
		
		private void tracePath(int depth,Ray ray,RayContext context,
				IntersectionDescription desc,Color3f color,Vector3f weight) {
			
			// test if the intersected object is a fake object e.g. sky object then ...
			if (desc.getRTObject() instanceof RTFakeObject) {
				// if intersected object is a unphysical fake object
				// -> return object color and abort recursion
				((RTFakeObject)desc.getRTObject()).getColor(ray,desc,color);

				// debugging log
				// TODO remove this
				m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
				Debug3d.logInfiniteRay(ray,m_weightAverage);
				
				return;
			}
			
			//--- calculate diffuse color -----------------------------------------
			
			// debugging log
			// TODO remove this
			m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
			Debug3d.logFiniteRay(ray,desc.getT(),m_weightAverage);
			Debug3d.logNormal(desc.getPoint(),desc.getNormal());			
			
			// get diffuse color
			m_curEnvironment.rays.clear();
			if (!desc.getRTObject().getUserData().isInside) {
				m_lightProcessor.getLightRays(ray,desc,m_curEnvironment.rays);
			}
			refreshEnvironment(m_curEnvironment,ray,context,desc);
			// debug
			// log light rays
			// TODO remove this
			for (int i=0;i<m_curEnvironment.rays.size();i++) {
				Debug3d.logDirectLightRay(m_curEnvironment.rays.rays[i]);
			}
			// calculate color
			desc.getRTObject().getShader().getShadingColor(m_curEnvironment,color);
			
			// --- get next random ray ----------------------------------------
			if (depth<=getRecursionDepth()) {
				
				Ray local_ray;
				Vector3f random_weight;
				if (USE_MEMORY_POOL) {
					local_ray     = m_memoryPool.newRay();
					random_weight = m_memoryPool.newVector3f();
				} else {
					local_ray     = new Ray();
					random_weight = new Vector3f();
				}
				
				// get reflected or refracted random ray				
				desc.getRTObject().getShader().generateRandomRay(m_curEnvironment,local_ray);
				
				// reset attributes
				random_weight.set(local_ray.color);

				if ((local_ray.color.x*weight.x+
					 local_ray.color.y*weight.y+
					 local_ray.color.z*weight.z)>WEIGHT_SUM_MIN) {
					
					Color3f additional_color;
					Vector3f weight_memo;
					if (USE_MEMORY_POOL) {
						additional_color = m_memoryPool.newColor3f();
						additional_color.set(0.0f,0.0f,0.0f);
						weight_memo      = m_memoryPool.newVector3f();
					} else {
						additional_color = new Color3f();
						weight_memo      = new Vector3f();
					}
					
					m_scalar1 = 
						desc.getNormal().x*ray.getDirection().x+
						desc.getNormal().y*ray.getDirection().y+
						desc.getNormal().z*ray.getDirection().z;
					m_scalar2 = 
						desc.getNormal().x*local_ray.getDirection().x+
						desc.getNormal().y*local_ray.getDirection().y+
						desc.getNormal().z*local_ray.getDirection().z;
					
					weight_memo.set(weight);
					weight.x *= local_ray.color.x;
					weight.y *= local_ray.color.y;
					weight.z *= local_ray.color.z;	
					if ((m_scalar1*m_scalar2)<=0) {
						
						if (!desc.getRTObject().getUserData().isInside) {
							context.excludeObject = desc.getRTObject();
						} else {
							context.excludeObject = null;
						}
			
						if (!m_intersectionProcessor.getFirstIntersectionDescription(local_ray,context,desc)) {
							// debugging log
							// TODO remove this
							m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
							Debug3d.logInfiniteRay(local_ray,m_weightAverage);							
						} else {
							tracePath(depth+1,local_ray,context,desc,additional_color,weight);
						}
						
					} else {
						
						RTObject obj_memo = desc.getRTObject();
						int stackIndex = -1;
						if (obj_memo.isSolid()) {
							obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
							if (!obj_memo.getUserData().isInside) {
								// ray is leaving 
								// -> ignore object in intersection calculation
								m_rayContext.excludeObject = obj_memo;
								// -> test if object is last in material stack 
								//    ... if not set ior ration to 1
								if (m_rayContext.isLastMaterial(obj_memo)) {
									m_rayContext.popMaterial();
								} else {
									// TODO not last
									stackIndex = m_rayContext.deleteMaterial(obj_memo);
								}
							} else {
								// ray enters object 
								// -> ignore no object
								m_rayContext.excludeObject = null;
								// -> add object material to material stack
								m_rayContext.pushMaterial(obj_memo);
							}
						} else {
							m_rayContext.excludeObject = obj_memo;
						}
						
						if (!m_intersectionProcessor.getFirstIntersectionDescription(local_ray,context,desc)) {
							// debugging log
							// TODO remove this
							m_weightAverage = (weight.x+weight.y+weight.z)/3.0f;
							Debug3d.logInfiniteRay(local_ray,m_weightAverage);							
						} else {
							tracePath(depth+1,local_ray,context,desc,additional_color,weight);
						}
						
						if (obj_memo.isSolid()) {
							if (!obj_memo.getUserData().isInside) {
								// ray is leaving 
								// -> test if object was (former line commented with 1) 
								//    last in material stack 
								//    ... if not insert material into stack
								if (stackIndex==-1) {
									m_rayContext.pushMaterial(obj_memo);
								} else {
									m_rayContext.insertMaterial(obj_memo,stackIndex);
								}


							} else {
								// ray enters object 
								// -> pop object material from material stack
								m_rayContext.popMaterial();
							}
							obj_memo.getUserData().isInside = !obj_memo.getUserData().isInside;
						}
						
					}
					weight.set(weight_memo);					
					color.x += additional_color.x*random_weight.x;
					color.y += additional_color.y*random_weight.y;
					color.z += additional_color.z*random_weight.z;
					
					if (USE_MEMORY_POOL) { 
						m_memoryPool.freeVector3f(weight_memo);
						m_memoryPool.freeColor3f(additional_color);
					}
					
				}
				
				if (USE_MEMORY_POOL) {
					m_memoryPool.freeVector3f(random_weight);
					m_memoryPool.freeRay(local_ray);
				}				
				
			}
			
		}
		
	}
	
	

	
}
