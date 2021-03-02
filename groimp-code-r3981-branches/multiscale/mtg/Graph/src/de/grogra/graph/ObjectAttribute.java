
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

public class ObjectAttribute<T> extends Attribute<T>
{


	public class IdentityAccessor implements ObjectAttributeAccessor<T>
	{

		public T getObject (Object object, GraphState gs)
		{
			return (T) object;
		}


		public T getObject (Object object, T placeIn,
								 GraphState gs)
		{
			return (T) object;
		}


		public T setObject (Object object, T value,
								 GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}


		public Object setSubfield (Object object, FieldChain field,
								   int[] indices, Object value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}


		public Type getType ()
		{
			return ObjectAttribute.this.getType ();
		}


		public Attribute<T> getAttribute ()
		{
			return ObjectAttribute.this;
		}


		public Field getField ()
		{
			return null;
		}


		public boolean isWritable (Object object, GraphState gs)
		{
			return false;
		}
	}


	private final boolean convert;

	public ObjectAttribute (Type type, boolean convert, Quantity quantity)
	{
		super (type, quantity);
		this.convert = convert;
	}


	public ObjectAttribute (Class<T> type, boolean convert, Quantity quantity)
	{
		this (ClassAdapter.wrap (type), convert, quantity);
	}


	public T valueOf (Object v)
	{
		return (T) (convert ? Reflection.toType (v, attrType) : v);
	}


	public Object toType (T v, Type t)
	{
		return convert ? Reflection.toType (v, t) : v;
	}


	protected T getDerived (Object object, boolean asNode, T placeIn,
								 GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public Object setSubfield (Object object, boolean asNode, FieldChain field,
							   int[] indices, Object value, GraphState gs)
	{
		if (isDerived ())
		{
			assert field.length () == 0;
			return setDerived (object, asNode, (T) value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((ObjectAttributeAccessor) a)
				.setSubfield (object, field, indices, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}




	@Override
	public final T get (Object o, boolean asNode, GraphState gs)
	{
		return (gs.getObject (o, asNode, this));
	}


	@Override
	public final T set (Object o, boolean asNode, Object value, GraphState gs)
	{
		T v = setObject (o, asNode, (T) (value), gs);
		return (v);
	}




	public T setObject (Object object, boolean asNode, T value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((ObjectAttributeAccessor<T>) a).setObject (object, (T) toType (value, a.getType ()), gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected T setDerived (Object object, boolean asNode, T value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}




}

