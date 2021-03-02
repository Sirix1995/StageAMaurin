
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

package de.grogra.graph;

import de.grogra.util.*;

/**
 * A <code>Graph</code> represents a graph-like structure. It consists of 
 * nodes, connected by directed edges. Both nodes and edges are objects,
 * their classes are not restricted by this interface.
 * 
 * <h3>Topological Structure</h3>
 * 
 * The nodes and edges of a graph are obtained in the following way:
 * <ul>
 * <li>The method {@link #getRootKeys()} returns an array of strings, the
 * <em>root keys</em>. These are used as arguments to
 * {@link #getRoot(String)}. The returned value is a node, namely the root
 * of the subgraph identified by the root key. The root key
 * {@link #MAIN_GRAPH} is a predefined root key, it identifies the root
 * of the main graph which should always be present. Further root keys
 * may be defined, depending on the <code>Graph</code>.
 * <li>For every node <em>n</em>, there is a linked list of edges with
 * unspecified order. The first edge of this linked list is obtained by
 * {@link #getFirstEdge(Object)} with <em>n</em> passed as argument, the
 * next edges are obtained by {@link #getNextEdge(Object, Object)} with
 * the previous edge as first argument and <em>n</em> as second.
 * Thus, a loop over all edges of <em>n</em> in a graph <em>g</em>
 * can be implemented as follows:
 * <pre>
 * for (Object e = g.getFirstEdge(n); e != null; e = g.getNextEdge(e, n))
 * {
 *     // do something with the current edge e
 * }
 * </pre>
 * <li>Given an edge <em>e</em>, its source and target nodes are obtained by
 * the method {@link #getSourceNode(Object)} and
 * {@link #getTargetNode(Object)}.
 * </ul>
 * 
 * <h3>Attributes</h3>
 * 
 * Besides topological information, a <code>Graph</code> provides
 * attribute-like information for nodes and edges:
 * <ul>
 * <li>Every node has a unique id which is obtained by {@link #getId(Object)}.
 * The inverse method {@link #getNodeForId(long)} returns the node identified
 * by the given id.
 * <li>Every node and every edge has a name which is obtained by
 * {@link #getName(Object, boolean)}. The name is not necessarily unique,
 * the inverse method {@link #getObjectForName(boolean, String)} returns
 * one of the nodes or edges with the given name.
 * <li>Every edge has a set of <em>edge bits</em>
 * ({@link #getEdgeBits(Object)}). These are stored in a single
 * <code>int</code>-value which is interpreted
 * as a set of sub-edges in the following way:
 *  <ul>
 *  <li>The bits 0 to 7 (masked by {@link #SPECIAL_EDGE_MASK})
 *  represent the edge's special sub-edge.
 *  If these bits, interpreted as a byte, have the value 0, no special
 *  sub-edge is present. Otherwise, the special sub-edge identified by
 *  this byte is present in this edge. The value 255 (all bits set)
 *  is reserved for special purposes. Note that at most one special edge
 *  may exist at a time between an ordered tuple of nodes.
 *  <li>The other 24 bits (bits 8 to 31) represent 24 possible
 *  sub-edges, each indicated by the presence of its specific bit in the
 *  <code>int</code>-value. It is up to the concrete graph to specify the
 *  meaning of the sub-edges. The <code>Graph</code> interface provides
 *  three standard meanings: {@link #SUCCESSOR_EDGE}, {@link #BRANCH_EDGE},
 *  and {@link #CONTAINMENT_EDGE}.
 *  </ul>
 * <li>Every node and every edge has a set of attributes which is returned by
 * {@link #getAttributes(Object, boolean)}. Each attribute is represented by
 * an instance of {@link de.grogra.graph.Attribute},
 * attribute values are read and written
 * on nodes and edges within the context of a
 * {@link de.grogra.graph.GraphState}.
 * </ul>
 * 
 * <h3>Threading Issues</h3>
 * 
 * In order to ensure a stable and predictable behaviour in the context
 * of multiple threads, the following rules have to be followed:
 * <ol>
 * <li>Modifications to a graph may only be performed within the context
 * of the <em>main graph state</em> (see {@link #getMainState()}), and
 * only when a write lock has been obtained by
 * (see {@link de.grogra.util.Lockable} which is extended by this interface).
 * <li>In principle, reading of structure and attributes can be done
 * without any special arrangement. However, it is safer to do this
 * when a read lock has been obtained
 * (again see {@link de.grogra.util.Lockable}). 
 * because then it is guaranteed that no other thread may modify the graph
 * during the invocation (if the other threads conform to these rules).
 * <li>The methods for adding and removing of event listeners in this
 * interface have to be implemented thread-safe.
 * <li>The implementation of this interface has to
 * ensure that its event listeners are notified about modifications
 * only within the context of the main graph state. 
 * </ol>
 * 
 * <h3>The Tree of a Graph</h3>
 * 
 * A graph defines a <em>tree pattern</em> (see {@link #getTreePattern()}).
 * Starting at the root of the main graph
 * ({@link #getRoot(String)}, {@link #MAIN_GRAPH}), this pattern is used
 * as a filter to construct a subgraph out of the whole graph. The subgraph
 * has to be a tree, but it is not necessarily a spanning tree for the whole
 * graph. The <em>parent attribute</em> (see {@link #getParentAttribute()})
 * of the graph has to be a derived attribute which has as value for every node
 * of the tree its parent edge and for every edge of the tree its parent
 * node. For objects which are not part of the tree the value is
 * <code>null</code>.
 * 
 * @author Ole Kniemeyer
 * @see de.grogra.graph.GraphState
 * @see de.grogra.graph.Attribute
 */
