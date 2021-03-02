
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

package de.grogra.imp3d.msml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.vecmath.Matrix3f;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.shading.*;
//import de.grogra.msml.CoordTransformer;
import de.grogra.msml.MSMLDatatype;
import de.grogra.msml.MSNode;
import de.grogra.msml.MSMLMetadata;
import de.grogra.pf.io.DOMSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.NodeReference;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.registry.Registry;
import de.grogra.util.MimeType;

public class MSMLWriter extends FilterBase implements DOMSource
{
	public static final IOFlavor FLAVOR
		= new IOFlavor (new MimeType ("text/msml+extsrc+xml", null),
						IOFlavor.DOM, null);
	public static final String namespaceURI="http://grogra.de/msml"; 
	public static final String qualifiedName="msml";
	public static final String schemaSource = "msml-base.xsd";
	public static final String msmlSchemaLocation=namespaceURI+" "+schemaSource;
	
	static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";
    
    static final int ROOT=0;
    static final int MSOBJECT=1;
    static final int GROUP=2;
    static final int SHAPE=3;
    
    public static final CoordTransformer COORD =
    	new CoordTransformer(
    		new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1),
    		new Matrix3f(1, 0, 0, 0, 0, -1, 0, 1, 0)
    	);
    
    HashMap elemAllocation = new HashMap ();
    HashMap edgeTypes = new HashMap ();
    HashMap idconvert = new HashMap();
    public HashMap librarymaterials = new HashMap();
    
	public MSMLWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
		initElemAllocation ();
		initEdgeTypeAllocation ();
	}
	
	public HashMap getLibraryMaterials(){
		return librarymaterials;
	}
	
	private void initEdgeTypeAllocation ()
	{
		edgeTypes.put (new Integer(Graph.SUCCESSOR_EDGE),"successor");
		edgeTypes.put (new Integer(Graph.BRANCH_EDGE),"branch");
		edgeTypes.put (new Integer(Graph.CONTAINMENT_EDGE),"refinement");
	}

	private void initElemAllocation ()
	{
		for (Node i = item.getBranch (); i != null; i = i.getSuccessor ())
		{
			if (i instanceof ObjectItem)
			{
				ObjectItem oi = (ObjectItem) i;
				elemAllocation.put (oi.getName (), oi.getObject ());
			}
		}
	}
	
	public Document getDocument () throws IOException, DOMException
	{
		Document doc=null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(false);
		try {
	        dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
	    } catch (IllegalArgumentException x) {
	        System.out.println("The parser does not support JAXP 1.2");
	    }
	    dbf.setIgnoringComments(true);
	    DocumentBuilder db;
	    try {
	    	db = dbf.newDocumentBuilder();
	    	doc = db.newDocument();
	    }catch (ParserConfigurationException e){
	    	System.out.println("Parser misconfigured:"+e);
	    	return null;
	    }
	    
	    Registry r = (Registry) ((ObjectSource) source).getObject();
		GraphManager g = r.getProjectGraph();
		Node node = (Node) g.getRoot(GraphManager.MAIN_GRAPH);
		/* rekursiv den Graphen traversieren.
		 * wenn es ein Baum ist, kein Problem.
		 * wenn es Zyklen gibt, beim Traversieren die Knoten markieren, wenn
		 * sie abgearbeitet sind, um Endlosschleifen zu verhindern. In diesem Fall
		 * im XML-Dokument nur noch mit Referenzen auf die bereits bestehenden 
		 * XML-elemente arbeiten.
		 */
		
		Element root = (Element) doc.createElementNS(namespaceURI,"msml"); 
		doc.appendChild(root);
		Attr attr=doc.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance","xsi:schemaLocation");
		attr.setNodeValue(msmlSchemaLocation);
		root.setAttributeNodeNS(attr);
		root.setAttribute("version", "1.0");
		//create library
		Element library =(Element)doc.createElementNS(namespaceURI,"library");
		Element data =(Element)doc.createElementNS(namespaceURI,"data");
		Item dir = r.getDirectory("/project/objects/3d/materials",null);
		for (Node e = dir.getBranch(); e != null; e = e.getSuccessor())
		{
			if (e instanceof ObjectItem)
			{
				ObjectItem oi = (ObjectItem) e;
				Appearance.exportShader(this, doc, data, (Shader)(oi.getObject()),oi.getName());
				librarymaterials.put(oi.getObject(),oi.getName ());
			}
		}
		if (data.hasChildNodes()){
			library.appendChild(data);
			root.appendChild(library);
		}
		traverseNode(doc,root,null,node,checkNodeType(node));
	    return doc;
	}
	private int checkNodeType(Node n){
		if (Attributes.isVisible(n,true,n.getGraph())){
			return SHAPE;
		}
		if (n instanceof MSNode){
			return MSOBJECT;
		}
		//if the node is a root and has only one child, then mode=ROOT,
		//else mode=GROUP
		if (n.isRoot()&&(n.getDirectChildCount()==1)){
			return ROOT;
		}
		return GROUP;
	}
	
	private void traverseNode(Document doc, Element node, Element msnode, Node n,int mode){
		switch (mode){
			case ROOT:{
				for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)){
					if (e.isSource(n)){
						traverseNode(doc,node,msnode,e.getTarget(),checkNodeType(e.getTarget()));
					}
				}
				break;
			}
			case SHAPE:{
				exportSSObject(doc,node,n);
				break;
			}
			case MSOBJECT:{
				exportMSObject(doc,node,(MSNode)n);
				break;
			}
			case GROUP:{
				Element group=(Element) doc.createElementNS(namespaceURI,"group");
				String groupId=transformId(n);
				group.setAttribute("id",groupId);
				String name=(n.getName()==null)?"":n.getName();
				if (name!=""){
					group.setAttribute("name",name);
				}
				node.appendChild(group);
				if (elemAllocation.containsKey(n.getClass().getName())){
					Element data=(Element) doc.createElementNS(namespaceURI,"data");
					((MSMLDatatype)elemAllocation.get(n.getClass().getName())).export(this,doc,data,n);
					group.appendChild(data);
				}
				for (Edge e = n.getFirstEdge(); e != null; e = e.getNext(n)){
					//only branch/successor-edges will be traversed
					if (e.isSource(n)&&
							(((e.getEdgeBits()&(Graph.SUCCESSOR_EDGE|Graph.BRANCH_EDGE))>0))){
						traverseNode(doc,group,msnode,e.getTarget(),checkNodeType(e.getTarget()));
					}
				}
				break;
			}
		}
	}
	
	private void exportMSObject (Document doc, Element elem, MSNode msn){
		Element node=(Element) doc.createElementNS(namespaceURI,"msobject");
		node.setAttribute("id",transformId(msn));
		String name=(msn.getName()==null)?"":msn.getName();
		if (name!=""){
			node.setAttribute("name",name);
		}
		Node scale=msn.getScale();
		if ((scale!=null)&&(scale.getName()!="")){
			node.setAttribute("showScale",scale.getName());
		}
		for (Edge e = msn.getFirstEdge(); e != null; e = e.getNext(msn)){
			if (e.isSource(msn)){
				exportScale(doc,node,e.getTarget());
			}
		}
		elem.appendChild(node);
	}
	
	private void exportScale (Document doc, Element msnode, Node n){
		Element node=(Element) doc.createElementNS(namespaceURI,"scale");
		msnode.appendChild(node);
		node.setAttribute("id",transformId(n));
		String name=(n.getName()==null)?"":n.getName();
		if (name!=""){
			node.setAttribute("name",name);
		}
		for (Edge e = n.getFirstEdge (); e != null; e = e.getNext(n)){
			if (e.isSource(n)){
				traverseNode(doc,node,e.getTarget());
			}
		}
	}
	
	private void exportSSObject (Document doc, Element elem, Node n){
		Element node=(Element) doc.createElementNS(namespaceURI,"msobject");
		node.setAttribute("id",transformId(n)+"ms");
		Element scale=(Element) doc.createElementNS(namespaceURI,"scale");
		scale.setAttribute("id",transformId(n)+"s");
		node.appendChild(scale);
		traverseNode(doc,scale,n);
		elem.appendChild(node);
	}
	
	/* 
	 * Starting from node n, all nodes connected with branch/successor-edges will be traversed.
	 * The nodes and edges, that are starting from them, will be exported.
	 */ 
	private void traverseNode(Document doc, Element node, Node n){
		exportNode(doc,node,n);
		for (Edge e = n.getFirstEdge (); e != null; e = e.getNext(n)){
			if (e.isSource(n)){
				Node next=e.getTarget();
				exportEdge(doc,node,e,Graph.CONTAINMENT_EDGE);
				org.w3c.dom.Node parent=node.getParentNode();
				//if refinement-edges exist, they will be exported
				if (parent!=null){
					exportEdge(doc,(Element)parent,Graph.CONTAINMENT_EDGE,e);
				}
				if ((e.getEdgeBits()&(Graph.SUCCESSOR_EDGE|Graph.BRANCH_EDGE))>0){
					if (next instanceof MSNode){
						traverseMSNode(doc,node,(MSNode)next);
					}else{
						traverseNode(doc,node,next);
					}
				}
			}
		}
	}
	
	private void traverseMSNode(Document doc, Element elem, MSNode msn){
		//msobject
		Element mso=(Element) doc.createElementNS(namespaceURI,"msobject");
		elem.appendChild(mso);
		mso.setAttribute("id",transformId(msn));
		String name=(msn.getName()==null)?"":msn.getName();
		if (name!=""){
			mso.setAttribute("name",name);
		}
		Node selectedscale=msn.getScale();
		if ((selectedscale!=null)&&(selectedscale.getName()!="")){
			mso.setAttribute("showScale",selectedscale.getName());
		}
		for (Edge e = msn.getFirstEdge(); e != null; e = e.getNext(msn)){
			if (e.isSource(msn)){
				//scale
				Node scale=e.getTarget();
				Element s=(Element) doc.createElementNS(namespaceURI,"scale");
				mso.appendChild(s);
				s.setAttribute("id",transformId(scale));
				name=(scale.getName()==null)?"":scale.getName();
				if (name!=""){
					s.setAttribute("name",name);
				}
				for (Edge e2 = scale.getFirstEdge (); e2 != null; e2 = e2.getNext(scale)){
					if (e2.isSource(scale)){
						traverseMSNodeScale(doc,s,e2.getTarget(),elem,mso,selectedscale.equals(scale));
					}
				}
			}
		}
	}
	
	private void traverseMSNodeScale (Document doc, Element scale, Node n,Element supernode,Element mso, boolean selectedscale){
		exportNode(doc,scale,n);
		for (Edge e = n.getFirstEdge (); e != null; e = e.getNext(n)){
			if (e.isSource(n)){
				Node next=e.getTarget();
				if ((e.getEdgeBits()&Graph.STD_EDGE_5)==0){
					exportEdge(doc,scale,e,Graph.CONTAINMENT_EDGE);//?
					//if refinement-edges exist, they will be exported
					exportEdge(doc,mso,Graph.CONTAINMENT_EDGE,e);
					if ((e.getEdgeBits()&(Graph.SUCCESSOR_EDGE|Graph.BRANCH_EDGE))>0){
						traverseMSNodeScale(doc,scale,next,supernode,mso,selectedscale);
					}
				}else{
					exportEdge(doc,supernode,e,Graph.CONTAINMENT_EDGE);//?
					//if refinement-edges exist, they will be exported
					exportEdge(doc,mso,Graph.CONTAINMENT_EDGE,e);
					if ((e.getEdgeBits()&(Graph.SUCCESSOR_EDGE|Graph.BRANCH_EDGE))>0){
						//if the actual scale is the choosen one, then traverse
						//next object (but no longer as part of msobject)
						if (selectedscale){
							if (next instanceof MSNode){
								traverseMSNode(doc,supernode,(MSNode)next);
							}else{
								traverseNode(doc,supernode,next);
							}
						}
					}
				}
			}
		}
	}
	private void exportNode (Document doc, Element elem, Node n){
		Element node=(Element) doc.createElementNS(namespaceURI,"node");
		node.setAttribute("id",transformId(n));
		String name=(n.getName()==null)?"":n.getName();
		if (name!=""){
			node.setAttribute("name",name);
		}
		//process attributes
		Element data=doc.createElementNS(namespaceURI,"data");
		GraphState gs = GraphState.current(n.getGraph());
		Object shape = gs.getObjectDefault(n, true, Attributes.SHAPE, null);
		if (shape!=null){
			String key=shape.getClass().getName();
			if (elemAllocation.containsKey(key)){
				((MSMLDatatype)elemAllocation.get(key)).export(this,doc,data,n);
			}
		}
		try{
			MSMLMetadata metadata=(MSMLMetadata)n.getFirst(Graph.STD_EDGE_5);
			if (metadata!=null){
				Set keys=metadata.getAllKeys();
				Iterator it=keys.iterator();
				while(it.hasNext()){
					org.w3c.dom.Node tempNode = doc.importNode(metadata.getMetadata(it.next().toString()),true);
					data.appendChild(tempNode);
				}
			}
		}catch(ClassCastException e){}
		if (data.hasChildNodes()){
			node.appendChild(data);
		}
		elem.appendChild(node);
	}
	
	//exports all edgetypes of an edge e, except the GroIMP-edgetypes that are specified in exclude
	private void exportEdge (Document doc, Element n, Edge e, int exclude){
		Set types=edgeTypes.keySet();
		Iterator it=types.iterator();
		while (it.hasNext()){
			int edgeType=((Integer)it.next()).intValue();
			if (e.testEdgeBits(edgeType)&&((edgeType&exclude)==0)){
				exportEdge(doc,n,edgeType,e);
			}
		}
	}
	
	//	exports all edgetypes of an edge e
	private void exportEdge (Document doc, Element n, Edge e){
		exportEdge(doc,n,e,0);
	}
	
	//exports an edge with the GroIMP-edgetype, that is specified in type
	private void exportEdge (Document doc, Element n, int type, Edge e){
		if (e.testEdgeBits(type)){
			Element node=(Element) doc.createElementNS(namespaceURI,"edge");
			node.setAttribute("source",transformId(e.getSource()));
			node.setAttribute("target",transformId(e.getTarget()));
			if ((e.getEdgeBits() & Graph.SPECIAL_EDGE_MASK) > 0)
			{
				node.setAttribute("order",""+(e.getEdgeBits() & Graph.SPECIAL_EDGE_MASK));
			}
			node.setAttribute("type",(String)edgeTypes.get(new Integer(type)));
			n.appendChild(node);
		}
	}
	
	private String transformId(Node n){
		return "id"+Long.toString(n.getId());
	}
}
