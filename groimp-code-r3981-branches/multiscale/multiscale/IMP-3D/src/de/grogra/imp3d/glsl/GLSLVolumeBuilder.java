package de.grogra.imp3d.glsl;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Pool;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Cone;
import de.grogra.vecmath.geom.Cube;
import de.grogra.vecmath.geom.Cylinder;
import de.grogra.vecmath.geom.Frustum;
import de.grogra.vecmath.geom.Sphere;
import de.grogra.vecmath.geom.Square;
import de.grogra.vecmath.geom.TransformableVolume;
import de.grogra.vecmath.geom.Variables;

/**
 * Simple class that will gather Scene information about the extend of all
 * objects
 * 
 * @author Konni Hartmann
 */
public class GLSLVolumeBuilder implements RenderState {

	private GLDisplay disp = null;

	private final Vector3d minPoint = new Vector3d();
	private final Vector3d maxPoint = new Vector3d();

	private final Matrix4d mat = new Matrix4d();
	private final Matrix3d rot = new Matrix3d();
	private final Matrix3d rotInv = new Matrix3d();

	private final Vector3d trans = new Vector3d();

	private void setInvTransformation(Matrix4d t, double dz) {
		t.getRotationScale(rot);
		rotInv.invert(rot);
		t.get(trans);
		trans.x += dz * t.m02;
		trans.y += dz * t.m12;
		trans.z += dz * t.m22;
	}

	private boolean needUpdate() {
		return old_Stamp != cur_Stamp;
	}
	
	boolean needUpdate = true;

	private int cur_Stamp = -1;
	private int old_Stamp = -2;

	/**
	 * Should be called after all renderables have been processed. If not, calculated
	 * scene information will be incorrect.
	 */
	public void finish() {
		needData = false;
	}

	/**
	 * Initializes values for calculation of scene extend. Should be called before
	 * nodes are processed by this class.
	 * @param disp The Display using this class.
	 */
	public void initSceneExtent(GLDisplay disp) {
		if (this.disp == null)
			this.disp = disp;
		old_Stamp = cur_Stamp;
		cur_Stamp = disp.getView().getGraph().getStamp();

		if(!needUpdate()) {
			GLSLDisplay.printDebugInfoN("Szene extend will not be updated!");
			return;
		}
		
		needData = true;
		
		GLSLDisplay.printDebugInfoN("Szene extend will be updated!");

		minPoint.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		maxPoint.set(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
		radiusUpToDate = false;
		centerUpToDate = false;
		vol = null;
	}

	private boolean radiusUpToDate = false;
	private double cachedRadius = 0.0;

	/**
	 * @return The radius of a sphere containing all renderable 
	 * nodes of the scene.
	 */
	public double getRadius() {
		if (!radiusUpToDate) {
			tmpVec1.sub(maxPoint, minPoint);
			cachedRadius = tmpVec1.length() * 0.5;
			radiusUpToDate = true;
		}
		return cachedRadius;
	}

	private boolean centerUpToDate = false;
	private Vector3d cachedCenter = new Vector3d();

	/**
	 * @return The center of a sphere containing all renderable 
	 * nodes of the scene.
	 */
	public Vector3d getCenter() {
		if (!centerUpToDate) {
			tmpVec1.add(minPoint, maxPoint);
			tmpVec1.scale(0.5);

			tmpVec4.set(tmpVec1);
			tmpVec4.w = 1;

			mat.invert(disp.getView3D().getCamera()
					.getWorldToViewTransformation());
			mat.transform(tmpVec4);
			cachedCenter.set(tmpVec4.x, tmpVec4.y, tmpVec4.z);
			centerUpToDate = true;
		}

		return cachedCenter;
	}

	private void updateMin(Vector3d v) {
//		System.err.println("UPMIN: "+v);
		minPoint.x = Math.min(minPoint.x, v.x);
		minPoint.y = Math.min(minPoint.y, v.y);
		minPoint.z = Math.min(minPoint.z, v.z);
	}

	private void updateMax(Vector3d v) {
//		System.err.println("UPMAX: "+v);
		maxPoint.x = Math.max(maxPoint.x, v.x);
		maxPoint.y = Math.max(maxPoint.y, v.y);
		maxPoint.z = Math.max(maxPoint.z, v.z);
	}

	private final Vector3d tmpVec1 = new Vector3d();
	private final Vector3d tmpVec2 = new Vector3d();
	private final Vector3d radiusVec = new Vector3d();
	private final Vector4d tmpVec4 = new Vector4d();

	Variables tmp = new Variables();
	TransformableVolume vol;

	private final Matrix4d squareXform = new Matrix4d();

	public void drawParallelogram(float axis, Vector3f secondAxis,
			float scaleU, float scaleV, Shader s, int highlight, Matrix4d t) {

//		if (!needUpdate())
//			return;

		t = disp.getTransformation(t);
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
		if (Math.abs(secondAxis.x) < Math.abs(secondAxis.y)) {
			squareXform.m02 = 1;
			squareXform.m12 = 0;
			squareXform.m22 = 0;
		} else {
			squareXform.m02 = 0;
			squareXform.m12 = 1;
			squareXform.m22 = 0;
		}
		Math2.mulAffine(squareXform, t, squareXform);

		setInvTransformation(squareXform, 0);
		Square square = new Square();
		square.setTransformation(rotInv, trans);

		vol = square;

		vol.getExtent(tmpVec1, tmpVec2, tmp);
		setRadius(tmpVec1, tmpVec2);
		updateMin(tmpVec1);
		updateMax(tmpVec2);
	}

	public void drawSphere(float radius, Shader s, int highlight, Matrix4d t) {

//		if (!needUpdate())
//			return;

		t = disp.getTransformation(t);
		setInvTransformation(t, 0);

		Sphere sphere = new Sphere();

		sphere.setTransformation(rotInv, trans);
		radius = 1 / radius;
		sphere.scale(radius, radius, radius);

		vol = sphere;

		vol.getExtent(tmpVec1, tmpVec2, tmp);
//		System.err.println(vol+" "+tmpVec1+" "+tmpVec2);
		setRadius(tmpVec1, tmpVec2);
		updateMin(tmpVec1);
		updateMax(tmpVec2);
	}
	
	public void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader s, int highlight, Matrix4d t)
	{
	}

