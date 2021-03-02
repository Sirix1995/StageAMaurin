package de.grogra.imp3d.msml;

import javax.vecmath.*;
//import javax.media.j3d.Transform3D;

/**
 * This class provides methods to transform coordinates from a coordinatesystem
 * to another one.
 *
 * @version	05.07.2004
 * @author	Soeren Schneider
 */

public class CoordTransformer {

		protected Matrix3f baseTransformMatrix;
		// angleDirection=-1 if the two coordinatesystems(CS) use different
		// handedness; else angleDirection=1 , its used for angle-transformations
		protected float angleDirection;


		public CoordTransformer (Matrix3f in, Matrix3f out) {
				baseTransformMatrix = new Matrix3f();
				in.invert();
				baseTransformMatrix.mul(in, out);
				if (isRightHandedCS(in)==isRightHandedCS(out)) {
						angleDirection=1;
				} else {
						angleDirection=-1;
				}
		} // CoordTransformer

	/**
	 * Test, whether a CS given by a Matrix uses righthanded orientation.
	 *
	 * @return	true, if CS uses righthanded orientation; else false
	 * @param	m3f - contains base of a perpendicular coordinate-system(CS)
	 */
		protected boolean isRightHandedCS (Matrix3f m3f) {
				Vector3f v1 = new Vector3f();
				Vector3f v2 = new Vector3f();
				Vector3f v3 = new Vector3f();
				Vector3f vcross = new Vector3f();
				m3f.getColumn(0, v1);
				m3f.getColumn(1, v2);
				m3f.getColumn(2, v3);
				vcross.cross(v1, v2);
				vcross.normalize();
				v3.normalize();
				vcross.add(v3);
				if (vcross.length() == 0) {
						return false;
				} // if
				else {
						return true;
				} // else
		} // isRightHandedCS


		public Point3d transform (Point3d in) {
				Matrix3d transformMatrix = new Matrix3d(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Point3d out = new Point3d();	
				transformMatrix.transform(in, out);
				return out;
		} // transform


		public Vector3d transform (Vector3d in) {
				Matrix3d transformMatrix = new Matrix3d(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Vector3d out = new Vector3d ();
				transformMatrix.transform(in, out);
				return out;
		} // transform


		public Point3f transform (Point3f in) {
				Matrix3f transformMatrix = new Matrix3f(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Point3f out = new Point3f ();	
				transformMatrix.transform(in, out);
				return out;
		} // transform


		public Vector3f transform (Vector3f in) {
				Matrix3f transformMatrix = new Matrix3f(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Vector3f out = new Vector3f ();
				transformMatrix.transform(in, out);
				return out;
		} // transform


		public AxisAngle4f transform (AxisAngle4f in) {
				Matrix3f transformMatrix = new Matrix3f(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Vector3f out = new Vector3f ();
				transformMatrix.transform(new Vector3f(in.x, in.y, in.z), out);
				return new AxisAngle4f(out.x, out.y, out.z, angleDirection*in.angle);
		} // transform


		public AxisAngle4d transform (AxisAngle4d in) {
				Matrix3d transformMatrix = new Matrix3d(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);
				Vector3d out = new Vector3d ();
				transformMatrix.transform(new Vector3d(in.x, in.y, in.z), out);
				return new AxisAngle4d(out.x, out.y, out.z, angleDirection*in.angle);
		} // transform
		
		
		public Matrix3f transform (Matrix3f in) {
				Matrix3f transformMatrix = new Matrix3f(baseTransformMatrix);
				Matrix3f result = new Matrix3f();
				result.mul(in, transformMatrix);

				transformMatrix = new Matrix3f(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);

				result.mul(transformMatrix, result);
				return result;
		} // transform


		public Matrix3d transform(Matrix3d in) {
				Matrix3d transformMatrix = new Matrix3d(baseTransformMatrix);
				Matrix3d result = new Matrix3d();
				result.mul(in, transformMatrix);

				transformMatrix = new Matrix3d(baseTransformMatrix);
				transformMatrix.invert(transformMatrix);

				result.mul(transformMatrix, result);
				return result;
		} // transform


		public Matrix4f transform (Matrix4f in) {
				Matrix3f in_3x3 = new Matrix3f(in.m00, in.m01, in.m02,
											   in.m10, in.m11, in.m12,
											   in.m20, in.m21, in.m22);
				Vector3f in_v3f = new  Vector3f(in.m03, in.m13, in.m23);
				in_3x3 = transform(in_3x3);
				in_v3f = transform(in_v3f);

				Matrix4f result =
						new Matrix4f(in_3x3.m00, in_3x3.m01, in_3x3.m02, in_v3f.x,
												 in_3x3.m10, in_3x3.m11, in_3x3.m12, in_v3f.y,
												 in_3x3.m20, in_3x3.m21, in_3x3.m22, in_v3f.z,
														  0,		  0,		  0,		1);
				return result;
		} // transform


		public Matrix4d transform (Matrix4d in) {
				Matrix3d in_3x3 = new Matrix3d(in.m00, in.m01, in.m02,
											   in.m10, in.m11, in.m12,
											   in.m20, in.m21, in.m22);
				Vector3d in_v3d = new  Vector3d(in.m03, in.m13, in.m23);
												in_3x3 = transform(in_3x3);
												in_v3d = transform(in_v3d);

				return new Matrix4d(in_3x3.m00, in_3x3.m01, in_3x3.m02, in_v3d.x,
									in_3x3.m10, in_3x3.m11, in_3x3.m12, in_v3d.y,
									in_3x3.m20, in_3x3.m21, in_3x3.m22, in_v3d.z,
											 0,			 0,			 0,		   1);
		} // transform

		/*
		public Transform3D transform (Transform3D in) {
			Matrix4d inMatrix = new Matrix4d();
			    in.get(inMatrix);
			inMatrix=transform(inMatrix);
			return new Transform3D(inMatrix);
		} // transform
		*/
} // CoordTransformer
