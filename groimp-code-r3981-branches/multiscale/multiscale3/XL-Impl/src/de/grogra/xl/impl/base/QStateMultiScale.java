package de.grogra.xl.impl.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import de.grogra.reflect.Reflection;
import de.grogra.xl.impl.base.Graph;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.QueryStateMultiScale;
import de.grogra.xl.query.QueryStateMultiScaleException;
import de.grogra.xl.util.BooleanList;
import de.grogra.xl.util.IntList;
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
public class QStateMultiScale implements QueryStateMultiScale{
	
	final Graph graph;									//reference to graph instance
	
	private BooleanList isMultiScaleMatcher;			//indicator flag if invoked Matchers requires query-context processing
	private int isMultiScaleMatcherTrueCount;			//number of flags in isMultiScaleMatcher that are true
	private int relationCount;							//number of relation pairs bound 
	
	public static final int RELATION_EQUAL	=	0;	
	public static final int RELATION_REFINE	=	1;
	public static final int RELATION_CROSS	=	2;
	
	ObjectList<Relation> relationsEqual;				//list of node pairs, each a pair of nodes of the same scale
	ObjectList<Relation> relationsRefine;				//list of node pairs, each with src node coarser than the tgt node
	ObjectList<Relation> relationsCross;				//list of node pairs, each with src node finer than tgt node
	
	ObjectList<Relation> prevRelations;					//stack containing list of Relation instances added by the previous Matchers
	IntList prevRelationsType;							//type of the Relation instances in the prevRelations stack
	
	//temporary variables used during query-context matching
	private int reSize;									//original size of relationsEqual list before query-context matching
	private int rrSize;									//original size of relationsRefine list before query-context matching
	private int rcSize;									//original size of relationsCross list before query-context matching
	private int crossCount;								//number of edge connections between 2 word groups. query-context match fails if 0.
	
	private HashMap<Object, Integer> typeOrder;			//temporary reference to order sorted type graph nodes and scale values
	
	ObjectList<Object> firstNodes;
	ObjectList<Object> lastNodes;
	HashMap<Object, ObjectList<Object> > trailingInNodes;
	HashMap<Object, ObjectList<Object> > trailingOutNodes;
	
	/**
	 * This class contains 2 related nodes that are bound in the query.
	 * Instances of this class are collected in QueryStateMultiScale for Query Context Matching.
	 * @author yongzhi ong
	 *
	 */
	private class Relation
	{
		public Object src;
		public Object tgt;
		
		public Relation(Object src, Object tgt)
		{
			this.src= src;
			this.tgt= tgt;
		}
	}
	
	public QStateMultiScale(Graph g)
	{
		this.graph = g;
		this.relationCount = 0;
		this.isMultiScaleMatcher= new BooleanList();
		this.isMultiScaleMatcherTrueCount=0;
		
		this.prevRelationsType = new IntList();
		this.prevRelations = new ObjectList<Relation>();
		
		this.relationsEqual = new ObjectList<Relation>();
		this.relationsRefine = new ObjectList<Relation>();
		this.relationsCross = new ObjectList<Relation>();
		
		//this.relationsRefineDynamic = new ObjectList<Relation>();
		//this.relationsEqualDynamic = new ObjectList<Relation>();
	}
	
	public int getRelationCount()
	{
		return relationCount;
	}
	
	public void addRelation(Object src, Object tgt, int relation) throws QueryStateMultiScaleException
	{
		//check for valid node instances and relation type
		if((src == null)||
			(tgt == null) ||
			(relation < RELATION_EQUAL) ||
			(relation > RELATION_CROSS)
			)
			throw new QueryStateMultiScaleException();
		
		//create new instance of Relation
		Relation newRelation = new Relation(src,tgt);
		
		//add new Relation instance to correct list
		switch(relation)
		{
		case RELATION_EQUAL:
			relationsEqual.add(newRelation);
			break;
		case RELATION_REFINE:
			relationsRefine.add(newRelation);
			break;
		case RELATION_CROSS:
			relationsCross.add(newRelation);
			break;
		default:
			throw new QueryStateMultiScaleException();
		}
		
		relationCount++; //increment counter
		
		//set the last added Relation and type
		prevRelations.add(newRelation);
		prevRelationsType.add(relation);
	}
	
