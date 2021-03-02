/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.components;

import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.ComponentSelection;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.util.Disposable;
import de.grogra.util.DisposableWrapper;
import de.grogra.util.EventListener;
import de.grogra.util.Map;

public class ComponentDescriptionManager implements Disposable, EventListener
{			 
	private boolean disposed = false;
	private final Panel panel;

	public ComponentDescriptionManager (Panel panel, boolean listenToSelection)
	{
		this.panel = panel;
		UIProperty.WORKBENCH_SELECTION.addPropertyListener (panel, this);
	}


	@Override
	public void eventOccured (java.util.EventObject event)
	{
		Object n = ((UIPropertyEditEvent) event).getNewValue ();
		if ((n == null) || (n instanceof ComponentSelection))
		{
			setSelection ((ComponentSelection) n);
		}
	}


	@Override
	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		
		//yong 20 november 2012 - removing this listener will cause the component descriptor panel to fail to refresh when another component is selected in the component explorer.
		//UIProperty.WORKBENCH_SELECTION.removePropertyListener (panel, this); 
		
		disposed = true;
		setSelection (null);
	}


	private void setSelection (ComponentSelection source)
	{
		panel.setContent (null);
		if ((source != null) && setSelection0 (source))
		{
			panel.show (false, source.getContext().getPanel ());
		}
	}

	private boolean setSelection0 (ComponentSelection sel)
	{
		ComponentWrapper w = sel.createComponentDescriptionComponent (this);
		if (w == null)
		{
			return false;
		}
		panel.setContent (w);
		return true;
	}

	public static Panel createPanel (Context ctx, Map params)
	{
		DisposableWrapper w = new DisposableWrapper ();
		Panel p = UIToolkit.get (ctx).createPanel (ctx, w, params);
		
		ComponentDescriptionManager mgr = new ComponentDescriptionManager (p, true);
		Object sel = UIProperty.WORKBENCH_SELECTION.getValue (p);
		if (sel instanceof ComponentSelection)
		{
			mgr.setSelection0 ((ComponentSelection) sel);
		}
		w.disposable = mgr;
		
		return p;
	}

}
