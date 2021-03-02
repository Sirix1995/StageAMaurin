package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DText;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a text object.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DTextIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DText newText = new X3DText();
		
		String valueString;
		
		valueString = atts.getValue("length");
		if (valueString != null)
			newText.setLength(Util.splitStringToArrayOfFloat(valueString));
		
		valueString = atts.getValue("maxExtent");
		if (valueString != null)
			newText.setMaxExtent(Float.valueOf(valueString));
		
		valueString = atts.getValue("solid");
		if (valueString != null)
			newText.setSolid(Boolean.valueOf(valueString));

		valueString = atts.getValue("string");
		if (valueString != null)
			newText.setString(Util.splitStringToArrayOfString(valueString));
		
		valueString = atts.getValue("DEF");
		newText.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newText.setUse(valueString);
		
		return newText;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
	}

}
