
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

public class Vector2fAttribute extends Tuple2fAttribute
{
	public static final Type TYPE
		= de.grogra.math.Tuple2fType.VECTOR;


	public Vector2fAttribute (Quantity quantity)
	{
		super (TYPE, quantity);
	}


	public Vector2fAttribute ()
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
		else if (		(((t) == (Vector2fAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Vector2fAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Vector2fAttribute.TYPE))
	)
		{
			return new Vector2f ((Tuple2f) value);
		}
		else if (		(((t) == (Vector2dAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Vector2dAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Vector2dAttribute.TYPE))
	)
		{
			return new Vector2d ((Tuple2f) value);
		}
		else if (		(((t) == (Point2fAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Point2fAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Point2fAttribute.TYPE))
	)
		{
			return new Point2f ((Tuple2f) value);
		}
		else if (		(((t) == (Point2dAttribute.TYPE))
		 || ((t).getImplementationClass ()
			 == (Point2dAttribute.TYPE).getImplementationClass ())
		 || de.grogra.reflect.Reflection.equal (t, Point2dAttribute.TYPE))
	)
		{
			return new Point2d ((Tuple2f) value);
		}
		else
		{
			return super.toType (value, t);
		}
	}


	@Override
	public Object valueOf (Object value)
	{
		if (value instanceof Vector2f)
		{
			return value;
		}
		else if (value instanceof Tuple2f)
		{
			return new Vector2f ((Tuple2f) value);
		}
		else if (value instanceof Tuple2d)
		{
			return new Vector2f ((Tuple2d) value);
		}
		else
		{
			return super.valueOf (value);
		}
	}


	public Object cloneValue (Object value)
	{
		return new Vector2f ((Tuple2f) value);
	}

}

