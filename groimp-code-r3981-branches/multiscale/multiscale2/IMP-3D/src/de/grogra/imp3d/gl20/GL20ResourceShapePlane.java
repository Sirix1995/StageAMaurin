package de.grogra.imp3d.gl20;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20MeshServer;
import de.grogra.imp3d.gl20.GL20ResourceMeshMultiUser;
import de.grogra.imp3d.gl20.GL20ResourceShape;
import de.grogra.imp3d.PolygonArray;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class GL20ResourceShapePlane extends GL20ResourceShape {
	/**
	 * the name of the mesh
	 */
	final private static String MESH_PLANE_NAME = new String("PLANE");

	/**
	 * plane size
	 */
	final private static float PLANE_SIZE = 100.0f;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * mesh resource for this <code>GL20ResourceShapePlane</code>
	 */
	private GL20ResourceMeshMultiUser mesh;

	public GL20ResourceShapePlane() {
		super(GL20Resource.GL20RESOURCE_SHAPE_PLANE);

		GL20MeshServer meshServer = GL20MeshServer.getInstance();
		mesh = meshServer.getMultiUserMeshByName(MESH_PLANE_NAME);
		if (mesh == null) {
			// no mesh with the MESH_PLANE_NAME was found, so create one
			mesh = new GL20ResourceMeshMultiUser(MESH_PLANE_NAME);

			PolygonArray polygonArray = new PolygonArray();
			polygonArray.init(3);
			polygonArray.planar = true;
			polygonArray.edgeCount = 4;

			FloatList vertexList = polygonArray.vertices;
			FloatList uvList = polygonArray.uv;
			IntList polygonList = polygonArray.polygons;
			polygonArray.setNormal(0,0.0f,0.0f,1.0f);
			polygonArray.setNormal(1,0.0f,0.0f,1.0f);
			polygonArray.setNormal(2,0.0f,0.0f,1.0f);
			polygonArray.setNormal(3,0.0f,0.0f,1.0f);
			uvList.add(0.0f); uvList.add(0.0f);
			uvList.add(0.0f); uvList.add(1.0f);
			uvList.add(1.0f); uvList.add(1.0f);
			uvList.add(1.0f); uvList.add(0.0f);
			vertexList.add(-1.0f); vertexList.add(-1.0f); vertexList.add(0.0f);
			vertexList.add(-1.0f); vertexList.add(1.0f); vertexList.add(0.0f);
			vertexList.add(1.0f); vertexList.add(1.0f); vertexList.add(0.0f);
			vertexList.add(1.0f); vertexList.add(-1.0f); vertexList.add(0.0f);
			polygonList.add(0); polygonList.add(1); polygonList.add(2); polygonList.add(3);

			polygonArray.setNormal(4,0.0f,0.0f,-1.0f);
			polygonArray.setNormal(5,0.0f,0.0f,-1.0f);
			polygonArray.setNormal(6,0.0f,0.0f,-1.0f);
			polygonArray.setNormal(7,0.0f,0.0f,-1.0f);
			uvList.add(0.0f); uvList.add(0.0f);
			uvList.add(1.0f); uvList.add(0.0f);
			uvList.add(1.0f); uvList.add(1.0f);
			uvList.add(0.0f); uvList.add(1.0f);
			vertexList.add(-1.0f); vertexList.add(-1.0f); vertexList.add(0.0f);
			vertexList.add(1.0f); vertexList.add(-1.0f); vertexList.add(0.0f);
			vertexList.add(1.0f); vertexList.add(1.0f); vertexList.add(0.0f);
			vertexList.add(-1.0f); vertexList.add(1.0f); vertexList.add(0.0f);
			polygonList.add(4); polygonList.add(5); polygonList.add(6); polygonList.add(7);

			mesh.setPolygonArray(polygonArray);
		}

		setShapeTransformationMatrix(new Matrix4d(PLANE_SIZE / 2.0, 0.0, 0.0, 0.0,
												  0.0, PLANE_SIZE / 2.0, 0.0, 0.0,
												  0.0, 0.0, 1.0, 0.0,
												  0.0, 0.0, 0.0, 1.0));
		mesh.registerUser(this);
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
	 * check if this <code>GL20ResourceShapePlane</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShapePlane</code> is up to date.
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceShapePlane</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		super.update();
	}

	/**
	 * destroy this <code>GL20ResourceShapePlane</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		// unregister user by multi user mesh
		mesh.unregisterUser(this);
		mesh = null;

		super.destroy();
	}
}