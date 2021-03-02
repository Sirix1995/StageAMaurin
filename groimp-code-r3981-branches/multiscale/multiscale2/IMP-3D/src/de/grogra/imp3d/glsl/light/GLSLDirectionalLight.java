package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.DirectionalLight;

/**
 * Implementation of the directional light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLDirectionalLight extends GLSLLightShader {
	final static String DirLightPrologue =
			"void DirectionalLight(in vec3 normal,\n"+
								  "in vec3 ecPosition3, \n"+
								  "in float shininess,\n"+
								  "in vec3 lightDir,\n"+
								  "in float radientPower,\n"+
								  "in vec3 col0,\n"+
			// "                      inout vec4 ambient," ,
								  "inout vec3 diffuse,\n"+
								  "inout vec3 specular)";
	final static String DirLightPre =
			"     float nDotVP;\n"+
			"     float nDotHV;\n"+
			"     float pf;\n"+
			"	  vec3 	R;" +

			"	  R = reflect(-lightDir, normal);\n"+
	
			"     nDotVP = max(0.0, dot(normal, lightDir));\n";

			final static String DirLightPerspective = " nDotHV = max(0.0, dot(R, -normalize(ecPosition3)));\n";
			final static String DirLightParallel = " nDotHV = max(0.0, dot(R, vec3(0.0, 0.0, 1.0)));\n"; 

			final static String DirLightPost =
			" if ((nDotHV <= 0.0) || (shininess >= 65504.0))\n"+
			"  pf = 0.0;\n"+
			" else\n"+
			"  pf = pow(nDotHV, shininess);\n"+

			" diffuse  += col0 * nDotVP * radientPower;\n"+
			" specular += col0 * pf * nDotVP * radientPower;";

	protected String lightDir, col0, radientPower;
	
	@Override
	public String getLightFunction() {
		String lightFunc = 				
			" vec3 normal = getEyeNormal(norm);\n"+
   		    " DirectionalLight(normal.rgb, pos, shininess, " +
								lightDir+", "+
								radientPower+", "+
								col0+", "+
			// "				amb, " +
								"diff, spec);";
			return lightFunc;
	};
	
	@Override
	public String[] getFragmentShader(Object sh) {
		config.clearTmpVariables();
		lightDir = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		col0 = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		radientPower = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		
		config.registerFunc(LightShaderConfiguration.getNormalSig, LightShaderConfiguration.getNormal);

		config.registerFunc(DirLightPrologue, DirLightPre + (getLightShaderConfig().isPerspective()?DirLightPerspective:DirLightParallel) +  DirLightPost);

		return getLightShaderConfig().completeShader(getLightFunction());
	}

	@Override
	public boolean needsRecompilation(Object data) {
		return false;
	}

	@Override
	public Class instanceFor() {
		return DirectionalLight.class;
	}

	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
		dirLoc = gl.glGetUniformLocation(getShaderProgramNumber(), lightDir);

		rpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), radientPower);

		colLoc = gl.glGetUniformLocation(getShaderProgramNumber(), col0);
	}

	int dirLoc = -1, rpLoc = -1, colLoc = -1;

	
	@Override
	protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data,
			int shaderNo) {
		LightPos s = null;
		assert (data instanceof LightPos);
		s = (LightPos) data;

		assert (s.getLight() instanceof DirectionalLight);

		DirectionalLight light = (DirectionalLight) s.getLight();

		Vector3d lDir = new Vector3d(-s.getLightDir().x, -s.getLightDir().y, -s
				.getLightDir().z);
		disp.getCurrentGLState().getWorldToView().transform(lDir);

		gl.glUniform3f(dirLoc, (float) lDir.x, (float) lDir.y, (float) lDir.z);

		// Vector3d _dir = new Vector3d(light.lightDir);

		// gl.glUniform4f(amb, (float)light.ambientColor.x,
		// (float)light.ambientColor.y,
		// (float)light.ambientColor.z, (float)light.ambientColor.w);
		this.spec.set (light.getColor());
		this.spec.scale(1./this.spec.integrate());		
		gl.glUniform3f(colLoc, this.spec.x, this.spec.y, this.spec.z);
		
		gl.glUniform1f(rpLoc, light.getPowerDensity());
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLDirectionalLight();
	}
	
//	@Override
//	public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
//		GLSLLightShader result = new GLSLDirectionalLight();
//		result.setLightShaderConfiguration(lsc);
//		return result;
//	}

}