package de.grogra.ext.x3d.io;

import java.io.IOException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.AxisObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DCone;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a cone.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DConeIO extends AxisObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DCone newCone = new X3DCone();
		
		String valueString;
		
		valueString = atts.getValue("height");
		if (valueString != null)
			newCone.setX3dHeight(Float.valueOf(valueString));
		
		valueString = atts.getValue("bottomRadius");
		if (valueString != null)
			newCone.setX3dBottomRadius(Float.valueOf(valueString));
		
		valueString = atts.getValue("bottom");
		if (valueString != null)
			newCone.setX3dBottom(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("side");
		if (valueString != null)
			newCone.setX3dSide(Boolean.valueOf(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newCone.setX3dSolid(Boolean.valueOf(valueString));		
		
		return newCone;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Element coneElement = export.getDoc().createElement("Cone");
		
		// read and set cone attributes
		X3DCone defaultCone = new X3DCone();
		
		// radius
		float radius = node.getFloat (de.grogra.imp.objects.Attributes.RADIUS);
		if (radius != defaultCone.getX3dBottomRadius())
			coneElement.setAttribute("bottomRadius", String.valueOf(radius));
		
		// height
		float height = (float) node.getDouble (de.grogra.imp.objects.Attributes.LENGTH);
		if (height != defaultCone.getX3dHeight())
			coneElement.setAttribute("height", String.valueOf(height));
		
		// TODO: bottom open
//		boolean bottom = !node.getBoolean(de.grogra.imp3d.objects.Attributes.BASE_OPEN);
//		if (bottom != defaultCone.isX3dBottom())
//			coneElement.setAttribute("bottom", String.valueOf(bottom));
		
		// read and set x3d cone attributes
		if (node.object instanceof X3DCone) {
			
			// solid
			boolean solid = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSOLID);
			if (solid != defaultCone.isX3dSolid())
				coneElement.setAttribute("solid", String.valueOf(solid));
			
			// side open
			boolean side = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSIDE);
			if (side != defaultCone.isX3dSide())
				coneElement.setAttribute("side", String.valueOf(side));
		}
		
		parentElement.appendChild(coneElement);
	}

}
