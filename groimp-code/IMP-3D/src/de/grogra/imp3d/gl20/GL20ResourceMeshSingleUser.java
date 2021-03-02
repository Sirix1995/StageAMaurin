package de.grogra.imp3d.gl20;

import de.grogra.imp3d.gl20.GL20ResourceMesh;

public class GL20ResourceMeshSingleUser extends GL20ResourceMesh {
	public GL20ResourceMeshSingleUser() {
		super(GL20Resource.GL20RESOURCE_MESH_SINGLE_USER);
	}

	/**
	 * check if this <code>GL20ResourceMeshSingleUser</code> is up to date.
	 *
	 * @return <code>true</code> - this <code>GL20ResourceMeshSingleUser</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		return super.isUpToDate();
	}

	/**
	 * update the state of this <code>GL20ResourceMeshSingleUser</code>.
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void update() {
		super.update();
	}

	/**
	 * destroy this <code>GL20ResourceMeshSingleUser</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		super.destroy();
	}
}