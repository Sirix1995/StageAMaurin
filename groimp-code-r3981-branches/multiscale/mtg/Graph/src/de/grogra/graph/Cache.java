
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

package de.grogra.graph;

import java.util.WeakHashMap;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.EHashMap.ObjectEntry;

public abstract class Cache
{
	private static final class HashEntry extends ObjectEntry<ContextDependent,Info>
	{
		private Object strategy;

		void setKey (ContextDependent c, Object s)
		{
			super.setKey (c);
			strategy = s;
			if (s != null)
			{
				hashCode ^= s.hashCode ();
			}
		}

		@Override
		protected boolean keyEquals (EHashMap.Entry e)
		{
			if (!super.keyEquals (e))
			{
				return false;
			}
			return (strategy == ((HashEntry) e).strategy)
				|| ((strategy != null) && strategy.equals (((HashEntry) e).strategy));
		}

	
		@Override
		protected void clear ()
		{
			super.clear ();
			strategy = null;
		}
	}

	public abstract static class Entry
	{
		private final GraphState gs;

		private final Object object;
		private final boolean asNode;
		private final ContextDependent dependent;
		private final Object strategy;

		private final FloatList data = new FloatList ();

		private int index;
		private boolean dirty = true;
		private int stamp;

		private Object value;


		public Entry (Object object, boolean asNode, ContextDependent dependent,
					  Object strategy, GraphState gs)
		{
			this.object = object;
			this.asNode = asNode;
			this.dependent = dependent;
			this.strategy = strategy;
			this.gs = gs;
		}


		public final void write (int value)
		{
			write ((float) (char) value);
			write ((float) (value >>> 16));
		}


		public final void write (float value)
		{
			if (dirty)
			{
				data.set (index++, value);
			}
			else if (data.set (index++, value) != value)
			{
				dirty = true;
			}
		}


		public final void write (float[] value)
		{
			write (value, 0, value.length);
		}


		public final void write (float[] value, int begin, int length)
		{
			int s = index + length;
			if (data.size < s)
			{
				data.setSize (s);
				dirty = true;
			}
			length += begin;
			while ((begin < length) && !dirty)
			{
				float v;
				if (data.set (index++, v = value[begin++]) != v)
				{
					dirty = true;
				}
			}
			if (dirty)
			{
				System.arraycopy (value, begin, data.elements, index, length - begin);
				index = s;
			}
		}


		final Object getValue ()
		{
			int s = gs.getGraph ().getStamp ();
			if (!dirty && (s == stamp))
			{
				return value;
			}
			stamp = s;
			gs.setObjectContext (object, asNode);
			index = 0;
			int sizeBefore = data.size;
			dependent.writeStamp (this, gs);
			if (sizeBefore != index)
			{
				dirty = true;
				data.setSize (index);
			}
			if (dirty)
			{
				value = computeValue (value, dependent, strategy, gs);
				dirty = false;
			}
			return value;
		}


		protected abstract Object computeValue
			(Object oldValue, ContextDependent object, Object strategy, GraphState gs);
	}


	protected final GraphState gs;

	private final EHashMap<HashEntry> infos = new EHashMap<HashEntry> ();


	private static final class Info
	{
		int stamp;
		Object object;
		boolean asNode;
		Entry entry;
		WeakHashMap<Object,Entry> nodeEntries;
		WeakHashMap<Object,Entry> edgeEntries;
	}


	public Cache (GraphState gs)
	{
		this.gs = gs;
	}


	public void clearUnused ()
	{
		int s = gs.getGraph ().getStamp ();
		HashEntry f;
		for (HashEntry e = infos.getFirstEntry (); e != null; e = f)
		{
			f = (HashEntry) e.listNext;
			if (e.value.stamp != s)
			{
				infos.remove (e);
			}
		}
	}


	public void clear ()
	{
		infos.clear ();	
	}

	
	public GraphState getGraphState ()
	{
		return gs;
	}


	public Object getValue (Object object, boolean asNode,
							ContextDependent dependent, Object strategy)
	{
		HashEntry e = infos.popEntryFromPool ();
		if (e == null)
		{
			e = new HashEntry ();
		}
		e.setKey (dependent, strategy);
		HashEntry old = infos.getOrPut (e);
		Info i;
		if (old != null)
		{
			i = old.value;
		}
		else
		{
			i = new Info ();
			e.value = i;
		}
		Entry c;
		if (dependent.dependsOnContext ())
		{
			if (i.object == null)
			{
				i.object = object;
				i.asNode = asNode;
				i.entry = c = createEntry (object, asNode, dependent, strategy);
			}
			else if ((i.object == object) && (i.asNode == asNode))
			{
				c = i.entry;
			}
			else
			{
				WeakHashMap<Object,Entry> h = asNode ? i.nodeEntries : i.edgeEntries;
				if (h == null)
				{
					c = null;
					h = new WeakHashMap<Object,Entry> ();
					if (asNode)
					{
						i.nodeEntries = h;
					}
					else
					{
						i.edgeEntries = h;
					}
				}
				else
				{
					c = h.get (object);
				}
				if (c == null) 
				{
					h.put (object, c = createEntry (object, asNode, dependent, strategy));
				}
			}
		}
		else
		{
			if (i.object != null)
			{
				i.object = null;
				i.entry = null;
			}
			if (i.nodeEntries != null)
			{
				i.nodeEntries.clear ();
				i.nodeEntries = null;
			}
			if (i.edgeEntries != null)
			{
				i.edgeEntries.clear ();
				i.edgeEntries = null;
			}
			if ((c = i.entry) == null)
			{
				i.entry = c = createEntry (null, false, dependent, strategy);
			}
		}
		i.stamp = gs.getGraph ().getStamp ();
		return c.getValue ();
	}


	protected abstract Entry createEntry (Object object, boolean asNode,
										  ContextDependent dependent, Object strategy);
}
