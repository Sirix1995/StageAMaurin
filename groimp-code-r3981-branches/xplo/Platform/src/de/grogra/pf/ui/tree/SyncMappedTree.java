
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

import javax.swing.tree.*;
import de.grogra.pf.ui.*;
import de.grogra.util.*;

public class SyncMappedTree extends MappedTree
	implements Synchronizer.Callback
{
	protected final Synchronizer sync;
	protected final TreeMapper mapper;


	public SyncMappedTree (TreeModel sourceTree, Object sourceRoot,
						   Synchronizer sync)
	{
		this.sync = sync;
		sync.initCallback (this);
		this.mapper = new TreeMapper (sourceTree, sourceRoot, this, null);
		mapper.map ();
	}


	public TreeModel getSourceTree ()
	{
		return mapper.getSourceTree ();
	}


	protected static final int INSERT = 0;
	protected static final int REMOVE = 1;
	protected static final int SET_ROOT = 2;
	protected static final int VALUE_FOR_PATH_CHANGED = 3;

	protected static final int ACTION_COUNT = 4;


	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		switch (action)
		{
			case INSERT:
				Object[] a = (Object[]) oarg2;
				insertSync (oarg1, iarg, a[0], a[1]);
				break;
			case REMOVE:
				removeSync (oarg1, iarg);
				break;
			case SET_ROOT:
				setRootSync (oarg1);
				break;
			case VALUE_FOR_PATH_CHANGED:
				valueForPathChangedSync ((TreePath) oarg1, oarg2);
				break;
			default:
				throw new AssertionError (action);
		}
		return null;
	}


	@Override
	public void insert (Object parent, int index, Object child,
						Object constraints)
	{
		sync.invokeAndWait (INSERT, index, parent,
							new Object[] {child, constraints});
	}


	protected void insertSync (Object parent, int index, Object child,
							   Object constraints)
	{
		super.insert (parent, index, child, constraints);
	}


	@Override
	public void remove (Object parent, int index)
	{
		sync.invokeAndWait (REMOVE, index, parent, null);
	}


	protected void removeSync (Object parent, int index)
	{
		super.remove (parent, index);
	}


	@Override
	public void setRoot (Object root)
	{
		sync.invokeAndWait (SET_ROOT, root);
	}


	protected void setRootSync (Object root)
	{
		super.setRoot (root);
	}


	@Override
	public void valueForPathChanged (TreePath path, Object newValue)
	{
		sync.invokeAndWait (VALUE_FOR_PATH_CHANGED, 0, path, newValue);
	}


	protected void valueForPathChangedSync (TreePath path, Object newValue)
	{
		super.valueForPathChanged (path, newValue);
	}


	@Override
	protected void firstListenerAdded ()
	{
		mapper.installListener ();
	}


	@Override
	protected void allListenersRemoved ()
	{
		mapper.dispose ();
	}

}
