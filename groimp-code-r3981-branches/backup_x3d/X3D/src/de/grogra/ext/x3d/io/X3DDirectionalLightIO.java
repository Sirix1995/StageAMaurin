package de.grogra.ext.x3d.io;

import java.io.IOException;
import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DDirectionalLight;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.SceneTree.Leaf;
import de.grogra.math.TMatrix4d;
import de.grogra.math.Transform3D;

/**
 * Used to import and export a directional light.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DDirectionalLightIO extends X3DLightIO {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DDirectionalLight newDirectionalLight = new X3DDirectionalLight();
		
		String valueString;
		
		valueString = atts.getValue("ambientIntensity");
		if (valueString != null)
			newDirectionalLight.setX3dAmbientIntensity(Float.valueOf(valueString));

		valueString = atts.getValue("color");
		if (valueString != null)
			newDirectionalLight.setX3dColor(Util.splitStringToTuple3f(new Color3f(), valueString));

		valueString = atts.getValue("direction");
		if (valueString != null)
			newDirectionalLight.setX3dDirection(Util.splitStringToTuple3f(new Vector3f(), valueString));

		valueString = atts.getValue("intensity");
		if (valueString != null)
			newDirectionalLight.setX3dIntensity(Float.valueOf(valueString));

		valueString = atts.getValue("on");
		if (valueString != null)
			newDirectionalLight.setX3dOn(Boolean.valueOf(valueString));
		
		return newDirectionalLight;
	}
	
	protected static void exportDirectionalLightImpl(Leaf node,
			DirectionalLight light, X3DExport export, Element parentElement)
			throws IOException {
		Element directionalLightElement = export.getDoc().createElement("DirectionalLight");
		
		// read and set light attributes
		X3DDirectionalLight defaultDirectionalLight = new X3DDirectionalLight();

		// color
		Tuple3f color = light.getColor();
		if (!color.equals(defaultDirectionalLight.getX3dColor()))
			directionalLightElement.setAttribute("color", color.x + " " + color.y + " " + color.z);
		
		// direction
		Transform3D dir = (Transform3D) node.getObject(de.grogra.imp3d.objects.Attributes.TRANSFORM);
		Vector3d dirVec = new Vector3d(0, 0, 1);
		if (dir instanceof TMatrix4d) {
			((TMatrix4d) dir).transform(dirVec);
		}
		if (!dirVec.equals(defaultDirectionalLight.getX3dDirection()))
			directionalLightElement.setAttribute("direction", dirVec.x + " " + dirVec.z + " " + -dirVec.y);
		
		// intensity
		float power = light.getPowerDensity();
		if (power != defaultDirectionalLight.getX3dIntensity())
			directionalLightElement.setAttribute("intensity", String.valueOf(power/10f));
		
		// read and set x3dbox attributes
		if (node.object instanceof X3DDirectionalLight) {
		
			// ambientIntensity
			float ambientIntensity = node.getFloat(de.grogra.ext.x3d.Attributes.X3DAMBIENT_INTENSITY);
			if (ambientIntensity != defaultDirectionalLight.getX3dAmbientIntensity())
				directionalLightElement.setAttribute("ambientIntensity", String.valueOf(ambientIntensity));
			
			// on
			boolean on = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DON);
			if (on != defaultDirectionalLight.isX3dOn())
				directionalLightElement.setAttribute("on", String.valueOf(on));
		}
		
//		parentElement.appendChild(directionalLightElement);
		X3DExport.getTheExport().getScene().appendChild(directionalLightElement);
	}

}
