
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

package de.grogra.persistence;

import java.lang.ref.*;
import java.util.Hashtable;
import de.grogra.persistence.Transaction.Data;
import de.grogra.persistence.Transaction.Key;

public final class LogStore
{
	/**
	 * If <code>true</code>, all entries are kept. If <code>false</code>
	 * the data of an entry is referenced softly via instances of
	 * {@link DataRef} and, thus, may be garbage-collected.
	 */
	private final boolean keepAll;

	/**
	 * The maximum amount of bytes to keep strongly referenced. Data
	 * exceeding this limit will be softly referenced. 
	 */
	private int maxBytes = (int) Math.min (32 << 20, Runtime.getRuntime ().maxMemory () * 0.05);

	private int maxSoftBytes = 2 * maxBytes;

	private final Hashtable<Key, Entry> map = new Hashtable<Key, Entry> ();

	/**
	 * First (oldest) entry in this store.
	 */
	private volatile Entry first = null;

	/**
	 * Last (most recent) entry in this store.
	 */
	private volatile Entry last = null;

	/**
	 * First (oldest) entry in this store which contains a strong reference
	 * to its data.
	 */
	private Entry firstStrong = null;

	private ReferenceQueue queue = new ReferenceQueue ();

	
	static class DataRef extends SoftReference<Data>
	{
		final Key key;

		DataRef (Data object, ReferenceQueue ref)
		{
			super (object, ref);
			key = object.getKey ();
		}
	}


	public static final class Entry
	{
		Key key;
		
		
		/**
		 * Contains the data as soft reference.
		 */
		DataRef softRef;
		
		/**
		 * Contains the data as strong reference.
		 */
		Data strongRef;

		/**
		 * Next (more recent) entry in the list.
		 */
		Entry next = null;

		/**
		 * Previous (less recent) entry in the list.
		 */
		Entry previous = null;

		/**
		 * Size in bytes from the first entry up to and including this entry.
		 */
		private final long accumulatedSize;

		/**
		 * Consecutive number of this entry, starting from 0 for the first entry.
		 */
		private final int id;

		/**
		 * (Estimate of) size in bytes of the data of this entry.
		 */
		private final int size;


		Entry (Key key, Data data, ReferenceQueue queue, Entry previous)
		{
			this.key = key;
			softRef = new DataRef (data, queue);
			strongRef = data;
			size = data.getUsedMemoryForPrimitives () + 400;
			if (previous != null)
			{
				previous.next = this;
				this.previous = previous;
				id = previous.id + 1;
				accumulatedSize = previous.accumulatedSize + size;
			}
			else
			{
				id = 0;
				accumulatedSize = size;
			}
		}


		long getByteSizeUpTo (Entry last)
		{
			return size + last.accumulatedSize - accumulatedSize;
		}


		int getEntryCountUpTo (Entry last)
		{
			return 1 + last.id - id;
		}


		public Entry getPrevious ()
		{
			return previous;

		}


		public Entry getNext ()
		{
			return next;
		}


		public Key getKey ()
		{
			return key;
		}


		public Data getData ()
		{
			return softRef.get ();
		}


		void clear ()
		{
			key = null;
			softRef.clear ();
			softRef = null;
			strongRef = null;
			next = null;
			previous = null;
		}
	}


	public LogStore (boolean keepAll)
	{
		this.keepAll = keepAll;
	}


	private void addImpl (Key key, Data data)
	{
		Entry e = new Entry (key, data, queue, last);
		if (first == null)
		{
			first = e;
			firstStrong = e;
		}
		last = e;
		if (map.put (key, e) != null)
		{
			throw new AssertionError ();
		}
	}


	private void updateRefs ()
	{
		if (!keepAll)
		{
			for (Entry e = firstStrong; ; e = e.getNext ())
			{
				long bytes = e.getByteSizeUpTo (last);
				if ((e == last) || (bytes < maxBytes))
				{
					// now e is the first entry which is to be kept
					// for the moment. Clear all strong references
					// up to but excluding e
					while (firstStrong != e)
					{
						firstStrong.strongRef = null;
						firstStrong = firstStrong.getNext ();
					}
					break;
				}
			}

			DataRef data;
			while ((data = (DataRef) queue.poll ()) != null)
			{
				Entry e = map.get (data.key);
				if (e != null)
				{
					first = e.getNext ();
					first.previous = null;
					
					while (e != null)
					{
						map.remove (e.key);
						Entry f = e.getPrevious ();
						e.clear ();
						e = f;
					}
				}
			}

			if (firstStrong != null)
			{
				Entry last = firstStrong.getPrevious ();
				if (last != null)
				{
					for (Entry e = first; ; e = e.getNext ())
					{
						long bytes = e.getByteSizeUpTo (last);
						if ((e == last) || (bytes < maxSoftBytes))
						{
							// now e is the first entry which is to be kept
							// softly for the moment. Clear all soft references
							// up to but excluding e
							while (first != e)
							{
								map.remove (first.key);
								Entry f = first.getNext ();
								first.clear ();
								first = f;
							}
							first.previous = null;
							break;
						}
					}
					
				}
			}
		}
	}


	public void add (Key key, Data data)
	{
		addImpl (key, data);
		updateRefs ();
	}


	public void removeLast (int count)
	{
		while ((--count >= 0) && (last != null))
		{
			if (last == first)
			{
				clear ();
				return;
			}
			else
			{
				map.remove(last.getKey());
				last = last.previous;
				last.next = null;
			}
		}
	}


	public Data get (Key key)
	{
		Entry e = map.get (key);
		return (e == null) ? null : e.getData ();
	}


	public Entry getFirstEntry ()
	{
		if (keepAll)
		{
			return first;
		}
		throw new IllegalStateException
			("getFirstEntry() invoked, keepAll == false");
	}


	public Entry getLastEntry ()
	{
		return last;
	}


	public void clear ()
	{
		Entry f;
		for (Entry e = first; e != null; e = f)
		{
			f = e.getNext ();
			e.clear ();
		}
		map.clear ();
		first = null;
		firstStrong = null;
		last = null;
	}

/*
	public void add (LogStore source)
	{
		for (Entry e = source.getFirstEntry (); e != null; e = e.getNext ())
		{
			Data d = e.getData ();
			if (d != null)
			{
				addImpl (e.getKey (), d);
			}
		}
		updateRefs ();
	}
*/
}
