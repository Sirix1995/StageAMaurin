
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

package de.grogra.pf.ui.edit;

import de.grogra.util.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.event.*;

public class PropertyEditorManager implements Disposable, EventListener
{
	private boolean disposed = false;
	private Panel panel;


	public PropertyEditorManager (Panel panel, boolean listenToSelection)
	{
		this.panel = panel;
		UIProperty.WORKBENCH_SELECTION.addPropertyListener (panel, this);
	}


	public void eventOccured (java.util.EventObject event)
	{
		Object n = ((UIPropertyEditEvent) event).getNewValue ();
		if ((n == null) || (n instanceof Selection))
		{
			setSelection ((Selection) n);
		}
	}


	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		UIProperty.WORKBENCH_SELECTION.removePropertyListener (panel, this);
		disposed = true;
		setSelection (null);
	}


	private void setSelection (Selection source)
	{
		panel.setContent (null);
		if ((source != null) && setSelection0 (source))
		{
			panel.show (false, source.getContext ().getPanel ());
		}
	}

	private boolean setSelection0 (Selection sel)
	{
		ComponentWrapper w = sel.createPropertyEditorComponent ();
		if (w == null)
		{
			return false;
		}
		panel.setContent (w);
		return true;
	}

	public static Panel createEditor (Context ctx, Map params)
	{
		DisposableWrapper w = new DisposableWrapper ();
		Panel p = UIToolkit.get (ctx).createPanel (ctx, w, params);
		PropertyEditorManager mgr = new PropertyEditorManager (p, true);
		Object sel = UIProperty.WORKBENCH_SELECTION.getValue (p);
		if (sel instanceof Selection)
		{
			mgr.setSelection0 ((Selection) sel);
		}
		w.disposable = mgr;
		return p;
	}

}
