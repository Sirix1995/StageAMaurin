package de.grogra.ext.x3d.io;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.ext.x3d.objects.X3DPointSet;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

/**
 * Used to import and export a point set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DPointSetIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		X3DPointSet newPointSet = new X3DPointSet();
		
		String valueString;
		
		valueString = atts.getValue("DEF");
		newPointSet.setDef(valueString);
		
		valueString = atts.getValue("USE");
		newPointSet.setUse(valueString);
		
		return newPointSet;
	}
	
	@Override
	public void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
	}

}
