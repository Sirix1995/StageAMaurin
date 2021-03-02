package de.grogra.xl.query;

import java.io.IOException;
import java.io.Serializable;

import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.query.Pattern.Matcher;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

/**
 * This class is used to represent a space character between 2 primary predicates 
 * (i.e. 'Type' primary pattern) when the compiler traverses the AST to build the expression tree.
 * 
 * E.g. A B ==> 
 * A and B in the above rule statement results in 2 Type predicates. During the 3rd pass of the 
 * compilation, such a pair of primary Type predicates adjacent to one another results in the
 * addition of a SpacingPattern into the expression tree.
 * 
 * This class is part of the extension of XL for multiscale modelling.
 * 
 * @since 17-01-2013
 * @author yong
 *
 */
public final class SpacingPattern extends EdgePattern {

	/**
	 * Constructor for SpacingPattern.
	 * 
	 * nodeType is the data type of a standard node specified in the compile time model. 
	 * e.g. In the RGG CompiletimeModel, getNodeType() returns de.grogra.graph.impl.Node.NType
	 * 
	 * edgeType is the data type of a standard edge specified in the compile time model. 
	 * e.g. In the RGG CompiletimeModel, getEdgeType() returns de.grogra.reflect.Type.INT, i.e. Integer
	 * 
	 * edge is an actual instance of the edgeType. e.g. In the RGG model, edge will be an Integer object.
	 * direction is the direction of the (edge) pattern, as defined in de.grogra.query.xl.EdgeDirection. e.g. EdgeDirection.FORWARD_INT
	 * 
	 * This constructor is called from de.grogra.xl.compiler.pattern.PatternBuilder.join (int edgeType, Place in, Place out, AST pos)
	 * 
	 * @param nodeType
	 * @param edgeType
	 * @param edge
	 * @param direction
	 */
	public SpacingPattern (Type nodeType, Type edgeType, Serializable edge, int direction)
	{
		super (nodeType, edgeType, edge, direction);
	}
	
	//Note that EdgePattern has constructor for parameters (Type nodeType, Type edgeType, int direction)
	//used for non-constant edge types (relation pattern types). 
	//See PatternBuilder.java:addEdge(AST label, EdgeDirection direction, Expression term, AST pos)
	//Such a constructor is not necessary for SpacingPattern because a space is interpreted either as 
	//    i) a constant edge for single scale graphs 
	//    ii) part of a multi-scale query 
	
	/**
	 * Method to create a matcher for this SpacingPattern instance.
	 * 
	 * src is the graph in which the created matcher will search to find matches of this SpacingPattern instance.
	 * 
	 */
	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{		
		//for backward compatibility, spacing interpretation remains identical 
		//to that before extension for multi-scale interpretation if type graph 
		//is absent in the data model
		if(src.getTypeRoot() == null)
		{
			return super.createMatcher(src, providedConstants, neededConstantsOut);
		}
		//multi-scale interpretation with reference to type graph
		else
		{
			return createMatcherMultiScale(src, providedConstants, neededConstantsOut);
		}
	}
	
