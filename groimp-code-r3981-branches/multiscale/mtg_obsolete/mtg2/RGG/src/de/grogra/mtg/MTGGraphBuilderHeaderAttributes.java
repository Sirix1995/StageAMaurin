package de.grogra.mtg;

import java.util.ArrayList;

public class MTGGraphBuilderHeaderAttributes {
	
	protected static final int MTG_FEATURES_STAGE_FEATURES			=0; 
	protected static final int MTG_FEATURES_STAGE_COLUMN_NAMES		=1;
	protected static final int MTG_FEATURES_STAGE_INFO				=2;
	protected static final int MTG_FEATURES_STAGE_END				=3;

	int stage;
	int infoCount;
	MTGNode rootNode;
	
	public MTGGraphBuilderHeaderAttributes(MTGNode rootNode)
	{
		this.rootNode=rootNode;
		stage=MTG_FEATURES_STAGE_FEATURES;
		this.infoCount=0;
	}
	
	private void processFeatures(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
			
			if( (token.equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES)) ||	//equals "FEATURES"
				(token.equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES+":"))  //equals "FEATURES:"
						)
				stage=MTG_FEATURES_STAGE_COLUMN_NAMES; //progress to next expectation in 'Features Section' of MTG file.
		}
	}
	
	private void processColumnHeaders(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
		
			if(token.equals("\t")) //Allow tabs between "FEATURES" AND "NAME"
				continue;
			
			if(!token.equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_NAME)) //Keyword "Left" not found after 0 or more tabs
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'NAME' in MTG File Header-Features section not found.");
			
			if(tokens.length < (k+3))
				throw new MTGError.MTGGraphBuildException("Incorrect number of column headers in MTG File Header-Features section not found.");
				
			//Keywords "NAME" found. Verify rest of column headers
			if(!(tokens[k+1]).equals("\t"))
				throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Features section not found.");
			if(!(tokens[k+2]).equals(MTGKeys.MTG_ATTRIBUTE_KEYWORD_TYPE))
				throw new MTGError.MTGGraphBuildException("Expected Identifier 'TYPE' in MTG File Header-Features section not found.");
			
			stage=MTG_FEATURES_STAGE_INFO; //progress to next expectation in 'Features Section' of MTG file.
			return;	
		}
	}
	
	private void processInfo(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		//Check if encountered keyword for next sub section ("MTG"). If so, end the parsing for this 'Features' (attributes) sub section.
		//If no features are found before encountering keyword for next sub section ("MTG"), no exception. It is
		//optional for MTG Files to have features/attributes
		if(tokens.length>=1)
		{
			if( (tokens[0].equals(MTGKeys.MTG_DATA_KEYWORD_MTG)) ||
				(tokens[0].equals(MTGKeys.MTG_DATA_KEYWORD_MTG + ":"))
				)
			{
				stage=MTG_FEATURES_STAGE_END;
				return;
			}
		}
		
		//Check if there are 3 tokens including tabs
		if(tokens.length<3)
		{
			throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Features information Section.");
		}
		
		//If there are more than 3 tokens, only allow tabs behind
		if(tokens.length>3)
		{
			for(int i=3;i<tokens.length;++i)
			{
				if(!tokens[i].equals("\t"))
					throw new MTGError.MTGGraphBuildException("Unexpected number of tokens in MTG File Header-Features information Section.");
			}
		}
		
		//'NAME' column verification
		if(!tokens[0].matches("[a-zA-Z_][a-zA-Z0-9_]*")) //Feature/Attribute name must begin with non-numeric or '_' character, followed by 0 or more alpha-numeric or '_' characters.
			throw new MTGError.MTGGraphBuildException("Unacceptable Feature name in Column 'Name' in MTG File Header-Features information Section.");
		//Obtain index of feature name. -1 if feature name is not standard name.
		int featureNameIndex = MTGKeys.keywordToCodeStandardFeatureName(tokens[0]);
			
		if(!(tokens[1]).equals("\t"))
			throw new MTGError.MTGGraphBuildException("Expected column tab in MTG File Header-Classes section not found.");
		
		//'TYPE' column verification
		int featureTypeIndex = MTGKeys.keywordToCodeStandardFeatureTypes(tokens[2]);
		if(featureTypeIndex==-1)
			throw new MTGError.MTGGraphBuildException("Unacceptable Feature type in Column 'Type' in MTG File Header-Features information Section.");
		
		//if feature name is standard name, verify if correct type according to file specifications is used
		if(featureNameIndex>=0)
		{
			//Check if standard feature names have acceptable feature types according to MTG specifications
			if(!MTGKeys.featureNameMatchesFeatureType(featureNameIndex, featureTypeIndex))
				throw new MTGError.MTGGraphBuildException("Standard Feature Name with unacceptable Feature Type in Column 'Type' in MTG File Header-Features information Section.");
		}
		
		
		//Retrieve List of features from root node
		Object mtgFeaturesObj = rootNode.getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		if(mtgFeaturesObj==null)
			rootNode.setObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES, new ArrayList<MTGNodeDataFeature>());
		mtgFeaturesObj = rootNode.getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
		if(mtgFeaturesObj instanceof ArrayList)
		{
			ArrayList<MTGNodeDataFeature> mtgFeatures = (ArrayList<MTGNodeDataFeature>)rootNode.getObject(MTGKeys.MTG_ATTRIBUTE_KEYWORD_FEATURES);
			
			//Create new object containing MTG node class information
			MTGNodeDataFeature dataFeature = new MTGNodeDataFeature(tokens[0],
															featureNameIndex,
															featureTypeIndex
				                                            );
			//Insert class in list inside rootNode
			mtgFeatures.add(dataFeature);
			
			infoCount++;
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected data type in rootNode in MTG File Header-Features section.");
		
	}
	
	public int processTokensHeaderAttributes(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		if(stage==MTG_FEATURES_STAGE_FEATURES)
		{
			processFeatures(tokens);
		}
		else if(stage == MTG_FEATURES_STAGE_COLUMN_NAMES)
		{
			processColumnHeaders(tokens);
		}
		else if(stage == MTG_FEATURES_STAGE_INFO)
		{
			processInfo(tokens);
		}
		else
			throw new MTGError.MTGGraphBuildException("Unexpected end stage parsing in MTG-Header Features/Attributes section.");
		
		return stage;
	}
}
