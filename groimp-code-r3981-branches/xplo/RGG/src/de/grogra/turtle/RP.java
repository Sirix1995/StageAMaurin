
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
 * An <code>RP(x,s)</code> node represents a rotation which
 * implements
 * a positional tropism towards the point
 * <code>x</code>.
 * The {@link #strength} of the tropism is given by <code>s</code>.
 * <br>
 * This class declares the predicate
 * {@link RP.Pattern} to allow one to write
 * <code>RP(x,s)</code> in a pattern of an XL query.
 *
 * @author Ole Kniemeyer
 *
 * @see de.grogra.rgg.Library#positionalTropism(Matrix34d, Tuple3d, float, Matrix4d)
 */
public class RP extends Tropism
{


	/**
	 * This defines the position to which this tropism tends.
	 */
	public Point3d target;
	//enh:field type=Tuple3dType.POINT
	

	private static void initType ()
	{
		$TYPE.setDependentAttribute (target$FIELD, Attributes.TRANSFORMATION);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, RP.$TYPE, new NType.Field[] {target$FIELD, strength$FIELD});
		}

		public static void signature (@In @Out RP n, Point3d d, float s)
		{
		}
	}


	public RP ()
	{
		this (null, 0.01f);
	}


	public RP (Point3d target, float strength)
	{
		super ();
		this.target = target;
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
		if (Library.positionalTropism (r, target, strength, out))
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

	public static final NType.Field target$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (RP.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((RP) o).target = (Point3d) Tuple3dType.POINT.setObject (((RP) o).target, value);
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
					return ((RP) o).target;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new RP ());
		$TYPE.addManagedField (target$FIELD = new _Field ("target", _Field.PUBLIC  | _Field.SCO, Tuple3dType.POINT, null, 0));
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
		return new RP ();
	}

//enh:end

}
