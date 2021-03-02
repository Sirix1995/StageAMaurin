
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
import org.w3c.dom.*;
import de.grogra.util.*;
import de.grogra.vfs.FileSystem;

public abstract class LazyFilter extends FilterBase
	implements InputStreamSource, OutputStreamSource, ReaderSource,
		WriterSource, FileReaderSource, FileWriterSource, ObjectSource,
		SAXSource, DOMSource, Configurable, VirtualFileReaderSource, VirtualFileWriterSource
{
	private FilterSource fs = null;


	public LazyFilter (FilterItem item, FilterSource source, IOFlavor flavor)
	{
		super (item, source);
		setFlavor (flavor);
	}


	protected abstract FilterSource createFilterSource ();


	protected final FilterSource getFilterSource ()
	{
		if (fs == null)
		{
			fs = createFilterSource ();
		}
		return fs;
	}


	public InputStream getInputStream () throws IOException
	{
		return ((InputStreamSource) getFilterSource ()).getInputStream ();
	}


	public long length ()
	{
		return ((InputStreamSource) getFilterSource ()).length ();
	}


	public void write (OutputStream out) throws IOException
	{
		((OutputStreamSource) getFilterSource ()).write (out);
	}


	public Reader getReader () throws IOException
	{
		return ((ReaderSource) getFilterSource ()).getReader ();
	}


	public void write (Writer out) throws IOException
	{
		((WriterSource) getFilterSource ()).write (out);
	}


	public File getInputFile ()
	{
		return ((FileReaderSource) getFilterSource ()).getInputFile ();
	}


	public void write (File out) throws IOException
	{
		((FileWriterSource) getFilterSource ()).write (out);
	}


	public FileSystem getFileSystem ()
	{
		return ((VirtualFileReaderSource) getFilterSource ()).getFileSystem ();
	}

	public Object getFile ()
	{
		return ((VirtualFileReaderSource) getFilterSource ()).getFile ();
	}


	public void write (FileSystem fs, Object out) throws IOException
	{
		((VirtualFileWriterSource) getFilterSource ()).write (fs, out);
	}


	public Object getObject () throws IOException
	{
		return ((ObjectSource) getFilterSource ()).getObject ();
	}


	public boolean getFeature (String name)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		return ((SAXSource) getFilterSource ()).getFeature (name);
	}


	public void setFeature (String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		((SAXSource) getFilterSource ()).setFeature (name, value);
	}


	public void parse (ContentHandler ch, ErrorHandler eh, LexicalHandler lh,
					   DTDHandler dh, EntityResolver er)
		throws IOException, SAXException
	{
		((SAXSource) getFilterSource ()).parse (ch, eh, lh, dh, er);
	}


	public Document getDocument () throws IOException, DOMException
	{
		return ((DOMSource) getFilterSource ()).getDocument ();
	}


	public void addConfigurations (ConfigurationSet set)
	{
		FilterSource fs = getFilterSource ();
		if (fs instanceof Configurable)
		{
			set.add ((Configurable) fs);
		}
	}

}
