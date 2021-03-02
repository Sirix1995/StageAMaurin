
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

package de.grogra.pf.registry.expr;

import de.grogra.pf.registry.*;
import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;

public abstract class Expression extends Item
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (Expression.class);
		$TYPE.validate ();
	}

//enh:end

	Expression ()
	{
		super (null);
	}

	
	public Expression (String key)
	{
		super (key);
	}


	public abstract Object evaluate (RegistryContext ctx, StringMap args);


	public static boolean evaluateBoolean (Item i, RegistryContext ctx,
										   StringMap args)
	{
		return (i instanceof Expression)
			&& Boolean.TRUE.equals (((Expression) i).evaluate (ctx, args));
	}


	public static boolean isConditionFulfilled
		(Item dir, String condition, RegistryContext ctx, StringMap args)
	{
		if ((condition != null)
			&& !evaluateBoolean (dir.getRegistry ().getItem (condition),
								 ctx, args))
		{
			return false;
		}
		if ((dir.getBranch () instanceof Expression)
			&& !evaluateBoolean ((Item) dir.getBranch (), ctx, args))
		{
			return false;
		}
		return true;
	}


	public static Object[] getArgs (Item i, RegistryContext ctx,
									StringMap args, Item caller)
	{
		args.put ("item", caller);
		ObjectList v = new ObjectList ();
		while (i != null)
		{
			Item r = (Item) i.resolveLink (ctx);
			if (r instanceof Expression)
			{
				v.add (((Expression) r).evaluate (ctx, args));
			}
			else if (r instanceof Executable)
			{
				((Executable) r).run (ctx, args);
			}
			i = (Item) i.getSuccessor ();
		}
		return v.toArray ();
	}

}
