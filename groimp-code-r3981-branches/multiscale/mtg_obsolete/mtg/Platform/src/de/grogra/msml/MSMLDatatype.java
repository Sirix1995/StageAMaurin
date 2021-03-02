package de.grogra.msml;

import java.io.IOException;
import java.net.URL;
import de.grogra.graph.impl.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import de.grogra.pf.registry.Registry;

public interface MSMLDatatype
{
	public Node export (Registry r, org.w3c.dom.Node node, Node n, URL baseURL)throws IOException;

	public void export (Object o, Document doc, Element e, Node n);
}
