package de.grogra.gpuflux.scene.shading;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.shading.RGBAShader;

public class FluxRGBAShader extends FluxShader {

	private RGBAShader s;

	public FluxRGBAShader( RGBAShader s )
	{
		this.s = s;
	}
	
	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		out.writeInt(SHADER_RGBA);
		out.writeFloat(s.x);
		out.writeFloat(s.y);
		out.writeFloat(s.z);
		out.writeFloat(s.w);
	}

}
