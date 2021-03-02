
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

package de.grogra.pf.ui.tree;

import java.util.Vector;
import javax.swing.table.*;
import javax.swing.event.*;
import de.grogra.pf.ui.*;
import de.grogra.xl.util.ObjectList;

public class SyncMappedTable extends DefaultTableModel
	implements Synchronizer.Callback, TableModelListener
{
	protected final Synchronizer sync;
	protected final TableModel source;
	protected final ObjectList columnClasses;
	protected final Context context;


	public SyncMappedTable (TableModel source, Synchronizer sync, Context ctx)
	{
		super (source.getRowCount (), source.getColumnCount ());
		this.sync = sync;
		this.source = source;
		this.context = ctx;
		sync.initCallback (this);
		columnClasses = new ObjectList ();
		copyAll ();
	}


	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		tableChangedSync ((TableModelEvent) oarg1);
		return null;
	}


	@Override
	public void addTableModelListener (TableModelListener l)
	{
		boolean b = listenerList.getListenerCount () == 0;
		super.addTableModelListener (l);
		if (b)
		{
			source.addTableModelListener (this);
		}
	}


	@Override
	public void removeTableModelListener (TableModelListener l)
	{
		super.removeTableModelListener (l);
		if (listenerList.getListenerCount () == 0)
		{
			source.removeTableModelListener (this);
		}
	}


	@Override
	public boolean isCellEditable (int row, int column)
	{
		return source.isCellEditable (row, column); 
	}


	@Override
	public Class getColumnClass (int column)
	{
		return (Class) columnClasses.get (column);
	}


	@Override
	public void setValueAt (final Object value, final int row, final int column)
	{
		UI.getJobManager (context).execute
			(new Command ()
			{
				public String getCommandName ()
				{
					return null;
				}
				
				public void run (Object arg, Context ctx)
				{
					source.setValueAt (value, row, column);
				}
			}, null, context, JobManager.ACTION_FLAGS);
	}


	public void tableChanged (TableModelEvent e)
	{
		sync.invokeAndWait (0, e);
	}


	protected void tableChangedSync (TableModelEvent e)
	{
		copyAll ();
	}


	private void copyAll ()
	{
		int rc = source.getRowCount (), cc = source.getColumnCount ();
		Vector dv = new Vector (rc);
		for (int i = 0; i < rc; i++)
		{
			Vector row = new Vector (cc);
			for (int j = 0; j < cc; j++)
			{
				row.add (source.getValueAt (i, j));
			}
			dv.add (row);
		}
		Vector c = new Vector (cc);
		columnClasses.clear ();
		for (int j = 0; j < cc; j++)
		{
			c.add (source.getColumnName (j));
			columnClasses.add (source.getColumnClass (j));
		}
		setDataVector (dv, c);
	}

}
