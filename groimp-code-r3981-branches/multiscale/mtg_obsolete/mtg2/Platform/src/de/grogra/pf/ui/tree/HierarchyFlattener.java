
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

package de.grogra.pf.ui.tree;

import javax.swing.tree.*;
import de.grogra.xl.lang.ObjectToBoolean;
import de.grogra.pf.ui.tree.UITreePipeline.Node;

public class HierarchyFlattener implements UITreePipeline.Transformer
{
	private final boolean insertSeparator;
	private final ObjectToBoolean filter;


	public HierarchyFlattener (ObjectToBoolean filter, boolean insertSeparator)
	{
		this.filter = filter;
		this.insertSeparator = insertSeparator;
	}


	public HierarchyFlattener ()
	{
		this (null, true);
	}


	public void initialize (UITreePipeline pipeline)
	{	
	}


	public void dispose ()
	{	
	}


	public void transform (Node root)
	{
		Node nn;
		boolean sep = true;
		for (Node n = (Node) root.children; n != null; n = nn)
		{
			nn = (Node) n.next;
			transform (n);
			if (n.getType () == UINodeHandler.NT_SEPARATOR)
			{
				if (sep)
				{
					n.remove ();
					n.dispose ();
				}
				else
				{
					sep = true;
				}
			}
			else if (isLeaf (n))
			{
				if (hasContent (n)
					&& ((filter == null) || filter.evaluateBoolean (n.getNode ())))
				{
					sep = n.getType () == UINodeHandler.NT_FILL;
				}
				else
				{
					n.remove (); 
					n.dispose ();
				}
			}
			else if (n.children == null)
			{
				n.remove (); 
				n.dispose ();
			}
			else if (flattenGroup (n))
			{
				if (insertSeparator && !sep)
				{
					n.insertChainBefore
						(n.getPipeline ().new Node (UINodeHandlerImpl.SEPARATOR,
						 null));
				}
				Node c = (Node) n.children;
				n.children = null;
				c = (UITreePipeline.Node) n.insertChainBefore (c);
				n.remove ();
				n.dispose ();
				if (c != null)
				{
					sep = c.getType () == UINodeHandler.NT_SEPARATOR;
				}
			}
			else
			{
				sep = false;
			}
		}
		if (insertSeparator && sep)
		{
			root.removeLastChild ();
		}
	}	


	public boolean isAffectedBy (TreePath path)
	{
		return false;
	}


	protected boolean isLeaf (Node node)
	{
		return node.isLeaf ();
	}


	protected boolean hasContent (Node node)
	{
		int t = node.getType ();
		return (t != UINodeHandler.NT_DIRECTORY)
			&& (t != UINodeHandler.NT_GROUP)
			&& (t != UINodeHandler.NT_CHOICE_GROUP)
			&& ((t != UINodeHandler.NT_SELECTABLE)
				|| (node.invoke (UINodeHandler.GET_SELECTABLE_METHOD, null) != null));
	}


	protected boolean flattenGroup (Node node)
	{
		int t = node.getType ();
		return (t == UINodeHandler.NT_GROUP)
			|| (t == UINodeHandler.NT_CHOICE_GROUP);
	}

}