public interface Graph extends Lockable
{
	int SPECIAL_EDGE_OF_SOURCE_BIT = 128;

	/**
	 * The bit mask for the special edge in edge bits.
	 * 
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int SPECIAL_EDGE_MASK = 255;

	int EDGENODE_IN_EDGE = 1;
	int EDGENODE_OUT_EDGE = SPECIAL_EDGE_OF_SOURCE_BIT;


	int MIN_NORMAL_BIT_INDEX = 8;

	/**
	 * The bit mask indicating the presence of a successor edge in edge bits.
	 * This means that the target node of the edge is the successor
	 * (in some graph-dependent sense) of the source node of the edge.
	 * 
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int SUCCESSOR_EDGE = 0x0100;

	/**
	 * The bit mask indicating the presence of a branch edge in edge bits.
	 * This means that the target node of the edge is the first node of
	 * a branch (in some graph-dependent sense) originating at the source
	 * node of the edge.
	 * 
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int BRANCH_EDGE = 0x0200;

	/**
	 * The bit mask indicating the presence of a containment edge in edge bits.
	 * This means that the target node of the edge is contained
	 * (in some graph-dependent sense) in the source node of the edge.
	 * 
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int CONTAINMENT_EDGE = 0x0400;

	/**
	 * The bit mask indicating the presence in edge bits
	 * of an edge signalling "end of containment".
	 * 
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int CONTAINMENT_END_EDGE = 0x0800;

	int REFINEMENT_EDGE = 0x1000;

	/**
	 * The bit mask indicating the presence of a mark edge in edge bits.
	 * The precise meaning of a mark edge is not specified by this interface,
	 * it may be used for multiple purposes.
	 *  
	 * @see Graph
	 * @see #getEdgeBits(Object)
	 */
	int MARK_EDGE = 0x2000;
	
	int NOTIFIES_EDGE = 0x4000;

	int STD_EDGE_5 = 0x8000;
	int STD_EDGE_6 = 0x10000;
	int MIN_UNUSED_EDGE = 0x20000;
	
	int MIN_UNUSED_EDGE_BIT = 17;

	int RECTANGLE_SYMBOL = 0;
	int ROUND_RECTANGLE_SYMBOL = 1;
	int ELLIPSE_SYMBOL = 2;
	int RHOMBUS_SYMBOL = 3;


	/**
	 * Return value for {@link #getLifeCycleState} indicating that the object
	 * is persistent, i.e., it belongs the graph. 
	 */
	int PERSISTENT = 0;

	/**
	 * Return value for {@link #getLifeCycleState} indicating that the object
	 * has been persistent and is currently being deleted from the graph. 
	 */
	int PERSISTENT_DELETED = 1;
	
	/**
	 * Return value for {@link #getLifeCycleState} indicating that the object
	 * is transient, i.e., it does not belong to the graph. 
	 */
	int TRANSIENT = 2;

	/**
	 * The predefined root key which identifies the main graph.
	 * 
	 * @see Graph 
	 */
	String MAIN_GRAPH = "MainGraph";

	
	/**
	 * Returns the main graph state. The main graph state is the only
	 * graph state within which modifications to the graph may be done.
	 * The notification of event listeners is done in the context
	 * of this state, too.
	 * 
	 * @return this graph's main graph state
	 * @see Graph
	 */
	GraphState getMainState ();
	
	/**
	 * Returns a modification stamp for the whole graph. Each modification
	 * increments the value, so that the test whether some modification
	 * occured can be simply performed on values of the stamp.
	 * 
	 * @return a stamp for the whole graph
	 */
	int getStamp ();
	
	/**
	 * Returns the root keys for the graph.
	 * 
	 * @return an array of root keys
	 * @see Graph
	 * @see #getRoot(String)
	 */
	String[] getRootKeys ();

	/**
	 * Returns the root node for the given root key.
	 * 
	 * @param key a root key, one of {@link #getRootKeys()}
	 * @return the root node of the graph identified by <code>key</code>,
	 * or <code>null</code> if no such root node exists
	 * @see Graph
	 */
	Object getRoot (String key);

	/**
	 * Returns the first edge of the linked list of edges of
	 * <code>node</code>. 
	 * 
	 * @param node the common node of the edges of the linked list
	 * @return the first edge of the linked list
	 * @see Graph
	 * @see #getNextEdge(Object, Object)
	 */
	Object getFirstEdge (Object node);
	
