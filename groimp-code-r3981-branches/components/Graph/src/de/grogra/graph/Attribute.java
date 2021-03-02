
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

import java.util.HashMap;

import de.grogra.reflect.FieldChain;
import de.grogra.reflect.Type;
import de.grogra.util.Described;
import de.grogra.util.I18NBundle;
import de.grogra.util.KeyDescription;
import de.grogra.util.Quantity;
import de.grogra.util.Utils;

/**
 * An <code>Attribute</code> represents an attribute of nodes and edges
 * in a {@link de.grogra.graph.Graph}. Attribute values are read and
 * written on objects within the context of a
 * {@link de.grogra.graph.GraphState}, the corresponding
 * <code>get</code>- and <code>set</code>-methods are declared in
 * this class, its type-dependent subclasses
 * ({@link de.grogra.graph.IntAttribute},
 * {@link de.grogra.graph.ObjectAttribute}, ...), and in
 * {@link de.grogra.graph.GraphState}.
 * <p>
 * Attributes may be <em>derived</em> (see {@link #isDerived()}),
 * which means that their values
 * are not directly stored in objects, but are computed on the basis
 * of the state of the object, and possibly of neighbouring objects, too.
 * Values of derived attributes cannot be written.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Attribute<T> implements KeyDescription, Comparable
{
	/**
	 * An attribute array of length 0.
	 */
	public static final Attribute[] ATTRIBUTE_0 = new Attribute[0];

	private static final HashMap NAME_TO_ATTR = new HashMap ();

	private static int nextId = 0, lastDerivedId = 0;

	private String name, simpleName, resourceKey;
	private I18NBundle bundle;

	private Attribute[] array;

	protected final Type attrType;
	protected final Quantity quantity;
	final int id;

	
	/**
	 * Returns the attribute with the specified <code>name</code>.
	 * If an attribute with that name has not yet been registered
	 * by {@link #initializeName(String)}, <code>null</code> is
	 * returned. 
	 * 
	 * @param name name of an attribute
	 * @return corresponding attribute or <code>null</code>
	 */
	public static final Attribute forName (String name)
	{
		synchronized (NAME_TO_ATTR)
		{
			return (Attribute) NAME_TO_ATTR.get (name);
		}
	}


	Attribute (Type attrType, Quantity quantity)
	{
		this.attrType = attrType;
		this.quantity = quantity;
		synchronized (NAME_TO_ATTR)
		{
			id = isDerived () ? --lastDerivedId : nextId++;
		}
	}


	public final Attribute initializeName (String name)
	{
		if (this.name != null)
		{
			throw new IllegalStateException ();
		}
		this.name = name;
		int i = name.lastIndexOf ('.');
		this.simpleName = (i < 0) ? name : name.substring (i + 1);
		synchronized (NAME_TO_ATTR)
		{
			NAME_TO_ATTR.put (name, this);
		}
		return this;
	}


	public final Attribute initializeI18N (I18NBundle bundle,
										   String resourceKey)
	{
		if (this.bundle != null)
		{
			throw new IllegalStateException ();
		}
		this.bundle = bundle;
		this.resourceKey = resourceKey;
		return this;
	}


	/**
	 * Returns an array of length one containing this attribute.
	 * The component of the returned array must not be modified.
	 * 
	 * @return an array with this attribute as single component
	 */
	public final synchronized Attribute[] toArray ()
	{
		if (array == null)
		{
			array = new Attribute[] {this};
		}
		assert array[0] == this;
		return array;
	}


	/**
	 * Returns the type of values of this attribute.
	 * 
	 * @return the type of values
	 */
	public final Type getType ()
	{
		return attrType;
	}


	/**
	 * Returns the unique, fully-qualified name of this attribute.
	 * 
	 * @return fully-qualified name
	 */
	public final String getKey ()
	{
		return name;
	}


	/**
	 * Returns the simple name of this attribute. Simple names are not
	 * necessarily unique.
	 * 
	 * @return this attribute's simple name
	 */
	public final String getSimpleName ()
	{
		return simpleName;
	}


	/**
	 * Returns the physical quantity of this attribute.
	 * 
	 * @return quantity of attribute
	 */
	public final Quantity getQuantity ()
	{
		return quantity;
	}


	public Object getDescription (String type)
	{
		return Utils.get (bundle, resourceKey, type,
						  Utils.isStringDescription (type) ? name : null);
	}


	@Override
	public String toString ()
	{
		return (String) getDescription (Described.NAME);
	}


	/**
	 * Returns the value of this attribute for the given object in
	 * the given graph state. If necessary, the value is wrapped in one
	 * of the standard wrapper classes. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param gs the graph state context
	 * @return the value of this attribute for the given object
	 */
	public abstract T get (Object object, boolean asNode, GraphState gs);


	/**
	 * Sets the value of this attribute for the given <code>object</code>
	 * to the given <code>value</code> 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param value the new value of the attribute for the object
	 * @param gs the graph state context
	 */
	public abstract T set (Object object, boolean asNode, Object value, GraphState gs);


	/**
	 * Finds an attribute accessor corresponding to a <code>name</code>
	 * in a list of <code>accessors</code>. If the attribute of one of the
	 * <code>accessors</code> has a name or a simple name which equals
	 * the given <code>name</code>, then this accessor is returned, otherwise
	 * <code>null</code>. <code>null</code>-values are allowed within
	 * <code>accessors</code>.
	 * 
	 * @param accessors the array of accessors
	 * @param name the name to search
	 * @return an accessor of the list, corresponding to <code>name</code>,
	 * or <code>null</code>
	 */
	public static AttributeAccessor find (AttributeAccessor[] accessors,
										  String name)
	{
		for (int i = accessors.length - 1; i >= 0; i--)
		{
			if ((accessors[i] != null)
				&& (accessors[i].getAttribute ().name.equals (name)
					|| accessors[i].getAttribute ().simpleName.equals (name)))
			{
				return accessors[i];
			}
		}
		return null;
	}


	/**
	 * Determines whether this is a derived attribute or not.
	 * 
	 * @return <code>true</code> iff this is a derived attribute
	 */
	public boolean isDerived ()
	{
		return false;
	}


	/**
	 * Checks whether a value for this attribute can be written to the
	 * given object.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param gs the graph state context
	 * @return <code>true</code> iff the attribute is writable for the given object
	 */
	public boolean isWritable (Object object, boolean asNode, GraphState gs)
	{
		if (isDerived ())
		{
			return false;
		}
		AttributeAccessor a;
		return ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
			&& a.isWritable (object, gs);
	}


	/**
	 * Returns a unique id for this attribute. The ids of derived attributes
	 * are negative, the ids of normal attributes are non-negative.
	 * 
	 * @return a unique id
	 */
	public final int getId ()
	{
		return id;
	}


	@Override
	public final int hashCode ()
	{
		return id;
	}


	public final int compareTo (Object o)
	{
		return id - ((Attribute) o).id;
	}


	/**
	 * Checks whether this attribute is contained in the sorted array
	 * <code>b</code>. The array has to be sorted in ascending order
	 * according to the order induced by {@link #compareTo(Object)}.
	 * 
	 * @param b a sorted array of attributes
	 * @return <code>true</code> iff this attribute is contained in <code>b</code>
	 */
	public final boolean isContained (Attribute[] b)
	{
		return (b != null) && (java.util.Arrays.binarySearch (b, this) >= 0);
	}


	protected final void setAttributeState (GraphState gs, Object info)
	{
		synchronized (gs.attributeMap)
		{
			gs.attributeMap.put (id, info);
		}
	}


	protected final Object getAttributeState (GraphState gs)
	{
		synchronized (gs.attributeMap)
		{
			return gs.attributeMap.get (id, null);
		}
	}

}
