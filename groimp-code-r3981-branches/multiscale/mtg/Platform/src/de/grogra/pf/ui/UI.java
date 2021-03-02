
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

import java.awt.Point;
import java.awt.event.InputEvent;
import java.net.URL;
import java.util.EventObject;

import de.grogra.graph.impl.Node;
import de.grogra.icon.IconOverlay;
import de.grogra.icon.IconSource;
import de.grogra.icon.IconTheme;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.MimeTypeItem;
import de.grogra.pf.io.ProgressMonitor;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.registry.Plugin;
import de.grogra.pf.registry.PluginClassLoader;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.event.InputEditEvent;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.pf.ui.tree.ChoiceGroupBuilder;
import de.grogra.pf.ui.tree.HierarchyFlattener;
import de.grogra.pf.ui.tree.LinkResolver;
import de.grogra.pf.ui.tree.RegistryAdapter;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.pf.ui.tree.UITree;
import de.grogra.pf.ui.tree.UITreePipeline;
import de.grogra.util.Described;
import de.grogra.util.I18NBundle;
import de.grogra.util.Lock;
import de.grogra.util.LockProtectedRunnable;
import de.grogra.util.Lockable;
import de.grogra.util.Map;
import de.grogra.util.ModifiableMap;
import de.grogra.util.ResourceConverter;
import de.grogra.util.StringMap;
import de.grogra.util.ThreadContext;
import de.grogra.util.Utils;
import de.grogra.xl.util.ObjectList;

public final class UI extends Plugin implements ResourceConverter
{
	public static final I18NBundle I18N = I18NBundle.getInstance (UI.class);

	private static UI PLUGIN;


	public UI ()
	{
		assert PLUGIN == null;
		PLUGIN = this;
	}


	public static IconSource getIcon (String name)
	{
		return (IconSource) PLUGIN.convert ("icon", name, null);
	}

	
	@Override
	public void startup ()
	{
		super.startup ();
		I18NBundle.addResourceConverter (this);
	}


	public boolean canHandleConversion (String name)
	{
		return "icon".equals (name);
	}


	public Object convert (String name, String argument, I18NBundle bundle)
	{
		if ("icon".equals (name))
		{
			Item d = getRegistry ().getItem ("/ui/iconthemes");
			if (d != null)
			{
				for (d = (Item) d.getBranch (); d != null;
					 d = (Item) d.getSuccessor ())
				{
					if (d instanceof ObjectItem)
					{
						Object o = ((ObjectItem) d).getObject ();
						if (o instanceof IconTheme)
						{
							IconSource s
								= ((IconTheme) o).getSource (argument);
							if (s != null)
							{
								return s;
							}
						}
					}
				}
			}
			return null;
		}
		else
		{
			throw new IllegalArgumentException (name);
		}
	}


	public static JobManager getJobManager (Context ctx)
	{
		return ctx.getWorkbench ().getJobManager ();
	}


	public static ThreadContext getThreadContext (Context ctx)
	{
		return ctx.getWorkbench ().getJobManager ().getThreadContext ();
	}


	public static Node getRootOfProjectGraph (Context ctx)
	{
		return ctx.getWorkbench ().getRegistry ().getProjectGraph ().getRoot ();
	}

	
	public static Registry getRegistry (Context ctx)
	{
		return ctx.getWorkbench ().getRegistry ();
	}


	public static StringMap getArgs (Context ctx, Map parent)
	{
		return new StringMap (parent)
			.putObject ("registry", ctx.getWorkbench ().getRegistry ())
			.putObject ("context", ctx);
	}


	public static UITree getUITreeForMenu (Context ctx, Item menu, de.grogra.util.Map pipelineParams)
	{
		if (menu == null)
		{
			return null;
		}
		UITreePipeline p = new UITreePipeline ();
		Item i = menu.getItem ("src");
		if (i != null)
		{
			Item xf = menu.getItem ("xf");
			menu = i;
			if (xf != null)
			{
				StringMap args = getArgs (ctx, pipelineParams);
				for (i = (Item) xf.getBranch (); i != null;
					 i = (Item) i.getSuccessor ())
				{
					if (i instanceof Expression)
					{
						args.put ("item", i);
						p.add ((UITreePipeline.Transformer) ((Expression) i)
							   .evaluate (ctx.getWorkbench (), args));
					}
				}
			}
		}
		p.add (new LinkResolver ());
		p.add (new ChoiceGroupBuilder ());
		p.add (new HierarchyFlattener ());
		p.initialize (new RegistryAdapter (ctx), menu, pipelineParams);
		return p;
	}

	public static void setMenu (Panel panel, Item menu, de.grogra.util.Map pipelineParams)
	{
		UITree t = getUITreeForMenu (panel, menu, pipelineParams);
		if (t != null)
		{
			panel.setMenu (t);
		}
	}

	public static boolean isAvailable (Item i, Context ctx)
	{
		return isAvailable (i, ctx, ".available");
	}


