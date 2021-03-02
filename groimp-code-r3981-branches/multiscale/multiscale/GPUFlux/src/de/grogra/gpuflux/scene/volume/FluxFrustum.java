package de.grogra.gpuflux.scene.volume;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Cone;
import de.grogra.vecmath.geom.Variables;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class FluxFrustum extends FluxFrustumBase
{
	private Cone v;

	public FluxFrustum(Cone frustum) {
		super();
		
		this.v = frustum;
	}

	@Override
	public void getExtent(BoundingBox3d bb, Variables temp) {
		v.getExtent(bb.getMin(), bb.getMax(), temp);
	}

	@Override
	public void serialize(ComputeByteBuffer out) throws IOException {
		serialize( out, PRIM_FRUSTUM, v );
		
		out.writeFloat((float) v.getBase());
		out.writeFloat((float) v.getTop());
		out.writeFloat((float) v.scaleV);
		
		int flags = 0;
		if( v.isTopOpen() )
			flags |= TOP_OPEN;
		if( v.isBaseOpen() )
			flags |= BASE_OPEN;
		if( v.isUVRotated() )
			flags |= UV_ROTATED;
		
		out.writeInt(flags);
	}
	
}