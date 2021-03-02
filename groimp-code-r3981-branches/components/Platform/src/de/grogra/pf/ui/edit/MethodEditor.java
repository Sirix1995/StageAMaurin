
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

import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public class MethodEditor extends PropertyEditor
{
	boolean nullAllowed;
	//enh:field getter

	String method;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field nullAllowed$FIELD;
	public static final NType.Field method$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MethodEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((MethodEditor) o).nullAllowed = (boolean) value;
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
					return ((MethodEditor) o).isNullAllowed ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((MethodEditor) o).method = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((MethodEditor) o).method;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new MethodEditor ());
		$TYPE.addManagedField (nullAllowed$FIELD = new _Field ("nullAllowed", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.addManagedField (method$FIELD = new _Field ("method", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
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
		return new MethodEditor ();
	}

	public boolean isNullAllowed ()
	{
		return nullAllowed;
	}

//enh:end

	private MethodEditor ()
	{
		super (null);
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		if (tree.isMenu ())
		{
			return null;
		}
		try
		{
			return (Node) de.grogra.util.Utils.invoke
				(method, new Object[] {this, tree, p, label}, getClassLoader ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}

}
