package de.grogra.mtg;

public class MTGKeys 
{

	//Sub Section - Header - Code
	//	Keywords
	public static final String MTG_CODE_KEYWORD_CODE 							= "CODE";
	public static final String MTG_CODE_KEYWORD_FORM_A 							= "FORM-A";
	public static final String MTG_CODE_KEYWORD_FORM_B 							= "FORM-B";
	
	//Sub Section - Header - Classes
	//	Keywords
	public static final String MTG_CLASSES_KEYWORD_CLASSES 						= "CLASSES";
	public static final String MTG_CLASSES_KEYWORD_SYMBOL 						= "SYMBOL";
	public static final String MTG_CLASSES_KEYWORD_SCALE 						= "SCALE";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION				= "DECOMPOSITION";
	public static final String MTG_CLASSES_KEYWORD_INDEXATION					= "INDEXATION";
	public static final String MTG_CLASSES_KEYWORD_DEFINITION					= "DEFINITION";
	
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_CONNECTED		= "CONNECTED";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR			= "LINEAR";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_SUCC	= "<-LINEAR";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_BRAN	= "+-LINEAR";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_FREE	= "FREE";
	public static final String MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_NONE	= "NONE";
	
	public static final String MTG_CLASSES_KEYWORD_DEFINITION_IMPLICIT			= "IMPLICIT";
	public static final String MTG_CLASSES_KEYWORD_DEFINITION_EXPLICIT			= "EXPLICIT";
	
	//	Keycodes
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_CONNECTED			= 0;
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR			= 1;
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_SUCC		= 2;
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_BRAN		= 3;
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_FREE		= 4;
	public static final int MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_NONE		= 5;
	
	public static final int MTG_CLASSES_KEYCODE_DEFINITION_IMPLICIT				= 0;
	public static final int MTG_CLASSES_KEYCODE_DEFINITION_EXPLICIT				= 1;
	
	//Sub Section - Header - Description (Topo constraints)
		//	Keywords
	public static final String MTG_TOPO_KEYWORD_DESCRIPTION						= "DESCRIPTION";
	
	public static final String MTG_TOPO_KEYWORD_LEFT							= "LEFT";
	public static final String MTG_TOPO_KEYWORD_RIGHT							= "RIGHT";
	public static final String MTG_TOPO_KEYWORD_RELTYPE							= "RELTYPE";
	public static final String MTG_TOPO_KEYWORD_MAX								= "MAX";
	
	public static final String MTG_TOPO_KEYWORD_RELTYPE_SUCC					= "<";
	public static final String MTG_TOPO_KEYWORD_RELTYPE_BRAN					= "+";

	public static final String MTG_TOPO_KEYWORD_MAX_ONE							= "1";
	public static final String MTG_TOPO_KEYWORD_MAX_MANY						= "?";
	
	public static final int MTG_TOPO_KEYCODE_RELTYPE_SUCC						= 0;
	public static final int MTG_TOPO_KEYCODE_RELTYPE_BRAN						= 1;
	
	public static final int MTG_TOPO_KEYCODE_MAX_ONE							= 1;
	public static final int MTG_TOPO_KEYCODE_MAX_MANY							= 0;
		
	//Sub Section - Header - Features (Attributes)
			//	Keywords
	public static final String MTG_ATTRIBUTE_KEYWORD_FEATURES					= "FEATURES";
	
	public static final String MTG_ATTRIBUTE_KEYWORD_NAME						= "NAME";
	public static final String MTG_ATTRIBUTE_KEYWORD_TYPE						= "TYPE";
	
	public static final String[] MTG_ATTRIBUTE_KEYWORD_FEATURE_NAMES			= 
		{"Alias",
		   "Date",
		   "NbEl",
		   "Length",
		   "BottomDiameter",
		   "TopDiameter",
		   "State"
			};

	public static final String[] MTG_ATTRIBUTE_KEYWORD_STATE_CHARS				= 
		{"D", //dead
		   "A", //alive
		   "B", //broken
		   "P", //pruned
		   "G", //growing
		   "V", //vegetative
		   "R", //resting
		   "C", //completed
		   "M"  //modified
			};
	
