
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.util.CountingInputStream;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.ModifiableMap;
import de.grogra.vfs.FSFile;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;

public class FileSource extends FilterSourceBase implements InputStreamSource,
	ReaderSource, FileReaderSource, VirtualFileReaderSource, CountingInputStream.Monitor
{
	private final FileSystem fs;
	private final Object file;
	private final String systemId;


	public FileSource (FileSystem fs, Object file, String systemId,
					   MimeType mimeType, Registry reg, ModifiableMap metaData)
	{
		super (new IOFlavor (mimeType,
							 IOFlavor.INPUT_STREAM | IOFlavor.READER | IOFlavor.VFILE_READER
							 | (file instanceof File ? IOFlavor.FILE_READER : 0),
							 null), reg, metaData);
		fs.getClass ();
		file.getClass ();
		this.fs = fs;
		this.file = file;
		this.systemId = systemId;
	}


	public FileSource (FileSystem fs, Object file, RegistryContext reg, ModifiableMap metaData)
	{
		this (fs, file, IO.toSystemId (fs, file), fs.getMimeType (file),
			  reg.getRegistry (), metaData);
	}


	public FileSource (File file, MimeType mimeType, Registry reg, ModifiableMap metaData)
	{
		this (LocalFileSystem.FILE_ADAPTER, file, IO.toSystemId (file),
			  mimeType, reg, metaData);
	}


	public FileSystem getFileSystem ()
	{
		return fs;
	}


	public Object getFile ()
	{
		return file;
	}


	public String getSystemId ()
	{
		return systemId;
	}


	public boolean isReadOnly ()
	{
		return fs.isReadOnly (file);
	}


	private long fileSize;
	private String progrText;


	public InputStream getInputStream () throws IOException
	{
		CountingInputStream cis
			= new CountingInputStream (fs.getInputStream (file));
		progrText = IO.I18N.msg ("progress.reading", fs.getName (file));
		fileSize = fs.getSize (file);
		cis.setMonitor (this, fileSize >> 10);
		return cis;
	}


	public long length ()
	{
		return fs.getSize (file);
	}


	public void bytesRead (long bytes)
	{
		if (useAutoProgress ())
		{
			setProgress0 (progrText, (float) bytes / fileSize);
		}
	}


	public void streamClosed ()
	{
		if (useAutoProgress ())
		{
			setProgress0 (IO.I18N.msg ("progress.done", fs.getName (file)),
						  ProgressMonitor.DONE_PROGRESS);
		}
	}


	public OutputStream getOutputStream (boolean append) throws IOException
	{
		return fs.getOutputStream (file, append);
	}


	public Writer getWriter (boolean append) throws IOException
	{
		return fs.getWriter (file, append);
	}


	public Reader getReader () throws IOException
	{
		return new InputStreamReader (getInputStream (), fs.getCharset (file));
	}


	public File getInputFile ()
	{
		return (File) file;
	}


	public StringBuffer readContent () throws IOException
	{
		Reader in = null;
		try
		{
			in = getReader ();
			StringBuffer b = new StringBuffer ();
			int c;
			while ((c = in.read ()) >= 0)
			{
				b.append ((char) c);
			}
			return b;
		}
		finally
		{
			if (in != null)
			{
				in.close ();
			}
		}
	}


	public URL toURL ()
	{
		String n = Registry.ALL_FILE_SYSTEMS.getFileSystemName (fs);
		if (n == null)
		{
			return fs.toURL (file);
		}
		return Registry.ALL_FILE_SYSTEMS.toURL
			(Registry.ALL_FILE_SYSTEMS.getFile (n + fs.getPathWithLeadingSlash (file)));
	}


	public static FileSource createFileSource
		(String systemId, MimeType mimeType, RegistryContext ctx, ModifiableMap metaData)
	{
		FSFile f = IO.toFile (ctx, systemId);
		return new FileSource
			(f.fileSystem, f.file,
			 systemId, mimeType, ctx.getRegistry (), metaData);
	}

}
