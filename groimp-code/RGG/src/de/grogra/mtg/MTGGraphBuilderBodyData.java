package de.grogra.mtg;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.mtg.MTGError.MTGGraphBuildException;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.reflect.Method;
import de.grogra.reflect.Type;

public class MTGGraphBuilderBodyData {

	protected static final int MTG_BODY_STAGE_MTG				= 0; 
	protected static final int MTG_BODY_STAGE_COLUMN_HEADERS	= 1;
	protected static final int MTG_BODY_STAGE_INFO				= 2;
	
	int stage;
	
	MTGNode rootNode;
	
	/**
	 * List of graph nodes created from loading MTG file.
	 */
	ArrayList<MTGNode> nodes;
	
	/**
	 * Last node (for each scale) traversed during processing of each line of entity information
	 */
	HashMap<Integer,MTGNode> lastNodeTraversedEachScale;
	
	/**
	 * History of tokens specified at each tab level. To be re-used when tabs or '^' characters are used
	 * in line of entity information
	 */
	ArrayList<String> lastTokensAtTabs;
	
	/**
	 * Traversed entity path during the parsing of each line. 
	 * "<.<", "+.+", "<<", "++" symbols are removed and replaced by succession of individual entity labels.
	 */
	ArrayList<String> traverseBuffer;
	
	/**
	 * Number of tabs from the beginning of the line currently being processed 
	 * in this class instance.
	 */
	int tabCount;
	
	/**
	 * Number of tabs between keyword "ENTITY-CODE" and first attribute/feature column header.
	 */
	int tabCountBetweenKeyword;
	
	/**
	 * Number of MTG sub-section lines processed
	 */
	int infoLineCount;
	
	/**
	 * List of nodes indices that represent nodes that should have feature/attribute values loaded.
	 */
	int[] nodesWithData;
	
	/**
	 * Loaded feature values;
	 */
	HashMap<String,Object> featureData;
	
	//Information on the current edge/entity/entity index loaded
	int currEdgeTypeIndex;
	String currEntityClassChar;
	int currEntityClassIndex;
	int currEntityIndex;
	int currEntityClassScale;
	
	//Information on the previous edge/entity/entity index loaded
	int prevEdgeTypeIndex;
	String prevEntityClassChar;
	int prevEntityClassIndex;
	int prevEntityIndex;
	int prevEntityClassScale;
	
	//last successor or branch edge used
	int lastSuccOrBran;
	
	public MTGGraphBuilderBodyData(MTGNode rootNode, ArrayList<MTGNode> nodes)
	{
		stage=MTG_BODY_STAGE_MTG;
		this.rootNode = rootNode;
		this.nodes = nodes;
		
		this.tabCount=0;
		infoLineCount=0;
		nodesWithData=null;
		tabCountBetweenKeyword=0;
		
		lastNodeTraversedEachScale = new HashMap<Integer,MTGNode>();
		lastNodeTraversedEachScale.put(new Integer(0), rootNode);
		lastTokensAtTabs = new ArrayList<String>();
		traverseBuffer = new ArrayList<String>();
		lastSuccOrBran=MTGKeys.MTG_UNKNOWN_KEYCODE;
	}
	
