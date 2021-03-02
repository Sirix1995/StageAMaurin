
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

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventObject;
import java.util.WeakHashMap;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.event.EditEvent;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.registry.CheckBoxItem;
import de.grogra.pf.ui.registry.ChoiceGroup;
import de.grogra.pf.ui.tree.UINodeHandler;
import de.grogra.pf.ui.tree.UITree;
import de.grogra.util.Described;
import de.grogra.util.Disposable;
import de.grogra.util.EventListener;

public abstract class ButtonSupport extends MouseAdapter implements EventListener,
	ActionListener, ItemListener, MouseMotionListener, Runnable, Disposable
{
	protected final UITree tree;
	protected Object node;
	protected final String shortDescription;
	protected final int type;
	protected final Object button;

	private static final int NO_DRAGGING = 0;
	private static final int DRAGGING_POSSIBLE = 1;
	private static final int DRAGGING = 2;

	private boolean selected = false;
	private boolean enabled;
	private int state = NO_DRAGGING, lastDragX, lastDragY;
	private Point dragOrigin = null, robotDelta = new Point ();

	private Component cursorComponent = null;
	private boolean disposed = false;

	private boolean actionListenerSet = false;
	private ActionListener actionListener;

	private Runnable immediateListener;

	// for each device store a robot
	private final WeakHashMap<GraphicsDevice, Robot> robots = new WeakHashMap<GraphicsDevice, Robot>();
	
	private final class Dispatcher implements Command
	{
		private final EditEvent event;

		
		Dispatcher (EditEvent event)
		{
			this.event = event;
		}


		public String getCommandName ()
		{
			return (event instanceof ActionEditEvent)
				? ((ActionEditEvent) event).getName () : null;
		}

		
		public void run (Object info, Context ctx)
		{
			if (((event instanceof ActionEditEvent) && ((type == UINodeHandler.NT_CHECKBOX_ITEM) || !selected))
				|| (event instanceof DragEvent))
			{
				tree.eventOccured (node, event);
			}
		}
	}


	public ButtonSupport (UITree tree, Object node, int type,
						  Button button)
	{
		this (tree, node, type, (Object) button);
	}


	public ButtonSupport (UITree tree, Object node, int type,
						  Checkbox button)
	{
		this (tree, node, type, (Object) button);
	}


	public ButtonSupport (UITree tree, Object node, int type,
						  AbstractButton button)
	{
		this (tree, node, type, (Object) button);
	}


	public ButtonSupport (UITree tree, Object node, int type,
						  MenuItem button)
	{
		this (tree, node, type, (Object) button);
	}


	private ButtonSupport (UITree tree, Object node, int type, Object button)
	{
		this.tree = tree;
		this.node = node;
		this.type = type;
		this.button = button;
		this.shortDescription
			= (String) tree.getDescription (node, Described.SHORT_DESCRIPTION);
		switch (type)
		{
			case UINodeHandler.NT_ITEM:
				if (button instanceof AbstractButton)
				{
					((AbstractButton) button).addActionListener (this);
				}
				else if (button instanceof MenuItem)
				{
					((MenuItem) button).addActionListener (this);
				}
				else
				{
					((Button) button).addActionListener (this);
				}
				immediateListener = (Runnable) tree.invoke (node, UINodeHandler.GET_IMMEDIATE_LISTENER_METHOD, null);
				break;
			case UINodeHandler.NT_CHOICE_ITEM:
			case UINodeHandler.NT_CHECKBOX_ITEM:
				if (button instanceof AbstractButton)
				{
					((AbstractButton) button).addItemListener (this);
				}
				else if (button instanceof CheckboxMenuItem)
				{
					((CheckboxMenuItem) button).addItemListener (this);
				}
				else
				{
					((Checkbox) button).addItemListener (this);
				}
				UIProperty p = (type == UINodeHandler.NT_CHOICE_ITEM) ? ChoiceGroup.get (tree, node).getProperty ()
					: CheckBoxItem.get (tree, node).getProperty ();
				if (p != null)
				{
					p.addPropertyListener (tree.getContext (), this);
				}
				break;
			case UINodeHandler.NT_MOUSE_MOTION:
				((Component) button).addMouseListener (this);
				((Component) button).addMouseMotionListener (this);
				break;
		}
		updateInternal ();
		updateButton ();
	}


	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		immediateListener = null;
		switch (type)
		{
			case UINodeHandler.NT_ITEM:
				if (button instanceof AbstractButton)
				{
					((AbstractButton) button).removeActionListener (this);
				}
				else if (button instanceof MenuItem)
				{
					((MenuItem) button).removeActionListener (this);
				}
				else
				{
					((Button) button).removeActionListener (this);
				}
				break;
			case UINodeHandler.NT_CHOICE_ITEM:
			case UINodeHandler.NT_CHECKBOX_ITEM:
				if (button instanceof AbstractButton)
				{
					((AbstractButton) button).removeItemListener (this);
				}
				else if (button instanceof CheckboxMenuItem)
				{
					((CheckboxMenuItem) button).removeItemListener (this);
				}
				else
				{
					((Checkbox) button).removeItemListener (this);
				}
				UIProperty p = (type == UINodeHandler.NT_CHOICE_ITEM) ? ChoiceGroup.get (tree, node).getProperty ()
					: CheckBoxItem.get (tree, node).getProperty ();
				if (p != null)
				{
					p.removePropertyListener (tree.getContext (), this);
				}
				break;
			case UINodeHandler.NT_MOUSE_MOTION:
				((Component) button).removeMouseMotionListener (this);
				((Component) button).removeMouseListener (this);
				break;
		}
	}


	public void setActionListener (ActionListener listener)
	{
		actionListenerSet = true;
		this.actionListener = listener;
	}


	public void actionPerformed (ActionEvent event)
	{
		if (actionListenerSet)
		{
			if (actionListener != null)
			{
				actionListener.actionPerformed (event);
			}
		}
		else if (immediateListener != null)
		{
			immediateListener.run ();
		}
		else
		{
			postAction (event);
		}
	}


	public void postAction (ActionEvent event)
	{
		UI.getJobManager (tree.getContext ()).runLater
			(new Dispatcher (new ActionEditEvent (shortDescription, event).set (tree.getContext ())),
			 null, tree.getContext (), JobManager.ACTION_FLAGS);
	}


	public void itemStateChanged (ItemEvent event)
	{
		if ((event.getStateChange() == ItemEvent.SELECTED)
			|| (((type == UINodeHandler.NT_CHECKBOX_ITEM) && (event.getStateChange() == ItemEvent.DESELECTED))))
		{
			if ((type == UINodeHandler.NT_CHECKBOX_ITEM) || !selected)
			{
				UI.getJobManager (tree.getContext ()).runLater
					(new Dispatcher (new ActionEditEvent (null, event.getStateChange() == ItemEvent.SELECTED, 0).set (tree.getContext ())),
					 null, tree.getContext (), JobManager.ACTION_FLAGS);
			}
		}
		else if (selected)
		{
			updateButton ();
		}
	}


	public void mouseDragged (MouseEvent e)
	{
		switch (state)
		{
			case DRAGGING_POSSIBLE:
				state = DRAGGING;
				robotDelta.setLocation (0, 0);
				postDrag (DragEvent.DRAGGING_STARTED, 0, 0, e);
				lastDragX = 0;
				lastDragY = 0;
				// no break
			case DRAGGING:
				Point p = new Point (-dragOrigin.x, -dragOrigin.y);
				SwingUtilities.convertPointToScreen (p, e.getComponent ());
				e.translatePoint (p.x, p.y);
				int x = e.getX (), y = e.getY ();
				if (type == UINodeHandler.NT_MOUSE_MOTION)
				{
					GraphicsConfiguration configuration = e.getComponent().getGraphicsConfiguration();
					Rectangle bounds = configuration.getBounds();
					Robot r = getRobot (configuration.getDevice());
					if (r != null)
					{
						e.translatePoint (robotDelta.x, robotDelta.y);
						if ((x != 0) || (y != 0))
						{
							robotDelta.translate (x, y);
							r.mouseMove (dragOrigin.x - bounds.x, dragOrigin.y - bounds.y);
						}
						x = e.getX ();
						y = e.getY ();
					}
				}
				if ((x != lastDragX) || (y != lastDragY))
				{
					postDrag (DragEvent.DRAGGING_CONTINUED,
							  x - lastDragX, y - lastDragY, e);
					lastDragX = x;
					lastDragY = y;
				}
				break;
		}
	}


	public void mouseMoved (MouseEvent e)
	{
	}
	
	@Override
	public void mousePressed (MouseEvent e)
	{
		if (type == UINodeHandler.NT_MOUSE_MOTION)
		{
			Cursor c = getTransparentCursor ();
			if ((c != null) && (cursorComponent == null))
			{
				cursorComponent = getCursorComponent (e);
				cursorComponent.setCursor (c);
			}
		}
		if (state != DRAGGING)
		{
			state = DRAGGING_POSSIBLE;
			dragOrigin = e.getPoint ();
			SwingUtilities.convertPointToScreen (dragOrigin, e.getComponent ());			
		}
	}


	@Override
	public void mouseReleased (MouseEvent e)
	{
		if (cursorComponent != null)
		{
			cursorComponent.setCursor (null);
			cursorComponent = null;
		}
		if (state == DRAGGING)
		{
			state = NO_DRAGGING;
			postDrag (DragEvent.DRAGGING_FINISHED, 0, 0, e);
		}
	}


	private void postDrag (int state, int dx, int dy, MouseEvent me)
	{
		UI.getJobManager (tree.getContext ()).runLater
			(new Dispatcher (new DragEvent ().setDragData (state, dx, dy).set (me).set (tree.getContext ())),
			 null, tree.getContext (), JobManager.UPDATE_FLAGS);
	}


	public void eventOccured (EventObject event)
	{
		if (event instanceof UIPropertyEditEvent)
		{
			updateState (node);
		}
	}


	protected Component getCursorComponent (MouseEvent e)
	{
		return e.getComponent ();
	}


	public void updateState (Object newSourceNode)
	{
		node = newSourceNode;
		if (updateInternal ())
		{
			EventQueue.invokeLater (this);
		}
	}


	private boolean updateInternal ()
	{
		boolean u = false;
		if (enabled != tree.isEnabled (node))
		{
			u = true;
			enabled = !enabled;
		}
		if (((type == UINodeHandler.NT_CHOICE_ITEM)
			 && (selected != ChoiceGroup.get (tree, node).isSelected (tree.getContext (), tree, node)))
			|| ((type == UINodeHandler.NT_CHECKBOX_ITEM)
				&& (selected != CheckBoxItem.get (tree, node).getValue (tree.getContext ()))))
		{
			u = true;
			selected = !selected;
		}
		return u;
	}


	public void run ()
	{
		updateButton ();
	}


	private void updateButton ()
	{
		if (button instanceof Component)
		{
			((Component) button).setEnabled (enabled);
		}
		else
		{
			((MenuItem) button).setEnabled (enabled);
		}
		if ((type == UINodeHandler.NT_CHOICE_ITEM)
			|| (type == UINodeHandler.NT_CHECKBOX_ITEM))
		{
			if (button instanceof AbstractButton)
			{
				((AbstractButton) button).setSelected (selected);
			}
			else if (button instanceof CheckboxMenuItem)
			{
				((CheckboxMenuItem) button).setState (selected);
			}
			else
			{
				((Checkbox) button).setState (selected);
			}
		}
	}


	private static ButtonSupport get (java.util.EventListener[] a)
	{
		for (int i = a.length - 1; i >= 0; i--)
		{
			if (a[i] instanceof ButtonSupport)
			{
				return (ButtonSupport) a[i];
			}
		}
		return null;
	}


	public static ButtonSupport get (Object button)
	{
		if (button instanceof CheckboxMenuItem)
		{
			return get (((CheckboxMenuItem) button).getItemListeners ());
		}
		else if (button instanceof MenuItem)
		{
			return get (((MenuItem) button).getActionListeners ());
		}
		else if (button instanceof Component)
		{
			Component c = (Component) button;
			ButtonSupport b;
			if ((b = get (c.getListeners (ActionListener.class))) != null)
			{
				return b;
			}
			if ((b = get (c.getListeners (ItemListener.class))) != null)
			{
				return b;
			}
			if ((b = get (c.getListeners (MouseListener.class))) != null)
			{
				return b;
			}
		}
		return null;
	}
	
	
	protected abstract Cursor getTransparentCursor ();


	protected abstract Robot getRobot ();

	
	/**
	 * On multi-screen environments there should be one robot per GraphicsDevice.
	 * Those robots will be allocated on-demand and returned by this function.
	 * @param o
	 * @return
	 */
	protected Robot getRobot(GraphicsDevice device)
	{
		Robot r = robots.get(device);
		if (r == null) {
			try {
				r = new Robot(device);
			} catch (AWTException ex) {
				r = getRobot();
			}
			robots.put(device, r);
		}
		return r;
	}
	
}
