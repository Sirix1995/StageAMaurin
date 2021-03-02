
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


package de.grogra.graph;

import de.grogra.reflect.*;
import de.grogra.util.*;

public class FloatAttribute extends Attribute<Float>
{


	public FloatAttribute (Type type, Quantity quantity)
	{
		super (type, quantity);
	}


	public FloatAttribute (Quantity quantity)
	{
		this (Type.FLOAT, quantity);
	}



	@Override
	public final Float get (Object o, boolean asNode, GraphState gs)
	{
		return Float.valueOf (gs.getFloat (o, asNode, this));
	}


	@Override
	public final Float set (Object o, boolean asNode, Object value, GraphState gs)
	{
		float v = setFloat (o, asNode, (Float) (((Number) (value)).floatValue ()), gs);
		return Float.valueOf (v);
	}




	protected float getDerived (Object object, boolean asNode,
								GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public float setFloat (Object object, boolean asNode, float value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((FloatAttributeAccessor) a).setFloat (object, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected float setDerived (Object object, boolean asNode, float value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}



	public float getMinValue ()
	{
		return -Float.MAX_VALUE;
	}


	public float getMaxValue ()
	{
		return Float.MAX_VALUE;
	}

}

