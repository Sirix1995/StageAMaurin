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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;

import de.grogra.vfs.FileSystem;

public final class NullSource extends FilterSourceBase implements SAXSource,
		InputStreamSource, OutputStreamSource, ReaderSource, WriterSource,
		ObjectSource, DOMSource, FileReaderSource, FileWriterSource,
		VirtualFileReaderSource, VirtualFileWriterSource
{

	public NullSource (IOFlavor flavor, de.grogra.pf.registry.Registry r,
			de.grogra.util.ModifiableMap metaData)
	{
		super (flavor, r, metaData);
	}

	public String getSystemId ()
	{
		return "null";
	}

	public boolean getFeature (String name) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		throw new UnsupportedOperationException ();
	}

	public void parse (ContentHandler ch, ErrorHandler eh, LexicalHandler lh, DTDHandler dh, EntityResolver er) throws IOException, SAXException
	{
		throw new UnsupportedOperationException ();
	}

	public void setFeature (String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
	{
		throw new UnsupportedOperationException ();
	}

	public InputStream getInputStream () throws IOException
	{
		throw new UnsupportedOperationException ();
	}

	public long length ()
	{
		throw new UnsupportedOperationException ();
	}

	public void write (OutputStream out) throws IOException
	{
		throw new UnsupportedOperationException ();
	}

	public Reader getReader () throws IOException
	{
		throw new UnsupportedOperationException ();
	}

	public void write (Writer out) throws IOException
	{
		throw new UnsupportedOperationException ();
	}

	public Object getObject () throws IOException
	{
		throw new UnsupportedOperationException ();
	}

	public Document getDocument () throws IOException, DOMException
	{
		throw new UnsupportedOperationException ();
	}

	public File getInputFile ()
	{
		throw new UnsupportedOperationException ();
	}

	public void write (File out) throws IOException
	{
		throw new UnsupportedOperationException ();
	}
	
	public FileSystem getFileSystem ()
	{
		throw new UnsupportedOperationException ();
	}
	
	public Object getFile ()
	{
		throw new UnsupportedOperationException ();
	}

	public void write (FileSystem fs, Object out)
	{
		throw new UnsupportedOperationException ();
	}

}
