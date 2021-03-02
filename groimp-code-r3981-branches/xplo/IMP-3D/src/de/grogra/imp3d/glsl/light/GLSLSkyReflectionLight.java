package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.shading.Light;
import de.grogra.math.Sqrt;
import de.grogra.vecmath.Math2;

/**
 * Implementation of the directional light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLSkyReflectionLight extends GLSLLightShader {

	final static String LightPrologue = "void SkyReflect(in vec3 normal,\n"
			+ "in vec3 ecPosition3,\n"
			+ "in float shininess,\n"
			+ "in float radientPower,\n"
			+ "in float diffuseRadientPower,\n"			
			+ "inout vec3 diffuse,\n" 
			+ "inout vec3 specular)";
	final static String LightPerspective = 
		  " vec3 reflVec = normalize((gl_TextureMatrix[2] * vec4(reflect(ecPosition3, normal), 1.0))).xyz;\n";
		final static String LightParallel = 
			  " vec3 reflVec = normalize((gl_TextureMatrix[2] * vec4(reflect(vec3(0.0, 0.0, ecPosition3.z), normal), 1.0))).xyz;\n";

	protected String radientPower, diffuseRadientPower, skyCube, skyDiffCube, invNormalMat;

	@Override
	public String getLightFunction() {
		String lightFunc = 
			" vec3 normal = getEyeNormal(norm);\n"
		  + " SkyReflect(normal, pos, shininess, " + radientPower + ", "
		  + diffuseRadientPower + ", " + "diff, spec);";
		return lightFunc;
	};

	@Override
	public String[] getFragmentShader(Object sh) {
		config.clearTmpVariables();
		radientPower = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		diffuseRadientPower = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		skyCube = config.registerNewUniform(ShaderConfiguration.T_SAMPLERCUBE);
		skyDiffCube = config.registerNewUniform(ShaderConfiguration.T_SAMPLERCUBE);
		
		invNormalMat = config.registerNewUniform(ShaderConfiguration.T_MAT3);
		
		config.registerFunc(LightShaderConfiguration.getNormalSig,
				LightShaderConfiguration.getNormal);
		
		String Light = 
			  " vec3 world_Normal = "+invNormalMat+" * normal;\n"
//			+ " vec3 world_Normal = normal;\n"
			+ " vec3 sampler = textureCube("+skyCube+", reflVec).rgb;\n"
//	    	+ " vec3 samplerDiff = textureCube("+skyDiffCube+", reflVec).rgb;\n"
	    	+ " vec3 samplerDiff = textureCube("+skyDiffCube+", world_Normal).rgb;\n"
			+ " diffuse  += samplerDiff * diffuseRadientPower;\n"
			+ " specular += sampler * radientPower;\n";
		
		config.registerFunc(LightPrologue, (getLightShaderConfig().isPerspective()?LightPerspective:LightParallel) + Light);

		return getLightShaderConfig().completeShader(getLightFunction());
	}

	@Override
	public boolean needsRecompilation(Object data) {
		return false;
	}

	@Override
	public Class instanceFor() {
		return Sky.class;
	}

	Matrix3f invNormal = new Matrix3f();

	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
		int cube = gl.glGetUniformLocation(getShaderProgramNumber(), skyCube);
		gl.glUniform1i(cube, 6);
		cube = gl.glGetUniformLocation(getShaderProgramNumber(), skyDiffCube);
		gl.glUniform1i(cube, 7);
		rpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), radientPower);
		drpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), diffuseRadientPower);
		invMatLoc = gl.glGetUniformLocation(getShaderProgramNumber(), invNormalMat);
	}
	
	int rpLoc = -1, drpLoc = -1, invMatLoc = -1;
	
	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {	
		super.setupDynamicUniforms(gl, disp, data, shaderNo);
		assert (data instanceof LightPos);
		LightPos l = (LightPos) data;

		assert (l.getLight() instanceof Sky);
		Sky sky = (Sky)l.getLight();
		
		float power = 0.5f*Math2.M_1_2PI*disp.getCurrentGLState().getBgPowerDensity();
		
		gl.glUniform1f(rpLoc, power);

		if(sky.getShader() instanceof Light)
			power *= 6.76823384282909f;
		else
			power *= 2.0;

		gl.glUniform1f(drpLoc, power);
		
//			power = sky.getPowerDensity();
//		gl.glUniform1f(loc, 0.079627245028143f*disp.getCurrentGLState().getBgPowerDensity());
//		gl.glUniform1f(loc, 4.827445849111136f*0.079627245028143f*disp.getCurrentGLState().getBgPowerDensity());
			
		Matrix4d worldToView = disp.getCurrentGLState().getWorldToView(); 
		worldToView.getRotationScale(invNormal);
		
		invNormal.invert();
//		invNormal.transpose();
				
		gl.glUniformMatrix3fv(invMatLoc, 1, false, disp.getCurrentGLState().toGLMatrix3f(invNormal), 0);
		
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLSkyReflectionLight();
	}

//	@Override
//	public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
//		GLSLLightShader result = new GLSLSkyReflectionLight();
//		result.setLightShaderConfiguration(lsc);
//		return result;
//	}
}