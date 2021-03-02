
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

package de.grogra.pf.ui.util;

import de.grogra.reflect.*;
import de.grogra.util.*;

public class Numeric2String implements WidgetConversion
{
	public final Quantity quantity;
	private final Type type;
	private final boolean allowNull;

	private Unit lastUnit = null;
	private String lastString;

	public Numeric2String (Type type, Quantity quantity)
	{
		if (allowNull = Reflection.isSuperclassOrSame (Type.NUMBER, type))
		{
			for (int i = TypeId.BYTE; i <= TypeId.DOUBLE; i++)
			{
				if (type.getSimpleName ()
					.indexOf (Reflection.getTypeSuffix (i)) >= 0)
				{
					type = Reflection.getType (i);
					break;
				}
			}
		}
		this.type = type;
		this.quantity = quantity;
	}


	public Object toWidget (Object propertyValue)
	{
		if (propertyValue == null)
		{
			if ((quantity != null) && (lastUnit == null))
			{
				lastUnit = quantity.getPreferredUnit ();
			}
			return null;
		}
		switch (type.getTypeId ())
		{
			case TypeId.BYTE:
			case TypeId.SHORT:
			case TypeId.INT:
			case TypeId.LONG:
			{
				int radix = 10;
				String prefix = "";
				if (lastString != null)
				{
					if (lastString.startsWith ("0x"))
					{
						radix = 16;
						prefix = "0x";
					}
					else if (lastString.startsWith ("0X"))
					{
						radix = 16;
						prefix = "0X";
					}
					else if (lastString.startsWith ("#"))
					{
						radix = 16;
						prefix = "#";
					}
				}
				return prefix + Long.toString (((Number) propertyValue).longValue (), radix);
			}
			case TypeId.CHAR:
			{
				return String.valueOf (propertyValue);
			}
/*!!
#foreach ($type in ["float", "double"])
$pp.setType($type)
			case TypeId.$pp.TYPE:
			{
				$type v = $pp.unwrap("propertyValue");
#if ($pp.fnumeric)
				if (quantity != null)
				{
					if (lastUnit == null)
					{
						lastUnit = quantity.chooseUnit ((float) v);
					}
#if ($pp.double)
					return (v == (float) v) ? lastUnit.toString ((float) v)
						: lastUnit.toString (v);
#else
					return lastUnit.toString (v);
#end
				}
				else
				{
#if ($pp.double)
					return (v == (float) v) ? String.valueOf ((float) v)
						: String.valueOf (v);
#else
					return String.valueOf (v);
#end
				}
#else
				return String.valueOf (v);
#end
			}
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.FLOAT:
			{
				float v = (((Number) (propertyValue)).floatValue ());
				if (quantity != null)
				{
					if (lastUnit == null)
					{
						lastUnit = quantity.chooseUnit ((float) v);
					}
					return lastUnit.toString (v);
				}
				else
				{
					return String.valueOf (v);
				}
			}
// generated
			case TypeId.DOUBLE:
			{
				double v = (((Number) (propertyValue)).doubleValue ());
				if (quantity != null)
				{
					if (lastUnit == null)
					{
						lastUnit = quantity.chooseUnit ((float) v);
					}
					return (v == (float) v) ? lastUnit.toString ((float) v)
						: lastUnit.toString (v);
				}
				else
				{
					return (v == (float) v) ? String.valueOf ((float) v)
						: String.valueOf (v);
				}
			}
//!! *# End of generated code
			default:
				throw new IllegalStateException (type.getName ());
		}
	}


	public Object fromWidget (Object widgetValue)
	{
		String wv = (widgetValue instanceof String)
			? ((String) widgetValue).trim () : null;
		if (allowNull && ((wv == null) || (wv.length () == 0)))
		{
			return null;
		}
		lastString = wv;
		switch (type.getTypeId ())
		{
/*!!
#foreach ($type in $numeric)
$pp.setType($type)
			case TypeId.$pp.TYPE:
			{
#if ($pp.fnumeric)
				if (quantity != null)
				{
					lastUnit = quantity.parseUnit (wv, lastUnit);
				}
				return (lastUnit == null) ? new $pp.wrapper (wv)
					: new $pp.wrapper (lastUnit.parse (wv));
#else
				return ${pp.wrapper}.decode (wv);
#end
			}
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BYTE:
			{
				return Byte.decode (wv);
			}
// generated
			case TypeId.SHORT:
			{
				return Short.decode (wv);
			}
// generated
			case TypeId.INT:
			{
				return Integer.decode (wv);
			}
// generated
			case TypeId.LONG:
			{
				return Long.decode (wv);
			}
// generated
			case TypeId.FLOAT:
			{
				if (quantity != null)
				{
					lastUnit = quantity.parseUnit (wv, lastUnit);
				}
				return (lastUnit == null) ? new Float (wv)
					: new Float (lastUnit.parse (wv));
			}
// generated
			case TypeId.DOUBLE:
			{
				if (quantity != null)
				{
					lastUnit = quantity.parseUnit (wv, lastUnit);
				}
				return (lastUnit == null) ? new Double (wv)
					: new Double (lastUnit.parse (wv));
			}
//!! *# End of generated code
			case TypeId.CHAR:
				return new Character (((wv == null)
									   || (wv.length () == 0))
									  ? 0 : wv.charAt (0));
			default:
				throw new IllegalStateException (type.getName ());
		}
	}
}
