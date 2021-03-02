
package de.grogra.imp3d.shading;

public class Laplace3D extends Filter
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
		float result = 0.0f;
		gx--;
		gy--;
		gz--;
		if (gx == 0 && gy == 0 && gz == 0)
		{
			result = 6.0f;
		}
		if (Math.abs (gx) + Math.abs (gy) + Math.abs (gz) == 1)
		{
			result = -1.0f;
		}
		result /= 12.0f;
		return result;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Laplace3D ());
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
		return new Laplace3D ();
	}

//enh:end
}
