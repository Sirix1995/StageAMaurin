
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

package de.grogra.graph.impl;

import java.io.PrintWriter;
import java.util.Collection;
import de.grogra.reflect.*;

/**
 * An <code>Extent</code> keeps track of all nodes of a specific class in a
 * {@link de.grogra.graph.impl.GraphManager}. This is used to efficiently
 * obtain all nodes which are instances of some given class.
 * 
 * @author Ole Kniemeyer
 */
public final class Extent
{
	/**
	 * The type of which the nodes of this extent are. Note that nodes of
	 * subtypes are not included.
	 */
	private final Type type;

	/**
	 * The lock which has to be used for modifying operations.
	 */
	private final Object lock;

	/**
	 * The extent of the supertype, or <code>null</code> if this extent
	 * corresponds to the base class {@link Node}.
	 */
	private final Extent superExtent;

	/**
	 * The first extent in the linked list of subextents. The types of
	 * subextents are direct subtypes of the type of this extent.
	 */
	private Extent subExtents;

	/**
	 * The next extent in the linked list of subextents.
	 */
	private final Extent next;

	/**
	 * The first nodes in the doubly-linked lists of nodes of this extent.
	 * For each extent index from 0 to {@link Node#LAST_EXTENT_INDEX}, there
	 * is such a list.
	 *
	 * @see Node#extentNext
	 * @see Node#extentPrev
	 */
	private Node[] first = new Node[Node.LAST_EXTENT_INDEX + 1];

	/**
	 * The current size of this extent, i.e., the number of nodes having
	 * exactly the type of this extent.
	 */
	private int size;

	/**
	 * The current size of this extent including subextents, i.e.,
	 * the number of nodes which are instances of the type of this extent.
	 * This is negative if it is invalid and has to be recomputed. 
	 */
	private int totalSize;


	Extent (Object lock, Type type, Extent superExtent)
	{
		this.lock = lock;
		this.type = type;
		this.superExtent = superExtent;
		if (superExtent == null)
		{
			this.next = null;
		}
		else
		{
			this.next = superExtent.subExtents;
			superExtent.subExtents = this;
		}
	}

	
	/**
	 * Returns the type of which the nodes of this extent are. Note that nodes of
	 * subtypes are not included.
	 * 
	 * @return type of nodes of this extent
	 */
	public Type getType ()
	{
		return type;
	}

	/**
	 * Returns the current size of this extent excluding subextents,
	 * i.e., the number of nodes belonging to this extent only.
	 * @return
	 */
	public int size ()
	{
		return size;
	}
	
	/**
	 * Returns the current size of this extent including subextents,
	 * i.e., the number of nodes belonging to this extent and subextents.
	 * 
	 * @return size of this extent including subextents
	 */
	public int totalSize ()
	{
		synchronized (lock)
		{
			return (totalSize >= 0) ? totalSize : totalSizeImpl ();
		}
	}

	
	/**
	 * Returns the first node of the <code>extentIndex</code>-th
	 * linked list of nodes which constitute this extent.
	 * 
	 * @return first node of <code>extentIndex</code>-th linked list of this extent
	 * 
	 * @see #getNextNode(Node)
	 */
	public Node getFirstNode (int extentIndex)
	{
		return first[extentIndex];
	}

	
	/**
	 * Returns the node following <code>prev</code> in the linked list of
	 * nodes which constitute this extent.
	 * 
	 * @return node following <code>prev</code> in linked list of this extent
	 * 
	 * @see #getFirstNode
	 */
	public Node getNextNode (Node prev)
	{
		return prev.extentNext;
	}

	
	/**
	 * Adds all direct subextents of this extent to <code>list</code>. Direct
	 * subextents are the extents of direct subtypes of the type of this
	 * extent.
	 * 
	 * @param list list to which subextents are added
	 */
	public void getSubExtents (Collection<? super Extent> list)
	{
		synchronized (lock)
		{
			for (Extent e = subExtents; e != null; e = e.next)
			{
				list.add (e);
			}
		}
	}

	
	/**
	 * Recomputes (if necessary) the size of this extent including subextents and returns it.
	 * 
	 * @return size of this extent including subextents
	 */
	private int totalSizeImpl ()
	{
		if (totalSize < 0)
		{
			int n = 0;
			for (Extent e = subExtents; e != null; e = e.next)
			{
				n += e.totalSizeImpl ();
			}
			totalSize = n + size;
		}
		return totalSize;
	}


