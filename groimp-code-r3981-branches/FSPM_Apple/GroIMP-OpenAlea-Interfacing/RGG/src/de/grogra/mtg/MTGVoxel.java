package de.grogra.mtg;

import javax.vecmath.Vector3d;

public class MTGVoxel {

	private int branchIndex;
	private Vector3d min;
	private Vector3d max;
	private double deltax;
	private double deltay;
	private double deltaz;
	public static int voxelsNumber;
	
	public MTGVoxel(int branchIndex, Vector3d min, Vector3d max)
	{
		this.branchIndex = branchIndex;
		min = new Vector3d();
		max = new Vector3d();
		this.min = min;
		this.max = max;
		voxelsNumber++;
		deltax=deltay=deltaz=0;
	}
	
	public MTGVoxel(int branchIndex)
	{
		this.branchIndex = branchIndex;
		voxelsNumber++;
		deltax=deltay=deltaz=0;
		
		min = new Vector3d();
		max = new Vector3d();
		
		min.x = Double.MAX_VALUE;
		min.y = Double.MAX_VALUE;
		min.z = Double.MAX_VALUE;
		
		max.x = Double.MIN_VALUE;
		max.y = Double.MIN_VALUE;
		max.z = Double.MIN_VALUE;
	}
	
	public void setMin(Vector3d min)
	{
		this.min = min;
	}
	
	public void setMax(Vector3d max)
	{
		this.max = max;
	}
	
	public Vector3d getMin()
	{
		return min;
	}
	
	public Vector3d getMax()
	{
		return max;
	}
	
	public int getBranchIndex()
	{
		return branchIndex;
	}
	
	public double getDeltax() {
		return deltax;
	}

	public void setDeltax(double deltax) {
		this.deltax = deltax;
	}

	public double getDeltay() {
		return deltay;
	}

	public void setDeltay(double deltay) {
		this.deltay = deltay;
	}

	public double getDeltaz() {
		return deltaz;
	}

	public void setDeltaz(double deltaz) {
		this.deltaz = deltaz;
	}
	
	public void translate()
	{
		Vector3d trans = new Vector3d(deltax,deltay,deltaz);
		min.add(trans);
		max.add(trans);
	}
	
	public boolean equals(MTGVoxel another)
	{
		return (another.getBranchIndex()==this.branchIndex);
	}
}

