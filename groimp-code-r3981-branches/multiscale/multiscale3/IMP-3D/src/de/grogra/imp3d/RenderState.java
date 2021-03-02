
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.imp3d;

import javax.vecmath.*;
import java.awt.*;
import de.grogra.math.Pool;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.shading.Shader;

/**
 * This interface describes all functions a render device must implement.
 * 
 * @author Reinhard Hemmerling
 */
public interface RenderState
{
	/**
	 * Passed as <code>highlight</code>-parameter to the
	 * <code>draw</code>-methods of this interface if the
	 * current highlight has to be used instead of the parameter.
	 */
	int CURRENT_HIGHLIGHT = -1;

	GraphState getRenderGraphState ();
	
	Pool getPool ();

	FontMetrics getFontMetrics (Font font);

	int getCurrentHighlight ();
	
	float estimateScaleAt (Tuple3f point);

	Shader getCurrentShader ();

	void drawPoint (Tuple3f location, int pixelSize, Tuple3f color, int highlight, Matrix4d t);
	
	/**
	 * Draw a set of points. The array locations contains a sequence of points given as triples
	 * of floats for x, y and z position of each point. If the same reference for the array
	 * location is passed in the implementation might assume that the contents of the array
	 * are the same as well. This is necessary for GLDisplay, for instance, to ensure a 
	 * performant implementation. The class PointCloud ensures that this is the case.
	 * @param locations array containing a sequence (x,y,z) of points
	 * @param pointSize size of the point on screen
	 * @param color color of the point
	 * @param highlight
	 * @param t transformation of the point cloud
	 */
	void drawPointCloud (float[] locations, float pointSize, Tuple3f color, int highlight, Matrix4d t);

	void drawLine (Tuple3f start, Tuple3f end, Tuple3f color, int highlight, Matrix4d t);

	void drawParallelogram (float axis, Vector3f secondAxis, float scaleU, float scaleV, Shader s, int highlight, Matrix4d t);

	void drawPlane (Shader s, int highlight, Matrix4d t);

	void drawSphere (float radius, Shader s, int highlight, Matrix4d t);

	/**
	 * Draw a supershape around the origin (0/0/0).
	 * 
	 * An implementation of Johan Gielis's Superformula which was published in the
	 * American Journal of Botany 90(3): 333–338. 2003.
     * INVITED SPECIAL PAPER A GENERIC GEOMETRIC TRANSFORMATION 
     * THAT UNIFIES A WIDE RANGE OF NATURAL AND ABSTRACT SHAPES
     *
     * @param a, b length of curves 
     * @param m, n shape parameters
     * @param shader
     * @param highlight
	 * @param t transformation of the point cloud
	 */
	void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader s, int highlight, Matrix4d t);
	
	void drawBox (float halfWidth, float halfLength, float height, Shader s, int highlight, Matrix4d t);

	void drawFrustum (float height, float baseRadius, float topRadius,
					  boolean baseClosed, boolean topClosed,
					  float scaleV, Shader s, int highlight, Matrix4d t);

	void drawPolygons (Polygonizable polygons, Object obj, boolean asNode, Shader s, int highlight, Matrix4d t);

	/**
	 * Computes the window coordinates in pixels of a location
	 * in the current object coordinates.
	 * 
	 * @param location a location in local object coordinates
	 * @param out the computed window coordinates are placed in here
	 * @return <code>true</code> iff the window coordinates are valid
	 * (i.e., the location is in the clipping region)
	 */
	boolean getWindowPos (Tuple3f location, Tuple2f out);

	void drawRectangle (int x, int y, int w, int h, Tuple3f color);
	
	void fillRectangle (int x, int y, int w, int h, Tuple3f color);

	void drawString (int x, int y, String text, Font font, Tuple3f color);
	
	void drawFrustumIrregular(float height, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topclosed, 
			float scaleV, Shader s, int highlight, Matrix4d t);
}
