package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DIndexedLineSet;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export an indexed line set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DIndexedLineSetIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DIndexedLineSet newIndexedLineSet = new X3DIndexedLineSet();
		
		String valueString;
		
		valueString = atts.getValue("colorIndex");
		if (valueString != null)
			newIndexedLineSet.setColorIndex(Util.splitStringToArrayOfInt(valueString));
		
		valueString = atts.getValue("colorPerVertex");
		if (valueString != null)
			newIndexedLineSet.setColorPerVertex(Boolean.valueOf(valueString));
		
		valueString = atts.getValue("coordIndex");
		if (valueString != null)
			newIndexedLineSet.setCoordIndex(Util.splitStringToArrayOfInt(valueString));
		
		valueString = atts.getValue("DEF");
		newIndexedLineSet.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newIndexedLineSet.setUse(valueString);
		
		return newIndexedLineSet;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
	}

}
