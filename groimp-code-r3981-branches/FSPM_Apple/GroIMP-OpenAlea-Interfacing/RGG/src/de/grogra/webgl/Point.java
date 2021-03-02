
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Point extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		

		// write object
//		out.println("\tvar geometry = new THREE.PointGeometry( "+round(r*FACTOR)+", "+round(l*FACTOR)+", "+SEGMENTS+" );");
//		wirteBody(node, "point"+node.pathId, (float)l);
		return false;
	}

}
