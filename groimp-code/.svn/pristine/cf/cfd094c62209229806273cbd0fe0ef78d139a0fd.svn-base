
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
 * A <code>Translate</code> node translates the local coordinate system
 * using the translation distances {@link #translateX},
 * {@link #translateY} and {@link #translateZ}.
 * <br>
 * This class declares the predicate
 * {@link Translate.Pattern} to allow one to write
 * <code>Translate(x,y,z)</code> in a pattern of an XL query.
 * 
 * @author Ole Kniemeyer
 */
public class Translate extends Node implements Transformation
{
	/**
	 * Translation distance in x-direction.
	 */
	public float translateX;
	//enh:field

	/**
	 * Translation distance in y-direction.
	 */
	public float translateY;
	//enh:field

	/**
	 * Translation distance in z-direction.
	 */
	public float translateZ;
	//enh:field

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Translate.$TYPE, new NType.Field[] {translateX$FIELD, translateY$FIELD, translateZ$FIELD});
		}

		public static void signature (@In @Out Translate n, float x, float y, float z)
		{
		}
	}


	/**
	 * Creates a new <code>Translate</code> node whose
	 * translation vector is (0, 0, 0).
	 */
	public Translate ()
	{
	}


	/**
	 * Create a new <code>Translate</code> node which translates
	 * according to the specified values.
	 * 
	 * @param translateX translation distance in x-direction
	 * @param translateY translation distance in y-direction
	 * @param translateZ translation distance in z-direction
	 */
	public Translate (float translateX, float translateY, float translateZ)
	{
		this.translateX = translateX;
		this.translateY = translateY;
		this.translateZ = translateZ;
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);

		out.m03 += translateX * out.m00 + translateY * out.m01 + translateZ * out.m02;
		out.m13 += translateX * out.m10 + translateY * out.m11 + translateZ * out.m12;
		out.m23 += translateX * out.m20 + translateY * out.m21 + translateZ * out.m22;
	}


	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		out.set (in);
	}


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (translateX$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (translateY$FIELD, Attributes.TRANSFORMATION);
		$TYPE.setDependentAttribute (translateZ$FIELD, Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field translateX$FIELD;
	public static final NType.Field translateY$FIELD;
	public static final NType.Field translateZ$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Translate.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Translate) o).translateX = (float) value;
					return;
				case 1:
					((Translate) o).translateY = (float) value;
					return;
				case 2:
					((Translate) o).translateZ = (float) value;
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
					return ((Translate) o).translateX;
				case 1:
					return ((Translate) o).translateY;
				case 2:
					return ((Translate) o).translateZ;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Translate ());
		$TYPE.addManagedField (translateX$FIELD = new _Field ("translateX", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (translateY$FIELD = new _Field ("translateY", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (translateZ$FIELD = new _Field ("translateZ", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
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
		return new Translate ();
	}

//enh:end

}
