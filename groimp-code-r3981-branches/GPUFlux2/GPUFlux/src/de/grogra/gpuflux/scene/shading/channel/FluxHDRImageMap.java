package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;

import javax.vecmath.Tuple3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;

public class FluxHDRImageMap extends FluxChannelMap {

	private Tuple3f[][] image;
	
	public FluxHDRImageMap(Tuple3f[][] image) {
		this.image = image;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		serialize( out, CHANNEL_HDRIMAGE_MAP );
		
		int w = image.length, h = image[0].length;
		
		out.writeInt( w );
		out.writeInt( h );
		
		for( int y = 0 ; y < h; y++ )
			for( int x = 0 ; x < w; x++ )
			{
				out.write( image[y][x] );
			}			
	}
}
