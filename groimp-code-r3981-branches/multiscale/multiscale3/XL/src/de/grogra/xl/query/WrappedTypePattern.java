
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
import de.grogra.reflect.TypeId;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class WrappedTypePattern extends BuiltInPattern
{

	public WrappedTypePattern (Type wrapperType, Type type)
	{
		super (new Type[] {wrapperType, type}, 2);
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
		if (!providedConstants.get (0))
		{
			neededConstantsOut.add (0);
		}
		final Type wrappedType = getParameterType (1);
		return new Matcher (0.8f)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				Object o = qs.abound (0);
				if (qs.model.isWrapperFor (o, wrappedType))
				{
					switch (wrappedType.getTypeId ())
					{
/*!!
#foreach ($type in $types)
$pp.setType($type)
						case TypeId.$pp.TYPE:
							qs.${pp.prefix}match
								(1,
								 qs.model.unwrap$pp.Type (o) $pp.type2vm,
								 consumer, arg);
							break;
#end
!!*/
//!! #* Start of generated code
// generated
						case TypeId.BOOLEAN:
							qs.imatch
								(1,
								 qs.model.unwrapBoolean (o)  ? 1 : 0,
								 consumer, arg);
							break;
// generated
						case TypeId.BYTE:
							qs.imatch
								(1,
								 qs.model.unwrapByte (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.SHORT:
							qs.imatch
								(1,
								 qs.model.unwrapShort (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.CHAR:
							qs.imatch
								(1,
								 qs.model.unwrapChar (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.INT:
							qs.imatch
								(1,
								 qs.model.unwrapInt (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.LONG:
							qs.lmatch
								(1,
								 qs.model.unwrapLong (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.FLOAT:
							qs.fmatch
								(1,
								 qs.model.unwrapFloat (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.DOUBLE:
							qs.dmatch
								(1,
								 qs.model.unwrapDouble (o) ,
								 consumer, arg);
							break;
// generated
						case TypeId.OBJECT:
							qs.amatch
								(1,
								 qs.model.unwrapObject (o) ,
								 consumer, arg);
							break;
//!! *# End of generated code
					}
				}
			}
		};
	}


	@Override
	public int getParameterKind (int index)
	{
		return (index == 0) ? INPUT_MASK : CONTEXT_MASK;
	}
	

	@Override
	protected String paramString ()
	{
		return getParameterType (0) + "," + getParameterType (1);
	}

}
