package de.grogra.ext.x3d.io;

import java.io.IOException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.ObjectBase;
import de.grogra.ext.x3d.X3DExport;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.objects.SceneTree.Leaf;

public class X3DLightIO extends ObjectBase {

	@Override
	protected Node doImportImpl(Attributes atts) {
		return null;
	}

	@Override
	protected void exportImpl(Leaf node, X3DExport export, Element parentElement)
			throws IOException {
		Object light = node.getObject(de.grogra.imp3d.objects.Attributes.LIGHT);
		if (light instanceof SpotLight)
			X3DSpotLightIO.exportSpotLightImpl(node, (SpotLight) light, export, parentElement);
		else if (light instanceof PointLight)
			X3DPointLightIO.exportPointLightImpl(node, (PointLight) light, export, parentElement);
		else if (light instanceof DirectionalLight)
			X3DDirectionalLightIO.exportDirectionalLightImpl(node, (DirectionalLight) light, export, parentElement);
	}
	
}
