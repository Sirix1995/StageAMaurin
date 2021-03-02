
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

package de.grogra.graph;

import de.grogra.reflect.*;
import de.grogra.util.Quantity;
import de.grogra.xl.util.ObjectList;

public abstract class ObjectTreeAttribute<T> extends ObjectAttribute<T>
{
	static final class AttributeState
	{
		final ObjectMap<ObjectTreeAttribute.Listener> listeners;

		private boolean queueEvents = false;
		private ObjectList<Object> pendingEvents = new ObjectList<Object> ();
		private ObjectList<Object> pendingEvents2 = new ObjectList<Object> ();

		AttributeState (GraphState gs)
		{
			listeners = gs.getGraph ().createObjectMap ();
		}

		void fire (GraphState gs, Object o, ObjectTreeAttribute.Listener l)
		{
			pendingEvents.push (l).push (o);
			if (!queueEvents)
			{
				queueEvents = true;
				try
				{
					while (!pendingEvents.isEmpty ())
					{
						ObjectList<Object> queue = pendingEvents;
						pendingEvents = pendingEvents2;
						pendingEvents2 = queue;
						for (int i = 0; i < queue.size; i += 2)
						{
							l = (ObjectTreeAttribute.Listener) queue.get (i);
							gs.fireAttributeChanged (queue.get (i + 1), l.objIsNode, l.getAttribute (), null, null);
						}
						queue.clear ();
					}
				}
				finally
				{
					queueEvents = false;
					pendingEvents.clear ();
					pendingEvents2.clear ();
				}
			}
		}

	}

	final class Listener implements AttributeChangeListener
	{
		Object parent;
		Object obj;
		boolean objIsNode;
		T value;

		ObjectTreeAttribute getAttribute ()
		{
			return ObjectTreeAttribute.this;
		}

		public void attributeChanged (AttributeChangeEvent e)
		{
			if (obj == null)
			{
				if ((parent != null) && ((e.object != parent) || (e.node == objIsNode))
					&& getParentAttribute (e.state).isContained (e.dependent))
				{
					e.state.getGraph ().removeAttributeChangeListener (parent, !objIsNode, this);
					parent = null;
				}
			}
			else
			{
				if ((obj == e.object) && (objIsNode == e.node))
				{
					if (getParentAttribute (e.state).isContained (e.dependent))
					{
						assert parent != null;
						e.state.getGraph ().removeAttributeChangeListener (parent, !objIsNode, this);
						parent = null;
					}
					else if (!dependsOn (e.dependent))
					{
						return;
					}
				}
				else if (!isContained (e.dependent))
				{
					return;
				}
				Object o = obj;
				obj = null;
				getAttrState (e.state).fire (e.state, o, this);
			}
		}
	}



	public ObjectTreeAttribute (Type type, boolean convert, Quantity quantity)
	{
		super (type, convert, quantity);
	}


	public ObjectTreeAttribute (Class<T> type, boolean convert, Quantity quantity)
	{
		super (type, convert, quantity);
	}



	protected ObjectAttribute getParentAttribute (GraphState gs)
	{
		return gs.getGraph ().getParentAttribute ();
	}


	synchronized AttributeState getAttrState (GraphState gs)
	{
		AttributeState s;
		if ((s = (AttributeState) getAttributeState (gs)) == null)
		{
			s = new AttributeState (gs);
			setAttributeState (gs, s);
		}
		return s;
	}

	@Override
	protected T getDerived (Object object, boolean asNode,
								T placeIn,
								GraphState gs)
	{
		int instanceIndex = gs.getInstancingPathIndex ();
		if (instanceIndex > 0)
		{
			placeIn = (T) gs.getInstancingTreeAttribute (this, instanceIndex);
			if (gs.wasTreeAttributeValid ())
			{
				return placeIn;
			}
			gs.moveToPreviousInstance ();
			Object p = gs.getInstancingPath ().getObject (instanceIndex - 1);
			T v = getDerived (p, !asNode,
								  null,
								  gs);
			gs.moveToNextInstance ();
			v = derive (object, asNode, v, placeIn, gs);
			gs.setInstancingTreeAttribute (this, instanceIndex, v);
			return v;
		}
		AttributeState as = getAttrState (gs);
		ObjectList stack = gs.treeAttributeStack;
		int stackStart = stack.size;
		try
		{
			synchronized (as)
			{
				T v;
				while (true)
				{
					Listener w;
					if ((w = as.listeners.getObject (object, asNode)) == null)
					{
						w = new Listener ();
						as.listeners.putObject (object, asNode, w);
						gs.getGraph ().addAttributeChangeListener (object, asNode, w);
					}
					if (w.obj != null)
					{
						v = w.value;
						break;
					}
					w.obj = object;
					w.objIsNode = asNode;
					stack.push (w);
					object = getParentAttribute (gs).getDerived (object, asNode, null, gs);
					asNode = !asNode;
					if (object == null)
					{
						v = getInitialValue (gs);
						assert w.parent == null;
						break;
					}
					else if (w.parent != object)
					{
						assert w.parent == null;
						w.parent = object;
						gs.getGraph ().addAttributeChangeListener (object, asNode, w);
					}
				}
				while (stack.size > stackStart)
				{
					Listener w = (Listener) stack.pop ();
					w.value = v = derive (w.obj, w.objIsNode, v,
											  w.value,
											  gs);
				}
				return v;
			}
		}
		finally
		{
			stack.setSize (stackStart);
		}
	}



	protected abstract T derive (Object object, boolean asNode, T parentValue,
									 T placeIn,
									 GraphState gs);


	protected abstract T getInitialValue (GraphState gs);


	@Override
	public boolean isDerived ()
	{
		return true;
	}


	public abstract boolean dependsOn (Attribute[] b);


	public T getParentValue (Object object, boolean asNode,
								 T placeIn,
								 GraphState gs)
	{
		int i = gs.getInstancingPathIndex ();
		if (i > 0)
		{
			gs.moveToPreviousInstance ();
			T v = getDerived (gs.getInstancingPath ().getObject (i - 1), !asNode,
								  placeIn,
								  gs);
			gs.moveToNextInstance ();
			return v;
		}
		else
		{
			object = getParentAttribute (gs).getDerived (object, asNode, null, gs);
			return (object == null) ? getInitialValue (gs)
				: getDerived (object, !asNode,
							  placeIn,
							  gs);
		}
	}
}

