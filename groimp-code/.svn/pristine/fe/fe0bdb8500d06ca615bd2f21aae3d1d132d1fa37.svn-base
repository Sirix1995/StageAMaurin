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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Pool;
import de.grogra.vecmath.geom.Volume;

public abstract class VolumeBuilder extends VolumeBuilderBase implements RenderState
{
	private Graphics graphics;
	private View3D view;
	private final Pool pool = new Pool ();

	public VolumeBuilder (PolygonizationCache polyCache, float epsilon)
	{
		super( polyCache , epsilon );
	}

	protected abstract void addVolume (Volume v, Matrix4d t, Shader s);

	private void createVolume( Volume v, Matrix4d t, Shader s)
	{
		if( v != null )	addVolume( v, t, s );
	}

	@Override
	public Pool getPool ()
	{
		return pool;
	}

	@Override
	public FontMetrics getFontMetrics (Font font)
	{
		return graphics.getFontMetrics (font);
	}

	@Override
	public int getCurrentHighlight ()
	{
		return 0;
	}

	@Override
	public float estimateScaleAt (Tuple3f point)
	{
		return (view != null) ? view.estimateScaleAt (point,
			getCurrentTransformation ()) : 1 / epsilon;
	}

	@Override
	public void drawPoint (Tuple3f location, int pixelSize, Tuple3f color,
			int highlight, Matrix4d t)
	{
	}

	@Override
	public void drawPointCloud (float[] locations, float pointSize, Tuple3f color, int highlight, Matrix4d t)
	{
	}

	@Override
	public void drawLine (Tuple3f start, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t)
	{
	}

	//private final Matrix4d squareXform = new Matrix4d ();

	@Override
	public void drawParallelogram (float axis, Vector3f secondAxis, float scaleU,
			float scaleV, Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume (buildParallelogram( axis, secondAxis, scaleU, scaleV, t ), t, s);
	}

	@Override
	public void drawPlane (Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildPlane( t ), t, s);
	}

	@Override
	public void drawSphere (float radius, Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildSphere( radius, t ), t, s);
	}
	
	@Override
	public void drawSphereSegmentSolid (float radius, float theta1, float theta2, float phi, Shader s, int highlight, boolean asWireframe, Matrix4d t) {
		t = getTransformation (t);
		createVolume( buildSphereSegment( radius, theta1, theta2, phi, t ), t, s);
	}
	
	@Override
	public void drawTextBlock(String caption, Font font, float depth, Shader s, int highlight, boolean asWireframe, Matrix4d t) {}

	@Override
	public void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildSupershape( a, b, m1, n11, n12, n13, m2, n21, n22, n23, t ), t, s);
	}

	@Override
	public void drawBox (float halfWidth, float halfLength, float height,
			Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildBox(halfWidth, halfLength, height, t ), t, s);
	}

	@Override
	public void drawLamella (float halfWidth, float halfLength, float height, float a, float b,
			Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildLamella(halfWidth, halfLength, height, a,b, t ), t, s);
	}

	//private final Matrix4d frustumXform = new Matrix4d ();

	@Override
	public void drawFrustum (float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader s,
			int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildBaseFrustum (height, baseRadius, topRadius,
				baseClosed, topClosed, scaleV, t) , t , s );
	}

	@Override
	public void drawPolygons (Polygonizable pz, Object obj, boolean asNode, Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
		t = getTransformation (t);
		createVolume( buildPolygons (pz, obj, asNode, t) ,t ,s );
	}

	@Override
	public boolean getWindowPos (Tuple3f location, Tuple2f out)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawRectangle (int x, int y, int w, int h, Tuple3f color)
	{
	}

	@Override
	public void fillRectangle (int x, int y, int w, int h, Tuple3f color)
	{
	}

	@Override
	public void drawString (int x, int y, String text, Font font, Tuple3f color)
	{
	}

	@Override
	public void drawFrustumIrregular(float height, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topclosed, 
			float scaleV, Shader s, int highlight, boolean asWireframe, Matrix4d t)
	{
	}
	
	@Override
	public void drawPrismRectangular(float y, float xPos, float xNeg, float zPos, float zNeg, int highlight, boolean asWireframe, Matrix4d t)
	{
	}
}
