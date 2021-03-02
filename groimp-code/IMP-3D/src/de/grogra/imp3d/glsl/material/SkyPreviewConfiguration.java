package de.grogra.imp3d.glsl.material;

import java.awt.Image;
import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.imp3d.ParallelProjection;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.Texture;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLQueuedTexture;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.vecmath.Math2;

/**
 * This class defines a state for a glsl-Shader, where individual ChannelMaps
 * may register Textures, TmpVariables or UniformVariables. It also may complete
 * a shader by querying all needed input from a GLSLCachedMaterial.
 * 
 * @author shi
 * 
 */
public class SkyPreviewConfiguration extends MaterialConfiguration {

	@Override
	public String[] completeShader(Result[] input) {
		String s = "#version " + version + "\n";
		s += "#extension GL_ARB_texture_rectangle : enable\n";
		
		for (int i = 0; i < uniform.size(); i++)
			s += uniform.elementAt(i);
		if (uniform.size() > 0)
			s += "\n";

		for (int i = 0; i < sampler.size(); i++)
			s += sampler.elementAt(i);
		s += "uniform sampler2DRect inputTex;\n";
		s += "uniform sampler2DRect alphaTex;\n";

		s += "\n";

		for (int i = 0; i < customSampler.size(); i++)
			s += customSampler.elementAt(i);

		if (customSampler.size() > 0)
			s += "\n";

		s += "varying vec2 uv;\n";
		s += "varying vec3 normal;\n";
		s += "varying float depth;\n";
		s += "varying vec4 pos;\n";
		s += "varying vec3 n_pos;\n";
		s += "varying vec3 g_pos;\n\n";

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

		s += "void main() {\n";

		for (int i = 0; i < var.size(); i++)
			s += " " + var.elementAt(i);

		if (var.size() > 0)
			s += "\n";

		s += input[IT_PROLOGUE] + "\n";
		// s += "vec3 n_normal = normalize("
		// + input[IT_NORMAL].convert(Result.ET_VEC3) + ");\n";
		// s += "if(!gl_FrontFacing) n_normal *= -1.0;\n";
		// s += " gl_FragData[0] = vec4(n_normal.xy, abs("
		// + input[IT_POSITION].convert(Result.ET_VEC3) + ").z, "
		// + input[IT_SHININESS].convert(Result.ET_FLOAT) + ");\n";
		s += " vec4 alpha = texture2DRect(alphaTex, gl_FragCoord.st);\n";
		s += " vec4 lastCol = texture2DRect(inputTex, gl_FragCoord.st);\n";

		s += " vec3 col = "+input[IT_DIFFUSE].convert(Result.ET_VEC3)+";\n";
//		s += " col *= powerDensity / dot(col, vec3(1.0));\n";

		s += " gl_FragColor = vec4(alpha.rgb, 1.0) * vec4(col, 1.0) + lastCol;\n";
		s += "}";

		String[] code = { s };
		return code;
	}

	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo) {
		bindTextures(gl, disp, shaderNo, 2);
	}

	@Override
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {
		int lastCol = gl.glGetUniformLocation(shaderNo, "inputTex");
		gl.glUniform1i(lastCol, 0);
		int alpha = gl.glGetUniformLocation(shaderNo, "alphaTex");
		gl.glUniform1i(alpha, 1);
		setupTextures(gl, disp, shaderNo, 2);
	}

	@Override
	public ShaderConfiguration clone() {
		SkyPreviewConfiguration sc = new SkyPreviewConfiguration();
		sc.setThisToOther(this);
		return sc;
	};
}
