package de.grogra.gpuflux.scene.light;

import java.io.IOException;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Tuple3fType;

public class FluxParallelLight extends FluxLight {

	private static final int SAMPLE_COUNT = 50;
	private final Parallelogram parallelogram;

	public FluxParallelLight(Parallelogram parallelogram) {
		super( parallelogram );
		this.parallelogram = parallelogram;
	}

	@Override
	public int getSampleCount() { return SAMPLE_COUNT; };
	
	@Override
	public void serialize(ComputeByteBuffer out)
			throws IOException {
		float power = parallelogram.getAreaLight().getPower();
		Shader sh = parallelogram.getShader();
		
		Color3f power3f = new Color3f ();
		Tuple3fType.setColor (power3f, (sh != null) ? sh.getAverageColor () : -1);
		power3f.scale (3 * power / (power3f.x+power3f.y+power3f.z));
		
		// serialize the base light
		serializeLightBase( out, LIGHT_AREA, power3f, null );
		
		// write exponent
		out.writeFloat( parallelogram.getAreaLight().getExponent() );
		
		Matrix4d t = getTransformation();
		
		// compute normal transformation matrix
		Matrix4d n = new Matrix4d();
		n.invert(t);
		n.transpose();
		
		Vector3f axis = new Vector3f();
		axis.set( parallelogram.getAxis() );
		
		// calculate normal vector for surface
		Vector3f normal = new Vector3f();
		Vector3f par_axis = new Vector3f (0, 0, parallelogram.getLength());
		normal.cross (axis, par_axis);
		normal.normalize();
		
		// transform parallelogram to world space
		t.transform(axis);
		t.transform(par_axis);
		n.transform(normal);
		
		// write parallelogram
		out.write( axis );	
		out.write( par_axis );
		out.write( normal );
	}
	
}
