
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

import de.grogra.reflect.Type;
import de.grogra.xl.property.CompiletimeModel.Property;

/**
 * A <code>CompileModel</code> is used by an XL compiler to
 * parametrize the access to graph-like data sources in queries. 
 * This mechanism allows XL to be used for a variety of
 * data sources, e.g., trees, graphs or hypergraphs. Implementations
 * have to provide a suitable mapping of the specific structure
 * of the data source in order to define an easy-to-use model.
 * <p>
 * A <code>CompiletimeModel</code> defines the specificity of a
 * data source at compile-time. This has to be accompanied by an implementation
 * of a {@link RuntimeModel} that is used at run-time.
 * The correct run-time model instance is obtained by invocations of
 * {@link RuntimeModelFactory#modelForName} with the name
 * returned by {@link #getRuntimeName()} as parameter.  
 * <p>
 * A comprehensive specification of the behaviour of <code>Model</code>
 * is given by the specification of the XL programming language.
 * 
 * @author Ole Kniemeyer
 */
public interface CompiletimeModel
{

	/**
	 * Determines whether a value of the given type has to be wrapped before
	 * it is added as a node to a {@link Graph}.
	 * 
	 * @param type a value type
	 * @return <code>true</code> iff a wrapper is needed for <code>type</code>
	 */
	boolean needsWrapperFor (Type<?> type);


	/**
	 * Return the type of wrappers for values of the given type. This method
	 * is only invoked when {@link #needsWrapperFor} returns <code>true</code>. 
	 * 
	 * @param type a value type 
	 * @return the corresponding wrapper type
	 */
	Type<?> getWrapperTypeFor (Type<?> type);


	/**
	 * Specifies the base type of nodes of this model. This type is used
	 * where no specific type is available, e.g., for the node predicate
	 * "." and for the ends of standard edges.
	 *  
	 * @return base type of nodes
	 */
	Type<?> getNodeType ();


	/**
	 * Specifies the compile-time type of edges for a given type. This
	 * method returns the type to which an expression of compile-time type
	 * <code>type</code> should be cast before it is interpreted as
	 * an edge expression. 
	 * 
	 * @param type the type of the expression
	 * @return the type of the resulting edge expression 
	 */
	Type<?> getEdgeTypeFor (Type<?> type);
	

	/**
	 * Specifies the type of standard edges. This method returns the type
	 * which is used for the representation of standard edges in this model.
	 * This has to be the type of the (possibly unwrapped) values returned by
	 * {@link #getStandardEdgeFor}.
	 * 
	 * @return the type of standard edges
	 */
	Type<?> getEdgeType ();
	

	/**
	 * Returns a constant value representing a standard edge. The returned
	 * value represents the given standard <code>edge</code>
	 * ({@link de.grogra.xl.query.EdgePattern#ANY_EDGE},
	 * {@link de.grogra.xl.query.EdgePattern#BRANCH_EDGE},
	 * {@link de.grogra.xl.query.EdgePattern#SUCCESSOR_EDGE} or
	 * {@link de.grogra.xl.query.EdgePattern#REFINEMENT_EDGE}) for this
	 * model. It is used as argument to constructor invocations of
	 * {@link de.grogra.xl.query.EdgePattern}. Primitive values have to
	 * be wrapped as usual.
	 * 
	 * @param edge <code>int</code>-value encoding a standard edge
	 * @return corresponding representation for this model
	 */
	java.io.Serializable getStandardEdgeFor (int edge);

	/**
	 * Returns the type of producers for right-hand sides
	 * of rules for this model.
	 * 
	 * @return type of producers
	 */
	Type<? extends Producer> getProducerType ();

	/**
	 * Returns the type of query states to be used for queries
	 * for this model.
	 * 
	 * @return type of producers
	 */
	Type<? extends QueryState> getQueryStateType ();

	/**
	 * Returns the unwrapping property of a wrapper type.
	 * <code>wrapperType</code> is the type of a wrapper. If there
	 * is a property declared in this type which can be used to obtain
	 * the wrapped argument, this property is returned, otherwise
	 * <code>null</code>. 
	 *
	 * @param wrapperType the type of a wrapper
	 * @return the unwrapping property for this type
	 */
	Property getWrapProperty (Type<?> wrapperType);


	/**
	 * Defines the name of the corresponding
	 * {@link RuntimeModel}. This name
	 * is used during run-time in invocations of
	 * {@link RuntimeModelFactory#modelForName}
	 * in order to obtain the {@link RuntimeModel}
	 * suitable for the code that is compiled within this compile-time model. 
	 * 
	 * @return the name of the corresponding run-time model
	 */
	String getRuntimeName ();
}
