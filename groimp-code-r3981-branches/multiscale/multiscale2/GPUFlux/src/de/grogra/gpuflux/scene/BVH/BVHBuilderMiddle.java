package de.grogra.gpuflux.scene.BVH;

import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import de.grogra.gpuflux.scene.volume.FluxVolume;
import de.grogra.vecmath.BoundingBox3d;

public class BVHBuilderMiddle implements BVHBuilder {

	private class BVHPrimitive
	{
		public BoundingBox3d bb = new BoundingBox3d();
		public double center[] = new double[3]; 
		//public FluxVolume volume = null;
		public int idx;
	}
	
	private static final int MAX_DEPTH = 63;
	private static final int MIN_PRIMS = 1; //5; // TODO
	
	String log = "";
	private Integer[] primIdx;
	private BVHPrimitive[] prims;
	
	public BVHTree construct(Vector<? extends FluxVolume> volumes) {
		
		final int N = volumes.size();
		
		log = "";
		
		BoundingBox3d bbox = new BoundingBox3d();

		prims = new BVHPrimitive[N];
		primIdx = new Integer[N];

		Point3d centroid = new Point3d();
		
		long computeBB = System.currentTimeMillis ();
		for( int i = 0 ; i < N ; i++ )
		{
			prims[i] = new BVHPrimitive();
			// compute bounding box
			//(volumes.get(i)).getExtent(prims[i].bb, temp);
			prims[i].bb = volumes.get(i).getBoundingBox();
			prims[i].bb.getCenter(centroid);
			
			prims[i].center[0] = centroid.x;
			prims[i].center[1] = centroid.y;
			prims[i].center[2] = centroid.z;
			
			//prims[i].volume = volumes.get(i);
			prims[i].idx = i;
			
			bbox.extent(prims[i].bb);
			
			primIdx[i] = new Integer(i);
		}
		computeBB = System.currentTimeMillis () - computeBB;
		
		long subdivideTree = System.currentTimeMillis ();
		BVHNode root = subdivide( 0 , N , bbox, bbox, 0);
		subdivideTree = System.currentTimeMillis () - subdivideTree;

		log += "        Compute BB time:       " + computeBB + " ms\n";
		log += "        Subdivide tree time:   " + subdivideTree + " ms\n";
		
		BVHTree tree = new BVHTree(root, bbox, primIdx);
		
		for( int i = 0 ; i < N ; i++ )
		{
			primIdx[i] = prims[i].idx;
		}
		
		return tree;
	}
	
	Point3d centerTmp = new Point3d();

	private BVHNode subdivide(int from, int to, BoundingBox3d bbox, BoundingBox3d nodebox , int depth) {
		
		if(depth < MAX_DEPTH && (to - from) > MIN_PRIMS)
		{
			int splitAxis = getMaxAxis(nodebox);
			nodebox.getCenter(centerTmp);
			double splitPos = getAxis( centerTmp, splitAxis );
			
			BoundingBox3d leftbb = new BoundingBox3d();
			BoundingBox3d rightbb = new BoundingBox3d();
			
			int low = from;
			int high = to - 1;
			while(true)
			{ 
				while(splitPos<=prims[high].center[splitAxis])
				{
					if(high==low) break;
					rightbb.extent(prims[high].bb);
					high--;
				}
				while(splitPos>prims[low].center[splitAxis])
				{
					if(high==low) break;
					leftbb.extent(prims[low].bb);
					low++;
				}
							
				if(high==low)
					break;
				
				BVHPrimitive tmp = prims[high];
				prims[high] = prims[low];
				prims[low] = tmp;
			}
			
			rightbb.extent(prims[high].bb);
			
			BoundingBox3d leftnodebox = nodebox.clone();
			BoundingBox3d rightnodebox = nodebox.clone();
			
			if(splitAxis == 0) {
				leftnodebox.getMax().x = Math.min(splitPos,leftnodebox.getMax().x);
				rightnodebox.getMin().x = Math.max(splitPos,rightnodebox.getMin().x);
			} else if (splitAxis == 1) {
				leftnodebox.getMax().y = Math.min(splitPos,leftnodebox.getMax().y);
				rightnodebox.getMin().y = Math.max(splitPos,rightnodebox.getMin().y);
			} else {
				leftnodebox.getMax().z = Math.min(splitPos,leftnodebox.getMax().z);
				rightnodebox.getMin().z = Math.max(splitPos,rightnodebox.getMin().z);
			}
			
			BVHNode leftNode = subdivide( from, low , leftbb, leftnodebox, depth+1 );
			BVHNode rightNode = subdivide( high, to , rightbb, rightnodebox, depth+1 );
			
			if( low - from == 0 )
				return rightNode;
			if( to - high == 0 )
				return leftNode;
			
			return new BVHNode( splitAxis, from , to - from , bbox , leftNode , rightNode );
		}
		else
		{
			// create a leaf
			return new BVHNode( -1, from , to - from , bbox , null , null );
		}
	}
	
	public int getMaxAxis(BoundingBox3d bb) {
		Tuple3d max = bb.getMax();
		Tuple3d min = bb.getMin();
		double dx = max.x - min.x;
		double dy = max.y - min.y;
		double dz = max.z - min.z;
		if( dx > dy )
		{
			if( dx > dz )
				return 0;
			else
				return 2;
		}
		else
		{
			if( dy > dz )
				return 1;
			else
				return 2;
		}
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

	public String getLog() {
		return log;
	}

}
