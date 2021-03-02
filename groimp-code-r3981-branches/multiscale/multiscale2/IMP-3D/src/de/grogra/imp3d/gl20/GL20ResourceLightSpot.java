package de.grogra.imp3d.gl20;

import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceLight;

public class GL20ResourceLightSpot extends GL20ResourceLight {
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	public GL20ResourceLightSpot() {
		super(GL20Resource.GL20RESOURCE_LIGHT_SPOT);
	}

	/**
	 * check if this <code>GL20ResourceLightSpot</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceLightSpot</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceLightSpot</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		super.update();
	}

	/**
	 * destroy this <code>GL20ResourceLightSpot</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		super.destroy();
	}
}