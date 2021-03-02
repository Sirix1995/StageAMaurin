
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

package de.grogra.rgg.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

import de.grogra.chem.Model;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Extent;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp3d.objects.GRSVertex;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.IntersectionType;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.rgg.Multiplicity;
import de.grogra.util.IOWrapException;
import de.grogra.xl.impl.base.GraphQueue;
import de.grogra.xl.impl.base.RuntimeModel;
import de.grogra.xl.impl.queues.Queue;
import de.grogra.xl.impl.queues.QueueCollection;
import de.grogra.xl.impl.queues.QueueDescriptor;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.HasModel;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.NodeData;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.util.ObjectList;

@HasModel(Compiletime.class)
public class RGGGraph extends de.grogra.xl.impl.base.Graph
{
	static final int QUEUES_MAGIC_START = 0x83e9fa3b;

	static final int QUEUES_MAGIC_END = 0xb1910cba;

	static final int QUEUE_MAGIC = 0x840d920b;

	final GraphManager manager;

	//yong 23 mar 2012 - type graph
	boolean doNotEnumerateTypeGraph;
	//yong 23 mar 2012 - end
	
	public static final String XL_GRAPH = "de.grogra.rgg.model.RGGGraph";


	RGGGraph (Runtime model, GraphManager manager)
	{
		super (model);
		this.manager = manager;
		
		//yong 23 mar 2012 - type graph
		doNotEnumerateTypeGraph=true;
		//yong 23 mar 2012 - end
		
		manager.setProperty (XL_GRAPH, this);
	}

	static class RGGThreadData extends ThreadData
	{
		Runnable productionCallback = null;
		boolean removeInterpretiveNodes;
		int visibleExtents = (1 << Node.LAST_EXTENT_INDEX) - 1;
		ObjectList<GRSVertex> newGRSVertices = new ObjectList<GRSVertex> ();
	}

	@Override
	protected ThreadData createThreadData ()
	{
		return new RGGThreadData ();
	}

	RGGThreadData getThreadData0 ()
	{
		return (RGGThreadData) getThreadData ();
	}

	public ObjectList<GRSVertex> getNewGRSVertices ()
	{
		return getThreadData0 ().newGRSVertices;
	}

	static RGGGraph get (GraphManager manager)
	{
		return (RGGGraph) manager.getProperty (XL_GRAPH);
	}

	public Producer createProducer (QueryState match)
	{
		return new RGGProducer (match);
	}


