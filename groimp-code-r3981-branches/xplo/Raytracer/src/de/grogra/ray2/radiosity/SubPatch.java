package de.grogra.ray2.radiosity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;

/**
 * This class describes a patch containing a triangle.
 * @author Ralf Kopsch
 */
public class SubPatch implements de.grogra.vecmath.geom.Mesh {
	private static final double oneThird = 0.333333333333333333333d;
	private Vector3d[] vertices; 
	private Map<SubPatch, FormFactor> ffMap = null;
	private Color3f radiosity = new Color3f();
	
	/**
	 * Constructor, creates a new SubPatch.
	 * @param a vertice 1
	 * @param b vertice 2
	 * @param c vertice 3
	 */
	public SubPatch(Vector3d a, Vector3d b, Vector3d c) {
		this.vertices = new Vector3d[3];
		this.vertices[0] = a;
		this.vertices[1] = b;
		this.vertices[2] = c;
		this.ffMap = new HashMap<SubPatch, FormFactor>();
	}

	/**
	 * Copy Constructor
	 * @param in the SubPatch to copy.
	 */
	public SubPatch(SubPatch in) {
		this.vertices = new Vector3d[3];
		this.vertices[0] = (Vector3d) in.vertices[0].clone();
		this.vertices[1] = (Vector3d) in.vertices[1].clone();
		this.vertices[2] = (Vector3d) in.vertices[2].clone();
		this.radiosity = in.radiosity;
	}
	
	/**
	 * Returns the form factor map.
	 * @return Returns the form factor map.
	 */
	public Map<SubPatch, FormFactor> getFFMap() {
		return this.ffMap;
	}
	
	/**
	 * Clears the form factor map.
	 */
	public void clearFFMap() {
		this.ffMap.clear();
	}
	
	/**
	 * Sets the radiosity color.
	 * @param col the radiosity color to set.
	 */
	public void setRadiosity(Color3f col) {
		this.radiosity.x = col.x;
		this.radiosity.y = col.y;
		this.radiosity.z = col.z;
	}
	
	/**
	 * Sets the radiosity color.
	 * @param x the x-value.
	 * @param y the y-value.
	 * @param z the z-value.
	 */
	public void setRadiosity(float x, float y, float z) {
		this.radiosity.x = x;
		this.radiosity.y = y;
		this.radiosity.z = z;
	}
	
	/**
	 * Returns the radiosity color.
	 * @return Returns the radiosity color.
	 */
	public Color3f getRadiosity() {
		return this.radiosity;
	}
	
	/**
	 * Sets the vertices for this patch. 
	 * @param a vertice 1 
	 * @param b vertice 2
	 * @param c vertice 3
	 */
	public void set(Vector3d a, Vector3d b, Vector3d c) {
		this.vertices[0] = a;
		this.vertices[1] = b;
		this.vertices[2] = c;
	}
	
	/**
	 * Return the center of this patch.
	 * @return Return the center of this patch.
	 */
	public Vector3d getCenter() {
		Vector3d neu = new Vector3d();
		neu.add(vertices[0]);
		neu.add(vertices[1]);
		neu.add(vertices[2]);
		neu.scale(oneThird);
		return neu;
	}
	
	/**
	 * Returns the normal vector of this patch.
	 * @return Returns the normal vector of this patch.
	 */
	public Vector3d getNormal() {
		Vector3d v = new Vector3d(vertices[2]);
		v.sub(vertices[0]);
		Vector3d w = new Vector3d(vertices[1]);
		w.sub(vertices[0]);
		v.cross(v, w);
		v.scale(1 / v.length());
		return v;
	}
	
	/**
	 * Returns true.
	 * @return
	 */
	public boolean isTriangle() {
		return true;
	}
	
	/**
	 * Returns an array of vertices.
	 * @return Returns an array of vertices.
	 */
	public Vector3d[] getVertices() {
		return this.vertices;
	}
	
