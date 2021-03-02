package de.grogra.ray.intersection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayContext;
import de.grogra.ray.Raytracer;

public class OctreeIntersectionProcessor implements IntersectionProcessor {

	private final static boolean TEST_GET_FIRST_INTERSECTION_DESCRIPTION = false;
	private final static boolean TEST_GET_FIRST_INTERSECTION_T           = false;
	
	private final static int MAX_DIVISION_DEPTH = 5;
	private final static int MAX_LINKED_OBJECTS = 1;
	
	
	private RTScene m_sceneGraph = null;
//	private int          m_evaluationParameters = 0;
	private RTObject[]   m_objects = null;
	
	private OctreeCell       m_root = new OctreeCell(0);
	private final ArrayList  m_finiteObjects   = new ArrayList();
	private final ArrayList  m_infiniteObjects = new ArrayList();
	
	private float   m_finiteCurT;
	private float   m_finiteMinT;
	private int     m_finiteMinIndex;
	
	private float   m_infiniteCurT;
	private float   m_infiniteMinT;
	private int     m_infiniteMinIndex;
	
	private CellGenerator m_cellGenerator = new EndlCellGenerator();
	private final CellGenerator.NextCellOutput m_nextCellOutput =
		new CellGenerator.NextCellOutput();
	
	private boolean m_weiter;
	
