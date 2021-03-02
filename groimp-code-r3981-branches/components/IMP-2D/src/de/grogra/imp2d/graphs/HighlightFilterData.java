package de.grogra.imp2d.graphs;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;

import de.grogra.graph.*;
import javax.vecmath.Color3f;
import java.awt.Shape;
import java.awt.Rectangle;

import de.grogra.imp2d.objects.*;


public class HighlightFilterData
{
	LinkedList fHides; //of FilterHide
	LinkedList fHighlights; //of FilterHighlight
	LinkedList fFoldings; //of FilterFolding
	
	//String fileName = "../IMP-2D/src/de/grogra/imp2d/graphs/HighlightFilter_Specification.xml";
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
		
	public HighlightFilterData(String xmlFile)
	{
		fHides = new LinkedList();
		fHighlights = new LinkedList();
		fFoldings = new LinkedList();
		factory = DocumentBuilderFactory.newInstance();
		try
        {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document document = builder.parse(xmlFile);
	        
	        NodeList filterHideList = document.getElementsByTagName("Filter_Hide");
	        NodeList filterHighlightList = document.getElementsByTagName("Filter_Highlight");
	        NodeList filterFoldingList = document.getElementsByTagName("Filter_Folding");
	        
	        //System.out.println("start hide");
	        //for all Filter_Hide-Filters
	        for (int i = 0; i < filterHideList.getLength(); i++)
	        {
	        		LinkedList hideCriteria = getCriteriaForFilter(filterHideList.item(i).getChildNodes());
	        		if (hideCriteria != null)
	        		{
	        			//System.out.println("speichern hide-criteria "+hideCriteria.get(0));
	        			boolean subnodes = getSubnodesForFilter(filterHideList.item(i).getChildNodes());
	        			FilterHide fh = new FilterHide();
	        			fh.setHideCriteria(hideCriteria);
	        			fh.setSubnodes(subnodes);
	        			fHides.add(fh);
	        		}
	        		
	        		
	        }
	        //System.out.println("start highlight");
	        //for all Filter_Highlight-Filters
	        for (int i = 0; i < filterHighlightList.getLength(); i++)
	        {
	        		LinkedList highlightCriteria = getCriteriaForFilter(filterHighlightList.item(i).getChildNodes());
	        		Hashtable highlightResults = getResultsForFilter(filterHighlightList.item(i).getChildNodes());
	        		boolean subnodes = getSubnodesForFilter(filterHighlightList.item(i).getChildNodes());
	        		FilterHighlight fhl = new FilterHighlight();
	        		fhl.setHighlightCriteria(highlightCriteria);
	        		fhl.setSubnodes(subnodes);
	        		fhl.setHighlightResults(highlightResults);
	        		fHighlights.add(fhl);
	        }
	        
	        //System.out.println("start folding");
	        //for all Filter_Folding-Filters
	        for (int i = 0; i < filterFoldingList.getLength(); i++)
	        {
	        		LinkedList foldingCriteria = getCriteriaForFilter(filterFoldingList.item(i).getChildNodes());
	        		Hashtable foldingNodeAttributes = getResultsForFilter(filterFoldingList.item(i).getChildNodes());
	        		boolean subnodes = getSubnodesForFilter(filterFoldingList.item(i).getChildNodes());
	        		FilterFolding ff = new FilterFolding();
	        		ff.setFoldingCriteria(foldingCriteria);
	        		ff.setSubnodes(subnodes);
	        		ff.setFoldingNodeAttributes(foldingNodeAttributes);
	        		fFoldings.add(ff);
	        		
	        		//FilterHighlight fhl = new FilterHighlight();
	        		//fhl.setHighlightCriteria(foldingCriteria);
	        		//fhl.setHighlightResults(foldingNodeAttributes);
	        		//fHighlights.add(fhl);
	        }
        } catch(Exception e)
        {
        		System.out.println("Error occured: "+e);
        }
	}
	public void addFilterHide(FilterHide fh)
	{
		this.fHides.add(fh);
	}
	
	public LinkedList getFilterHides()
	{
		return this.fHides;
	}
	
	public void addFilterHighlight(FilterHighlight fhl)
	{
		this.fHighlights.add(fhl);
	}
	
	public LinkedList getFilterHighlights()
	{
		return this.fHighlights;
	}
	
	public void addFilterFolding(FilterFolding ff)
	{
		this.fFoldings.add(ff);
	}
	
