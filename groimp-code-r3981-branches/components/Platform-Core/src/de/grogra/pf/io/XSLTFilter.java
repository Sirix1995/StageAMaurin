
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

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.util.IOWrapException;
import de.grogra.util.StringMap;

public class XSLTFilter extends SAXFilterBase implements OutputStreamSource
{
	private final Templates xslt;


	public XSLTFilter (FilterItem item, FilterSource source, Templates xslt,
					   IOFlavor targetFlavor)
	{
		super (item, source, targetFlavor);
		this.xslt = xslt;
	}


	public void parse (ContentHandler ch, ErrorHandler eh, LexicalHandler lh,
					   DTDHandler dh, EntityResolver er)
		throws IOException, SAXException
	{
		SAXResult r = new SAXResult (ch);
		if (lh != null)
		{
			r.setLexicalHandler (lh);
		}
		transform (r);
	}


	public void write (OutputStream out) throws IOException
	{
		try
		{
			transform (new StreamResult (out));
		}
		catch (SAXException e)
		{
			throw new IOWrapException (e);
		}
	}


	private void transform (Result res)
		throws IOException, SAXException
	{
		try
		{
			Transformer xf = xslt.newTransformer ();
			Item dir = item.getItem ("params");
			if (dir != null)
			{
				for (Item i = (Item) dir.getBranch (); i != null; i = (Item) i.getSuccessor ())
				{
					if (i instanceof Expression)
					{
						xf.setParameter (i.getName (), ((Expression) i).evaluate (this, new StringMap (this)));
					}
				}
			}
			dir = item.getItem ("output");
			if (dir != null)
			{
				for (Item i = (Item) dir.getBranch (); i != null; i = (Item) i.getSuccessor ())
				{
					if (i instanceof Expression)
					{
						xf.setOutputProperty (i.getName (), String.valueOf (((Expression) i).evaluate (this, new StringMap ())));
					}
				}
			}
			xf.transform
				(new javax.xml.transform.sax.SAXSource (this, new InputSource (source.getSystemId ())),
				 res);
		}
		catch (TransformerException e)
		{
			throw new de.grogra.util.IOWrapException (e);
		}
	}


	@Override
	public boolean getFeature (String name)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return ((SAXSource) source).getFeature (name);
	}


	@Override
	public void setFeature (String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		((SAXSource) source).setFeature (name, value);
	}

}
