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

import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ComponentDescriptionXMLWriter {

	public static boolean writeXML(OutputStream os, ComponentDescriptionParser parser)
	{
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer=null;
		try
		{
			writer = factory.createXMLStreamWriter(os);
			writer.writeStartDocument();
		    writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE);
		    
		    	//Module
		    	ComponentDescriptionContent moduleContent = parser.getModuleContent();
		    	writeXMLContent(writer,moduleContent);
		    	
		    	//Methods or Variables
		    	ArrayList<ComponentDescriptionContent> content = parser.getContent();
		    	
		    	for(int j=0; j<content.size(); ++j)
		    	{
		    		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_CONTENT);
		    		writeXMLContent(writer,content.get(j));
		    		writer.writeEndElement();
		    	}
		    	
		    
		    writer.writeEndElement();
		    writer.writeEndDocument();
		    
		    writer.flush();
		    writer.close();
		}
		catch(Throwable t)
		{
			return false;
		}
		finally
		{
			if(writer!=null)
			try 
			{
				writer.close();
			} 
			catch (XMLStreamException e) 
			{
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	private static void writeXMLContent(XMLStreamWriter writer, ComponentDescriptionContent content) throws Exception
	{
		//single line module elements/attributes
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_NAME);
			writer.writeCharacters(content.getName());
		writer.writeEndElement();
		
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_DATE);
			writer.writeCharacters(content.getDate());
		writer.writeEndElement();
		
		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_AUTHOR);
			writer.writeCharacters(content.getAuthor());
		writer.writeEndElement();
		
		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_VERSION);
			writer.writeCharacters(content.getVersion());
		writer.writeEndElement();
		
		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_RETURN);
			writer.writeCharacters(content.getReturn());
		writer.writeEndElement();
		
		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_DESCRIPTION);
			writer.writeCharacters(content.getDescriptionInSingleString());
		writer.writeEndElement();
		
		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_ISMETHOD);
			writer.writeCharacters((content.isMethod())?"1":"0");
		writer.writeEndElement();
    	
    	//multi line module elements/attributes
    	
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_SPECIFIERS);
    	for(int i=0; i<content.getSpecifierCount(); ++i)
    	{
    		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_SPECIFIER_ELEMENT);
				writer.writeCharacters(content.getSpecifier(i));
			writer.writeEndElement();
    	}
    	writer.writeEndElement();
    	
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_PARAMETERS);
    	for(int i=0; i<content.getParamCount(); ++i)
    	{
    		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_PARAMETER_ELEMENT);
    			writer.writeCharacters(content.getParam(i));
			writer.writeEndElement();
    	}
    	writer.writeEndElement();
    	
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_INSLOTS);
    	for(int i=0; i<content.getInSlotCount(); ++i)
    	{
    		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_INSLOTS_ELEMENT);
    			writer.writeCharacters(content.getInSlot(i));
			writer.writeEndElement();
    	}
    	writer.writeEndElement();
    	
    	writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_OUTSLOTS);
    	for(int i=0; i<content.getOutSlotCount(); ++i)
    	{
    		writer.writeStartElement(ComponentDescriptionContent.XML_TAG_MODULE_OUTSLOTS_ELEMENT);
    			writer.writeCharacters(content.getOutSlot(i));
			writer.writeEndElement();
    	}
    	writer.writeEndElement();
	}
}

