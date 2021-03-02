package de.grogra.gpuflux.scene.BVH;

import de.grogra.vecmath.BoundingBox3d;

public class BVHNode
{
	public BVHNode( int axis, int idx , int pcount , BoundingBox3d bb , BVHNode left , BVHNode right )
	{
		assert( !((left == null) ^ (right == null)) );
		
		this.axis = axis;
		this.idx = idx;
		this.pcount = pcount;
		this.bb = bb.clone();
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeaf(){ return left == null; };
	
	public int axis;
	public int idx, pcount;
	public BoundingBox3d bb;
	public BVHNode left, right;
	public int nidx;
	
	public double computeSAH(double parentArea) {
		double area = bb.area();
		double p = bb.area() / parentArea;
		double cost = BVHTree.TAABB;
		
		if( isLeaf() )
		{
			cost += p * pcount * BVHTree.TPRIM;
		}
		else
		{
			if( left != null )
				cost += p * left.computeSAH(area);
			if( right != null )
				cost += p * right.computeSAH(area);
		}
		
		return cost;
	}
	
	static BVHNode pruneEmptyNodes( BVHNode node )
	{
		if( node.isLeaf() )
		{
			if( node.pcount == 0 )
				return null;
			else
				return node;
		}

		node.left = pruneEmptyNodes( node.left );
		node.right = pruneEmptyNodes( node.right );

		if( node.left == null )
			return node.right;
		if( node.right == null )
			return node.left;
			
		return node;
	}

	void set( BVHNode node )
	{
		axis = node.axis;
		idx = node.idx;
		pcount = node.pcount;
		bb = node.bb;
		left = node.left;
		right = node.right;
		nidx = node.nidx;
	}

};