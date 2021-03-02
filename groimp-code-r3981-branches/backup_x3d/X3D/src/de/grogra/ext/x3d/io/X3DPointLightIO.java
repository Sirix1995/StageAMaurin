package de.grogra.ext.x3d.io;

import java.io.IOException;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DPointLight;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a point light.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DPointLightIO extends X3DLightIO {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DPointLight newPointLight = new X3DPointLight();
		
		String valueString;
		
		valueString = atts.getValue("ambientIntensity");
		if (valueString != null)
			newPointLight.setX3dAmbientIntensity(Float.valueOf(valueString));

		valueString = atts.getValue("attenuation");
		if (valueString != null)
			newPointLight.setX3dAttenuation(Util.splitStringToTuple3f(new Vector3f(), valueString));
		
		valueString = atts.getValue("color");
		if (valueString != null)
			newPointLight.setX3dColor(Util.splitStringToTuple3f(new Color3f(), valueString));

		valueString = atts.getValue("intensity");
		if (valueString != null)
			newPointLight.setX3dIntensity(Float.valueOf(valueString));

		valueString = atts.getValue("location");
		if (valueString != null)
			newPointLight.setX3dLocation(Util.splitStringToTuple3f(new Point3f(), valueString));
		
		valueString = atts.getValue("on");
		if (valueString != null)
			newPointLight.setX3dOn(Boolean.valueOf(valueString));

		valueString = atts.getValue("radius");
		if (valueString != null)
			newPointLight.setX3dRadius(Float.valueOf(valueString));

		return newPointLight;
	}
	
	public static void exportPointLightImpl(Leaf node, PointLight light, X3DExport export, Element parentElement)
			throws IOException {
		Element pointLightElement = export.getDoc().createElement("PointLight");
		
		// read and set light attributes
		X3DPointLight defaultPointLight = new X3DPointLight();

		// color
		Tuple3f color = light.getColor();
		if (!color.equals(defaultPointLight.getX3dColor()))
			pointLightElement.setAttribute("color", color.x + " " + color.y + " " + color.z);
		
		// intensity
		float power = light.getPower();
		if (power != defaultPointLight.getX3dIntensity())
			pointLightElement.setAttribute("intensity", String.valueOf(power/100f));
		
		// TODO: attenuation
			
		// read and set x3dbox attributes
		if (node.object instanceof X3DPointLight) {
		
			// ambientIntensity
			float ambientIntensity = node.getFloat(de.grogra.ext.x3d.Attributes.X3DAMBIENT_INTENSITY);
			if (ambientIntensity != defaultPointLight.getX3dAmbientIntensity())
				pointLightElement.setAttribute("ambientIntensity", String.valueOf(ambientIntensity));
			
			// on
			boolean on = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DON);
			if (on != defaultPointLight.isX3dOn())
				pointLightElement.setAttribute("on", String.valueOf(on));
			
			// radius
			float radius = node.getFloat(de.grogra.ext.x3d.Attributes.X3DRADIUS);
			if (radius != defaultPointLight.getX3dRadius())
				pointLightElement.setAttribute("radius", String.valueOf(radius));
		}
		
		parentElement.appendChild(pointLightElement);		
	}

}
