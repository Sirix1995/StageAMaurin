package de.grogra.imp3d.glsl.material;

import de.grogra.imp3d.glsl.utility.GLSLCollection;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;

/**
 * Interface for all GLSLShaders. Activates / compiles and deletes
 * shaderprograms. Holds a list of all GLSLShaders.
 * @author Konni Hartmann
 */
public class MaterialCollection {
	private static final GLSLCollection col = new GLSLCollection();
	
	public static GLSLManagedShader getGLSLManagedObject(Object inp) {
		return col.getGLSLManagedObject(inp);
	}

	public static void initMap() {		
		// add complete Shader
		col.AddToMap(new GLSLPhong());
		col.AddToMap(new GLSLRGBAShader());
		col.AddToMap(new GLSLSunSky());
		col.AddToMap(new GLSLSideSwitchShader());
	}
}
