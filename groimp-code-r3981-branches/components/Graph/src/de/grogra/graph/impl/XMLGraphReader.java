
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

package de.grogra.graph.impl;

import java.util.HashSet;

import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.persistence.*;
import org.xml.sax.*;

public class XMLGraphReader extends XMLPersistenceReader
	implements ContentHandler
{
	
	public XMLGraphReader (GraphManager manager)
	{
		super (manager, manager.getBindings ());
	}


	public void startDocument () throws SAXException
	{
	}


	public void endDocument () throws SAXException
	{
		resolve ();
		while (!dummies.isEmpty ())
		{
			dummies.pop ().removeAll (null);
		}
	}


	private final ObjectList<Node> dummies = new ObjectList<Node> ();
	private final ObjectList<Object> stack = new ObjectList<Object> (20);
	private final ObjectList<SAXElement> saxElements = new ObjectList<SAXElement> ();
	private final HashSet<String> notFoundTypes = new HashSet<String> ();

	private SAXElement parent;
	private SAXElement previous;
	private int fieldDepth = 0;


	private void readFields () throws SAXException
	{
		if (parent != null)
		{
			if ((parent.children != null)
				&& !(stack.peek (1) instanceof PlaceholderNode))
			{
				readElements ((Node) stack.peek (1), parent);
			}
			recycle (parent, saxElements);
			parent = null;
		}
	}


	private static void recycle (SAXElement e, ObjectList<SAXElement> s)
	{
		SAXElement t = e.children;
		e.children = null;
		e.next = null;
		s.add (e);
		for (e = t; e != null; e = t)
		{
			t = e.next;
			recycle (e, s);
		}
	}


	public void startElement (String uri, String localName, String qName,
							  Attributes atts) throws SAXException
	{
		if ((fieldDepth == 0) && XMLGraphWriter.NAMESPACE.equals (uri))
		{
			if (stack.isEmpty ())
			{
				if (!"graph".equals (localName))
				{
					throw new SAXException ("graph");
				}
				stack.push (null);
				return;
			}
			else if ("node".equals (localName))
			{
				readFields ();
				Node n;
				String v = atts.getValue ("", "id");
				if (v != null)
				{
					long id = Long.parseLong (v);
					v = replaceType (atts.getValue ("", "type"));
					try
					{
						n = (Node) getBindings ().typeForName (v, true).newInstance ();
					}
					catch (ClassNotFoundException e)
					{
						if (notFoundTypes.add (v))
						{
							System.err.println ("Type " + v + " not found");
						}
						n = new PlaceholderNode ();
						dummies.add (n);
					}
					catch (Exception e)
					{
						throw new SAXException (e);
					}
					((GraphManager) getPersistenceManager ())
						.makePersistentImpl (n, id, null);
					v = atts.getValue ("", "root");
					if (v != null)
					{
						((GraphManager) getPersistenceManager ())
							.setRoot (v, n);
					}
					parent = saxElements.isEmpty () ? new SAXElement ()
							: saxElements.pop ();
					previous = null;
					parent.set (uri, localName, qName, atts);
					fieldDepth = 0;
				}
				else
				{
					v = atts.getValue ("", "ref");
					n = ((GraphManager) getPersistenceManager ())
						.getNodeOrPlaceholder (Long.parseLong (v));
				}
				v = atts.getValue ("", "edges");
				if (v != null)
				{
					Node p = (Node) stack.peek (1);
					if (p == null)
					{
						throw new SAXException ("error");
					}
					if(v.contains (",")) {
						while(v.length ()>1) {
							p.getOrCreateEdgeTo (n).addEdgeBits (
								Edge.parseEdgeKeys (v.substring (0,v.indexOf (',')), p, n), null);
							if(v.indexOf (',')!=-1) {
								v = v.substring (v.indexOf (',')+1);
							} else {
								v="";
							}
						}
					}
					p.getOrCreateEdgeTo (n).addEdgeBits (
						Edge.parseEdgeKeys (v, p, n), null);
				}
				stack.push (n);
				return;
			}
		}
		if (parent != null)
		{
			SAXElement e = saxElements.isEmpty () ? new SAXElement ()
					: saxElements.pop ();
			e.set (uri, localName, qName, atts);
			if (previous != null)
			{
				previous.next = e;
			}
			else
			{
				parent.children = e;
			}
			stack.push (parent);
			parent = e;
			previous = null;
			fieldDepth++;
		}
		else
		{
			throw new SAXException (uri + ":" + localName);
		}
	}

	public void endElement (String uri, String localName, String qName)
			throws SAXException
	{
		if (fieldDepth > 0)
		{
			previous = parent;
			parent = (SAXElement) stack.pop ();
			fieldDepth--;
		}
		else
		{
			readFields ();
			stack.pop ();
		}
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

	@Override
	protected ManageableType.Field getManagedField (ManageableType t, String name)
	{
		ManageableType.Field f = super.getManagedField (t, name);
		if ((f == null) && "extentTail".equals (name))
		{
			f = Node.extentTail$FIELD;
		}
		return f;
	}

}
