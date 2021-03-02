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

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.vecmath.Tuple3f;

import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.io.ImageWriter;
import de.grogra.imp.net.ClientConnection;
import de.grogra.imp.net.Commands;
import de.grogra.imp.net.Connection;
import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.io.FileTypeItem.Filter;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ObjectSourceImpl;
import de.grogra.pf.io.OutputStreamSource;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.io.RegistryLoader;
import de.grogra.pf.registry.Application;
import de.grogra.pf.registry.Directory;
import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Plugin;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.FileChooserResult;
import de.grogra.pf.ui.HeadlessToolkit;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.pf.ui.registry.CommandPlugin;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.reflect.Type;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.util.ObjectList;

public final class IMP extends Plugin implements CommandPlugin
{
	public static final I18NBundle I18N = I18NBundle.getInstance (IMP.class);

	public static final MimeType TYPES_MIME_TYPE = MimeType
		.valueOf (Type[].class);
	public static final IOFlavor TYPES_FLAVOR = new IOFlavor (TYPES_MIME_TYPE);

	public static final Command CLOSE = new Command ()
	{
		public void run (Object info, Context ctx)
		{
			((IMPWorkbench) ctx.getWorkbench ()).close ((Command) info);
		}

		public String getCommandName ()
		{
			return null;
		}
	};

	private static IMP PLUGIN;

	public static IMP getInstance ()
	{
		return PLUGIN;
	}

	private ObjectList workbenches = new ObjectList (4, false);
	private IMPWorkbench mainWorkbench;
	private UIToolkit ui;

	public IMP ()
	{
		assert PLUGIN == null;
		PLUGIN = this;
	}

	public UIToolkit getToolkit ()
	{
		return ui;
	}

	public IMPWorkbench getMainWorkbench ()
	{
		return mainWorkbench;
	}

	public static void run (Application app)
	{
		getInstance ().runImpl (app);
	}

	private void runImpl (Application app)
	{
		Registry r = Registry.create (getRegistry ());
		try
		{
			loadRegistry (r, null, false);
		}
		catch (IOException e)
		{
			throw new AssertionError (e);
		}
		r.setEmptyGraph ();

		if ("true".equals (Main.getProperty ("headless")))
		{
			ui = new HeadlessToolkit ();
		}
		else
		{
			Expression e = (Expression) getRegistry ().getItem ("/ui/toolkits");
			Object o;
			if ((e == null)
				|| !((o = e.evaluate (this, new StringMap ().putObject ("registry",
					getRegistry ()))) instanceof UIToolkit))
			{
				de.grogra.pf.boot.Main.showMessage ("No UI toolkit found", true);
				return;
			}
			ui = (UIToolkit) o;
		}
		mainWorkbench = new IMPWorkbench (r, null);
		mainWorkbench.getIMPJobManager ().run ();
		mainWorkbench = null;
	}

	void registerWorkbench (IMPWorkbench wb)
	{
		synchronized (workbenches)
		{
			workbenches.add (wb);
		}
	}

	public void start (final IMPWorkbench workbench, Window feedback)
	{
		boolean hide;
		synchronized (workbenches)
		{
			hide = workbenches.size () == 2;
		}
		if (hide)
		{
			mainWorkbench.getJobManager ().runLater (new Command ()
			{
				public void run (Object info, Context ctx)
				{
					Window w = ctx.getWindow ();
					if ((w != null) && (w.getPanels (null).length == 0))
					{
						w.hide ();
					}
					workbench.getIMPJobManager ().start (null);
				}

				public String getCommandName ()
				{
					return null;
				}
			}, null, mainWorkbench, JobManager.UPDATE_FLAGS);
		}
		else
		{
			workbench.getIMPJobManager ().start (feedback);
		}
	}

