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

package de.grogra.imp;

import java.io.File;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import de.grogra.graph.GraphState;
import de.grogra.imp.net.Commands;
import de.grogra.imp.net.Connection;
import de.grogra.imp.net.MessageHandler;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.Map;
import de.grogra.util.StringMap;
import de.grogra.util.ThreadContext;
import de.grogra.util.Utils;
import de.grogra.vfs.FileSystem;
import de.grogra.xl.util.ObjectList;

public final class IMPWorkbench extends Workbench implements TreeModelListener
{
	private Window window;

	// these presence of this field ensures that the graph
	// state is not garbage collected
	private GraphState regState;

	private Filter logFilter;
	private ObjectList pendingLogs;

	private ObjectList connections;

	public IMPWorkbench (Registry registry, Map initParams)
	{
		super (registry, new IMPJobManager (),
			IMP.getInstance ().getToolkit (), initParams);
		logFilter = getLogger ().getFilter ();
		pendingLogs = new ObjectList ();
		connections = new ObjectList ();
		getLogger ().setFilter (new Filter ()
		{
			public boolean isLoggable (LogRecord r)
			{
				if ((logFilter == null) || logFilter.isLoggable (r))
				{
					pendingLogs.add (r);
				}
				return false;
			}
		});
		getIMPJobManager ().initialize (this);
		IMP.getInstance ().registerWorkbench (this);
	}

	IMPJobManager getIMPJobManager ()
	{
		return (IMPJobManager) getJobManager ();
	}

	public static IMPWorkbench get (Context ctx)
	{
		return (IMPWorkbench) ctx.getWorkbench ();
	}

	@Override
	public Workbench getMainWorkbench ()
	{
		return IMP.getInstance ().getMainWorkbench ();
	}

	void close (final Command afterDispose)
	{
		if (getWindow () != null)
		{
			final Panel[] panels = getWindow ().getPanels (null);
			new Command ()
			{
				public void run (Object info, Context ctx)
				{
					for (int i = 0; i < panels.length; i++)
					{
						Panel p;
						if ((p = panels[i]) != null)
						{
							panels[i] = null;
							p.checkClose (this);
							return;
						}
					}
					close0 (afterDispose);
				}

				public String getCommandName ()
				{
					return null;
				}
			}.run (null, this);
		}
		else
		{
			close0 (afterDispose);
		}
	}

	void close0 (Command afterDispose)
	{
		if (isModified () && (getWindow () != null))
		{
			int res = getWindow ().showDialog (
				UI.I18N.msg ("project.savequestion.title"),
				UI.I18N.msg ("project.savequestion.msg", getName ()),
				Window.QUESTION_CANCEL_MESSAGE);
			if (res == Window.CANCEL_RESULT)
			{
				return;
			}
			else if (res == Window.YES_OK_RESULT)
			{
				if (!save (true))
				{
					return;
				}
			}
		}
		Registry r = getRegistry ().getRootRegistry ();
		Executable.runExecutables (r, "/hooks/close", r, UI.getArgs (this, null));
		getIMPJobManager ().stop (afterDispose);
	}

	void disposeWhenNotInitialized ()
	{
		getRegistry ().dispose ();
		IMP.getInstance ().deregisterWorkbench (this, window);
	}

	void dispose (Command afterDispose)
	{
		if (window != null)
		{
			window.dispose ();
		}
		getRegistry ().removeFileSystemListener (this);
		disposeWhenNotInitialized ();
		if (afterDispose != null)
		{
			afterDispose.run (null, this);
		}
		setCurrent (null);
		window = null;
	}

