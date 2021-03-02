package de.grogra.mtg;


public class MTGGraphBuilderHeaderCode 
{
	protected static final int MTG_CODE_STAGE_CODE			=0; 
	protected static final int MTG_CODE_STAGE_FORM			=1;
	protected static final int MTG_CODE_STAGE_END			=2;

	int stage;
	private MTGNode rootNode;
	
	public MTGGraphBuilderHeaderCode(MTGNode rootNode)
	{
		stage=MTG_CODE_STAGE_CODE;
		this.rootNode = rootNode;
	}
	
	public int processTokensHeaderCode(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		for(int k=0; k<tokens.length; ++k)
		{
			String token = tokens[k];
			
			switch(stage)
			{
			case MTG_CODE_STAGE_CODE:
				if( (token.equals(MTGKeys.MTG_CODE_KEYWORD_CODE)) ||	//"CODE"
					(token.equals(MTGKeys.MTG_CODE_KEYWORD_CODE+":"))	//"CODE:"
						)
				stage=MTG_CODE_STAGE_FORM; //progress to next expectation in 'Code Section' of MTG file.
				break;
			case MTG_CODE_STAGE_FORM:
				if(token.equals(MTGKeys.MTG_CODE_KEYWORD_FORM_A))
				{
					((MTGRoot)rootNode).setObject(MTGKeys.MTG_CODE_KEYWORD_CODE,MTGKeys.MTG_CODE_KEYWORD_FORM_A);
					stage=MTG_CODE_STAGE_END; //progress to next expectation in 'Code Section' of MTG file.
					return stage;
				}
				if(token.equals(MTGKeys.MTG_CODE_KEYWORD_FORM_B))
				{
					((MTGRoot)rootNode).setObject(MTGKeys.MTG_CODE_KEYWORD_CODE,MTGKeys.MTG_CODE_KEYWORD_FORM_B);
					stage=MTG_CODE_STAGE_END; //progress to next expectation in 'Code Section' of MTG file.
					return stage;
				}
					
				break;
			}
		}
		
		return stage;
	}
}
