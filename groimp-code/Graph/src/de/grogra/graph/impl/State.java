
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

package de.grogra.graph.impl;

import de.grogra.reflect.*;
import de.grogra.util.*;
import de.grogra.persistence.*;
import de.grogra.graph.*;

public final class State extends GraphState implements Transaction.Consumer,
	GraphTransaction.Consumer, XAListener
{
	final GraphManager manager;
	private final GraphTransaction.Reader reader;

	State (GraphManager manager, ThreadContext ctx, boolean createStatic)
	{
		super ();
		initialize (manager, ctx);
		this.manager = manager;
		if (createStatic)
		{
			this.reader = null;
		}
		else
		{
			this.reader = (GraphTransaction.Reader)
				new GraphTransaction (manager, null).createReader ();
			manager.addXAListener (new WeakDelegate (this, manager));
		}
	}


	public Transaction getActiveTransaction ()
	{
		return manager.getActiveTransaction ();
	}

	
	@Override
	public void fireAttributeChanged
		(Object object, boolean asNode, Attribute a, FieldChain field,
		 int[] indices)
	{
		manager.support.fireAttributeChanged
			(object, asNode, a, field, indices, this);
	}

	
	@Override
	protected void fireEdgeChanged (Object source, Object target, Object edge)
	{
		manager.support.fireEdgeChanged (source, target, edge, this);
	}

	
	@Override
	public boolean containsInTree (Object object, boolean asNode)
	{
		return ((ParentAttribute) manager.getParentAttribute ()).contains (object, asNode, this);
	}


	public void setField (PersistenceCapable o, PersistenceField field,
						  int[] indices, Transaction.Reader reader)
	{
		Field f = field.getField (0);
		if (f instanceof Node.NType.Field)
		{
			fireAttributeChanged (o, true, ((Node.NType.Field) f).getDependentAttribute (),
								  (field.length () == 1) ? null
								  : ((IndirectField) field).getShallowSubchain (1),
								  indices);
		}
	}


	public void insertComponent (PersistenceCapable o, PersistenceField field,
								 int[] indices, Transaction.Reader reader)
	{
		setField (o, field, indices, reader);
	}


	public void removeComponent (PersistenceCapable o, PersistenceField field,
								 int[] indices, Transaction.Reader reader)
	{
		setField (o, field, indices, reader);
	}


	public void makePersistent (long id, ManageableType type)
	{
	}


	public void makeTransient (PersistenceCapable o, ManageableType type)
	{
	}


	public void readData (PersistenceCapable o, Transaction.Reader reader)
	{
	}


	public void begin ()
	{
	}


	public void end ()
	{
	}


	public void addEdgeBits (Node source, Node target, int mask)
	{
		manager.support.fireEdgeChanged (source, target, source.getEdgeTo (target), this);
	}


	public void removeEdgeBits (Node source, Node target, int mask)
	{
		manager.support.fireEdgeChanged (source, target, source.getEdgeTo (target), this);
	}


	public void transactionApplied (final Transaction.Data xa,
									final boolean rollback)
	{
		manager.support.fireBeginChange (this);
		try
		{
			reader.getQueue ().restore (xa);
			reader.resetCursor ();
			try
			{
				if (rollback)
				{
					reader.supplyInverse (this);
				}
				else
				{
					reader.supply (this);
				}
			}
			catch (java.io.IOException e)
			{
				throw new FatalPersistenceException (e);
			}
		}
		finally
		{
			manager.support.fireEndChange (this);
		}
	}


	@Override
	public GraphState createDelegate (ThreadContext tc)
	{
		return new DelegateGraphState (this, tc)
		{
			@Override
			public boolean containsInTree (Object object, boolean asNode)
			{
				return ((ParentAttribute) manager.getParentAttribute ()).contains (object, asNode, this);

			}
		};
	}


	@Override
	public void setEdgeBits (Object edge, int bits)
	{
		((Edge) edge).setEdgeBits (bits, manager.getActiveTransaction ());
	}
	
}