	public void drawBox(float halfWidth, float halfLength, float height,
			Shader s, int highlight, Matrix4d t) {

//		if (!needUpdate())
//			return;

		t = disp.getTransformation(t);
		setInvTransformation(t, 0);

		Cube cube = new Cube();
		cube.setTransformation(rotInv, trans);
		cube.scale(1 / halfWidth, 1 / halfLength, 2 / height);

		vol = cube;

		vol.getExtent(tmpVec1, tmpVec2, tmp);
		setRadius(tmpVec1, tmpVec2);
		updateMin(tmpVec1);
		updateMax(tmpVec2);
	}

	private final Matrix4d frustumXform = new Matrix4d();

	private final float epsilon = 1e-5f;

	public void drawFrustum(float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader s,
			int highlight, Matrix4d t) {

//		if (!needUpdate())
//			return;

		if (baseRadius < 0) {
			baseRadius = -baseRadius;
		}
		if (topRadius < 0) {
			topRadius = -topRadius;
		}
		if ((Math.abs(height) < epsilon) || (baseRadius + topRadius < epsilon)) {
			return;
		}

		t = disp.getTransformation(t);

		boolean rotate = baseRadius < 0.999f * topRadius;
		if (rotate) {
			frustumXform.setIdentity();
			frustumXform.m11 = frustumXform.m22 = -1;
			frustumXform.m23 = height;
			frustumXform.mul(t, frustumXform);
			t = frustumXform;
			float r = baseRadius;
			baseRadius = topRadius;
			topRadius = r;
			boolean c = baseClosed;
			baseClosed = topClosed;
			topClosed = c;
		}

		if (topRadius < 0.001f * baseRadius) {

			setInvTransformation(t, height);
			Cone cone = new Cone();
			cone.setTransformation(rotInv, trans);
			cone.base = 1;
			cone.scale(1 / baseRadius, -1 / baseRadius, -1 / height);
			cone.baseOpen = !baseClosed;
			cone.rotateUV = rotate;
			cone.scaleV = scaleV;
			vol = cone;
		} else if (topRadius < 0.999f * baseRadius) {
			Frustum frustum = new Frustum();
			frustum.base = baseRadius / topRadius;
			setInvTransformation(t, height * frustum.base / (frustum.base - 1));

			frustum.setTransformation(rotInv, trans);
			frustum.scale(1 / topRadius, -1 / topRadius, (1 - frustum.base)
					/ height);
			frustum.baseOpen = !baseClosed;
			frustum.topOpen = !topClosed;
			frustum.rotateUV = rotate;
			frustum.scaleV = scaleV;
			vol = frustum;
		} else {
			setInvTransformation(t, height / 2);
			Cylinder cylinder = new Cylinder();
			cylinder.setTransformation(rotInv, trans);
			cylinder.scale(1 / baseRadius, 1 / baseRadius, 2 / height);
			cylinder.baseOpen = !baseClosed;
			cylinder.topOpen = !topClosed;
			cylinder.scaleV = scaleV;
			vol = cylinder;
		}

		vol.getExtent(tmpVec1, tmpVec2, tmp);
		setRadius(tmpVec1, tmpVec2);
		updateMin(tmpVec1);
		updateMax(tmpVec2);
	}