	public void popRelation() throws QueryStateMultiScaleException
	{
		int prevRelationsSize = prevRelations.size();
		int prevRelationsTypeSize = prevRelationsType.size();
		
		if((relationCount < 1) ||
			(prevRelationsSize == 0) ||
			(prevRelationsTypeSize == 0)
			)
			throw new QueryStateMultiScaleException();
		
		//pop from prevRelationsType stack
		int prevType = prevRelationsType.get(prevRelationsTypeSize-1);
		prevRelationsType.removeAt(prevRelationsTypeSize-1);
		
		//pop from prevRelations stack
		Relation prevRelation = prevRelations.get(prevRelationsSize-1);
		prevRelations.remove(prevRelationsSize-1);
		
		//pop from respective categorized stack based on the type
		switch(prevType)
		{
		case RELATION_EQUAL:
			{
				Relation lastRelation = relationsEqual.get(relationsEqual.size()-1);
				if(lastRelation == prevRelation)
					relationsEqual.remove(relationsEqual.size()-1);
				else
					throw new QueryStateMultiScaleException();
				break;
			}
		case RELATION_REFINE:
			{
				Relation lastRelation = relationsRefine.get(relationsRefine.size()-1);
				if(lastRelation == prevRelation)
					relationsRefine.remove(relationsRefine.size()-1);
				else
					throw new QueryStateMultiScaleException();
				break;
			}
		case RELATION_CROSS:
			{
				Relation lastRelation = relationsCross.get(relationsCross.size()-1);
				if(lastRelation == prevRelation)
					relationsCross.remove(relationsCross.size()-1);
				else
					throw new QueryStateMultiScaleException();
				break;
			}
		default:
			throw new QueryStateMultiScaleException();
		}
		
		relationCount--; //decrement counter
	}
	
	public void clear()
	{
		relationCount = 0;
		isMultiScaleMatcher.clear();
		isMultiScaleMatcherTrueCount=0;
		
		relationsEqual.clear();				
		relationsRefine.clear();
		relationsCross.clear();
		
		prevRelations.clear();
		prevRelationsType.clear();
		
		//relationsRefineDynamic.clear();
		//relationsEqualDynamic.clear();
	}
	
	public void addIsMultiScaleMatcher(boolean isMultiScale)
	{
		isMultiScaleMatcher.add(isMultiScale);
		if(isMultiScale==true)
			isMultiScaleMatcherTrueCount++;
	}
	
	public boolean getIsMultiScaleMatcher()
	{
		return isMultiScaleMatcher.get(isMultiScaleMatcher.size()-1);
	}
	
	public boolean popIsMultiScaleMatcher()
	{
		boolean removedFlag = isMultiScaleMatcher.removeAt(isMultiScaleMatcher.size()-1);
		if(removedFlag==true)
			isMultiScaleMatcherTrueCount--;
		return removedFlag;
	}
	
	public void setIsMultiScaleMatcherLast(boolean isMultiScale)
	{
		boolean prevLastFlag = getIsMultiScaleMatcher();
		isMultiScaleMatcher.set(isMultiScaleMatcher.size()-1, isMultiScale);
		if((!prevLastFlag)&&(isMultiScale))
			isMultiScaleMatcherTrueCount++;
		else if((prevLastFlag)&&(!isMultiScale))
			isMultiScaleMatcherTrueCount--;
	}
	
	public int getIsMultiScaleMatcherSize()
	{
		return isMultiScaleMatcher.size();
	}
	
	public int getIsMultiScaleMatcherTrueCount()
	{
		return isMultiScaleMatcherTrueCount;
	}
	
