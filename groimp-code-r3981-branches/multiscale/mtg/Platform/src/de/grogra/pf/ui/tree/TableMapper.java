
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

import javax.swing.table.*;
import javax.swing.event.*;

public class TableMapper extends AbstractTableModel
	implements TreeModelListener
{
	protected final UITree source;
	protected final String[] names;
	protected final String[] keys;
	protected final boolean listen;


	public TableMapper (UITree source, String[] names, String[] keys,
						boolean listen)
	{
		this.source = source;
		this.names = names;
		this.keys = keys;
		this.listen = listen;
	}


	@Override
	public void addTableModelListener (TableModelListener l)
	{
		boolean b = listenerList.getListenerCount () == 0;
		super.addTableModelListener (l);
		if (b && listen)
		{
			source.addTreeModelListener (this);
		}
	}


	@Override
	public void removeTableModelListener (TableModelListener l)
	{
		super.removeTableModelListener (l);
		if (listenerList.getListenerCount () == 0)
		{
			source.removeTreeModelListener (this);
		}
	}


	public void treeNodesChanged (TreeModelEvent e)
	{
		// TODO Auto-generated method stub

	}


	public void treeNodesInserted (TreeModelEvent e)
	{
		// TODO Auto-generated method stub

	}


	public void treeNodesRemoved (TreeModelEvent e)
	{
		// TODO Auto-generated method stub

	}


	public void treeStructureChanged (TreeModelEvent e)
	{
		// TODO Auto-generated method stub

	}


	public int getRowCount ()
	{
		return source.getChildCount (source.getRoot ());
	}


	public int getColumnCount ()
	{
		return names.length;
	}


	public Object getValueAt (int rowIndex, int columnIndex)
	{
		return getColumnValue
			(source.getChild (source.getRoot (), rowIndex), columnIndex);
	}


	public Object getColumnValue (Object node, int column)
	{
		return source.invoke (node, "getValue", keys[column]);
	}


	@Override
	public String getColumnName (int column)
	{
		return names[column];
	}

}
