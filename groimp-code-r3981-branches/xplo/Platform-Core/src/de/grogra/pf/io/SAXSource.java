
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

/**
 * This subinterface of <code>FilterSource</code> has to be implemented
 * by filter sources whose flavor supports SAX events
 * ({@link de.grogra.pf.io.IOFlavor#SAX}). It represents the data
 * as a stream of SAX events.
 * 
 * @author Ole Kniemeyer
 */
public interface SAXSource extends FilterSource
{
	String NAMESPACES = "http://xml.org/sax/features/namespaces";

	String NAMESPACE_PREFIXES
		= "http://xml.org/sax/features/namespace-prefixes";


	boolean getFeature (String name)
		throws SAXNotRecognizedException, SAXNotSupportedException;

	void setFeature (String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException;

	void parse (ContentHandler ch, ErrorHandler eh, LexicalHandler lh,
				DTDHandler dh, EntityResolver er)
		throws IOException, SAXException;
}