	public LinkedList getFilterFoldings()
	{
		return this.fFoldings;
	}
	private Object getStringToObject(Node nodeTemp)
	{
		
		if ((Attribute.forName(nodeTemp.getNodeName()).getType().toString().equals("java.lang.String")))
		{
			return nodeTemp.getFirstChild().getNodeValue();
		} else if ((Attribute.forName(nodeTemp.getNodeName()).getType().toString().equals("javax.vecmath.Color3f")))
		{
			String[] colorValueStrings = nodeTemp.getFirstChild().getNodeValue().split(",");
			 return new Color3f(Float.parseFloat(colorValueStrings[0]),Float.parseFloat(colorValueStrings[1]),Float.parseFloat(colorValueStrings[2]));
		} else if ((Attribute.forName(nodeTemp.getNodeName()).getType().toString().equals("int")))
		{	
			return new Integer(Integer.parseInt(nodeTemp.getFirstChild().getNodeValue()));
		} else if ((Attribute.forName(nodeTemp.getNodeName()).getType().toString().equals("java.awt.Shape")))
		{	
			if (nodeTemp.getFirstChild().getNodeValue().equals("Octagon"))
			{
				return new Octagon();
			} else if (nodeTemp.getFirstChild().getNodeValue().equals("Rhombus"))
			{
				return new Rhombus();
			} else if (nodeTemp.getFirstChild().getNodeValue().equals("Triangle"))
			{
				return new Triangle();
			} else if (nodeTemp.getFirstChild().getNodeValue().equals("Hexagon"))
			{
				return new Hexagon();
			} else if (nodeTemp.getFirstChild().getNodeValue().equals("Rectangle"))
			{
				return new Rectangle();
			}
		} else
		{
			System.out.println("Unknown Type: "+(Attribute.forName(nodeTemp.getNodeName()).getType().toString()));
		}
		
		return null;
	}
	
	private boolean getSubnodesForFilter(NodeList filterData)
	{
		for (int j = 0; j < filterData.getLength(); j++)
		{
			if (filterData.item(j).getNodeName().equals("Subnodes"))
			{
				return true;
			}
		}
		return false;
	}
	
	private LinkedList getCriteriaForFilter(NodeList filterData)
	{
		//System.out.println("start getCriteriaForFilter");
        	LinkedList allCriteria = new LinkedList();
        	for (int j = 0; j < filterData.getLength(); j++)
        	{
        		if (filterData.item(j).getNodeName().equals("Criterion"))
        		{
        			Hashtable criterion = new Hashtable();
        			NodeList criteria = filterData.item(j).getChildNodes();
        			for (int k = 0; k < criteria.getLength(); k++)
        			{
        				criterion.put(criteria.item(k).getNodeName(), new CompareValue(getStringToObject(criteria.item(k)), criteria.item(k).getAttributes().getNamedItem("compare").getNodeValue().charAt(0)));
        				//System.out.println("Criterion found "+criteria.item(k).getNodeName()+" -> "+criteria.item(k).getFirstChild());
        			}
        			if (criterion.size() > 0)
    	        		{
        				allCriteria.add(criterion);
    	        		}
        		} 
        	}
        	if (allCriteria.size() > 0)
        	{
        		return allCriteria;
        	} else
        	{
        		return null;
        	}
	}
	
