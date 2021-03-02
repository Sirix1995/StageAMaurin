
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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.LogRecord;
import javax.swing.table.TableModel;
import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.icon.*;
import de.grogra.reflect.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.io.*;

public abstract class UIToolkit
{
	public static final Dimension ABOUT_ICON_SIZE = new Dimension (64, 64);
	public static final Dimension TOOLBAR_ICON_SIZE = new Dimension (32, 32);
	public static final Dimension MEDIUM_ICON_SIZE = new Dimension (22, 22);
	public static final Dimension MENU_ICON_SIZE = new Dimension (16, 16);
	public static final Dimension WINDOW_ICON_SIZE = new Dimension (16, 16);

	public static final String EXPLORER_ACTION = "explore";

	public static final String UPDATE_COMPONENT_WRAPPER_METHOD = "updateComponentWrapper";
	public static final String CREATE_COMPONENT_WRAPPER_METHOD = "createComponentWrapper";

	public static final int FONT_SIZE_MASK = 0x000ff;

	public static final int FONT_PLAIN = 0x00100;
	public static final int FONT_ITALIC = 0x00200;
	public static final int FONT_BOLD = 0x00400;

	public static final int FONT_MONOSPACED = 0x00800;
	public static final int FONT_SANS_SERIF = 0x01000;
	public static final int FONT_SERIF = 0x02000;
	public static final int FONT_DIALOG = 0x04000;
	public static final int FONT_DIALOG_INPUT = 0x08000;

	public static final int FONT_MASK = 0x0ffff;

	public static final int ALIGNMENT_LEADING = 0x10000;
	public static final int ALIGNMENT_CENTER = 0x20000;
	public static final int ALIGNMENT_TRAILING = 0x40000;

	public static final int FORCE_DIMENSION = 0x80000;
	public static final int FOR_MENU = 0x100000;


	public static UIToolkit get (Context ctx)
	{
		return ctx.getWorkbench ().getToolkit ();
	}


	protected static Object getFirstMatching (int flags, int[] masks,
											  Object[] values, Object def)
	{
		for (int i = 0; i < masks.length; i++)
		{
			if ((masks[i] & flags) != 0)
			{
				return values[i];
			}
		}
		return def;
	}


	protected static int getFirstMatching (int flags, int[] masks,
										   int[] values, int def)
	{
		for (int i = 0; i < masks.length; i++)
		{
			if ((masks[i] & flags) != 0)
			{
				return values[i];
			}
		}
		return def;
	}


	public Object createLabel (Described dp, int flags)
	{
		return createLabel (dp, MENU_ICON_SIZE, flags);
	}


	public Object createLabel (Described dp, Dimension size, int flags)
	{
		return createLabel
			((String) dp.getDescription (Described.NAME),
			 (IconSource) dp.getDescription (Described.ICON), size, flags);
	}


	public Object createLabel (String text, int flags)
	{
		return createLabel (text, null, MENU_ICON_SIZE, flags);
	}


	public Object createLabel (String text, IconSource icon, int flags)
	{
		return createLabel (text, icon, MENU_ICON_SIZE, flags);
	}


	public abstract Object createLabel (String text, IconSource icon,
										Dimension size, int flags);


	public Object createButton (I18NBundle bundle, String key, Dimension size, int flags, Command cmd, Context ctx)
	{
		return createButton
			(bundle.getString (key + ('.' + Described.NAME), key),
			 (IconSource) bundle.getObject (key + ('.' + Described.ICON), null),
			 size, flags, cmd, ctx);
	}


	public Object createButton (Described dp, int flags, Command cmd, Context ctx)
	{
		return createButton (dp, MENU_ICON_SIZE, flags, cmd, ctx);
	}


	public Object createButton (Described dp, Dimension size, int flags,
								Command cmd, Context ctx)
	{
		return createButton
			((String) dp.getDescription (Described.NAME),
			 (IconSource) dp.getDescription (Described.ICON), size, flags,
			 cmd, ctx);
	}


