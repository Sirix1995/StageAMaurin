package de.grogra.imp3d.gl20;

import java.util.ArrayList;
import java.util.Stack;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceShapeBox;
import de.grogra.imp3d.gl20.GL20ResourceShapeFrustum;
import de.grogra.imp3d.gl20.GL20ResourceShapeLine;
import de.grogra.imp3d.gl20.GL20ResourceShapeLineStrip;
import de.grogra.imp3d.gl20.GL20ResourceShapeParallelogram;
import de.grogra.imp3d.gl20.GL20ResourceShapePlane;
import de.grogra.imp3d.gl20.GL20ResourceShapePolygons;
import de.grogra.imp3d.gl20.GL20ResourceShapeSphere;
import de.grogra.imp3d.gl20.GL20ResourceTexture;
// TODO implement GL20ResourceTextureCubeMap class
//import de.grogra.imp3d.gl20.GL20ResourceTextureCubeMap;

public class GL20Node {
	// ------------------------------------------------------------------------
	// graph traveling attributes
	// ------------------------------------------------------------------------
	/**
	 * stack of the <code>GL20Node</code>s to travel the graph backward
	 * with less management overhead.
	 */
	private static Stack<Stack<GL20Node>> nodeStack = new Stack<Stack<GL20Node>>();

	/**
	 * TODO
	 */
	private static Stack<Integer> activeNodeIndexStack = new Stack<Integer>();

	/**
	 * the current <code>GL20Node</code>
	 */
	private static GL20Node currentNode;

	// ------------------------------------------------------------------------
	// graph traveling methods
	// ------------------------------------------------------------------------

	/**
	 * start a new traveling over the graph
	 *
	 * @param rootNode the root <code>GL20Node</code> of the graph
	 */
	public static void startNewVisit(GL20Node rootNode) {
		// clean up all stacks first
		nodeStack.empty();
		activeNodeIndexStack.empty();

		Stack<GL20Node> rootCacheLine = new Stack<GL20Node>();
		rootCacheLine.add(rootNode);
		nodeStack.push(rootCacheLine);
		activeNodeIndexStack.push(new Integer(0));

		// set current node
		currentNode = rootNode;

		// collect all children of the node ...
		Stack<GL20Node> cacheLine = new Stack<GL20Node>();
		GL20Node tempNode = currentNode.childNode;
		while (tempNode != null) {
			cacheLine.add(tempNode);
			tempNode = tempNode.brotherNode;
		}

		// ... and add them to the node stack
		nodeStack.add(cacheLine);
		activeNodeIndexStack.add(new Integer(0));
	}

	/**
	 * enter the <code>GL20Node</code> that is identified with given
	 * <code>nodeID</code>. If such a node doesn't exists, it will created
	 * and added to the graph structure.
	 *
	 * @param nodeID the ID of the node that should be entered
	 */
	public static void enterNode(long nodeID) {
		Stack<GL20Node> currentChildren = nodeStack.peek();
		int childrenCount = currentChildren.size();
		int childrenToSkip = (int)activeNodeIndexStack.peek();

		// look for a child node with the given nodeID
		boolean foundChild = false;
		int childSearchIndex = childrenToSkip;
		while (childSearchIndex < childrenCount) {
			if (currentChildren.get(childSearchIndex).nodeID == nodeID) {
				foundChild = true;
				break;
			}
			else
				childSearchIndex++;
		}

		if (foundChild == true) {
			// we found the child with the given nodeID
			if (childSearchIndex == childrenToSkip) {
				// child is at correct position in child-order, so do nothing
				// FIXME only for debug reason
				// System.out.print(".");
			}
			else {
				// child is at a wrong position in child-order, so re-order it
				GL20Node tempNode = currentChildren.remove(childSearchIndex);
				currentChildren.insertElementAt(tempNode, childrenToSkip);
				
				// FIXME only for debug reason
				// System.out.print("s");
			}
		}
		else {
			// child doesn't exist until now, so create one and add them
			GL20Node newChild = new GL20Node(nodeID);

			// FIXME only for debug reason
			// System.out.print("c");

			if (childrenToSkip < childrenCount)
				currentChildren.insertElementAt(newChild, childrenToSkip);
			else
				currentChildren.add(newChild);
		}

		GL20Node childNode = currentChildren.get(childrenToSkip);

		// build a new parent-child relation when the child is the first
		if (childrenToSkip == 0)
			currentNode.childNode = childNode;

		// set current node
		currentNode = childNode;

		// collect all children of the node ...
		Stack<GL20Node> cacheLine = new Stack<GL20Node>();
		GL20Node tempNode = currentNode.childNode;
		while (tempNode != null) {
			cacheLine.add(tempNode);
			tempNode = tempNode.brotherNode;
		}

		// ... and add them to node stack
		nodeStack.push(cacheLine);
		activeNodeIndexStack.push(new Integer(0));
	}

