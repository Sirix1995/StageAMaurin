package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.glsl.utility.GLSLQueuedFloatTexture;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.shading.SyntheticTexture;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of ChannelMaps implementing surface-shader: SyntheticTexture. 
 *
 * @author Konni Hartmann
 */
public abstract class GLSLSyntheticTexture extends GLSLChannelMapNode {
	GLSLQueuedFloatTexture data;
	
	/**
	 * Function to register a texture holding all calculated values of the represented shader.
	 * The texture will be automatically generated on use and deleted on deletion of the shaders configuration
	 * @param sTex The SyntheticTexure Objekt that will calculated textures content.
	 * @param p The configuration of the shader using this ChannelMap
	 * @return
	 */
	String regSyntheticTexture(SyntheticTexture sTex, ShaderConfiguration p) {
		data = new GLSLQueuedFloatTexture();
		data.setData(sTex.getWidth(), sTex.getHeight(), sTex.getFloatData());
		return p.registerCustomTexture(data);
	}

	/**
	 * Function for manual bilinear blending
	 */
	final static String linearBlendSig = 
			"float linBlend(sampler2D s, vec2 uv, vec2 texelSize, vec2 textureSize)";
	final static String linearBlend = 
	    "float v00 = texture2D(s, uv).r;\n"+
	    "float v10 = texture2D(s, uv + vec2(texelSize.x, 0)).r;\n"+
	    "float v01 = texture2D(s, uv + vec2(0, texelSize.y)).r;\n"+
	    "float v11 = texture2D(s, uv + texelSize).r;\n"+
	    "vec2 f = fract( uv.xy * textureSize );\n"+
	    "float wx0 = mix( v00, v10, f.x );\n"+ 
	    "float wx1 = mix( v01, v11, f.x );\n"+ 
	    "return mix( wx0, wx1, f.y );";
	
	/**
	 * Function for manual bilinear interpolated finite differencing
	 */
	final static String linearBlendDifSig = 
		"vec2 linBlendDif(sampler2D s, vec2 uv, vec2 texelSize, vec2 textureSize)";
	final static String linearBlendDif = 
    "float v00 = texture2D(s, uv).r;\n"+
    "float v10 = texture2D(s, uv + vec2(texelSize.x, 0)).r;\n"+
    "float v01 = texture2D(s, uv + vec2(0, texelSize.y)).r;\n"+
    "float v11 = texture2D(s, uv + vec2(texelSize.x)).r;\n"+
    "float v20 = texture2D(s, uv + vec2(2.*texelSize.x, 0)).r;\n"+
    "float v02 = texture2D(s, uv + vec2(0, 2.*texelSize.y)).r;\n"+
    "float v21 = texture2D(s, uv + vec2(2.,1.)*texelSize).r;\n"+
    "float v12 = texture2D(s, uv + vec2(1.,2.)*texelSize).r;\n"+
    "vec2 f = fract( uv.xy * textureSize );\n"+
    "float wx0 = mix( v00, v10, f.x );\n"+ 
    "float wx1 = mix( v01, v11, f.x );\n"+ 
    "v00 = mix( wx0, wx1, f.y );"+
    "wx0 = mix( v10, v20, f.x );\n"+ 
    "wx1 = mix( v11, v21, f.x );\n"+ 
    "v10 = mix( wx0, wx1, f.y );"+
    "wx0 = mix( v01, v11, f.x );\n"+ 
    "wx1 = mix( v02, v12, f.x );\n"+ 
    "v01 = mix( wx0, wx1, f.y );"+
	"return (vec2(v10, v01)-v00)*textureSize;";
	
	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof SyntheticTexture);
		SyntheticTexture tex = (SyntheticTexture) inp;
		String sampler = regSyntheticTexture(tex, cs);

		String UV = generateResultWithChannelDefault(tex.getInput(), cs,
				inpChan, Channel.U).convert(Result.ET_VEC2);


		if (channel == Channel.DPXDU) {
			
			cs.registerFunc(linearBlendDifSig, linearBlendDif);
			return new Result("linBlendDif("+sampler+","+UV+",vec2("+1./tex.getHeight()+","+1./tex.getWidth()+"),vec2("+tex.getHeight()+".,"+tex.getWidth()+".))", Result.ET_VEC2);
		}
		
		cs.registerFunc(linearBlendSig, linearBlend);
		return new Result("linBlend("+sampler+","+UV+",vec2("+1./tex.getHeight()+","+1./tex.getWidth()+"),vec2("+tex.getHeight()+".,"+tex.getWidth()+".))", Result.ET_FLOAT);
//		return new Result("texture2D(" + sampler + ","
//				+ UV + ").r", Result.ET_FLOAT);
	}

}
