package de.grogra.ext.x3d;

import org.w3c.dom.Element;

import de.grogra.imp3d.objects.SceneTree.InnerNode;

/**
 * This class represents an element of the groupToElement stack.
 * It links the last read transformation of the groimp scene
 * with the written xml element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class GroupToElement {
	
	private InnerNode group;
	private Element element;
	
	public GroupToElement(InnerNode group, Element element) {
		this.group = group;
		this.element = element;
	}
	
	public InnerNode getGroup() {
		return group;
	}
	
	public Element getElement() {
		return element;
	}
}
