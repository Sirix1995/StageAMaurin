package de.grogra.ext.sunshine.spectral.shader;

import java.util.Random;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import de.grogra.imp3d.shading.ImageMap;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class SunshineImageChannel extends ImageMap implements SunshineChannel {

	public int getFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		// TODO Auto-generated method stub

	}

	public void computeMaxRays(Environment env, Vector3f in, Spectrum specIn,
			Ray reflected, Tuple3f refVariance, Ray transmitted,
			Tuple3f transVariance) {
		// TODO Auto-generated method stub

	}

	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void shade(Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color) {
		// TODO Auto-generated method stub

	}
	
	public Object getContent()
	{
		return this.getImageAdapter().getNativeImage();
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new SunshineImageChannel ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new SunshineImageChannel ();
	}

//enh:end

}