	public void drawPolygons(Polygonizable polygons, Object obj,
			boolean asNode, Shader s, int highlight, Matrix4d t) {

//		if (!needUpdate())
//			return;

		t = disp.getTransformation(t);
		setInvTransformation(t, 0);

		if ((disp.polyCache != null)
				&& (disp.polyCache.getGraphState() != getRenderGraphState())) {
			disp.polyCache.clear();
			disp.polyCache = null;
		}
		if (disp.polyCache == null) {
			disp.polyCache = new PolygonizationCache(getRenderGraphState(),
					Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV,
					10, true);
		}
		
		PolygonArray mesh = disp.polyCache.get(obj, asNode, polygons);

		Vector3d p = new Vector3d();
		Vector3d min = new Vector3d();
		Vector3d max = new Vector3d();

		min.x = min.y = min.z = Double.POSITIVE_INFINITY;
		max.x = max.y = max.z = Double.NEGATIVE_INFINITY;

		for (int i = mesh.getVertexCount() - 1; i >= 0; i--) {
			mesh.getVertex(i, p);
			rot.transform(p);
			tmpVec1.add(trans, p);
			Math2.min(min, tmpVec1);
			Math2.max(max, tmpVec1);
		}
		
		vol = null;
		
		setRadius(min, max);
		updateMin(min);
		updateMax(max);
	}
	
	float radius = -1;

	private boolean needData = true;
	
	/**
	 * @return true, if the calculated scene extend needs to be updated.
	 */
	public boolean needsData() {
		return needData;
	}
	
	private void setRadius(Vector3d min, Vector3d max) {
		radiusVec.sub(maxPoint, minPoint);
		radiusVec.scale(0.5);
		radius = (float)radiusVec.length();
	}
	
	/**
	 * @return The radius of the last processed renderable node.
	 */
	public float getCurrentRadius() {
		// TODO Auto-generated method stub
		return radius;
	}
	
	/**
	 * @return The Volume-Object for the last processed renderable.
	 * This will also reset all information about this renderable to 
	 * default values. It should be called after {@link #getCurrentRadius()}
	 */
	public TransformableVolume getCurrentVolume() {
		TransformableVolume cache = vol;
		vol = null;
		radius = -1;
		return cache;
	}

	public void drawLine(Tuple3f start, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t) {
		vol = null;
		radius = -1;
	}

	public void drawPlane(Shader s, int highlight, Matrix4d t) {
		vol = null;
		radius = -1;
	}

	public void drawPoint(Tuple3f location, int pixelSize, Tuple3f color,
			int highlight, Matrix4d t) {
		vol = null;
		radius = -1;
	}

	public void drawPointCloud(float[] locations, float pointSize, Tuple3f color,
			int highlight, Matrix4d t)
	{
		vol = null;
		radius = -1;
	}

	public void drawRectangle(int x, int y, int w, int h, Tuple3f color) {
		vol = null;
		radius = -1;
	}

	public void drawString(int x, int y, String text, Font font, Tuple3f color) {
		vol = null;
		radius = -1;
	}

	public float estimateScaleAt(Tuple3f point) {
		return disp.estimateScaleAt(point);
	}

	public void fillRectangle(int x, int y, int w, int h, Tuple3f color) {
		vol = null;
		radius = -1;
	}

	public int getCurrentHighlight() {
		return disp.getCurrentHighlight();
	}

	public Shader getCurrentShader() {
		return disp.getCurrentShader();
	}

	public FontMetrics getFontMetrics(Font font) {
		return disp.getFontMetrics(font);
	}

	public Pool getPool() {
		return disp.getPool();
	}

	public GraphState getRenderGraphState() {
		return disp.getRenderGraphState();
	}

	public boolean getWindowPos(Tuple3f location, Tuple2f out) {
		return disp.getWindowPos(location, out);
	}
	
	//yong 3 apr 2012 - multiscale sierpinski
	/**
	 * Draw a regular tetrahedron
	 */
	public void drawTetrahedronReg(float length, Shader s, int highlight, Matrix4d t)
	{
	}
	//yong 3 apr 2012 - multiscale sierpinski end
}
