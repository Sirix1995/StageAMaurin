
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

public abstract class NumberLiteral extends Literal
{
	public static final int INT = 1 << INT_LITERAL;
	public static final int LONG = 1 << LONG_LITERAL;
	public static final int FLOAT = 1 << FLOAT_LITERAL;
	public static final int DOUBLE = 1 << DOUBLE_LITERAL;


	public NumberLiteral (int type, String text)
	{
		super (type, text);
	}


	public abstract int intValue ();


	public abstract long longValue ();


	public abstract float floatValue ();


	public abstract double doubleValue ();

}
