package de.grogra.imp3d;

import java.awt.Graphics;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.shading.Shader;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Cone;
import de.grogra.vecmath.geom.Cube;
import de.grogra.vecmath.geom.Cylinder;
import de.grogra.vecmath.geom.Frustum;
import de.grogra.vecmath.geom.FrustumBase;
import de.grogra.vecmath.geom.HalfSpace;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Sphere;
import de.grogra.vecmath.geom.Square;
import de.grogra.vecmath.geom.Supershape;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * A <code>VolumeBuilderBase</code> is used to build volumes.
 * 
 * @author Dietger van Antwerpen
 * 
 * @see #VolumeBuilder
 */

public abstract class VolumeBuilderBase {
	public final float epsilon;
	
	private final Matrix4d xform = new Matrix4d ();
	private final Matrix4d squareXform = new Matrix4d ();

	private final Matrix3d rot = new Matrix3d ();
	private final Vector3d trans = new Vector3d ();
	
	protected PolygonizationCache polyCache;
	
	protected abstract Matrix4d getCurrentTransformation ();
	
	public VolumeBuilderBase( PolygonizationCache polyCache, float epsilon )
	{
		this.polyCache = polyCache;
		this.epsilon = epsilon;
		xform.setIdentity ();
	}	
	
	protected Matrix4d getTransformation (Matrix4d t)
	{
		if (t == null)
		{
			return getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, getCurrentTransformation (), t);
			return xform;
		}
	}

	public void setInvTransformation (TransformableVolume v, Matrix4d t, double dz)
	{
		t.getRotationScale (rot);
		rot.invert ();
		t.get (trans);
		trans.x += dz * t.m02;
		trans.y += dz * t.m12;
		trans.z += dz * t.m22;
		v.setTransformation (rot, trans);
	}
	
	public Square buildParallelogram (float axis, Vector3f secondAxis, float scaleU,
			float scaleV, Matrix4d t)
	{
		squareXform.m03 = -secondAxis.x;
		squareXform.m13 = -secondAxis.y;
		squareXform.m23 = -secondAxis.z;
		squareXform.m33 = 1;
		squareXform.m00 = secondAxis.x * 2;
		squareXform.m10 = secondAxis.y * 2;
		squareXform.m20 = secondAxis.z * 2;
		squareXform.m01 = 0;
		squareXform.m11 = 0;
		squareXform.m21 = axis;
		if (Math.abs (secondAxis.x) < Math.abs (secondAxis.y))
		{
			squareXform.m02 = 1;
			squareXform.m12 = 0;
			squareXform.m22 = 0;
		}
		else
		{
			squareXform.m02 = 0;
			squareXform.m12 = 1;
			squareXform.m22 = 0;
		}
		Math2.mulAffine (squareXform, t, squareXform);
		Square v = new Square ();
		setInvTransformation (v, squareXform, 0);
		v.scaleU = scaleU;
		v.scaleV = scaleV;
		return v;
	}
	
	public HalfSpace buildPlane (Matrix4d t)
	{
		HalfSpace v = new HalfSpace ();
		setInvTransformation (v, t, 0);
		return v;
	}
	
	public Sphere buildSphere (float radius, Matrix4d t)
	{
		if (Math.abs (radius) < epsilon)
		{
			return null;
		}
		Sphere v = new Sphere ();
		setInvTransformation (v, t, 0);
		radius = 1 / radius;
		v.scale (radius, radius, radius);
		return v;
	}
	
	public Supershape buildSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Matrix4d t)
	{
		Supershape v = new Supershape (a, b, m1, n11, n12, n13, m2, n21, n22, n23);
		setInvTransformation (v, t, 0);
		return v;
	}

	public Cube buildBox (float halfWidth, float halfLength, float height,
			 Matrix4d t)
	{
		if ((Math.abs (halfWidth) < epsilon)
			|| (Math.abs (halfLength) < epsilon)
			|| (Math.abs (height) < epsilon))
		{
			return null;
		}
		Cube v = new Cube ();
		setInvTransformation (v, t, height / 2);
		v.scale (1 / halfWidth, 1 / halfLength, 2 / height);
		return v;
	}

	private final Matrix4d frustumXform = new Matrix4d ();

	public Frustum buildFrustum (float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Matrix4d t)
	{
		if (baseRadius < 0)
		{
			baseRadius = -baseRadius;
		}
		if (topRadius < 0)
		{
			topRadius = -topRadius;
		}
		if ((Math.abs (height) < epsilon) || (baseRadius + topRadius < epsilon))
		{
			return null;
		}
		
		boolean rotate = baseRadius < 0.999f * topRadius;
		if (rotate)
		{
			frustumXform.setIdentity ();
			frustumXform.m11 = frustumXform.m22 = -1;
			frustumXform.m23 = height;
			frustumXform.mul (t, frustumXform);
			t = frustumXform;
			float r = baseRadius;
			baseRadius = topRadius;
			topRadius = r;
			boolean c = baseClosed;
			baseClosed = topClosed;
			topClosed = c;
		}
		
		Frustum v = new Frustum ();
		v.base = baseRadius / topRadius;
		setInvTransformation (v, t, height * v.base / (v.base - 1));
		v.scale (1 / topRadius, -1 / topRadius, (1 - v.base) / height);
		v.baseOpen = !baseClosed;
		v.topOpen = !topClosed;
		v.rotateUV = rotate;
		v.scaleV = scaleV;
		return v;
	}
	
	public FrustumBase buildBaseFrustum (float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Matrix4d t)
	{
		if (baseRadius < 0)
		{
			baseRadius = -baseRadius;
		}
		if (topRadius < 0)
		{
			topRadius = -topRadius;
		}
		if ((Math.abs (height) < epsilon) || (baseRadius + topRadius < epsilon))
		{
			return null;
		}

		boolean rotate = baseRadius < 0.999f * topRadius;
		if (rotate)
		{
			frustumXform.setIdentity ();
			frustumXform.m11 = frustumXform.m22 = -1;
			frustumXform.m23 = height;
			frustumXform.mul (t, frustumXform);
			t = frustumXform;
			float r = baseRadius;
			baseRadius = topRadius;
			topRadius = r;
			boolean c = baseClosed;
			baseClosed = topClosed;
			topClosed = c;
		}

		if (topRadius < 0.001f * baseRadius)
		{
			Cone v = new Cone ();
			setInvTransformation (v, t, height);
			v.base = 1;
			v.scale (1 / baseRadius, -1 / baseRadius, -1 / height);
			v.baseOpen = !baseClosed;
			v.rotateUV = rotate;
			v.scaleV = scaleV;
			return v;
		}
		else if (topRadius < 0.999f * baseRadius)
		{
			Frustum v = new Frustum ();
			v.base = baseRadius / topRadius;
			setInvTransformation (v, t, height * v.base / (v.base - 1));
			v.scale (1 / topRadius, -1 / topRadius, (1 - v.base) / height);
			v.baseOpen = !baseClosed;
			v.topOpen = !topClosed;
			v.rotateUV = rotate;
			v.scaleV = scaleV;
			return v;
		}
		else
		{
			Cylinder v = new Cylinder ();
			setInvTransformation (v, t, height / 2);
			v.scale (1 / baseRadius, 1 / baseRadius, 2 / height);
			v.baseOpen = !baseClosed;
			v.topOpen = !topClosed;
			v.scaleV = scaleV;
			return v;
		}
	}
	
	public MeshVolume buildPolygons (Polygonizable pz, Object obj, boolean asNode, Matrix4d t)
	{
		MeshVolume v = null;
		PolygonArray mesh = polyCache.get (obj, asNode, pz);
		if (mesh.wasCleared ())
		{
			if (mesh.vertices.isEmpty ())
			{
				mesh.userObject = this;
			}
			else
			{
				v = new MeshVolume ();
				v.setMesh (mesh);
				v.setTransformation (t);
				mesh.userObject = v;
			}
		}
		else if (mesh.userObject != this)
		{
			v = ((MeshVolume) mesh.userObject).dup ();
			v.setTransformation (t);
		}

		return v;
	}
}
