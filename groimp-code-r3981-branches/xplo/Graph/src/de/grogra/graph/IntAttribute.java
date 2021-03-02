
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

public class IntAttribute extends Attribute<Integer>
{


	public IntAttribute (Type type)
	{
		super (type, null);
	}


	public IntAttribute ()
	{
		this (Type.INT);
	}



	@Override
	public final Integer get (Object o, boolean asNode, GraphState gs)
	{
		return Integer.valueOf (gs.getInt (o, asNode, this));
	}


	@Override
	public final Integer set (Object o, boolean asNode, Object value, GraphState gs)
	{
		int v = setInt (o, asNode, (Integer) (((Number) (value)).intValue ()), gs);
		return Integer.valueOf (v);
	}




	protected int getDerived (Object object, boolean asNode,
								GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public int setInt (Object object, boolean asNode, int value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((IntAttributeAccessor) a).setInt (object, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected int setDerived (Object object, boolean asNode, int value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}



	public int getMinValue ()
	{
		return Integer.MIN_VALUE;
	}


	public int getMaxValue ()
	{
		return Integer.MAX_VALUE;
	}

}

