package de.grogra.gpuflux.scene.shading;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.shading.IORShader;

public class FluxIORShader extends FluxShader {

	float iorA, iorB;
	FluxShader fluxInput;
	
	public FluxIORShader(IORShader iorShader, FluxShader fluxInput) {
		iorA = iorShader.getIorA();
		iorB = iorShader.getIorB();
		this.fluxInput = fluxInput;
	}

	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		out.writeInt(SHADER_IOR);
		out.writeFloat(iorA);
		out.writeFloat(iorB);
		out.writeInt(fluxInput.getOffset());
	}

}
