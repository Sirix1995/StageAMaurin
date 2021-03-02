package de.grogra.ext.x3d.io;

import java.io.IOException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.AxisObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DCylinder;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a cylinder.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DCylinderIO extends AxisObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DCylinder newCylinder = new X3DCylinder();
		
		String valueString;
		
		valueString = atts.getValue("height");
		if (valueString != null)
			newCylinder.setX3dHeight(Float.valueOf(valueString));
		
		valueString = atts.getValue("radius");
		if (valueString != null)
			newCylinder.setX3dRadius(Float.valueOf(valueString));
		
		valueString = atts.getValue("bottom");
		if (valueString != null)
			newCylinder.setX3dBottom(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("side");
		if (valueString != null)
			newCylinder.setX3dSide(Boolean.valueOf(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newCylinder.setX3dSolid(Boolean.valueOf(valueString));

		valueString = atts.getValue("top");
		if (valueString != null)
			newCylinder.setX3dTop(Boolean.valueOf(valueString));		
		
		return newCylinder;
	}
	
	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Element cylinderElement = export.getDoc().createElement("Cylinder");
		
		// read and set cylinder attributes
		X3DCylinder defaultCylinder = new X3DCylinder();
		
		// radius
		float radius = node.getFloat (de.grogra.imp.objects.Attributes.RADIUS);
		if (radius != defaultCylinder.getX3dRadius())
			cylinderElement.setAttribute("radius", String.valueOf(radius));
		
		// height
		float height = (float) node.getDouble (de.grogra.imp.objects.Attributes.LENGTH);
		if (height != defaultCylinder.getX3dHeight())
			cylinderElement.setAttribute("height", String.valueOf(height));
		
		// top and bottom open
		boolean top = !node.getBoolean(de.grogra.imp3d.objects.Attributes.TOP_OPEN);
		boolean bottom = !node.getBoolean(de.grogra.imp3d.objects.Attributes.BASE_OPEN);
		if (top != defaultCylinder.isX3dTop())
			cylinderElement.setAttribute("top", String.valueOf(top));
		if (bottom != defaultCylinder.isX3dBottom())
			cylinderElement.setAttribute("bottom", String.valueOf(bottom));		

		// read and set x3d cylinder attributes
		if (node.object instanceof X3DCylinder) {
			// solid
			boolean solid = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSOLID);
			if (solid != defaultCylinder.isX3dSolid())
				cylinderElement.setAttribute("solid", String.valueOf(solid));
			
			// side open
			boolean side = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSIDE);
			if (side != defaultCylinder.isX3dSide())
				cylinderElement.setAttribute("side", String.valueOf(side));
		}
		
		parentElement.appendChild(cylinderElement);
	}

}
