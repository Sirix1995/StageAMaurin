
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

package de.grogra.docking;

import java.awt.*;
import java.awt.dnd.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.plaf.*;

public class DockedFrame extends JPanel
	implements DockableWrapper, PropertyChangeListener
{
	private DockManager manager;
	private Dockable content;
	private TitleLabel label;


	private class TitleLabel extends JLabel
		implements DragSourceComponent
	{
		private Color selectedTitleColor;
		private Color selectedTextColor;
		private Color notSelectedTitleColor;
		private Color notSelectedTextColor;
		private Object[] listeners;

		private boolean selected;


		TitleLabel (String text, Icon icon)
		{
			super (text, icon, JLabel.LEADING);
			setOpaque (true);
		}


		@Override
		public void setUI (LabelUI ui)
		{
			super.setUI (ui);
			setFont (UIManager.getFont ("InternalFrame.titleFont"));
			selectedTitleColor = UIManager.getColor
				("InternalFrame.activeTitleBackground");
			selectedTextColor = UIManager.getColor
				("InternalFrame.activeTitleForeground");
			notSelectedTitleColor = UIManager.getColor
				("InternalFrame.inactiveTitleBackground");
			notSelectedTextColor = UIManager.getColor
				("InternalFrame.inactiveTitleForeground");
			setSelected (!(selected = !selected));
		}


		public DragDockableContext createContext (DragGestureEvent e)
		{
			return new DragDockableContext
				(getDockManager (), DockedFrame.this);
		}


		protected void installListeners ()
		{
			listeners = getDockManager ().installListeners
				(this, getDockManager (), getDockable ());
		}


		protected void uninstallListeners ()
		{
			DockManager.uninstallListeners (listeners);
		}


		void setSelected (boolean selected)
		{
			if (selected != this.selected)
			{
				this.selected = selected;
				setForeground (selected ? selectedTextColor
							   : notSelectedTextColor);
				setBackground (selected ? selectedTitleColor
							   : notSelectedTitleColor);
			}
		}
	}


	public DockedFrame (DockManager manager, Dockable content)
	{
		super (new BorderLayout ());
		this.manager = manager;
		this.content = content;
		label = new TitleLabel (content.getPanelTitle (), content.getIcon ());
		add (label, BorderLayout.NORTH);
		add ((Component) content, BorderLayout.CENTER);
		content.addPropertyChangeListener (this);
		label.installListeners ();
	}


	public DockManager getDockManager ()
	{
		return manager;
	}


	public Dockable getDockable ()
	{
		return content;
	}


	public void releaseDockable ()
	{
		content.removePropertyChangeListener (this);
		label.uninstallListeners ();
		content = null;
	}


	public DockContainer getDockParent ()
	{
		return DockManager.getDockParent (this);
	}


	public void propertyChange (PropertyChangeEvent e)
	{
		String n = e.getPropertyName ();
		Object v = e.getNewValue ();
		if (Dockable.PANEL_TITLE.equals (n))
		{
			label.setText (String.valueOf (v));
		}
		else if (Dockable.TOOL_TIP.equals (n))
		{
			label.setToolTipText (String.valueOf (v));
		}
		else if (Dockable.ICON.equals (n))
		{
			label.setIcon ((Icon) v);
		}
	}


	public void setSelected (boolean selected)
	{
		label.setSelected (selected);
	}

/*
	public Component getGlassPane ()
	{
		throw new UnsupportedOperationException ();
	}


	public void setGlassPane (Component glassPane)
	{
		throw new UnsupportedOperationException ();
	}


	public Container getContentPane ()
	{
		return null;
	}


	public void setContentPane (Container contentPane)
	{
		throw new UnsupportedOperationException ();
	}


	public JLayeredPane getLayeredPane ()
	{
		throw new UnsupportedOperationException ();
	}


	public void setLayeredPane (JLayeredPane layeredPane)
	{
		throw new UnsupportedOperationException ();
	}
*/
}
