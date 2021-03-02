
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
import java.util.jar.Attributes;

import javax.swing.event.*;

public class JoinedFileSystem extends FileSystemBase
{

	private static class Entry
	{
		final FileSystem fs;
		final Object root;
		final Object file;
		final String name;
		long time;

		Entry (FileSystem fs, Object file)
		{
			this.fs = fs;
			this.file = file;
			this.root = null;
			this.name = null;
		}

		Entry (FileSystem fs, String name, Object root)
		{
			this.fs = fs;
			this.file = null;
			this.root = root;
			this.name = name;
			this.time = System.currentTimeMillis ();
		}
		
		Object getFile ()
		{
			return (file != null) ? file : root;
		}
	}


	final Entry root;
	final HashMap<FileSystem,Entry> fsToEntry = new HashMap (10);
	final HashMap<String,Entry> nameToEntry = new HashMap (10);


	public JoinedFileSystem (String fsName, String protocol)
	{
		super (fsName, protocol);
		root = new Entry (null, null, null);	
	}


	@Override
	public boolean isPersistent ()
	{
		return false;
	}


	@Override
	public void delete (Object file) throws IOException
	{
		TreeModelEvent t = getEventFor (file);
		Entry e = (Entry) file;
		if (e.file == null)
		{
			throw new IOException ("Cannot delete " + e);
		}
		e.fs.delete (e.file);
		fireTreeModelEvent (NODES_REMOVED, t);
	}


	@Override
	public String getName (Object file)
	{
		Entry e = (Entry) file;
		return (e.file != null) ? e.fs.getName (e.file) : e.name;
	}


	@Override
	public Object getParent (Object file)
	{
		Entry e = (Entry) file;
		if (e.file != null)
		{
			Object p = e.fs.getParent (e.file);
			Entry r;
			synchronized (this)
			{
				r = fsToEntry.get (e.fs);
			}
			if (e.fs.equals (p, r.root))
			{
				return r;
			}
			else
			{
				return new Entry (e.fs, p);
			}
		}
		else
		{
			return (e.fs != null) ? root : null;
		}
	}


	public boolean isLeaf (Object file)
	{
		Entry e = (Entry) file;
		return (e.file != null) && e.fs.isLeaf (e.file);
	}


	@Override
	public boolean isReadOnly (Object file)
	{
		Entry e = (Entry) file;
		return (file == root)
			|| ((e.file != null) && e.fs.isReadOnly (e.file));
	}


	@Override
	public long getTime (Object file)
	{
		Entry e = (Entry) file;
		return (e.file != null) ? e.fs.getTime (e.file) : e.time;
	}


	@Override
	public void setTime (Object file, long time)
	{
		Entry e = (Entry) file;
		if (e.file != null)
		{
			e.fs.setTime (e.file, time);
		}
		else
		{
			e.time = time;
		}
	}  


	@Override
	public long getSize (Object file)
	{
		Entry e = (Entry) file;
		return (e.file != null) ? e.fs.getSize (e.file) : 0;
	}


	@Override
	public Object getRoot ()
	{
		return root;
	}


	@Override
	public Object[] listFiles (Object parent)
	{
		Entry e = (Entry) parent;
		if (e.fs != null)
		{
			Object[] list = e.fs.listFiles (e.getFile ());
			Entry[] a = new Entry[list.length];
			for (int i = 0; i < list.length; i++)
			{
				a[i] = new Entry (e.fs, list[i]);
			}
			return a;
		}
		else
		{
			synchronized (this)
			{
				return fsToEntry.values ().toArray ();
			}
		}
	}


	@Override
	public Object getFile (Object parent, String name)
	{
		Entry e = (Entry) parent;
		if (e.fs != null)
		{
			Object f = e.fs.getFile (e.getFile (), name);
			return (f != null) ? new Entry (e.fs, f) : null;
		}
		else
		{
			synchronized (this)
			{
				return nameToEntry.get (name);
			}
		}
	}


	@Override
	protected Object createImpl (Object parent, String name, boolean createDirectory)
		throws IOException
	{
		Entry e = (Entry) parent;
		if (e.fs != null)
		{
			return new Entry
				(e.fs, e.fs.create (e.getFile (), name, createDirectory));
		}
		else
		{
			throw new IOException ("Cannot create file in root directory");
		}
	}


	@Override
	public InputStream getInputStream (Object file) throws IOException
	{
		Entry e = (Entry) file;
		return e.fs.getInputStream (e.file);
	}


	@Override
	public OutputStream getOutputStream (Object file, boolean append) throws IOException
	{
		Entry e = (Entry) file;
		return new Out (e.fs.getOutputStream (e.file, append), file);
	}


	@Override
	public Attributes getAttributes (Object file, boolean create)
	{
		Entry e = (Entry) file;
		return e.fs.getAttributes (e.file, create);
	}


	public synchronized void addFileSystem (FileSystem fs, String name, Object root)
	{
		Entry e = new Entry (fs, name, root);
		Entry old = (Entry) nameToEntry.put (name, e);
		if (old != null)
		{
			fsToEntry.remove (old.fs);
		}
		fsToEntry.put (fs, e);
	}


	public synchronized void removeFileSystem (FileSystem fs)
	{
		Entry e = (Entry) fsToEntry.remove (fs);
		if (e != null)
		{
			nameToEntry.remove (e.name);
		}
	}


	public synchronized String getFileSystemName (FileSystem fs)
	{
		Entry e = (Entry) fsToEntry.get (fs);
		return (e != null) ? e.name : null;
	}

}
