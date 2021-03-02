
package de.grogra.stl;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Plane extends ObjectBase {

	@Override
	public void exportImpl (Leaf node, InnerNode transform, STLExport export)
			throws IOException
	{

		float w = 10;
		float l = 10;

		double x0 = -w/2d;
		double y0 = -l/2d;
		double x1 = w/2d;
		double y1 = l/2d;

		Matrix4d m = export.matrixStack.peek();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		m = n;

		Point4d[] p = new Point4d[] {
				new Point4d (x0, y0, 0, 1), new Point4d (x0, y1, 0, 1),
				new Point4d (x1, y0, 0, 1), new Point4d (x1, y1, 0, 1)};

		for (int i = 0; i < p.length; i++) {
			m.transform (p[i]);
		}

		// write object
		out.println("solid Parallelogram"+node.pathId);
		writeFace(new Vector3d(p[1].x,p[1].y,p[1].z),new Vector3d(p[0].x,p[0].y,p[0].z),new Vector3d(p[2].x,p[2].y,p[2].z));
		writeFace(new Vector3d(p[1].x,p[1].y,p[1].z),new Vector3d(p[2].x,p[2].y,p[2].z),new Vector3d(p[3].x,p[3].y,p[3].z));
		out.println("endsolid Parallelogram"+node.pathId);
	}

}

