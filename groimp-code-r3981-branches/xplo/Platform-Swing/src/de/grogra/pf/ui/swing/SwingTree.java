
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
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import de.grogra.util.*;
import de.grogra.icon.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.awt.LabelRenderer.LabelData;
import de.grogra.pf.ui.event.*;

class SwingTree extends JTree
	implements ComponentWrapper, Command, CellEditorListener
{
	DefaultTreeCellRenderer dr = new DefaultTreeCellRenderer ();

	private static final EmptyBorder BORDER = new EmptyBorder (1, 0, 1, 0);

	private Listener listener;


	private class Listener extends MouseAdapter implements KeyListener
	{
		@Override
		public void mousePressed (MouseEvent e)
		{
			TreePath p;
			if (((p = getSelectionPath ()) != null)
				&& getModel ().isLeaf (p.getLastPathComponent ())
				&& (getRowForLocation (e.getX (), e.getY ())
					== getRowForPath (p)))
			{
				e.consume ();
				actionPerformed (e, (e.getClickCount () == 1)
								 ? UINodeHandler.ACTION_SELECT
								 : UINodeHandler.ACTION_OPEN, null, p.getLastPathComponent ());
			}
		}


		public void keyPressed (KeyEvent e)
		{
			TreePath p;
			switch (e.getKeyCode ())
			{
				case KeyEvent.VK_ENTER:
					if (((p = getSelectionPath ()) != null)
						&& getModel ().isLeaf (p.getLastPathComponent ()))
					{
						e.consume ();
						actionPerformed (e, UINodeHandler.ACTION_OPEN, null, p.getLastPathComponent ());
					}
					break;
				case KeyEvent.VK_DELETE:
					if ((p = getSelectionPath ()) != null)
					{
						e.consume ();
						actionPerformed (e, UINodeHandler.ACTION_DELETE, null, p.getLastPathComponent ());
					}
					break;
			}
		}


		public void keyReleased (KeyEvent e)
		{
		}


		public void keyTyped (KeyEvent e)
		{
		}
	}


	private static class SyncTree extends SyncMappedTree
	{

		private static class Node extends SyncMappedTree.Node
		{
			final LabelData renderData;
			final LabelData expandedData;

			Node (LabelRenderer rl, Object sourceNode)
			{
				super (sourceNode);
				renderData = rl.new LabelData (this);
				expandedData = rl.new LabelData (this);
			}

		}


		SwingTree component;
		LabelRenderer render;
		IconAdapter icon = new IconAdapter (null, null, 0, 0, true, 0);


		SyncTree (final UITree tree)
		{
			super (tree, tree.getRoot (), new AWTSynchronizer (null));
		}


		@Override
		public Object createNode (Object sourceNode, Object targetParent)
		{
			if (render == null)
			{
				render = new LabelRenderer (((UITree) getSourceTree ()).getContext (), 32, true)
				{
					@Override
					protected void updateNodes (LabelData[] nodes, boolean layout)
					{
						Object[] children = new Object[1];
						int[] indices = new int[1];
						TreePath path = null;
						Node last = null;
						for (int i = nodes.length - 1; i >= 0; i--)
						{
							Node n = (Node) nodes[i].getUserData ();
							if (hasValidPath (n))
							{
								TreeModelEvent e;
								if (n.parent == null)
								{
									if (last != n)
									{
										last = n;
										path = n.getTreePath ();
									}
									e = new TreeModelEvent
										(SyncTree.this, path, null, null);
								}
								else
								{
									if (last != n.parent)
									{
										last = (Node) n.parent;
										path = last.getTreePath ();
									}
									indices[0] = getIndexOfChild (n);
									children[0] = n;
									e = new TreeModelEvent
										(this, path, indices, children);
								}
								fireTreeModelEvent (NODES_CHANGED, e);
							}
						}
						if (layout)
						{
							java.util.Enumeration e = component.getExpandedDescendants
								(((Node) getRoot ()).getTreePath ());
							children[0] = getRoot ();
							fireTreeModelEvent
								(STRUCTURE_CHANGED, new TreeModelEvent (this, children));
							if (e != null)
							{
								while (e.hasMoreElements ())
								{
									component.setExpandedState ((TreePath) e.nextElement(), true);
								}
							}
						}
					}
					
					
					@Override
					protected String getText (LabelData n)
					{
						return String.valueOf (((UITree) getSourceTree ()).getDescription
											   (((Node) n.getUserData ()).getSourceNode (), Described.NAME));
					}
						
						
					@Override
					protected IconSource getIconSource (LabelData n)
					{
						Object src = ((Node) n.getUserData ()).getSourceNode ();
						IconSource is = (IconSource) ((UITree) getSourceTree ()).getDescription (src, Described.ICON);
						if ((is == null) && !getSourceTree ().isLeaf (src))
						{
							is = (IconSource) UI.I18N.getObject
								((((Node) n.getUserData ()).expandedData == n)
								 ? "registry.open-directory.Icon"
								 : "registry.directory.Icon");
						}
						return is;
					}
				};
			}
			return new Node (render, sourceNode);
		}


		@Override
		protected void insertSync (Object parent, int index,
								   Object child, Object constraints)
		{
			super.insertSync (parent, index, child, constraints);
			if (component != null)
			{
				component.makeVisible (((Node) child).getTreePath ());
			}
		}


		@Override
		protected void valueForPathChangedSync (TreePath path, Object newValue)
		{
			super.valueForPathChangedSync (path, newValue);
			((Node) path.getLastPathComponent ()).renderData.invalidate ();
			((Node) path.getLastPathComponent ()).expandedData.invalidate ();
		}


		LabelData getData (Object node, boolean expanded)
		{
			Node n = (Node) node;
			LabelData d = expanded ? n.expandedData : n.renderData;
			d.revalidate ();
			return d;
		}

	}


	boolean initialized = false;

	private static final TreeCellRenderer RENDERER = new TreeCellRenderer ()
	{
		public Component getTreeCellRendererComponent
			(final JTree tree, final Object value, final boolean selected,
			 final boolean expanded, final boolean leaf, final int row,
			 final boolean hasFocus)
		{
			SwingTree st = (SwingTree) tree;
			Component r = st.dr.getTreeCellRendererComponent
				(tree, value, selected, expanded, leaf, row, hasFocus);
			if (r instanceof JLabel)
			{
				((JLabel) r).setBorder (BORDER);
				if (st.initialized)
				{
					((JLabel) r).setIcon (((SyncTree) st.getModel ()).getData (value, expanded).getIcon ());
				}
			}
			return r;
		}
	};

	SwingTree (UITree tree)
	{
		super ((TreeModel) null);
		setShowsRootHandles (true);
		getSelectionModel ()
			.setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
		setEditable (true);
		setRootVisible (false);
		setCellRenderer (RENDERER);
		addMouseListener (listener = new Listener ());
		addKeyListener (listener);
		SyncTree t = new SyncTree (tree);
		t.component = this;
		setModel (t);
		initialized = true;
		getCellEditor ().addCellEditorListener (this);
	}

	
	public void editingStopped (ChangeEvent e)
	{
		actionPerformed (null, UINodeHandler.ACTION_RENAME, getCellEditor ().getCellEditorValue (),
			getEditingPath ().getLastPathComponent ());
	}


	public void editingCanceled(ChangeEvent e)
	{
	}


	@Override
	public String convertValueToText (final Object value, boolean selected,
									  boolean expanded, boolean leaf,
									  int row, boolean hasFocus)
	{
		return initialized ? ((SyncTree) getModel ()).getData (value, expanded).getText ()
			: "";
	}


	public Object getComponent ()
	{
		return this;
	}


	public void dispose ()
	{
		setModel (null);
		removeKeyListener (listener);
		removeMouseListener (listener);
		listener = null;
	}


	UITree getUITree ()
	{
		return (UITree) ((SyncMappedTree) getModel ()).getSourceTree ();
	}


	static Object getNode (Object node)
	{
		return ((SyncMappedTree.Node) node).getSourceNode ();
	}


	void actionPerformed (InputEvent e, String action, Object param, Object syncNode)
	{
		Context c = getUITree ().getContext ();
		UI.getJobManager (c).runLater
			(this, new ActionEditEvent (action, param, (e != null) ? e.getModifiers () : 0)
			 .set (c, getNode (syncNode)), c, JobManager.ACTION_FLAGS);
	}

	
	public String getCommandName ()
	{
		return null;
	}


	public void run (Object info, Context ctx)
	{
		getUITree ().eventOccured (((EditEvent) info).getSource (), (EditEvent) info);
	}


	@Override
	public void setRowHeight (int rowHeight)
	{
		super.setRowHeight (0);
	}

}
