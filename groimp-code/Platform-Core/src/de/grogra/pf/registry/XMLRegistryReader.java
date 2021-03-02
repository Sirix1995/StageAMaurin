
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

import java.lang.reflect.InvocationTargetException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.grogra.persistence.PersistenceBindings;
import de.grogra.persistence.XMLPersistenceReader;
import de.grogra.util.Utils;
import de.grogra.xl.util.ObjectList;

class XMLRegistryReader extends XMLPersistenceReader implements ContentHandler
{
	private final Registry registry;
	private final PluginDescriptor plugin;
	private final boolean processRegistry;
	private final boolean readUserItems;

	private final ObjectList stack = new ObjectList (32);
	private Item root = null;


	XMLRegistryReader (Registry registry, PluginDescriptor plugin,
					   PersistenceBindings bindings)
	{
		this (registry, plugin, bindings,
			  (registry == null) ? null : (Item) registry.getRoot (), false);
	}


	XMLRegistryReader (Registry registry, PluginDescriptor plugin,
					   PersistenceBindings bindings, Item root,
					   boolean readUserItems)
	{
		super (bindings);
		this.registry = registry;
		this.plugin = plugin;
		this.root = root;
		this.processRegistry = (registry != null) || (plugin != null);
		this.readUserItems = readUserItems;
	}

	
	private static Item replaceReference (Item parent, String name)
	{
		if (parent.getName ().equals ("project") && name.equals ("natures"))
		{
			return null;
		}
		Item r = null;
		if (parent.getName ().equals ("3d") && name.equals ("materials"))
		{
			r = parent.getItem ("shaders");
		}
		if (r == null)
		{
			System.err.println ("reference " + name + " cannot be resolved in " + parent);
		}
		return r;
	}

	Item createItem (String uri, String name, Attributes atts, Item parent)
		throws InvocationTargetException, InstantiationException,
		IllegalAccessException, ClassNotFoundException
	{
		Item i = null;
		String s;
		if (Registry.NAMESPACE.equals (uri))
		{
			if ("plugin".equals (name))
			{
				if (registry != null)
				{
					return null;
				}
				PluginDescriptor pd = new PluginDescriptor ();
				pd.initPluginDescriptor (null);
				return pd;
			}
			else if ((registry != null) && ("project".equals (name)))
			{
				for (int j = 0; j < atts.getLength (); j++)
				{
					registry.importAttributes.put (atts.getLocalName (j),
												   atts.getValue (j));
				}
				return null;
			}
			else if ("import".equals (name))
			{
				if (registry != null)
				{
					return null;
				}
				PluginPrerequisite p = new PluginPrerequisite ();
				parent.appendBranchNode (p);
				p.initPluginDescriptor ((PluginDescriptor) parent);
				return p;
			}
			else if ("library".equals (name))
			{
				if (registry != null)
				{
					return null;
				}
				Library l = new Library ();
				parent.appendBranchNode (l);
				l.initPluginDescriptor ((PluginDescriptor) parent);
				return l;
			}
			else if ("optpackage".equals (name))
			{
				if (registry != null)
				{
					return null;
				}
				OptionalPackage p = new OptionalPackage ();
				parent.appendBranchNode (p);
				p.initPluginDescriptor ((PluginDescriptor) parent);
				return p;
			}
			else if (!processRegistry)
			{
				return null;
			}
			else if ("registry".equals (name))
			{
				return root;
			}
			else if ("ref".equals (name))
			{
				if ((s = atts.getValue ("", "name")) == null)
				{
					throw new InstantiationException
						("reference " + name + " has no 'name'-attribute");
				}
				if ((i = parent.getItem (s)) == null)
				{
					i = replaceReference (parent, s);
				}
				return i;
			}
			else if ("directory".equals (name))
			{
				i = new Directory (null);
			}
			else if ("item".equals (name))
			{
				i = (Item) getBindings ().typeForName
					(atts.getValue ("", "class"), true).newInstance ();
			}
			else if ((i = parent.createItem (getBindings (), name))
					 != null)
			{
			}
			else
			{
				i = (Item) getBindings ().typeForName (name, true).newInstance ();
			}
		}
		if (i != null)
		{
			parent.add (i);
			if (plugin != null)
			{
				i.initPluginDescriptor (plugin);
			}
			else if (readUserItems)
			{
				i.makeUserItem (false);
			}
			return i;
		}
		throw new InstantiationException (uri + ':' + name);
	}


	public void startDocument () throws SAXException
	{
	}


	public void endDocument () throws SAXException
	{
	}


	public Item getRoot ()
	{
		return root;
	}


	private static String unquote (String s)
	{
		return ((s == null) || s.equals ("null")) ? null : Utils.unquote (s);
	}


	public void startElement (String uri, String localName, String qName,
							  Attributes atts) throws SAXException
	{
		Item o;
		Item p = stack.isEmpty () ? null : (Item) stack.peek (1);
		if (p == null)
		{
			p = new Item ("");
		}
		try
		{
			o = createItem (uri, localName, atts, p);
		}
		catch (Exception e)
		{
			throw Utils.newSAXException (e);
		}
		if ((o != null) && !("ref".equals (localName) && Registry.NAMESPACE.equals (uri)))
		{
			String s;
			if (((s = unquote (atts.getValue ("", "name"))) != null)
				&& ((p = p.getItem (s)) != null) && (p != o))
			{
				throw new SAXException ("Duplicate item " + p);
			}
			if (root == null)
			{
				root = o;
			}
			boolean item = Registry.NAMESPACE.equals (uri)
				&& "item".equals (localName);
			for (int i = 0, l = atts.getLength (); i < l; i++)
			{
				s = atts.getValue (i);
				if (!(item && "".equals (atts.getURI (i))
					  && "class".equals (atts.getLocalName (i)))
					&& !o.readAttribute (atts.getURI (i), atts.getLocalName (i), unquote (s)))
				{
					readAttribute (o, atts.getURI (i), atts.getLocalName (i),
								   s);
				}
			}
		}
		stack.push (o);
	}


	public void endElement (String uri, String localName, String qName)
		throws SAXException
	{
		stack.pop ();
	}


	public void characters (char[] ch, int start, int length)
		throws SAXException
	{
	}


	public void ignorableWhitespace (char[] ch, int start, int length)
	{
	}


	public void startPrefixMapping (String prefix, String uri)
		throws SAXException
	{
	}


	public void endPrefixMapping (String prefix) throws SAXException
	{
	}


	public void skippedEntity (String name) throws SAXException
	{
	}


	public void setDocumentLocator (Locator locator)
	{
	}


	public void processingInstruction (String target, String data)
		throws SAXException
	{
	}

}
