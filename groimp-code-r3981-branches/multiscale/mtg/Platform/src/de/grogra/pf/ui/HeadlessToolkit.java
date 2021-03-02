
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
import java.awt.HeadlessException;
import java.awt.Point;
import java.net.URL;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import de.grogra.icon.IconSource;
import de.grogra.pf.ui.tree.UITree;
import de.grogra.reflect.Type;
import de.grogra.util.Disposable;
import de.grogra.util.Map;
import de.grogra.util.Quantity;

public class HeadlessToolkit extends UIToolkit
{

	@Override
	public Panel createPanel (Context ctx, Disposable toDispose, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Console createConsole (Context context, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Panel createStatusBar (Context context, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Window createWindow (Command close, Map params)
	{
		return null;
	}

	@Override
	public Panel createViewerPanel (Context ctx, URL url, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public TextEditor createTextEditor (Context context, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Panel createToolBar (Context context, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createTextViewer (URL url, String mimeType, String content, Command hyperlink, boolean asBrowser)
	{
		throw new HeadlessException ();
	}

	@Override
	public void setContent (Object textViewer, String mimeType, String content)
	{
		throw new HeadlessException ();
	}

	@Override
	public void setContent (Object textViewer, URL content)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createLabel (String text, IconSource icon, Dimension size, int flags)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createButton (String text, IconSource source, Dimension size, int flags, Command cmd, Context ctx)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createLabeledComponent (Object component, Object label)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createNumericWidget (Type type, Quantity quantity, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createStringWidget (Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createTreeChoiceWidget (UITree tree)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createChoiceWidget (ListModel list, boolean forMenu)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createBooleanWidget (boolean forMenu, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Widget createColorWidget (Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public ChartPanel createChartPanel (Context ctx, Map params)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createScrollPane (Object view)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createTabbedPane (String[] titles, Object[] components)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createContainer (int gap)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createSplitContainer (int orientation)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createContainer (int rows, int cols, int gap)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object createContainer (float[] weights, int gap)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object setBorder (Object component, int gap)
	{
		throw new HeadlessException ();
	}

	@Override
	public void addComponent (Object container, Object component, Object constraints, int index)
	{
		throw new HeadlessException ();
	}

	@Override
	public void removeComponent (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object getParent (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public int indexOf (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public int getComponentCount (Object container)
	{
		throw new HeadlessException ();
	}

	@Override
	public Object getComponent (Object container, int index)
	{
		throw new HeadlessException ();
	}

	@Override
	public Point getLocationOnScreen (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public int getWidth (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public int getHeight (Object component)
	{
		throw new HeadlessException ();
	}

	@Override
	public void revalidate (Object component)
	{
	}

	@Override
	public void repaint (Object component)
	{
	}

	@Override
	public Object getTextViewerComponent (Panel viewerPanel)
	{
		throw new HeadlessException ();
	}

	@Override
	public ComponentWrapper createTree (UITree tree)
	{
		throw new HeadlessException ();
	}

	@Override
	public ComponentWrapper createComponentTree (UITree componentTree)
	{
		throw new HeadlessException ();
	}

	@Override
	public ComponentWrapper createComponentMenu (UITree componentTree)
	{
		throw new HeadlessException ();
	}

	@Override
	public ComponentWrapper createTable (TableModel table, Context ctx)
	{
		throw new HeadlessException ();
	}

	@Override
	public int getSelectedRow (ComponentWrapper table)
	{
		throw new HeadlessException ();
	}

	@Override
	public TableModel getTable (ComponentWrapper table)
	{
		throw new HeadlessException ();
	}

	@Override
	public void showPopupMenu (UITree menu, Object component, int x, int y)
	{
		throw new HeadlessException ();
	}

	@Override
	public ComponentWrapper createTreeInSplit(UITree tree, Object split) {
		throw new HeadlessException ();
	}

}
