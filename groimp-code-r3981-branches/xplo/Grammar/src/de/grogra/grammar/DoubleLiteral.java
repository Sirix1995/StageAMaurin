
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

package de.grogra.grammar;

public final class DoubleLiteral extends NumberLiteral
{
	private double value;


	public DoubleLiteral (String value)
	{
		super (DOUBLE_LITERAL, value);
		this.value = Double.NEGATIVE_INFINITY;
	}


	public DoubleLiteral (double value)
	{
		super (DOUBLE_LITERAL, null);
		this.value = value;
	}


	@Override
	public double doubleValue ()
	{
		if (value == Double.NEGATIVE_INFINITY)
		{
			return value = new Double (super.getText ()).doubleValue ();
		}
		return value;
	}


	@Override
	public int intValue ()
	{
		return (int) doubleValue ();
	}


	@Override
	public long longValue ()
	{
		return (long) doubleValue ();
	}


	@Override
	public float floatValue ()
	{
		return (float) doubleValue ();
	}


	@Override
	public String getText ()
	{
		String s = super.getText ();
		if (s == null)
		{
			s = Double.toString (value);
			setText (s);
		}
		return s;
	}
}
