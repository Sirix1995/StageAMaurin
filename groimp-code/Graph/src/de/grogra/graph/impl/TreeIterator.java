
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.grogra.xl.util.ObjectList;

/**
 * This <code>Iterator</code> class iterates over all nodes
 * of the subtree starting at a specified root node. The subtree is spanned
 * by edges of type {@link de.grogra.graph.Graph#BRANCH_EDGE} or
 * {@link de.grogra.graph.Graph#SUCCESSOR_EDGE} in forward direction.
 * 
 * @author Ole Kniemeyer
 */
public class TreeIterator extends ObjectList implements Iterator
{
	
	/**
	 * Create a new iterator. It has to be configured by
	 * {@link #setRoot(Node)} before it is used. 
	 */
	public TreeIterator ()
	{
	}

	
	/**
	 * Creates a new iterator iterating over the subtree starting
	 * at <code>root</code>.
	 * 
	 * @param root the root node of the subtree
	 */
	public TreeIterator (Node root)
	{
		root.getClass ();
		push (root);
	}


	/**
	 * Configures this iterator to iterate over the subtree starting
	 * at <code>root</code>.
	 * 
	 * @param root the root node of the subtree
	 */
	public void setRoot (Node root)
	{
		clear ();
		push (root);
	}


	public boolean hasNext ()
	{
		return !isEmpty ();
	}
	
	
	public Object next ()
	{
		return nextNode ();
	}
	
	
	public void remove ()
	{
		throw new UnsupportedOperationException ();
	}
	

	/**
	 * Returns the next node of this iterator. This is equivalent
	 * to {@link #next()}.
	 * 
	 * @return the next node of this iterator
	 */
	public Node nextNode ()
	{
		if (isEmpty ())
		{
			throw new NoSuchElementException ();
		}
		Node n = (Node) pop ();
		for (Edge e = n.getFirstEdge (); e != null; e = e.getNext (n))
		{
			Node t = e.getTarget ();
			if ((t != n)
				&& e.testEdgeBits (GraphManager.BRANCH_EDGE | GraphManager.SUCCESSOR_EDGE))
			{
				push (t);
			}
		}
		return n;
	}

}
