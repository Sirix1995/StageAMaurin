package de.grogra.gpuflux.scene.shading;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;

public class FluxSwitchShader extends FluxShader{

	public FluxSwitchShader(FluxShader front, FluxShader back)
	{
		this.front = front;
		this.back = back;
	}
	
	FluxShader front, back;
	
	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		out.writeInt(SHADER_SWITCH);
		out.writeInt(front.getOffset());
		out.writeInt(back.getOffset());
	}

}
