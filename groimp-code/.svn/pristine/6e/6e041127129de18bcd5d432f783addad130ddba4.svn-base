
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

import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceBindings;

public class Insert extends Hook
{
	private static final int RESOLVE_LINKS_MASK = 1 << Hook.USED_BITS;
	public static final int USED_BITS = Hook.USED_BITS + 1;

	// boolean resolveLinks
	//enh:field type=bits(RESOLVE_LINKS_MASK)

	String target;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field resolveLinks$FIELD;
	public static final NType.Field target$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Insert.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Insert) o).target = (String) value;
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
					return ((Insert) o).target;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Insert ());
		$TYPE.addManagedField (resolveLinks$FIELD = new NType.BitField ($TYPE, "resolveLinks", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, RESOLVE_LINKS_MASK));
		$TYPE.addManagedField (target$FIELD = new _Field ("target", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Insert ();
	}

//enh:end

	Insert ()
	{
		this (null);
	}


	public Insert (String key)
	{
		super (key);
		bits |= RESOLVE_LINKS_MASK;
	}


	@Override
	protected void runImpl (RegistryContext ctx, de.grogra.util.StringMap args)
	{
		Item d = ctx.getRegistry ()
			.getDirectory (target, getPluginDescriptor ());
/*		if (isI18NKeySet ())
		{
			d.setI18NKey (getI18NKey ());
		}
		*/
		insert (this, d, ctx);
	}


	private void insert (Node src, Item target, RegistryContext ctx)
	{
		for (src = src.getBranch (); src != null;
			 src = src.getSuccessor())
		{
			Item s = (Item) src;
			if ((bits & RESOLVE_LINKS_MASK) != 0)
			{
				s = s.resolveLink (ctx);
			}
			if (s instanceof Substitute)
			{
				((Substitute) s).run
					(target.getRegistry (), new de.grogra.util.StringMap ()
					 .putObject ("target", target));
			}
			else
			{
				Item t;
				if ((t = target.getItem (s.getName ())) == null)
				{
					try
					{
						t = (Item) s.clone (true);
						t.initPluginDescriptor (s.getPluginDescriptor ());
						if (s != src)
						{
//							t.setI18NKey (s.getI18NKey ());
						}
						target.add (t);
					}
					catch (CloneNotSupportedException e)
					{
						e.printStackTrace ();
					}
				}
				insert (s, t, ctx);
			}
		}
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		if ("create".equals (name))
		{
			return new Create ();
		}
		else if ("substitute".equals (name))
		{
			return new Substitute ();
		}
		else
		{
			return super.createItem (pb, name);
		}
	}


}
