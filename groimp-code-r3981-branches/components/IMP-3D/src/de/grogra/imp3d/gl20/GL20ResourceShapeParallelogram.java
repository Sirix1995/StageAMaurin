package de.grogra.imp3d.gl20;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20ResourceMeshSingleUser;
import de.grogra.imp3d.gl20.GL20ResourceShape;
import de.grogra.imp3d.PolygonArray;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class GL20ResourceShapeParallelogram extends GL20ResourceShape {
	/**
	 * axis attribute bit
	 */
	final private static int AXIS = 0x1;

	/**
	 * secondAxis attribute bit
	 */
	final private static int SECOND_AXIS = 0x2;

	/**
	 * scaleU attribut bit
	 */
	final private static int SCALE_U = 0x4;

	/**
	 * scaleV attribute bit
	 */
	final private static int SCALE_V = 0x8;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * axis attribute
	 */
	private float axis = 1.0f;

	/**
	 * secondAxis attribute
	 */
	private Vector3f secondAxis = new Vector3f(1.0f,1.0f,1.0f);

	/**
	 * scaleU attribute
	 */
	private float scaleU = 1.0f;

	/**
	 * scaleV attribute
	 */
	private float scaleV = 1.0f;

	/**
	 * mesh resource of this <code>GL20ResourceShapeParallelogram</code>
	 */
	private GL20ResourceMeshSingleUser mesh = null;

	final private PolygonArray createParallelogramPolygonArray() {
		PolygonArray polygonArray = new PolygonArray();
		polygonArray.init(3);
		polygonArray.planar = true;
		polygonArray.edgeCount = 4;

		FloatList vertexList = polygonArray.vertices;
		FloatList uvList = polygonArray.uv;
		IntList polygonList = polygonArray.polygons;

		// calculate normal of this parallelogram
		Vector3f normal = new Vector3f(0.0f, 0.0f, axis);
		normal.cross(secondAxis,normal);

		polygonArray.setNormal(0,normal.x,normal.y,normal.z);
		polygonArray.setNormal(1,normal.x,normal.y,normal.z);
		polygonArray.setNormal(2,normal.x,normal.y,normal.z);
		polygonArray.setNormal(3,normal.x,normal.y,normal.z);
		uvList.add(0.0f); uvList.add(0.0f);
		uvList.add(1.0f); uvList.add(0.0f);
		uvList.add(1.0f); uvList.add(1.0f);
		uvList.add(0.0f); uvList.add(1.0f);
		vertexList.add(-secondAxis.x); vertexList.add(-secondAxis.y); vertexList.add(-secondAxis.z);
		vertexList.add(secondAxis.x); vertexList.add(secondAxis.y); vertexList.add(secondAxis.z);
		vertexList.add(secondAxis.x); vertexList.add(secondAxis.y); vertexList.add(axis + secondAxis.z);
		vertexList.add(-secondAxis.x); vertexList.add(-secondAxis.y); vertexList.add(axis - secondAxis.z);
		polygonList.add(0); polygonList.add(1); polygonList.add(2); polygonList.add(3);

		// flip normal for other side of this parallelogram
		normal.scale(-1.0f);

		polygonArray.setNormal(4,normal.x,normal.y,normal.z);
		polygonArray.setNormal(5,normal.x,normal.y,normal.z);
		polygonArray.setNormal(6,normal.x,normal.y,normal.z);
		polygonArray.setNormal(7,normal.x,normal.y,normal.z);
		uvList.add(0.0f); uvList.add(0.0f);
		uvList.add(0.0f); uvList.add(1.0f);
		uvList.add(1.0f); uvList.add(1.0f);
		uvList.add(1.0f); uvList.add(0.0f);
		vertexList.add(-secondAxis.x); vertexList.add(-secondAxis.y); vertexList.add(-secondAxis.z);
		vertexList.add(-secondAxis.x); vertexList.add(-secondAxis.y); vertexList.add(axis - secondAxis.z);
		vertexList.add(secondAxis.x); vertexList.add(secondAxis.y); vertexList.add(axis + secondAxis.z);
		vertexList.add(secondAxis.x); vertexList.add(secondAxis.y); vertexList.add(secondAxis.z);
		polygonList.add(4); polygonList.add(5); polygonList.add(6); polygonList.add(7);

		return polygonArray;
	}

	public GL20ResourceShapeParallelogram() {
		super(GL20Resource.GL20RESOURCE_SHAPE_PARALLELOGRAM);
	}

	/**
	 * set the axis of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @param axis the axis
	 */
	final public void setAxis(float axis) {
		if (this.axis != axis) {
			this.axis = axis;
			changeMask |= AXIS;
		}
	}

	/**
	 * get the axis of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @return the axis
	 */
	final public float getAxis() {
		return axis;
	}

	/**
	 * set the second axis of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @param secondAxis the second axis
	 */
	final public void setSecondAxis(Vector3f secondAxis) {
		if (this.secondAxis.equals(secondAxis) == false) {
			this.secondAxis.set(secondAxis);
			changeMask |= SECOND_AXIS;
		}
	}

	/**
	 * get the second axis of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @return the second axis
	 */
	final public Vector3f getSecondAxis() {
		return secondAxis;
	}

	/**
	 * set the u scale of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @param scaleU the u scale
	 */
	final public void setScaleU(float scaleU) {
		if (this.scaleU != scaleU) {
			this.scaleU = scaleU;
			changeMask |= SCALE_U;
		}
	}

	/**
	 * get the u scale of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @return the u scale
	 */
	final public float getScaleU() {
		return scaleU;
	}

	/**
	 * set the v scale of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @param scaleV the v scale
	 */
	final public void setScaleV(float scaleV) {
		if (this.scaleV != scaleV) {
			this.scaleV = scaleV;
			changeMask |= SCALE_V;
		}
	}

	/**
	 * get the v scale of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @return the v scale
	 */
	final public float getScaleV() {
		return scaleV;
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
	 * check if this <code>GL20ResourceShapeParallelogram</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShapeParallelogram</code> is up to date.
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if ((changeMask != 0) || (mesh == null) || (mesh.isUpToDate() == false))
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceShapeParallelogram</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		if ((changeMask != 0) || (mesh == null)) {
			if ((changeMask & (AXIS | SECOND_AXIS)) != 0) {
				if (mesh != null) {
					mesh.destroy();
					mesh = null;
				}
			}

			if (mesh == null) {
				// create a new mesh
				mesh = new GL20ResourceMeshSingleUser();
				mesh.setPolygonArray(createParallelogramPolygonArray());
			}

			changeMask = 0;
		}

		// apply changes to <code>GL20ResourceShape</code>
		super.update();
		// apply changes to <code>GL20ResourceMeshSingleUser</code>
		mesh.update();
	}

	/**
	 * destroy this <code>GL20ResourceShapeParallelogram</code>
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