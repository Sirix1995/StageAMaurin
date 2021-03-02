package de.grogra.math.convexhull;

/**
 * Maintains a single-linked list of faces for use by QuickHull3D
 */
class FaceList {
	private Face head;
	private Face tail;

	/**
	 * Adds a vertex to the end of this list.
	 */
	public void add(Face vtx) {
		if (head == null) {
			head = vtx;
		} else {
			tail.next = vtx;
		}
		vtx.next = null;
		tail = vtx;
	}

	/**
	 * Clears this list.
	 */
	public void clear() {
		head = tail = null;
	}

	public Face first() {
		return head;
	}

	/**
	 * Returns true if this list is empty.
	 */
	public boolean isEmpty() {
		return head == null;
	}
}
