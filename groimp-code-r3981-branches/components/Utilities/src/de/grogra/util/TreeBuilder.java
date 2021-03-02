
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

package de.grogra.util;

import java.util.List;
import javax.swing.tree.TreeModel;

import de.grogra.xl.util.ObjectList;

public abstract class TreeBuilder
{
	protected TreeModel source;
	protected MutableTreeModel target;
	protected List nodes;


	public void buildTree (TreeModel source, Object sourceRoot,
						   MutableTreeModel target, List nodes)
	{
		this.source = source;
		this.target = target;
		this.nodes = nodes;
		ObjectList stack = new ObjectList ();
		stack.push (sourceRoot);
		stack.push (target.getRoot ());
		buildTree (stack);
		this.source = null;
		this.target = null;
		this.nodes = null;
	}


	private void buildTree (ObjectList stack)
	{
		Object sp = stack.peek (2);
		for (int i = 0, n = source.getChildCount (sp); i < n; i++)
		{
			Object sc = source.getChild (sp, i);
			if (isLeafSource (sc))
			{
				for (int j = 0, m = nodes.size (); j < m; j++)
				{
					Object tc = createNodes (nodes.get (j), sc);
					if (tc != null)
					{
						Object tp = getTargetParent (stack);
						target.insert (tp, target.getChildCount (tp), tc, null);
						nodes.remove (j);
						j--;
						m--;
					}
				}
			}
			else
			{
				stack.push (sc);
				stack.push (null);
				buildTree (stack);
				stack.pop ();
				stack.pop ();
			}
		}
		for (int j = 0, m = nodes.size (); j < m; j++)
		{
			Object tc = createNodesInGroup (nodes.get (j), sp);
			if (tc != null)
			{
				Object tp = getTargetParent (stack);
				target.insert (tp, target.getChildCount (tp), tc, null);
				nodes.remove (j);
				j--;
				m--;
			}
		}

	}


	private Object getTargetParent (ObjectList stack)
	{
		Object g = stack.peek (1);
		if (g != null)
		{
			return g;
		}
		stack.pop ();
		Object p = stack.pop ();
		Object gp = getTargetParent (stack);
		stack.push (p);
		g = createGroup (p);
		stack.push (g);
		target.insert (gp, target.getChildCount (gp), g, null);
		return g;
	}


	protected boolean isLeafSource (Object sourceNode)
	{
		return source.isLeaf (sourceNode);
	}


	protected abstract Object createNodes (Object node, Object sourceNode);


	protected abstract Object createGroup (Object sourceGroup);


	protected abstract Object createNodesInGroup (Object node,
												  Object sourceGroup);
}