	void initialize ()
	{
		ThreadContext s = getJobManager ().getThreadContext ();
		regState = getRegistry ().getRegistryGraph ().createStaticState (s);
		getRegistry ().getProjectGraph ().initMainState ((IMPJobManager) getJobManager ());
		setCurrent (this);
		getRegistry ().startup ();
		getRegistry ().addFileSystemListener (this);
		updateName ();
		
		Map propMap = Map.EMPTY_MAP;
		Object o = getRegistry().getRootRegistry().getUserProperty(Main.SCREEN_PROPERTY_ID);
		if (o != null)
		{
			propMap = new StringMap().putInt(Main.SCREEN_PROPERTY, (Integer) o);
		}
		window = getToolkit ().createWindow (IMP.CLOSE, propMap);
		if (window != null)
		{
			initializeWindow ();
			window.show (true, null);
		}
		Main.closeSplashScreen ();
		getLogger ().setFilter (null);
		for (int i = 0; i < pendingLogs.size (); i++)
		{
			getLogger ().log ((LogRecord) pendingLogs.get (i));
		}
		getLogger ().setFilter (logFilter);
		pendingLogs = null;
		logFilter = null;
		StringMap m = new StringMap ().putObject ("registry", getRegistry ());
		m.putObject("workbench", this);
		m.putObject("filesystem", this.getRegistry().getFileSystem());
		Executable.runExecutables (getRegistry ().getRootRegistry (), "/hooks/projectloaded",
			getRegistry (), m);
		if (this == IMP.getInstance ().getMainWorkbench ())
		{
			getJobManager ().runLater (new Command ()
			{
				public void run (Object info, Context ctx)
				{
					executeCommandLine ();
				}

				public String getCommandName ()
				{
					return null;
				}
			}, null, this, JobManager.ACTION_FLAGS);
		}
	}

	void executeCommandLine ()
	{
		int argc = Main.getArgCount ();
		int arg = 0;
		while (arg < argc)
		{
			String s = Main.getArg (arg);
			if (s.equals ("-cmd"))
			{
				if (arg + 1 < argc)
				{
					String cmd = Main.getArg (arg + 1);
					String param = null;
					int i = cmd.indexOf ('=');
					if (i > 0)
					{
						param = cmd.substring (i + 1);
						cmd = cmd.substring (0, i);
					}
					Item c = Item.resolveItem (this, cmd);
					if (c instanceof Command)
					{
						((Command) c).run (param, this);
					}
				}
				arg += 2;
			}
			else if (arg + 1 == argc)
			{
				File f = new File (s);
				if (f.isFile ())
				{
					open (FileSource.createFileSource (IO.toSystemId (f), IO
						.getMimeType (f.getName ()), this, null), null);
				}
				arg++;
			}
			else
			{
				arg++;
			}
		}
	}

	void initializeWindow ()
	{
		initializeWindow (window);
	}

	public Item getRegistryItem (String item)
	{
		return getRegistry ().getItem (item);
	}

	public Window getWindow ()
	{
		return window;
	}

	@Override
	public Workbench open (FilterSource fs, Map initParams)
	{
		IMPWorkbench w = IMP.getInstance ()
			.openWorkbench (this, fs, initParams);
		if (Utils.getBoolean (initParams, START_AS_DEMO) && (w != null))
		{
			w.ignoreIfModified ();
			w.setFile (null, null);
		}
		return w;
	}

	public void treeNodesInserted (TreeModelEvent e)
	{
		treeStructureChanged (e);
	}

	public void treeNodesRemoved (TreeModelEvent e)
	{
		treeStructureChanged (e);
	}

	public void treeNodesChanged (TreeModelEvent e)
	{
		treeStructureChanged (e);
	}

	public void treeStructureChanged (TreeModelEvent e)
	{
		if (!((FileSystem) e.getSource ()).isPersistent ())
		{
			setModified ();
		}
	}

	private final MessageHandler msgHandler = new Commands (this);

	public void addConnection (Connection cx)
	{
		cx.addMessageHandler (msgHandler);
		synchronized (connections)
		{
			connections.add (cx);
		}
	}

	public void removeConnection (Connection cx)
	{
		cx.removeMessageHandler (msgHandler);
		synchronized (connections)
		{
			connections.remove (cx);
		}
	}

	public Connection[] getConnections ()
	{
		synchronized (connections)
		{
			for (int i = connections.size () - 1; i >= 0; i--)
			{
				if (((Connection) connections.get (i)).isClosed ())
				{
					connections.remove (i);
				}
			}
			return (Connection[]) connections
				.toArray (new Connection[connections.size ()]);
		}
	}

}
