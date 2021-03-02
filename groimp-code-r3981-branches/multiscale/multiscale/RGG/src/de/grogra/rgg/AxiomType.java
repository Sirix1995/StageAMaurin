package de.grogra.rgg;

/**
 * AxiomType is a node class. 
 * It is intended to be connected to a TypeRoot via a branch edge when reset() is called on RGGGraph.
 * It is the axiom for the type graph.
 * 
 * @author Yongzhi Ong
 * @since 23 March 2012
 *
 */
public final class AxiomType extends de.grogra.graph.impl.Node
{
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	static
	{
		$TYPE = new NType (new AxiomType ());
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
		return new AxiomType ();
	}

//enh:end
}