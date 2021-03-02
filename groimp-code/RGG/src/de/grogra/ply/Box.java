
package de.grogra.ply;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Box extends ObjectBase {

	@Override
	public void exportImpl (Leaf node, InnerNode transform, PLYExport export)
			throws IOException
	{

		float w = (float) node.getDouble (Attributes.WIDTH);
		float h = (float) node.getDouble (Attributes.HEIGHT);
		float l = (float) node.getDouble (Attributes.LENGTH);

		double x0 = -w / 2d;
		double y0 = -h / 2d;
		double z0 = 0;
		double x1 = w / 2d;
		double y1 = h / 2d;
		double z1 = l;

		Matrix4d m = export.matrixStack.peek();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		m = n;

		Point4d[] p = new Point4d[] {new Point4d (x0, y0, z0, 1),
				new Point4d (x0, y0, z1, 1), new Point4d (x1, y0, z0, 1),
				new Point4d (x1, y0, z1, 1), new Point4d (x1, y1, z0, 1),
				new Point4d (x1, y1, z1, 1), new Point4d (x0, y1, z0, 1),
				new Point4d (x0, y1, z1, 1)};

		for (int i = 0; i < p.length; i++) {
			m.transform (p[i]);
		}

		//generate colour string
		getColorString(node);
		
		// write object
		writeVertices(p);
		writeFacets(new String[] {
			"1 0 2",
			"1 2 3",
			"3 2 4",
			"3 4 5",
			"5 4 6",
			"5 6 7",
			"7 6 0",
			"7 0 1",
			"4 2 0",
			"0 6 4",
			"1 3 5",
			"5 7 1"});
		
	}

}

