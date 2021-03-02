package de.grogra.mtg;

import java.util.ArrayList;

public class MTGGraphBuilderHeaderTopo 
{	
	protected static final int MTG_TOPO_STAGE_DESCRIPTION			=0; 
	protected static final int MTG_TOPO_STAGE_COLUMN_NAMES			=1;
	protected static final int MTG_TOPO_STAGE_INFO					=2;
	protected static final int MTG_TOPO_STAGE_END					=3;

	MTGNode rootNode;
	int stage;
	int infoCount;
	
	public MTGGraphBuilderHeaderTopo(MTGNode rootNode)
	{
		stage=MTG_TOPO_STAGE_DESCRIPTION;
		this.rootNode=rootNode;
		this.infoCount=0;
	}
	
	private void processDescription(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
			
			if( (token.equals(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION)) ||	//equals "DESCRIPTION"
				(token.equals(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION+":"))  //equals "DESCRIPTION:"
						)
				stage=MTG_TOPO_STAGE_COLUMN_NAMES; //progress to next expectation in 'Description Section' of MTG file.
		}
	}
	
	private void processColumnHeaders(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
		
			if(token.equals("\t")) //Allow tabs between "DESCRIPTION" AND "LEFT"
				continue;
			
			if(!token.equals(MTGKeys.MTG_TOPO_KEYWORD_LEFT)) //Keyword "Left" not found after 0 or more tabs
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'LEFT' in MTG File Header-Description section not found.");
			
			if(tokens.length < (k+7))
				throw new MTGError.MTGGraphBuildException("Incorrect number of column headers in MTG File Header-Description section not found.");
				
			//Keywords "LEFT" found. Verify rest of column headers
			if(!(tokens[k+1]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
			if(!(tokens[k+2]).equals(MTGKeys.MTG_TOPO_KEYWORD_RIGHT))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'RIGHT' in MTG File Header-Description section not found.");
			
			if(!(tokens[k+3]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
			if(!(tokens[k+4]).equals(MTGKeys.MTG_TOPO_KEYWORD_RELTYPE))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'RELTYPE' in MTG File Header-Description section not found.");
			
			if(!(tokens[k+5]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
			if(!(tokens[k+6]).equals(MTGKeys.MTG_TOPO_KEYWORD_MAX))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'MAX' in MTG File Header-Description section not found.");
			
			stage=MTG_TOPO_STAGE_INFO; //progress to next expectation in 'Description Section' of MTG file.
			return;	
		}
	}
	
	private void processInfo(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		//Check if encountered keyword for next sub section ("FEATURES"). If so, end the parsing for this 'description' (topo) sub section.
		//If no topo constraints are found before encountering keyword for next sub section ("FEATURES"), no exception. It is
		//optional for MTG Files to have topological constraints
		if(tokens.length>=1)
		{
			if( (tokens[0].equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES)) ||
				(tokens[0].equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES + ":"))
				)
			{
				stage=MTG_TOPO_STAGE_END;
				return;
			}
		}
		
		//Check if there are 7 tokens including tabs
		//7 tokens because "LEFT	RIGHT	RELTYPE	MAX" is 4 words + 3 tabs
		if(tokens.length<7)
		{
			throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Description information Section.");
		}
		
		//If there are more than 7 tokens, only allow tabs behind
		if(tokens.length>7)
		{
			for(int i=7;i<tokens.length;++i)
			{
				if(!tokens[i].equals("\t"))
					throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Description information Section.");
			}
		}
		
		//'LEFT' column
		int leftClassIndex = rootNode.classSymbolToClassIndex(tokens[0]);
		if(leftClassIndex==-1)//Class reference must exist in loaded classes
			throw new MTGError.MTGGraphBuildException("Reference Class in description/topo constraints not found.");
		
		if(!(tokens[1]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
		
		//'RIGHT' column
		String[] rightClassesTokens = MTGTokenizer.tokenizeTopoRightClasses(tokens[2]);
		if(rightClassesTokens==null)
			throw new MTGError.MTGGraphBuildException("Expected 'right' class symbols in MTG File Header-Description section not found.");
		//Classes must be loaded in rootNode
		int[] rightClassesIndices = rootNode.classSymbolsToClassIndices(rightClassesTokens);
		if(rightClassesIndices==null)
			throw new MTGError.MTGGraphBuildException("Reference Class in description/topo constraints not found.");
		
		if(!(tokens[3]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
		
		//'RELTYPE' column
		if(!((tokens[4].equals(MTGKeys.MTG_TOPO_KEYWORD_RELTYPE_SUCC)) || 
			 (tokens[4].equals(MTGKeys.MTG_TOPO_KEYWORD_RELTYPE_BRAN)))
			)
		{
			throw new MTGError.MTGGraphBuildException("Unexpected relationship type in MTG File Header-Description section.");
		}
		
		if(!(tokens[5]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Description section not found.");
		
		//'MAX' column
		int tempMax = -1;
		if(!((tokens[6].equals(MTGKeys.MTG_TOPO_KEYWORD_MAX_ONE)) ||
			 (tokens[6].equals(MTGKeys.MTG_TOPO_KEYWORD_MAX_MANY)))
			)
		{
			try
			{
				tempMax = Integer.parseInt(tokens[6]);
			}
			catch (NumberFormatException ex)
			{
				throw new MTGError.MTGGraphBuildException("Unexpected 'MAX' column value in MTG File Header-Description section.");
			}
		}
		else
		{
			tempMax = MTGKeys.keywordToCodeMax(tokens[6]);
		}
		
		//Retrieve List of classes from root node
		Object mtgDescriptionObj = rootNode.getObject(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION);
		if(mtgDescriptionObj==null)
			rootNode.setObject(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION, new ArrayList<MTGNodeDataDescription>());
		
		mtgDescriptionObj = rootNode.getObject(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION);
		if(mtgDescriptionObj instanceof ArrayList)
		{
			ArrayList<MTGNodeDataDescription> mtgDescriptions = (ArrayList<MTGNodeDataDescription>)rootNode.getObject(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION);
			
			//Create new object containing MTG node class information
			MTGNodeDataDescription dataDescription = new MTGNodeDataDescription(leftClassIndex,
																		rightClassesIndices,
																		MTGKeys.keywordToCodeRelType(tokens[4]),
																		tempMax
				                                            			);
			//Insert class in list inside rootNode
			mtgDescriptions.add(dataDescription);
			
			infoCount++;
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected data type in rootNode in MTG File Header-Classes section.");
		
	}
	
	public int processTokensHeaderTopo(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		if(stage==MTG_TOPO_STAGE_DESCRIPTION)
		{
			processDescription(tokens);
		}
		else if(stage == MTG_TOPO_STAGE_COLUMN_NAMES)
		{
			processColumnHeaders(tokens);
		}
		else if(stage == MTG_TOPO_STAGE_INFO)
		{
			processInfo(tokens);
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected end stage parsing in MTG-Header Topo constraint section.");
		
		return stage;
	}
}
