package de.grogra.gpuflux.jocl.compute;

import de.grogra.gpuflux.jocl.JOCLKernel;

public class Kernel {

	private ComputeContext context;
	private JOCLKernel kernels[];
	
	protected Kernel(int size, ComputeContext context) {
		this.context = context;
		kernels = new JOCLKernel[size];
	}

	protected void setContextKernel(int deviceIdx, JOCLKernel joclKernel) {
		kernels[deviceIdx] = joclKernel;
	}

	public ComputeContext getContext() {
		return context;
	}

	protected JOCLKernel getKernel(int contextID) {
		return kernels[contextID];
	}

}
