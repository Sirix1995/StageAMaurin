
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

public abstract class Executable extends Item
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (Executable.class);
		$TYPE.validate ();
	}

//enh:end

	public Executable (String key)
	{
		super (key);
	}


	public abstract void run (RegistryContext ctx, StringMap args);


	public static void runExecutables (Item dir, RegistryContext ctx,
									   StringMap args)
	{
		if (dir == null)
		{
			return;
		}
		args.put ("registry", ctx.getRegistry ());
		for (dir = (Item) dir.getBranch (); dir != null;
			 dir = (Item) dir.getSuccessor ())
		{
			if (dir.resolveLink (ctx) instanceof Executable)
			{
				((Executable) dir.resolveLink (ctx)).run (ctx, args);
			}
		}
	}


	public static void runExecutables
		(Registry reg, String dir, RegistryContext ctx, StringMap args)
	{
		runExecutables (reg.getItem (dir), ctx, args);
	}

}