	/**
	 * Adds <code>n</code> to this extent. The type of <code>n</code> has to
	 * be the type of this extent. 
	 * 
	 * @param n node to add
	 */
	void add (Node n)
	{
		assert (n.id >= 0) && (n.extentNext == null) && (n.extentPrev == null);
		
		int ei = n.getExtentIndex ();
		
		// add n to doubly linked list
		if (first[ei] == null)
		{
			// n is first node
			first[ei] = n;
		}
		else
		{
			// prepend n at head
			first[ei].extentPrev = n;
			n.extentNext = first[ei];
			first[ei] = n;
		}
		size++;
		if (totalSize >= 0)
		{
			// increase size
			totalSize++;
			
			// invalidate superextents
			for (Extent e = superExtent; (e != null) && (e.totalSize >= 0); e = e.superExtent)
			{
				e.totalSize = -1;
			}
		}
	}
	
	
	/**
	 * Remove <code>n</code> from this extent.
	 * 
	 * @param n node to remove
	 */
	void remove (Node n)
	{
		// remove n from doubly linked list
	removeFromPrev:
		if (n.extentPrev != null)
		{
			n.extentPrev.extentNext = n.extentNext;
		}
		else
		{
			for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++)
			{
				if (first[i] == n)
				{
					first[i] = n.extentNext;
					break removeFromPrev;
				}
			}
			if (n.extentNext != null)
			{
				throw new AssertionError ();
			}
		}
		if (n.extentNext != null)
		{
			n.extentNext.extentPrev = n.extentPrev;
		}
		n.extentNext = null;
		n.extentPrev = null;

		size--;
		if (totalSize >= 0)
		{
			// decrease size
			totalSize--;
			
			// invalidate superextents
			for (Extent e = superExtent; (e != null) && (e.totalSize >= 0); e = e.superExtent)
			{
				e.totalSize = -1;
			}
		}
	}

	
	/**
	 * Reenqueue node in linked list (i.e., remove and add it). This should be
	 * invoked when the {@link Node#getExtentIndex extentIndex} property of a
	 * node has changed.
	 * 
	 * @param node to reenqueue
	 */
	void reenqueue (Node node)
	{
		remove (node);
		add (node);
	}

	void clear ()
	{
		Node m;
		for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++)
		{
			for (Node n = first[i]; n != null; n = m)
			{
				m = n.extentNext;
				n.extentNext = null;
				n.extentPrev = null;
			}
			first[i] = null;
		}
		size = 0;
		totalSize = -1;
		for (Extent e = subExtents; e != null; e = e.next)
		{
			e.clear ();
		}
	}

	void dump (String indent, boolean recursive)
	{
		System.err.print (indent);
		System.err.println (this);
		for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++)
		{
			for (Node n = first[i]; n != null; n = n.extentNext)
			{
				System.err.print (indent);
				System.err.print ("    ");
				System.err.println (n);
			}
		}
		if (recursive)
		{
			for (Extent e = subExtents; e != null; e = e.next)
			{
				e.dump (indent + "  ", true);
			}
		}
	}

	
	void dumpAll ()
	{
		dump ("", true);
	}


	@Override
	public String toString ()
	{
		return "Extent[type=" + type.getBinaryName () + ",size=" + size + ']';
	}

	void dumpStatistics (PrintWriter out, int totalSize, int indent)
	{
		for (int i = 0; i < indent; i++)
		{
			out.print (' ');
		}
		out.print (type.getBinaryName ());
		out.print ('[');
		out.print (size);
		out.print (" nodes = ");
		out.print (size * 100f / totalSize);
		out.print (" %, ");
		out.print (totalSize ());
		out.print (" total = ");
		out.print (totalSize () * 100f / totalSize);
		out.println (" %]");
		for (Extent e = subExtents; e != null; e = e.next)
		{
			e.dumpStatistics (out, totalSize, indent + 1);
		}
	}

}
