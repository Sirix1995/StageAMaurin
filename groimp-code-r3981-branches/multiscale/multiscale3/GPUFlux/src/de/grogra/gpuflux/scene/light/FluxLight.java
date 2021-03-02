package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.scene.FluxObject;
import de.grogra.gpuflux.scene.shading.FluxSpectrum;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.spectral.RGBSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.vecmath.Math2;

public abstract class FluxLight extends FluxObject{

	protected static final int LIGHT_POINT = 0;
	protected static final int LIGHT_DIRECTIONAL = 1;
	protected static final int LIGHT_SPOT = 2;
	protected static final int LIGHT_AREA = 3;
	protected static final int LIGHT_PHYSICAL = 4;
	protected static final int LIGHT_SKY = 5;
	protected static final int LIGHT_SPECTRAL = 6;
		
	private Matrix4d transformation;
	protected Light light;
	
	public FluxLight( Light light )
	{
		this.light = light;
	}
	
	protected Matrix4d getTransformation() {
		return transformation;
	}

	public void setTransformation(Matrix4d transformation) {
		this.transformation = (Matrix4d) transformation.clone();
	}
	
	protected void serializeLightBase(ComputeByteBuffer out, int type, final Tuple3f power, SpectralCurve SPD) throws IOException
	{
		// set shader offset in output buffer
		setOffset( out.size() );
		
		out.writeInt( type );
		out.writeInt( getSampleCount() );
				
		Matrix4d m = getTransformation();
		Matrix3f rot = new Matrix3f();
		m.getRotationScale(rot);
		Vector3d trans = new Vector3d();
		m.get(trans);
		
		// serialize object to world transformation matrix
		out.write( rot );
		out.write( trans );
		
		Matrix4d m1 = new Matrix4d();
		m1.m33 = 1;
		Math2.invertAffine (m, m1);
		m1.getRotationScale(rot);
		m1.get(trans);
		
		// serialize world to object transformation matrix
		out.write( rot );
		out.write( trans );

		// serialize color
		out.writeFloat( power.x );
		out.writeFloat( power.y );
		out.writeFloat( power.z );
		
		// set default curve, bases on rgb color
		if( SPD == null )
		{
			SPD = new RGBSpectralCurve(power.x,power.y,power.z);
		}
		
		// serialize discretized cumulative spectral importance
		FluxSpectrum.serializeCorrectedCumulativeSPD( out, SPD );
	}
	
	public int getSampleCount() { return 1; };
	
	public Light getLight() {
		return light;
	}

}
