package de.grogra.gpuflux.scene.shading.channel;

import java.awt.image.BufferedImage;
import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;

public class FluxImageMap extends FluxChannelMap {

	private BufferedImage image;
	
	public FluxImageMap(BufferedImage image) {
		this.image = image;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		serialize( out, CHANNEL_IMAGE_MAP );
		
		int w = image.getWidth (), h = image.getHeight ();
		
		int pixels[] = image.getRGB(0,0,w,h,null,0,w);
		
		out.writeInt( w );
		out.writeInt( h );
		
		out.write( pixels );
	}

}
