/*
 * XML Type:  Root
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Root
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Root(@).
 *
 * This is a complex type.
 */
public class RootImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Root
{
    
    public RootImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ROOTID$0 = 
        new javax.xml.namespace.QName("", "root_id");
    
    
    /**
     * Gets the "root_id" attribute
     */
    public long getRootId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ROOTID$0);
            if (target == null)
            {
                return 0L;
            }
            return target.getLongValue();
        }
    }
    
    /**
     * Gets (as xml) the "root_id" attribute
     */
    public de.grogra.ext.exchangegraph.xmlbeans.IdType xgetRootId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ROOTID$0);
            return target;
        }
    }
    
    /**
     * Sets the "root_id" attribute
     */
    public void setRootId(long rootId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ROOTID$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ROOTID$0);
            }
            target.setLongValue(rootId);
        }
    }
    
    /**
     * Sets (as xml) the "root_id" attribute
     */
    public void xsetRootId(de.grogra.ext.exchangegraph.xmlbeans.IdType rootId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ROOTID$0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().add_attribute_user(ROOTID$0);
            }
            target.set(rootId);
        }
    }
}
