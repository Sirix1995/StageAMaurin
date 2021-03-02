
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.pf.io;

import java.io.*;
import de.grogra.util.*;
import org.xml.sax.*;

public abstract class XMLReaderBase extends FilterBase
	implements ObjectSource, ContentHandler
{
	
	protected Locator loc;


	public XMLReaderBase (FilterItem item, FilterSource source)
	{
		super (item, source);
	}


	public Object getObject () throws IOException
	{
		try
		{
			((SAXSource) source).parse (this, null, null, null, null);
			return getObjectImpl ();
		}
		catch (SAXException e)
		{
			throw new IOWrapException (e);
		}
	}

	
	protected abstract Object getObjectImpl () throws IOException;

	
	public void setDocumentLocator (Locator l)
	{
		loc = l;
	}


	public void startDocument () throws SAXException
	{
	}


	public void endDocument () throws SAXException
	{
	}


	public void startPrefixMapping (String prefix, String uri)
		throws SAXException
	{
	}


	public void endPrefixMapping (String prefix) throws SAXException
	{
	}


	public void characters (char[] ch, int start, int length)
		throws SAXException
	{
	}


	public void ignorableWhitespace (char[] ch, int start, int length)
		throws SAXException
	{
	}


	public void processingInstruction (String target, String data)
		throws SAXException
	{
	}


	public void skippedEntity (String name) throws SAXException
	{
	}

}
