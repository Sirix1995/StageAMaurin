package de.grogra.gpuflux.scene.volume;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.HalfSpace;
import de.grogra.vecmath.geom.Variables;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class FluxPlane extends FluxPrimitive
{

	private HalfSpace v;

	public FluxPlane(HalfSpace v) {
		super();
		
		this.v = v;
	}

	@Override
	public void getExtent(BoundingBox3d bb, Variables temp) {
		v.getExtent(bb.getMin(), bb.getMax(), temp);
	}

	@Override
	public void serialize(ComputeByteBuffer out) throws IOException {
		serialize( out, PRIM_PLANE, v );
	}
	
}