package de.grogra.imp3d.glsl.light.shadow;

import de.grogra.imp3d.glsl.light.GLSLAreaLight;
import de.grogra.imp3d.glsl.light.GLSLSkyReflectionLight;
import de.grogra.imp3d.glsl.utility.GLSLCollection;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;

/**
 * Interface for all GLSLLightShaders.
 * @author Konni Hartmann
 */
public class ShadowLightCollection {
	private static final GLSLCollection col = new GLSLCollection();
	
	public static GLSLManagedShader getGLSLManagedObject(Object inp) {
		return col.getGLSLManagedObject(inp);
	}
	
	public static void initMap() {
		col.AddToMap(new GLSLSpotLightShadow());
		col.AddToMap(new GLSLPointLightShadow());
		col.AddToMap(new GLSLDirectionalLightShadow());
		col.AddToMap(new GLSLSkyLightShadow());
		col.AddToMap(new GLSLSkyReflectionLight());
		col.AddToMap(new GLSLAreaLight());
	}
}
