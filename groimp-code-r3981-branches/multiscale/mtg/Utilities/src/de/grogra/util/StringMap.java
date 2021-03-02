
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

import de.grogra.xl.util.XHashMap;
import de.grogra.xl.util.ObjectList;

public class StringMap implements ModifiableMap, java.io.Serializable
{
	private static final long serialVersionUID = 2334469118725787256L;

	public static final de.grogra.reflect.Type TYPE
		= de.grogra.reflect.ClassAdapter.wrap (StringMap.class);

	private final boolean reverseSort;

	private String[] keys;
	private Object[] values;
	private int size;
	private Map parent;


	public StringMap (int capacity, boolean reverseSort)
	{
		this.reverseSort = reverseSort;
		if (capacity <= 0)
		{
			capacity = 2;
		}
		keys = new String[capacity];
		values = new Object[capacity];
		size = 0;
		parent = null;
	}


	public StringMap (int capacity)
	{
		this (capacity, false);
	}


	public StringMap ()
	{
		this (16);
	}


	public StringMap (Object[] keyValuePairs)
	{
		this (keyValuePairs.length);
		for (int i = 0; i < keyValuePairs.length; i += 2)
		{
			put ((String) keyValuePairs[i], keyValuePairs[i + 1]);
		}
	}


	public StringMap (Map parent)
	{
		this ();
		this.parent = parent;
	}


	public StringMap dup ()
	{
		StringMap m = new StringMap (size, reverseSort);
		m.size = size;
		System.arraycopy (keys, 0, m.keys, 0, size);
		System.arraycopy (values, 0, m.values, 0, size);
		return m;
	}


	public final String[] getKeys ()
	{
		String[] k = new String[size];
		System.arraycopy (keys, 0, k, 0, size);
		return k;
	}


	public final void getValues (Object[] values)
	{
		System.arraycopy (this.values, 0, values, 0, size);
	}


	public final int findIndex (String key)
	{
		return findIndex (key, 0, key.length ());
	}


	public final int findIndex (String key, int begin, int length)
	{
		int fromIndex = 0, toIndex = size;
		while (fromIndex < toIndex)
		{
			int p = (fromIndex + toIndex) >> 1;
			String k = keys[p];
		compareLT:
			{
			compareGT:
				{
					if (reverseSort)
					{
						int ki = k.length ();
						for (int i = length - 1; i >= 0; i--)
						{
							if (--ki < 0)
							{
								break compareLT;
							}
							int d;
							if ((d = k.charAt (ki) - key.charAt (begin + i))
								> 0)
							{
								break compareGT;
							}
							else if (d < 0)
							{
								break compareLT;
							}
						}
					}
					else
					{
						for (int i = 0; i < length; i++)
						{
							if (i == k.length ())
							{
								break compareLT;
							}
							int d;
							if ((d = k.charAt (i) - key.charAt (begin + i))
								> 0)
							{
								break compareGT;
							}
							else if (d < 0)
							{
								break compareLT;
							}
						}
					}
					if (length == k.length ())
					{
						// keys[p] == key
						return p;
					}
				}
				// keys[p] > key
				toIndex = p;
				continue;
			}
			// keys[p] < key
			fromIndex = p + 1;
		}
		return ~fromIndex;
	}


	public final int findIndex (CharSequence key, int begin, int length,
								int fromIndex, int toIndex)
	{
		while (fromIndex < toIndex)
		{
			int p = (fromIndex + toIndex) >> 1;
			String k = keys[p];
		compareLT:
			{
			compareGT:
				{
					if (reverseSort)
					{
						int ki = k.length ();
						for (int i = length - 1; i >= 0; i--)
						{
							if (--ki < 0)
							{
								break compareLT;
							}
							int d;
							if ((d = k.charAt (ki) - key.charAt (begin + i))
								> 0)
							{
								break compareGT;
							}
							else if (d < 0)
							{
								break compareLT;
							}
						}
					}
					else
					{
						for (int i = 0; i < length; i++)
						{
							if (i == k.length ())
							{
								break compareLT;
							}
							int d;
							if ((d = k.charAt (i) - key.charAt (begin + i))
								> 0)
							{
								break compareGT;
							}
							else if (d < 0)
							{
								break compareLT;
							}
						}
					}
					if (length == k.length ())
					{
						// keys[p] == key
						return p;
					}
				}
				// keys[p] > key
				toIndex = p;
				continue;
			}
			// keys[p] < key
			fromIndex = p + 1;
		}
		return ~fromIndex;
	}


	public final synchronized Object synchronizedPut (String key, Object object)
	{
		return put (key, object);
	}


	public final Object put (Object key, Object object)
	{
		return put ((String) key, object);
	}


