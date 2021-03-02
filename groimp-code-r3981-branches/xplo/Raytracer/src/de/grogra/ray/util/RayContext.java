package de.grogra.ray.util;

import java.util.ArrayList;
import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.ray.RTObject;
import de.grogra.ray.shader.RTMedium;


/**
 * This class is needed for a correct intersection computation. Because the 
 * lack of precision in floating point operations there must be remembered some
 * context information. 
 * Additionally it is used to optimize the intersection processing by knowing 
 * the former computation context.
 * 
 * @author Micha
 *
 */
public class RayContext {

	public final Point3d lastIntersectionPoint = new Point3d();
	public RTObject      excludeObject         = null;
//	public float         iorRatio              = 1.0f;
	
	private final ArrayList m_enteringStack = new ArrayList(2);
	
	private final Vector m_materialStack = new Vector();

	
//	public void setLastIntersectionPoint(double x,double y,double z) {
//		m_lastIntersectionPoint.set(x,y,z);
//	}
//	public Point3d getLastIntersectionPoint() { return m_lastIntersectionPoint; }
//	public void setExcludeObject(RTObject object) { m_excludeObject = object; }
//	public RTObject getExcludeObject() { return m_excludeObject; }
	
	
	public void initializeContext() {
		lastIntersectionPoint.set(0,0,0);
		excludeObject = null;
		m_materialStack.clear();
	}
	
	
	public void pushMaterial(RTObject object) {
//		System.err.println("push:"+object);
		m_materialStack.add(object);
	}
	
	
	public void popMaterial() {
		if (m_materialStack.size()==0) { return; }
		
		m_materialStack.remove(m_materialStack.size()-1);
	}
	
	
	public boolean isLastMaterial(RTObject object) {
		if (m_materialStack.size()==0) { return false; }
		
		return (m_materialStack.elementAt(m_materialStack.size()-1)==object);
	}
	
	
	public int deleteMaterial(RTObject object) {
		for (int i=m_materialStack.size()-1;i>=0;i--) {
			if (m_materialStack.elementAt(i)==object) {
				m_materialStack.remove(i);
				return i;
			}
		}
		return -1;
	}
	
	
	public void insertMaterial(RTObject object,int index) {
		m_materialStack.add(index,object);
	}
	
	
	public void printMaterials() {
		System.out.println("  materials stack ("+m_materialStack.size()+")");
		for (int i=0;i<m_materialStack.size();i++) {
			System.out.println("    material:"+m_materialStack.get(i));
		}
	}
	
	
	public void enteringObject(RTObject object,RTMedium medium) {
		EnteringStackItem item = new EnteringStackItem(object,medium);
		m_enteringStack.add(item);
	}
	
	
	public void exitingObject(RTObject object) {
		if (m_enteringStack.size()==0) { return; }
		if (m_enteringStack.size()==1) {
			m_enteringStack.clear();
			return;
		}
		int index = -1;
		for (int i=0;i<m_enteringStack.size();i++) {
			if (((EnteringStackItem)m_enteringStack.get(i)).object==object) {
				index = i;
				break;
			}
		}
		if (index>-1) {
			m_enteringStack.remove(index);
		}
	}
	
	
	public float getEnteringIORRation() {
		if (m_enteringStack.size()==0) {
			System.err.println("ERROR in RayContent");
			return 1.0f; 
		} else
		if (m_enteringStack.size()==1) {
			return 
				1.0f/((EnteringStackItem)m_enteringStack.get(0)).ior;
		} else {
			return 
			((EnteringStackItem)m_enteringStack.get(m_enteringStack.size()-2)).ior/
			((EnteringStackItem)m_enteringStack.get(m_enteringStack.size()-1)).ior;
		}
	}
	
	
//	public float getExitingIORRation() {
//		if (m_enteringStack.size()==0) {
//			System.err.println("ERROR in RayContent");
//			return 1.0f; 
//		} else
//		if (m_enteringStack.size()==1) {
//			return ((EnteringStackItem)m_enteringStack.get(0)).ior;
//		} else {
//			return 
//			((EnteringStackItem)m_enteringStack.get(m_enteringStack.size()-1)).ior/
//			((EnteringStackItem)m_enteringStack.get(m_enteringStack.size()-2)).ior;
//		}
//	}
	
	
	public float getExitingIORRation() {
		if (m_materialStack.size()==0) {
			System.err.println("ERROR in RayContent");
			return 1.0f; 
		} else
		if (m_materialStack.size()==1) {
			if (((RTObject)m_materialStack.get(0)).getMedium()==null) {
				return 1.0f;
			} else {
				return ((RTObject)m_materialStack.get(0)).getMedium().getIndexOfRefraction();
			}
		} else {
			return 
			((RTObject)m_materialStack.get(m_materialStack.size()-1)).getMedium().getIndexOfRefraction()/
			((RTObject)m_materialStack.get(m_materialStack.size()-2)).getMedium().getIndexOfRefraction();
		}
	}
	
	
//	public float getCurrentIOR() {
//		if (m_enteringStack.size()==0) { 
//			return 1.0f;
//		} else {
//			return ((EnteringStackItem)m_enteringStack.
//						get(m_enteringStack.size()-1)).ior;
//		}
//	}
	
	
	public float getCurrentIOR() {
		if (m_materialStack.size()==0) { return 1.0f; }
		if (((RTObject)m_materialStack.get(m_materialStack.size()-1)).getMedium()==null) {
			return 1.0f;
		}
		return ((RTObject)m_materialStack.
			get(m_materialStack.size()-1)).getMedium().getIndexOfRefraction();
	}
	
	
	private class EnteringStackItem {
		public RTObject object;
		public float    ior = RTMedium.REFRACTION_INDEX_DEFAULT;
		
		public EnteringStackItem(RTObject object,RTMedium medium) {
			this.object = object;
			this.ior    = medium.getIndexOfRefraction();
		}
	}
	
	
}
