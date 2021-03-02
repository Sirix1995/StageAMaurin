package de.grogra.gpuflux.scene.filter;

import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.ShareableBase;

/**
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.0                                       
 * @since       2011.0824                                 
 *
 * The ObjectFilter interface filters objects while traversing the scene graph.
 * For each object the filter method is called. The enter and leave callback methods are called when entering or leaving a subtree.
 */

public abstract class ObjectFilter extends ShareableBase {
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (ObjectFilter.class);
		$TYPE.validate ();
	}

//enh:end
	
	public abstract boolean filter(Object obj);

	public abstract void enter(Object obj);
	public abstract void leave(Object obj);
}
