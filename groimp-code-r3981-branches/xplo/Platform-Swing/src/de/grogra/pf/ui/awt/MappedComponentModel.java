
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

package de.grogra.pf.ui.awt;

import de.grogra.pf.ui.tree.*;
import de.grogra.util.*;

public abstract class MappedComponentModel extends ComponentModel
	implements MappedTreeModel, java.util.Comparator
{
	protected final UITree sourceTree;
	protected final TreeMapper mapper;
	protected final Object sourceRoot;
	private boolean disposed = false;


	public MappedComponentModel (UITree sourceTree, Object sourceRoot)
	{
		super ();
		this.sourceTree = sourceTree;
		this.sourceRoot = sourceRoot;
		this.mapper = new TreeMapper (sourceTree, sourceRoot, this, this);
	}


	public MappedComponentModel (UITree sourceTree)
	{
		this (sourceTree, sourceTree.getRoot ());
	}

	
	public AWTToolkitBase getToolkit ()
	{
		return (AWTToolkitBase) sourceTree.getContext ().getWorkbench ().getToolkit ();
	}


	public int compare (Object a, Object b)
	{
		return sourceTree.nodesEqual (a, b) ? 0 : 1;
	}


	public void map (boolean attach)
	{
		mapper.map ();
		if (attach)
		{
			mapper.installListener ();
		}
	}


	protected boolean isSourceRoot (Object sourceNode)
	{
		return sourceTree.nodesEqual (sourceNode, sourceRoot);
	}


	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		mapper.dispose ();
		disposeImpl ();
	}


	protected void disposeImpl ()
	{
	}


	protected void revalidate (java.awt.Component c)
	{
		de.grogra.pf.ui.UIToolkit.get (sourceTree.getContext ()).revalidate (c);
	}


	@Override
	protected void treeChangedSync (java.awt.Container c)
	{
		revalidate (c);
	}


	public UITree getSourceTree ()
	{
		return sourceTree;
	}
}
