package de.grogra.ext.x3d.io;

import java.io.IOException;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.AxisObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.vecmath.Math2;

public class X3DParallelogramIO extends AxisObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		return null;
	}

	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		// Rectangle2D doesn't work in many x3d viewers, so create a indexed face set
		
		float length = (float) node.getDouble (de.grogra.imp.objects.Attributes.LENGTH);
		
		Matrix4d t = new Matrix4d();
		Math2.makeAffine(t);
		Vector3f a1 = new Vector3f();
		Vector3f a2 = new Vector3f();
		a1.set ((Vector3f) node.getObject(a2, de.grogra.imp3d.objects.Attributes.AXIS));
		float tmp_y = a1.y;
		a1.y = a1.z;
		a1.z = -tmp_y;

		t.m03 = -a1.x;
		t.m13 = -a1.y;
		t.m23 = -a1.z;
		a1.scale(2f);
		a2.set(0, length, 0);
		Matrix3f m = new Matrix3f();
		m.setColumn(0, a1);
		m.setColumn(2, a2);
		a1.cross(a1, a2);
		
		a1.x = (a1.x < 0) ? (float) -Math.sqrt(Math.abs(a1.x)) : (float) Math.sqrt(a1.x);
		a1.y = (a1.y < 0) ? (float) -Math.sqrt(Math.abs(a1.y)) : (float) Math.sqrt(a1.y);
		a1.z = (a1.z < 0) ? (float) -Math.sqrt(Math.abs(a1.z)) : (float) Math.sqrt(a1.z);
		
		m.setColumn(1, a1);
		t.setRotationScale (m);
		
		Point3d p1 = new Point3d(0, 0,  0);
		Point3d p2 = new Point3d(0, 0, -1);
		Point3d p3 = new Point3d(1, 0, -1);
		Point3d p4 = new Point3d(1, 0,  0);
		
		t.transform(p1);
		t.transform(p2);
		t.transform(p3);
		t.transform(p4);
		
		Element parallelogramElement = export.getDoc().createElement("IndexedFaceSet");
		parallelogramElement.setAttribute("coordIndex", "0 1 2 3");
		parallelogramElement.setAttribute("solid", "false");
		Element coordElement = export.getDoc().createElement("Coordinate");
		coordElement.setAttribute("point", p1.x + " " + (p1.y + length/2) + " " + p1.z
				+ " " + p2.x + " " + (p2.y + length/2) + " " + p2.z
				+ " " + p3.x + " " + (p3.y + length/2) + " " + p3.z
				+ " " + p4.x + " " + (p4.y + length/2) + " " + p4.z);
		parallelogramElement.appendChild(coordElement);
		
		parentElement.appendChild(parallelogramElement);
	}

}
