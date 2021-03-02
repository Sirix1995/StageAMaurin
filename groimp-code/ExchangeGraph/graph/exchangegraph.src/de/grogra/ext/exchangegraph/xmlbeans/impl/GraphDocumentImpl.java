/*
 * An XML document type.
 * Localname: graph
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.GraphDocument
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * A document containing one graph(@) element.
 *
 * This is a complex type.
 */
public class GraphDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.GraphDocument
{
    
    public GraphDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GRAPH$0 = 
        new javax.xml.namespace.QName("", "graph");
    
    
    /**
     * Gets the "graph" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Graph getGraph()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Graph target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Graph)get_store().find_element_user(GRAPH$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "graph" element
     */
    public void setGraph(de.grogra.ext.exchangegraph.xmlbeans.Graph graph)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Graph target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Graph)get_store().find_element_user(GRAPH$0, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.Graph)get_store().add_element_user(GRAPH$0);
            }
            target.set(graph);
        }
    }
    
    /**
     * Appends and returns a new empty "graph" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Graph addNewGraph()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Graph target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Graph)get_store().add_element_user(GRAPH$0);
            return target;
        }
    }
}
