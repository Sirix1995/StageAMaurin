package de.grogra.gpuflux.scene.volume;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import de.grogra.ray2.radiosity.Vector3d;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class FluxVertex {
	public Point3d point;
	public Vector3d normal;
	public Point2d uv;
	
	public FluxVertex(  Point3d point , Vector3d normal , Point2d uv )
	{
		this.point = (Point3d) point.clone();
		this.normal = (Vector3d) normal.clone();
		this.uv = (Point2d) uv.clone();
	}
	public void transform(Matrix4d m, Matrix4d n) {
		m.transform(point);
		n.transform(normal);
	};
	
	public Object clone()
	{
		return new FluxVertex( (Point3d)point.clone(), (Vector3d)normal.clone(), (Point2d)uv.clone() );
	}
}
