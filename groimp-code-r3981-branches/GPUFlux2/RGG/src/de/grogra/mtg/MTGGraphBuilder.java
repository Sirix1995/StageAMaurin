/*
 * Copyright (C) 2011 Abteilung Oekoinformatik, Biometrie und Waldwachstum, 
 * Buesgeninstitut, Georg-August-Universitaet Göttingen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.mtg;

import java.util.ArrayList;

import de.grogra.graph.impl.Edge;
import de.grogra.mtg.MTGError.MTGPlantFrameException;

/**
 * Maintains state of the MTG data loading.
 * Also maintains the generated graph structure.
 * 
 * @author Ong Yongzhi
 * @since  2011-11-24
 */
public class MTGGraphBuilder {

	/**
	 * Root node of graph generated from MTG file data. Null if translation has not been executed.
	 */
	private final MTGNode rootNode;
	private final ArrayList<MTGNode> nodes;
	
	/**
	 * Represents the stage in the MTG file syntax, namely:
	 * 0 - MTG_HEADER_CODE
	 * 1 - MTG_HEADER_CLASSES
	 * 2 - MTG_HEADER_TOPO
	 * 3 - MTG_HEADER_ATTRIBUTES
	 * 4 - MTG_BODY_DATA
	 * 
	 * MTGTranslator checks this at the end of file parsing to determine if all stages have been processed.
	 */
	private int stage;
	protected static final int MTG_HEADER_CODE			=0; 
	protected static final int MTG_HEADER_CLASSES		=1;
	protected static final int MTG_HEADER_TOPO			=2;
	protected static final int MTG_HEADER_ATTRIBUTES	=3;
	protected static final int MTG_BODY_DATA			=4;
	
	/**
	 * Builder objects responsible for Header-Code section of the MTG file.
	 */
	private final MTGGraphBuilderHeaderCode 		builderHeaderCode;
	
	/**
	 * Builder objects responsible for Header-Classes section of the MTG file.
	 */
	private final MTGGraphBuilderHeaderClasses 	builderHeaderClasses;
	
	/**
	 * Builder objects responsible for Header-TopoConstraint section of the MTG file.
	 */
	private final MTGGraphBuilderHeaderTopo 		builderHeaderTopo;
	
	/**
	 * Builder objects responsible for Header-Attributes section of the MTG file.
	 */
	private final MTGGraphBuilderHeaderAttributes builderHeaderAttributes;
	
	/**
	 * Builder objects responsible for Body-Data section of the MTG file.
	 */
	private final MTGGraphBuilderBodyData		 	builderBodyData;
	
	/**
	 * Builder object responsible for create Node classes or Module types representing MTG classes.
	 */
	//private MTGModuleBuilder				builderModule;
	
	/**
	 * System id reference
	 */
	private final String systemId;
	
	/**
	 * Indicates if the token array being processed now is the first line of the body section in the MTG file.
	 * As such, the modules required must be compiled.
	 */
	private final boolean createModules;
	
	
	public MTGGraphBuilder(String sysId)
	{
		rootNode=new MTGRoot();
		this.systemId = sysId;
		nodes = new ArrayList<MTGNode>();
		createModules = false;
		
		builderHeaderCode = new MTGGraphBuilderHeaderCode(rootNode);
		builderHeaderClasses = new MTGGraphBuilderHeaderClasses(rootNode);
		builderHeaderTopo = new MTGGraphBuilderHeaderTopo(rootNode);
		builderHeaderAttributes = new MTGGraphBuilderHeaderAttributes(rootNode);
		builderBodyData = new MTGGraphBuilderBodyData(rootNode,nodes);
		//builderModule = new MTGModuleBuilder(rootNode,systemId);
	}
	
	/**
	 * Allows MTGTranslator to obtain the root node of the graph generated from translating MTG data file.
	 * @return MTGNode The root node of the graph generated from MTG data file. Null if no graph generated.
	 */
	protected MTGNode getMTGRootNode()
	{
		return rootNode;
	}
	
	public MTGNode getFirstNode()
	{
		/*
		MTGNodeRoot root = new MTGNodeRoot();
		for (Edge e = rootNode.getFirstEdge (); e != null; e = e.getNext (rootNode))
		{
			if ((e.getSource () == rootNode) && (e.testEdgeBits(Graph.REFINEMENT_EDGE)))
			{
				MTGNode firstNode = (MTGNode)e.getTarget ();
				removeRootLinks();
				root.addEdgeBitsTo(firstNode, Graph.REFINEMENT_EDGE, null);
				root.length=0;
				//return firstNode;
				return root;
			}
		}
		return null;
		*/
		//((MTGRoot)rootNode).setData(null);
		
		return rootNode;
	}
	
