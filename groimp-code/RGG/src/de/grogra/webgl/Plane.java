
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Plane extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
	//	Vector3f axis = (Vector3f) node.getObject(Attributes.AXIS);
	//	float l = (float) node.getDouble (Attributes.LENGTH);

		// write object
		out.println("\tvar geometry = new THREE.PlaneGeometry( 100, 100, "+SEGMENTS+", "+SEGMENTS+" );");
		wirteBody(node,"plane"+node.pathId, 0, false);
		return true;
	}

}

