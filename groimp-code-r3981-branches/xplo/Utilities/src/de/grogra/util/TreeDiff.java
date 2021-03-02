
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

import javax.swing.tree.TreeModel;
import javax.swing.event.*;

import de.grogra.xl.util.ObjectList;

public class TreeDiff
{
	public interface NodeModel
	{
		boolean equals (Object a, Object b);

		DiffInfo getDiffInfo (Object node);
	}


	public static final class DiffInfo
	{
		final Object node;
		Object oldNode;
		DiffInfo nextMatch;
		int matchIndex, treeCount;
		boolean inOptMatch;


		public DiffInfo (Object node)
		{
			if (node == null)
			{
				throw new NullPointerException ();
			} 
			this.node = node;
		}
	}


	private TreeModel oldTree, newTree;
	private NodeModel model;
	private Object eventSource;
	private TreeModelListener listener;
	
	private boolean removed;
	private boolean inserted;

	private DiffInfo firstMatch;
	private int optCount, maxCount, optLen;

	private final ObjectList oldPath = new ObjectList (10);
	private final ObjectList newPath = new ObjectList (10);


	public boolean compare (TreeModel oldTree, TreeModel newTree,
							NodeModel model, Object eventSource,
							TreeModelListener listener)
	{
		this.oldTree = oldTree;
		this.newTree = newTree;
		this.model = model;
		this.eventSource = eventSource;
		this.listener = listener;
		oldPath.clear ();
		newPath.clear ();
		removed = false;
		inserted = false;
		compare (oldTree.getRoot (), newTree.getRoot (), new boolean[32]);
		return removed || inserted;
	}


	private boolean[] compare
		(Object parent, Object newParent, boolean[] matched)
	{
		oldPath.push (parent);
		newPath.push (newParent);
		firstMatch = null;
		maxCount = 0;
		DiffInfo prev = null;
		int oldCount = oldTree.getChildCount (parent),
			newCount = newTree.getChildCount (newParent);
		if (newCount > matched.length)
		{
			matched = new boolean[newCount];
		}
		else
		{
			for (int i = 0; i < newCount; i++)
			{
				matched[i] = false;
			}
		}
		for (int k = 0; k < oldCount; k++)
		{
			Object c = oldTree.getChild (parent, k);
			Object m;
			for (int i = 0; i < newCount; i++)
			{
				if (!matched[i]
					&& model.equals (c, m = newTree.getChild (newParent, i))) 
				{
					matched[i] = true;
					DiffInfo mi = model.getDiffInfo (m);
					mi.oldNode = c;
					mi.matchIndex = i;
					mi.inOptMatch = false;
					mi.nextMatch = null;
					maxCount += getTreeCount (m);
					if (prev == null)
					{
						firstMatch = mi;
					}
					else
					{
						prev.nextMatch = mi;
					}
					prev = mi;
					break;
				}
			}
		}
		optLen = 0;
		optCount = 0;
		findOptMatch (firstMatch, -1, 0);
		if (oldCount > optLen)
		{
			DiffInfo d = firstMatch;
			int[] ir = new int[oldCount - optLen];
			Object[] or = new Object[oldCount - optLen];
			int i = 0;
			for (int k = 0; k < oldCount; k++)
			{
				Object c = oldTree.getChild (parent, k);
				if ((d != null) && (c == d.oldNode))
				{
					if (!d.inOptMatch)
					{
						ir[i] = k;
						or[i++] = c;
					}
					d = d.nextMatch;
				}
				else
				{
					ir[i] = k;
					or[i++] = c;
				}
			}
			assert d == null;
			assert i == ir.length;
			removed = true;
			listener.treeNodesRemoved
				(new TreeModelEvent (eventSource, oldPath.toArray (), ir, or));
		}
		if (newCount > optLen)
		{
			int[] ir = new int[newCount - optLen];
			Object[] or = new Object[newCount - optLen];
			int i = 0;
			for (int k = 0; k < newCount; k++)
			{
				Object m = newTree.getChild (newParent, k);
				if (!(matched[k] && model.getDiffInfo (m).inOptMatch))
				{
					ir[i] = k;
					or[i++] = m;
				}
			}
			assert i == ir.length;
			inserted = true;
			listener.treeNodesInserted
				(new TreeModelEvent (eventSource, newPath.toArray (), ir, or));
		}
		for (DiffInfo d = firstMatch; d != null; d = prev)
		{
			if (d.inOptMatch)
			{
				matched = compare (d.oldNode, d.node, matched);
			}
			prev = d.nextMatch;
			d.nextMatch = null;
			d.oldNode = null;
		}
		oldPath.pop ();
		newPath.pop ();
		return matched;
	}


	private void findOptMatch (DiffInfo n, int index, int count)
	{
		if (n == null)
		{
			if (count > optCount)
			{
				optCount = count;
				optLen = 0;
				for (DiffInfo i = firstMatch; i != null; i = i.nextMatch)
				{
					if (i.inOptMatch = (i.matchIndex >= 0))
					{
						optLen++;
					}
				}
			}
		}
		else if (optCount < maxCount)
		{
			int i = n.matchIndex;
			if (i > index)
			{
				findOptMatch (n.nextMatch, i, count + n.treeCount);
			}
			n.matchIndex = -1;
			findOptMatch (n.nextMatch, index, count);
			n.matchIndex = i;
		}
	}


	private int getTreeCount (Object newNode)
	{
		int tc = 1;
		for (int i = 0, n = newTree.getChildCount (newNode); i < n; i++) 
		{
			tc += getTreeCount (newTree.getChild (newNode, i));
		}
		return model.getDiffInfo (newNode).treeCount = tc;
	}

}
