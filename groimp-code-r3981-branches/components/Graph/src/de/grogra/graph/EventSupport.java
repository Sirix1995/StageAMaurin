
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

import java.util.Comparator;

import de.grogra.reflect.FieldChain;
import de.grogra.xl.util.ObjectList;

/**
 * An instance of <code>EventSupport</code> can be used in implementations
 * of {@link de.grogra.graph.Graph} for the management of event listeners
 * and for dispatching of events.
 * 
 * @author Ole Kniemeyer
 */
public class EventSupport
{
	static final Integer ACL = 0;
	static final Integer ECL = 1;
	static final Integer CBL = 2;


	static Comparator CBL_COMPARATOR = new Comparator ()
	{
		@Override
		public int compare (Object a, Object b)
		{
			return ((ChangeBoundaryListener) a).getPriority ()
				- ((ChangeBoundaryListener) b).getPriority ();
		}
	};


	static Object[] addListener (Integer type, Object[] a, Object listener, java.util.Comparator c)
	{
		int free;
		if (a != null)
		{
			free = -1;
			int i = a.length;
			while (i > 0)
			{
				if ((a[--i] == listener) & (a[--i] == type))
				{
					return a;
				}
				if (a[i] == null)
				{
					free = i;
				}
			}
			if (free < 0)
			{
				System.arraycopy (a, 0,
								  a = new Object[(free = a.length) + 4], 0,
								  free);
			}
		}
		else
		{
			a = new Object[4];
			free = 0;
		}
		a[free] = type;
		a[free + 1] = listener;
		if (c != null)
		{
			int i = 0;
			while (i < a.length)
			{
				if (a[i] == type)
				{
					if ((a[++i] != listener)
						&& (c.compare (listener, a[i]) < 0))
					{
						i--;
						break;
					}
					i++;
				}
				else
				{
					i += 2;
				}
			}
			if (i < free)
			{
				while (i <= free)
				{
					if (a[i] == type)
					{
						Object l = a[i + 1];
						a[i + 1] = listener;
						listener = l;
					}
					i += 2;
				}
			}
			else
			{
				i -= 2;
				while (i >= free)
				{
					if (a[i] == type)
					{
						Object l = a[i + 1];
						a[i + 1] = listener;
						listener = l;
					}
					i -= 2;
				}
			}
		}
		return a;
	}


	static Object[] removeListener (Integer type, Object[] a, Object listener)
	{
		if (a != null)
		{
			int i = a.length, m = i - 2;
			while (i > 0)
			{
				if (a[--i] == listener)
				{
					if (a[--i] == type)
					{
						if ((i == 0) && ((m == 0) || (a[2] == null)))
						{
							return null;
						}
						if (i < m)
						{
							System.arraycopy (a, i + 2, a, i, m - i);
						}
						a[m] = null;
						a[m + 1] = null;
						return a;
					}
				}
				else
				{
					--i;
				}
			}
		}
		return a;
	}


	public class Queue extends ObjectList
	{
		
		public void postAttributeChanged
			(Object object, boolean asNode, Attribute a, FieldChain field,
			 int[] indices)
		{
			int n = size;
			setSize (n + 5);
			elements[n] = asNode ? (Object) ACL : this;
			elements[n + 1] = object;
			elements[n + 2] = a;
			elements[n + 3] = field;
			elements[n + 4] = ((indices == null) || (indices.length == 0))
				? indices : indices.clone ();
		}
	

		public void postEdgeChanged
			(Object source, Object target, Object edgeSet)
		{
			int n = size;
			setSize (n + 3);
			elements[n] = source;
			elements[n + 1] = target;
			elements[n + 2] = edgeSet;
		}

		
		private GraphState clearIfProcessed (GraphState gs, int i)
		{
			if (i == size)
			{
				clear ();
			}
			return gs;
		}
		
		public void fire (GraphState gs, boolean fireBeginEnd)
		{
			if (size == 0)
			{
				return;
			}
			if (fireBeginEnd)
			{
				fireBeginChange (gs);
			}
			int i = 0;
			while (i < size)
			{
				Object o = elements[i];
				boolean b;
				if ((b = (o == ACL)) || (o == this))
				{
					fireAttributeChanged
						(elements[i + 1], b, (Attribute) elements[i + 2],
						 (FieldChain) elements[i + 3], (int[]) elements[i + 4], clearIfProcessed (gs, i += 5));
				}
				else
				{
					fireEdgeChanged (elements[i], elements[i + 1], elements[i + 2], clearIfProcessed (gs, i += 3));
				}
			}
			clear ();
			if (fireBeginEnd)
			{
				fireEndChange (gs);
			}
		}
	}


	Object[] listeners = null;
	ObjectMap objectListeners;

	final ObjectList listenersCopy = new ObjectList ();

	
	public EventSupport (ObjectMap objectListeners)
	{
		this.objectListeners = objectListeners;
	}


	@Override
	protected void finalize ()
	{
		listeners = null;
		if (objectListeners != null)
		{
			objectListeners.dispose ();
		}
	}


	public synchronized void addChangeBoundaryListener (ChangeBoundaryListener l)
	{
		listeners = addListener (CBL, listeners, l, CBL_COMPARATOR);
	}


	public synchronized void removeChangeBoundaryListener (ChangeBoundaryListener l)
	{
		listeners = removeListener (CBL, listeners, l);
	}


	public synchronized void addAttributeChangeListener (AttributeChangeListener l)
	{
		listeners = addListener (ACL, listeners, l, null);
	}


	public synchronized void addEdgeChangeListener (EdgeChangeListener l)
	{
		listeners = addListener (ECL, listeners, l, null);
	}


