package de.grogra.ext.x3d.io;

import java.io.IOException;
import javax.vecmath.Point3d;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class X3DPlaneIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		return null;
	}

	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Element planeElement = export.getDoc().createElement("IndexedFaceSet");


		planeElement.setAttribute("coordIndex", "0 1 2 3");
		planeElement.setAttribute("solid", "false");
		Element coordElement = export.getDoc().createElement("Coordinate");
		
		Point3d p1 = new Point3d(-50, 0, -50);
		Point3d p2 = new Point3d(-50, 0, 50);
		Point3d p3 = new Point3d(50, 0, 50);
		Point3d p4 = new Point3d(50, 0, -50);
		
		coordElement.setAttribute("point", p1.x + " " + p1.y + " " + p1.z
				+ " " + p2.x + " " + p2.y + " " + p2.z
				+ " " + p3.x + " " + p3.y + " " + p3.z
				+ " " + p4.x + " " + p4.y + " " + p4.z);
		planeElement.appendChild(coordElement);
		
		parentElement.appendChild(planeElement);
	}

}
