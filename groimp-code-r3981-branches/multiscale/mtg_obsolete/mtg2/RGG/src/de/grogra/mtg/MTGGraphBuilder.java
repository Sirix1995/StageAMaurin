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

import de.grogra.mtg.MTGError.MTGPlantFrameException;
import de.grogra.mtg.nodes.MTGRoot;

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
	private MTGNode rootNode;
	private ArrayList<MTGNode> nodes;
	
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
	private MTGGraphBuilderHeaderCode 		builderHeaderCode;
	
	/**
	 * Builder objects responsible for Header-Classes section of the MTG file.
	 */
	private MTGGraphBuilderHeaderClasses 	builderHeaderClasses;
	
	/**
	 * Builder objects responsible for Header-TopoConstraint section of the MTG file.
	 */
	private MTGGraphBuilderHeaderTopo 		builderHeaderTopo;
	
	/**
	 * Builder objects responsible for Header-Attributes section of the MTG file.
	 */
	private MTGGraphBuilderHeaderAttributes builderHeaderAttributes;
	
	/**
	 * Builder objects responsible for Body-Data section of the MTG file.
	 */
	private MTGGraphBuilderBodyData		 	builderBodyData;
	
	public MTGGraphBuilder()
	{
		rootNode=new MTGRoot();
		nodes = new ArrayList<MTGNode>();
		
		builderHeaderCode = new MTGGraphBuilderHeaderCode(rootNode);
		builderHeaderClasses = new MTGGraphBuilderHeaderClasses(rootNode);
		builderHeaderTopo = new MTGGraphBuilderHeaderTopo(rootNode);
		builderHeaderAttributes = new MTGGraphBuilderHeaderAttributes(rootNode);
		builderBodyData = new MTGGraphBuilderBodyData(rootNode,nodes);
	}
	
	/**
	 * Allows MTGTranslator to obtain the root node of the graph generated from translating MTG data file.
	 * @return MTGNode The root node of the graph generated from MTG data file. Null if no graph generated.
	 */
	protected MTGNode getMTGRootNode()
	{
		return rootNode;
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
		rootNode.setObject(MTGKeys.MTG_NODE_NODELIST, nodes);
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
	public void processTokens(String[] tokens) throws MTGError.MTGGraphBuildException
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
			}
			break;
		case MTG_BODY_DATA:
			builderBodyData.processTokensBodyData(tokens);
			break;
		default:
			throw new MTGError.MTGGraphBuildException("Unknown MTG File section.");
		}
		
		//If stage with unknown number of rows encountered keyword in next stage, builder progresses to next stage
		//Hence call this function again to process the same token array at the next stage.
		if(stageProgressed)
			processTokens(tokens);
	}
}
