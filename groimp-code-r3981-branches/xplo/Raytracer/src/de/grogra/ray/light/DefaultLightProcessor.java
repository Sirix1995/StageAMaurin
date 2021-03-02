package de.grogra.ray.light;

import javax.vecmath.Vector3d;

import de.grogra.ray.RTLight;
import de.grogra.ray.RTScene;
import de.grogra.ray.Raytracer;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.intersection.IntersectionProcessor;
import de.grogra.ray.util.RayList;
import de.grogra.ray.util.Ray;

public class DefaultLightProcessor implements LightProcessor {

	protected RTLight[] m_lights;
	//private MemoryPool m_pool;
	protected final Vector3d  m_toLight = new Vector3d();
	protected ShadowProcessor m_shadowProcessor;
	
	public void prepareLightProcessor(RTScene scene,
			IntersectionProcessor processor) {
		//System.err.println("DirectLightModel prepareLightModel()::");
		m_lights = Raytracer.getLights(scene);	
		//System.err.println("lights count: "+m_lights.length);
		
		m_shadowProcessor = new Shadows(processor);
		//m_shadowModel = new NoShadows();
		
	}

	
	public int getLightRays(Ray view,IntersectionDescription desc, RayList rays) {
		int added = 0;
		//System.err.println("DirectLightModel getLightRays()::");
		//int cur_offset = offset;
		//int cur_num;
		for (int i=0; i<m_lights.length;i++) {
			//m_toLight.set(m_lights[i].)
			added += m_lights[i].getLightRays(view, desc, m_shadowProcessor,rays);
			//cur_offset += cur_num;
			//System.err.println("DirectLightModel getLightRays()::    " +i +". Lichtquelle mit Farbe: ???");
		}
		return added;
	}
	
	
	//private class 

}
