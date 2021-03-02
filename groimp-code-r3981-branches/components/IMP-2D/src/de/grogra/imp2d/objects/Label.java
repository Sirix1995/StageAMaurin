
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

package de.grogra.imp2d.objects;

public abstract class Label extends FillableShape2D
{
	protected static final int TOP_MASK = 1 << FillableShape2D.USED_BITS;
	protected static final int BOTTOM_MASK = 2 << FillableShape2D.USED_BITS;
	protected static final int VERTICAL_MASK = TOP_MASK | BOTTOM_MASK;
	protected static final int LEFT_MASK = 4 << FillableShape2D.USED_BITS;
	protected static final int RIGHT_MASK = 8 << FillableShape2D.USED_BITS;
	protected static final int HORIZONTAL_MASK = LEFT_MASK | RIGHT_MASK;

	public static final int USED_BITS = FillableShape2D.USED_BITS + 4;


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (Label.class);
		$TYPE.validate ();
	}

//enh:end

}
