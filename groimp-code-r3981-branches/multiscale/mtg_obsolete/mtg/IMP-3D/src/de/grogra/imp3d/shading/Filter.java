
package de.grogra.imp3d.shading;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;

/**
 * A Filter is the base for 3D image filtering operations.
 * Subclasses provide a 3D filter coefficient matrix (which is queried by the getCoefficient* functions).
 * The filter is applied to the requested channel by sampling values for each coefficient in the matrix
 * and then calculating the weigthed sum. The size of the sampling cube can be adjusted by the fields
 * sx, sy and sy (in x/y/z and u/v directions).
 * 
 * @author nmi
 *
 */
abstract public class Filter extends ChannelMapNode
{
	// filter width
	float sx = 1;
	// enh:field getter setter

	// filter height
	float sy = 1;
	// enh:field getter setter

	// filter depth
	float sz = 1;
	// enh:field getter setter

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field sx$FIELD;
	public static final NType.Field sy$FIELD;
	public static final NType.Field sz$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Filter.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Filter) o).sx = (float) value;
					return;
				case 1:
					((Filter) o).sy = (float) value;
					return;
				case 2:
					((Filter) o).sz = (float) value;
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
					return ((Filter) o).getSx ();
				case 1:
					return ((Filter) o).getSy ();
				case 2:
					return ((Filter) o).getSz ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (Filter.class);
		$TYPE.addManagedField (sx$FIELD = new _Field ("sx", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (sy$FIELD = new _Field ("sy", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (sz$FIELD = new _Field ("sz", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.validate ();
	}

	public float getSx ()
	{
		return sx;
	}

	public void setSx (float value)
	{
		this.sx = (float) value;
	}

	public float getSy ()
	{
		return sy;
	}

	public void setSy (float value)
	{
		this.sy = (float) value;
	}

	public float getSz ()
	{
		return sz;
	}

	public void setSz (float value)
	{
		this.sz = (float) value;
	}

//enh:end

	abstract int getCoefficientMaskSizeX ();

	abstract int getCoefficientMaskSizeY ();

	abstract int getCoefficientMaskSizeZ ();

	/**
	 * Get the coefficient at position (gx/gy/gz) in the filter matrix. The
	 * functions getCoefficientMaskSize* are used to obtain the indexable range
	 * (i.e. sx, sy and sz), so 0 <= gx <= sx. Derived classes implement this
	 * function to provide filter coefficients. The Filter class will perform
	 * the filtering by applying those coefficients as weigths to the sampled
	 * values from the input channel.
	 * 
	 * @param gx
	 * @param gy
	 * @param gz
	 * @return
	 */
	abstract protected float getCoefficient (int gx, int gy, int gz);

	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		/*
		 * ChannelData in = data.getData (input); switch (channel) { case
		 * Channel.X: case Channel.Y: case Channel.Z: case Channel.U: case
		 * Channel.V: case Channel.W: case Channel.R: case Channel.G: case
		 * Channel.B: return data.getValidFloatValue (channel); default: return
		 * data.forwardGetFloatValue (in); }
		 */

		ChannelData in = data.getData (input);

		// get current values of input data vector
		float x = data.getData (null).getFloatValue (data, Channel.X);
		float y = data.getData (null).getFloatValue (data, Channel.Y);
		float z = data.getData (null).getFloatValue (data, Channel.Z);
		float u = data.getData (null).getFloatValue (data, Channel.U);
		float v = data.getData (null).getFloatValue (data, Channel.V);

		float result = 0;

		// dont modify alpha channel
		if ((channel & 3) == 3)
		{
			result = 1.0f;
		}
		else
		{
			// for each coefficient in filter cube
			int gxMax = getCoefficientMaskSizeX ();
			int gyMax = getCoefficientMaskSizeY ();
			int gzMax = getCoefficientMaskSizeZ ();
			for (int gx = 0; gx < gxMax; gx++)
			{
				for (int gy = 0; gy < gyMax; gy++)
				{
					for (int gz = 0; gz < gzMax; gz++)
					{
						// get coefficient from filter cube
						float c = getCoefficient (gx, gy, gz);

						// only perform calculation if coefficient is not zero
						if (c != 0)
						{
							// set current filter x/y/z position
							data
									.getData (null)
									.setTuple3f (
											Channel.X,
											x
													+ sx
													* ((float) gx
															/ (float) (gxMax - 1) - 0.5f),
											y
													+ sy
													* ((float) gy
															/ (float) (gyMax - 1) - 0.5f),
											z
													+ sz
													* ((float) gz
															/ (float) (gzMax - 1) - 0.5f));
							// set filter u/v position
							data
									.getData (null)
									.setTuple2f (
											Channel.U,
											u
													+ sx
													* ((float) gx
															/ (float) (gxMax - 1) - 0.5f),
											v
													+ sy
													* ((float) gy
															/ (float) (gyMax - 1) - 0.5f));
							// get channel value at this position
							float value = in.getFloatValue (data, channel);
							value *= c;
							result += value;
						}
					}
				}
			}
		}

		// restore input data vector
		data.getData (null).setFloat (Channel.X, x);
		data.getData (null).setFloat (Channel.Y, y);
		data.getData (null).setFloat (Channel.Z, z);
		data.getData (null).setFloat (Channel.U, u);
		data.getData (null).setFloat (Channel.V, v);

		return result;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
