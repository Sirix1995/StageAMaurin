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

package de.grogra.mtg;

import java.io.File;
import java.io.IOException;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.icon.IconSource;
import de.grogra.imp3d.View3D;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.MimeTypeItem;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProgressMonitor;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.io.ResourceLoader;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.Showable;
import de.grogra.pf.ui.TextEditor;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.PanelFactory;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.rgg.model.Runtime;
import de.grogra.util.Lock;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.lang.ObjectToBoolean;

public class MTGSourceFile extends Item implements
		de.grogra.pf.ui.registry.UIItem, ObjectToBoolean,
		TreeModelListener, Showable
{
	public static class Loader extends FilterBase implements ObjectSource,
			ProjectLoader
	{

		Registry m_registry;
		
		public Loader (FilterItem item, FilterSource source)
		{
			super (item, source);
			setFlavor (IOFlavor.PROJECT_LOADER);
		}
		
		public Object getObject ()
		{
			return this;
		}	
		
		private Node rootNode = null;
		private MTGFilter filter = null;

		public void loadRegistry (Registry registry) throws IOException
		{
			this.m_registry = registry;
			
			File file = ((FileSource) source).getInputFile ();
			registry.initFileSystem (new LocalFileSystem (IO.PROJECT_FS, file.getParentFile ()));		
			registry.setEmptyGraph ();

			filter = new MTGFilter(item, source);
			rootNode = (Node)filter.getObject();
			

			// this part is only to open the text editor 
			// and to load mtg-source code  
			final MTGSourceFile sf = new MTGSourceFile (
				IO.toSystemId (registry.getFileSystem (), file), 
				new MimeType ("text/mtg", null));
			sf.setMTGFilter (filter);
			registry.getDirectory ("/project/objects/files", null).addUserItem (sf);
			Workbench wb = Workbench.get (registry);
			if (wb != null)
			{
				Item i = item.getItem ("layout");
				if (i != null)
				{
					i = i.resolveLink (registry);
				}
				if (i != null)
				{
					wb.setProperty (Workbench.INITIAL_LAYOUT, i.getAbsoluteName ());
					sf.showLater (wb);
				}
			}
			
		}
		
		public void loadGraph (Registry registry) throws IOException
		{
			((Node) registry.getProjectGraph ().getRoot (Graph.MAIN_GRAPH)).appendBranchNode (rootNode);
			
			//Some functionalities (e.g. the console) retrieve the graph via the context thread referenced to by the runtime instance.
			//Hence it is necessary to point the Runtime instance's "current graph" to the project graph in the registry loaded.
			Runtime.INSTANCE.setCurrentGraph (registry.getProjectGraph ());
		}
		
	}

	private MTGFilter mtgFilter = null;
	
	MimeType mimeType;
	//enh:field

	boolean editable = true;
	//enh:field getter
	
	boolean disabled = false;
	//enh:field getter setter

	transient String deactivationCategory;
	transient int activationStamp = -1;

	private MTGSourceFile ()
	{
		this (null, null);
	}
	
	protected void setMTGFilter(MTGFilter filter) {
		mtgFilter = filter;
	}

	public MTGSourceFile (String key, MimeType mimeType)
	{
		super (key);
		this.mimeType = mimeType;
	}

	public FileSource toFileSource ()
	{
		return FileSource.createFileSource (getName (), mimeType, this, new StringMap (this));
	}
	
	public MimeType getMimeType ()
	{
		return mimeType;
	}
	
	public MimeTypeItem getMimeTypeItem ()
	{
		return MimeTypeItem.get (this, mimeType);
	}

	@Override
	protected void activateImpl ()
	{
		if (disabled)
		{
			activationStamp = -1;
			return;
		}
		Object f = getRegistry ().getProjectFile (getName ());
		if (f != null)
		{
			getRegistry ().addFileSystemListener (this);
			getRegistry ().getFileSystem ().setMimeType (f, mimeType);
		}
		FileSource fs = toFileSource ();
		FilterSource s = IO.createPipeline (fs, IOFlavor.RESOURCE_LOADER);
		if (!(s instanceof ObjectSource))
		{
			return;
		}
		s.initProgressMonitor (UI.createProgressAdapter (Workbench.get (this)));
		try
		{
			final ResourceLoader rl = (ResourceLoader) ((ObjectSource) s)
				.getObject ();
			deactivationCategory = rl.getJoinedDeactivationCategory ();
			if (activationStamp != getRegistry ().getActivationStamp ())
			{
				activationStamp = getRegistry ().getActivationStamp ();
				((Item) getAxisParent ()).forAll (null, null, new ItemVisitor ()
				{
					public void visit (Item item, Object info)
					{
						if (!item.isActivated () && (item instanceof MTGSourceFile)
							 && !((MTGSourceFile) item).disabled
							 && (((MTGSourceFile) item).activationStamp != activationStamp)
							 && rl.addResource (((MTGSourceFile) item).toFileSource ()))
						{
							((MTGSourceFile) item).activationStamp = activationStamp;
						}
					}
				}, null, false);
				rl.loadResource (getRegistry ());
			}
		}
		catch (Exception e)
		{
			Workbench.get (this).logGUIInfo (
				IO.I18N.msg ("openfile.failed", fs.getSystemId ()), e);
		}
		finally
		{
			s.setProgress (null, ProgressMonitor.DONE_PROGRESS);
		}
	}

	@Override
	protected void deactivateImpl ()
	{
		getRegistry ().removeFileSystemListener (this);
	}

	@Override
	protected Object getDescriptionImpl (String type)
	{
		if (NAME.equals (type))
		{
			return IO.toPath (getName ());
		}
		Object d = super.getDescriptionImpl (type);
		return ICON.equals (type) ? UI.getIcon (getName (), mimeType
			.getMediaType (), (IconSource) d, this, true) : d;
	}

	public Object invoke (Context ctx, String method, Object arg)
	{
		return null;
	}

	public void show (Context ctx)
	{
		show (ctx, null);
	}

	public void show (Context ctx, String ref)
	{
		MimeTypeItem m = getMimeTypeItem ();
		if (m != null)
		{
			if (editable && m.isEditable ())
			{
				Panel[] p = ctx.getWindow ().getPanels (this);
				TextEditor t;
				for (int i = 0; i < p.length; i++)
				{
					t = (TextEditor) p[i];
					if (Utils.isContained (getName (), t.getDocuments ()))
					{
						t.openDocument (getName (), ref);
						t.show (false, null);
						return;
					}
				}
				if (p.length > 0)
				{
					t = (TextEditor) p[0];
				}
				else
				{
					t = (TextEditor) PanelFactory.createPanel (ctx,
						"/ui/panels/texteditor", null);
				}
				t.openDocument (getName (), ref);
				t.show (false, null);
			}
			else if (m.isViewable ())
			{
				ctx.getWorkbench ().showViewerPanel (getName (), getName (),
					this);
			}
		}
	}

	public void showLater (Context ctx)
	{
		UI.getJobManager (ctx).runLater (new Command ()
		{
			public void run (Object arg, Context ctx)
			{
				show (ctx, null);
			}

			public String getCommandName ()
			{
				return null;
			}
		}, null, ctx, JobManager.ACTION_FLAGS);
	}

	public boolean isAvailable (Context ctx)
	{
		return true;
	}

	public boolean isEnabled (Context ctx)
	{
		return true;
	}

	public int getUINodeType ()
	{
		return de.grogra.pf.ui.tree.UINodeHandler.NT_SPECIAL;
	}

	public boolean evaluateBoolean (Object o)
	{
		return o instanceof TextEditor;
	}

	@Override
	public void addRequiredFiles (java.util.Collection list)
	{
		Object f = getRegistry ().getProjectFile (getName ());
		if (f != null)
		{
			list.add (f);
		}
	}

	public static MTGSourceFile get (RegistryContext ctx, String systemId)
	{
		Item dir = ctx.getRegistry ().getItem ("/project/objects/files");
		return (dir != null) ? (MTGSourceFile) dir.getItem (systemId) : null;
	}

	public void treeNodesInserted (TreeModelEvent e)
	{
	}

	public void treeNodesRemoved (TreeModelEvent e)
	{
	}

	public void treeNodesChanged (TreeModelEvent e)
	{
		if (FileSystem.isContainedInChildren (getRegistry ().getProjectFile (
			getName ()), e))
		{

			refresh ((Item) getAxisParent (), this, deactivationCategory, new Command ()
				 {
					public String getCommandName ()
					{
						return null;
					}

					public void run (Object info, Context context)
					{

						try
						{
							Node rootNode = null;
							if(mtgFilter==null) {
								// this is the case if a gsz project is opened with an included mtg file
								// then the mtg file is not loaded and so the mtgFilter is not set 
								FileSource fs = toFileSource ();
								mtgFilter = new MTGFilter(fs);
								if(mtgFilter==null) return;
								rootNode = (Node)mtgFilter.getObject (fs);							
							} 
							rootNode = (Node)mtgFilter.getObject ();
							if(rootNode!=null) {
								getRegistry().setEmptyGraph ();
								((Node) getRegistry().getProjectGraph ().getRoot (Graph.MAIN_GRAPH)).appendBranchNode (rootNode);
								
								View3D p = View3D.getDefaultView (context);
								if (p != null) {
									//p.move (0, 0); // also works
									p.repaint();
								}								
							}
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						
						if (Utils.getBoolean (UI.getOptions (context), "saveProjectOnFileModification"))
						{
							context.getWorkbench ().save (false);
						}
					}
				 });
			// interesting: this command opens a window with an text area including the src coude of 
			// the with this source file associated file.
			//(Workbench.get (this)).showViewerPanel (getName (), getName (),	this);
		}
	}

	public void treeStructureChanged (TreeModelEvent e)
	{
	}


	public static void refresh (final Item dir, final MTGSourceFile file, final String category, final Command afterRefresh)
	{
		class Deactivator extends LockProtectedCommand implements
				ItemVisitor
		{
			Deactivator (Workbench w)
			{
				super (w.getRegistry ().getProjectGraph (), true,
					JobManager.ACTION_FLAGS);
			}

			@Override
			public String getCommandName ()
			{
				return null;
			}

			@Override
			protected void runImpl (Object info, Context context, Lock lock)
			{
				
				dir.forAll (null, null, this, null, false);
				dir.getRegistry ().activateItems ();
				if (afterRefresh != null)
				{
					UI.getJobManager (context).runLater (afterRefresh, info, context, JobManager.UPDATE_FLAGS);
				}
			}

			public void visit (Item item, Object info)
			{
				if (!(item.isActivated () && (item instanceof MTGSourceFile)))
				{
					return;
				}
				if ((item == file)
					|| (((MTGSourceFile) item).activationStamp == -1)
					|| ((category != null)
						&& (item instanceof MTGSourceFile) && category
						.equals (((MTGSourceFile) item).deactivationCategory)))
				{
					item.deactivate ();
				}
			}
		}

		Workbench w = Workbench.get (dir);
		w.getJobManager ().runLater (new Deactivator (w), null, w, JobManager.ACTION_FLAGS);
	}


	//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field mimeType$FIELD;
	public static final NType.Field editable$FIELD;
	public static final NType.Field disabled$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MTGSourceFile.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((MTGSourceFile) o).editable = (boolean) value;
					return;
				case 2:
					((MTGSourceFile) o).disabled = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((MTGSourceFile) o).isEditable ();
				case 2:
					return ((MTGSourceFile) o).isDisabled ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((MTGSourceFile) o).mimeType = (MimeType) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((MTGSourceFile) o).mimeType;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new MTGSourceFile ());
		$TYPE.addManagedField (mimeType$FIELD = new _Field ("mimeType", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MimeType.class), null, 0));
		$TYPE.addManagedField (editable$FIELD = new _Field ("editable", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
		$TYPE.addManagedField (disabled$FIELD = new _Field ("disabled", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new MTGSourceFile ();
	}

	public boolean isEditable ()
	{
		return editable;
	}

	public boolean isDisabled ()
	{
		return disabled;
	}

	public void setDisabled (boolean value)
	{
		this.disabled = (boolean) value;
	}

//enh:end

}
