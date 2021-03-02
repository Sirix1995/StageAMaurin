package de.grogra.gpuflux.scene.volume;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Cylinder;
import de.grogra.vecmath.geom.Variables;

public class FluxCylinder extends FluxFrustumBase {

	Cylinder v;
	
	public FluxCylinder(Cylinder cylinder) {
		super();
		
		this.v = cylinder;
	}

	@Override
	public void getExtent(BoundingBox3d bb, Variables temp) {
		v.getExtent(bb.getMin(), bb.getMax(), temp);

	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		
		serialize( out, PRIM_FRUSTUM, v );
		
		out.writeFloat((float)  1.f);
		out.writeFloat((float) -1.f);
		out.writeFloat((float) v.scaleV);
		
		int flags = FRUSTUM_CYLINDER;
		out.writeInt(flags);
	}

}