	public static final String[] MTG_ATTRIBUTE_KEYWORD_FEATURE_TYPES			= 
		{"INT", 			//int
		   "REAL",			//float
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
	
	//Sub Section - Body - MTG DATA
	//	Keywords
	public static final String MTG_DATA_KEYWORD_MTG								= "MTG";
	public static final String MTG_DATA_KEYWORD_ENTITY_CODE						= "ENTITY-CODE";
	
	public static final String MTG_DATA_KEYWORD_EDGE_SUCC						= "<";
	public static final String MTG_DATA_KEYWORD_EDGE_BRAN						= "+";
	public static final String MTG_DATA_KEYWORD_EDGE_REFI						= "/";
	
	public static final String MTG_DATA_KEYWORD_EDGE_SUCC_MANY					= "<<";
	public static final String MTG_DATA_KEYWORD_EDGE_BRAN_MANY					= "++";
	public static final String MTG_DATA_KEYWORD_EDGE_SUCC_MANY_ATT				= "<.<";
	public static final String MTG_DATA_KEYWORD_EDGE_BRAN_MANY_ATT				= "+.+";
	
	//	Keycodes
	public static final int MTG_DATA_KEYCODE_EDGE_SUCC							= 0;
	public static final int MTG_DATA_KEYCODE_EDGE_BRAN							= 1;
	public static final int MTG_DATA_KEYCODE_EDGE_REFI							= 2;
	
	public static final int MTG_DATA_KEYCODE_EDGE_SUCC_MANY						= 3;
	public static final int MTG_DATA_KEYCODE_EDGE_BRAN_MANY						= 4;
	public static final int MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT					= 5;
	public static final int MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT					= 6;
	
	//Unknown codes
	public static final int MTG_UNKNOWN_KEYCODE									=-1;
	
	//Root node index
	public static final int MTG_ROOT_NODE										=-2;
	
	//Any
	public static final int MTG_ANY												=-1;
	
	//MTGNode contents
	public static final String MTG_NODE_ENTITY_CLASS							= "Entity Class";
	public static final String MTG_NODE_ENTITY_INDEX							= "Entity Index";
	//MTGRoot node contents
	public static final String MTG_NODE_LIST_INDEX								= "List Index";
	public static final String MTG_NODE_NODELIST								= "Node List";
	public static final String MTG_NODE_BRANCHES								= "Branches";
	public static final String MTG_NODE_PLANT_COUNT								= "Plant Count";
	public static final String MTG_NODE_PLANT_ORIGIN							= "Plant Origin";
	public static final String MTG_NODE_PLANT_MIN								= "Plant Min";
	public static final String MTG_NODE_PLANT_MAX								= "Plant Max";
	public static final String MTG_NODE_DRESSING								= "Dressing";
	public static final String MTG_NODE_COORD_ORIGIN							= "Coord Origin";
	public static final String MTG_NODE_COORD_SQUARES							= "Coord Squares";
	
	
	/*
	 * MTG Feature names
	 */

	public static final String TR_X												="L1";
	public static final String TR_Y												="L2";
	public static final String TR_Z												="L3";

	//Distances between vertices
	public static final String TR_DAB											="DAB"; // Distance between points A and B.
	public static final String TR_DAC											="DAC"; // Distance between points A and C.
	public static final String TR_DBC											="DBC"; // Distance between points B and C.

	// Attributes containing the coordinates in a Cartesian system of reference.
	public static final String CA_X												="XX";
	public static final String CA_Y												="YY";
	public static final String CA_Z												="ZZ";

	//Attributes containing measures.
	public static final String ATT_LENGTH										="Length";
	public static final String ATT_AZIMUT										="Azimut";
	public static final String ATT_ALPHA										="Alpha";
	public static final String ATT_TETA											="AA";
	public static final String ATT_PHI											="BB";
	public static final String ATT_PSI											="CC";
	public static final String ATT_TOPDIA										="TopDia";
	public static final String ATT_BOTTOMDIA									="BotDia";
	public static final String ATT_POSITION										="Position";
	public static final String ATT_CATEGORY										="Category";
	public static final String ATT_DIRECTION_PRI								="DirectionPrimary";
	public static final String ATT_ORDER										="Order";
	
	// MTG Types
	public static final int MTG_TYPE_STANDARD									= 1; // Type=1 : Standard mtg (by default).
	public static final int MTG_TYPE_COORD_TRI_REF								= 2; // Type=2 : Mtg with coordinates (triangular reference system).
	public static final int MTG_TYPE_COORD_CARTESIAN							= 3; // Type=3 : Mtg with cartesian coordinates.
	
	public static int keywordToCodeEdgeType(String keyword) throws MTGError.MTGGraphBuildException
	{
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_SUCC))
			return MTG_DATA_KEYCODE_EDGE_SUCC;
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_BRAN))
			return MTG_DATA_KEYCODE_EDGE_BRAN;
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_REFI))
			return MTG_DATA_KEYCODE_EDGE_REFI;
		
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_SUCC_MANY))
			return MTG_DATA_KEYCODE_EDGE_SUCC_MANY;
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_BRAN_MANY))
			return MTG_DATA_KEYCODE_EDGE_BRAN_MANY;
		
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_SUCC_MANY_ATT))
			return MTG_DATA_KEYCODE_EDGE_SUCC_MANY_ATT;
		if(keyword.equals(MTG_DATA_KEYWORD_EDGE_BRAN_MANY_ATT))
			return MTG_DATA_KEYCODE_EDGE_BRAN_MANY_ATT;
		else 
			throw new MTGError.MTGGraphBuildException("No keycode found that represents Edge Type Keyword.");
	}
	
	public static int keywordToCodeStandardFeatureName(String keyword)
	{
		for(int i=0; i<MTG_ATTRIBUTE_KEYWORD_FEATURE_NAMES.length;++i)
		{
			if(keyword.equals(MTG_ATTRIBUTE_KEYWORD_FEATURE_NAMES[i]))
				return i;
		}
		
		return MTG_UNKNOWN_KEYCODE;
	}
	
	public static int keywordToCodeStandardFeatureTypes(String keyword)
	{
		for(int i=0; i<MTG_ATTRIBUTE_KEYWORD_FEATURE_TYPES.length;++i)
		{
			if(keyword.equals(MTG_ATTRIBUTE_KEYWORD_FEATURE_TYPES[i]))
				return i;
		}
		
		return MTG_UNKNOWN_KEYCODE;
	}
	
	public static boolean featureNameMatchesFeatureType(int nameIndex, int typeIndex)
	{	
		//Alias - String
		if((nameIndex==0)&&(typeIndex==2))
			return true;
		//Date - DD/MM, DD/MM/YY, MM/YY, DD/MM-TIME, DD/MM/YY-TIME
		if((nameIndex==1)&&((typeIndex>=3)&&(typeIndex<=7)))
			return true;
		//NbEl - INT
		//NOTE: Not specified in MTG file specs if Length should be INT
		if((nameIndex==2)&&(typeIndex==0))
			return true;
		//Length - INT, REAL
		//NOTE: Not specified in MTG file specs if Length should be INT or REAL
		if((nameIndex==3)&&((typeIndex==0)||(typeIndex==1)))
			return true;
		//BottomDiameter,TopDiameter - INT, REAL
		//NOTE: Not specified in MTG file specs if BottomDiameter,TopDiameter should be INT or REAL
		if(((nameIndex==4)||(nameIndex==5))&&((typeIndex==0)||(typeIndex==1)))
			return true;
		//State - STRING
		if((nameIndex==6)&&(typeIndex==2))
			return true;
		
		return false;
	}
	
	public static boolean stateFeatureCharactersAcceptable(String valueString)
	{
		for(int i=0; i<valueString.length(); ++i)
		{
			boolean foundStandardStateChar=false;
			String valueChar = valueString.substring(i, i+1);
			for(int j=0; j<MTG_ATTRIBUTE_KEYWORD_STATE_CHARS.length; ++j)
			{
				if(valueChar.equals(MTG_ATTRIBUTE_KEYWORD_STATE_CHARS[j]))
					foundStandardStateChar=true;
			}
			if(!foundStandardStateChar)
				return false;
		}
		return true;
	}
	
	public static int keywordToCodeDecomposition(String keyword) throws MTGError.MTGGraphBuildException
	{
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_CONNECTED))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_CONNECTED;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_SUCC))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_SUCC;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_BRAN))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_BRAN;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_FREE))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_FREE;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DECOMPOSITION_LINEAR_NONE))
			return MTG_CLASSES_KEYCODE_DECOMPOSITION_LINEAR_NONE;
		else 
			throw new MTGError.MTGGraphBuildException("No keycode found that represents Keyword in Classes Decomposition type.");
	}
	
	public static int keywordToCodeDefinition(String keyword) throws MTGError.MTGGraphBuildException
	{
		if(keyword.equals(MTG_CLASSES_KEYWORD_DEFINITION_IMPLICIT))
			return MTG_CLASSES_KEYCODE_DEFINITION_IMPLICIT;
		
		if(keyword.equals(MTG_CLASSES_KEYWORD_DEFINITION_EXPLICIT))
			return MTG_CLASSES_KEYCODE_DEFINITION_EXPLICIT;
		
		else 
			throw new MTGError.MTGGraphBuildException("No keycode found that represents Keyword in Classes Definition type.");
	}
	
	public static int keywordToCodeRelType(String keyword) throws MTGError.MTGGraphBuildException
	{
		if(keyword.equals(MTG_TOPO_KEYWORD_RELTYPE_SUCC))
			return MTG_TOPO_KEYCODE_RELTYPE_SUCC;
		
		if(keyword.equals(MTG_TOPO_KEYWORD_RELTYPE_BRAN))
			return MTG_TOPO_KEYCODE_RELTYPE_BRAN;
		
		else 
			throw new MTGError.MTGGraphBuildException("No keycode found that represents Keyword in Description RelType type.");
	}
	
	public static int keywordToCodeMax(String keyword) throws MTGError.MTGGraphBuildException
	{
		if(keyword.equals(MTG_TOPO_KEYWORD_MAX_ONE))
			return MTG_TOPO_KEYCODE_MAX_ONE;
		
		if(keyword.equals(MTG_TOPO_KEYWORD_MAX_MANY))
			return MTG_TOPO_KEYCODE_MAX_MANY;
		
		else 
			throw new MTGError.MTGGraphBuildException("No keycode found that represents Keyword in Description Max type.");
	}
}
