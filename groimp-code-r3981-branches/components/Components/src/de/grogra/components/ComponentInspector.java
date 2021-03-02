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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.swing.Job;
import de.grogra.reflect.MemberBase;
import de.grogra.util.Lock;

public abstract class ComponentInspector implements TreeModel, TreeSelectionListener {

	public static class TreeNode implements javax.swing.tree.TreeNode {
		private final Object object;
		private final TreeNode parent;
		private ArrayList<TreeNode> children;

		public TreeNode(Object object, TreeNode parent) {
			this.object = object;
			this.parent = parent;
		}

		public void addChild(TreeNode treeNode) {
			if (children == null)
				children = new ArrayList<TreeNode>();
			children.add(treeNode);
		}

		public boolean hasChild(String[] path, TreeNode treeNode, int i) {
			if(i==path.length-1 || children==null) return false;			
			if(children.lastIndexOf(ComponentDescriptor.createCoreDescriptorDir(path[i])) != -1) {
				hasChild(path, children.get(children.lastIndexOf(ComponentDescriptor.createCoreDescriptorDir(path[i]))), i+1);
				return true;
			}				
			return false;
		}

		private TreeNode getChild(String name) {
			if(children==null) return null;
			for(TreeNode i : children) {
				ComponentDescriptor cd = (ComponentDescriptor)i.object;
				if(cd.getName().equals(name)) return i;
			}				
			return null;
		}		

		public void addChildAt(String key, ComponentDescriptor value, TreeNode root) {
			String[] path = key.split("/");
			if(!root.hasChild(path, root, 2)) {
				addChild(path, value, root, 2);
			}
		}

		private void addChild(String[] path, ComponentDescriptor value,
				TreeNode treeNode, int i) {
			if(i<path.length-1) {
				TreeNode node = treeNode.getChild(path[i]);
				if(node==null) {
					node = new TreeNode(ComponentDescriptor.createCoreDescriptorDir(path[i]), treeNode);
					treeNode.addChild(node);
				}
				treeNode.addChild(path, value, node, i+1);
			} else {
				treeNode.addChild(new TreeNode(value, treeNode));
			}
		}

		private String checkVersion(ComponentDescriptor node) {
			if(node == null) return "";
			if(node.getVersion().equals("dir") || node.getVersion().length()==0) 
				return "";
			return " [" + node.getVersion() + "]";
		}

		@Override
		public String toString() {
			if (object instanceof ComponentDescriptor) {
				ComponentDescriptor node = (ComponentDescriptor) object;
				if (node.getName() == null)
					return node.getNType().getSimpleName() + checkVersion(node);
				return node.getName() + checkVersion(node);
			}
			else if (object instanceof MemberBase) {
				MemberBase mb = (MemberBase) object;
				return mb.getSimpleName();
			}
			if (object != null)
				return object.toString();
			return "";
		}

		public Object getObject() {
			return object;
		}

		@Override
		public int getChildCount() {
			if (children == null)
				return 0;
			return children.size();
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			if (children == null)
				return null;
			return children.get(childIndex);
		}

		@Override
		public int getIndex(javax.swing.tree.TreeNode node) {
			if (children == null)
				return 0;
			return children.indexOf(node);
		}

		@Override
		public Enumeration<TreeNode> children() {
			if (children == null)
				return null;
			return Collections.enumeration(children);
		}

		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public TreeNode getParent() {
			return parent;
		}

		@Override
		public boolean isLeaf() {
			return getChildCount() == 0 ? true : false;
		}
	}

	final protected Context ctx;
	final protected GraphManager graph;
	protected Map<String, ComponentDescriptor> componentMap = null;
	protected TreeNode rootNode;
	private boolean activeTreeSelection = false;
	private boolean activeGISelection = false;

	public ComponentInspector(Context ctx, GraphManager graph, Map<String, ComponentDescriptor> componentMap) {
		this.ctx = ctx;
		this.graph = graph;
		this.componentMap = componentMap;
		initialize();
		buildTree();
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((TreeNode) parent).getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((TreeNode) parent).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	@Override
	public Object getRoot() {
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((TreeNode) node).isLeaf();
	}

	@Override
	public void valueChanged(final TreeSelectionEvent e) {
		if (activeGISelection)
			return;
		new Job(ctx) {
			@Override
			protected void runImpl(Object arg, Context ctx, Lock lock) {
				Object source = e.getSource();
				if (source instanceof JTree) {
					JTree tree = (JTree) source;
					TreePath[] paths = tree.getSelectionPaths();
					if(paths[0]!=null) {
						TreeNode node = (TreeNode)paths[0].getLastPathComponent();
						if(node.isLeaf()) {
							activeTreeSelection = true;
							ctx.getWorkbench().select((ComponentDescriptor)node.getObject());
						}
					}
				}
			}
		}.execute();
	}

	public void valueChanged(final Object componentDescriptor)
	{
		if (activeGISelection)
			return;
		new Job(ctx) {
			@Override
			protected void runImpl(Object arg, Context ctx, Lock lock) {
				activeTreeSelection = true;
				ctx.getWorkbench().select((ComponentDescriptor)componentDescriptor);
			}
		}.execute();
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) { }

	@Override
	public void removeTreeModelListener(TreeModelListener l) { }

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) { }

