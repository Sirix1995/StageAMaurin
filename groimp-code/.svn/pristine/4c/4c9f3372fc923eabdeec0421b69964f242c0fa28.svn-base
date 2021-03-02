
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

import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.registry.expr.*;

public final class Root extends Item
{

	Root ()
	{
		super ("", true);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException, ClassNotFoundException
	{
		if ("application".equals (name))
		{
			return new Application ();
		}
		else if ("link".equals (name))
		{
			return new Link ();
		}
		else if ("hook".equals (name))
		{
			return new Hook ();
		}
		else if ("forall".equals (name))
		{
			return new ForAll ();
		}
		else if ("or".equals (name))
		{
			return new Or ();
		}
		else if ("and".equals (name))
		{
			return new And ();
		}
		else if ("not".equals (name))
		{
			return new Not ();
		}
		else if ("exists".equals (name))
		{
			return new Exists ();
		}
		else if ("instanceof".equals (name))
		{
			return new InstanceOf ();
		}
		else if ("map".equals (name))
		{
			return new CreateMap ();
		}
		else if ("null".equals (name))
		{
			return new NullConst ();
		}
		else if ("int".equals (name))
		{
			return new IntConst ();
		}
		else if ("float".equals (name))
		{
			return new FloatConst ();
		}
		else if ("string".equals (name))
		{
			return new StringConst ();
		}
		else if ("array".equals (name))
		{
			return new Array ();
		}
		else if ("point".equals (name))
		{
			return new PointConst ();
		}
		else if ("resource".equals (name))
		{
			return new Resource ();
		}
		else if ("var".equals (name))
		{
			return new Var ();
		}
		else if ("vars".equals (name))
		{
			return new Vars ();
		}
		else if ("setvar".equals (name))
		{
			return new SetVar ();
		}
		else if ("first".equals (name))
		{
			return new First ();
		}
		else if ("object".equals (name))
		{
			return new ObjectExpr ();
		}
		else if ("block".equals (name))
		{
			return new Block ();
		}
		else if ("void".equals (name))
		{
			return new de.grogra.pf.registry.Void ();
		}
		else if ("eval".equals (name))
		{
			return new LazyObjectItem (null, true);
		}
		else if ("option".equals (name))
		{
			return new Option ();
		}
		else if ("optiongroup".equals (name))
		{
			return new OptionGroup ();
		}
		else if ("insert".equals (name))
		{
			return new Insert ();
		}
		else if ("init".equals (name))
		{
			return new InitializeClass ();
		}
		else
		{
			return super.createItem (pb, name);
		}
	}


	@Override
	public de.grogra.util.I18NBundle getI18NBundle ()
	{
		return de.grogra.pf.boot.Main.getApplication ().getI18NBundle ();
	}

}
