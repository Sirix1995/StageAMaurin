/*
 * Copyright (C) 2002 - 2006 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.msml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.grogra.pf.io.DOMSource;
import de.grogra.pf.io.FileReaderSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;

public class MTGReader extends FilterBase implements DOMSource
{
		
	public MTGReader (FilterItem item, FilterSource source)
	{
		super(item, source);
		setFlavor(item.getOutputFlavor());
	}
	
	public Document getDocument () throws IOException, DOMException
	{
		try {
			  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			  DocumentBuilder builder = factory.newDocumentBuilder();
			  DOMImplementation impl = builder.getDOMImplementation();
			  Document doc = impl.createDocument(null, "mtg", null);
			  return doc;
			}
			catch (FactoryConfigurationError e) { 
			  System.err.println(
			   "Could not locate a JAXP DocumentBuilderFactory class"); 
			}
			catch (ParserConfigurationException e) { 
			  System.err.println(
			   "Could not locate a JAXP DocumentBuilder class"); 
			}
		return null;	
	}
	
	@Override
	protected Object getImpl (MetaDataKey key, Object defaultValue)
	{
		if ("input-mtg-file".equals(key.toString ()))
		{
			return ((FileReaderSource) source).getInputFile ().toURI().toString();
		}
		else
		{
			return super.getImpl(key, defaultValue);
		}
	}
}
