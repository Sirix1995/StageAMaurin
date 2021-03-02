package de.grogra.imp3d.glsl.renderable;

import java.awt.Color;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import de.grogra.graph.GraphState;
import de.grogra.imp.objects.Attributes;
import de.grogra.imp3d.LineArray;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.NURBSCurve;

public class GLSLNURBSCurve extends GLSLRenderable {

	Object obj;
	boolean asNode = false;
	LineArray lines;
//	= ((GLSLDisplay)rs).getCurrentGLState().lineCache.get(object, asNode, shape);
	LineSegmentizable shape;
	Color3f color;
		

//	public LineSegmentizableDrawable(Object shape, Object object, Color3f col,
//			boolean asNode, Matrix34d worldTransform,
//			Matrix34d cachedWorldTransform) {
//		super(shape, object, null, null, asNode, worldTransform,
//				cachedWorldTransform);
//		this.color = col;
//	}
//
//	public LineSegmentizableDrawable(Object shape, Object object, Color3f col,
//			boolean asNode, Matrix4d worldTransform,
//			Matrix4d cachedWorldTransform) {
//		super(shape, object, null, null, asNode, worldTransform,
//				cachedWorldTransform);
//		this.color = col;
//	}

//	@Override
//	protected void draw(GLSLDisplay disp, OpenGLState glState) {
//
//
//	}


	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		// TODO Auto-generated method stub
		if(!(rs instanceof GLSLDisplay))
			return;
		lines = ((GLSLDisplay)rs).getCurrentGLState().lineCache.get (obj, asNode,
				(LineSegmentizable) shape);

		// setColor (object, asNode, s, state);
		int[] indices = lines.lines.elements;
		float[] vertices = lines.vertices.elements;
		int dim = lines.dimension;
		int n = lines.lines.size();

		// draw the lines
		Point3f p0 = new Point3f();
		Point3f p1 = new Point3f();
		boolean newLine = true;

		// iterate through indices
		for (int i = 0; i < n; i++) {
			// get current index
			int index = indices[i];

			// check if line strip was finished
			if (index < 0) {
				newLine = true;
				continue;
			}

			// check if a line is started
			if (newLine) {
				// set starting point
				p0.set(dim > 0 ? vertices[dim * index + 0] : 0,
						dim > 1 ? vertices[dim * index + 1] : 0,
						dim > 2 ? vertices[dim * index + 2] : 0);
				newLine = false;
			} else {
				// set end point
				p1.set(dim > 0 ? vertices[dim * index + 0] : 0,
						dim > 1 ? vertices[dim * index + 1] : 0,
						dim > 2 ? vertices[dim * index + 2] : 0);

				// draw the line
				rs.drawLine(p0, p1, color, 0, null);

				// endpoint is new startpoint
				p0.set(p1);
			}
		}		
	}


	@Override
	public GLSLRenderable getInstance() {
		return new GLSLNURBSCurve();
	}


	@Override
	public Class<?> instanceFor() {
		return NURBSCurve.class;
	}


	@Override
	public void updateInstance(Object reference, Object state,
			boolean asNode, GraphState gs) {
		// TODO Auto-generated method stub
		color = new Color3f(Color.WHITE);
		this.asNode = asNode;
		Object c = gs.getObjectDefault(state, asNode, Attributes.COLOR,
				this);
		if ((c != null) && (c != this)) {
			color = (Color3f) c;
		} 
//		else {
//			color = new Color3f(new Color(s.getAverageColor()));
//		}
		assert(reference instanceof LineSegmentizable);
		shape = (LineSegmentizable) reference;
		this.obj = state;
	}

//	@Override
//	public void attributeChanged(AttributeChangeEvent event) {
//		super.attributeChanged(event);
//		if(event.getAttribute() == Attributes.COLOR) {
//			Object val = event.getGraphState().getObjectDefault (event.getObject(), true,
//				de.grogra.imp.objects.Attributes.COLOR, color);
//			color = (Color3f) val;
//			System.out.println("new val = "+val);
//		} 
//	}
	
}
