
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

package de.grogra.imp2d.layout;

import javax.vecmath.Point2d;

/**
 * A <code>Node</code> represents a node of a graph which is to be
 * layouted by a {@link de.grogra.imp2d.layout.Layout}. Such a graph
 * is constructed as an image of an actual source {@link de.grogra.graph.Graph}.
 * The nodes and edges of this image provide a simple and safe means to query
 * and modify layout-relevant information. E.g., <code>Node</Node>
 * extends <code>javax.vecmath.Point2f</code>, the inherited fields
 * <code>x</code> and <code>y</code> represent the node's global 2D-coordinates.
 * <p>
 * Nodes are connected by instances of {@link de.grogra.imp2d.layout.Edge}.
 * To iterate over all edges of a node <code>n</code>, the following
 * pattern has to be used:
 * <pre>
 * for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n))
 * {
 *     // do something with the current edge e
 * }
 * </pre>
 * In addition to these edge connections, all nodes of a graph are
 * connected linearly in a linked list via their field <code>next</code>.
 * 
 * @author Ole Kniemeyer
 */
public final class Node extends javax.vecmath.Point2f
{
	/**
	 * The next node in the linked list of all nodes.
	 */
	public final Node next;

	/**
	 * The original node in the source <code>Graph</code>. 
	 */
	public final Object object;

	/**
	 * The width of the 2D-visualization. May be 0.
	 */
	public float width;

	/**
	 * The height of the 2D-visualization. May be 0.
	 */
	public float height;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float layoutVarX;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float layoutVarY;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float initialX;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float initialY;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float finalX;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public float finalY;

	/**
	 * This field may be used freely by layout algorithms.
	 */
	public int index;
	
	/**
	 * This field may be used freely by layout algorithms.
	 */
	public boolean isAccessed;
	
	private final javax.vecmath.Point2f backupCoordinate = new javax.vecmath.Point2f();
	
	public Node nextForHierarchicalView;

	private Edge firstEdge;


	Node (Node next, Object object)
	{
		this.next = next;
		this.object = object;
	}


	/**
	 * Returns the first edge of the edge list of this node.
	 * 
	 * @return this node's first edge
	 * @see Node
	 */
	public Edge getFirstEdge ()
	{
		return firstEdge;
	}

	Edge getOrCreateEdgeTo (Node target, Object object, boolean edgeNode, float weight) {
		//search if a edge already exist
		for (Edge e = firstEdge; e != null; e = e.getNext (this))
		{
			if (e.target == target)
			{
				return e;
			}
		}
		
		Edge s = new Edge (this, target, object, edgeNode, weight);
		s.setNext (target.firstEdge, target);
		target.firstEdge = s;
		s.setNext (firstEdge, this);
		firstEdge = s;
		return s;
	}
	
	/**
	 * Returns the edge from this node to <code>target</code>.
	 * 
	 * @param target the target node
	 * @return the edge from <code>this</code> as <code>source</code> to
	 * <code>target</code>, or <code>null</code> if no such edge exists 
	 */
	public Edge getEdgeTo (Node target)
	{
		for (Edge e = firstEdge; e != null; e = e.getNext (this))
		{
			if (e.target == target)
			{
				return e;
			}
		}
		return null;
	}
	
	public boolean nodeSearch(Object co, Point2d point)
	{
		Node p = this;
		while(p!=null)
		{
			if(p.object == co)
			{
				point.set(p.x, p.y);
				return true;
			}
			else
			{
				p = p.next;
			}
		}
		return false;
	}
	
	public Node nodeSearch(Object co)
	{
		Node p = this;
		while(p!=null)
		{
			if(p.object == co)
			{
				return p;
			}
			else
			{
				p = p.next;
			}
		}
		return null;
	}

	public Node nodeSearchByName(Object co)
	{
		Node p = this;
		while(p!=null)
		{
			if(p.object.getClass ().getSimpleName ().equals (co.getClass ().getSimpleName ()))
			{
				return p;
			}
			else
			{
				p = p.next;
			}
		}
		return null;
	}
	
	public void nodeCoordSet(Object co, Point2d point)
	{
		Node p = this;
		while(p!=null)
		{
			if(p.object == co)
			{
				p.x = (float) point.x;
				p.y = (float) point.y;
				break;
			}
			else
			{
				p = p.next;
			}
		}
	}
	
	public void coordinateBackup()
	{
		Node p = this;
		while(p!=null)
		{
			p.backupCoordinate.set(p);
			p = p.next;
		}
	}
	
	public void coordinateRecover()
	{
		Node p = this;
		while(p!=null)
		{
			p.set(p.backupCoordinate);
			p = p.next;
		}
	}

}
