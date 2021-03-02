
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

import de.grogra.pf.boot.Main;

class OptionalPackage extends Prerequisite
{
	String jar;
	//enh:field

	String[] check;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field jar$FIELD;
	public static final NType.Field check$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (OptionalPackage.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((OptionalPackage) o).jar = (String) value;
					return;
				case 1:
					((OptionalPackage) o).check = (String[]) value;
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
					return ((OptionalPackage) o).jar;
				case 1:
					return ((OptionalPackage) o).check;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new OptionalPackage ());
		$TYPE.addManagedField (jar$FIELD = new _Field ("jar", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (check$FIELD = new _Field ("check", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 1));
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
		return new OptionalPackage ();
	}

//enh:end


	OptionalPackage ()
	{
		super ();
	}


	@Override
	public boolean isFulfilled ()
	{
		for (int i = 0; i < check.length; i++)
		{
			try
			{
				Class.forName (check[i], false, getClass ().getClassLoader ());
			}
			catch (ClassNotFoundException e)
			{
				return false;
			}
		}
		return true;
	}


	@Override
	public void addMessage (StringBuffer b, String plugin)
	{
		if (jar != null)
		{
			b.append (Main.getI18NBundle ().msg
					  ("optionalpackage.jar-not-installed", getName (), jar,
					   plugin));
		}
		else
		{
			b.append (Main.getI18NBundle ().msg
					  ("optionalpackage.not-installed", getName (), plugin));
		}
	}

}