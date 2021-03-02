
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

package de.grogra.imp2d.objects;

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.math.*;

public class Null extends Node implements Transformation
{
	public static final int TRANSFORMING_MASK = 1 << Node.USED_BITS;
	public static final int USED_BITS = Node.USED_BITS + 1;


	Transform2D transform;
	//enh:field attr=Attributes.TRANSFORM getter

	// boolean transforming
	//enh:field type=bits(TRANSFORMING_MASK) attr=Attributes.TRANSFORMING getter setter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.addDependency (transform$FIELD.getAttribute (), Attributes.TRANSFORMATION);
		$TYPE.addDependency (transforming$FIELD.getAttribute (), Attributes.TRANSFORMATION);
		$TYPE.declareAlias ("x", transform$FIELD.concat (Tuple2dType.x$FIELD));
		$TYPE.declareAlias ("y", transform$FIELD.concat (Tuple2dType.y$FIELD));
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field transform$FIELD;
	public static final NType.Field transforming$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Null.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Null) o).transform = (Transform2D) value;
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
					return ((Null) o).getTransform ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Null ());
		$TYPE.addManagedField (transform$FIELD = new _Field ("transform", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Transform2D.class), null, 0));
		$TYPE.addManagedField (transforming$FIELD = new NType.BitField ($TYPE, "transforming", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, TRANSFORMING_MASK));
		$TYPE.declareFieldAttribute (transform$FIELD, Attributes.TRANSFORM);
		$TYPE.declareFieldAttribute (transforming$FIELD, Attributes.TRANSFORMING);
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
		return new Null ();
	}

	public Transform2D getTransform ()
	{
		return transform;
	}

	public boolean isTransforming ()
	{
		return (bits & TRANSFORMING_MASK) != 0;
	}

	public void setTransforming (boolean v)
	{
		if (v) bits |= TRANSFORMING_MASK; else bits &= ~TRANSFORMING_MASK;
	}

//enh:end


	public Null (Transform2D transform)
	{
		super ();
		this.transform = transform;
		setTransforming (true);
	}


	public Null ()
	{
		this (null);
	}


	public Null (double x, double y)
	{
		this (new TVector2d (x, y));
	}


	public Null setTransform (Transform2D t)
	{
		transform = t;
		return this;
	}


	public Null setTransform (TVector2d t)
	{
		transform = t;
		return this;
	}


	public Null setTransform (double x, double y)
	{
		transform = new TVector2d (x, y);
		return this;
	}


	public Null setTransform (Tuple2d t)
	{
		transform = new TVector2d (t);
		return this;
	}


	public Null setTransform (Tuple2f t)
	{
		transform = new TVector2d (t.x, t.y);
		return this;
	}


	public void preTransform (Object object, boolean asNode, Matrix3d in, Matrix3d out, GraphState gs)
	{
		Transform2D t;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = transform;
			}
			else
			{
				t = (Transform2D) gs.checkObject (this, true, Attributes.TRANSFORM, transform);
			}
		}
		else
		{
			t = (Transform2D) gs.getObject (object, asNode, null, Attributes.TRANSFORM);
		}
		if (t != null)
		{
			t.transform (in, out);
		}
		else
		{
			out.set (in);
		}
	}


	public void postTransform (Object object, boolean asNode, Matrix3d in, Matrix3d out, Matrix3d pre,
							   GraphState gs)
	{
		boolean t;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = (bits & TRANSFORMING_MASK) != 0;
			}
			else
			{
				t = gs.checkBoolean (this, true, Attributes.TRANSFORMING,
									 (bits & TRANSFORMING_MASK) != 0);
			}
		}
		else
		{
			t = gs.getBoolean (object, asNode, Attributes.TRANSFORMING);
		}
		if (t)
		{
			out.set (in);
		}
		else if (out != pre)
		{
			out.set (pre);
		}
	}

}
