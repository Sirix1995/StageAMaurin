package de.grogra.ext.x3d.objects;

import de.grogra.imp3d.objects.Null;

/**
 * A groimp node class. This node is visible in the scene graph
 * and is the root of all x3d nodes.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DScene extends Null {

	/**
	 * Constructor.
	 */
	public X3DScene() {
		super();
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new X3DScene ());
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
		return new X3DScene ();
	}

//enh:end
}
