package de.grogra.ext.x3d.io;

import java.io.IOException;
import javax.vecmath.Vector3f;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.AxisObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DBox;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a box.
 * 
 * @author Udo Bischof
 *
 */
public class X3DBoxIO extends AxisObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DBox newBox = new X3DBox();
		
		String valueString;
		
		valueString = atts.getValue("size");
		if (valueString != null)
			newBox.setX3dSize(Util.splitStringToTuple3f(new Vector3f(), valueString));
		
		valueString = atts.getValue("solid");
		if (valueString != null)
			newBox.setX3dSolid(Boolean.valueOf(valueString));
		
		return newBox;
	}

	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Element boxElement = export.getDoc().createElement("Box");
		
		// read and set box attributes
		X3DBox defaultBox = new X3DBox();
		
		// length, width, height
		float width = (float) node.getDouble (de.grogra.imp.objects.Attributes.WIDTH);
		float length = (float) node.getDouble (de.grogra.imp.objects.Attributes.LENGTH);
		float height = (float) node.getDouble (de.grogra.imp.objects.Attributes.HEIGHT);
		if (!((width == defaultBox.getX3dSize().x) && (length == defaultBox.getX3dSize().y) && (height == defaultBox.getX3dSize().z)))
			boxElement.setAttribute("size", width + " " + length + " " + height);

		// read and set x3dbox attributes
		if (node.object instanceof X3DBox) {
		
			// solid
			boolean solid = node.getBoolean(de.grogra.ext.x3d.Attributes.X3DSOLID);
			if (solid != defaultBox.isX3dSolid())
				boxElement.setAttribute("solid", String.valueOf(solid));			
		}
		
		parentElement.appendChild(boxElement);
	}

}
