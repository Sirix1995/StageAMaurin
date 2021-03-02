
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
import java.awt.event.*;
import javax.swing.*;

import de.grogra.pf.ui.*;
import de.grogra.pf.ui.edit.*;
import de.grogra.pf.ui.tree.*;

class MenuModel extends MenuModelBase
{
	boolean isPopup;
	final ActionListener listener;



	MenuModel (UITree sourceTree, ActionListener listener, boolean isPopup)
	{
		super (sourceTree);
		this.isPopup = isPopup;
		this.listener = listener;
	}

/*
	private static final int REMOVE_MENU = MappedComponentModel.ACTION_COUNT;


	public void run (int action, int iarg, Object oarg1, Object oarg2)
	{
		switch (action)
		{
			case REMOVE_MENU:
			{
				((JComponent) root).getRootPane ().setJMenuBar (null);
				break;
			}
			default:
			{
				super.run (action, iarg, oarg1, oarg2);
			}
		}
	}

/*
	void set (UITree2 menu, Object menuRoot)
	{
		if (updater != null)
		{
			updater.dispose ();
			if (!isPopup && (root.getParent () != null))
			{
				sync.invokeAndWait (REMOVE_MENU);
			}
		}
		if (menu != null)
		{
			tree = menu;
			updater = new UITreeUpdater (menu, menuRoot, this);
			root = (JComponent) (isPopup ? updater.createTree ()
								 : updater.initialize ());
		}
		else
		{
			updater = null;
			root = null;
		}
	}
*/

	@Override
	protected Container getContentPane (Object c)
	{
		return (c instanceof JMenuBar) ? (Container) c
			: (c instanceof JPopupMenu) ? (JPopupMenu) c
			: (c instanceof JMenu) ? ((JMenu) c).getPopupMenu ()
			: null;
	}


	@Override
	public boolean isLeaf (Object node)
	{
		return !((node instanceof JPopupMenu) || (node instanceof JMenu)
				 || (node instanceof JMenuBar))
			|| ((node != root) && (node instanceof JMenu)
				&& (((JMenu) node).getClientProperty (WRAPPER) != null));
	}


	protected JComponent createNodeImpl (Object sourceNode, Object targetParent)
	{
		int mods = 0;
		JComponent node;
		if (isSourceRoot (sourceNode))
		{
			node = isPopup ? (JComponent) new JPopupMenu () : new JMenuBar ();
		}
		else if (sourceTree.isLeaf (sourceNode))
		{
			switch (sourceTree.getType (sourceNode))
			{
				case UINodeHandler.NT_CHOICE_ITEM:
					node = new JRadioButtonMenuItem ();
					new SwingButtonSupport (sourceTree, sourceNode,
											(AbstractButton) node,
											UINodeHandler.NT_CHOICE_ITEM);
					mods |= SwingToolkit.TEXT | SwingToolkit.MENU_ICON;
					break;
				case UINodeHandler.NT_SEPARATOR:
					node = new JPopupMenu.Separator ();
					break;
				case UINodeHandler.NT_FILL:
					node = (JComponent) Box.createHorizontalGlue ();
					break;
				case UINodeHandler.NT_MOUSE_MOTION:
					node = new JButton ();
					new SwingButtonSupport (sourceTree, sourceNode,
											(AbstractButton) node,
											UINodeHandler.NT_MOUSE_MOTION);
					mods |= SwingToolkit.MENU_ICON;
					break;
				case UINodeHandler.NT_SELECTABLE:
					Selectable a = (Selectable) sourceTree.invoke (sourceNode, UINodeHandler.GET_SELECTABLE_METHOD, null);
					Selection sel;
					if ((a != null)
						&& ((sel = a.toSelection (sourceTree.getContext ())) != null))
					{
						ComponentWrapper w = sel.createPropertyEditorMenu ();
						node = (JMenu) w.getComponent ();
						node.putClientProperty (WRAPPER, w);
					}
					else
					{
						node = new JMenuItem ();
					}
					break;
				default:
					node = new JMenuItem ();
					SwingButtonSupport s = new SwingButtonSupport
						(sourceTree, sourceNode, (AbstractButton) node,
						 UINodeHandler.NT_ITEM);
					if (listener != null)
					{
						s.setActionListener (null);
						((AbstractButton) node).addActionListener (listener);
					}
					mods |= SwingToolkit.TEXT | SwingToolkit.MENU_ICON;
					break;
			}
		}
		else
		{
			node = new JMenu ();
			mods |= (isPopup ? targetParent instanceof JPopupMenu
					 : targetParent instanceof JMenuBar) ? SwingToolkit.TEXT
				: SwingToolkit.TEXT | SwingToolkit.MENU_ICON;
		}
		SwingToolkit.initialize (node, sourceTree, sourceNode, mods);
		return node;
	}



	public Object createNode (Object sourceNode, Object targetParent)
	{
		JComponent node = createNodeImpl (sourceNode, targetParent);
		node.putClientProperty (SwingToolkit.SOURCE, sourceNode);
		return node;
	}


	@Override
	protected void treeChangedSync (Container parent)
	{
		while ((parent != root) && !(parent instanceof JMenu))
		{
			parent = parent.getParent ();
		}
		updateIcons (parent);
		super.treeChangedSync (parent);
	}


	private void updateIcons (Container p)
	{
		boolean bar = p instanceof JMenuBar;
		p = getContentPane (p);
		boolean buttonsExist = false, buttonsMiss = false,
			dummiesExist = false;
		for (int i = p.getComponentCount () - 1; i >= 0; i--)
		{
			Component c = p.getComponent (i);
			if (c instanceof AbstractButton)
			{
				Icon icon = ((AbstractButton) c).getIcon ();
				if (icon == SwingToolkit.MENU_ICON_DUMMY)
				{
					dummiesExist = true;
				}
				else if (icon != null)
				{
					buttonsExist = true;
				}
				else if (!bar)
				{
					buttonsMiss = true;
				}
				if ((c == root) || (c instanceof JMenu))
				{
					updateIcons ((Container) c);
				}
			}
		}
		if ((buttonsMiss && (buttonsExist || dummiesExist))
			|| (dummiesExist && !buttonsExist))
		{
			for (int i = p.getComponentCount () - 1; i >= 0; i--)
			{
				Component c = p.getComponent (i);
				if (c instanceof AbstractButton)
				{
					Icon icon = ((AbstractButton) c).getIcon ();
					if (icon == SwingToolkit.MENU_ICON_DUMMY)
					{
						if (!buttonsExist)
						{
							((AbstractButton) c).setIcon (null);
						}
					}
					else if (icon == null)
					{
						if (buttonsExist)
						{
							((AbstractButton) c)
								.setIcon (SwingToolkit.MENU_ICON_DUMMY);
						}
					}
				}
			}
		}
	}

}
