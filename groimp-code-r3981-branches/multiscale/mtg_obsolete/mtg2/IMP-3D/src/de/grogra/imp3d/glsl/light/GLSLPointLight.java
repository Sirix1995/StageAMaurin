package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Point4d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.vecmath.Math2;

/**
 * Implementation of the point light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLPointLight extends GLSLLightShader {
	final static String PointLightPrologue = "void PointLight(in vec3 normal,\n"
			+ "in vec3 eye,\n"
			+ "in vec3 ecPosition3,\n"
			+ "in vec3 lightPos,\n"
			+ "in float shininess,\n"
			+ "in vec3 col0,\n"
			+ "in float fade_distance,\n"
			+ "in float fade_power,\n" + "in float radientPower,\n" +
			// "             inout vec4 ambient," ,
			"inout vec3 diffuse,\n" + "inout vec3 specular)";
	final static String PointLightPre = " float nDotVP;\n" + " float nDotHV;\n"
			+ " float pf;\n" + " float d;\n" + " float attenuation;\n"
			+ " vec3 	VP;\n" +
			// " vec3	halfVector;\n"+
			" vec3	R;\n" +

			" VP = lightPos - ecPosition3;\n" +

			" d = length(VP);\n" + " VP /= d;\n" +

			" R = reflect(-VP, normal);\n" +
			// " halfVector = normalize(VP + eye);\n"+
			// " halfVector = normalize(VP + normalize(ecPosition3));\n"+

			// " if(fade_power > 1000.0)\n"+
			// "  attenuation = exp(-d/fade_distance);\n"+
			// " else\n"+
			// "  attenuation = 1.0 / ( 1.0 + pow(d/fade_distance, fade_power) );\n"+
			// " attenuation = 1.0 / (d*d);"+
			// TODO: This is the non-physical based factor

			" attenuation = 1.0;" + " nDotVP = max(0.0, dot(normal, VP));\n";
	final static String quadraticFallof = " attenuation /= (d*d);";
	final static String PointLightPerspective = " nDotHV = max(0.0, dot(R, -normalize(ecPosition3)));\n";
	final static String PointLightParallel = " nDotHV = max(0.0, dot(R, vec3(0.0, 0.0, 1.0)));\n";

	final static String PointLightPost =

	" if ((nDotHV <= 0.0) || (shininess >= 65504.0))\n" + "  pf = 0.0;\n"
			+ " else\n" + "  pf = pow(nDotHV, shininess);\n" +

			" diffuse  += col0 * nDotVP * attenuation * radientPower;\n"
			+ " specular += col0 * pf * nDotVP * attenuation * radientPower;";

	protected String lightPos, col0, fade_power, fade_distance, radientPower;

	@Override
	public String getLightFunction() {
		String lightFunc = " vec3 eye = vec3 (0.0, 0.0, 1.0);\n"
				+ " vec3 normal = getEyeNormal(norm);\n"
				+ " PointLight( normal.rgb, " + "eye, " + "pos, " + lightPos
				+ ", " + "shininess, " + col0 + ", " + fade_distance + ", "
				+ fade_power + ", " + radientPower + ", " +
				// "				amb, " +
				"diff, spec);";
		return lightFunc;
	};

	@Override
	public String[] getFragmentShader(Object sh) {
		config.clearTmpVariables();
		lightPos = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		col0 = config.registerNewUniform(ShaderConfiguration.T_VEC3);

		fade_distance = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		fade_power = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		radientPower = config.registerNewUniform(ShaderConfiguration.T_FLOAT);

		config.registerFunc(LightShaderConfiguration.getNormalSig,
				LightShaderConfiguration.getNormal);

		config
				.registerFunc(
						PointLightPrologue,
						PointLightPre
								+ (getLightShaderConfig().isPhysical() ? quadraticFallof
										: "")
								+ (getLightShaderConfig().isPerspective() ? PointLightPerspective
										: PointLightParallel) + PointLightPost);

		return getLightShaderConfig().completeShader(getLightFunction());
	}

	@Override
	public boolean needsRecompilation(Object data) {
		return false;
	}

	@Override
	public Class instanceFor() {
		return PointLight.class;
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLPointLight();
	}

	// @Override
	// public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
	// GLSLLightShader result = new GLSLPointLight();
	// result.setLightShaderConfiguration(lsc);
	// return result;
	// }

	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
//		fpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), fade_power);
//		fdLoc = gl
//				.glGetUniformLocation(getShaderProgramNumber(), fade_distance);
		rpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), radientPower);

		posLoc = gl.glGetUniformLocation(getShaderProgramNumber(), lightPos);
		colLoc = gl.glGetUniformLocation(getShaderProgramNumber(), col0);
	}

//	int fpLoc = -1, fdLoc = -1; 
	int rpLoc = -1, posLoc = -1, colLoc = -1;

	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data,
			int shaderNo) {
		LightPos s = null;
		assert (data instanceof LightPos);
		s = (LightPos) data;

		assert (s.getLight() instanceof PointLight);

		PointLight light = (PointLight) s.getLight();

		// gl.glUniform1f(near, c.getZNear());
		// gl.glUniform1f(far, c.getZFar());

		Point4d position = new Point4d(s.getLightPos());
		disp.getCurrentGLState().getWorldToView().transform(position);

		gl.glUniform3f(posLoc, (float) position.x, (float) position.y,
				(float) position.z);

//		gl.glUniform1f(fpLoc, light.getAttenuationExponent());
//		gl.glUniform1f(fdLoc, light.getAttenuationDistance());

		this.spec.set(light.getColor());
		this.spec.scale(1. / this.spec.integrate());

		gl.glUniform3f(colLoc, this.spec.x, this.spec.y, this.spec.z);

		gl.glUniform1f(rpLoc, light.getPower() * (Math2.M_1_2PI * 0.5f));
	}

}
