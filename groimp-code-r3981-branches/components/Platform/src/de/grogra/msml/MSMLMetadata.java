
package de.grogra.msml;

import de.grogra.graph.impl.Node;
import java.util.HashMap;
import java.util.Set;

public class MSMLMetadata extends Node
{
	private HashMap metadata;

	public MSMLMetadata(){
		super();
		metadata=new HashMap();
	}
	
	
	public org.w3c.dom.Node getMetadata (String name)
	{
		return (org.w3c.dom.Node)metadata.get (name);
	}

	public void addMetadata (String name, org.w3c.dom.Node value)
	{
		metadata.put (name, value);
	}
	
	public int numberOfMetadataentries(){
		return metadata.size();
	}
	
	public Set getAllKeys(){
		return metadata.keySet();
	}
}
