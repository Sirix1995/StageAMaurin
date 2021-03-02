
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
import de.grogra.pf.registry.Void;
import de.grogra.util.StringMap;

public class Block extends Expression
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Block ());
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
		return new Block ();
	}

//enh:end


	public Block ()
	{
		super (null);
	}


	@Override
	public Object evaluate (RegistryContext ctx, StringMap args)
	{
		Object value = null;
		for (Item i = (Item) getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			Item r;
			if ((r = i.resolveLink (ctx)) instanceof Expression)
			{
				value = ((Expression) r).evaluate (ctx, args);
			}
			else if (i instanceof Void)
			{
				((Void) i).invoke (value, ctx, args);
			}
			else if (i instanceof Executable)
			{
				((Executable) i).run (ctx, args);
			}
		}
		return value;
	}

}