	public final Object put (String key, Object object)
	{
		int i = findIndex (key, 0, key.length ());
		if (i >= 0)
		{
			Object prev = values[i];
			values[i] = object;
			return prev;
		}
		i = ~i;
		if (keys.length == size)
		{
			System.arraycopy (keys, 0, keys = new String[2 * size], 0, size);
			System.arraycopy (values, 0, values = new Object[2 * size], 0,
							  size);
		}
		if (i < size)
		{
			System.arraycopy (keys, i, keys, i + 1, size - i);
			System.arraycopy (values, i, values, i + 1, size - i);
		}
		keys[i] = key;
		values[i] = object;
		size++;
		return null;
	}


	public final synchronized Object synchronizedGet (String key)
	{
		return get (key);
	}


	public final Object get (String key)
	{
		int i = findIndex (key, 0, key.length ());
		return (i >= 0) ? values[i]
			: (parent != null) ? parent.get (key, null) : null;
	}


	public final Object get (String key, boolean includeParent)
	{
		int i = findIndex (key, 0, key.length ());
		return (i >= 0) ? values[i]
			: (includeParent && (parent != null)) ? parent.get (key, null)
			: null;
	}


	public final synchronized Object synchronizedRemove (String key)
	{
		return remove (key);
	}


	public final Object remove (String key)
	{
		int i = findIndex (key, 0, key.length ());
		if (i >= 0)
		{
			return removeAt (i);
		}
		return null;
	}


	public final Object removeAt (int index)
	{
		if (index >= size)
		{
			throw new ArrayIndexOutOfBoundsException (String.valueOf (index));
		}
		Object o = values[index];
		size--;
		System.arraycopy (keys, index + 1, keys, index, size - index);
		System.arraycopy (values, index + 1, values, index, size - index);
		keys[size] = null;
		values[size] = null;
		return o;
	}


	public final boolean containsKey (String key)
	{
		return (findIndex (key, 0, key.length ()) >= 0)
			|| ((parent != null)
				&& (parent.get (key, DEFAULT_VALUE) != DEFAULT_VALUE));
	}


	public final boolean containsKey (Object key)
	{
		if (key instanceof String)
		{
			String k = (String) key;
			if (findIndex (k, 0, k.length ()) >= 0)
			{
				return true;
			}
		}
		return (parent != null)
			&& (parent.get (key, DEFAULT_VALUE) != DEFAULT_VALUE);
	}


	public final Object get (Object key, Object defaultValue)
	{
		if (key instanceof String)
		{
			int i = findIndex ((String) key, 0, ((String) key).length ());
			if (i >= 0)
			{
				return values[i];
			}
		}
		return (parent != null) ? parent.get (key, defaultValue)
			: defaultValue;
	}


	public final int size ()
	{
		return size;
	}

	
	public final boolean isEmpty ()
	{
		return size == 0;
	}


	public final String getKeyAt (int index)
	{
		return keys[index];
	}


	public final Object getValueAt (int index)
	{
		return values[index];
	}


	public final Object setValueAt (int index, Object value)
	{
		Object o = values[index];
		values[index] = value;
		return o;
	}


	public final void clear ()
	{
		for (int i = size - 1; i >= 0; i--)
		{
			keys[i] = null;
			values[i] = null;
		}
		size = 0;
	}


