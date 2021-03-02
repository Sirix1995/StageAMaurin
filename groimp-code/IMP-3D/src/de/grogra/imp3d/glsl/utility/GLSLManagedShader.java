package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;

/**
 * Represents a class of Shaders that need to be recompiled for every property
 * change. (e.g GLSLPhong)
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLManagedShader extends GLSLShader {

	private GLSLManagedShader(OpenGLState glState) {
		super(glState);
	}
	
	public GLSLManagedShader() {
		super(null);
	}
	
	protected ShaderConfiguration config = null;
	
	public void setConfig(ShaderConfiguration config) {
		this.config = config;
	}
	
	public ShaderConfiguration getConfig() {
		return config;
	}
	
	private int material_stamp = -1;

	public void setMaterial_stamp(int material_stamp) {
		this.material_stamp = material_stamp;
	}

	public int getMaterial_stamp() {
		return material_stamp;
	}
	
	@Override
	public void deleteShader(GL gl, boolean javaonly) {
		super.deleteShader(gl, javaonly);
		if(config != null)
			config.cleanUp(gl, javaonly);
	}
	
}
