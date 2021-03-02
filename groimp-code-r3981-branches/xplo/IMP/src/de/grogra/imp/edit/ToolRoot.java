
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

package de.grogra.imp.edit;

import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.event.EditEvent;

/**
 * Instances of this class are used as root nodes of tool graphs for
 * the interactive manipulation of objects in two- or three-dimensional
 * views. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class ToolRoot extends Node implements Disposable 
{
	/**
	 * The tool to which this tool node is associated.
	 */
	protected Tool tool;


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (ToolRoot.class);
		$TYPE.validate ();
	}

//enh:end

	/**
	 * Initializes this tool root. This method initializes
	 * the field {@link #tool} with the given <code>tool</code>.
	 * 
	 * @param tool the tool to which this tool root is associated
	 * @return <code>true</code> if this tool root actually shall be used
	 * for the tool, <code>false</code> if it shall not be used (e.g., if
	 * the actual tool target cannot be manipulated by the tool graph
	 * of this tool root).
	 */
	protected boolean initialize (Tool tool)
	{
		this.tool = tool;
		return true;
	}


	/**
	 * Returns the target object of the tool.
	 * 
	 * @see Tool#getObject()
	 * @return the target object of the tool
	 */
	public Object getToolTarget ()
	{
		return tool.getObject ();
	}

	
	/**
	 * Returns <code>true</code> iff the target object of the tool is a node.
	 * 
	 * @see Tool#isNode() 
	 * @return is the target object a node?
	 */
	public boolean isTargetNode ()
	{
		return tool.isNode ();
	}
	
	
	/**
	 * Executes the given <code>command</code> in the main thread
	 * of the workbench and with a write-lock on the graph.
	 * The <code>context</code>-argument of the command's <code>run</code>-method
	 * will be the given <code>event</code>, the <code>arg</code>-argument
	 * will be <code>this</code>.
	 * 
	 * @param command a command to be executed
	 * @param event the event, it is used as {@link Context}
	 */
	public void executeWithWriteLock (Command command, EditEvent event)
	{
		UI.executeLockedly
			(tool.getGraphState ().getGraph (), true, command, this,
			 event, JobManager.ACTION_FLAGS);
	}


	/**
	 * This method is invoked to notify the tool root about modifications
	 * of attributes of the tool target.
	 * 
	 * @param b an array of attributes which have changed
	 */
	protected abstract void attributeChanged (Attribute[] b);

}
