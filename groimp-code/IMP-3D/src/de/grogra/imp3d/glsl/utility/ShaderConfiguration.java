package de.grogra.imp3d.glsl.utility;

import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.Texture;
import de.grogra.imp3d.glsl.material.channel.Result;

public abstract class ShaderConfiguration {

	public static final byte USE_UV = 1;
	public static final byte USE_LOCAL_POS = 2;
	public static final byte USE_GLOBAL_POS = 4;
	public static final byte USE_DERIVATES = 8;

	private byte BIT_SET = 0;
	
	private void clearBits() { BIT_SET = 0; }
	public void setBit(byte mask) {
		BIT_SET |= mask;
	}
	public boolean getBit(byte mask) {
		return (BIT_SET & mask) > 0;
	}
	
	public static final int T_FLOAT 			= Result.ET_FLOAT;
	public static final int T_VEC2 				= Result.ET_VEC2;
	public static final int T_VEC3 				= Result.ET_VEC3;
	public static final int T_VEC4 				= Result.ET_VEC4;
	public static final int T_BOOL 				= Result.ET_BOOL;
	public static final int T_SAMPLER2D 		= T_BOOL + 1;
	public static final int T_SAMPLER2DRECT 	= T_SAMPLER2D + 1;
	public static final int T_SAMPLER2DSHADOW 	= T_SAMPLER2DRECT + 1;
	public static final int T_SAMPLERCUBE 		= T_SAMPLER2DSHADOW + 1;
	public static final int T_MAT3	 			= T_SAMPLERCUBE + 1;
	
	static final String[] typeDef = { "float", "vec2", "vec3", "vec4", "bool", 
		"sampler2D", "sampler2DRect", "sampler2DShadow", "samplerCube", "mat3"};
	
	protected transient Vector<String> var = new Vector<String>();
	transient int currentTmpVar = 0;
	protected transient Vector<String> constVar = new Vector<String>();
	transient int currentConstVar = 0;
	protected transient Vector<String> uniform = new Vector<String>();
	transient int currentUniform = 0;
	protected transient LinkedHashMap<String, String> funcMap = new LinkedHashMap<String, String>();
	protected transient final Vector<String> sampler = new Vector<String>();

	public void clearTmpVariables() {
		currentTmpVar = 0;
		var.clear();
		currentConstVar = 0;
		constVar.clear();
		currentTexture = 0;
		sampler.clear();
		textureRequest.clear();
		currentCustomTexture = 0;
		customSampler.clear();
		customTextureRequest.clear();
		funcMap.clear();
		uniform.clear();
		clearBits();
	}

	transient final Vector<Image> textureRequest = new Vector<Image>();
	transient int currentTexture = 0;
	protected transient final Vector<String> customSampler = new Vector<String>();
	transient final Vector<GLSLQueuedTexture> customTextureRequest = new Vector<GLSLQueuedTexture>();
	transient int currentCustomTexture = 0;

	public ShaderConfiguration() {
		super();
	}

	public void setupTextures(GL gl, GLSLDisplay disp, int shaderNo, int offset) {
		for (int i = 0; i < textureRequest.size(); ++i) {
				int loc = gl.glGetUniformLocation(shaderNo, "tex" + i);
				gl.glUniform1i(loc, i + offset);
				gl.glActiveTexture(GL.GL_TEXTURE0 + i + offset);
		}
		for (int i = 0; i < customTextureRequest.size(); ++i) {
				int loc = gl.glGetUniformLocation(shaderNo, "customTex" + i);
				gl.glUniform1i(loc, i + offset + currentTexture + 1);
		}	
	}
	
