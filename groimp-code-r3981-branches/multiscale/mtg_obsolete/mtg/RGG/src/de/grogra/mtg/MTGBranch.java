package de.grogra.mtg;

import java.util.ArrayList;

public class MTGBranch {

	/**
	 * Indicates the supporting node for this branch.
	 * E.g. I1+I2<I3<I4, then I1 is the supporting node for branch I2<I3<I4.
	 */
	int supportNodeIndex;
	
	/**
	 * indicates which plant this branch belongs to.
	 */
	int plant;
	
	int scale;
	
	private ArrayList<MTGBranchElement> elements;
	
	public MTGBranch(int nodeIndex, int plant, int scale)
	{
		supportNodeIndex = nodeIndex;
		this.plant = plant;
		elements = new ArrayList<MTGBranchElement>();
		this.scale = scale;
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public int baseOfBranch()
	{
		if(elements.size()==0)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		
		return elements.get(0).getNodeIndex();
	}
	
	public int endOfBranch()
	{
		if(elements.size()==0)
			return MTGKeys.MTG_UNKNOWN_KEYCODE;
		
		return elements.get(elements.size()-1).getNodeIndex();
	}
	
	public void addElement(MTGBranchElement element)
	{
		elements.add(element);
	}
	
	public MTGBranchElement getElement(int elementIndex)
	{
		if(elements.size()-1<elementIndex)
			return null;
		else
			return elements.get(elementIndex);
	}
	
	public int getElementIndex(MTGBranchElement element)
	{
		for(int i=0; i<elements.size(); ++i)
		{
			if(elements.get(i).equals(element))
				return i;
		}
		
		return MTGKeys.MTG_UNKNOWN_KEYCODE;
	}
	
	public int getElementCount()
	{
		return elements.size();
	}
	
	public int getSupportNodeIndex()
	{
		return supportNodeIndex;
	}
	
	/**
	 * Get plant which this branch belongs to.
	 * @return Plant index (1-based) which this branch belongs to.
	 */
	public int getPlant()
	{
		return plant;
	}
	
	/**
	 * Compares if branch a is less than branch b.
	 * @param a 
	 * @param b
	 * @return true if a is less than b, else false.
	 */
	public static boolean lessThan(MTGBranch a, MTGBranch b)
	{
		boolean result = false;
		if (a.getPlant()==b.getPlant())
		{
			if (a.getSupportNodeIndex()<b.getSupportNodeIndex())
			{
				result=true;
			}
		}
		else
		{
			if (a.getPlant()<b.getPlant())
			{
				result=true;
			}
		}

		return result;
	}
	
	public static boolean greaterThan(MTGBranch a, MTGBranch b)
	{
		boolean result = false;
		if (a.getPlant()==b.getPlant())
		{
			if (a.getSupportNodeIndex()>b.getSupportNodeIndex())
				result=true;
		}
		else
		{
			if (a.getPlant()>b.getPlant())
				result=true;
		}

		return result;
	}
	
	/**
	 * Compares if branch a is equal to branch b
	 * @param a
	 * @param b
	 * @return true if a is equal to b, else false.
	 */
	public static boolean equal(MTGBranch a, MTGBranch b)
	{
		boolean result=false;


		if ((a.getPlant()==b.getPlant()) && (a.getSupportNodeIndex()==b.getSupportNodeIndex()))
		{
			result=true;
		}
		else
		{
			result=false;
		}


		return result;
	}
}
