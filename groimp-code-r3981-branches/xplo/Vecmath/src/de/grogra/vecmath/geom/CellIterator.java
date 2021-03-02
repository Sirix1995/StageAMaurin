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

import java.util.Iterator;

/**
 * A <code>CellIterator</code> is used to iterate over the cells of an
 * octree which are intersected by a line. The iterated cells are
 * ordered according to their position on the line. A typical usage is:
 * <pre>
 *     iterator.setLine (line);
 *     while (iterator.hasNext ())
 *     {
 *         Octree.Cell c = iterator.nextCell ();
 *         double param = iterator.getEnteringParameter ();
 *         ...
 *     }
 * </pre>
 * 
 * @author Ole Kniemeyer
 */
public interface CellIterator extends Iterator<Octree.Cell>
{
	/**
	 * Returns a clone of this <code>CellIterator</code>.
	 * All constant variables which are related to the structure
	 * of the octree are copied shallowly, state variables of the
	 * iterator are newly created without copying.
	 * 
	 * @return clone of this iterator
	 */
	CellIterator dup ();

	/**
	 * This method has to be invoked once to initialize the iterator
	 * for a given octree.
	 * 
	 * @param tree the octree
	 */
	void initialize (Octree tree);

	/**
	 * This method has to invoked in order to start a new iteration
	 * sequence over the octree cells which intersect <code>line</code>.
	 * 
	 * @param line the line for which octree cells shall be enumerated
	 */
	void setLine (Line line);
	
	/**
	 * Returns the parameter at which the current cell was entered.
	 * The current cell is the returned cell of the most recent
	 * invocation of {@link #nextCell()}. This cell is entered at the
	 * position <code>line.start + parameter * line.direction</code>.
	 * 
	 * @return line parameter where current cell was entered 
	 */
	double getEnteringParameter ();
}
