
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

package de.grogra.vfs;

import java.io.*;
import java.util.*;
import java.net.*;
import de.grogra.util.StringMap;

public abstract class FileSystemBase extends FileSystem
{
	private final class Connection extends URLConnection
	{
		private final Object file;
		private final StringMap header;


		Connection (URL url, Object file)
		{
			super (url);
			this.file = file;
			String date = new java.util.Date (getTime (file)).toString ();
			header = new StringMap (4)
				.putObject ("date", date)
				.putObject ("last-modified", date)
				.putObject ("content-length", String.valueOf (getSize (file)));
			de.grogra.util.MimeType mt = getMimeType (file);
			if (mt != null)
			{
				header.putObject ("content-type", mt.getMediaType ());
			}
		}


		@Override
		public void connect ()
		{
			connected = true;
		}


		@Override
		public InputStream getInputStream () throws IOException
		{
			connect ();
			return FileSystemBase.this.getInputStream (file);
		}


		@Override
		public OutputStream getOutputStream () throws IOException
		{
			connect ();
			return FileSystemBase.this.getOutputStream (file, false);
		}


		@Override
		public String getHeaderFieldKey (int n)
		{
			return ((n >= 0) && (n < header.size ()))
				? header.getKeyAt (n) : null;
		}


		@Override
		public String getHeaderField (int n)
		{
			return ((n >= 0) && (n < header.size ()))
				? (String) header.getValueAt (n) : null;
		}


		@Override
		public String getHeaderField (String name)
		{
			return (String) header.get (name);
		}


		@Override
		public Map getHeaderFields ()
		{
			return header.toMap ();
		}
	}


	private final URLStreamHandler handler = new URLStreamHandler ()
	{
		@Override
		protected URLConnection openConnection (URL u) throws IOException
		{
			String path = u.getPath ();
			Object file;
			if (path.equals ("/"))
			{
				file = getRoot ();
			}
			else if (path.endsWith ("/"))
			{
				file = getFile (path.substring (1, path.length () - 1));
			}
			else
			{
				file = getFile (path.substring (1));
			}
			if (file == null)
			{
				throw new FileNotFoundException (u.toString ());
			}
			return new Connection (u, file);
		}
	};


	public FileSystemBase (String fsName, String protocol)
	{
		super (fsName, protocol);
	}

	
	@Override
	public URL toURL (Object file)
	{
		String path;
		if (getRoot ().equals (file))
		{
			path = "/";
		}
		else if (!isLeaf (file))
		{
			path = '/' + getPath (file) + '/';
		}
		else
		{
			path = '/' + getPath (file);
		}
		try
		{
			return new URL (getProtocol (), null, -1, path, handler);
		}
		catch (MalformedURLException e)
		{
			throw new AssertionError (e);
		}
	}
	
	
	public URLStreamHandler getURLStreamHandler ()
	{
		return handler;
	}


	@Override
	public Object toFile (URL url)
	{
		if (!getProtocol ().equals (url.getProtocol ()))
		{
			return null;
		}
		String p = url.getPath ();
		if ("/".equals (p))
		{
			return getRoot ();
		}
		int n = p.length () - 1;
		return getFile ((p.charAt (n) == '/') ? p.substring (1, n)
						: p.substring (1));
	}

}
