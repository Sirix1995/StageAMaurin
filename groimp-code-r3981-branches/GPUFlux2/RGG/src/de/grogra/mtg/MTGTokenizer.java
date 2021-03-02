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

import java.util.StringTokenizer;

/**
 * Tokenizer for lines read from MTG file.
 * 
 * @author Ong Yongzhi
 * @since  2011-11-24
 */
public class MTGTokenizer {

	/**
	 * String to be tokenized
	 */
	private String lineString;
	
	/**
	 * Delimiter characters
	 * '#' is comment character in MTG files
	 * '\t' are semantically individually useful in MTG files
	 */
	private final static String delimiters = " \t\n\r\f#";
	
	/**
	 * Delimiter characters for right-hand-side classes in description/topo-constraints header section
	 * Class symbols seperated by ','
	 */
	private final static String delimitersTopo = ",";
	
	/**
	 * Delimiter characters for entity names in body section
	 * Edge-Node pairs separated by '+', '<' ,'/', '.', '^' characters
	 */
	private final static String delimitersEntity = "+</.^*";
	
	/**
	 * Flag indicating if a '#' comment char is encountered
	 * Comments can be blocks bounded by 2 '#', or a single line comment from '#' up till the next tab character
	 */
	boolean commentCharFound;
	
	public MTGTokenizer (String lineString)
	{
		this.lineString = lineString;
		commentCharFound=false;
	}
	
	public void setLineString(String lineString)
	{
		this.lineString = lineString;
	}
	
	public void setCommentCharFound(boolean comment)
	{
		commentCharFound=comment;
	}
	
	/**
	 * Tokenizes member variable lineString.
	 * Tab characters are returned individually as tokens.
	 * Commented tokens (from a '#' character/token to up to a '\t' (tab) character/token are not returned.
	 * @return String[] array of Strings representing tokens.
	 */
	public String[] tokenizeLineString()
	{
		//tokenize the member variable 'lineString'
		StringTokenizer javaTokenizer = new StringTokenizer(lineString,delimiters,true);
		
		int tokenCount = javaTokenizer.countTokens();
		if(tokenCount==0)
			return null;
		
		//array for tokens before filtering away space characters
		String[] resultTokensPreSpaceFilter = new String[tokenCount];
		int counter=0;
		
		while(javaTokenizer.hasMoreTokens())
		{
			String tempToken = javaTokenizer.nextToken();
			
			//if token is '\t' or '#', and in the midst of handling commented tokens, end comment from this token onwards
			if(commentCharFound && ((tempToken.equalsIgnoreCase("\t"))||(tempToken.equalsIgnoreCase("#"))))
				commentCharFound=false;
			
			//if token is '#', comment begins from this token until the next '\t'(tab) character
			if(tempToken.equalsIgnoreCase("#"))
				commentCharFound=true;
			
			//If handling non-comment tokens
			//Skip space characters (while keeping tab characters - which are meaningful in MTG files)
			if((!commentCharFound)&&(!tempToken.equalsIgnoreCase(" ")))
			{
				resultTokensPreSpaceFilter[counter]=tempToken;
				++counter;
			}
		}
		
		//if no tokens (comments or spaces) were filtered, return resultTokensPreSpaceFilter immediately. No need to copy array.
		if(counter==tokenCount)
			return resultTokensPreSpaceFilter;
		
		//Copy to array containing no space characters and commented tokens
		String[] resultTokensPostSpaceFilter = new String[counter];
		System.arraycopy(resultTokensPreSpaceFilter, 0, resultTokensPostSpaceFilter, 0, counter);
		
		return resultTokensPostSpaceFilter;
	}
	
	/**
	 * Tokenizes 'Right' column strings in MTG file header-Description section
	 * @return String[] array of Strings representing class symbols. Null if no tokens.
	 */
	public static String[] tokenizeTopoRightClasses(String rightClasses)
	{
		//tokenize rightClasses
		StringTokenizer javaTokenizer = new StringTokenizer(rightClasses,delimitersTopo,false);
		
		int tokenCount = javaTokenizer.countTokens();
		if(tokenCount==0)
			return null;
		
		String[] resultTokens = new String[tokenCount];
		int counter=0;
		
		while(javaTokenizer.hasMoreTokens())
		{
			resultTokens[counter]=javaTokenizer.nextToken();
			++counter;
		}
		
		return resultTokens;
	}
	
	/**
	 * Tokenizes Entity-Name strings in MTG file body
	 * @return String[] array of Strings representing nodes,edges,MTG notations. Null if no tokens.
	 */
	public static String[] tokenizeEntityNames(String entityNameString)
	{
		//tokenize entity names
		StringTokenizer javaTokenizer = new StringTokenizer(entityNameString,delimitersEntity,true);
		
		int tokenCount = javaTokenizer.countTokens();
		if(tokenCount==0)
			return null;
		
		String[] resultTokens = new String[tokenCount];
		int counter=0;
		
		while(javaTokenizer.hasMoreTokens())
		{
			resultTokens[counter]=javaTokenizer.nextToken();
			++counter;
		}
		
		return resultTokens;
	}
}
