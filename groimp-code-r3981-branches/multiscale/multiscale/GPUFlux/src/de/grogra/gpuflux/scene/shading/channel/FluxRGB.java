package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.math.RGBColor;

public class FluxRGB extends FluxChannelMap {

	RGBColor rgbColor;
	
	public FluxRGB(RGBColor rgbColor) {
		this.rgbColor = rgbColor;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		//out.writeInt( CHANNEL_RGB );
		serialize( out, CHANNEL_RGB );
		
		out.writeFloat(rgbColor.x);
		out.writeFloat(rgbColor.y);
		out.writeFloat(rgbColor.z);
	}

}
