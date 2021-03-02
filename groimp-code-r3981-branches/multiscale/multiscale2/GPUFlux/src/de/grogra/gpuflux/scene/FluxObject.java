package de.grogra.gpuflux.scene;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;

public abstract class FluxObject {

	private int offset;
	
	public abstract void serialize ( ComputeByteBuffer computeByteBuffer ) throws IOException;
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}
	
}