	void deregisterWorkbench (IMPWorkbench wb, Window window)
	{
		synchronized (workbenches)
		{
			workbenches.remove (wb);
			if (workbenches.size () == 1)
			{
				mainWorkbench.getJobManager ().runLater (new Command ()
				{
					public void run (Object info, Context ctx)
					{
						if (ctx.getWorkbench ().isHeadless ())
						{
							return;
						}
						ctx.getWindow ().show (true, null);
					}

					public String getCommandName ()
					{
						return null;
					}
				}, null, mainWorkbench, JobManager.UPDATE_FLAGS);
			}
		}
	}

	public void exit ()
	{
		IMPWorkbench w;
		synchronized (workbenches)
		{
			if (workbenches.isEmpty ())
			{
				return;
			}
			w = (IMPWorkbench) workbenches.peek (1);
		}
		w.getJobManager ().execute (CLOSE, new Command ()
		{
			public void run (Object i, Context c)
			{
				exit ();
			}

			public String getCommandName ()
			{
				return null;
			}
		}, w, JobManager.UI_PRIORITY);
	}

	public static void loadRegistry (Registry reg, RegistryLoader loader,
			boolean project) throws IOException
	{
		StringMap m = new StringMap ().putObject ("registry", reg);
		if (project)
		{
			((Item) reg.getRoot ()).add (new Directory ("project"));
		}
		Executable.runExecutables (reg.getRootRegistry (), "/hooks/configure",
			reg, m);
		if (loader != null)
		{
			loader.loadRegistry (reg);
		}
		Executable.runExecutables (reg.getRootRegistry (), "/hooks/complete",
			reg, m);
		reg.activateItems ();
	}

	public void run (Object info, Context ctx, CommandItem item)
	{
		final IMPWorkbench wb = (IMPWorkbench) ctx.getWorkbench ();
		String n = item.getName ();
		if ("close".equals (n))
		{
			CLOSE.run (null, ctx);
		}
		else if ("exit".equals (n))
		{
			exit ();
		}
		else if ("aboutsoftware".equals (n))
		{
			wb.showAboutAppDialog (getPluginDescriptor ());
		}
		else if ("export".equals (n))
		{
			if (!(info instanceof ActionEditEvent))
			{
				return;
			}
			ActionEditEvent e = (ActionEditEvent) info;
			if (e.isConsumed () || !(e.getPanel () instanceof View))
			{
				return;
			}
			ctx.getWorkbench ().export (toFilterSource ((View) e.getPanel ()));
		}
		else if ("snapshot".equals (n))
		{
			if (!(info instanceof ActionEditEvent))
			{
				return;
			}
			ActionEditEvent e = (ActionEditEvent) info;
			if (e.isConsumed () || !(e.getPanel () instanceof View))
			{
				return;
			}
			final View v = (View) e.getPanel ();
			
			class Snapshot implements Command, ObjectConsumer<RenderedImage>
			{
				public String getCommandName ()
				{
					return null;
				}

				public void run (Object info, Context context)
				{
					IOFlavor flavor = ImageWriter.RENDERED_IMAGE_FLAVOR;
					Filter filter = null;
					Filter[] fileFilter = IO.getWritableFileTypes (flavor);
					if (fileFilter != null) {
						for (int i = 0; i < fileFilter.length; i++) {
							FileTypeItem fti = (FileTypeItem) (fileFilter[i].getItem());
							MimeType mt = fti.getMimeType();
							if (mt.equals(MimeType.PNG)) {
								filter = fileFilter[i];
								break;
							}
						}
					}
					FileChooserResult fr = wb.chooseFileToSave (
							I18N.msg ("snapshot.title"), flavor, filter);
					if (fr == null)
					{
						return;
					}
					writeImage (v, (RenderedImage) info, fr.getMimeType (), fr.file);
				}

				public void consume (RenderedImage value)
				{
					wb.getJobManager ().runLater (this, value, v, JobManager.ACTION_FLAGS);
				}
				
			}

			v.getViewComponent ().makeSnapshot (new Snapshot ());
		}
	}

	private static FilterSource toFilterSource (View view)
	{
		return new ObjectSourceImpl (view, "view", view.getFlavor (), view
			.getWorkbench ().getRegistry ().getRootRegistry (), null);
	}

