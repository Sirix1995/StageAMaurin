
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


package de.grogra.imp.objects;

import javax.vecmath.*;
import de.grogra.reflect.*;
import de.grogra.util.Quantity;

public class Point3fAttribute extends Tuple3fAttribute
{
	public static final Type TYPE
		= de.grogra.math.Tuple3fType.POINT;


	public Point3fAttribute (Quantity quantity)
	{
		super (TYPE, quantity);
	}


	public Point3fAttribute ()
	{
		super (TYPE, Quantity.LENGTH);
	}


	@Override
	public Object toType (Object value, Type t)
	{
		if (		(((t) == (TYPE))
		 || ((t).getImplementationClass ()
			 == (TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, TYPE))
	)
		{
			return value;
		}
		else if (		(((t) == (Vector3fAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Vector3fAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Vector3fAttribute.TYPE))
	)
		{
			return new Vector3f ((Tuple3f) value);
		}
		else if (		(((t) == (Vector3dAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Vector3dAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Vector3dAttribute.TYPE))
	)
		{
			return new Vector3d ((Tuple3f) value);
		}
		else if (		(((t) == (Point3fAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Point3fAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Point3fAttribute.TYPE))
	)
		{
			return new Point3f ((Tuple3f) value);
		}
		else if (		(((t) == (Point3dAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Point3dAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Point3dAttribute.TYPE))
	)
		{
			return new Point3d ((Tuple3f) value);
		}
		else
		{
			return super.toType (value, t);
		}
	}


	@Override
	public Object valueOf (Object value)
	{
		if (value instanceof Point3f)
		{
			return value;
		}
		else if (value instanceof Tuple3f)
		{
			return new Point3f ((Tuple3f) value);
		}
		else if (value instanceof Tuple3d)
		{
			return new Point3f ((Tuple3d) value);
		}
		else
		{
			return super.valueOf (value);
		}
	}


	public Object cloneValue (Object value)
	{
		return new Point3f ((Tuple3f) value);
	}

}
