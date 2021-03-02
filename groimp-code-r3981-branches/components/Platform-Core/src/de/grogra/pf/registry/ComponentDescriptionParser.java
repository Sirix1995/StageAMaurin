/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.pf.registry;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class ComponentDescriptionParser {

	//BufferedReader of the Component file
	private final BufferedReader bReader;
	
	//Number of written lines in Output File for the last parsed comment section
	private int tagCount;
	
	//Buffer for lines to be written to Output file - description.txt
	private final ArrayList<String> fileOutput;
	
	//Wait flags - indicates that parser is expecting a particular set of upcoming token(s)
	private boolean waitFlagComment;
	private boolean waitFlagType;
	private boolean waitFlagIdentifier;
	private boolean waitFlagIsMethod;
	//private boolean waitCommentOwners;
	
	//Flags - indicates particular state of parser
	private boolean flagModule;
	
	//Words in Component File
	private static final int COMPONENT_PARSER_KEYWORD_TAG=1;
	private static final int COMPONENT_PARSER_KEYWORD_SCOPE=2;
	private static final int COMPONENT_PARSER_KEYWORD_VARIABLE=3;
	private static final int COMPONENT_PARSER_KEYWORD_MODIFIER=4;
	private static final int COMPONENT_PARSER_KEYWORD_MODULE=5;
	
	private final String keywordsTag[] = {"@date", "@version","@author","@return","@param", "@inSlot", "@outSlot"};
	private final String keywordsScope[] = {"public", "private","protected"};
	private final String keywordsVariableType[] = {"int", "float","double","String","inSlot","outSlot","void"};
	private final String keywordsModifier[] = {"const", "final","static"};
	private final String keywordsModule[] = {"module"};
	
	//Words in Output File
	private static final String COMPONENT_PARSER_SYMBOL_VARIABLE_TYPE = "$";
	private static final String COMPONENT_PARSER_SYMBOL_IDENTIFIER = "$$";
	
	//Line count
	private int lineCount = 0;
	
	//Delimiters for tokenizer
	private final static String delimiters = " \t\n\r\f()";
	
	//Parsed contents
	ComponentDescriptionContent moduleContent;		//module's content and description
	ArrayList<ComponentDescriptionContent> content; //list of content loaded
	ComponentDescriptionContent currentContent;		//current content being loaded
	
	public ComponentDescriptionParser(BufferedReader bReader)
	{
		this.bReader = bReader;
		waitFlagComment=true;
		flagModule=false;
		tagCount=0;
		fileOutput = new ArrayList<String>();
		lineCount=0;
		moduleContent = new ComponentDescriptionContent();
		content = new ArrayList<ComponentDescriptionContent>();
		currentContent = null;
	}
	
	private boolean isIdentifier(String token)
	{
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_TAG))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_SCOPE))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_VARIABLE))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODIFIER))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODULE))
			return false;
		if(delimiters.contains(token))
			return false;
			
		return true;
	}
	
	private boolean isType(String token)
	{
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_TAG))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_SCOPE))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODIFIER))
			return false;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODULE))
			return false;
		if(delimiters.contains(token))
			return false;
		
		return true;
	}
	
	private boolean isSpecifier(String token)
	{
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_TAG))
			return true;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_SCOPE))
			return true;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODIFIER))
			return true;
		if(isKeyword(token,COMPONENT_PARSER_KEYWORD_MODULE))
			return true;
		if(delimiters.contains(token))
			return false;
		
		return false;
	}
	
	private int keyWordIndex(String token, int type)
	{
		switch(type)
		{
		case COMPONENT_PARSER_KEYWORD_TAG:
			for(int i=0; i<keywordsTag.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsTag[i])==0)
					return i;
			}
			break;
		case COMPONENT_PARSER_KEYWORD_SCOPE:
			for(int i=0; i<keywordsScope.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsScope[i])==0)
					return i;
			}
			break;
		case COMPONENT_PARSER_KEYWORD_VARIABLE:
			for(int i=0; i<keywordsVariableType.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsVariableType[i])==0)
					return i;
			}
			break;
		case COMPONENT_PARSER_KEYWORD_MODIFIER:
			for(int i=0; i<keywordsModifier.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsModifier[i])==0)
					return i;
			}
			break;
		case COMPONENT_PARSER_KEYWORD_MODULE:
			for(int i=0; i<keywordsModule.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsModule[i])==0)
					return i;
			}
			break;
		default:
			break;
		}
		
		return -1;
	}
	
	private boolean isKeyword(String token, int type)
	{
		switch(type)
		{
		case COMPONENT_PARSER_KEYWORD_TAG:
			for(int i=0; i<keywordsTag.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsTag[i])==0)
					return true;
			}
			return false;
		case COMPONENT_PARSER_KEYWORD_SCOPE:
			for(int i=0; i<keywordsScope.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsScope[i])==0)
					return true;
			}
			return false;
		case COMPONENT_PARSER_KEYWORD_VARIABLE:
			for(int i=0; i<keywordsVariableType.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsVariableType[i])==0)
					return true;
			}
			return false;
		case COMPONENT_PARSER_KEYWORD_MODIFIER:
			for(int i=0; i<keywordsModifier.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsModifier[i])==0)
					return true;
			}
			return false;
		case COMPONENT_PARSER_KEYWORD_MODULE:
			for(int i=0; i<keywordsModule.length; ++i)
			{
				if(token.compareToIgnoreCase(keywordsModule[i])==0)
					return true;
			}
			return false;
		default:
			return false;
		}
		
	}
	
	public void parse()
	{
		//Buffer for reading each line of the component file
		String buf;
		
		//Buffer for accumulating tags and descriptions in each comment section
		ArrayList<String> tagsAndDescriptions = new ArrayList<String>();
		
		//Buffer for accumulating each line of tag or description
		StringBuffer lineBuffer = new StringBuffer();
		
		//clear fileOutputBuffer
		fileOutput.clear();
		
		try
		{
			while ((buf = bReader.readLine ()) != null)
			{
				lineCount++;
				StringTokenizer st = new StringTokenizer(buf,delimiters,true);
				while (st.hasMoreTokens()) 
				{
					//expecting comment section begin
					String commentContentToken = st.nextToken();
					if(waitFlagComment)
					{
						if(commentContentToken.compareTo("/**")==0)
						{
							//if did not manage to match previous comment section with a variable or a method,
							//remove previous comment section from fileOutput buffer
							if(waitFlagType || waitFlagIdentifier)
							{
								for(int k=0; k<tagCount; ++k)
									fileOutput.remove(fileOutput.size()-1);
								
								content.remove(content.size()-1);
							}
							
							waitFlagComment=false;
							tagCount=0;
							
							if(lineCount==1)
							{
								flagModule=true;
								currentContent=moduleContent;
							}
							else
							{
								currentContent=new ComponentDescriptionContent();
								content.add(currentContent);
							}
						}
						//looking out for module
						else if(flagModule)
						{
							if(isKeyword(commentContentToken,COMPONENT_PARSER_KEYWORD_MODULE))
							{
								fileOutput.add(COMPONENT_PARSER_SYMBOL_VARIABLE_TYPE+" "+commentContentToken);
								++tagCount;
								flagModule=false;
								waitFlagType=false;
								
								//xml
								currentContent.addSpecifier(commentContentToken);
							}
						}
						//looking out for type
						else if(waitFlagType)
						{
							if(isType(commentContentToken))
							{
								fileOutput.add(COMPONENT_PARSER_SYMBOL_VARIABLE_TYPE+" "+commentContentToken);
								++tagCount;
								waitFlagType=false;
								
								//xml
								currentContent.addSpecifier(commentContentToken);
							}
							else if(isSpecifier(commentContentToken))
							{
								fileOutput.add(COMPONENT_PARSER_SYMBOL_VARIABLE_TYPE+" "+commentContentToken);
								++tagCount;
								
								//xml
								currentContent.addSpecifier(commentContentToken);
							}
						}
						//looking out for method return type and method name
						else if((!waitFlagType) && waitFlagIdentifier
								&& (isIdentifier(commentContentToken))
								)
						{
							fileOutput.add(COMPONENT_PARSER_SYMBOL_IDENTIFIER+" "+commentContentToken);
							++tagCount;
							waitFlagIdentifier=false;
							//xml
							currentContent.setName(commentContentToken);
							
							//until ';' or '{' is encountered, check if ( is encounter;
							//if ( is encountered, this is a method, otherwise it is a variable
//							if(st.hasMoreTokens())
//							{
//								commentContentToken = st.nextToken();
//								while((!commentContentToken.equals(";"))&&(!commentContentToken.equals("{")))
//								{
//									
//								}
//							}
						}
						//looking out for indication that the currentContent is method or variable
						else if((!waitFlagType)&&(!waitFlagIdentifier)&&(waitFlagIsMethod))
						{
							if(commentContentToken.equals("("))
							{
								currentContent.setIsMethod(true);
							}
							else if((commentContentToken.equals(";"))||(commentContentToken.equals("{")))
							{
								waitFlagIsMethod=false;
							}
						}
					}
					//within comment section
					else
					{	
						//explicitly ignore any additional * symbols
						if(commentContentToken.compareTo("*")==0)
						{
						}
						//stop expecting more tags and descriptions
						else if(commentContentToken.compareTo("*/")==0)
						{
							//write previous line to tagsAndDescriptions Buffer
							if(lineBuffer.length()>0)
							{
								tagsAndDescriptions.add(lineBuffer.toString());
								
								//xml
								//tokenize the buffer string
								StringTokenizer buffetST = new StringTokenizer(lineBuffer.toString());
								//load 'currentContent' with string tokens 
								loadContent(buffetST, lineBuffer.toString());
							}
							//clear tagsAndDescriptions Buffer
							lineBuffer.delete(0, lineBuffer.length()-1);
							
							//Write accumulated tags and descriptions to fileOutput array
							if(tagsAndDescriptions.size()>0)
							{
								for(int i=0;i<tagsAndDescriptions.size(); i++)
								{
									fileOutput.add(tagsAndDescriptions.get(i));
								}
								tagCount=tagsAndDescriptions.size();
							}
							tagsAndDescriptions.clear();
							//begin expecting comment section again
							waitFlagComment=true;
							waitFlagType=true;
							waitFlagIdentifier=true;
							waitFlagIsMethod=true;
							//waitCommentOwners=true;
						}
						else
						{
							if(isKeyword(commentContentToken,COMPONENT_PARSER_KEYWORD_TAG))
							{
								//write previous line to tagsAndDescriptions Buffer
								if(lineBuffer.length()>0)
								{
									tagsAndDescriptions.add(lineBuffer.toString());
									
									//xml
									//tokenize the buffer string
									StringTokenizer buffetST = new StringTokenizer(lineBuffer.toString());
									//load 'currentContent' with string tokens 
									loadContent(buffetST, lineBuffer.toString());
								}
								//clear tagsAndDescriptions Buffer
								lineBuffer.delete(0, lineBuffer.length()-1);
							}

							lineBuffer.append(commentContentToken);
							lineBuffer.append(" ");
						}
					}
				}
			}
		} 
		catch (Throwable e) 
		{
		}
	}
	
	private void loadContent(StringTokenizer st, String fullString)
	{
		if (st.hasMoreTokens()) 
		{
			//expecting comment section begin
			String firstToken = st.nextToken();
			if(isKeyword(firstToken,COMPONENT_PARSER_KEYWORD_TAG))
			{
				int index = keyWordIndex(firstToken, COMPONENT_PARSER_KEYWORD_TAG);
				
				if(index!=-1)
				{
					if(st.hasMoreTokens())
						fullString = fullString.substring(firstToken.length()+2, fullString.length());
					else
						fullString = "";
				}
				
				//{"@date", "@version","@author","@return","@param","@inSlot","@outSlot"};
				switch(index)
				{
				case 0:
					currentContent.setDate(fullString);
					break;
				case 1:
					currentContent.setVersion(fullString);
					break;
				case 2:
					currentContent.setAuthor(fullString);
					break;
				case 3:
					currentContent.setReturn(fullString);
					break;
				case 4:
					currentContent.addParam(fullString);
					break;
				case 5: //yong 11 jan 2013 - inSlots
					currentContent.addInSlot(fullString);
					break;
				case 6: //yong 11 jan 2013 - outSlots
					currentContent.addOutSlot(fullString);
					break;
				default:
					currentContent.addDescription(fullString);
					break;
				}
			}
			else
			{
				currentContent.addDescription(fullString);
			}
		}
		
		
	}
	
	public Object[] getFileOutput()
	{
		return fileOutput.toArray();
	}
	
	public ComponentDescriptionContent getModuleContent()
	{
		return moduleContent;
	}
	
	public ArrayList<ComponentDescriptionContent> getContent()
	{
		return content;
	}
}
