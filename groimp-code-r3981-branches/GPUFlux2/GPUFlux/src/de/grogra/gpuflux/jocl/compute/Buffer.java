package de.grogra.gpuflux.jocl.compute;

import java.io.IOException;
import de.grogra.gpuflux.jocl.JOCLBuffer;

public class Buffer {

	private JOCLBuffer buffer;
	private Device device;
	
	protected Buffer(JOCLBuffer buffer, Device device)
	{
		this.buffer = buffer;
		this.device = device;
	}
	
	public Device getDevice() {
		return device;
	}

	public JOCLBuffer getBuffer() {
		return buffer;
	}
	
	public void clear()
	{
		try {
			Kernel clearkernel = device.getContext().createKernel("kernel/clear_kernel.cl" , "clearBuffer", "");
			
			int nwords = buffer.getSize() / 4;
			
			device.setKernelArgMemBuffer(clearkernel, 1, this);
			device.setKernelArgInt(clearkernel, 2, nwords);
			
			// execute kernel
			device.executeKernel(clearkernel , nwords);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readBuffer(byte[] out) {
		device.getDevice().readBuffer(buffer, out);
	}
	
	public void readBuffer(int[] out) {
		device.getDevice().readBuffer(buffer, out);
	}
}
