package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DSphere;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class X3DSphereIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DSphere newSphere = new X3DSphere();
		
		String valueString;

		valueString = atts.getValue("radius");
		if (valueString != null)
			newSphere.setX3dRadius(Float.valueOf(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newSphere.setX3dSolid(Boolean.valueOf(valueString));
		
		return newSphere;
	}
	
	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Element sphereElement = export.getDoc().createElement("Sphere");
				
		// read and set sphere attributes
		X3DSphere defaultSphere = new X3DSphere();
		
		// radius
		float radius = node.getFloat (de.grogra.imp.objects.Attributes.RADIUS);
		if (radius != defaultSphere.getX3dRadius())
			sphereElement.setAttribute("radius", String.valueOf(radius));
		
		// read and set x3dsphere attributes
		if (node.object instanceof X3DSphere) {
			
			// solid
			boolean solid = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSOLID);
			if (solid != defaultSphere.isX3dSolid())
				sphereElement.setAttribute("solid", String.valueOf(solid));
		}
		
		parentElement.appendChild(sphereElement);

	}

}
