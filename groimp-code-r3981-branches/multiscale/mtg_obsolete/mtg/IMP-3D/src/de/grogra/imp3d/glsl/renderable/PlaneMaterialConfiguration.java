package de.grogra.imp3d.glsl.renderable;

import java.util.Iterator;

import javax.media.opengl.GL;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;

public class PlaneMaterialConfiguration extends MaterialConfiguration {
	
	@Override
	public String[] completeShader(Result[] input) {
		String s = "#version " + version +
				"\n#extension GL_ARB_draw_buffers : enable\n";

		s += "uniform mat3 normalMat;\n";

		for (int i = 0; i < uniform.size(); i++)
			s += uniform.elementAt(i);
		if (uniform.size() > 0)
			s += "\n";

		for (int i = 0; i < sampler.size(); i++)
			s += sampler.elementAt(i);

		if (sampler.size() > 0)
			s += "\n";

		for (int i = 0; i < customSampler.size(); i++)
			s += customSampler.elementAt(i);

		if (customSampler.size() > 0)
			s += "\n";

		s += "varying vec2 TexUnit2;\n";

		for (int i = 0; i < constVar.size(); i++)
			s += constVar.elementAt(i);
		if (constVar.size() > 0)
			s += "\n";

		// Add functions
		Iterator<String> it = funcMap.values().iterator();
		while (it.hasNext()) {
			s += it.next();
		}

		if (funcMap.size() > 0)
			s += "\n";

		s += "float packToFloat(vec2 val) {\n"
				+ " val = clamp(val, 0.0, 1.0);\n"
				+ " val.x = floor(val.x * 255. / 256. * 128.) * 0.0078125 * 16.;\n"
				+ " vec2 X = vec2(floor(val.x), fract(val.x));\n"
				+
				" return (X.y + (val.y * 255. / 257. * 0.125) + (1.0)) * exp2(X.x);\n"
				+ "}\n\n";

		s += "vec2 encodeNormal(vec3 normal){\n"
				+ " float f = sqrt(8.0*normal.z+8.0);\n"
				+ " return normal.xy / f + 0.5;\n" + "}\n\n";

		s+=
		"void main(void)\n"+
		"{\n"+
		"	vec3 plane_normal = normalize(normalMat * vec3(0.0, 0.0, 1.0));"+
		"	vec4 base = (gl_TextureMatrix[2] * vec4(0.0, 0.0, 0.0, 1.0));"+
		"	vec3 viewPos = normalize(vec3(TexUnit2, -1.0));\n"+

		"	float dotP = dot(plane_normal, viewPos);"+
		"	if (dotP >= 0.0) {\n"+
		"		discard;\n"+
		"	}\n"+

		"   float t = ( dot(base.xyz, plane_normal) ) / dotP;\n "+
		"	if(t < 0.0) discard;"+
		"	vec4 plane_pos = vec4(t * viewPos, 1.0);"+
		"	vec4 local_plane_pos = gl_TextureMatrix[3] * plane_pos;\n"+
		"	vec2 plane_uv = local_plane_pos.xy;\n";

		for (int i = 0; i < var.size(); i++)
			s += " " + var.elementAt(i);

		if (var.size() > 0)
			s += "\n";
		if(input[IT_PROLOGUE].toString().length() > 0)
			s += input[IT_PROLOGUE] + "\n";
//		s += " vec3 n_normal = normalize("
//				+ input[IT_NORMAL].convert(Result.ET_VEC3) + ");\n";
		s += " vec3 emissive = " + input[IT_EMISSIVE].convert(Result.ET_VEC3)+";\n";
		s += " vec3 diff_transp = " + input[IT_DIFFUSE_TRANSPERENCY].convert(Result.ET_VEC3)+";\n";
		s += " gl_FragData[0] = vec4(encodeNormal(plane_normal), "
				+ input[IT_SHININESS].reduce(Result.ET_FLOAT) + ", "
				+ input[IT_TRANSPERENCY_SHININESS].reduce(Result.ET_FLOAT)+");\n";
		s += " gl_FragData[1] = vec4("
				+ input[IT_DIFFUSE].convert(Result.ET_VEC3)+",packToFloat(emissive.rg));\n";
		// XXX: Add emissive part here!
		s += " gl_FragData[2] = vec4("
				+ input[IT_SPECULAR].convert(Result.ET_VEC3) + ", packToFloat(vec2(emissive.b, diff_transp.r)));\n";
//				+ input[IT_AMBIENT].convert(Result.ET_FLOAT) + ");\n";
		s += " gl_FragData[3] = vec4("
				+ input[IT_TRANSPERENCY].convert(Result.ET_VEC3) + ",packToFloat(diff_transp.gb));\n";
		s +=" plane_pos = gl_TextureMatrix[0] * plane_pos;"+
		" gl_FragDepth =  clamp((plane_pos.z / plane_pos.w)*.5+.5, 0., 1.);";

		s += "}";

		String[] code = { s };
		return code;
	}
	
	@Override
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {
		// TODO Auto-generated method stub
		super.setupShader(gl, disp, shaderNo);
		normalMatLoc = gl.glGetUniformLocation(shaderNo,"normalMat");
	}
	
	int normalMatLoc = -1;

	Matrix3f mat = new Matrix3f();
	
	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo) {
		// TODO Auto-generated method stub
		super.setupDynamicUniforms(gl, disp, shaderNo);
		disp.getTransformation(null).getRotationScale(mat);
		mat.invert();
		mat.transpose();
		gl.glUniformMatrix3fv(normalMatLoc, 1, false, disp.getCurrentGLState()
				.toGLMatrix3f(mat), 0);
	}
	
	@Override
	public ShaderConfiguration clone() {
		PlaneMaterialConfiguration sc = new PlaneMaterialConfiguration();
		sc.setThisToOther(this);
		return sc;
	}
	
	@Override
	public GLSLChannelMap getDefaultInputChannel() {
		return new GLSLPlaneInput();
	}
}
