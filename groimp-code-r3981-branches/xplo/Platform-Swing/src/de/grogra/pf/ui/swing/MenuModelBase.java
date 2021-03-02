
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

import javax.swing.*;

import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.awt.*;

abstract class MenuModelBase extends MappedComponentModel
{
	
	static final String WRAPPER = "de.grogra.pf.ui.swing.WRAPPER";

	MenuModelBase (UITree sourceTree)
	{
		super (sourceTree);
	}


	public void disposeNode (Object targetNode)
	{
		ButtonSupport b = ButtonSupport.get (targetNode);
		if (b != null)
		{
			b.dispose ();
		}
		ComponentWrapper w = (ComponentWrapper)
			((JComponent) targetNode).getClientProperty (WRAPPER);
		if (w != null)
		{
			w.dispose ();
		}
	}


	public void valueForPathChanged (javax.swing.tree.TreePath path,
									 Object newValue)
	{
		JComponent c = (JComponent) path.getLastPathComponent ();
		c.putClientProperty (SwingToolkit.SOURCE, newValue);
		ButtonSupport b = ButtonSupport.get (c);
		if (b != null)
		{
			b.updateState (newValue);
		}
	}


	public boolean isImage (Object sourceNode, Object targetNode)
	{
		return sourceTree.nodesEqual
			(sourceNode, SwingToolkit.getSource (targetNode));
	}

}
