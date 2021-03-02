
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Frustum extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		float br = node.getFloat (Attributes.BASE_RADIUS);
		float tr = node.getFloat (Attributes.TOP_RADIUS);
		double l = node.getDouble (Attributes.LENGTH);

		// write object
		out.println("\tvar geometry = new THREE.ConeGeometry( "+round(br*FACTOR)+", "+round(tr*FACTOR)+", "+round(l*FACTOR)+", "+SEGMENTS+" );");
		wirteBody(node, "frustum"+node.pathId, (float)l, true);
		return true;
	}

}
