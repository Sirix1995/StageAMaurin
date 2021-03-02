package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.vecmath.Math2;

/**
 * Implementation of the spot light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLSpotLight extends GLSLLightShader {
	final static String SpotLightPrologue = "void SpotLight(in vec3 normal,\n"
			+ "in vec3 eye,\n" + "in vec3 ecPosition3,\n"
			+ "in vec3 lightPos,\n" + "in vec3 spotDirection,\n"
			+ "in float spotCosCutoff,\n" + "in float spotInnerCosCutoff,\n"
			+ "in float shininess,\n" + "in vec3 col0,\n"
			+ "in float fade_distance,\n" + "in float fade_power,\n"
			+ "in float radientPower,\n" +
			// "             inout vec4 ambient," ,
			"inout vec3 diffuse,\n" + "inout vec3 specular)";
	final static String SpotLightPre = " float nDotVP;\n"
			+ " float nDotHV;\n"
			+ " float pf;\n"
			+ " float d;\n"
			+ " float attenuation;\n"
			+ " float spotDot;\n"
			+ " float spotAttenuation;\n"
			+ " vec3 	VP;\n"
			+
			// " vec3	halfVector;\n"+
			" vec3	R;\n"
			+

			" VP = lightPos - ecPosition3;\n"
			+ " d = length(VP);\n"
			+ " VP /= d;\n"
			+

			// " halfVector = normalize(VP + eye);\n"+
			" R = reflect(-VP, normal);\n"
			+

			// " if(fade_power > 1000.0)\n"+
			// "  attenuation = exp(-d/fade_distance);\n"+
			// " else\n"+
			// "  attenuation = 1.0 / ( 1.0 + pow(d/fade_distance, fade_power) );\n"+
			// TODO: This is the non-physical based factor
			" attenuation = 1.0;"
			+
			// " attenuation = 1.0 / (d*d);\n"+

			// See if point on surface is inside cone of illumination
			" spotDot = dot(-VP, normalize(spotDirection));\n"
			+

			" if (spotDot <= spotCosCutoff)\n"
			+ "  spotAttenuation = 0.0;\n"
			+ " else {\n"
			+
			// "		float d = (1 - 0.5 * (spotInnerCosCutoff + spotCosCutoff) );"
			// ,
			"  if(spotDot >= spotInnerCosCutoff)\n"
			+ "   spotAttenuation = 1.0;\n"
			+ "  else {\n"
			+ "   float falloff = (spotDot - spotCosCutoff) / (spotInnerCosCutoff - spotCosCutoff);\n"
			+ "   spotAttenuation = (3.0 - 2.0 * falloff) * falloff * falloff;\n"
			+ "  }\n"
			+ " }\n"
			+

			// Combine the spotlight and distance attenuation.
			" attenuation *= spotAttenuation;\n"+
			" nDotVP = max(0.0, dot(normal, VP));\n";

			final static String quadraticFallof = " attenuation /= (d*d);"; 
				

			final static String SpotLightPerspective = " nDotHV = max(0.0, dot(R, -normalize(ecPosition3)));\n";
			final static String SpotLightParallel = " nDotHV = max(0.0, dot(R, vec3(0.0, 0.0, 1.0)));\n"; 
			
			final static String SpotLightPost = 
			" if ((nDotHV <= 0.0) || (shininess >= 65504.0))\n"+
			"  pf = 0.0;\n" + " else\n"
			+ "  pf = pow(nDotHV, shininess);\n" +

			" diffuse  += col0 * nDotVP * attenuation * radientPower;\n"
			+ " specular += col0 * nDotVP * pf * attenuation * radientPower;";

	protected String lightPos, spotDirection, spotCosCutoff,
			spotInnerCosCutoff, col0, fade_power, fade_distance, radientPower;

	@Override
	public String getLightFunction() {
		String lightFunc = 
				  " vec3 normal = getEyeNormal(norm);\n"
				+ " vec3 eye = vec3 (0.0, 0.0, 1.0);\n"
				+ " SpotLight( normal.rgb, " + "eye, " + "pos, " + lightPos
				+ ", " + spotDirection + ", " + spotCosCutoff + ", "
				+ spotInnerCosCutoff + ", " + "shininess, " + col0 + ", "
				+ fade_distance + ", " + fade_power + ", " + radientPower
				+ ", " +
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
		spotDirection = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		spotCosCutoff = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
		spotInnerCosCutoff = config
				.registerNewUniform(ShaderConfiguration.T_FLOAT);

		config.registerFunc(LightShaderConfiguration.getNormalSig,
				LightShaderConfiguration.getNormal);

		config.registerFunc(SpotLightPrologue, SpotLightPre + (getLightShaderConfig().isPhysical() ? quadraticFallof : "")+
				(getLightShaderConfig().isPerspective()?SpotLightPerspective:SpotLightParallel) +  SpotLightPost);

		return getLightShaderConfig().completeShader(getLightFunction());
	}

	@Override
	public boolean needsRecompilation(Object data) {
		return false;
	}

	@Override
	public Class instanceFor() {
		return SpotLight.class;
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLSpotLight();
	}

	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
//		fpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), fade_power);
//		fdLoc = gl
//				.glGetUniformLocation(getShaderProgramNumber(), fade_distance);
		rpLoc = gl.glGetUniformLocation(getShaderProgramNumber(), radientPower);

		posLoc = gl.glGetUniformLocation(getShaderProgramNumber(), lightPos);
		colLoc = gl.glGetUniformLocation(getShaderProgramNumber(), col0);
		
		spotdirLoc = gl.glGetUniformLocation(getShaderProgramNumber(),
				spotDirection);
		spotCosCutLoc = gl.glGetUniformLocation(getShaderProgramNumber(),
				spotCosCutoff);
		spotInnerCosCutLoc = gl.glGetUniformLocation(getShaderProgramNumber(),
				spotInnerCosCutoff);
	}

//	int fpLoc = -1, fdLoc = -1; 
	int rpLoc = -1, posLoc = -1, colLoc = -1, spotdirLoc = -1, spotCosCutLoc = -1, spotInnerCosCutLoc = -1;


	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {
		LightPos s = null;
		assert (data instanceof LightPos);
		s = (LightPos) data;

		assert (s.getLight() instanceof SpotLight);

		SpotLight light = (SpotLight) s.getLight();

		// gl.glUniform1f(near, c.getZNear());
		// gl.glUniform1f(far, c.getZFar());

		Point4d position = new Point4d(s.getLightPos());
		disp.getCurrentGLState().getWorldToView().transform(position);

		gl.glUniform3f(posLoc, (float) position.x, (float) position.y,
				(float) position.z);

		Vector3f spotDirN = new Vector3f((float) s.getLightDir().x, (float) s
				.getLightDir().y, (float) s.getLightDir().z);

		spotDirN.normalize();
		disp.getCurrentGLState().getWorldToView().transform(spotDirN);

		gl.glUniform3f(spotdirLoc, spotDirN.x, spotDirN.y, spotDirN.z);

		double cosInnerAngle = java.lang.Math.cos(light.getInnerAngle());
		double cosOuterAngle = java.lang.Math.cos(light.getOuterAngle());

		gl.glUniform1f(spotCosCutLoc, (float) cosOuterAngle);
		gl.glUniform1f(spotInnerCosCutLoc, (float) cosInnerAngle);

//		gl.glUniform1f(fp, light.getAttenuationExponent());
//		gl.glUniform1f(fd, light.getAttenuationDistance());

		this.spec.set(light.getColor());
		this.spec.scale(1. / this.spec.integrate());
		gl.glUniform3f(colLoc, this.spec.x, this.spec.y, this.spec.z);

		// the divisor is the area integral of the lit cone on a 
		gl.glUniform1f(rpLoc, (float) (light.getPower() /*  * sphereFactor */
		/ (Math2.M_2PI * (1.0 - cosInnerAngle + (cosInnerAngle - cosOuterAngle)/2 ))));
	}

}
