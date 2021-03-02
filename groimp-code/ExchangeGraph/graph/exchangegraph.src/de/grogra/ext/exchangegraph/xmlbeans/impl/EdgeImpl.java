/*
 * XML Type:  Edge
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Edge
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Edge(@).
 *
 * This is a complex type.
 */
public class EdgeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Edge
{
    
    public EdgeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ID$0 = 
        new javax.xml.namespace.QName("", "id");
    private static final javax.xml.namespace.QName SRCID$2 = 
        new javax.xml.namespace.QName("", "src_id");
    private static final javax.xml.namespace.QName DESTID$4 = 
        new javax.xml.namespace.QName("", "dest_id");
    private static final javax.xml.namespace.QName TYPE$6 = 
        new javax.xml.namespace.QName("", "type");
    
    
    /**
     * Gets the "id" attribute
     */
    public long getId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$0);
            if (target == null)
            {
                return 0L;
            }
            return target.getLongValue();
        }
    }
    
    /**
     * Gets (as xml) the "id" attribute
     */
    public de.grogra.ext.exchangegraph.xmlbeans.IdType xgetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ID$0);
            return target;
        }
    }
    
    /**
     * True if has "id" attribute
     */
    public boolean isSetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(ID$0) != null;
        }
    }
    
    /**
     * Sets the "id" attribute
     */
    public void setId(long id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$0);
            }
            target.setLongValue(id);
        }
    }
    
    /**
     * Sets (as xml) the "id" attribute
     */
    public void xsetId(de.grogra.ext.exchangegraph.xmlbeans.IdType id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ID$0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().add_attribute_user(ID$0);
            }
            target.set(id);
        }
    }
    
    /**
     * Unsets the "id" attribute
     */
    public void unsetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(ID$0);
        }
    }
    
    /**
     * Gets the "src_id" attribute
     */
    public long getSrcId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SRCID$2);
            if (target == null)
            {
                return 0L;
            }
            return target.getLongValue();
        }
    }
    
    /**
     * Gets (as xml) the "src_id" attribute
     */
    public de.grogra.ext.exchangegraph.xmlbeans.IdType xgetSrcId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(SRCID$2);
            return target;
        }
    }
    
    /**
     * Sets the "src_id" attribute
     */
    public void setSrcId(long srcId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(SRCID$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(SRCID$2);
            }
            target.setLongValue(srcId);
        }
    }
    
    /**
     * Sets (as xml) the "src_id" attribute
     */
    public void xsetSrcId(de.grogra.ext.exchangegraph.xmlbeans.IdType srcId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(SRCID$2);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().add_attribute_user(SRCID$2);
            }
            target.set(srcId);
        }
    }
    
    /**
     * Gets the "dest_id" attribute
     */
    public long getDestId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DESTID$4);
            if (target == null)
            {
                return 0L;
            }
            return target.getLongValue();
        }
    }
    
    /**
     * Gets (as xml) the "dest_id" attribute
     */
    public de.grogra.ext.exchangegraph.xmlbeans.IdType xgetDestId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(DESTID$4);
            return target;
        }
    }
    
    /**
     * Sets the "dest_id" attribute
     */
    public void setDestId(long destId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(DESTID$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(DESTID$4);
            }
            target.setLongValue(destId);
        }
    }
    
    /**
     * Sets (as xml) the "dest_id" attribute
     */
    public void xsetDestId(de.grogra.ext.exchangegraph.xmlbeans.IdType destId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.IdType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(DESTID$4);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().add_attribute_user(DESTID$4);
            }
            target.set(destId);
        }
    }
    
    /**
     * Gets the "type" attribute
     */
    public java.lang.String getType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "type" attribute
     */
    public org.apache.xmlbeans.XmlString xgetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$6);
            return target;
        }
    }
    
    /**
     * Sets the "type" attribute
     */
    public void setType(java.lang.String type)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TYPE$6);
            }
            target.setStringValue(type);
        }
    }
    
    /**
     * Sets (as xml) the "type" attribute
     */
    public void xsetType(org.apache.xmlbeans.XmlString type)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(TYPE$6);
            }
            target.set(type);
        }
    }
}
