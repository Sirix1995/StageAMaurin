
package de.grogra.stl;

import java.io.IOException;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Sphere extends ObjectBase {

	@Override
	public void exportImpl (Leaf node, InnerNode transform, STLExport export)
			throws IOException
	{
		Point3d pos = new Point3d ();
		Matrix4d m = getTransformation ();
		float r = node.getFloat (Attributes.RADIUS);

		final int uCount = 30;
		final int vCount = 15;

		// prepare polygon array for indexed geometry
		// store 3d quads in this array
		PolygonArray p = new PolygonArray ();
		p.dimension = 3;
		p.edgeCount = 4;

		// generate indexed face set
		// note that (uCount - u > 1 ? 1 : 1 - uCount) makes the quad
		// strip wrap around
		int index = 1;
		pos.set(0, 0, -r);
		m.transform (pos);
		p.vertices.push ((float)pos.x).push ((float)pos.y).push ((float)pos.z);
		for (int v = 1; v < vCount; v++)
		{
			float theta = (float) (Math.PI * ((float) v / (float) vCount - 0.5f));
			for (int u = 0; u < uCount; u++)
			{
				float phi = (float) (Math.PI * 2 * u / uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				float cosTheta = (float) Math.cos (theta);
				float sinTheta = (float) Math.sin (theta);
				float x = r * cosPhi * cosTheta;
				float y = r * sinPhi * cosTheta;
				float z = r * sinTheta;
				pos.set(x, y, z);
				m.transform (pos);
				x = (float) pos.x;
				y = (float) pos.y;
				z = (float) pos.z;
				p.vertices.push (x).push (y).push (z);
				if (v == 1)
				{
					p.polygons.push (0).push (index).push (
						index + (uCount - u > 1 ? 1 : 1 - uCount)).push (0);
				}
				else
				{
					p.polygons.push (index - uCount).push (index).push (
						index + (uCount - u > 1 ? 1 : 1 - uCount)).push (
						index - uCount + (uCount - u > 1 ? 1 : 1 - uCount));
				}
				index++;
			}
		}
		pos.set(0, 0, r);
		m.transform (pos);
		p.vertices.push ((float)pos.x).push ((float)pos.y).push ((float)pos.z);
		for (int u = 0; u < uCount; u++) {
			p.polygons.push (index - uCount+u).push (index).push (index).push (
				index - uCount + (uCount - u > 1 ? u+1 : 0));
		}

		// write object
		out.println("solid Sphere"+node.pathId);
		mesh2(p);
		out.println("endsolid Sphere"+node.pathId);
	}

}
