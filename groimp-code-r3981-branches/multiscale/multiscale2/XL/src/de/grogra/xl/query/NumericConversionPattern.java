
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

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class NumericConversionPattern extends BuiltInPattern
{

	public NumericConversionPattern (Type firstType, Type secondType)
	{
		super (new Type[] {firstType, secondType}, 2);
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		out.visitType (getParameterType (0));
		out.visitType (getParameterType (1));
		out.endMethod ();
	}


	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{
		final boolean forward;
		if (providedConstants.get (0))
		{
			forward = true;
		}
		else if (providedConstants.get (1))
		{
			forward = false;
		}
		else
		{
			forward = true;
			neededConstantsOut.add (0);
		}
		final int from = forward ? 0 : 1, to = forward ? 1 : 0;
		final int fromType = Reflection.getJVMTypeId (getParameterType (from)),
			toType = Reflection.getJVMTypeId (getParameterType (to));
		return new Matcher (0.8f)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				switch (toType)
				{
/*!!
#foreach ($type in $vmnumeric)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						switch (fromType)
						{
							case TypeId.INT:
								qs.${pp.prefix}match
									(to, ($type) qs.ibound (from),
									 consumer, arg);
								return;
							case TypeId.LONG:
								qs.${pp.prefix}match
									(to, ($type) qs.lbound (from),
									 consumer, arg);
								return;
							case TypeId.FLOAT:
								qs.${pp.prefix}match
									(to, ($type) qs.fbound (from),
									 consumer, arg);
								return;
							case TypeId.DOUBLE:
								qs.${pp.prefix}match
									(to, ($type) qs.dbound (from),
									 consumer, arg);
								return;
						}
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.INT:
						switch (fromType)
						{
							case TypeId.INT:
								qs.imatch
									(to, (int) qs.ibound (from),
									 consumer, arg);
								return;
							case TypeId.LONG:
								qs.imatch
									(to, (int) qs.lbound (from),
									 consumer, arg);
								return;
							case TypeId.FLOAT:
								qs.imatch
									(to, (int) qs.fbound (from),
									 consumer, arg);
								return;
							case TypeId.DOUBLE:
								qs.imatch
									(to, (int) qs.dbound (from),
									 consumer, arg);
								return;
						}
						break;
// generated
					case TypeId.LONG:
						switch (fromType)
						{
							case TypeId.INT:
								qs.lmatch
									(to, (long) qs.ibound (from),
									 consumer, arg);
								return;
							case TypeId.LONG:
								qs.lmatch
									(to, (long) qs.lbound (from),
									 consumer, arg);
								return;
							case TypeId.FLOAT:
								qs.lmatch
									(to, (long) qs.fbound (from),
									 consumer, arg);
								return;
							case TypeId.DOUBLE:
								qs.lmatch
									(to, (long) qs.dbound (from),
									 consumer, arg);
								return;
						}
						break;
// generated
					case TypeId.FLOAT:
						switch (fromType)
						{
							case TypeId.INT:
								qs.fmatch
									(to, (float) qs.ibound (from),
									 consumer, arg);
								return;
							case TypeId.LONG:
								qs.fmatch
									(to, (float) qs.lbound (from),
									 consumer, arg);
								return;
							case TypeId.FLOAT:
								qs.fmatch
									(to, (float) qs.fbound (from),
									 consumer, arg);
								return;
							case TypeId.DOUBLE:
								qs.fmatch
									(to, (float) qs.dbound (from),
									 consumer, arg);
								return;
						}
						break;
// generated
					case TypeId.DOUBLE:
						switch (fromType)
						{
							case TypeId.INT:
								qs.dmatch
									(to, (double) qs.ibound (from),
									 consumer, arg);
								return;
							case TypeId.LONG:
								qs.dmatch
									(to, (double) qs.lbound (from),
									 consumer, arg);
								return;
							case TypeId.FLOAT:
								qs.dmatch
									(to, (double) qs.fbound (from),
									 consumer, arg);
								return;
							case TypeId.DOUBLE:
								qs.dmatch
									(to, (double) qs.dbound (from),
									 consumer, arg);
								return;
						}
						break;
//!! *# End of generated code
				}
				throw new AssertionError ();
			}
		};
	}

	@Override
	public int getParameterKind (int index)
	{
		return 0;
	}

	@Override
	protected String paramString ()
	{
		return getParameterType (0) + "," + getParameterType (1);
	}

}
