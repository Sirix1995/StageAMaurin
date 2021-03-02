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
package de.grogra.nurbseditor3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.LineArray;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.LineSegmentizationCache;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Box;
import de.grogra.imp3d.objects.Cone;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.Frustum;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.imp3d.objects.Sphere;



public class NURBSDisplay3D implements GLEventListener{

	public static GL gl = null;
	private static GLU glu = new GLU();
	public static GLJPanel panel;
	private static GraphState gs;
	private static String geometry = "Curve";

	private float optionGridColorR = 0.8f;
	private float optionGridColorG = 0.8f;
	private float optionGridColorB = 0.8f;

	private static int rotateXAngle = 0;
	private static int rotateYAngle = 0;
	private static int rotateZAngle = 0;
	
	public NURBSDisplay3D(GLJPanel panel, GraphState gs) {
		NURBSDisplay3D.panel = panel;
		NURBSDisplay3D.gs = gs;
	}
	
	public static void setGeometry(String g) {
		geometry = g;
	}
	
	public static String getGeometry() {
		return geometry;
	}

	public void setRotation(int[] r) {
		rotateXAngle = r[0];
		rotateYAngle = r[1];
		rotateZAngle = r[2];
	}
	
	public static int[] getRotation() {
		return new int [] {rotateXAngle, rotateYAngle, rotateZAngle};
	}

	public static Point3f pixelToCoordinate(Point3f input) {
		Point3f out = null;
		int yRotation = rotateYAngle;

			float newLeft =-6;
			float newRight = 6;
			float newTop = 6;
			float newBottom = -4;
			
			float xValue = newLeft + (float) input.x / (float) panel.getWidth() * (newRight - newLeft);
			float yValue = newTop + (float) input.y / (float) panel.getHeight() * (newBottom - newTop);

			if(yRotation == 0) {	
				if(rotateZAngle == 0) {
					out = new Point3f(input.z, yValue, -xValue);
				}else if(rotateZAngle == 90) {
					out = new Point3f(yValue, input.z, -xValue);
				}else if(rotateZAngle == -90){
					out = new Point3f(-yValue, input.z, -xValue);
				}else if(Math.abs(rotateZAngle) == 180) {
					out = new Point3f(input.z, -yValue, -xValue);
				}
			}else if(yRotation == 90) {
				if(rotateZAngle == 0) {
					out = new Point3f(xValue, yValue, input.z); 
				}else if(rotateZAngle == 90) {
					out = new Point3f(yValue, -xValue, input.z); 
				}else if(rotateZAngle == -90){
					out = new Point3f(-yValue, xValue, input.z); 
				}else if(Math.abs(rotateZAngle) == 180) {
					out = new Point3f(-xValue, -yValue, input.z); 
				}
			}else if(yRotation == -90) {
				if(rotateZAngle == 0) {
					out = new Point3f(-xValue, yValue, input.z); 
				}else if(rotateZAngle == 90) {
					out = new Point3f(yValue, xValue, input.z); 
				}else if(rotateZAngle == -90){
					out = new Point3f(-yValue, -xValue, input.z); 
				}else if(Math.abs(rotateZAngle) == 180) {
					out = new Point3f(xValue, -yValue, input.z); 					
				}	
			}else if(Math.abs(yRotation) == 180) {
				if(rotateZAngle == 0) {
					out = new Point3f(input.z, yValue, xValue);
				}else if(rotateZAngle == 90) {
					out = new Point3f(yValue, input.z, xValue);
				}else if(rotateZAngle == -90){
					out = new Point3f(-yValue, input.z, xValue);
				}else if(Math.abs(rotateZAngle) == 180) {
					out = new Point3f(input.z, -yValue, xValue);
				}	
			}
		return out;
	}	
	
	
	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		gl.glRotatef(-90, 0, 1, 0);
		gl.glRotatef(rotateYAngle, 0, 1, 0);
		gl.glRotatef(rotateZAngle, 0, 0, 1);
	
		drawGrid(gl);
			
		for(int i = 0; i < ObjectGeometry3D.getCurveCounter(); i++){ 
			if(ObjectGeometry3D.getNURBSCurve(i) != null) {
				if(ObjectGeometry3D.getCurveData(ObjectGeometry3D.getNURBSCurve(i)) != null) {
					int size = ObjectGeometry3D.getCurveSize(ObjectGeometry3D.getNURBSCurve(i));
					if(size > 0) {
						drawCircle(ObjectGeometry3D.getCurveCenter(ObjectGeometry3D.getNURBSCurve(i)), 0.05f, new Color3f(0.6f, 0.6f, 0.9f));
						int j = 0;
						while(j < size){
							drawCircle(ObjectGeometry3D.getCurvePoint(ObjectGeometry3D.getNURBSCurve(i), j), 0.05f, new Color3f(0, 0, 0));
							j++;
						}
					}
					drawCurve(ObjectGeometry3D.getNURBSCurve(i));
				}
			}
		}
		
