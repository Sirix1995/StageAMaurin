
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
 * If a <code>Polygonizable</code> object is the value of the
 * attribute {@link de.grogra.imp3d.objects.Attributes#SHAPE}, it
 * defines a shape which is composed of polygons.
 * 
 * @author Ole Kniemeyer
 */
public interface Polygonizable
{
	/**
	 * Defines the data source on which the polygonization
	 * depends.
	 * 
	 * @param gs the current graph state
	 * @return the data source
	 */
	ContextDependent getPolygonizableSource (GraphState gs);

	/**
	 * Defines the algorithm which is used to decompose the data
	 * into a polygon mesh representation.
	 *
	 * @return polygonization algorithm
	 */
	Polygonization getPolygonization ();
}
