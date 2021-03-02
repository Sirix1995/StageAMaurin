
package de.grogra.stl;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Box extends ObjectBase {

	@Override
	public void exportImpl (Leaf node, InnerNode transform, STLExport export)
			throws IOException
	{

		float w = (float) node.getDouble (Attributes.WIDTH);
		float h = (float) node.getDouble (Attributes.HEIGHT);
		float l = (float) node.getDouble (Attributes.LENGTH);

		double x0 = -w / 2;
		double y0 = -h / 2;
		double z0 = 0;
		double x1 = w / 2;
		double y1 = h / 2;
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

		// write object
		out.println("solid Box"+node.pathId);
		writeFace(new Vector3d(p[0].x,p[0].y,p[0].z),new Vector3d(p[1].x,p[1].y,p[1].z),new Vector3d(p[2].x,p[2].y,p[2].z));
		writeFace(new Vector3d(p[1].x,p[1].y,p[1].z),new Vector3d(p[2].x,p[2].y,p[2].z),new Vector3d(p[3].x,p[3].y,p[3].z));
		writeFace(new Vector3d(p[2].x,p[2].y,p[2].z),new Vector3d(p[3].x,p[3].y,p[3].z),new Vector3d(p[4].x,p[4].y,p[4].z));
		writeFace(new Vector3d(p[3].x,p[3].y,p[3].z),new Vector3d(p[4].x,p[4].y,p[4].z),new Vector3d(p[5].x,p[5].y,p[5].z));
		writeFace(new Vector3d(p[4].x,p[4].y,p[4].z),new Vector3d(p[5].x,p[5].y,p[5].z),new Vector3d(p[6].x,p[6].y,p[6].z));
		writeFace(new Vector3d(p[5].x,p[5].y,p[5].z),new Vector3d(p[6].x,p[6].y,p[6].z),new Vector3d(p[7].x,p[7].y,p[7].z));
		writeFace(new Vector3d(p[6].x,p[6].y,p[6].z),new Vector3d(p[7].x,p[7].y,p[7].z),new Vector3d(p[0].x,p[0].y,p[0].z));
		writeFace(new Vector3d(p[7].x,p[7].y,p[7].z),new Vector3d(p[0].x,p[0].y,p[0].z),new Vector3d(p[1].x,p[1].y,p[1].z));
		writeFace(new Vector3d(p[0].x,p[0].y,p[0].z),new Vector3d(p[2].x,p[2].y,p[2].z),new Vector3d(p[4].x,p[4].y,p[4].z));
		writeFace(new Vector3d(p[4].x,p[4].y,p[4].z),new Vector3d(p[6].x,p[6].y,p[6].z),new Vector3d(p[0].x,p[0].y,p[0].z));
		writeFace(new Vector3d(p[1].x,p[1].y,p[1].z),new Vector3d(p[3].x,p[3].y,p[3].z),new Vector3d(p[5].x,p[5].y,p[5].z));
		writeFace(new Vector3d(p[5].x,p[5].y,p[5].z),new Vector3d(p[7].x,p[7].y,p[7].z),new Vector3d(p[1].x,p[1].y,p[1].z));
		out.println("endsolid Box"+node.pathId);
	}

}

