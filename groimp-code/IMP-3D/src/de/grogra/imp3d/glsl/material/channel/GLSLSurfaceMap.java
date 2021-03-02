package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.math.ChannelMap;

public abstract class GLSLSurfaceMap extends GLSLChannelMapNode {

	public abstract Result generateImpl(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int Channel);

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int Channel) {
		
		
		
		
		return null;
	}

	@Override
	public Class instanceFor() {
		// TODO Auto-generated method stub
		return null;
	}

}
