
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

import javax.swing.ListModel;

import de.grogra.pf.ui.edit.PropertyEditorTree.Node;
import de.grogra.reflect.Type;
import de.grogra.util.EnumValue;
import de.grogra.util.EnumerationType;

public class EnumerationEditor extends PropertyEditor
{
	protected ListModel list;

	boolean intenum = false;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field intenum$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (EnumerationEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((EnumerationEditor) o).intenum = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((EnumerationEditor) o).intenum;
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new EnumerationEditor ());
		$TYPE.addManagedField (intenum$FIELD = new _Field ("intenum", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
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
		return new EnumerationEditor ();
	}

//enh:end

	private EnumerationEditor ()
	{
		this (null, null);
	}


	public EnumerationEditor (String key, ListModel list)
	{
		super (key);
		this.list = list;
	}


	@Override
	public boolean isNullAllowed ()
	{
		return false;
	}


	@Override
	public Type getPropertyType ()
	{
		return intenum ? EnumerationType.INT_ENUMERATION
			: ((list != null) || (type != null)) ? super.getPropertyType ()
			: EnumerationType.OBJECT_ENUMERATION;
	}


	public void setList (ListModel list)
	{
		this.list = list;
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		ListModel m = list;
		if (m == null)
		{
			if (p.getType () instanceof ListModel)
			{
				m = (ListModel) p.getType ();
			}
			else
			{
				Object v = p.getValue ();
				if (v instanceof EnumValue)
				{
					m = ((EnumValue) v).getList ();
				}
				else
				{
					m = (ListModel) v;
				}
			}
		}
		final EnumerationType et = (m instanceof EnumerationType) ? (EnumerationType) m : null;
		return tree.new PropertyNode (p, p.getToolkit ().createChoiceWidget (m, tree.isMenu ()), label)
		{
			@Override
			protected Object toWidget (Object propertyValue)
			{
				if ((et == null) || (propertyValue == null))
				{
					return propertyValue;
				}
				return et.getDescriptionFor (propertyValue);
			}

			@Override
			protected Object fromWidget (Object widgetValue)
			{
				if (et == null)
				{
					return widgetValue;
				}
				return et.getValueFor (widgetValue);
			}
		};
	}

}
