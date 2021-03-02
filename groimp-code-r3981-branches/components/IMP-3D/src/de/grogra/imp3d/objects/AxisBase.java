
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

import javax.vecmath.Color3f;

import de.grogra.imp.objects.FontAdapter;

public abstract class AxisBase extends ShadedNull
{
	protected static final int SCALE_V_MASK = 1 << ShadedNull.USED_BITS;

	public static final int USED_BITS = ShadedNull.USED_BITS + 1;

	// boolean scaleV
	//enh:field type=bits(SCALE_V_MASK) attr=Attributes.SCALE_V getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field scaleV$FIELD;

	static
	{
		$TYPE = new NType (AxisBase.class);
		$TYPE.addManagedField (scaleV$FIELD = new NType.BitField ($TYPE, "scaleV", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, SCALE_V_MASK));
		$TYPE.declareFieldAttribute (scaleV$FIELD, Attributes.SCALE_V);
		$TYPE.validate ();
	}

	public boolean isScaleV ()
	{
		return (bits & SCALE_V_MASK) != 0;
	}

	public void setScaleV (boolean v)
	{
		if (v) bits |= SCALE_V_MASK; else bits &= ~SCALE_V_MASK;
	}

//enh:end
}