	private Matcher createMatcherMultiScale(Graph src, XBitSet providedConstants, IntList neededConstantsOut)
	{
		//determine if matching should take place in forward or backward manner
		boolean forward;					//boolean flag to indicate if matching should take place in forward or backward manner
		if (providedConstants.get (0))
		{
			//constant at index 0 is provided, node in front is provided
			//matching shall take place in a forward manner
			forward = true;
		}
		else if (providedConstants.get (1))
		{
			//constant at index 1 is provided, node behind edge is provided
			//matching shall take place in a backward manner
			forward = false;
		}
		else
		{
			//by default, matching takes place in a forward manner
			forward = true;
			
			//since no node instance constants are provided, node in front needs to be bounded
			//Add to list of needed that node binding is needed.
			neededConstantsOut.add (0);
		}
		
		//if spacing edge is not a constant edge, this edge is a pattern edge.
		//if pattern of pattern edge is not provided, add to list of needed constants that the pattern is needed
		if (!(constEdge || providedConstants.get (2)))
		{
			neededConstantsOut.add (2);
		}
		
		//determining the stack variable index/address of "source node" (from) and "target node" (to)
		final int from = forward ? 0 : 1, to = forward ? 1 : 0;
		final EdgeDirection dir = (direction == EdgeDirection.UNDIRECTED_INT) ? EdgeDirection.UNDIRECTED
			: (direction == EdgeDirection.BOTH_INT) ? EdgeDirection.BOTH
			: forward ? EdgeDirection.FORWARD : EdgeDirection.BACKWARD;
		final Type edgeType = getParameterType (2);
		final int patternIndex = constEdge ? -1 : 2;
		final int matchIndex = constEdge ? 2 : 3;

		if (!src.canEnumerateEdges (dir, constEdge, edge)) //This check is not fully implemented in RGGGraph.java
		{
			return null;
		}
		return new Matcher (2)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				Object n = qs.abound (from);
				if (!qs.model.isNode (n))
				{
					return;
				}
				
				//multiscale begin
				//set flag to indicate next matcher does not require multi-scale handling (by default)
				//this flag will be set to true if the invoked matcher performs multiscale matching and requires further checks/validations
				qs.addIsMultiScaleMatcher(true);
				//multiscale end
				
				//set flag to indicate that the CompoundPattern instance need to perform further type checking
				//qs.setIsMultiScaleMatcherLast(true);
				
				//invoke search in graph
				qs.graph.enumerateSpaces
					(n, dir, edgeType, qs, to,
					 patternIndex, edge, matchIndex, consumer, arg);
				
				//multiscale begin
				try
				{
					if(qs.getIsMultiScaleMatcherSize()>0)
					{
						int numOfTrue = qs.getIsMultiScaleMatcherTrueCount();
						int numOfRelations = qs.getRelationCount();
						
						//get last Matcher used
						boolean isMs = qs.popIsMultiScaleMatcher();
						if((isMs)&&(numOfTrue==numOfRelations))
							qs.popRelation();							
					}
				}
				catch(Exception e)
				{
					throw qs.breakPattern;
				}
				//multiscale end
			}


			@Override
			public void visitMatch (QueryState qs, Producer prod)
			{
				if (qs.isNull (matchIndex))
				{
					return;
				}
				prod.producer$visitEdge (SpacingPattern.this);
 			}

		};
	}
	
	
	/*
	 *
	//This implementation of the method 'createMatcherMultiScale' is wrong. It is incorrect to assume that 
	//spacing patterns always have a forward direction and are always ear-marked as successor edges.
	//This is due to the fact that square bracket symbols are also regarded as spacing patterns and
	//the compoundpattern rearranges the patterns in a optimal manner, sometimes changing the pattern directions
	private Matcher createMatcherMultiScale(Graph src, XBitSet providedConstants,
			  IntList neededConstantsOut)
	{
		//matching should take place in forward manner for all multiscale interpretations
		boolean forward = true;	//boolean flag to indicate if matching should take place in forward or backward manner
		if ( (!providedConstants.get (0)) &&
			 (!providedConstants.get (1))
				)
		{
			//if no node instance constants are provided, node in front needs to be bounded
			//Add to list of needed that node binding is needed.
			neededConstantsOut.add (0);
		}
	
		//flag to indicate if this spacing edge qualifies to be interpreted as a multiscale spacing pattern
		boolean qualified = true;
		
		//if spacing edge is not a constant edge, this edge is a boolean/object edge.
		//in such cases, spacing cannot be interpreted using in multiscale manner
		if (!constEdge)
			qualified = false;

		if((direction == EdgeDirection.UNDIRECTED_INT)|| (direction == EdgeDirection.BOTH_INT)||(direction == EdgeDirection.BACKWARD_INT))
			qualified = false;
		
		//the stack variable index/address of "source node" (from)
		final int from = 0, to = 1;
		final boolean isQualified = qualified;			//copy qualified variable to final variable
		final int patternIndex = -1; 					//spacing patterns are never pattern edges, so set this to -1
		final int matchIndex = 2;  						//not sure what this is used for
		final Type edgeType = getParameterType (2); 	//edge type - can be int or object
		if(edgeType.getTypeId () != TypeId.INT) 		//if edge type is not int, unable to interpret this spacing
			qualified = false;
		final EdgeDirection dir = EdgeDirection.FORWARD;//spacing patterns are always forward edges, otherwise not qualified for interpretation
		
		return new Matcher (2)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{				
				if(!isQualified)
					return;
				
				//Get "source" bounded node object, i.e. the node before this spacing is encountered
				Object n = qs.abound(from);
				
				//if "source" bounded node object is not a node, matching is terminated
				if(!qs.model.isNode(n))
					return;
				
				qs.graph.enumerateSpaces
				(n, dir, edgeType, qs, to, patternIndex, edge, matchIndex, consumer, arg);
			}


			@Override
			public void visitMatch (QueryState qs, Producer prod)
			{
//				if (qs.isNull (matchIndex))
//				{
//					return;
//				}
//				prod.producer$visitEdge (EdgePattern.this);
 			}

		};
	}
	*/
}
