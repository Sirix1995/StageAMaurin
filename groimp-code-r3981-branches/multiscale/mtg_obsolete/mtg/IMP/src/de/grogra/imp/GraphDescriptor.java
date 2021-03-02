
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

package de.grogra.imp;

import de.grogra.graph.*;
import de.grogra.persistence.*;

/**
 * A <code>GraphDescriptor</code> represents the persistent information
 * which is needed to obtain a specific {@link de.grogra.graph.Graph}
 * in the context of a {@link de.grogra.imp.View}.
 *
 * @author Ole Kniemeyer
 */
public abstract class GraphDescriptor implements Manageable
{
	//enh:sco SCOType
	private transient int stamp = 0;


	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
	}


	public int getStamp ()
	{
		return stamp;
	}

	
	public Manageable manageableReadResolve ()
	{
		return this;
	}

	
	public Object manageableWriteReplace ()
	{
		return this;
	}

	
	/**
	 * Returns the graph described by this descriptor, given the context
	 * <code>view</code>.
	 * 
	 * @param view the context for the graph
	 * @return graph corresponding to this descriptor
	 */
	public abstract Graph getGraph (View view);

	
	public void substituteSelection (GraphState[] gs, Object[] object,
									 boolean asNode[], int index)
	{
	}


	public Path getPathFor (View view, GraphState gs, Object obj, boolean node)
	{
		if (view.getGraph ().getLifeCycleState (obj, node) != Graph.PERSISTENT)
		{
			return null;
		}
		Path p = GraphUtils.getTreePath (view.getWorkbenchGraphState (), obj, node);
		if ((p == null) || (p.getObject (0) != p.getGraph ().getRoot (Graph.MAIN_GRAPH)))
		{
			return null;
		}
		return p;
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (GraphDescriptor representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}
	}

	static
	{
		$TYPE = new Type (GraphDescriptor.class);
		$TYPE.validate ();
	}

//enh:end

}
