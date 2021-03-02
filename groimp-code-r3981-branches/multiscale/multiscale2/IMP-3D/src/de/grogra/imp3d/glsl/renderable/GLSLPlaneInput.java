package de.grogra.imp3d.glsl.renderable;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.glsl.material.channel.GLSLChannelMap;
import de.grogra.imp3d.glsl.material.channel.Result;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLPlaneInput extends GLSLChannelMap {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			de.grogra.imp3d.glsl.material.channel.GLSLChannelMap inpChan, int channel) {
		assert (inp == null);
		assert (inpChan == null);
		
		switch (channel) {
		case Channel.U:
			return new Result("plane_uv", Result.ET_VEC2);
		case Channel.X:
			return new Result("local_plane_pos.xyz", Result.ET_VEC3);
		case Channel.NX:
			return new Result("plane_normal", Result.ET_VEC3);
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
