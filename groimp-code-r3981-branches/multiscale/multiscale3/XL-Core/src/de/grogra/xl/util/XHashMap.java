
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

package de.grogra.xl.util;

import java.util.Iterator;

public class XHashMap<K,V> implements Iterable<XHashMap.Entry<K,V>>
{
	public static class Entry<K,V>
	{
		final XHashMap<K,V> map;
		final int hashCode;
		final K key;
		V value;
		Entry<K,V> next;


		public void remove ()
		{
			map.remove (this);
		}


		Entry (XHashMap<K,V> map, int hashCode, K key, V value)
		{
			this.map = map;
			this.hashCode = hashCode;
			this.key = key;
			this.value = value;
		}
	

		public Entry<K,V> next ()
		{
			Entry<K,V> e = next;
			while (e != null)
			{
	            if ((e.hashCode == hashCode) && map.equals (key, e.key))
	            {
	            	return e;
	            }
				e = e.next;
			}
			return null;
		}


		public K getKey ()
		{
			return key;
		}

		
		public V getValue ()
		{
			return value;
		}

		
		public V setValue (V newValue)
		{
			V old = value;
			value = newValue;
			return old;
		}

		@Override
		public String toString ()
		{
			return key + "=" + value;
		}
	}


	private float loadFactor;
	private int size;
	private int resizeThreshold;
	private int lengthM1;
	private Entry<K,V>[] table;

	
	public XHashMap (int capacity, float loadFactor)
	{
        this.loadFactor = loadFactor;
        capacity = (int) ((capacity + 1) / loadFactor) + 2;
        int c = 1;
        while (c < capacity)
        {
            c <<= 1;
        }
        resizeThreshold = (int) (c * loadFactor);
        table = (Entry<K,V>[]) new XHashMap.Entry[c];
        lengthM1 = c - 1;
		size = 0;
	}


	public XHashMap (int capacity)
	{
		this (capacity, 0.75f);
	}


	public XHashMap ()
	{
		this (16, 0.75f);
	}


	public boolean isEmpty ()
	{
		return size == 0;
	}
	
	
	public int size ()
	{
		return size;
	}

	
	public void clear ()
	{
		Entry<K,V>[] t = table;
		for (int i = lengthM1; i >= 0; i--)
		{
			t[i] = null;
		}
		size = 0;
	}

	
	public void add (K key, V value)
	{
        add (new Entry<K,V> (this, getHashCode (key), key, value));
	}

	
	public V put (K key, V value)
	{
        int hashCode = getHashCode (key);
        Entry<K,V> e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.hashCode == hashCode) && equals (key, e.key))
            {
            	V old = e.value;
            	e.value = value;
            	return old;
            }
            e = e.next;
        }
        add (new Entry<K,V> (this, hashCode, key, value));
        return null;
	}


	private void add (Entry<K,V> e)
	{
		int i = 	e.hashCode & lengthM1
;
		e.next = table[i];
		table[i] = e;
        if (++size > resizeThreshold)
        {
            Entry<K,V>[] t = new XHashMap.Entry[(lengthM1 + 1) << 1];
            Entry<K,V>[] old = table;
            int tlM1 = t.length - 1;
            for (int j = lengthM1; j >= 0; j--)
            {
            	e = old[j];
                if (e != null)
                {
                    old[j] = null;
                    do
                    {
                        Entry<K,V> n = e.next;
						i = 	e.hashCode & tlM1
;
                        e.next = t[i];
                        t[i] = e;
                        e = n;
                    } while (e != null);
                }
            }
            table = t;
            lengthM1 = tlM1;
            resizeThreshold = (int) (t.length * loadFactor);
        }
    }


	public void remove (Entry<K,V> entry)
	{
		int i = 	entry.hashCode & lengthM1
;
        Entry<K,V> e = table[i], prev = null;
        while (e != null)
        {
            if (e == entry)
            {
            	size--;
            	if (prev != null)
            	{
            		prev.next = e.next;
            	}
            	else
            	{
            		table[i] = e.next;
            	}
            	return;
            }
            prev = e;
            e = e.next;
        }
	}


	public V remove (K key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<K,V> e = table[i], prev = null;
        while (e != null)
        {
            if ((e.hashCode == hashCode) && equals (key, e.key))
            {
            	size--;
            	if (prev != null)
            	{
            		prev.next = e.next;
            	}
            	else
            	{
            		table[i] = e.next;
            	}
            	return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
	}


	public V remove (K key, V value)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<K,V> e = table[i], prev = null;
        while (e != null)
        {
            if ((value == e.value) && (e.hashCode == hashCode) && equals (key, e.key))
            {
            	size--;
            	if (prev != null)
            	{
            		prev.next = e.next;
            	}
            	else
            	{
            		table[i] = e.next;
            	}
            	return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
	}


	public void removeAll (K key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<K,V> e = table[i], prev = null;
        while (e != null)
        {
            if ((e.hashCode == hashCode) && equals (key, e.key))
            {
            	size--;
            	if (prev != null)
            	{
            		prev.next = e.next;
            	}
            	else
            	{
            		table[i] = e.next;
            	}
            }
            else
            {
                prev = e;
            }
            e = e.next;
        }
	}

	
	public Entry getEntry (K key)
	{
		int hashCode = getHashCode (key);
        Entry<K,V> e = table[hashCode & lengthM1];
        while (e != null)
        {
            if ((e.hashCode == hashCode) && equals (key, e.key))
            {
            	return e;
            }
            e = e.next;
        }
        return null;
	}


	public V get (K key)
	{
		Entry<K,V> e = getEntry (key);
		return (e != null) ? e.value : null;
	}

	public Object get (K key, Object defaultValue)
	{
		Entry<K,V> e = getEntry (key);
		return (e != null) ? e.value : defaultValue;
	}

	public boolean containsKey (K key)
	{
		return getEntry (key) != null;
	}



	protected int getHashCode (K o)
	{
		if (o == null)
		{
			return 0;
		}
		int h = o.hashCode();
		h ^= (h >> 20) ^ (h >> 12);
		return h ^ (h >> 7) ^ (h >> 4);
	}

	protected boolean equals (K a, K b)
	{
		return (a == b) || ((a != null) && a.equals (b));
	}


	public Iterator<Entry<K,V>> iterator ()
	{
		return new Iterator<Entry<K,V>> ()
		{
			private int index = -1;
			private Entry<K,V> next;
			private Entry<K,V> current;

			public boolean hasNext ()
			{
				while (next == null)
				{
					if (++index > lengthM1)
					{
						return false;
					}
					next = table[index];
				}
				return next != null;
			}

			public Entry<K,V> next ()
			{
				if (!hasNext ())
				{
					return null;
				}
				current = next;
				next = current.next;
				return current;
			}

			public void remove ()
			{
				XHashMap.this.remove (current);
			}
		};
	}

}

