package de.grogra.imp3d.glsl.utility;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.ParallelProjection;
import de.grogra.imp3d.PerspectiveProjection;
import de.grogra.imp3d.glsl.GLSLDisplay;

public class FrustumCullingTester {

	boolean isPerspective;
	
	float angleX;
	float near;
	float far;
	float fov;
	float sphereFactorX;
	float sphereFactorY;
	float ratio;
	float width;
	float height;
	
	Vector3d X = new Vector3d(1, 0, 0);
	Vector3d Y = new Vector3d(0, 1, 0);
	Vector3d Z = new Vector3d(0, 0, -1);
	
	
	public void setupViewFrustum( GLSLDisplay disp ) {
		Camera cam = disp.getView3D().getCamera();
		this.far = cam.getZFar();
		this.near = cam.getZNear();
		this.ratio = cam.getProjection().getAspect();
		
		if(cam.getProjection() instanceof ParallelProjection) {
			ParallelProjection proj = ((ParallelProjection)cam.getProjection());
			isPerspective= false;
			width = proj.getWidth();
			height = width * ratio;
		} else if (cam.getProjection() instanceof PerspectiveProjection)
		{
			PerspectiveProjection proj =  ((PerspectiveProjection)cam.getProjection());
			isPerspective = true;
			fov = PerspectiveProjection.clampFieldOfView(proj.getFieldOfView());
			double tang = Math.tan( fov );
			height = (float)(near * tang);
			width = height * ratio;			
			sphereFactorX = (float)(1.0/Math.cos(fov));
			double angley = Math.atan(tang*ratio);
			sphereFactorY = (float)(1.0/Math.cos(angley));
		}
		
	}
	
	boolean isPointInFrustum(Matrix4d mat) {
		double aux;
		
		if(-mat.m32 > far || -mat.m32 < near)
			return false;
		
		aux = -mat.m32 * fov;
		if(mat.m31 > aux || mat.m31 < -aux)
			return false;
		
		aux *= ratio;
		if(mat.m30 > aux || mat.m30 < -aux)
			return false;
		
		return true;
	}
	
	boolean isSphereInFrustum(Matrix4d mat, float radius) {
		
		if(radius < 0.0)
			return isPointInFrustum(mat);
		
		double az, d;
//		boolean isInside = false;
		
		az = -mat.m33;
		if(az > far + radius || az < near - radius)
			return false;
//		if(az > far - radius || az < near + radius)
//			isInside = true;
		
		d = sphereFactorY * radius;
		az *= fov;
		if (mat.m31 > az+d || mat.m31 < -az-d)
			return false;
//		if (mat.m31 > az-d || mat.m31 < -az+d)
//			isInside = true;

		az *= ratio;
		d = sphereFactorX * radius;
		if (mat.m30 > az+d || mat.m30 < -az-d)
			return false;
//		if (mat.m30 > az-d || mat.m30 < -az+d)
//			isInside = true;		
//		return isInside;
		return true;
	}
}
