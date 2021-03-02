
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

import de.grogra.graph.*;

/**
 * This interface represents an algorithm which decomposes a data source
 * returned by
 * {@link de.grogra.imp3d.Polygonizable#getPolygonizableSource(GraphState)}
 * into a polygon mesh representation.
 * 
 * @author Ole Kniemeyer
 */
public interface Polygonization
{
	/**
	 * Bit mask for <code>polygonize</code> indicating that normal vectors
	 * have to be computed.
	 */
	int COMPUTE_NORMALS = 1;

	/**
	 * Bit mask for <code>polygonize</code> indicating that uv coordinates
	 * have to be computed.
	 */
	int COMPUTE_UV = 2;
	

	/**
	 * Performs the polygonization of the source into polygons.
	 * The field <code>out.userObject</code> is not modified.
	 * 
	 * @param source the data source as reported by a previous invocation of
	 * {@link Polygonizable#getPolygonizableSource(GraphState)}
	 * with the same <code>gs</code>
	 * @param gs the current graph state
	 * @param out the polygons are written to <code>out</code>
	 * @param flags combination of bit masks
	 * @param flatness a parameter for the degree of flatness, a typical value is 1
	 */
	void polygonize (ContextDependent source, GraphState gs, PolygonArray out,
					 int flags, float flatness);
}
