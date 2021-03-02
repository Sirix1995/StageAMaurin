package de.grogra.ray.intersection;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.intersection.CellGenerator.NextCellOutput;
import de.grogra.ray.quality.Quality;
import de.grogra.ray.quality.Timer;
import de.grogra.ray.util.Ray;

public class DefaultCellGenerator implements CellGenerator {

	private OctreeCell m_root = null;
	private float[]    m_lengthX = null;
	private float[]    m_lengthY = null;
	private float[]    m_lengthZ = null;
	private int        m_maxDepth;
	private int        m_maxDivisionX;
	
	private boolean    m_hasNextCell = false;
	private boolean    m_isFirstCell = true;
	
	private final BoxIntersectionInput m_intersectionInput = 
		new BoxIntersectionInput();
	private final BoxIntersectionLocals m_intersectionLocals =
		new BoxIntersectionLocals();
	private final BoxIntersectionOutput m_intersectionOutput = 
		new BoxIntersectionOutput();
	
	private final FirstCellLocals m_firstCellLocals =
		new FirstCellLocals();
	
	//private Quality m_quality = Quality.getQualityKit();
	//private Timer   m_timer;
	
	//private OctreeCell m_lastFirstCell = null;
	//private int        m_lastFirstCellPart;
	
	public int m_same = 0;
	public int m_different = 0;
	
	private OctreeCell    m_lastCell         = null;
	private final Point3f m_lastLeavingPoint = new Point3f();
	private int           m_lastLeavingPart;
	
	
	public void initialize(OctreeCell sceneNode, int maxDepth) {
		m_root = sceneNode;
		m_maxDepth = maxDepth;
		m_lengthX = new float[maxDepth+2];
		m_lengthY = new float[maxDepth+2];
		m_lengthZ = new float[maxDepth+2];
		float cur_depth_x = m_root.getMaxValues().x-m_root.getMinValues().x;
		float cur_depth_y = m_root.getMaxValues().y-m_root.getMinValues().y;
		float cur_depth_z = m_root.getMaxValues().z-m_root.getMinValues().z;
		for (int i=0;i<(maxDepth+2);i++) {
			m_lengthX[i] = cur_depth_x;
			m_lengthY[i] = cur_depth_y;
			m_lengthZ[i] = cur_depth_z;
			cur_depth_x /= 2.0f;
			cur_depth_y /= 2.0f;
			cur_depth_z /= 2.0f;
		}
		m_maxDivisionX = 1 << maxDepth;
		//System.out.println("div:"+m_maxDivisionsX);
		//getFirstCell();
	}
	
	
	public void setRay(Ray ray) {
		m_intersectionInput.origin.set(ray.getOrigin());
		m_intersectionInput.direction.set(ray.getDirection());
		// TODO initialize cell generator
		if (Intersections.isPointInsideBox(m_intersectionInput.origin,m_root.getMinValues(),m_root.getMaxValues())) {
			m_hasNextCell = true;
			getFirstCellFromPoint(m_intersectionInput.origin);
		} else {
			// determine first intersection cell
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.8032335)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.y-  0.47582844)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.z- -0.3583339)<0.0001)) {
//						
//				System.err.println("outside");
//					
//			}
			
			m_intersectionInput.minValues.set(m_root.getMinValues());
			m_intersectionInput.maxValues.set(m_root.getMaxValues());
			getBoxEnteringIntersection(m_intersectionInput,m_intersectionLocals,m_intersectionOutput);
			if (!m_intersectionOutput.hasIntersection) {
				m_hasNextCell = false;
				return;
			}  else {
				m_hasNextCell = true;
				getFirstCellFromOutside();
			}
		}
		m_intersectionInput.minValues.set(m_firstCellLocals.cur_cell.getMinValues());
		m_intersectionInput.maxValues.set(m_firstCellLocals.cur_cell.getMaxValues());
		getBoxLeavingIntersection(m_intersectionInput,m_intersectionLocals,m_intersectionOutput);
		m_lastCell = m_firstCellLocals.cur_cell;		
		m_lastLeavingPoint.set(m_intersectionOutput.leavingPoint);
		m_lastLeavingPart = m_intersectionOutput.leavingPart;
		
	}
	
	
