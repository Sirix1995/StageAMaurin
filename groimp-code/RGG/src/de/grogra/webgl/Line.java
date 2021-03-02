
package de.grogra.webgl;

import java.io.IOException;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.imp3d.objects.SceneTreeWithShader;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;

public class Line extends ObjectBase {

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		export.getGraphState ().setObjectContext (node.object, node.asNode);
		de.grogra.imp3d.objects.Line line = (de.grogra.imp3d.objects.Line)node.object;
		Vector3f axis = line.getAxis();

		// translation
		Vector3d trans = new Vector3d();
		transformation.get(trans);
		
		// RGBA
		Shader shader = ((SceneTreeWithShader.Leaf)node).shader;
		if (shader instanceof RGBAShader) {
			RGBAShader rgba = (RGBAShader) shader;
			out.println("\tvar material = new THREE.LineBasicMaterial( { " + "color: "+rgba.getAverageColor()+" });");
		}
		// write object
		out.println("\tvar geometry = new THREE.Geometry();");
		out.println("\tgeometry.vertices.push(");
		out.println("\t\tnew THREE.Vector3( "+trans.x+", "+trans.y+", "+trans.z+" ),");
		out.println("\t\tnew THREE.Vector3( "+(axis.x)+", "+(axis.y)+", "+(axis.z)+" )");
		out.println("\t);");
		out.println("\tvar line"+node.pathId+" = new THREE.Line( geometry, material );");
		out.println("\tgroup.add( line"+node.pathId+" );");
		return true;
	}

}
