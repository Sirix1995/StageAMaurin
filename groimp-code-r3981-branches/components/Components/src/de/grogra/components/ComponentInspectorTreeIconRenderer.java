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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.grogra.components.ComponentInspector.TreeNode;

/**
 * The type ComponentInspectorTreeIconRenderer is responsible for changing the
 * tree icons and highlighting the path of selected leafs.
 */

public class ComponentInspectorTreeIconRenderer extends DefaultTreeCellRenderer {

	public ComponentInspectorTreeIconRenderer() {
		super();
//		leafIcon = new ImageIcon(getClass().getResource("/images/system/box.gif"));
//		openIcon = new ImageIcon(getClass().getResource("/images/system/arrow_down.gif"));
//		closedIcon = new ImageIcon(getClass().getResource("/images/system/arrow.gif"));
	}

	/**
	 * Creating the component in the GridBagLayout.
	 * 
	 * @param tree
	 * @param value
	 * @param selected
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasFocus
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		// setting icons
//		setLeafIcon(leafIcon);
//		setOpenIcon(openIcon);
//		setClosedIcon(closedIcon);
//		setBackgroundSelectionColor(new Color(0, 0, 200));
//		putClientProperty("JTree.lineStyle", "None");
		TreePath selectionPath = tree.getSelectionPath();
		TreePath nodePath = new TreePath(((ComponentInspector)tree.getModel()).getPathToRoot((TreeNode) value));
		if(selectionPath != null &&
		  (selectionPath.equals(nodePath) || 
		   nodePath.isDescendant(selectionPath))) {
				setFont(getFont().deriveFont(Font.BOLD));
				setBackgroundSelectionColor(new Color(220, 220, 255));
		} else {
				setFont(getFont().deriveFont(Font.PLAIN));
		}
		return this;
	}

}