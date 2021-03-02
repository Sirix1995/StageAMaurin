
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
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

public final class NodeData extends Entry
{
	public Object node;
	public boolean context;
	public int foldingId;

	//yong 15 mar 2012 - scaling
	ObjectList macroNodes;
	ObjectList microNodes;
	int lastMicro;
	IntList microScalesIndices;
	//yong 15 mar 2012 - end

	public void setNode (Object node)
	{
		this.node = node;
		hashCode = (node != null) ? node.hashCode () : 0;
		context = true;
		lastMicro=-1;
		microScalesIndices = new IntList();
	}

	//yong 15 mar 2012 - scaling
	public void addMacroNode(Object node)
	{
		if(macroNodes==null)
			macroNodes = new ObjectList();
		
		macroNodes.add(node);
	}
	
	public void addMicroNode(Object node)
	{
		if(microNodes==null)
			microNodes = new ObjectList();
		
		microNodes.add(node);
		lastMicro++;
	}
	//yong 15 mar 2012 - end
	
	//yong 20 mar 2012 - scaling - retrieve by producer
	public ObjectList getMacroNodes()
	{
		return macroNodes;
	}
	
	public ObjectList getMicroNodes()
	{
		return microNodes;
	}
	//yong 20 mar 2012 - end

	@Override
	protected void clear ()
	{
		node = null;
		
		//yong 27 mar 2012
		if(microNodes!=null)
			microNodes.clear();
		if(macroNodes!=null)
			macroNodes.clear();
		if(microScalesIndices!=null)
			microScalesIndices.clear();
		//yong 27 mar 2012 - end
	}
	

	@Override
	protected void copyValue (Entry e)
	{
		NodeData n = (NodeData) e;
		context = n.context;
		
		//yong 27 mar 2012
		if(microNodes==null)
			microNodes = new ObjectList();
		microNodes.clear();
		microNodes.addAll(((NodeData)e).getMicroNodes());
		
		if(macroNodes==null)
			macroNodes = new ObjectList();
		macroNodes.clear();
		macroNodes.addAll(((NodeData)e).getMacroNodes());
		
		if(microScalesIndices==null)
			microScalesIndices = new IntList();
		microScalesIndices.clear();
		microScalesIndices.addAll(((NodeData)e).getMicroScaleIndices());
		//yong 27 mar 2012 - end
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
	
	public int getLastMicro()
	{
		return lastMicro;
	}

	public void addMicroScaleIndex()
	{
		microScalesIndices.add(lastMicro);
	}
	
	public IntList getMicroScaleIndices()
	{
		return microScalesIndices;
	}
	
}
