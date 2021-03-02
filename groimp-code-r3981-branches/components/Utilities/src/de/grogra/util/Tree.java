
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
import javax.swing.tree.TreePath;

import de.grogra.xl.lang.ObjectToBoolean;

import java.util.Comparator;

public class Tree extends TreeModelSupport implements TreeModel
{

	public static class Node
	{
		public Node next = null, children = null, parent = null;


		public Node setNext (Node next)
		{
			this.next = next;
			while (next != null)
			{
				next.parent = parent;
				next = next.next;
			}
			return this;
		}


		public Node setChildren (Node children)
		{
			this.children = children;
			while (children != null)
			{
				children.parent = this;
				children = children.next;
			}
			return this;
		}


		public void addChild (Node child)
		{
			if (children == null)
			{
				setChildren (child);
			}
			else
			{
				children.getLastSibling ().setNext (child);
			}
		}


		public void insertChild (Node child, int index)
		{
			Node prev = null, n;
			for (n = children; n != null; n = n.next)
			{
				if (index-- == 0)
				{
					break;
				}
				prev = n;
			}
			if (prev == null)
			{
				children = child;
			}
			else
			{
				prev.next = child;
			}
			while (child.next != null)
			{
				child.parent = this;
				child = child.next;
			}
			child.parent = this;
			child.next = n;
		}


		public Node insertChainBefore (Node chain)
		{
			if (chain == null)
			{
				return null;
			}
			Node n = parent.children;
			if (n == this)
			{
				parent.children = chain;
			}
			else
			{
				while (n.next != this)
				{
					n = n.next;
				}
				n.next = chain;
			}
			while (true)
			{
				chain.parent = parent;
				if (chain.next == null)
				{
					chain.next = this;
					return chain;
				}
				chain = chain.next;
			}
		}


		public void replace (Node newNode, boolean newKeepsChildren)
		{
			Node n = parent.children;
			if (n == this)
			{
				parent.children = newNode;
			}
			else
			{
				while (n.next != this)
				{
					n = n.next;
				}
				n.next = newNode;
			}
			newNode.parent = parent;
			newNode.next = next;
			if (!newKeepsChildren)
			{
				newNode.children = children;
				for (n = children; n != null; n = n.next)
				{
					n.parent = newNode;
				}
				children = null;
			}
		}


		public Node getLastSibling ()
		{
			for (Node s = this;; s = s.next)
			{
				if (s.next == null)
				{
					return s;
				}
			}
		}


		public void removeChild (Node child)
		{
			Node c = children;
			if (c == child)
			{
				children = child.next;
			}
			else
			{
				while (c.next != child)
				{
					c = c.next;
					if (c == null)
					{
						return;
					}
				}
				c.next = child.next;
			}
			child.next = null;
			child.parent = null;
		}


		public void remove ()
		{
			Node n = parent.children;
			if (n == this)
			{
				parent.children = next;
			}
			else
			{
				while (n.next != this)
				{
					n = n.next;
				}
				n.next = next;
			}
		}


		public void removeLastChild ()
		{
			Node n = children;
			if (n != null)
			{
				if (n.next == null)
				{
					children = null;
				}
				else
				{
					while (n.next.next != null)
					{
						n = n.next;
					}
					n.next = null;
				}
			}
		}


		public int getChildCount ()
		{
			Node c = children;
			int count = 0;
			while (c != null)
			{
				count++;
				c = c.next;
			}
			return count;
		}


		public Node getChild (int index)
		{
			Node c = children;
			while (--index >= 0)
			{
				c = c.next;
				if (c == null)
				{
					return null;
				}
			}
			return c;
		}

		
		public Node[] getChildren ()
		{
			int n = getChildCount ();
			Node[] a = new Node[n];
			n = -1;
			Node c = children;
			while (c != null)
			{
				a[++n] = c;
				c = c.next;
			}
			return a;
		}

		
		public Node[] getPath ()
		{
			int n = 0;
			for (Node p = this; p != null; p = p.parent)
			{
				n++;
			}
			Node[] a = new Node[n];
			for (Node p = this; p != null; p = p.parent)
			{
				a[--n] = p;
			}
			return a;
		}


		public TreePath getTreePath ()
		{
			return new TreePath (getPath ());
		}

	}


	protected Node root;


	public Tree (Node root)
	{
		super (false);
		this.root = root;
	}


	public boolean isLeaf (Object node)
	{
		return ((Node) node).children == null;
	}


	public Object getChild (Object parent, int index)
	{
		return ((Node) parent).getChild (index);
	}


	public Object getParent (Object child)
	{
		return ((Node) child).parent;
	}


	public int getChildCount (Object parent)
	{
		return ((Node) parent).getChildCount ();
	}


	public int getIndexOfChild (Object parent, Object child)
	{
		Node c = ((Node) parent).children;
		if (c == null)
		{
			return -1;
		}
		int index = 0;
		while (!nodesEqual (c, (Node) (child)))
		{
			index++;
			c = c.next;
			if (c == null)
			{
				return -1;
			}
		}
		return index;
	}


	public boolean nodesEqual (Node a, Node b)
	{
		return a.equals (b);
	}


	public final int getIndexOfChild (Node child)
	{
		return getIndexOfChild (child.parent, child);
	}


	public Object getRoot ()
	{
		return root;
	}


	public static Object findFirst (TreeModel tree, Object root, ObjectToBoolean filter)
	{
		if (filter.evaluateBoolean (root))
		{
			return root;
		}
		for (int i = 0, n = tree.getChildCount (root); i < n; i++)
		{
			Object o = findFirst (tree, tree.getChild (root, i), filter);
			if (o != null)
			{
				return o;
			}
		}
		return null;
	}


	public static Object findMax (TreeModel tree, Object root,
								  Comparator comparator)
	{
		return findMax (tree, root, comparator, null);
	}


	public static Object findMax (TreeModel tree, Object root,
								  Comparator comparator, Object max)
	{
		if (comparator.compare (root, max) > 0)
		{
			max = root;
		}
		for (int i = 0, n = tree.getChildCount (root); i < n; i++)
		{
			max = findMax (tree, tree.getChild (root, i), comparator, max);
		}
		return max;
	}


	public void valueForPathChanged (TreePath path, Object newValue)
	{
	}


	public boolean hasValidPath (Node leaf)
	{
		if (leaf == null)
		{
			return false;
		}
		while (true)
		{
			if (nodesEqual (leaf, root))
			{
				return true;
			}
			Node p = leaf.parent;
			if ((p == null) || (getIndexOfChild (p, leaf) < 0))
			{
				return false;
			}
			leaf = p;
		}
	}

}
