package de.grogra.xl.query;

import java.util.HashMap;

import de.grogra.xl.util.ObjectList;

/**
 * This class contains the state information of a query pertaining to multiple scales.
 * 
 * This class is part of the extension of XL for multiscale modelling.
 * 
 * @since 18-04-2013
 * @author yongzhi ong
 *
 */
public interface QueryStateMultiScale {

	public void popRelation() throws QueryStateMultiScaleException;
	
	public void addRelation(Object src, Object tgt, int relationType) throws QueryStateMultiScaleException;
	
	public boolean queryContextMatch() throws QueryStateMultiScaleException;
	
	public void addIsMultiScaleMatcher(boolean isMultiScale);
	
	public boolean getIsMultiScaleMatcher();
	
	public boolean popIsMultiScaleMatcher();
	
	public void setIsMultiScaleMatcherLast(boolean isMultiScale);
	
	public int getIsMultiScaleMatcherSize();
	
	public int getIsMultiScaleMatcherTrueCount();
	
	public int getRelationCount();
	
	public void clear();
	
	public void removeDynamicConnections();
	
	public void updateFirstLastNodes();
	
	public ObjectList<Object> getFirstNodes();
	
	public ObjectList<Object> getLastNodes();
	
	public HashMap<Object, ObjectList<Object> > getTrailingIncomingRefinements();
	
	public HashMap<Object, ObjectList<Object> > getTrailingOutgoingRefinements();
	
	public void updateTrailingRefinements();
}