	public static boolean isEnabled (Item i, Context ctx)
	{
		return isAvailable (i, ctx, ".enabled");
	}

	
	private static boolean isAvailable (Item i, Context ctx, String prefix)
	{
		StringMap params = null;
		for (i = (Item) i.getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			if ((i instanceof Expression)
				&& i.getName ().startsWith (prefix)) 
			{
				if (params == null)
				{
					params = new StringMap (i).putObject ("context", ctx);
				}
				if (!Expression.evaluateBoolean (i, ctx.getWorkbench (), params))
				{
					return false;
				}
			}
		}
		return true;
	}


	public static boolean isConsumed (EventObject e)
	{
		return (e instanceof InputEditEvent)
			? ((InputEditEvent) e).isConsumed ()
			: (e instanceof InputEvent) ? ((InputEvent) e).isConsumed ()
			: false;
	}


	public static void consume (Object event)
	{
		if (event instanceof InputEditEvent)
		{
			((InputEditEvent) event).consume ();
		}
		else if (event instanceof InputEvent)
		{
			((InputEvent) event).consume ();
		}
	}


	public static void dumpCommand (Item item, Object info, Context ctx)
	{
		System.err.println ("Item: " + item);
		System.err.println ("Info: " + info);
		System.err.println ("Ctx : " + ctx);
	}


	public static ProgressMonitor createProgressAdapter (Context ctx)
	{
		final Workbench w = ctx.getWorkbench ();
		if ((w == null) || !w.isWorkbenchThread ())
		{
			return null;
		}

		return new ProgressMonitor ()
		{
			public void setProgress (String text, float progress)
			{
				w.beginStatus (this);
				if (progress == INDETERMINATE_PROGRESS)
				{
					w.setIndeterminateProgress (this);
				}
				else if (progress == DONE_PROGRESS)
				{
					w.clearProgress (this);
				}
				else
				{
					w.setProgress (this, progress);
				}
				w.setStatus (this, text);
			}
		};
	}


	public static String getClassDescription (Class cls)
	{
		String s = cls.getName ();
		int i = s.lastIndexOf ('.');
		if (i >= 0)
		{
			s = s.substring (i + 1);
		}
		return (cls.getClassLoader () instanceof PluginClassLoader)
			? ((PluginClassLoader) cls.getClassLoader ())
				.getPluginDescriptor ().getI18NBundle ()
				.getString (cls.getName (), s)
			: s;
	}


	public static Described nodeToDescribed
		(final UINodeHandler h, final Object node)
	{
		return new Described ()
		{
			public Object getDescription (String type)
			{
				return h.getDescription (node, type);
			}
		};
	}


	public static Map configureViewerParams
		(Map params, URL url, String mimeType, RegistryContext ctx)
	{
		StringMap sm = null;
		if (mimeType != null)
		{
			Object icon = getIcon (null, mimeType, null, ctx, false);
			if (icon != null)
			{
				if (sm == null)
				{
					sm = new StringMap (params);
				}
				sm.put (UIProperty.ICON.getName (), icon);
			}
		}
		if (url != null)
		{
			String name = url.getPath ();
			name = name.substring (name.lastIndexOf ('/') + 1);
			if (sm == null)
			{
				sm = new StringMap (params);
			}
			sm.put (UIProperty.PANEL_TITLE.getName (), name);
		}
		return (sm != null) ? sm : params;
	}


