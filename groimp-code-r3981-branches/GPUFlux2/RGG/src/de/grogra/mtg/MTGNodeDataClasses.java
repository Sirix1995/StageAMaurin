package de.grogra.mtg;

import java.io.Serializable;

public class MTGNodeDataClasses implements Serializable
{

	private static final long serialVersionUID = 7117297848211263377L;

	private String symbol;
	private int scale;
	private int decomposition;
	//private int indexation;//unused according to MTG File specifications at:
	//http://openalea.gforge.inria.fr/doc/vplants/newmtg/doc/_build/html/user/syntax.html#header
	private int definition;
	
	public MTGNodeDataClasses(String symbol, int scale, int decomposition, int definition)
	{
		this.symbol=symbol;
		this.scale=scale;
		this.decomposition=decomposition;
		this.definition=definition;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public int getDecomposition()
	{
		return decomposition;
	}
	
	public int getDefinition()
	{
		return definition;
	}
}
