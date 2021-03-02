
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

import javax.vecmath.Matrix3d;
import de.grogra.graph.*;
import de.grogra.graph.impl.*;

public class Rotation extends Node implements Transformation
{
	double angle = 0f;
	//enh:field attr=Attributes.ANGLE setmethod=setAngle getter

	private transient double sin = 0d;
	private transient double cos = 1d;


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.addDependency (Attributes.ANGLE, Attributes.TRANSFORMATION);
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
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((Rotation) o).setAngle ((double) value);
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Rotation) o).getAngle ();
			}
			return super.getDouble (o);
		}
	}

	static
	{
		$TYPE = new NType (new Rotation ());
		$TYPE.addManagedField (angle$FIELD = new _Field ("angle", 0 | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
		$TYPE.declareFieldAttribute (angle$FIELD, Attributes.ANGLE);
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
		return new Rotation ();
	}

	public double getAngle ()
	{
		return angle;
	}

//enh:end


	public Rotation ()
	{
		super ();
	}


	public Rotation (double angle)
	{
		this ();
		setAngle (angle);
	}


	public void setAngle (double angle)
	{
		this.angle = angle;
		sin = Math.sin (angle);
		cos = Math.cos (angle);
	}


	public void preTransform (Object object, boolean asNode, Matrix3d in, Matrix3d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object object, boolean asNode, Matrix3d in, Matrix3d out, Matrix3d pre,
							   GraphState gs)
	{
		double t, c, s;
	getCosSin:
		{
			if (object == this) 
			{
				if (gs.getInstancingPathIndex () <= 0)
				{
					c = cos;
					s = sin;
					break getCosSin;
				}
				else
				{
					t = gs.checkDouble (this, true, Attributes.ANGLE, angle);
					if (t == angle)
					{
						c = cos;
						s = sin;
						break getCosSin;
					}
				}
			}
			else
			{
				t = gs.getDouble (object, asNode, Attributes.ANGLE);
			}
			c = Math.cos (t);
			s = Math.sin (t);
		}
		out.m00 = c * (t = in.m00) + s * in.m01;
		out.m01 = c * in.m01 - s * t;
		out.m02 = in.m02;
		out.m10 = c * (t = in.m10) + s * in.m11;
		out.m11 = c * in.m11 - s * t;
		out.m12 = in.m12;
	}

}
