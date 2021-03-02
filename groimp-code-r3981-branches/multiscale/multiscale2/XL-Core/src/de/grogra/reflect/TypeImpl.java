
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

import java.lang.reflect.InvocationTargetException;

public class TypeImpl<T> extends MemberBase implements Type<T>
{
	protected final int typeId;
	private final Type<? super T> superclass;
	private final Type<?> componentType;
	private final String binaryName;
	private final String simpleName;
	private boolean valueOfSet = false;
	private Method valueOf;

	
	private static String getSimpleName (String binaryName, Type declaring)
	{
		return binaryName.substring
			(((declaring != null) ? declaring.getBinaryName ().length ()
			 : binaryName.lastIndexOf ('.')) + 1);
	}


	TypeImpl (int typeId, String binaryName, String descriptor,
			  int modifiers, Type<?> declaringClass, Type<? super T> superclass,
			  Type<?> componentType)
	{
		super ((componentType != null) ? componentType.getName () + "[]"
			   : (declaringClass == null) ? binaryName
			   : declaringClass.getName () + '.' + getSimpleName (binaryName,
			   													  declaringClass),
			   descriptor, modifiers, declaringClass);
		this.binaryName = binaryName;
		this.typeId = typeId;
		this.superclass = superclass;
		this.componentType = componentType;
		this.simpleName = (componentType != null)
			? componentType.getSimpleName () + "[]"
			: getSimpleName (binaryName, declaringClass);
	}

	
	protected TypeImpl (String binaryName, Type<? super T> superclass)
	{
		this (superclass.getTypeId (), binaryName, "??", PUBLIC, null, superclass, null);
	}

	
	public String getBinaryName ()
	{
		return binaryName;
	}

	
	public String getPackage ()
	{
		return (componentType != null) ? componentType.getPackage ()
			: binaryName.substring (0, Math.max (0, binaryName.lastIndexOf ('.')));
	}


	public int getTypeId ()
	{
		return typeId;
	}


	public Type<? super T> getSupertype ()
	{
		return superclass;
	}


	public int getDeclaredInterfaceCount ()
	{
		return 0;
	}


	public Type<?> getDeclaredInterface (int index)
	{
		throw new IndexOutOfBoundsException ();
	}


	public int getDeclaredFieldCount ()
	{
		return 0;
	}


	public Field getDeclaredField (int index)
	{
		throw new IndexOutOfBoundsException ();
	}


	public int getDeclaredTypeCount ()
	{
		return 0;
	}


	public Type<?> getDeclaredType (int index)
	{
		throw new IndexOutOfBoundsException ();
	}


	public int getDeclaredMethodCount ()
	{
		return 0;
	}


	public Method getDeclaredMethod (int index)
	{
		throw new IndexOutOfBoundsException ();
	}


	@Override
	public String getSimpleName ()
	{
		return simpleName;
	}


	public Type<?> getComponentType ()
	{
		return componentType;
	}


	public Type<?> getArrayType ()
	{
		return new XArray (this);
	}


	public Object createArray (int length)
	{
		throw new AssertionError ();
	}


	@Override
	public String toString ()
	{
		return Reflection.toString (this);
	}


	public boolean isInstance (Object object)
	{
		return false;
	}


	public Class<T> getImplementationClass ()
	{
		return (Class<T>) Object.class;
	}


	public TypeLoader getTypeLoader ()
	{
		return new ClassLoaderAdapter
			(getImplementationClass ().getClassLoader ());
	}


	public T cloneObject (T o, boolean deep)
	{
		return o;
	}


	public boolean isStringSerializable ()
	{
		if (!valueOfSet)
		{
			Type t = this;
			if (Reflection.isPrimitive (t))
			{
				t = ClassAdapter.wrap (Reflection.getWrapperClass (t.getTypeId ()));
			}
			Method m = Reflection.getDeclaredMethod
				(t, "mvalueOf;(Ljava/lang/String;)" + getDescriptor ());
			if ((m != null) && Reflection.isStatic (m)
				&& Reflection.isPublic (m))
			{
				valueOf = m;
			}
			else
			{
				m = Reflection.getDeclaredMethod
					(t, "m<init>;(Ljava/lang/String;)V");
				if ((m != null) && Reflection.isPublic (m))
				{
					valueOf = m;
				}
			}
			valueOfSet = true;
		}
		return valueOf != null;
	}


	public T valueOf (String s)
	{
		if (isStringSerializable ())
		{
			try
			{
				return (T) valueOf.invoke (null, new Object[] {s});
			}
			catch (IllegalAccessException e)
			{
				throw (UnsupportedOperationException) 
					new UnsupportedOperationException ()
					.initCause (e);
			}
			catch (InvocationTargetException e)
			{
				throw (UnsupportedOperationException) 
					new UnsupportedOperationException ()
					.initCause (e.getCause ());
			}
		}
		else
		{
			throw new UnsupportedOperationException (getName ());
		}
	}


	public T newInstance () throws InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		throw new UnsupportedOperationException ();
	}


	private Lookup lookup;

	public final synchronized Lookup getLookup ()
	{
		if (lookup == null)
		{
			lookup = new Lookup (this);
		}
		return lookup;
	}

	public Object getDefaultElementValue (String name)
	{
		return null;
	}

}
