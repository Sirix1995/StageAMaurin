package de.grogra.ray;

import de.grogra.ray.intersection.BoundingVolume;
import de.grogra.ray.intersection.IntersectionDescription;
import de.grogra.ray.shader.RTMedium;
import de.grogra.ray.shader.RTShader;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;


public interface RTObject {

	public float getDistance(Ray ray,RayContext context);
	public void getIntersectionDescription(IntersectionDescription desc);
	
	public boolean isShadeable();
	public boolean isSolid();
	public boolean isConvex();
	
//	public void setUserData(Object data);
	public RTObjectUserData getUserData();
	
	public BoundingVolume getBoundingVolume();
	//public boolean isInsideBox(Vector3f minValues, Vector3f maxValues);
	
	public RTShader getShader();
	public RTMedium getMedium();
	
	
	public class RTObjectUserData {
		public boolean isInside = false;
	}
	
}
