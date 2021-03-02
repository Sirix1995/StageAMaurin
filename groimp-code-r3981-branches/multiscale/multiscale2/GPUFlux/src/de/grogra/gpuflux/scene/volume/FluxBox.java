package de.grogra.gpuflux.scene.volume;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Cube;
import de.grogra.vecmath.geom.Variables;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class FluxBox extends FluxPrimitive
{
	private Cube v;

	public FluxBox(Cube v) {
		super();
		
		this.v = v;
	}

	@Override
	public void getExtent(BoundingBox3d bb, Variables temp) {
		v.getExtent(bb.getMin(), bb.getMax(), temp);
	}

	@Override
	public void serialize(ComputeByteBuffer out) throws IOException {
		serialize( out, PRIM_BOX, v );
	}	
}