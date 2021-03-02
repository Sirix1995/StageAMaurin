
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
import org.xml.sax.helpers.AttributesImpl;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceBindings;

final class LazyItem extends Item
{
	private final AttributesImpl attributes = new de.grogra.util.SAXElement ();


	LazyItem (String name)
	{
		super (name);
	}


	@Override
	protected Item createItem (PersistenceBindings pb,
							   String name) throws ClassNotFoundException
	{
		return new LazyItem (name);
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		attributes.addAttribute (uri, name, null, "CDATA", value);
		return true;
	}


	static void parse (Node parent, ContentHandler h) throws SAXException
	{
		for (parent = parent.getBranch (); parent != null;
			 parent = parent.getSuccessor ())
		{
			LazyItem l = (LazyItem) parent;
			h.startElement (Registry.NAMESPACE, l.getName (), null,
							l.attributes);
			parse (l, h);
			h.endElement (Registry.NAMESPACE, l.getName (), null);			
		}
	}
}
