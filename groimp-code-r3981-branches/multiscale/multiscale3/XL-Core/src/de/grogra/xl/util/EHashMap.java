
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

public class EHashMap<E extends EHashMap.Entry>
{
	public static abstract class Entry
	{
		public Entry listNext; 
		public Entry listPrev;

		protected int hashCode;
		Entry next;


		public Entry next ()
		{
			Entry e = next;
			while (e != null)
			{
	            if ((e.hashCode == hashCode) && keyEquals (e))
	            {
	            	return e;
	            }
				e = e.next;
			}
			return null;
		}

		
		protected abstract boolean keyEquals (Entry e);
		
		
		protected abstract void copyValue (Entry src);
		
		
		protected abstract void clear ();
	}

	
	public static class ObjectEntry<K,V> extends Entry
	{
		K key;
		public V value;
		
		
		public K getKey ()
		{
			return key;
		}
		
		
		public void setKey (K key)
		{
			this.hashCode = (key != null) ? key.hashCode () : 0;
			this.key = key;
		}

		
		@Override
		protected boolean keyEquals (Entry e)
		{
			return (key == ((ObjectEntry) e).key)
				|| ((key != null) && key.equals (((ObjectEntry) e).key));
		}

	
		@Override
		protected void copyValue (Entry src)
		{
			value = ((ObjectEntry<K,V>) src).value;
		}
	
	
		@Override
		protected void clear ()
		{
			key = null;
			value = null;
		}
		
	}

	
	public static class IdentityEntry<K,V> extends ObjectEntry<K,V>
	{
		@Override
		protected boolean keyEquals (Entry e)
		{
			return key == ((IdentityEntry) e).key;
		}
	}


	private final float loadFactor;
	private int size;
	private int resizeThreshold;
	private int lengthM1;
	private Entry[] table;
	private E first;
	private E last;
	private final Entry[] pool;

	
	public EHashMap (Entry[] entryPool, int capacity, float loadFactor)
	{
		this.pool = entryPool;
        this.loadFactor = loadFactor;
        capacity = (int) ((capacity + 1) / loadFactor) + 2;
        int c = 1;
        while (c < capacity)
        {
            c <<= 1;
        }
        resizeThreshold = (int) (c * loadFactor);
        table = new Entry[c];
        lengthM1 = c - 1;
		size = 0;
	}

	
	public EHashMap (int capacity, float loadFactor)
	{
		this (new Entry[1], capacity, loadFactor);
	}


	public EHashMap (int capacity)
	{
		this (capacity, 0.75f);
	}


	public EHashMap ()
	{
		this (16, 0.75f);
	}

	
	public E popEntryFromPool ()
	{
		E e = (E) pool[0];
		if (e != null)
		{
			pool[0] = e.listNext;
			e.listNext = null;
		}
		return e;
	}
	
	
	public void addEntryToPool (E e)
	{
    	e.clear ();
    	e.listNext = pool[0];
    	pool[0] = e;
	}


	public E getFirstEntry ()
	{
		return first;
	}


	public E getLastEntry ()
	{
		return last;
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
		size = 0;
		int m = lengthM1;
		for (Entry e = first; e != null; e = e.listNext)
		{
			t[e.hashCode & m] = null;
			e.next = null;
			e.listPrev = null;
			e.clear ();
		}
		if (last != null)
		{
			last.listNext = pool[0];
			pool[0] = first;
			first = null;
			last = null;
		}
	}

	
	public E put (E entry)
	{
        int hashCode = entry.hashCode;
        Entry e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.hashCode == hashCode) && entry.keyEquals (e))
            {
            	e.copyValue (entry);
            	entry.clear ();
            	entry.listNext = pool[0];
            	pool[0] = entry;
            	return (E) e;
            }
            e = e.next;
        }
        add (entry);
        return null;
	}

	
	public E getOrPut (E entry)
	{
        int hashCode = entry.hashCode;
        Entry e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.hashCode == hashCode) && entry.keyEquals (e))
            {
            	entry.clear ();
            	entry.listNext = pool[0];
            	pool[0] = entry;
            	return (E) e;
            }
            e = e.next;
        }
        add (entry);
        return null;
	}

	
	public E get (E key)
	{
        int hashCode = key.hashCode;
        Entry e = table[hashCode & lengthM1]; 
        while (e != null)
        {
            if ((e.hashCode == hashCode) && key.keyEquals (e))
            {
            	return (E) e;
            }
            e = e.next;
        }
        return null;
	}


	public void add (E e)
	{
		int i = e.hashCode & lengthM1;
		e.next = table[i];
		table[i] = e;
		if (first == null)
		{
			first = e;
		}
		if ((e.listPrev = last) != null)
		{
			last.listNext = e;
		}
		last = e;
        if (++size > resizeThreshold)
        {
            Entry[] t = new Entry[(lengthM1 + 1) << 1];
            Entry[] old = table;
            int tlM1 = t.length - 1;
            for (int j = lengthM1; j >= 0; j--)
            {
            	e = (E) old[j];
                if (e != null)
                {
                    old[j] = null;
                    do
                    {
                        Entry n = e.next;
                        i = e.hashCode & tlM1;  
                        e.next = t[i];
                        t[i] = e;
                        e = (E) n;
                    } while (e != null);
                }
            }
            table = t;
            lengthM1 = tlM1;
            resizeThreshold = (int) (t.length * loadFactor);
        }
    }

	
	public void makeFirst (E entry)
	{
    	if (entry != first)
    	{
    		if (entry == last)
    		{
    			last = (E) entry.listPrev;
    		}
    		if ((entry.listPrev.listNext = entry.listNext) != null)
    		{
    			entry.listNext.listPrev = entry.listPrev;
    		}
    		entry.listPrev = null;
    		if ((entry.listNext = first) != null)
    		{
    			first.listPrev = entry;
    		}
    		first = entry;
    	}
	}


	public void remove (E entry)
	{
        int hashCode = entry.hashCode;
        Entry e = table[hashCode & lengthM1], prev = null;
        while (e != null)
        {
            if ((e == entry) || ((e.hashCode == hashCode) && entry.keyEquals (e)))
            {
            	size--;
            	if (prev != null)
            	{
            		prev.next = e.next;
            	}
            	else
            	{
            		table[hashCode & lengthM1] = e.next;
            	}
            	if (e == last)
            	{
            		last = (E) e.listPrev;
            	}
            	if (e == first)
            	{
            		if ((first = (E) e.listNext) != null)
            		{
            			first.listPrev = null;
            		}
            	}
            	else
            	{
            		if ((e.listPrev.listNext = e.listNext) != null)
            		{
            			e.listNext.listPrev = e.listPrev;
            		}
            		e.listPrev = null;
            	}
            	e.next = null;
            	e.clear ();
            	e.listNext = pool[0];
            	pool[0] = e;
            	return;
            }
            prev = e;
            e = e.next;
        }
        return;
	}

}
