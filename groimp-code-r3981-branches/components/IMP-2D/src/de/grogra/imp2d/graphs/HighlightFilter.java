
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

package de.grogra.imp2d.graphs;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Color3f;

import de.grogra.graph.AccessorMap;
import de.grogra.graph.Attribute;
import de.grogra.graph.AttributeOverwritingFilter;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;

public class HighlightFilter extends AttributeOverwritingFilter
{
	
	/**
	 * Contains the descriptor which created this graph filter. The
	 * descriptor contains some information which parametrizes the
	 * behaviour of this filter.
	 */
	private final HighlightFilterDescriptor descr;
	
	private HighlightFilterData hfd;
	private Hashtable accessorBridges;
	private boolean hasFoldedNode;
	private String foldedNodeName;
	
	public HighlightFilter (Graph source, HighlightFilterDescriptor descr)
	{
		super (source);
		this.descr = descr;
		
		hfd = new HighlightFilterData(descr.xmlFile);
		hasFoldedNode = false;
		foldedNodeName = "";
		initAttributeOverwritingFilter ();
	}
	
	
	@Override
	protected void initNodeAccessors (AccessorMap accessors)
	{
		accessorBridges = new Hashtable();
		
		LinkedList allChangingAttributesNames = hfd.getAllChangingAttributesNames();
		for (int i = 0; i < allChangingAttributesNames.size(); i++)
		{
			AccessorBridge abTemp = new AccessorBridge (Attribute.forName((String)allChangingAttributesNames.get(i)), true);
			accessors.add(abTemp);
			accessorBridges.put(allChangingAttributesNames.get(i), abTemp);
		}//for i
		
	}

	@Override
	protected int getInt (Object object, AccessorBridge accessor, GraphState gs)
	{
		//HighlightFilter
		Integer test = (Integer)getHighlightResult(object, accessor, null, gs);
		return test.intValue();
	}
	
	@Override
	public String getType() {
		return GraphManager.MAIN_GRAPH;
	}	
	
	@Override
	protected Object getObject (Object object, AccessorBridge accessor,	Object placeIn, GraphState gs)
	{
		//if (accessor.equals((AccessorBridge)accessorBridges.get("FILL_COLOR")))
		//{
			// overwrite fill color of nodes whose name is not null
		//	if (gs.getObjectDefault (object, true, Attributes.NAME, null) != null)
		//	{
		//		return descr.color;
		//	}
		//}
		
		//Highlight-Filter
		return getHighlightResult(object, accessor, placeIn, gs);
	}
	
	protected Object getHighlightResult(Object object, AccessorBridge accessor, Object placeIn, GraphState gs)
	{
		LinkedList filterHighlights = hfd.getFilterHighlights();
		LinkedList filterFoldings = hfd.getFilterFoldings();
		LinkedList filterHides = hfd.getFilterHides();
		
		if (accessorBridges.contains(accessor))
		{
			Enumeration e = accessorBridges.keys();
			
			while (e.hasMoreElements())
			{	
				String abKey = (String)e.nextElement();
				if (accessorBridges.get(abKey) == accessor)
				{
					//FilterHide - alle gefundenen Knoten schwarz
					for (int i = 0; i < filterHides.size(); i++)
					{
						FilterHide fh = (FilterHide)filterHides.get(i);
						boolean subnodes = fh.getSubnodes();
						if (checkCriteria(fh.getHideCriteria(), object, gs) && abKey.equals("de.grogra.imp.fillColor") && !subnodes)
						{	
							return new Color3f(0, 0, 0);
						}
					}
					
					//FilterHighlight
					for (int i = 0; i < filterHighlights.size(); i++)
					{
						FilterHighlight fhl = (FilterHighlight)filterHighlights.get(i);
						if (checkCriteria(fhl.getHiglightCriteria(), object, gs) && fhl.getHighlightResults().containsKey(abKey))
						{	
							
//							if (fhl.getSubnodes())
//							{
//								//all subnodes will get the same new attributes
//								for (Object edgeTemp = source.getFirstEdge(object); edgeTemp != null; edgeTemp = source.getNextEdge(edgeTemp, object))
//								{
//									Object neighborNode = (source.getSourceNode(edgeTemp) == object) ? source.getTargetNode(edgeTemp) : source.getSourceNode(edgeTemp);
//									Attribute[] neighborNodeAttr = source.getAttributes(neighborNode, true);
//									for (int j = 0; j < neighborNodeAttr.length; j++)
//									{
//										if ((neighborNodeAttr[j].getKey()).equals(abKey))
//										{
//											System.out.println(abKey+" gleich!");
//											System.out.println("alter Value: "+neighborNodeAttr[j].get(neighborNode, true, gs));
//											System.out.println("neuer Value: "+fhl.getHighlightResults().get(abKey));
//											neighborNodeAttr[j].set(neighborNode, true, "TEST", gs);
//										}
//									}
//								}
//							}
							//System.out.println("getHighlightResults: "+abKey+ " -> "+fhl.getHighlightResults().get(abKey));
							return fhl.getHighlightResults().get(abKey);
						}
					}
					
					//FilterFolding
					for (int i = 0; i < filterFoldings.size(); i++)
					{
						FilterFolding ff = (FilterFolding)filterFoldings.get(i);
//						if (checkCriteria(ff.getFoldingCriteria(), object, gs) && ff.getFoldingNodeAttributes().containsKey(abKey) && ff.getHideSubnodes())
//						{	
//							//System.out.println("getHighlightResults: "+abKey+ " -> "+fhl.getHighlightResults().get(abKey));
//							return ff.getFoldingNodeAttributes().get(abKey);
//						}
						if (checkCriteria(ff.getFoldingCriteria(), object, gs) && !ff.getSubnodes() && abKey.equals("de.grogra.imp.fillColor"))
						{
							return new Color3f(0,0,0);
						} 
//						else if (checkCriteria(ff.getFoldingCriteria(), source.getTargetNode(object), gs))
//						{
//							System.out.println("test edge");
//							return new Color3f(0,0,0);
//						}
					}
				}
			}
		}
		
		
		//System.out.println("accessor: "+accessor.getType());
		if (accessor.getType().toString().equals("int"))
		{
			return new Integer(super.getInt(object, accessor, gs));
		} else
		{
			return super.getObject (object, accessor, placeIn, gs);
		}
	}
	
