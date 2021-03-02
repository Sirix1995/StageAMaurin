
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

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.XMLGraphWriter;
import de.grogra.persistence.PersistenceOutputListener;
import de.grogra.pf.registry.Registry;
import de.grogra.util.MimeType;
import de.grogra.util.Utils;

public class GraphXMLSource extends SAXSourceBase
{
	public static final MimeType MIME_TYPE
		= new MimeType ("application/x-grogra-graph+xml", null);

	static final IOFlavor FLAVOR = new IOFlavor (MIME_TYPE, IOFlavor.SAX, null);


	private final GraphManager graph;
	private final PersistenceOutputListener ol;
	private final boolean writeOnlyReferences;
	private Node root;


	public GraphXMLSource (GraphManager graph, Registry reg,
						   PersistenceOutputListener ol)
	{
		this(graph, reg, ol, false);
	}


	public GraphXMLSource (GraphManager graph, Registry reg,
						   PersistenceOutputListener ol, Node root)
	{
		this(graph, reg, ol, false);
		this.root = root;
	}

	public GraphXMLSource (GraphManager graph, Registry reg,
			   PersistenceOutputListener ol, boolean writeOnlyReferences)
	{
		super (FLAVOR, reg, null);
		this.graph = graph;
		this.ol = ol;
		this.writeOnlyReferences = writeOnlyReferences;
	}

	public String getSystemId ()
	{
		return "graph";
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
				lh.startDTD ("graph", "-//GROGRA.DE//DTD graph//EN",
							 "graph.dtd");
				lh.endDTD ();
			}
			*/
			try
			{
				graph.writeExtent (new XMLGraphWriter (ch, ol, writeOnlyReferences), root);
			}
			catch (java.io.IOException e)
			{
				throw Utils.newSAXException (e);
			}
			ch.endDocument ();
		}
	}

}
