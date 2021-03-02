package de.grogra.animation.handler.sgnodereference;

import de.grogra.graph.impl.Node;

public class StorageNode extends Node {

	
	final public static int ANIM_STORAGE_EDGE = Node.MIN_UNUSED_SPECIAL_OF_TARGET;
	final public static int ANIM_NODE_EDGE = Node.MIN_UNUSED_SPECIAL_OF_SOURCE;
	
	public StorageNode() {
		
	}
	
	
	private static void initType() {
		$TYPE.declareSpecialEdge(ANIM_STORAGE_EDGE, "AnimationStorageEdge", new Node[0]);
		$TYPE.declareSpecialEdge(ANIM_NODE_EDGE, "AnimationNodeEdge", new Node[0]);
	}

	
	//enh:insert initType();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new StorageNode ());
		initType();
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
		return new StorageNode ();
	}

//enh:end
	
}
