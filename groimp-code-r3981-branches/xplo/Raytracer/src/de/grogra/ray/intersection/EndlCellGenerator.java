package de.grogra.ray.intersection;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.intersection.DefaultCellGenerator.BoxIntersectionInput;
import de.grogra.ray.intersection.DefaultCellGenerator.BoxIntersectionLocals;
import de.grogra.ray.intersection.DefaultCellGenerator.BoxIntersectionOutput;
import de.grogra.ray.util.Ray;


public class EndlCellGenerator implements CellGenerator {

	private OctreeCell m_root;
	private int        m_maxDepth;
	private int        m_maxAxisDivisions;
	
	private float m_cellLengthX[];
	private float m_cellLengthY[];
	private float m_cellLengthZ[];
	
	private boolean m_isFirstCell = false;
	
	private BoxIntersectionInput m_intersectionInput =
		new BoxIntersectionInput();
	private BoxIntersectionLocals m_intersectionLocals =
		new BoxIntersectionLocals();
	private BoxIntersectionOutput m_intersectionOutput =
		new BoxIntersectionOutput();
	
	private OctreeCell m_nextToLastCell;
	private LastCell m_lastCell = new LastCell();
	
	private DeterminingCellLocals m_determiningCellLocals =
		new DeterminingCellLocals();
	
	private Point3d m_tmp_point = new Point3d();
	
	private boolean m_test = true;
	
	
	public void initialize(OctreeCell sceneNode, int maxDepth) {
		m_root = sceneNode;
		m_maxDepth = maxDepth;
		
		m_cellLengthX = new float[maxDepth+2];
		m_cellLengthY = new float[maxDepth+2];
		m_cellLengthZ = new float[maxDepth+2];
		float cur_depth_x = m_root.getMaxValues().x-m_root.getMinValues().x;
		float cur_depth_y = m_root.getMaxValues().y-m_root.getMinValues().y;
		float cur_depth_z = m_root.getMaxValues().z-m_root.getMinValues().z;
		for (int i=0;i<(maxDepth+2);i++) {
			m_cellLengthX[i] = cur_depth_x;
			m_cellLengthY[i] = cur_depth_y;
			m_cellLengthZ[i] = cur_depth_z;
			cur_depth_x /= 2.0f;
			cur_depth_y /= 2.0f;
			cur_depth_z /= 2.0f;
		}
		m_maxAxisDivisions = 1 << maxDepth;
	}

	
	public void setRay(Ray ray) {
		m_intersectionInput.origin.set(ray.getOrigin());
		m_intersectionInput.direction.set(ray.getDirection());
		m_isFirstCell = true;
		m_nextToLastCell = null;
		m_lastCell.cell = null;
		m_lastCell.leavingPart = -1;
	}
	

