
package de.grogra.ply;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Parallelogram extends ObjectBase {

	@Override
	public void exportImpl (Leaf node, InnerNode transform, PLYExport export)
			throws IOException
	{

		float l = (float) node.getDouble (Attributes.LENGTH);
		Vector3f a = (Vector3f) node.getObject (Attributes.AXIS);

		double x0 = -a.x;
		double x1 = a.x;
		double z1 = l;

		Matrix4d m = export.matrixStack.peek();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		m = n;

		Point4d[] p = new Point4d[] {
				new Point4d (x0, 0, 0, 1), new Point4d (x0, 0, z1, 1),
				new Point4d (x1, 0, 0, 1), new Point4d (x1, 0, z1, 1)};

		for (int i = 0; i < p.length; i++) {
			m.transform (p[i]);
		}

		//generate colour string
		getColorString(node);
		
		// write object
		writeVertices(p);
		writeFacets(new String[] {
			//to make it visible from both sides: double the facets with opposite order
			"1 0 2", "1 2 3", //one side
			"1 2 0", "1 3 2", //other side
		});
		
	}

}

