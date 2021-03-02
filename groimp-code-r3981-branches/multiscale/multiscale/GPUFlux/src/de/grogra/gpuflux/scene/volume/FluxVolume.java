package de.grogra.gpuflux.scene.volume;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.FluxObject;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Variables;

public abstract class FluxVolume extends FluxObject {
	
	protected BoundingBox3d bbox = new BoundingBox3d();
	
	public void computeExtent(Variables temp)
	{
		getExtent( bbox, temp );
	}
	
	protected abstract void getExtent (BoundingBox3d bb , Variables temp);
	
	public BoundingBox3d getBoundingBox()
	{
		return bbox;
	}
	
	public void serialize(ComputeByteBuffer out) throws IOException {
		out.writeFloat( bbox.getMin().x );
		out.writeFloat( bbox.getMax().x );
		
		out.writeFloat( bbox.getMin().y );
		out.writeFloat( bbox.getMax().y );
		
		out.writeFloat( bbox.getMin().z );
		out.writeFloat( bbox.getMax().z );
	}
}
