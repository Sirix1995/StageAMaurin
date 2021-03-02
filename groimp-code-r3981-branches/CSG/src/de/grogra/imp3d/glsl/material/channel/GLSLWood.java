package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Wood;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLWood extends GLSLVolumeFunction {

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Wood);
		Wood wd = (Wood)inp;
		
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(wd.getInput());
		Result res = input != null ? input.generate(wd.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		String pos = res.convert(Result.ET_VEC3);
		String X = "(" + pos + ").x";
		String Y = "(" + pos + ").y";

		return new Result("sqrt(" + X + "*" + X + "+" + Y + "*" + Y + ")",
				Result.ET_FLOAT);
	}

	@Override
	public Class instanceFor() {
		return Wood.class;
	}

}
