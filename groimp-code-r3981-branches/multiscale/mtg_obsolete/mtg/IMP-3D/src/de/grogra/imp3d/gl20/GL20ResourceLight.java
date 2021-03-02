package de.grogra.imp3d.gl20;

import java.util.ArrayList;

import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4d;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20Resource;

public class GL20ResourceLight extends GL20Resource {
	final private static ArrayList<GL20ResourceLight> lights = new ArrayList<GL20ResourceLight>();
	
	/**
	 * color attribute bit
	 */
	final private static int COLOR = 0x1;

	/**
	 * worldMatrix attribute bit
	 */
	final private static int WORLD_MATRIX = 0x2;

	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * color attribute
	 */
	private Vector3f color = new Vector3f();

	/**
	 * worldMatrix attribute
	 */
	private Matrix4d worldMatrix = new Matrix4d(GL20Const.identityMatrix4d);
	
	final public static void registerLight(GL20ResourceLight light) {
		if (lights.contains(light) == false)
			lights.add(light);
	}
	
	final public static void unregisterLight(GL20ResourceLight light) {
		lights.remove(light);
	}

	protected GL20ResourceLight(int resourceClassType) {
		super(resourceClassType);

		assert (resourceClassType & GL20Resource.GL20RESOURCE_CLASS_MASK) == GL20Resource.GL20RESOURCE_CLASS_LIGHT;
		
		GL20ResourceLight.registerLight(this);
	}

	/**
	 * set the color of this <code>GL20ResourceLight</code>
	 *
	 * @param color the color
	 */
	final public void setColor(Vector3f color) {
		if (this.color.equals(color) == false) {
			this.color.set(color);
			changeMask |= COLOR;
		}
	}

	/**
	 * get the color of this <code>GL20ResourceLight</code>
	 *
	 * @return the color
	 */
	final public Vector3f getColor() {
		return color;
	}

	/**
	 * set world transformation matrix of this <code>GL20ResourceLight</code>
	 *
	 * @param worldMatrix the world transformation matrix
	 */
	final public void setWorldTransformationMatrix(Matrix4d worldMatrix) {
		if (this.worldMatrix.equals(worldMatrix) == false) {
			this.worldMatrix.set(worldMatrix);
			changeMask |= WORLD_MATRIX;
		}
	}

	/**
	 * get the world transformation matrix of this <code>GL20ResourceLight</code>
	 *
	 * @return the world transformation matrix
	 */
	final public Matrix4d getWorldTransformationMatrix() {
		return worldMatrix;
	}

	/**
	 * check if this <code>GL20ResourceLight</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceLight</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceLight</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		if (changeMask != 0) {
			changeMask = 0;
		}
		super.update();
	}

	/**
	 * destroy the resource
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destory() {
		unregisterLight(this);
		super.destroy();
	}
	
	public static void setupLights() {
		
	}
}