//	public double getFirstT() {
//		m_intersectionInput.minValues.set(m_root.getMinValues());
//		m_intersectionInput.maxValues.set(m_root.getMaxValues());
//		getBoxEnteringIntersection(m_intersectionInput,
//				m_intersectionLocals,m_intersectionOutput);
//		return m_intersectionOutput.enteringT;
//	}
	
	
	private void getFirstCellFromPoint(Point3d point) {
		
		m_firstCellLocals.x = (int)Math.
			floor((point.x-m_root.getMinValues().x)/
				  (m_lengthX[0])*m_maxDivisionX);
		m_firstCellLocals.y = (int)Math.
			floor((point.y-m_root.getMinValues().y)/
				  (m_lengthY[0])*m_maxDivisionX);
		m_firstCellLocals.z = (int)Math.
			floor((point.z-m_root.getMinValues().z)/
				  (m_lengthZ[0])*m_maxDivisionX);
		
		if (m_firstCellLocals.x>=m_maxDivisionX) { m_firstCellLocals.x = m_maxDivisionX-1; }
		if (m_firstCellLocals.y>=m_maxDivisionX) { m_firstCellLocals.y = m_maxDivisionX-1; }
		if (m_firstCellLocals.z>=m_maxDivisionX) { m_firstCellLocals.z = m_maxDivisionX-1; }
		
		getCellFromPoint(m_firstCellLocals.x,
				m_firstCellLocals.y,
				m_firstCellLocals.z);
		/*
		m_firstCellLocals.cur_cell = m_root;
		for (int i=0;i<m_maxDepth;i++) {
			if (m_firstCellLocals.cur_cell.hasChildren()) {
				m_firstCellLocals.shift = m_maxDepth-1-i;
				m_firstCellLocals.bit_mask = (1 << m_firstCellLocals.shift);
				m_firstCellLocals.bit_x = 
					(m_firstCellLocals.x & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.bit_y = 
					(m_firstCellLocals.y & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;	
				m_firstCellLocals.bit_z = 
					(m_firstCellLocals.z & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.child = (m_firstCellLocals.bit_x << 2) +
					(m_firstCellLocals.bit_y << 1) +
					(m_firstCellLocals.bit_z);
				m_firstCellLocals.cur_cell = m_firstCellLocals.cur_cell.getChild(
					m_firstCellLocals.child);
			} else {
				break;
			}
		}
		*/
	}
	
	
	private void getCellFromPoint(int x, int y, int z) {
		m_firstCellLocals.cur_cell = m_root;
		for (int i=0;i<m_maxDepth;i++) {
			if (m_firstCellLocals.cur_cell.hasChildren()) {
				m_firstCellLocals.shift = m_maxDepth-1-i;
				m_firstCellLocals.bit_mask = (1 << m_firstCellLocals.shift);
				m_firstCellLocals.bit_x = 
					(x & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.bit_y = 
					(y & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;	
				m_firstCellLocals.bit_z = 
					(z & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.child = (m_firstCellLocals.bit_x << 2) +
					(m_firstCellLocals.bit_y << 1) +
					(m_firstCellLocals.bit_z);
				m_firstCellLocals.cur_cell = m_firstCellLocals.cur_cell.
					getChild(m_firstCellLocals.child);
			} else {
				break;
			}
		}
//		System.out.println("inner cell"+m_firstCellLocals.cur_cell);
	}
	
	
	private void getFirstCellFromOutside() {

//		if ((Math.abs(m_intersectionInput.direction.x- -0.8032335)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.y-  0.47582844)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.z- -0.3583339)<0.0001)) {
//					
//			System.err.println("getFirstCellFromOutside()");
//			System.err.println("m_intersectionOutput.enteringPart:"+m_intersectionOutput.enteringPart);
//				
//		}
		
		m_isFirstCell = true;
		
		//if (testLastFirstCellResult()) { return; }
		
		//m_different++;
		
		switch(m_intersectionOutput.enteringPart) {
		case Intersections.BOX_FRONT:
			m_firstCellLocals.y = 0;
			m_firstCellLocals.x = (int)Math.
				floor((m_intersectionOutput.enteringPoint.x-m_root.getMinValues().x)/
					  (m_lengthX[0])*m_maxDivisionX);
			m_firstCellLocals.z = (int)Math.
				floor((m_intersectionOutput.enteringPoint.z-m_root.getMinValues().z)/
					  (m_lengthZ[0])*m_maxDivisionX);
			if (m_firstCellLocals.x>=m_maxDivisionX) { m_firstCellLocals.x = m_maxDivisionX-1; }
			if (m_firstCellLocals.z>=m_maxDivisionX) { m_firstCellLocals.z = m_maxDivisionX-1; }
			break;
		case Intersections.BOX_BACK:
			m_firstCellLocals.y = m_maxDivisionX-1;
			m_firstCellLocals.x = (int)Math.
				floor((m_intersectionOutput.enteringPoint.x-m_root.getMinValues().x)/
					  (m_lengthX[0])*m_maxDivisionX);
			m_firstCellLocals.z = (int)Math.
				floor((m_intersectionOutput.enteringPoint.z-m_root.getMinValues().z)/
					  (m_lengthZ[0])*m_maxDivisionX);
			if (m_firstCellLocals.x>=m_maxDivisionX) { m_firstCellLocals.x = m_maxDivisionX-1; }
			if (m_firstCellLocals.z>=m_maxDivisionX) { m_firstCellLocals.z = m_maxDivisionX-1; }
			break;
		case Intersections.BOX_TOP:
			m_firstCellLocals.z = m_maxDivisionX-1;
			m_firstCellLocals.x = (int)Math.
				floor((m_intersectionOutput.enteringPoint.x-m_root.getMinValues().x)/
					  (m_lengthX[0])*m_maxDivisionX);
			m_firstCellLocals.y = (int)Math.
				floor((m_intersectionOutput.enteringPoint.y-m_root.getMinValues().y)/
					  (m_lengthY[0])*m_maxDivisionX);
			if (m_firstCellLocals.x>=m_maxDivisionX) { System.err.println("***");m_firstCellLocals.x = m_maxDivisionX-1; }
			if (m_firstCellLocals.y>=m_maxDivisionX) { System.err.println("***");m_firstCellLocals.y = m_maxDivisionX-1; }
			break;
		case Intersections.BOX_BOTTOM:
			m_firstCellLocals.z = 0;
			m_firstCellLocals.x = (int)Math.
				floor((m_intersectionOutput.enteringPoint.x-m_root.getMinValues().x)/
					  (m_lengthX[0])*m_maxDivisionX);
			m_firstCellLocals.y = (int)Math.
				floor((m_intersectionOutput.enteringPoint.y-m_root.getMinValues().y)/
					  (m_lengthY[0])*m_maxDivisionX);
			if (m_firstCellLocals.x>=m_maxDivisionX) { m_firstCellLocals.x = m_maxDivisionX-1; }
			if (m_firstCellLocals.y>=m_maxDivisionX) { m_firstCellLocals.y = m_maxDivisionX-1; }
			break;
		case Intersections.BOX_LEFT:
			m_firstCellLocals.x = 0;
			m_firstCellLocals.y = (int)Math.
				floor((m_intersectionOutput.enteringPoint.y-m_root.getMinValues().y)/
					  (m_lengthY[0])*m_maxDivisionX);
			m_firstCellLocals.z = (int)Math.
				floor((m_intersectionOutput.enteringPoint.z-m_root.getMinValues().z)/
					  (m_lengthZ[0])*m_maxDivisionX);
			if (m_firstCellLocals.y>=m_maxDivisionX) { m_firstCellLocals.y = m_maxDivisionX-1; }
			if (m_firstCellLocals.z>=m_maxDivisionX) { m_firstCellLocals.z = m_maxDivisionX-1; }
			break;
		case Intersections.BOX_RIGHT:
			m_firstCellLocals.x = m_maxDivisionX-1;
			m_firstCellLocals.y = (int)Math.
				floor((m_intersectionOutput.enteringPoint.y-m_root.getMinValues().y)/
					  (m_lengthY[0])*m_maxDivisionX);
			m_firstCellLocals.z = (int)Math.
				floor((m_intersectionOutput.enteringPoint.z-m_root.getMinValues().z)/
					  (m_lengthZ[0])*m_maxDivisionX);
			if (m_firstCellLocals.y>=m_maxDivisionX) { m_firstCellLocals.y = m_maxDivisionX-1; }
			if (m_firstCellLocals.z>=m_maxDivisionX) { m_firstCellLocals.z = m_maxDivisionX-1; }
			break;
		}
		
//		System.out.println("*xyz:"+m_firstCellLocals.x+","+m_firstCellLocals.y+","+m_firstCellLocals.z);
		
//		if ((Math.abs(m_intersectionInput.direction.x- -0.8032335)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.y-  0.47582844)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.z- -0.3583339)<0.0001)) {
//					
//			System.err.println("cell_from_outside:"+m_firstCellLocals.x+","+m_firstCellLocals.y+","+m_firstCellLocals.z);
//				
//		}
		
		m_firstCellLocals.cur_cell = m_root;
		for (int i=0;i<m_maxDepth;i++) {
			if (m_firstCellLocals.cur_cell.hasChildren()) {
				m_firstCellLocals.shift = m_maxDepth-1-i;
				m_firstCellLocals.bit_mask = (1 << m_firstCellLocals.shift);
				m_firstCellLocals.bit_x = 
					(m_firstCellLocals.x & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.bit_y = 
					(m_firstCellLocals.y & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;	
				m_firstCellLocals.bit_z = 
					(m_firstCellLocals.z & m_firstCellLocals.bit_mask) >> 
						m_firstCellLocals.shift;
				m_firstCellLocals.child = (m_firstCellLocals.bit_x << 2) +
					(m_firstCellLocals.bit_y << 1) +
					(m_firstCellLocals.bit_z);
				m_firstCellLocals.cur_cell = m_firstCellLocals.cur_cell.getChild(
					m_firstCellLocals.child);
			} else {
				break;
			}
		}
		
		
		
		//m_firstCellLocals.cur_cell.testInt = 1;
		m_firstCellLocals.lastFirstCellFromOutside = m_firstCellLocals.cur_cell;
		m_firstCellLocals.lastPart = m_intersectionOutput.enteringPart;
		
	}
	
	
	private boolean testLastFirstCellResult() {
		
		if (m_firstCellLocals.lastFirstCellFromOutside == null) { return false; }
		
		switch(m_firstCellLocals.lastPart) {
		case Intersections.BOX_FRONT:
		case Intersections.BOX_BACK:
			if ((m_intersectionOutput.enteringPoint.x>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().x) &&
				(m_intersectionOutput.enteringPoint.x<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().x) &&
				(m_intersectionOutput.enteringPoint.z>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().z) &&
				(m_intersectionOutput.enteringPoint.z<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().z)) {
				m_firstCellLocals.cur_cell = m_firstCellLocals.lastFirstCellFromOutside;
				return true;
			}
			break;
		case Intersections.BOX_TOP:
		case Intersections.BOX_BOTTOM:
			if ((m_intersectionOutput.enteringPoint.x>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().x) &&
				(m_intersectionOutput.enteringPoint.x<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().x) &&
				(m_intersectionOutput.enteringPoint.y>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().y) &&
				(m_intersectionOutput.enteringPoint.y<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().y)) {
				m_firstCellLocals.cur_cell = m_firstCellLocals.lastFirstCellFromOutside;
				return true;
			}
			break;	
		case Intersections.BOX_LEFT:
		case Intersections.BOX_RIGHT:
			if ((m_intersectionOutput.enteringPoint.y>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().y) &&
				(m_intersectionOutput.enteringPoint.y<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().y) &&
				(m_intersectionOutput.enteringPoint.z>=m_firstCellLocals.lastFirstCellFromOutside.getMinValues().z) &&
				(m_intersectionOutput.enteringPoint.z<=m_firstCellLocals.lastFirstCellFromOutside.getMaxValues().z)) {
				m_firstCellLocals.cur_cell = m_firstCellLocals.lastFirstCellFromOutside;
				return true;
			}
			break;
		}
		
		return false;
	}
	
	
	public class FirstCellLocals {
		public int x;
		public int y;
		public int z;
		public int cur_depth;
		public OctreeCell cur_cell;
		
		public int bit_x;
		public int bit_y;
		public int bit_z;
		public int shift;
		public int bit_mask;
		public int child;
		
		public int        lastPart;
		public OctreeCell lastFirstCellFromOutside;
		//public OctreeCell lastFirstCellFromOutside;
	}
	

	public void nextCell(NextCellOutput output) {
		if (!m_hasNextCell) { 
			output.nextCell = null;
			return;
		}
		
		if (m_isFirstCell) {
			m_isFirstCell = false;
			output.nextCell  = m_firstCellLocals.cur_cell; 
			output.leavingT  = m_intersectionOutput.leavingT;
			//m_lastCell = m_firstCellLocals.cur_cell;
		} else {
//			System.out.println("next cell 1");
//			System.out.println("entering point:"+m_intersectionOutput.enteringPoint);
//			System.out.println("leaving point:"+m_intersectionOutput.leavingPoint);
//			System.out.println("cell:"+m_firstCellLocals.cur_cell);
//			System.out.println("min:"+m_firstCellLocals.cur_cell.getMinValues());
//			System.out.println("max:"+m_firstCellLocals.cur_cell.getMaxValues());
			
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.64528644)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.y-0.6121121)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.z- -0.45708218)<0.0001)) {
//					
//				System.out.println("p1:"+m_intersectionOutput.enteringPoint);
//				System.out.println("p2:"+m_intersectionOutput.leavingPoint);
//				System.out.println("cell:"+m_firstCellLocals.cur_cell);
//				System.out.println("min:"+m_firstCellLocals.cur_cell.getMinValues());
//				System.out.println("max:"+m_firstCellLocals.cur_cell.getMaxValues());
//					
//			}
			
			// determine point in next cell
//			m_intersectionLocals.point.set(m_intersectionOutput.leavingPoint);
			m_intersectionLocals.point.set(m_lastLeavingPoint);
//			switch (m_intersectionOutput.leavingPart) {
			switch (m_lastLeavingPart) {
			case Intersections.BOX_FRONT:
				m_intersectionLocals.point.y -= m_lengthY[m_maxDepth+1];
				if (m_intersectionLocals.point.y<m_root.getMinValues().y) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			case Intersections.BOX_BACK:
				m_intersectionLocals.point.y += m_lengthY[m_maxDepth+1];
				if (m_intersectionLocals.point.y>m_root.getMaxValues().y) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			case Intersections.BOX_TOP:
				m_intersectionLocals.point.z += m_lengthZ[m_maxDepth+1];
				if (m_intersectionLocals.point.z>m_root.getMaxValues().z) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			case Intersections.BOX_BOTTOM:
				m_intersectionLocals.point.z -= m_lengthZ[m_maxDepth+1];
				if (m_intersectionLocals.point.z<m_root.getMinValues().z) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			case Intersections.BOX_LEFT:
				//System.out.println("LEFT");
				m_intersectionLocals.point.x -= m_lengthX[m_maxDepth+1];
				if (m_intersectionLocals.point.x<m_root.getMinValues().x) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			case Intersections.BOX_RIGHT:
				m_intersectionLocals.point.x += m_lengthX[m_maxDepth+1];
				if (m_intersectionLocals.point.x>m_root.getMaxValues().x) { 
					m_hasNextCell = false;
					output.nextCell = null;
					return;
				}
				break;
			default:
//				System.out.println("DefaultOctreeCellGenerator: " +
//						"unknown value for intersection part");
				m_hasNextCell = false;
				output.nextCell = null;
				return;
			}
			
//			System.out.println("next cell 2");
//			System.out.println("entering point:"+m_intersectionOutput.enteringPoint);
//			System.out.println("leaving point:"+m_intersectionOutput.leavingPoint);
//			System.out.println("next point:"+m_intersectionLocals.point);
			
			m_firstCellLocals.x = (int)Math.
				floor((m_intersectionLocals.point.x-m_root.getMinValues().x)/
					  (m_lengthX[0])*m_maxDivisionX);
			m_firstCellLocals.y = (int)Math.
				floor((m_intersectionLocals.point.y-m_root.getMinValues().y)/
					  (m_lengthY[0])*m_maxDivisionX);
			m_firstCellLocals.z = (int)Math.
				floor((m_intersectionLocals.point.z-m_root.getMinValues().z)/
					  (m_lengthZ[0])*m_maxDivisionX);
			if (m_firstCellLocals.x>=m_maxDivisionX) { System.err.println("###");m_firstCellLocals.x = m_maxDivisionX-1; }
			if (m_firstCellLocals.y>=m_maxDivisionX) { System.err.println("###");m_firstCellLocals.y = m_maxDivisionX-1; }
			if (m_firstCellLocals.z>=m_maxDivisionX) { System.err.println("###");m_firstCellLocals.z = m_maxDivisionX-1; }
			
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.8032335)<0.01) &&
//				(Math.abs(m_intersectionInput.direction.y-  0.47582844)<0.01) &&
//				(Math.abs(m_intersectionInput.direction.z- -0.3583339)<0.01)) {
//						
//				System.err.println("dir:"+m_intersectionInput.direction);
//				System.err.println("cell:"+m_firstCellLocals.x+","+m_firstCellLocals.y+","+m_firstCellLocals.z);
//					
//			}
			
//			System.out.println("#xyz:"+m_firstCellLocals.x+","+m_firstCellLocals.y+","+m_firstCellLocals.z);
			getCellFromPoint(m_firstCellLocals.x,
					m_firstCellLocals.y,
					m_firstCellLocals.z);
				
//			BoxIntersectionInput input_ = new BoxIntersectionInput();
//			BoxIntersectionLocals locals_ = new BoxIntersectionLocals();
//			BoxIntersectionOutput output_ = new BoxIntersectionOutput();
//			
//			input_.direction.set(m_intersectionInput.direction);
//			input_.origin.set(m_intersectionInput.origin);
//			input_.minValues.set(m_firstCellLocals.cur_cell.getMinValues());
//			input_.maxValues.set(m_firstCellLocals.cur_cell.getMaxValues());
//			
//			
//			getBoxEnteringIntersection(input_,locals_,output_);
//			if (!output_.hasIntersection) {
//				System.err.println("###intersection:"+output_.hasIntersection);
//			}
			
//			System.out.println("outer cell:"+m_firstCellLocals.cur_cell);
			
			if (output.nextCell==m_firstCellLocals.cur_cell) {
				output.errorOccurred = true;
//				System.err.println("same cell:"+m_intersectionOutput.leavingPart);
				//System.err.println("dir:"+m_intersectionInput.direction);
				m_hasNextCell = true;
				output.nextCell = null;
				return;
			}
			
			m_intersectionInput.minValues.set(m_firstCellLocals.cur_cell.getMinValues());
			m_intersectionInput.maxValues.set(m_firstCellLocals.cur_cell.getMaxValues());
			getBoxLeavingIntersection(m_intersectionInput,m_intersectionLocals,m_intersectionOutput);
			
			
			m_lastCell = m_firstCellLocals.cur_cell;
			m_lastLeavingPoint.set(m_intersectionOutput.leavingPoint);
			m_lastLeavingPart = m_intersectionOutput.leavingPart;
			
			output.nextCell  = m_firstCellLocals.cur_cell; 
			output.leavingT  = m_intersectionOutput.leavingT;
			
			
			
			
			
			
//			System.out.println("next cell 4");
		}
		
	}

	
	public static void getBoxEnteringIntersection(BoxIntersectionInput input, 
			BoxIntersectionLocals local, BoxIntersectionOutput output) {

		
//		if ((Math.abs(m_intersectionInput.direction.x- -0.8032335)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.y-  0.47582844)<0.0001) &&
//			(Math.abs(m_intersectionInput.direction.z- -0.3583339)<0.0001)) {
//					
//			System.err.println("getFirstCellFromOutside()");
//				
//		}
		
		output.hasIntersection = false;
		
		// left
		if ((input.direction.x>0.0f) && (input.origin.x<input.minValues.x)) {
			//System.out.println("LEFT");
			output.enteringT = (input.minValues.x-input.origin.x)/input.direction.x;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.y>=input.minValues.y) && (output.enteringPoint.y<=input.maxValues.y) &&
				(output.enteringPoint.z>=input.minValues.z) && (output.enteringPoint.z<=input.maxValues.z)) {
				
				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_LEFT;
				return;
			}
		}
		// front
		if ((input.direction.y>0.0f) && (input.origin.y<input.minValues.y)) {
			//System.out.println("FRONT");
			output.enteringT = (input.minValues.y-input.origin.y)/input.direction.y;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.x>=input.minValues.x) && (output.enteringPoint.x<=input.maxValues.x) &&
				(output.enteringPoint.z>=input.minValues.z) && (output.enteringPoint.z<=input.maxValues.z)) {
				
				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_FRONT;
				return;
			}
		}
		// bottom
		if ((input.direction.z>0.0f) && (input.origin.z<input.minValues.z)) {
			//System.out.println("BOTTOM");
			output.enteringT = (input.minValues.z-input.origin.z)/input.direction.z;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.x>=input.minValues.x) && (output.enteringPoint.x<=input.maxValues.x) &&
				(output.enteringPoint.y>=input.minValues.y) && (output.enteringPoint.y<=input.maxValues.y)) {
				
				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_BOTTOM;
				return;
			}
		}
		// right
		if ((input.direction.x<0.0f) && (input.origin.x>input.maxValues.x)) {
			//System.out.println("RIGHT");
			output.enteringT = (input.maxValues.x-input.origin.x)/input.direction.x;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.y>=input.minValues.y) && (output.enteringPoint.y<=input.maxValues.y) &&
				(output.enteringPoint.z>=input.minValues.z) && (output.enteringPoint.z<=input.maxValues.z)) {
				
				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_RIGHT;
				return;
			}
		}
		// back
		if ((input.direction.y<0.0f) && (input.origin.y>input.maxValues.y)) {
			//System.out.println("BACK");
			output.enteringT = (input.maxValues.y-input.origin.y)/input.direction.y;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.x>=input.minValues.x) && (output.enteringPoint.x<=input.maxValues.x) &&
				(output.enteringPoint.z>=input.minValues.z) && (output.enteringPoint.z<=input.maxValues.z)) {
				
				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_BACK;
				return;
			}
		}
		// top
		if ((input.direction.z<0.0f) && (input.origin.z>input.maxValues.z)) {
			//System.out.println("TOP");
			output.enteringT = (input.maxValues.z-input.origin.z)/input.direction.z;
			output.enteringPoint.scaleAdd(output.enteringT,input.direction,input.origin);
			if ((output.enteringPoint.x>=input.minValues.x) && (output.enteringPoint.x<=input.maxValues.x) &&
				(output.enteringPoint.y>=input.minValues.y) && (output.enteringPoint.y<=input.maxValues.y)) {

				output.hasIntersection = true;
				output.enteringPart = Intersections.BOX_TOP;
				return;
			}
		}

 	}
	
	
	public void getBoxLeavingIntersection(BoxIntersectionInput input, 
			BoxIntersectionLocals local, BoxIntersectionOutput output) {
		
//		BoxIntersectionInput input_ = new BoxIntersectionInput();
//		BoxIntersectionLocals locals_ = new BoxIntersectionLocals();
//		BoxIntersectionOutput output_ = new BoxIntersectionOutput();
//		
//		input_.direction.set(input.direction);
//		input_.origin.set(input.origin);
//		input_.minValues.set(input.minValues);
//		input_.maxValues.set(input.maxValues);
//		getBoxEnteringIntersection(input_,locals_,output_);
////		System.err.println("###intersection:"+output_.hasIntersection);
//		
////		getBoxEnteringIntersection(input_,locals_,output_);
//		if (!output_.hasIntersection) {
//			System.err.println("###intersection:"+output_.hasIntersection);
//		}
		
		if ((input.direction.x>0)) {
			output.leavingT = (input.maxValues.x-input.origin.x)/input.direction.x;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			if ((output.leavingPoint.y>=input.minValues.y) && 
				(output.leavingPoint.y<=input.maxValues.y) &&
				(output.leavingPoint.z>=input.minValues.z) &&
				(output.leavingPoint.z<=input.maxValues.z)) {
				
				output.leavingPart = Intersections.BOX_RIGHT;
				return;
			}
		}
		if ((input.direction.x<0)) {
			output.leavingT = (input.minValues.x-input.origin.x)/input.direction.x;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.6174192)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.y-  0.5474341)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.z- -0.5648978)<0.0001)) {
//						
//				System.err.println("leaving point left:"+output.leavingPoint);
//					
//			}
			
			if ((output.leavingPoint.y>=input.minValues.y) && 
				(output.leavingPoint.y<=input.maxValues.y) &&
				(output.leavingPoint.z>=input.minValues.z) &&
				(output.leavingPoint.z<=input.maxValues.z)) {
				
				output.leavingPart = Intersections.BOX_LEFT;
				return;
			}
		}
		if ((input.direction.y>0)) {
			output.leavingT = (input.maxValues.y-input.origin.y)/input.direction.y;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.6174192)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.y-  0.5474341)<0.0001) &&
