
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

package de.grogra.turtle;

import javax.vecmath.Matrix4d;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Transformation;

/**
 * A <code>Rotate</code> node rotates the local coordinate system
 * by {@link #rotateX} degrees about the local x-axis, then
 * by {@link #rotateY} degrees about the local y-axis, then
 * by {@link #rotateZ} degrees about the local z-axis.
 * <br>
 * This class declares the predicate
 * {@link Rotate.Pattern} to allow one to write
 * <code>Rotate(x,y,z)</code> in a pattern of an XL query.
 * 
 * @author Ole Kniemeyer
 */
public class Rotate extends Node implements Transformation
{
	/**
	 * The rotation angle about the x-axis in degrees.
	 */
	public float rotateX;
	//enh:field

	/**
	 * The rotation angle about the y-axis in degrees.
	 */
	public float rotateY;
	//enh:field

	/**
	 * The rotation angle about the z-axis in degrees.
	 */
	public float rotateZ;
	//enh:field


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Rotate.$TYPE, new NType.Field[] {rotateX$FIELD, rotateY$FIELD, rotateZ$FIELD});
		}

		public static void signature (@In @Out Rotate n, float x, float y, float z)
		{
		}
	}


	/**
	 * Creates a new <code>Rotate</code> node whose angles
	 * are set to zero.
	 */
	public Rotate ()
	{
		super ();
	}


	/**
	 * Create a new <code>Rotate</code> node which rotates
	 * according to the specified values.
	 * 
	 * @param rotateX rotation angle about x-axis in degrees
	 * @param rotateY rotation angle about y-axis in degrees
	 * @param rotateZ rotation angle about z-axis in degrees
	 */
	public Rotate (float rotateX, float rotateY, float rotateZ)
	{
		this.rotateX = rotateX;
		this.rotateY = rotateY;
		this.rotateZ = rotateZ;
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		double s, c, t;
		
		// rotation about x-axis
		c = Math.cos (rotateX * (Math.PI / 180));
		s = Math.sin (rotateX * (Math.PI / 180));
		out.m00 = in.m00;
		out.m01 = c * (t = in.m01) + s * in.m02;
		out.m02 = c * in.m02 - s * t;
		out.m03 = in.m03;
		out.m10 = in.m10;
		out.m11 = c * (t = in.m11) + s * in.m12;
		out.m12 = c * in.m12 - s * t;
		out.m13 = in.m13;
		out.m20 = in.m20;
		out.m21 = c * (t = in.m21) + s * in.m22;
		out.m22 = c * in.m22 - s * t;
		out.m23 = in.m23;

		in = out;

		// rotation about y-axis
		c = Math.cos (rotateY * (Math.PI / 180));
		s = Math.sin (rotateY * (Math.PI / 180));
		out.m00 = c * (t = in.m00) - s * in.m02;
		out.m02 = s * t + c * in.m02;
		out.m10 = c * (t = in.m10) - s * in.m12;
		out.m12 = s * t + c * in.m12;
		out.m20 = c * (t = in.m20) - s * in.m22;
		out.m22 = s * t + c * in.m22;

		// rotation about z-axis
		c = Math.cos (rotateZ * (Math.PI / 180));
		s = Math.sin (rotateZ * (Math.PI / 180));
		out.m00 = c * (t = in.m00) + s * in.m01;
		out.m01 = c * in.m01 - s * t;
		out.m10 = c * (t = in.m10) + s * in.m11;
		out.m11 = c * in.m11 - s * t;
		out.m20 = c * (t = in.m20) + s * in.m21;
		out.m21 = c * in.m21 - s * t;
	}


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (rotateX$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (rotateY$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (rotateZ$FIELD, Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field rotateX$FIELD;
	public static final NType.Field rotateY$FIELD;
	public static final NType.Field rotateZ$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Rotate.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Rotate) o).rotateX = (float) value;
					return;
				case 1:
					((Rotate) o).rotateY = (float) value;
					return;
				case 2:
					((Rotate) o).rotateZ = (float) value;
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
					return ((Rotate) o).rotateX;
				case 1:
					return ((Rotate) o).rotateY;
				case 2:
					return ((Rotate) o).rotateZ;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Rotate ());
		$TYPE.addManagedField (rotateX$FIELD = new _Field ("rotateX", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (rotateY$FIELD = new _Field ("rotateY", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (rotateZ$FIELD = new _Field ("rotateZ", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		initType ();
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
		return new Rotate ();
	}

//enh:end

}
