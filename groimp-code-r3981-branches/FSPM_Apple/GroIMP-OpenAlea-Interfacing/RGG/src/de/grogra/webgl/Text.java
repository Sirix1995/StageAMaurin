
package de.grogra.webgl;

import java.io.IOException;

import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Text extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		String text = "test";//node.getString (Attributes.CAPTION);

		// write object
		out.println("\tvar geometry = new THREE.TextGeometry( \""+text+"\", {font: font, size: 0.04, height: 0.02, curveSegments: 4});");
		wirteBody(node, "text"+node.pathId, -0.05f, true);
		return true;
	}

}
