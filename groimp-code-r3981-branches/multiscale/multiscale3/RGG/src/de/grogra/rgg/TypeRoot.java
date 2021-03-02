package de.grogra.rgg;

import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;

/**
 * This class represents the root node of a type graph in a 
 * {@link de.grogra.graph.impl.GraphManager}.
 * 
 * The enumeration or access of nodes must exclude all children and descendents
 * of this node unless explicitly indicated. This prevents rules from affecting
 * the type graph unless explicitly indicated.
 * 
 * This class is part of the extension of XL for multiscale modelling.
 * 
 * @since 18-02-2013
 * @author yong
 *
 */
public class TypeRoot extends de.grogra.graph.impl.Node{
	//enh:insert
	//enh:begin
	// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new TypeRoot ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new TypeRoot ();
	}
}
