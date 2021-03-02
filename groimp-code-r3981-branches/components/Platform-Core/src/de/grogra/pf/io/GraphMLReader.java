
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

package de.grogra.pf.io;

import java.util.*;

import de.grogra.util.Utils;
import de.grogra.xl.util.ObjectList;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.*;
import de.grogra.persistence.*;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import org.xml.sax.*;

public class GraphMLReader extends XMLReaderBase
{
	static final String GRAPHML_NAMESPACE
		= "http://graphml.graphdrawing.org/xmlns";
	
	static final int ILLEGAL_DATA = -1, BITS = 0, TYPE = 1, VALUE = 2;

	HashMap keyToAttr = new HashMap (), idToNode = new HashMap ();
	ObjectList edges = new ObjectList ();
	XMLPersistenceReader xmlReader;

	int data;

	Node root;

	String nodeId, type, value;
	String sourceId, targetId, bits;
	

	public GraphMLReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.NODE);
	}


	@Override
	protected Object getObjectImpl ()
	{
		return root;
	}


	private static int getBits (String bits, Node s, Node t) throws SAXException
	{
		return (bits != null) ? Edge.parseEdgeKeys (bits, s, t) : Graph.BRANCH_EDGE;
	}


	@Override
	public void startDocument ()
	{
		xmlReader = new XMLPersistenceReader
			(null,
			 new PersistenceBindings (getRegistry (), getRegistry ()));
	}


	@Override
	public void endDocument () throws SAXException
	{
		while (!edges.isEmpty ())
		{
			Object e = edges.pop ();
			Node t = (Node) idToNode.get (edges.pop ());
			Node s = (Node) idToNode.get (edges.pop ());
			if (e instanceof Node)
			{
				s.addEdgeBitsTo ((Node) e, Graph.EDGENODE_IN_EDGE, null);
				((Node) e).addEdgeBitsTo (t, Graph.EDGENODE_OUT_EDGE, null);
			}
			else
			{
				s.addEdgeBitsTo (t, getBits ((String) e, s, t), null);
			}
		}
	}


	public void startElement (String uri, String localName, String qName,
							  Attributes atts) throws SAXException
	{
		data = ILLEGAL_DATA;
		if (GRAPHML_NAMESPACE.equals (uri))
		{
			String id = atts.getValue ("", "id");
			if ("data".equals (localName))
			{
				Object o = keyToAttr.get (atts.getValue ("", "key"));
				if (o == null)
				{
					throw new SAXException
						("Unknown key " + atts.getValue ("", "key"));
				}
				data = ((Integer) o).intValue ();
			}
			else if ("node".equals (localName))
			{
				nodeId = id;
				type = null;
				value = null;
			}
			else if ("edge".equals (localName))
			{
				sourceId = atts.getValue ("", "source");
				targetId = atts.getValue ("", "target");
				bits = null;
				type = null;
				value = null;
			}
			else if ("key".equals (localName))
			{
				String s = atts.getValue ("", "attr.name");
			addKey:
				if (s != null)
				{
					String f = atts.getValue ("", "for");
					int attr;
					if ("node".equals (f) || "edge".equals (f))
					{
						if (s.equals ("type"))
						{
							attr = TYPE;
						}
						else if (s.equals ("value"))
						{
							attr = VALUE;
						}
						else if ("edge".equals (f) && s.equals ("bits"))
						{
							attr = BITS;
						}
						else
						{
							break addKey;
						}
					}
					else
					{
						break addKey;
					}
					keyToAttr.put (id, Integer.valueOf (attr));
				}
			}
		}
	}


	private Node createNode () throws SAXException
	{
		if (type == null)
		{
			return null;
		}
		Node n;
		try
		{
			n = (Node) getRegistry ().typeForName (type).newInstance ();
		}
		catch (Exception e)
		{
			throw Utils.newSAXException (e);
		}
		if (value != null)
		{
			xmlReader.valueOf (n, value);
		}
		return n;
	}


	public void endElement (String uri, String localName, String qName)
		throws SAXException
	{
		data = ILLEGAL_DATA;
		if (GRAPHML_NAMESPACE.equals (uri))
		{
			if ("node".equals (localName))
			{
				Node n = createNode ();
				if (n == null)
				{
					n = new Node ();
				}
				idToNode.put (nodeId, n);
				if (root == null)
				{
					root = n;
				}
			}
			else if ("edge".equals (localName))
			{
				Node e = createNode ();
				Node t = (Node) idToNode.get (targetId), s;
				if ((t != null)
					&& ((s = (Node) idToNode.get (sourceId)) != null))
				{
					if (e != null)
					{
						s.addEdgeBitsTo (e, Graph.EDGENODE_IN_EDGE, null);
						e.addEdgeBitsTo (t, Graph.EDGENODE_OUT_EDGE, null);
					}
					else
					{
						s.addEdgeBitsTo (t, getBits (bits, s, t), null);
					}
				}
				else
				{
					edges.push (sourceId).push (targetId).push ((e != null) ? e : bits);
				}
			}
		}
	}


	@Override
	public void characters (char[] ch, int start, int length)
		throws SAXException
	{
		String s = new String (ch, start, length).trim ();
		if (s.length () > 0)
		{
			switch (data)
			{
				case BITS:
					bits = s;
					break;
				case TYPE:
					type = s;
					break;
				case VALUE:
					value = s;
					break;
			}
		}
	}

}
