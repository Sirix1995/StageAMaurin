package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.graph.Cache.Entry;
import de.grogra.imp3d.PolygonArray;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * This class encapsulates a static PolygonArray. Polygonalization is just
 * making a copy of the stored polygon array to the output array.
 *
 * @author Reinhard Hemmerling
 */
public class PolygonMesh extends PolygonsBase {

	/**
	 * generated serial version id
	 */
	private static final long serialVersionUID = -4160866965967445749L;

	protected int[] indices;
	protected float[] v;
	protected float[] n;
	protected float[] t;

	protected int stamp;

	public PolygonMesh() {

	}

	public final float[] getVertexData() {

		return v;
	}

	public final float[] getNormalData() {

		return n;
	}

	public final float[] getTextureData() {

		return t;
	}

	public final int[] getIndexData() {

		return indices;
	}

	/**
	 * Computes max and min values of vertex data. Needed for BoundingBox
	 * computation.
	 *
	 * @param m
	 *          Use can use m to transform all vertices to global coordinates.
	 * @return Array{MaxX, MinX, MaxY, MinY, MaxZ, MinZ}
	 */
	public float[] computeMaxMin( Matrix4d m ) {

		if (m == null) return computeMaxMin();
		float[] result = new float[6];
		boolean first = true;
		for (int i = 0; i <= indices.length - 3; i += 3) {// iterating over triangles
			Point3d v0 = new Point3d( v[indices[i] * 3] , v[indices[i] * 3 + 1] , v[indices[i] * 3 + 2] );
			Point3d v1 = new Point3d( v[indices[i + 1] * 3] , v[indices[i + 1] * 3 + 1] , v[indices[i + 1] * 3 + 2] );
			Point3d v2 = new Point3d( v[indices[i + 2] * 3] , v[indices[i + 2] * 3 + 1] , v[indices[i + 2] * 3 + 2] );
			PolygonMesh.multiplyMatrixWithTuple3d( m , v0 );
			PolygonMesh.multiplyMatrixWithTuple3d( m , v1 );
			PolygonMesh.multiplyMatrixWithTuple3d( m , v2 );

			if (first) {
				result[0] = (float) v0.x;
				result[1] = (float) v0.x;
				result[2] = (float) v0.y;
				result[3] = (float) v0.y;
				result[4] = (float) v0.z;
				result[5] = (float) v0.z;
				first = false;
			}

			Point3d[] vt = { v0 , v1 , v2 };
			for (int j = 0; j < 3; j++) {
				Point3d v = vt[j];
				if (v.x > result[0]) result[0] = (float)v.x;
				if (v.x < result[1]) result[1] = (float)v.x;
				if (v.y > result[2]) result[2] = (float)v.y;
				if (v.y < result[3]) result[3] = (float)v.y;
				if (v.z > result[4]) result[4] = (float)v.z;
				if (v.z < result[5]) result[5] = (float)v.z;
			}
		}

		return result;
	}

	/**
	 * Computes max and min values of vertex data. Needed for BoundingBox
	 * computation.
	 *
	 * @return Array{MaxX, MinX, MaxY, MinY, MaxZ, MinZ}
	 */
	public float[] computeMaxMin() {

		float[] result = new float[6];
		boolean first = true;
		int i = 0;
		for (float a : this.v) {
			if (i == 0) {
				//xValue
				if (first) {
					result[0] = a;
					result[1] = a;
				} else {
					if (a > result[0]) result[0] = a;
					if (a < result[1]) result[1] = a;
				}
			}
			if (i == 1) {
				//yValue
				if (first) {
					result[2] = a;
					result[3] = a;
				} else {
					if (a > result[2]) result[2] = a;
					if (a < result[3]) result[3] = a;
				}
			}
			if (i == 2) {
				//zValue

				if (first) {
					result[4] = a;
					result[5] = a;

					first = false;
				} else {
					if (a > result[4]) result[4] = a;
					if (a < result[5]) result[5] = a;
				}

				i = -1;
			}
			i++;
		}
		return result;
	}


	public void polygonize( ContextDependent source , GraphState gs , PolygonArray out , int flags , float flatness ) {

		final int vertexCount = v.length / 3;

		// prepare output polygon array
		out.init( 3 );
		out.edgeCount = 3;
		out.planar = true;
		out.closed = false;
		out.usePolygonNormals = false;
		out.userObject = null;

		// copy data to output array
		out.polygons.addAll( indices , 0 , indices.length );
		out.vertices.addAll( v , 0 , v.length );

		// check if normal vector needs to be calculated
		if ((flags & Polygons.COMPUTE_NORMALS) != 0) {
			out.normals.clear( );
			// check if normals were set
			if (n != null) {
				// then copy normals into polygon array
				for (int i = 0; i < n.length; i++) {
					out.normals.add( (byte) (n[i] * 127) );
				}
			} else {
				// no normals were set, so compute them
				out.computeNormals( );
				// and cache the result
				// n = out.normals.toArray();
			}
		}

		// check if texture coordinates need to be calculated
		if ((flags & Polygons.COMPUTE_UV) != 0) {
			out.uv.clear( );
			// check if texture coordinates were set
			if (t != null) {
				// if so, then copy texture coordinates into polygon array
				out.uv.addAll( t , 0 , t.length );
			} else {
				// if not then just generate dummy data
				for (int i = 0; i < vertexCount; i++) {
					out.uv.push( 0 , 0 );
				}
			}
		}
	}

	public void setIndexData( IntList indexData ) {

		this.indices = indexData.toArray( );
		incrementStamp( );
	}

	public void setVertexData( FloatList vertexData ) {

		this.v = vertexData.toArray( );
		incrementStamp( );
	}

	public void setNormalData( float[] normalData ) {

		this.n = normalData;
		incrementStamp( );
	}

	public void setTextureData( float[] textureData ) {

		this.t = textureData;
		incrementStamp( );
	}

	public void incrementStamp() {

		stamp++;
	}

	public void writeStamp( Entry cache , GraphState gs ) {

		cache.write( System.identityHashCode( this ) );
		cache.write( getStamp( ) );
	}

	public int getStamp() {

		return stamp;
	}


	/**
	 * Multiplies matrix with point and writes result into point.
	 *
	 * @param matrix
	 * @param point
	 */
	public static void multiplyMatrixWithTuple3d( Matrix4d matrix , Tuple3d point ) {

		double x = point.x;
		double y = point.y;
		double z = point.z;
		double w = 1;

		point.x = matrix.m00 * x + matrix.m01 * y + matrix.m02 * z + matrix.m03;
		point.y = matrix.m10 * x + matrix.m11 * y + matrix.m12 * z + matrix.m13;
		point.z = matrix.m20 * x + matrix.m21 * y + matrix.m22 * z + matrix.m23;
		w = matrix.m30 * x + matrix.m31 * y + matrix.m32 * z + matrix.m33;

		point.x = point.x / w;
		point.y = point.y / w;
		point.z = point.z / w;
	}

}
