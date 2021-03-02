package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DElevationGrid;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export an elevation grid.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DElevationGridIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DElevationGrid newElevationGrid = new X3DElevationGrid();
		
		String valueString;
		
		valueString = atts.getValue("ccw");
		if (valueString != null)
			newElevationGrid.setX3dCcw(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("colorPerVertex");
		if (valueString != null)
			newElevationGrid.setX3dColorPerVertex(Boolean.valueOf(valueString));

		valueString = atts.getValue("creaseAngle");
		if (valueString != null)
			newElevationGrid.setX3dCreaseAngle(Float.valueOf(valueString));
	
		valueString = atts.getValue("height");
		if (valueString != null)
			newElevationGrid.setX3dHeight(Util.splitStringToArrayOfFloat(valueString));
		
		valueString = atts.getValue("normalPerVertex");
		if (valueString != null)
			newElevationGrid.setX3dNormalPerVertex(Boolean.valueOf(valueString));

		valueString = atts.getValue("solid");
		if (valueString != null)
			newElevationGrid.setX3dSolid(Boolean.valueOf(valueString));

		valueString = atts.getValue("xDimension");
		if (valueString != null)
			newElevationGrid.setX3dXDimension(Integer.valueOf(valueString));

		valueString = atts.getValue("xSpacing");
		if (valueString != null)
			newElevationGrid.setX3dXSpacing(Float.valueOf(valueString));

		valueString = atts.getValue("zDimension");
		if (valueString != null)
			newElevationGrid.setX3dZDimension(Integer.valueOf(valueString));
		
		valueString = atts.getValue("zSpacing");
		if (valueString != null)
			newElevationGrid.setX3dZSpacing(Float.valueOf(valueString));

		valueString = atts.getValue("DEF");
		newElevationGrid.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newElevationGrid.setUse(valueString);
		
		return newElevationGrid;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
	}

}
