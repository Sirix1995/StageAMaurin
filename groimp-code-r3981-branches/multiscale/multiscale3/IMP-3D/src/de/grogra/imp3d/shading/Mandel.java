
package de.grogra.imp3d.shading;

public class Mandel extends VolumeFunction
{

	int iterations = 10;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field iterations$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Mandel.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((Mandel) o).iterations = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Mandel) o).getIterations ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (new Mandel ());
		$TYPE.addManagedField (iterations$FIELD = new _Field ("iterations", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
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
		return new Mandel ();
	}

	public int getIterations ()
	{
		return iterations;
	}

	public void setIterations (int value)
	{
		this.iterations = (int) value;
	}

//enh:end

	@Override
	protected float getFloatValue (float x, float y, float z)
	{
		x = ((x > 0) ? x - (int) x : x + (1 + (int) -x)) * 4 - 2;
		y = ((y > 0) ? y - (int) y : y + (1 + (int) -y)) * 4 - 2;
		float a = x;
		float b = y;
		float a2 = a * a;
		float b2 = b * b;
		float dist2;

		int i;
		for (i = 0; i < iterations; i++)
		{
			b = 2.0f * a * b + y;
			a = a2 - b2 + x;
			a2 = a * a;
			b2 = b * b;
			dist2 = a2 + b2;
			if (dist2 > 4.0f)
			{
				break;
			}
		}

		return (float) (2 * i - iterations) / (float) iterations;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
