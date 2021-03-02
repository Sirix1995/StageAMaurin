package de.grogra.imp3d.glsl.material;

import java.awt.Image;
import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.Texture;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.GLSLQueuedTexture;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;

/**
 * This class defines a state for a glsl-Shader, where individual ChannelMaps
 * may register Textures, TmpVariables or UniformVariables. It also may complete
 * a shader by querying all needed input from a GLSLCachedMaterial.
 * 
 * @author shi
 */
public class SkyReflectionMaterialConfiguration extends SkyMaterialConfiguration {

	@Override
	public String[] completeShader(Result[] input) {
		String s = "#version " + version +
				"\n";

		for (int i = 0; i < uniform.size(); i++)
			s += uniform.elementAt(i);
		if (uniform.size() > 0)
			s += "\n";

		for (int i = 0; i < sampler.size(); i++)
			s += sampler.elementAt(i);

		s += "\n";

		for (int i = 0; i < customSampler.size(); i++)
			s += customSampler.elementAt(i);

		if (customSampler.size() > 0)
			s += "\n";

		s += "varying vec2 TexUnit2;\n";
		s += "varying vec3 normal;\n";
		s += "varying float depth;\n";
		s += "varying vec4 pos;\n";
		s += "varying vec3 n_pos;\n\n";

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

		s += " vec3 col = "+input[IT_DIFFUSE].convert(Result.ET_VEC3)+";\n";
		
		s += " gl_FragColor = " +
//				"clamp(" +
				"vec4("
				+ "col"
				+ ", 1.0)" +
//						", 0.0, 1.0)" +
				";\n";
		s += "}";

		String[] code = { s };
		return code;
	}

	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo)
	{
		bindTextures(gl, disp, shaderNo, 0);
	}
	
	@Override
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {
		setupTextures(gl, disp, shaderNo, 0);
	}
	
	@Override
	public GLSLChannelMap getDefaultInputChannel() {
		return new GLSLSphereTracedInput();
	}
	
	@Override
	public ShaderConfiguration clone() {
		SkyReflectionMaterialConfiguration sc =  new SkyReflectionMaterialConfiguration();
		sc.setThisToOther(this);
		return sc;
	};
}
