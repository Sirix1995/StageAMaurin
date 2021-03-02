
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

package de.grogra.imp3d.objects;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node.NType;
import de.grogra.math.TMatrix4d;
import de.grogra.math.Transform3D;
import de.grogra.vecmath.Math2;

public abstract class Axis extends AxisBase
{
	protected float length = 1;
	//enh:field attr=Attributes.LENGTH getter setter

	protected float startPosition = 0;
	//enh:field attr=Attributes.START_POSITION getter setter

	protected float endPosition = 1;
	//enh:field attr=Attributes.END_POSITION getter setter


	public Axis ()
	{
	}


	public Axis (Transform3D transform)
	{
		setTransform (transform);
	}


	protected float getPivotShift (Object object, boolean asNode, GraphState gs)
	{
		return 0;
	}


	@Override
	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		Transform3D t;
		float p;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = transform;
				p = startPosition * length;
			}
			else
			{
				t = (Transform3D) gs.checkObject (this, true, Attributes.TRANSFORM, transform);
				p = gs.checkFloat (this, true, Attributes.START_POSITION, startPosition);
				if (p != 0)
				{
					p *= gs.checkDouble (this, true, Attributes.LENGTH, length);
				}
			}
		}
		else
		{
			t = (Transform3D) gs.getObject (object, asNode, null, Attributes.TRANSFORM);
			p = gs.getFloat (object, asNode, Attributes.START_POSITION);
			if (p != 0)
			{
				p *= gs.getDouble (object, asNode, Attributes.LENGTH);
			}
		}
		if (t != null)
		{
			transform.transform (in, out);
		}
		else
		{
			out.set (in);
		}
		p -= getPivotShift (object, asNode, gs);
		if (p != 0)
		{
			out.m03 += p * out.m02;
			out.m13 += p * out.m12;
			out.m23 += p * out.m22;
		}
	}


	@Override
	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
	transform:
		{
			float p;
			if (object == this) 
			{
				if (gs.getInstancingPathIndex () <= 0)
				{
					if ((bits & TRANSFORMING_MASK) == 0)
					{
						break transform;
					}
					p = (endPosition - startPosition) * length;
				}
				else
				{
					if (!gs.checkBoolean (this, true, Attributes.TRANSFORMING, (bits & TRANSFORMING_MASK) != 0))
					{
						break transform;
					}
					p = gs.checkFloat (this, true, Attributes.END_POSITION, endPosition)
						- gs.checkFloat (this, true, Attributes.START_POSITION, startPosition);
					if (p != 0)
					{
						p *= gs.checkDouble (this, true, Attributes.LENGTH, length);
					}
				}
			}
			else
			{
				if (!gs.getBoolean (object, asNode, Attributes.TRANSFORMING))
				{
					break transform;
				}
				p = gs.getFloat (object, asNode, Attributes.END_POSITION)
					- gs.getFloat (object, asNode, Attributes.START_POSITION);
				if (p != 0)
				{
					p *= gs.getDouble (object, asNode, Attributes.LENGTH);
				}
			}
			out.set (in);
			p += getPivotShift (object, asNode, gs);
			if (p != 0)
			{
				out.m03 += p * out.m02;
				out.m13 += p * out.m12;
				out.m23 += p * out.m22;
			}
			return;
		}
		if (out != pre)
		{
			out.set (pre);
		}
	}
	
	
	public void setEndPoints (Tuple3d start, Tuple3d end)
	{
		setEndPoints (start.x, start.y, start.z, end.x, end.y, end.z);
	}


	public void setEndPoints (double xs, double ys, double zs,
								double xe, double ye, double ze)
	{
		Vector3d a = new Vector3d (xe - xs, ye - ys, ze - zs);
		double len = a.length ();
		setLength ((float) len);
		if (len > 0)
		{
			Matrix3d m = new Matrix3d ();
			Math2.getOrthogonalBasis (a, m, true);
			TMatrix4d t = new TMatrix4d ();
			t.m03 = xs;
			t.m13 = ys;
			t.m23 = zs;
			t.setRotationScale (m);
			setTransform (t);
		}
		else
		{
			setTransform (xs, ys, zs);
		}
	}

	public void setEndPoints (Matrix4d coords, Tuple3d target)
	{
		Point3d p = new Point3d (target);
		Math2.invTransformPoint (coords, p);
		setEndPoints (0, 0, 0, p.x, p.y, p.z);
	}
	
	private static void initType ()
	{
		$TYPE.addDependency (startPosition$FIELD.getAttribute (), Attributes.TRANSFORMATION);
		$TYPE.addDependency (endPosition$FIELD.getAttribute (), Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field length$FIELD;
	public static final NType.Field startPosition$FIELD;
	public static final NType.Field endPosition$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Axis.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Axis) o).length = (float) value;
					return;
				case 1:
					((Axis) o).startPosition = (float) value;
					return;
				case 2:
					((Axis) o).endPosition = (float) value;
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
					return ((Axis) o).getLength ();
				case 1:
					return ((Axis) o).getStartPosition ();
				case 2:
					return ((Axis) o).getEndPosition ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (Axis.class);
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (startPosition$FIELD = new _Field ("startPosition", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (endPosition$FIELD = new _Field ("endPosition", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.declareFieldAttribute (length$FIELD, Attributes.LENGTH);
		$TYPE.declareFieldAttribute (startPosition$FIELD, Attributes.START_POSITION);
		$TYPE.declareFieldAttribute (endPosition$FIELD, Attributes.END_POSITION);
		initType ();
		$TYPE.validate ();
	}

	public float getLength ()
	{
		return length;
	}

	public void setLength (float value)
	{
		this.length = (float) value;
	}

	public float getStartPosition ()
	{
		return startPosition;
	}

	public void setStartPosition (float value)
	{
		this.startPosition = (float) value;
	}

	public float getEndPosition ()
	{
		return endPosition;
	}

	public void setEndPosition (float value)
	{
		this.endPosition = (float) value;
	}

//enh:end

}
