package de.grogra.mtg;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class MTGGraphBuilderHeaderClasses
{	
	protected static final int MTG_CLASSES_STAGE_CLASSES			=0; //Expecting keyword  "CLASSES" 
	protected static final int MTG_CLASSES_STAGE_COLUMN_NAMES		=1; //Expecting Keywords "SYMBOL  SCALE   DECOMPOSITION   INDEXATION  DEFINITION"
	protected static final int MTG_CLASSES_STAGE_INFO				=2; //Expecting symbol,scale,decomposition,indexation and definition information
	protected static final int MTG_CLASSES_STAGE_END				=3;

	int stage;
	int classCount;
	MTGNode rootNode;
	
	public MTGGraphBuilderHeaderClasses(MTGNode rootNode)
	{
		stage=MTG_CLASSES_STAGE_CLASSES;
		this.rootNode = rootNode;
		this.classCount=0;
	}
	
	private boolean isKeywordDecomposition(String keyword)
	{
		if((!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_CONNECTED)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_SUCC)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_BRAN)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_FREE)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_NONE))
		   )
			return false;
		
		return true;
	}
	
	private boolean isKeywordDefinition(String keyword)
	{
		if((!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DEFINITION_IMPLICIT)) &&
		   (!keyword.equals(MTGKeys.MTG_CLASSES_KEYWORD_DEFINITION_EXPLICIT)) 
		   )
			return false;
		
		return true;
	}
	
	private void processClasses(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
			
			if( (token.equals(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES)) ||	//equals "CLASSES"
				(token.equals(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES+":"))  //equals "CLASSES:"
						)
				stage=MTG_CLASSES_STAGE_COLUMN_NAMES; //progress to next expectation in 'Classes Section' of MTG file.
		}
	}
	
	private void processColumnHeaders(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
		
			if(token.equals("\t")) //Allow tabs between "CLASSES" AND "SYMBOL"
				continue;
			
			if(!token.equals(MTGKeys.MTG_CLASSES_KEYWORD_SYMBOL)) //Keyword "SYMBOL" not found after 0 or more tabs
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'SYMBOL' in MTG File Header-Classes section not found.");
			
			if(tokens.length < (k+9)) //9 tokens because "SYMBOL  SCALE   DECOMPOSITION   INDEXATION  DEFINITION" is 5 words + 4 tabs
				throw new MTGError.MTGGraphBuildException("Incorrect number of column headers in MTG File Header-Classes section not found.");
				
			//Keywords "SYMBOL" found. Verify rest of column headers
			if(!(tokens[k+1]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
			if(!(tokens[k+2]).equals(MTGKeys.MTG_CLASSES_KEYWORD_SCALE))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'SCALE' in MTG File Header-Classes section not found.");
			
			if(!(tokens[k+3]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
			if(!(tokens[k+4]).equals(MTGKeys.MTG_CLASSES_KEYWORD_DECOMPOSITION))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'DECOMPOSITION' in MTG File Header-Classes section not found.");
			
			if(!(tokens[k+5]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
			if(!(tokens[k+6]).equals(MTGKeys.MTG_CLASSES_KEYWORD_INDEXATION))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'INDEXATION' in MTG File Header-Classes section not found.");
			
			if(!(tokens[k+7]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
			if(!(tokens[k+8]).equals(MTGKeys.MTG_CLASSES_KEYWORD_DEFINITION))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'DEFINITION' in MTG File Header-Classes section not found.");

			stage=MTG_CLASSES_STAGE_INFO; //progress to next expectation in 'Classes Section' of MTG file.
			return;	
		}
	}
	
	private void processInfo(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		//Check if encountered keyword for next sub section ("DESCRIPTION"). If so, end the parsing for this 'classes' sub section.
		//If no classes were found before encountering keyword for next sub section ("DESCRIPTION"), throw exception. It is
		//mandatory for MTG Files to have 1 or more classes
		if(tokens.length>=1)
		{
			if( (tokens[0].equals(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION)) ||
				(tokens[0].equals(MTGKeys.MTG_TOPO_KEYWORD_DESCRIPTION + ":"))
				)
			{
				if(classCount<=0)
					throw new MTGError.MTGGraphBuildException("Missing class information in MTG File Header-Classes information Section.");
				stage=MTG_CLASSES_STAGE_END;
				return;
			}
		}
		
		//Check if there are 9 tokens including tabs
		//9 tokens because "SYMBOL  SCALE   DECOMPOSITION   INDEXATION  DEFINITION" is 5 words + 4 tabs
		if(tokens.length<9)
		{
			throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Classes information Section.");
		}
		
		//If there are more than 9 tokens, only allow tabs behind
		if(tokens.length>9)
		{
			for(int i=9;i<tokens.length;++i)
			{
				if(!tokens[i].equals("\t"))
					throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Classes information Section.");
			}
		}
		
		//Symbol token
		if(!tokens[0].matches("[a-zA-Z$]")) //This symbol most be an alphabetic character (either upper or lower-case letter)
			throw new MTGError.MTGGraphBuildException("Unacceptable Symbol name in Column 'Symbol' in MTG File Header-Classes information Section.");
		
		if(!(tokens[1]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
		
		if(!(Pattern.matches("[0-9]*", tokens[2])))
			throw new MTGError.MTGGraphBuildException("Unacceptable scale value in Column 'Scale' MTG File Header-Classes information Section.");
		
		if(!(tokens[3]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
		
		if(!isKeywordDecomposition(tokens[4]))
			throw new MTGError.MTGGraphBuildException("Unacceptable keyword in Column 'Decomposition' in MTG File Header-Classes information Section.");
		
		if(!(tokens[5]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
		
		//Column 'INDEXATION' is not used, according to MTG File syntax specification at:
		//http://openalea.gforge.inria.fr/doc/vplants/newmtg/doc/_build/html/user/syntax.html#header
		
		//if(!isKeywordDecomposition(tokens[6]))
		//	throw new MTGError.MTGGraphBuildException("Unacceptable keyword in MTG File Header-Classes information Section.");
		
		if(!(tokens[7]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
		
		if(!isKeywordDefinition(tokens[8]))
			throw new MTGError.MTGGraphBuildException("Unacceptable keyword in Column 'Definition' in MTG File Header-Classes information Section.");
		
		
		//Retrieve List of classes from root node
		Object mtgClassesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj==null)
			((MTGRoot)rootNode).setObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES, new ArrayList<MTGNodeDataClasses>());
		
		mtgClassesObj = ((MTGRoot)rootNode).getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
		if(mtgClassesObj instanceof ArrayList)
		{
			ArrayList<MTGNodeDataClasses> mtgClasses = (ArrayList<MTGNodeDataClasses>)((MTGRoot)rootNode).getObject(MTGKeys.MTG_CLASSES_KEYWORD_CLASSES);
			
			//Create new object containing MTG node class information
			MTGNodeDataClasses dataClass = new MTGNodeDataClasses(tokens[0],
															Integer.parseInt(tokens[2]),
				                                            MTGKeys.keywordToCodeDecomposition(tokens[4]),
				                                            MTGKeys.keywordToCodeDefinition(tokens[8])
				                                            );
			//Insert class in list inside rootNode
			mtgClasses.add(dataClass);
			
			classCount++;
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected data type in rootNode in MTG File Header-Classes section.");
		
	}
	
	public int processTokensHeaderClasses(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		if(stage==MTG_CLASSES_STAGE_CLASSES)
		{
			processClasses(tokens);
		}
		else if(stage==MTG_CLASSES_STAGE_COLUMN_NAMES)
		{
			processColumnHeaders(tokens);
		}
		else if(stage == MTG_CLASSES_STAGE_INFO)
		{
			processInfo(tokens);
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected end stage parsing in MTG-Header Classes section.");
		return stage;
	}
}
