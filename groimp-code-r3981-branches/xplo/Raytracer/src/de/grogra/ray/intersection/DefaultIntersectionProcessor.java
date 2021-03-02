package de.grogra.ray.intersection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

import de.grogra.ray.RTLight;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.RTSceneVisitor;
import de.grogra.ray.Raytracer;
import de.grogra.ray.quality.Quality;
import de.grogra.ray.quality.Timer;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;

public class DefaultIntersectionProcessor implements IntersectionProcessor {

	
	
	private RTScene m_sceneGraph = null;
	private int m_evaluationParameters = 0;
	
	private RTObject[] m_objects = null;
	private RTObject[] m_fakeObjects = null;
	
//	private IntersectionSceneVisitor m_intersectionSceneVisitor = 
//		new IntersectionSceneVisitor(null);
	
	private Timer m_timer;
	private boolean m_isTimerEnabled = false;
	
	private float   m_curT;
	private float   m_minT;
	private int     m_minIndex;

	
	/*
	public void setSceneGraph(RTSceneGraph sceneGraph) {
		m_sceneGraph = sceneGraph;
	}
	*/

	
	public void setEvaluationParameters(int value) {
		m_evaluationParameters = value;
	}

	
	public void prepareProcessing(RTScene sceneGraph) {
		m_sceneGraph = sceneGraph;
		m_objects = Raytracer.getShadeables(m_sceneGraph);
//		System.out.println("count:"+m_objects.length);
		// do nothing
		//m_timer = Quality.getQualityKit().getTimer(Raytracer.INTERSECTION_TIMER);
		//m_timer.reset();
	}
	
	
	public void cleanupProcessing() {
		
	}
	
	
	public boolean getFirstIntersectionDescription(Ray ray, RayContext context, 
			IntersectionDescription desc) {
		
//		System.out.println("context:");
//		System.out.println("  exclude:"+context.excludeObject);
//		context.printMaterials();
//		for (int i=0;i<m_objects.length;i++) {
//			System.out.println("  object "+i+" is entered:"+m_objects[i].getUserData().isInside);
//		}
		
		m_minT = Float.MAX_VALUE;
		m_minIndex = -1;
		for (int i=0;i<m_objects.length;i++) {
			
			if (m_objects[i]==context.excludeObject) { continue; }
			m_curT = m_objects[i].getDistance(ray,context);
						
//			System.out.println("m_curT:"+m_curT);
			if (m_curT<m_minT) {
//				System.out.println("<");
				m_minIndex = i;
				m_minT = m_curT;
			}
		}
		if (m_minIndex>-1) {
			m_objects[m_minIndex].getIntersectionDescription(desc);
			return true;
		}
		return false;
	}
	
	
	public float getFirstIntersectionT(Ray ray, RayContext context) {
		
		
		
//		if (1==1) { return Float.NaN; }
		
		m_minT = Float.MAX_VALUE;
		for (int i=0;i<m_objects.length;i++) {
			if (m_objects[i]==context.excludeObject) { continue; }
			m_curT = m_objects[i].getDistance(ray,context);
			if (m_curT<m_minT) {
				m_minT = m_curT;
			}
		}
		if (m_minT<Float.MAX_VALUE) {
			return m_minT;
		} else {
			return Float.NaN;
		}
	}

	
	/*
	public void getFirstIntersectionDescriptions(Ray ray, Collection intersections) {
		
		//if (m_isTimerEnabled) { m_timer.start(); }
		intersections.clear();
		if (m_sceneGraph==null) { return; }
				
		m_intersectionSceneVisitor.initialize(ray,intersections);
		//IntersectionSceneVisitor intersection_visitor = 
		//	new IntersectionSceneVisitor(ray);
		m_sceneGraph.traversSceneObjects(m_intersectionSceneVisitor);
		//if (m_isTimerEnabled) { m_timer.stop(); }
		
	
		//return m_intersectionSceneVisitor.getIntersections();
	}
	*/
	
	
//	public void getLightRays(Point3d point, Collection rays) {
//		
//	}


//	public void getAdditionColor(Ray ray, Color4f color) {
//		for (int i=0;i<m_objects.length;i++) {
//			if (m_objects[i].getBoundingVolume()!=null) {
//				if (m_objects[i].getBoundingVolume().hasIntersection(ray)) {
//					color.set(1.0f,0.0f,0.0f,0.5f);
//					return;
//				}
//			}
//		}
//		color.set(0.0f,0.0f,0.0f,0.0f);
//	}
//
//
//	public void cleanupProcessing() {
//		// TODO Auto-generated method stub
//		
//	}	
	
	
//	private class IntersectionSceneVisitor implements RTSceneVisitor {
//		
//		private Ray m_ray;
//		//private ArrayList m_intersections = new ArrayList(10);
//		//private
//		private Collection m_intersections;
//		
//		public IntersectionSceneVisitor(Ray ray) {
//			initialize(ray,null);
//		}
//		
//		
//		public void initialize(Ray ray, Collection intersections) {
//			m_ray = ray;
//			m_intersections = intersections;
//		}
//		
//
//		public void visitObject(RTObject object) {
//			IntersectionDescription desc = 
//				object.getNearestIntersectionDescription(m_ray,m_evaluationParameters);
//			if ((desc!=null) && (desc.getIntersectionCount()>0)) {
//				m_intersections.add(desc);
//			}
//		}
//
//		/*
//		public void visitLight(RTLight light) {
//			// TODO Auto-generated method stub
//			
//		}
//		*/
//		
//		/*
//		public IntersectionDescription[] getIntersections() {
//			
//			
//			IntersectionDescription[] res = new IntersectionDescription[m_intersections.size()];
//			for (int i=0;i<m_intersections.size();i++) {
//				res[i] = (IntersectionDescription)m_intersections.elementAt(i);
//			}
//			return res;
//			
//		}
//		*/
//		
//		/*
//		public IntersectionDescription getNearestIntersectionDescription() {
//			double t_max = Double.MAX_VALUE;
//			int index = -1;
//			IntersectionDescription cur_desc;
//			for (int i=0;i<m_intersections.size();i++) {
//				cur_desc = (IntersectionDescription)m_intersections.elementAt(i);
//				if (cur_desc.getT()<t_max) {
//					t_max = cur_desc.getT();
//					index = i;
//				}
//			}
//			if (index>-1) {
//				return (IntersectionDescription)m_intersections.elementAt(index);
//			} else {
//				return null;
//			}
//		}
//		*/
//		
//	}

}
