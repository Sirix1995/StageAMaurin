
package de.grogra.ply;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Cylinder extends ObjectBase {

	@Override
	void exportImpl (Leaf node, InnerNode transform, PLYExport export)
			throws IOException
	{
		Point3d pos = new Point3d ();
		Matrix4d m = getTransformation ();
		float r = node.getFloat (Attributes.RADIUS);
		double l = node.getDouble (Attributes.LENGTH);

		// prepare polygon array for indexed geometry
		// store 3d quads in this array
		PolygonArray p = new PolygonArray ();
		p.dimension = 3;
		p.edgeCount = 4;

		final int uCount = 30;

		// generate geometry
		int index = 2;
		pos.set (0, 0, 0);
		m.transform (pos);
		p.vertices.push ((float) pos.x).push ((float) pos.y).push ((float) pos.z);
		pos.set (0, 0, l);
		m.transform (pos);
		p.vertices.push ((float) pos.x).push ((float) pos.y).push ((float) pos.z);
		for (int u = 0; u < uCount; u++) {
			float phi = (float) (Math.PI * 2 * u / uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);

			Point3d p0 = new Point3d (cosPhi, sinPhi, 0);
			p0.scale (r);
			m.transform (p0);
			Point3d p1 = new Point3d (cosPhi, sinPhi, l/r);
			p1.scale (r);
			m.transform (p1);

			p.vertices.push ((float) p0.x).push ((float) p0.y).push ((float) p0.z);
			p.vertices.push ((float) p1.x).push ((float) p1.y).push ((float) p1.z);

			// bottom face
			p.polygons.push (0).push (index).push (
				index + 2 * (uCount - u > 1 ? 1 : 1 - uCount)).push (0);
			// top face
			p.polygons.push (1).push (index + 1 + 2 * (uCount - u > 1 ? 1 : 1 - uCount)).push (
				index + 1 ).push (1);
			// mantle face
			p.polygons.push (index).push (index + 1).push (
				index + 3 + 2 * (uCount - u > 1 ? 0 : 0 - uCount)).push (
				index + 2 + 2 * (uCount - u > 1 ? 0 : 0 - uCount));

			index += 2;
		}

		// write object
		mesh2(p, node, true);
	}

}