	/**
	 * Performs query-wide context condition validation. Adds additional edge connections to be deleted.
	 * @return true if all query-wide context conditions are met, false otherwise
	 * @throws QueryStateMultiScaleException 
	 */
	public boolean queryContextMatch() throws QueryStateMultiScaleException
	{
		//get type graph root
		Object typeRoot = this.graph.getTypeRoot();
		
		//if no type graph, conditions are all met
		if(typeRoot == null)
			return true;
		
		//if no relations, there are no conditions, i.e. conditions are all met
		if(relationCount == 0)
			return true;
		
		//update sorted types in order from finest to coarsest
		typeOrder = this.graph.sortedTypeGraph();

		if(typeOrder == null)
			throw new QueryStateMultiScaleException();
		if(typeOrder.isEmpty())
			throw new QueryStateMultiScaleException();
		
		//find finest scale value (not necessary to differentiate incomparable scales here)
		int finest = 0;
		Set<Entry<Object, Integer> > typeOrderSet = typeOrder.entrySet();
		Iterator<Entry<Object, Integer> > typeOrderSetIter = typeOrderSet.iterator();
		while(typeOrderSetIter.hasNext())
		{
			Entry<Object,Integer> ent = typeOrderSetIter.next();
			int val = ent.getValue().intValue();
			if(val > finest)
				finest = val;
		}
		
		//store size of lists - to differentiate original relations from those added during query-context matching
		reSize = relationsEqual.size();
		rrSize = relationsRefine.size();
		rcSize = relationsCross.size();
		
		//loop through scales from finest to coarsest
		for(int currScale = finest; currScale > 0; currScale--)
		{
			//iterator of the type graph nodes and scale values
			typeOrderSetIter = typeOrderSet.iterator();
			
			//Step 1 of Query Context Matching Procedure - Verify/Establish Refinement Relationships
			//iterate through type graph nodes and scale values, in search of entries with the currScale
			while(typeOrderSetIter.hasNext())
			{
				Entry<Object, Integer> ent = typeOrderSetIter.next();
				if(ent.getValue().intValue() == currScale)
				{
					if(!queryContextMatchVerifyRefine(ent.getKey()))
						return false;
				}
			}
			
			//Step 2 of Query Context Matching Procedure - Verify/Establish Sucessor/Branching Relationships
			//iterate through type graph nodes and scale values, in search of entries with the currScale-1
			if(currScale > 0)
			{
				typeOrderSetIter = typeOrderSet.iterator();
				int coarserScale = currScale - 1;
				while(typeOrderSetIter.hasNext())
				{
					Entry<Object, Integer> ent = typeOrderSetIter.next();
					if(ent.getValue().intValue() == coarserScale)
					{
						if(!queryContextMatchVerifySuccBran(ent.getKey()))
							return false;
					}
				}
			}
		}
	
		return true;
	}
	
	public void removeDynamicConnections()
	{
		relationsEqual.clear(reSize);
		relationsRefine.clear(rrSize);
		relationsCross.clear(rcSize);
	}
	
