package de.grogra.imp3d.gl20;

import java.util.ArrayList;

import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20MeshServer;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.gl20.GL20ResourceMesh;

public class GL20ResourceMeshMultiUser extends GL20ResourceMesh {
	/**
	 * all changes that was made since last update
	 */
	private int changeMask = GL20Const.ALL_CHANGED;

	/**
	 * a list with all users of this <code>GL20ResourceMeshMultiUser</code>
	 */
	private ArrayList<GL20Resource> userList = new ArrayList<GL20Resource>();

	/**
	 * the name of this <code>GL20ResourceMeshMultiUser</code>
	 */
	private String name;

	public GL20ResourceMeshMultiUser(String name) {
		super(GL20Resource.GL20RESOURCE_MESH_MULTI_USER);

		this.name = new String(name);
		GL20MeshServer.getInstance().registerMultiUserMesh(this);
	}

	/**
	 * get the name of this <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @return name of this <code>GL20ResourceMeshMultiUser</code>
	 */
	final public String getName() {
		return name;
	}

	/**
	 * add a user to this <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @param user the <code>GL20Resource</code> that use this <code>GL20ResourceMeshMultiUser</code>
	 */
	final public void registerUser(GL20Resource user) {
		if (user != null) {
			if (userList.contains(user) == false)
				userList.add(user);
		}
	}

	/**
	 * remove a user from this <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @param user the <code>GL20Resource</code> that should removed
	 */
	final public void unregisterUser(GL20Resource user) {
		if (user != null) {
			userList.remove(user);
		}
	}

	/**
	 * check if this <code>GL20ResourceMeshMultiUser</code> is up to date
	 *
	 * @return <code>true</code> - this <code>GL20ResourceMeshMultiUser</code> is up to date
	 * @see <code>GL20Resource</code>
	 */
	public boolean isUpToDate() {
		if (changeMask != 0)
			return false;
		else
			return super.isUpToDate();

	}

	/**
	 * update the state of this <code>GL20ResourceMeshMultiUser</code>
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
	 * destroy this <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @see <code>GL20Resource</code>
	 */
	public void destroy() {
		GL20MeshServer.getInstance().unregisterMultiUserMesh(this);

		super.destroy();
	}
}