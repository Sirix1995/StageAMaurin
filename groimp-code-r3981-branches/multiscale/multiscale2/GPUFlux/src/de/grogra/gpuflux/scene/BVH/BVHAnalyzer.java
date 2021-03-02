package de.grogra.gpuflux.scene.BVH;

public class BVHAnalyzer {

	private double SAH;
	
	private int depthHist[];
	private int leafHist[];

	public String analyzeBVH( BVHTree tree )
	{
		depthHist = new int[BVHTree.MAX_DEPTH];
		leafHist = new int[99];
		
		String stats = ""; 
		
		BVHNode root = tree.getRoot();
		
		analyzeLeafs( root , 0 );
		SAH = computeSAH( root , tree.getBounds().area() );
		
		stats += "    SAH cost: " + SAH + "\n";
		
		stats += "    Leaf depth histrogram:";
		for( int i = 0 ; i < depthHist.length ; i++ )
		{
			if( (i % 25) == 0 ) stats += "\n        ";
			stats += depthHist[i] + " ";
		}
		stats += "\n";
		
		stats += "    Leaf primitives histrogram:";
		for( int i = 0 ; i < leafHist.length ; i++ )
		{
			if( (i % 25) == 0 ) stats += "\n        ";
			stats += leafHist[i] + " ";
		}
		stats += "\n";
		
		return stats;
	}

	private void analyzeLeafs(BVHNode node, int depth) {
		if( node.isLeaf() )
		{
			depthHist[Math.min(depth,depthHist.length - 1)]++;
			leafHist[Math.min(node.pcount,leafHist.length - 1)]++;
		}
		
		if( node.left != null )
			analyzeLeafs( node.left , depth + 1 );
		if( node.right != null )
			analyzeLeafs( node.right , depth + 1 );
		
	}

	private double computeSAH(BVHNode node, double parentArea) {
		
		double area = node.bb.area();
		double p = node.bb.area() / parentArea;
		double cost = BVHTree.TAABB;
		
		if( node.isLeaf() )
		{
			cost += p * node.pcount * BVHTree.TPRIM;
		}
		else
		{
			if( node.left != null )
				cost += p * computeSAH(node.left , area);
			if( node.right != null )
				cost += p * computeSAH(node.right , area);
		}
		
		return cost;
	}
	
}
