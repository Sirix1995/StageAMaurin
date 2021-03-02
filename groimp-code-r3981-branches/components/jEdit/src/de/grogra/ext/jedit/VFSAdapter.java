
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

package de.grogra.ext.jedit;

import java.awt.Component;
import java.io.*;
import java.text.*;
import java.util.Date;
import org.gjt.sp.jedit.io.*;
import de.grogra.vfs.*;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.pf.registry.Registry;

public class VFSAdapter extends VFS
{

	public static class Entry extends DirectoryEntry
	{
		public static DateFormat DATE_FORMAT = DateFormat.getInstance();

		private final long modified;


		public Entry (String protocol, FileSystem fs, Object file)
		{
			super (fs.getName (file), null, null,
				   fs.isLeaf (file) ? FILE : DIRECTORY,
				   fs.getSize (file), false);
			this.canRead = true;
			this.canWrite = !fs.isReadOnly (file);
			this.modified = fs.getTime (file);
			this.path = this.symlinkPath = this.deletePath
				= protocol + ':' + fs.getPathWithLeadingSlash (file);
		}


		@Override
		public String getExtendedAttribute (String name)
		{
			return name.equals (EA_MODIFIED)
				? DATE_FORMAT.format (new Date (modified))
				: super.getExtendedAttribute(name);
		}
	}


	private static VFSAdapter allProjects;

	public static synchronized VFSAdapter forAllProjects ()
	{
		if (allProjects == null)
		{
			allProjects
				= new VFSAdapter ("project", Registry.ALL_FILE_SYSTEMS, true);
		}
		return allProjects;
	}

	
	private final FileSystem fs;

	public VFSAdapter (String name, FileSystem fs, boolean lowLatency)
	{
		super (name, READ_CAP | WRITE_CAP | DELETE_CAP | MKDIR_CAP
			   | (lowLatency ? LOW_LATENCY_CAP : 0),
			   new String[] {EA_TYPE, EA_SIZE, EA_STATUS, EA_MODIFIED});
		this.fs = fs;
	}


	private Object getFileOrNull (String path)
	{
		String n = getName ();
		if ((path.length () > n.length ()) && path.startsWith (n)
			&& (path.charAt (n.length ()) == ':'))
		{
			path = path.substring (n.length () + 1);
		}
		return (path.length () == 0) ? fs.getRoot () : fs.getFile (path);
	}


	private Object getFile (String path) throws FileNotFoundException
	{
		Object f = getFileOrNull (path);
		if (f == null)
		{
			throw new FileNotFoundException (path);
		}
		return f;
	}


	private String getPath (Object file)
	{
		return getName () + ':' + fs.getPathWithLeadingSlash (file);
	}

	
	@Override
	public String getParentOfPath (String path)
	{
		String n = getName ();
		if (path.length () >= 2)
		{
			int i = path.lastIndexOf ('/', path.length () - 2);
			if (i > 0)
			{
				return path.substring (0, i + 1);
			}
		}
		return n + ":/";
	}


	@Override
	public String constructPath (String parent, String path)
	{
		return parent.endsWith ("/") ? parent + path
			: parent + '/' + path;
	}


	@Override
	public char getFileSeparator ()
	{
		return '/';
	}


	@Override
	public DirectoryEntry[] _listDirectory (Object session, String path,
											Component comp)
	{
		Object directory = getFileOrNull (path);
		if (directory == null)
		{
			return null;
		}
		Object[] list = fs.listFiles (directory);
		Entry[] a = new Entry[list.length];
		for (int i = 0; i < list.length; i++)
		{
			a[i] = new Entry (getName (), fs, list[i]);
		}
		return a;
	}


	@Override
	public DirectoryEntry _getDirectoryEntry (Object session, String path,
											  Component comp)
	{
		Object f = getFileOrNull (path);
		return (f != null) ? new Entry (getName (), fs, f) : null;
	}


	@Override
	public boolean _delete (Object session, String path, Component comp)
	{
		try
		{
			fs.delete (getFile (path));
		}
		catch (IOException e)
		{
			return false;
		}
		VFSManager.sendVFSUpdate (this, path, true);
		return true;
	}


	@Override
	public boolean _mkdir (Object session, String directory, Component comp)
	{
		String parent = getParentOfPath (directory);
		Object pf = getFileOrNull (parent);
		if (pf == null)
		{
			if (!_mkdir (session, parent, comp))
			{
				return false;
			}
			pf = getFileOrNull (parent);
			if (pf == null)
			{
				return false;
			}
		}

		int i = directory.lastIndexOf ('/', directory.length () - 2);
		try
		{
			fs.create (pf, directory.substring (i + 1), true);
		}
		catch (IOException e)
		{
			return false;
		}
		VFSManager.sendVFSUpdate (this, directory, true);
		return true;
	}


	@Override
	public InputStream _createInputStream
		(Object session, String path, boolean ignoreErrors, Component comp)
		throws IOException
	{
		try
		{
			return fs.getInputStream (getFile (path));
		}
		catch (IOException e)
		{
			if (ignoreErrors)
			{
				return null;
			}
			else
			{
				throw e;
			}
		}
	}


	@Override
	public OutputStream _createOutputStream
		(Object session, String path, Component comp) throws IOException
	{
		Object f = getFileOrNull (path);
		if (f == null)
		{
			f = fs.create (getFile (getParentOfPath (path)),
						   path.substring (path.lastIndexOf ('/') + 1),
						   false);
		}
		return fs.getOutputStream (f, false);
	}

	
	@Override
	public String getDefaultEncoding (String path)
	{
		Object f = getFileOrNull (path);
		return (f != null) ? fs.getCharset (f).name () : super.getDefaultEncoding (path);
	}


	@Override
	public void setEncoding (String path, String encoding)
	{
		Object f = getFileOrNull (path);
		if (f == null)
		{
			return;
		}
		MimeType t = fs.getMimeType (f);
		if ((t != null) && "text".equals (t.getPrimaryType ()))
		{
			StringMap m = t.getParameters ();
			m.put ("charset", encoding);
			t = new MimeType (t.getMediaType (), m, t.getRepresentationClass ());
			fs.setMimeType (f, t);
		}
	}

}
