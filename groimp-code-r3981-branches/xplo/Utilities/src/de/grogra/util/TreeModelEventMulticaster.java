
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

package de.grogra.util;

import javax.swing.event.*;

public class TreeModelEventMulticaster extends TreeModelSupport
	implements TreeModelListener
{
	public TreeModelEventMulticaster ()
	{
		super (false);
	}


	public void treeNodesChanged (TreeModelEvent e)
	{
		fireTreeModelEvent (NODES_CHANGED, e);
	}


	public void treeNodesInserted (TreeModelEvent e)
	{
		fireTreeModelEvent (NODES_INSERTED, e);
	}


	public void treeNodesRemoved (TreeModelEvent e)
	{
		fireTreeModelEvent (NODES_REMOVED, e);
	}


	public void treeStructureChanged (TreeModelEvent e)
	{
		fireTreeModelEvent (STRUCTURE_CHANGED, e);
	}

}