		for(int k = 0; k < ObjectGeometry3D.getSurfaceCounter(); k++) {
			if(ObjectGeometry3D.getNURBSSurface(k) != null) {
				if(ObjectGeometry3D.getSurfaceData(ObjectGeometry3D.getNURBSSurface(k)) != null) {
					for(int m = 0; m < ObjectGeometry3D.getSurfaceSize(ObjectGeometry3D.getNURBSSurface(k));m++) {
						drawCircle(ObjectGeometry3D.getSurfaceCenter(ObjectGeometry3D.getNURBSSurface(k)), 0.05f, new Color3f(0.6f, 0.6f, 0.9f));
						drawCircle(ObjectGeometry3D.getSurfacePoint(ObjectGeometry3D.getNURBSSurface(k), m), 0.05f, new Color3f(0, 0, 0));
					}
					drawSurface(ObjectGeometry3D.createNURBSSurface(ObjectGeometry3D.getNURBSSurface(k)));
				}
			}
		}
		renderObjects();	
	}
	
	public void renderObjects() {
		gl.glColor3f(0.4f, 0.55f, 0.43f);
		for(int k = 0; k < ObjectGeometry3D.getQCounter(); k++) {
			Box b = (ObjectGeometry3D.getQuads())[k];
			Point3f p = (ObjectGeometry3D.getQuadCoordinates())[k];
			gl.glTranslatef(p.x, p.y, p.z);
			gl.glRotatef(-90, 1, 0, 0);
			drawBoxImpl(gl, -b.getWidth()/2, -b.getLength() /2, 0, b.getWidth()/2, b.getLength() /2, b.getHeight());
			gl.glRotatef(90, 1, 0, 0);
			gl.glTranslatef(-p.x, -p.y, -p.z);  
		}
		for(int k = 0; k < ObjectGeometry3D.getCCounter(); k++) {
			Cone c = (ObjectGeometry3D.getCones())[k];
			Point3f p = (ObjectGeometry3D.getConeCoordinates())[k];
			gl.glTranslatef(p.x, p.y, p.z);  
			gl.glRotatef(-90, 1, 0, 0);
			drawFrustumImpl(gl, 20, c.isOpen(), 0.0f, true, c.getRadius());
			gl.glRotatef(90, 1, 0, 0);
			gl.glTranslatef(-p.x, -p.y, -p.z);
		}
		for(int k = 0; k < ObjectGeometry3D.getCylCounter(); k++) {
			Cylinder c = (ObjectGeometry3D.getCylinders())[k];
			Point3f p = (ObjectGeometry3D.getCylinderCoordinates())[k];
			gl.glTranslatef(p.x, p.y, p.z);  
			gl.glRotatef(-90, 1, 0, 0);
			drawFrustumImpl(gl, 20, c.isTopOpen(), c.getRadius(), c.isBaseOpen(), c.getRadius());
			gl.glRotatef(90, 1, 0, 0);
			gl.glTranslatef(-p.x, -p.y, -p.z);
		}
		for(int k = 0; k < ObjectGeometry3D.getFCounter(); k++) {
			Frustum f = (ObjectGeometry3D.getFrustums())[k];
			Point3f p = (ObjectGeometry3D.getFrustumCoordinates())[k];
			gl.glTranslatef(p.x, p.y, p.z);  
			gl.glRotatef(-90, 1, 0, 0);
			drawFrustumImpl(gl, 20, f.isTopOpen(), f.getTopRadius(), f.isBaseOpen(), f.getBaseRadius());
			gl.glRotatef(90, 1, 0, 0);
			gl.glTranslatef(-p.x, -p.y, -p.z);
		}
		for(int k = 0; k < ObjectGeometry3D.getSCounter(); k++) {
			Sphere s = (ObjectGeometry3D.getSpheres())[k];
			Point3f p = (ObjectGeometry3D.getSphereCoordinates())[k];
			drawCircle(new Point4f(p.x, p.y, p.z, 0.0f), s.getRadius(), new Color3f(0.4f, 0.55f, 0.43f));
		}
	}

	private void drawGrid(GL gl) {
		Color3f gridColor = new Color3f(optionGridColorR, optionGridColorG, optionGridColorB);
		Point3f startPoint = new Point3f(), endPoint = new Point3f();
		gl.glLineWidth(2);
		for(int i = 0; i <=5; i++) {
			startPoint.set(i, 0, 0);endPoint.set(i, 5, 0);
			drawLine(startPoint, endPoint, gridColor);
			startPoint.set(i, 0, 0);endPoint.set(i, 0, 5);
			drawLine(startPoint, endPoint, gridColor);
			startPoint.set(0, i, 0);endPoint.set(5, i, 0);
			drawLine(startPoint, endPoint, gridColor);
			startPoint.set(0, i, 0);endPoint.set(0, i, 5);
			drawLine(startPoint, endPoint, gridColor);
			startPoint.set(0, 0, i);endPoint.set(0, 5, i);
			drawLine(startPoint, endPoint, gridColor);
			startPoint.set(0, 0, i);endPoint.set(5, 0, i);
			drawLine(startPoint, endPoint, gridColor);
			gl.glLineWidth(1);
		}	
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
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
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
		gl.glOrtho(-6, 6, -4, 6, -100, 100);
		gl.glMatrixMode(GL.GL_MODELVIEW); 	
		gl.glLoadIdentity();
	}

	public static void drawCircle(Point4f p, float radius, Color3f color) {
		gl.glColor3f(color.x, color.y, color.z);
		
		gl.glTranslatef(p.x, p.y, p.z);
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluSphere(quad, radius, 10, 15);
		glu.gluDeleteQuadric(quad);
		gl.glTranslatef(-p.x, -p.y, -p.z);			
	}
    
	//copied from imp3d.gl.GLDisplay
    public void drawCurve(NURBSCurve nurbs) {
    
		Color3f color = new Color3f(0.0f, 0.0f, 0.0f);
		
    	LineSegmentizationCache lineCache = new LineSegmentizationCache(gs, 1);
    	boolean asNode = true;        
    	LineArray lines = lineCache.get (nurbs, asNode,
				(LineSegmentizable) nurbs);

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
    
    static ByteBuffer newByteBuffer (int length)
	{
		return ByteBuffer.allocateDirect (length).order (
			ByteOrder.nativeOrder ());
	}
    
	private PolygonizationCache polyCache;
    
	//copied from imp3d.gl.GLDisplay
    public void drawSurface(NURBSSurface nurbs){
		Color3f color = new Color3f(0.4f, 0.55f, 0.43f);

		if ((polyCache != null)
				&& (polyCache.getGraphState () != gs))
			{
				polyCache.clear ();
				polyCache = null;
			}
			if (polyCache == null)
			{
				polyCache = new PolygonizationCache (gs,
					Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV, 10,
					true);
		}
		PolygonArray polys = polyCache.get (nurbs, true, nurbs);
		// obtain the buffer(s) for the polygons
		// or create such buffers if not already done so
		CacheData data;
		if (polys.wasCleared () || !(polys.userObject instanceof CacheData))
		{
			data = new CacheData ();
			data.polygonSize = polys.polygons.size;
			if (data.polygonSize > 0)
			{
			// convert arrays to buffers
				data.ib = newByteBuffer (polys.polygons.size * 4)
					.asIntBuffer ();
				polys.polygons.writeTo (data.ib);

				data.vb = newByteBuffer (polys.vertices.size * 4)
					.asFloatBuffer ();
				polys.vertices.writeTo (data.vb);

				data.nb = newByteBuffer (polys.normals.size);
				polys.normals.writeTo (data.nb);

				data.uvb = newByteBuffer (polys.uv.size * 4)
					.asFloatBuffer ();
				polys.uv.writeTo (data.uvb);
				
			}
			polys.userObject = data;
		}
		else
		{
			data = (CacheData) polys.userObject;
		}
		if (data.polygonSize > 0)
		{
			data.ib.rewind ();
			data.vb.rewind ();
			data.nb.rewind ();
			data.uvb.rewind ();
			switch (polys.visibleSides)
			{
				case Attributes.VISIBLE_SIDES_BACK:
					gl.glCullFace (GL.GL_FRONT);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 1);
					break;
				case Attributes.VISIBLE_SIDES_BOTH:
					gl.glDisable (GL.GL_CULL_FACE);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 1);
					break;
			}
			
			// enable client states for vertex, normal und texcoord
			gl.glEnableClientState (GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState (GL.GL_NORMAL_ARRAY);
			gl.glEnableClientState (GL.GL_TEXTURE_COORD_ARRAY);

			gl.glColor3f (color.x, color.y, color.z);
		
			// draw the object
			gl.glVertexPointer (polys.dimension, GL.GL_FLOAT, 0, data.vb);
			gl.glNormalPointer (GL.GL_BYTE, 0, data.nb);
			gl.glTexCoordPointer (2, GL.GL_FLOAT, 0, data.uvb);

			gl.glDrawElements ((polys.edgeCount == 3) ? GL.GL_TRIANGLES
					: GL.GL_QUADS, data.polygonSize, GL.GL_UNSIGNED_INT,
				data.ib);

			// disable client states
			gl.glDisableClientState (GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState (GL.GL_NORMAL_ARRAY);
			gl.glDisableClientState (GL.GL_TEXTURE_COORD_ARRAY);
			
			gl.glEnable (GL.GL_CULL_FACE);
			gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 0);
			
			// restore previous state
			gl.glMatrixMode (GL.GL_MODELVIEW);
			gl.glPopMatrix ();
		}
	}  
    
  //copied from imp3d.gl.GLDisplay
    void drawBoxImpl (GL gl, float x0, float y0, float z0, float x1, float y1,
			float z1)
	{
		gl.glBegin (GL.GL_QUADS);

		gl.glNormal3f (-1, 0, 0);
		gl.glTexCoord2f (0, 1f/3);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);
		gl.glTexCoord2f (0, 2f/3);
		gl.glVertex3f (x0, y1, z0);

		gl.glNormal3f (1, 0, 0);
		gl.glTexCoord2f (0.75f, 1f/3);
		gl.glVertex3f (x1, y0, z0);
		gl.glTexCoord2f (0.75f, 2f/3);
		gl.glVertex3f (x1, y1, z0);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);

		gl.glNormal3f (0, -1, 0);
		gl.glTexCoord2f (0.25f, 0);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (0.5f, 0);
		gl.glVertex3f (x1, y0, z0);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);

		gl.glNormal3f (0, 1, 0);
		gl.glTexCoord2f (0.25f, 1);
		gl.glVertex3f (x0, y1, z0);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.5f, 1);
		gl.glVertex3f (x1, y1, z0);

		gl.glNormal3f (0, 0, -1);
		gl.glTexCoord2f (1, 1f/3);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (1, 2f/3);
		gl.glVertex3f (x0, y1, z0);
		gl.glTexCoord2f (0.75f, 2f/3);
		gl.glVertex3f (x1, y1, z0);
		gl.glTexCoord2f (0.75f, 1f/3);
		gl.glVertex3f (x1, y0, z0);

		gl.glNormal3f (0, 0, 1);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);

		gl.glEnd ();
	}
    
    //copied from imp3d.gl.GLDisplay
    void drawFrustumImpl (GL gl, int uCount, boolean topClosed,
			float topRadius, boolean baseClosed, float baseRadius)
	{
		// draw connection from top to bottom
		gl.glBegin (GL.GL_QUAD_STRIP);
		for (int u = 0; u <= uCount; u++)
		{
			float phi = (float) (2.0f * Math.PI * u / uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);
			gl.glNormal3f (cosPhi, sinPhi, 0);
			gl.glTexCoord2f (phi / (float) (2.0f * Math.PI), 1);
			gl.glVertex3f (topRadius * cosPhi,
				topRadius * sinPhi, 1);
			gl.glTexCoord2f (phi / (float) (2.0f * Math.PI), 0);
			gl.glVertex3f (baseRadius * cosPhi,
				baseRadius * sinPhi, 0);
		}
		gl.glEnd ();

		// draw base circle
		if (baseClosed)
		{
			gl.glBegin (GL.GL_TRIANGLE_FAN);
			gl.glNormal3f (0, 0, -1);
			gl.glTexCoord2f (0.5f, 0.5f);
			gl.glVertex3f (0, 0, 0);
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * -u / uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				gl.glTexCoord2f (cosPhi / 2 + 0.5f, sinPhi / 2 + 0.5f);
				gl.glVertex3f (baseRadius * cosPhi,
					baseRadius * sinPhi, 0);
			}
			gl.glEnd ();
		}

		// draw top circle
		if (topClosed)
		{
			gl.glBegin (GL.GL_TRIANGLE_FAN);
			gl.glNormal3f (0, 0, +1);
			gl.glTexCoord2f (0.5f, 0.5f);
			gl.glVertex3f (0, 0, 1);
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * u / uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				gl.glTexCoord2f (cosPhi / 2 + 0.5f, sinPhi / 2 + 0.5f);
				gl.glVertex3f (topRadius * cosPhi,
					topRadius * sinPhi, 1);
			}
			gl.glEnd ();
		}
	}
}