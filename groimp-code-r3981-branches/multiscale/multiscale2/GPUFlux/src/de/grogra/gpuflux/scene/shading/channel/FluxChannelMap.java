package de.grogra.gpuflux.scene.shading.channel;

import java.io.IOException;

import javax.vecmath.Matrix3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.FluxObject;

public abstract class FluxChannelMap extends FluxObject {

	protected static final int CHANNEL_IMAGE_MAP = 0;
	protected static final int CHANNEL_RGB = 1;
	protected static final int CHANNEL_UVTRANSFORMATION = 2;
	protected static final int CHANNEL_HDRIMAGE_MAP = 3;
	protected static final int CHANNEL_BLEND = 4;
	protected static final int CHANNEL_SPECTRAL = 0x100;
	//protected static final int CHANNEL_IMAGE_MAP_LINEAR = 0x10;
	private float m00;
	private float m01;
	private float m02;
	private float m10;
	private float m11;
	private float m12;
	
	public void setUVTransformation(Matrix3f m) {
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
	}
	
	public void serialize(ComputeByteBuffer out, int type)
		throws IOException {
		
		out.writeInt( type );
		
		out.writeFloat(m00);
		out.writeFloat(m01);
		out.writeFloat(m02);
		out.writeFloat(m10);
		out.writeFloat(m11);
		out.writeFloat(m12);
		
		out.writeInt( -1 );
	}
	
}
