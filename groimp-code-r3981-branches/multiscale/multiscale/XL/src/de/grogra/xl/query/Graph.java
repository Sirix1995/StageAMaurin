
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

package de.grogra.xl.query;

import java.io.Serializable;

import de.grogra.reflect.Type;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.XBitSet;

/**
 * A <code>Graph</code> represents a single relational data source for XL's
 * relational query and rewriting facilities within the context of a single
 * thread (i.e., the methods are not thread-safe). While a
 * {@link RuntimeModel} provides methods for obtaining information
 * and performing operations which are common to a set of
 * relational data sources with an equal structure, a
 * <code>Graph</code> provides methods which operate on a single instance
 * of these data sources within a fixed thread.
 * E.g., a <code>RuntimeModel</code> could represent
 * features of XML documents in general, while a <code>Graph</code> of this
 * model represents a single XML document. Thus, a <code>RuntimeModel</code>
 * provides a set of common operations, a <code>Graph</code> provides the data.
 * 
 * @author Ole Kniemeyer
 */
public interface Graph
{
	/**
	 * Returns this graph's run-time model.
	 * 
	 * @return the run-time model
	 */
	RuntimeModel getModel ();

	/**
	 * Creates a query state to be used for queries which use
	 * this graph. The returned query state has to be an instance
	 * of the type defined by {@link CompiletimeModel#getQueryStateType}
	 * of the corresponding compile-time model.
	 * 
	 * @return a new query state
	 */
	QueryState createQueryState ();
	

	/**
	 * Creates an instance of <code>Producer</code>. This factory
	 * method creates an instance of a subclass of
	 * {@link Producer} which will be exclusively used later on
	 * in the context of the current match represented by
	 * <code>qs</code>. The instance must be an
	 * instance of the class returned by
	 * {@link CompiletimeModel#getProducerType}
	 * for the corresponding compile-time model.
	 * <p>
	 * The usage of producers is specified by the
	 * XL programming language.
	 * 
	 * @param qs query state which holds current match for which the producer
	 * shall produce a replacement
	 * @return a producer for use in the context of <code>qs</code> 
	 */
	Producer createProducer (QueryState qs);
	

	Pattern.Matcher createMatcher
		(Pattern pred, XBitSet providedConstants, IntList neededConstantsOut);


	/**
	 * Can nodes of the specified <code>type</code> be enumerated? This
	 * method returns <code>true</code> iff this graph can enumerate
	 * nodes of the given <code>type</code> using the method
	 * {@link #enumerateNodes}.
	 * 
	 * @param type the type of nodes
	 * @return <code>true</code> iff nodes of <code>type</code> can be enumerated
	 */
	boolean canEnumerateNodes (Type type);


	/**
	 * This methods enumerates all nodes of this graph of the given
	 * type to the given <code>MatchConsumer</code> via the given query state.
	 * I.e., for every
	 * node <code>c</code> of this extent that has type <code>type</code>,
	 * the following statement is executed:
	 * <pre>
	 *     qs.amatch (tp, c, consumer, arg);
	 * </pre>
	 * 
	 * @param type the type of nodes
	 * @param qs the query state on which the <code>amatch</code>-method has to be invoked
	 * @param tp the index-parameter for the <code>amatch</code>-method
	 * @param consumer the consumer-parameter for the <code>amatch</code>-method
	 * @param arg the arg-parameter for the <code>amatch</code>-method
	 */
	void enumerateNodes (Type type, QueryState qs, int tp,
					  MatchConsumer consumer, int arg);


	boolean canEnumerateEdges (EdgeDirection dir, boolean constEdge, Serializable edge);


	void enumerateEdges (Object node, EdgeDirection dir, Type edgeType, QueryState qs,
						  int toIndex, int patternIndex,
						  java.io.Serializable pattern, int matchIndex, MatchConsumer consumer, int arg);


	/**
	 * Returns the root node of this graph.
	 *  
	 * @return the root
	 */
	Object getRoot ();
	
	/**
	 * This method determines if nodes of the specified <code>type</code> 
	 * have scaling relations specified in the schema.
	 * @param type
	 * <code>true</code> iff nodes of <code>type</code> can be related in scale relations
	 */
	boolean canScaleNodes (Type t);
	
	/**
	 * This method attempts to find the refinement parent nodes and the refinement children
	 * nodes of the matched nodes.
	 * The matched nodes are referenced in the QueryState.
	 * @param qs
	 */
	void scaleNodes(QueryState qs);
	
	/**
	 * Gets list of refinement nodes for the given node object
	 * @param node
	 * @return
	 */
	ObjectList getRefinements(Object node);
}
