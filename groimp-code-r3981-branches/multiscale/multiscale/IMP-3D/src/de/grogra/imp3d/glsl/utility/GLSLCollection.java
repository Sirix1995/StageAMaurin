package de.grogra.imp3d.glsl.utility;

import java.util.HashMap;


public class GLSLCollection {
	public void AddToMap(GLSLManagedShader sj) {
		map.put(sj.instanceFor(), sj);		
	}

	public GLSLManagedShader getGLSLManagedObject(Object inp) {
		return inp != null ?
				map.get(inp.getClass()) :
				null;
	}
	
	/**
	 * Cache for all Shader-Bases (like Phong, RGBAColor ...)
	 */
	private final HashMap<Class, GLSLManagedShader> map = new HashMap<Class, GLSLManagedShader>();

}
