
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

package de.grogra.xl.impl.dom;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.impl.base.EdgeIterator;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.util.ObjectList;

public class RuntimeModel extends de.grogra.xl.impl.base.RuntimeModel
{
	public static final int SIBLING = MIN_USER_EDGE;
	public static final int ATTRIBUTE = MIN_USER_EDGE << 1;
	static final int CHILD = BRANCH_EDGE | SUCCESSOR_EDGE;

	@Override
	public Class getNodeType ()
	{
		return Node.class;
	}


	@Override
	public void addEdgeBits (Object source, Object target, int bits)
	{
		if (!addEdgeBits ((Node) source, (Node) target, bits))
		{
			System.out.println ("Cannot make " + source + " and " + target + " siblings");
		}
	}


	static boolean addEdgeBits (Node source, Node target, int bits)
	{
		if ((bits & CHILD) != 0)
		{
			source.appendChild (target);
		}
		else if ((bits & SIBLING) != 0)
		{
			Node p = source.getParentNode ();
			if (p != null)
			{
				p.insertBefore (target, source.getNextSibling ());
			}
			else
			{
				p = target.getParentNode ();
				if (p != null)
				{
					p.insertBefore (source, target);
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public int getEdgeBits (Object source, Object target)
	{
		Node s = (Node) source;
		Node t = (Node) target;
		Node n = t.getParentNode ();
		if ((n != null) && s.equals (n))
		{
			return CHILD;
		}
		n = t.getPreviousSibling ();
		if ((n != null) && s.equals (n))
		{
			return SIBLING;
		}
		if ((t.getNodeType () == Node.ATTRIBUTE_NODE)
			&& (s.getNodeType () == Node.ELEMENT_NODE)
			&& (((Element) s).getAttributeNode (t.getLocalName ()) == t))
		{
			return ATTRIBUTE;
		}
		return 0;
	}


	public boolean isWrapperFor (Object object, Type type)
	{
		if (Reflection.equal (type, Type.STRING))
		{
			return ((Node) object).getNodeType () == Node.TEXT_NODE;
		}
		return false;
	}

	public Object unwrapObject (Object wrapper)
	{
		Node w = (Node) wrapper;
		switch (w.getNodeType ())
		{
			case Node.TEXT_NODE:
				return ((Text) w).getData ();
		}
		return null;
	}

	final ObjectList iterators = new ObjectList ();
	
	static final int START = -1;
	static final int PARENT = 0;
	static final int PREV = 1;
	static final int NEXT = 2;
	static final int CHILDREN_ATTRIBUTES = 3;

	private final class Iterator extends EdgeIterator
	{
		Node node;
		private int state;
		private boolean children;
		private Node child;
		private NamedNodeMap attribs;
		private int attrIndex;
		private EdgeDirection direction;
		
		@Override
		public void moveToNext ()
		{
			while (true)
			{
				switch (++state)
				{
					case PARENT:
						source = node.getParentNode ();
						if (source != null)
						{
							target = node;
							edgeBits = RuntimeModel.CHILD;
							return;
						}
						break;
					case PREV:
						source = node.getPreviousSibling ();
						if (source != null)
						{
							target = node;
							edgeBits = RuntimeModel.SIBLING;
							return;
						}
						break;
					case NEXT:
						target = node.getNextSibling ();
						if (target != null)
						{
							source = node;
							edgeBits = RuntimeModel.SIBLING;
							return;
						}
						break;
					default:
						if (children)
						{
							if (child == null)
							{
								child = node.getFirstChild ();
								source = node;
								edgeBits = RuntimeModel.CHILD;
							}
							else
							{
								child = child.getNextSibling ();
							}
							target = child;
							if (child != null)
							{
								return;
							}
							children = false;
							if (direction.contains (EdgeDirection.FORWARD)
								&& (node.getNodeType () == Node.ELEMENT_NODE))
							{
								attribs = node.getAttributes ();
								source = node;
								edgeBits = RuntimeModel.ATTRIBUTE;
								attrIndex = 0;
							}
						}
						if (attribs != null)
						{
							if (attrIndex < attribs.getLength ())
							{
								target = attribs.item (attrIndex++);
							}
							else
							{
								attribs = null;
							}
						}
						return;
				}
			}
		}
		
		
		@Override
		public boolean hasEdge ()
		{
			return (state < CHILDREN_ATTRIBUTES) || children || (attribs != null); 
		}
		
		
		@Override
		public void dispose ()
		{
			child = null;
			source = null;
			target = null;
			attribs = null;
			synchronized (RuntimeModel.this)
			{
				node = null;
				iterators.push (this);
			}
		}
	}

	@Override
	public synchronized EdgeIterator createEdgeIterator (Object node, EdgeDirection dir)
	{
		Iterator i = iterators.isEmpty () ? new Iterator () : (Iterator) iterators.pop ();
		i.node = (Node) node;
		i.state = START;
		i.children = true;
		i.child = null;
		i.attribs = null;
		i.direction = dir;
		i.moveToNext ();
		return i;
	}

}