	public void nextCell(NextCellOutput output) {
		
		//System.err.println("cell: "+m_lastCell.cell+"part: "+m_lastCell.leavingPart);
		
		output.errorOccurred = false;
		
		if (m_isFirstCell) {
			getStartingCell(output);
			m_isFirstCell = false;
		} else {
			if (m_lastCell.cell==null) {
				return;
			}
			
			switch (m_lastCell.leavingPart) {
			case Intersections.BOX_FRONT:
				if (m_lastCell.cell.getNeighbourFront()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourFront().hasChildren()) {
					//System.err.println("not front has children");
					output.nextCell = m_lastCell.cell.getNeighbourFront();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.y -= m_cellLengthY[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			case Intersections.BOX_BACK:
				if (m_lastCell.cell.getNeighbourBack()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourBack().hasChildren()) {
					output.nextCell = m_lastCell.cell.getNeighbourBack();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.y += m_cellLengthY[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			case Intersections.BOX_TOP:
				if (m_lastCell.cell.getNeighbourTop()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourTop().hasChildren()) {
					output.nextCell = m_lastCell.cell.getNeighbourTop();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.z += m_cellLengthZ[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			case Intersections.BOX_BOTTOM:
				if (m_lastCell.cell.getNeighbourBottom()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourBottom().hasChildren()) {
					output.nextCell = m_lastCell.cell.getNeighbourBottom();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.z -= m_cellLengthZ[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			case Intersections.BOX_LEFT:
				if (m_lastCell.cell.getNeighbourLeft()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourLeft().hasChildren()) {
					output.nextCell = m_lastCell.cell.getNeighbourLeft();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.x -= m_cellLengthX[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			case Intersections.BOX_RIGHT:
				if (m_lastCell.cell.getNeighbourRight()==null) {
					output.nextCell = null;
					return;
				}
				if (!m_lastCell.cell.getNeighbourRight().hasChildren()) {
					output.nextCell = m_lastCell.cell.getNeighbourRight();
				} else {
					m_tmp_point.set(m_lastCell.leavingPoint);
					m_tmp_point.x += m_cellLengthX[m_maxDepth+1];
					output.nextCell = getCellFromPoint(m_tmp_point);
				}
				getLeavingInformation(output);
				break;
			default:
				output.nextCell = null;
				m_lastCell.cell = null;
				return;
			}
			
			if (output.nextCell==m_lastCell.cell) {
				//System.err.println("cell:"+m_lastCell.cell);
				output.errorOccurred = true;
				output.nextCell = null;
				return;
			}
			
			if ((output.nextCell!=null) && (output.nextCell==m_nextToLastCell)) {
				//System.err.println("###cell:"+m_lastCell.cell);
				output.errorOccurred = true;
				output.nextCell = null;
				return;
			}
			
			m_nextToLastCell = m_lastCell.cell;
			
		}
		
		
		
		m_lastCell.cell = output.nextCell;
	}
	
	
	private void getStartingCell(NextCellOutput output) {
		
		if (Intersections.isPointInsideBox(m_intersectionInput.origin,
				m_root.getMinValues(),m_root.getMaxValues())) {
			
			// ray starts inside the scene box
			output.nextCell = getCellFromPoint(m_intersectionInput.origin);
			//System.err.println("cell: "+m_lastCell.cell);
						
		} else {
			
			// ray starts outside of the scene box
			m_intersectionInput.minValues.set(m_root.getMinValues());
			m_intersectionInput.maxValues.set(m_root.getMaxValues());
			getBoxEnteringIntersection(m_intersectionInput,
					m_intersectionLocals,m_intersectionOutput);
			if (!m_intersectionOutput.hasIntersection) {
				// ray misses scene box ...
				output.nextCell = null;
				return;
			} else {
				output.nextCell = getBorderCellFromPoint(
						m_intersectionOutput.enteringPoint,
						m_intersectionOutput.enteringPart);
			}
			
		}
		
		// determine leaving point		
		getLeavingInformation(output);
		
	}
	
	
	private void getLeavingInformation(NextCellOutput output) {
		
		if (output.nextCell==null) {
			output.errorOccurred = true;
			output.nextCell = null;
			return;
		}
		
		m_intersectionInput.minValues.set(output.nextCell.getMinValues());
		m_intersectionInput.maxValues.set(output.nextCell.getMaxValues());
		getBoxLeavingIntersection(m_intersectionInput,
				m_intersectionLocals,m_intersectionOutput);
		
		if (m_intersectionOutput.errorOccurred) {
			output.errorOccurred = true;
			output.nextCell = null;
			return;
		}
		
		output.leavingT = m_intersectionOutput.leavingT;
		
		m_lastCell.leavingPart = m_intersectionOutput.leavingPart;
		m_lastCell.leavingPoint.set(m_intersectionOutput.leavingPoint);
	}
	
	
	private OctreeCell getCellFromPoint(Point3d point) {
		
		m_determiningCellLocals.index_x = (int)Math.
			floor((point.x-m_root.getMinValues().x)/
				  (m_cellLengthX[0])*m_maxAxisDivisions);
		m_determiningCellLocals.index_y = (int)Math.
			floor((point.y-m_root.getMinValues().y)/
				  (m_cellLengthY[0])*m_maxAxisDivisions);
		m_determiningCellLocals.index_z = (int)Math.
			floor((point.z-m_root.getMinValues().z)/
				  (m_cellLengthZ[0])*m_maxAxisDivisions);
		if (m_determiningCellLocals.index_x>=m_maxAxisDivisions) { 
			System.err.println("x index out of bounds ..."); 
//			if (m_test==true) {
//				System.err.println("x index out of bounds ...");
//				System.err.println("point:"+point);
//				System.err.println("m_min:"+m_root.getMinValues());
//				System.err.println("m_max:"+m_root.getMaxValues());
//				m_test = false;
//			}
			m_determiningCellLocals.index_x = m_maxAxisDivisions-1; 
		}
		if (m_determiningCellLocals.index_y>=m_maxAxisDivisions) { 
			System.err.println("y index out of bounds ..."); 
			m_determiningCellLocals.index_y = m_maxAxisDivisions-1; 
		}
		if (m_determiningCellLocals.index_z>=m_maxAxisDivisions) { 
			System.err.println("z index out of bounds ..."); 
			m_determiningCellLocals.index_z = m_maxAxisDivisions-1; 
		}
		
		determineCell(m_determiningCellLocals);
		
		return m_determiningCellLocals.cell;
	}
	
	
	private OctreeCell getBorderCellFromPoint(Point3d point, int part) {
		
		switch (part) {
		case Intersections.BOX_FRONT:
			m_determiningCellLocals.index_y = 0;
			m_determiningCellLocals.index_x = (int)Math.
				floor((point.x-m_root.getMinValues().x)/
					  (m_cellLengthX[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_z = (int)Math.
				floor((point.z-m_root.getMinValues().z)/
					  (m_cellLengthZ[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_x>=m_maxAxisDivisions) { 
//				System.err.println("index out of bounds ..."); 
				m_determiningCellLocals.index_x = m_maxAxisDivisions-1; 
			}
			if (m_determiningCellLocals.index_z>=m_maxAxisDivisions) {
//				System.err.println("index out of bounds ..."); 
				m_determiningCellLocals.index_z = m_maxAxisDivisions-1; 
			}
			break;
		case Intersections.BOX_BACK:
			m_determiningCellLocals.index_y = m_maxAxisDivisions-1;
			m_determiningCellLocals.index_x = (int)Math.
				floor((point.x-m_root.getMinValues().x)/
					  (m_cellLengthX[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_z = (int)Math.
				floor((point.z-m_root.getMinValues().z)/
					  (m_cellLengthZ[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_x>=m_maxAxisDivisions) { m_determiningCellLocals.index_x = m_maxAxisDivisions-1; }
			if (m_determiningCellLocals.index_z>=m_maxAxisDivisions) { m_determiningCellLocals.index_z = m_maxAxisDivisions-1; }
			break;
		case Intersections.BOX_TOP:
			m_determiningCellLocals.index_z = m_maxAxisDivisions-1;
			m_determiningCellLocals.index_x = (int)Math.
				floor((point.x-m_root.getMinValues().x)/
					  (m_cellLengthX[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_y = (int)Math.
				floor((point.y-m_root.getMinValues().y)/
					  (m_cellLengthY[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_x>=m_maxAxisDivisions) { m_determiningCellLocals.index_x = m_maxAxisDivisions-1; }
			if (m_determiningCellLocals.index_y>=m_maxAxisDivisions) { m_determiningCellLocals.index_y = m_maxAxisDivisions-1; }
			break;
		case Intersections.BOX_BOTTOM:
			m_determiningCellLocals.index_z = 0;
			m_determiningCellLocals.index_x = (int)Math.
				floor((point.x-m_root.getMinValues().x)/
					  (m_cellLengthX[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_y = (int)Math.
				floor((point.y-m_root.getMinValues().y)/
					  (m_cellLengthY[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_x>=m_maxAxisDivisions) { m_determiningCellLocals.index_x = m_maxAxisDivisions-1; }
			if (m_determiningCellLocals.index_y>=m_maxAxisDivisions) { m_determiningCellLocals.index_y = m_maxAxisDivisions-1; }
			break;
		case Intersections.BOX_LEFT:
			m_determiningCellLocals.index_x = 0;
			m_determiningCellLocals.index_y = (int)Math.
				floor((point.y-m_root.getMinValues().y)/
					  (m_cellLengthY[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_z = (int)Math.
				floor((point.z-m_root.getMinValues().z)/
					  (m_cellLengthZ[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_y>=m_maxAxisDivisions) { m_determiningCellLocals.index_y = m_maxAxisDivisions-1; }
			if (m_determiningCellLocals.index_z>=m_maxAxisDivisions) { m_determiningCellLocals.index_z = m_maxAxisDivisions-1; }
			break;
		case Intersections.BOX_RIGHT:
			m_determiningCellLocals.index_x = m_maxAxisDivisions-1;
			m_determiningCellLocals.index_y = (int)Math.
				floor((point.y-m_root.getMinValues().y)/
					  (m_cellLengthY[0])*m_maxAxisDivisions);
			m_determiningCellLocals.index_z = (int)Math.
				floor((point.z-m_root.getMinValues().z)/
					  (m_cellLengthZ[0])*m_maxAxisDivisions);
			if (m_determiningCellLocals.index_y>=m_maxAxisDivisions) { m_determiningCellLocals.index_y = m_maxAxisDivisions-1; }
			if (m_determiningCellLocals.index_z>=m_maxAxisDivisions) { m_determiningCellLocals.index_z = m_maxAxisDivisions-1; }
			break;
		}
		
		determineCell(m_determiningCellLocals);
		
		return m_determiningCellLocals.cell;
	}
	
	
	private void determineCell(DeterminingCellLocals locals) {
		locals.cell = m_root;
		for (int i=0;i<m_maxDepth;i++) {
			if (locals.cell.hasChildren()) {
				locals.shift = m_maxDepth-1-i;
				locals.bit_mask = (1 << locals.shift);
				locals.bit_x = 
					(locals.index_x & locals.bit_mask) >>	locals.shift;
				locals.bit_y = 
					(locals.index_y & locals.bit_mask) >> locals.shift;	
				locals.bit_z = 
					(locals.index_z & locals.bit_mask) >> locals.shift;
				locals.child_index = 
					(locals.bit_x << 2) +
					(locals.bit_y << 1) +
					(locals.bit_z);
				locals.cell = locals.cell.getChild(locals.child_index);
			} else {
				return;
			}
		}
	}
	
	
	public static void getBoxEnteringIntersection(BoxIntersectionInput input, 
			BoxIntersectionLocals local, BoxIntersectionOutput output) {
		
		output.hasIntersection = false;
		
		// left
		if ((input.direction.x>0.0f) && (input.origin.x<input.minValues.x)) {
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
		
		output.errorOccurred = false;
		
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
			if ((output.leavingPoint.x>=input.minValues.x) && 
				(output.leavingPoint.x<=input.maxValues.x) &&
				(output.leavingPoint.y>=input.minValues.y) &&
				(output.leavingPoint.y<=input.maxValues.y)) {
				
				output.leavingPart = Intersections.BOX_BOTTOM;
				return;
			}
		}
		
		output.leavingPart = Intersections.UNDEFINED_PART;
		output.errorOccurred = true;
		//System.err.println("error");
	}
	
	
	public class BoxIntersectionInput {
		public final Point3d  origin    = new Point3d();
		public final Vector3d direction = new Vector3d();
		public final Vector3f minValues = new Vector3f();
		public final Vector3f maxValues = new Vector3f();
	}
	
	
	public class BoxIntersectionOutput {
		public boolean       hasIntersection;		
		public double        enteringT;
		public final Point3d enteringPoint = new Point3d();
		public int           enteringPart;
		public double        leavingT;
		public final Point3d leavingPoint = new Point3d();
		public int           leavingPart;
		public boolean       errorOccurred = false;
	}
	
	
	public class BoxIntersectionLocals {
	}
	
	
	public class LastCell {
		public OctreeCell    cell;
		public final Point3d leavingPoint = new Point3d();
		public int           leavingPart;
	}
	
	
	public class DeterminingCellLocals {
		public int index_x; // in
		public int index_y; // in
		public int index_z; // in
		public int bit_x; // local
		public int bit_y; // local
		public int bit_z; // local
		public int shift; // local
		public int bit_mask; // local
		public int child_index; // local
		public OctreeCell cell; // out
	}


}