	public static void export (View view, MimeType mt, File file)
	{
		view.getWorkbench ().export (toFilterSource (view), mt, file);
	}

	public static void writeImage (Image img, File file)
	{
		if (!(img instanceof RenderedImage))
		{
			throw new IllegalArgumentException ("image has to be instance of RenderedImage");
		}
		Workbench w = Workbench.current ();
		if (w == null)
		{
			throw new IllegalStateException ("no current workbench");
		}
		FileTypeItem i = FileTypeItem.get (w, file.getName ());
		if (i == null)
		{
			throw new UnsupportedOperationException ("unsupported image format of " + file);
		}
		writeImage (w, (RenderedImage) img, i.getMimeType (), file);
	}

	public static void writeImage (Context ctx, RenderedImage r, MimeType mt,
			File file)
	{
		try
		{
			OutputStream out = new BufferedOutputStream (new FileOutputStream (file));
			writeImage (ctx, r, mt, out, file.getPath ());
			out.flush ();
			out.close ();
		}
		catch (IOException e)
		{
			ctx.getWorkbench ().logGUIInfo (I18N.msg ("snapshot.failed", file), e);
		}
	}

	public static void writeImage (Context ctx, RenderedImage r, MimeType mt,
			OutputStream out, String outName)
	{
		FilterSource fs = IO.createPipeline (new ObjectSourceImpl (r,
			"snapshot", ImageWriter.RENDERED_IMAGE_FLAVOR, ctx.getWorkbench ()
				.getRegistry ().getRootRegistry (), null), new IOFlavor (mt,
			IOFlavor.OUTPUT_STREAM, null));
		if (fs == null)
		{
			ctx.getWorkbench ().logGUIInfo (
				IO.I18N.msg ("save.unsupported", outName, IO.getDescription (mt)));
			return;
		}
		try
		{
			((OutputStreamSource) fs).write (out);
		}
		catch (IOException ex)
		{
			ctx.getWorkbench ().logGUIInfo (
				I18N.msg ("snapshot.failed", outName), ex);
		}
	}

	public static void closeWorkbench (Context ctx)
	{
		UI.getJobManager (ctx).runLater (CLOSE, null, ctx,
			JobManager.ACTION_FLAGS);
	}

	IMPWorkbench openWorkbench (IMPWorkbench wb, FilterSource fs, Map initParams)
	{
		FilterSource s = IO.createPipeline (fs, IOFlavor.PROJECT_LOADER);
		if (!(s instanceof ObjectSource))
		{
			wb.logGUIInfo (IO.I18N.msg ("openproject.unsupported", IO
				.toName (fs.getSystemId ()), IO.getDescription (fs.getFlavor ()
				.getMimeType ())));
			return null;
		}
		Registry r = Registry.create (getRegistry ());
		IMPWorkbench w = new IMPWorkbench (r, initParams);
		try
		{
			ProjectLoader loader = (ProjectLoader) ((ObjectSource) s)
				.getObject ();
			Registry.setCurrent (r);
			w.setName (IO.toSimpleName (fs.getSystemId ()));
			loadRegistry (r, loader, true);
			loader.loadGraph (r);
			if (fs instanceof FileSource)
			{
				w.setFile (((FileSource) fs).getInputFile (), fs.getFlavor ()
					.getMimeType ());
			}
			if (loader instanceof Workbench.Loader)
			{
				((Workbench.Loader) loader).loadWorkbench (w);
			}
		}
		catch (Exception e)
		{
			w.disposeWhenNotInitialized ();
			wb.logGUIInfo (IO.I18N.msg ("openproject.failed", IO.toName (fs
				.getSystemId ())), e);
			return null;
		}
		finally
		{
			Registry.setCurrent (wb);
		}
		start (w, wb.getWindow ());
		return w;
	}

	public static void openClientWorkbench (Item item, Object info,
			Context context) throws IOException
	{
		Socket s = Commands.getSocket (context, "localhost:58090");
		if (s != null)
		{
			Connection cx = new Connection (s);
			cx.start ();
			openClientWorkbench (cx, context);
		}
	}

