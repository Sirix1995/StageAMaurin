
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d;

import java.util.Arrays;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3f;

import de.grogra.vecmath.geom.Mesh;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.imp3d.objects.Attributes;

/**
 * This class represents a list of polygons.
 * 
 * @author Ole Kniemeyer
 */
public class PolygonArray extends VertexArray implements Mesh
{
	/**
	 * This list contains the uv coordinates. The coordinates
	 * are contained contiguously, with two <code>float</code>-values
	 * for each vertex.
	 */
	public FloatList uv = new FloatList ();


	/**
	 * This list contains the (unnormalized) normal vectors. The components
	 * are contained contiguously, with three <code>byte</code>-values
	 * for each vertex. Each <code>byte</code>-value <em>c</em> is to be
	 * interpreted as a floating-point vector component <em>f</em>
	 * in the range [-1, 1] using the formula
	 * <center>
	 * <em>f</em> = (2 <em>c</em> + 1) / 255
	 * </center>
	 */
	public ByteList normals = new ByteList ();


	/**
	 * Specifies the polygons. Each polygon is represented by a
	 * contiguous sequence of <code>edgeCount</code> indices
	 * into the other arrays (however note that if
	 * {@link #usePolygonNormals} is <code>true</code>, normal indices
	 * are specified by {@link #polygonNormals}).
	 * The polygons are placed one after
	 * another without space inbetween.
	 */
	public IntList polygons = new IntList ();


	/**
	 * Specifies the normal indices of polygons if
	 * {@link #usePolygonNormals} is <code>true</code>.
	 * For each entry in {@link #polygons}, i.e., a vertex,
	 * a corresponding entry has to
	 * be present which defines the index into {@link #normals}.
	 */
	public IntList polygonNormals = new IntList ();


	/**
	 * Indicates whether normal indices are specified separately
	 * by {@link #polygonNormals} or just like the other indices
	 * by {@link #polygons}. 
	 */
	public boolean usePolygonNormals = false;


	/**
	 * Defines the number of edges of the polygons in this list. All
	 * polygons have the same number of edges. <code>edgeCount</code>
	 * may only be 3 (triangles) or 4 (quadrilaterals).
	 */
	public int edgeCount;


	/**
	 * Flag indicating whether the polygons are <code>planar</code>. In
	 * the case of triangles, this is always the case. In the case of
	 * quadrilaterals, it is guaranteed that each quadrilateral will be planar
	 * (all vertices lie on a plane) if <code>planar</code> is <code>true</code>.
	 */
	public boolean planar;

	
	/**
	 * Enumeration value indicating the visible polygon sides. If this value
	 * is {@link Attributes#VISIBLE_SIDES_FRONT}, only front sides
	 * (as defined by the counter-clockwise order of vertices) are visible.
	 * If this is {@link Attributes#VISIBLE_SIDES_BACK}, only back sides
	 * are visible. For {@link Attributes#VISIBLE_SIDES_BOTH}, both sides
	 * are visible.
	 */
	public int visibleSides = Attributes.VISIBLE_SIDES_FRONT;

	/**
	 * Indicates whether this polygon is a closed surface or not. If it is
	 * closed, normal vectors have to point to the exterior.
	 */
	public boolean closed;
	
	@Override
	public void freeArrays ()
	{
		super.freeArrays ();
		uv.clear ();
		uv.trimToSize ();
		normals.clear ();
		normals.trimToSize ();
		polygons.clear ();
		polygons.trimToSize ();
		polygonNormals.clear ();
		polygonNormals.trimToSize ();
	}


	@Override
	void clear ()
	{
		super.clear ();
		uv.clear ();
		normals.clear ();
		polygons.clear ();
		polygonNormals.clear ();
		visibleSides = Attributes.VISIBLE_SIDES_FRONT;
	}

	
	private static byte f2b (float f)
	{
		return (f > 0) ? (byte) (127.5f * f) : (byte) (127.5f * f - 1);
	}


	/**
	 * Copies a vector in the list of normal vectors.
	 * 
	 * @param from the source index
	 * @param to the target index
	 */
	public void copyNormal (int from, int to)
	{
		from *= 3;
		to *= 3;
		if (normals.size < to + 3)
		{
			normals.setSize (to + 3);
		}
		byte[] a = normals.elements;
		a[to] = a[from];
		a[to + 1] = a[from + 1];
		a[to + 2] = a[from + 2];
	}


