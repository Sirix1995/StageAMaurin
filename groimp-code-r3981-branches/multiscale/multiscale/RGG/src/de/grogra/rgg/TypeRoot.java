package de.grogra.rgg;

/**
 * TypeRoot is a node class. 
 * It is intended to be connected to a RGGRoot via a branch edge.
 * It signifies the root node of a type graph for the RGGGraph.
 * 
 * @author Yongzhi Ong
 * @since 23 March 2012
 *
 */
public final class TypeRoot extends de.grogra.graph.impl.Node
{
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

//enh:end
}