
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
 * If a <code>LineSegmentizable</code> object is the value of the
 * attribute {@link de.grogra.imp3d.objects.Attributes#SHAPE}, it
 * defines a shape which is composed of line segments.
 * 
 * @author Ole Kniemeyer
 */
public interface LineSegmentizable
{
	
	int getStamp ();

	/**
	 * Defines the data source on which the line segmentization
	 * depends.
	 * 
	 * @param gs the current graph state
	 * @return the data source
	 */
	ContextDependent getSegmentizableSource (GraphState gs);

	/**
	 * Performs the segmentization of the source into line segments.
	 * The field <code>out.userObject</code> is not modified.
	 * 
	 * @param source the data source as reported by a previous invocation of
	 * {@link #getSegmentizableSource(GraphState)} with the same <code>gs</code>
	 * @param gs the current graph state
	 * @param out the line segments are written to <code>out</code>
	 * @param flatness a parameter for the degree of flatness, a typical value is 1
	 */
	void segmentize (ContextDependent source, GraphState gs, LineArray out, float flatness);
}
