package de.grogra.imp3d.glsl.light;

import java.util.Iterator;

import javax.media.opengl.GL;

import de.grogra.imp3d.ParallelProjection;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.light.shadow.ShadowLightCollection;
import de.grogra.imp3d.glsl.utility.GLSLManagedShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.ray.physics.Light;

public class LightShaderConfiguration extends ShaderConfiguration {
	
	boolean shadow, perspective, physical;
	
	@Override
	protected void setThisToOther(ShaderConfiguration other) {
		super.setThisToOther(other);
		assert(other instanceof LightShaderConfiguration);
		LightShaderConfiguration lsc = (LightShaderConfiguration)other;
		this.shadow = lsc.shadow;
		this.perspective = lsc.perspective;
		this.physical = lsc.physical;
	}
	
	@Override
	public ShaderConfiguration clone() {
		LightShaderConfiguration lsc = new LightShaderConfiguration();
		lsc.setThisToOther(this);
		return lsc;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (perspective ? 1231 : 1237);
		result = prime * result + (shadow ? 1231 : 1237);
		result = prime * result + (physical ? 1231 : 1237);
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
		LightShaderConfiguration other = (LightShaderConfiguration) obj;
		if (perspective != other.perspective)
			return false;
		if (shadow != other.shadow)
			return false;
		if (physical != other.physical)
			return false;
		return true;
	}



	@Override
	public void set(de.grogra.imp3d.glsl.OpenGLState glState, GLSLDisplay disp, Object obj) {
		super.set(glState, disp, obj);
		assert(obj instanceof Light);
		this.shadow = !((Light)obj).isShadowless() && (disp.isOptionShowShadows());
		this.perspective = !(disp.getView3D().getCamera().getProjection() instanceof ParallelProjection);		
		this.physical = (disp.isOptionPhysicalLighting());		
		
	};
	
	public boolean isShadow() {
		return shadow;
	}

	public boolean isPerspective() {
		return perspective;
	}
	
	public boolean isPhysical() {
		return physical;
	}

	@Override
	public String toString() {
		return super.toString() + " - Reference: "+ ((getReferenceKeyValue() == null) ? "(null)" : getReferenceKeyValue().toString()) +
		" - shadow: " + shadow + " - perspective: " + perspective  + " - physical: " + physical;
	}
	
	private static final String getPosSig = "vec3 getEyePos(vec2 depth)";
	private static final String getPos = " return vec3(TexCoord2.st, -1.0) * dot(fract(depth), vec2(1., 1./1024.)) * farplane;";
//	private static final String getPos = " return vec3(TexCoord2.st, -1.0) * dot(fract(depth), vec2(1., 0.0009765625)) * farplane;";

	private static final String getPosSigParallel = "vec3 getEyePos(vec2 depth)";
	private static final String getPosParallel = " return vec3(TexCoord2.st, - dot(fract(depth), vec2(1., 0.0009765625) ) * farplane );";

	public void registerEyePosFunc() {
		if(isPerspective())
			registerFunc(LightShaderConfiguration.getPosSig, LightShaderConfiguration.getPos);
		else
			registerFunc(LightShaderConfiguration.getPosSigParallel, LightShaderConfiguration.getPosParallel);
	}
	
	public static final String getNormalSig = "vec3 getEyeNormal(vec2 encNorm)";
//	public static final String getNormal = " return normalize(vec3(normal.xy, sqrt(1.0 - normal.x * normal.x - normal.y * normal.y)));";
	public static final String getNormal =
		" encNorm = encNorm*4.0-2.0;\n"+
		" float f = dot(encNorm,encNorm);\n"+
		" float g = sqrt(1.0-f*0.25);\n"+
		" return vec3(encNorm*g,1.0-f*0.5);\n";

//		" return vec3(normal.xy, sqrt(1.0 - normal.x * normal.x - normal.y * normal.y));";

	public void setupDynamicUniforms(GL gl, GLSLDisplay disp, int shaderNo) {
		super.setupDynamicUniforms(gl, disp, shaderNo);
		gl.glUniform1f(farLoc, disp.getView3D().getCamera().getZFar());
	};
	
	int farLoc = -1;
	
	@Override
	public void setupShader(GL gl, GLSLDisplay disp, int shaderNo) {

		int tex0 = gl.glGetUniformLocation(shaderNo, "firstTex");
		int tex1 = gl.glGetUniformLocation(shaderNo, "secondTex");
		int tex2 = gl.glGetUniformLocation(shaderNo, "thirdTex");
		int tex3 = gl.glGetUniformLocation(shaderNo, "fourthTex");
		int tex4 = gl.glGetUniformLocation(shaderNo, "fithTex");
		int tex5 = gl.glGetUniformLocation(shaderNo, "alphaTex");
		int tex6 = gl.glGetUniformLocation(shaderNo, "zTex");
		farLoc = gl.glGetUniformLocation(shaderNo, "farplane");

		gl.glUniform1i(tex0, 0);
		gl.glUniform1i(tex1, 1);
		gl.glUniform1i(tex2, 2);
		gl.glUniform1i(tex3, 3);
		gl.glUniform1i(tex4, 4);
		gl.glUniform1i(tex5, 5);
		gl.glUniform1i(tex6, 8);
	};
	
