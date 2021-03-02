/*
 * Copyright (C) 2013 GroIMP Developer Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.
 */

package de.grogra.pf.registry;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * Parses the method xml description file (Platfor-Core/jel.xml) and returns a object representation of it.
 * 
 * @author mh, yong
 *
 */
public class MethodDescriptionXMLReader {

	public static ArrayList<MethodDescriptionContent> readXML(InputStream is) {
		//list of content objects
		ArrayList<MethodDescriptionContent> contentList = new ArrayList<MethodDescriptionContent>();
		
		//traversal stack - last integer is index in contentList. Refers to current content being loaded
		ArrayList<Integer> tagStack = new ArrayList<Integer>();
		
		//counts number of recognized start tags that creates a content object
		int tagCount=0;
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader=null;
		try {
			reader = factory.createXMLEventReader(is);
			
			MethodDescriptionContent content=null;
			int contentType=-1;
			
			while (reader.hasNext()) {
				XMLEvent e = reader.nextEvent();

				//ignore spaces
				if (e.isCharacters() && (e.asCharacters()).isWhiteSpace()) continue;
				
				//start tags
				if(e.getEventType()==XMLEvent.START_ELEMENT) {
					//if start tag is <method>
					if(isStartElementMethod(e)) {
						content = new MethodDescriptionContent();
						loadContent(content, e.asStartElement ());
						contentList.add(content);
						tagStack.add(new Integer(tagCount));
						tagCount++;
					}
					//if other start tags - check if recognized.
					//if recognized, set positive type of recognized content as int in contentType
					if((isAnnotation(e) || isParameter(e) || isAttribute(e)) && tagStack.size()>0) {
						contentType = recognizeTag(contentList.get(tagStack.get(tagStack.size()-1)), e, true);
					}
					if(isDescription(e) && tagStack.size()>0) {
						contentType += recognizeTag(contentList.get(tagStack.get(tagStack.size()-1)), e, true);
					}
				} else if(e.getEventType()==XMLEvent.END_ELEMENT) {
					//if end tag is </method>
					if(isEndElementMethod(e)) {
						//pop from stack - current content being loaded goes back to previous content in in stack
						if(tagStack.size()>0)
							tagStack.remove(tagStack.size()-1);
						else
							throw new Exception("Incorrect xml syntax.");
					}
					contentType=-1;
				} else if(e.getEventType()==XMLEvent.CHARACTERS) {
					if((contentType!=-1)&&
						(!(e.toString().equals("<"))) &&
						(!(e.toString().equals(">")))) {
						//load contents, if type of recognized tag is already set in contentType
						loadContent(contentList.get(tagStack.get(tagStack.size()-1)), e.toString(), contentType);
					}
				}
			}
		} catch (Throwable t) {
			return null;
		}
		finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
		}
		return contentList;
	}

	private static void loadContent (MethodDescriptionContent content, StartElement e) {
		Iterator iterator = e.getAttributes();
		while (iterator.hasNext()) {
			Attribute attribute = (Attribute) iterator.next();
			QName name = attribute.getName();
			if(name.toString ().equals ("static"))			content.setStatic (true);
			if(name.toString ().equals ("visibility"))		content.setVisibility (attribute.getValue());
			if(name.toString ().equals ("name"))			content.setName (attribute.getValue());
			if(name.toString ().equals ("type"))			content.setType (attribute.getValue());
			if(name.toString ().equals ("returncomment"))	content.setReturncomment (attribute.getValue());
		}
	}

	private final static void loadContent(MethodDescriptionContent content, String s, int type) {
		switch(type) {
		case 9:
			content.addDescription(s);
			break;
		case 11:
			content.addAttributeParameter(s);
			break;
		case 12:
			content.setReturn (s);
			break;
		case 13:
			content.addSee (s);
			break;
		case 14:
			content.addAnnotation (s);
			break;
		case 15:
			content.addParameter (s);
			break;
		default:
			break;
		}
	}

	private final static int recognizeTag(MethodDescriptionContent content, XMLEvent e, boolean isStart) {
		String elementName = (isStart)?getStartElementName(e):getEndElementName(e);
		if(elementName.equals(MethodDescriptionContent.XML_TAG_METHOD_DESCRIPTION))
			return 10;
		else if(elementName.equals(MethodDescriptionContent.XML_TAG_METHOD_ATTRIBUTE)) {
			Iterator iterator = e.asStartElement ().getAttributes();
			if (iterator.hasNext()) {
				Attribute attribute = (Attribute) iterator.next();
				String value = attribute.getValue();
				if(value.contains ("param")) return 1;
				if(value.contains ("return")) return 2;
				if(value.contains ("see")) return 3;
			}
		}
		else if(elementName.equals(MethodDescriptionContent.XML_TAG_METHOD_ANNOTATION))
			return 4;
		else if(elementName.equals(MethodDescriptionContent.XML_TAG_METHOD_PARAMETER))
			return 5;
		return -1;
	}

	private final static String getStartElementName(XMLEvent e) {
		return e.asStartElement().getName().getLocalPart();
	}

	private final static String getEndElementName(XMLEvent e) {
		return e.asEndElement().getName().getLocalPart();
	}

	private final static boolean isStartElementMethod(XMLEvent e) {
		if(getStartElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD)) return true;
		return false;
	}

	private final static boolean isDescription(XMLEvent e) {
		if(getStartElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD_DESCRIPTION)) return true;
		return false;
	}

	private final static boolean isAnnotation(XMLEvent e) {
		if(getStartElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD_ANNOTATION)) return true;
		return false;
	}

	private final static boolean isParameter(XMLEvent e) {
		if(getStartElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD_PARAMETER)) return true;
		return false;
	}

	private final static boolean isAttribute(XMLEvent e) {
		if(getStartElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD_ATTRIBUTE)) return true;
		return false;
	}
	
	private final static boolean isEndElementMethod(XMLEvent e) {
		if(getEndElementName(e).equals(MethodDescriptionContent.XML_TAG_METHOD)) return true;
		return false;
	}

}
