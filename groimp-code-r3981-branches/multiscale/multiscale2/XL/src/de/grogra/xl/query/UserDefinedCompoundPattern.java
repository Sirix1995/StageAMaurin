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

package de.grogra.xl.query;

import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public abstract class UserDefinedCompoundPattern extends UserDefinedPattern
{
	private final CompoundPattern pattern;

	protected UserDefinedCompoundPattern (CompoundPattern pattern)
	{
		super (null);
		this.pattern = pattern;
	}

	@Override
	public final Matcher createMatcher (Graph src, XBitSet providedConstants, IntList neededConstantsOut)
	{
		return pattern.createMatcher (src, providedConstants, neededConstantsOut);
	}

	@Override
	public final int getParameterKind (int index)
	{
		return pattern.getParameterKind (index);
	}
	
	@Override
	public boolean isDeleting ()
	{
		return pattern.isDeleting ();
	}

	public final CompoundPattern getPattern ()
	{
		return pattern;
	}

}
