package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DExtrusion;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export an extrusion object.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DExtrusionIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DExtrusion newExtrusion = new X3DExtrusion();
		
		String valueString;
		
		valueString = atts.getValue("beginCap");
		if (valueString != null)
			newExtrusion.setBeginCap(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("ccw");
		if (valueString != null)
			newExtrusion.setCcw(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("convex");
		if (valueString != null)
			newExtrusion.setConvex(Boolean.valueOf(valueString));

		valueString = atts.getValue("creaseAngle");
		if (valueString != null)
			newExtrusion.setCreaseAngle(Float.valueOf(valueString));

		valueString = atts.getValue("crossSection");
		if (valueString != null)
			newExtrusion.setCrossSection(Util.splitStringToArrayOfFloat(valueString));
		
		valueString = atts.getValue("endCap");
		if (valueString != null)
			newExtrusion.setEndCap(Boolean.valueOf(valueString));

		valueString = atts.getValue("orientation");
		if (valueString != null)
			newExtrusion.setOrientation(Util.splitStringToArrayOfFloat(valueString));

		valueString = atts.getValue("scale");
		if (valueString != null)
			newExtrusion.setScale(Util.splitStringToArrayOfFloat(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newExtrusion.setSolid(Boolean.valueOf(valueString));

		valueString = atts.getValue("spine");
		if (valueString != null)
			newExtrusion.setSpine(Util.splitStringToArrayOfFloat(valueString));
		
		valueString = atts.getValue("DEF");
		newExtrusion.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newExtrusion.setUse(valueString);
		
		return newExtrusion;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
	}

}
