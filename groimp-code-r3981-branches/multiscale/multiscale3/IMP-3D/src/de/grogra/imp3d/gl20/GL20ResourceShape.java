package de.grogra.imp3d.gl20;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20GfxServer;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceShader;

public abstract class GL20ResourceShape extends GL20Resource {
	/**
	 * worldMatrix attribute bit
	 */
	final private static int WORLD_MATRIX = 0x1;

	/**
	 * shapeMatrix attribute bit
	 */
	final private static int SHAPE_MATRIX = 0x2;
	
	/**
	 * shader attribute bit
	 */
	final private static int SHADER = 0x4;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * the world transformation matrix of this <code>GL20ResourceShape</code>
	 * worldMatrix attribute
	 */
	private Matrix4d worldMatrix = new Matrix4d(GL20Const.identityMatrix4d);

	/**
	 * the shape transformation matrix of this <code>GL20ResourceShape</code>
	 * shapeMatrix attribute
	 */
	private Matrix4d shapeMatrix = new Matrix4d(GL20Const.identityMatrix4d);
	
	/**
	 * the shader of this <code>GL20ResourceShape</code>
	 * shader attribute
	 */
	private GL20ResourceShader shader = null;

	/**
	 * the final transformation matrix of this <code>GL20ResourceShape</code>
	 */
	private Matrix4d finalMatrix = new Matrix4d(GL20Const.identityMatrix4d);

	protected GL20ResourceShape(int resourceClassType) {
		super(resourceClassType);

		assert (resourceClassType & GL20Resource.GL20RESOURCE_CLASS_MASK) == GL20Resource.GL20RESOURCE_CLASS_SHAPE;
	}

	/**
	 * set the transformation matrix of this <code>GL20ResourceShape</code>
	 *
	 * @param matrix the transformation matrix
	 */
	final public void setWorldTransformationMatrix(Matrix4d matrix) {
		if (this.worldMatrix.equals(matrix) == false) {
			this.worldMatrix.set(matrix);
			changeMask |= WORLD_MATRIX;
		}
	}

	/**
	 * get the transformation matrix of this <code>GL20ResourceShape</code>
	 *
	 * @return the transformation matrix
	 */
	final public Matrix4d getWorldTransformationMatrix() {
		return worldMatrix;
	}

	/**
	 * set the shape transformation matrix of this <code>GL20ResourceShape</code>
	 *
	 * @param matrix the shape transformation matrix
	 */
	final public void setShapeTransformationMatrix(Matrix4d matrix) {
		if (this.shapeMatrix.equals(matrix) == false) {
			this.shapeMatrix.set(matrix);
			changeMask |= SHAPE_MATRIX;
		}
	}

	/**
	 * get the shape transformation matrix of this <code>GL20ResourceShape</code>
	 *
	 * @return the shape transformation matrix
	 */
	final public Matrix4d getShapeTransformationMatrix() {
		return shapeMatrix;
	}
	
	/**
	 * set the shader of this <code>GL20ResourceShape</code>
	 * 
	 * @param shader the shader
	 */
	final public void setShader(GL20ResourceShader shader) {
		if (this.shader != shader) {
			if (this.shader != null)
				this.shader.unregisterUser();
			
			this.shader = shader;
			if (this.shader != null)
				this.shader.registerUser();
			changeMask |= SHADER;			
		}
	}
	
	/**
	 * get the shader of this <code>GL20ResourceShape</code>
	 * 
	 * @return the shader of this <code>GL20ResourceShape</code>
	 */
	final public GL20ResourceShader getShader() {
		return shader;
	}

	/**
	 * tell this <code>GL20ResourceShape</code> that it should apply the
	 * geometry to the <code>GL20GfxServer</code>
	 */
	public void applyGeometry() {
		GL20GfxServer.getInstance().setWorldTransformationMatrix(finalMatrix);
	}

	/**
	 * check if this <code>GL20ResourceShape</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceShape</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		// TODO check if shader is up to date
		if ((changeMask != 0))
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceShape</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		if (changeMask != 0) {
			// world and/or shape matrix was changed
			finalMatrix.set(worldMatrix);
			finalMatrix.mul(shapeMatrix);
			changeMask = 0;
		}
		
		//if (shader != null)
			// apply changes to <code>GL20ResourceShader</code>
		//	shader.update();

		// apply changes to <code>GL20Resource</code>
		super.update();
	}

	/**
	 * destroy the resource
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		if (shader != null)
			// destroy the shader
			shader.unregisterUser();
		super.destroy();
	}
}