package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.objects.PhysicalLight;
import de.grogra.math.RGBColor;

public class FluxPhysicalLight extends FluxLight {


	private PhysicalLight physicalLight;

	public FluxPhysicalLight(PhysicalLight physicalLight) {
		super( physicalLight );
		this.physicalLight = physicalLight;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		RGBColor power3f = (RGBColor) physicalLight.getColor().clone();
		power3f.scale (3 * physicalLight.getPower() / (power3f.x + power3f.y + power3f.z));
		
		serializeLightBase( out, LIGHT_PHYSICAL, power3f, null );
		
		int width = physicalLight.getDistribution().getWidth();
		int height = physicalLight.getDistribution().getHeight();
		double [][] lipdf = physicalLight.getDistribution().getDistribution();
		double [] licdf = physicalLight.getDistribution().getLinearCDF();
		
		out.writeInt( width );
		out.writeInt( height );
		
		for( int y = 0 ; y < height ; y++ )
			for( int x = 0 ; x < width ; x++ )
				out.writeFloat( (float)lipdf[y][x] );
				
		for( int i = 0 ; i < licdf.length ; i++ )
			out.writeFloat( (float)licdf[i] );
	}
	

}
