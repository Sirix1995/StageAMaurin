
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

import de.grogra.reflect.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public class BooleanEditor extends PropertyEditor
{
	private static final int WRAPPED_MASK = 1 << PropertyEditor.USED_BITS;
	public static final int USED_BITS = PropertyEditor.USED_BITS + 1;

	// boolean wrapped
	//enh:field type=bits(WRAPPED_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field wrapped$FIELD;

	static
	{
		$TYPE = new NType (new BooleanEditor ());
		$TYPE.addManagedField (wrapped$FIELD = new NType.BitField ($TYPE, "wrapped", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, WRAPPED_MASK));
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
		return new BooleanEditor ();
	}

//enh:end

	private BooleanEditor ()
	{
		this (null);
	}


	public BooleanEditor (String key)
	{
		super (key);
	}


	@Override
	public boolean isNullAllowed ()
	{
		return (bits & WRAPPED_MASK) != 0;
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		return tree.new PropertyNode
			(p, p.getToolkit ().createBooleanWidget (tree.isMenu (), this), label);
	}


	@Override
	public Type getPropertyType ()
	{
		return ((bits & WRAPPED_MASK) != 0) ? ClassAdapter.wrap (Boolean.class)
			: Type.BOOLEAN;
	}
}
