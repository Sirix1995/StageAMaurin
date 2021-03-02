package de.grogra.ray2.radiosity;

import javax.vecmath.Tuple3d;

/**
 * This class extends the <i>javax.vecmath.Vector3d</i> class and adds same rotation functions.
 * @author Ralf Kopsch 
 */
public class Vector3d extends javax.vecmath.Vector3d {
	private static double M_05PI = (float) (Math.PI / 2d);
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Vector3D.
	 */
	public Vector3d() {
		super();
	}

	/**
	 * A copy constructor.
	 * @param v the vector to clone.
	 */
	public Vector3d(Tuple3d v) {
		super(v);
	}

	/**
	 * A copy constructor.
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 * @param z the z-coordinate.
	 */
	public Vector3d(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Rotates the vector around the x-axis.
	 * @param sinTheta the sinus angle.
	 * @param cosTheta the cosinus angle.
	 */
	public void rotateX(double sinTheta, double cosTheta) {
		double oldY = y;
		y = y * cosTheta - z * sinTheta;
		z = oldY * sinTheta + z * cosTheta;
	}

	/**
	 * Rotates the vector around the y-axis.
	 * @param sinTheta the sinus angle.
	 * @param cosTheta the cosinus angle.
	 */
	public void rotateY(double sinTheta, double cosTheta) {
		double oldX = x;
		x = cosTheta * x + sinTheta * z;
		z = -sinTheta * oldX + cosTheta * z;
	}

	/**
	 * Rotates the vector around the z-axis.
	 * @param sinTheta the sinus angle.
	 * @param cosTheta the cosinus angle.
	 */
	public void rotateZ(double sinTheta, double cosTheta) {
		double oldX = x;
		x = cosTheta * x - sinTheta * y;
		y = sinTheta * oldX + cosTheta * y;
	}

	/**
	 * Pair of angles. 
	 * @author Ralf Kopsch
	 */
	public class Theta {
		public double thetaX = 0;
		public double thetaY = 0;
	}

	/**
	 * Pre calculates the rotation around the z axis. 
	 * @return the calculated angles.
	 */
	public Theta calcRotationToZ() {
		Vector3d tempNorm = new Vector3d(x, y, z);
		double tanThetaX = 0;
		Theta T = new Theta();
		T.thetaX = 0;

		if (tempNorm.z == 0) {
			if (tempNorm.y > 0) {
				T.thetaX = Vector3d.M_05PI;
			} else {
				T.thetaX = -Vector3d.M_05PI;
			}
		} else {
			tanThetaX = tempNorm.y / -tempNorm.z;
			T.thetaX = -Math.atan(tanThetaX);

			if (tempNorm.z < 0) {
				T.thetaX += Math.PI;
			}
		}
		double cosTheta = Math.cos(T.thetaX);
		double sinTheta = Math.sin(T.thetaX);
		tempNorm.rotateX(sinTheta, cosTheta);		

		double tanThetaY = 0;

		T.thetaY = 0;

		if (tempNorm.z == 0) {
			if (tempNorm.x > 0) {
				T.thetaY = -Vector3d.M_05PI;
			} else {
				T.thetaY = Vector3d.M_05PI;
			}
		} else {
			tanThetaY = tempNorm.x / tempNorm.z;
			T.thetaY = -Math.atan(tanThetaY);
			if (tempNorm.z < 0) {
				T.thetaY += Math.PI;
			}
		}
		return T;
	}

	@Override
	public Object clone() {
		return new Vector3d(this);
	}

}
