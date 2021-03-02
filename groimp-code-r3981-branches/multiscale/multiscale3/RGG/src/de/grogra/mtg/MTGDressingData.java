package de.grogra.mtg;

import java.util.HashMap;

public class MTGDressingData {

	String _symbol_path;

	HashMap<String,Float> _minLengthList;
	HashMap<String,Float> _minTopDiaList;
	HashMap<String,Float> _minBotDiaList;
	float _lengthUnit;
	float _diametersUnit;
	//SymbLib* _symbLib; //TODO
	//ColorLib* _colorLib; //TODO
	int _defaultEdge;
	boolean _isAlphaRelative;
	float _defaultAlpha;
	float _defaultTeta;
	float _defaultPhi;
	float _defaultPsi;
	//FormsLib* _formsLib; //TODO
	int _defaultCategory;
	int _axeDefaultCategory;
	float _phillotaxy;
	float _nbTours;
	float _nbLeaves;
	boolean _isIndice;
	float _mediumTresholdGreen;
	float _minTresholdGreen;
	float _maxTresholdGreen;
	float _mediumTresholdBlue;
	float _minTresholdBlue;
	float _maxTresholdBlue;
	float _mediumTresholdRed;
	float _minTresholdRed;
	float _maxTresholdRed;
	float _elementLength;
	int _deltaIndex;
	float _defaultDistance;
	float _nbpLine;
	float _azimutUnit;
	float _alphaUnit;
	float _tetaUnit;
	float _phiUnit;
	float _psiUnit;
	int _verticille;


	float _defaultLeafBottomDia;
	float _defaultLeafTopDia;
	float _defaultLeafLength;
	float _defaultLeafAlpha;
	float _defaultLeafBeta;

	float _defaultFruitBottomDia;
	float _defaultFruitTopDia;
	float _defaultFruitLength;
	float _defaultFruitAlpha;
	float _defaultFruitBeta;

	float _defaultFlowerBottomDia;
	float _defaultFlowerTopDia;
	float _defaultFlowerLength;
	float _defaultFlowerAlpha;
	float _defaultFlowerBeta;

	char _fruitSymbol;
	char _flowerSymbol;
	char _leafSymbol;
	
