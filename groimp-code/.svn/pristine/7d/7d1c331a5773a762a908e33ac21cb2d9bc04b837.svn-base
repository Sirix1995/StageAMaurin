
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

package de.grogra.pf.registry;

import de.grogra.util.StringMap;

public class ForAll extends Hook implements ItemVisitor
{
	String directory = "/";
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field directory$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ForAll.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ForAll) o).directory = (String) value;
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
					return ((ForAll) o).directory;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ForAll ());
		$TYPE.addManagedField (directory$FIELD = new _Field ("directory", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new ForAll ();
	}

//enh:end

	ForAll ()
	{
		this (null);
	}


	public ForAll (String key)
	{
		super (key);
	}


	@Override
	protected void runImpl (RegistryContext ctx, StringMap args)
	{
		forAll (Item.resolveItem (ctx, directory), null, null,
				this, args, false);
	}


	public void visit (Item item, Object info)
	{
		StringMap args = (StringMap) info;
		args.put ("item", item);
		runExecutables (this, (Registry) args.get ("registry"), args);
	}

}
