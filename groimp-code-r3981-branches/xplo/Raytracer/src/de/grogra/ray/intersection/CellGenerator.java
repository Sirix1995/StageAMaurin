package de.grogra.ray.intersection;

import de.grogra.ray.util.Ray;


public interface CellGenerator {

	public void initialize(OctreeCell sceneNode, int maxDepth);
	public void setRay(Ray ray);
	public void nextCell(NextCellOutput output);
	//public double getFirstT();
	
	
	public class NextCellOutput {
		public OctreeCell nextCell = null;
		//public double     enteringT;
		public double     leavingT;
		public boolean    errorOccurred = false;
	}
	
}
