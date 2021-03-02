
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Cylinder extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		float r = node.getFloat (Attributes.RADIUS);
		double l = node.getDouble (Attributes.LENGTH);

		// write object
		out.println("\tvar geometry = new THREE.CylinderGeometry( "+round(r*FACTOR)+", "+round(r*FACTOR)+", "+round(l*FACTOR)+", "+SEGMENTS+");");
		wirteBody(node, "cylinder"+node.pathId, (float)l, true);
		return true;
	}
}