	//private int m_continue = 0;
	
	
//	public void setEvaluationParameters(int value) {
//		m_evaluationParameters = value;
//	}

	
	public void prepareProcessing(RTScene sceneGraph) {
		m_sceneGraph = sceneGraph;
		m_objects = Raytracer.getShadeables(sceneGraph);
		
		//System.out.println("test");
		
		// TODO build octree
		Vector3f min_values = new Vector3f(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
		Vector3f max_values = new Vector3f(-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE);
		BoundingVolume cur_bounding;
		for (int i=0;i<m_objects.length;i++) {
			cur_bounding = m_objects[i].getBoundingVolume();
			if (cur_bounding!=null) {
				m_finiteObjects.add(m_objects[i]);
				if (cur_bounding.getMinX()<min_values.x) { min_values.x = cur_bounding.getMinX(); }
				if (cur_bounding.getMinY()<min_values.y) { min_values.y = cur_bounding.getMinY(); }
				if (cur_bounding.getMinZ()<min_values.z) { min_values.z = cur_bounding.getMinZ(); }
				if (cur_bounding.getMaxX()>max_values.x) { max_values.x = cur_bounding.getMaxX(); }
				if (cur_bounding.getMaxY()>max_values.y) { max_values.y = cur_bounding.getMaxY(); }
				if (cur_bounding.getMaxZ()>max_values.z) { max_values.z = cur_bounding.getMaxZ(); }
			} else {
				// TODO add this object to infinite objects list
				m_infiniteObjects.add(m_objects[i]);
			}
		}
		// make cubic scene bounding
		float scene_cube_length = max_values.x-min_values.x;
		if ((max_values.y-min_values.y)>scene_cube_length) {
			scene_cube_length = max_values.y-min_values.y;
		}
		if ((max_values.z-min_values.z)>scene_cube_length) {
			scene_cube_length = max_values.z-min_values.z;
		}
		Vector3f avg_values = new Vector3f();
		avg_values.x = (max_values.x+min_values.x)/2.0f;
		avg_values.y = (max_values.y+min_values.y)/2.0f;
		avg_values.z = (max_values.z+min_values.z)/2.0f;
		min_values.x = avg_values.x-scene_cube_length/2.0f;
		max_values.x = avg_values.x+scene_cube_length/2.0f;
		min_values.y = avg_values.y-scene_cube_length/2.0f;
		max_values.y = avg_values.y+scene_cube_length/2.0f;
		min_values.z = avg_values.z-scene_cube_length/2.0f;
		max_values.z = avg_values.z+scene_cube_length/2.0f;
		
		generateOctree(min_values,max_values);
		
		m_cellGenerator.initialize(m_root,MAX_DIVISION_DEPTH);
		
//		exportOctreeToVRML(new File("C:/Dokumente und Einstellungen/Micha/Desktop/octree.wrl"));
		
		
		
//		Ray test_ray = new Ray();
//		test_ray.getOrigin().set((float)38.500195, (float)-31.86595, (float)30.76243);
//		test_ray.getDirection().set((float)-0.8032335, (float)0.47582844, (float)-0.3583339);
//		m_cellGenerator.setRay(test_ray);
//		
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		
//		m_root.testInt = 1;
//		
//		while (m_nextCellOutput.nextCell!=null) {
//			m_nextCellOutput.nextCell.testInt = 1;
//			m_cellGenerator.nextCell(m_nextCellOutput);
//			System.out.println("cell:"+m_nextCellOutput.nextCell);
//		}
		
		
		
//		if (m_nextCellOutput.nextCell!=null) {
//			m_nextCellOutput.nextCell.testInt = 1;
//			//System.out.println("cell:"+m_nextCellOutput.nextCell);
//		} else {
//			System.err.println("fehler");
//		}
//		
//		
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		if (m_nextCellOutput.nextCell!=null) {
//			m_nextCellOutput.nextCell.testInt = 1;
//			//System.out.println("cell:"+m_nextCellOutput.nextCell);
//		} else {
//			System.err.println("fehler");
//		}
		
		
		
		
	}
	
	
	private void exportOctreeToVRML(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("#VRML V2.0 utf8");writer.newLine();
			writer.write("");writer.newLine();
			recursivelyExportOctree(m_root,writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	private void recursivelyExportOctree(OctreeCell node, BufferedWriter writer) {
		if (node.hasChildren()) { 
			for (int i=0;i<8;i++) {
				recursivelyExportOctree(node.getChild(i),writer);
			}
		} else {
			if (node.getLinkedObjects().size()>0) {
				float scale_x = (node.getMaxValues().x-node.getMinValues().x)/2.0f;
				float scale_y = (node.getMaxValues().y-node.getMinValues().y)/2.0f;
				float scale_z = (node.getMaxValues().z-node.getMinValues().z)/2.0f;
				float translate_x = node.getMinValues().x+(scale_x/1.0f);
				float translate_y = node.getMinValues().y+(scale_y/1.0f);
				float translate_z = node.getMinValues().z+(scale_z/1.0f);
				try {
					writer.write("Transform {");writer.newLine();
					writer.write("  children [");writer.newLine();
					writer.write("    Shape {");writer.newLine();
					writer.write("      appearance Appearance {");writer.newLine();
					writer.write("        material Material {");writer.newLine();
//					writer.write("          transparency 0.5");writer.newLine();
					writer.write("        }");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      geometry Box {}");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  ]");writer.newLine();
					writer.write("  scale "+scale_x+" "+scale_y+" "+scale_z);writer.newLine();
					writer.write("  translation "+translate_x+" "+translate_y+" "+translate_z);writer.newLine();
					writer.write("}");writer.newLine();
					writer.write("");writer.newLine();
				} catch(Exception e) {
					System.err.println(e);
				}
			}
		}
	}
	 
	
	private void generateOctree(Vector3f minValues, Vector3f maxValues) {
		m_root.setExtension(minValues,maxValues);
		m_root.getLinkedObjects().clear();
		for (int i=0;i<m_finiteObjects.size();i++) {
			m_root.getLinkedObjects().add(m_finiteObjects.get(i));
		}
//		for (int i=0;i<m_infiniteObjects.size();i++) {
//			if ( ((RTObject)m_infiniteObjects.get(i)).
//					isInsideBox(m_root.getMinValues(),m_root.getMaxValues()) ) {
//				m_root.getLinkedObjects().add(m_infiniteObjects.get(i));
//				System.out.println(""+m_infiniteObjects.get(i).getClass().getName());
//				m_root.testInt = 1;
//			}
//		}
		recursivelyDivideNode(m_root,1);
		// set neighbourhood
		m_root.setNeighbours(null,null,null,null,null,null);
		if (m_root.hasChildren()) {
			for (int i=0;i<8;i++) {
				recursivelySetNeighbourhood(m_root,null,null,null,null,null,null,i);
			}
		}
	}
	
	
	private void recursivelyDivideNode(OctreeCell node, int depth) {
		if ((depth<=MAX_DIVISION_DEPTH) && 
			(node.getLinkedObjects().size()>MAX_LINKED_OBJECTS)) {
			
			node.divideNode();
			OctreeCell cur_node;
			RTObject cur_linked;
			for (int i=0;i<8;i++) {
				
				// add finite objects
				cur_node = node.getChild(i);
				cur_node.getLinkedObjects().clear();
				for (int j=0;j<node.getLinkedObjects().size();j++) {
					cur_linked = (RTObject)node.getLinkedObjects().get(j);
					if (cur_linked.getBoundingVolume().isInsideBox(
					//if (cur_linked.isInsideBox(
							cur_node.getMinValues(),cur_node.getMaxValues())) {
						cur_node.getLinkedObjects().add(cur_linked);
						
						if (cur_linked.getClass().getName().equals("de.grogra.imp3d.ray.RTPlane")) {
							cur_node.testInt = 1;
							//System.out.println("BINGO");
						}
						//System.out.println("add to child "+i+" the object "+j);
					}
				}
//				System.out.println("size:"+m_infiniteObjects.size());
//				// add infinite objects
//				for (int j=0;j<m_infiniteObjects.size();j++) {
//					cur_linked = (RTObject)m_infiniteObjects.get(j);
//					if (cur_linked.isInsideBox(cur_node.getMinValues(),cur_node.getMaxValues())) {
//						cur_node.getLinkedObjects().add(cur_linked);
//					}
//				}
				
				recursivelyDivideNode(cur_node,depth+1);
			}
			node.getLinkedObjects().clear();
		}
	}
	
	
	private void recursivelySetNeighbourhood(OctreeCell parent, 
			OctreeCell parentFront, OctreeCell parentBack,
			OctreeCell parentTop, OctreeCell parentBottom,
			OctreeCell parentLeft, OctreeCell parentRight, int childIndex) {
		
		OctreeCell child = parent.getChild(childIndex);
		
		// link child neighbourhood
		switch(childIndex) {
		case 0:
			if (parentFront!=null) {
				if (parentFront.hasChildren()) {
					parentFront = parentFront.getChild(2);
				}
			}
			if (parentBottom!=null) {
				if (parentBottom.hasChildren()) {
					parentBottom = parentBottom.getChild(1);
				}
			}
			if (parentLeft!=null) {
				if (parentLeft.hasChildren()) {
					parentLeft = parentLeft.getChild(4);
				}
			}
			child.setNeighbours(
				parentFront,parent.getChild(2),
				parent.getChild(1),parentBottom,
				parentLeft,parent.getChild(4));
			break;
		case 1:
			if (parentFront!=null) {
				if (parentFront.hasChildren()) {
					parentFront = parentFront.getChild(3);
				}
			}
			if (parentTop!=null) {
				if (parentTop.hasChildren()) {
					parentTop = parentTop.getChild(0);
				}
			}
			if (parentLeft!=null) {
				if (parentLeft.hasChildren()) {
					parentLeft = parentLeft.getChild(5);
				}
			}
			child.setNeighbours(
				parentFront,parent.getChild(3),
				parentTop,parent.getChild(0),
				parentLeft,parent.getChild(5));
			break;
		case 2:
			if (parentBack!=null) {
				if (parentBack.hasChildren()) {
					parentBack = parentBack.getChild(0);
				}
			}
			if (parentBottom!=null) {
				if (parentBottom.hasChildren()) {
					parentBottom = parentBottom.getChild(3);
				}
			}
			if (parentLeft!=null) {
				if (parentLeft.hasChildren()) {
					parentLeft = parentLeft.getChild(6);
				}
			}
			child.setNeighbours(
				parent.getChild(0),parentBack,
				parent.getChild(3),parentBottom,
				parentLeft,parent.getChild(6));
			break;
		case 3:
			if (parentTop!=null) {
				if (parentTop.hasChildren()) {
					parentTop = parentTop.getChild(2);
				}
			}
			if (parentBack!=null) {
				if (parentBack.hasChildren()) {
					parentBack = parentBack.getChild(1);
				}
			}
			if (parentLeft!=null) {
				if (parentLeft.hasChildren()) {
					parentLeft = parentLeft.getChild(7);
				}
			}
			child.setNeighbours(
				parent.getChild(1),parentBack,
				parentTop,parent.getChild(2),
				parentLeft,parent.getChild(7));
			break;
		case 4:
			if (parentFront!=null) {
				if (parentFront.hasChildren()) {
					parentFront = parentFront.getChild(6);
				}
			}
			if (parentBottom!=null) {
				if (parentBottom.hasChildren()) {
					parentBottom = parentBottom.getChild(5);
				}
			}
			if (parentRight!=null) {
				if (parentRight.hasChildren()) {
					parentRight = parentRight.getChild(0);
				}
			}
			child.setNeighbours(
				parentFront,parent.getChild(6),
				parent.getChild(5),parentBottom,
				parent.getChild(0),parentRight);
			break;
		case 5:
			if (parentFront!=null) {
				if (parentFront.hasChildren()) {
					parentFront = parentFront.getChild(7);
				}
			}
			if (parentTop!=null) {
				if (parentTop.hasChildren()) {
					parentTop = parentTop.getChild(4);
				}
			}
			if (parentRight!=null) {
				if (parentRight.hasChildren()) {
					parentRight = parentRight.getChild(1);
				}
			}
			child.setNeighbours(
				parentFront,parent.getChild(7),
				parentTop,parent.getChild(4),
				parent.getChild(1),parentRight);
			break;
		case 6:
			if (parentBack!=null) {
				if (parentBack.hasChildren()) {
					parentBack = parentBack.getChild(4);
				}
			}
			if (parentBottom!=null) {
				if (parentBottom.hasChildren()) {
					parentBottom = parentBottom.getChild(7);
				}
			}
			if (parentRight!=null) {
				if (parentRight.hasChildren()) {
					parentRight = parentRight.getChild(2);
				}
			}
			child.setNeighbours(
				parent.getChild(4),parentBack,
				parent.getChild(7),parentBottom,
				parent.getChild(2),parentRight);
			break;
		case 7:
			if (parentBack!=null) {
				if (parentBack.hasChildren()) {
					parentBack = parentBack.getChild(5);
				}
			}
			if (parentTop!=null) {
				if (parentTop.hasChildren()) {
					parentTop = parentTop.getChild(6);
				}
			}
			if (parentRight!=null) {
				if (parentRight.hasChildren()) {
					parentRight = parentRight.getChild(3);
				}
			}
			child.setNeighbours(
				parent.getChild(5),parentBack,
				parentTop,parent.getChild(6),
				parent.getChild(3),parentRight);
			break;
		default:
			System.err.println("OctreeIntersectionProcessor: wrong child index");
			return;
		}
		
		// recursively do this for all descendants
		if (child.hasChildren()) {
			for (int i=0;i<8;i++) {
				recursivelySetNeighbourhood(child,
						child.getNeighbourFront(),child.getNeighbourBack(),
						child.getNeighbourTop(),child.getNeighbourBottom(),
						child.getNeighbourLeft(),child.getNeighbourRight(),
						i);
			}
		} 
	}
	

	public boolean getFirstIntersectionDescription(Ray ray, RayContext context,
			IntersectionDescription desc) {
		
		boolean res = false;
		
		if ((m_infiniteObjects.size()==0)&&(m_finiteObjects.size()==0)) {
			return false;
		}
		
		if (m_infiniteObjects.size()==0) {
			res = getFirstIntersectionDescription_noInfiniteObjects(ray,context,desc);
		} else {
			res = getFirstIntersectionDescription_infiniteObjects(ray,context,desc);
		}
		
		if (TEST_GET_FIRST_INTERSECTION_DESCRIPTION) {
			boolean res2 = false;
			float m_minT = Float.MAX_VALUE;
			int m_minIndex = -1;
//			System.out.println("length:"+m_objects.length);
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				float m_curT = m_objects[i].getDistance(ray,context);
//				System.out.println("curT:"+m_curT);
				if (m_curT<m_minT) {
					m_minIndex = i;
					m_minT = m_curT;
				}
			}
			if (m_minIndex>-1) {
				m_objects[m_minIndex].getIntersectionDescription(desc);
				res2 = true;
			}
			if ((res!=res2)&&(res2==true)) {
				System.err.println("ERROR: wrong result for octree intersection processor");
				System.err.println("  res:"+res+" res2:"+res2);
			}
		}
		
		return res;
		
	}
	
	
	public boolean getFirstIntersectionDescription_noInfiniteObjects(Ray ray,RayContext context, 
			IntersectionDescription desc) {
		
		//	... test intersection with finite objects
		m_cellGenerator.setRay(ray);
		m_cellGenerator.nextCell(m_nextCellOutput);
		
		while (m_nextCellOutput.nextCell!=null) {
						
			m_finiteMinT = Float.MAX_VALUE;
			m_finiteMinIndex = -1;
			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
				if (m_nextCellOutput.nextCell.getLinkedObjects().get(i)==context.excludeObject) { continue; }
				
				m_finiteCurT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray,context);
				if (m_finiteCurT!=m_finiteCurT) { continue; } // (if m_curT is equals NaN)				
				if (m_finiteCurT>m_nextCellOutput.leavingT) {
					continue;
				} 
				
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
						
			if (m_finiteMinIndex>-1) {
				((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().
						get(m_finiteMinIndex)).getIntersectionDescription(desc);
				return true;
			}
			
			m_cellGenerator.nextCell(m_nextCellOutput);
		}
		
		if (m_nextCellOutput.errorOccurred) {
			System.out.println("error in octree intersection processor - using defaut intersection");
			// ... test intersection with default intersection test
			m_finiteMinT = Float.MAX_VALUE;
			m_finiteMinIndex = -1;
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				
				m_finiteCurT = m_objects[i].getDistance(ray,context);
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
			if (m_finiteMinIndex>-1) {
				m_objects[m_finiteMinIndex].getIntersectionDescription(desc);
				return true;
			}
		}
		
		return false;		
	}
	
	
	public boolean getFirstIntersectionDescription_infiniteObjects(Ray ray, 
			RayContext context, IntersectionDescription desc) {
		
//		if (exclude!=null) {
//			System.out.println("BINGO1");
//		}
		
		//	... test intersection with infinite objects
		m_infiniteMinT = Float.MAX_VALUE;
		m_infiniteMinIndex = -1;
		for (int i=0;i<m_infiniteObjects.size();i++) {
			if (m_infiniteObjects.get(i)==context.excludeObject) { continue; }
			
			m_infiniteCurT = ((RTObject)m_infiniteObjects.get(i)).getDistance(ray,context);
			if (m_infiniteCurT<=m_infiniteMinT) {
//				if (exclude!=null) {
//					System.out.println("BINGO2");
//				}
				m_infiniteMinIndex = i;
				m_infiniteMinT = m_infiniteCurT;
			}
		}
	
		//	... test intersection with finite objects
		m_cellGenerator.setRay(ray);		
		m_cellGenerator.nextCell(m_nextCellOutput);
			
		while (m_nextCellOutput.nextCell!=null) {
						
			m_finiteMinT = Float.MAX_VALUE;
			m_finiteMinIndex = -1;
			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
				if (m_nextCellOutput.nextCell.getLinkedObjects().get(i)==context.excludeObject) { continue; }
				
				m_finiteCurT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray,context);
				if (m_finiteCurT!=m_finiteCurT) { continue; } // (if m_curT is equals NaN)				
				if (m_finiteCurT>m_nextCellOutput.leavingT) {
					continue;
				} 
				
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
						
			if (m_infiniteMinIndex>-1) {
//				if (exclude!=null) {
//					System.out.println("BINGO3");
//				}
				if (m_infiniteMinT<m_nextCellOutput.leavingT) {
					if ((m_finiteMinIndex>-1) && (m_finiteMinT<m_infiniteMinT)) {
						((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().
								get(m_finiteMinIndex)).getIntersectionDescription(desc);
						return true;
					} else {
						((RTObject)m_infiniteObjects.get(m_infiniteMinIndex)).
								getIntersectionDescription(desc);
						return true;
					}
				}
			}
			if (m_finiteMinIndex>-1) {
				((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().
						get(m_finiteMinIndex)).getIntersectionDescription(desc);
				return true;
			}
			
			m_cellGenerator.nextCell(m_nextCellOutput);
			
		}
		
//		if (exclude!=null) {
//			System.out.println("BINGO4");
//		}
		
		if (m_nextCellOutput.errorOccurred) {
			System.err.println("### error has occurred ###");
			// ... test intersection with default intersection test
			m_finiteMinT = Float.MAX_VALUE;
			m_finiteMinIndex = -1;
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				
				m_finiteCurT = m_objects[i].getDistance(ray,context);
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
			if (m_finiteMinIndex>-1) {
				m_objects[m_finiteMinIndex].getIntersectionDescription(desc);
				return true;
			}
		} else {
			if (m_infiniteMinIndex>-1) {
//				if (exclude!=null) {
//					System.out.println("BINGO5");
//				}
				((RTObject)m_infiniteObjects.get(m_infiniteMinIndex)).
						getIntersectionDescription(desc);
				return true;
			}
		}
		
		return false;
		
		
		
//		m_cellGenerator.setRay(ray);
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		
//		while (m_nextCellOutput.nextCell!=null) {
//			
//			m_minT = Float.MAX_VALUE;
//			//m_minT = 
//			m_minIndex = -1;
//			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
//				m_curT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray);
//				if (m_curT!=m_curT) { continue; } // (if m_curT is equals NaN)				
//				if (m_curT>m_nextCellOutput.leavingT) {
//					continue;
//				} 
//				
//				if (m_curT<m_minT) {
//					m_minIndex = i;
//					m_minT = m_curT;
//				}
//			}
//			
//			if (m_minIndex>-1) {
//				((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().
//						get(m_minIndex)).getIntersectionDescription(m_evaluationParameters,desc);
//				return true;
//			}
//			
//			m_cellGenerator.nextCell(m_nextCellOutput);
//			
//		}
//		
//		return false;
		
		
//		if (m_nextCellOutput.nextCell == null) {
//			return false;
//		} else {
//			
//			//m_nextCellOutput.nextCell.testInt = 1;
//			m_minT = Float.MAX_VALUE;
//			m_minIndex = -1;
//			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
//				m_curT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray);
//				if (m_curT!=m_curT) { continue; } // (if m_curT is equals NaN)				
//				if (m_curT>m_nextCellOutput.leavingT) {
//					continue;
//				} 
//				
//				if (m_curT<m_minT) {
//					m_minIndex = i;
//					m_minT = m_curT;
//				}
//			}
//			
//			if (m_minIndex>-1) {
//				((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().
//						get(m_minIndex)).getIntersectionDescription(m_evaluationParameters,desc);
//				return true;
//			}
//			return false;
//		}
		
		
		
//		return false;
		
		
		
//		m_cellGenerator.setRay(ray);
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		
//		if (m_nextCellOutput.nextCell!=null) {
//			//m_nextCellOutput.nextCell.testInt = 1;
//			//System.out.println("ray:"+ray);
//		}
//		
//		
//		int cells = 0;
//		
//		while (m_nextCellOutput.nextCell!=null) {
//			cells++;
//			m_cellGenerator.nextCell(m_nextCellOutput);	
//		}
//		
//		if (cells>0) {
//			//System.out.println("cells: "+cells);
//		}
//		
//		return false;
		
	}

	
	public float getFirstIntersectionT(Ray ray, RayContext context) {
		
		float res = 0.0f;
		
		if (m_infiniteObjects.size()==0) {
			res = getFirstIntersectionT_noInfiniteObjects(ray,context);
		} else {
			res = getFirstIntersectionT_infiniteObjects(ray,context);
		}
		
		//--- test result -----------------------------------------------------
		if (TEST_GET_FIRST_INTERSECTION_T) {
			float res2;
			float m_minT = Float.MAX_VALUE;
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				float m_curT = m_objects[i].getDistance(ray,context);
				if (m_curT<m_minT) {
					m_minT = m_curT;
				}
			}
			if (m_minT<Float.MAX_VALUE) {
				res2 = m_minT;
			} else {
				res2 = Float.NaN;
			}
			// compare results
			if ((res!=res) ^ (res2!=res2)) {
				System.err.println("ERROR: wrong result for octree intersection processor");
			} else {
				if (res!=res2) {
					System.err.println("ERROR: wrong result for octree intersection processor");
				}
			}
		}
		//---------------------------------------------------------------------
 
		return res;
	}
	
	
	public float getFirstIntersectionT_noInfiniteObjects(
			Ray ray, RayContext context) {
		
		m_cellGenerator.setRay(ray);
		m_cellGenerator.nextCell(m_nextCellOutput);
		
		//System.out.println("in");
		while (m_nextCellOutput.nextCell!=null) {
			
			m_finiteMinT = Float.MAX_VALUE;
			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
				if (m_nextCellOutput.nextCell.getLinkedObjects().get(i)==context.excludeObject) { continue; }
				m_finiteCurT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray,context);
				if (m_finiteCurT!=m_finiteCurT) { continue; } // (if m_curT is equals NaN)				
				if (m_finiteCurT>m_nextCellOutput.leavingT) {	continue; } 
				
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
			
			if (m_finiteMinT<Float.MAX_VALUE) {
				//System.out.println("out");
				return m_finiteMinT;
			}
			
			m_cellGenerator.nextCell(m_nextCellOutput);
			//System.out.print(".");
			
		}
		//System.out.println("out");
		
		if (m_nextCellOutput.errorOccurred) {
			//System.err.println("!!! error has occurred !!!");
			System.out.println("error in octree intersection processor - using defaut intersection");
			// ... test intersection with default intersection test
			m_finiteMinT = Float.MAX_VALUE;
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				m_finiteCurT = m_objects[i].getDistance(ray,context);
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinT = m_finiteCurT;
				}
			}
			if (m_finiteMinT<Float.MAX_VALUE) {
				return m_finiteMinT;
			} else {
				return Float.NaN;
			}
		}
		
