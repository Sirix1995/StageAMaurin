package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.shading.Light;
import de.grogra.vecmath.Matrix34d;

/**
 * Wrapper class to hold a light with its corresponding transformation.
 * @author Konni Hartmann
 */
public class LightPos /* implements AttributeChangeListener */ {
	
	public LightPos(Light light) {
		setLight(light);	
	}

	public LightPos(Light light, Matrix4d lightToWorld) {
		setLight(light);
		setLightPos(lightToWorld);
	}
	
	Light light = null;
	Point4d lightPos = new Point4d();
	Vector3d lightDir = new Vector3d();
	Matrix4d lightTransform = new Matrix4d();
	
	public Light getLight() {
		return light;
	}
	
	public Point4d getLightPos() {
		return lightPos;
	}
	
	public void setLight(Light light) {
		this.light = light;
	}
		
	/**
	 * Rotate Matrix to correspond to a ViewToWorld Matrix by rotating it
	 * by ¹ around the x-Axis 
	 * @param rotateRight
	 */
	void setRotateFront(Matrix4d m) {
		m.m01 *= -1; m.m02 *= -1;
		m.m11 *= -1; m.m12 *= -1;
		m.m21 *= -1; m.m22 *= -1;
		m.m31 *= -1; m.m32 *= -1;
	}
	
	public void setLightPos(Matrix4d lightToWorld) {
		if(lightToWorld == null)
			return;
		lightTransform.set(lightToWorld);
		updateDerived();
		if(!isArea())
			setRotateFront(lightTransform);
	}
	
	boolean isArea() {
		return light.getLightType() == Light.AREA;
	}
	
	private void updateDerived() {
		if(isArea()) {
			lightPos.set(0, 0,((Parallelogram)light).getLength()*.5, 1);
		} else lightPos.set(0, 0, 0, 1);
		
		lightTransform.transform(lightPos);
		if(isArea())
			lightDir.set(-lightTransform.m22,
					lightTransform.m12, lightTransform.m02);
		else
		lightDir.set(lightTransform.m02,
				lightTransform.m12, lightTransform.m22);

	}
	
	public void setLightDir(Vector3d lightDir) {
		this.lightDir.set(lightDir);
	}
	
	public Vector3d getLightDir() {
		return lightDir;
	}	
	
	public Matrix4d getLightTransform() { 
		return lightTransform; 
	}
	
	public void setLightTransform(Matrix4d lightTransform) {
		this.lightTransform = lightTransform;
	}
	
	/**
	 * @param m
	 * @return
	 */
	double[] toGLMatrix (Matrix4d m)
	{
		double matrixArray[] = new double[16];
		matrixArray[0] = m.m00;
		matrixArray[1] = m.m10;
		matrixArray[2] = m.m20;
		matrixArray[3] = m.m30;
		matrixArray[4] = m.m01;
		matrixArray[5] = m.m11;
		matrixArray[6] = m.m21;
		matrixArray[7] = m.m31;
		matrixArray[8] = m.m02;
		matrixArray[9] = m.m12;
		matrixArray[10] = m.m22;
		matrixArray[11] = m.m32;
		matrixArray[12] = m.m03;
		matrixArray[13] = m.m13;
		matrixArray[14] = m.m23;
		matrixArray[15] = m.m33;
		return matrixArray;
	}
	
	public void setupView(GL gl) {
		gl.glPushMatrix();
		gl.glMatrixMode (GL.GL_PROJECTION);
		
		Matrix4d m = new Matrix4d (lightTransform);
		double aspect = 1;
		m.mul (new Matrix4d (1, 0, 0, 0, 0, aspect, 0, 0, 0, 0, 1, 0, 0, 0, 0,
			1));
		gl.glLoadMatrixd (toGLMatrix (m), 0);
		gl.glMatrixMode (GL.GL_MODELVIEW);
	}

	void updateMat(Matrix4d local, Matrix34d val) {
		if(val == null)
			return;
		local.m00 = val.m00;
		local.m01 = val.m01;
		local.m02 = val.m02;
		local.m03 = val.m03;
		local.m10 = val.m10;
		local.m11 = val.m11;
		local.m12 = val.m12;
		local.m13 = val.m13;
		local.m20 = val.m20;
		local.m21 = val.m21;
		local.m22 = val.m22;
		local.m23 = val.m23;		
	}
	
//	public void attributeChanged(AttributeChangeEvent event) {
//		Attribute<?> att = event.getAttribute();
//		if(att == GlobalTransformation.ATTRIBUTE) {
//			Matrix34d val = ((Matrix34dPair)att.get(event.getObject(), true, event.getGraphState())).get(false);
//			setLightPos(val);
//		} else if(att == de.grogra.imp3d.objects.Attributes.LIGHT)  {
//			Light val = (Light)att.get(event.getObject(), true, event.getGraphState());
//			setLight(val);
//		}
//	}
}