	protected boolean checkCriteria (LinkedList criteria, Object object, GraphState gs)
	{
		//System.out.println("checkCriteria: "+object);
		Pattern starPattern = Pattern.compile("\\*");
		
		Attribute[] objectAttr = source.getAttributes(object, true);
		
		boolean matchesCriterium = false;
		
		for (int i = 0; i < criteria.size(); i++)
		{
			matchesCriterium = false;
			Hashtable criterion = (Hashtable)criteria.get(i);
			Enumeration e = criterion.keys();
			
			while (e.hasMoreElements())
			{	
				String criterionName = (String)e.nextElement();
				Object criterionValue = (String)((CompareValue)criterion.get(criterionName)).getValue();
				char compare = (char)((CompareValue)criterion.get(criterionName)).getCompare();
				//System.out.println("criterionName: "+criterionName+" criterionValue: "+criterionValue+" compareValue: "+compare);
				for (int j = 0; j < objectAttr.length; j++)
				{
					if (objectAttr[j].getType().toString().equals("java.lang.String"))
					//System.out.println("Attribute: "+objectAttr[j].getKey()+" wert: "+(String)objectAttr[j].get(object, true, getSourceState(gs)));
					if (criterionName.equals(objectAttr[j].getKey()))
					{
						//System.out.println("GEFUNDEN!!!");
						switch (compare)
						{
							case '=': case '!':
								if (objectAttr[j].getType().toString().equals("java.lang.String"))
								{
									Matcher m = starPattern.matcher((String)criterionValue);
									
									if (m.find() && objectAttr[j].getType().toString().equals("java.lang.String"))
									{
										Pattern pointPattern = Pattern.compile("\\.");
										Matcher n = pointPattern.matcher((String)criterionValue);
										String newCriterionValue = n.replaceAll("\\\\.");
										
										String[] test = starPattern.split(newCriterionValue);
										String regExp = "";
										if (((String)criterionValue).charAt(0) != '*')
										{
											regExp+= "^";
										} else
										{
											regExp+= "^.*?";
										}
										
										regExp+= test[0];
										for (int k = 1; k < test.length; k++)
										{
											if (test[k].length() > 0)
											{
												regExp+= ".*?"+test[k];
											}
										}
										
										if (((String)criterionValue).charAt(((String)criterionValue).length()-1) != '*')
										{
											regExp+= "$";
										} else
										{
											regExp+= ".*?$";
										}
										//System.out.println("regexp: "+regExp);
										
										Pattern regExpPattern = Pattern.compile(regExp);
										Matcher regExpMatcher = regExpPattern.matcher((String)objectAttr[j].get(object, true, getSourceState(gs)));
										boolean regExpFound = regExpMatcher.find();
										if (objectAttr[j].getKey().equals(criterionName) && ((compare == '=' && regExpFound == true)||(compare == '!' && regExpFound == false)))
										{
											//System.out.println("1: "+criterionName+", "+(String)objectAttr[j].get(object, true, getSourceState(gs)));
											matchesCriterium = true;
										} else
										{
											//System.out.println("NICHT: "+criterionName+", "+(String)objectAttr[j].get(object, true, getSourceState(gs)));
										}
										m.reset();
										n.reset();
										regExpMatcher.reset();
									} else
									{
										if ((objectAttr[j].getKey().equals(criterionName)) && ((objectAttr[j].get(object, true, getSourceState(gs)).equals(criterionValue) && compare == '=') || (!objectAttr[j].get(object, true, getSourceState(gs)).equals(criterionValue) && compare == '!')))
										{
											//System.out.println("2: "+criterionName+", "+criterionValue);
											matchesCriterium = true;
										}//if
									}
								} else
								{
									if (objectAttr[j].getKey().equals(criterionName) && ((objectAttr[j].get(object, true, getSourceState(gs)).equals(criterionValue) && compare == '=') || (!objectAttr[j].get(object, true, getSourceState(gs)).equals(criterionValue) && compare == '!')))
									{
										//System.out.println("4: "+criterionName+", "+criterionValue);
										matchesCriterium = true;
									}//if
								}
								break;
							case '<':
								if (objectAttr[j].getType().toString().equals("int") || objectAttr[j].getType().toString().equals("float") || objectAttr[j].getType().toString().equals("double"))
								{
									if (((Double)objectAttr[j].get(object, true, gs)).doubleValue() < ((Double)criterionValue).doubleValue())
									{
										matchesCriterium = true;
									}
								} else
								{
									//System.out.println("Error: operator '<' not applicable to datatype "+objectAttr[j].getType());
								}
								break;
							case '>':
								if (objectAttr[j].getType().toString().equals("int") || objectAttr[j].getType().toString().equals("float") || objectAttr[j].getType().toString().equals("double"))
								{
									if (((Double)objectAttr[j].get(object, true, gs)).doubleValue() > ((Double)criterionValue).doubleValue())
									{
										matchesCriterium = true;
									}
								} else
								{
									//System.out.println("Error: operator '<' not applicable to datatype "+objectAttr[j].getType());
								}
								break;
							default:
								//System.out.println("Error: compare operator unknown");
								break;
						}
					}
				}//for
			}//while
			
			if (matchesCriterium)
			{
				return true;
			}
		}//for i
		return false;
	}
	
