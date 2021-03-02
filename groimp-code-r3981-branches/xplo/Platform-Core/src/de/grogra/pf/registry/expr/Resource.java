
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

import java.io.IOException;
import java.net.URL;

import de.grogra.pf.registry.RegistryContext;
import de.grogra.util.StringMap;

public final class Resource extends Expression
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Resource ());
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
		return new Resource ();
	}

//enh:end

	public Resource ()
	{
		this (null);
	}


	public Resource (String name)
	{
		super (name);
	}


	@Override
	public Object evaluate (RegistryContext ctx, StringMap args)
	{
		URL url;
		if (args.getBoolean ("pluginURL"))
		{
			url = getPluginDescriptor ().getURLForResource (getName ());
		}
		else
		{
			url = getClassLoader ().getResource (getName ());
		}
		if ((url != null) && (url.getPath ().indexOf (' ') >= 0))
		{
			// workaround for bug in JRE 1.4: replace spaces by %20
			StringBuffer buf = new StringBuffer (url.toString ());
			for (int i = buf.length () - 1; i >= 0; i--)
			{
				if (buf.charAt (i) == ' ')
				{
					buf.replace (i, i + 1, "%20");
				}
			}
			try
			{
				url = new URL (buf.toString ());
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		}
		return url;
	}

}
