
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d.shading;

import javax.vecmath.Matrix3f;

import de.grogra.graph.impl.Node.NType;
import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XObject;

public class AffineUVTransformation extends UVTransformation
{
	float offsetU;
	//enh:field getter setter

	float offsetV;
	//enh:field getter setter

	float scaleU = 1;
	//enh:field getter setter

	float scaleV = 1;
	//enh:field getter setter

	float angle;
	//enh:field getter setter quantity=ANGLE

	float shear;
	//enh:field getter setter

	private transient boolean transformValid = false;
	private transient Matrix3f transform = new Matrix3f (),
		invTransform = new Matrix3f ();

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field offsetU$FIELD;
	public static final NType.Field offsetV$FIELD;
	public static final NType.Field scaleU$FIELD;
	public static final NType.Field scaleV$FIELD;
	public static final NType.Field angle$FIELD;
	public static final NType.Field shear$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (AffineUVTransformation.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((AffineUVTransformation) o).offsetU = (float) value;
					return;
				case 1:
					((AffineUVTransformation) o).offsetV = (float) value;
					return;
				case 2:
					((AffineUVTransformation) o).scaleU = (float) value;
					return;
				case 3:
					((AffineUVTransformation) o).scaleV = (float) value;
					return;
				case 4:
					((AffineUVTransformation) o).angle = (float) value;
					return;
				case 5:
					((AffineUVTransformation) o).shear = (float) value;
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
					return ((AffineUVTransformation) o).getOffsetU ();
				case 1:
					return ((AffineUVTransformation) o).getOffsetV ();
				case 2:
					return ((AffineUVTransformation) o).getScaleU ();
				case 3:
					return ((AffineUVTransformation) o).getScaleV ();
				case 4:
					return ((AffineUVTransformation) o).getAngle ();
				case 5:
					return ((AffineUVTransformation) o).getShear ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new AffineUVTransformation ());
		$TYPE.addManagedField (offsetU$FIELD = new _Field ("offsetU", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (offsetV$FIELD = new _Field ("offsetV", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (scaleU$FIELD = new _Field ("scaleU", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (scaleV$FIELD = new _Field ("scaleV", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (angle$FIELD = new _Field ("angle", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (shear$FIELD = new _Field ("shear", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		angle$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
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
		return new AffineUVTransformation ();
	}

	public float getOffsetU ()
	{
		return offsetU;
	}

	public void setOffsetU (float value)
	{
		this.offsetU = (float) value;
	}

	public float getOffsetV ()
	{
		return offsetV;
	}

	public void setOffsetV (float value)
	{
		this.offsetV = (float) value;
	}

	public float getScaleU ()
	{
		return scaleU;
	}

	public void setScaleU (float value)
	{
		this.scaleU = (float) value;
	}

	public float getScaleV ()
	{
		return scaleV;
	}

	public void setScaleV (float value)
	{
		this.scaleV = (float) value;
	}

	public float getAngle ()
	{
		return angle;
	}

	public void setAngle (float value)
	{
		this.angle = (float) value;
	}

	public float getShear ()
	{
		return shear;
	}

	public void setShear (float value)
	{
		this.shear = (float) value;
	}

//enh:end

	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		super.fieldModified (field, indices, t);
		transformValid = false;
	}

	private void constructTransformMatrix()
	{
		Matrix3f m = transform;
		Matrix3f i = invTransform;
		if (!transformValid)
		{
			transformValid = true;
			m.setIdentity ();
			m.m02 = -offsetU;
			m.m12 = -offsetV;
			i.rotZ (-angle);
			i.mul (m);

			m.m00 = scaleU;
			m.m11 = scaleV;
			m.m01 = -shear * scaleV;
			m.m02 = 0;
			m.m12 = 0;
			m.mul (i);
			de.grogra.vecmath.Math2.invertAffine (m, i);
		}
	}
	
	public Matrix3f getTransform()
	{
		constructTransformMatrix();
		return transform;
	}
	
	public Matrix3f getInvTransform()
	{
		constructTransformMatrix();
		return invTransform;
	}

	@Override
	protected void transform (ChannelData src, ChannelData dest,
							  boolean calculateDerivatives)
	{
		Matrix3f m = getTransform();
		Matrix3f i = getInvTransform();
		float u = src.getFloatValue (dest, Channel.U);
		float v = src.getFloatValue (dest, Channel.V);
		dest.setFloat (Channel.U, m.m00 * u + m.m01 * v + m.m02);
		dest.setFloat (Channel.V, m.m10 * u + m.m11 * v + m.m12);
		if (calculateDerivatives)
		{
			setDerivatives (i.m00, i.m01, i.m10, i.m11, src, dest);
		}
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}

}
