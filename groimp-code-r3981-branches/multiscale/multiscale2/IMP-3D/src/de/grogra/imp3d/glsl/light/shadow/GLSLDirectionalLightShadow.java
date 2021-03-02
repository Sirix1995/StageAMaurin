package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.light.GLSLDirectionalLight;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;

/**
 * Implementation of the directional light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLDirectionalLightShadow extends GLSLDirectionalLight {

	final static String ShadowFuncPrologue =
			"float lookup(sampler2DShadow ShadowMap, vec2 offset, vec3 ViewPos, float Epsilon)";
	final static String ShadowFunc = 
			" vec4 Coord = gl_TextureMatrix[2] * vec4(ViewPos, 1.0);\n"+
			" return shadow2D(ShadowMap, Coord.xyz/Coord.w + vec3(offset* Epsilon, 0.0) ).x;";
	
	String shadowSam;

	@Override
	public String getLightFunction() {
		shadowSam = config.registerNewUniform(ShaderConfiguration.T_SAMPLER2DSHADOW);
		config.registerFunc(ShadowFuncPrologue, ShadowFunc);

		String lightFunc = 				
			" vec3 normal = getEyeNormal(norm);\n"+
			" float mul = 0.0;\n"+
			" float texel = 1.0/2048.0;\n"+
			" mul += lookup("+shadowSam+", vec2(0.0,  0.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2(-1.0,  1.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2( 0.0,  1.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2( 1.0,  1.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2(-1.0,  0.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2( 1.0,  0.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2(-1.0, -1.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2( 0.0, -1.0), pos, texel);\n"+
//			" mul += lookup("+shadowSam+", vec2( 1.0, -1.0), pos, texel);\n"+
			" if(mul > 0.0)"+
			" DirectionalLight(normal.rgb, pos.rgb, shininess, " +
								lightDir+", "+
								radientPower+", "+
								col0+", "+
			// "				amb, " +
								"diff, spec);\n"+
			" diff *= mul; spec *= mul;";
			return lightFunc;
	};
	

	@Override
	public void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
		int tex5 = gl.glGetUniformLocation(getShaderProgramNumber(),
		shadowSam);
		gl.glUniform1i(tex5, 7);
	}
	
	@Override
	public GLSLShader getInstance() {
		return new GLSLDirectionalLightShadow();
	}
	
//	@Override
//	public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
//		if(lsc.isShadow()) {
//				GLSLLightShader result = new GLSLDirectionalLightShadow();
//				result.setLightShaderConfiguration(lsc);
//				return result;
//		} else
//			return super.getInstance(lsc);
//	}

	@Override
	public boolean canDisplayShadows() { return true; }

}
