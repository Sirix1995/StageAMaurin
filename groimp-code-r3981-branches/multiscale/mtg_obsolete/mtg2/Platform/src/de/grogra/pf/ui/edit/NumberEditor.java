
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
import de.grogra.pf.ui.edit.PropertyEditorTree.Node;

public class NumberEditor extends PropertyEditor
{
	private double min;
	//enh:field

	private double max;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field min$FIELD;
	public static final NType.Field max$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NumberEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 0:
					((NumberEditor) o).min = (double) value;
					return;
				case 1:
					((NumberEditor) o).max = (double) value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 0:
					return ((NumberEditor) o).min;
				case 1:
					return ((NumberEditor) o).max;
			}
			return super.getDouble (o);
		}
	}

	static
	{
		$TYPE = new NType (new NumberEditor ());
		$TYPE.addManagedField (min$FIELD = new _Field ("min", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 0));
		$TYPE.addManagedField (max$FIELD = new _Field ("max", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 1));
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
		return new NumberEditor ();
	}

//enh:end

	private NumberEditor ()
	{
		this (null);
	}


	public NumberEditor (String key)
	{
		super (key);
	}


	@Override
	public boolean isNullAllowed ()
	{
		return true;
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		return tree.isMenu () ? null : tree.new PropertyNode
			(p, p.getToolkit ().createNumericWidget (p.getType (), p.getQuantity (), this), label);
	}


	@Override
	public Type getPropertyType ()
	{
		return Type.NUMBER;
	}
}
