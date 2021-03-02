
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

package de.grogra.imp3d.io;

import java.io.*;
import java.util.HashSet;
import java.util.HashMap;
import de.grogra.util.*;
import de.grogra.graph.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.objects.SceneTree.*;
import de.grogra.pf.io.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.registry.*;
import de.grogra.vfs.*;
import de.grogra.xl.util.ObjectList;

/**
 * This base class is used to export the 3D scene graph of a
 * {@link de.grogra.imp3d.View3D} to another representation (usually, this
 * is a file representation for which this class provides a set of
 * useful methods). The export is done via the creation of an
 * intermediate {@link de.grogra.imp3d.objects.SceneTree} in the method
 * {@link #createSceneTree(View3D)}.
 * 
 * @author Ole Kniemeyer
 */
public abstract class SceneGraphExport extends FilterBase
	implements SceneTree.Visitor
{
	/**
	 * This interface is used to export a leaf of the
	 * {@link SceneTree} to the destination.
	 * 
	 * @author Ole Kniemeyer
	 */
	public interface NodeExport
	{
		/**
		 * Exports the leaf <code>node</code> to the destination. If
		 * <code>transform</code> is non-<code>null</code>, this means that
		 * <code>node</code> is the only child of <code>transform</code>
		 * and the transformation of <code>transform</code> has to be
		 * exported together with the <code>node</code>.
		 * 
		 * @param node a leaf of the scene tree
		 * @param transform transformation of node
		 * @param sge invoking export instance
		 * 
		 * @throws IOException
		 */
		void export (Leaf node, InnerNode transform,
					 SceneGraphExport sge) throws IOException;
	}


	/**
	 * This interface is used to export an object (as an attribute of
	 * a node of the scene tree) to the destination.
	 * 
	 * @author Ole Kniemeyer
	 */
	public interface ObjectExport
	{
		/**
		 * Exports <code>object</code> to the destination.
		 * 
		 * @param object some attribute value of the scene tree
		 * @param sge invoking export instance
		 * 
		 * @throws IOException
		 */
		void export (Object object, SceneGraphExport sge) throws IOException;
	}


	/**
	 * The list of files which have been created by this export.
	 */
	protected ObjectList files = new ObjectList ();

	private View3D view;
	
	private FileSystem fileSystem;
	private Object directory;

	/**
	 * This set contains the names of files which have been created
	 * during this export in the method {@link #getFile(String)}. 
	 */
	private HashSet createdFiles = new HashSet ();
	private HashMap fileItemFiles = new HashMap ();

	/**
	 * This map is used to look up exports for given classes.
	 */
	private final LookupForClass exports;

	
	/**
	 * Delegates to {@link FilterBase#FilterBase(FilterItem, FilterSource)}.
	 * The source is expected to be an {@link ObjectSource} representing
	 * an instance of {@link View3D}. 
	 * 
	 * @param item the defining <code>FilterItem</code>
	 * @param source the data source
	 */
	public SceneGraphExport (FilterItem item, FilterSource source)
	{
		super (item, source);
		exports = new LookupForClass (item);
	}


	/**
	 * This method returns the file system to use for the export.
	 * On first invocation, the file system is obtained from
	 * {@link #initFileSystem()}.
	 * 
	 * @return file system to use for export
	 *
	 * @throws IOException
	 */
	public FileSystem getFileSystem () throws IOException
	{
		if (fileSystem == null)
		{
			fileSystem = initFileSystem ();
		}
		return fileSystem;
	}


	/**
	 * This method returns the base directory within
	 * {@link #getFileSystem()} to use for the export.
	 * On first invocation, the directory is obtained from
	 * {@link #initDirectory()}.
	 * 
	 * @return directory to use for export
	 *
	 * @throws IOException
	 */
	public Object getDirectory () throws IOException
	{
		if (directory == null)
		{
			getFileSystem ();
			directory = initDirectory ();
		}
		return directory;
	}


	/**
	 * Determines the file system to use. This method is invoked
	 * once by {@link #getFileSystem()}.
	 * 
	 * @return file system to use
	 * 
	 * @throws IOException
	 */
	protected FileSystem initFileSystem () throws IOException
	{
		return null;
	}


	/**
	 * Determines the directory to use. This method is invoked
	 * once by {@link #getDirectory()}.
	 * 
	 * @return directory to use
	 * 
	 * @throws IOException
	 */
	protected Object initDirectory () throws IOException
	{
		return null;
	}


	/**
	 * This method creates a file in {@link #getDirectory()}. The file
	 * name is based ob <code>name</code>: E.g., if <code>name</code>
	 * is <code>test.png</code>, the actual name could be something like
	 * <code>test123.png</code>. It is ensured that no file will be returned
	 * twice during a single export.
	 * 
	 * @param name name for file
	 * @return file
	 *  
	 * @throws IOException
	 */
	public Object getFile (String name) throws IOException
	{
		int i = name.lastIndexOf ('.');
		String base = (i < 0) ? name : name.substring (0, i);
		String ext = (i < 0) ? "" : name.substring (i);
		i = 0;
		while (!createdFiles.add (name))
		{
			name = base + ++i + ext;
		}
		return getFileSystem ()
			.create (getDirectory (), name, false, false);
	}


	/**
	 * Returns the path of a file of this export's file system
	 * ({@link #getFileSystem()}) as a string.
	 * 
	 * @param file file of this export's file system
	 * @return path of <code>file</code>
	 * 
	 * @throws IOException
	 */
	public String getPath (Object file) throws IOException
	{
		return getFileSystem ().getPath (file);
	}


	public OutputStream getOutputStream (Object file) throws IOException
	{
		return getFileSystem ().getOutputStream (file, false);
	}


	public String getPath (FileObjectItem file) throws IOException
	{
		FileSystem fs = getFileSystem ();
		Object f = fileItemFiles.get (file);
		if (f == null)
		{
			FileSource s = file.createFileSource ();
			FileSystem sfs = s.getFileSystem ();
			if ((fs instanceof LocalFileSystem)
				&& (sfs instanceof LocalFileSystem))
			{
				f = s.getInputFile ();
			}
			else if (sfs == fs)
			{
				f = s.getFile ();
			}
			else
			{
				f = getFile (sfs.getName (s.getFile ()));
				sfs.copyFileTo (s.getFile (), fs, f);
			}
			if (f instanceof File)
			{
				f = ((File) f).getAbsoluteFile ();
			}
			fileItemFiles.put (file, f);
		}
		return (f instanceof File)
			? Utils.relativize (((File) fs.getRoot ()).getAbsoluteFile (),
								(File) f).getPath ()
			: fs.getPath (f); 
	}


	/**
	 * The implementation of this method creates the scene tree which is
	 * to be exported by this export.
	 * 
	 * @param scene the view which defines the scene to export
	 * @return scene tree for the view
	 */
	protected abstract SceneTree createSceneTree (View3D scene);


	/**
	 * Returns the view which defines the scene to export. This requires
	 * that the source of this export is an {@link ObjectSource} representing
	 * an instance of <code>View3D</code>. 
	 * 
	 * @return view
	 * 
	 * @throws IOException
	 */
	public View3D getView () throws IOException
	{
		if (view == null)
		{
			view = (View3D) ((ObjectSource) source).getObject ();
		}
		return view;
	}


	/**
	 * Returns the graph state which is used for export. This is the
	 * graph state of the view's workbench.
	 * 
	 * @return graph state for export
	 */
	public GraphState getGraphState ()
	{
		return view.getWorkbenchGraphState ();
	}


	/**
	 * Returns the list of files which have been created during export.
	 * 
	 * @return created files
	 */
	public java.util.Collection getFiles ()
	{
		return files;
	}


	private InnerNode innerNodeOfLeaf;


	public void visitEnter (InnerNode node)
	{
		Node n = node.children;
		if (n != null)
		{
			if ((n.next == null) && (n instanceof Leaf))
			{
				innerNodeOfLeaf = node;
			}
			else
			{
				try
				{
					beginGroup (node);
				}
				catch (IOException e)
				{
					throw new WrapException (e);
				}
			}
		}
	}


	public void visitLeave (InnerNode node)
	{
		Node n = node.children;
		if (n != null)
		{
			if (!((n.next == null) && (n instanceof Leaf)))
			{
				try
				{
					endGroup (node);
				}
				catch (IOException e)
				{
					throw new WrapException (e);
				}
			}
		}
	}


	public void visit (Leaf leaf)
	{
		try
		{
			export (leaf, innerNodeOfLeaf);
			innerNodeOfLeaf = null;
		}
		catch (IOException e)
		{
			throw new WrapException (e);
		}
	}


	/**
	 * Exports the scene of the view. This method at first creates the
	 * scene tree using {@link #createSceneTree(View3D)}, then it lets this
	 * export instance visit the created scene tree. During visit, the
	 * methods {@link #beginGroup}, {@link #endGroup}
	 * and {@link #export(SceneTree.Leaf, SceneTree.InnerNode)} are invoked; these have to be
	 * implemented to perform the actual export to the destination.
	 * 
	 * @throws IOException
	 */
	protected void write () throws IOException
	{
		SceneTree t = createSceneTree (getView ());
		try
		{
			t.accept (this);
		}
		catch (WrapException e)
		{
			if (e.getCause () instanceof IOException)
			{
				throw (IOException) e.getCause ();
			}
			throw e;
		}
	}


	/**
	 * This method is invoked at the beginning of every inner node
	 * <code>group</code> which has more than one child. Its children
	 * will be processed until the corresponding method
	 * {@link #endGroup} is invoked. The export should write
	 * the hierarchy and transformation information of the <code>group</code>.
	 * 
	 * @param group the group
	 * 
	 * @throws IOException
	 */
	protected abstract void beginGroup (InnerNode group) throws IOException;


	/**
	 * This method is invoked at the end of every inner node
	 * <code>group</code> which has more than one child. Its children
	 * have been processed completely.
	 * 
	 * @param group the group
	 * 
	 * @throws IOException
	 * 
	 * @see #beginGroup
	 */
	protected abstract void endGroup (InnerNode group) throws IOException;


	/**
	 * This method is used to obtain an instance of <code>NodeExport</code>
	 * capable of exporting the object of a
	 * {@link de.grogra.imp3d.objects.SceneTree.Leaf}. If no such export
	 * is defined, <code>null</code> is returned.
	 * <p>
	 * This implementation looks for an export defined in the registry:
	 * As name, the name of the class of <code>object</code> is chosen,
	 * the export is searched as a child of {@link FilterBase#item}.
	 * 
	 * @param object object of the original scene graph
	 * @param asNode is object a node or an edge?
	 * @return suitable export for the object or <code>null</code> 
	 */
	public NodeExport getExportFor (Object object, boolean asNode)
	{
		return (NodeExport) exports.lookup (object.getClass ());
	}


	/**
	 * This method is used to obtain an instance of <code>ObjectExport</code>
	 * capable of exporting the <code>object</code> (which is some attribute
	 * value). If no such export is defined, <code>null</code> is returned.
	 * <p>
	 * This implementation looks for an export defined in the registry:
	 * As name, the name of the class of <code>object</code> is chosen,
	 * the export is searched as a child of {@link FilterBase#item}.
	 * 
	 * @param object object to export
	 * @return suitable export for the object or <code>null</code> 
	 */
	public ObjectExport getExportForObject (Object object)
	{
		return (ObjectExport) exports.lookup (object.getClass ());
	}

	/**
	 * This method is invoked for every leaf of the scene tree in
	 * order to export this leaf. If
	 * <code>transform</code> is non-<code>null</code>, this means that
	 * <code>node</code> is the only child of <code>transform</code>
	 * and the transformation of <code>transform</code> has to be
	 * exported together with the <code>node</code>.
	 * <p>
	 * This implementation obtains a {@link NodeExport} via
	 * {@link #getExportFor(Object, boolean)} and, if successful,
	 * uses this to export the leaf.
	 * 
	 * @param node a leaf of the scene tree
	 * @param transform transformation of node
	 * 
	 * @throws IOException
	 * 
	 * @see #beginGroup
	 * @see #endGroup
	 */
	protected void export (Leaf node, InnerNode transform) throws IOException
	{
		NodeExport ex = getExportFor (node.object, node.asNode);
		if (ex != null)
		{
			ex.export (node, transform, this);
		}
	}


	/**
	 * This method is invoked for some attribute value in
	 * order to export the value.
	 * <p>
	 * This implementation obtains an {@link ObjectExport} via
	 * {@link #getExportForObject(Object)} and, if successful,
	 * uses this to export the value.
	 * 
	 * @param object object to export
	 * 
	 * @throws IOException
	 */
	public boolean export (Object object) throws IOException
	{
		if (object != null)
		{
			ObjectExport ex = getExportForObject (object);
			if (ex != null)
			{
				ex.export (object, this);
				return true;
			}
		}
		return false;
	}

}