	/**
	 * Complete a shader to be used as a light.
	 * @param lightFunction added just before color is set. 
	 * 		  should modify variables diff and spec (vec3).
	 * @return
	 */
	public String[] completeShader(String lightFunction) {
		String s = "#version " + version +
				"\n#extension GL_ARB_draw_buffers : enable\n#extension GL_ARB_texture_rectangle : enable\n";
		s += "\n";
		s += "varying vec2 TexCoord;\n";
		s += "varying vec2 TexCoord2;\n\n";

		for (int i = 0; i < uniform.size(); i++)
			s += uniform.elementAt(i);
		if (uniform.size() > 0)
			s += "\n";

		s += "uniform float farplane;\n";
		s += "uniform sampler2DRect firstTex;\n";
		s += "uniform sampler2DRect secondTex;\n";
		s += "uniform sampler2DRect thirdTex;\n";
		s += "uniform sampler2DRect fourthTex;\n";
		s += "uniform sampler2DRect fithTex;\n";
		s += "uniform sampler2DRect alphaTex;\n";
		s += "uniform sampler2D zTex;\n";
			
		registerEyePosFunc();
		
		// Add functions
		Iterator<String> it = funcMap.values().iterator();
		while (it.hasNext()) {
			s += it.next();
		}

		if (funcMap.size() > 0)
			s += "\n";
		s += 
			"vec2 unpackFromFloat(float src) {\n"+
		  	" float exp = floor(log2(src));\n"+
//		  	" src = exp <= 0.0 ? (src-1.0) * 8. : (src*(1.0 / exp2(exp))-1.0) * 8.;\n"+
			" src = (src*(1.0 / exp2(exp))-1.0) * 8.;\n"+
			" return vec2( (floor(src) * 0.0078125 + exp * 0.0625) * 256. / 255. , fract(src) * 256. / 255. );\n"+
			"}\n\n";
				
		s += "void main() {\n";

		for (int i = 0; i < var.size(); i++)
			s += " " + var.elementAt(i);

		if (var.size() > 0)
			s += "\n";

		s +=
		"	vec4 posshine = texture2DRect(firstTex, gl_FragCoord.st);\n"+
		"	vec4 diffuseEm = texture2DRect(secondTex, gl_FragCoord.st);\n"+
		"	vec3 diffuse = diffuseEm.rgb;\n"+
		"	vec3 emissive = vec3(unpackFromFloat(diffuseEm.a),0.);\n"+
		"	vec4 specemdifftransp = texture2DRect(thirdTex, gl_FragCoord.st);\n"+
		"	vec3 specular = specemdifftransp.rgb;\n"+
		"	vec3 diffTransp = vec3(unpackFromFloat(specemdifftransp.a),0.0);\n"+
		"	emissive.b = diffTransp.r;\n"+
		"	vec4 alpha = texture2DRect(fourthTex, gl_FragCoord.st);\n"+
		"	diffTransp = vec3(diffTransp.g, unpackFromFloat(alpha.a));\n"+
		"	vec2 norm = posshine.rg;\n"+
//		"	vec2 norm = unpackFromFloat(posshine.b)*2.0-1.0;\n"+
		// calculate needed information
		"	float shininess = posshine.b;\n"+
		"	float transpShininess = posshine.a;\n"+
		"	vec3 diff = vec3(0.0);\n"+
		"	vec3 spec = vec3(0.0);\n"+
		"	vec4 frontAlpha = texture2DRect(alphaTex, gl_FragCoord.st);\n"+
		"	vec4 zNorm = gl_TextureMatrix[4] * vec4(0., 0., texture2D(zTex, TexCoord.st).r*2.-1., 1.);\n"
+
//;
				"	zNorm /= zNorm.w;\n";
//				"	zNorm *= 0.5;\n";
		
		s += isPerspective() ? " vec3 pos = vec3(-TexCoord2.st * zNorm.z, zNorm.z) ;\n" : " vec3 pos = vec3(TexCoord2.st, zNorm.z);\n";
//		s += " vec3 pos = getEyePos(posshine.r);\n";
//		s += " vec3 pos = getEyePos(vec2(em.w, alpha.w));\n";
//		s += isPerspective() ? " vec3 pos = vec3(TexCoord2.st, -1.0) * posshine.r;\n" : " vec3 pos = vec3(TexCoord2.st, -1.0 * posshine.r);\n";
//		s += isPerspective() ? " vec3 pos3 = vec3(TexCoord2.st, -1.0) * posshine.r;\n" : " vec3 pos3 = vec3(TexCoord2.st, -1.0 * posshine.r);\n";
//		s += " pos = vec3(mix(pos, pos3, step(0.5, TexCoord.s)));\n";
		s += lightFunction;
		
		s +=
		"	vec4 color = texture2DRect(fithTex, gl_FragCoord.st)\n"+
						" + vec4( (" +
						"(frontAlpha.rgb) * (1. - alpha.rgb) * " +
						"(diff * diffuse.rgb + spec * specular)), 0.0);\n"+ 
//		"	color = vec4(vec3(abs(pos.z-pos3.z)), 1.0);\n"+
//		"	color = vec4(vec3(mix(-pos.z, -pos3.z, step(0.5, TexCoord.s))), 1.);\n"+
		"	gl_FragColor = color;\n"+
		"}";
		
		String[] code = { s };
		return code;	
	}

	@Override
	public GLSLManagedShader getShaderByDefaultCollection(GLSLDisplay disp,
			Object reference) {
		return (disp.isOptionShowShadows() && !((Light)reference).isShadowless()) ?
				ShadowLightCollection.getGLSLManagedObject(reference) :
				LightCollection.getGLSLManagedObject(reference);
	}
}
