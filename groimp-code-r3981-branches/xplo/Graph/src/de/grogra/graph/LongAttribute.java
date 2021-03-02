
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

public class LongAttribute extends Attribute<Long>
{


	public LongAttribute (Type type)
	{
		super (type, null);
	}


	public LongAttribute ()
	{
		this (Type.LONG);
	}



	@Override
	public final Long get (Object o, boolean asNode, GraphState gs)
	{
		return Long.valueOf (gs.getLong (o, asNode, this));
	}


	@Override
	public final Long set (Object o, boolean asNode, Object value, GraphState gs)
	{
		long v = setLong (o, asNode, (Long) (((Number) (value)).longValue ()), gs);
		return Long.valueOf (v);
	}




	protected long getDerived (Object object, boolean asNode,
								GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public long setLong (Object object, boolean asNode, long value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((LongAttributeAccessor) a).setLong (object, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected long setDerived (Object object, boolean asNode, long value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}



	public long getMinValue ()
	{
		return Long.MIN_VALUE;
	}


	public long getMaxValue ()
	{
		return Long.MAX_VALUE;
	}

}

