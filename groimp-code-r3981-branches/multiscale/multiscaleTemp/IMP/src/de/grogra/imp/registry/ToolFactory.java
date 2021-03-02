
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

package de.grogra.imp.registry;

import de.grogra.imp.edit.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.registry.*;

public class ToolFactory extends Item implements UIItem
{
	String[] tools;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field tools$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ToolFactory.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ToolFactory) o).tools = (String[]) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((ToolFactory) o).tools;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ToolFactory ());
		$TYPE.addManagedField (tools$FIELD = new _Field ("tools", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 0));
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
		return new ToolFactory ();
	}

//enh:end


	public ToolFactory ()
	{
		super (null);
	}


	public Tool createTool (Object object, boolean asNode)
	{
		ToolRoot[] a = new ToolRoot[tools.length];
		for (int i = 0; i < tools.length; i++)
		{
			Exception f = null;
			try
			{
				a[i] = (ToolRoot) classForName (tools[i], true).newInstance ();
			}
			catch (ClassNotFoundException e)
			{
				f = e;
			}
			catch (IllegalAccessException e)
			{
				f = e;
			}
			catch (InstantiationException e)
			{
				f = e;
			}
			if (f != null)
			{
				Workbench.current ().logInfo
					(de.grogra.imp.IMP.getInstance ().getI18NBundle ()
					 .msg ("msg.tool.instantiation-failed", tools[i]), f);
			}
		}
		return new Tool (object, asNode, a);
	}


	public int getUINodeType ()
	{
		return UINodeHandler.NT_ITEM;
	}


	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}


	public boolean isAvailable (Context ctx)
	{
		return true;
	}


	public boolean isEnabled (Context ctx)
	{
		return true;
	}

}
