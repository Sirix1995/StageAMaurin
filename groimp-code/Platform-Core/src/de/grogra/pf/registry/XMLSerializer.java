
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

import java.util.Collection;

import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.persistence.XMLPersistenceWriter;
import de.grogra.pf.io.*;

import org.xml.sax.*;
import org.xml.sax.ext.*;
import org.xml.sax.helpers.AttributesImpl;

public final class XMLSerializer extends SAXSourceBase implements ObjectSource
{
	static final IOFlavor FLAVOR = new IOFlavor
		(Registry.MIME_TYPE, IOFlavor.SAX | IOFlavor.OBJECT, Registry.class);

	private static final AttributesImpl NS_ATTRIBUTE = new AttributesImpl ();
	static
	{
		NS_ATTRIBUTE.addAttribute ("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA",
								   Registry.NAMESPACE);
	}


	public interface Filter
	{
		// when filter returns null, the item shall not be written.
		// when filter returns a non-empty attribute set, those attributes shall be used instead of the attributes of the item
		AttributesImpl filter(Item item, AttributesImpl attr);
	}

	XMLPersistenceWriter writer;

	private final Collection neededPlugins;
	Filter filter;

	private final ObjectList stack = new ObjectList ();
	private final AttributesImpl attr = new SAXElement ();
	private final AttributesImpl attr2 = new SAXElement ();
	private ContentHandler ch = null;


	XMLSerializer (Registry registry, Collection neededPlugins, Filter filter)
	{
		super (FLAVOR, registry, null);
		this.writer = new XMLPersistenceWriter
			(null, new PluginCollector (neededPlugins));
		this.neededPlugins = neededPlugins;
		this.filter = filter;
	}


	public String getSystemId ()
	{
		return "registry";
	}


	public Object getObject ()
	{
		return getRegistry ();
	}


	public void parse (ContentHandler ch, ErrorHandler eh,
					   LexicalHandler lh, DTDHandler dh,
					   EntityResolver er) throws SAXException
	{
		if (ch != null)
		{
			ch.startDocument ();
/*			if (lh != null)
			{
				lh.startDTD ("registry", "-//GROGRA.DE//DTD registry//EN",
							 "registry.dtd");
				lh.endDTD ();
			}
			*/
			this.ch = ch;
			AttributesImpl a;
			if (providePrefixes)
			{
				a = NS_ATTRIBUTE;
			}
			else
			{
				a = attr;
				a.clear ();
			}
			ch.startElement (Registry.NAMESPACE, "project", "project", a);
			((Item) getRegistry ().getRoot ()).accept (this);
			ch.endElement (Registry.NAMESPACE, "project", "project");
			ch.endDocument ();
		}
	}


	AttributesImpl startElement (Item item)
	{
		attr.clear ();
		AttributesImpl f = (filter != null) ? filter.filter(item, attr) : attr;
		if (f == null)
		{
			return null;
		}
		if (item.isUserItem ())
		{
			item.addPluginPrerequisites (neededPlugins);
		}
		stack.push (item);
		return f;
	}


	void setAttributes (AttributesImpl a) throws SAXException
	{
		int m = stack.size () - 1, i = m - 1;
		while ((i >= 0) && (stack.get (i) instanceof Item))
		{
			i--;
		}
		while (++i <= m)
		{
			AttributesImpl ea;
			String n;
			if (i == 0)
			{
				ea = attr2;
				ea.clear ();
			}
			else
			{
				if (i < m)
				{
					ea = attr2;
					ea.clear ();
					ea.addAttribute ("", "name", "name", "CDATA", ((Item) stack.get (i)).getName ());
				}
				else
				{
					ea = a;
				}
			}
			n = (i == 0) ? "registry" : (i < m) ? "ref"
				: ((Item) stack.get (i)).getXMLElementName ();
			ch.startElement (Registry.NAMESPACE, n, n, ea);
			stack.set (i, n);
		}
	}


	void endElement () throws SAXException
	{
		Object o;
		if ((o = stack.pop ()) instanceof String)
		{
			String n = stack.isEmpty () ? "registry" : (String) o;
			ch.endElement (Registry.NAMESPACE, n, n);
		}
	}

}
