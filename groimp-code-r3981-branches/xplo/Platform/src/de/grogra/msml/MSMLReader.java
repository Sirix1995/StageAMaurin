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

package de.grogra.msml;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;


import org.w3c.dom.Document;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.pf.io.DOMSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.StringMap;

public class MSMLReader extends FilterBase implements ObjectSource
{
	static final String MSML_NAMESPACE = "http://grogra.de/msml";

	Registry registry;
	HashMap elemAllocation = new HashMap ();
	HashMap edgeTypes = new HashMap ();

	HashMap idToNode = new HashMap ();

	public MSMLReader (FilterItem item, FilterSource source)
	{
		this(Workbench.current ().getRegistry(),item, source);
	}
	
	public MSMLReader (Registry r, FilterItem item, FilterSource source)
	{
		super (item, source);
		this.registry=r;
		setFlavor (IOFlavor.NODE);
		initElemAllocation ();
		initEdgeTypeAllocation ();

	}


	private void initEdgeTypeAllocation ()
	{
		edgeTypes.put ("successor", new Integer(Graph.SUCCESSOR_EDGE));
		edgeTypes.put ("branch", new Integer(Graph.BRANCH_EDGE));
		edgeTypes.put ("refinement", new Integer(Graph.CONTAINMENT_EDGE));
		edgeTypes.put ("objectsplitter", new Integer(Graph.STD_EDGE_5));
		edgeTypes.put ("userdefined", new Integer(Graph.STD_EDGE_6));
	}

	private void initElemAllocation ()
	{
		for (Node i = item.getBranch (); i != null; i = i.getSuccessor ())
		{
			if (i instanceof Expression)
			{
				Expression oi = (Expression) i;
				elemAllocation.put (oi.getName (), oi.evaluate(this, new StringMap ()));
			}
		}
	}

	public Object getObject () throws IOException
	{
		Document doc = ((DOMSource) source).getDocument ();
		org.w3c.dom.Node root = doc.getLastChild ();
		Node result = null;
		if ((root.getNamespaceURI().equals(MSML_NAMESPACE) && (getAttributeContent (root,
			"version").equals ("1.0"))))
		{
			result = traverse (root, toURL());
		}
		else
		{
			throw new IOException ();
		}
		return result;
	}

