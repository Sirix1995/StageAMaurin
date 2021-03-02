
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

package de.grogra.pf.ui.edit;

import de.grogra.util.*;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.*;
import de.grogra.persistence.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;

public class NodeSelection extends FieldSelection implements GraphSelection
{
	final boolean invokeValueForPathChanged;


	class Tree extends PropertyEditorTree implements XAListener
	{
		Tree (Context c)
		{
			super (c);
			((Node) root).setDecription (NodeSelection.this);
		}
		
		@Override
		protected void firstListenerAdded ()
		{
			((de.grogra.graph.impl.Node) object).getPersistenceManager ().addXAListener (this);
		}

		@Override
		protected void allListenersRemoved ()
		{
			((de.grogra.graph.impl.Node) object).getPersistenceManager ().removeXAListener (this);
		}
		
		public void transactionApplied (Transaction.Data xa, boolean rollback)
		{
			fireChanged (xa);
			if (invokeValueForPathChanged)
			{
				valueForPathChanged (root.getTreePath (), null);
			}
		}

		@Override
		protected boolean isNodeAffectedBy (PropertyNode node, Object changeEvent)
		{
			return true;
		}
		
	}


	public NodeSelection (Context ctx, Node object,
						  PersistenceField[] fields, int[][] indices,
						  PropertyEditor[] editors,
						  String[] labels, Item description,
						  boolean invokeValueForPathChanged)
	{
		super (ctx, object, fields, indices, editors, labels, description);
		this.invokeValueForPathChanged = invokeValueForPathChanged;
	}


	@Override
	public Object getDescription (String type)
	{
		Object d = super.getDescription (type);
		if (d != null)
		{
			return d;
		}
		else if (Utils.isStringDescription (type))
		{
			String n = (String) ((Node) object).getGraph ()
				.getDescription (object, true, type);
			return (n == null) ? UI.getClassDescription (object.getClass ())
				: UI.getClassDescription (object.getClass ()) + " [" + n + ']';
		}
		else
		{
			return ((Node) object).getGraph ().getDescription (object, true, type);
		}
	}


	@Override
	protected PropertyEditorTree createTree ()
	{
		return new Tree (context);
	}
	
	
	@Override
	protected FieldProperty createProperty (Context c, int i)
	{
		return new NodeProperty (c, (Node) object, fields[i], indices[i], i);
	}

	
	public int size ()
	{
		return 1;
	}
	

	public GraphState getGraphState (int index)
	{
		return ((Node) object).getGraph().getMainState ();
	}


	public Object getObject (int index)
	{
		return object;
	}
	

	public boolean isNode (int index)
	{
		return true;
	}


	public boolean contains (Graph graph, Object object, boolean asNode)
	{
		return asNode && (object == this.object)
			&& (graph == ((Node) this.object).getGraph ());
	}

}
