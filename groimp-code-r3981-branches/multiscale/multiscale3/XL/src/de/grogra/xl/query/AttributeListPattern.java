
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

public abstract class AttributeListPattern extends UserDefinedPattern
{
	private final int[] typeIds;


	public AttributeListPattern (int attributeCount)
	{
		this (null, attributeCount);
	}


	public AttributeListPattern (Type cls, int attributeCount)
	{
		super (cls);
		if (getParameterCount () != attributeCount + 1)
		{
			throw new IllegalArgumentException ("Illegal term count");
		}
		typeIds = new int[attributeCount];
		for (int i = 0; i < attributeCount; i++)
		{
			typeIds[i] = getParameterType (i + 1).getTypeId ();
		}
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	protected $type get$pp.Type (Object o, int index)
	{
		throw new AssertionError (index);
	}

#end

!!*/
//!! #* Start of generated code
// generated
// generated
	protected boolean getBoolean (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected byte getByte (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected short getShort (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected char getChar (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected int getInt (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected long getLong (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected float getFloat (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected double getDouble (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
// generated
	protected Object getObject (Object o, int index)
	{
		throw new AssertionError (index);
	}
// generated
// generated
//!! *# End of generated code

	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{
		if (!providedConstants.get (0))
		{
			neededConstantsOut.add (0);
		}
		return new Matcher (0.8f)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				Object o = qs.abound (0);
				long matched = 0;
				int i = 0;
				int n = typeIds.length;
				try
				{
					for (; i < n; i++)
					{
						switch (typeIds[i])
						{
/*!!
#foreach ($type in $types)
$pp.setType($type)
							case TypeId.$pp.TYPE:
								switch (qs.${pp.prefix}bind (1 + i, get$pp.Type (o, i) $pp.type2vm))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
#end
!!*/
//!! #* Start of generated code
// generated
							case TypeId.BOOLEAN:
								switch (qs.ibind (1 + i, getBoolean (o, i)  ? 1 : 0))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.BYTE:
								switch (qs.ibind (1 + i, getByte (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.SHORT:
								switch (qs.ibind (1 + i, getShort (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.CHAR:
								switch (qs.ibind (1 + i, getChar (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.INT:
								switch (qs.ibind (1 + i, getInt (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.LONG:
								switch (qs.lbind (1 + i, getLong (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.FLOAT:
								switch (qs.fbind (1 + i, getFloat (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.DOUBLE:
								switch (qs.dbind (1 + i, getDouble (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
// generated
							case TypeId.OBJECT:
								switch (qs.abind (1 + i, getObject (o, i) ))
								{
									case QueryState.BINDING_PERFORMED:
										break;
									case QueryState.BINDING_MATCHED:
										matched |= 1L << i;
										break;
									default:
										return;
								}
								break; 
//!! *# End of generated code
						}
					}
					consumer.matchFound (qs, arg);
				}
				catch (ClassCastException e)
				{
				}
				finally
				{
					while (--i >= 0)
					{
						if ((matched & (1L << i)) == 0)
						{
							qs.unbind (1 + i);
						}
					}
				}
			}
		};
	}


	@Override
	public int getParameterKind (int index)
	{
		return (index == 0) ? INPUT_MASK : OUTPUT_MASK;
	}
	
}