	private Node traverse (org.w3c.dom.Node node, URL baseURL) throws IOException
	{
		Node n = null;
		if (node.getNamespaceURI ().equals (MSML_NAMESPACE))
		{
			String lname = node.getLocalName ();
			if (lname.equals ("node"))
			{
				MSMLMetadata metadatanode = new MSMLMetadata();
				for (org.w3c.dom.Node sub_node = node.getFirstChild ();sub_node != null; sub_node = sub_node
				.getNextSibling ()){
					if ((isDOMElement_Node(sub_node))&&
							(sub_node.getLocalName().equals ("data"))&&
							(sub_node.getNamespaceURI ().equals (MSML_NAMESPACE))){
						for (org.w3c.dom.Node sub_data = sub_node.getFirstChild (); sub_data != null; sub_data = sub_data
						.getNextSibling ()){	
							if (isDOMElement_Node(sub_data)){
								String key=sub_data.getNamespaceURI ()+sub_data.getLocalName();
								if (elemAllocation.containsKey(key)){
									//Precondition:Order in data is:(Shape|Transform),Appearance
									//search the item and subelements of namespace g: search in elemAllocation
									//and call the required export-file with node
									n=((MSMLDatatype)elemAllocation.get(key)).export(registry,sub_data,n,baseURL);
								}else{
									metadatanode.addMetadata(key+sub_data.getAttributes().getNamedItem("name"),sub_data);
								}
							}
						}
					}
				}
				if (n==null){
					n=new Node();
				}
				if (metadatanode.numberOfMetadataentries()>0){
					n.addEdgeBitsTo(metadatanode,Graph.STD_EDGE_5,null);
				}
				n.setName (getAttributeContent (node, "name"));
				idToNode.put (getAttributeContent (node, "id"), n);
			}			
			else if (lname.equals ("scale"))
			{
				n = new Node ();
				n.setName (getAttributeContent (node, "name"));
				//find all nodes and put them into idToNode
				//the IDs of all nodes, that belong to msobject are in msobjectNodeIDs
				Vector msobjectNodeIDs = new Vector ();
				for (org.w3c.dom.Node i = node.getFirstChild (); i != null; i = i
					.getNextSibling ())
				{
					if ((isDOMElement_Node(i))
							&& ((i.getLocalName ().equals ("msobject"))
								||(i.getLocalName ().equals ("node"))))
					{
						traverse (i,baseURL);
						msobjectNodeIDs.addElement (getAttributeContent (i, "id"));
					}
				}
				//Find all edges and assign them to the nodes, they belong to.
				//An edge connecting a node (that is not in msobjectNodeIDs)
				//with a node (that is in msobjectNodeIDs), is marked.
				//Thats important for the export, to be able to distinguish the different
				//objects, because they are all connected with successoredges.
				findEdges (node,msobjectNodeIDs);
				//find all roots of the object (nodes, thats are not connected
				//with other nodes are also roots)
				for (int i = 0; i < msobjectNodeIDs.size (); i++)
				{
					Node msobjectNode = (Node) (idToNode.get (msobjectNodeIDs
						.get (i)));
					if (msobjectNode.getSource () == null)
					{
						//add edges from the msobjectnode to all roots
						n.addEdgeBitsTo (msobjectNode, Graph.BRANCH_EDGE,
								null);
					}
				}
			}
			else if (lname.equals ("msobject"))
			{
				MSNode msn=new MSNode();
				Node transform=n;
				String showScale=getAttributeContent(node,"showScale");
				msn.setName (getAttributeContent (node, "name"));
				for (org.w3c.dom.Node sub_msnode = node.getFirstChild (); sub_msnode != null; sub_msnode = sub_msnode
					.getNextSibling ())
				{
					if (isDOMElement_Node(sub_msnode)){
						if (sub_msnode.getLocalName().equals ("data")){
							for (org.w3c.dom.Node sub_data = sub_msnode.getFirstChild (); sub_data != null; sub_data = sub_data
							.getNextSibling ()){	
								if (isDOMElement_Node(sub_data)){
									String key=sub_data.getNamespaceURI ()+sub_data.getLocalName();
									if (elemAllocation.containsKey(key)){
										//Precondition:Order in data is: Shape,(Appearance|Transform)
										transform=((MSMLDatatype)elemAllocation.get(key)).export(registry,sub_data,transform,baseURL);
									}
								}
							}
						}
						if (sub_msnode.getLocalName ().equals ("scale")){
							Node scale=traverse (sub_msnode,baseURL);
							msn.addScale (scale);
							if (showScale.equals(getAttributeContent(sub_msnode,"name"))){
								msn.setScale (scale);
							}
						}						
					}
				}
				if (transform==null){
					n=msn;
				}else{
					transform.appendBranchNode(msn);
					n=transform;
				}
				findEdges (node,null);
				idToNode.put (getAttributeContent (node, "id"), n);
			}
			else if (lname.equals ("group"))
			{
				for (org.w3c.dom.Node sub_group = node.getFirstChild (); sub_group != null; sub_group = sub_group
					.getNextSibling ())
				{
					if ((isDOMElement_Node(sub_group))&&
							(sub_group.getNamespaceURI ().equals (MSML_NAMESPACE))){
						if (sub_group.getLocalName().equals ("data")){
							for (org.w3c.dom.Node sub_data = sub_group.getFirstChild (); sub_data != null; sub_data = sub_data
							.getNextSibling ()){	
								if (isDOMElement_Node(sub_data)){
									String key=sub_data.getNamespaceURI ()+sub_data.getLocalName();
									if (elemAllocation.containsKey(key)){
										//Precondition: only g:Transform is permitted
										n=((MSMLDatatype)elemAllocation.get(key)).export(registry,sub_data,n,baseURL);
									}
								}
							}
						}
						if (sub_group.getLocalName ().equals ("msobject")){
							if (n==null){
								n=new Node();
							}
							n.addEdgeBitsTo (traverse (sub_group,baseURL), Graph.BRANCH_EDGE,
								null);
							n.setName (getAttributeContent (node, "name"));
						}
						if (sub_group.getLocalName ().equals ("group")){
							if (n==null){
								n=new Node();
							}
							n.addEdgeBitsTo (traverse (sub_group,baseURL), Graph.BRANCH_EDGE,
								null);
							n.setName (getAttributeContent (node, "name"));
						}
					}
				}	
			}
			else if (lname.equals ("msml"))
			{
				for (org.w3c.dom.Node sub_msml = node.getFirstChild (); sub_msml != null; sub_msml = sub_msml
					.getNextSibling ())
				{
					if (isDOMElement_Node(sub_msml)&&
							(sub_msml.getNamespaceURI ().equals (MSML_NAMESPACE))){
						if (sub_msml.getLocalName ().equals ("library"))
						{
							for (org.w3c.dom.Node sub_library = sub_msml.getFirstChild (); sub_library != null; sub_library = sub_library
							.getNextSibling ())
							{
								if (isDOMElement_Node(sub_library)&&sub_library.getLocalName().equals("data")){
									for (org.w3c.dom.Node sub_data = sub_library.getFirstChild (); sub_data != null; sub_data = sub_data
									.getNextSibling ())
									{
										if (isDOMElement_Node(sub_data)){
											String key=sub_data.getNamespaceURI ()+sub_data.getLocalName();
											if (elemAllocation.containsKey(key)){
												//only the local libraries (for instance in Appearance) are filled
												((MSMLDatatype)elemAllocation.get(key)).export(registry,sub_data,n,baseURL);
											}
										}
									}
								}
							}
						}
						if ((sub_msml.getLocalName ().equals ("msobject"))
									||(sub_msml.getLocalName ().equals ("group")))
						{
							n = traverse (sub_msml,baseURL);
						}
					}
				}
			}
		}
		return n;
	}
	