	/**
	 * Returns the edge after <code>edge</code> in the linked list
	 * of edges of <code>node</code>.
	 * 
	 * @param edge the previous edge in the linked list
	 * @param node the common node of the edges of the linked list
	 * @return the next edge of the linked list
	 * @see Graph
	 * @see #getFirstEdge(Object)
	 */
	Object getNextEdge (Object edge, Object node);

	/**
	 * Returns the source node of <code>edge</code>.
	 * 
	 * @param edge an edge
	 * @return the source node
	 * @see Graph
	 */
	Object getSourceNode (Object edge);

	/**
	 * Returns the target node of <code>edge</code>.
	 * 
	 * @param edge an edge
	 * @return the target node
	 * @see Graph
	 */
	Object getTargetNode (Object edge);
	
	/**
	 * Returns the life cycle state of the given object as part of this graph.
	 * 
	 * @param object the object to test
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @return life cycle state, one of {@link #PERSISTENT}, {@link #PERSISTENT_DELETED},
	 * {@link #TRANSIENT}
	 */
	int getLifeCycleState (Object object, boolean asNode);
	
	/**
	 * Returns a unique identifier for the given <code>node</code>.
	 * 
	 * @param node a node
	 * @return the node's unique identifier
	 * @see #getNodeForId(long)
	 */
	long getId (Object node);
	
	/**
	 * Returns the node identified by <code>id</code>.
	 * 
	 * @param id an identifier
	 * @return the corresponding node, or <code>null</code> if <code>id</code>
	 * identifies no node
	 * @see #getId(Object)
	 */
	Object getNodeForId (long id);
	
	/**
	 * Returns a name for the given object. Names are not necessarily
	 * unique.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @return a name
	 * @see #getObjectForName(boolean, String)
	 */
	String getName (Object object, boolean asNode);
	
	/**
	 * Returns the object with the given name. If several such objects
	 * exist, one of them is chosen in an unspecified manner. If no such
	 * object exists, <code>null</code> is returned. 
	 * 
	 * @param node <code>true</code> if a node of the given name is to be found,
	 * <code>false</code> if an edge is to be found
	 * @param name the name of the object
	 * @return an object of the given kind (node or edge) with the given name,
	 * or <code>null</code> if no such object exists
	 * @see #getName(Object, boolean)
	 */
	Object getObjectForName (boolean node, String name);

	/**
	 * Returns the edge bits of an edge.
	 * 
	 * @param edge the edge
	 * @return the edge's edge bits
	 * @see Graph
	 */
	int getEdgeBits (Object edge);
	
	/**
	 * Returns the set of attributes which are available for the given object.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @return the object's attributes
	 */
	Attribute[] getAttributes (Object object, boolean asNode);
	
	/**
	 * Returns the set of attributes whose values depend on the given
	 * attribute <code>a</code> for the given <code>object</code>.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute
	 * @return the set of dependent attributes
	 */
	Attribute[] getDependent (Object object, boolean asNode, Attribute a);

	/**
	 * Returns an attribute accessor for the given attribute on the given
	 * object.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param attribute the attribute
	 * @return an accessor for the object's attribute value
	 */
	AttributeAccessor getAccessor (Object object, boolean asNode, Attribute attribute);
	
	Instantiator getInstantiator (Object node);
	
	void accept (Object startNode, Visitor visitor, ArrayPath placeInPath);

	java.util.Map getStateMap ();

	BooleanMap createBooleanMap ();

	<V> ObjectMap<V> createObjectMap ();
	
	/**
	 * Defines the derived attribute which has as value the parent object.
	 * 
	 * @return the parent attribute
	 * @see Graph
	 * @see #getTreePattern()
	 */
	ObjectAttribute getParentAttribute ();
	
	/**
	 * Defines the pattern used for the construction of this graph's tree.
	 * 
	 * @return an edge pattern
	 * @see Graph
	 * @see #getParentAttribute()
	 */
	EdgePattern getTreePattern ();
	
	SpecialEdgeDescriptor[] getSpecialEdgeDescriptors (Object node, boolean asSource);
	
	/**
	 * Returns a description for the given object. The type of
	 * the desired description (e.g., text, icon) is specified in the argument
	 * <code>type</code>; it is interpreted as in
	 * {@link Described#getDescription(String)}.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param type the type of description
	 * @return a description of the given type, or <code>null</code>
	 */
	Object getDescription (Object object, boolean asNode, String type);
	
	int getSymbol (Object object, boolean asNode);
	
	int getColor (Object object, boolean asNode);

	void addChangeBoundaryListener (ChangeBoundaryListener l);

	void removeChangeBoundaryListener (ChangeBoundaryListener l);
	
	void addAttributeChangeListener (AttributeChangeListener l);
	
	void removeAttributeChangeListener (AttributeChangeListener l);

	void addEdgeChangeListener (EdgeChangeListener l);

	void removeEdgeChangeListener (EdgeChangeListener l);

	void addAttributeChangeListener (Object object, boolean asNode, AttributeChangeListener l);

	void removeAttributeChangeListener (Object object, boolean asNode, AttributeChangeListener l);

	void addEdgeChangeListener (Object object, boolean asNode, EdgeChangeListener l);

	void removeEdgeChangeListener (Object object, boolean asNode, EdgeChangeListener l);
}
