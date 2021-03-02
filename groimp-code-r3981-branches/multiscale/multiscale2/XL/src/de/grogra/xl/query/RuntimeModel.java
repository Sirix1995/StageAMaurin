
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
import de.grogra.reflect.TypeLoader;

/**
 * A <code>RuntimeModel</code> is used by the XL run-time system
 * as an interface to the concrete relational data source in use.
 * It corresponds to a compile-time model which was used at
 * compile-time: The invocation of
 * {@link de.grogra.xl.query.CompiletimeModel#getRuntimeName()} returns a name,
 * which is passed to {@link RuntimeModelFactory#modelForName} in order to
 * obtain the corresponding <code>RuntimeModel</code>.
 * <p>
 * This interface contains methods related to unwrapping
 * of values, for determining if a value represents a node value,
 * and for the retrieval of a {@link Graph} associated with this model
 * and the current thread. Roughly speaking, a <code>RuntimeModel</code>
 * provides a set of operations, a <code>Graph</code> provides the data.
 * <p>
 * A comprehensive specification of the behaviour of <code>RuntimeModel</code>
 * is given by the specification of the XL programming language.
 *
 * @author Ole Kniemeyer
 */
public interface RuntimeModel
{

	/**
	 * Initializes this model. This method is invoked by
	 * {@link RuntimeModelFactory#modelForName} after a new <code>RuntimeModel</code>
	 * instance has been created. The format of <code>params</code>
	 * depends on implementations of <code>RuntimeModel</code>.
	 * 
	 * @param params initialization parameters, possibly <code>null</code>
	 */
	void initialize (String params);


	/**
	 * Returns the graph that shall be used in the context of the
	 * current thread. This is needed by graph-related XL statements
	 * which do not explicitly specify the graph to use. 
	 * 
	 * @return the current graph for this model 
	 */
	Graph currentGraph ();


	/**
	 * Tests if the object is a wrapper for values of the given type. 
	 * 
	 * @param object the potential wrapper to be tested
	 * @param type the value type
	 * @return <code>true</code> iff <code>object</code> is a wrapper for values of type <code>type</code>
	 */
	boolean isWrapperFor (Object object, Type<?> type);

	
	/**
	 * Determines if <code>value</code> represents a valid node
	 * for this run-time model.
	 * 
	 * @param value the value to be tested
	 * @return <code>true</code> iff the value represents a node
	 */
	boolean isNode (Object value);


/*!!
#foreach ($type in $types)
$pp.setType($type)

	/**
	 * Extracts the $type value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 $C
	$type unwrap$pp.Type (Object wrapper);
	
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	/**
	 * Extracts the boolean value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	boolean unwrapBoolean (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the byte value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	byte unwrapByte (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the short value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	short unwrapShort (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the char value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	char unwrapChar (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the int value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	int unwrapInt (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the long value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	long unwrapLong (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the float value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	float unwrapFloat (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the double value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	double unwrapDouble (Object wrapper);
	
// generated
// generated
	/**
	 * Extracts the Object value of a wrapper.
	 * 
	 * @param wrapper the wrapper
	 * @return the wrapped value of the wrapper 
	 */
	Object unwrapObject (Object wrapper);
	
//!! *# End of generated code

}
