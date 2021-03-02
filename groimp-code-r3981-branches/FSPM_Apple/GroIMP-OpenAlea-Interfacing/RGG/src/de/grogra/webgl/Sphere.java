
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Sphere extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		float r = node.getFloat (Attributes.RADIUS);

		// write object
		out.println("\tvar geometry = new THREE.SphereGeometry( "+round(r*FACTOR)+", "+SEGMENTS+", "+SEGMENTS/2+" );");
		wirteBody(node, "sphere"+node.pathId, r, false);
		return true;
	}

}
