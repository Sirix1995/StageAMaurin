
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

import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.vfs.FileSystem;

/**
 * An <code>IOFlavor</code> is similar to a
 * {@link java.awt.datatransfer.DataFlavor}: For some data to be transfered
 * in an IO operation, it describes both the type of data and the way how to
 * transfer it. The type is specified by a {@link de.grogra.util.MimeType},
 * the possible ways for transfer by a combination of those bit masks
 * which are defined in this class.
 * 
 * @author Ole Kniemeyer
 */
public final class IOFlavor
{
	/**
	 * Bit mask indicating that the data can be obtained from
	 * an <code>InputStream</code>. The source which provides the
	 * data has to implement {@link InputStreamSource}.
	 */
	public static final int INPUT_STREAM = 1; 

	/**
	 * Bit mask indicating that the data can be written to
	 * an <code>OutputStream</code>. The source which provides the
	 * data has to implement {@link OutputStreamSource}.
	 */
	public static final int OUTPUT_STREAM = 2; 

	/**
	 * Bit mask indicating that the data can be obtained from
	 * a <code>Reader</code>. The source which provides the
	 * data has to implement {@link ReaderSource}.
	 */
	public static final int READER = 4; 

	/**
	 * Bit mask indicating that the data can be written to
	 * a <code>Writer</code>. The source which provides the
	 * data has to implement {@link WriterSource}.
	 */
	public static final int WRITER = 8; 

	/**
	 * Bit mask indicating that the data can be obtained as a series
	 * of SAX events. The source which provides the
	 * data has to implement {@link SAXSource}.
	 */
	public static final int SAX = 16; 

	/**
	 * Bit mask indicating that the data can be obtained from
	 * a <code>File</code>. The source which provides the
	 * data has to implement {@link FileReaderSource}.
	 */
	public static final int FILE_READER = 32; 

	/**
	 * Bit mask indicating that the data can be written to
	 * a <code>File</code>. The source which provides the
	 * data has to implement {@link FileWriterSource}.
	 */
	public static final int FILE_WRITER = 64;

	/**
	 * Bit mask indicating that the data can be obtained as a
	 * DOM tree. The source which provides the
	 * data has to implement {@link DOMSource}.
	 */
	public static final int DOM = 128;

	/**
	 * Bit mask indicating that the data can be obtained immediately
	 * as an <code>Object</code>. The source which provides the
	 * data has to implement {@link ObjectSource}.
	 */
	public static final int OBJECT = 256;
	/**
	 * Bit mask indicating that the data can be obtained from
	 * a virtual file. The source which provides the
	 * data has to implement {@link VirtualFileReaderSource}.
	 */
	public static final int VFILE_READER = 512; 

	/**
	 * Bit mask indicating that the data can be written to
	 * a virtual file. The source which provides the
	 * data has to implement {@link VirtualFileWriterSource}.
	 */
	public static final int VFILE_WRITER = 1024;

	private static final String[] FEATURE_NAMES
		= {"is", "os", "reader", "writer", "sax", "freader", "fwriter", "dom", "object", "vfreader", "vfwriter"};

	public static final int TRANSFER_IO_STREAM = INPUT_STREAM | OUTPUT_STREAM;
	public static final int TRANSFER_STREAM
		= TRANSFER_IO_STREAM | READER | WRITER | SAX | DOM;
	public static final int TRANSFER_TYPES = 2047;

	public static final int FILE_OUT = OUTPUT_STREAM | WRITER | FILE_WRITER;


	public static final IOFlavor INVALID = new IOFlavor
		(MimeType.INVALID, 0, null);

	public static final IOFlavor FS_FLAVOR = new IOFlavor
		(FileSystem.MIME_TYPE, OBJECT, FileSystem.class);

	public static final IOFlavor XML_FLAVOR = new IOFlavor
		(MimeType.TEXT_XML, SAX, null);

	/**
	 * The IOFlavor type <code>SCI_FLAVOR</code> indicates greenscilab files
	 */
	public static final IOFlavor SCI_FLAVOR = new IOFlavor(MimeType.TEXT_SCI, READER, null);	
	
	public static final IOFlavor PROJECT_LOADER
		= IOFlavor.valueOf (ProjectLoader.class);

	public static final IOFlavor RESOURCE_LOADER
		= IOFlavor.valueOf (ResourceLoader.class);

	public static final IOFlavor REGISTRY_LOADER
		= IOFlavor.valueOf (RegistryLoader.class);

	public static final IOFlavor GRAPH_LOADER
		= IOFlavor.valueOf (GraphLoader.class);

	public static final IOFlavor REGISTRY
		= IOFlavor.valueOf (de.grogra.pf.registry.Registry.class);

	public static final IOFlavor NODE
		= IOFlavor.valueOf (de.grogra.graph.impl.Node.class);

	
	private final MimeType mimeType;
	private final int features;
	private final Class objectClass;


	public IOFlavor (MimeType javaMimeType)
	{
		this (javaMimeType, OBJECT, javaMimeType.getRepresentationClass ());
	}


