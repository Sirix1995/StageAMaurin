
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Box extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		float w = (float) node.getDouble (Attributes.WIDTH);
		float h = (float) node.getDouble (Attributes.HEIGHT);
		float l = (float) node.getDouble (Attributes.LENGTH);

		// write object
		out.println("\tvar geometry = new THREE.BoxGeometry( "+round(w*FACTOR)+", "+round(h*FACTOR)+", "+round(l*FACTOR)+" );");
		wirteBody(node, "box"+node.pathId, l, true);
		return true;
	}

}