	public synchronized void removeAttributeChangeListener (AttributeChangeListener l)
	{
		listeners = removeListener (ACL, listeners, l);
	}


	public synchronized void removeEdgeChangeListener (EdgeChangeListener l)
	{
		listeners = removeListener (ECL, listeners, l);
	}


	synchronized int copyListeners (Integer type, Object[] a)
	{
		int n = listenersCopy.size;
		if (a == null)
		{
			return n;
		}
		int i = a.length;
		while (i > 0)
		{
			Object l = a[--i];
			if (a[--i] == type)
			{
				listenersCopy.add (l);
			}
		}
		return n;
	}


	public void fireBeginChange (GraphState gs)
	{
		int n = copyListeners (CBL, listeners);
		try
		{
			while (listenersCopy.size > n)
			{
				((ChangeBoundaryListener) listenersCopy.pop ()).beginChange (gs);
			}
		}
		finally
		{
			listenersCopy.setSize (n);
		}
	}


	public void fireEndChange (GraphState gs)
	{		
		int n = copyListeners (CBL, listeners);
		try
		{
			while (listenersCopy.size > n)
			{
				((ChangeBoundaryListener) listenersCopy.pop ()).endChange (gs);
			}
		}
		finally
		{
			listenersCopy.setSize (n);
		}
	}


	private void fireAttributeChanged (Integer type, Object[] listeners,
									   AttributeChangeEvent e)
	{		
		if (listeners == null)
		{
			return;
		}
		int n = copyListeners (type, listeners);
		try
		{
			while (listenersCopy.size > n)
			{
				((AttributeChangeListener) listenersCopy.pop ()).attributeChanged (e);
			}
		}
		finally
		{
			listenersCopy.setSize (n);
		}
	}


	public void fireEdgeChanged (Integer type, Object[] listeners,
								 Object source, Object target,
								 Object edgeSet, GraphState gs)
	{
		if (listeners == null)
		{
			return;
		}
		int n = copyListeners (type, listeners);
		try
		{
			while (listenersCopy.size > n)
			{
				((EdgeChangeListener) listenersCopy.pop ())
					.edgeChanged (source, target, edgeSet, gs);
			}
		}
		finally
		{
			listenersCopy.setSize (n);
		}
	}


	public void fireEdgeChanged (Object source, Object target, Object edge,
								 GraphState gs)
	{
		Object[] s, t, e;
		synchronized (this)
		{
			s = (Object[]) objectListeners.getObject (source, true);
			t = (Object[]) objectListeners.getObject (target, true);
			e = (edge == null) ? null : (Object[]) objectListeners.getObject (edge, false);
		}
		if (e != null)
		{
			fireEdgeChanged (ECL, e, source, target, edge, gs);
		}
		if (s != null)
		{
			fireEdgeChanged (ECL, s, source, target, edge, gs);
		}
		if (t != null)
		{
			fireEdgeChanged (ECL, t, source, target, edge, gs);
		}
		if ((s = listeners) != null)
		{
			fireEdgeChanged (ECL, s, source, target, edge, gs);
		}
	}


	private final ObjectList eventPool = new ObjectList ();

	public void fireAttributeChanged (Object object, boolean asNode,
									  Attribute a, FieldChain field, int[] indices,
									  GraphState gs)
	{
		Object[] l;
		synchronized (this)
		{
			l = (Object[]) objectListeners.getObject (object, asNode);
		}
		AttributeChangeEvent e = null;
		if (l != null)
		{
			e = eventPool.isEmpty () ? new AttributeChangeEvent (gs)
				: (AttributeChangeEvent) eventPool.pop ();
			e.object = object;
			e.node = asNode;
			e.attr = a;
			e.field = field;
			e.indices = indices;
			e.dependent = (a != null) ? gs.getGraph ().getDependent (object, asNode, a) : null;
			fireAttributeChanged (ACL, l, e);
		}
		if ((l = listeners) != null)
		{
			if (e == null)
			{
				e = eventPool.isEmpty () ? new AttributeChangeEvent (gs)
					: (AttributeChangeEvent) eventPool.pop ();
				e.object = object;
				e.node = asNode;
				e.attr = a;
				e.field = field;
				e.indices = indices;
				e.dependent = (a != null) ? gs.getGraph ().getDependent (object, asNode, a) : null;
			}
			fireAttributeChanged (ACL, l, e);
		}
		if (e != null)
		{
			eventPool.push (e);
		}
	}


	private synchronized void addListener (Object object, boolean asNode, Integer type, Object l)
	{
		Object[] a = (Object[]) objectListeners.getObject (object, asNode);
		l = addListener (type, a, l, null);
		if (a != l)
		{
			objectListeners.putObject (object, asNode, l);
		}
	}

	
	private synchronized void removeListener (Object object, boolean asNode, Integer type, Object l)
	{
		Object[] a = (Object[]) objectListeners.getObject (object, asNode);
		l = removeListener (type, a, l);
		if (a != l)
		{
			objectListeners.putObject (object, asNode, l);
		}
	}


	public void addAttributeChangeListener (Object object, boolean asNode, AttributeChangeListener l)
	{
		addListener (object, asNode, ACL, l);
	}


	public void removeAttributeChangeListener (Object object, boolean asNode, AttributeChangeListener l)
	{
		removeListener (object, asNode, ACL, l);
	}


	public void addEdgeChangeListener (Object object, boolean asNode, EdgeChangeListener l)
	{
		addListener (object, asNode, ECL, l);
	}


	public void removeEdgeChangeListener (Object object, boolean asNode, EdgeChangeListener l)
	{
		removeListener (object, asNode, ECL, l);
	}

}
