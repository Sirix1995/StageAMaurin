package de.grogra.ray;

/**
 * Implement this interface with a object processing functionality
 * that is performed when scene graph is traversed.
 * 
 * @see de.grogra.ray.TRSceneGraph
 * @author Micha
 */
public interface RTSceneVisitor {

	/**
	 * Implement this function to process a scene object.
	 * 
	 * @param object a scene object. This may be a visible object or a light.
	 * @see de.grogra.ray.TRSceneGraph
	 */
	public void visitObject(RTObject object);
	
}
