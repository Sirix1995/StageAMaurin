package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.light.GLSLPointLight;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;

/**
 * Implementation of the point light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLPointLightShadow extends GLSLPointLight {

	final static String ShadowFuncPrologue =
		"float lookup(samplerCube ShadowCube, vec2 offset, vec3 ViewPos, float Epsilon)";
	final static String ShadowFunc = 
		" vec4 Coord = gl_TextureMatrix[2] * vec4(ViewPos + vec3(offset, 0.0) * Epsilon, 1.0);\n"+
		" float depth = textureCube(ShadowCube, Coord.xyz).r;\n"+
		" vec3 absCoord = abs(Coord.xyz);\n"+
		" float max_z = max(absCoord.x, absCoord.y);\n"+
		" max_z = max(max_z, absCoord.z);\n"+
	// XXX: set as uniform!
		" float zFar = 2000.0; float zNear = 0.01;\n"+
		" float val = zFar / (zFar-zNear);\n"+
		" float depth_z = -1.0/max_z * zNear * val + val;\n"+
		" return (depth_z > depth) ? 0.0: 1.0;";

	protected String shadowCube;
	
	@Override
	public String getLightFunction() {
		shadowCube = config.registerNewUniform(ShaderConfiguration.T_SAMPLERCUBE);
		config.registerFunc(ShadowFuncPrologue, ShadowFunc);

		String lightFunc = 				
			" vec3 normal = getEyeNormal(norm);\n"+
			" vec3 eye = vec3 (0.0, 0.0, 1.0);\n"+
			" float mul = 0.0;\n"+
//			" mul += lookup("+shadowCube+", vec2(0.0), pos, 0.001);\n"+
			" mul += lookup("+shadowCube+", vec2(0.0), pos, 0.0);\n"+
			" if(mul > 0.0)"+
			" PointLight( normal.rgb, " +
								"eye, "+
								"pos, "+
								lightPos+", "+
								"shininess, "+
								col0+", "+
								fade_distance+", "+
								fade_power+", "+
								radientPower+", "+								
			// "				amb, " +
								"diff, spec);\n"+
			" diff *= mul; spec *= mul;";
		return lightFunc;
	};

	@Override
	public void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
		int tex5 = gl.glGetUniformLocation(getShaderProgramNumber(),
		shadowCube);
		
		gl.glUniform1i(tex5, 7);
	}
	
	@Override
	public GLSLShader getInstance() {
		return new GLSLPointLightShadow();
	}
	
//	@Override
//	public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
//		if(lsc.isShadow()) {
//				GLSLLightShader result = new GLSLPointLightShadow();
//				result.setLightShaderConfiguration(lsc);
//				return result;
//		} else
//			return super.getInstance(lsc);
//	}
	
	@Override
	public boolean canDisplayShadows() { return true; }

}
