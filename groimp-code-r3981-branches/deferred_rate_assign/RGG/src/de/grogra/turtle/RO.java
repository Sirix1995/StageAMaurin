
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

import javax.vecmath.*;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.math.*;
import de.grogra.vecmath.*;
import de.grogra.graph.impl.*;
import de.grogra.imp3d.objects.*;
import de.grogra.rgg.Library;

/**
 * An <code>RO(x,s)</code> node represents a rotation which
 * implements
 * an orthogonal tropism which moves away from a given direction
 * <code>x</code>.
 * The {@link #strength} of the tropism is given by <code>s</code>.
 * <br>
 * This class declares the predicate
 * {@link RO.Pattern} to allow one to write
 * <code>RO(x,s)</code> in a pattern of an XL query.
 *
 * @author Ole Kniemeyer
 *
 * @see de.grogra.rgg.Library#orthogonalTropism(Matrix34d, Tuple3d, float, Matrix4d)
 */
public class RO extends Tropism
{


	/**
	 * This defines the normal vector of the plane to which this tropism tends.
	 */
	public Vector3d direction;
	//enh:field type=Tuple3dType.VECTOR
	

	private static void initType ()
	{
		$TYPE.setDependentAttribute (direction$FIELD, Attributes.TRANSFORMATION);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, RO.$TYPE, new NType.Field[] {direction$FIELD, strength$FIELD});
		}

		public static void signature (@In @Out RO n, Vector3d d, float s)
		{
		}
	}


	public RO ()
	{
		this (null, 0.01f);
	}


	public RO (Vector3d direction, float strength)
	{
		super ();
		this.direction = direction;
		this.strength = strength;
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		Matrix34d r = GlobalTransformation.getParentValue (object, asNode, gs, true);
		if (Library.orthogonalTropism (r, direction, strength, out))
		{
			Math2.mulAffine (out, in, out);
		}
		else if (out != pre)
		{
			out.set (pre);
		}
	}


	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field direction$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (RO.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((RO) o).direction = (Vector3d) Tuple3dType.VECTOR.setObject (((RO) o).direction, value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((RO) o).direction;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new RO ());
		$TYPE.addManagedField (direction$FIELD = new _Field ("direction", _Field.PUBLIC  | _Field.SCO, Tuple3dType.VECTOR, null, 0));
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
		return new RO ();
	}

//enh:end

}