		return Float.NaN;
	}
	
	
	public float getFirstIntersectionT_infiniteObjects(
			Ray ray, RayContext context) {
		
//		System.out.println("ray1: "+ray);
		// test infinite objects
		m_infiniteMinT = Float.MAX_VALUE;
		for (int i=0;i<m_infiniteObjects.size();i++) {
			if (m_infiniteObjects.get(i)==context.excludeObject) { continue; }
			m_infiniteCurT = ((RTObject)m_infiniteObjects.get(i)).
				getDistance(ray,context);
			if (m_infiniteCurT<=m_infiniteMinT) {
				m_infiniteMinT = m_infiniteCurT;
			}
		}	
		
		m_cellGenerator.setRay(ray);
		m_cellGenerator.nextCell(m_nextCellOutput);
		
//		System.out.println("ray2: "+ray);
		while (m_nextCellOutput.nextCell!=null) {
			
			m_finiteMinT = Float.MAX_VALUE;
			for (int i=0;i<m_nextCellOutput.nextCell.getLinkedObjects().size();i++) {
				if (m_nextCellOutput.nextCell.getLinkedObjects().get(i)==context.excludeObject) { continue; }
				m_finiteCurT = ((RTObject)m_nextCellOutput.nextCell.getLinkedObjects().get(i)).getDistance(ray,context);
				if (m_finiteCurT!=m_finiteCurT) { continue; } // (if m_curT is equals NaN)				
				if (m_finiteCurT>m_nextCellOutput.leavingT) {	continue; } 
				
				if (m_finiteCurT<=m_finiteMinT) {
					//m_finiteMinIndex = i;
					m_finiteMinT = m_finiteCurT;
				}
			}
			
//			if (m_finiteMinT<Float.MAX_VALUE) {
//				return m_finiteMinT;
//			}
						
			if (m_finiteMinT<Float.MAX_VALUE) {
				if (m_finiteMinT<m_infiniteMinT) {
					return m_finiteMinT;
				} else if (m_infiniteMinT<m_finiteMinT) {
//					System.out.println("INFINITE m_infiniteMinT:"+m_infiniteMinT+" m_finiteMinT:"+m_finiteMinT);
					return m_infiniteMinT;
				}
			}
			
			m_cellGenerator.nextCell(m_nextCellOutput);
			
		}
		
		if (m_nextCellOutput.errorOccurred) {
			//System.err.println("!!! error has occurred !!!");
			System.out.println("error in octree intersection processor - using defaut intersection");
			// ... test intersection with default intersection test
			m_finiteMinT = Float.MAX_VALUE;
			for (int i=0;i<m_objects.length;i++) {
				if (m_objects[i]==context.excludeObject) { continue; }
				m_finiteCurT = m_objects[i].getDistance(ray,context);
				if (m_finiteCurT<=m_finiteMinT) {
					m_finiteMinT = m_finiteCurT;
				}
			}
			if (m_finiteMinT<Float.MAX_VALUE) {
				return m_finiteMinT;
			} else {
				return Float.NaN;
			}
		} else {
			if (m_infiniteMinT<Float.MAX_VALUE) {
				return m_infiniteMinT;
			} else {
				return Float.NaN;
			}
		}
	}

	
