
package de.grogra.xl.compiler;

import java.util.Vector;

import de.grogra.reflect.Method;
import de.grogra.reflect.Type;

/**
 * Contains information about all possible conversions from one
 * type to other types. 
 * 
 * @author Reinhard Hemmerling
 */
public class ConversionNode
{
	/**
	 * Head of a singly linked list of all possible conversions
	 * from the type represented by this node to other types.
	 */
	ConversionEdge edges;

	/**
	 * The type information for the type represented by this node. 
	 */
	Type type;

	ConversionNode (Type type)
	{
		this.type = type;
	}

	ConversionEdge createEdge (ConversionNode target, Method m)
	{
		// create the edge
		ConversionEdge edge = new ConversionEdge ();

		// set values of edge
		edge.next = edges;
		edge.target = target;
		edge.method = m;

		// put edge onto list
		edges = edge;

		// return the newly created edge
		return edge;
	}

	/**
	 * Returns the ConversionEdge for a conversion from the type
	 * represented by this node to the type represented by the
	 * target node or null if no such conversion existed.
	 * @param target
	 * @return
	 */
	ConversionEdge findEdge (ConversionNode target)
	{
		ConversionEdge result = edges;
		while (result != null)
		{
			if (result.target == target)
			{
				break;
			}
			result = result.next;
		}
		return result;
	}

	/**
	 * Returns all possible ConversionEdges for a conversion from the
	 * type represented by this node to the type represented by the
	 * target node or null if no such conversion existed.
	 * Because not all functions are accessible from the same context,
	 * multiple conversions between the same types may coexist.
	 * @param target
	 * @return
	 */
	ConversionEdge[] findEdges (ConversionNode target)
	{
		ConversionEdge[] result = null;
		Vector conversions = new Vector ();
		ConversionEdge current = edges;
		while (current != null)
		{
			if (current.target == target)
			{
				conversions.add (current);
			}
			current = current.next;
		}
		if (!conversions.isEmpty ())
		{
			result = (ConversionEdge[]) conversions
					.toArray (new ConversionEdge[0]);
		}
		return result;
	}
}
