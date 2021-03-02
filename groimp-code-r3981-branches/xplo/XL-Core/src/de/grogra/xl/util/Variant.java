
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

package de.grogra.xl.util;

import de.grogra.xl.lang.ConversionConstructor;

/**
 * 
 * @author Ole Kniemeyer
 */
public final class Variant
{
	private static final int NULL = 1;
	private static final int STRING = 2;
	private static final int BOOLEAN = 4;
	private static final int INT = 8;
	private static final int LONG = 16;
	private static final int FLOAT = 32;
	private static final int DOUBLE = 64;

	private final String svalue;
	private final Number nvalue;
	private final boolean bvalue;
	private final int type;

	private Variant (String value, boolean isString)
	{
		svalue = value;
		if (value != null)
		{
			value = value.trim ();
			for (int i = 0; i < value.length (); i++)
			{
				if (value.charAt (i) <= ' ')
				{
					value = value.substring (0, i);
					isString = true;
					break;
				}
			}
			int type = STRING;
			Number nvalue;
			try
			{
				Long v = Long.decode (value);
				if (v.longValue () == v.intValue ())
				{
					nvalue = v.intValue ();
					type = INT;
				}
				else
				{
					nvalue = v;
					type = LONG;
				}
			}
			catch (NumberFormatException e)
			{
				try
				{
					nvalue = Double.valueOf (value);
					type = DOUBLE;
				}
				catch (NumberFormatException e2)
				{
					nvalue = 0;
				}
			}
			this.nvalue = nvalue;
			value = value.toLowerCase ();
			if (value.equals ("true") || value.equals ("yes"))
			{
				bvalue = true;
				type = BOOLEAN;
			}
			else if (value.equals ("false") || value.equals ("no"))
			{
				bvalue = false;
				type = BOOLEAN;
			}
			else
			{
				bvalue = (type != STRING) && (nvalue.intValue () != 0);
			}
			this.type = isString ? STRING | type : type;
		}
		else
		{
			type = NULL;
			nvalue = 0;
			bvalue = false;
		}
	}

	public boolean isNull ()
	{
		return type == NULL;
	}

	@ConversionConstructor
	public Variant (boolean value)
	{
		svalue = String.valueOf (value);
		nvalue = value ? 1 : 0;
		bvalue = value;
		type = BOOLEAN;
	}

	@ConversionConstructor
	public Variant (int value)
	{
		svalue = String.valueOf (value);
		nvalue = value;
		bvalue = value != 0;
		type = INT;
	}

	@ConversionConstructor
	public Variant (long value)
	{
		svalue = String.valueOf (value);
		nvalue = value;
		bvalue = value != 0;
		type = LONG;
	}

	@ConversionConstructor
	public Variant (float value)
	{
		svalue = String.valueOf (value);
		nvalue = value;
		bvalue = value != 0;
		type = FLOAT;
	}

	@ConversionConstructor
	public Variant (double value)
	{
		svalue = String.valueOf (value);
		nvalue = value;
		bvalue = value != 0;
		type = DOUBLE;
	}

	@ConversionConstructor
	public Variant (Object value)
	{
		this ((value != null) ? value.toString () : null, false);
	}

	public boolean booleanValue ()
	{
		return bvalue;
	}

	public int intValue ()
	{
		return nvalue.intValue ();
	}

	public long longValue ()
	{
		return nvalue.longValue ();
	}

	public float floatValue ()
	{
		return nvalue.floatValue ();
	}

	public double doubleValue ()
	{
		return nvalue.doubleValue ();
	}

	public String toString ()
	{
		return svalue;
	}

	public static Variant operator$add (Variant a, Variant b)
	{
		int m = a.type | b.type;
		if ((m & STRING) == 0)
		{
			if ((m & DOUBLE) != 0)
			{
				return new Variant (a.doubleValue () + b.doubleValue ());
			}
			else if ((m & FLOAT) != 0)
			{
				return new Variant (a.floatValue () + b.floatValue ());
			}
			else if ((m & LONG) != 0)
			{
				return new Variant (a.longValue () + b.longValue ());
			}
			else if ((m & INT) != 0)
			{
				return new Variant (a.intValue () + b.intValue ());
			}
		}
		return new Variant (a.svalue + b.svalue, true);
	}

	public static Variant operator$mul (Variant a, Variant b)
	{
		int m = a.type | b.type;
		if ((m & DOUBLE) != 0)
		{
			return new Variant (a.doubleValue () * b.doubleValue ());
		}
		else if ((m & FLOAT) != 0)
		{
			return new Variant (a.floatValue () * b.floatValue ());
		}
		else if ((m & LONG) != 0)
		{
			return new Variant (a.longValue () * b.longValue ());
		}
		else if ((m & INT) != 0)
		{
			return new Variant (a.intValue () * b.intValue ());
		}
		else
		{
			return new Variant (a.doubleValue () * b.doubleValue ());
		}
	}


	public static Variant operator$sub (Variant a, Variant b)
	{
		int m = a.type | b.type;
		if ((m & DOUBLE) != 0)
		{
			return new Variant (a.doubleValue () - b.doubleValue ());
		}
		else if ((m & FLOAT) != 0)
		{
			return new Variant (a.floatValue () - b.floatValue ());
		}
		else if ((m & LONG) != 0)
		{
			return new Variant (a.longValue () - b.longValue ());
		}
		else if ((m & INT) != 0)
		{
			return new Variant (a.intValue () - b.intValue ());
		}
		else
		{
			return new Variant (a.doubleValue () - b.doubleValue ());
		}
	}


	public static Variant operator$div (Variant a, Variant b)
	{
		int m = a.type | b.type;
		if ((m & DOUBLE) != 0)
		{
			return new Variant (a.doubleValue () / b.doubleValue ());
		}
		else if ((m & FLOAT) != 0)
		{
			return new Variant (a.floatValue () / b.floatValue ());
		}
		else
		{
			return new Variant (a.doubleValue () / b.doubleValue ());
		}
	}

	public static boolean operator$ge (Variant a, Variant b)
	{
		return ((a.type | b.type) == STRING) ? (a.svalue.compareTo (b.svalue) >= 0)
				: (a.doubleValue () >= b.doubleValue ()); 
	}

	public static boolean operator$gt (Variant a, Variant b)
	{
		return ((a.type | b.type) == STRING) ? (a.svalue.compareTo (b.svalue) > 0)
				: (a.doubleValue () > b.doubleValue ()); 
	}

	public static boolean operator$le (Variant a, Variant b)
	{
		return ((a.type | b.type) == STRING) ? (a.svalue.compareTo (b.svalue) <= 0)
				: (a.doubleValue () <= b.doubleValue ()); 
	}

	public static boolean operator$lt (Variant a, Variant b)
	{
		return ((a.type | b.type) == STRING) ? (a.svalue.compareTo (b.svalue) < 0)
				: (a.doubleValue () < b.doubleValue ()); 
	}

}
