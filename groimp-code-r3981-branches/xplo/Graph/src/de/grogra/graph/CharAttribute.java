
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

public class CharAttribute extends Attribute<Character>
{


	public CharAttribute (Type type)
	{
		super (type, null);
	}


	public CharAttribute ()
	{
		this (Type.CHAR);
	}



	@Override
	public final Character get (Object o, boolean asNode, GraphState gs)
	{
		return Character.valueOf (gs.getChar (o, asNode, this));
	}


	@Override
	public final Character set (Object o, boolean asNode, Object value, GraphState gs)
	{
		char v = setChar (o, asNode, (Character) (((Character) (value)).charValue ()), gs);
		return Character.valueOf (v);
	}




	protected char getDerived (Object object, boolean asNode,
								GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public char setChar (Object object, boolean asNode, char value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((CharAttributeAccessor) a).setChar (object, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected char setDerived (Object object, boolean asNode, char value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}




}

