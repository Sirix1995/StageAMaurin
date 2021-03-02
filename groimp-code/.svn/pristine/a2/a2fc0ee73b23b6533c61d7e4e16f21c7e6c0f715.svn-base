package de.grogra.imp3d.glsl.light.shadow;

import java.util.HashMap;
import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.SunSkyLight;


/**
 * This class is both a collection of shadow-map-prototypes and a cache for instances of
 * shadow-maps referenced by Light Objects. Mapping between Lights and ShadowMaps is 
 * static but is subject to change.  
 * @author Konni Hartmann
 */
public class ShadowMapCollection {

	HashMap<Class, GLSLShadowMap> shadowMapTypes = new HashMap<Class, GLSLShadowMap>();
	HashMap<Light, GLSLShadowMap> shadowMaps = new HashMap<Light, GLSLShadowMap>();

	public ShadowMapCollection() {
		initMap();
	}
	
	public void initMap() {
		addNewShadowMapType(SpotLight.class, new GLSLShadowPerspective());
		addNewShadowMapType(PointLight.class, new GLSLShadowCube());
		
		GLSLShadowMap parallel = new GLSLShadowParallel();
		addNewShadowMapType(DirectionalLight.class, parallel);
		addNewShadowMapType(SunSkyLight.class, parallel);
	}
	
	public int getShadowMapMemoryConsumption() {
		int memory = 0;
		Iterator<GLSLShadowMap> it = shadowMaps.values().iterator();
		while(it.hasNext())
			memory += it.next().estimateSizeInByte();
		return memory;
	}
	
	public void addNewShadowMapType(Class key, GLSLShadowMap map) {
		shadowMapTypes.put(key, map);
	}

	public void cleanUp(OpenGLState glState, boolean javaonly) {
		Iterator<GLSLShadowMap> it = shadowMaps.values().iterator();
		while (it.hasNext()) {
			it.next().delete(glState, javaonly);
		}
	}

	public void removeUnused(OpenGLState glState) {
		Iterator<GLSLShadowMap> it = shadowMaps.values().iterator();
		while (it.hasNext()) {
			GLSLShadowMap map = it.next();
			if(map.GRAPH_STAMP < glState.getGraphStamp()) {
				map.delete(glState, false);
				it.remove();
			}
		}
	}
	
	public GLSLShadowMap getDefaultCachedMap(Light light) {
		GLSLShadowMap smap = shadowMaps.get(light);
		if (smap == null) {
			smap = shadowMapTypes.get(light.getClass());
			smap = smap.getInstance();
			if (smap == null) {
				System.err.println("No default ShadowMap found for Light: "
						+ light);
				return null;
			}
			shadowMaps.put(light, smap);
		}
		return smap;
	}

}
