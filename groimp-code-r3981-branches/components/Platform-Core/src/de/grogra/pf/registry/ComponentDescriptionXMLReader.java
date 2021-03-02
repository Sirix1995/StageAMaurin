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

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


/**
 * Parses the xml description file and returns object representation of descriptions.
 * 
 * @author yong
 *
 */
public class ComponentDescriptionXMLReader 
{
	public static ArrayList<ComponentDescriptionContent> readXML(InputStream is)
	{
		//list of content objects
		ArrayList<ComponentDescriptionContent> contentList = new ArrayList<ComponentDescriptionContent>();
		
		//traversal stack - last integer is index in contentList. Refers to current content being loaded
		ArrayList<Integer> tagStack = new ArrayList<Integer>();
		
		//counts number of recognized start tags that creates a content object
		int tagCount=0;
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader=null;
		try {
			reader = factory.createXMLEventReader(is);
			
			ComponentDescriptionContent content=null;
			int contentType=-1;
			
			while (reader.hasNext()) {
				
	            XMLEvent e = reader.nextEvent();
	            
	            //ignore spaces
	            if (e.isCharacters() && (e.asCharacters()).isWhiteSpace())
	                  continue;
	            
	            //start tags
	            if(e.getEventType()==XMLEvent.START_ELEMENT)
	            {
	            	//if start tag is <module> or <content>
	            	if(isStartElementModule(e)||isStartElementContent(e))
	            	{
	            		content=new ComponentDescriptionContent();
	            		contentList.add(content);
	            		tagStack.add(new Integer(tagCount));
	            		tagCount++;
	            	}
	            	//if other start tags - check if recognized.
	            	//if recognized, set positive type of recognized content as int in contentType
	            	else
	            	{
	            		contentType = recognizeTag(contentList.get(tagStack.get(tagStack.size()-1)), e,true);
	            	}
	            }
	            else if(e.getEventType()==XMLEvent.END_ELEMENT)
	            {
	            	//if end tag is </module> or </content>
	            	if(isEndElementModule(e)||isEndElementContent(e))
	            	{
	            		//pop from stack - current content being loaded goes back to previous content in in stack
	            		if(tagStack.size()>0)
	            			tagStack.remove(tagStack.size()-1);
	            		else
	            			throw new Exception("Incorrect xml syntax.");
	            	}
	            	else
	            	{
	            		//close content type if encounter recognized closing tag
	            		if(recognizeTag(contentList.get(tagStack.get(tagStack.size()-1)), e,false)!=-1)
	            			contentType=-1;
	            	}
	            }
	            else if(e.getEventType()==XMLEvent.CHARACTERS)
	            {
	            	if((contentType!=-1)&&
	            		(!(e.toString().equals("<"))) &&
	            		(!(e.toString().equals(">"))) 	
	            		)
	            	{
	            		//load contents, if type of recognized tag is already set in contentType
	            		loadContent(contentList.get(tagStack.get(tagStack.size()-1)), e.toString(), contentType);
	            	}
	            }
	      }
			
		} catch (Throwable t) {
			return null;
		}
		finally
		{
			if(reader!=null)
			try {
				reader.close();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		
		return contentList;
	}
	
	private final static void loadContent(ComponentDescriptionContent content, String s, int type)
	{
		switch(type)
		{
		case 0:
			content.setName(s);
			break;
		case 1:
			content.setDate(s);
			break;
		case 2:
			content.setAuthor(s);
			break;
		case 3:
			content.addDescription(s);
			break;
		case 4:
			content.setVersion(s);
			break;
		case 5:
			content.setReturn(s);
			break;
		case 6:
			content.addSpecifier(s);
			break;
		case 7:
			content.addParam(s);
			break;
		case 8:
			content.setIsMethod((s.equals("1"))?true:false);
			break;
		case 9:
			content.addInSlot(s);
			break;
		case 10:
			content.addOutSlot(s);
			break;
		default:
			break;
		}
	}
	
	private final static int recognizeTag(ComponentDescriptionContent content, XMLEvent e, boolean isStart)
	{
		String elementName = (isStart)?getStartElementName(e):getEndElementName(e);

		if(elementName.equals(ComponentDescriptionContent.XML_TAG_NAME))
			return 0;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_DATE))
			return 1;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_AUTHOR))
			return 2;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_DESCRIPTION))
			return 3;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_VERSION))
			return 4;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_RETURN))
			return 5;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_MODULE_SPECIFIER_ELEMENT))
			return 6;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_MODULE_PARAMETER_ELEMENT))
			return 7;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_ISMETHOD))
			return 8;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_MODULE_INSLOTS_ELEMENT))
			return 9;
		else if(elementName.equals(ComponentDescriptionContent.XML_TAG_MODULE_OUTSLOTS_ELEMENT))
			return 10;
		return -1;
	}
	
	private final static String getStartElementName(XMLEvent e)
	{
		return e.asStartElement().getName().getLocalPart();
	}
	
	private final static String getEndElementName(XMLEvent e)
	{
		return e.asEndElement().getName().getLocalPart();
	}
	
	private final static boolean isStartElementModule(XMLEvent e)
	{
		if(getStartElementName(e).equals(ComponentDescriptionContent.XML_TAG_MODULE))
			return true;
		return false;
	}
	
	private final static boolean isStartElementContent(XMLEvent e)
	{
		if(getStartElementName(e).equals(ComponentDescriptionContent.XML_TAG_CONTENT))
			return true;
		return false;
	}
	
	private final static boolean isEndElementModule(XMLEvent e)
	{
		if(getEndElementName(e).equals(ComponentDescriptionContent.XML_TAG_MODULE))
			return true;
		return false;
	}
	
	private final static boolean isEndElementContent(XMLEvent e)
	{
		if(getEndElementName(e).equals(ComponentDescriptionContent.XML_TAG_CONTENT))
			return true;
		return false;
	}
	
}
