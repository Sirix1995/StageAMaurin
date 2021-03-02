
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

package de.grogra.util;

public class Unit
{
	private final String name;
	private final double factor;
	private final boolean check;


	public Unit (String name, double factor, boolean check)
	{
		this.name = name;
		this.factor = factor;
		this.check = check;
	}


	public double toUnit (double qvalue)
	{
		return qvalue / factor;
	}


	public double fromUnit (double uvalue)
	{
		return uvalue * factor;
	}


	public boolean checkWhenChoosing ()
	{
		return check;
	}


	public float getRatio (float value)
	{
		if (value == 0)
		{
			return -1;
		}
		value = Math.abs (value) / (float) factor;
		if (value > 1)
		{
			value = 1 / value;
		}
		return value;
	}


	public boolean isSpecified (String s)
	{
		int i = s.lastIndexOf (name);
		if (i >= 0)
		{
			if ((i == 0) || !Character.isLetter (s.charAt (i - 1)))
			{
				i += name.length ();
				while (i < s.length ())
				{
					if (!Character.isWhitespace (s.charAt (i++)))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}


	public String toString (float value)
	{
		return (float) toUnit (value) + " " + name;
	}


	public String toString (double value)
	{
		return toUnit (value) + " " + name;
	}


	public double parse (String s) throws NumberFormatException
	{
		int i = s.lastIndexOf (name);
		if (i >= 0)
		{
			s = s.substring (0, i);
		}
		s = s.trim ();
		return fromUnit (Double.parseDouble (s));
	}

}
