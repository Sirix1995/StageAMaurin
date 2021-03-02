
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

package de.grogra.xl.query;

import de.grogra.xl.util.EHashMap.Entry;

public final class NodeData extends Entry
{
	public Object node;
	public boolean context;
	public int foldingId;


	public void setNode (Object node)
	{
		this.node = node;
		hashCode = (node != null) ? node.hashCode () : 0;
		context = true;
	}


	@Override
	protected void clear ()
	{
		node = null;
	}
	

	@Override
	protected void copyValue (Entry e)
	{
		NodeData n = (NodeData) e;
		context = n.context;
	}
	
	
	@Override
	protected boolean keyEquals (Entry e)
	{
		NodeData n = (NodeData) e;
		return (node == n.node) || ((node != null) && node.equals (n.node));
	}
	
	
	@Override
	public String toString ()
	{
		return "NodeData@" + Integer.toHexString (hashCode ())
			+ '[' + node + ',' + context + ',' + foldingId + ']';
	}

}
