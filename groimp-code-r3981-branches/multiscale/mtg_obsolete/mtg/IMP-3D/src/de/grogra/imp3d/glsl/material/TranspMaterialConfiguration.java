package de.grogra.imp3d.glsl.material;

import java.util.Iterator;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.shading.RGBAShader;

/**
 * This class defines a state for a glsl-Shader, where individual ChannelMaps
 * may register Textures, TmpVariables or UniformVariables. It also may complete
 * a shader by querying all needed input from a GLSLCachedMaterial.
 * 
 * @author shi
 * 
 */
public class TranspMaterialConfiguration extends MaterialConfiguration {

	@Override
	public String[] completeShader(Result[] input) {

		String s = "#version " + version +
				"\n";

//		if (useTransparency) {
		if (true) {

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

			s += "varying vec2 uv;\n";
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
			s += "vec3 n_normal = normalize("
					+ input[IT_NORMAL].convert(Result.ET_VEC3) + ");\n";
			s += "if(!gl_FrontFacing) n_normal *= -1.0;\n";
			s += "if(" + input[IT_TRANSPERENCY].convert(Result.ET_FLOAT)
					+ " > 0.5) discard;\n";
			s += "gl_FragColor = vec4(1.0);\n}";
		}
		else {
			s += "void main() { gl_FragColor = vec(1.0); }";
		}
		String[] code = { s };
		return code;
	}

//	boolean useTransparency = false;

	@Override
	public void set(Object obj) {
//		assert (obj instanceof Shader);
//		useTransparency = ((Shader) obj).isTransparent();
//		if (obj instanceof RGBAShader)
//			useTransparency = ((RGBAShader) obj).w < 1.0;
		super.set(obj);
	}

	/**
	 * Should be implemented by ReferenceKey! using a Interface or such
	 */
	@Override
	protected boolean perInstance() {
		return (referenceKey.getClass() == RGBAShader.class);
	};

	@Override
	public ShaderConfiguration clone() {
		TranspMaterialConfiguration sc = new TranspMaterialConfiguration();
		setThisToOther(sc);
		return sc;
	};
}