	public void bindTextures(GL gl, GLSLDisplay disp, int shaderNo, int offset) {
		for (int i = 0; i < textureRequest.size(); ++i) {
			Image img = textureRequest.elementAt(i);
			if (img != null) {
				Texture tex = disp.getTextureManager().getTexture(gl, img);
				gl.glActiveTexture(GL.GL_TEXTURE0 + i + offset);
				gl.glBindTexture(GL.GL_TEXTURE_2D, tex.index);
			}
		}
		for (int i = 0; i < customTextureRequest.size(); ++i) {
			GLSLQueuedTexture img = customTextureRequest.elementAt(i);
			if (img != null) {
				gl.glActiveTexture(GL.GL_TEXTURE0 + i + currentTexture + 1 + offset);
				gl.glBindTexture(GL.GL_TEXTURE_2D, img.getIndex(gl));
			}
		}	

	} 
	
	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo)
	{
		bindTextures(gl, disp, shaderNo, 0);
	}
	
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {
		setupTextures(gl, disp, shaderNo, 0);
	}

	public String registerTexture(Image img) {
		String texName = "tex" + currentTexture;
		currentTexture++;
		sampler.add("uniform sampler2D " + texName + ";\n");
		textureRequest.add(img);
		return texName;
	}

	public String registerCustomTexture(GLSLQueuedTexture data) {
		String texName = "customTex" + currentCustomTexture;
		currentCustomTexture++;
		customSampler.add("uniform sampler2D " + texName + ";\n");
		customTextureRequest.add(data);
		return texName;
	}

	public String registerNewTmpVar(int type, String value) {
		String varName = "tmp" + currentTmpVar;
		var.add(typeDef[type] + " " + varName + " = " + value + ";\n");
		currentTmpVar++;
		return varName;
	}
	
	public String registerGlobalConst(int type, String value) {
		String varName = "const" + currentConstVar;
		constVar.add("const "+typeDef[type] + " " + varName + " = " + value + ";\n");
		currentConstVar++;
		return varName;
	}
	
	public String registerNewUniform(int type) {
		String varName = "uni" + currentUniform;
		uniform.add("uniform " + typeDef[type] + " " + varName + ";\n");
		currentUniform++;
		return varName;
	}

	/**
	 * Register a function to be used within this shaders code. Functions are
	 * guarantied to be added only once per signature using the last added.
	 * Appearance will be in order of registration
	 * 
	 * @param signature
	 *            Signature of the function for example "float calc(vec3 pos)"
	 * @param source
	 *            Source of the function without surrounding "{...}"
	 */
	public void registerFunc(String signature, String source) {
		String fullSource = signature + "{\n" + source + "\n}\n";
		funcMap.put(signature, fullSource);
	}

	public void cleanUp(GL gl, boolean javaonly) {
		for(int i = 0; i < customTextureRequest.size(); ++i)
			(customTextureRequest.elementAt(i)).delete(gl, javaonly);
	}
	
	protected Object referenceKey = null;
	
	public void set(Object obj) {
		referenceKey = obj;
	}
	
	public void set(OpenGLState glState, GLSLDisplay disp, Object obj) {
		set(obj);
	}

	
	protected Object getReferenceKeyValue() {
//		if(referenceKey == null)
//			return null;
		return perInstance() ? referenceKey.getClass() : referenceKey;
	}
	
	protected boolean perInstance() {
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((referenceKey == null) ? 0 : getReferenceKeyValue().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShaderConfiguration other = (ShaderConfiguration) obj;
		if (referenceKey == null) {
			if (other.referenceKey != null)
				return false;
		} else if (!getReferenceKeyValue().equals(other.getReferenceKeyValue()))
			return false;
		return true;
	}

	/**
	 * Sets all permanent attributes of this instance to the values from other. 
	 * This method is used by clone() to generate a shallow copy of a ShaderConfiguration
	 * @param other Reference from which attributes are copied 
	 */
	protected void setThisToOther(ShaderConfiguration other) {
		this.referenceKey = other.referenceKey;
	}
	
	@Override
	public abstract ShaderConfiguration clone();
	
	public abstract GLSLManagedShader getShaderByDefaultCollection(GLSLDisplay disp, Object reference);

	protected int version = 110;
	public void setVersion(int i) {
		version = version < i ? i : version; 
	}
}