package de.grogra.mtg;

import java.io.Serializable;

public class MTGNodeDataDescription implements Serializable
{
	private static final long serialVersionUID = -5896214495349618010L;

	private int left;
	private int[] right;
	private int relType;
	private int max;
	
	public MTGNodeDataDescription(int left, int[] right, int relType, int max)
	{
		this.left=left;
		this.right=right;
		this.relType=relType;
		this.max=max;
	}
	
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getRelType() {
		return relType;
	}
	public void setRelType(int relType) {
		this.relType = relType;
	}
	public int[] getRight() {
		return right;
	}
	public void setRight(int[] right) {
		this.right = right;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	
	
}
