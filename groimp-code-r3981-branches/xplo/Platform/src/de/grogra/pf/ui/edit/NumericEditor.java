
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

package de.grogra.pf.ui.edit;

import de.grogra.reflect.Type;

public class NumericEditor extends NumberEditor
{
	private static final int SLIDER_MASK = 1 << NumberEditor.USED_BITS;
	public static final int USED_BITS = NumberEditor.USED_BITS + 1;

	// boolean slider;
	//enh:field type=bits(SLIDER_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field slider$FIELD;

	static
	{
		$TYPE = new NType (new NumericEditor ());
		$TYPE.addManagedField (slider$FIELD = new NType.BitField ($TYPE, "slider", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, SLIDER_MASK));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new NumericEditor ();
	}

//enh:end

	private NumericEditor ()
	{
		super (null);
	}


	@Override
	public boolean isNullAllowed ()
	{
		return false;
	}


	@Override
	public Type getPropertyType ()
	{
		return Type.NUMERIC;
	}
}