	public final void disposeValuesAndClear ()
	{
		int i;
		while ((i = --size) >= 0)
		{
			keys[i] = null;
			if (values[i] instanceof Disposable)
			{
				((Disposable) values[i]).dispose ();
			}
			values[i] = null;
		}
		size = 0;
	}

/*!!

#foreach ($type in $types)
$pp.setType($type)

	public final StringMap put$pp.Type (String key, $type value)
	{
		put (key, $pp.wrap("value"));
		return this;
	}


	public final $type get$pp.Type (String key)
	{
#if ($pp.Object)
		return get (key);
#else
		Object v = get (key);
		return (v != null) ? $pp.unwrap("v") : $pp.null;
#end
	}


	public final $type get$pp.Type (String key, $type defaultValue)
	{
#if ($pp.Object)
		return get (key, defaultValue);
#else
		Object v = get (key);
		return (v != null) ? $pp.unwrap("v") : defaultValue;
#end
	}

#end

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
	public final StringMap putBoolean (String key, boolean value)
	{
		put (key, ((value) ? Boolean.TRUE : Boolean.FALSE));
		return this;
	}
// generated
// generated
	public final boolean getBoolean (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Boolean) (v)).booleanValue ()) : false;
	}
// generated
// generated
	public final boolean getBoolean (String key, boolean defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Boolean) (v)).booleanValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putByte (String key, byte value)
	{
		put (key, Byte.valueOf (value));
		return this;
	}
// generated
// generated
	public final byte getByte (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).byteValue ()) : ((byte) 0);
	}
// generated
// generated
	public final byte getByte (String key, byte defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).byteValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putShort (String key, short value)
	{
		put (key, Short.valueOf (value));
		return this;
	}
// generated
// generated
	public final short getShort (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).shortValue ()) : ((short) 0);
	}
// generated
// generated
	public final short getShort (String key, short defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).shortValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putChar (String key, char value)
	{
		put (key, Character.valueOf (value));
		return this;
	}
// generated
// generated
	public final char getChar (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Character) (v)).charValue ()) : ((char) 0);
	}
// generated
// generated
	public final char getChar (String key, char defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Character) (v)).charValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putInt (String key, int value)
	{
		put (key, Integer.valueOf (value));
		return this;
	}
// generated
// generated
	public final int getInt (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).intValue ()) : ((int) 0);
	}
// generated
// generated
	public final int getInt (String key, int defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).intValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putLong (String key, long value)
	{
		put (key, Long.valueOf (value));
		return this;
	}
// generated
// generated
	public final long getLong (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).longValue ()) : ((long) 0);
	}
// generated
// generated
	public final long getLong (String key, long defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).longValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putFloat (String key, float value)
	{
		put (key, Float.valueOf (value));
		return this;
	}
// generated
// generated
	public final float getFloat (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).floatValue ()) : ((float) 0);
	}
// generated
// generated
	public final float getFloat (String key, float defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).floatValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putDouble (String key, double value)
	{
		put (key, Double.valueOf (value));
		return this;
	}
// generated
// generated
	public final double getDouble (String key)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).doubleValue ()) : ((double) 0);
	}
// generated
// generated
	public final double getDouble (String key, double defaultValue)
	{
		Object v = get (key);
		return (v != null) ? (((Number) (v)).doubleValue ()) : defaultValue;
	}
// generated
// generated
// generated
	public final StringMap putObject (String key, Object value)
	{
		put (key, (value));
		return this;
	}
// generated
// generated
	public final Object getObject (String key)
	{
		return get (key);
	}
// generated
// generated
	public final Object getObject (String key, Object defaultValue)
	{
		return get (key, defaultValue);
	}
// generated
// generated
//!! *# End of generated code


	public final String getString (String key)
	{
		return (String) get (key);
	}


	public final java.util.Map toMap ()
	{
		java.util.TreeMap map = new java.util.TreeMap ();
		for (int i = 0; i < size; i++)
		{
			map.put (keys[i], values[i]);
		}
		return map;
	}


	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer ("StringMap{");
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				b.append (',');
			}
			b.append (keys[i]).append ("->").append (values[i]);
		}
		return b.append('}').toString ();
	}

	
	public static void main (String[] args)
	{
		String[] a = {
			"get", "set", "is", "to", "value", "string", "map", "int",
			"put", "remove", "add", "contained", "out", "parent", "size"};
		
		java.util.Random rnd = new java.util.Random ();
		StringBuffer b = new StringBuffer ();
		ObjectList v = new ObjectList ();
		for (int i = 0; i < 100000; i++)
		{
			b.setLength (0);
			int n = 1 + rnd.nextInt (4);
			while (--n >= 0)
			{
				b.append (a[rnd.nextInt (a.length)]);
			}
			v.add (b.toString ());
		}

		v.toArray (a = new String[v.size]);

		int m = a.length - 1, n = m / 2;

		java.util.HashMap h1 = new java.util.HashMap (),
		h2 = new java.util.HashMap ();

		XHashMap x = new XHashMap ();

		Utils.resetTime ();
		for (int i = n; i >= 0; i--)
		{
			h1.put (a[i], a);
		}
		Utils.printTime ("put H1");

		Utils.resetTime ();
		for (int i = n; i >= 0; i--)
		{
			h2.put (a[i], a);
		}
		Utils.printTime ("put H2");

		Utils.resetTime ();
		for (int i = n; i >= 0; i--)
		{
			x.put (a[i], a);
		}
		Utils.printTime ("put X");

		StringMap s = new StringMap ();

		Utils.resetTime ();
		for (int i = n; i >= 0; i--)
		{
			s.put (a[i], a);
		}
		Utils.printTime ("put S");
		
		for (int i = m; i >= 0; i--)
		{
			a[i] = new String (a[i]);
		}

		Utils.resetTime ();
		for (int i = m; i >= 0; i--)
		{
			h1.get (a[i]);
		}
		Utils.printTime ("get H1");

		Utils.resetTime ();
		for (int i = m; i >= 0; i--)
		{
			h2.get (a[i]);
		}
		Utils.printTime ("get H2");

		Utils.resetTime ();
		for (int i = m; i >= 0; i--)
		{
			x.get (a[i], null);
		}
		Utils.printTime ("get X");

		Utils.resetTime ();
		for (int i = m; i >= 0; i--)
		{
			s.get (a[i]);
		}
		Utils.printTime ("get S");
	}
}