	public MTGDressingData()
	{
		_symbol_path =MTGDressingDefaultValues.DEFAULT_SYMBOL_PATH;
		_minLengthList=new HashMap<String,Float>();
		_minTopDiaList=new HashMap<String,Float>();
		_minBotDiaList=new HashMap<String,Float>();
		//_symbLib=new SymbLib;
		//_colorLib=new ColorLib;
		//_formsLib=new FormsLib;
		_diametersUnit=MTGDressingDefaultValues.DEFAULT_DIAMETERS_UNIT;
		_lengthUnit=MTGDressingDefaultValues.DEFAULT_LENGTH_UNIT;
		_defaultEdge=MTGDressingDefaultValues.DEFAULT_EDGE;
		_defaultAlpha=MTGDressingDefaultValues.DEFAULT_ALPHA;
		_defaultTeta=MTGDressingDefaultValues.DEFAULT_TETA;
		_defaultPhi=MTGDressingDefaultValues.DEFAULT_PHI;
		_defaultPsi=MTGDressingDefaultValues.DEFAULT_PSI;
		_defaultCategory=MTGDressingDefaultValues.DEFAULT_CATEGORY;
		_axeDefaultCategory=MTGDressingDefaultValues.AXE_DEFAULT_CATEGORY;
		_isAlphaRelative=true;
		_isIndice=false;
		_phillotaxy=MTGDressingDefaultValues.DEFAULT_AZIMUT;
		_mediumTresholdGreen=MTGDressingDefaultValues.MEDIUM_TRESHOLD_GREEN;
		_mediumTresholdBlue=MTGDressingDefaultValues.MEDIUM_TRESHOLD_BLUE;
		_mediumTresholdRed=MTGDressingDefaultValues.MEDIUM_TRESHOLD_RED;
		_minTresholdGreen=MTGDressingDefaultValues.MIN_TRESHOLD_GREEN;
		_minTresholdBlue=MTGDressingDefaultValues.MIN_TRESHOLD_BLUE;
		_minTresholdRed=MTGDressingDefaultValues.MIN_TRESHOLD_RED;
		_maxTresholdGreen=MTGDressingDefaultValues.MAX_TRESHOLD_GREEN;
		_maxTresholdBlue=MTGDressingDefaultValues.MAX_TRESHOLD_BLUE;
		_maxTresholdRed=MTGDressingDefaultValues.MAX_TRESHOLD_RED;
		_deltaIndex=MTGDressingDefaultValues.DELTA_INDEX;
		_elementLength=MTGDressingDefaultValues.ELEMENT_LENGTH;
		_defaultDistance=MTGDressingDefaultValues.DEFAULT_DISTANCE;
		_azimutUnit=MTGDressingDefaultValues.DEFAULT_AZIMUTUNIT;
		_alphaUnit=MTGDressingDefaultValues.DEFAULT_ALPHAUNIT;
		_tetaUnit=MTGDressingDefaultValues.DEFAULT_TETAUNIT;
		_phiUnit=MTGDressingDefaultValues.DEFAULT_PHIUNIT;
		_psiUnit=MTGDressingDefaultValues.DEFAULT_PSIUNIT;
		_nbpLine=MTGDressingDefaultValues.DEFAULT_NBPLINE;
		_fruitSymbol=MTGDressingDefaultValues.DEFAULT_FRUIT_SYMBOL;
		_leafSymbol=MTGDressingDefaultValues.DEFAULT_FRUIT_SYMBOL;
		_flowerSymbol=MTGDressingDefaultValues.DEFAULT_FLOWER_SYMBOL;
		_verticille=MTGDressingDefaultValues.DEFAULT_VERTICILLE;

		_defaultLeafTopDia=MTGDressingDefaultValues.DEFAULT_LEAF_TOPDIA;
		_defaultLeafBottomDia=MTGDressingDefaultValues.DEFAULT_LEAF_BOTTOMDIA;
		_defaultLeafLength=MTGDressingDefaultValues.DEFAULT_LEAF_LENGTH;
		_defaultLeafAlpha=MTGDressingDefaultValues.DEFAULT_LEAF_ALPHA;
		_defaultLeafBeta=MTGDressingDefaultValues.DEFAULT_LEAF_BETA;

		_defaultFruitTopDia=MTGDressingDefaultValues.DEFAULT_FRUIT_TOPDIA;
		_defaultFruitBottomDia=MTGDressingDefaultValues.DEFAULT_FRUIT_BOTTOMDIA;
		_defaultFruitLength=MTGDressingDefaultValues.DEFAULT_FRUIT_LENGTH;
		_defaultFruitAlpha=MTGDressingDefaultValues.DEFAULT_FRUIT_ALPHA;
		_defaultFruitBeta=MTGDressingDefaultValues.DEFAULT_FRUIT_BETA;

		_defaultFlowerTopDia=MTGDressingDefaultValues.DEFAULT_FLOWER_TOPDIA;
		_defaultFlowerBottomDia=MTGDressingDefaultValues.DEFAULT_FLOWER_BOTTOMDIA;
		_defaultFlowerLength=MTGDressingDefaultValues.DEFAULT_FLOWER_LENGTH;
		_defaultFlowerAlpha=MTGDressingDefaultValues.DEFAULT_FLOWER_ALPHA;
		_defaultFlowerBeta=MTGDressingDefaultValues.DEFAULT_FLOWER_BETA;
	}
	
	public float getLengthUnit()  {return _lengthUnit;};
	public float getDiametersUnit()  {return _diametersUnit;};
	public int getDefaultEdge()  {return _defaultEdge;};
	public float getDefaultAlpha()  {return _defaultAlpha;};
	public float getDefaultTeta()  {return _defaultTeta;};
	public float getDefaultPhi()  {return _defaultPhi;};
	public float getDefaultPsi()  {return _defaultPsi;};
	public int getDefaultCategory()  {return _defaultCategory;};
	public int getAxeDefaultCategory()  {return _axeDefaultCategory;};
	public boolean isIndicePhillotaxy()  {return _isIndice;};
	public float getPhillotaxy()  {return _phillotaxy;};
	public float getNbTours()  {return _nbTours;};
	public float getNbLeaves()  {return _nbLeaves;};
	public boolean isAlphaRelative()  {return _isAlphaRelative;};
	
