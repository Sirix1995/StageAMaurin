package de.grogra.imp3d.ray;

import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.SceneTree;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.imp3d.objects.SceneTree.Node;
import de.grogra.ray.RTLight;
import de.grogra.ray.RTObject;
import de.grogra.ray.RTScene;
import de.grogra.ray.RTSceneVisitor;

public class GroIMPSceneGraph implements RTScene {

	private Node m_root;
	private int m_stamp;
	private Graph m_graph;
	private SceneCounterVisitor m_counterVisitor = new SceneCounterVisitor();
	private ObjectsVisitor      m_objectsVisitor = new ObjectsVisitor();
	private LightsVisitor       m_lightsVisitor  = new LightsVisitor();
	
	
	
	//private RTObject[] m_visibleObjects;
	//private RTLight[] m_lights;
	
	
	public GroIMPSceneGraph(Tree tree) {
		m_root = (Node) tree.getRoot ();
		m_stamp = tree.getGraphState ().getGraph ().getStamp ();
		m_graph = tree.getGraphState().getGraph();		
		m_root.accept(m_counterVisitor);
	}
	
	/*
	public RTObject getRootNode() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	
	public void traversSceneObjects(RTSceneVisitor visitor) {
		m_objectsVisitor.setVisitor(visitor);
		m_root.accept(m_objectsVisitor);		
	}

	
	public void traversSceneLights(RTSceneVisitor visitor) {
		m_lightsVisitor.setVisitor(visitor);
		m_root.accept(m_lightsVisitor);	
	}

	/*
	public void traversScene(RTSceneVisitor visitor) {
		// TODO Auto-generated method stub

	}
	*/
	
	
	public int getShadeablesCount() {
		return m_counterVisitor.getShadeablesCount();
	}


	public int getLightsCount() {
		return m_counterVisitor.getLightsCount();
	}
	
	
	private class ObjectsVisitor implements SceneTree.Visitor {
		
		private RTSceneVisitor m_visitor;
		
		public ObjectsVisitor() {}
		
		public ObjectsVisitor(RTSceneVisitor visitor) {
			setVisitor(visitor);
		}
		
		public void setVisitor(RTSceneVisitor visitor) { m_visitor = visitor; }
		

		public void visit(Leaf node) {
			if (node instanceof RTObject) {
				m_visitor.visitObject((RTObject)node);
			}
		}
		
		public void visitEnter(InnerNode node) {}
		public void visitLeave(InnerNode node) {}
	}
	
	
	private class LightsVisitor implements SceneTree.Visitor {
		
		private RTSceneVisitor m_visitor;
		
		public LightsVisitor() {}
		
		public LightsVisitor(RTSceneVisitor visitor) {
			setVisitor(visitor);
		}
		
		public void setVisitor(RTSceneVisitor visitor) { m_visitor = visitor; }
		

		public void visit(Leaf node) {
			if (node instanceof RTLight) {
				m_visitor.visitObject((RTObject)node);
			}
		}
		
		public void visitEnter(InnerNode node) {}
		public void visitLeave(InnerNode node) {}
	}
	
	
	private class SceneCounterVisitor implements SceneTree.Visitor {

		private int m_shadeables = 0;
		private int m_lights     = 0;
		
		public int getShadeablesCount() { return m_shadeables; }
		public int getLightsCount() { return m_lights; }
		
		
		public void visit(Leaf node) {
			if (!(node instanceof RTObject)) { return; }
			
			if (((RTObject)node).isShadeable()) {
				m_shadeables++;
			}
			
			if (node instanceof RTLight) {
				m_lights++;
			}
		}
		
		public void visitEnter(InnerNode node) {}
		public void visitLeave(InnerNode node) {}
		
		
	}

	
	public int getStamp ()
	{
		return m_stamp;
	}

	
	public Object getGraph()
	{
		return m_graph;
	}
}
