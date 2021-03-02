package de.grogra.gpuflux.scene.volume;

import java.io.IOException;
import java.util.Vector;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Variables;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public class FluxPolygon extends FluxPrimitive
{
	private int[] idx = new int[3];
	private boolean parallel;
	private Vector<FluxVertex> vertices;
	Vector3d normal = new Vector3d();
	Matrix4f world2obj;

	public FluxPolygon(int idx1, int idx2, int idx3, Vector<FluxVertex> vertices,
			boolean parallel, Matrix4f world2obj) {
		super();
		
		idx[0] = idx1;
		idx[1] = idx2;
		idx[2] = idx3;
		
		//finish();
		
		this.world2obj = world2obj;
		this.parallel = parallel;
		this.vertices = vertices;
	}

	public void finish()
	{
		FluxVertex v0 = vertices.get( idx[0] );
		FluxVertex v1 = vertices.get( idx[1] );
		FluxVertex v2 = vertices.get( idx[2] );
		
		Vector3d d1 = new Vector3d();
		Vector3d d2 = new Vector3d();
		
		d1.sub( v1.point , v0.point );
		d2.sub( v2.point , v0.point );
		
		normal.cross(d1, d2);
				
		normal.normalize();
	}
	
	@Override
	public void getExtent(BoundingBox3d bb, Variables temp) {
		
		Point3d v0 =vertices.get(idx[0]).point;
		Point3d v1 =vertices.get(idx[1]).point;
		Point3d v2 =vertices.get(idx[2]).point;
		
		bb.extent(v0);
		bb.extent(v1);
		bb.extent(v2);		
		
		if( parallel ){
			Point3d edge1 = temp.tmpPoint0;//new Point3d();
			Point3d edge2 = temp.tmpPoint1;//new Point3d();
			
			edge1.sub(v1,v0);
			edge2.sub(v2,v0);
			
			Point3d v3 = temp.tmpPoint2;//new Point3d();
			v3.add( edge1, edge2 );
			v3.add( v0 );
			
			bb.extent(v3);
		}
	}

	@Override
	public void serialize(ComputeByteBuffer out) throws IOException {
		serialize( out, parallel?PRIM_PARALLEL:PRIM_TRIANGLE, world2obj );
		
		FluxVertex v0 = vertices.get( idx[0] );
		FluxVertex v1 = vertices.get( idx[1] );	
		FluxVertex v2 = vertices.get( idx[2] );
				
		out.write( new Point3f(v0.point) );
		out.write( new Point3f(v1.point) );
		out.write( new Point3f(v2.point) );
		
		out.write( new Point3f(normal) );
		
		out.write( new Point2f(v0.uv) );
		out.write( new Point2f(v1.uv) );
		out.write( new Point2f(v2.uv) );

		out.write( new Point3f(v0.normal) );
		out.write( new Point3f(v1.normal) );
		out.write( new Point3f(v2.normal) );
	}

	public Object clone()
	{
		return new FluxPolygon( idx[0] , idx[1] , idx[2], vertices, parallel, world2obj );
	}

	public void shiftIndex(int offset) {
		idx[0] += offset;
		idx[1] += offset;
		idx[2] += offset;
	}
	
	public void setWorld2Obj(Matrix4f world2obj) {
		this.world2obj = world2obj;
	}

}