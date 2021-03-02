
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

import javax.swing.event.*;

public class MutableTree extends Tree implements MutableTreeModel
{

	public MutableTree (Node root)
	{
		super (root);
	}


	public synchronized void setRoot (Object root)
	{
		this.root = (Node) root;
		fireTreeModelEvent (STRUCTURE_CHANGED,
							new TreeModelEvent (this, new Object[] {root}));
	}


	public synchronized void insert (Object parent, int index, Object child,
									 Object constraints)
	{
		Node p = (Node) parent, c = (Node) child;
		int n = 0;
		for (Node a = c; a != null; a = a.next)
		{
			n++;
		}
		int[] i = new int[n];
		Object[] o = new Object[n];
		n = 0;
		for (Node a = c; a != null; a = a.next, n++)
		{
			o[n] = a;
			i[n] = index + n;
		}
		p.insertChild (c, index);
		fireTreeModelEvent (NODES_INSERTED,
							new TreeModelEvent (this, p.getPath (), i, o));
	}


	public synchronized void remove (Object parent, int index)
	{
		remove (parent, index, 1);
	}


	public synchronized void remove (Object parent, int index, int count)
	{
		Node p = (Node) parent, c = p.getChild (index);
		int n = 0;
		for (Node a = c; (a != null) && (n < count); a = a.next)
		{
			n++;
		}
		if (n > 0)
		{
			int[] i = new int[n];
			Object[] o = new Object[n];
			for (count = 0; count < n; count++)
			{
				o[count] = c;
				i[count] = index + count;
				Node next = c.next;
				p.removeChild (c);
				c = next;
			}
			fireTreeModelEvent (NODES_REMOVED,
								new TreeModelEvent (this, p.getPath (), i, o));
		}
	}


	@Override
	public void valueForPathChanged (javax.swing.tree.TreePath path, Object newValue)
	{
		TreeModelEvent e;
		if (path.getPathCount () <= 1)
		{
			e = new TreeModelEvent (this, path, null, null);
		}
		else
		{
			Node n = (Node) path.getLastPathComponent ();
			path = path.getParentPath ();
			e = new TreeModelEvent (this, path, new int[] {getIndexOfChild (n)},
									new Object[] {n});
		}
		fireTreeModelEvent (NODES_CHANGED, e);
	}


	public void dispose ()
	{
	}

}
