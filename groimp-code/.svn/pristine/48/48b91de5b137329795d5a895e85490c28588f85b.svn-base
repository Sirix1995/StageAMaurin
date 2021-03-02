package de.grogra.imp3d.gl20;

import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceLight;

public class GL20ResourceLightDirectional extends GL20ResourceLight {
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	public GL20ResourceLightDirectional() {
		super(GL20Resource.GL20RESOURCE_LIGHT_DIRECTIONAL);
	}

	/**
	 * check if this <code>GL20ResourceLightDirectional</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceLightDirectional</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceLightDirectional</code>
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
	 * destroy the <code>GL20ResourceLightDirectional</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		super.destroy();
	}
}