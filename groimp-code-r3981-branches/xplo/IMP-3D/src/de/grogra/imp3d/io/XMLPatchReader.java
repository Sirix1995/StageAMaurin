
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

package de.grogra.imp3d.io;

import java.io.IOException;
import org.xml.sax.*;
import de.grogra.util.*;
import de.grogra.pf.io.*;
import de.grogra.graph.impl.*;
import de.grogra.imp3d.objects.*;
import de.grogra.math.*;

public class XMLPatchReader extends XMLReaderBase
{
	public static final MimeType MIME_TYPE
		= new MimeType ("text/x-grogra-tuple3table+xml", null);

	public static final IOFlavor FLAVOR
		= IOFlavor.valueOf (Node.class);


	private int depth, rowCount, index;
	private VertexGrid grid;
	private float[] floatArray = new float[3];


	public XMLPatchReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	@Override
	protected Object getObjectImpl () throws IOException
	{
		return new Patch (grid);
	}


	@Override
	public void startDocument ()
	{
		depth = 0;
		rowCount = 0;
		index = 0;
//		grid = new VertexGrid ();
	}


	@Override
	public void endDocument () throws SAXException
	{
//		grid.setLength (index);
//		grid.setGridDimension (index / rowCount, rowCount);
	}


	public void startElement (String uri, String localName, String qName,
							  org.xml.sax.Attributes atts) throws SAXException
	{
		if (depth == 1)
		{
			rowCount++;
		}
		depth++;
	}


	public void endElement (String uri, String localName, String qName)
		throws SAXException
	{
		depth--;
	}


	@Override
	public void characters (char[] ch, int start, int length)
		throws SAXException
	{
		if (depth == 3)
		{
			try
			{
				String s = new String (ch, start, length);
				if (Utils.parseFloatArray (s, floatArray, " ,") != 3)
				{
					throw new SAXParseException
						("Illegal string for triple: " + s, loc);
				}
			}
			catch (NumberFormatException e)
			{
				throw new SAXParseException (null, loc, e);
			}
/*			if (grid.length () <= index)
			{
				grid.setLength (2 * (index + 1));
			}
			grid.setVertex
				(index++, floatArray[0], floatArray[1], floatArray[2]);
				*/
		}
	}

}
