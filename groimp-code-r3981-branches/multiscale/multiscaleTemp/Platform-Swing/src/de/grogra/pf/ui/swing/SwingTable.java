
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

package de.grogra.pf.ui.swing;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.awt.AWTSynchronizer;
import de.grogra.pf.ui.tree.SyncMappedTable;
import de.grogra.util.Disposable;

class SwingTable extends JTable implements Disposable//, EventListener
{

/*	private class Listener extends MouseAdapter implements KeyListener
	{
		public void mousePressed (MouseEvent e)
		{
			TreePath p;
			if ((e.getClickCount () == 2)
				&& ((p = getSelectionPath ()) != null)
				&& getUITree ().isLeaf (getNode (p.getLastPathComponent ()))
				&& (getRowForLocation (e.getX (), e.getY ())
					== getRowForPath (p)))
			{
				e.consume ();
				actionPerformed (e, getNode (p.getLastPathComponent ()));
			}
		}


		public void keyPressed (KeyEvent e)
		{
		}


		public void keyReleased (KeyEvent e)
		{
		}


		public void keyTyped (KeyEvent e)
		{
			TreePath p;
			if ((e.getKeyChar () == '\n')
				&& ((p = getSelectionPath ()) != null)
				&& getUITree ().isLeaf (getNode (p.getLastPathComponent ())))
			{
				e.consume ();
				actionPerformed (e, getNode (p.getLastPathComponent ()));
			}
		}

	}
*/

	final TableModel srcTable;

	SwingTable (TableModel table, Context ctx)
	{
		super (new SyncMappedTable (table, new AWTSynchronizer (null), ctx));
		setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
		setCellSelectionEnabled (true);
		this.srcTable = table;
/*		Listener l = new Listener ();
		addMouseListener (l);
		addKeyListener (l);
*/
	}


	public void dispose ()
	{
		setModel (new DefaultTableModel ());
	}

/*
	void actionPerformed (InputEvent e, Object node)
	{
		Context c = getUITree ().getContext ();
		UIUtils.getJobManager (c).postEvent
			(new ActionEditEvent (e.getModifiers ()).set (c, node), this);
	}


	public void eventOccured (java.util.EventObject event, de.grogra.vmx.VMXState t)
	{
		getUITree ().eventOccured (((EditEvent) event).getObject (), event);
	}
*/
}
