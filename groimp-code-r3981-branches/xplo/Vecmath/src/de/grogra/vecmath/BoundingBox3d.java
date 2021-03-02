package de.grogra.vecmath;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class BoundingBox3d {
	private Point3d min, max;
	
	public BoundingBox3d()
	{
		empty();
	}
	
	public double volume()
	{
		return
			Math.max( 0, max.x - min.x )*
			Math.max( 0, max.y - min.y )*
			Math.max( 0, max.z - min.z );
	}
	
	public double area()
	{
		double dx = Math.max( 0, max.x - min.x );
		double dy = Math.max( 0, max.y - min.y );
		double dz = Math.max( 0, max.z - min.z );
		return 2 * (dx*dy + dx*dz + dy*dz);
	}
	
	public boolean isEmpty() { return volume() == 0; };
	
	public void empty() {
		max = new Point3d( Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY );
		min = new Point3d( Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );
	}
	
	public void extent( Tuple3d t )
	{
		max.max(t);
		min.min(t);
	}
	
	public void extent( BoundingBox3d b )
	{
		max.max(b.max);
		min.min(b.min);
	}

	public Tuple3d getMax() {
		return max;
	}
	
	public Tuple3d getMin() {
		return min;
	}
	
	public void getCenter( Tuple3d c ) {
		c.interpolate(min, max, 0.5);
	}
	
	public double getRadius() {
		Vector3d d = new Vector3d();
		d.sub(max,min);
		return d.length() / 2;
	}
	
	public BoundingBox3d clone()
	{
		BoundingBox3d bb = new BoundingBox3d();
		bb.min = (Point3d) min.clone();bb.max = (Point3d) max.clone();
		return bb;
	}
}
