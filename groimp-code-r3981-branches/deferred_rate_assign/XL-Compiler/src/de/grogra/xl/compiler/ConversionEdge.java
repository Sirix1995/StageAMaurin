package de.grogra.xl.compiler;

import de.grogra.reflect.Method;

/**
 * Contains information about a possible conversion from one
 * type to another type. 
 *  
 * @author Reinhard Hemmerling
 */
public class ConversionEdge
{
	/**
	 * All edges emitted from one node are connected
	 * in a singly linked list. 
	 */
	ConversionEdge next;
	
	/**
	 * The target node of the conversion.
	 */
	ConversionNode target;
	
	/**
	 * Conversion method. 
	 */
	Method method;

	public Method getMethod ()
	{
		return method;
	}

	public void setMethod (Method method)
	{
		this.method = method;
	}

	public ConversionEdge getNext ()
	{
		return next;
	}

	public void setNext (ConversionEdge next)
	{
		this.next = next;
	}

	public ConversionNode getTarget ()
	{
		return target;
	}

	public void setTarget (ConversionNode target)
	{
		this.target = target;
	}
}
