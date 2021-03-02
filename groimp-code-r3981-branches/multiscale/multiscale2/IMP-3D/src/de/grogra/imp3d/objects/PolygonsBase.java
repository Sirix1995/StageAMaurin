
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

package de.grogra.imp3d.objects;

import de.grogra.graph.GraphState;
import de.grogra.graph.Cache.Entry;

/**
 * This abstract class can be used as superclass for user-defined polygonal data.
 * The method {@link de.grogra.imp3d.Polygonization#polygonize} has to be
 * implemented.
 * 
 * @author Ole Kniemeyer
 */
public abstract class PolygonsBase implements Polygons, java.io.Serializable
{
	public boolean dependsOnContext ()
	{
		return false;
	}

	public void writeStamp (Entry cache, GraphState gs)
	{
	}

	public int getStamp ()
	{
		return 0;
	}
}
