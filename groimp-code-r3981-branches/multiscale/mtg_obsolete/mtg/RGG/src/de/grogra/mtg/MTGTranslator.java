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

import java.io.Reader;

import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.mtg.MTGError.MTGPlantFrameException;
import de.grogra.mtg.nodes.MTGRoot;
import de.grogra.rgg.RGGRoot;

/**
 * Main Class for parsing and translating the MTG file data into a graph
 * 
 * @author Ong Yongzhi
 * @since  2011-11-18
 */
public class MTGTranslator 
{
	/**
	 * File resource from GroIMP's filter interface (Filter interface in this case instantiated as MTGFilter).
	 */
	private Reader mtgReader;
	
	/**
	 * Line reader for reading each line of data from MTG file.
	 */
	private MTGLineReader mtgLineReader;
	
	/**
	 * Maintains state of the MTG data loading. e.g. Last node at each scale of a column in the MTG file. 
	 * Also maintains the generated graph structure.
	 */
	private MTGGraphBuilder mtgGraphBuilder;
	
	/**
	 * Counts number of lines in MTG file parsed.
	 */
	private int lineCounter;
	
	/**
	 * Saves error message
	 */
	private String errorMessage;
		
	/**
	 * MTGTranslator constructor.
	 * @param reader Reader instance from the MTGFilter. 
	 * File resources are loaded into FilterSource objects in GroIMP. 
	 * The MTGFilter is constructed with a member FilterSource variable. 
	 */
	public MTGTranslator(Reader reader)
	{
		this.mtgReader = reader;
		mtgLineReader=null;
		mtgGraphBuilder=null;
		errorMessage=new String("");
	}
	
	/**
	 * Translates the MTG file into a graph.
	 * @return Code specifying success or error in translation 
	 * @throws MTGPlantFrameException 
	 */
	public int translateMTGFile()
	{
		//set counter to 0
		lineCounter=0;
		
		//set error message to nothing
		errorMessage="";
		
		//Check .mtg input file
		try {
			mtgLineReader = new MTGLineReader(mtgReader);
		} catch (Throwable t) {
			return MTGError.MTG_TRANSLATOR_ERROR_MTG_BUFFER_CHECK;
		}
		
		//Instantiate Graph builder for constructing graph from tokens
		mtgGraphBuilder = new MTGGraphBuilder();
		
		//Begin .mtg file read loop
		String mtgLine = mtgLineReader.readLine();
		
		//Instantiate Tokenizer
		MTGTokenizer mtgTokenizer = new MTGTokenizer(mtgLine);
		
		while(mtgLine!=null)
		{
			lineCounter++;
			
			mtgTokenizer.setCommentCharFound(false);
			mtgTokenizer.setLineString(mtgLine);
			String[] mtgTokens = mtgTokenizer.tokenizeLineString();
			
			//FOR DEBUG
			/*
			System.out.println("MTG Translator " + mtgLine);
			if(mtgTokens!=null)
			{
				for(int k=0; k<mtgTokens.length; ++k)
					System.out.println("MTG Translator Tokens: " + mtgTokens[k]);
			}
			*/
			//END DEBUG
			
			if(mtgTokens!=null)
			{
				try
				{
					mtgGraphBuilder.processTokens(mtgTokens);
				}
				catch(MTGError.MTGGraphBuildException ex)
				{
					//Exception message indicates where exception occurred.
					errorMessage = ex.getError();
					mtgLineReader.close();
					return MTGError.MTG_TRANSLATOR_ERROR_MTG_FILE_SYNTAX_ERROR;
				}
				catch(Throwable t)
				{
					errorMessage = t.getMessage();
					mtgLineReader.close();
					return MTGError.MTG_TRANSLATOR_ERROR_MTG_BUILDER;
				}
			}
			
			//read next line in mtg file
			mtgLine=mtgLineReader.readLine();
		}
		
		//Close or end .mtg file read
		if(mtgLineReader.close()!=0)
		{
			errorMessage = "Error closing file buffer.";
			return MTGError.MTG_TRANSLATOR_ERROR_MTG_BUFFER_CLOSE;
		}
		
		//Check if all stages of MTG file have been processed
		if(mtgGraphBuilder.getStage()!=MTGGraphBuilder.MTG_BODY_DATA)
		{
			errorMessage = "Incomplete File Syntax.";
			return MTGError.MTG_TRANSLATOR_ERROR_MTG_FILE_INCOMPLETE;
		}
		
		//Store list of nodes in root Node
		mtgGraphBuilder.storeNodeListInRoot();
		
		//Remove inter-scale topological relations
		try {
			mtgGraphBuilder.removeInterScaleTopoRelations();
		} 
		catch (MTGPlantFrameException ex) {
			errorMessage = ex.getError();
			int debugIndex = ((MTGRoot)mtgGraphBuilder.getMTGRootNode()).getDebugIndex();
			return MTGError.MTG_TRANSLATOR_TOPO_RELATION_REMOVE_ERROR;
		}
		catch (Throwable t)
		{
			int debugIndex = ((MTGRoot)mtgGraphBuilder.getMTGRootNode()).getDebugIndex();
			return MTGError.MTG_TRANSLATOR_UNEXPECTED_ERROR;
		}
		
		return MTGError.MTG_TRANSLATOR_TRANSLATE_SUCCESSFUL;
	}
	
	/**
	 * Allows other classes to obtain the root node of the graph generated from translating MTG data file.
	 * @return MTGNode The root node of the graph generated from MTG data file.
	 */
	public MTGNode getMTGRootNode()
	{
		return mtgGraphBuilder.getMTGRootNode();
	}
	
	public RGGRoot getRootNode()
	{
		RGGRoot root = new RGGRoot();
		root.addEdgeBitsTo(mtgGraphBuilder.getMTGRootNode(), Graph.BRANCH_EDGE,null);
		return root;
	}
	
	public Sphere getSphere()
	{
		Sphere s = new Sphere(3.0f);
		return s;
	}

	public int getLineCounter() {
		return lineCounter;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
}
