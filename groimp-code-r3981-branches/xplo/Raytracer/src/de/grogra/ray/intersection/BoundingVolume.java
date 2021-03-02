package de.grogra.ray.intersection;

import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;

public interface BoundingVolume {

	public float getMinX();
	public float getMaxX();
	public float getMinY();
	public float getMaxY();
	public float getMinZ();
	public float getMaxZ();
	
	public boolean hasIntersection(Ray ray);
	public boolean isInsideBox(Vector3f minValues, Vector3f maxValues);
	
}
