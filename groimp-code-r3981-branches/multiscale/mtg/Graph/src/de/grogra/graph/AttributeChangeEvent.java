
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

public class AttributeChangeEvent extends java.util.EventObject
{
	Object object;
	boolean node;
	Attribute attr;
	FieldChain field;
	int[] indices;
	Attribute[] dependent;

	final GraphState state;

	
	AttributeChangeEvent (GraphState gs)
	{
		super (gs.getGraph ());
		this.state = gs;
	}
	
	
	public GraphState getGraphState ()
	{
		return state;
	}
	
	
	public Object getObject ()
	{
		return object;
	}

	
	public boolean isNode ()
	{
		return node;
	}

	
	public Attribute getAttribute ()
	{
		return attr;
	}

	
	public FieldChain getSubField ()
	{
		return field;
	}

	
	public int[] getIndices ()
	{
		return indices;
	}

	
	public Attribute[] getDependentAttributes ()
	{
		return dependent;
	}
	
	
	@Override
	public String toString ()
	{
		return "AttributeChangeEvent[" + object + ',' + attr + ',' + field + ']';  
	}

}
