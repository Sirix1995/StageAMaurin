package de.grogra.animation;

import de.grogra.animation.handler.sgnodereference.SGNodeReferenceHandler;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.math.TMatrix4d;
import de.grogra.math.TVector3d;
import de.grogra.math.UniformScale;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.edit.GraphSelection;

public class AnimToolbar {

	/**
	 * Method of button "Set Key" to create transformation key of selected object
	 * @param item
	 * @param arg
	 * @param ctx
	 */
	public static void setKey(Item item, Object arg, Context ctx) {
		System.out.println("setKey gedrueckt");
		
		// find current time
		AnimCore animCore = (AnimCore) ctx.getWorkbench().getProperty(Init.ANIMCORE);
		int time = animCore.getCurrentTime();
		
		// find selected object
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		if (s instanceof GraphSelection) {
			
			GraphSelection gs = (GraphSelection) s;
			
			for (int i = 0; i < gs.size(); i++) {
				Object o = gs.getObject(i);
				
				if (o instanceof Null) {
					Null node = (Null) o;
					
					NType.Field property = Null.transform$FIELD;
					Object value = property.getObject(node);
					
					if (value == null)
						value = new TMatrix4d();
					else if (value instanceof TVector3d)
						value = new TMatrix4d((TVector3d) value);
					else if (value instanceof UniformScale) {
						double scale = ((UniformScale) value).getScale();
						value = new TMatrix4d();
						((TMatrix4d) value).setScale(scale);
					}
					
					animCore.getAnimManager().putValue(time, node, property, value);
					// update to new object type
					animCore.getAnimManager().updateAnimValues(time, false);
				} // if 
			} // for
		} // if
	} // setKey(...)
	
	public static void setRadiusKey(Item item, Object arg, Context ctx) {
		System.out.println("setRadiusKey gedrueckt");
		
		// find current time
		AnimCore animCore = (AnimCore) ctx.getWorkbench().getProperty(Init.ANIMCORE);
		int time = animCore.getCurrentTime();
		
		// find selected object
		Object s = UIProperty.WORKBENCH_SELECTION.getValue (ctx);
		if (s instanceof GraphSelection) {
			
			GraphSelection gs = (GraphSelection) s;
			
			for (int i = 0; i < gs.size(); i++) {
				Object o = gs.getObject(i);
				
				if (o instanceof Sphere) {
					Sphere node = (Sphere) o;
					
					NType.Field property = Sphere.radius$FIELD;
					//Object value = property.getObject(node);
					float value = property.getFloat(node);
					
					animCore.getAnimManager().putValue(time, node, property, value);
				} // if 
			} // for
		} // if
	} // setKey(...)
	
	
	public static void testButton1(Item item, Object arc, Context ctx) {

		AnimCore animCore = (AnimCore) ctx.getWorkbench().getProperty(Init.ANIMCORE);
		
//		testGetValue(anim);
		testSetRange(animCore, 50, 150);
		
	}
	
	public static void testButton2(Item item, Object arc, Context ctx) {
		AnimCore animCore = (AnimCore) ctx.getWorkbench().getProperty(Init.ANIMCORE);
		SGNodeReferenceHandler handler = (SGNodeReferenceHandler) animCore.getAnimManager().getValueHandler();
		handler.testRestoreNode();
	}
	
	
	private static void testSetRange(AnimCore anim, int start, int end) {
		anim.getAnimManager().setAnimationRange(start, end);
	}
	
	
	private static void testGetValue(AnimCore anim) {
		
		// find a Null node
		Node node = null;

		Node n = anim.getGraph().getRoot();
		for (Edge e = n.getFirstEdge(); e != null; e.getNext(n)) {
			Node target = e.getTarget();
			if (target == n)
				continue;
			if (target instanceof Sphere) {
				node = target;
				break;
			}			
		}
		
		if (node != null) {
//			anim.getAnimManager().getValue(0, node, Sphere.radius$FIELD);
//			Debug.println("transform-value: " +
//					anim.getAnimManager().getValue(anim.getCurrentTime(), node, Null.transform$FIELD));
		}
	}
	
}
