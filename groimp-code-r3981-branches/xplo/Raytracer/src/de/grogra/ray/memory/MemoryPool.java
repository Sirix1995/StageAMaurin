package de.grogra.ray.memory;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import de.grogra.ray.util.Ray;


public class MemoryPool {

	private static MemoryPool m_instance = null;

	private int m_newCapacity;
	
	private int   m_capacityRay     = 100;
	private int   m_countRay        = 0;	
	private Ray[] m_instancesRay    = new Ray[m_capacityRay];
	private Ray[] m_newInstancesRay = null;
	
	private int   m_capacityVector3f          = 100;
	private int   m_countVector3f             = 0;	
	private Vector3f[] m_instancesVector3f    = new Vector3f[m_capacityVector3f];
	private Vector3f[] m_newInstancesVector3f = null;
	
	private int   m_capacityColor3f         = 100;
	private int   m_countColor3f            = 0;	
	private Color3f[] m_instancesColor3f    = new Color3f[m_capacityColor3f];
	private Color3f[] m_newInstancesColor3f = null;
	
	private int   m_capacityColor4f         = 100;
	private int   m_countColor4f            = 0;	
	private Color4f[] m_instancesColor4f    = new Color4f[m_capacityColor4f];
	private Color4f[] m_newInstancesColor4f = null;
	
	
	private final static int COLOR4F_MAX = 50;
	private Color4f[] m_instancesColor4f_2 = new Color4f[COLOR4F_MAX];
	private int       m_countColor4f_2     = 0;
	
	private MemoryPool() {
		
	}
	
	
	public static MemoryPool getPool() {
		if (m_instance == null) { m_instance = new MemoryPool(); }
		return m_instance;
	}

	
	public Ray newRay() {
		
		if (m_countRay>=m_capacityRay) {
			m_newCapacity = (m_capacityRay+1)*2;
			System.out.println("ENLARGE");
			if (m_instancesRay==null) {
				m_instancesRay = new Ray[m_newCapacity];
			} else {
				m_newInstancesRay = new Ray[m_newCapacity];
				System.arraycopy(m_instancesRay,0,m_newInstancesRay,0,m_capacityRay);
				m_instancesRay = m_newInstancesRay;
				m_newInstancesRay = null;
			}
			m_capacityRay = m_newCapacity;
		}
		
		m_countRay++;
		if (m_instancesRay[m_countRay-1]==null) {
			m_instancesRay[m_countRay-1] = new Ray();
		}
		return m_instancesRay[m_countRay-1];
	}
	
	
	public void freeRay(Ray obj) {
		if (m_instancesRay[m_countRay-1]==obj) {
			m_countRay--;
		} else {
			System.err.println("ERROR: MemoryPool has to be used like a stack (error in freeRay)");
		}
	}
	
	
	public Vector3f newVector3f() {
		
		if (m_countVector3f>=m_capacityVector3f) {
			m_newCapacity = (m_capacityVector3f+1)*2;
			System.out.println("ENLARGE");
			if (m_instancesVector3f==null) {
				m_instancesVector3f = new Vector3f[m_newCapacity];
			} else {
				m_newInstancesVector3f = new Vector3f[m_newCapacity];
				System.arraycopy(m_instancesVector3f,0,m_newInstancesVector3f,0,m_capacityVector3f);
				m_instancesVector3f = m_newInstancesVector3f;
				m_newInstancesVector3f = null;
			}
			m_capacityVector3f = m_newCapacity;
		}
		
		m_countVector3f++;
		if (m_instancesVector3f[m_countVector3f-1]==null) {
			m_instancesVector3f[m_countVector3f-1] = new Vector3f();
		}
		return m_instancesVector3f[m_countVector3f-1];
	}
	
	
	public void freeVector3f(Vector3f obj) {
		if (m_instancesVector3f[m_countVector3f-1]==obj) {
			m_countVector3f--;
		} else {
			System.err.println("ERROR: MemoryPool has to be used like a stack (error in freeVector3f)");
		}
	}
	
	
	public Color3f newColor3f() {
		
		if (m_countColor3f>=m_capacityColor3f) {
			m_newCapacity = (m_capacityColor3f+1)*2;
			System.out.println("ENLARGE");
			if (m_instancesColor3f==null) {
				m_instancesColor3f = new Color3f[m_newCapacity];
			} else {
				m_newInstancesColor3f = new Color3f[m_newCapacity];
				System.arraycopy(m_instancesColor3f,0,m_newInstancesColor3f,0,m_capacityColor3f);
				m_instancesColor3f = m_newInstancesColor3f;
				m_newInstancesColor3f = null;
			}
			m_capacityColor3f = m_newCapacity;
		}
		
		m_countColor3f++;
		if (m_instancesColor3f[m_countColor3f-1]==null) {
			m_instancesColor3f[m_countColor3f-1] = new Color3f();
		}
		return m_instancesColor3f[m_countColor3f-1];
	}
	
	
	public void freeColor3f(Color3f obj) {
		if (m_instancesColor3f[m_countColor3f-1]==obj) {
			m_countColor3f--;
		} else {
			System.err.println("ERROR: MemoryPool has to be used like a stack (error in freeColor3f)");
		}
	}
	
	
	public Color4f newColor4f() {
		
		if (m_countColor4f>=m_capacityColor4f) {
			m_newCapacity = (m_capacityColor4f+1)*2;
			System.out.println("ENLARGE");
			if (m_instancesColor4f==null) {
				m_instancesColor4f = new Color4f[m_newCapacity];
			} else {
				m_newInstancesColor4f = new Color4f[m_newCapacity];
				System.arraycopy(m_instancesColor4f,0,m_newInstancesColor4f,0,m_capacityColor4f);
				m_instancesColor4f = m_newInstancesColor4f;
				m_newInstancesColor4f = null;
			}
			m_capacityColor4f = m_newCapacity;
		}
		
		m_countColor4f++;
		if (m_instancesColor4f[m_countColor4f-1]==null) {
			m_instancesColor4f[m_countColor4f-1] = new Color4f();
		}
		return m_instancesColor4f[m_countColor4f-1];
	}
	
	
	public void freeColor4f(Color4f obj) {
		if (m_instancesColor4f[m_countColor4f-1]==obj) {
			m_countColor4f--;
		} else {
			System.err.println("ERROR: MemoryPool has to be used like a stack (error in freeColor4f)");
		}
	}
	
	
//	public Color4f newColor4f_2() {
//		Color4f res;
//		if (m_countColor4f_2>0) {
//			res = m_instancesColor4f_2[m_countColor4f_2-1];
//			m_instancesColor4f_2[m_countColor4f_2-1] = null;
//			m_countColor4f_2--;
//		} else {
//			res = new Color4f();
//		}
//		return res;
//	}
//	
//	
//	public void freeColor4f_2(Color4f obj) {
//		if (m_countColor4f_2<COLOR4F_MAX) {
//			m_instancesColor4f_2[m_countColor4f_2] = obj;
//			m_countColor4f_2++;
//		} else {
//			System.err.println("Color4f ZU WENIG");
//		}
//	}
	
	
}
