
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
 * A <code>Scale</code> node scales the local coordinate system
 * using the scaling factors {@link #scaleX},
 * {@link #scaleY} and {@link #scaleZ}. In general, the
 * scaling factors may differ. However, there is the
 * constructor {@link #Scale(float)} which initializes
 * the three factors with the same value such that
 * a uniform scaling is performed.
 * <br>
 * This class declares two predicates:
 * {@link Scale.Pattern} allows one to write
 * <code>Scale(x,y,z)</code> in a pattern of an XL query.
 * {@link Scale.XPattern} is useful for uniform
 * <code>Scale</code> nodes since it allows one to write
 * <code>Scale(x)</code> in a pattern. It uses
 * {@link #scaleX} to obtain the value for the
 * predicate argument.
 * 
 * @author Ole Kniemeyer
 */
public class Scale extends Node implements Transformation
{
	/**
	 * Scaling factor in x-direction.
	 */
	public float scaleX;
	//enh:field

	/**
	 * Scaling factor in y-direction.
	 */
	public float scaleY;
	//enh:field

	/**
	 * Scaling factor in z-direction.
	 */
	public float scaleZ;
	//enh:field

	public static class XPattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public XPattern ()
		{
			super (Scale.$TYPE, scaleX$FIELD);
		}

		public static void signature (@In @Out Scale n, float x)
		{
		}
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Scale.$TYPE, new NType.Field[] {scaleX$FIELD, scaleY$FIELD, scaleZ$FIELD});
		}

		public static void signature (@In @Out Scale n, float x, float y, float z)
		{
		}
	}


	/**
	 * Creates a new <code>Scale</code> node whose scaling
	 * factors are set to one.
	 */
	public Scale ()
	{
		this (1, 1, 1);
	}


	/**
	 * Creates a new <code>Scale</code> node whose scaling
	 * factors are set to <code>scale</code>. This is a
	 * uniform scaling.
	 * 
	 * @param scale scaling factor
	 */
	public Scale (float scale)
	{
		this (scale, scale, scale);
	}


	/**
	 * Creates a new <code>Scale</code> node whose scaling
	 * factors are set to the specified values.
	 * 
	 * @param scaleX scaling factor in x-direction
	 * @param scaleY scaling factor in y-direction
	 * @param scaleZ scaling factor in z-direction
	 */
	public Scale (float scaleX, float scaleY, float scaleZ)
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		out.m00 = in.m00 * scaleX;
		out.m01 = in.m01 * scaleY;
		out.m02 = in.m02 * scaleZ;
		out.m03 = in.m03;

		out.m10 = in.m10 * scaleX;
		out.m11 = in.m11 * scaleY;
		out.m12 = in.m12 * scaleZ;
		out.m13 = in.m13;

		out.m20 = in.m20 * scaleX;
		out.m21 = in.m21 * scaleY;
		out.m22 = in.m22 * scaleZ;
		out.m23 = in.m23;

		out.m30 = in.m30;
		out.m31 = in.m31;
		out.m32 = in.m32;
		out.m33 = in.m33;
	}


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (scaleX$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (scaleY$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (scaleZ$FIELD, Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field scaleX$FIELD;
	public static final NType.Field scaleY$FIELD;
	public static final NType.Field scaleZ$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Scale.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Scale) o).scaleX = (float) value;
					return;
				case 1:
					((Scale) o).scaleY = (float) value;
					return;
				case 2:
					((Scale) o).scaleZ = (float) value;
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
					return ((Scale) o).scaleX;
				case 1:
					return ((Scale) o).scaleY;
				case 2:
					return ((Scale) o).scaleZ;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Scale ());
		$TYPE.addManagedField (scaleX$FIELD = new _Field ("scaleX", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (scaleY$FIELD = new _Field ("scaleY", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (scaleZ$FIELD = new _Field ("scaleZ", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
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
		return new Scale ();
	}

//enh:end

}