//				(Math.abs(m_intersectionInput.direction.z- -0.5648978)<0.0001)) {
//						
//				System.err.println("leaving point back:"+output.leavingPoint);
//					
//			}
			
			if ((output.leavingPoint.x>=input.minValues.x) && 
				(output.leavingPoint.x<=input.maxValues.x) &&
				(output.leavingPoint.z>=input.minValues.z) &&
				(output.leavingPoint.z<=input.maxValues.z)) {
				
				output.leavingPart = Intersections.BOX_BACK;
				return;
			}
		}
		if ((input.direction.y<0)) {
			output.leavingT = (input.minValues.y-input.origin.y)/input.direction.y;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			if ((output.leavingPoint.x>=input.minValues.x) && 
				(output.leavingPoint.x<=input.maxValues.x) &&
				(output.leavingPoint.z>=input.minValues.z) &&
				(output.leavingPoint.z<=input.maxValues.z)) {
				
				output.leavingPart = Intersections.BOX_FRONT;
				return;
			}
		}
		if ((input.direction.z>0)) {
			output.leavingT = (input.maxValues.z-input.origin.z)/input.direction.z;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			if ((output.leavingPoint.x>=input.minValues.x) && 
				(output.leavingPoint.x<=input.maxValues.x) &&
				(output.leavingPoint.y>=input.minValues.y) &&
				(output.leavingPoint.y<=input.maxValues.y)) {
				
				output.leavingPart = Intersections.BOX_TOP;
				return;
			}
		}
		if ((input.direction.z<0)) {
			output.leavingT = (input.minValues.z-input.origin.z)/input.direction.z;
			output.leavingPoint.scaleAdd(output.leavingT,input.direction,input.origin);
			
//			if ((Math.abs(m_intersectionInput.direction.x- -0.6174192)<0.0001) &&
//					(Math.abs(m_intersectionInput.direction.y-  0.5474341)<0.0001) &&
//					(Math.abs(m_intersectionInput.direction.z- -0.5648978)<0.0001)) {
//							
//					System.err.println("leaving point bottom:"+output.leavingPoint);
//						
//				}
			
			if ((output.leavingPoint.x>=input.minValues.x) && 
				(output.leavingPoint.x<=input.maxValues.x) &&
				(output.leavingPoint.y>=input.minValues.y) &&
				(output.leavingPoint.y<=input.maxValues.y)) {
				
				output.leavingPart = Intersections.BOX_BOTTOM;
				return;
			}
		}
		
