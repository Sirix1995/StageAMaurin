package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.shading.BlendItem;

public class FluxBlendItem {
	
	private FluxChannelMap channel;
	private BlendItem item;
	
	public FluxBlendItem( FluxChannelMap channel, BlendItem item )
	{
		this.channel = channel;
		this.item = item;
	}

	public void serialize(ComputeByteBuffer out) throws IOException {
		out.writeFloat(item.getValue());
		out.writeInt(channel.getOffset());
	}
}
