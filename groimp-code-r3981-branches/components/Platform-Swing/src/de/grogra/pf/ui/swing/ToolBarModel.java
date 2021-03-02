
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

import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.tree.*;

class ToolBarModel extends MenuModelBase
{
	static final String POPUP_PARENT = "de.grogra.pf.ui.swing.ToolBarModel.popupParent";
	static final String KEYSTROKE = "de.grogra.pf.ui.swing.ToolBarModel.keyStroke";

	private static class ItemGroupButton extends JButton
		implements ContentPaneContainer, ActionListener
	{
		private JPopupMenu popup;
		AbstractButton button;


		ItemGroupButton ()
		{
			popup = new JPopupMenu ();
			popup.putClientProperty (POPUP_PARENT, this);
			addActionListener (this);
			popup.setLayout (new GridLayout (2, 2));
		}


		public Container getContentPane ()
		{
			return popup;
		}


		@Override
		protected void paintComponent (Graphics g)
		{
			super.paintComponent (g);
			int x = getWidth () - 11, y = getHeight () - 8;
			g.setColor (Color.LIGHT_GRAY);
			g.fillRect (x, y, 9, 6);
			g.setColor (Color.BLACK);
			g.drawLine (x + 1, ++y, x + 7, y);
			g.drawLine (x + 2, ++y, x + 6, y);
			g.drawLine (x + 3, ++y, x + 5, y);
			g.drawLine (x + 4, ++y, x + 4, y);
		}


		public void actionPerformed (ActionEvent e)
		{
			if (e.getSource () == this)
			{
				AWTEvent a = EventQueue.getCurrentEvent ();
				if (a instanceof MouseEvent)
				{
					MouseEvent m = (MouseEvent) a;
					if (m.getX () > getWidth () - 12)
					{
						popup.show (this, getWidth () - 6, getHeight () - 3);
						return;
					}
				}
			}
			else
			{
				popup.setVisible (false);
				setButton ((AbstractButton) e.getSource ());				
			}
			if (button != null)
			{
				ButtonSupport.get (button).postAction (e);
			}
		}


		void setButton (AbstractButton button)
		{
			this.button = button;
			setText (button.getText ());
			setMnemonic (button.getMnemonic ());
			setIcon (button.getIcon ());
			revalidate ();
		}


		void updateLayout ()
		{
			GridLayout g = (GridLayout) popup.getLayout ();
			int n = popup.getComponentCount ();
			int r = Math.max (1, (int) Math.sqrt (n)), c;
			while (true)
			{
				if (r * r >= n)
				{
					c = r;
					break;
				}
				if (r * (r + 1) >= n)
				{
					c = r + 1;
					break;
				}
				r++;
			}
			g.setColumns (c);
			g.setRows (r);
		}
	}


	private ToolBar toolBar;


	ToolBarModel (UITree sourceTree, ToolBar toolBar)
	{
		super (sourceTree);
		this.toolBar = toolBar;
	}


	@Override
	public Object run (int action, int iarg, Object oarg1, Object oarg2)
	{
		oarg2 = super.run (action, iarg, oarg1, oarg2);
		if (oarg1 instanceof ItemGroupButton)
		{
			((ItemGroupButton) oarg1).updateLayout ();
		}
		return oarg2;
	}


	public Object createNode (final Object sourceNode, Object targetParent)
	{
		final JComponent node;
		if (isSourceRoot (sourceNode))
		{
			node = toolBar;
		}
		else
		{
			boolean createSupport = false;
			int type = sourceTree.getType (sourceNode);
			int mods = SwingToolkit.TOOLBAR_ICON;
			switch (type)
			{
				case UINodeHandler.NT_ITEM:
					node = (targetParent == toolBar) ? new JButton ()
						: (AbstractButton) new JMenuItem ();
					createSupport = true;
					KeyStroke k = SwingToolkit.getKeyStroke (sourceTree, sourceNode);
					if (k != null)
					{
						node.putClientProperty (KEYSTROKE, k);
						toolBar.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put (k, node);
						toolBar.getActionMap ().put (node, new AbstractAction () {
							public void actionPerformed (ActionEvent e)
							{
								ButtonSupport s = ButtonSupport.get (node);
								if (s != null)
								{
									s.actionPerformed (new ActionEvent (node, e.getID (), e.getActionCommand (), e.getWhen (), e.getModifiers ()));
								}
							}
						});
					}
					break;
				case UINodeHandler.NT_MOUSE_MOTION:
					node = new JButton ();
					createSupport = true;
					break;
				case UINodeHandler.NT_CHOICE_ITEM:
					node = new JToggleButton ();
					createSupport = true;
					break;
				case UINodeHandler.NT_CHECKBOX_ITEM:
					node = new JCheckBox ();
					createSupport = true;
					break;
				case UINodeHandler.NT_SEPARATOR:
					JToolBar.Separator s = new JToolBar.Separator (null);
					s.setOrientation
						 ((toolBar.getOrientation () == JToolBar.VERTICAL)
						  ? JSeparator.HORIZONTAL : JSeparator.VERTICAL);
					node = s;
					break;
				case UINodeHandler.NT_ITEM_GROUP:
					node = new ItemGroupButton ();
					break;
				case UINodeHandler.NT_GROUP:
				case UINodeHandler.NT_DIRECTORY:
					node = new JMenu ();
					mods = SwingToolkit.TEXT;
					break;
				case UINodeHandler.NT_SELECTABLE:
					Selectable a = (Selectable) sourceTree.invoke (sourceNode, UINodeHandler.GET_SELECTABLE_METHOD, null);
					Selection sel;
					if ((a != null)
						&& ((sel = a.toSelection (sourceTree.getContext ())) != null))
					{
						// not useable
						ComponentWrapper w = sel.createPropertyEditorMenu ();
						JMenu menu = (JMenu) w.getComponent ();
						node = (JComponent) menu.getMenuComponent(1);						
						menu.remove(node);
						node.putClientProperty (WRAPPER, w);
					}
					else
					{
						node = new JLabel ();
					}
					break;

				default:
					node = new JLabel ();
					break;
			}
			if (node instanceof AbstractButton)
			{
//				((AbstractButton) node).setBorderPainted (false);
			}
			SwingToolkit.initialize (node, sourceTree, sourceNode, mods);
			if (createSupport)
			{
				SwingButtonSupport s = new SwingButtonSupport
					(sourceTree, sourceNode, (AbstractButton) node, type);
				Component c = (Component) targetParent;
				while (c != toolBar)
				{
					if (c instanceof ItemGroupButton)
					{
						ItemGroupButton gb = (ItemGroupButton) c;
						s.setActionListener (gb);
						if (gb.button == null)
						{
							gb.setButton ((AbstractButton) node);
						}
						break;
					}
					c = (c instanceof JPopupMenu)
						? (Component) ((JPopupMenu) c).getClientProperty (POPUP_PARENT)
						: c.getParent ();
				}
			}
		}
		node.putClientProperty (SwingToolkit.SOURCE, sourceNode);
		return node;
	}

	public void disposeNode (Object targetNode)
	{
		KeyStroke k = (KeyStroke) ((JComponent) targetNode).getClientProperty (KEYSTROKE);
		if (k != null)
		{
			toolBar.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).remove (k);
			toolBar.getActionMap ().remove (targetNode);
			((JComponent) targetNode).putClientProperty (KEYSTROKE, null);
		}
		super.disposeNode (targetNode);
	}

}