	private Hashtable getResultsForFilter(NodeList filterData)
	{
		Hashtable allResults = new Hashtable();
		for (int j = 0; j < filterData.getLength(); j++)
		{
    			if (filterData.item(j).getNodeName().equals("Result") || filterData.item(j).getNodeName().equals("FoldedNode"))
    			{
    				NodeList results = filterData.item(j).getChildNodes();
    				for (int k = 0; k < results.getLength(); k++)
    				{
    					allResults.put(results.item(k).getNodeName(), getStringToObject(results.item(k)));
    				//	System.out.println("results found "+results.item(k).getNodeName()+" -> "+results.item(k).getFirstChild());
    				}
    			} 
    		}
		return allResults;
	}

/*	
	LinkedList getAllCriteriaNames()
	{
		LinkedList criteriaNames = new LinkedList();
		
		//All criterias of the hide filter
		for (int i = 0; i < hideCriteria.size(); i++)
		{
			Hashtable criteria = (Hashtable)hideCriteria.get(i);
			Enumeration e = criteria.keys();
			while (e.hasMoreElements())
			{
				String criterionAttribute = (String)e.nextElement();
				if (!criteriaNames.contains(criterionAttribute))
				{
					criteriaNames.add(criterionAttribute);
				}
			}
		}
		
		//All criterias of the highlight filter
		for (int i = 0; i < highlightCriteria.size(); i++)
		{
			Hashtable criteria = (Hashtable)highlightCriteria.get(i);
			Enumeration e = criteria.keys();
			while (e.hasMoreElements())
			{
				String criterionAttribute = (String)e.nextElement();
				if (!criteriaNames.contains(criterionAttribute))
				{
					criteriaNames.add(criterionAttribute);
				}
			}
		}//for i
		
		//All criterias of the folding filter
		for (int i = 0; i < foldingCriteria.size(); i++)
		{
			Hashtable criteria = (Hashtable)foldingCriteria.get(i);
			Enumeration e = criteria.keys();
			while (e.hasMoreElements())
			{
				String criterionAttribute = (String)e.nextElement();
				if (!criteriaNames.contains(criterionAttribute))
				{
					criteriaNames.add(criterionAttribute);
				}
			}
		}
		return criteriaNames;
	}
	*/
	LinkedList getAllChangingAttributesNames()
	{
		LinkedList allChangingAttributesNames = new LinkedList();
		LinkedList filterHighlights = this.getFilterHighlights();
		
		for (int i = 0; i < filterHighlights.size(); i++)
		{
			Hashtable highlightResults = ((FilterHighlight)filterHighlights.get(i)).getHighlightResults();
		
			Enumeration e = highlightResults.keys();
			while (e.hasMoreElements())
			{
				String criterionAttribute = (String)e.nextElement();
				if (!allChangingAttributesNames.contains(criterionAttribute))
				{
					allChangingAttributesNames.add(criterionAttribute);
				}
			}
		}	
		LinkedList filterFoldings = this.getFilterFoldings();
		
		for (int i = 0; i < filterFoldings.size(); i++)
		{
			Hashtable foldingNodeAttributes = ((FilterFolding)filterFoldings.get(i)).getFoldingNodeAttributes();
			
			Enumeration f = foldingNodeAttributes.keys();
			while (f.hasMoreElements())
			{
				String criterionAttribute = (String)f.nextElement();
				if (!allChangingAttributesNames.contains(criterionAttribute))
				{
					allChangingAttributesNames.add(criterionAttribute);
				}
			}
		}
		
		return allChangingAttributesNames;
	}
}
class FilterHide
{
	private LinkedList hideCriteria = new LinkedList();
	private boolean subnodes = false;
	
	public void setHideCriteria(LinkedList hideCriteria)
	{
		this.hideCriteria = hideCriteria;
	}
	
	public void setSubnodes(boolean subnodes)
	{
		this.subnodes = subnodes;
	}
	
	public LinkedList getHideCriteria()
	{
		return this.hideCriteria;
	}
	
	public boolean getSubnodes()
	{
		return this.subnodes;
	}
}
class FilterHighlight
{
	private LinkedList highlightCriteria = new LinkedList();
	private Hashtable highlightResults = new Hashtable();
	private boolean subnodes = false;
	
	public void setHighlightCriteria(LinkedList highlightCriteria)
	{
		this.highlightCriteria = highlightCriteria;
	}
	
	public void setSubnodes(boolean subnodes)
	{
		this.subnodes = subnodes;
	}
	
	public void setHighlightResults(Hashtable highlightResults)
	{
		this.highlightResults = highlightResults;
	}
	public LinkedList getHiglightCriteria()
	{
		return this.highlightCriteria;
	}
	
	public boolean getSubnodes()
	{
		return this.subnodes;
	}
	
	public Hashtable getHighlightResults()
	{
		return this.highlightResults;
	}
}
class FilterFolding
{
	private LinkedList foldingCriteria = new LinkedList();
	private Hashtable foldingNodeAttributes = new Hashtable();
	private boolean subnodes = false;
	
	public void setFoldingCriteria(LinkedList foldingCriteria)
	{
		this.foldingCriteria = foldingCriteria;
	}
	
	public void setFoldingNodeAttributes(Hashtable foldingNodeAttributes)
	{
		this.foldingNodeAttributes = foldingNodeAttributes;
	}
	
	public void setSubnodes(boolean subnodes)
	{
		this.subnodes = subnodes;
	}
	
	public LinkedList getFoldingCriteria()
	{
		return this.foldingCriteria;
	}
	
	public Hashtable getFoldingNodeAttributes()
	{
		return this.foldingNodeAttributes;
	}
	
	public boolean getSubnodes()
	{
		return this.subnodes;
	}
}
class CompareValue
{
	private Object value;
	private char compare;
	
	CompareValue (Object value, char compare)
	{
		this.value = value;
		this.compare = compare;
	}//Konstruktor CompareValue
	
	Object getValue()
	{
		return this.value;
	}//Object getValue
	
	char getCompare()
	{
		return this.compare;
	}//char getCompare
	
	void setValue(Object value)
	{
		this.value = value;
	}//void setValue
	
	void setCompare(char compare)
	{
		this.compare = compare;
	}//setCompare
}//class CompareValue