	public Object createButton (String text, int flags, Command cmd, Context ctx)
	{
		return createButton (text, null, MENU_ICON_SIZE, flags, cmd, ctx);
	}


	public Object createButton (String text, IconSource source, int flags,
								Command cmd, Context ctx)
	{
		return createButton (text, source, MENU_ICON_SIZE, flags, cmd, ctx);
	}


	public abstract Object createButton
		(String text, IconSource source, Dimension size, int flags,
		 Command cmd, Context ctx);

/*
	public static final String SCROLL = "scroll";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String DIRECTORY = "directory";
*/

	public abstract Object createLabeledComponent
		(Object component, Object label);


	public abstract Widget createNumericWidget
		(Type type, Quantity quantity, Map params);


	public abstract Widget createStringWidget (Map params);


	public abstract Widget createTreeChoiceWidget (UITree tree);


	public abstract Widget createChoiceWidget (javax.swing.ListModel list, boolean forMenu);


	public abstract Widget createBooleanWidget (boolean forMenu, Map params);


	public abstract Widget createColorWidget (Map params);

	
	public abstract ChartPanel createChartPanel (Context ctx, Map params);


	public abstract Panel createPanel (Context ctx, Disposable toDispose, Map params);


	public abstract Object createScrollPane (Object view);

	public abstract Object createTabbedPane
		(String[] titles, Object[] components);


	public abstract Object createContainer (int gap);


	public abstract Object createSplitContainer (int orientation);


	public abstract Object createContainer (int rows, int cols, int gap);


	public abstract Object createContainer (float[] weights, int gap);


	public abstract Object setBorder (Object component, int gap);


	public void addComponent (Object container, Object component,
							  Object constraints)
	{
		addComponent (container, component, constraints, -1);
	}


	public abstract void addComponent (Object container, Object component,
									   Object constraints, int index);


	public abstract void removeComponent (Object component);


	public abstract Object getParent (Object component);


	public abstract int indexOf (Object component);


	public abstract int getComponentCount (Object container);


	public abstract Object getComponent (Object container, int index);


	public abstract java.awt.Point getLocationOnScreen (Object component);


	public abstract int getWidth (Object component);


	public abstract int getHeight (Object component);


	public abstract void revalidate (Object component);


	public abstract void repaint (Object component);


	public void dispose (Object component)
	{
		if (getParent (component) == null)
		{
			return;
		}
		if (component instanceof Disposable)
		{
			((Disposable) component).dispose ();
		}
		else
		{
			for (int i = getComponentCount (component) - 1; i >= 0; i--)
			{
				dispose (getComponent (component, i));
			}
		}
		removeComponent (component);
	}


	public abstract Window createWindow (Command close, Map params);


	public abstract Panel createToolBar (Context context, Map params);


	public abstract Panel createStatusBar (Context context, Map params);


