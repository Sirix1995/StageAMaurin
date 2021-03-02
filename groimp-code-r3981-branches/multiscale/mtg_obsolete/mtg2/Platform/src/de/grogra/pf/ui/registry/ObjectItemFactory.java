
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

package de.grogra.pf.ui.registry;

import de.grogra.persistence.*;
import de.grogra.graph.impl.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.util.*;

public class ObjectItemFactory extends ItemFactory
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new ObjectItemFactory ());
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
		return new ObjectItemFactory ();
	}

//enh:end


	@Override
	protected Item createItemImpl (Context ctx)
	{
		Object o = evaluate (ctx.getWorkbench (), UI.getArgs (ctx, null));
		if (o == null)
		{
			return null;
		}
		return ObjectItem.createReference (ctx.getWorkbench (), o, null);
	}


	@Override
	public Object evaluate (RegistryContext ctx, StringMap args)
	{
		Object oldValue = args.get ("oldValue");
		if ((getBranch () == null) && (oldValue != null))
		{
			Throwable t;
			try
			{
				try
				{
					return Utils.invoke (expr, new Object[] {oldValue},
										 getClassLoader ());
				}
				catch (NoSuchMethodException e)
				{
					return Utils.evaluate (expr, Utils.OBJECT_0,
										   getClassLoader ());
				}
			}
			catch (ClassNotFoundException e)
			{
				t = e;
			}
			catch (NoSuchFieldException e)
			{
				t = e;
			}
			catch (NoSuchMethodException e)
			{
				t = e;
			}
			catch (IllegalAccessException e)
			{
				t = e;
			}
			catch (InstantiationException e)
			{
				t = e;
			}
			catch (java.lang.reflect.InvocationTargetException e)
			{
				t = e.getCause ();
				if (t instanceof Error)
				{
					throw (Error) t;
				}
			}
			((Context) args.get ("context"))
				.getWorkbench ().logGUIInfo (null, t);
			return null;
		}
		return super.evaluate (ctx, args);
	}

}
