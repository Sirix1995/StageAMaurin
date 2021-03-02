
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

public class IntHashMap<V> implements Iterable<IntHashMap.Entry<V>>
{
	public static class Entry<V>
	{
		final int key;
		V value;
		Entry<V> next;


		Entry (int key, V value)
		{
			this.key = key;
			this.value = value;
		}
	

		public Entry<V> next ()
		{
			Entry<V> e = next;
			while (e != null)
			{
				if (e.key == key)
	            {
	            	return e;
	            }
				e = e.next;
			}
			return null;
		}


		public int getKey ()
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
	private Entry<V>[] table;

	
	public IntHashMap (int capacity, float loadFactor)
	{
        this.loadFactor = loadFactor;
        capacity = (int) ((capacity + 1) / loadFactor) + 2;
        int c = 1;
        while (c < capacity)
        {
            c <<= 1;
        }
        resizeThreshold = (int) (c * loadFactor);
        table = (Entry<V>[]) new IntHashMap.Entry[c];
        lengthM1 = c - 1;
		size = 0;
	}


	public IntHashMap (int capacity)
	{
		this (capacity, 0.75f);
	}


	public IntHashMap ()
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
		Entry<V>[] t = table;
		for (int i = lengthM1; i >= 0; i--)
		{
			t[i] = null;
		}
		size = 0;
	}

	
	public void add (int key, V value)
	{
        add (new Entry<V> (key, value));
	}

	
	public V put (int key, V value)
	{
        int hashCode = getHashCode (key);
        Entry<V> e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.key == key))
            {
            	V old = e.value;
            	e.value = value;
            	return old;
            }
            e = e.next;
        }
        add (new Entry<V> (key, value));
        return null;
	}


	private void add (Entry<V> e)
	{
		int i = 	getHashCode (e.key) & lengthM1
;
		e.next = table[i];
		table[i] = e;
        if (++size > resizeThreshold)
        {
            Entry<V>[] t = new IntHashMap.Entry[(lengthM1 + 1) << 1];
            Entry<V>[] old = table;
            int tlM1 = t.length - 1;
            for (int j = lengthM1; j >= 0; j--)
            {
            	e = old[j];
                if (e != null)
                {
                    old[j] = null;
                    do
                    {
                        Entry<V> n = e.next;
						i = 	getHashCode (e.key) & tlM1
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


	public void remove (Entry<V> entry)
	{
		int i = 	getHashCode (entry.key) & lengthM1
;
        Entry<V> e = table[i], prev = null;
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


	public V remove (int key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<V> e = table[i], prev = null;
        while (e != null)
        {
            if ((e.key == key))
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


	public V remove (int key, V value)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<V> e = table[i], prev = null;
        while (e != null)
        {
            if ((value == e.value) && (e.key == key))
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


	public void removeAll (int key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry<V> e = table[i], prev = null;
        while (e != null)
        {
            if ((e.key == key))
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

	
	public Entry getEntry (int key)
	{
		int hashCode = getHashCode (key);
        Entry<V> e = table[hashCode & lengthM1];
        while (e != null)
        {
            if ((e.key == key))
            {
            	return e;
            }
            e = e.next;
        }
        return null;
	}


	public V get (int key)
	{
		Entry<V> e = getEntry (key);
		return (e != null) ? e.value : null;
	}

	public Object get (int key, Object defaultValue)
	{
		Entry<V> e = getEntry (key);
		return (e != null) ? e.value : defaultValue;
	}

	public boolean containsKey (int key)
	{
		return getEntry (key) != null;
	}



	static int getHashCode (int h)
	{
		h ^= (h >> 20) ^ (h >> 12);
		return h ^ (h >> 7) ^ (h >> 4);
	}


	public Iterator<Entry<V>> iterator ()
	{
		return new Iterator<Entry<V>> ()
		{
			private int index = -1;
			private Entry<V> next;
			private Entry<V> current;

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

			public Entry<V> next ()
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
				IntHashMap.this.remove (current);
			}
		};
	}

}

