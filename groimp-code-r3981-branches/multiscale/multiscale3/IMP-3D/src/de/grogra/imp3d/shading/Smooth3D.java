
package de.grogra.imp3d.shading;

public class Smooth3D extends Filter
{
	@Override
	int getCoefficientMaskSizeX ()
	{
		return 3;
	}

	@Override
	int getCoefficientMaskSizeY ()
	{
		return 3;
	}

	@Override
	int getCoefficientMaskSizeZ ()
	{
		return 3;
	}

	@Override
	protected float getCoefficient (int gx, int gy, int gz)
	{
		// TODO Auto-generated method stub
		return (float)1.0f / (float)27.0f;
	}

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Smooth3D ());
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
		return new Smooth3D ();
	}

//enh:end
}
