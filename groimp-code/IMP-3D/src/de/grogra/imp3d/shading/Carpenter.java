
package de.grogra.imp3d.shading;

import java.util.Random;

public class Carpenter extends SyntheticTexture
{
	float color = 0.5f;
	// enh:field getter setter

	float noise = 1.0f;
	// enh:field getter setter
	
	float roughness = 1.0f;
	// enh:field getter setter
	
	long seed = 0;
	// enh:field getter setter
	
	static private final Random rnd = new Random ();

	@Override
	protected void calculateImageData ()
	{
		rnd.setSeed(0);
		
		// start condition: set pixel to color
		setPixel (0, 0, color);

		// calculate bounds
		int x0 = 0;
		int y0 = 0;
		int x1 = x0 + width;
		int y1 = y0 + height;

		carpenter (x0, y0, x1, y1, noise);
	}
	
	private float rand(float d)
	{
		return d * (2.0f * rnd.nextFloat() - 1.0f);
	}

	private void carpenter (int x0, int y0, int x1, int y1, float d)
	{
		int w = x1 - x0;
		int h = y1 - y0;

		while (w >= 2 || h >= 2)
		{

			for (int x = x0; w > 0 && x < x1; x += w)
			{
				for (int y = y0; h > 0 && y < y1; y += h)
				{
					// do diamond step
					diamondStep (x, y, x + w, y + h, d);
				}
			}

			for (int x = x0; w > 0 && x < x1; x += w)
			{
				for (int y = y0; h > 0 && y < y1; y += h)
				{
					// do square steps
					squareStep (x - w / 2, y, x + w / 2, y + h, d);
					squareStep (x, y - h / 2, x + w, y + h / 2, d);
				}
			}

			w /= 2;
			h /= 2;
			
			d *= Math.pow(2, -roughness);
		}
	}

	private void diamondStep (int x0, int y0, int x1, int y1, float d)
	{
		// take four square corner points and generate a point in the center

		// calculate width and height of the rectangle
		int w = x1 - x0;
		int h = y1 - y0;

		// end recursion if necessary
		if (w < 2 || h < 2)
			return;

		// calculate center coordinates
		int x = (x0 + x1) / 2;
		int y = (y0 + y1) / 2;

		// get the colors
		float c00 = getPixel (x0, y0);
		float c01 = getPixel (x1, y0);
		float c10 = getPixel (x0, y1);
		float c11 = getPixel (x1, y1);

		// calculate new color
		float c = (c00 + c01 + c10 + c11) / 4;

		// add noise
		c += rand(d);

		// set pixel with new color
		setPixel (x, y, c);
	}

	private void squareStep (int x0, int y0, int x1, int y1, float d)
	{
		// take four diamond corner points and generate a point in the center

		// calculate center coordinates
		int x = (x0 + x1) / 2;
		int y = (y0 + y1) / 2;

		// get the colors
		float cx0 = getPixel (x0, y);
		float cx1 = getPixel (x1, y);
		float cy0 = getPixel (x, y0);
		float cy1 = getPixel (x, y1);

		// calculate new color
		float c = (cx0 + cx1 + cy0 + cy1) / 4;

		// add noise
		c += rand(d);

		// set pixel with new color
		setPixel (x, y, c);
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field noise$FIELD;
	public static final NType.Field roughness$FIELD;
	public static final NType.Field seed$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Carpenter.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setLong (Object o, long value)
		{
			switch (id)
			{
				case 3:
					((Carpenter) o).seed = (long) value;
					return;
			}
			super.setLong (o, value);
		}

		@Override
		public long getLong (Object o)
		{
			switch (id)
			{
				case 3:
					return ((Carpenter) o).getSeed ();
			}
			return super.getLong (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Carpenter) o).color = (float) value;
					return;
				case 1:
					((Carpenter) o).noise = (float) value;
					return;
				case 2:
					((Carpenter) o).roughness = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Carpenter) o).getColor ();
				case 1:
					return ((Carpenter) o).getNoise ();
				case 2:
					return ((Carpenter) o).getRoughness ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Carpenter ());
		$TYPE.addManagedField (color$FIELD = new _Field ("color", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (noise$FIELD = new _Field ("noise", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (roughness$FIELD = new _Field ("roughness", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (seed$FIELD = new _Field ("seed", 0 | _Field.SCO, de.grogra.reflect.Type.LONG, null, 3));
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
		return new Carpenter ();
	}

	public long getSeed ()
	{
		return seed;
	}

	public void setSeed (long value)
	{
		this.seed = (long) value;
	}

	public float getColor ()
	{
		return color;
	}

	public void setColor (float value)
	{
		this.color = (float) value;
	}

	public float getNoise ()
	{
		return noise;
	}

	public void setNoise (float value)
	{
		this.noise = (float) value;
	}

	public float getRoughness ()
	{
		return roughness;
	}

	public void setRoughness (float value)
	{
		this.roughness = (float) value;
	}

//enh:end

}