//	public void getLightRays(Point3d point, Collection rays) {
//		
//	}

	
	public void getAdditionColor(Ray ray, Color4f color) {
		
//		m_cellGenerator.setRay(ray);
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		
//		if (m_nextCellOutput.nextCell!=null) {
//			color.set(1.0f,0.0f,0.0f,0.5f);
//		} else {
//			color.set(0.0f,0.0f,0.0f,0.0f);
//		}
		
		
		color.set(0.0f,0.0f,0.0f,0.0f);
		
//		ray.getDirection().set(-0.55364966f, 0.49571064f, -0.669136f);
//		
//		m_nextCellOutput.error = false;
//		
//		m_cellGenerator.setRay(ray);
//		m_cellGenerator.nextCell(m_nextCellOutput);
//		
//		if (m_nextCellOutput.error) {
//			//System.out.println("ERROR");
//			color.set(1.0f,0.0f,1.0f,1.0f);
//			return;
//		}
//		
//		while (m_nextCellOutput.nextCell!=null) {
//			
//			
//			
//			m_cellGenerator.nextCell(m_nextCellOutput);
//			
//			if (m_nextCellOutput.error) {
//				//System.out.println("ERROR");
//				color.set(1.0f,0.0f,0.0f,1.0f);
//				return;
//			}
//			
//		}
		
		
		
		
//		color.set(1.0f,0.0f,0.0f,0.0f);
//		m_weiter = true;
//		recursivelyGetAdditionalColor(m_root,ray,color);
//		if (color.w>0.9f) { color.w = 0.9f; }
//		
//		m_cellGenerator.setRay(ray);
//		m_cellGenerator.nextCell(m_nextCellOutput);
		
		
		
		
//		if (m_nextCellOutput.nextCell!=null) {
//			color.set(1.0f,0.0f,0.0f,0.5f);
//		} else {
//			color.set(0.0f,0.0f,0.0f,0.0f);
//		}
		
	}
	
	
	private void recursivelyGetAdditionalColor(OctreeCell node, Ray ray, Color4f color) {
		if (!m_weiter) { return; }
		if (node.hasChildren()) { 
			for (int i=0;i<8;i++) {
				recursivelyGetAdditionalColor(node.getChild(i),ray,color);
			}
		} else {
			//if ((node.getLinkedObjects().size()>0) && (node.hasIntersection(ray))) {
			//if ((node.getLinkedObjects().size()>0) && (node.testInt>0) && (node.hasIntersection(ray))) {
			if ((node.testInt>0) && (node.hasIntersection(ray))) {
				//color.w += 0.25f;
				color.w = 0.5f;
				m_weiter = false;
			}
		}
	}


	public void cleanupProcessing() {
		
//		System.out.println("continue: "+m_continue);
		
//		System.out.println("same:"+((DefaultOctreeCellGenerator)m_cellGenerator).m_same);
//		System.out.println("diff:"+((DefaultOctreeCellGenerator)m_cellGenerator).m_different);
		

//		OctreeCell cell = m_root.getChild(7).getChild(0);
//		cell.getNeighbourFront().testInt = 1;
//		cell.getNeighbourBack().testInt = 1;
//		cell.getNeighbourTop().testInt = 1;
//		cell.getNeighbourBottom().testInt = 1;
//		cell.getNeighbourLeft().testInt = 1;
//		cell.getNeighbourRight().testInt = 1;
		
		
//		File file = new File("C:/Dokumente und Einstellungen/Micha/Desktop/first_cells.wrl");
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//			writer.write("#VRML V2.0 utf8");writer.newLine();
//			writer.write("");writer.newLine();
//			recursivelyExportFirstCells(m_root,writer);
//			writer.flush();
//			writer.close();
//		} catch (Exception e) {
//			System.err.println(e);
//		}
	}
	
	
	private void recursivelyExportFirstCells(OctreeCell node, BufferedWriter writer) {
		if (node.hasChildren()) { 
			for (int i=0;i<8;i++) {
				recursivelyExportFirstCells(node.getChild(i),writer);
			}
//		}
		} else //{
			if ((node.testInt>0)) {
			//if ((node.getLinkedObjects().size()>0) && (node.testInt>0)) {
				float scale_x = (node.getMaxValues().x-node.getMinValues().x)/2.0f;
				float scale_y = (node.getMaxValues().y-node.getMinValues().y)/2.0f;
				float scale_z = (node.getMaxValues().z-node.getMinValues().z)/2.0f;
				float translate_x = node.getMinValues().x+(scale_x/1.0f);
				float translate_y = node.getMinValues().y+(scale_y/1.0f);
				float translate_z = node.getMinValues().z+(scale_z/1.0f);
				try {
					writer.write("Transform {");writer.newLine();
					writer.write("  children [");writer.newLine();
					writer.write("    Shape {");writer.newLine();
					writer.write("      appearance Appearance {");writer.newLine();
					writer.write("        material Material {}");writer.newLine();
					writer.write("      }");writer.newLine();
					writer.write("      geometry Box {}");writer.newLine();
					writer.write("    }");writer.newLine();
					writer.write("  ]");writer.newLine();
					writer.write("  scale "+scale_x+" "+scale_y+" "+scale_z);writer.newLine();
					writer.write("  translation "+translate_x+" "+translate_y+" "+translate_z);writer.newLine();
					writer.write("}");writer.newLine();
					writer.write("");writer.newLine();
				} catch(Exception e) {
					System.err.println(e);
				}
			}
//		}
	}

}
