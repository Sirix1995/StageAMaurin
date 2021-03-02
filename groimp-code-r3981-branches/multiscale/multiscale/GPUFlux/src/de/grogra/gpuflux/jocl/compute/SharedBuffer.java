package de.grogra.gpuflux.jocl.compute;

import de.grogra.gpuflux.jocl.JOCLBuffer;

public class SharedBuffer {

	private ComputeContext context;
	private JOCLBuffer littleEndianBuffers[];
	private JOCLBuffer bigEndianBuffers[];
	
	protected SharedBuffer(int size, ComputeContext context) {
		littleEndianBuffers = new JOCLBuffer[size];
		bigEndianBuffers = new JOCLBuffer[size];
		this.context = context;
	}

	protected void setContextBuffer(int i, JOCLBuffer joclLittleEndianBuffer, JOCLBuffer joclBigEndianBuffer) {
		littleEndianBuffers[i] = joclLittleEndianBuffer;
		bigEndianBuffers[i] = joclBigEndianBuffer;
	}
	
	public ComputeContext getContext() {
		return context;
	}

	protected JOCLBuffer getBuffer(int contextID, boolean littleEndian) {
		return littleEndian?littleEndianBuffers[contextID]:bigEndianBuffers[contextID];
	}
	
	

}