	/**
	 * This method performs step 1 of the Query-Context Matching Procedure.
	 * Nodes A and B of the same scale are matched and connected by successor/branching edges.
	 * They share the same encoarsement node C. 
	 * Here, it is validated that A and B should have refinement edges from C.
	 * @param typeNode node from type graph representing scale of A or B.
	 * @return true if all refinement connections are verified successfully, false otherwise.
	 */
	private boolean queryContextMatchVerifyRefine(Object typeNode)
	{
		ObjectList<Relation> nodesWithRefine = new ObjectList<Relation>(); 
		
		//collect nodes with existing refinement relationships bound from neighbour context match
		Iterator<Relation> reIter = relationsEqual.iterator();
		queryContextMatchVerifyRefine(reIter, nodesWithRefine, typeNode);
		
		//collect nodes with existing refinement relationships bound from query context match
		//reIter = relationsEqualDynamic.iterator();
		//queryContextMatchVerifyRefine(reIter, nodesWithRefine, typeNode);
		
		//from list of refine relations with desired node type, 
		//propogate refinement relationship to other equal scale nodes as defined in relationsEqual
		Iterator<Relation> nodesWithRefineIter = nodesWithRefine.iterator();
		try
		{
			while(nodesWithRefineIter.hasNext())
			{
				Relation rr = nodesWithRefineIter.next();
				
				propogateRefinement(rr.tgt,rr.src); //rr.src is the encoarsement node. propogation begins from the fine node.
			}
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	private void queryContextMatchVerifyRefine(Iterator<Relation> reIter, ObjectList<Relation> nodesWithRefine, Object typeNode)
	{
		//find equal relations where one of the nodes have a refinement relation bound 
		while(reIter.hasNext())
		{
			Relation re = reIter.next();
			Object src = re.src;
			Object tgt = re.tgt;
			boolean checkSrc=false;
			boolean checkTgt=false;
			
			if(Reflection.equal(Reflection.getType(src), Reflection.getType(typeNode)))
				checkSrc=true;
			if(Reflection.equal(Reflection.getType(tgt), Reflection.getType(typeNode)))
				checkTgt=true;
			
			if(!(checkSrc || checkTgt))
				continue;
			
			Iterator<Relation> rrIter = relationsRefine.iterator();
			while(rrIter.hasNext())
			{
				Relation rr = rrIter.next();
				if((src == rr.tgt)&&(checkSrc))
					nodesWithRefine.add(rr);
				if((tgt == rr.tgt)&&(checkTgt))
					nodesWithRefine.add(rr);
			}
		}
	}
	
	private void propogateRefinement(Object node, Object encoarseNode) throws QueryStateMultiScaleException
	{
		//iterate through equal refinements list
		Iterator<Relation> reIter = relationsEqual.iterator();
		propogateRefinement(reIter, node, encoarseNode);
		
		//iterate through equal refinements dynamic list
		//reIter = relationsEqualDynamic.iterator();
		//propogateRefinement(reIter, node, encoarseNode);
	}
	
	private void propogateRefinement(Iterator<Relation> reIter, Object node, Object encoarseNode) throws QueryStateMultiScaleException
	{
		while(reIter.hasNext())
		{
			Relation re = reIter.next();

			if(re.src == node)
			{
				// check that encoarseNode is connected to re.tgt by refinement edge
				Object tgt=re.tgt;
				
				int bits = graph.getModel().getEdgeBits(encoarseNode, tgt);
				if(!RuntimeModel.testEdgeBits(bits, RuntimeModel.REFINEMENT_EDGE))
					throw new QueryStateMultiScaleException();
				else
				{
					//ensure that refinement does not already exist
					Iterator<Relation> rrIter = relationsRefine.iterator();
					boolean doesNotExist = true;
					while(rrIter.hasNext())
					{
						Relation rr = rrIter.next();
						if((rr.src == encoarseNode)&&(rr.tgt == tgt))
							doesNotExist=false;
					}
					
					if(doesNotExist)
					{
						//add dynamic refine relationship
						relationsRefine.add(new Relation(encoarseNode,tgt));
						
						//propogate refinement further from tgt node
						propogateRefinement(tgt,encoarseNode);
					}
				}
				
			}
			else if(re.tgt == node)
			{
				// check that encoarseNode is connected to re.src by refinement edge
				Object src=re.src;
				
				int bits = graph.getModel().getEdgeBits(encoarseNode, src);
				if(!RuntimeModel.testEdgeBits(bits, RuntimeModel.REFINEMENT_EDGE))
					throw new QueryStateMultiScaleException();
				else
				{
					//ensure that refinement does not already exist
					//ensure that refinement does not already exist
					Iterator<Relation> rrIter = relationsRefine.iterator();
					boolean doesNotExist = true;
					while(rrIter.hasNext())
					{
						Relation rr = rrIter.next();
						if((rr.src == encoarseNode)&&(rr.tgt == src))
							doesNotExist=false;
					}
					
					if(doesNotExist)
					{
						//add dynamic refine relationship
						relationsRefine.add(new Relation(encoarseNode,src));
						
						//propogate refinement further from src node
						propogateRefinement(src,encoarseNode);
					}
				}
			}
		}
	}
	
	/**
	 *  This method performs step 2 of the Query-Context Matching Procedure.
	 *  Nodes A and B of different scales are matched and connected either by
	 *  a) in the case where the LHS node is coarser than RHS node - Refinement edges
	 *  b) in the case where the LHS node is finer than RHS node - by edges between their Refinements/Encoarsements.
	 *  
	 *  Edges between word groups (in the case of b) ) are validated.
	 *  
	 * @param typeNode node from type graph representing the relationship pairs to be validated. Only relationship pairs with the coarser node at the same scale of typeNode are validated.
	 * @return true if all word-crossing connections are verified successfully, false otherwise.
	 */
	private boolean queryContextMatchVerifySuccBran(Object typeNode)
	{
		//look for all word-crossing relations involving nodes A and B such that scale of B is coarser than A.
		//and B is same type as input typeNode		
		ObjectList<Relation> crosses = new ObjectList<Relation>(); //container for filtered crossing relations
		
		Iterator<Relation> rcIter = relationsCross.iterator(); //iterate through crossing relations to search
		while(rcIter.hasNext())
		{
			Relation rc = rcIter.next();

			int srcScale = this.graph.getScaleValue(typeOrder, rc.src);
			int tgtScale = this.graph.getScaleValue(typeOrder, rc.tgt);
			
			if((srcScale == -1)||(tgtScale==-1)) //unable to find scale of bound node
				return false;
			
			if(srcScale < tgtScale)
			{
				if(Reflection.equal(Reflection.getType(rc.src), Reflection.getType(typeNode)))
					crosses.add(rc);
			}
			else if(srcScale > tgtScale)
			{
				if(Reflection.equal(Reflection.getType(rc.tgt), Reflection.getType(typeNode)))
					crosses.add(rc);
			}
			else
				return false; //wording crossing nodes should not be same scale. return false to reject match
		}
		
		//The ObjectList crosses now contains a list of word crossing relations to be validated.
		//Iterate through the list to ensure at least one non-refinement edge connection exists between 2 word groups
		Iterator<Relation> crossesIter = crosses.iterator();
		while(crossesIter.hasNext())
		{
			Relation cross = crossesIter.next();
			
			int srcScale = this.graph.getScaleValue(typeOrder, cross.src);
			int tgtScale = this.graph.getScaleValue(typeOrder, cross.tgt);
			
			Object nodeFine = (srcScale>tgtScale)?cross.src:cross.tgt;
			Object nodeCoarse = (srcScale>tgtScale)?cross.tgt:cross.src;
			
			//ensure they are comparable scales
			if(!graph.areComparableScales(nodeFine,nodeCoarse))
				return false;
			
			if(!queryContextMatchVerifyCross(nodeFine,nodeCoarse))
				return false;
		}
		
		return true;
	}
	
	private boolean queryContextMatchVerifyCross(Object fine, Object coarse)
	{
		crossCount = 0;
		
		if(!queryContextMatchVerifyCrossInternal(fine,coarse))
			return false;
		
		if(crossCount<1)
			return false;
		
		return true;
	}
	
	private boolean queryContextMatchVerifyCrossInternal(Object fine, Object coarse)
	{
		//Check if coarse node is same scale as fine node
		if((graph.isSameScale(fine, coarse)) && (fine!=coarse))
		{
			int edgeBits = graph.model.getEdgeBits(fine, coarse);
			
			if( (RuntimeModel.testEdgeBits(edgeBits, RuntimeModel.BRANCH_EDGE)) ||
				(RuntimeModel.testEdgeBits(edgeBits, RuntimeModel.SUCCESSOR_EDGE))
				)
			{
				relationsEqual.add(new Relation(fine,coarse));
				crossCount++;
				return true;
			}
			else
				return false;
			
			//no recursive call since fine side already same scale as coarse side
		}
		else
		{
			//Check if any of bounded refinements of coarse node (originally bound by neighbour context matching)
			//are same scale as fine node
			Object boundRefineOfCoarse = getRefineAtCross(fine,coarse);
			if(boundRefineOfCoarse != null) //if coarse node has bounded refinement at word cross boundary with same scale as fine node
			{
				int edgeBits = graph.model.getEdgeBits(fine, boundRefineOfCoarse);
				if( (RuntimeModel.testEdgeBits(edgeBits, RuntimeModel.BRANCH_EDGE)) ||
						(RuntimeModel.testEdgeBits(edgeBits, RuntimeModel.SUCCESSOR_EDGE))
						)
				{
					relationsEqual.add(new Relation(fine,boundRefineOfCoarse));
					crossCount++;
				}
				else
					return false;
			}
			
			//recursive call to next encoarsements of fine node
			for (EdgeIterator j = graph.model.createEdgeIterator (fine, EdgeDirection.BACKWARD); j.hasEdge (); j.moveToNext ())
			{
				if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
					continue;
				if(j.source == fine)
					continue;
				
				if(!queryContextMatchVerifyCrossInternal(j.source, coarse))
					return false;
			}
			return true;
		}
	}
	
	private Object getRefineAtCross(Object tgtScaleNode, Object coarseNode)
	{
		//search original bound refinement relations
		for(int i=0; i<rrSize; ++i)
		{
			Relation rr = relationsRefine.get(i);
			if(rr.src == coarseNode)
			{
				Object refineNode = rr.tgt;
				if(graph.isSameScale(refineNode, tgtScaleNode))
					return refineNode;
				else
					return getRefineAtCross(tgtScaleNode,refineNode);
			}
		}
		return null;
	}
	
	public void updateFirstLastNodes()
	{
		//update First nodes
		updateFirstNodes();
		
		//update Last nodes
		updateLastNodes();
		
		//update Trailing refinement connections
		updateTrailingRefineNodes();
	}
	
	private void updateFirstNodes()
	{
		if(firstNodes==null)
			firstNodes = new ObjectList<Object>();
		else
			firstNodes.clear();
		
		//go through all original (not dynamically added during query context match) refine relationships
		for(int i=0; i<rrSize; ++i)
		{
			Relation rr = relationsRefine.get(i);
			
			//check if src and target nodes have incoming bound
			//if not, they are the first nodes for their respective scales
			if(!haveIncomingBound(rr.src))
				addFirstNode(rr.src);
			
			if(!haveIncomingBound(rr.tgt))
				addFirstNode(rr.tgt);
		}
	}
	
	private boolean haveIncomingBound(Object node)
	{
		for(int j=0; j<relationsEqual.size(); ++j)
		{
			Relation re = relationsEqual.get(j);
			if(re.tgt == node)
				return true;
		}
		return false;
	}
	
	private void updateLastNodes()
	{
		if(lastNodes==null)
			lastNodes = new ObjectList<Object>();
		else
			lastNodes.clear();
		
		//go through all equal relationships
		for(int i=0; i<relationsEqual.size(); ++i)
		{
			Relation re = relationsEqual.get(i);
			
			//check target node have outgoing edge
			//if not, it is the last node for their respective scales
			if(!haveOutgoingBound(re.tgt))
				addLastNode(re.tgt);
		}
		
		//go through all refinement relationships
		for(int i=0; i<relationsRefine.size(); ++i)
		{
			Relation rr = relationsRefine.get(i);
			
			//check target and source nodes have outgoing edges
			//if not, it is the last node for their respective scales
			if(!haveOutgoingBound(rr.tgt))
				addLastNode(rr.tgt);
			if(!haveOutgoingBound(rr.src))
				addLastNode(rr.src);
		}
		
		//go through all cross relationships
		for(int i=0; i<relationsCross.size(); ++i)
		{
			Relation rc = relationsCross.get(i);
			
			//check target and source nodes have outgoing edges
			//if not, it is the last node for their respective scales
			if(!haveOutgoingBound(rc.tgt))
				addLastNode(rc.tgt);
			if(!haveOutgoingBound(rc.src))
				addLastNode(rc.src);
		}
	}
	
	private boolean haveOutgoingBound(Object node)
	{
		for(int j=0; j<relationsEqual.size(); ++j)
		{
			Relation re = relationsEqual.get(j);
			if(re.src == node)
				return true;
		}
		return false;
	}
	
	public ObjectList<Object> getFirstNodes()
	{
		return firstNodes;
	}
	
	public ObjectList<Object> getLastNodes()
	{
		return lastNodes;
	}
	
	private void addFirstNode(Object node)
	{
		boolean alreadyHasFirst = false; //scale already has first node flag
		for(int i=0; i<firstNodes.size(); ++i)
		{
			Object n = firstNodes.get(i);
			if(graph.isSameScale(n, node))
				alreadyHasFirst=true;
		}
		
		if(!alreadyHasFirst)
			firstNodes.add(node);
	}
	
	private void addLastNode(Object node)
	{
		boolean alreadyHasLast = false; //scale already has first node flag
		for(int i=0; i<lastNodes.size(); ++i)
		{
			Object n = lastNodes.get(i);
			if(graph.isSameScale(n, node))
				alreadyHasLast=true;
		}
		
		if(!alreadyHasLast)
			lastNodes.add(node);
	}
	
	/**
	 * This method uses the updated list of first nodes. If these first nodes are removed while leaving some refinement
	 * relationships undeleted, the undeleted refinement relationships are added to a list.
	 * During embedding, these refinement relationships are re-established using nodes from the production graph if they are
	 * provided.
	 */
	private void updateTrailingRefineNodes()
	{
		//trailing incoming refinements
		if(trailingInNodes==null)
			trailingInNodes = new HashMap<Object, ObjectList<Object>>();
		else
			trailingInNodes.clear();
		
		//trailing outgoing refinements
		if(trailingOutNodes==null)
			trailingOutNodes = new HashMap<Object, ObjectList<Object>>();
		else
			trailingOutNodes.clear();
		
		//go through list of first nodes (assume they are already updated in a previous procedure - updateFirstNodes)
		for(int i=0; i<firstNodes.size(); ++i)
		{
			Object firstNode = firstNodes.get(i);
			
			//check for incoming refinement relationships that are not matched (trailing incoming)
			updateTrailingRefineIn(firstNode);
			
			//check for outgoing refinement relationships that are not matched (trailing outgoing)
			updateTrailingRefineOut(firstNode);
		}
		
		//go through list of last nodes (assume they are already updated in a previous procedure - updateLastNodes) 
		for(int i=0; i<lastNodes.size(); ++i)
		{
			Object lastNode = lastNodes.get(i);
			
			//check for incoming refinement relationships that are not matched (trailing incoming)
			updateTrailingRefineIn(lastNode);
			
			//check for outgoing refinement relationships that are not matched (trailing outgoing)
			updateTrailingRefineOut(lastNode);
		}
	}
	
	private void updateTrailingRefineIn(Object node)
	{
		for (EdgeIterator j = graph.model.createEdgeIterator (node, EdgeDirection.BACKWARD); j.hasEdge (); j.moveToNext ())
		{
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			if(j.source == node)
				continue;
			
			//incoming refinement edge found
			//check if the encoarsement is part of the queried graph
			boolean encoarseInQuery = false;
			for(int i=0; i<relationsRefine.size(); ++i)
			{
				Relation rr = relationsRefine.get(i);
				
				if((rr.src==j.source)&&(rr.tgt==node))
					encoarseInQuery=true;
			}
			//if encoarsement not in query, this is a trailing incoming refinement connection
			if(!encoarseInQuery)
			{
				ObjectList<Object> trailingInList = trailingInNodes.get(node);
				if(trailingInList==null)
				{
					trailingInList= new ObjectList<Object>();
					trailingInNodes.put(node,trailingInList);
				}
				if(!trailingInList.contains(j.source))
					trailingInList.add(j.source);
			}
		}
	}
	
	private void updateTrailingRefineOut(Object node)
	{
		for (EdgeIterator j = graph.model.createEdgeIterator (node, EdgeDirection.FORWARD); j.hasEdge (); j.moveToNext ())
		{
			if(!RuntimeModel.testEdgeBits(j.edgeBits, RuntimeModel.REFINEMENT_EDGE))
				continue;
			if(j.target == node)
				continue;
			
			//outgoing refinement edge found
			//check if the refinement is part of the queried graph
			boolean refineInQuery = false;
			for(int i=0; i<relationsRefine.size(); ++i)
			{
				Relation rr = relationsRefine.get(i);
				
				if((rr.tgt==j.target)&&(rr.src==node))
					refineInQuery=true;
			}
			//if refinement not in query, this is a trailing outgoing refinement connection
			if(!refineInQuery)
			{
				ObjectList<Object> trailingOutList = trailingOutNodes.get(node);
				if(trailingOutList==null)
				{
					trailingOutList= new ObjectList<Object>();
					trailingOutNodes.put(node, trailingOutList);
				}
				if(!trailingOutList.contains(j.target))
					trailingOutList.add(j.target);
			}
		}
	}
	
	public HashMap<Object, ObjectList<Object> > getTrailingIncomingRefinements()
	{
		return trailingInNodes;
	}
	
	public HashMap<Object, ObjectList<Object> > getTrailingOutgoingRefinements()
	{
		return trailingOutNodes;
	}
	
	public void updateTrailingRefinements()
	{
		updateTrailingRefineNodes();
	}
}
