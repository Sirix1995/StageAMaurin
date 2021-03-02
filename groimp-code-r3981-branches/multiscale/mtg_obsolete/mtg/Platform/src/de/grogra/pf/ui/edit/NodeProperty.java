
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

import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;

public class NodeProperty extends FieldProperty
{
	
	NodeProperty (Context context, Node object, PersistenceField field,
				  int[] indices, int index)
	{
		super (context, object, field, indices, index);
	}
	
	
	public NodeProperty (Context context, Node object, PersistenceField field,
						 int[] indices)
	{
		this (context, object, field, indices, -1);
	}
	
	
	@Override
	public void setValue (Object value)
	{
		UI.executeLockedly
			(((Node) object).getPersistenceManager (), true,
			 new Command ()
			 {
				public String getCommandName ()
				{
					return null;
				}
				
				public void run (Object arg, Context ctx)
				{
					field.set ((Node) object, indices, arg, ((Node) object).getGraph ().getActiveTransaction ());
				}
			 }, value, getContext (), JobManager.ACTION_FLAGS);
	}

	
	@Override
	public FieldProperty createSubProperty (PersistenceField f, int i)
	{
		return new NodeProperty
			(getContext (), (Node) object, f, addIndex (i), -1);
	}
	
}