	private void findEdges (org.w3c.dom.Node node, Vector msobjectNodeIDs)
	{
		if (node.getNamespaceURI ().equals (MSML_NAMESPACE))
		{			
			for (org.w3c.dom.Node n = node.getFirstChild (); n != null; n = n
				.getNextSibling ())
			{
				if ((isDOMElement_Node(n))&&(n.getLocalName ().equals ("edge")))
				{
					String sourceID=getAttributeContent (n, "source");
					String targetID=getAttributeContent (n, "target");
					
					Node source = (Node) idToNode.get (sourceID);
					Node target = (Node) idToNode.get (targetID);
					int type = ((Integer) edgeTypes.get (getAttributeContent (n, "type")))
						.intValue ();
					if ((msobjectNodeIDs!=null)&&(
							(!msobjectNodeIDs.contains(sourceID)&&(msobjectNodeIDs.contains(targetID))))){
						type=type|((Integer) edgeTypes.get ("objectsplitter")).intValue();
					}
					try{
						int order=Integer.parseInt(getAttributeContent (n, "order"));
						type = (type & ~Graph.SPECIAL_EDGE_MASK) | order;
					}catch (NumberFormatException e){}
					source.addEdgeBitsTo (target, type, null);
				}
			}
		}
	}

	protected boolean isDOMElement_Node(org.w3c.dom.Node node){
		return (node.getNodeType () == org.w3c.dom.Node.ELEMENT_NODE);
	}
	
	private String getAttributeContent(org.w3c.dom.Node node, String attrname)
	{
		String result = "";
		org.w3c.dom.Node n = node.getAttributes ().getNamedItem (attrname);
		if (n != null)
		{
			result = n.getNodeValue ();
		}
		return result;
	}
}