	public TreeNode getTreeNodeForNode(Node node) {
		return findNode(rootNode, node);
	}

	private TreeNode findNode(TreeNode treeNode, Node node) {
		if (treeNode.getObject() == node)
			return treeNode;
		int size = treeNode.getChildCount();
		for (int i = 0; i < size; i++) {
			TreeNode child = findNode(treeNode.getChildAt(i) , node);
			if (child != null)
				return child;
		}
		return null;
	}

	public void getPathToTreeNode(TreeNode treeNode, LinkedList<TreeNode> path) {
		path.addFirst(treeNode);
		while(treeNode != rootNode) {
			treeNode = treeNode.getParent();
			path.addFirst(treeNode);
		}
	}

	/**
	 * Use this method for declarations etc. Only called once in constructor.
	 */
	public abstract void initialize();

	/**
	 * Implement this method to set {@link rootNode} and its children.
	 * The method is called in the constructor of ObjectInspector and every time
	 * the GroIMP scene graph changes.
	 */
	public abstract void buildTree();

	public boolean isActiveTreeSelection() {
		return activeTreeSelection;
	}

	public void setActiveTreeSelection(boolean activeTreeSelection) {
		this.activeTreeSelection = activeTreeSelection;
	}

	public boolean isActiveGISelection() {
		return activeGISelection;
	}

	public void setActiveGISelection(boolean activeGISelection) {
		this.activeGISelection = activeGISelection;
	}

	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 * 
	 * @param aNode the TreeNode to get the path for
	 */
	public TreeNode[] getPathToRoot(TreeNode aNode) {
		return getPathToRoot(aNode, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 * 
	 * @param aNode  the TreeNode to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node 
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		TreeNode[]              retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
		if(aNode == null) {
			if(depth == 0)
				return null;
			else
				retNodes = new TreeNode[depth];
		}
		else {
			depth++;
			if(aNode == rootNode)
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	/**
	 * Searches the tree from rootNode for nodes' objects with names including the search input
	 * @param rootNode, searchInput
	 * @return array of components' name
	 */
	protected void searchWithComponentName(TreeNode rootNode, String searchInput, ArrayList<String> results)
	{
		if(results==null)
			return;

		if(!rootNode.isLeaf())
		{
			int childCount = rootNode.getChildCount();
			for(int i=0; i<childCount; ++i)
			{
				Object o = rootNode.getChildAt(i);
				if(o==null)
					continue;
				else
				{
					if(o instanceof TreeNode)
					{
						searchWithComponentName((TreeNode)o,searchInput,results);
					}
				}
			}
		}
		else
		{
			Object currObj = rootNode.getObject();
			if(currObj !=null)
			{
				if(currObj instanceof ComponentDescriptor)
				{
					ComponentDescriptor cDescriptor = (ComponentDescriptor)currObj;
					String name = cDescriptor.getName();
					if(name!=null)
					{
						if(name.toLowerCase().contains(searchInput))
							results.add(name);
					}
				}
			}
		}
	}

	/**
	 * Searches the tree from rootNode for nodes' objects with names including the search input
	 * @param rootNode, searchInput
	 * @return array of components' name
	 */
	protected Object getComponentDescriptor(TreeNode rootNode, String searchInput)
	{
		if(rootNode == null)
			return null;

		if(!rootNode.isLeaf())
		{
			int childCount = rootNode.getChildCount();
			for(int i=0; i<childCount; ++i)
			{
				Object o = rootNode.getChildAt(i);
				if(o==null)
					continue;
				else
				{
					if(o instanceof TreeNode)
					{
						Object cd = getComponentDescriptor((TreeNode)o,searchInput);
						if(cd!=null)
							return cd;
					}
				}
			}
		}
		else
		{
			Object currObj = rootNode.getObject();
			if(currObj !=null)
			{
				if(currObj instanceof ComponentDescriptor)
				{
					ComponentDescriptor cDescriptor = (ComponentDescriptor)currObj;
					String name = cDescriptor.getName();
					if(name!=null)
					{
						if(name.equals(searchInput))
							return cDescriptor;
					}
				}
			}
		}

		return null;
	}
}
