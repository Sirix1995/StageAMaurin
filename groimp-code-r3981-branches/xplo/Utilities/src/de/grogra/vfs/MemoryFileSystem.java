
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
import javax.swing.event.TreeModelEvent;
import de.grogra.util.*;

public class MemoryFileSystem extends FileSystemBase
{

	private class Entry extends ByteArrayOutputStream
	{
		final Entry parent;
		final String name;
		final ArrayList files;
		long time = System.currentTimeMillis ();
		byte[] bufForInput = Utils.BYTE_0;
		int bufForInputCount = 0;


		Entry (Entry parent, String name, boolean isDirectory)
		{
			super (isDirectory ? 0 : 128);
			this.parent = parent;
			this.name = name;
			this.files = isDirectory ? new ArrayList () : null;
		}


		Entry getFile (String name)
		{
			for (int i = files.size () - 1; i >= 0; i--)
			{
				if (((Entry) files.get (i)).name.equals (name))
				{
					return (Entry) files.get (i);	
				}
			}
			return null;
		}


		Entry createFile (String name, boolean createDirectory)
		{
			Entry e = new Entry (this, name, createDirectory);
			files.add (e);
			return e;
		}


		void reset (boolean append)
		{
			reset ();
			buf = new byte[Math.max (256, append ? bufForInputCount : (bufForInputCount >> 1))];
			if (append)
			{
				write (bufForInput, 0, bufForInputCount);
			}
		}

		
		@Override
		public void flush ()
		{
		}
		
		@Override
		public void close ()
		{
			synchronized (MemoryFileSystem.this)
			{
				time = System.currentTimeMillis ();
				bufForInput = buf;
				bufForInputCount = count;
			}
	    	fireTreeModelEvent (NODES_CHANGED, getEventFor (this));
		}
		
		/**
		 * close file without triggering event
		 */
		public void closeQuiet()
		{
			synchronized (MemoryFileSystem.this)
			{
				time = System.currentTimeMillis ();
				bufForInput = buf;
				bufForInputCount = count;
			}
		}
	}


	final Entry root;


	public MemoryFileSystem (String fsName)
	{
		super (fsName, "memfs");
		root = new Entry (null, "", true);	
	}


	@Override
	public boolean isPersistent ()
	{
		return false;
	}


	@Override
	public void delete (Object file)
	{
		TreeModelEvent t;
		synchronized (this)
		{
			t = getEventFor (file);
			((Entry) file).parent.files.remove (file);
		}
		fireTreeModelEvent (NODES_REMOVED, t);
	}


	@Override
	public synchronized String getName (Object file)
	{
		return ((Entry) file).name;
	}


	@Override
	public synchronized Object getParent (Object file)
	{
		return ((Entry) file).parent;
	}


	public synchronized boolean isLeaf (Object file)
	{
		return ((Entry) file).files == null;
	}


	@Override
	public boolean isReadOnly (Object file)
	{
		return false;
	}


	@Override
	public synchronized long getTime (Object file)
	{
		return ((Entry) file).time;
	}

	/**
	 * close file without triggering event
	 */
	public void closeQuiet(Object file)
	{
		((Entry)file).closeQuiet();
	}

	@Override
	public synchronized void setTime (Object file, long time)
	{
		((Entry) file).time = time;
	}  


	@Override
	public synchronized long getSize (Object file)
	{
		return ((Entry) file).bufForInputCount;
	}


	@Override
	public Object getRoot ()
	{
		return root;
	}


	@Override
	public synchronized Object[] listFiles (Object parent)
	{
		return (((Entry) parent).files == null) ? null
			: ((Entry) parent).files.toArray ();
	}


	@Override
	public synchronized Object getFile (Object parent, String name)
	{
		return ((Entry) parent).getFile (name);
	}


	@Override
	protected Object createImpl (Object parent, String name, boolean createDirectory)
		throws IOException
	{
		Entry e = ((Entry) parent).getFile (name);
		if (e != null)
		{
			if (createDirectory != (e.files != null))
			{
				throw new IOException ("File " + e + " exists.");
			}
		}
		else
		{
			return getEventFor (((Entry) parent).createFile (name, createDirectory));
		}
		return e;		
	}


	@Override
	public synchronized InputStream getInputStream (Object file)
	{
		return new ByteArrayInputStream
			(((Entry) file).bufForInput, 0, ((Entry) file).bufForInputCount);
	}


	@Override
	public synchronized OutputStream getOutputStream (Object file, boolean append)
	{
		((Entry) file).reset (append);
		return (Entry) file;
	}


	public void removeNonlistedFiles (Collection list)
	{
		removeNonlistedFiles (getRoot (), list);
	}


	private boolean removeNonlistedFiles (Object file, Collection list)
	{
		boolean b;
		if (!isLeaf (file))
		{
			if (equals (file, getRoot ()))
			{
				b = true;
			}
			else
			{
				b = false;
				Object[] files = listFiles (file);
				for (int i = 0; i < files.length; i++)
				{
					if (removeNonlistedFiles (files[i], list))
					{
						b = true;
						break;
					}
				}
			}
		}
		else
		{
			b = list.contains (file);
		}
		if (!b)
		{
			delete (file);
			String n = getManifestName (file);
			if (n.length () > 0)
			{
				manifest.getEntries ().remove (n);
			}
		}
		return b;
	}

}
