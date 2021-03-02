package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.ray.physics.Spectrum3f;

/**
 * Convention: If ambient is set to less than -1.0 the pixel should not be lit,
 * only its diffuse color will be used to render it.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLLightShader extends GLSLManagedShader {

//	public abstract GLSLLightShader getInstance(LightShaderConfiguration lsc);
	
	public final static String stdV[] = { 
			"varying vec2 TexCoord;",
			"varying vec2 TexCoord2;", "void main() {",
			"	TexCoord = gl_MultiTexCoord0.st;",
			"	TexCoord2 = gl_MultiTexCoord1.st;",
			"	gl_Position = ftransform();", 
			"}" };
		

	public LightShaderConfiguration getLightShaderConfig() {
		assert(config instanceof LightShaderConfiguration);
		return (LightShaderConfiguration)config;
	}
	
	@Override
	protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data,
			int shaderNo) {
		getLightShaderConfig().setupDynamicUniforms(gl, disp, getShaderProgramNumber());
	}
	
	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		getLightShaderConfig().setupShader(gl, disp, getShaderProgramNumber());
	}
		
	/**
	 * Sets middle part of a light shader (after properties 
	 * have been extracted from deferred-shading-textures
	 * @return
	 */
	public String getLightFunction() { return ""; }

	@Override
	protected String[] getVertexShader(Object sh) {
		return stdV;
	}
	
	/**
	 * Used for working with radiant power
	 */
	protected Spectrum3f spec = new Spectrum3f();

	
	public boolean canDisplayShadows() { return false; }
	
	@Override
	public String toString() {
		return super.toString() + ":"+config.toString();
	}
}
