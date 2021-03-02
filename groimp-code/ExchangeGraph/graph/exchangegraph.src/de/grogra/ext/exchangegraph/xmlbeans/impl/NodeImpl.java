/*
 * XML Type:  Node
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Node
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Node(@).
 *
 * This is a complex type.
 */
public class NodeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Node
{
    
    public NodeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PROPERTY$0 = 
        new javax.xml.namespace.QName("", "property");
    private static final javax.xml.namespace.QName ID$2 = 
        new javax.xml.namespace.QName("", "id");
    private static final javax.xml.namespace.QName TYPE$4 = 
        new javax.xml.namespace.QName("", "type");
    private static final javax.xml.namespace.QName NAME$6 = 
        new javax.xml.namespace.QName("", "name");
    
    
    /**
     * Gets a List of "property" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Property> getPropertyList()
    {
        final class PropertyList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Property>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Property get(int i)
                { return NodeImpl.this.getPropertyArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Property set(int i, de.grogra.ext.exchangegraph.xmlbeans.Property o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Property old = NodeImpl.this.getPropertyArray(i);
                NodeImpl.this.setPropertyArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Property o)
                { NodeImpl.this.insertNewProperty(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Property remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Property old = NodeImpl.this.getPropertyArray(i);
                NodeImpl.this.removeProperty(i);
                return old;
            }
            
            public int size()
                { return NodeImpl.this.sizeOfPropertyArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new PropertyList();
        }
    }
    
    /**
     * Gets array of all "property" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Property[] getPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(PROPERTY$0, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.Property[] result = new de.grogra.ext.exchangegraph.xmlbeans.Property[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "property" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Property getPropertyArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Property target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().find_element_user(PROPERTY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "property" element
     */
    public int sizeOfPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PROPERTY$0);
        }
    }
    
    /**
     * Sets array of all "property" element
     */
    public void setPropertyArray(de.grogra.ext.exchangegraph.xmlbeans.Property[] propertyArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(propertyArray, PROPERTY$0);
        }
    }
    
    /**
     * Sets ith "property" element
     */
    public void setPropertyArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Property property)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Property target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().find_element_user(PROPERTY$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(property);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "property" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Property insertNewProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Property target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().insert_element_user(PROPERTY$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "property" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Property addNewProperty()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Property target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().add_element_user(PROPERTY$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "property" element
     */
    public void removeProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PROPERTY$0, i);
        }
    }
    
    /**
     * Gets the "id" attribute
     */
    public long getId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$2);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ID$2);
            return target;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$2);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$2);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().find_attribute_user(ID$2);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.IdType)get_store().add_attribute_user(ID$2);
            }
            target.set(id);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$4);
            return target;
        }
    }
    
    /**
     * True if has "type" attribute
     */
    public boolean isSetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(TYPE$4) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TYPE$4);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$4);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(TYPE$4);
            }
            target.set(type);
        }
    }
    
    /**
     * Unsets the "type" attribute
     */
    public void unsetType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(TYPE$4);
        }
    }
    
    /**
     * Gets the "name" attribute
     */
    public java.lang.String getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "name" attribute
     */
    public org.apache.xmlbeans.XmlString xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(NAME$6);
            return target;
        }
    }
    
    /**
     * True if has "name" attribute
     */
    public boolean isSetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(NAME$6) != null;
        }
    }
    
    /**
     * Sets the "name" attribute
     */
    public void setName(java.lang.String name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(NAME$6);
            }
            target.setStringValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "name" attribute
     */
    public void xsetName(org.apache.xmlbeans.XmlString name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(NAME$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(NAME$6);
            }
            target.set(name);
        }
    }
    
    /**
     * Unsets the "name" attribute
     */
    public void unsetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(NAME$6);
        }
    }
}
