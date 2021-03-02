package de.grogra.ray.intersection;

import java.util.ArrayList;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;

public class OctreeCell {

	private final Vector3f m_minValues = new Vector3f();
	private final Vector3f m_maxValues = new Vector3f();
	
	private int             m_depth = 0;
	private OctreeCell[]    m_children = null;
	private OctreeCell[]    m_neighbours;
	private final ArrayList m_linkedObjects = new ArrayList();

	// TODO delete this
	private final Intersections.BoxIntersectionInput m_boxInput = 
		new Intersections.BoxIntersectionInput();
	private final Intersections.BoxIntersectionLocalVariables m_boxLocalVariables =
		new Intersections.BoxIntersectionLocalVariables();
	
	public int testInt = 0;
	
	
	public OctreeCell(int depth) {
		m_depth = depth;
		m_neighbours = new OctreeCell[6];
		for (int i=0;i<6;i++) { m_neighbours[i] = null; }
	}
	
	
	public OctreeCell(int depth, Vector3f minValues, Vector3f maxValues) {
		this(depth);
		setExtension(minValues,maxValues);
	}
	
	
	public int getDepth() { return m_depth; }
	public ArrayList getLinkedObjects() { return m_linkedObjects; }
	public boolean hasChildren() { return (m_children!=null); }
	public Vector3f getMinValues() { return m_minValues; }
	public Vector3f getMaxValues() { return m_maxValues; }
	
	public OctreeCell getNeighbourFront() { 
		return m_neighbours[Intersections.BOX_FRONT-Intersections.BOX_FRONT]; 
	}
	public OctreeCell getNeighbourBack() { 
		return m_neighbours[Intersections.BOX_BACK-Intersections.BOX_FRONT]; 
	}
	public OctreeCell getNeighbourTop() { 
		return m_neighbours[Intersections.BOX_TOP-Intersections.BOX_FRONT]; 
	}
	public OctreeCell getNeighbourBottom() { 
		return m_neighbours[Intersections.BOX_BOTTOM-Intersections.BOX_FRONT]; 
	}
	public OctreeCell getNeighbourLeft() { 
		return m_neighbours[Intersections.BOX_LEFT-Intersections.BOX_FRONT]; 
	}
	public OctreeCell getNeighbourRight() { 
		return m_neighbours[Intersections.BOX_RIGHT-Intersections.BOX_FRONT]; 
	}
	
	
	public void setNeighbours(OctreeCell front, OctreeCell back, 
			OctreeCell top, OctreeCell bottom, 
			OctreeCell left, OctreeCell right) {
		m_neighbours[Intersections.BOX_FRONT-Intersections.BOX_FRONT]  = front;
		m_neighbours[Intersections.BOX_BACK-Intersections.BOX_FRONT]   = back;
		m_neighbours[Intersections.BOX_TOP-Intersections.BOX_FRONT]    = top;
		m_neighbours[Intersections.BOX_BOTTOM-Intersections.BOX_FRONT] = bottom;
		m_neighbours[Intersections.BOX_LEFT-Intersections.BOX_FRONT]   = left;
		m_neighbours[Intersections.BOX_RIGHT-Intersections.BOX_FRONT]  = right;
	}
	
	
	public OctreeCell getChild(int index) {
		if (!this.hasChildren() || (index<0) || (index>7)) { return null; }
		return m_children[index];
	}
	
	
	public void setExtension(Vector3f minValues, Vector3f maxValues) {
		m_minValues.set(minValues);
		m_maxValues.set(maxValues);
		m_boxInput.minValues.set(minValues);
		m_boxInput.maxValues.set(maxValues);
	}
	
	
	public boolean hasIntersection(Ray ray) {
		m_boxInput.ray.setRay(ray);
		return Intersections.getBox_hasIntersection(
				m_boxInput,m_boxLocalVariables);
	}
	
	
	public void divideNode() {
		m_children = new OctreeCell[8];
		float dx = m_maxValues.x-m_minValues.x;
		float dy = m_maxValues.y-m_minValues.y;
		float dz = m_maxValues.z-m_minValues.z;
		Vector3f new_min = new Vector3f();
		Vector3f new_max = new Vector3f();
		
		// child 0 (x:0,y:0,z:0)
		new_min.x = m_minValues.x;
		new_min.y =	m_minValues.y;
		new_min.z = m_minValues.z;
		new_max.x = m_minValues.x+dx/2.0f;
		new_max.y =	m_minValues.y+dy/2.0f;
		new_max.z = m_minValues.z+dz/2.0f;		
		m_children[0] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 1 (x:0,y:0,z:1)
		new_min.x = m_minValues.x;
		new_min.y =	m_minValues.y;
		new_min.z = m_minValues.z+dz/2.0f;
		new_max.x = m_minValues.x+dx/2.0f;
		new_max.y =	m_minValues.y+dy/2.0f;
		new_max.z = m_minValues.z+dz;		
		m_children[1] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 2 (x:0,y:1,z:0)
		new_min.x = m_minValues.x;
		new_min.y =	m_minValues.y+dy/2.0f;
		new_min.z = m_minValues.z;
		new_max.x = m_minValues.x+dx/2.0f;
		new_max.y =	m_minValues.y+dy;
		new_max.z = m_minValues.z+dz/2.0f;		
		m_children[2] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 3 (x:0,y:1,z:1)
		new_min.x = m_minValues.x;
		new_min.y =	m_minValues.y+dy/2.0f;
		new_min.z = m_minValues.z+dz/2.0f;
		new_max.x = m_minValues.x+dx/2.0f;
		new_max.y =	m_minValues.y+dy;
		new_max.z = m_minValues.z+dz;		
		m_children[3] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 4 (x:1,y:0,z:0)
		new_min.x = m_minValues.x+dx/2.0f;
		new_min.y =	m_minValues.y;
		new_min.z = m_minValues.z;
		new_max.x = m_minValues.x+dx;
		new_max.y =	m_minValues.y+dy/2.0f;
		new_max.z = m_minValues.z+dz/2.0f;
		m_children[4] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 5 (x:1,y:0,z:1)
		new_min.x = m_minValues.x+dx/2.0f;
		new_min.y =	m_minValues.y;
		new_min.z = m_minValues.z+dz/2.0f;
		new_max.x = m_minValues.x+dx;
		new_max.y =	m_minValues.y+dy/2.0f;
		new_max.z = m_minValues.z+dz;
		m_children[5] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 6 (x:1,y:1,z:0)
		new_min.x = m_minValues.x+dx/2.0f;
		new_min.y =	m_minValues.y+dy/2.0f;
		new_min.z = m_minValues.z;
		new_max.x = m_minValues.x+dx;
		new_max.y =	m_minValues.y+dy;
		new_max.z = m_minValues.z+dz/2.0f;
		m_children[6] = new OctreeCell(getDepth()+1,new_min,new_max);
		// child 7 (x:1,y:1,z:1)
		new_min.x = m_minValues.x+dx/2.0f;
		new_min.y =	m_minValues.y+dy/2.0f;
		new_min.z = m_minValues.z+dz/2.0f;
		new_max.x = m_minValues.x+dx;
		new_max.y =	m_minValues.y+dy;
		new_max.z = m_minValues.z+dz;
		m_children[7] = new OctreeCell(getDepth()+1,new_min,new_max);
	}

}