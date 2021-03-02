
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

package de.grogra.util;

import javax.swing.event.ListDataListener;

import de.grogra.reflect.Type;
import de.grogra.reflect.TypeImpl;
import de.grogra.xl.util.ObjectList;

/**
 * An <code>EnumerationType</code> is a type whose values are drawn
 * from an enumeration. The enumeration type is either integral
 * (consecutive <code>int</code> values beginning with 0) or a
 * reference type (a set of <code>Object</code> values).
 * 
 * @author Ole Kniemeyer
 */
public class EnumerationType extends TypeImpl
	implements javax.swing.ListModel
{
	/**
	 * An "abstract" base enumeration type which is used as supertype
	 * for those enumeration types which use <code>int</code>s
	 * to encode their values.
	 */
	public static final EnumerationType INT_ENUMERATION
		= new EnumerationType ("intenum", INT);

	/**
	 * An "abstract" base enumeration type which is used as supertype
	 * for those enumeration types which use <code>Object</code>s
	 * to encode their values.
	 */
	public static final EnumerationType OBJECT_ENUMERATION
		= new EnumerationType ("objectenum", OBJECT);

	/**
	 * Contains the values of this enumeration type. 
	 */
	protected ObjectList values;

	/**
	 * Contains the descriptions for the values of this enumeration type. 
	 */
	protected ObjectList descriptions;

	private final Type implType;


	private EnumerationType (String name, Type type)
	{
		super (name, type);
		this.implType = OBJECT;
	}


	/**
	 * Creates a new <code>EnumerationType</code> consisting of
	 * <code>values.length</code> values, their type being
	 * determined by <code>supertype/code>.
	 * 
	 * @param name a name for the type
	 * @param values the representation of the enumeration values
	 * @param superType the supertype
	 * @param implType the class of the internal values
	 */
	public EnumerationType (String name, Object[] values,
							EnumerationType superType, Type implType)
	{
		super (name, superType);
		this.descriptions = this.values = new ObjectList (values);
		this.implType = implType;
	}


	/**
	 * Creates a new <code>EnumerationType</code> consisting of
	 * <code>descriptions.length</code> values of type <code>int</code> beginning
	 * with 0. A string representation of the values is obtained from the
	 * specified <code>descriptions</code>.
	 * 
	 * @param name a name for the type
	 * @param descriptions strings to represent the values
	 */
	public EnumerationType (String name, String[] descriptions)
	{
		super (name, INT_ENUMERATION);
		this.implType = INT;
		this.descriptions = new ObjectList (descriptions);
		initIntValues ();
	}


	/**
	 * Creates a new <code>EnumerationType</code> consisting of
	 * <code>keys.length</code> values of type <code>int</code> beginning
	 * with 0. The representation of the values is obtained from the
	 * specified resource <code>bundle</code> using the <code>suffixes</code>:
	 * The keys for the <code>bundle</code> are the concatenations
	 * <code>name + '.' + suffixes[i]</code>.
	 * 
	 * @param name a name for the type
	 * @param bundle the resource bundle to obtain representations for the values
	 * @param suffixes the suffixes to use for the resource bundle
	 */
	public EnumerationType (String name, I18NBundle bundle, String[] suffixes)
	{
		super (name, INT_ENUMERATION);
		this.implType = INT;
		this.descriptions = new ObjectList ();
		for (int i = 0; i < suffixes.length; i++)
		{
			descriptions.add (bundle.keyToDescribed (name + '.' + suffixes[i]));
		}
		initIntValues ();
	}


	public EnumerationType (String name, I18NBundle bundle, String[] suffixes, Object[] values, Type implType)
	{
		super (name, OBJECT_ENUMERATION);
		this.implType = implType;
		this.values = new ObjectList (values);
		this.descriptions = new ObjectList ();
		for (int i = 0; i < suffixes.length; i++)
		{
			descriptions.add (bundle.keyToDescribed (name + '.' + suffixes[i]));
		}
	}


	/**
	 * Creates a new <code>EnumerationType</code> consisting of
	 * <code>count</code> values of type <code>int</code> beginning with 0. The
	 * representation of the values is obtained from the specified
	 * resource <code>bundle</code>: The used key for value <code>i</code>
	 * is <code>name.i</code>.
	 *  
	 * @param name the base name for the keys and the name of the type
	 * @param bundle the resource bundle to obtain representations for the values
	 * @param count the number of values of this enumeration
	 */
	public EnumerationType (String name, I18NBundle bundle, int count)
	{
		super (name, INT_ENUMERATION);
		this.implType = INT;
		this.descriptions = new ObjectList ();
		for (int i = 0; i < count; i++)
		{
			descriptions.add (bundle.keyToDescribed (name + '.' + i));
		}
		initIntValues ();
	}


	private void initIntValues ()
	{
		values = new ObjectList ();
		for (int i = 0; i < descriptions.size (); i++)
		{
			values.add (Integer.valueOf (i));
		}
	}


	@Override
	public Class getImplementationClass ()
	{
		return implType.getImplementationClass ();
	}


	public Object getElementAt (int index)
	{
		return descriptions.get (index);
	}


	public int getSize ()
	{
		return descriptions.size ();
	}


	public Object getDescriptionFor (Object value)
	{
		int i = values.indexOf (value);
		return (i >= 0) ? descriptions.get (i) : null;
	}


	public Object getValueFor (Object description)
	{
		int i = descriptions.indexOf (description);
		return (i >= 0) ? values.get (i) : null;
	}


	public void addListDataListener (ListDataListener l)
	{
	}


	public void removeListDataListener (ListDataListener l)
	{
	}


	@Override
	public boolean isStringSerializable ()
	{
		return implType.isStringSerializable ();
	}

	@Override
	public Object valueOf (String s)
	{
		return implType.valueOf (s);
	}

}
