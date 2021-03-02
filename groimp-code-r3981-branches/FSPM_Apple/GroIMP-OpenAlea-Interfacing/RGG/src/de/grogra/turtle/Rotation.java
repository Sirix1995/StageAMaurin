
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
import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.imp3d.objects.*;

/**
 * This class is the base class for rotations about one of the
 * coordinate axes. The rotation {@link #angle} is specified
 * in degrees.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Rotation extends Node implements Transformation
{
	/**
	 * The rotation angle in degrees. 
	 */
	public float angle = 0f;
	//enh:field setmethod=setAngle getter

	private transient double sin = 0d;
	private transient double cos = 1d;


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.addDependency (Attributes.ANGLE, Attributes.TRANSFORMATION);
		$TYPE.addAccessor (new FieldAttributeAccessor (Attributes.ANGLE, angle$FIELD, Math.PI / 180));
		$TYPE.setAttribute (angle$FIELD, Attributes.ANGLE);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field angle$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Rotation.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Rotation) o).setAngle ((float) value);
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
					return ((Rotation) o).getAngle ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (Rotation.class);
		$TYPE.addManagedField (angle$FIELD = new _Field ("angle", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		initType ();
		$TYPE.validate ();
	}

	public float getAngle ()
	{
		return angle;
	}

//enh:end


	public Rotation ()
	{
		super ();
	}


	public Rotation (float angle)
	{
		this ();
		setAngle (angle);
	}


	public void setAngle (float angle)
	{
		this.angle = angle;
		sin = Math.sin (angle * (Math.PI / 180));
		cos = Math.cos (angle * (Math.PI / 180));
	}


	public void preTransform (Object node, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object node, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		double a;
		if (node == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				transform (cos, sin, in, out);
				return;
			}
			else
			{
				a = gs.checkDouble (this, asNode, Attributes.ANGLE, angle * (Math.PI / 180));
			}
		}
		else
		{
			a = gs.getDouble (node, asNode, Attributes.ANGLE);
		}
		transform (Math.cos (a), Math.sin (a), in, out);
	}


	protected abstract void transform (double cos, double sin,
									   Matrix4d in, Matrix4d out);

}
