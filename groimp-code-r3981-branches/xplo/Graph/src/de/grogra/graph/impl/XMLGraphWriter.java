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

import java.io.IOException;

import de.grogra.util.IOWrapException;
import de.grogra.xl.util.ObjectList;
import de.grogra.persistence.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class XMLGraphWriter extends XMLPersistenceWriter implements GraphOutput {
	private final AttributesImpl atts = new de.grogra.util.SAXElement();
	private final StringBuffer buf = new StringBuffer();
	private GraphManager manager;
	private int visitedMark;
	private ObjectList nodesToWrite;
	private boolean onlyReferences;

	private void add(String name, String value) {
		atts.addAttribute("", name, name, "CDATA", value);
	}

	public XMLGraphWriter(ContentHandler ch, PersistenceOutputListener listener) {
		this(ch, listener, false);
	}

	public XMLGraphWriter(ContentHandler ch, PersistenceOutputListener listener, boolean onlyReferences) {
		super(ch, listener);
		this.onlyReferences = onlyReferences;
	}

	public void beginExtent(GraphManager manager, int rootCount)
			throws IOException {
		beginExtent(manager);
		atts.setAttributes(NS_ATTRIBUTE);
		try {
			getContentHandler().startPrefixMapping(NS_PREFIX, NAMESPACE);
		} catch (SAXException e) {
			throw new IOWrapException(e);
		}
		startElement("graph", atts);
		this.manager = manager;
		visitedMark = manager.allocateBitMark(false);
		nodesToWrite = new ObjectList();
	}

	@Override
	public void endExtent() throws IOException {
		while (!nodesToWrite.isEmpty()) {
			Node n = (Node) nodesToWrite.pop();
			if (!n.getBitMark(visitedMark)) {
				beginNode(n, null);
				endNode(n);
			}
		}
		nodesToWrite = null;
		manager.disposeBitMark(visitedMark, true);
		manager = null;
		endElement("graph");
		try {
			getContentHandler().endPrefixMapping(NS_PREFIX);
		} catch (SAXException e) {
			throw new IOWrapException(e);
		}
		super.endExtent();
	}

	private String rootName;

	public void beginRoot(String name) throws IOException {
		rootName = name;
	}

	public void endRoot(String name) throws IOException {
	}

	public void beginNode(Node node, Edge edge) throws IOException {
		atts.clear();
		if (rootName != null) {
			add("root", rootName);
			rootName = null;
		}
		boolean firstVisit = !node.setBitMark(visitedMark, true) && !onlyReferences;
		if (firstVisit) {
			add("id", Long.toString(node.getId()));
			add("type", node.getNType().getBinaryName());
			if (listener != null) {
				listener.objectWritten(node);
			}
		} else {
			add("ref", Long.toString(node.getId()));
		}
		if (edge != null) {
			buf.setLength(0);
			edge.getEdgeKeys(buf, true, false);
			add("edges", buf.toString());
		}
		startElement("node", atts);
		if (firstVisit) {
			writeFields(node);
		}
	}

	public void endNode(Node node) throws IOException {
		endElement("node");
	}

	@Override
	public void writePersistentObjectReference(PersistenceCapable o)
			throws IOException {
		super.writePersistentObjectReference(o);
		if (!((Node) o).getBitMark(visitedMark)) {
			nodesToWrite.add(o);
		}
	}

}