//		BoxIntersectionInput input_ = new BoxIntersectionInput();
//		BoxIntersectionLocals locals_ = new BoxIntersectionLocals();
//		BoxIntersectionOutput output_ = new BoxIntersectionOutput();
		
//		input_.direction.set(input.direction);
//		input_.origin.set(input.origin);
//		input_.minValues.set(input.minValues);
//		input_.maxValues.set(input.maxValues);
//		getBoxEnteringIntersection(input_,locals_,output_);
//		System.err.println("2_intersection:"+output_.hasIntersection);
//		
		output.leavingPart = Intersections.UNDEFINED_PART;
		
//		System.err.println("ray"+input.origin+","+input.direction);
//		System.err.println("this should never be..."+"min:"+input.minValues+"max:"+input.maxValues);
//		Thread.dumpStack();
		//System.exit(1);
		
//		System.err.println("ray"+input.origin+","+input.direction);
//		System.err.println("test:"+test);
//		System.err.println("this should never be..."+output.leavingPoint+"min:"+input.minValues+"max:"+input.maxValues);
 	}
	
	
	public class BoxIntersectionInput {
		public final Point3d  origin    = new Point3d();
		public final Vector3d direction = new Vector3d();
		public final Vector3f minValues = new Vector3f();
		public final Vector3f maxValues = new Vector3f();
	}
	
	
	public class BoxIntersectionOutput {
		
//		public final static int FRONT  = 0;
//		public final static int BACK   = 1;
//		public final static int TOP    = 2;
//		public final static int BOTTOM = 3;
//		public final static int RIGHT  = 4;
//		public final static int LEFT   = 5;
		
		public boolean       hasIntersection;		
		public double        enteringT;
		public final Point3d enteringPoint = new Point3d();
		public int           enteringPart;
		public double        leavingT;
		public final Point3d leavingPoint = new Point3d();
		public int           leavingPart;
	}
	
	
	public class BoxIntersectionLocals {
		public final Point3f point = new Point3f();
	}
	

}
