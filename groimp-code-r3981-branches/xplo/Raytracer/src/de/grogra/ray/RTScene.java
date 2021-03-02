package de.grogra.ray;

/**
 * This class represents a scene of 3d objects. 
 * It gives access to all objects and lights in this scene. 
 * 
 * To receive an array of all scene shadeable objects or of all light 
 * use the methodes de.grogra.ray.Raytracer.getShadeables(RTScene scene) or
 * de.grogra.ray.Raytracer.getLights(RTScene scene).
 * 
 * @see de.grogra.ray.Raytracer.getShadeables
 * @see de.grogra.ray.Raytracer.getLights
 */
public interface RTScene {
	
	/**
	 * Provides an access to all objects in this scene.
	 * For each object the methode RTSceneVisitor.visitObject(RTObject object)
	 * is called.
	 * 
	 * @param visitor a visitor object that can perform a proccessing with 
	 * a concrete scene object.
	 * @see de.grogra.ray.RTSceneVisitor
	 */
	public void traversSceneObjects(RTSceneVisitor visitor);
	
	
	/**
	 * Provides an access to all light objects in this scene.
	 * For each light object the methode 
	 * RTSceneVisitor.visitObject(RTObject object) is called.
	 * 
	 * @param visitor a visitor object that can perform a proccessing with 
	 * a concrete scene light.
	 * @see de.grogra.ray.RTSceneVisitor
	 */
	public void traversSceneLights(RTSceneVisitor visitor);
	
	
	/**
	 * Returns the number of shadeable (visible) objects in the scene.
	 * 
	 * @return number of shadeable objects
	 */
	public int getShadeablesCount();
	
	
	/**
	 * Returns the number of light in this scene.
	 * 
	 * @return number of scene lights 
	 */
	public int getLightsCount();
	
	
	/**
	 * Returns a modification stamp for the scene graph. Each modification
	 * increments the value, so that the test whether some modification
	 * occured can be simply performed on values of the stamp.
	 * 
	 * @return a stamp for the whole graph
	 */
	public int getStamp ();
	
	
	public Object getGraph();
}
