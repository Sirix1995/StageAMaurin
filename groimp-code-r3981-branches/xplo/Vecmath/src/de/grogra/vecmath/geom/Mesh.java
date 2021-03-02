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

package de.grogra.vecmath.geom;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;

/**
 * Instances of this interface define a polygonal mesh
 * which can be used in {@link de.grogra.vecmath.geom.MeshVolume}
 * to define a volume. The polygons are obtained through
 * {@link #getPolygon} as index arrays into the
 * list of vertices which is obtained through {@link #getVertex} 
 * and the list of normals which is obtained through
 * {@link #getNormal}. 
 * 
 * @author Ole Kniemeyer
 */
public interface Mesh
{
	/**
	 * Returns the number of polygons of the mesh.
	 * 
	 * @return number of polygons
	 */
	int getPolygonCount ();

	/**
	 * Returns the maximum number of edges of a single polygon
	 * of the mesh.
	 * 
	 * @return maximum number of edges
	 */
	int getMaxEdgeCount ();

	/**
	 * Returns the number of vertices of the mesh.
	 * 
	 * @return number of vertices
	 */
	int getVertexCount ();

	/**
	 * Returns the number of normals of the mesh.
	 * 
	 * @return number of normals
	 */
	int getNormalCount ();

	/**
	 * Returns <code>true</code> iff the polygon number
	 * <code>index</code> is planar.
	 * 
	 * @param index polygon number
	 * @return is polygon planar?
	 */
	boolean isPolygonPlanar (int index);

	/**
	 * Writes the vertex indices of polygon <code>index</code>
	 * to <code>indicesOut</code> and the normal indices to
	 * <code>normalsOut</code> and returns the number of
	 * vertices of the polygon. If the mesh is closed
	 * (see {@link #isClosed()}), inside and outside are determined by the
	 * ordering of the vertices: when seen from the outside, vertices have
	 * to be oriented in a counter-clockwise manner. Then also the normal
	 * vectors have to point to the outside.
	 * 
	 * @param index polygon number
	 * @param indicesOut the vertex indices will be placed in here
	 * @param normalsOut the normal indices will be placed in here
	 * @return number of vertex indices of polygon
	 */
	int getPolygon (int index, int[] indicesOut, int[] normalsOut);

	/**
	 * Gets the spatial vertex coordinates of vertex <code>index</code>. 
	 * 
	 * @param index vertex number
	 * @param out vertex coordinates will be placed in here
	 */
	void getVertex (int index, Tuple3d out);

	/**
	 * Gets the normal vector of normal <code>index</code>.
	 * The normal vector is not necessarily normalized. 
	 * 
	 * @param index normal number
	 * @param out normal vector will be placed in here
	 */
	void getNormal (int index, Tuple3d out);

	/**
	 * Gets the uv coordinates of vertex <code>index</code>. 
	 * 
	 * @param index vertex number
	 * @param out uv coordinates will be placed in here
	 */
	void getUV (int index, Tuple2d out);

	/**
	 * Indicates whether this mesh is a closed surface or not. If it is
	 * closed, normal vectors have to point to the exterior.
	 * 
	 * @return is surface closed?
	 */
	boolean isClosed ();
}