	/**
	 * Moves the patch along the given vector. 
	 * @param deltaVert the move vector.
	 */
	public void move(Vector3d deltaVert) {
		for(int i=0; i<3; i++) {
			vertices[i].add(deltaVert);
		}
	} 

	/**
	 * Rotates the patch around the x-axis. 
	 * @param thetaX the rotation angle.
	 */
	public void rotateX(double thetaX) {
		double sinTheta = Math.sin( thetaX );
		double cosTheta = Math.cos( thetaX );
		
		for( int i=0; i<3; i++) {
			vertices[i].rotateX( sinTheta, cosTheta );
		}
	}

	/**
	 * Rotates the patch around the y-axis. 
	 * @param thetaY the rotation angle.
	 */
	public void rotateY(double thetaY) {
		double sinTheta = Math.sin( thetaY );
		double cosTheta = Math.cos( thetaY );
		
		for( int i=0; i<3; i++) {
			vertices[i].rotateY( sinTheta, cosTheta );
		}
	}

	/**
	 * Rotates the patch around the z-axis. 
	 * @param thetaZ the rotation angle.
	 */
	public void rotateZ(double thetaZ) {
		double sinTheta = Math.sin( thetaZ );
		double cosTheta = Math.cos( thetaZ );
		
		for( int i=0; i<3; i++) {
			vertices[i].rotateZ( sinTheta, cosTheta );
		}
	}
	
	/**
	 * Returns the maximum difference of radiosity color. 
	 * @param other another patch.
	 * @return Returns the maximum difference of radiosity color.
	 */
	public float getMaxRadDifference(SubPatch other) {
		float max1 = Math.abs(this.radiosity.x - other.getRadiosity().x);
		float max2 = Math.abs(this.radiosity.y - other.getRadiosity().y);
		float max3 = Math.abs(this.radiosity.z - other.getRadiosity().z);
		return Math.max(Math.max(max1, max2), max3);
	}
	
	
	@Override
	public String toString() {
		String str;
		str = "a=" + this.vertices[0] + "\n" +
			  "b=" + this.vertices[1] + "\n" + 
			  "c=" + this.vertices[2];
		return str;
	}

	/**
	 * Converts this patch into a mesh.
	 * @return Converts this patch into a mesh.
	 */
	public MyMeshVolume createMesh() {
		MyMeshVolume mv = new MyMeshVolume();
		mv.setMesh(this);
		Matrix4d mat = new Matrix4d();
		mat.setIdentity();
		mv.setTransformation(mat);
		return mv;
	}

	/**
	 * Checks whether, the list of deprecated patches contains this patch.
	 * @param deprecatedPatches the list of deprecated patches.
	 * @return
	 */
	public boolean ffMapContains(List<SubPatch> deprecatedPatches) {
		for (SubPatch p : deprecatedPatches) {
			if (this.ffMap.containsKey(p)) {
				return true;
			}
		}
		return false;
	}
	
	public int getMaxEdgeCount() {
		return 3;
	}

	public void getNormal(int index, Tuple3d out) {
		Vector3d v = getNormal();
		out.x = v.x;
		out.y = v.y;
		out.z = v.z;
	}

	public int getNormalCount() {
		return 1;
	}

	public int getPolygon(int index, int[] indicesOut, int[] normalsOut) {
		for (int i = 0; i < 3; i++) {
			indicesOut[i] = i;
		}
		normalsOut[0] = 0;
		return 3;
	}

	public int getPolygonCount() {
		return 1;
	}

	public void getUV(int index, Tuple2d out) {
		// not yet implemented
	}

	public void getVertex(int index, Tuple3d out) {
		out.x = vertices[index].x;
		out.y = vertices[index].y;
		out.z = vertices[index].z;
	}

	public int getVertexCount() {
		return 3;
	}

	public boolean isPolygonPlanar(int index) {
		return true;
	}

	public boolean isClosed () {
		return false;
	}
}
