
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

package de.grogra.imp3d.objects;

import de.grogra.imp3d.*;

public abstract class Label extends Null implements Renderable
{
	protected static final int TOP_MASK = 1 << Null.USED_BITS;
	protected static final int BOTTOM_MASK = 2 << Null.USED_BITS;
	protected static final int VERTICAL_MASK = TOP_MASK | BOTTOM_MASK;
	protected static final int LEFT_MASK = 4 << Null.USED_BITS;
	protected static final int RIGHT_MASK = 8 << Null.USED_BITS;
	protected static final int HORIZONTAL_MASK = LEFT_MASK | RIGHT_MASK;

	public static final int USED_BITS = Null.USED_BITS + 4;

	// int horizontalAlignment
	//enh:field type=bits(HORIZONTAL_MASK) attr=Attributes.HORIZONTAL_ALIGNMENT

	// int verticalAlignment
	//enh:field type=bits(VERTICAL_MASK) attr=Attributes.VERTICAL_ALIGNMENT

	
	public Label ()
	{
		setLayer (3);
	}


	public void setAlignment (int horizontalAlignment, int verticalAlignment)
	{
		setHorizontalAlignment (horizontalAlignment);
		setVerticalAlignment (verticalAlignment);
	}

	public void setHorizontalAlignment (int alignment)
	{
		bits &= ~(LEFT_MASK | RIGHT_MASK);
		if (alignment == Attributes.H_ALIGN_LEFT)
		{
			bits |= LEFT_MASK;
		}
		else if (alignment == Attributes.H_ALIGN_RIGHT)
		{
			bits |= RIGHT_MASK;
		}
	}


	public int getHorizontalAlignment ()
	{
		switch (bits & (LEFT_MASK | RIGHT_MASK))
		{
			case LEFT_MASK:
				return Attributes.H_ALIGN_LEFT;
			case RIGHT_MASK:
				return Attributes.H_ALIGN_RIGHT;
			default:
				return Attributes.H_ALIGN_CENTER;
		}
	}


	public void setVerticalAlignment (int alignment)
	{
		bits &= ~(TOP_MASK | BOTTOM_MASK);
		if (alignment == Attributes.V_ALIGN_TOP)
		{
			bits |= TOP_MASK;
		}
		else if (alignment == Attributes.V_ALIGN_BOTTOM)
		{
			bits |= BOTTOM_MASK;
		}
	}


	public int getVerticalAlignment ()
	{
		switch (bits & (TOP_MASK | BOTTOM_MASK))
		{
			case TOP_MASK:
				return Attributes.V_ALIGN_TOP;
			case BOTTOM_MASK:
				return Attributes.V_ALIGN_BOTTOM;
			default:
				return Attributes.V_ALIGN_CENTER;
		}
	}


//	enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field horizontalAlignment$FIELD;
	public static final NType.Field verticalAlignment$FIELD;

	static
	{
		$TYPE = new NType (Label.class);
		$TYPE.addManagedField (horizontalAlignment$FIELD = new NType.BitField ($TYPE, "horizontalAlignment", 0 | NType.BitField.SCO, de.grogra.reflect.Type.INT, HORIZONTAL_MASK));
		$TYPE.addManagedField (verticalAlignment$FIELD = new NType.BitField ($TYPE, "verticalAlignment", 0 | NType.BitField.SCO, de.grogra.reflect.Type.INT, VERTICAL_MASK));
		$TYPE.declareFieldAttribute (horizontalAlignment$FIELD, Attributes.HORIZONTAL_ALIGNMENT);
		$TYPE.declareFieldAttribute (verticalAlignment$FIELD, Attributes.VERTICAL_ALIGNMENT);
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.validate ();
	}

//enh:end

}
