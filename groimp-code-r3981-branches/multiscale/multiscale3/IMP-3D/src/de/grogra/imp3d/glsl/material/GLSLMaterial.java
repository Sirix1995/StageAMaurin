package de.grogra.imp3d.glsl.material;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;

/**
 * Represents a class of Shaders that need to be recompiled for every property
 * change. (e.g GLSLPhong)
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLMaterial extends GLSLManagedShader {

	@Override
	protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data,
			int shaderNo) {
		config.setupDynamicUniforms(gl, disp, getShaderProgramNumber());
	}
	
	/**
	 * Function that loads uniforms of the represented Shader. 
	 * Called by {@link GLSLMaterial#setupShader(GL, GLSLDisplay, Object)}.
	 * Use this to add special uniforms instead of overloading {@link GLSLMaterial#setupShader(GL, GLSLDisplay, Object)}.
	 * Used by SideSwitchShader to initialize both shaders. 
	 * @param gl 
	 * @param disp GLSLDisplay that tries using this shader 
	 * @param s The GroIMP-Shader-Object represented by this shader.
	 */
	protected void setupUniforms(GL gl, GLSLDisplay disp, Object s, int shaderProgramNumber) {
	}
	
	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object s) {
		config.setupShader(gl, disp, getShaderProgramNumber());
		setupUniforms(gl, disp, s, getShaderProgramNumber());
	}

	MaterialConfiguration getMaterialConfig() {
		assert(config instanceof MaterialConfiguration);
		return ((MaterialConfiguration)config);
	}
	
	protected abstract Result[] getAllChannels(Object sha);

	/**
	 * Generate the FragmentShaderCode by using the configuration of the Shader.
	 */
	@Override
	protected String[] getFragmentShader(Object sha) {
		config.clearTmpVariables();
		String[] shader = getMaterialConfig().completeShader(getAllChannels(sha));
		return shader;
	}
	
	/**
	 * @param s
	 *            GroImp Shader that acts as a base for this shader
	 * @return True, if fragments may be discarded by this shader
	 */
	public boolean mayDiscard(Object s) {
		return false;
	}

	public boolean isOpaque(Object s) {
		return true;
	}

}