	public static void executeHyperlinkURL (String url, Context ctx)
	{
		if ((url == null) || (ctx == null))
		{
			return;
		}
		if (url.startsWith ("command:"))
		{
			int i = url.indexOf ('?');
			String cmd = (i >= 0) ? url.substring ("command:".length (), i)
				: url.substring ("command:".length ());
			if (!cmd.startsWith ("/"))
			{
				cmd = "/ui/commands/" + cmd;
			}
			Item c = Item.resolveItem (ctx.getWorkbench (), cmd);
			if (c instanceof Command)
			{
				getJobManager (ctx).execute
					((Command) c, (i >= 0) ? url.substring (i + 1) : null, ctx,
					 JobManager.ACTION_FLAGS);
			}
			return;
		}
		int i = url.indexOf ('#');
		String file = (i >= 0) ? url.substring (0, i) : url;
		SourceFile s = SourceFile.get (ctx.getWorkbench (), file);
		if (s != null)
		{
			s.show (ctx, (i >= 0) ? url.substring (i + 1) : null);
		}
	}

	
	public static Point[] parsePlainTextRange (String ref)
	{
		if (ref == null)
		{
			return null;
		}
		int i = ref.indexOf ('-');
		if (i < 0)
		{
			Point a = parsePlainTextLocation (ref);
			return (a == null) ? null : new Point[] {a, null};
		}
		else
		{
			Point start = parsePlainTextLocation (ref.substring (0, i)),
				end = parsePlainTextLocation (ref.substring (i + 1));
			return (start != null) ? new Point[] {start, end}
				: (end != null) ? new Point[] {end, null} : null;
		}
	}

	
	public static Point parsePlainTextLocation (String ref)
	{
		if (ref == null)
		{
			return null;
		}
		try
		{
			int i = ref.indexOf (':');
			if (i < 0)
			{
				i = Integer.parseInt (ref.trim ());
				return new Point (-1, i);
			}
			else
			{
				int line = Integer.parseInt (ref.substring (0, i).trim ());
				i = Integer.parseInt (ref.substring (i + 1));
				return new Point (i, line);
			}
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}


	public static void putDocuments (TextEditor e, ModifiableMap out)
	{
		String[] d = e.getDocuments ();
		if (d.length == 0)
		{
			return;
		}
		StringBuffer b = new StringBuffer ();
		for (int i = 0; i < d.length; i++)
		{
			if (i > 0)
			{
				b.append (',');
			}
			b.append (Utils.quote (d[i]));
		}
		out.put ("documents", b.toString ());
	}


	public static String[] getDocuments (Map params)
	{
		String s = (String) params.get ("documents", null);
		if (s == null)
		{
			return Utils.STRING_0;
		}
		ObjectList v = new ObjectList ();
		int i = 0;
		int[] end = new int[1];
		while (i < s.length ())
		{
			v.add (Utils.unquote (s, i, -1, end));
			i = end[0];
			if ((i < s.length ()) && (s.charAt (i) == ','))
			{
				i++;
			}
		}
		return (String[]) v.toArray (new String[v.size ()]);
	}

	
	public static IconSource getIcon (String systemId, String mimeType,
									  IconSource base, RegistryContext ctx,
									  boolean useDefaultIcon)
	{
		if (base == null)
		{ 
			Item i = MimeTypeItem.get (ctx, mimeType);
			if (i != null)
			{
				base = (IconSource) i.getDescription (Described.ICON);
			}
		}
		if (base == null)
		{
			if (useDefaultIcon)
			{
				base = (IconSource) UI.I18N.getObject ("registry.file.Icon");
			}
			else
			{
				return null;
			}
		}
		if ((systemId != null) && !systemId.startsWith (IO.PROJECT_FS + ':'))
		{
			IconSource link = (IconSource)
				UI.I18N.getObject ("registry.link.Icon");
			if (link != null)
			{
				return new IconOverlay (base, link);
			}
		}
		return base;
	}
	
	
	public static ModifiableMap getOptions (Context ctx)
	{
		final Item opt = Item.resolveItem (ctx.getWorkbench (), "/ui/options");
		return new ModifiableMap ()
		{
			public Object get (Object key, Object def)
			{
				return (opt != null) ? opt.get (key, def) : def;
			}
			
			public Object put (Object key, Object value)
			{
				if (opt != null)
				{
					Object old = opt.get (key, null);
					opt.setOption ((String) key, value);
					return old;
				}
				return null;
			}
		};
	}

	
	public static void executeLockedly
		(final Lockable resource, final boolean write,
		 final Command cmd, final Object arg, final Context ctx, final int flags, final boolean passLockAsArg)
	{
		class Task implements LockProtectedRunnable, Command
		{
			private boolean executed;
			private Object monitor;
			private Lock retainedLock;

			public void run (boolean sameThread, Lock lock)
			{
				JobManager j = getJobManager (ctx);
				if (j.getThreadContext ().isCurrent ())
				{
					executed = true;
					cmd.run (passLockAsArg ? lock : arg, ctx);
				}
				else if (monitor == null)
				{
					j.runLater (this, null, ctx, flags);
				}
				else
				{
					lock.retain ();
					retainedLock = lock;
					synchronized (monitor)
					{
						monitor.notifyAll ();
					}
				}
			}
			
			public void run (Object o, Context c)
			{
				monitor = new Object ();
				synchronized (monitor)
				{
					resource.execute (this, write);
					if (executed)
					{
						return;
					}
					while (retainedLock == null)
					{
						try
						{
							monitor.wait ();
						}
						catch (InterruptedException e)
						{
						}
					}
				}
				Utils.executeForcedlyAndUninterruptibly (resource, this, retainedLock);
			}
			
			public String getCommandName ()
			{
				return cmd.getCommandName ();
			}
		}

		resource.execute (new Task (), write);
	}

	public static void executeLockedly
		(final Lockable resource, final boolean write,
		 final Command cmd, final Object arg, final Context ctx, final int flags)
	{
		executeLockedly(resource, write, cmd, arg, ctx, flags, false);
	}

	public static void setField
		(final Node node, final PersistenceField field,
		 Object value, Context ctx)
	{
		executeLockedly (node.getGraph (), true, new Command ()
		{
			public String getCommandName ()
			{
				return null;
			}

			public void run (Object o, Context c)
			{
				field.set (node, null, o, node.getGraph ().getActiveTransaction ());
			}
		}, value, ctx, JobManager.ACTION_FLAGS);
	}

}