	@Override
	public int getLifeCycleState (Object object, boolean asNode)
	{
		//System.out.println("hier lifecycle");
		GraphState gs = GraphState.current(this);
		
		//if ((descr.classToHide != null) && asNode
		//	&& (object.getClass ().getName ().indexOf (descr.classToHide) >= 0))
		//{
			// hide nodes whose class name contains classToHide
		//	return false;
		//} 
	
		//hiding nodes
		//System.out.println("CONTAINS: "+object);
		LinkedList filterHides = hfd.getFilterHides();
		for (int i = 0; i < filterHides.size(); i++)
		{
			FilterHide fh = (FilterHide)(filterHides.get(i));
			if (checkCriteria(fh.getHideCriteria(), object, gs) && fh.getSubnodes())
			{
			//	System.out.println("zu versteckender Knoten mit Substruktur gefunden!");
			//return TRANSIENT;
				return TRANSIENT;
			} else if (checkCriteria(fh.getHideCriteria(), object, gs) && !fh.getSubnodes())
			{
				//System.out.println("zu versteckender Knoten gefunden!");
				//return INVISIBLE;
				return TRANSIENT;
			}
		}
		
		//folding nodes
		LinkedList filterFoldings = hfd.getFilterFoldings();
		for (int i = 0; i < filterFoldings.size(); i++)
		{
			FilterFolding ff = (FilterFolding)filterFoldings.get(i);
			if (checkCriteria(ff.getFoldingCriteria(), object, gs) && ff.getSubnodes())
			{
				return TRANSIENT;
			} else if (checkCriteria(ff.getFoldingCriteria(), object, gs) && !ff.getSubnodes())
			{
				//return INVISIBLE;
			} 
			
//				if (hasFoldedNode == false)
//				{
//					System.out.println ("hasFoldedNode - false");
//					
//					
//					
//					hasFoldedNode = true;
//				}
//				LinkedList newTargetNodes = new LinkedList();
//				Object	newSourceNode = null;
//				for (Object e = source.getFirstEdge(object); e != null; e = source.getNextEdge(e, object))
//				{
//					Object neighborNode = source.getTargetNode(e);
//					if (e.equals(object))
//					{
//						//new source node
//						newSourceNode = object;
//					} else
//					{
//						//new target node
//						newTargetNodes.add(object);
//					}
//					for (int j = 0; j < newTargetNodes.size(); j++)
//					{
//						Node nodeTemp = ((Node)object);
//						 Edge edgeTemp = nodeTemp.getFirstEdge();
//					//	edgeTemp.source = nodeTemp;
//					//	nodeTemp.next = nodeTemp;
//					}
//					
//				}
//				return TRANSIENT;
//			}
		}			
		return super.getLifeCycleState (object, asNode);
	}

public void changeSubNodes(Object node, String abKey, Hashtable highlightResults)
{
	
}
}
