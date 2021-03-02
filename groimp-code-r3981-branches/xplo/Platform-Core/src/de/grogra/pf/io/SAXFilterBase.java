
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
import org.xml.sax.*;
import org.xml.sax.ext.*;

public abstract class SAXFilterBase extends FilterBase
	implements SAXSource, XMLReader
{
	public static final String LEX_HANDLER
		= "http://xml.org/sax/properties/lexical-handler";


	public SAXFilterBase (FilterItem item, FilterSource source)
	{
		super (item, source);
	}


	public SAXFilterBase (FilterItem item, FilterSource source,
						  IOFlavor targetFlavor)
	{
		super (item, source);
		setFlavor (targetFlavor);
	}


	private ContentHandler ch = null;
	private ErrorHandler eh = null;
	private LexicalHandler lh = null;
	private DTDHandler dh = null;
	private EntityResolver er = null;


	public Object getProperty (String name) throws SAXNotRecognizedException
	{
		if (LEX_HANDLER.equals (name))
		{
			return lh;
		}
		throw new SAXNotRecognizedException (name);
	}


	public void setProperty (String name, Object value)
		throws SAXNotRecognizedException
	{
		if (LEX_HANDLER.equals (name))
		{
			lh = (LexicalHandler) value;
			return;
		}
		throw new SAXNotRecognizedException (name);
	}


	public boolean getFeature (String name)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		throw new SAXNotRecognizedException (name);
	}


	public void setFeature (String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		throw new SAXNotRecognizedException (name);
	}


	public ContentHandler getContentHandler ()
	{
		return ch;
	}


	public void setContentHandler (ContentHandler ch)
	{
		this.ch = ch;
	}


	public ErrorHandler getErrorHandler ()
	{
		return eh;
	}


	public void setErrorHandler (ErrorHandler eh)
	{
		this.eh = eh;
	}


	public DTDHandler getDTDHandler ()
	{
		return dh;
	}


	public void setDTDHandler (DTDHandler dh)
	{
		this.dh = dh;
	}


	public EntityResolver getEntityResolver ()
	{
		return er;
	}


	public void setEntityResolver (EntityResolver er)
	{
		this.er = er;
	}


	public void parse (InputSource input) throws IOException, SAXException
	{
		((SAXSource) source).parse (ch, eh, lh, dh, er);
	}


	public void parse (String systemId) throws IOException, SAXException
	{
		parse ((InputSource) null);
	}

}
