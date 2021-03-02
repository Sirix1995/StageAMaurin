
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

import de.grogra.xl.util.IntList;

/**
 * This class represents a list of line strips.
 * 
 * @author Ole Kniemeyer
 */
public class LineArray extends VertexArray
{
	/**
	 * Specifies the line strips. Each line strip is represented by a
	 * contiguous sequence of vertex indices into {@link #vertices}.
	 * Line strips are separated by negative values in this list. Thus,
	 * the individual strips may be of different length.
	 * <p>
	 * Note that vertex indices have to me multiplied by
	 * {@link #dimension} in order to obtain list indices for
	 * {@link #vertices}.  
	 */
	public final IntList lines = new IntList ();

	
	@Override
	public void freeArrays ()
	{
		super.freeArrays ();
		lines.clear ();
		lines.trimToSize ();
	}


	@Override
	void clear ()
	{
		super.clear ();
		lines.clear ();
	}

}
