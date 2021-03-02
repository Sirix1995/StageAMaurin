
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

package de.grogra.imp.io;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.XMLReaderBase;
import de.grogra.util.MimeType;
import de.grogra.xl.util.ObjectList;

public class XMLTableReader extends XMLReaderBase
{

	public static final MimeType MIME_TYPE
		= new MimeType ("text/x-grogra-table+xml", null);

	public static final IOFlavor DOUBLE_FLAVOR
		= IOFlavor.valueOf (double[][].class);

	public static final IOFlavor STRING_FLAVOR
		= IOFlavor.valueOf (String[][].class);


	private int depth;
	private ObjectList row, rows;
	private final boolean readDoubles;
	private Locator loc;


	public XMLTableReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		readDoubles = item.getName ().indexOf ("double") >= 0;
		setFlavor (readDoubles ? DOUBLE_FLAVOR : STRING_FLAVOR);
	}


	@Override
	protected Object getObjectImpl () throws IOException
	{
		//to fix the VerifyError you get on "file open" when the version below is used
		return rows.toArray (new double[rows.size ()][]);
		
		//return rows.toArray (readDoubles ? (Object[]) new double[rows.size ()][]
		//					 : (Object[]) new String[rows.size ()][]);
	}


	@Override
	public void startDocument ()
	{
		depth = 0;
		row = new ObjectList ();
		rows = new ObjectList ();
	}


	@Override
	public void startElement (String uri, String localName, String qName,
							  Attributes atts) throws SAXException
	{
		if (depth == 1)
		{
			row.clear ();
		}
		depth++;
	}


	@Override
	public void endElement (String uri, String localName, String qName)
		throws SAXException
	{
		if (--depth == 1)
		{
			if (readDoubles)
			{
				double[] r = new double[row.size ()];
				for (int i = 0; i < r.length; i++)
				{
					try
					{
						r[i] = Double.parseDouble ((String) row.get (i));
					}
					catch (NumberFormatException e)
					{
						throw new SAXParseException (null, loc, e);
					}
				}
				rows.add (r);
			}
			else
			{
				rows.add (row.toArray (new String[row.size ()]));
			}
		}
	}


	@Override
	public void characters (char[] ch, int start, int length)
		throws SAXException
	{
		if (depth == 3)
		{
			row.add (new String (ch, start, length).trim ());
		}
	}

}
