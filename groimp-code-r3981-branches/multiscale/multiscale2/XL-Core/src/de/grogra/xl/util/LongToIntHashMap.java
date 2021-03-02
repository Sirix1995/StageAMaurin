
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

public class LongToIntHashMap implements Iterable<LongToIntHashMap.Entry>
{
	public static class Entry
	{
		final long key;
		int value;
		Entry next;


		Entry (long key, int value)
		{
			this.key = key;
			this.value = value;
		}
	

		public Entry next ()
		{
			Entry e = next;
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


		public long getKey ()
		{
			return key;
		}

		
		public int getValue ()
		{
			return value;
		}

		
		public int setValue (int newValue)
		{
			int old = value;
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
	private Entry[] table;

	
	public LongToIntHashMap (int capacity, float loadFactor)
	{
        this.loadFactor = loadFactor;
        capacity = (int) ((capacity + 1) / loadFactor) + 2;
        int c = 1;
        while (c < capacity)
        {
            c <<= 1;
        }
        resizeThreshold = (int) (c * loadFactor);
        table = (Entry[]) new LongToIntHashMap.Entry[c];
        lengthM1 = c - 1;
		size = 0;
	}


	public LongToIntHashMap (int capacity)
	{
		this (capacity, 0.75f);
	}


	public LongToIntHashMap ()
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
		Entry[] t = table;
		for (int i = lengthM1; i >= 0; i--)
		{
			t[i] = null;
		}
		size = 0;
	}

	
	public void add (long key, int value)
	{
        add (new Entry (key, value));
	}

	
	public int put (long key, int value)
	{
        int hashCode = getHashCode (key);
        Entry e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.key == key))
            {
            	int old = e.value;
            	e.value = value;
            	return old;
            }
            e = e.next;
        }
        add (new Entry (key, value));
        return 0;
	}


	private void add (Entry e)
	{
		int i = 	getHashCode (e.key) & lengthM1
;
		e.next = table[i];
		table[i] = e;
        if (++size > resizeThreshold)
        {
            Entry[] t = new LongToIntHashMap.Entry[(lengthM1 + 1) << 1];
            Entry[] old = table;
            int tlM1 = t.length - 1;
            for (int j = lengthM1; j >= 0; j--)
            {
            	e = old[j];
                if (e != null)
                {
                    old[j] = null;
                    do
                    {
                        Entry n = e.next;
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


	public void remove (Entry entry)
	{
		int i = 	getHashCode (entry.key) & lengthM1
;
        Entry e = table[i], prev = null;
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


	public int remove (long key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry e = table[i], prev = null;
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
        return 0;
	}


	public int remove (long key, int value)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry e = table[i], prev = null;
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
        return 0;
	}


	public void removeAll (long key)
	{
		int hashCode = getHashCode (key);
		int i = hashCode & lengthM1;
        Entry e = table[i], prev = null;
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

	
	public Entry getEntry (long key)
	{
		int hashCode = getHashCode (key);
        Entry e = table[hashCode & lengthM1];
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


	public int get (long key)
	{
		Entry e = getEntry (key);
		return (e != null) ? e.value : 0;
	}

	public int get (long key, int defaultValue)
	{
		Entry e = getEntry (key);
		return (e != null) ? e.value : defaultValue;
	}

	public boolean containsKey (long key)
	{
		return getEntry (key) != null;
	}



	static int getHashCode (long k)
	{
		int h = (int) k ^ (int) (k >> 32);
		h ^= (h >> 20) ^ (h >> 12);
		return h ^ (h >> 7) ^ (h >> 4);
	}


	public Iterator<Entry> iterator ()
	{
		return new Iterator<Entry> ()
		{
			private int index = -1;
			private Entry next;
			private Entry current;

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

			public Entry next ()
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
				LongToIntHashMap.this.remove (current);
			}
		};
	}

}

