package de.grogra.imp3d.glsl.material;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.math.Channel;

/**
 * Implementation of RGBAShader. Will share one glsl program for all shaders
 * (using uniform values to change color)
 * 
 * @author Konni Hartmann
 */
public class GLSLRGBAShader extends GLSLMaterial {

//	final static String firstV[] = { "", 
//			"varying vec3 normal;",
//			"varying vec4 pos;",
//			"varying vec2 TexUnit;",
//			"varying vec2 TexUnit2;",
//			"void main() {", "	gl_Position = ftransform();",
//			"	pos = gl_ModelViewMatrix * gl_Vertex;" +
//			"	TexUnit = gl_MultiTexCoord0.st;",
//			"	TexUnit2 = gl_MultiTexCoord1.st;",
//			"	normal = normalize(gl_NormalMatrix * gl_Normal);", "}" };
//
//	@Override
//	protected String[] getVertexShader(Object sh) {
//		return firstV;
//	}

	String diffuse = "";

	@Override
	protected Result[] getAllChannels(Object sha) {
		if (sha instanceof RGBAShader) {
			diffuse = config.registerNewUniform(Result.ET_VEC4);

			Result[] input = { null,
					null,
					new Result("4.0", Result.ET_FLOAT),
					new Result(diffuse + ".rgb", Result.ET_VEC3),
					new Result("0.0", Result.ET_FLOAT),
					new Result("0.0", Result.ET_FLOAT),
					new Result("vec3(0.0)", Result.ET_VEC3),
					new Result(diffuse+".a", Result.ET_FLOAT),
					new Result("0.0", Result.ET_FLOAT),
					new Result("0.0", Result.ET_FLOAT),
					new Result("", Result.ET_UNKNOWN), };

			GLSLChannelMap defInp = getMaterialConfig().getDefaultInputChannel();
			
			input[MaterialConfiguration.IT_POSITION] = defInp.generate(null, getMaterialConfig(), null, Channel.X);
			input[MaterialConfiguration.IT_NORMAL] = defInp.generate(null, getMaterialConfig(), null, Channel.NX);

			return input;
		}
		return null;
	}

	@Override
	public GLSLManagedShader getInstance() {
		return new GLSLRGBAShader();
	}

	@Override
	public Class instanceFor() {
		return RGBAShader.class;
	}

	int loc = -1;
	
	@Override
	protected void setupUniforms(GL gl, GLSLDisplay disp, Object s,
			int shaderProgramNumber) {
		super.setupUniforms(gl, disp, s, shaderProgramNumber);
		loc = gl
		.glGetUniformLocation(shaderProgramNumber, diffuse);		
	}
	
	@Override
		public void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {
			super.setupDynamicUniforms(gl, disp, data, shaderNo);
			assert(data instanceof RGBAShader); 
			RGBAShader rgbs = (RGBAShader) data;
			gl.glUniform4f(loc, rgbs.x, rgbs.y, rgbs.z, 1.f - rgbs.w);
		}
	
	@Override
	public boolean needsRecompilation(Object s) {
		return false;
	}
	
	@Override
	public boolean isOpaque(Object s) {
		assert(s instanceof RGBAShader);
		// XXX: Add eps value here!
		return (((RGBAShader)s).w >= 1.0);
	}
}
