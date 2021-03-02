
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package de.grogra.rgg;

import de.grogra.graph.impl.Node;
import de.grogra.xl.lang.VoidToObjectGenerator;
import de.grogra.xl.query.UserDefinedPattern;
import de.grogra.xl.query.Graph;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class acropetal extends UserDefinedPattern
{
	private static <T extends Node, S extends Node> void signature (@In Node start,
			Class<T> nodeClass, VoidToObjectGenerator<T> children,
			Class<S> leafClass, VoidToObjectGenerator<S> leaves,
			@Out T parent)
	{
	}

	@Override
	public int getParameterKind (int index)
	{
		switch (index)
		{
			case 0:
				return NODE_MASK | INPUT_MASK;
			case 1:
			case 3:
				return INPUT_MASK;
			case 2:
			case 4:
				return OUTPUT_MASK;
			case 5:
				return NODE_MASK | OUTPUT_MASK;
			default:
				throw new AssertionError ();
		}
	}

	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
			IntList neededConstantsOut)
	{
		return new TreeMatcher (100, false);
	}
}
