package de.grogra.gpuflux.scene.BVH;

import java.io.IOException;

import javax.vecmath.Tuple3d;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.vecmath.BoundingBox3d;

public class BVHTree {

	public static final double TPRIM = 10;
	public static final float TAABB = 1;
	public static final int MAX_DEPTH = 63;
	
	public BVHTree( BVHNode root, BoundingBox3d bbox, Integer [] primOrder)
	{
		this.root = root;
		this.bbox = bbox;
		this.primOrder = primOrder;
	}
	
	private BoundingBox3d bbox;
	private BVHNode root;
	private Integer []primOrder;
	
	public BVHNode getRoot(){ return root; };
	public BoundingBox3d getBounds() { return bbox; };
	public Integer [] getVolumeOrdering() { return primOrder; };
	
	public int getRootIdx()
	{
		return root.nidx==0?-1:root.nidx;
	}
	
	private int serializedNodes = 0;
	public void serializeBVH ( ComputeByteBuffer computeByteBuffer , int primitiveIdxOffset ) throws IOException
	{
		assert( root != null );
		
		serializedNodes = 0;
		serializeBVH( computeByteBuffer , root ,  primitiveIdxOffset );
	}
	
	private int serializeBVH ( ComputeByteBuffer computeByteBuffer , BVHNode node , int primitiveIdxOffset) throws IOException
	{
		if( !node.isLeaf() )
		{
			BVHNode left = node.left;
			BVHNode right = node.right;
			
			// convert the two subtrees
			int c0idx = serializeBVH( computeByteBuffer , left , primitiveIdxOffset );
			int c1idx = serializeBVH( computeByteBuffer , right , primitiveIdxOffset );
			
			// write bounding box for left child
			computeByteBuffer.writeFloat((float) left.bb.getMin().x); computeByteBuffer.writeFloat((float) left.bb.getMax().x);
			computeByteBuffer.writeFloat((float) left.bb.getMin().y); computeByteBuffer.writeFloat((float) left.bb.getMax().y);
			computeByteBuffer.writeFloat((float) left.bb.getMin().z); computeByteBuffer.writeFloat((float) left.bb.getMax().z);
					
			// NOTE: second bounding box has an alternative order!
			
			// write bounding box for right child
			computeByteBuffer.writeFloat((float) right.bb.getMin().z); computeByteBuffer.writeFloat((float) right.bb.getMax().z);
			computeByteBuffer.writeFloat((float) right.bb.getMin().x); computeByteBuffer.writeFloat((float) right.bb.getMax().x);
			computeByteBuffer.writeFloat((float) right.bb.getMin().y); computeByteBuffer.writeFloat((float) right.bb.getMax().y);
						
			// store child indices
			c0idx = left.isLeaf()?-c0idx-1:c0idx;
			c1idx = right.isLeaf()?-c1idx-1:c1idx;
			
			computeByteBuffer.writeInt(c0idx);
			computeByteBuffer.writeInt(c1idx);
			
			// write the dummy values
			computeByteBuffer.writeInt(0);
			computeByteBuffer.writeInt(0);
		}
		else
		{
			// skip the bounding box data
			computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);
			computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);
			
			computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);
			computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);computeByteBuffer.writeFloat(-1.f);
			
			// write the segment of the primitive index array
			computeByteBuffer.writeInt(node.idx + primitiveIdxOffset);
			computeByteBuffer.writeInt(node.pcount);
			
			// write the dummy values
			computeByteBuffer.writeInt(0);
			computeByteBuffer.writeInt(0);
		}
		
		node.nidx = serializedNodes;
		
		return serializedNodes++;
	}
	
	public void serializeBIH ( ComputeByteBuffer computeByteBuffer , int primitiveIdxOffset ) throws IOException
	{
		assert( root != null );
		
		serializedNodes = 0;
		serializeBIH( computeByteBuffer , root ,  primitiveIdxOffset );
	}
	
	private int serializeBIH ( ComputeByteBuffer computeByteBuffer , BVHNode node , int primitiveIdxOffset) throws IOException
	{
		if( !node.isLeaf() )
		{
			BVHNode left = node.left;
			BVHNode right = node.right;
			
			int axis = node.axis;
			float left_boundary = (float) getAxis( left.bb.getMax() , axis );
			float right_boundary = (float) getAxis( right.bb.getMin() , axis );
			
			// convert the two subtrees
			int c0idx = serializeBIH( computeByteBuffer , left , primitiveIdxOffset );
			int c1idx = serializeBIH( computeByteBuffer , right , primitiveIdxOffset );
			
			// store child indices
			c0idx = left.isLeaf()?-c0idx-1:c0idx;
			c1idx = right.isLeaf()?-c1idx-1:c1idx;
			
			computeByteBuffer.writeInt((c0idx << 2) | axis);
			computeByteBuffer.writeInt(c1idx);
			
			// store boundaries
			computeByteBuffer.writeFloat(left_boundary);
			computeByteBuffer.writeFloat(right_boundary);
		}
		else
		{
			// store axis
			computeByteBuffer.writeInt(-1);
			// store dummy
			computeByteBuffer.writeInt(0);
			
			// write the segment of the primitive index array
			computeByteBuffer.writeInt(node.idx + primitiveIdxOffset);
			computeByteBuffer.writeInt(node.pcount);
		}
		
		node.nidx = serializedNodes;
		
		return serializedNodes++;
	}
	
	public static double getSAH(double area, double leftArea, double rightArea, int leftPrims, int rightPrims)
	{
		return 2*BVHTree.TAABB + (leftArea * leftPrims * BVHTree.TPRIM + rightArea * rightPrims * BVHTree.TPRIM ) / area;
	}
	
	public double computeSAH()
	{
		if( root != null )
			return root.computeSAH( bbox.area() );
		return 0.f;
	}
	
	private double getAxis(Tuple3d t,int idx) {
		if( idx == 0 )
			return t.x;
		if( idx == 1 )
			return t.y;
		if( idx == 2 )
			return t.z;
		return 0;
		
	}
	public void prune() {
		BVHNode.pruneEmptyNodes(root);
	}
	
}
