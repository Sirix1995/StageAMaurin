package de.grogra.ext.exchangegraph.helpnodes;

import java.util.List;
import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Frustum;

public class XEGFrustum {

	public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

		float taper = 0;
		float radius = 0;
		
		for (Property p : properties) {
			if (p.getName().equals("radius")) {
				radius = Float.valueOf(p.getValue());
				((Frustum) node).setBaseRadius(radius);
				handledProperties.add(p);
			}
			else if (p.getName().equals("height")) {
				((Frustum) node).setLength(Float.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("taper")) {
				taper = Float.valueOf(p.getValue());
				handledProperties.add(p);
			}
			else if (p.getName().equals("bottom_open")) {
				((Frustum) node).setBaseOpen(Boolean.valueOf(p.getValue()));
				handledProperties.add(p);
			}
			else if (p.getName().equals("top_open")) {
				((Frustum) node).setTopOpen(Boolean.valueOf(p.getValue()));
				handledProperties.add(p);
			}
		}
		
		((Frustum) node).setTopRadius(taper * radius);
		
	}
	
	public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
		Property xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("radius");
		xmlProperty.setValue(String.valueOf(((Frustum)node).getBaseRadius()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("height");
		xmlProperty.setValue(String.valueOf(((Frustum)node).getLength()));

		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("taper");
		xmlProperty.setValue(String.valueOf(((Frustum)node).getTopRadius() / ((Frustum)node).getBaseRadius()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("top_open");
		xmlProperty.setValue(String.valueOf(((Frustum)node).isTopOpen()));
		
		xmlProperty = xmlNode.addNewProperty();
		xmlProperty.setName("bottom_open");
		xmlProperty.setValue(String.valueOf(((Frustum)node).isBaseOpen()));
	}

}
