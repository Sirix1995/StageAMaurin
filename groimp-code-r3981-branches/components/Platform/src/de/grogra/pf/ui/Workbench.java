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

package de.grogra.pf.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.filechooser.FileFilter;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.LogStore;
import de.grogra.persistence.Transaction;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FileTypeItem.Filter;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ObjectSourceImpl;
import de.grogra.pf.io.ProgressMonitor;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.registry.Directory;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.registry.Value;
import de.grogra.pf.ui.edit.ComponentSelection;
import de.grogra.pf.ui.edit.GraphSelectionImpl;
import de.grogra.pf.ui.edit.MapSource;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.registry.Layout;
import de.grogra.pf.ui.registry.PanelFactory;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.pf.ui.tree.RegistryAdapter;
import de.grogra.pf.ui.tree.TableMapper;
import de.grogra.pf.ui.tree.UISubTree;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.util.Configurable;
import de.grogra.util.ConfigurationSet;
import de.grogra.util.Described;
import de.grogra.util.DescribedLevel;
import de.grogra.util.DetailedException;
import de.grogra.util.Lock;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.MimeTypeFileFilter;
import de.grogra.util.StringMap;
import de.grogra.util.ThreadContext;
import de.grogra.util.UserException;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.xl.util.ObjectList;

public abstract class Workbench implements Context, RegistryContext,
		ClipboardOwner
{
	public static final Timer TIMER = new Timer (true);

	public static final String CURRENT_DIRECTORY = "current-dir";
	public static final String[] LAST_FILES = new String[]{
		"last-file-01","last-file-02","last-file-03",
		"last-file-04","last-file-05","last-file-06",
		"last-file-07","last-file-08","last-file-09","last-file-10"};

	public static final String START_AS_DEMO = "start-as-demo";
	public static final String INITIAL_LAYOUT = "initial-layout";

	public static final Level SOFT_GUI_INFO = new DescribedLevel (UI.I18N,
		"log.guiinfo", 850);

	public static final Level GUI_INFO = new DescribedLevel (UI.I18N,
		"log.guiinfo", 870);

	final java.util.HashMap propertyMap = new java.util.HashMap ();

	private static final int CURRENT = ThreadContext.registerProperty ();

	private static final int REG_ID = Registry.allocatePropertyId ();

	private static final HashSet PREFERENCES_PROPERTIES;
	static
	{
		PREFERENCES_PROPERTIES = new HashSet ();
		PREFERENCES_PROPERTIES.add (CURRENT_DIRECTORY);
		for (int i = 0; i < 10; i++)
			PREFERENCES_PROPERTIES.add (LAST_FILES[i]);
	}

	private final Registry registry;
	private final JobManager jm;
	private final UIToolkit ui;
	private final StringMap props;
	private String name;
	private boolean modified;
	private boolean ignoreIfModified;
	private File file;
	private MimeType mimeType;

	public interface Loader extends ProjectLoader
	{
		void loadWorkbench (Workbench wb);
	}

	public Workbench (Registry registry, JobManager jm, UIToolkit ui,
			Map initParams)
	{
		this.props = new StringMap (initParams);
		this.registry = registry;
		registry.setUserProperty (REG_ID, this);
		this.jm = jm;
		this.ui = ui;

		class Helper extends Handler implements Command
		{
			private final ObjectList logRecords = new ObjectList (),
					newGUIRecords = new ObjectList ();

			Helper ()
			{
				setLevel (GUI_INFO);
			}

			@Override
			public void close ()
			{
			}

			@Override
			public void flush ()
			{
				synchronized (logRecords)
				{
					long t = System.currentTimeMillis () - 60000;
					while (logRecords.size > 10)
					{
						LogRecord r = (LogRecord) logRecords.get (0);
						if (r.getMillis () < t)
						{
							Throwable e = Utils.getMainException (r
								.getThrown ());
							if (e instanceof DetailedException)
							{
								((DetailedException) e).dispose ();
							}
							logRecords.remove (0);
						}
						else
						{
							break;
						}
					}
				}
			}

			@Override
			public void run (Object info, Context c)
			{
				synchronized (newGUIRecords)
				{
					try
					{
						LogRecord[][] r = new LogRecord[1][newGUIRecords.size];
						newGUIRecords.toArray (r[0]);
						PanelFactory.getAndShowPanel (c, "/ui/panels/log",
							new StringMap ().putObject ("logrecords", r));
					}
					catch (RuntimeException e)
					{
						getErrorManager ().error (
							"Error while checking log viewer", e,
							ErrorManager.GENERIC_FAILURE);
					}
					newGUIRecords.clear ();
				}
			}

			@Override
			public String getCommandName ()
			{
				return null;
			}

			@Override
			public void publish (LogRecord record)
			{
				synchronized (logRecords)
				{
					logRecords.add (record);
				}
				synchronized (newGUIRecords)
				{
					if (record.getLevel ().intValue () >= GUI_INFO.intValue ())
					{
						newGUIRecords.add (record);
						if (newGUIRecords.size == 1)
						{
							getJobManager ().runLater (this, null,
								Workbench.this, JobManager.UPDATE_FLAGS);
						}
					}
				}
			}
		}

		getLogger ().addHandler (new Helper ());

		class Notifier extends LockProtectedCommand implements Runnable
		{
			private final GraphManager graph;

			Notifier (GraphManager graph)
			{
				super (graph, false, JobManager.ACTION_FLAGS);
				this.graph = graph;
				graph.initNonlocalTransactionNotifier (this);
			}

			@Override
			protected void runImpl (Object arg, Context ctx, Lock lock)
			{
				Transaction t = graph.getTransaction (true);
				t.begin (true);
				t.commit ();
			}

			@Override
			public void run ()
			{
				getJobManager ().runLater (this, null, Workbench.this,
					JobManager.ACTION_FLAGS);
			}
		}

		new Notifier (registry.getProjectGraph ());
	}

	public static Workbench current ()
	{
		return current (ThreadContext.current ());
	}

	public static Workbench get (RegistryContext ctx)
	{
		Object w = ctx.getRegistry ().getUserProperty (REG_ID);
		return (w != null) ? (Workbench) w : current ();
	}

	public static Workbench current (ThreadContext tc)
	{
		return (Workbench) tc.getProperty (CURRENT);
	}

	public static void setCurrent (Workbench w)
	{
		ThreadContext.current ().setProperty (CURRENT, w);
		Registry.setCurrent (w);
	}

	public void runAsCurrent (Runnable r)
	{
		ThreadContext tc = ThreadContext.current ();
		Workbench old = current (tc);
		Registry reg = Registry.current ();
		try
		{
			tc.setProperty (CURRENT, this);
			Registry.setCurrent (this);
			r.run ();
		}
		finally
		{
			tc.setProperty (CURRENT, old);
			Registry.setCurrent (reg);
		}
	}

	public boolean isWorkbenchThread ()
	{
		return Thread.currentThread () == jm.getMainThread ();
	}

	protected void initializeWindow (Window w)
	{
		if (w == null)
		{
			return;
		}
		w.initializeWorkbench (this);
		UI.setMenu (w, getRegistry ().getItem ("/workbench/menu"), null);

		refreshLastUsed(getRegistry(), getMainWorkbench());

		Item i = getRegistry ().getItem ("/workbench/state/layout");
		if (i instanceof Layout)
		{
			w.setLayout ((Layout) i, props);
		}
		else
		{
			String initLayout = (String) props.get (INITIAL_LAYOUT);
			if (initLayout != null)
			{
				i = Item.resolveItem (this, initLayout);
				initLayout = null;
				if (i instanceof Layout)
				{
					w.setLayout ((Layout) i, props);
					return;
				}
			}
			i = getRegistry ().getItem ("/workbench/layouts");
			if (i != null)
			{
				i = (Item) i.getBranch ();
				if (i != null)
				{
					i = i.resolveLink (this);
					if (i instanceof Layout)
					{
						w.setLayout ((Layout) i, props);
					}
				}
			}
		}
	}

	public abstract Workbench getMainWorkbench ();

	public final Logger getLogger ()
	{
		return registry.getLogger ();
	}

	@Override
	public final Registry getRegistry ()
	{
		return registry;
	}

	public final JobManager getJobManager ()
	{
		return jm;
	}

	public final UIToolkit getToolkit ()
	{
		return ui;
	}

	@Override
	public final Workbench getWorkbench ()
	{
		return this;
	}

	@Override
	public final Panel getPanel ()
	{
		return (getWindow () != null) ? getWindow ().getPanel () : null;
	}

	@Override
	public final Object getComponent ()
	{
		return (getWindow () != null) ? getWindow ().getComponent () : null;
	}

	public boolean isHeadless ()
	{
		return getWindow () == null;
	}

	public void setProperty (String key, Object value)
	{
		synchronized (PREFERENCES_PROPERTIES)
		{
			props.put (key, value);
			if (PREFERENCES_PROPERTIES.contains (key))
			{
				Preferences p = Preferences.userRoot ().node (
					"/de/grogra/workbench");
				p.put (key, String.valueOf (value));
			}
		}
	}

	public Object getProperty (String key)
	{
		synchronized (PREFERENCES_PROPERTIES)
		{
			if (props.containsKey (key))
			{
				return props.get (key);
			}
			if (getMainWorkbench ().props.containsKey (key))
			{
				return getMainWorkbench ().props.get (key);
			}
			if (PREFERENCES_PROPERTIES.contains (key))
			{
				Preferences p = Preferences.userRoot ().node (
					"/de/grogra/workbench");
				String value = p.get (key, null);
				getMainWorkbench ().props.put (key, value);
				return value;
			}
			return null;
		}
	}

	public void setName (String name)
	{
		getRegistry ().setProjectName (name);
		this.name = name;
		updateName ();
	}

	public String getName ()
	{
		return (name != null) ? name : "";
	}

	public final void setModified ()
	{
		setModified (true);
	}

	public final boolean isModified ()
	{
		return modified;
	}

	protected void setModified (boolean modified)
	{
		if (!ignoreIfModified && (modified != this.modified))
		{
			this.modified = modified;
			updateName ();
		}
	}

	protected void updateName ()
	{
		String n = (String) Main.getApplication ().getDescription (
			Described.NAME);
		n = (name == null) ? UI.I18N.msg ("title.noprojectname", n)
				: modified ? UI.I18N
					.msg ("title.projectname.modified", n, name) : UI.I18N.msg (
					"title.projectname.unmodified", n, name);
		Map opt = UI.getOptions (this);
		if (Utils.getBoolean(opt, "showHostname", false)) {
			// set name of machine in window title
			try {
				java.net.InetAddress lm = java.net.InetAddress.getLocalHost();
				String name = lm.getHostName();
				n = n + " [" + name + "]";
			}
			catch (java.net.UnknownHostException uhe) {
				n = n + " [unknown]";
			}
		}
		UIProperty.WORKBENCH_TITLE.setValue (this, n);
	}

	/**
	 * Undo the last action.
	 * 
	 */
	public void undo ()
	{
		final boolean[] result = new boolean[1]; 
		UI.executeLockedly (getRegistry ().getProjectGraph (), true,
			new Command ()
			{
				@Override
				public String getCommandName ()
				{
					return null;
				}

				@Override
				public void run (Object arg, Context c)
				{
					GraphManager g = getRegistry ().getProjectGraph ();
					try
					{
						result[0] = g.undo (g.getActiveTransaction());
					}
					catch (IOException e)
					{
						throw new WrapException(e);
					}
				}
			}, null, this, JobManager.ACTION_FLAGS);
		if (result[0])
		{
			LogStore log = getRegistry().getProjectGraph().getLog();
			log.removeLast(2);
		}
	}
	
	public boolean save (boolean allowSaveAs)
	{
		if (file == null)
		{
			return allowSaveAs && saveAs ();
		}
		else if (save (file, mimeType))
		{
			setModified (false);
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean saveAs ()
	{
		FileChooserResult fr = chooseFileToSave (UI.I18N.getString (
			"filedialog.saveproject", "Save Project"), IOFlavor.REGISTRY, null);
		if ((fr != null) && save (fr.file, fr.getMimeType ()))
		{
			ignoreIfModified = false;
			setModified (false);
			setName (IO.toSimpleName (fr.file.getName ()));
			file = fr.file;
			mimeType = fr.getMimeType ();
			return true;
		}
		return false;
	}

	public abstract Workbench open (FilterSource fs, Map initParams);

	public static void save (Item item, Object info, Context ctx)
	{
		ctx.getWorkbench ().save (true);
	}

	/**
	 * Undo the last derivation step.
	 * 
	 */
	public static void undo (Item item, Object info, Context ctx) {
		ctx.getWorkbench ().undo ();
	}

	public static void saveAs (Item item, Object info, Context ctx)
	{
		ctx.getWorkbench ().saveAs ();
	}

	public static void open (Item item, Object info, Context ctx)
	{
		Workbench wb = ctx.getWorkbench ();
		FileChooserResult fr = wb.chooseFileToOpen (UI.I18N.getString (
			"filedialog.openproject", "Open Project"), IOFlavor.PROJECT_LOADER);
		if (fr != null)
		{
			FileSource fs = fr.createFileSource (wb.getRegistry (), null);
			wb.open (fs, null);
			addToLastUsed(wb, fr.file);
		}
	}

	public static void openRecent (Item item, Object info, Context ctx)
	{
		Workbench wb = ctx.getWorkbench ();
		ActionEditEvent aee = (ActionEditEvent) info;
		String filename = (String) ((Value) aee.getSource()).getObject();
		FileSource fs = FileSource.createFileSource(filename, IO.getMimeType(filename), ctx.getWorkbench(), null);
		wb.open(fs, null);
		addToLastUsed(wb, fs.getInputFile());
	}

	public static void openAsDemo (Item item, Object info, Context ctx)
	{
		if (info instanceof URL)
		{
			info = Utils.urlToFile ((URL) info).getAbsolutePath ();
		}
		else if (info instanceof File)
		{
			info = ((File) info).getAbsolutePath ();
		}
		Workbench wb = ctx.getWorkbench ();
		StringMap init = new StringMap ().putBoolean (START_AS_DEMO, true);
		FileSource fs = FileSource.createFileSource ((String) info, IO
				.getMimeType ((String) info), ctx.getWorkbench (), null);
		wb.open (fs, init);
		addToLastUsed(wb, fs.getInputFile());
	}

	public static void addToLastUsed(Workbench wb, File file) {
		Workbench mwb = wb.getMainWorkbench();
		final Object new_file = file.getAbsolutePath();

		Object tmp_file_1 = new_file;
		Object tmp_file_2;

		for (int i = 0; i < 10; i++) {
			tmp_file_2 = mwb.getProperty(LAST_FILES[i]);
			if ((tmp_file_2 == null) || (tmp_file_2.equals(new_file))) {
				mwb.setProperty(LAST_FILES[i], tmp_file_1);
				return;
			}
			mwb.setProperty(LAST_FILES[i], tmp_file_1);
			tmp_file_1 = tmp_file_2;
		}
	}

	public static void refreshLastUsed(Registry r, Workbench mwb) {
		for (int i = 0; i < 10; i++) {
			Item ori = r.getItem("/workbench/menu/src/file/openclose/openrecent");
			String filename = (String) mwb.getProperty(LAST_FILES[i]);
			if (filename == null)
				break;
			Value v = new Value((i+1) + ": " + filename, filename);
			ori.add(v);
		}
	}

	public void ignoreIfModified ()
	{
		ignoreIfModified = true;
	}

	public void setFile (File file, MimeType mimeType)
	{
		if ((file != null)
			&& (mimeType != null)
			&& IOFlavor.REGISTRY.isWritableTo (new IOFlavor (mimeType,
				IOFlavor.FILE_WRITER, null)))
		{
			this.file = file;
			this.mimeType = mimeType;
		}
		else
		{
			this.file = null;
			this.mimeType = null;
		}
	}

	private boolean save (File f, MimeType mt)
	{
		FilterSource fs = IO.createPipeline (
			new ObjectSourceImpl (getRegistry (), "registry",
				IOFlavor.REGISTRY, getRegistry (), null), new IOFlavor (mt,
				IOFlavor.FILE_WRITER, null));
		if (fs == null)
		{
			logGUIInfo (IO.I18N.msg ("save.unsupported", f, IO
				.getDescription (mt)));
			return false;
		}
		try
		{
			Item s = new Directory ("state");
			getState (s);
			Item c = (Item) s.getBranch ();
			if (c != null)
			{
				s.makeUserItem (true);
				s.setBranch (null);
				s = getRegistry ().getDirectory ("/workbench/state", null);
				s.setBranch (c);
			}
			((FileWriterSource) fs).write (f);
			addToLastUsed(Workbench.current(), f);
			return true;
		}
		catch (IOException e)
		{
			logGUIInfo (IO.I18N.msg ("saveproject.failed", file), e);
			return false;
		}
	}

	public void export (FilterSource src)
	{
		FileChooserResult fr = chooseFileToSave ("Export", src.getFlavor (), null);
		if (fr != null)
		{
			export (src, fr.getMimeType (), fr.file);
		}
	}

	public void export (FilterSource src, MimeType mt, File file)
	{
		FilterSource fs = IO.createPipeline (src, new IOFlavor (mt,
			IOFlavor.FILE_WRITER, null));
		if (fs == null)
		{
			logGUIInfo (IO.I18N.msg ("save.unsupported", file, IO
				.getDescription (mt)));
			return;
		}
		try
		{
			fs.setMetaData (FilterSource.DESTINATION_FILE, file);
			fs.setMetaData (FilterSource.DESTINATION_URL, Utils.fileToURL (file));
			((FileWriterSource) fs).write (file);
		}
		catch (java.io.IOException ex)
		{
			logGUIInfo (IO.I18N.msg ("saveproject.failed", file), ex);
		}
	}

	protected void getState (Item state)
	{
		Layout layout = getWindow ().getLayout ();
		layout.setName ("layout");
		state.appendBranchNode (layout);
	}

	public FileChooserResult chooseFile (String title, FileFilter[] filters,
			int type, boolean mustExist, FileFilter selectedFilter)
	{
		if (title == null)
		{
			switch (type)
			{
				case Window.OPEN_FILE:
					title = "filedialog.openfile";
					break;
				case Window.ADD_FILE:
					title = "filedialog.addfile";
					break;
				case Window.SAVE_FILE:
					title = "filedialog.savefile";
					break;
			}
			title = UI.I18N.getString (title);
		}
		String cwds = (String) getProperty (CURRENT_DIRECTORY);
		File cwd = (cwds != null) ? new File (cwds) : null;
		if ((type != Window.SAVE_FILE) && (filters != null)
			&& (filters.length > 1))
		{
			FileFilter[] a = new FileFilter[filters.length + 1];
			System.arraycopy (filters, 0, a, 1, filters.length);
			final FileFilter[] list = filters;
			a[0] = new MimeTypeFileFilter ()
			{
				@Override
				public MimeType getMimeType (File f)
				{
					for (int i = 0; i < list.length; i++)
					{
						if ((list[i] instanceof MimeTypeFileFilter)
							&& list[i].accept (f))
						{
							return ((MimeTypeFileFilter) list[i])
								.getMimeType (f);
						}
					}
					return IO.getMimeType (f.getName ());
				}

				@Override
				public boolean accept (File f)
				{
					for (int i = 0; i < list.length; i++)
					{
						if (list[i].accept (f))
						{
							return true;
						}
					}
					return false;
				}

				@Override
				public String getDescription ()
				{
					return UI.I18N.msg ("filedialog.allfilters");
				}
			};
			filters = a;
		}
		FileChooserResult r = getWindow ().chooseFile (title, cwd, filters,
			type, mustExist, selectedFilter);
		if (r != null)
		{
			getMainWorkbench ().setProperty (CURRENT_DIRECTORY,
				r.file.getParentFile ().getAbsolutePath ());
		}
		return r;
	}
	
	
	public FileChooserResult chooseFileToOpen (String title,
			IOFlavor acceptableFlavor)
	{
		return chooseFile (title, IO.getReadableFileTypes (new IOFlavor[] {acceptableFlavor}), Window.OPEN_FILE, true, null);
	}

	/**
	 * 
	 * 
	 * @param title
	 * @param flavor
	 * @param selectedfilter - the selected file filter type
	 * @return
	 */
	public FileChooserResult chooseFileToSave (String title, IOFlavor flavor, Filter selectedfilter)
	{
		FileChooserResult fcr = null;
		boolean fileExists = true;
		while (fileExists) {
			fcr = chooseFile (title, IO.getWritableFileTypes (flavor),
					Window.SAVE_FILE, false, selectedfilter);
			if (fcr == null)
			{
				return null;
			}
			fileExists = fcr.file.exists();
			if (fileExists) {
				int result = Workbench.current().getWindow().showDialog(
						UI.I18N.msg ("filedialog.fileexists.title"),
						UI.I18N.msg ("filedialog.fileexists.message"),
						Window.QUESTION_CANCEL_MESSAGE);
				if (result == Window.YES_OK_RESULT)
				{
					return fcr;
				}
				else if (result == Window.CANCEL_RESULT) {
					return null;
				}
			}
		}
		return fcr;
	}	
	
	public Object readObject (FileChooserResult src, IOFlavor flavor)
	{
		if (src == null)
		{
			return null;
		}
		return readObject (src.createFileSource (registry, new StringMap ()), flavor);
	}

	public Object readObject (FilterSource src, IOFlavor flavor)
	{
		FilterSource s = IO.createPipeline (src, flavor);
		if (!(s instanceof ObjectSource))
		{
			logGUIInfo (IO.I18N.msg ("openfile.unsupported", IO.toName (src
				.getSystemId ()), IO.getDescription (src.getFlavor ()
				.getMimeType ())));
			return null;
		}
		ConfigurationSet cs = new ConfigurationSet (IO.I18N.msg (
			"openfile.options", src.getSystemId ()));
		FilterSource fsi = s;
		while (true)
		{
			if (fsi instanceof Configurable)
			{
				cs.add ((Configurable) fsi);
			}
			de.grogra.pf.io.Filter fi = fsi.getFilter ();
			if (fi == null)
			{
				break;
			}
			if (fi instanceof Configurable)
			{
				cs.add ((Configurable) fi);
			}
			fsi = fi.getSource ();
		}
		if (cs.size () > 0)
		{
			if (!showConfigurationDialog (cs))
			{
				return null;
			}
		}
		s.initProgressMonitor (UI.createProgressAdapter (this));
		try
		{
			return ((ObjectSource) s).getObject ();
		}
		catch (IOException e)
		{
			logGUIInfo (IO.I18N.msg ("openfile.failed", IO.toName (src
				.getSystemId ())), e);
			return null;
		}
		finally
		{
			s.setProgress (null, ProgressMonitor.DONE_PROGRESS);
		}
	}

	public void showAboutAppDialog (PluginDescriptor plugin)
	{
		String[] a = {"provider", "name", "version", "id"};
		for (int i = 0; i < 4; i++)
		{
			a[i] = UI.I18N.getString ("plugins." + a[i], a[i]);
		}
		final Item plugins = registry.getPluginDirectory ();
		UISubTree t = new UISubTree (new RegistryAdapter (this),
			new javax.swing.tree.TreePath (plugins.getPath ()));
		final ComponentWrapper table = ui.createTable (new TableMapper (t, a,
			new String[] {"provider", "pluginName", "version", "pluginId"},
			false), this);
		Object c = ui.createContainer (10);
		ui.addComponent (c, table.getComponent (), BorderLayout.CENTER);
		Object x = ui.createContainer (0);
		ui.addComponent (c, x, BorderLayout.SOUTH);
		ui.addComponent (x, ui.createButton (UI.I18N, "plugins.moreinfo",
			UIToolkit.MENU_ICON_SIZE, 0, new Command ()
			{
				@Override
				public String getCommandName ()
				{
					return null;
				}

				@Override
				public void run (Object info, Context ctx)
				{
					int r = ui.getSelectedRow (table);
					if (r < 0)
					{
						return;
					}
					Item i = (Item) plugins.getBranchNode (r);
					if (!(i instanceof PluginDescriptor))
					{
						return;
					}
					showAboutPluginDialog ((PluginDescriptor) i);
				}
			}, this), BorderLayout.EAST);
		ObjectList tc = new ObjectList (4).push (
			UI.I18N.getString ("aboutapp.tab.plugins")).push (c);

		c = ui.createContainer (10);
		ui.addComponent (c, ui.createLabel (UI.I18N
			.keyToDescribed ("thirdparty.info"), 0), BorderLayout.NORTH);
		StringBuffer b = new StringBuffer ("<html>");
		for (Item i = (Item) plugins.getBranch (); i != null; i = (Item) i
			.getSuccessor ())
		{
			if (i instanceof PluginDescriptor)
			{
				getThirdPartyContent ((PluginDescriptor) i, b, false);
			}
		}
		x = ui.createLabel (b.append ("</html>").toString (), 0);
		ui.addComponent (c, ui.createScrollPane (x), BorderLayout.CENTER);
		tc.push (UI.I18N.getString ("aboutapp.tab.thirdparty")).push (c);

		getWindow ().showDialog (
			plugin.getI18NBundle ().getString ("aboutapp.Title",
				plugin.getPluginName ()),
			ui.createAbout (plugin, "aboutapp", tc),
			Window.RESIZABLE_PLAIN_MESSAGE);
	}

	public void showAboutPluginDialog (PluginDescriptor plugin)
	{
		String s = (String) plugin
			.getFromResource ("aboutplugin.tab.about.content");
		if (s == null)
		{
			s = UI.I18N.msg ("aboutplugin.tab.about.content",
				plugin.getName (), plugin.getPluginName (), plugin
					.getPluginProvider (), plugin.getPluginVersion (), String
					.valueOf (plugin.getFromResource ("aboutplugin.License")));
		}
		ObjectList tc = new ObjectList (4).push (
			UI.I18N.getString ("aboutplugin.tab.about")).push (
			ui.createScrollPane (ui.createLabel (s, 0)));

		StringBuffer b = new StringBuffer ();
		getThirdPartyContent (plugin, b, true);
		if (b.length () > 0)
		{
			b.insert (0, "<html>").append ("</html>");
			tc.push (UI.I18N.getString ("aboutplugin.tab.thirdparty")).push (
				ui.createScrollPane (ui.createLabel (b.toString (), 0)));
		}
		getWindow ().showDialog (
			plugin.getI18NBundle ().getString ("aboutplugin.Title",
				plugin.getPluginName ()),
			ui.createAbout (plugin, "aboutplugin", tc),
			Window.RESIZABLE_PLAIN_MESSAGE);
	}

	private static void getThirdPartyContent (PluginDescriptor p,
			StringBuffer b, boolean singlePlugin)
	{
		String s = (String) p.getFromResource ("thirdparty.list");
		if (s != null)
		{
			if (!singlePlugin)
			{
				b.append ("<h2>").append (
					UI.I18N.msg ("thirdparty.ofplugin", p.getPluginName ()))
					.append ("</h2>");
			}

			StringTokenizer t = new StringTokenizer (s);
			String h = singlePlugin ? "h2>" : "h3>";
			while (t.hasMoreTokens ())
			{
				String k = "thirdparty." + t.nextToken () + '.';
				b.append ('<').append (h).append (
					p.getFromResource (k + "Name")).append ("</").append (h);
				if (!singlePlugin)
				{
					b.append ("<blockquote>");
				}
				if ((s = (String) p.getFromResource (k + "Copy")) != null)
				{
					b.append (s).append ("<br>");
				}
				if ((s = (String) p.getFromResource (k + "License")) != null)
				{
					b.append (UI.I18N.getString ("thirdparty.license")).append (
						": ").append (s).append ("<br>");
				}
				if ((s = (String) p.getFromResource (k + "URL")) != null)
				{
					b.append (UI.I18N.getString ("thirdparty.url")).append (
						": ").append (s).append ("<br>");
				}
				if (singlePlugin)
				{
					if ((s = (String) p.getFromResource (k + "Details")) != null)
					{
						b.append ("<blockquote>").append (s).append (
							"</blockquote>");
					}
				}
				else
				{
					b.append ("</blockquote>");
				}
			}
			if (!singlePlugin)
			{
				b.append ("<hr>");
			}
		}
	}

	public boolean showConfigurationDialog (ConfigurationSet config)
	{
		MapSource m = new MapSource (this, config,
			config.getKeyDescriptions (), config.getName ());
		boolean b = showConfigurationDialog (m);
		if (b)
		{
			config.writeBack ();
		}
		return b;
	}

	public boolean showConfigurationDialog (Selection properties)
	{
		ComponentWrapper c = properties.createPropertyEditorComponent ();
		if (c == null)
		{
			return true;
		}
		int res = getWindow ().showDialog (
			(String) properties.getDescription (Described.NAME),
			ui.createScrollPane (c.getComponent ()),
			Window.RESIZABLE_OK_CANCEL_MESSAGE);
		c.dispose ();
		return res == Window.YES_OK_RESULT;
	}

	public ChartPanel getChartPanel (String chart, Map params)
	{
		if (getWindow () == null)
		{
			return null;
		}
		String chartId = (chart == null) ? "/ui/panels/chart"
				: ("/ui/panels/chart?" + chart);
		if (chart == null)
		{
			chart = "Chart";
		}
		ChartPanel p = (ChartPanel) getWindow ().getPanel (chartId);
		if (p == null)
		{
			StringMap map = new StringMap (params).putObject (Panel.PANEL_ID,
				chartId).putObject (UIProperty.PANEL_TITLE.getName (), chart);
			p = (ChartPanel) PanelFactory.createPanel (this,
				"/ui/panels/chart", map);
			p.show (false, null);
		}
		return p;
	}

	public void showViewerPanel (String viewerId, URL url, Map params)
	{
		if (isHeadless ())
		{
			return;
		}
		viewerId = (viewerId == null) ? "/ui/panels/viewer"
				: ("/ui/panels/viewer?" + viewerId);
		Panel p = getWindow ().getPanel (viewerId);
		if (p != null)
		{
			ui.setContent (ui.getTextViewerComponent (p), url);
			p.show (false, null);
		}
		else
		{
			StringMap map = new StringMap (params).putObject (Panel.PANEL_ID,
				viewerId).putObject ("systemId",
				IO.toSystemId (getRegistry ().getFileSystem (), url));
			p = PanelFactory.createPanel (this, "/ui/panels/viewer", map);
			if (p != null)
			{
				p.show (false, null);
			}
		}
	}

	public void showViewerPanel (String viewerId, String systemId, Map params)
	{
		try
		{
			showViewerPanel (viewerId, IO.toURL (getRegistry (), systemId),
				params);
		}
		catch (java.net.MalformedURLException e)
		{
			logGUIInfo (null, e);
		}
	}

	public void logInfo (String msg)
	{
		logInfo (msg, null);
	}

	public void logGUIInfo (String msg)
	{
		logGUIInfo (msg, null);
	}

	public void logInfo (String msg, Throwable thrown)
	{
		if (msg == null)
		{
			msg = "";
		}
		getLogger ().log (Level.INFO, msg, Utils.initCauses (thrown));
	}

	public void logGUIInfo (String msg, Throwable thrown)
	{
		if (msg == null)
		{
			msg = "";
		}
		thrown = Utils.getMainException (thrown);
		getLogger ().log (
			((thrown == null) || (thrown instanceof UserException)) ? GUI_INFO
					: Level.WARNING, msg, Utils.initCauses (thrown));
	}

	public static void log (Throwable thrown)
	{
		Workbench w = current ();
		if (w != null)
		{
			w.logGUIInfo ("", thrown);
		}
		else
		{
			Main.logWarning (thrown);
		}
	}

	private final Object statusLock = new Object ();
	private Object statusOwner;
	private String status;
	private Float progress;
	private final PropertyChangeSupport statusListeners = new PropertyChangeSupport (
		this);

	public void addStatusChangeListener (PropertyChangeListener l)
	{
		statusListeners.addPropertyChangeListener (l);
	}

	public void removeStatusChangeListener (PropertyChangeListener l)
	{
		statusListeners.removePropertyChangeListener (l);
	}

	public void beginStatus (Object owner)
	{
		boolean b;
		synchronized (statusLock)
		{
			if (b = (owner != statusOwner))
			{
				statusOwner = owner;
			}
		}
		if (b)
		{
			clearStatusAndProgress (owner);
		}
	}

	public void setStatus (Object owner, String text)
	{
		String old;
		synchronized (statusLock)
		{
			if (owner != statusOwner)
			{
				return;
			}
			old = status;
			status = text;
		}
		if (!Utils.equal (old, text))
		{
			if (isHeadless ())
			{
				System.err.println (text);
			}
			else
			{
				statusListeners.firePropertyChange ("status", old, text);
			}
		}
	}

	private void setProgress (Object owner, Float value)
	{
		Float old;
		synchronized (statusLock)
		{
			if (owner != statusOwner)
			{
				return;
			}
			old = progress;
			progress = value;
		}
		if (!Utils.equal (old, value))
		{
			if (isHeadless ())
			{
				System.err.println ((value == null) ? "Done" : value.floatValue () * 100 + "%");
			}
			else
			{
				statusListeners.firePropertyChange ("progress", old, value);
			}
		}
	}

	public void setStatus (Object owner, String text, float progress)
	{
		setStatus (owner, text);
		setProgress (owner, progress);
	}

	public void setStatusClearProgress (Object owner, String text)
	{
		setStatus (owner, text);
		clearProgress (owner);
	}

	public void setProgress (Object owner, float progress)
	{
		setProgress (owner, new Float (progress));
	}

	public void clearProgress (Object owner)
	{
		setProgress (owner, null);
	}

	public void setIndeterminateProgress (Object owner)
	{
		setProgress (owner, -1);
	}

	public void clearStatusAndProgress (Object owner)
	{
		setStatus (owner, null);
		setProgress (owner, null);
	}

	public static void cut (Item item, Object info, Context ctx)
	{
	}

	public static boolean isCutEnabled (Context ctx)
	{
		return isCopyEnabled (ctx) && isDeleteEnabled (ctx);
	}

	public static void copy (Item item, Object info, Context ctx)
	{
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		if ((s instanceof Selection)
			&& ((((Selection) s).getCapabilities () & Selection.TRANSFERABLE) != 0))
		{
			Toolkit.getDefaultToolkit ().getSystemClipboard ().setContents (
				((Selection) s).toTransferable (true), ctx.getWorkbench ());
		}
	}

	public static boolean isCopyEnabled (Context ctx)
	{
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		return (s instanceof Selection)
			&& ((((Selection) s).getCapabilities () & Selection.TRANSFERABLE) != 0);
	}

	public static void paste (Item item, Object info, Context ctx)
	{
		Thread t = Thread.currentThread ();
		ClassLoader ccl = t.getContextClassLoader ();
		try
		{
			t.setContextClassLoader (Main.getLoaderForAll ());
			Transferable x = Toolkit.getDefaultToolkit ().getSystemClipboard ()
				.getContents (ctx.getWorkbench ());
		}
		finally
		{
			t.setContextClassLoader (ccl);
		}
	}

	public static void delete (Item item, Object info, Context ctx)
	{
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		if ((s instanceof Selection)
			&& ((((Selection) s).getCapabilities () & Selection.DELETABLE) != 0))
		{
			ctx.getWorkbench ().setDeleteEvent (true);
			((Selection) s).delete (true);
		}
	}
	
	private boolean deleteEvent = false;
	
	public boolean isDeleteEvent () {
		return deleteEvent;
	}
	
	public void setDeleteEvent (boolean value) {
		deleteEvent = value;
	}

	public static boolean isDeleteEnabled (Context ctx)
	{
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		return (s instanceof Selection)
			&& ((((Selection) s).getCapabilities () & Selection.DELETABLE) != 0);
	}

	@Override
	public void lostOwnership (Clipboard c, Transferable t)
	{
		System.out.println ("lostOwnership of " + c);
	}

	public void select (Node[] nodes)
	{
		if ((nodes == null) || (nodes.length == 0))
		{
			UIProperty.WORKBENCH_SELECTION.setValue (this, null);
			return;
		}
		GraphState[] states = new GraphState[nodes.length];
		Arrays.fill (states, GraphState.get (getRegistry ().getProjectGraph (), jm.getThreadContext ()));
		boolean[] trueArray = new boolean[nodes.length];
		Arrays.fill (trueArray, true);
		UIProperty.WORKBENCH_SELECTION
			.setValue (this, new GraphSelectionImpl (this, states, nodes, null, trueArray));
	}

	public void select (ComponentDescriptor component)
	{
		if (component == null)
		{
			UIProperty.WORKBENCH_SELECTION.setValue (this, null);
			return;
		}
		GraphState state = GraphState.get (getRegistry ().getProjectGraph (), jm.getThreadContext ());
		UIProperty.WORKBENCH_SELECTION
			.setValue (this, new ComponentSelection (this, state, component));
	}
	
	public File getFile() {
		return this.file;
	}

	/**
	 * Refreshes JEdit for the file fielName.
	 * 
	 * @param fileName, including suffix, e.g. "rgg", but without file system identifier ("pfs" is assumed)
	 */
	public static void refreshJEdit (String fileName) {
		if(current ()==null) return;
		Item dir = current ().getRegistry().getDirectory ("/project/objects/files", null);
		for(Node n = dir.getBranch(); n != null; n = n.getSuccessor()) {
			if(n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
				
				//if generated file is found in file explorer, refresh it in jedit
				if(sf.getName().equals("pfs:"+fileName)) {
					sf.showLater(current ());
				}
			}
		}
	}

	/**
	 * Refreshes JEdit for the file fielName.
	 * 
	 * @param the current workbench
	 * @param fileName, including suffix, e.g. "rgg", but without file system identifier ("pfs" is assumed)
	 */
	public static void refreshJEdit (Workbench wb, String fileName) {
		if(wb==null) return;
		Item dir = wb.getRegistry ().getDirectory ("/project/objects/files", null);
		for(Node n = dir.getBranch(); n != null; n = n.getSuccessor()) {
			if(n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
				
				//if generated file is found in file explorer, refresh it in jedit
				if(sf.getName().equals("pfs:"+fileName)) {
					sf.showLater(wb);
				}
			}
		}
	}

}
