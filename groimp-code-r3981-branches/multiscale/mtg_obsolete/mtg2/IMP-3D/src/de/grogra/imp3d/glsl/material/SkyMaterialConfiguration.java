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
public class SkyMaterialConfiguration extends MaterialConfiguration {

	boolean isPerspective = true;
	
	@Override
	public String[] completeShader(Result[] input) {
		String s = "#version " + version + 
				"\n";
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
		s += "uniform float powerDensity;\n";

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

		// if (input[IT_DIFFUSE].getReturnType() == Result.ET_VEC4) {
		// s += " if(diffuse.a <= 0.5) discard;\n";
		// }
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

		// Scale by Spectrum assumed to be full white		
		s += " col *= powerDensity;\n";
		
		s += " gl_FragColor = vec4(alpha.rgb, 1.0) * vec4("
				// s += " gl_FragColor = vec4("
				+ "col"
				+ ", 0.0) + lastCol;\n";
		// XXX: Add emissive part here!
		// s += " gl_FragData[2] = vec4("
		// + input[IT_EMISSIVE].convert(Result.ET_VEC3) + ", "
		// + input[IT_AMBIENT].convert(Result.ET_FLOAT) + ");\n";
		// s += " gl_FragData[3] = vec4("
		// + input[IT_TRANSPERENCY].convert(Result.ET_VEC3) + ", "
		// + input[IT_SPECULAR_TRANSPERENCY].convert(Result.ET_FLOAT)
		// + ");\n";
		s += "}";

		String[] code = { s };
		return code;
	}

	@Override
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo)
	{
		bindTextures(gl, disp, shaderNo, 2);
		gl.glUniform1f(rpLoc, 0.5f*Math2.M_1_2PI*disp.getCurrentGLState().getBgPowerDensity());
	}
	
	int rpLoc = -1;
	
	@Override
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {
		int lastCol = gl.glGetUniformLocation(shaderNo, "inputTex");
		gl.glUniform1i(lastCol, 0);
		int alpha = gl.glGetUniformLocation(shaderNo, "alphaTex");
		gl.glUniform1i(alpha, 1);
		setupTextures(gl, disp, shaderNo, 2);
		rpLoc = gl.glGetUniformLocation(shaderNo, "powerDensity");
	}

	@Override
	public GLSLChannelMap getDefaultInputChannel() {
		return isPerspective? new GLSLSphereTracedInput() : new GLSLSphereTracedInputParallel();
	}

	@Override
	public void set(OpenGLState glState, GLSLDisplay disp, Object obj) {
		super.set(glState, disp, obj);
		isPerspective = !(disp.getView3D().getCamera().getProjection() instanceof ParallelProjection);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isPerspective ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkyMaterialConfiguration other = (SkyMaterialConfiguration) obj;
		if (isPerspective != other.isPerspective)
			return false;
		return true;
	}

	@Override
	protected void setThisToOther(ShaderConfiguration other) {
		super.setThisToOther(other);
		assert(other instanceof SkyMaterialConfiguration);
		this.isPerspective = ((SkyMaterialConfiguration)other).isPerspective;
	}
	
	@Override
	public ShaderConfiguration clone() {
		SkyMaterialConfiguration sc =  new SkyMaterialConfiguration();
		sc.setThisToOther(this);
		return sc;
	};
}
