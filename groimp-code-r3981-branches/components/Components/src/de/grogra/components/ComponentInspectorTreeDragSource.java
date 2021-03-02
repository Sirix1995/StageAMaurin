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

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import de.grogra.pf.registry.ComponentDescriptor;

/**
 * 
 * A drag source wrapper for a JTree. This class is used to make
 * a rearrangeable DnD tree for the component explorer with the 
 * TransferableComponentTreeNode class as the transfer data type.
 * 
 * @author mhenke
 *
 */
public class ComponentInspectorTreeDragSource implements DragSourceListener, DragGestureListener {

	private final DragSource source;
	private TransferableComponentTreeNode transferable;
	private final JTree sourceTree;

	public ComponentInspectorTreeDragSource (JTree tree, int actions) {
		sourceTree = tree;
		source = new DragSource ();
		source.createDefaultDragGestureRecognizer (sourceTree, actions, this);
	}

	/*
	 * Drag Gesture Handler
	 */
	@Override
	public void dragGestureRecognized (DragGestureEvent dge) {
		TreePath path = sourceTree.getSelectionPath ();
		if ((path == null) || (path.getPathCount () <= 1)) {
			// We can't move the root node or an empty selection
			return;
		}
		ComponentInspector.TreeNode selectedNode = (ComponentInspector.TreeNode)path.getLastPathComponent ();
		transferable = new TransferableComponentTreeNode (((ComponentDescriptor)selectedNode.getObject ()).getIdentificationKey () );
		source.startDrag (dge, DragSource.DefaultCopyDrop, transferable, this);
	}

	/*
	 * Drag Event Handlers
	 */
	@Override
	public void dragEnter (DragSourceDragEvent dsde) {}

	@Override
	public void dragExit (DragSourceEvent dse) {}

	@Override
	public void dragOver (DragSourceDragEvent dsde) {}

	@Override
	public void dropActionChanged (DragSourceDragEvent dsde) {}

	@Override
	public void dragDropEnd (DragSourceDropEvent dsde) {}
}