	/**
	 * Should be called when a node will be leaved. The returned
	 * <code>GL20Node</code>s wasn't visited in this run.
	 *
	 * @return <code>GL20Node</code>s that wasn't visited in this run OR
	 * <code>null</code> when all <code>GL20Node</code>s was visit.
	 */
	public static ArrayList<GL20Node> leaveNode() {
		ArrayList<GL20Node> returnValue = null;

		// get all children from stack
		Stack<GL20Node> currentChildren = nodeStack.pop();
		int childrenCount = currentChildren.size();
		int childrenToSkip = (int)activeNodeIndexStack.pop();

		// build node-brother relations for the children that was visit
		int childIndex = 0;
		while (childIndex < childrenToSkip - 1) {
			currentChildren.get(childIndex).brotherNode = currentChildren.get(childIndex + 1);
			childIndex++;
		}

		if (childrenToSkip > 0)
			// at least one child was visited, so set the node-brother relation of the last to null
			currentChildren.get(childrenToSkip - 1).brotherNode = null;
		else
			// no child was visit, so set the node-child relation of the current node to null
			currentNode.childNode = null;

		// add all unvisited children to the returnValue
		if (childrenToSkip < childrenCount) {
			childIndex = childrenToSkip;
			returnValue = new ArrayList<GL20Node>();

			while (childIndex < childrenCount) {
				returnValue.add(currentChildren.get(childIndex));
				childIndex++;
			}
		}

		// mark current node as visited by incrementing the visit activeNodeIndex
		int visitedChildrenCount = (int)activeNodeIndexStack.pop();
		int parentIndex = (int)activeNodeIndexStack.peek();
		visitedChildrenCount++;
		activeNodeIndexStack.push(new Integer(visitedChildrenCount));

		// set current node
		currentNode = nodeStack.get(nodeStack.size() - 2).get(parentIndex);

		return returnValue;
	}

	public static GL20Node getCurrentNode() {
		return currentNode;
	}

	/**
	 * child node of this <code>GL20Node</code>
	 */
	private GL20Node childNode = null;

	/**
	 * brother node of this <code>GL20Node</code>
	 */
	private GL20Node brotherNode = null;

	/**
	 * the ID of this <code>GL20Node</code>
	 */
	private long nodeID;

	/**
	 * last modification stamp of this <code>GL20Node</code>
	 */
	private int stamp = GL20Const.INVALID_ID;
	
	/**
	 * the selection state of this <code>GL20Node</code>
	 */
	private int selectionState = 0;

	/**
	 * the <code>GL20Resource</code> that this <code>GL20Node</code> contains
	 */
	private GL20Resource resource = null;

	public GL20Node(long nodeID) {
		this.nodeID = nodeID;
	}

	/**
	 * get the node ID of this <code>GL20Node</code>
	 *
	 * @return the node ID
	 */
	final public long getID() {
		return nodeID;
	}

