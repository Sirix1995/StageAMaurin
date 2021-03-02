
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

package de.grogra.reflect;

public class TypeDecorator<T> extends MemberDecorator implements Type<T>
{
	public static <T> Type<T> undecorate (Type<T> t)
	{
		while (t instanceof TypeDecorator)
		{
			t = ((TypeDecorator<T>) t).getDecoratedType ();
		}
		return t;
	}


	public TypeDecorator (Type<T> type)
	{
		super (type);
	}


	private Type<T> getDecoratedType ()
	{
		return (Type<T>) getDecoratedMember ();
	}

	
	public String getPackage ()
	{
		return getDecoratedType ().getPackage ();
	}


	public String getBinaryName ()
	{
		return getDecoratedType ().getBinaryName ();
	}


	public int getTypeId ()
	{
		return getDecoratedType ().getTypeId ();
	}


	public boolean isInstance (Object object)
	{
		return getDecoratedType ().isInstance (object);
	}


	public Class<T> getImplementationClass ()
	{
		return getDecoratedType ().getImplementationClass ();
	}


	public TypeLoader getTypeLoader ()
	{
		return getDecoratedType ().getTypeLoader ();
	}


	public Type<? super T> getSupertype ()
	{
		return getDecoratedType ().getSupertype ();
	}


	public int getDeclaredInterfaceCount ()
	{
		return getDecoratedType ().getDeclaredInterfaceCount ();
	}


	public Type<?> getDeclaredInterface (int index)
	{
		return getDecoratedType ().getDeclaredInterface (index);
	}


	public int getDeclaredFieldCount ()
	{
		return getDecoratedType ().getDeclaredFieldCount ();
	}


	public Field getDeclaredField (int index)
	{
		return getDecoratedType ().getDeclaredField (index);
	}


	public int getDeclaredTypeCount ()
	{
		return getDecoratedType ().getDeclaredTypeCount ();
	}


	public Type<?> getDeclaredType (int index)
	{
		return getDecoratedType ().getDeclaredType (index);
	}


	public int getDeclaredMethodCount ()
	{
		return getDecoratedType ().getDeclaredMethodCount ();
	}


	public Method getDeclaredMethod (int index)
	{
		return getDecoratedType ().getDeclaredMethod (index);
	}
	

	public Type<?> getComponentType ()
	{
		return getDecoratedType ().getComponentType ();
	}


	public Type<?> getArrayType ()
	{
		return getDecoratedType ().getArrayType ();
	}


	public Object createArray (int length)
	{
		return getDecoratedType ().createArray (length);
	}


	@Override
	public String toString ()
	{
		return getDecoratedType ().toString ();
	}


	public T cloneObject (T o, boolean deep)
		throws CloneNotSupportedException
	{
		return getDecoratedType ().cloneObject (o, deep);
	}


	public boolean isStringSerializable ()
	{
		return getDecoratedType ().isStringSerializable();
	}


	public T valueOf (String s)
	{
		return getDecoratedType ().valueOf (s);
	}


	public T newInstance () throws java.lang.reflect.InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		return getDecoratedType ().newInstance ();
	}


	public Lookup getLookup ()
	{
		return getDecoratedType ().getLookup ();
	}


	public Object getDefaultElementValue (String name)
	{
		return getDecoratedType ().getDefaultElementValue (name);
	}

}
