package de.grogra.xl.impl.base;

import de.grogra.xl.query.EdgePattern;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * This class contains the first and last nodes of a produced graph at a particular scale.
 * 
 * This class is part of the extension of XL for multiscale modelling.
 * 
 * @since 18-04-2013
 * @author yongzhi ong
 *
 */
public class FirstLastScale {

	private Object prevNode;
	private Object firstNode;
	private Object lastNode;
	private int depth;
	private ObjectList<Object> firstDeepNodes;
	boolean firstPart;
	private int stdEdgeType;
	private final IntList istack;
	private final ObjectList ostack;
	RuntimeModel model;
	private ObjectList<Object> noEncoarseNodes;
	
	/**
	 * Constructor
	 * @param model
	 * @param currDepth - depth of production when this FirstLastScale is added
	 */
	public FirstLastScale(RuntimeModel model, int currDepth)
	{
		prevNode = null;
		firstNode = null;
		lastNode = null;
		depth = currDepth;
		firstDeepNodes = new ObjectList<Object>();
		firstPart=true;
		stdEdgeType = EdgePattern.SUCCESSOR_EDGE;
		istack = new IntList ();			//stack for [, ] brackets parameter restore
		ostack = new ObjectList<Object> ();		//stack for [, ] brackets parameter restore
		noEncoarseNodes = new ObjectList<Object>();
		this.model = model;
	}
	
	public Object getPrevNode()
	{
		return prevNode;
	}
	
	public void setPrevNode(Object node)
	{
		prevNode = node;
	}
	
	public Object getFirstNode()
	{
		return firstNode;
	}
	
	public void setFirstNode(Object node)
	{
		firstNode = node;
	}
	
	public Object getLastNode()
	{
		return lastNode;
	}
	
	public void setLastNode(Object node)
	{
		lastNode = node;
	}
	
	public void incrementDepth()
	{
		depth++;
	}
	
	public void decrementDepth()
	{
		depth--;
	}
	
	public int getDepth()
	{
		return depth;
	}
	
	public void addFirstDeepNode(Object node)
	{
		firstDeepNodes.add(node);
	}
	
	public ObjectList<Object> getFirstDeepNodes()
	{
		return firstDeepNodes;
	}
	
	public boolean getFirstPart()
	{
		return firstPart;
	}
	
	public void setFirstPart(boolean isFirstPart)
	{
		firstPart = isFirstPart;
	}
	
	public void pushIStack(int value)
	{
		istack.push(value);
	}
	
	public int popIStack()
	{
		if(ostack.size() > 0)
			return istack.pop();
		else 
			return -1;
	}
	
	public void pushOStack(Object obj)
	{
		ostack.push(obj);
	}
	
	public Object popOStack()
	{
		if(ostack.size() > 0)
			return ostack.pop();
		else 
			return null;
	}
	
	public int getStdEdgeType()
	{
		return this.stdEdgeType;
	}
	
	public void setStdEdgeType(int stdEdgeType)
	{
		this.stdEdgeType = stdEdgeType;
	}
	
	public void pushNoEncoarse(Object node)
	{
		if(!this.noEncoarseNodes.contains(node))
			this.noEncoarseNodes.add(node);
	}
	
	public void popNoEncoarse(Object node)
	{
		this.noEncoarseNodes.remove(node);
	}
	
	public ObjectList<Object> getNoEncoarseNodes()
	{
		return this.noEncoarseNodes;
	}
}
