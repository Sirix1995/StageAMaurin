package de.grogra.imp3d.glsl.material;

import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLDefaultInput extends GLSLChannelMap {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			de.grogra.imp3d.glsl.material.channel.GLSLChannelMap inpChan, int channel) {
		assert (inp == null);
		assert (inpChan == null);
		
		switch (channel) {
		case Channel.U: {
			cs.setBit(ShaderConfiguration.USE_UV);
			return new Result("uv", Result.ET_VEC2);
		}
		case Channel.PX: {
			cs.setBit(ShaderConfiguration.USE_GLOBAL_POS);
			System.out.println("Using global pos");
			return new Result("g_pos", Result.ET_VEC3);
		}
		case Channel.DPXDU:
			cs.setBit(ShaderConfiguration.USE_DERIVATES);
			return new Result("dpdu", Result.ET_VEC3);
		case Channel.DPXDV:
			cs.setBit(ShaderConfiguration.USE_DERIVATES);
			return new Result("dpdv", Result.ET_VEC3);			
		case Channel.R:
		case Channel.X: {
			cs.setBit(ShaderConfiguration.USE_LOCAL_POS);
			return new Result("n_pos", Result.ET_VEC3);
		}
		case Channel.NX:
			return new Result("normal", Result.ET_VEC3);
		default:
			break;
		}
		
		return null;
	}

	@Override
	public Class instanceFor() {
		return null;
	}

}
