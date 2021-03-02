
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

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import de.grogra.xl.util.FloatList;

/**
 * This abstract base class contains a list of vertex coordinates.
 * 
 * @author Ole Kniemeyer
 */
public abstract class VertexArray
{
	/**
	 * This list contains the vertex coordinates. The coordinates
	 * are contained contiguously, with {@link #dimension}
	 * <code>float</code>-values for each vertex.
	 */
	public FloatList vertices = new FloatList ();


	/**
	 * The number of <code>float</code>-values per vertex in
	 * {@link #vertices}.
	 */
	public int dimension;


	/**
	 * This field may be used freely by user code. It is not modified
	 * by methods of this class and its subclasses.
	 */
	public Object userObject;


	private boolean cleared;


	/**
	 * Clears this vertex array and initializes the
	 * <code>dimension</code>-value.
	 * 
	 * @param dimension the new value for {@link #dimension}
	 */
	public void init (int dimension)
	{
		clear ();
		this.dimension = dimension;
	}

	
	public void freeArrays ()
	{
		vertices.clear ();
		vertices.trimToSize ();
	}


	/**
	 * Clears all arrays.
	 */
	void clear ()
	{
		vertices.clear ();
		cleared = true;
	}
	
	
	/**
	 * Tests whether this list was cleared previously. This method
	 * returns <code>true</code> iff the <code>init</code>-method
	 * has been invoked between the last invocation of <code>wasCleared</code>
	 * and this invocation.
	 * 
	 * @return <code>true</code> if this list was cleared
	 */
	public boolean wasCleared ()
	{
		if (cleared)
		{
			cleared = false;
			return true;
		}
		return false;
	}


	/**
	 * Computes the (unnormalized) normal vector for the triangle
	 * <code>(v1, v2, v3)</code>. The corners are specified by the
	 * vertex indices <code>v1, v2, v3</code>
	 * (which this method multiplies by <code>dimension</code> to obtain
	 * indices in the list <code>vertices</code>). The cross product of
	 * the vector from <code>v1</code> to <code>v2</code> with the
	 * vector from <code>v1</code> to <code>v3</code> is computed and
	 * place in <code>out</code>.
	 *
	 * @param out the resulting normal vector is placed in here
	 * @param v1 the vertex index of the first corner
	 * @param v2 the vertex index of the second corner
	 * @param v3 the vertex index of the third corner
	 */
	public void computeNormal (float[] out, int v1, int v2, int v3)
	{
		float[] a = vertices.elements;
		v1 *= dimension;
		v2 *= dimension;
		v3 *= dimension;
		out[0] = (a[v2+1] - a[v1+1]) * (a[v3+2] - a[v1+2])
			- (a[v2+2] - a[v1+2]) * (a[v3+1] - a[v1+1]);
		out[1] = (a[v2+2] - a[v1+2]) * (a[v3] - a[v1])
			- (a[v2] - a[v1]) * (a[v3+2] - a[v1+2]);
		out[2] = (a[v2] - a[v1]) * (a[v3+1] - a[v1+1])
			- (a[v2+1] - a[v1+1]) * (a[v3] - a[v1]);
	}

	
	public int addVertex (float x, float y, float z)
	{
		vertices.push (x, y, z);
		return vertices.size () / 3 - 1;
	}


	public int addVertex (Tuple3f v)
	{
		vertices.push (v.x, v.y, v.z);
		return vertices.size () / 3 - 1;
	}


	public int addVertex (Tuple3d v)
	{
		vertices.push ((float) v.x, (float) v.y, (float) v.z);
		return vertices.size () / 3 - 1;
	}

}
