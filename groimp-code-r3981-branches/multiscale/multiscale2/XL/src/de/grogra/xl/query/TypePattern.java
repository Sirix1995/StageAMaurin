
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

import de.grogra.reflect.Type;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class TypePattern extends BuiltInPattern
{

	public TypePattern (Type type)
	{
		super (new Type[] {type}, 1);
	}


	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{
		if (!providedConstants.get (0))
		{
			neededConstantsOut.add (0);
		}
		return NULL_MATCHER;
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		out.visitType (getParameterType (0));
		out.endMethod ();
	}

	@Override
	public int getParameterKind (int index)
	{
		return 0;
	}

}