	public static void openClientWorkbench (final Connection server, Context context)
			throws IOException
	{
		final IMPWorkbench wb = (IMPWorkbench) context.getWorkbench ();
		wb.removeConnection (server);
		final Registry r = new Registry (getInstance ().getRegistry ());

		final ClientConnection conn = new ClientConnection (new PersistenceBindings (
			r, r), server);

		r.createGraphs (conn);

		new Thread (new Runnable ()
		{
			public void run ()
			{
				try
				{
					conn.initialize (r.getRegistryGraph ());
					r.initialize ();
					r.initFileSystem (new MemoryFileSystem (de.grogra.pf.io.IO.PROJECT_FS));
				}
				catch (IOException e)
				{
					wb.logGUIInfo (null, e);
					return;
				}
				IMPWorkbench w = new IMPWorkbench (r, null);
				Registry.setCurrent (r);
				try
				{
					loadRegistry (r, null, true);
					conn.initialize (r.getProjectGraph ());
					w.setName (conn.getName () + '@'
						+ server.getSocket ().getRemoteSocketAddress ());
					w.ignoreIfModified ();
					getInstance ().start (w, wb.getWindow ());
					w.addConnection (server);
				}
				catch (IOException e)
				{
					wb.logGUIInfo (null, e);
				}
			}
		}, "OpenClientWorkbench@" + server).start ();
	}

	public static SourceFile getFileToAdd (Context ctx)
	{
		IMPWorkbench wb = (IMPWorkbench) ctx.getWorkbench ();
		FileChooserResult fr = wb.chooseFile (null,
			IO.getReadableFileTypes (new IOFlavor [] {IOFlavor.RESOURCE_LOADER}), Window.ADD_FILE, false, null); 
		if (fr == null)
		{
			return null;
		}
		MimeType mt = fr.getMimeType ();
		if (fr.file.exists ())
		{
			switch (ctx.getWindow ().showChoiceDialog (fr.file.getName (), UI.I18N,
				"addfiledialog", new String[] {"add", "link"}))
			{
				case 0:
					break;
				case 1:
					return new SourceFile (IO.toSystemId (fr.file), mt);
				default:
					return null;
			}
		}
		return toSourceFile (fr.file, mt, ctx);
	}
	
	public static SourceFile toSourceFile (File file, MimeType mt, Context ctx)
	{
		FileSystem fs = ctx.getWorkbench ().getRegistry ().getFileSystem ();
		Object f;
		try
		{
			if (file.exists ())
			{
				f = fs.addLocalFile (file, fs.getRoot (), file.getName ());
			}
			else
			{
				f = fs.create (fs.getRoot (), file.getName (), false);
			}
		}
		catch (IOException e)
		{
			ctx.getWorkbench().logGUIInfo (IO.I18N.msg ("addfile.failed", file), e);
			return null;
		}
		return new SourceFile (IO.toSystemId (fs, f), mt);
	}

	public static SourceFile addSourceFile (File file, MimeType mt, Context ctx)
	{
		SourceFile f = toSourceFile(file, mt, ctx);
		if (f != null)
		{
			ctx.getWorkbench ().getRegistry ().getDirectory ("/project/objects/files", null).addUserItem (f);
		}
		return f;
	}

	public static void addNode (Item item, Object info, Context context)
	{
		final Node node;
		final Expression expr;
		if (info instanceof Node)
		{
			node = (Node) info;
			expr = null;
		}
		else if (info instanceof ActionEditEvent)
		{
			node = null;
			ActionEditEvent e = (ActionEditEvent) info;
			if (e.isConsumed ()
				|| !((info = e.getSource ()) instanceof Expression))
			{
				return;
			}
			e.consume ();
			expr = (Expression) info;
		}
		else
		{
			return;
		}
		final Workbench w = context.getWorkbench ();
		UI.executeLockedly (w.getRegistry ().getProjectGraph (), true,
			new Command ()
			{
				public String getCommandName ()
				{
					return null;
				}

				public void run (Object arg, Context c)
				{
					Object o = (node != null) ? node : expr.evaluate (w, UI
						.getArgs (c, expr));
					if (!(o instanceof Node))
					{
						return;
					}
					((Node) o).setExtentIndex (Node.LAST_EXTENT_INDEX);
					GraphManager g = w.getRegistry ().getProjectGraph ();
					g.getRoot ()
						.addEdgeBitsTo (
							(Node) o,
							de.grogra.graph.Graph.BRANCH_EDGE,
							g.getActiveTransaction ());
				}
			}, null, context, JobManager.ACTION_FLAGS);
	}