	private void removeRootLinks()
	{
		for (Edge e = rootNode.getFirstEdge (); e != null; e = e.getNext (rootNode))
		{
			if (e.getSource () == rootNode)
			{
				rootNode.removeEdgeBitsTo(e.getTarget(), e.getEdgeBits(), null);
			}
			if(e.getTarget() == rootNode)
			{
				e.getSource().removeEdgeBitsTo(rootNode, e.getEdgeBits(), null);
			}
		}
	}
	
	/**
	 * Number of nodes generated.
	 * @return long Number of nodes generated.
	 */
	public long getMTGNodeCount()
	{
		return nodes.size();
	}
	
	/**
	 * Current stage being processed by the graph builder.
	 * @return int Current stage being processed by the graph builder.
	 */
	public long getStage()
	{
		return stage;
	}
	
	/**
	 * Put node list into root node after building graph.
	 */
	public void storeNodeListInRoot()
	{
		((MTGRoot)rootNode).setObject(MTGKeys.MTG_NODE_NODELIST, nodes);
	}
	
	/**
	 * Removes inter-scale topological relations (i.e. successor and branching edges).
	 * @throws MTGPlantFrameException
	 */
	public void removeInterScaleTopoRelations() throws MTGPlantFrameException
	{
		((MTGRoot)rootNode).removeInterScaleTopoRelations();
		
		//FOR DEBUGGING plantFrame - TODO:To delete later
		//can trigger in XL console by:
		//	import de.grogra.mtg.nodes.MTGRoot
		//	[aa:MTGRoot ::> aa.plantFrame(3);]
		//try {
		//	((MTGRoot)rootNode).plantFrame(3,400);
		//	((MTGRoot)rootNode).plantFrame(2,400);
		//} catch (MTGPlantFrameException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//END DEBUG
	}
	
	/**
	 * Processes tokens and generates nodes and edges as necessary to the current graph.
	 * @param tokens Array of strings representing tokens.
	 */
	public boolean processTokens(String[] tokens) throws MTGError.MTGGraphBuildException
	{
		boolean stageProgressed=false;
		switch(stage)
		{
		case MTG_HEADER_CODE: 		//number of tokens to be expected known beforehand in this stage
			if(builderHeaderCode.processTokensHeaderCode(tokens)==MTGGraphBuilderHeaderCode.MTG_CODE_STAGE_END)
				stage=MTG_HEADER_CLASSES;
			break;
		case MTG_HEADER_CLASSES:	//number of tokens to be expected unknown beforehand in this stage
			if(builderHeaderClasses.processTokensHeaderClasses(tokens)==MTGGraphBuilderHeaderClasses.MTG_CLASSES_STAGE_END)
			{
				stage=MTG_HEADER_TOPO;
				stageProgressed=true;
			}
			break;
		case MTG_HEADER_TOPO:		//number of tokens to be expected unknown beforehand in this stage
			if((builderHeaderTopo.processTokensHeaderTopo(tokens))==MTGGraphBuilderHeaderTopo.MTG_TOPO_STAGE_END)
			{
				stage=MTG_HEADER_ATTRIBUTES;
				stageProgressed=true;
			}
			break;
		case MTG_HEADER_ATTRIBUTES:	//number of tokens to be expected unknown beforehand in this stage
			if(builderHeaderAttributes.processTokensHeaderAttributes(tokens)==MTGGraphBuilderHeaderAttributes.MTG_FEATURES_STAGE_END)
			{
				stage=MTG_BODY_DATA;
				stageProgressed=true;
				
				//create node classes or modules types
				//createTypes();
				//createModules=true;
				return true;
			}
			break;
		case MTG_BODY_DATA:
			//if(!createModules)
				builderBodyData.processTokensBodyData(tokens);
			//else
			//	return true;
			break;
		default:
			throw new MTGError.MTGGraphBuildException("Unknown MTG File section.");
		}
		
		//If stage with unknown number of rows encountered keyword in next stage, builder progresses to next stage
		//Hence call this function again to process the same token array at the next stage.
		if(stageProgressed)
			processTokens(tokens);
		
		return false;
	}
	
	/**
	 * Creates Node classes or Modules Types and stores them in the root node.
	 */
	/*
	private void createTypes() throws MTGError.MTGGraphBuildException
	{
		Type[] types = builderModule.createTypes();
		if(types!=null)
			rootNode.setObject(MTGKeys.MTG_RGG_MODULES, types);
		else
			throw new MTGError.MTGGraphBuildException("Unable to create and compile modules representing MTG classes.");
	}
	*/
	public void removeTypes()
	{
		((MTGRoot)rootNode).setObject(MTGKeys.MTG_RGG_MODULES, null);
	}
	/*
	public void setCreateModules(boolean create)
	{
		createModules = create;
	}
	*/
}
