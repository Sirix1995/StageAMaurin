
package de.grogra.xl.compiler;

import java.util.HashMap;
import java.util.Vector;

import de.grogra.reflect.Method;
import de.grogra.reflect.Type;

public class ConversionGraph
{
	/**
	 * Stores references to all nodes in the graph.
	 * The keys are the binary names of the types,
	 * the values are the instances of the nodes for each type.
	 */
	final HashMap nodes = new HashMap ();

	ConversionNode getNode (Type type)
	{
		return (ConversionNode) nodes.get (type.getBinaryName ());
	}

	ConversionNode addNode (Type type, ConversionNode node)
	{
		return (ConversionNode) nodes.put (type.getBinaryName (), node);
	}

	/**
	 * Add a new edge between the source and the destination types.
	 * The edge that was created is returned.
	 * @param source
	 * @param destination
	 */
	ConversionEdge addConversion (Type source, Type destination, Method m)
	{
		ConversionEdge result = null;

		// obtain the nodes for source and destination types
		ConversionNode sourceNode = getNode (source);
		ConversionNode destinationNode = getNode (destination);

		// check if both nodes existed
		if ((sourceNode != null) && (destinationNode != null))
		{
			// find the edge between those nodes
			result = sourceNode.findEdge (destinationNode);
		}
		else
		{
			// create nodes if necessary
			if (sourceNode == null)
			{
				sourceNode = new ConversionNode (source);
				addNode (source, sourceNode);
			}
			if (destinationNode == null)
			{
				destinationNode = new ConversionNode (destination);
				addNode (destination, destinationNode);
			}
		}

		// check if no edge was found
		if (result == null)
		{
			// so create an edge
			result = sourceNode.createEdge (destinationNode, m);
		}

		// also define a conversion to the interfaces which destination implements
		for (int i = 0; i < destination.getDeclaredInterfaceCount (); i++)
		{
			addConversion (source, destination.getDeclaredInterface (i), m);
		}
		
		// also define a conversion to the supertype of destination
		Type supertype = destination.getSupertype ();
		if (supertype != null)
		{
			addConversion (source, supertype, m);
		}

		return result;
	}

	
	/**
	 * Do a breath-first-search from the source node to find the
	 * shortest path to the target node. The search iterates for
	 * each number of conversions needed, starting at 0. The
	 * iteration is performed until at least one possible conversion
	 * is found. Then all conversions needing the same minimal
	 * number of conversion steps is returned.
	 * Each conversion path is stored as a vector, containing a
	 * sequence of ConversionNodes.
	 * 
	 * @return
	 */
	Vector[] getAllShortestPath(ConversionNode source, ConversionNode target)
	{
		return null;
	}

	public HashMap getNodes ()
	{
		return nodes;
	}
}