	public Panel createViewerPanel (Context context, String systemId, Map params)
	{
		try
		{
			return createViewerPanel (context,
									  IO.toURL (UI.getRegistry (context), systemId),
									  params);
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}


	public abstract Panel createViewerPanel (Context ctx, URL url, Map params);


	public abstract Object getTextViewerComponent (Panel viewerPanel);


	public abstract TextEditor createTextEditor (Context context, Map params);

	public abstract Console createConsole (Context context, Map params);

	public abstract ComponentWrapper createTree (UITree tree);
	
	public abstract ComponentWrapper createTreeInSplit (UITree tree, Object split);

	public abstract ComponentWrapper createComponentTree (UITree componentTree);

	public abstract ComponentWrapper createComponentMenu (UITree componentTree);

	public abstract Object createTextViewer
		(URL url, String mimeType, String content, Command hyperlink,
		 boolean asBrowser);
	
	public abstract void setContent (Object textViewer,
									 String mimeType, String content);
	
	public abstract void setContent (Object textViewer, URL content);

	public abstract ComponentWrapper createTable (TableModel table, Context ctx);

	public abstract int getSelectedRow (ComponentWrapper table);

	public abstract TableModel getTable (ComponentWrapper table);

/*	public abstract URLPair chooseURL (Window frame, String title,
									   String container);
*/

	public abstract void showPopupMenu (UITree menu, Object component,
										int x, int y);

	
	public Panel createLogViewer (final Context ctx, Map params)
	{
		final LoggingHandler handler = new LoggingHandler (ctx);
		handler.setFormatter
			(new HTMLLoggingFormatter (UI.I18N, UIToolkit.MEDIUM_ICON_SIZE));
		handler.setMimeType ("text/html");
		handler.setLevel (Workbench.SOFT_GUI_INFO);
		Object o = Utils.get (params, "logrecords", null);
		if (o instanceof LogRecord[][])
		{
			LogRecord[][] a = (LogRecord[][]) o;
			for (int i = 0; i < a.length; i++)
			{
				LogRecord[] b = a[i];
				if ((b != null) && (b.length > 0))
				{
					handler.beginGrouping ();
					for (int j = 0; j < b.length; j++)
					{
						handler.publish (b[j]);
					}
					handler.endGrouping ();
				}
			}
		}

		class Helper implements ComponentWrapper, JobManager.ExecutionListener
		{
			private boolean executing;

			public void dispose ()
			{
				UI.getJobManager (ctx).removeExecutionListener (this);
				ctx.getWorkbench ().getLogger ().removeHandler (handler);
			}
			
			public Object getComponent ()
			{
				return handler.getComponent ();
			}

			public void executionStarted (JobManager jm)
			{
				executing = true;
				handler.beginGrouping ();
			}

			public void executionFinished (JobManager jm)
			{
				if (executing)
				{
					executing = false;
					handler.endGrouping ();
				}
			}
		}

		ctx.getWorkbench ().getLogger ().addHandler (handler);
		Helper h = new Helper ();
		UI.getJobManager (ctx).addExecutionListener (h);

		Panel p = createPanel (ctx, null, params);
		p.setContent (h);
		return p;
	}


	public Object createAbout (PluginDescriptor plugin, String prefix,
							   ObjectList tabComponents)
	{
		Object c = createContainer (10);
		I18NBundle b = plugin.getI18NBundle ();
		Object l = createLabel
			(b.getString (prefix + ".Name", plugin.getPluginName ()),
			 (IconSource) b.getObject (prefix + ".Icon", null),
			 ABOUT_ICON_SIZE, ALIGNMENT_LEADING | FONT_DIALOG | FONT_BOLD | 18);
		addComponent (c, l, BorderLayout.NORTH);

		int n = (tabComponents != null) ? tabComponents.size () / 2 : 0;
		String s = b.getString (prefix + ".tabs", null);
		StringTokenizer t = null;
		if (s != null)
		{
			t = new StringTokenizer (s);
			n += t.countTokens ();
		}
		String[] titles = new String[n];
		Object[] components = new Object[n];
		int i = 0;
		if (t != null)
		{
			while (t.hasMoreTokens ())
			{
				s = t.nextToken ();
				String r = b.getString (prefix + ".tab." + s + ".content",
										null);
				URL u;
				Object v;
				if ((r != null)
					&& ((u = plugin.getURLForResource (r)) != null)
					&& ((v = createTextViewer (u, null, null, null, false)) != null))
				{
					titles[i] = b.getString (prefix + ".tab." + s, s);
					components[i++] = setBorder (createScrollPane (v), 4);
				}
			}
		}
		if (tabComponents != null)
		{
			for (int k = 0; k < tabComponents.size (); k += 2)
			{
				titles[i] = (String) tabComponents.get (k);
				components[i++] = setBorder (tabComponents.get (k + 1), 4);
			}
		}
		if (i > 0)
		{
			if (i < n)
			{
				System.arraycopy (titles, 0, titles = new String[i], 0, i);
				System.arraycopy (components, 0,
								  components = new Object[i], 0, i);
			}
			addComponent (c, createTabbedPane (titles, components),
						  BorderLayout.CENTER);
		}
		return c;
	}

}