	public void setProductionCallback (Runnable c)
	{
		getThreadData0 ().productionCallback = c;
	}

	
	public GraphManager getGraphManager ()
	{
		return manager;
	}

	
	public void enumerateNodes (Type type, QueryState qs, int index,
							 MatchConsumer consumer, int arg)
	{
		RGGThreadData d = getThreadData0 ();
		ObjectList<Extent> stack = qs.userStack0;
		int sp = stack.size;
		ObjectList<Extent> base = qs.userStack1;
		int bp = base.size;
		try
		{
			Type t;
			if ((type instanceof IntersectionType)
				&& (type.getDeclaredInterfaceCount () == 1)
				&& Reflection.equal (type.getSupertype (), Node.$TYPE))
			{
				type = type.getDeclaredInterface (0);
				t = type;
			}
			else
			{
				t = Reflection.getBinaryType (type);
			}
			if ((t.getModifiers () & Member.INTERFACE) != 0)
			{
				base.push (manager.getExtent (Node.$TYPE));
				while (base.size () > bp)
				{
					Extent e = base.pop ();
					if (Reflection.isSupertype (t, e.getType ()))
					{
						stack.push (e);
					}
					else
					{
						e.getSubExtents (base);
					}
				}
			}
			else
			{
				stack.push (manager.getExtent (t));
			}
			if (Reflection.equal (t, type))
			{
				type = null;
			}
			while (stack.size () > sp)
			{
				Extent e = stack.pop ();
				e.getSubExtents (stack);
				for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++)
				{
					if (((1 << i) & d.visibleExtents) != 0)
					{
						for (Node n = e.getFirstNode (i); n != null; n = e.getNextNode (n))
						{
							assert n.getGraph () != null : n;
							if ((type == null) || type.isInstance (n))
							{
								//yong 23 mar 2012 - exclude type graph nodes from matches
								if(doNotEnumerateTypeGraph)
								{
									boolean isPartOfTypeGraph=false;
									for (Edge edge = n.getFirstEdge (); edge != null; edge = edge.getNext (n))
									{
										Node tgt = edge.getTarget();
										if ( (tgt == n) /*&&  (edge.testEdgeBits (Graph.BRANCH_EDGE))*/ )
										{
											Node s = edge.getSource();
											NType srcNType = s.getNType();
											if(srcNType!=null)
											{
												if((srcNType.getSimpleName()).equals("TypeRoot"))
													isPartOfTypeGraph=true;
											}
										}
									}
									if(!isPartOfTypeGraph)
										qs.amatch (index, n, consumer, arg);
								}
								//yong 23 mar 2012 - end
								else
									qs.amatch (index, n, consumer, arg);
							}
						}
					}
				}
			}
		}
		finally
		{
			stack.setSize (sp);
			base.setSize (bp);
		}
/*		Node n = (Node) graph.getRoot (GraphManager.MAIN_GRAPH);
		if (n == null)
		{
			return;
		}
		int h = graph.allocateBitMark (false);
		XList stack = qs.userStack;
		int sp = stack.size;
		try 
		{
			n.setBitMark (h, true);
			stack.push (n);
			while (stack.size > sp)
			{
				n = (Node) stack.pop ();
				if (type.isInstance (n))
				{
					qs.amatch (index, n, consumer, arg);
				}
				for (Edge e = n.getFirstEdge (); e != null; e = e.getNext (n))
				{
					Node m;
					if ((e.getEdgeBits () != 0)
						&& !(m = e.getNeighbor (n)).setBitMark (h, true))
					{
						stack.push (m);
					}
				}
			}
		}
		finally
		{
			graph.disposeBitMark (h, true);
			stack.setSize (sp);
		}
		//*/
	}


	@Override
	public boolean canEnumerateNodes (Type type)
	{
		return Reflection.isSuperclassOrSame (Node.$TYPE, type)
			|| ((type.getModifiers () & Member.INTERFACE) != 0);
	}


	public Object getRoot ()
	{
		return de.grogra.rgg.RGGRoot.getRoot (manager);
	}


	public boolean canEnumerateEdges (EdgeDirection dir, boolean constEdge, Serializable edge)
	{
		return true;
	}

	public void enumerateEdges
		(Object node, EdgeDirection dir, Type edgeType, QueryState qs, int toIndex,
		 int patternIndex, java.io.Serializable pattern, int matchIndex, MatchConsumer consumer, int arg)
	{
		Node n = (Node) node;
		switch (edgeType.getTypeId ())
		{
			case TypeId.INT:
				int bits;
				if (patternIndex >= 0)
				{
					bits = qs.ibound (patternIndex);
				}
				else if (pattern != null)
				{
					bits = ((Number) pattern).intValue ();
				}
				else
				{
					bits = -1;
				}
				for (Edge e = n.getFirstEdge (), f; e != null; e = f)
				{
					f = e.getNext (n);
					Node o;
					if (dir == EdgeDirection.UNDIRECTED)
					{
						o = e.getNeighbor (n);
					}
					else
					{
						o = (dir == EdgeDirection.BACKWARD) ? e.getSource () : e.getTarget ();
						if (o == n)
						{
							continue;
						}
					}
					int b = e.getEdgeBits ();
					if (RuntimeModel.testEdgeBits (b, bits))
					{
						int binding = qs.ibind (matchIndex, b);
						if (binding == QueryState.BINDING_MISMATCHED)
						{
							continue;
						}
						try
						{
							if (dir == EdgeDirection.BOTH)
							{
								int b2 = o.getEdgeBitsTo (n);
								if (!RuntimeModel.testEdgeBits (b2, bits))
								{
									continue;
								}
								switch (qs.ibind (matchIndex + 1, b2))
								{
									case QueryState.BINDING_MISMATCHED:
										break;
									case QueryState.BINDING_MATCHED:
										qs.amatch (toIndex, o, consumer, arg);
										break;
									case QueryState.BINDING_PERFORMED:
										try
										{
											qs.amatch (toIndex, o, consumer, arg);
										}
										finally
										{
											qs.unbind (matchIndex + 1);
										}
										break;
								}
							}
							else
							{
								qs.amatch (toIndex, o, consumer, arg);
							}
						}
						finally
						{
							if (binding == QueryState.BINDING_PERFORMED)
							{
								qs.unbind (matchIndex);
							}
						}
					}
				}
				break;
			case TypeId.OBJECT:
				if ((patternIndex >= 0) || (pattern != null))
				{
					return;
				}
				for (Edge e = n.getFirstEdge (), f; e != null; e = f)
				{
					f = e.getNext (n);
					Node x;
					if (dir == EdgeDirection.UNDIRECTED)
					{
						x = e.getNeighbor (n);
					}
					else
					{
						x = (dir == EdgeDirection.BACKWARD) ? e.getSource () : e.getTarget ();
						if (x == n)
						{
							continue;
						}
					}
					if (!edgeType.isInstance (x))
					{
						continue;
					}
					boolean forward = e.isSource (n);
					if (e.testEdgeBits (forward ? Graph.EDGENODE_IN_EDGE : Graph.EDGENODE_OUT_EDGE))
					{
						for (Edge e2 = x.getFirstEdge (), f2; e2 != null; e2 = f2)
						{
							f2 = e2.getNext (x);
							Node o = forward ? e2.getTarget () : e2.getSource ();
							if ((o == x) || (o == n))
							{
								continue;
							}
							if (e2.testEdgeBits (forward ? Graph.EDGENODE_OUT_EDGE : Graph.EDGENODE_IN_EDGE))
							{
								if (dir == EdgeDirection.BOTH)
								{
									if (forward)
									{
										if (!RuntimeModel.testEdgeBits (o.getEdgeBitsTo (x), Graph.EDGENODE_IN_EDGE)
											|| !RuntimeModel.testEdgeBits (x.getEdgeBitsTo (n), Graph.EDGENODE_OUT_EDGE))
										{
											continue;
										}
									}
									else
									{
										if (!RuntimeModel.testEdgeBits (n.getEdgeBitsTo (x), Graph.EDGENODE_IN_EDGE)
											|| !RuntimeModel.testEdgeBits (x.getEdgeBitsTo (o), Graph.EDGENODE_OUT_EDGE))
										{
											continue;
										}
									}
								}
								int binding = qs.abind (matchIndex, x);
								if (binding == QueryState.BINDING_MISMATCHED)
								{
									continue;
								}
								try
								{
									if (dir == EdgeDirection.BOTH)
									{
										switch (qs.abind (matchIndex + 1, x))
										{
											case QueryState.BINDING_MISMATCHED:
												break;
											case QueryState.BINDING_MATCHED:
												qs.amatch (toIndex, o, consumer, arg);
												break;
											case QueryState.BINDING_PERFORMED:
												try
												{
													qs.amatch (toIndex, o, consumer, arg);
												}
												finally
												{
													qs.unbind (matchIndex + 1);
												}
												break;
										}
									}
									else
									{
										qs.amatch (toIndex, o, consumer, arg);
									}
								}
								finally
								{
									if (binding == QueryState.BINDING_PERFORMED)
									{
										qs.unbind (matchIndex);
									}
								}
							}
						}
					}
				}
				break;
		}
	}

	@Override
	public GraphQueue createQueue (QueueCollection qc, QueueDescriptor descr)
	{
		return new QueueImpl (descr, qc, qc.getQueue (PropertyQueue.MAKE_PERSISTENT));
	}


	@Override
	protected void beginModifications ()
	{
		if (manager.getMainState ().getContext ().isCurrent ())
		{
			manager.getActiveTransaction ();
		}
	}


	@Override
	protected void commitModifications ()
	{
		if (manager.getMainState ().getContext ().isCurrent ())
		{
			manager.getTransaction (false).commitAll ();
		}
	}


	@Override
	protected Object getPredecessor (Object node)
	{
		Node start = (Node) node;
		for (Edge e = start.getFirstEdge (); e != null; e = e.getNext (start))
		{
			Node pred = e.getSource ();
			if ((pred != start)
				&& e.testEdgeBits (Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE))
			{
				return pred;
			}
		}
		return null;
	}


	void productionStateEnded (RGGProducer ps)
	{
		Runnable c = getThreadData0 ().productionCallback;
		if (c != null)
		{
			c.run ();
		}
	}

	
	public void removeInterpretiveNodes ()
	{
		RGGThreadData d = getThreadData0 ();
		Extent e = manager.getExtent (InterpretiveMark.$TYPE);
		Transaction t = manager.getActiveTransaction ();
		for (int i = 0; i <= Node.LAST_EXTENT_INDEX; i++)
		{
			if (((1 << i) & d.visibleExtents) != 0)
			{
				for (Node mark = e.getFirstNode (i); mark != null; mark = e.getNextNode (mark))
				{
					Node start = null;
					Node end = null;
					for (Edge edge = mark.getFirstEdge (); edge != null; edge = edge.getNext (mark))
					{
						Node s = edge.getSource ();
						if (s == mark)
						{
							end = edge.getTarget ();
						}
						else
						{
							start = s;
						}
					}
					if ((start == null) || (end == null))
					{
						throw new IllegalStateException ();
					}
		
					Node left = null;
					int bits = 0;
					for (Edge edge = start.getFirstEdge (); edge != null; edge = edge.getNext (start))
					{
						if (edge.testEdgeBits (Graph.CONTAINMENT_EDGE))
						{
							left = edge.getSource ();
							if (left == start)
							{
								throw new IllegalStateException ();
							}
							bits = edge.getEdgeBits () & ~Graph.CONTAINMENT_EDGE;
							break;
						}
					}
					if (left == null)
					{
						throw new IllegalStateException ();
					}
					
					Node interpreted = null;
					for (Edge edge = end.getFirstEdge (); edge != null; edge = edge.getNext (end))
					{
						if (edge.testEdgeBits (Graph.CONTAINMENT_END_EDGE))
						{
							interpreted = edge.getTarget ();
							if (interpreted == end)
							{
								throw new IllegalStateException ();
							}
							break;
						}
					}
					if (interpreted == null)
					{
						throw new IllegalStateException ();
					}
					
					start.removeAll (t);
					if (end != start)
					{
						end.removeAll (t);
					}
		
					left.addEdgeBitsTo (interpreted, bits, t);
				}
			}
		}
	}

	public void removeInterpretiveNodesOnDerivation ()
	{
		getThreadData0 ().removeInterpretiveNodes = true;
	}

	
	public void setVisibleExtents (int indices)
	{
		getThreadData0 ().visibleExtents = indices;
	}


	@Override
	public long derive ()
	{
		RGGThreadData d = getThreadData0 ();
		if (d.removeInterpretiveNodes)
		{
			d.removeInterpretiveNodes = false;
			removeInterpretiveNodes ();
		}
		return super.derive ();
	}


	public static void writeQueues (ObjectList<? extends Queue> list, DataOutput out) throws IOException
	{
		out.writeInt (QUEUES_MAGIC_START);
		out.writeInt (list.size);
		for (int i = 0; i < list.size; i++)
		{
			Queue q = list.get (i);
			if (q instanceof TransferableQueue)
			{
				out.writeInt (QUEUE_MAGIC);
				out.writeUTF (q.getDescriptor ().getClass ().getName ());
				((TransferableQueue) q).write (out);
			}
			else
			{
				throw new IOException ("Queue " + q + " is not transferable");
			}
		}
		out.writeInt (QUEUES_MAGIC_END);
	}


	private static void consume (DataInput in, int value) throws IOException
	{
		int v = in.readInt ();
		if (v != value)
		{
			throw new IOException ("Expected " + value + " instead of " + v);
		}
	}


	public static ObjectList<Queue> readQueues (QueueCollection qc, DataInput in, ClassLoader loader) throws IOException
	{
		consume (in, QUEUES_MAGIC_START);
		int n = in.readInt ();
		ObjectList<Queue> list = new ObjectList<Queue> (n);
		while (--n >= 0)
		{
			consume (in, QUEUE_MAGIC);
			QueueDescriptor<TransferableQueue> d;
			try
			{
				d = (QueueDescriptor<TransferableQueue>) Class.forName (in.readUTF (), false, loader).newInstance (); 
			}
			catch (Exception e)
			{
				throw new IOWrapException (e);
			}
			TransferableQueue q = d.createQueue (qc);
			q.read (in);
			list.add (q);
		}
		consume (in, QUEUES_MAGIC_END);
		return list;
	}

	
	public static void addQueues (QueueCollection qc, ObjectList<? extends Queue> queues)
	{
		for (int i = 0; i < queues.size; i++)
		{
			qc.addQueue (queues.get (i));
		}
	}
	
	//yong - 15 mar 2012 - for scaling queries
	public boolean canScaleNodes(Type t)
	{
		return true;
	}
	
	public void scaleNodes(QueryState qs)
	{
		NodeData nodeData = qs.getFirstNodeData();
		
		//no propogation necessary if no nodes are matched
		if(nodeData==null)
			return;
		
		
			
		//for each matched node, find the refinement parents and children
		//add references of the refinement parents and children to the NodeData instance
		while(nodeData!=null)
		{
			scaleNodesInternal(nodeData);
			nodeData = (NodeData)nodeData.listNext;
		}
	}	
	
	private void scaleNodesInternal(NodeData nodeData)
	{
		Node thisNode = ((Node)(nodeData.node));
		
		scaleNodesInternalRecurMacro(nodeData, thisNode/*, true*/);
		ObjectList<Node> microList = new ObjectList<Node>();
		microList.add(thisNode);
		scaleNodesInternalRecurMicro(nodeData, microList/*, false*/);
	}
	
	private void scaleNodesInternalRecurMacro(NodeData nodeData, Node node/*, boolean towardsMacroScale*/)
	{
		for (Edge edge = node.getFirstEdge (); edge != null; edge = edge.getNext (node))
		{
			Node t = edge.getTarget();
			if ( (t == node) &&  (edge.testEdgeBits (Graph.REFINEMENT_EDGE)) )
			{
				Node s = edge.getSource();
				nodeData.addMacroNode(s);
				
				scaleNodesInternalRecurMacro(nodeData, s);
			}
		}
	}
	
	private void scaleNodesInternalRecurMicro(NodeData nodeData, ObjectList nodes)
	{
		ObjectList<Node> microList = new ObjectList<Node>();
		for(int i=0; i<nodes.size();++i)
		{
			Node node = (Node)nodes.get(i);
			for (Edge edge = node.getFirstEdge (); edge != null; edge = edge.getNext (node))
			{
				Node s = edge.getSource();
				if ( (s == node) &&  (edge.testEdgeBits (Graph.REFINEMENT_EDGE)) )
				{
					Node t = edge.getTarget();
					nodeData.addMicroNode(t);
					
					microList.add(t);
				}
			}
		}
		
		if(microList.size()>0)
		{
			nodeData.addMicroScaleIndex();
			scaleNodesInternalRecurMicro(nodeData, microList);
		}
	}
	//yong - 15 mar 2012 - end scaling queries
	
	//yong - 20 mar 2012 - scaling producer add nodes
	/**
	 * Gets hierarchy of refinement parents of given node. 
	 * @param node
	 * @return List of lists of nodes at each macro scale nodes
	 */
	public ObjectList<Object> getMacroNodes(Object node)
	{
		ObjectList<Object> macroNodes = new ObjectList<Object>();
		Node n = (Node)node;
		
		getMacroNodesRecur(macroNodes,n);
		
		if(macroNodes.size()>0)
			return macroNodes;
		else
			return null;
	}
	
	private void getMacroNodesRecur(ObjectList<Object> macroNodes, Node node)
	{ 
		for (Edge edge = node.getFirstEdge (); edge != null; edge = edge.getNext (node))
		{
			Node t = edge.getTarget();
			if ( (t == node) &&  (edge.testEdgeBits (Graph.REFINEMENT_EDGE)) )
			{
				Node s = edge.getSource();
				macroNodes.add(s);
				getMacroNodesRecur(macroNodes,s);
			}
		}
	}
	//yong - 20 mar 2012 - end scaling producer
	
	//yong - 21 mar 2012 - scaling 
	/**
	 * Determines if an existing macro node can refine to a newly added node.
	 * The macro being the refinement parent of the new node's predecessor or G- node on the left-most-side.
	 * 'G-' refers to the graph without the matched (and not in context) nodes.
	 * @param macroNode
	 * @param newNode
	 * @return
	 */
	public boolean canAbsorbL(Object macroNode, Object newNode, ObjectList currRefinements)
	{
//		int maxMul = 2;
//		int currConnect = 0;
//		if(this.getModel().isNode(macroNode))
//		{
//			Node macNode = (Node)macroNode;
//			
//			for (Edge edge = macNode.getFirstEdge (); edge != null; edge = edge.getNext (macNode))
//			{
//				Node s = edge.getSource();
//				if ( (s == macNode) &&  (edge.testEdgeBits (Graph.REFINEMENT_EDGE)) )
//				{
//					currConnect++;
//				}
//			}
//		}
//		
//		if(currConnect>maxMul)
//			return false;
//		else
//			return true;
		
		Multiplicity mul = typeGraphMultiplicity(macroNode, newNode);
		if(mul==null)
			return false;
		
		//max number of edges from macro to new node
		int tgtMax = mul.getMultiplicityTgtMax();
		
		//if unable to obtain current refinement references from producer, no absorbing
		if(currRefinements==null)
			return false;
		
		//if all refinements were removed, macro node is deleted and can no longer be referenced
		if(currRefinements.size()==0)
			return false;
		
		//if max number of refinements <= current refinements, no more absorbing
		if(tgtMax <= currRefinements.size())
			return false;
		//else absorb new node
		else
			return true;
	}
	
	/**
	 * Determines if an existing macro node can refine to a newly added node.
	 * The macro being the refinement parent of the new node's successor or G- node on the right-most-side.
	 * 'G-' refers to the graph without the matched (and not in context) nodes.
	 * @param macroNode
	 * @param newNode
	 * @return
	 */
	public boolean canAbsorbR(Object macroNode, Object newNode, ObjectList currRefinements)
	{
//		int maxMul = 2;
//		int currConnect = 0;
//		if(this.getModel().isNode(macroNode))
//		{
//			Node macNode = (Node)macroNode;
//			
//			for (Edge edge = macNode.getFirstEdge (); edge != null; edge = edge.getNext (macNode))
//			{
//				Node s = edge.getSource();
//				if ( (s == macNode) &&  (edge.testEdgeBits (Graph.REFINEMENT_EDGE)) )
//				{
//					currConnect++;
//				}
//			}
//		}
//		
//		if(currConnect>maxMul)
//			return false;
//		else
//			return true;
		return false;
	}
	
	//yong 23 mar 2012 - type graph functionality
	public Node typeGraphNode(Object node)
	{
		if(node!=null)
		{
			Node n = (Node)node;
			Object typeRootObj = getTypeRoot();
			if(typeRootObj!=null)
			{
				Node typeRoot = (Node)typeRootObj;
				//locate newNode in type graph
				for (Edge edge = typeRoot.getFirstEdge (); edge != null; edge = edge.getNext (typeRoot))
				{
					Node s = edge.getSource();
					Node t = edge.getTarget();
					if(s == typeRoot)
					{
						NType typeGraphType = t.getNType();
						NType nodeType = n.getNType();
						if(typeGraphType.equals(nodeType))
						{
							return t;
						}
					}
				}
			}
		}
		return null;
	}
	
	public ObjectList typeGraphRefineParents(Object node)
	{
		Node nodeInTypeGraph = typeGraphNode(node);
		if(nodeInTypeGraph!=null)
		{
			ObjectList<Node> multipliNodes = new ObjectList<Node>();
			for (Edge edge = nodeInTypeGraph.getFirstEdge (); edge != null; edge = edge.getNext (nodeInTypeGraph))
			{
				Node t = edge.getTarget();
				if((t == nodeInTypeGraph) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
				{
					multipliNodes.add(edge.getSource());
				}
			}
			
			ObjectList<Node> refineParents = new ObjectList<Node>();
			for(int i=0; i<multipliNodes.size(); ++i)
			{
				Node mulNode = multipliNodes.get(i);
				for (Edge edge = mulNode.getFirstEdge (); edge != null; edge = edge.getNext (mulNode))
				{
					Node t = edge.getTarget();
					if((t == mulNode) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
					{
						refineParents.add(edge.getSource());
					}
				}
			}
			
			return refineParents;
		}
		return null;
	}
	
	public ObjectList typeGraphRefineChildren(Object node)
	{
		Node nodeInTypeGraph = typeGraphNode(node);
		if(nodeInTypeGraph!=null)
		{
			ObjectList<Node> multipliNodes = new ObjectList<Node>();
			for (Edge edge = nodeInTypeGraph.getFirstEdge (); edge != null; edge = edge.getNext (nodeInTypeGraph))
			{
				Node s = edge.getSource();
				if((s == nodeInTypeGraph) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
				{
					multipliNodes.add(edge.getTarget());
				}
			}
			
			ObjectList<Node> refineChildren = new ObjectList<Node>();
			for(int i=0; i<multipliNodes.size(); ++i)
			{
				Node mulNode = multipliNodes.get(i);
				for (Edge edge = mulNode.getFirstEdge (); edge != null; edge = edge.getNext (mulNode))
				{
					Node s = edge.getSource();
					if((s == mulNode) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
					{
						refineChildren.add(edge.getTarget());
					}
				}
			}
			
			return refineChildren;
		}
		return null;
	}
	
	public Multiplicity typeGraphMultiplicity(Object srcNode, Object tgtNode)
	{
		Node nodeInTypeGraph = typeGraphNode(srcNode);
		Node tgtNodeInTypeGraph = typeGraphNode(tgtNode);
		
		if(nodeInTypeGraph!=null)
		{
			ObjectList<Node> multipliNodes = new ObjectList<Node>();
			for (Edge edge = nodeInTypeGraph.getFirstEdge (); edge != null; edge = edge.getNext (nodeInTypeGraph))
			{
				Node s = edge.getSource();
				if((s == nodeInTypeGraph) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
				{
					multipliNodes.add(edge.getTarget());
				}
			}
			
			for(int i=0; i<multipliNodes.size(); ++i)
			{
				Node mulNode = multipliNodes.get(i);
				for (Edge edge = mulNode.getFirstEdge (); edge != null; edge = edge.getNext (mulNode))
				{
					Node s = edge.getSource();
					if((s == mulNode) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
					{
						Node t = edge.getTarget();
						if(t==tgtNodeInTypeGraph)
						{
							if(mulNode instanceof Multiplicity)
								return (Multiplicity)mulNode;
						}
					}
				}
			}
			
		}
		return null;
	}
	
	//yong 23 mar 2012 - END type graph functionality
	
	/**
	 * Creates new macro node for newly added node
	 * @param newNode
	 * @return
	 */
	public Object newMacro(Object newNode)
	{
		// yong 23 mar 2012 - using type graph
		Node newMacroNode = null;
		if(newNode!=null)
		{
			newMacroNode = ((Node)newNode).newInstanceMacro();
		}
		
		Object typeRootObj = getTypeRoot();
		if(typeRootObj!=null)
		{
			ObjectList<Node> refineParents = typeGraphRefineParents(newNode);
			
			//type graph determines macro node
			if(newMacroNode==null)
			{
				if(refineParents==null)
					return null;
				if(refineParents.size()==0)
					return null;
				
				Node representative = refineParents.get(0);
				try 
				{
					//return representative.getClass().newInstance();
					return representative.clone(true);
				} 
				catch (Throwable t)
				{
					return null;
				}
			}
			//user has overridden newInstanceMacro, perform check
			else
			{
				if(refineParents==null)
					return null;
				if(refineParents.size()==0)
					return null;
				
				NType newMacroNodeType = newMacroNode.getNType();
				for(int i=0; i<refineParents.size();++i)
				{
					Node typeNode = refineParents.get(i);
					NType typeNodeType = typeNode.getNType();
					if(newMacroNodeType.equals(typeNodeType))
						return newMacroNode;
				}
				return null;
			}
		}
		else
			return null;
		//yong 23 mar 2012 - end
	}
	
	/**
	 * Creates new micro nodes for newly added node
	 * @param newNode
	 * @return
	 */
	public ObjectList newMicro(Object newNode)
	{		
		// yong 23 mar 2012 - using type graph
		ObjectList<Node> newMicroNodes = null;
		if(newNode!=null)
		{
			newMicroNodes = ((Node)newNode).newInstanceMicro();
		}
		
		Object typeRootObj = getTypeRoot();
		if(typeRootObj!=null)
		{
			ObjectList<Node> refineChildren = typeGraphRefineChildren(newNode);
			
			//type graph determines macro node
			if(newMicroNodes==null)
			{
				if(refineChildren==null)
					return null;
				if(refineChildren.size()==0)
					return null;
				
				newMicroNodes = new ObjectList<Node>();
				
				Node representative = refineChildren.get(0);
				try 
				{
					Multiplicity mul = typeGraphMultiplicity(newNode,representative);
					if(mul!=null)
					{
						int mulMax = mul.getMultiplicityTgtMax();
						if(mulMax<=0)
						{
							newMicroNodes.add(representative.clone(true));
						}
						else
						{
							for(int i=0; i<mulMax; ++i)
							{
								newMicroNodes.add(representative.clone(true));
							}
						}
						return newMicroNodes;
					}
					return null;
				} 
				catch (Throwable t)
				{
					return null;
				}
			}
			//user has overridden newInstanceMacro, perform check
			else
			{
				if(refineChildren==null)
					return null;
				if(refineChildren.size()==0)
					return null;
				
				for(int j=0; j<newMicroNodes.size(); ++j)
				{
					NType microNodeType = newMicroNodes.get(j).getNType();
					boolean foundTypeMatch=false;
					for(int i=0; i<refineChildren.size();++i)
					{
						Node typeNode = refineChildren.get(i);
						NType typeNodeType = typeNode.getNType();
						if(microNodeType.equals(typeNodeType))
							foundTypeMatch=true;
					}
					if(!foundTypeMatch)
						return null;
				}
				
				return newMicroNodes;
			}
		}
		else
			return null;
		//yong 23 mar 2012 - end
	}
	//yong - 21 mar 2012 - end
	
	//yong 23 mar 2012 - type graph
	public void setDoNotEnumerateTypeGraph(boolean doNotEnumerate)
	{
		doNotEnumerateTypeGraph=doNotEnumerate;
	}
	
	public Object getTypeRoot()
	{
		Object rootObj = getRoot();
		if(rootObj!=null)
		{
			Node rootNode = (Node)rootObj;
			for (Edge edge = rootNode.getFirstEdge (); edge != null; edge = edge.getNext (rootNode))
			{
				Node s = edge.getSource();
				if ( (s == rootNode) /*&&  (edge.testEdgeBits (Graph.BRANCH_EDGE))*/ )
				{
					Node t = edge.getTarget();
					NType tgtNType = t.getNType();
					if(tgtNType!=null)
					{
						if((tgtNType.getSimpleName()).equals("TypeRoot"))
							return t;
					}
				}
			}
		}
		
		return null;
	}
	//yong 23 mar 2012 - end
	
	//yong 26 mar 2012 - get refinement nodes
	public ObjectList getRefinements(Object node)
	{
		ObjectList<Node> refineNodes = new ObjectList<Node>();
		Node currNode = (Node) node;
		for (Edge edge = currNode.getFirstEdge (); edge != null; edge = edge.getNext (currNode))
		{
			Node s = edge.getSource();
			if((s == currNode) && (edge.testEdgeBits (Graph.REFINEMENT_EDGE)))
			{
				refineNodes.add(edge.getTarget());
			}
		}
		return refineNodes;
	}
	//yong 26 mar 2012 - end
}
