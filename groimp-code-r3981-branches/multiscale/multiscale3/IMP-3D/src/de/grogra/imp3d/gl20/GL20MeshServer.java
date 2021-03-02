package de.grogra.imp3d.gl20;

import java.util.ArrayList;

import de.grogra.imp3d.gl20.GL20ResourceMeshMultiUser;

public class GL20MeshServer {
	/**
	 * the server instance of this <code>GL20MeshServer</code>
	 */
	private static GL20MeshServer singleton = new GL20MeshServer();

	/**
	 * a list of all <code>GL20ResourceMeshMultiUser</code>
	 */
	private ArrayList<GL20ResourceMesh> meshList = new ArrayList<GL20ResourceMesh>();

	/**
	 * return the server instance of this <code>GL20MeshServer</code>
	 *
	 * @return the server instance
	 */
	final public static GL20MeshServer getInstance() {
		return singleton;
	}

	private GL20MeshServer() {
	}

	/**
	 * get a <code>GL20ResourceMeshMultiUser</code> by a given <code>name</code>.
	 * if no <code>GL20ResourceMeshMultiUser</code> was found with given <code>name</code>
	 * <code>null</code> will returned
	 *
	 * @param name the name of the <code>GL20ResourceMeshMultiUser</code>
	 * @return <code>null</code> - no <code>GL20ResourceMeshMultiUser</code> is registered,
	 * otherwise - the <code>GL20ResourceMeshMultiUser</code>
	 */
	final public GL20ResourceMeshMultiUser getMultiUserMeshByName(String name) {
		GL20ResourceMeshMultiUser returnValue = null;

		final int meshCount = meshList.size();
		int meshIndex = 0;
		while (meshIndex < meshCount) {
			GL20ResourceMeshMultiUser tempMesh;
			tempMesh = (GL20ResourceMeshMultiUser)meshList.get(meshIndex);
			if (tempMesh.getName().equals(name) == true) {
				returnValue = tempMesh;
				break;
			}
			meshIndex++;
		}

		return returnValue;
	}

	/**
	 * register a <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @param mesh the <code>GL20ResourceMeshMultiUser</code> that should registered
	 */
	final public void registerMultiUserMesh(GL20ResourceMeshMultiUser mesh) {
		if (mesh != null) {
			if (meshList.contains(mesh) == false)
				meshList.add(mesh);
		}
	}

	/**
	 * unregister a <code>GL20ResourceMeshMultiUser</code>
	 *
	 * @param mesh the <code>GL20ResourceMeshMultiUser</code> that should unregistered
	 */
	final public void unregisterMultiUserMesh(GL20ResourceMeshMultiUser mesh) {
		if (mesh != null) {
			meshList.remove(mesh);
		}
	}

	/**
	 * update all <code>GL20ResourceMeshMultiUser</code>
	 */
	final public void updateAllMultiUserMeshes() {
		final int meshCount = meshList.size();
		for (int meshIndex=0;meshIndex < meshCount;meshIndex++) {
			GL20ResourceMeshMultiUser tempMesh = (GL20ResourceMeshMultiUser)meshList.get(meshIndex);
			if (tempMesh.isUpToDate() == false) {
				System.out.println(tempMesh.getName() + " will be updated because its not up to date!");
				tempMesh.update();
			}
		}
	}
}