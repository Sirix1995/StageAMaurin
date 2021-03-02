
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

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.math.*;

public class Null extends Node implements Transformation
{
	public static final int TRANSFORMING_MASK = 1 << Node.USED_BITS;

	public static final int USED_BITS = Node.USED_BITS + 1;


	protected Transform3D transform;
	//enh:field attr=Attributes.TRANSFORM getter setter

	// boolean transforming
	//enh:field type=bits(TRANSFORMING_MASK) attr=Attributes.TRANSFORMING getter setter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.addDependency (transform$FIELD.getAttribute (), Attributes.TRANSFORMATION);
		$TYPE.addDependency (transforming$FIELD.getAttribute (), Attributes.TRANSFORMATION);
		$TYPE.declareAlias ("scale", transform$FIELD.concat (UniformScale.scale$FIELD));
		$TYPE.declareAlias ("x", transform$FIELD.concat (Tuple3dType.x$FIELD));
		$TYPE.declareAlias ("y", transform$FIELD.concat (Tuple3dType.y$FIELD));
		$TYPE.declareAlias ("z", transform$FIELD.concat (Tuple3dType.z$FIELD));
		$TYPE.declareAlias ("position", transform$FIELD.cast (TVector3d.$TYPE));
	}

	public Null (Transform3D transform)
	{
		super ();
		this.transform = transform;
		setTransforming (true);
	}


	public Null ()
	{
		this (null);
	}


	public Null (double x, double y, double z)
	{
		this (new TVector3d (x, y, z));
	}


	public void setTransform (TVector3d t)
	{
		transform = t;
	}


	public void setTransform (TMatrix4d t)
	{
		transform = t;
	}


	/**
	 * This method sets the local transformation to a
	 * {@link TVector3d}, i.e., a translation.
	 * 
	 * @param x the x-coordinate of the translation vector
	 * @param y the y-coordinate of the translation vector
	 * @param z the z-coordinate of the translation vector
	 *
	 */
	public void setTransform (double x, double y, double z)
	{
		transform = new TVector3d (x, y, z);
	}


	/**
	 * This method sets the local transformation to a
	 * {@link TVector3d}, i.e., a translation.
	 * 
	 * @param x the x-coordinate of the translation vector
	 * @param y the y-coordinate of the translation vector
	 * @param z the z-coordinate of the translation vector
	 */
	public void setTranslation (double x, double y, double z)
	{
		transform = new TVector3d (x, y, z);
	}


	/**
	 * This method sets the local transformation to a
	 * rotation about the coordinate axes
	 * 
	 * @param x the x-coordinate of the translation vector
	 * @param y the y-coordinate of the translation vector
	 * @param z the z-coordinate of the translation vector
	 */
	public void setRotation (double x, double y, double z)
	{
		TMatrix4d t = new TMatrix4d ();
		t.rotZ (z);
		Matrix4d m = new Matrix4d ();
		m.rotY (y);
		t.mul (m);
		m.rotX (x);
		t.mul (m);
		transform = t;
	}

	public void setTransform (Tuple3d t)
	{
		transform = new TVector3d (t);
	}


	public void setTransform (Tuple3f t)
	{
		transform = new TVector3d (t.x, t.y, t.z);
	}


	public void setTransform (Matrix3d t)
	{
		transform = new TMatrix4d (t);
	}


	public void setTransform (Matrix4d t)
	{
		transform = new TMatrix4d (t);
	}


	public void setScale (float scale)
	{
		transform = new UniformScale (scale);
	}


	public Vector3d getTranslation ()
	{
		if (transform == null)
		{
			return new Vector3d ();
		}
		Matrix4d t = new Matrix4d ();
		t.setIdentity ();
		transform.transform (t, t);
		return new Vector3d (t.m03, t.m13, t.m23);
	}


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		Transform3D t;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = transform;
			}
			else
			{
				t = (Transform3D) gs.checkObject (this, true, Attributes.TRANSFORM, transform);
			}
		}
		else
		{
			t = (Transform3D) gs.getObject (object, asNode, null, Attributes.TRANSFORM);
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


	public void postTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
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
	
	/**
	 * Returns a new transformation matrix which contains the
	 * local transformations of the node.
	 * @return
	 */
	public Matrix4d getLocalTransformation()
	{
		Matrix4d in = new Matrix4d();
		in.setIdentity();
		Matrix4d out = new Matrix4d();
		if (transform != null) {
			transform.transform(in, out);
			return out;
		}
		return in;
	}


//	enh:insert initType ();
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
					((Null) o).transform = (Transform3D) value;
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
		$TYPE.addManagedField (transform$FIELD = new _Field ("transform", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Transform3D.class), null, 0));
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

	public Transform3D getTransform ()
	{
		return transform;
	}

	public void setTransform (Transform3D value)
	{
		transform$FIELD.setObject (this, value);
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


}