	public IOFlavor (MimeType mimeType, int features, Class objectClass)
	{
		mimeType.getClass ();
		this.mimeType = mimeType;
		this.features = features;
		this.objectClass = objectClass;
	}


	public MimeType getMimeType ()
	{
		return mimeType;
	}


	public boolean isInputStreamSupported ()
	{
		return (features & INPUT_STREAM) != 0;
	}


	public boolean isOutputStreamSupported ()
	{
		return (features & OUTPUT_STREAM) != 0;
	}


	public boolean isReaderSupported ()
	{
		return (features & READER) != 0;
	}


	public boolean isWriterSupported ()
	{
		return (features & WRITER) != 0;
	}


	public boolean isSAXSupported ()
	{
		return (features & SAX) != 0;
	}


	public boolean isDOMSupported ()
	{
		return (features & DOM) != 0;
	}


	public boolean isFileReaderSupported ()
	{
		return (features & FILE_READER) != 0;
	}


	public boolean isFileWriterSupported ()
	{
		return (features & FILE_WRITER) != 0;
	}


	public boolean isObjectSupported ()
	{
		return (features & OBJECT) != 0;
	}


	public boolean isVirtualFileReaderSupported ()
	{
		return (features & VFILE_READER) != 0;
	}


	public boolean isVirtualFileWriterSupported ()
	{
		return (features & VFILE_WRITER) != 0;
	}


	public Class getObjectClass ()
	{
		return objectClass;
	}


	public boolean isAssignableFrom (IOFlavor f)
	{
		return (((features & f.features & TRANSFER_TYPES) != 0)
				|| (((features & (TRANSFER_STREAM | FILE_WRITER | VFILE_WRITER)) != 0)
					&& ((f.features & TRANSFER_STREAM) != 0))
				|| (((features & TRANSFER_STREAM) != 0)
					&& ((f.features & (TRANSFER_STREAM | FILE_READER)) != 0)))
			&& mimeType.isAssignableFrom (f.mimeType)
			&& (((features & OBJECT) == 0) || (objectClass == null)
				|| objectClass.isAssignableFrom (f.objectClass));
	}


	FilterSource createAdapter (FilterSource s)
	{
		IOFlavor f = s.getFlavor ();
		if ((features & f.features & TRANSFER_TYPES) != 0)
		{
			return s;
		}
		if (((features & (TRANSFER_STREAM | FILE_WRITER | VFILE_WRITER)) != 0)
			&& ((f.features & TRANSFER_STREAM) != 0))
		{
			return new StreamAdapter (s, this);
		}
		if (((features & TRANSFER_STREAM) != 0)
			&& ((f.features & (TRANSFER_STREAM | FILE_READER)) != 0))
		{
			return new StreamAdapter (s, this);
		}
		throw new IllegalArgumentException ("Can't adapt " + s
											+ " to " + this); 
	}


	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer (mimeType.toString ());
		b.append ("; io=");
		int m = 1;
		boolean first = true;
		for (int i = 0; i < FEATURE_NAMES.length; i++)
		{
			if ((features & m) != 0)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					b.append (",");
				}
				b.append (FEATURE_NAMES[i]);
			}
			m <<= 1;
		}
		return Utils.quote (b);
	}


	public static IOFlavor valueOf (String s)
	{
		return valueOf (MimeType.valueOf (Utils.unquote (s)));
	}


	public static IOFlavor valueOf (Class cls)
	{
		return new IOFlavor (MimeType.valueOf (cls), OBJECT, cls);
	}


	public static IOFlavor valueOf (MimeType t)
	{
		StringMap p = t.getParameters ();
		String f = (String) p.get ("io");
		p.remove ("io");
		int fm = 0;
		int m = 1;
		for (int i = 0; i < FEATURE_NAMES.length; i++)
		{
			int pos = f.indexOf (FEATURE_NAMES[i]);
			if (pos >= 0)
			{
				if ((pos == 0) || !Character.isLetter (f.charAt (pos - 1)))
				{
					pos += FEATURE_NAMES[i].length ();
					if ((pos == f.length ()) || !Character.isLetter (f.charAt (pos)))
					{
						fm |= m;
					}
				}
			}
			m <<= 1;
		}
		return new IOFlavor
			(new MimeType (t.getMediaType (), p, t.getRepresentationClass ()),
			 fm, t.getRepresentationClass ());
	}


	public boolean isWritableTo (IOFlavor dest)
	{
		return IO.isWritableTo (this, dest);
	}


	public boolean isWritableTo (MimeType dest)
	{
		return isWritableTo
			(new IOFlavor (dest, IOFlavor.OUTPUT_STREAM | IOFlavor.WRITER,
			 null));
	}


	@Override
	public boolean equals (Object o)
	{
		IOFlavor f;
		return (o == this)
			|| ((o instanceof IOFlavor)
				&& ((f = (IOFlavor) o).features == features)
				&& Utils.equal (f.objectClass, objectClass)
				&& f.mimeType.equals (mimeType));
	}


	@Override
	public int hashCode ()
	{
		return features ^ mimeType.hashCode ();
	}

}