	public static void addEdgeBits (final Node source, final Node target, final int bits, Context context)
	{
		final Workbench w = context.getWorkbench ();
		UI.executeLockedly (w.getRegistry ().getProjectGraph (), true,
			new Command ()
			{
				public String getCommandName ()
				{
					return null;
				}

				public void run (Object arg, Context c)
				{
					GraphManager g = w.getRegistry ().getProjectGraph ();
					source.addEdgeBitsTo (target, bits, g.getActiveTransaction ());
				}
			}, null, context, JobManager.ACTION_FLAGS);
	}
	
	public static void removeEdgeBits (final Node source, final Node target, final int bits, Context context)
	{
		final Workbench w = context.getWorkbench ();
		UI.executeLockedly (w.getRegistry ().getProjectGraph (), true,
			new Command ()
			{
				public String getCommandName ()
				{
					return null;
				}

				public void run (Object arg, Context c)
				{
					GraphManager g = w.getRegistry ().getProjectGraph ();
					source.removeEdgeBitsTo (target, bits, g.getActiveTransaction ());
				}
			}, null, context, JobManager.ACTION_FLAGS);
	}

	private static float clamp (float v)
	{
		return (v >= 1) ? 1 : (v <= 0) ? 0 : v;
	}

	public static Color getAWTColor (float r, float g, float b, float a)
	{
		return new Color (clamp (r), clamp (g), clamp (b), clamp (a));
	}

	public static Color getAWTColor (Tuple3f color)
	{
		return getAWTColor (color.x, color.y, color.z, 1);
	}

	public static Tuple3f setColor (Tuple3f t, int rgb)
	{
		t.set (((rgb >> 16) & 255) * (1f / 255), ((rgb >> 8) & 255)
			* (1f / 255), (rgb & 255) * (1f / 255));
		return t;
	}

	
	public static void exportGraphToFile (GraphManager graphManager, File file)
	{
		Workbench w = Workbench.current ();
		if (w == null)
		{
			throw new IllegalStateException ("no current workbench");
		}
		writeGraph (w, graphManager, MimeType.TEXT_XML, file);
	}
	
	
	public static void writeGraph (Context ctx, GraphManager graphManager, MimeType mt,	File file)
	{
		try
		{
			OutputStream out = new BufferedOutputStream (new FileOutputStream (file));
			writeGraph (ctx, graphManager, mt, out, file.getPath ());
			out.flush ();
			out.close ();
		}
		catch (IOException e)
		{
			ctx.getWorkbench ().logGUIInfo (I18N.msg ("export.graph.failed", file), e);
		}
	}
	
	public static void writeGraph (Context ctx, GraphManager graphManager, MimeType mt,
			OutputStream out, String outName)
	{
		FilterSource fs = IO.createPipeline (new ObjectSourceImpl (graphManager,
			"snapshot", ImageWriter.RENDERED_IMAGE_FLAVOR, ctx.getWorkbench ()
				.getRegistry ().getRootRegistry (), null), new IOFlavor (mt,
			IOFlavor.OUTPUT_STREAM, null));
		if (fs == null)
		{
			ctx.getWorkbench ().logGUIInfo (
				IO.I18N.msg ("save.unsupported", outName, IO.getDescription (mt)));
			return;
		}
		try
		{
			((OutputStreamSource) fs).write (out);
		}
		catch (IOException ex)
		{
			ctx.getWorkbench ().logGUIInfo (
				I18N.msg ("export.graph.failed", outName), ex);
		}
	}	
}
