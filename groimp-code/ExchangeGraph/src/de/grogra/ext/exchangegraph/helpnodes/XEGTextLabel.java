package de.grogra.ext.exchangegraph.helpnodes;

import java.util.List;

import de.grogra.ext.exchangegraph.xmlbeans.Property;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.TextLabel;

public class XEGTextLabel {

    public static void handleImportProperties(Node node, List<Property> properties, List<Property> handledProperties) {

        for (Property p : properties) {
            if (p.getName().equals("caption")) {
                ((TextLabel) node).setCaption(String.valueOf(p.getValue()));
                handledProperties.add(p);
            }  
        }

    }

    public static void handleExportProperties(Node node, de.grogra.ext.exchangegraph.xmlbeans.Node xmlNode) {
        Property xmlProperty = xmlNode.addNewProperty();
        xmlProperty.setName("caption");
        xmlProperty.setValue(String.valueOf(((TextLabel)node).getCaption()));

    }
}
