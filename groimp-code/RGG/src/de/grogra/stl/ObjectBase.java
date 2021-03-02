
package de.grogra.stl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.io.SceneGraphExport;
import de.grogra.imp3d.objects.SceneTree.InnerNode;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public abstract class ObjectBase implements SceneGraphExport.NodeExport {

	Matrix4d transformation;
	PrintWriter out;
	
	protected void writeFace(Vector3d a, Vector3d b, Vector3d c) {
		Vector3d normal = new Vector3d();
		Vector3d h1 = new Vector3d();
		Vector3d h2 = new Vector3d();
		h1.sub(b,a);
		h2.sub(c,a);
		normal.cross(h1, h2);
		normal.normalize();
		out.println("facet normal "+round(normal.x)+" "+round(normal.y)+" "+round(normal.z));
		out.println("  outer loop");
		out.println("    vertex "+round(a.x)+" "+round(a.y)+" "+round(a.z));
		out.println("    vertex "+round(b.x)+" "+round(b.y)+" "+round(b.z));
		out.println("    vertex "+round(c.x)+" "+round(c.y)+" "+round(c.z));
		out.println("  endloop");
		out.println("endfacet");
	}

	@Override
	public void export (Leaf node, InnerNode transform, SceneGraphExport sge) throws IOException {
		// convert to STLExport
		STLExport export = (STLExport) sge;

		// obtain output writer
		out = export.out;

		// obtain transformation matrix for this node
		Matrix4d m = export.matrixStack.peek ();
		Matrix4d n = new Matrix4d ();
		if (transform != null) {
			transform.transform (m, n);
		} else {
			n.set (m);
		}
		transformation = n;

		// call implementation export function
		exportImpl (node, transform, export);
	}

	abstract void exportImpl (Leaf node, InnerNode transform, STLExport export) throws IOException;

	Matrix4d getTransformation () {
		return transformation;
	}

	// round to 5 decimal digits
	static double round (double d) {
		return (double) Math.round (d * 100000) / 100000;
	}

	void mesh1 (PolygonArray p) {
		if (p.polygons.size == 0) return;
		int v1,v2,v3,v4;
		// output indices
		for (int i = 0; i < p.polygons.size (); i += p.edgeCount) {
			v1 = p.polygons.get (i + 0);
			v2 = p.polygons.get (i + 1);
			v3 = p.polygons.get (i + 2);
			v4 = p.polygons.get (i + 3);
			writeFace(
				new Vector3d(p.vertices.get(v1*p.dimension+0),p.vertices.get(v1*p.dimension+1),p.vertices.get(v1*p.dimension+2)),
				new Vector3d(p.vertices.get(v2*p.dimension+0),p.vertices.get(v2*p.dimension+1),p.vertices.get(v2*p.dimension+2)),
				new Vector3d(p.vertices.get(v3*p.dimension+0),p.vertices.get(v3*p.dimension+1),p.vertices.get(v3*p.dimension+2))
			);
		}
	}

	void mesh2 (PolygonArray p) {
		if (p.polygons.size == 0) return;
		int v1,v2,v3,v4;
		// output indices
		for (int i = 0; i < p.polygons.size (); i += p.edgeCount) {
			v1 = p.polygons.get (i + 0);
			v2 = p.polygons.get (i + 1);
			v3 = p.polygons.get (i + 2);
			v4 = p.polygons.get (i + 3);
			writeFace(
				new Vector3d(p.vertices.get(v1*p.dimension+0),p.vertices.get(v1*p.dimension+1),p.vertices.get(v1*p.dimension+2)),
				new Vector3d(p.vertices.get(v2*p.dimension+0),p.vertices.get(v2*p.dimension+1),p.vertices.get(v2*p.dimension+2)),
				new Vector3d(p.vertices.get(v3*p.dimension+0),p.vertices.get(v3*p.dimension+1),p.vertices.get(v3*p.dimension+2))
			);
			writeFace(
				new Vector3d(p.vertices.get(v1*p.dimension+0),p.vertices.get(v1*p.dimension+1),p.vertices.get(v1*p.dimension+2)),
				new Vector3d(p.vertices.get(v3*p.dimension+0),p.vertices.get(v3*p.dimension+1),p.vertices.get(v3*p.dimension+2)),
				new Vector3d(p.vertices.get(v4*p.dimension+0),p.vertices.get(v4*p.dimension+1),p.vertices.get(v4*p.dimension+2))
			);
		}
	}
}