	public float getMediumTresholdGreen()  {return _mediumTresholdGreen;};
	public float getMaxTresholdGreen()  {return _maxTresholdGreen;};
	public float getMinTresholdGreen()  {return _minTresholdGreen;};

	public float getMediumTresholdBlue()  {return _mediumTresholdBlue;};
	public float getMaxTresholdBlue()  {return _maxTresholdBlue;};
	public float getMinTresholdBlue()  {return _minTresholdBlue;};

	public float getMediumTresholdRed()  {return _mediumTresholdRed;};
	public float getMaxTresholdRed()  {return _maxTresholdRed;};
	public float getMinTresholdRed()  {return _minTresholdRed;};

	public float getDeltaIndex()  {return _deltaIndex;};
	public float getElementLength()  {return _elementLength;};

	public float getDefaultDistance()  { return _defaultDistance;};
	public float getNbPlantsPerLine()  { return _nbpLine;};
	public float getAzimutUnit()  { return _azimutUnit;};
	public float getAlphaUnit()  { return _alphaUnit;};
	public float getTetaUnit()  { return _tetaUnit;};
	public float getPhiUnit()  { return _phiUnit;};
	public float getPsiUnit()  { return _psiUnit;};

	public int getDefaultVerticille()  { return _verticille; };

	public float getDefaultLeafLength()  { return _defaultLeafLength; };
	public float getDefaultLeafBottomDia()  { return _defaultLeafBottomDia; };
	public float getDefaultLeafTopDia()  { return _defaultLeafTopDia; };
	public float getDefaultLeafAlpha()  { return _defaultLeafAlpha; };
	public float getDefaultLeafBeta()  { return _defaultLeafBeta; };

	public float getDefaultFruitLength()  { return _defaultFruitLength; };
	public float getDefaultFruitBottomDia()  { return _defaultFruitBottomDia; };
	public float getDefaultFruitTopDia()  { return _defaultFruitTopDia; };
	public float getDefaultFruitAlpha()  { return _defaultFruitAlpha; };
	public float getDefaultFruitBeta()  { return _defaultFruitBeta; };

	public float getDefaultFlowerLength()  { return _defaultFlowerLength; };
	public float getDefaultFlowerBottomDia()  { return _defaultFlowerBottomDia; };
	public float getDefaultFlowerTopDia()  { return _defaultFlowerTopDia; };
	public float getDefaultFlowerAlpha()  { return _defaultFlowerAlpha; };
	public float getDefaultFlowerBeta()  { return _defaultFlowerBeta; };

	// Default symbols
	public char getLeafSymbol()  { return _leafSymbol; };
	public char getFruitSymbol()   { return _fruitSymbol; };
	public char getFlowerSymbol()  { return _flowerSymbol; };

	public String symbolPath()  {return _symbol_path;}
	
	public float getMinTopDia(String classSymbol)
	{
		Float topDia = _minTopDiaList.get(classSymbol);
		if(topDia!=null)
			return topDia.floatValue();
		else
			return MTGDressingDefaultValues.DEFAULT_MIN_TOPDIA;
	}
	
	public float getMinBotDia(String classSymbol)
	{
		Float botDia = _minBotDiaList.get(classSymbol);
		if(botDia!=null)
			return botDia.floatValue();
		else
			return MTGDressingDefaultValues.DEFAULT_MIN_BOTDIA;
	}
	
	public float getMinLength(String classSymbol)
	{
		Float length = _minLengthList.get(classSymbol);
		if(length!=null)
			return length.floatValue();
		else
			return MTGDressingDefaultValues.DEFAULT_MIN_LENGTH;
	}
	
	/**
	 * Returns Form for calculation of coordinates for branch of the given category.
	 * TODO: implementation
	 * @param category
	 */
	public void getBranchForm(int category)
	{
		
	}
}
