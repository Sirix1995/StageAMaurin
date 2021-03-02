
package de.grogra.imp3d.shading;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.ChannelData;

abstract public class SyntheticTexture extends SurfaceMap
{
	// image width
	int width = 256;
	// enh:field getter setter

	// image height
	int height = 256;
	// enh:field getter setter

	// contains calculated image data
	float[] image = new float[0];

	private transient int imageStamp = -1;

	// needed for use in procedural textures see de.grogra.imp3d.glsl.shader.GLSLSyntheticTexture
	public float[] getFloatData() { 
		// calculate image if needed
		int s = getStamp ();
		// check if image parameters were modified
		synchronized (this)
		{
			if (s != imageStamp)
			{
				// recreate image
				createImage ();
				// remember stamp
				imageStamp = s;
			}
		}
		return image; 
		}
	
	public int getAverageColor ()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	protected void createImage ()
	{
		// create image buffer if necessary
		if (image == null || image.length < width * height)
		{
			image = new float[width * height];
		}

		// check if buffer fits required size
		assert image.length >= (width * height);

		// generate content for the image
		calculateImageData ();
	}

	private int ensureRange (int value, int min, int max)
	{
		int range = max - min;
		while (value < min)
			value += range;
		while (value >= max)
			value -= range;
		return value;
	}

	protected float getPixel (int x, int y)
	{
		x = ensureRange (x, 0, width);
		y = ensureRange (y, 0, height);
		return image[y * width + x];
	}

	protected void setPixel (int x, int y, float value)
	{
		x = ensureRange (x, 0, width);
		y = ensureRange (y, 0, height);
		image[y * width + x] = value;
	}

	protected abstract void calculateImageData ();

	@Override
	protected float getFloatValueImpl (float u, float v, ChannelData data,
			int channel)
	{
		float result = 1.0f;

		// only perform calculation for non-alpha channel
		if ((channel & 3) != 3)
		{
			int s = getStamp ();
			// check if image parameters were modified
			synchronized (this)
			{
				if (s != imageStamp)
				{
					// recreate image
					createImage ();
					// remember stamp
					imageStamp = s;
				}
			}

			// calculate x/y position in image
			float x = u * (float) width;
			float y = v * (float) height;

			// get the four coordinates for the four corners
			int x0 = (int) x;
			int x1 = x0 + 1;
			int y0 = (int) y;
			int y1 = y0 + 1;

			// calculate weigths
			// float wx0 = x - (float) x0;
			// float wx1 = (float) x1 - x;
			// float wy0 = y - (float) y0;
			// float wy1 = (float) y1 - y;
			float wx0 = ((float) x1 - x) / ((float) x1 - (float) x0);
			float wx1 = (x - (float) x0) / ((float) x1 - (float) x0);
			float wy0 = ((float) y1 - y) / ((float) y1 - (float) y0);
			float wy1 = (y - (float) y0) / ((float) y1 - (float) y0);

			// sample image bilinear
			float v00 = getPixel (x0, y0);
			float v01 = getPixel (x1, y0);
			float v10 = getPixel (x0, y1);
			float v11 = getPixel (x1, y1);

			// calculate color value
			result = v00 * wy0 * wx0 + v01 * wy0 * wx1 + v10 * wy1 * wx0 + v11
					* wy1 * wx1;
		}

		return result;
	}

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field width$FIELD;
	public static final NType.Field height$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SyntheticTexture.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((SyntheticTexture) o).width = (int) value;
					return;
				case 1:
					((SyntheticTexture) o).height = (int) value;
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
					return ((SyntheticTexture) o).getWidth ();
				case 1:
					return ((SyntheticTexture) o).getHeight ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (SyntheticTexture.class);
		$TYPE.addManagedField (width$FIELD = new _Field ("width", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (height$FIELD = new _Field ("height", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.validate ();
	}

	public int getWidth ()
	{
		return width;
	}

	public void setWidth (int value)
	{
		this.width = (int) value;
	}

	public int getHeight ()
	{
		return height;
	}

	public void setHeight (int value)
	{
		this.height = (int) value;
	}

//enh:end

}
