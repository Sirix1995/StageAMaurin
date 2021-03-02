package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.AreaLight;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.vecmath.Math2;

/**
 * Implementation of the spot light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLAreaLight extends GLSLLightShader {
	final static String SpotLightPrologue = "void AreaLight(in vec3 normal,\n"
			+ "in vec3 eye,\n" + "in vec3 ecPosition3,\n"
//			+ "in vec3 lightPos,\n" + "in vec3 spotDirection,\n"
			+ "in float shininess,\n" + "in vec3 col0,\n"
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
			+ " vec3 v1, v2, v3, v4;\n"
			+ " vec3 vn1, vn2, vn3, vn4;\n"
			
			+ " vec3 R;\n"
			+ " attenuation = 1.0;\n";
			
		
			
//projectOnPlane(in vec3 p, in vec3 pc, in vec3 pn)
//{
//    float distance = dot(pn, p-pc);
//    return p - distance*pn;
//}
			
//			
//			" vec3 projectedPoint = (gl_TextureMatrix[3]*vec4(ecPosition3, 1.)).xyz;\n"+ 
//			" projectedPoint.y-=20.;\n"+
//			
//			" vec3 nearestPoint = vec3(clamp(projectedPoint.x, -1., 1.)," +
//				"clamp(projectedPoint.y, -20., 20.),0.);\n"
//			
//			+ " VP = projectedPoint-nearestPoint;\n"
//			+ " d = length(VP);\n"
//			+ " VP /= d;\n"
//			+
//
//			" R = reflect(-VP, normal);\n"
//			+

	
//			// See if point on surface is inside cone of illumination
//			" spotDot = dot(-VP, normalize(spotDirection));\n"
//			+
//
//			" if (spotDot <= spotCosCutoff)\n"
//			+ "  spotAttenuation = 0.0;\n"
//			+ " else {\n"
//			+
//
//			"  if(spotDot >= spotInnerCosCutoff)\n"
//			+ "   spotAttenuation = 1.0;\n"
//			+ "  else {\n"
//			+ "   float falloff = (spotDot - spotCosCutoff) / (spotInnerCosCutoff - spotCosCutoff);\n"
//			+ "   spotAttenuation = (3.0 - 2.0 * falloff) * falloff * falloff;\n"
//			+ "  }\n"
//			+ " }\n"
//			+
//
//			// Combine the spotlight and distance attenuation.
//			" attenuation *= spotDot;\n"+

			final static String quadraticFallof = " attenuation /= (d*d);"; 
				

			final static String SpotLightPerspective = " nDotHV = max(0.0, dot(R, -normalize(ecPosition3)));\n";
			final static String SpotLightParallel = " nDotHV = max(0.0, dot(R, vec3(0.0, 0.0, 1.0)));\n"; 
			
			final static String SpotLightPost = 
			" if ((nDotHV <= 0.0) || (shininess >= 65504.0))\n"+
			"  pf = 0.0;\n" + " else\n"
			+ "  pf = pow(nDotHV, shininess);\n"

			+ " nDotVP = max(0.0, dot(normal, VP));\n"
			+ " diffuse  += col0 * nDotVP * attenuation * radientPower;\n"
//			+ " specular += col0 * nDotVP * pf * attenuation * radientPower;"
			;

//	protected String lightPos, spotDirection, 
	protected String col0, radientPower;
	protected String v[];

	@Override
	public String getLightFunction() {
		String lightFunc = 
				  " vec3 normal = getEyeNormal(norm);\n"
				+ " vec3 eye = vec3 (0.0, 0.0, 1.0);\n"
				+ " AreaLight( normal.rgb, " + "eye, " + "pos" +
//						", " + lightPos+ ", " + spotDirection + 
				", shininess, " + col0 + ", "
				+ radientPower
				+ ", " +
				// "				amb, " +
				"diff, spec);";
		return lightFunc;
	};


	@Override
	public String[] getFragmentShader(Object sh) {
		config.clearTmpVariables();
//		lightPos = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		col0 = config.registerNewUniform(ShaderConfiguration.T_VEC3);
		radientPower = config.registerNewUniform(ShaderConfiguration.T_FLOAT);
//		spotDirection = config.registerNewUniform(ShaderConfiguration.T_VEC3);

		String VertexNormalization = ""; 
		v = new String[4];
		for(int i = 0; i < 4; i++) {
			v[i] = config
				.registerNewUniform(ShaderConfiguration.T_VEC3);
			VertexNormalization += " v"+(i+1)+" = "+v[i]+"-ecPosition3;\n vn"+(i+1)+" = normalize(v"+(i+1)+");\n";
		}
		
		
		for(int i = 0; i < 4; i++) 
//			VertexNormalization += " VP += acos(dot(v"+(i+1)+",v"+((i+1)%4+1)+")) * normalize(cross("+v[i]+","+v[(i+1)%4]+"));\n";
			VertexNormalization += " VP += acos(dot(vn"+(i+1)+",vn"+((i+1)%4+1)+")) * normalize(cross(v"+(i+1)+",v"+((i+1)%4+1)+"));\n";

		config.registerFunc(LightShaderConfiguration.getNormalSig,
				LightShaderConfiguration.getNormal);

		
		config.registerFunc(SpotLightPrologue, SpotLightPre + VertexNormalization + (getLightShaderConfig().isPhysical() ? quadraticFallof : "")+
				(getLightShaderConfig().isPerspective()?SpotLightPerspective:SpotLightParallel) +  SpotLightPost);

		
		return getLightShaderConfig().completeShader(getLightFunction());
	}

	@Override
	public boolean needsRecompilation(Object data) {
		return false;
	}

	@Override
	public Class instanceFor() {
		return Parallelogram.class;
	}

	@Override
	public GLSLShader getInstance() {
		return new GLSLAreaLight();
	}

	// @Override
	// public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
	// GLSLLightShader result = new GLSLSpotLight();
	// result.setLightShaderConfiguration(lsc);
	// return result;
	// }

	
	Vector4f vect = new Vector4f(0,0,0,1);
	
//	gl.glVertex3f (-axis.x, -axis.y, -axis.z);
//	gl.glVertex3f (axis.x, axis.y, axis.z);
//	gl.glVertex3f (axis.x, axis.y, length + axis.z);
//	gl.glVertex3f (-axis.x, -axis.y, length - axis.z);
	float f[] = {
			-1,-1, 0,-1,
			 1, 1, 0, 1,
			 1, 1, 1, 1,
			-1,-1, 1,-1
	};
	
	int rpLoc = -1, colLoc = -1;
	int vertLoc[] = new int[4]; 
	
	@Override
	protected void setupShader(GL gl, GLSLDisplay disp, Object data) {
		super.setupShader(gl, disp, data);
		
		rpLoc = gl
		.glGetUniformLocation(getShaderProgramNumber(), radientPower);
		colLoc = gl.glGetUniformLocation(getShaderProgramNumber(), col0);
		for(int i = 0; i < 4; i++)
			vertLoc[i] = gl.glGetUniformLocation(getShaderProgramNumber(), v[i]);
	}
	
	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {
		LightPos s = null;
		assert (data instanceof LightPos);
		s = (LightPos) data;

		assert (s.getLight() instanceof Parallelogram);

		Parallelogram par = (Parallelogram) s.getLight();
		AreaLight light = par.getLight();
		
	
		Vector3f a = par.getAxis();
		for(int i = 0; i < 4; i++) {
			vect.set(f[4*i]*a.x,f[4*i+1]*a.y,f[4*i+2]*par.getLength()+f[4*i+3]*a.z, 1);
			s.getLightTransform().transform(vect);
			disp.getCurrentGLState().getWorldToView().transform(vect);
			gl.glUniform3f(vertLoc[i], vect.x, vect.y, vect.z);
		}

		
		spec.set(1,1,1);
		if(par.getShader() != null) {
			spec.x = ((par.getShader().getAverageColor() >> 16)  & 0xff) / 255.f;
			spec.y = ((par.getShader().getAverageColor() >> 8) & 0xff) / 255.f;
			spec.z = (par.getShader().getAverageColor() & 0xff) / 255.f;
		}
		
		spec.scale(1. / this.spec.integrate());
		gl.glUniform3f(colLoc, spec.x, spec.y, spec.z);
		gl.glUniform1f(rpLoc, (float) (light.getPower()) * Math2.M_1_2PI );
	}

}
