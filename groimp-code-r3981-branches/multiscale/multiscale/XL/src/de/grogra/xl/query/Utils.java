
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

package de.grogra.xl.query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.xl.util.ClassLoaderObjectInputStream;
import de.grogra.xl.util.XHashMap;

public final class Utils
{
	private Utils ()
	{
	}


	public static void writeArray (BytecodeSerialization out, Object array)
		throws IOException
	{
		if (array == null)
		{
			out.visitObject (null);
		}
		else
		{
			int len = java.lang.reflect.Array.getLength (array);
			if ((len < 64) && (array instanceof boolean[]))
			{
				out.beginMethod (Reflection.getDeclaredMethod (ClassAdapter.wrap (Utils.class), "toBooleanArray"));
				out.visitLong (toLong ((boolean[]) array));
				out.endMethod ();
				return;
			}
			out.beginArray (len, ClassAdapter.wrap (array.getClass ().getComponentType ()));
			for (int i = 0; i < len; i++)
			{
				if (array instanceof Object[])
				{
					Object o = ((Object[]) array)[i];
					if (o != null)
					{
						out.beginArrayComponent (i);
						if (o.getClass ().getComponentType () != null)
						{
							writeArray (out, o);
						}
						else
						{
							out.visitObject (o);
						}
						out.endArrayComponent ();
					}
				}
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

				else if (array instanceof $type[])
				{
#if (!$pp.fnumeric)
					if ((($type[]) array)[i] != $pp.null)
#end
					{
						out.beginArrayComponent (i);
						out.visit$pp.Type ((($type[]) array)[i]);
						out.endArrayComponent ();
					}
				}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
				else if (array instanceof boolean[])
				{
					if (((boolean[]) array)[i] != false)
					{
						out.beginArrayComponent (i);
						out.visitBoolean (((boolean[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof byte[])
				{
					if (((byte[]) array)[i] != ((byte) 0))
					{
						out.beginArrayComponent (i);
						out.visitByte (((byte[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof short[])
				{
					if (((short[]) array)[i] != ((short) 0))
					{
						out.beginArrayComponent (i);
						out.visitShort (((short[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof char[])
				{
					if (((char[]) array)[i] != ((char) 0))
					{
						out.beginArrayComponent (i);
						out.visitChar (((char[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof int[])
				{
					if (((int[]) array)[i] != ((int) 0))
					{
						out.beginArrayComponent (i);
						out.visitInt (((int[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof long[])
				{
					if (((long[]) array)[i] != ((long) 0))
					{
						out.beginArrayComponent (i);
						out.visitLong (((long[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof float[])
				{
					{
						out.beginArrayComponent (i);
						out.visitFloat (((float[]) array)[i]);
						out.endArrayComponent ();
					}
				}
// generated
// generated
				else if (array instanceof double[])
				{
					{
						out.beginArrayComponent (i);
						out.visitDouble (((double[]) array)[i]);
						out.endArrayComponent ();
					}
				}
//!! *# End of generated code
			}
			out.endArray ();
		}
	}


	public static Method getConstructor (Object instance)
	{
		XHashMap.Entry e = ClassAdapter.wrap (instance.getClass ()).getLookup ().getMethods ("<init>");
		Method m = (Method) e.getValue ();
		if (e.next () != null)
		{
			throw new AssertionError ("More than one constructor in " + instance.getClass ());
		}
		return m;
	}


	public static Method getConstructor (Object instance, String paramInitials)
	{
	scanConstructors:
		for (XHashMap.Entry e = ClassAdapter.wrap (instance.getClass ()).getLookup ().getMethods ("<init>");
			 e != null; e = e.next ())
		{
			Method m = (Method) e.getValue ();
			if (m.getParameterCount () == paramInitials.length ())
			{
				for (int i = paramInitials.length () - 1; i >= 0; i--)
				{
					if (paramInitials.charAt (i)
						!= m.getParameterType (i).getSimpleName ().charAt (0))
					{
						continue scanConstructors; 
					}
				}
				return m;
			}
		}
		throw new NoSuchMethodError ("No constructor " + paramInitials + " in " + instance.getClass ());
	}


	public static Method getConstructor (Object instance, int length)
	{
		Method c = null;
		for (XHashMap.Entry e = ClassAdapter.wrap (instance.getClass ()).getLookup ().getMethods ("<init>");
			 e != null; e = e.next ())
		{
			Method m = (Method) e.getValue ();
			if (m.getParameterCount () == length)
			{
				if (c != null)
				{
					throw new AssertionError ("More than one constructor in " + instance.getClass ());
				}
				c = m;
			}
		}
		if (c == null)
		{
			throw new NoSuchMethodError ("No constructor with "  + length + " arguments in " + instance.getClass ());
		}
		return c;
	}


	public static String toString (byte[] bytes)
	{
		char[] c = new char[bytes.length];
		for (int i = 0; i < bytes.length; i++)
		{
			c[i] = (char) (bytes[i] & 0xff);
		}
		return new String (c);
	}


	public static byte[] toBytes (String bytes)
	{
		byte[] b = new byte[bytes.length ()];
		for (int i = 0; i < b.length; i++)
		{
			b[i] = (byte) bytes.charAt (i);
		}
		return b;
	}

	public static long toLong (boolean[] array)
	{
		if (array.length > 63)
		{
			throw new IllegalArgumentException ();
		}
		long bits = 1L << array.length;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i])
			{
				bits |= 1L << i;
			}
		}
		return bits;
	}

	public static boolean[] toBooleanArray (long bits)
	{
		int n = 63 - Long.numberOfLeadingZeros (bits);
		boolean[] a = new boolean[n];
		for (int i = 0; i < n; i++)
		{
			a[i] = ((int) (bits >> i) & 1) != 0;
		}
		return a;
	}
	
	
	public static ObjectInputStream getObjectInput (String bytes, ClassLoader loader)
		throws IOException
	{
		return new ClassLoaderObjectInputStream
			(new ByteArrayInputStream (toBytes (bytes)), loader); 
	}
	
	
	public static ClassAdapter toClassAdapter (Object[] array)
	{
		return ClassAdapter.wrap (array.getClass ().getComponentType (), false);
	}

}
