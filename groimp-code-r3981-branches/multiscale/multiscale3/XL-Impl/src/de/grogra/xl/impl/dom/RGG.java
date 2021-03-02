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

package de.grogra.xl.impl.dom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class RGG
{
	private static final RuntimeModel RUNTIME = new RuntimeModel ();

	public static final int child = RuntimeModel.CHILD;
	public static final int sibling = RuntimeModel.SIBLING;
	public static final int attr = RuntimeModel.ATTRIBUTE;

	protected Document document;
	protected Graph graph;

	public static boolean E (Element e, String name)
	{
		return (e.getNodeType () == Node.ELEMENT_NODE) && e.getTagName ().equals (name);
	}

	public Element E (String name)
	{
		return document.createElement (name);
	}

	public void readDocument (InputStream in) throws Exception
	{
		setDocument (DocumentBuilderFactory.newInstance ()
			.newDocumentBuilder ().parse (in));
	}

	public void writeDocument (OutputStream out) throws IOException,
			TransformerException
	{
		TransformerFactory f = TransformerFactory.newInstance ();
		Transformer t = f.newTransformer ();
		t.setOutputProperty (OutputKeys.INDENT, "yes");
		t.transform (new DOMSource (document), new StreamResult (out));
	}

	public void setDocument (Document doc)
	{
		this.document = doc;
		graph = new Graph (RUNTIME, doc);
		RUNTIME.setCurrentGraph (graph);
	}

	public void execute (String[] args) throws Exception
	{
		String in = args[0];
		String out = args[1];
		int n = Integer.parseInt (args[2]);
		readDocument (new BufferedInputStream ("-".equals (in) ? System.in
				: new FileInputStream (in)));
		init ();
		derive ();
		while (--n >= 0)
		{
			run ();
			derive ();
		}
		finish ();
		derive ();
		writeDocument (new BufferedOutputStream ("-".equals (out) ? System.out
				: (OutputStream) new FileOutputStream (out)));
	}

	public void derive ()
	{
		graph.derive ();
	}

	protected void init ()
	{
	}

	protected void run ()
	{
	}

	protected void finish ()
	{
	}

}
