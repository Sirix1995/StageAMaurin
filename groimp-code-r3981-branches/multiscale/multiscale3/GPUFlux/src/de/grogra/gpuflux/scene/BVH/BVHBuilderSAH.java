package de.grogra.gpuflux.scene.BVH;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.gpuflux.scene.BVH.BVHTree;
import de.grogra.gpuflux.scene.volume.FluxVolume;
import de.grogra.vecmath.BoundingBox3d;

public class BVHBuilderSAH implements BVHBuilder{
	
	private String log = "";
	
	private BVHPrimitive [] prims;
	
	private Integer [][] primIdx = new Integer [3][];
	private Integer [] tmpIdx;
	
	private int ncount;
	
	private BoundingBox3d [] sweepBBox;
	
	private class BVHPrimitive
	{
		public BoundingBox3d bb = new BoundingBox3d();
		public Point3d centroid = new Point3d();
		public boolean left;
	}
	
	public BVHTree construct( Vector<? extends FluxVolume> volumes )
	{
		assert( volumes != null );
		/* 	Compute bounding volumes for all primitives
		 	Sort primitives along all 3 axis
			Repeatedly:
				Check all splitting planes and select the optimal according to SAH
				Split primitive lists
		*/
		
		final int N = volumes.size();
		
		log = "";
		
		prims = new BVHPrimitive[N];
		
		sweepBBox = new BoundingBox3d[N];
		
		primIdx[0] = new Integer[N];
		primIdx[1] = new Integer[N];
		primIdx[2] = new Integer[N];
		tmpIdx = new Integer[N];
		
		BoundingBox3d bbox = new BoundingBox3d();
		
		long computeBB = System.currentTimeMillis ();
		for( int i = 0 ; i < N ; i++ )
		{
			prims[i] = new BVHPrimitive();
			// compute bounding box
			//(volumes.get(i)).getExtent(prims[i].bb, temp);
			prims[i].bb = volumes.get(i).getBoundingBox();
			prims[i].bb.getCenter(prims[i].centroid);
			
			bbox.extent(prims[i].bb);
			
			primIdx[0][i] = primIdx[1][i] = primIdx[2][i] = new Integer(i);
		}
		computeBB = System.currentTimeMillis () - computeBB;
			
		// sort primitives along 3D axis, based on centroids
		long sortPrimitives = System.currentTimeMillis ();
		Arrays.sort( primIdx[0] , 0 , N , new Comparator<Integer>()
			{
				public int compare(Integer o1, Integer o2) {
					return (int) (BVHBuilderSAH.this.prims[o1.intValue()].centroid.x - BVHBuilderSAH.this.prims[o2.intValue()].centroid.x);
				}
			} );
		Arrays.sort( primIdx[1] , 0 , N , new Comparator<Integer>()
			{
				public int compare(Integer o1, Integer o2) {
					return (int) (BVHBuilderSAH.this.prims[o1.intValue()].centroid.y - BVHBuilderSAH.this.prims[o2.intValue()].centroid.y);
				}
			} );
		Arrays.sort( primIdx[2] , 0 , N , new Comparator<Integer>()
			{
				public int compare(Integer o1, Integer o2) {
					return (int) (BVHBuilderSAH.this.prims[o1.intValue()].centroid.z - BVHBuilderSAH.this.prims[o2.intValue()].centroid.z);
				}
			} );
		sortPrimitives = System.currentTimeMillis () - sortPrimitives;
		
		ncount = 0;
		long subdivideTree = System.currentTimeMillis ();
		BVHNode root = subdivide( 0 , N , bbox , 0 );
		subdivideTree = System.currentTimeMillis () - subdivideTree;
				
		log += "        Compute BB time:        " + computeBB + " ms\n";
		log += "        Sort Primitives Time:   " + sortPrimitives + " ms\n";
		log += "        Subdivide tree time:    " + subdivideTree + " ms\n";
		
		return new BVHTree(root, bbox, primIdx[0]);
	}
	
	private BVHNode subdivide( int fromIdx , int toIdx , BoundingBox3d bbox , int depth )
	{
		ncount++;
		
		int pcount = toIdx - fromIdx;
		
		double area = bbox.area();
		
		double bestCost = depth==0?Double.MAX_VALUE:BVHTree.TPRIM*(double)(toIdx - fromIdx);
		int bestAxis = -1;
		int bestEvent = -1;
		
		BoundingBox3d leftBBox = new BoundingBox3d() , rightBBox = new BoundingBox3d();
		BoundingBox3d bestLeftBBox = null, bestRightBBox = null;
		
		
		if(depth < BVHTree.MAX_DEPTH)
		{
				
			// evaluate split planes along all 3 axis
			for( int axis = 0 ; axis < 3 ; axis++ )
			{
				leftBBox.empty();
				rightBBox.empty();
				
				// Sweep from left
				for( int left = fromIdx ; left < toIdx - 1 ; left++ )
				{
					int idx = primIdx[axis][left];
					
					leftBBox.extent( prims[idx].bb );
					
					sweepBBox[left] = leftBBox.clone();
				}
				
				// Sweep from right
				for( int right = toIdx - 1 ; right > fromIdx ; right-- )
				{
					int idx = primIdx[axis][right];
					
					rightBBox.extent( prims[idx].bb );
					
					double leftArea = sweepBBox[right-1].area();
					double rightArea = rightBBox.area();
									
					// evaluate SAH-cost
					//double thisCost = 2.f*BVHTree.TAABB + (leftArea * (right - fromIdx) * BVHTree.TPRIM + rightArea * (toIdx - right) * BVHTree.TPRIM ) / area;
					double thisCost = BVHTree.getSAH(area, leftArea, rightArea, (right - fromIdx), (toIdx - right));
					
					// update best candidate
					if( thisCost < bestCost )
					{
						bestCost = thisCost;
						bestEvent = right;
						bestAxis = axis;
						bestLeftBBox = sweepBBox[right-1].clone();
						bestRightBBox = rightBBox.clone();
					}
				}
			}
		}
		
		if( bestAxis == -1 )
		{
			// create a leaf
			return new BVHNode( -1, fromIdx , pcount , bbox , null , null );
		}
		else
		{
			// determine the side for each primitive
			for( int i = fromIdx ; i < toIdx ; i++ )
			{
				boolean left = i < bestEvent;
				int idx = primIdx[bestAxis][i];
				prims[idx].left = left;
			}
			
			// split index arrays for other two axis
			split( (bestAxis+1)%3 , fromIdx , toIdx , bestEvent );
			split( (bestAxis+2)%3 , fromIdx , toIdx , bestEvent );
			
			// further subdivide the two nodes
			BVHNode left = subdivide( fromIdx , bestEvent , bestLeftBBox , depth+1 );
			BVHNode right = subdivide( bestEvent , toIdx , bestRightBBox , depth+1 );
			
			// create interior node
			return new BVHNode( bestAxis, fromIdx , pcount , bbox , left , right );
		}
	}
		
	private void split(int axis, int fromIdx, int toIdx, int bestEvent) {
		// split the index list while preserving the order in both segments
		
		int leftCount = fromIdx, rightCount = bestEvent;
		
		// iterate over all indices in order
		for( int i = fromIdx ; i < toIdx ; i++ )
		{
			Integer idx = primIdx[axis][i];
			              
			// distribute to the right split side
			if( prims[idx].left )
				tmpIdx[leftCount++] = idx;
			else
				tmpIdx[rightCount++] = idx;
		}
		
		for( int i = fromIdx ; i < toIdx ; i++ )
			primIdx[axis][i] = tmpIdx[i];
	}

	public String getLog() {
		return log;
	}
}
