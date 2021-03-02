package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.math.ChannelMap;
import de.grogra.math.RGBColor;

/**
 * Implementation of Shader: RGBColor
 * 
 * @author Konni Hartmann
 */
public class GLSLRGBColor extends GLSLChannelMap {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof RGBColor);
		RGBColor rgbInp = (RGBColor) inp;
		return new Result("vec3(" + rgbInp.x + "," + rgbInp.y + "," + rgbInp.z
				+ ")", Result.ET_VEC3);
	}

	@Override
	public Class instanceFor() {
		return RGBColor.class;
	}

}
