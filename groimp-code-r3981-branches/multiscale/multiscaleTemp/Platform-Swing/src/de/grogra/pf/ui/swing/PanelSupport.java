
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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Container;

import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.util.*;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.util.*;

public class PanelSupport
	implements Panel, AWTSynchronizer.Callback, RegistryContext
{
	protected WindowSupport ws;
	protected ModifiableMap.Producer mapProducer;

	protected final AWTSynchronizer sync = new AWTSynchronizer (this);

	javax.swing.JComponent textViewer;

	private String panelId;
	private final ISwingPanel panel;
	private final SwingDockable dpanel;

	protected MappedComponentModel menu = null;
	private ComponentWrapper content = null;
	private boolean disposed = false;
	private boolean docked;

	private Panel decorator;
	private final java.util.HashMap propertyMap = new java.util.HashMap ();


	protected PanelSupport (SwingDockable dpanel)
	{
		this (dpanel, dpanel);
	}


	protected PanelSupport (RootPane frame)
	{
		this (frame, null);
	}


	private PanelSupport (ISwingPanel panel, SwingDockable dpanel)
	{
		this.panel = panel;
		this.dpanel = dpanel; 
		this.docked = dpanel != null;
	}


	static PanelSupport get (Component c)
	{
		while (c != null)
		{
			if (c instanceof ISwingPanel)
			{
				return ((ISwingPanel) c).getSupport ();
			}
			c = c.getParent ();
		}
		return null;
	}


	public final PanelSupport initialize (WindowSupport ws, Map params)
	{
		this.ws = ws;
		panelId = (String) params.get (Panel.PANEL_ID, panelId);
		panel.initialize (this, params);
		configure (params);
		return this;
	}

	
	public void initDecorator (Panel decorator)
	{
		this.decorator = decorator;
	}


	public Panel getDecorator ()
	{
		return decorator;
	}

	
	Panel unresolve ()
	{
		Panel p = this;
		while (p.getDecorator () != null)
		{
			p = p.getDecorator ();
		}
		return p;
	}


	public java.util.Map getUIPropertyMap ()
	{
		return propertyMap;
	}


	void addParameters (ModifiableMap map)
	{
		if (mapProducer != null)
		{
			mapProducer.addMappings (map);
		}
		Panel p = this;
		while ((p = p.getDecorator ()) != null)
		{
			if (p instanceof ModifiableMap.Producer)
			{
				((ModifiableMap.Producer) p).addMappings (map);
			}
		}
	}


	protected void configure (Map params)
	{
	}


	public String getPanelId ()
	{
		return panelId;
	}


	public Workbench getWorkbench ()
	{
		return ws.getWorkbench ();
	}


	public Window getWindow ()
	{
		return ws;
	}


	public Panel getPanel ()
	{
		return this;
	}


	public Object getComponent ()
	{
		return panel;
	}


	public Registry getRegistry ()
	{
		return getWorkbench ().getRegistry ();
	}

	
	public Panel resolve ()
	{
		return this;
	}


	private static final int CLOSE_DOCKABLE = 0;
	private static final int SET_MENU = 1;
	private static final int SET_CONTENT = 2;
	private static final int SET_VISIBLE = 3;
	protected static final int MIN_UNUSED_ACTION = 4;


	static final int MIN_PANEL_ACTION = 100;


	public Object run (int action, int iarg, Object arg, Object arg2)
	{
		switch (action)
		{
			case CLOSE_DOCKABLE:
			{
				ws.dockManager.closeDockable (dpanel);
				break;
			}
			case SET_MENU:
			{	
				panel.setMenu ((menu != null) ? (Component) menu.getRoot ()
							   : null);
				if (dpanel != null)
				{
					dpanel.revalidate ();
				}
				else
				{
					ws.manager.revalidate (panel);
				}
				break;
			}
			case SET_CONTENT:
			{
				Container c = dpanel.getContentPane ();
				c.removeAll ();
				if (arg != null)
				{
					c.add ((Component) arg);
				}
				c.repaint ();
				dpanel.revalidate ();
				break;
			}
			case SET_VISIBLE:
				setVisibleSync (iarg != 0, (Panel) arg, Boolean.TRUE.equals (arg2));
				break;
			default:
				if ((action < MIN_PANEL_ACTION)
					|| !(panel instanceof AWTSynchronizer.Callback))
				{
					throw new AssertionError ("Illegal action code: " + action);
				}
				return ((AWTSynchronizer.Callback) panel)
					.run (action, iarg, arg, arg2);
		}
		return null;
	}


	public void setMenu (UITree t)
	{
		if (menu != null)
		{
			menu.dispose ();
			menu = null;
		}
		if (t != null)
		{
			menu = (panel instanceof ToolBar)
				? new ToolBarModel (t, (ToolBar) panel)
				: (MappedComponentModel) new MenuModel (t, null, false);
			menu.map (true);
		}
		sync.invokeAndWait (SET_MENU);
	}


	public UITree getMenu ()
	{
		return menu.getSourceTree ();
	}


	public void setContent (ComponentWrapper content)
	{
		if (this.content != null)
		{
			this.content.dispose ();
		}
		this.content = content;
		sync.invokeAndWait
			(SET_CONTENT, (content != null) ? content.getComponent () : null);
	}

	
	public ComponentWrapper getContent ()
	{
		return content;
	}


	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		if (docked)
		{
			docked = false;
			sync.invokeAndWait (CLOSE_DOCKABLE);
		}
		setMenu (null);
		setContent (null);
		disposeImpl ();
	}


	public final void show (boolean moveToFront, Panel keepInFront)
	{
		sync.invokeAndWait (SET_VISIBLE, 1, keepInFront, moveToFront);
	}


	public final void hide ()
	{
		sync.invokeAndWait (SET_VISIBLE, 0, null, null);
	}


	void setVisibleSync (boolean visible, Panel keepInFront, boolean moveToFront)
	{
		if (visible)
		{
			if (SwingUtilities.getWindowAncestor ((Component) dpanel) == null)
			{
				ws.dockManager.createFloatingWindow (dpanel).setVisible (true);
			}
			else
			{
				ws.dockManager.select
					(dpanel, moveToFront,
					 (keepInFront == null) ? null
					 : get ((Component) keepInFront.resolve ().getComponent ()).dpanel);
			}
		}
	}


	private final Object cursorLock = new Object ();
	private int waitingCursorCount = 0;
	private int nonwaitingCursor = INHERIT_CURSOR;

	public void setCursor (int cursor)
	{
		synchronized (cursorLock)
		{
			if (cursor == INC_WAIT_CURSOR)
			{
				waitingCursorCount++;
			}
			else if (cursor == DEC_WAIT_CURSOR)
			{
				waitingCursorCount--;
			}
			else
			{
				nonwaitingCursor = cursor;
			}
			final int c = (waitingCursorCount > 0) ? WAIT_CURSOR : nonwaitingCursor;
			EventQueue.invokeLater (new Runnable ()
			{
				public void run ()
				{
					((Component) panel).setCursor
						((c == INHERIT_CURSOR) ? null : Cursor.getPredefinedCursor (c));
				}
			});
		}
	}


	protected void disposeImpl ()
	{
		panel.dispose ();
	}


	void dockableClosed ()
	{
		docked = false;
		if (!disposed)
		{
			ws.getWorkbench ().getJobManager ()
				.execute (Command.DISPOSE, this, ws, JobManager.ACTION_FLAGS);
		}
	}


