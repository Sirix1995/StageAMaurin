
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
import java.lang.ref.WeakReference;
import java.net.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public final class LocalFileSystem extends FileSystem
{
	public static final LocalFileSystem FILE_ADAPTER
		= new LocalFileSystem (null, null);

	private static class Listener implements TreeModelListener
	{
		private final WeakReference ref;
		

		Listener (LocalFileSystem fs)
		{
			ref = new WeakReference (fs);
		}

		
		private void handle (int type, TreeModelEvent e)
		{
			LocalFileSystem fs = (LocalFileSystem) ref.get ();
			if (fs == null)
			{
				FILE_ADAPTER.removeTreeModelListener (this);
			}
			else
			{
				TreePath p = e.getTreePath ();
				while (p != null)
				{
					if (fs.equals (fs.getRoot (), p.getLastPathComponent ()))
					{
						fs.fireTreeModelEvent (type, new TreeModelEvent
							(fs, fs.getTreePath (e.getTreePath ()
												 .getLastPathComponent ()),
							 e.getChildIndices (), e.getChildren ()));
						return;
					}
					p = p.getParentPath ();
				}
			}
		}

		
		public void treeNodesInserted (TreeModelEvent e)
		{
			handle (NODES_INSERTED, e);
		}

		
		public void treeNodesRemoved (TreeModelEvent e)
		{
			handle (NODES_REMOVED, e);
		}

		
		public void treeNodesChanged (TreeModelEvent e)
		{
			handle (NODES_CHANGED, e);
		}

		
		public void treeStructureChanged (TreeModelEvent e)
		{
			handle (STRUCTURE_CHANGED, e);
		}

	}


	protected final File root;


	public LocalFileSystem (String fsName, File root)
	{
		super (fsName, "file");
		this.root = (root != null) ? root.getAbsoluteFile () : null;
		if (root != null)
		{
			FILE_ADAPTER.addTreeModelListener (new Listener (this));
		}
	}
	
	
	@Override
	public boolean isPersistent ()
	{
		return true;
	}


	@Override
	protected Object createImpl (Object parent, String name, boolean createDirectory)
		throws IOException
	{
		File f = new File ((File) parent, name);
		if (createDirectory)
		{
			if (!f.isDirectory ())
			{
				if (f.mkdir ())
				{
					return getEventFor (f);
				}
				else
				{
					throw new IOException ("Directory " + f
										   + " could not be created.");
				}
			}
		}
		else if (!f.exists ())
		{
			f.createNewFile ();
			return getEventFor (f);
		}
		return f;
	}


	@Override
	protected Object fireInserted (Object createImplRet)
	{
		if (createImplRet instanceof TreeModelEvent)
		{
			FILE_ADAPTER.fireTreeModelEvent
				(NODES_INSERTED, (TreeModelEvent) createImplRet);
			return getFile ((TreeModelEvent) createImplRet);
		}
		else
		{
			return createImplRet;
		}
	}


	@Override
	public void delete (Object file) throws IOException
	{
		TreeModelEvent e = getEventFor (file);
		if (!((File) file).delete ())
		{
			throw new IOException ("File " + file + " could not be deleted.");
		}
		FILE_ADAPTER.fireTreeModelEvent (NODES_REMOVED, e);
	}


	@Override
	public String getName (Object file)
	{
		return ((File) file).getName ();
	}


	@Override
	public URL toURL (Object file)
	{
		return de.grogra.util.Utils.fileToURL ((File) file);
	}


	@Override
	public Object toFile (URL url)
	{
		if (!"file".equals (url.getProtocol ()))
		{
			return null;
		}
		File f = de.grogra.util.Utils.urlToFile (url).getAbsoluteFile ();
		for (File t = f; t != null; t = t.getParentFile ())
		{
			if (equals (t, root))
			{
				return f;
			}
		}
		return null;
	}


	@Override
	public Object getParent (Object file)
	{
		return ((root == null) || equals (file, root)) ? null
			: ((File) file).getParentFile ();
	}


	@Override
	public Object getRoot ()
	{
		return root;
	}


	@Override
	public Object[] listFiles (Object parent)
	{
		return ((File) parent).listFiles ();
	}


	@Override
	public Object getFile (Object parent, String name)
	{
		File f = new File ((File) parent, name);
		return f.exists () ? f : null;
	}


	public boolean isLeaf (Object file)
	{
		return !((File) file).isDirectory ();
	}


	@Override
	public boolean isReadOnly (Object file)
	{
		return !((File) file).canWrite ();
	}


	@Override
	public long getTime (Object file)
	{
		return ((File) file).lastModified ();
	}


	@Override
	public void setTime (Object file, long time)
	{
		((File) file).setLastModified (time);
	}  


	@Override
	public long getSize (Object file)
	{
		return ((File) file).length ();
	}


	@Override
	public OutputStream getOutputStream (Object file, boolean append) throws IOException
	{
		return new BufferedOutputStream
			(new Out (new FileOutputStream ((File) file, append), file));
	}


	@Override
	public InputStream getInputStream (Object file) throws IOException
	{
		return new BufferedInputStream
			(new FileInputStream ((File) file));
	}


	@Override
	public Object addLocalFile (File source, Object targetDir, String name)
		throws IOException
	{
		File f = new File ((File) targetDir, name);
		return (f.getCanonicalPath ().equals (source.getCanonicalPath ())) ? f
			: super.addLocalFile (source, targetDir, name);
	}

}
