package de.grogra.ext.x3d;

import java.io.IOException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public abstract class AxisObjectBase extends ObjectBase {

	@Override
	abstract protected Node doImportImpl(Attributes atts);

	@Override
	abstract protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException;
	
}
