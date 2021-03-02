
package de.grogra.gpuflux.scene.BVH;

import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import de.grogra.gpuflux.scene.volume.FluxVolume;
import de.grogra.gpuflux.utils.TaskMonitor;
import de.grogra.vecmath.BoundingBox3d;

public class ThreadedBVHBuilderMiddle implements BVHBuilder 
{
	private static final int WORK_PER_THREAD = 10000;
	private static final int TREE_WORK_PER_THREAD = 3000;

	private class BVHPrimitive
	{
		public BoundingBox3d bb = new BoundingBox3d();
		public double center[] = new double[3]; 
		public int idx;
	}
	
	private static final int MAX_DEPTH = 63;
	private static final int MIN_PRIMS = 1;
	
	private TaskMonitor jobs;
	
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
			prims[i].bb = volumes.get(i).getBoundingBox();
			prims[i].bb.getCenter(centroid);
			
			prims[i].center[0] = centroid.x;
			prims[i].center[1] = centroid.y;
			prims[i].center[2] = centroid.z;
			
			prims[i].idx = i;
			
			bbox.extent(prims[i].bb);
			
			primIdx[i] = new Integer(i);
		}
		computeBB = System.currentTimeMillis () - computeBB;
		
		long subdivideTree = System.currentTimeMillis ();
		
		// create threadpool
		ExecutorService executor = Executors.newCachedThreadPool();
		// create substitute root
		BVHNode root = new BVHNode( -1, 0 , N , bbox, null, null);
		// Create root job
		MedianSplitJob job = new MedianSplitJob( 0 , N , bbox, bbox.clone(), 0, primIdx, prims, root, executor );
		// Create job syncrhonizer
		jobs = new TaskMonitor();
		jobs.startTask();
		// Execute job
		executor.execute( job );
		// Wait for completion
		jobs.awaitTasks();
		// shutdown
		executor.shutdown();
		
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
			
	// a single median split job
	class MedianSplitJob implements Runnable
	{
		private int work = 0;
		
		// object range
		private int from, to;
		// cell bounding box
		private BoundingBox3d bbox;
		// object bounding box
		private BoundingBox3d nodebox;
		// tree depth
		private int depth;
		// primitive index list
		private Integer[] primIdx;
		// primitives
		private BVHPrimitive[] prims; 
		
		// inner node leaf
		private BVHNode leaf;
		
		Executor executor;
		
		public MedianSplitJob( 
			int from, int to,
			BoundingBox3d bbox,
			BoundingBox3d nodebox,
			int depth,
			Integer[] primIdx,
			BVHPrimitive[] prims,
			BVHNode leaf,
			Executor executor
			)
		{
			this.from = from;		
			this.to = to;
			this.bbox = bbox;
			this.nodebox = nodebox;
			this.depth = depth;
			this.primIdx = primIdx;
			this.prims = prims;
			this.leaf = leaf;
			this.executor = executor;
		}

		public void run()
		{
			// execute subdivision
			BVHNode root = subdivide( from, to, bbox, nodebox, depth, true );
			
			// copy subtree in root leaf
			leaf.set( root );
			
			// finish the task
			jobs.finishTask();
		};
		
		Point3d centerTmp = new Point3d();
		
		private BVHNode subdivide(int from, int to, BoundingBox3d bbox, BoundingBox3d nodebox , int depth, boolean root) {
			
			if( work > WORK_PER_THREAD && (to - from) > TREE_WORK_PER_THREAD )
			{
				BVHNode leaf = new BVHNode( -1, from , to - from , bbox , null , null );
				
				// create new job
				MedianSplitJob job = new MedianSplitJob( 
					from, to, 
					bbox,
					nodebox,
					depth,
					primIdx,
					prims,
					leaf, 
					executor );
								
				// start new task
				jobs.startTask();
				// execute job
				executor.execute( job );
				
				// return temporary leaf
				return leaf;
			}
			
			if(depth < MAX_DEPTH && (to - from) > MIN_PRIMS)
			{
				int splitAxis = getMaxAxis(nodebox);
				nodebox.getCenter(centerTmp);
				double splitPos = getAxis( centerTmp, splitAxis );
			
				BoundingBox3d leftbb = new BoundingBox3d();
				BoundingBox3d rightbb = new BoundingBox3d();
			
				// update work
				work += to - from;
			
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
			
				BVHNode leftNode = subdivide( from, low , leftbb, leftnodebox, depth+1, false );
				BVHNode rightNode = subdivide( high, to , rightbb, rightnodebox, depth+1, false );
			
				if( !root )
				{
					if( low - from == 0 )
						return rightNode;
					if( to - high == 0 )
						return leftNode;
				}
			
				return new BVHNode( splitAxis, from , to - from , bbox , leftNode , rightNode );
			}
			else
			{
				// create a leaf
				return new BVHNode( -1, from , to - from , bbox , null , null );
			}
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



