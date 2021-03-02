package de.grogra.imp3d.glsl.material.channel;

import java.awt.Image;
import java.awt.image.BufferedImage;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation for ImageMap. Will register and use associated texture. Antialiasing will be handled by mipmaps.
 * 
 * @author Konni Hartmann
 */
public class GLSLImageMap extends GLSLChannelMapNode {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof ImageMap);
		ImageMap map = (ImageMap) inp;
		Image img = map.getImageAdapter() != null ? map.getImageAdapter()
				.getImage() : null;
		if (img != null) {

			String UV = generateResultWithChannelDefault(map.getInput(), cs,
					inpChan, Channel.U).convert(Result.ET_VEC2);

			// TODO: deactivate bilinear filter not possible
			String imgS = cs.registerTexture(img);
			
			if (channel == Channel.DPXDU) {
				
				float du = (img != null) ? 1f / img.getWidth (null) : 0.01f;
				float dv = (img != null) ? 1f / img.getHeight (null) : 0.01f;

				String texUL = cs.registerNewTmpVar(Result.ET_FLOAT, "texture2D("
						+ imgS + ", vec2((" + UV + ").s, -(" + UV + ").t)).r");
				String texUR = cs.registerNewTmpVar(Result.ET_FLOAT, "texture2D("
						+ imgS + ", vec2((" + UV + ").s+"+du+", -(" + UV + ").t)).r");
				String texOL = cs.registerNewTmpVar(Result.ET_FLOAT, "texture2D("
						+ imgS + ", vec2((" + UV + ").s, -(" + UV + ").t-"+dv+")).r");
				return new Result("vec2(" +
						"("+texUR+"-"+texUL+")*"+1.f/du+","+
						"("+texOL+"-"+texUL+")*"+1.f/dv+")", Result.ET_VEC2);
			}
			
			String texVar = cs.registerNewTmpVar(Result.ET_VEC4, "texture2D("
					+ imgS + ", vec2((" + UV + ").s, -(" + UV + ").t))");
			return new Result(texVar, Result.ET_VEC4);
		}
		// XXX: Throw ImageNotFound or something
		return new Result("vec3(1.0, 0.0, 1.0)", Result.ET_VEC3);
	}

	@Override
	public Class instanceFor() {
		return ImageMap.class;
	}

	@Override
	public boolean mayDiscard() {
		return true;
	}

}
