
package de.grogra.webgl;

import java.io.IOException;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class Parallelogram extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		Vector3f axis = (Vector3f) node.getObject(Attributes.AXIS);
		float l = (float) node.getDouble (Attributes.LENGTH);

		// write object
		out.println("\tvar geometry = new THREE.PlaneGeometry( "+round(axis.x*FACTOR)+", "+round(l*FACTOR)+", "+SEGMENTS+" );");
//		out.println("\tvar squareShape = new THREE.Shape();");
//		out.println("\tsquareShape.moveTo( 0,0 );");
//		out.println("\tsquareShape.lineTo( 0, "+round(l*FACTOR)+" );");
//		out.println("\tsquareShape.lineTo( "+round(axis.x*FACTOR)+", "+round(l*FACTOR)+" );");
//		out.println("\tsquareShape.lineTo( "+round(axis.x*FACTOR)+", 0 );");
//		out.println("\tsquareShape.lineTo( 0, 0 );");
//		out.println("\tvar geometry = new THREE.ShapeGeometry( squareShape );");
		wirteBody(node,"paralellogram"+node.pathId, 0, false );
		return true;
	}

}

