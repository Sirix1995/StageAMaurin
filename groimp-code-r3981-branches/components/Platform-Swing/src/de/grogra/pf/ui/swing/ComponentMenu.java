
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

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreePath;
import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.tree.*;

class ComponentMenu extends MenuModel
{

	private Disposable toDispose;


	public ComponentMenu (UITree sourceTree, Disposable toDispose)
	{
		super (sourceTree, null, false);
		this.toDispose = toDispose;
	}


	@Override
	protected void disposeImpl ()
	{
		super.disposeImpl ();
		if (toDispose != null)
		{
			toDispose.dispose ();
			toDispose = null;
		}
	}


	@Override
	protected JComponent createNodeImpl (Object sourceNode, Object targetParent)
	{
		if (isSourceRoot (sourceNode))
		{
			return new JMenu ("ROOT");
		}
		else if (sourceTree.isLeaf (sourceNode))
		{
			ComponentWrapper w = (ComponentWrapper) sourceTree.invoke
				(sourceNode, UIToolkit.CREATE_COMPONENT_WRAPPER_METHOD, null);
			if (w != null)
			{
				AbstractButton b = (AbstractButton) w.getComponent ();
				setEnabled (b, sourceTree.isEnabled (sourceNode));
				b.putClientProperty (WRAPPER, w);
				b.setText ((String) sourceTree.getDescription (sourceNode, Described.NAME));
				return b;
			}
		}
		return super.createNodeImpl (sourceNode, targetParent);
	}


	@Override
	public void valueForPathChanged (TreePath path, Object newValue)
	{
		ComponentWrapper w = (ComponentWrapper)
			((JComponent) path.getLastPathComponent ()).getClientProperty (WRAPPER);
		if (w != null)
		{
			Object src = SwingToolkit.getSource (w.getComponent ());
			sourceTree.invoke
				(src, UIToolkit.UPDATE_COMPONENT_WRAPPER_METHOD, w);
			setEnabled ((Component) w.getComponent (),
						sourceTree.isEnabled (src));
		}
	}

}