//	void disposeImpl ()
//	{
/*		disposer.dispose ();
		window.disposeImpl ();
		frame.editor.removeWindow (window);
/*		if (disposed)
		{
			return;
		}
		disposed = true;
		if (owner != null)
		{
			owner.subwindows.remove (category, this);
		}
		else
		{
			editor.getDisposer ().remove (this);
		}
//		Container c = (Container) frame;
		disposer.dispose ();
		subwindows.dispose ();
//		dialogs.dispose ();
* /
		if (mainMenu != null)
		{
			mainMenu.dispose ();
			mainMenu = null;
		}

//		Disposer.dispose (SwingToolBarModel.MODEL, this, true);
/*		if (pane instanceof JFrame)
		{
			((JFrame) pane).dispose ();
		}

/*		if (c instanceof Window)
		{
		}
		c.removeAll ();
		editor = null;
		if (c instanceof Window)
		{
			((Window) c).dispose ();
		}
		else if (c.getParent () != null)
		{
			c.getParent ().remove (c);
		}
		popupMenu.set (null, null);
*/
//	}


	void installUpdater (UIProperty property, boolean forComponent, String method, Map params)
	{
		UIPropertyUpdater.install (property, this, forComponent, method,
								   AWTSynchronizer.QUEUE, params);
	}

	
	private final class CloseHelper implements Command
	{
		private final Runnable ok;
		
		CloseHelper (Runnable ok)
		{
			this.ok = ok;
		}

		public void run (Object info, Context context)
		{
			if (ok == null)
			{
				checkClose ((Command) info);
			}
			else
			{
				try
				{
					java.awt.EventQueue.invokeAndWait (ok);
				}
				catch (Exception e)
				{
					context.getWorkbench ().logInfo ("Exception", e);
				}
			}
		}

		public String getCommandName ()
		{
			return null;
		}
	}


	private final Command CHECK_CLOSE = new CloseHelper (null);
	

	public void checkClose (Runnable ok)
	{
		ok.run ();
	}


	protected void executeCheckClose (Runnable ok)
	{
		getWorkbench ().getJobManager ().execute
			(CHECK_CLOSE, new CloseHelper (ok), this, JobManager.ACTION_FLAGS);
	}
	

	public void checkClose (Command ok)
	{
		ok.run (null, this);
	}


	@Override
	public String toString ()
	{
		return super.toString () + "[panelId=" + getPanelId () + ']';
	}
}
