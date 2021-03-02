/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package de.grogra.rgg;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.Pattern;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

public final class TreeMatcher extends Pattern.Matcher
{
	private final boolean postOrder;

	public TreeMatcher (float matchCount, boolean postOrder)
	{
		super (matchCount);
		this.postOrder = postOrder;
	}

	@Override
	public void findMatches (QueryState qs,
			MatchConsumer consumer, int arg)
	{
		Node n = (Node) qs.abound (0);
		Class nodeClass = (Class) qs.abound (1);
		Class leafClass = (Class) qs.abound (3);
		ObjectSynth<Node, Node> nodes = new ObjectSynth<Node, Node> ();
		ObjectSynth<Node, Node> leaves = new ObjectSynth<Node, Node> ();
		nodes.startIndex = 0;
		nodes.valuesList = new ObjectList<Node> ();
		leaves.startIndex = 0;
		leaves.valuesList = new ObjectList<Node> ();
		ObjectList<Node> stack = (ObjectList<Node>) qs.userStack0;
		int sp = stack.size;
		IntList istack = qs.userIStack0;
		int isp = istack.size;
		try
		{
			stack.push (n);
			while (stack.size > sp)
			{
				n = stack.pop ();
				boolean popElements = false;
				if (n != null)
				{
					if (postOrder)
					{
						stack.push (n).push (null);
					}
					else
					{
						nodes.valuesList.clear ();
						leaves.valuesList.clear ();
					}
					int p = stack.size;
					stack.push (n);
					int childCount = 0;
					int sourceCount = 0;
					while (stack.size > p)
					{
						Node s = stack.pop ();
						for (Edge e = s.getFirstEdge (); e != null; e = e.getNext (s))
						{
							Node m = e.getTarget ();
							if ((m != s) && e.testEdgeBits (Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE))
							{
								if (nodeClass.isInstance (m))
								{
									nodes.valuesList.push (m);
									childCount++;
								}
								else
								{
									stack.push (m);
									if (leafClass.isInstance (m))
									{
										leaves.valuesList.push (m);
										sourceCount++;
									}
								}
							}
						}
					}
					if (postOrder)
					{
						istack.push (childCount).push (sourceCount);
						n = null;
					}
					stack.addAll (nodes.valuesList.elements, nodes.valuesList.size - childCount, childCount);
				}
				else
				{
					n = stack.pop ();
					int sourceCount = istack.pop ();
					int childCount = istack.pop ();
					nodes.object = n;
					nodes.startIndex = nodes.valuesList.size - childCount;
					leaves.object = n;
					leaves.startIndex = leaves.valuesList.size - sourceCount;
					popElements = true;
				}
				if (nodeClass.isInstance (n))
				{
					int b = qs.abind (5, n);
					if (b != QueryState.BINDING_MISMATCHED)
					{
						try
						{
							int b2 = qs.abind (4, leaves);
							if (b2 != QueryState.BINDING_MISMATCHED)
							{
								try
								{
									qs.amatch (2, nodes, consumer, arg);
								}
								finally
								{
									if (b2 == QueryState.BINDING_PERFORMED)
									{
										qs.unbind (4);
									}
								}
							}
						}
						finally
						{
							if (b == QueryState.BINDING_PERFORMED)
							{
								qs.unbind (5);
							}
						}
					}
				}
				if (popElements)
				{
					nodes.valuesList.setSize (nodes.startIndex);
					leaves.valuesList.setSize (leaves.startIndex);
				}
			}
		}
		finally
		{
			stack.setSize (sp);
			istack.setSize (isp);
		}
	}

}
