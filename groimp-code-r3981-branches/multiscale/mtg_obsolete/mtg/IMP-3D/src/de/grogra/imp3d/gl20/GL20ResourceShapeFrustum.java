package de.grogra.imp3d.gl20;

import java.lang.Math;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20ResourceMeshSingleUser;
import de.grogra.imp3d.gl20.GL20ResourceShape;
import de.grogra.imp3d.PolygonArray;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class GL20ResourceShapeFrustum extends GL20ResourceShape {
	/**
	 * height attribute bit
	 */
	final private static int HEIGHT = 0x1;

	/**
	 * baseRadius attribute bit
	 */
	final private static int BASE_RADIUS = 0x2;

	/**
	 * topRadius attribute bit
	 */
	final private static int TOP_RADIUS = 0x4;

	/**
	 * scaleV attribute bit
	 */
	final private static int SCALE_V = 0x8;

	/**
	 * baseClosed attribute bit
	 */
	final private static int BASE_CLOSED = 0x10;

	/**
	 * topClosed attribute bit
	 */
	final private static int TOP_CLOSED = 0x20;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * height attribute
	 */
	private float height = 1.0f;

	/**
	 * baseRadius attribute
	 */
	private float baseRadius = 1.0f;

	/**
	 * topRadius attribute
	 */
	private float topRadius = 1.0f;

	/**
	 * scaleV attribute
	 */
	private float scaleV = 1.0f;

	/**
	 * baseClosed attribute
	 */
	private boolean baseClosed = true;

	/**
	 * topClosed attribute
	 */
	private boolean topClosed = true;

	/**
	 * the sub divisions that was used if the <code>mesh</code> was created
	 */
	private int meshSubDivisions = 0;

	/**
	 * mesh resource for this <code>GL20ResourceShapeFrustum</code>
	 */
	private GL20ResourceMeshSingleUser mesh = null;

	/**
	 * create a <code>PolygonArray</code> for a frustum
	 *
	 * @param subDivisions number of sub divisions in horizontal way
	 * @param topRadius the top radius of the frustum
	 * @param baseRadius the base radius of the frustum
	 * @return <code>PolygonArray</code> of a frustum OR <code>null</code> if
	 * <code>subDivisions</code> is out of range [3,..)
	 */
	final private PolygonArray createFrustumPolygonArray(int subDivisions,float topRadius,float baseRadius) {
		if (subDivisions < 3)
			return null;

		PolygonArray polygonArray = new PolygonArray();
		polygonArray.init(3);
		polygonArray.planar = true;
		polygonArray.edgeCount = 3;

		FloatList vertexList = polygonArray.vertices;
		FloatList uvList = polygonArray.uv;
		IntList polygonList = polygonArray.polygons;
		int vertexCount = 0;

		final int SUB_DIVS = subDivisions;
		final double piOverSUB_DIVS = Math.PI / (double)SUB_DIVS;
		final float oneOverSUB_DIVS = 1.0f / (float)SUB_DIVS;

		// top of the frustum
		float x,y;
		polygonArray.setNormal(vertexCount++,0.0f,0.0f,1.0f);
		uvList.add(0.5f); uvList.add(0.5f);
		vertexList.add(0.0f); vertexList.add(0.0f); vertexList.add(1.0f);

		for (int step=0;step <= SUB_DIVS;step++) {
			double angle = 2.0 * piOverSUB_DIVS * (double)step;
			x = (float)Math.cos(angle);
			y = (float)Math.sin(angle);

			polygonArray.setNormal(vertexCount++,0.0f,0.0f,1.0f);
			uvList.add(x / 2.0f + 0.5f); uvList.add(y / 2.0f + 0.5f);
			vertexList.add(x * topRadius); vertexList.add(y * topRadius); vertexList.add(1.0f);

			if (step > 0) {
				polygonList.add(0); polygonList.add(vertexCount - 2); polygonList.add(vertexCount - 1);
			}
		}

		// middle of the frustum
		for (int step=0;step <= SUB_DIVS;step++) {
			double angle = 2.0 * piOverSUB_DIVS * (double)step;
			final float texCoordX = oneOverSUB_DIVS * (float)step;
			x = (float)Math.cos(angle);
			y = (float)Math.sin(angle);

			// TODO check normal calculation when topRadius != baseRadius
			polygonArray.setNormal(vertexCount++,x,y,0.0f);
			uvList.add(texCoordX); uvList.add(0.0f);
			vertexList.add(x * topRadius); vertexList.add(y * topRadius); vertexList.add(1.0f);

			// TODO check normal calculation when topRadius != baseRadius
			polygonArray.setNormal(vertexCount++,x,y,0.0f);
			uvList.add(texCoordX); uvList.add(1.0f);
			vertexList.add(x * baseRadius); vertexList.add(y * baseRadius); vertexList.add(0.0f);

			if (step > 0) {
				polygonList.add(vertexCount - 4); polygonList.add(vertexCount - 3); polygonList.add(vertexCount - 1);
				polygonList.add(vertexCount - 4); polygonList.add(vertexCount - 1); polygonList.add(vertexCount - 2);
			}
		}

		// base of the frustum
		final int lastVertexIndex = vertexCount + SUB_DIVS + 1;
		for (int step=0;step <= SUB_DIVS;step++) {
			double angle = 2.0 * piOverSUB_DIVS * (double)step;
			x = (float)Math.cos(angle);
			y = (float)Math.sin(angle);

			polygonArray.setNormal(vertexCount++,0.0f,0.0f,-1.0f);
			uvList.add(x / 2.0f + 0.5f); uvList.add(y / 2.0f + 0.5f);
			vertexList.add(x * baseRadius); vertexList.add(y * baseRadius); vertexList.add(0.0f);

			if (step > 0) {
				polygonList.add(vertexCount - 2); polygonList.add(lastVertexIndex); polygonList.add(vertexCount - 1);
			}
		}
		
		polygonArray.setNormal(vertexCount, 0.0f, 0.0f, -1.0f);
		uvList.add(0.5f); uvList.add(0.5f);
		vertexList.add(0.0f); vertexList.add(0.0f); vertexList.add(0.0f);

		return polygonArray;
	}

	public GL20ResourceShapeFrustum() {
		super(GL20Resource.GL20RESOURCE_SHAPE_FRUSTUM);
	}

	/**
	 * set the height of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @param height the height
	 */
	final public void setHeight(float height) {
		if (this.height != height) {
			this.height = height;
			changeMask |= HEIGHT;
		}
	}

	/**
	 * get the height of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @return the height
	 */
	final public float getHeight() {
		return height;
	}

	/**
	 * set the base radius of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @param baseRadius the base radius
	 */
	final public void setBaseRadius(float baseRadius) {
		if (this.baseRadius != baseRadius) {
			this.baseRadius = baseRadius;
			changeMask |= BASE_RADIUS;
		}
	}

	/**
	 * get the base radius of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @return the base radius
	 */
	final public float getBaseRadius() {
		return baseRadius;
	}

	/**
	 * set the top radius of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @param topRadius the top radius
	 */
	final public void setTopRadius(float topRadius) {
		if (this.topRadius != topRadius) {
			this.topRadius = topRadius;
			changeMask |= TOP_RADIUS;
		}
	}

	/**
	 * get the top radius of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @return the top radius
	 */
	final public float getTopRadius() {
		return topRadius;
	}

	/**
	 * set if the base is closed or not for this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @param baseClosed <code>true</code> - the base is closed
	 */
	final public void setBaseClosed(boolean baseClosed) {
		if (this.baseClosed != baseClosed) {
			this.baseClosed = baseClosed;
			changeMask |= BASE_CLOSED;
		}
	}

	/**
	 * get if the base is closed or not for this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @return <code>true</code> - the base is closed
	 */
	final public boolean isBaseClosed() {
		return baseClosed;
	}

	/**
	 * set if the top is closed or not for this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @param topClosed <code>true</code> - the top is closed
	 */
	final public void setTopClosed(boolean topClosed) {
		if (this.topClosed != topClosed) {
			this.topClosed = topClosed;
			changeMask |= TOP_CLOSED;
		}
	}

	/**
	 * get if the top is closed or not for this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @return <code>true</code> - the top is closed
	 */
	final public boolean isTopClosed() {
		return topClosed;
	}

	/**
	 * tell this <code>GL20ResourceShape</code> that it should apply the
	 * geometry to the <code>GL20GfxServer</code>
	 */
	public void applyGeometry() {
		super.applyGeometry();
		mesh.draw();
	}

	/**
	 * check if this <code>GL20ResourceShapeFrustum</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShapeFrustum</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if ((changeMask != 0) || (mesh == null) || (mesh.isUpToDate() == false))
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		if ((changeMask != 0) || (mesh == null)) {
			if ((changeMask & HEIGHT) != 0) {
				// height of frustum changed, so change the shape transformation matrix
				setShapeTransformationMatrix(new Matrix4d(1.0, 0.0, 0.0, 0.0,
														  0.0, (height > 0.0f) ? 1.0 : -1.0, 0.0, 0.0,
														  0.0, 0.0, height, 0.0,
														  0.0, 0.0, 0.0, 1.0));
			}

			if (((changeMask & TOP_RADIUS) != 0) || ((changeMask & BASE_RADIUS) != 0) || (mesh == null)) {
				// top and/or bottom radius has changed, create new polygon array
				if (mesh != null)
					// destroy old <code>GL20ResourceMeshSingleUser</code>
					mesh.destroy();

				mesh = new GL20ResourceMeshSingleUser();
				meshSubDivisions = 20;
				mesh.setPolygonArray(createFrustumPolygonArray(meshSubDivisions,topRadius,baseRadius));
			}

			if (((changeMask & TOP_CLOSED) != 0) || ((changeMask & BASE_CLOSED) != 0)) {
				// top and/or bottom state has changed, so change the indices offset and length
				if (mesh != null) {
					mesh.setUsedPolygonArea((topClosed == true) ? 0 : meshSubDivisions,
											(baseClosed == true) ? 0 : -meshSubDivisions);
				}
			}

			changeMask = 0;
		}

		// apply changes to <code>GL20ResourceShape</code>
		super.update();
		// apply changes to <code>GL20ResourceMeshSingleUser</code>
		mesh.update();
	}

	/**
	 * destroy this <code>GL20ResourceShapeFrustum</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		if (mesh != null)
			// destroy the <code>GL20ResourceMeshSingleUser</code>
			mesh.destroy();

		super.destroy();
	}
}