	/**
	 * Sets a normal vector. The vector is rescaled so that the
	 * maximum of the absolute values of its components is one.
	 * 
	 * @param index the index of the normal vector (note that the actual index
	 * into {@link #normals} is <code>3 * index</code>)
	 * @param x the x-component
	 * @param y the y-component
	 * @param z the z-component
	 */
	public void setNormal (int index, float x, float y, float z)
	{
		byte nx, ny, nz;
		float x2, y2;
	findMaxComponent:
		{
			if ((x2 = x * x) > (y2 = y * y))
			{
				if (x2 > z * z)
				{
					if (x > 0)
					{
						nx = 127;
						x2 = 1 / x;
						ny = f2b (x2 * y);
						nz = f2b (x2 * z);
					}
					else
					{
						nx = -128;
						x2 = -1 / x;
						ny = f2b (x2 * y);
						nz = f2b (x2 * z);
					}
					break findMaxComponent;
				}
			}
			else
			{
				if (y2 > z * z)
				{
					if (y > 0)
					{
						ny = 127;
						x2 = 1 / y;
						nx = f2b (x2 * x);
						nz = f2b (x2 * z);
					}
					else
					{
						ny = -128;
						x2 = -1 / y;
						nx = f2b (x2 * x);
						nz = f2b (x2 * z);
					}
					break findMaxComponent;
				}
			}
			if (z > 0)
			{
				nz = 127;
				x2 = 1 / z;
				nx = f2b (x2 * x);
				ny = f2b (x2 * y);
			}
			else
			{
				nz = -128;
				x2 = -1 / z;
				nx = f2b (x2 * x);
				ny = f2b (x2 * y);
			}
		}
		normals.set (index *= 3, nx);
		normals.set (index + 1, ny);
		normals.set (index + 2, nz);
	}


	/**
	 * Reads the value of the normal vector at <code>index</code>
	 * and places it in <code>out</code>.
	 * 
	 * @param out the result will be placed in here
	 * @param index the index of the normal vector (note that the actual index
	 * into {@link #normals} is <code>3 * index</code>)
	 */
	public void getNormal (float[] out, int index)
	{
		byte[] a = normals.elements;
		index *= 3;
		out[0] = (2 * a[index] + 1) * (1f / 255); 
		out[1] = (2 * a[index + 1] + 1) * (1f / 255); 
		out[2] = (2 * a[index + 2] + 1) * (1f / 255); 
	}


	public int getPolygonCount ()
	{
		return polygons.size / edgeCount;
	}


	public int getMaxEdgeCount ()
	{
		return edgeCount;
	}


	public int getVertexCount ()
	{
		return vertices.size / dimension;
	}


	public int getNormalCount ()
	{
		return normals.size / dimension;
	}

	
	public boolean isPolygonPlanar (int index)
	{
		return planar;
	}


	public int getPolygon (int index, int[] indicesOut, int[] normalsOut)
	{
		index *= edgeCount;
		int[] n = (usePolygonNormals ? polygonNormals : polygons).elements;
		for (int i = 0; i < edgeCount; i++)
		{
			indicesOut[i] = polygons.elements[index + i];
			normalsOut[i] = n[index + i];
		}
		return edgeCount;
	}


	public void getVertex (int index, Tuple3d out)
	{
		index *= dimension;
		out.x = vertices.elements[index];
		out.y = vertices.elements[index + 1];
		out.z = (dimension > 2) ? vertices.elements[index + 2] : 0;
	}


	public void getNormal (int index, Tuple3d out)
	{
		byte[] a = normals.elements;
		index *= 3;
		out.x = (2 * a[index] + 1) * (1d / 255); 
		out.y = (2 * a[index + 1] + 1) * (1d / 255); 
		out.z = (2 * a[index + 2] + 1) * (1d / 255); 
	}


	public void getUV (int index, Tuple2d out)
	{
		out.x = uv.elements[index <<= 1];
		out.y = uv.elements[index + 1];
	}


	public boolean isClosed ()
	{
		return closed;
	}


	/**
	 * Compute normal vector for every vertex.
	 */
	public void computeNormals()
	{
		// calculating normals only makes sense in 3D
		assert this.dimension == 3;
		
		// there must be at least 3 edges per polygon
		assert this.edgeCount >= 3;
		
		// allocate temporaries
		final Vector3f a = new Vector3f();
		final Vector3f b = new Vector3f();
		final Vector3f c = new Vector3f();
		float[] v = this.vertices.elements;
		int[] index = this.polygons.elements;
		float[] n = new float[this.vertices.size];
		Arrays.fill(n, 0);

		// remove previous normals
		this.normals.clear();
		
		// loop over faces
		for (int i = 0; i < this.polygons.size; i += this.edgeCount)
		{
			// obtain first three points of face
			a.set(v[3*index[i+0]+0],v[3*index[i+0]+1],v[3*index[i+0]+2]);
			b.set(v[3*index[i+1]+0],v[3*index[i+1]+1],v[3*index[i+1]+2]);
			c.set(v[3*index[i+2]+0],v[3*index[i+2]+1],v[3*index[i+2]+2]);
			// calculate plane vectors
			b.sub(a);
			c.sub(a);
			// calculate normal
			a.cross(b, c);
			a.normalize();
			// add normal to normal vector of every vertex
			for (int k = 0; k < this.edgeCount; k++)
			{
				n[3*index[i+k]+0] += a.x;
				n[3*index[i+k]+1] += a.y;
				n[3*index[i+k]+2] += a.z;
			}
		}
		// store normal vectors
		for (int i = 0; i < n.length; i+=3)
		{
			a.set(n[i+0],n[i+1],n[i+2]);
			a.normalize();
			a.scale(127);
			normals.add((byte)a.x);
			normals.add((byte)a.y);
			normals.add((byte)a.z);
		}
	}
}