	/**
	 * get the last modification stamp of this <code>GL20Node</code>
	 *
	 * @return the last modification stamp
	 */
	final public int getStamp() {
		return stamp;
	}

	/**
	 * set the modification stamp of this <code>GL20Node</code>
	 *
	 * @param stamp the modification stamp
	 */
	final public void setStamp(int stamp) {
		this.stamp = stamp;
	}
	
	final public void setSelectionState(int selectionState) {
		this.selectionState = selectionState;
	}
	
	final public int getSelectionState() {
		return selectionState;
	}

	/**
	 * create a <code>GL20Resource</code> resource for this <code>GL20Node</code>
	 * if no resource exists until now. if a <code>GL20Resource</code> exists it
	 * will be returned
	 *
	 * @param resourceClassType the class and the type of the <code>GL20Resource</code>
	 * that should be created
	 * @return <code>GL20Resource</code> of this <code>GL20Node</code>
	 * @see <code>getResource()</code>
	 */
	final public GL20Resource createResource(int resourceClassType) {
		if ((resource != null) && (resource.getResourceClassType() != resourceClassType)) {
			// we have already a resource for this <code>GL20Node</code> but
			// with an other resource class and/or type
			resource.destroy();
			resource = null;
		}

		if (resource == null) {
			switch (resourceClassType) {
			case GL20Resource.GL20RESOURCE_SHAPE_BOX:
				resource = new GL20ResourceShapeBox();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_FRUSTUM:
				resource = new GL20ResourceShapeFrustum();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_LINE:
				resource = new GL20ResourceShapeLine();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_LINE_STRIP:
				resource = new GL20ResourceShapeLineStrip();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_PARALLELOGRAM:
				resource = new GL20ResourceShapeParallelogram();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_PLANE:
				resource = new GL20ResourceShapePlane();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_POLYGONS:
				resource = new GL20ResourceShapePolygons();
				break;
			case GL20Resource.GL20RESOURCE_SHAPE_SPHERE:
				resource = new GL20ResourceShapeSphere();
				break;
			case GL20Resource.GL20RESOURCE_TEXTURE:
				resource = new GL20ResourceTexture();
				break;
			// TODO implement GL20ResourceTextureCubeMap class
			//case GL20Resource.GL20RESOURCE_TEXTURE_CUBEMAP:
			//	resource = new GL20ResourceTextureCubeMap();
			//	break;
			}
		}

		return resource;
	}

	/**
	 * get the <code>GL20Resource</code> of this <code>GL20Node</code>
	 *
	 * @return <code>null</code> - no <code>GL20Resource</code> exists to this
	 * <code>GL20Node</code>; otherwise - the <code>GL20Resource</code> of this
	 * <code>GL20Node</code>
	 */
	final public GL20Resource getResource() {
		return resource;
	}

	/**
	 * check if this <code>GL20Node</code> incl. its resources are up to date
	 *
	 * @param stamp the modification stamp of the associated
	 * @return <code>true</code> - this <code>GL20Node</code> and the
	 * resources are up to date
	 */
	final public boolean isUpToDate(int stamp) {
		if (this.stamp != stamp)
			return false;
		else if (resource != null) {
			if (resource.isUpToDate() == false)
				return false;
		}

		return true;
	}

	/**
	 * signal the end of an update of this <code>GL20Node</code>
	 * after updating this <code>GL20Node</code> and its resource
	 * the internal modification stamp will set to the given one.
	 *
	 * @param stamp the current modification stamp
	 */
	final public void update(int stamp) {
		if (resource != null)
			// update resource
			resource.update();

		this.stamp = stamp;
	}
	
	/**
	 * destroy this <code>GL20Node</code>
	 */
	final public void destroy() {
		if (resource != null)
			// destroy the <code>GL20Resource</code> of this <code>GL20Node</code>
			resource.destroy();
	}
}