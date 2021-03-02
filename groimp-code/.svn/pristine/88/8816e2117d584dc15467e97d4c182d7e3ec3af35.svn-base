/*
 * Copyright (C) 2020 GroIMP Developer Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor2d;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.LineArray;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.LineSegmentizationCache;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.Polygon;
import de.grogra.math.VertexListImpl;


public class NURBSDisplay2D implements GLEventListener{

	public static GL gl = null;
	private static GLU glu = new GLU();

	public static GLJPanel panel;
	public static GraphState gs;
	
	private static String geometry = "Curve";
	
	private float optionGridSpacing = 1.0f;
	private int optionGridDimension = 5;
		
	public NURBSDisplay2D(GLJPanel panel, GraphState gs) {
		NURBSDisplay2D.panel = panel;
		NURBSDisplay2D.gs = gs;
	}
	
	public static void setGeometry(String g) {
		geometry = g;
	}
	
	public static String getGeometry() {
		return geometry;
	}
	
	//determine range of view in display
	public static Point2f pixelToCoordinate(Point input) {
		Point2f out;

		float w = 10;
		float h = 10;
		
		float x = -w/2 + (float) input.x / (float) panel.getWidth() * w;
		float y = h/2 - (float) input.y / (float) panel.getHeight() * h;
		
		out = new Point2f(x, y);
		return out;
	}	
	
		
	private void drawGrid(GL gl) {
		gl.glLineWidth(1);
		Point3f startPoint = new Point3f(), endPoint = new Point3f();
		Color3f gridColor = new Color3f(0.8f, 0.8f, 0.8f);
		float gridSize = optionGridDimension * optionGridSpacing;
		for (float i = -gridSize; i <= gridSize; i+=optionGridSpacing) {
			for (float j = -gridSize; j <= gridSize; j+=optionGridSpacing) {
				startPoint.set(i,j,0); endPoint.set(-i,j,0);
				drawLine (startPoint, endPoint, gridColor);
				startPoint.set(i,j,0); endPoint.set(i,-j,0);
				drawLine (startPoint, endPoint, gridColor);
			}
		}
		
		gl.glLineWidth(3);
		startPoint.set(gridSize,0,0); endPoint.set(-gridSize,0,0);
		drawLine (startPoint, endPoint, gridColor);
		startPoint.set(0,gridSize,0); endPoint.set(0,-gridSize,0);
		drawLine (startPoint, endPoint, gridColor);
		gl.glLineWidth(1);
	}
	
	public void drawLine (Tuple3f origin, Tuple3f end, Tuple3f color)
	{
		gl.glColor3f (color.x, color.y, color.z);

		gl.glBegin (GL.GL_LINES);
		gl.glVertex3f (origin.x, origin.y, origin.z);
		gl.glVertex3f (end.x, end.y, end.z);
		gl.glEnd();
	}
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
			
		drawGrid(gl);
		for(int i = 0; i < ObjectGeometry2D.getCurveCounter(); i++){ 
			if(ObjectGeometry2D.getNURBSCurve(i) != null) {
				if(ObjectGeometry2D.getCurveData(ObjectGeometry2D.getNURBSCurve(i)) != null) {
					int size = ObjectGeometry2D.getSize(ObjectGeometry2D.getNURBSCurve(i));
					
					if(size > 0) {
						drawCircle(ObjectGeometry2D.getCenter(ObjectGeometry2D.getNURBSCurve(i)), new Color3f(0.6f, 0.6f, 0.9f));
						int j = 0;
						while(j < size){
							drawCircle(ObjectGeometry2D.getPoint(ObjectGeometry2D.getNURBSCurve(i), j), new Color3f(0, 0, 0));
							j++;
						}
					}
					drawCurve(ObjectGeometry2D.getNURBSCurve(i));
				}
			}
		}
		for(int k = 0; k < ObjectGeometry2D.getRCounter(); k++) {
			drawRectangle((ObjectGeometry2D.getRectangles())[k]);
		}
		for(int k = 0; k < ObjectGeometry2D.getTCounter(); k++) {
			drawTriangle((ObjectGeometry2D.getTriangles())[k]);
		}
		for(int k = 0; k < ObjectGeometry2D.getCCounter(); k++) {
			drawCircle((ObjectGeometry2D.getCircles())[k]);
		}
	}
	
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClearColor(1, 1, 1, 1);		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		gl = drawable.getGL();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-5, 5, -5, 5, -100, 100);
		gl.glMatrixMode(GL.GL_MODELVIEW); 	
		gl.glLoadIdentity();
	}

	
	public static void drawCircle(Point3f point, Color3f color) {
		float radius = 0.05f;
		
		gl.glColor3f(color.x, color.y, color.z);
		gl.glTranslatef(point.x, point.y, point.z);
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, radius, 10, 15);
		glu.gluDeleteQuadric(quad);
		gl.glTranslatef(-point.x, -point.y, -point.z);
	}
	
    //method copied from  imp3d.gl.GLDisplay
    public void drawCurve(NURBSCurve nurbs) {
		Color3f color = new Color3f(0.0f, 0.0f, 0.0f);
		
    	LineSegmentizationCache lineCache = new LineSegmentizationCache(gs, 1);
    	boolean asNode = true;        
    	LineArray lines = lineCache.get (nurbs, asNode,	(LineSegmentizable) nurbs);
		int[] indices = lines.lines.elements;
		float[] vertices = lines.vertices.elements;
		int dim = lines.dimension;
		int n = lines.lines.size ();

		// draw the lines
		Point3f p0 = new Point3f ();
		Point3f p1 = new Point3f ();
		boolean newLine = true;

		// iterate through indices
		for (int i = 0; i < n; i++)
		{
			// get current index
			int index = indices[i];

			// check if line strip was finished
			if (index < 0)
			{
				newLine = true;
				continue;
			}

			// check if a line is started
			if (newLine)
			{
				// set starting point
				p0.set (dim > 0 ? vertices[dim * index + 0] : 0,
					dim > 1 ? vertices[dim * index + 1] : 0,
					dim > 2 ? vertices[dim * index + 2] : 0);
				newLine = false;
			}
			else
			{
				// set end point
				p1.set (dim > 0 ? vertices[dim * index + 0] : 0,
					dim > 1 ? vertices[dim * index + 1] : 0,
					dim > 2 ? vertices[dim * index + 2] : 0);

				// draw the line
				drawLine (p0, p1, color);

				// endpoint is new startpoint
				p0.set (p1);
			}
		}    	
    }
    
    //render Polygon with four corner points
    public void drawRectangle(Polygon quad ) {
		gl.glColor3f(0.4f, 0.55f, 0.43f);
		VertexListImpl vertices = (VertexListImpl) quad.getVertices();
		float[] data = vertices.getData(); 
        gl.glLineWidth(1);
		gl.glBegin(GL.GL_QUADS);
			for(int i = 0; i < data.length/2; i++) {
				gl.glVertex2f(data[2* i], data[2 * i+1]);
			}
		gl.glEnd();
	}
	
	//render Polygon with three corner points
	public static void drawTriangle(Polygon triangle) {
		gl.glColor3f(0.4f, 0.55f, 0.43f);
		VertexListImpl vertices = (VertexListImpl) triangle.getVertices();
		float[] data = vertices.getData(); 		
        gl.glLineWidth(1);
		gl.glBegin(GL.GL_TRIANGLES);
			for(int i = 0; i <data.length/2; i++) {
				gl.glVertex2f(data[2 * i], data[2 * i + 1]);
			}
		gl.glEnd();	
	}
	
	public static void drawCircle(Point2f[] params) {
		Point2f point = params[0];
		Point2f r = params[1];
		float radius = (float) Math.sqrt(Math.pow(point.x - r.x,2) + Math.pow(point.y - r.y, 2));

		gl.glColor3f(0.4f, 0.55f, 0.43f);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		for(int i = 0; i < 30; i++) {
			float theta = 2.0f * 3.1415926f * (float) i / 30f;
			
			float tmpx = radius * (float) Math.cos((double) theta);
			float tmpy = radius * (float) Math.sin((double) theta);

			gl.glVertex2f(tmpx + point.x, tmpy + point.y);
		}
		gl.glEnd();
	}
    
    
	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
	}
}