	/**
	 * Searches for 'MTG' keyword while processing current line.
	 */
	private void processMTG(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
			
			if( (token.equals(MTGKeys.MTG_DATA_KEYWORD_MTG)) ||		//equals "MTG"
				(token.equals(MTGKeys.MTG_DATA_KEYWORD_MTG+":"))  	//equals "MTG:"
						)
				stage=MTG_BODY_STAGE_COLUMN_HEADERS; //progress to next expectation in 'Body Section' of MTG file.
		}
	}
	
	/**
	 * Searches for 'ENTITY-CODE' keyword  and Attribute/Feature column names while processing current line.
	 */
	private void processColumnHeaders(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		//Get feature/attributes list from root node
		Object mtgFeaturesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		ArrayList<MTGNodeDataFeature> mtgFeatures = (mtgFeaturesObj==null)?null:(ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
	
		//Parse line for column headers
		int entityKeywordIndex=-1;
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];

			if(token.equals("\t")) //Allow tabs between "MTG" AND "ENTITY-CODE"
				continue;

			//Expecting keyword "ENTITY-CODE"
			if(!token.equals(MTGKeys.MTG_DATA_KEYWORD_ENTITY_CODE)) //Keyword "ENTITY-CODE" not found after 0 or more tabs
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'ENTITY-CODE' in MTG File Body section not found.");

			entityKeywordIndex=k;
			break;
		}
	
		//If keyword 'ENTITY-CODE' not found, this line consists only of tab characters, i.e. an empty line.
		if(entityKeywordIndex==-1)
			return;
		
		tabCountBetweenKeyword=0;
		
		//Check if minimum number of tokens for number of loaded features exists
		if(mtgFeatures!=null)
		{
			if((tokens.length-entityKeywordIndex-1)<((mtgFeatures.size()*2)-1))
				throw new MTGError.MTGGraphBuildException("Expected number of column headers in MTG File Body section not found.");
			
			//Expecting Name of first feature, after an arbitrary number of tabs
			boolean featureNamesVerified=false;
			
			for(int j=entityKeywordIndex+1; j<tokens.length;++j)
			{
				//Allow tabs between "ENTITY-CODE" and name of first feature/attribute
				//If no features/attributes, allow only tab characters behind keyword "ENTITY-CODE"
				if(tokens[j].equals("\t")) 
				{
					if(!featureNamesVerified)
						++tabCountBetweenKeyword; //counting number of tabs between "ENTITY-CODE" and first attribute
					continue;
				}
				
				//There should be no non-tab tokens if feature/attribute names are already verified.
				if(featureNamesVerified)
					throw new MTGError.MTGGraphBuildException("Unexpected token in MTG File - Body - Column headers section not found.");
				
				//Encounter non-tab token. Check if remaining series of tokens correspond to feature names loaded
				if(mtgFeatures!=null)
				{
					int featureCount=mtgFeatures.size();
					for(int i=0; i<featureCount; ++i)
					{
						if(!(tokens[j+(i*2)]).equals(mtgFeatures.get(i).getFeatureName()))
							throw new MTGError.MTGGraphBuildException("Expected feature name in MTG File - Body - Column headers section not found.");
						
						//check if separating tokens are tab characters (only for features that are not the last)
						if(i!=featureCount-1)
						{
							if(!(tokens[j+(i*2)+1]).equals("\t"))
								throw new MTGError.MTGGraphBuildException("Unacceptable separating token in MTG File - Body - Column headers section not found.");
						}
					}
					j=j+(featureCount*2)-1;
					featureNamesVerified=true;
				}
			}
		}
		
		stage=MTG_BODY_STAGE_INFO; //progress to next expectation in 'Body Section' of MTG file.
		return;	
	}
	
	private int countHeadingTabCharacters(String[] tokens)
	{
		int tabCount = 0;
		for(int i=0; i<tokens.length;++i)
		{
			if(tokens[i].equals("\t"))
				++tabCount;
			else
			{
				break;
			}
		}
		
		return tabCount;
	}
	
	private ArrayList<MTGNodeDataClasses> getClassesInfo() throws MTGError.MTGGraphBuildException
	{
		try
		{
			return ((MTGRoot)rootNode).getClassesInfo();
		}
		catch(MTGError.MTGPlantFrameException ex)
		{
			throw new MTGError.MTGGraphBuildException("Expected class information in MTG root node not found.");
		}
	}
	
	private MTGNode createNewNodeType(String symbol) throws MTGError.MTGGraphBuildException
	{
		//get compiled module types from root node
		
		Registry r = Registry.current();
		Item dir = r.getItem("/classes");
		for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor())
		{
			if (c.getName().contains(MTGKeys.getGeneratedModuleName(symbol))) {
				// read all parameters from this class
				Type<?> t = (Type<?>) ((TypeItem) c).getObject();
				try 
				{
					MTGNode n = (MTGNode)(t.newInstance());
					long id = r.getProjectGraph().prepareId(n);
					(r.getProjectGraph()).makePersistent(n, id, null);
					return n;

				} 
				catch (InstantiationException e) 
				{
					throw new MTGError.MTGGraphBuildException("Missing RGG module equivalent to MTG class.");
				} 
				catch (IllegalAccessException e) 
				{
					throw new MTGError.MTGGraphBuildException("Missing RGG module equivalent to MTG class.");
				}
				catch (InvocationTargetException e) {
					throw new MTGError.MTGGraphBuildException("Missing RGG module equivalent to MTG class.");
				}
				//break;
			}
		}
		
		throw new MTGError.MTGGraphBuildException("Missing RGG module equivalent to MTG class.");
	}
	
	/**
	 * Returns index of class symbol in loaded classes.
	 * For example 'P1', index of class 'P' is searched for in the loaded classes and returned.
	 */
	
	private int getEntityClassIndex(String entityString) throws MTGError.MTGGraphBuildException
	{
		currEntityClassChar = entityString.substring(0,1);
		int entityClassIndex = ((MTGRoot)rootNode).classSymbolToClassIndex(currEntityClassChar);
		return entityClassIndex;
	}
	
	/**
	 * Returns entity index.
	 * For example 'U2', '2' is returned as int.
	 */
	private int getEntityIndex(String entityString)
	{
		String entityIndexString = entityString.substring(1,entityString.length());
		int entityIndex = -1;
		try
		{
			entityIndex=Integer.parseInt(entityIndexString);
		}
		catch(NumberFormatException ex)
		{
			return -1;
		}
		return entityIndex;
	}
	
	/**
	 * Returns scale of the entity class specified by the class index.
	 * For example 'U2', if scale of class 'U' is 3, 3 is returned.
	 */
	private int getEntityClassScale(int entityClassIndex) throws MTGError.MTGGraphBuildException
	{
		ArrayList<MTGNodeDataClasses> mtgClasses = getClassesInfo();
		MTGNodeDataClasses newNodeClass = mtgClasses.get(entityClassIndex);
		return newNodeClass.getScale();
	}
	
	private Object castFeatureValueStringToDataType(String valueString, int dataType) throws MTGError.MTGGraphBuildException
	{
		try
		{
			//List of data types as specified in MTG file syntax
			/*
			{"INT", 			//int
			   "REAL",			//double
			   "STRING",		//String
			   "DD/MM", 		//String
			   "DD/MM/YY", 		//String
			   "MM/YY", 		//String
			   "DD/MM-TIME",	//String
			   "DD/MM/YY-TIME", //String
			   "GEOMETRY", 		//String
			   "APPEARANCE", 	//String
			   "OBJECT", 		//String
				};
			*/
			switch(dataType)
			{
			case 0: 
				return Integer.parseInt(valueString);
			case 1:
				return Double.parseDouble(valueString);
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
				return valueString;
			}
		}
		catch(Throwable t)
		{
			throw new MTGError.MTGGraphBuildException("Unable to cast feature value to specified feature data type.");
		}
		return null;
	}
	
	/**
	 * Add the value (a standard MTG feature attribute) to the node
	 * @param node
	 * @param value
	 * @param featureName
	 * @throws MTGGraphBuildException 
	 */
	private void setStandardFeatureValue(MTGNode node, Object value, String featureName, int featureType) throws MTGGraphBuildException
	{
		//numeric variables for casting
		double dValue = 0;
		int iValue = 0;
		
		if(featureType==0)
		{
			iValue = ((Integer)value).intValue();
			dValue = MTGKeys.integerToDouble(value);
		}
		if(featureType==1)
		{
			iValue = MTGKeys.doubleToInt(value);
			dValue = ((Double)value).doubleValue();
		}
		
		//numeric values (inter-casting)
		if((featureType==1)||(featureType==0))
		{
			if(featureName.equals(MTGKeys.TR_X					))
				{node.L1 = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_X));}
			else if(featureName.equals(MTGKeys.TR_Y					))
				{node.L2 = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_Y));}
			else if(featureName.equals(MTGKeys.TR_Z					))
				{node.L3 = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_Z));}
			else if(featureName.equals(MTGKeys.TR_DAB				))
				{node.DAB = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DAB));}
			else if(featureName.equals(MTGKeys.TR_DAC				))
				{node.DAC = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DAC));}
			else if(featureName.equals(MTGKeys.TR_DBC				))
				{node.DBC = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.TR_DBC));}
			else if(featureName.equals(MTGKeys.CA_X					))
				{node.XX = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.CA_X));}
			else if(featureName.equals(MTGKeys.CA_Y					))
				{node.YY = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.CA_Y));}
			else if(featureName.equals(MTGKeys.CA_Z					))
				{node.ZZ = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.CA_Z));}
			//NOTE: There exists 2 'length' variables in the MTGNode class. 
			//      The double uppercase-named variable is for MTG loaded data value.
			//      The float lowercase-named variable is for the turtle interpretation value for rendering.
			else if(featureName.equals(MTGKeys.ATT_LENGTH			)) 
				{node.Length = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_LENGTH));}
			else if(featureName.equals(MTGKeys.ATT_AZIMUT			))
				{node.Azimut = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_AZIMUT));}
			else if(featureName.equals(MTGKeys.ATT_ALPHA			))
				{node.Alpha = dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_ALPHA));}
			else if(featureName.equals(MTGKeys.ATT_TETA				))
				{node.AA= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_TETA));}
			else if(featureName.equals(MTGKeys.ATT_PHI				))
				{node.BB= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_PHI));}
			else if(featureName.equals(MTGKeys.ATT_PSI				))
				{node.CC= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_PSI));}
			else if(featureName.equals(MTGKeys.ATT_TOPDIA			))
				{node.TopDia= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_TOPDIA));}
			else if(featureName.equals(MTGKeys.ATT_BOTTOMDIA		))
				{node.BotDia= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_BOTTOMDIA));}
			else if(featureName.equals(MTGKeys.ATT_POSITION			))
				{node.Position= dValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_POSITION));}
			else if(featureName.equals(MTGKeys.ATT_CATEGORY			))
				{node.Category= iValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_CATEGORY));}
			else if(featureName.equals(MTGKeys.ATT_ORDER			))
				{node.Order = iValue;node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_ORDER));}
		}
		
		if(featureName.equals(MTGKeys.ATT_DIRECTION_PRI	))
		{
			node.setDirectoryPrimary((javax.vecmath.Vector3d) value);
			node.setStdAttFlagOn(MTGKeys.getStdAttFlagMask(MTGKeys.ATT_DIRECTION_PRI));
		}
		
	}
	
	/**
	 * Add the loaded feature values to list of nodes created.
	 * Number of nodes to add values to is usually 1, i.e. the last node specified in the entity code.
	 * However if '<.<' or '+.+' was used, feature values may be added to more than one node.
	 */
	private void addFeatureValuesToNodes() throws MTGError.MTGGraphBuildException
	{
		if((featureData==null)||(nodesWithData==null))
			return;
		if(featureData.size()==0)
			return;
		if(nodesWithData.length==0)
			return;
		
		//Get feature/attributes list from root node
		Object mtgFeaturesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		ArrayList<MTGNodeDataFeature> mtgFeatures = (mtgFeaturesObj==null)?null:(ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
		
		try
		{
		for(int i=0; i<nodesWithData.length; ++i)
			{
				//yong 29 aug - 
				//    This check is to resolve bug where smaller scale values are set to coarser scale node.
			    //    For the list of nodes that are supposed to have values added, the coarser scale node that
				//	  refines to them are added to the end of list 'nodesWithData'. Reason why the coarse scale
				//    node is added to the list is unknown.
				if(i==nodesWithData.length-1)
					continue;
			
				int nodeIndex = nodesWithData[i];
				if((nodeIndex>=nodes.size())||(nodeIndex<0))
					throw new MTGError.MTGGraphBuildException("Unable to load data into node with unexpected index.");
				MTGNode nodeToAddData = nodes.get(nodeIndex);
				if(nodeToAddData==null)
					throw new MTGError.MTGGraphBuildException("Unable to load data into node that does not exist.");
				
				// yong 29 aug - set flag to indicate that node contains data in the original mtg file
				if((mtgFeatures.size() >0) && (featureData.size() > 0))
				{
					nodeToAddData.dataFlag = 1;
				}
				
				for(int j=0; j<mtgFeatures.size(); ++j)
				{
					MTGNodeDataFeature feature = mtgFeatures.get(j);
					int featureType = feature.getFeatureTypeIndex();
					String featureName = feature.getFeatureName();
					Object featureValue = featureData.get(featureName);
					
					if(MTGKeys.isStandardAttribute(featureName))
					{
						if(featureValue!=null)
						{
							setStandardFeatureValue(nodeToAddData,featureValue,featureName,featureType);
						}
					}
					else
					{
						//get compiled modules and entity class for node
						Registry r = Registry.current();
						Item dir = r.getItem("/classes");
						String entityClass = nodeToAddData.mtgClass;
						
						//if(featureValue!=null) //it is possible for the feature value to be null if it is skipped over in the MTG file with a tab character.
						{
							
							boolean valueSet=false;
							boolean flagSet=false;
							
							//search for matching compiled module to the entity class of the node
							for (Item c = (Item) dir.getBranch(); c != null; c = (Item) c.getSuccessor())
							{
								if (c.getName().contains(MTGKeys.getGeneratedModuleName(entityClass))) 
								{
									// read all parameters from this class
									Type<?> t = (Type<?>) ((TypeItem) c).getObject();
											
									int methodCount = t.getDeclaredMethodCount();
									
									for(int m=0; m<methodCount; ++m)
									{
										//Method method = types[k].getDeclaredMethod(m);
										Method method = t.getDeclaredMethod(m);
										
										//set value of attribute if setter method found
										if((method.getName()).equals("set"+feature.getFeatureName()))
										{
											//create parameter array
											Object[] parameters = new Object[1];
											if(featureValue!=null)
												parameters[0] = featureValue;
											else
											{
												switch(featureType)
												{
												case 0: //int
													parameters[0] = 0;
													break;
												case 1: //double
													parameters[0] = 0.0;
													break;
												case 2:
												case 11://string
													parameters[0] = "";
													break;
												case 3://DD/MM
												case 4://DD/MM/YY
												case 5://MM/YY
												case 6://DD/MM-TIME
												case 7://DD/MM/YY-TIME
												case 8://GEOMETRY
												case 9://APPEARANCE
												case 10://OBJECT
													parameters[0] = "";
													break;
												default:
													throw new MTGError.MTGGraphBuildException("Unable to initialize value for unknown feature value type.");
												}
											}
											
											method.invoke(nodeToAddData, parameters);
											valueSet=true;
										}
										
										//set flag to indicate value is specified in MTG file if method to set flag found
										if((method.getName()).equals("setHas"+feature.getFeatureName()))
										{
											//create parameter array
											Object[] parameters = new Object[1];
											if(featureValue!=null)
												parameters[0] = true;
											else
												parameters[0] = false;
											method.invoke(nodeToAddData, parameters);
											flagSet=true;
										}
										
										if(valueSet && flagSet)
											break;
									}
								}
								if(valueSet && flagSet)
									break;
							}
						}
						//yong 10 apr 2012 - end
					}
				}
			}
		}
		catch(Throwable t)
		{
			throw new MTGError.MTGGraphBuildException("Unable to load feature value in nodes.");
		}
	}
	
	/**
	 * Processes/loads feature values
	 */
	private void processFeatureValues(String[] tokens, int beginIndex) throws MTGError.MTGGraphBuildException
	{
		//Clear featureData array
		if(featureData!=null)
			featureData.clear();
		else
			featureData=new HashMap<String,Object>();
		
		//Get feature/attributes list from root node
		Object mtgFeaturesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		ArrayList<MTGNodeDataFeature> mtgFeatures = (mtgFeaturesObj==null)?null:(ArrayList<MTGNodeDataFeature>)mtgFeaturesObj;
		int numOfFeatures = 0;
		if(mtgFeatures!=null)
			numOfFeatures = mtgFeatures.size();
		
		//If no features, ensure every token behind entity-code is \t
		if(numOfFeatures==0)
		{
			while(beginIndex<tokens.length)
			{
				if(!tokens[beginIndex].equals("\t"))
					throw new MTGError.MTGGraphBuildException("Unexpected feature value in MTG Body section.");
				++beginIndex;
			}
			return;
		}
		
		int currentFeatureIndex=0;
		int tabsBetweenKeywordsRemaining=tabCountBetweenKeyword-tabCount;
		
		//Features exist
		while(beginIndex<tokens.length)
		{
			//Go through appropriate number of tabs 
			if(tabsBetweenKeywordsRemaining>0)
			{
				if(tokens[beginIndex].equals("\t"))
				{
					tabsBetweenKeywordsRemaining--;
				}
				else
				{
					throw new MTGError.MTGGraphBuildException("Incorrect number of tabs between entity-code and feature value.");
				}
			}
			else
			{
				//Feature values are separated by tab characters.
				//Missing feature values are skipped over by a tab character as well.
				if(tokens[beginIndex].equals("\t"))
				{
					++currentFeatureIndex;;
				}
				else
				{
					//get data type for feature being loaded
					MTGNodeDataFeature feature= mtgFeatures.get(currentFeatureIndex);
					if(feature==null)
						throw new MTGError.MTGGraphBuildException("No matching feature for data value.");
					int featureDataType = feature.getFeatureTypeIndex();
					featureData.put(feature.getFeatureName(), castFeatureValueStringToDataType(tokens[beginIndex],featureDataType));
				}
			}
			
			++beginIndex;
		}
		
		//add loaded feature values to last node or list of nodes (when <.< or +.+ is used)
		addFeatureValuesToNodes();
	}
	
	/**
	 * Assemble absolute full entity name. i.e. without tabs and '^' symbols
	 */
	private String assembleEntityFullNameSub(String entityName, int tabs, boolean removeRepeatChar)  throws MTGError.MTGGraphBuildException
	{
		String lastTabString = new String("");
		
		if(lastTokensAtTabs.size()-1<tabs)
		{ 
			//throw new MTGError.MTGGraphBuildException("Unable to find previous bifurcation point while assembling entity name.");
			
			//try to search for previous tab bifurcation point that exists
			if(lastTokensAtTabs.size()>=1)
				lastTabString = lastTokensAtTabs.get(lastTokensAtTabs.size()-1);
		}
		else
			lastTabString = lastTokensAtTabs.get(tabs);
			
		if(removeRepeatChar) //removes '^' character at start of entityName string
			return lastTabString + entityName.substring(1,entityName.length());
		else
			return lastTabString + entityName;
	}
	
	/**
	 * Based on number of tabs in current line, and the presence of the '^' symbol, 
	 * determine the full traversal path using the stacked tokens stored in 'lastTokensAtTabs'.
	 */
	private String assembleEntityFullName(String entityName) throws MTGError.MTGGraphBuildException
	{
		if(tabCount==0)
			if(entityName.charAt(0)!='^')
				return entityName;	
	
		if(entityName.charAt(0)=='^')
			return assembleEntityFullNameSub(entityName,tabCount,true);
		else
			return assembleEntityFullNameSub(entityName,tabCount-1,false);
	}
	
	private int getNextEdgeSymbol(String[] entityNameTokens, int processIndex) throws MTGError.MTGGraphBuildException
	{
		// '<'
		if(entityNameTokens[processIndex].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_SUCC))
		{
			//'<<'
			if(processIndex<entityNameTokens.length-1)
				if(entityNameTokens[processIndex+1].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_SUCC))
					return MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY;
		
			//'<.<'
			if(processIndex<entityNameTokens.length-2)
			{
				if( (entityNameTokens[processIndex+1].equals(".")) &&
					(entityNameTokens[processIndex+2].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_SUCC)) )
					return MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT;
			}

			return MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC;
		}
		// '+'
		else if(entityNameTokens[processIndex].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_BRAN))
		{
			//'++'
			if(processIndex<entityNameTokens.length-1)
				if(entityNameTokens[processIndex+1].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_BRAN))
					return MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY;
		
			//'+.+'
			if(processIndex<entityNameTokens.length-2)
			{
				if( (entityNameTokens[processIndex+1].equals(".")) &&
					(entityNameTokens[processIndex+2].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_BRAN)) )
					return MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT;
			}

			return MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN;			
		}
		// '/'
		else if(entityNameTokens[processIndex].equals(MTGKeys.MTG_DATA_KEYWORD_EDGE_REFI))
			return MTGKeys.MTG_DATA_KEYCODE_EDGE_REFI;
		
		// no known edge token found
		throw new MTGError.MTGGraphBuildException("Expected edge connection symbol not found.");
	}
	
	private int getNextEntity(String[] entityNameTokens, int processIndex) throws MTGError.MTGGraphBuildException
	{
		String nextEntityLabel=new String();
		
		int processIncrement = 0;
		
		switch(currEdgeTypeIndex)
		{
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC:
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN:
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_REFI:
			nextEntityLabel = entityNameTokens[processIndex+1];
			processIncrement=2;
			break;
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY:
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY:
			nextEntityLabel = entityNameTokens[processIndex+2];
			processIncrement=3;
			break;
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT:
		case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT:
			nextEntityLabel = entityNameTokens[processIndex+3];
			processIncrement=4;
			break;
		default:
			throw new MTGError.MTGGraphBuildException("Unknown edge type.");
		}
		
		currEntityClassIndex = getEntityClassIndex(nextEntityLabel);
		currEntityIndex = getEntityIndex(nextEntityLabel);
		currEntityClassScale = getEntityClassScale(currEntityClassIndex);
		
		if((currEntityClassIndex==-1)||(currEntityIndex==-1))
			throw new MTGError.MTGGraphBuildException("Undeclared entity class used.");
		
		return processIncrement;
	}
	
	private MTGNode previousNodeTraversed()
	{
		return lastNodeTraversedEachScale.get(new Integer(prevEntityClassScale));
	}
	
	private void addLastNodeToNodesWithData()
	{
		nodesWithData = new int[2];
		nodesWithData[0] = nodes.size()-1;
		
		MTGNode prevScaleNode = lastNodeTraversedEachScale.get(new Integer(currEntityClassScale-1));
		nodesWithData[1] = prevScaleNode.mtgID;
	}
	
	private void addToNodeList(MTGNode node, String classStr, int classId, int id) throws MTGGraphBuildException
	{
		nodes.add(node);
		node.mtgClass=classStr;
		node.mtgClassID=classId;
		node.mtgID=id;
		//node.mtgID = nodes.size();
		
		ArrayList<MTGNodeDataClasses> nodeDataClasses = getClassesInfo();
		for(int i=0; i<nodeDataClasses.size(); ++i)
		{
			String entityClass = node.mtgClass;
			if( (nodeDataClasses.get(i).getSymbol()).equals(entityClass) )
				node.mtgScale= nodeDataClasses.get(i).getScale();
		}
	}
	
	private void traverseSuccBranMany(boolean isSucc,boolean isLastEntity, boolean dataToAll) throws MTGError.MTGGraphBuildException
	{
		int beginIndex = prevEntityIndex+1;
		int endIndex = currEntityIndex;
		
		//if beginIndex is the same as endIndex, then the '<<','++','<.<' or '+.+' symbol is no different than
		//the normal successor or branching symbol
		if(beginIndex==endIndex)
		{
			traverseSuccBran(isSucc,isLastEntity);
			return;
		}
		
		//check if previous class is same as current class
		if(prevEntityClassIndex!=currEntityClassIndex)
			throw new MTGError.MTGGraphBuildException("Invalid usage of <<, ++, <.< or +.+ symbol. Entity class does not match.");
		
		//check if previous index is smaller than current index
		if(beginIndex>currEntityIndex)
			throw new MTGError.MTGGraphBuildException("Invalid usage of <<, ++, <.< or +.+ symbol. Previous entity index same or larger than next entity index.");
		
		//in-between entity data
		int inbetEdgeTypeIndex = -1;
		if((currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY) || (currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY))
			inbetEdgeTypeIndex = MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN;
		else
			inbetEdgeTypeIndex = MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC;
		String inbetEntityClassChar = currEntityClassChar;
		int inbetEntityClassIndex = currEntityClassIndex;
		int inbetEntityIndex;
		int inbetEntityClassScale = currEntityClassScale;
		
		//nodes added flag - indicate if new nodes were added
		boolean newNodesAdded=false;
		
		//loop through all in-between indices
		for(inbetEntityIndex=beginIndex; inbetEntityIndex<=endIndex; inbetEntityIndex++)
		{			
			//get last node traversed
			MTGNode prevNode = previousNodeTraversed();
			if(prevNode==null)
				throw new MTGError.MTGGraphBuildException("Stack not updated with previous specified entity.");
			
			//try to find edge from previous node to in-between node
			MTGNode targetNode=null;
			if(isSucc)
				targetNode = prevNode.findAdjacentMTG(true, Graph.SUCCESSOR_EDGE, currEntityClassChar, inbetEntityIndex);
			else
				targetNode = prevNode.findAdjacentMTG(true, Graph.BRANCH_EDGE, currEntityClassChar, inbetEntityIndex);
			
			//if node not found connected to previous node, add node
			if(targetNode==null)
			{
				//create new node
				MTGNode newNode = createNewNodeType(inbetEntityClassChar);
				//nodes.add(newNode);
				addToNodeList(newNode,inbetEntityClassChar,inbetEntityIndex,nodes.size());
				
				//add successor or branching edge
				if(isSucc)
					prevNode.addEdgeBitsTo(newNode, Graph.SUCCESSOR_EDGE, null);
				else
					prevNode.addEdgeBitsTo(newNode, Graph.BRANCH_EDGE, null);
				
				//update last node traversed
				 lastNodeTraversedEachScale.put(new Integer(inbetEntityClassScale),newNode);
				
				//find last node of coarser scale traversed
				int coarserScale = inbetEntityClassScale-1;
				MTGNode lastCoarserNode = lastNodeTraversedEachScale.get(new Integer(coarserScale));
				
				//if found last node of coarser scale traversed, add refinement edge from coarser node to current node
				if(lastCoarserNode!=null)
				{
					lastCoarserNode.addEdgeBitsTo(newNode, Graph.REFINEMENT_EDGE, null);
				}
				
				//set flag
				newNodesAdded=true;
			}
			else
			{
				//update traversal stack
				lastNodeTraversedEachScale.put(new Integer(inbetEntityClassScale), targetNode);
			}
			
			//update previous node data as current in-between node
			prevEdgeTypeIndex = inbetEdgeTypeIndex;
			prevEntityClassChar = inbetEntityClassChar;
			prevEntityClassIndex = inbetEntityClassIndex;
			prevEntityIndex = inbetEntityIndex;
			prevEntityClassScale = inbetEntityClassScale;
		}
		
		//update last successor or branching edge used
		if(isSucc)
			lastSuccOrBran = MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC;
		else
			lastSuccOrBran = MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN;
		
		if(isLastEntity && newNodesAdded)
		{
			// '<.<' or '+.+' symbols
			if( (currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT) ||
					(currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT)
					)
			{
				nodesWithData = new int[currEntityIndex-prevEntityIndex+1+1]; //additional 1 for coarser scale node
				for(int j=0; j<nodesWithData.length; ++j) //init all to -1
					nodesWithData[j]=nodes.size()-1-j;
				
				//put in coarser scale node index
				MTGNode prevScaleNode = lastNodeTraversedEachScale.get(new Integer(currEntityClassScale-1));

				nodesWithData[nodesWithData.length-1] = prevScaleNode.mtgID;
			}
			// '<<' or '++' symbols
			else if( (currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY) ||
					(currEdgeTypeIndex==MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY)
					)
			{
				addLastNodeToNodesWithData();
			}
		}
		else if(isLastEntity && (!newNodesAdded))
			throw new MTGError.MTGGraphBuildException("Repeated specification of attributes.");
	}
	
	private void traverseSuccBran(boolean isSucc,boolean isLastEntity) throws MTGError.MTGGraphBuildException
	{
		//get previous node traversed
		MTGNode prevNode = previousNodeTraversed();
		if(prevNode==null)
			throw new MTGError.MTGGraphBuildException("Stack not updated with previous specified entity.");
		
		//attempt to find existing edge to current specified entity
		MTGNode targetNode=null;
		if(isSucc)
			targetNode = prevNode.findAdjacentMTG(true, Graph.SUCCESSOR_EDGE, currEntityClassChar, currEntityIndex);
		else
			targetNode = prevNode.findAdjacentMTG(true, Graph.BRANCH_EDGE, currEntityClassChar, currEntityIndex);
		
		//if node not found connected to previous node, add node
		if(targetNode==null)
		{
			//create new node
			MTGNode newNode = createNewNodeType(currEntityClassChar);
			//nodes.add(newNode);
			addToNodeList(newNode,currEntityClassChar,currEntityIndex,nodes.size());
			
			//add successor or branching edge
			if(isSucc)
				prevNode.addEdgeBitsTo(newNode, Graph.SUCCESSOR_EDGE, null);
			else
				prevNode.addEdgeBitsTo(newNode, Graph.BRANCH_EDGE, null);
			
			//find last node of coarser scale traversed
			int coarserScale = currEntityClassScale-1;
			MTGNode lastCoarserNode = lastNodeTraversedEachScale.get(new Integer(coarserScale));
			
			//if found last node of coarser scale traversed, add refinement edge from coarser node to current node
			if(lastCoarserNode!=null)
			{
				lastCoarserNode.addEdgeBitsTo(newNode, Graph.REFINEMENT_EDGE, null);
			}
			
			//if previous node traversed is not of the same scale as current node,
			//add successor or branch edge to previous node of same scale
			if(prevEntityClassScale!=currEntityClassScale)
			{
				MTGNode sameScalePrevNode = lastNodeTraversedEachScale.get(new Integer(currEntityClassScale));
				if(sameScalePrevNode!=null)
				{
					if(isSucc)
						sameScalePrevNode.addEdgeBitsTo(newNode, Graph.SUCCESSOR_EDGE, null);
					else
						sameScalePrevNode.addEdgeBitsTo(newNode, Graph.BRANCH_EDGE, null);
				}
			}
			
			//update traversal stack
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), newNode);
			
			//add node index to list of nodes that attribute values must be added to,
			//iff node added is last entity specified in row
			if(isLastEntity)
				addLastNodeToNodesWithData();
		}
		//node found connected to previous node
		else
		{
			//update traversal stack
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), targetNode);
		}
		
		//update last successor or branching edge used
		if(isSucc)
			lastSuccOrBran = MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC;
		else
			lastSuccOrBran = MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN;
	}
	
	private void traverseRefine(boolean isLastEntity) throws MTGError.MTGGraphBuildException
	{
		//get previous node traversed
		MTGNode prevNode = previousNodeTraversed();
		if(prevNode==null)
			throw new MTGError.MTGGraphBuildException("Stack not updated with previous specified entity.");
		
		//attempt to find existing refinement edge to current specified entity
		MTGNode targetNode = prevNode.findAdjacentMTG(true, Graph.REFINEMENT_EDGE, currEntityClassChar, currEntityIndex);
		
		//if node not found connected to previous node, add node
		if(targetNode==null)
		{
			//create new node
			MTGNode newNode = createNewNodeType(currEntityClassChar);
			//nodes.add(newNode);
			addToNodeList(newNode,currEntityClassChar,currEntityIndex,nodes.size());
			
			//add refinement edge
			prevNode.addEdgeBitsTo(newNode, Graph.REFINEMENT_EDGE, null);
			
			//if there was a successor or branching edge used in traversal,
			//attempt to find last node traversed of same scale. 
			//Extend the last edge type(succ or bran) to this node if it exists.
			if(lastSuccOrBran!=MTGKeys.MTG_UNKNOWN_KEYCODE)
			{
				MTGNode sameScalePrevNode = lastNodeTraversedEachScale.get(new Integer(currEntityClassScale));
				if(sameScalePrevNode!=null)
				{
					if(lastSuccOrBran==MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN)
						sameScalePrevNode.addEdgeBitsTo(newNode, Graph.BRANCH_EDGE, null);
					else if(lastSuccOrBran==MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC)
						sameScalePrevNode.addEdgeBitsTo(newNode, Graph.SUCCESSOR_EDGE, null);
				}
			}
			
			//update traversal stack
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), newNode);
			
			//add node index to list of nodes that attribute values must be added to,
			//iff node added is last entity specified in row
			if(isLastEntity)
			{
				addLastNodeToNodesWithData();
			}
		}
		else
		{
			//update traversal stack
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), targetNode);
		}
	}
	
	private void firstNode() throws MTGError.MTGGraphBuildException
	{		
		MTGNode targetNode = rootNode.findAdjacentMTG(true, Graph.REFINEMENT_EDGE, currEntityClassChar, currEntityIndex);
			
		//if first node not found connected to root node, add first node
		if(targetNode==null)
		{
			//add first node
			MTGNode newNode = createNewNodeType(currEntityClassChar);
			//nodes.add(newNode);
			addToNodeList(newNode,currEntityClassChar,currEntityIndex,0);

			rootNode.addEdgeBitsTo(newNode, Graph.REFINEMENT_EDGE, null);
			
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), newNode);
		}
		else
		{
			lastNodeTraversedEachScale.put(new Integer(currEntityClassScale), targetNode);
		}
	}
	
	private void currentEntityToPrevious()
	{
		prevEdgeTypeIndex = currEdgeTypeIndex;
		prevEntityClassChar = currEntityClassChar;
		prevEntityClassIndex = currEntityClassIndex;
		prevEntityIndex = currEntityIndex;
		prevEntityClassScale = currEntityClassScale;
	}
	
	/**
	 * Traverse the graph using the entityNameTokens as path.
	 */
	private void traverseGraph(String[] entityNameTokens, int processIndex) throws MTGError.MTGGraphBuildException
	{
		while(processIndex!=entityNameTokens.length)
		{
			//place current edge and entity to previous
			currentEntityToPrevious();
			
			//get next edge
			currEdgeTypeIndex = getNextEdgeSymbol(entityNameTokens,processIndex);
			
			//get next entity label
			int processIncrement = getNextEntity(entityNameTokens,processIndex);
			
			//handle first node case - previous node is root node
			if(processIndex==0)
			{
				if(currEdgeTypeIndex!=MTGKeys.MTG_DATA_KEYCODE_EDGE_REFI)
					throw new MTGError.MTGGraphBuildException("Entity Code must begin with refinement edge symbol.");
				
				firstNode();
			}
			else
			{
				boolean isLastEntity=false;
				if((processIndex+processIncrement)==entityNameTokens.length)
					isLastEntity=true;
				
				switch(currEdgeTypeIndex)
				{
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC:
					traverseSuccBran(true,isLastEntity);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN:
					traverseSuccBran(false,isLastEntity);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_REFI:
					traverseRefine(isLastEntity);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY:
					traverseSuccBranMany(true,isLastEntity,false);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY:
					traverseSuccBranMany(false,isLastEntity,false);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT:
					traverseSuccBranMany(true,isLastEntity,true);
					break;
				case MTGKeys.MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT:
					traverseSuccBranMany(false,isLastEntity,true);
					break;
				default:
					break;
				}
			}
			
			//update traverse buffer
			for(int i=0; i<processIncrement; i++)
				traverseBuffer.add(entityNameTokens[processIndex+i]);
			
			//continue to next edge and entity label
			processIndex += processIncrement;
		}
	}
	
	/**
	 * Parses the given entity name tokens.
	 * New entities are then added to the graph with edges connected. Updates parsing states.
	 */
	private void processEntityNameTokens(String[] entityNameTokens) throws MTGError.MTGGraphBuildException
	{
		int processIndex=0;
		
		//traverse graph. add nodes and edges as necessary
		traverseGraph(entityNameTokens, processIndex);
		
		//update stacked bifurcation points in member variable 'lastTokensAtTabs'
		String traversedPath = new String("");
		for(int j=0; j<traverseBuffer.size(); ++j)
			traversedPath = traversedPath + traverseBuffer.get(j);
		if(lastTokensAtTabs.size()-1 >= tabCount)
			lastTokensAtTabs.remove(tabCount);
		lastTokensAtTabs.add(tabCount, traversedPath);
	}
	
	/**
	 * Processes/loads data from  a string of entity names and attribute/feature values for the entities.
	 * For example, '	/P1/U1/I1<I2<I3<<I6		1.2		100.5	600.0	...'.
	 */
	private void processInfo(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		infoLineCount++;
		
		//count number of tabs from 1st token, until a non-tab token is reached.
		//record index where entity name is encountered.
		tabCount = countHeadingTabCharacters(tokens);
		
		//if line consists of only 'tab' tokens, end processing
		if(tabCount==tokens.length)
			return;
		
		//obtain entity name tokens
		String entityName = tokens[tabCount];
		
		//obtain full entity name
		String entityFullName = assembleEntityFullName(entityName);
		String[] entityFullNameTokens = MTGTokenizer.tokenizeEntityNames(entityFullName);
		
		//parse entity names - E.g. P1, U2 or E3, etc.
		nodesWithData=null; //At this stage of parsing, no idea how many new nodes need to have values loaded. Set to null.
		lastNodeTraversedEachScale.clear();							//clear buffer of last nodes of each scale traversed.
		lastNodeTraversedEachScale.put(new Integer(0), rootNode); 	//root node as default node traversed at scale 0.
		traverseBuffer.clear();										//clear buffer of traversal buffer.
		processEntityNameTokens(entityFullNameTokens);
		
		//process feature/attribute values
		if((tabCount+1)<tokens.length) //if more tokens behind after entity-code
			processFeatureValues(tokens,tabCount+1);
	}
	
	/**
	 * Processes/loads data from a line read from the MTG file from keyword "MTG:" onwards.
	 */
	public int processTokensBodyData(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		if(stage==MTG_BODY_STAGE_MTG) //expecting keyword "MTG"
		{
			processMTG(tokens);
		}
		else if(stage == MTG_BODY_STAGE_COLUMN_HEADERS)	//expecting "ENTITY-CODE" and feature/attribute column names
		{
			processColumnHeaders(tokens);
		}
		else if(stage == MTG_BODY_STAGE_INFO) //expecting entity codes and attribute/feature values
		{
			processInfo(tokens);
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected end stage parsing in MTG Body section.");
		
		return stage;
	}
}
