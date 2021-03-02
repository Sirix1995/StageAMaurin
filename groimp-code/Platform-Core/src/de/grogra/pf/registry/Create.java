
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

import org.xml.sax.*;
import de.grogra.util.*;
import de.grogra.persistence.*;

final class Create extends de.grogra.pf.registry.Executable
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new Create ());
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
		return new Create ();
	}

//enh:end

	Create ()
	{
		super (null);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name) throws ClassNotFoundException
	{
		return new LazyItem (name);
	}


	void createTree (Item target)
	{
		ContentHandler r = new XMLRegistryReader
			(target.getRegistry (), getPluginDescriptor (),
			 new PersistenceBindings (getPluginDescriptor ()
									  .getPluginClassLoader (),
									  getRegistry ()),
			 target, false);
		try
		{
			r.startDocument ();
			r.startElement (Registry.NAMESPACE, "registry", null,
							new SAXElement ());
			LazyItem.parse (this, r);
			r.endElement (Registry.NAMESPACE, "registry", null);
			r.endDocument ();
		}
		catch (SAXException e)
		{
			throw new RuntimeException (e);
		}
	}


	@Override
	public void run (RegistryContext ctx, StringMap args)
	{
		createTree ((Item) args.get ("target"));
	}

}
