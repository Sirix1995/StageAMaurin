
package de.grogra.webgl;

import java.io.IOException;

import javax.vecmath.Vector3d;

import de.grogra.imp3d.objects.AmbientLight;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.Light;

public class LightExport extends ObjectBase {

	private final Light light;
	
	public LightExport(LightNode s) {
		light = s.getLight();
	}

	@Override
	boolean exportImpl (Leaf node, WebGLExport export) throws IOException {
		String name = "light"+node.pathId;
		if(light instanceof SpotLight) {
			// write object
			out.println("\t\tvar "+name+" = new THREE.SpotLight( "+((SpotLight)light).getColor().getAverageColor()+", 1.5 );");
			position(name);
			return true;
		}
		if(light instanceof PointLight) {
			// write object
			out.println("\t\tvar "+name+" = new THREE.PointLight( "+((PointLight)light).getColor().getAverageColor()+", 1.5 );");
			position(name);
			return true;
		}
		
		if(light instanceof DirectionalLight) {
			// write object
			out.println("\t\tvar "+name+" = new THREE.DirectionalLight( "+((DirectionalLight)light).getColor().getAverageColor()+", 1.5 );");
			position(name);
			return true;
		}
		if(light instanceof AmbientLight) {
			// write object
			out.println("\t\tvar "+name+" = new THREE.AmbientLight( "+((AmbientLight)light).getColor().getAverageColor()+" );");
			position(name);
			return true;
		}
		return true;
	}

	private void position(String name) {
		// translation
		Vector3d trans = new Vector3d();
		transformation.get(trans);
		
		out.println("\t\t"+name+".position.set( "+trans.x*FACTOR+", "+trans.z*FACTOR+", "+trans.y*FACTOR+" );");
		out.println("\t\tscene.add( "+name+" );");
	}
}
