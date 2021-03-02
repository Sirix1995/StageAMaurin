
package de.grogra.tex;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Cone extends ObjectBase {

	@Override
	void exportImpl (Leaf node, InnerNode transform, TEXExport export)
			throws IOException
	{
		Point3d pos = new Point3d ();
		Matrix4d m = getTransformation ();
		float r = node.getFloat (Attributes.RADIUS);
		double l = node.getDouble (Attributes.LENGTH);

		// prepare polygon array for indexed geometry
		// store 3d triangles in this array
		PolygonArray p = new PolygonArray ();
		p.dimension = 3;
		p.edgeCount = 3;

		final int uCount = 15;

		// generate geometry
		int index = 2;
		pos.set(0, 0, 0);
		m.transform (pos);
		p.vertices.push ((float)pos.x).push ((float)pos.y).push ((float)pos.z);
		pos.set(0, 0, l);
		m.transform (pos);
		p.vertices.push ((float)pos.x).push ((float)pos.y).push ((float)pos.z);
		for (int u = 0; u < uCount; u++) {
			float phi = (float) (Math.PI * 2 * u / uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);
			pos.set (cosPhi, sinPhi, 0);
			pos.scale (r);
			m.transform (pos);
			p.vertices.push ((float) pos.x).push ((float) pos.y).push ((float) pos.z);
			p.polygons.push (0).push (index + (uCount - u > 1 ? 1 : 1 - uCount)).push (index);
			p.polygons.push (index).push (index + (uCount - u > 1 ? 1 : 1 - uCount)).push (1);
			index++;
		}

		// write object
		mesh2(p, node, false);
	}

}
