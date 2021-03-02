package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;
import java.util.Vector;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;

public class FluxBlend extends FluxChannelMap {

	Vector<FluxBlendItem> blendItems;
	
	public FluxBlend(Vector<FluxBlendItem> blendItems) {
		this.blendItems = blendItems;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		serialize( out, CHANNEL_BLEND );
		out.writeInt(blendItems.size());
		
		for( FluxBlendItem item : blendItems )
		{
			item.serialize(out);
		}
	}

}
