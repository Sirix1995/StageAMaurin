/*
 * XML Type:  Type
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Type
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Type(@).
 *
 * This is a complex type.
 */
public class TypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Type
{
    
    public TypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName EXTENDS$0 = 
        new javax.xml.namespace.QName("", "extends");
    private static final javax.xml.namespace.QName IMPLEMENTS$2 = 
        new javax.xml.namespace.QName("", "implements");
    private static final javax.xml.namespace.QName PROPERTY$4 = 
        new javax.xml.namespace.QName("", "property");
    private static final javax.xml.namespace.QName NAME$6 = 
        new javax.xml.namespace.QName("", "name");
    
    
    /**
     * Gets the "extends" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ExtendsType getExtends()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ExtendsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ExtendsType)get_store().find_element_user(EXTENDS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "extends" element
     */
    public void setExtends(de.grogra.ext.exchangegraph.xmlbeans.ExtendsType xextends)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ExtendsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ExtendsType)get_store().find_element_user(EXTENDS$0, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.ExtendsType)get_store().add_element_user(EXTENDS$0);
            }
            target.set(xextends);
        }
    }
    
    /**
     * Appends and returns a new empty "extends" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ExtendsType addNewExtends()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ExtendsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ExtendsType)get_store().add_element_user(EXTENDS$0);
            return target;
        }
    }
    
    /**
     * Gets a List of "implements" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.ImplementsType> getImplementsList()
    {
        final class ImplementsList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.ImplementsType>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType get(int i)
                { return TypeImpl.this.getImplementsArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType set(int i, de.grogra.ext.exchangegraph.xmlbeans.ImplementsType o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.ImplementsType old = TypeImpl.this.getImplementsArray(i);
                TypeImpl.this.setImplementsArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.ImplementsType o)
                { TypeImpl.this.insertNewImplements(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.ImplementsType old = TypeImpl.this.getImplementsArray(i);
                TypeImpl.this.removeImplements(i);
                return old;
            }
            
            public int size()
                { return TypeImpl.this.sizeOfImplementsArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new ImplementsList();
        }
    }
    
    /**
     * Gets array of all "implements" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[] getImplementsArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(IMPLEMENTS$2, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[] result = new de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "implements" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType getImplementsArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ImplementsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ImplementsType)get_store().find_element_user(IMPLEMENTS$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "implements" element
     */
    public int sizeOfImplementsArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(IMPLEMENTS$2);
        }
    }
    
    /**
     * Sets array of all "implements" element
     */
    public void setImplementsArray(de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[] ximplementsArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(ximplementsArray, IMPLEMENTS$2);
        }
    }
    
    /**
     * Sets ith "implements" element
     */
    public void setImplementsArray(int i, de.grogra.ext.exchangegraph.xmlbeans.ImplementsType ximplements)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ImplementsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ImplementsType)get_store().find_element_user(IMPLEMENTS$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(ximplements);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "implements" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType insertNewImplements(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ImplementsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ImplementsType)get_store().insert_element_user(IMPLEMENTS$2, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "implements" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ImplementsType addNewImplements()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ImplementsType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ImplementsType)get_store().add_element_user(IMPLEMENTS$2);
            return target;
        }
    }
    
    /**
     * Removes the ith "implements" element
     */
    public void removeImplements(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(IMPLEMENTS$2, i);
        }
    }
    
    /**
     * Gets a List of "property" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Property> getPropertyList()
    {
        final class PropertyList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Property>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Property get(int i)
                { return TypeImpl.this.getPropertyArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Property set(int i, de.grogra.ext.exchangegraph.xmlbeans.Property o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Property old = TypeImpl.this.getPropertyArray(i);
                TypeImpl.this.setPropertyArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Property o)
                { TypeImpl.this.insertNewProperty(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Property remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Property old = TypeImpl.this.getPropertyArray(i);
                TypeImpl.this.removeProperty(i);
                return old;
            }
            
            public int size()
                { return TypeImpl.this.sizeOfPropertyArray(); }
            
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
            get_store().find_all_element_users(PROPERTY$4, targetList);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().find_element_user(PROPERTY$4, i);
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
            return get_store().count_elements(PROPERTY$4);
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
            arraySetterHelper(propertyArray, PROPERTY$4);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().find_element_user(PROPERTY$4, i);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().insert_element_user(PROPERTY$4, i);
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
            target = (de.grogra.ext.exchangegraph.xmlbeans.Property)get_store().add_element_user(PROPERTY$4);
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
            get_store().remove_element(PROPERTY$4, i);
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
