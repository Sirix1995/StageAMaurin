/*
 * XML Type:  Property
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Property
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Property(@).
 *
 * This is a complex type.
 */
public class PropertyImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Property
{
    
    public PropertyImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MATRIX$0 = 
        new javax.xml.namespace.QName("", "matrix");
    private static final javax.xml.namespace.QName RGB$2 = 
        new javax.xml.namespace.QName("", "rgb");
    private static final javax.xml.namespace.QName RGBA$4 = 
        new javax.xml.namespace.QName("", "rgba");
    private static final javax.xml.namespace.QName LISTOFINT$6 = 
        new javax.xml.namespace.QName("", "list_of_int");
    private static final javax.xml.namespace.QName LISTOFFLOAT$8 = 
        new javax.xml.namespace.QName("", "list_of_float");
    private static final javax.xml.namespace.QName NAME$10 = 
        new javax.xml.namespace.QName("", "name");
    private static final javax.xml.namespace.QName VALUE$12 = 
        new javax.xml.namespace.QName("", "value");
    private static final javax.xml.namespace.QName TYPE$14 = 
        new javax.xml.namespace.QName("", "type");
    
    
    /**
     * Gets the "matrix" element
     */
    public java.util.List getMatrix()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MATRIX$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "matrix" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.MatrixType xgetMatrix()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.MatrixType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.MatrixType)get_store().find_element_user(MATRIX$0, 0);
            return target;
        }
    }
    
    /**
     * True if has "matrix" element
     */
    public boolean isSetMatrix()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(MATRIX$0) != 0;
        }
    }
    
    /**
     * Sets the "matrix" element
     */
    public void setMatrix(java.util.List matrix)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MATRIX$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MATRIX$0);
            }
            target.setListValue(matrix);
        }
    }
    
    /**
     * Sets (as xml) the "matrix" element
     */
    public void xsetMatrix(de.grogra.ext.exchangegraph.xmlbeans.MatrixType matrix)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.MatrixType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.MatrixType)get_store().find_element_user(MATRIX$0, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.MatrixType)get_store().add_element_user(MATRIX$0);
            }
            target.set(matrix);
        }
    }
    
    /**
     * Unsets the "matrix" element
     */
    public void unsetMatrix()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(MATRIX$0, 0);
        }
    }
    
    /**
     * Gets the "rgb" element
     */
    public java.util.List getRgb()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RGB$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "rgb" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.RgbType xgetRgb()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.RgbType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.RgbType)get_store().find_element_user(RGB$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "rgb" element
     */
    public boolean isSetRgb()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RGB$2) != 0;
        }
    }
    
    /**
     * Sets the "rgb" element
     */
    public void setRgb(java.util.List rgb)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RGB$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RGB$2);
            }
            target.setListValue(rgb);
        }
    }
    
    /**
     * Sets (as xml) the "rgb" element
     */
    public void xsetRgb(de.grogra.ext.exchangegraph.xmlbeans.RgbType rgb)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.RgbType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.RgbType)get_store().find_element_user(RGB$2, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.RgbType)get_store().add_element_user(RGB$2);
            }
            target.set(rgb);
        }
    }
    
    /**
     * Unsets the "rgb" element
     */
    public void unsetRgb()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RGB$2, 0);
        }
    }
    
    /**
     * Gets the "rgba" element
     */
    public java.util.List getRgba()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RGBA$4, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "rgba" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.RgbaType xgetRgba()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.RgbaType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.RgbaType)get_store().find_element_user(RGBA$4, 0);
            return target;
        }
    }
    
    /**
     * True if has "rgba" element
     */
    public boolean isSetRgba()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(RGBA$4) != 0;
        }
    }
    
    /**
     * Sets the "rgba" element
     */
    public void setRgba(java.util.List rgba)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(RGBA$4, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(RGBA$4);
            }
            target.setListValue(rgba);
        }
    }
    
    /**
     * Sets (as xml) the "rgba" element
     */
    public void xsetRgba(de.grogra.ext.exchangegraph.xmlbeans.RgbaType rgba)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.RgbaType target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.RgbaType)get_store().find_element_user(RGBA$4, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.RgbaType)get_store().add_element_user(RGBA$4);
            }
            target.set(rgba);
        }
    }
    
    /**
     * Unsets the "rgba" element
     */
    public void unsetRgba()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(RGBA$4, 0);
        }
    }
    
    /**
     * Gets the "list_of_int" element
     */
    public java.util.List getListOfInt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LISTOFINT$6, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "list_of_int" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ListOfInt xgetListOfInt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ListOfInt target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfInt)get_store().find_element_user(LISTOFINT$6, 0);
            return target;
        }
    }
    
    /**
     * True if has "list_of_int" element
     */
    public boolean isSetListOfInt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LISTOFINT$6) != 0;
        }
    }
    
    /**
     * Sets the "list_of_int" element
     */
    public void setListOfInt(java.util.List listOfInt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LISTOFINT$6, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LISTOFINT$6);
            }
            target.setListValue(listOfInt);
        }
    }
    
    /**
     * Sets (as xml) the "list_of_int" element
     */
    public void xsetListOfInt(de.grogra.ext.exchangegraph.xmlbeans.ListOfInt listOfInt)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ListOfInt target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfInt)get_store().find_element_user(LISTOFINT$6, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfInt)get_store().add_element_user(LISTOFINT$6);
            }
            target.set(listOfInt);
        }
    }
    
    /**
     * Unsets the "list_of_int" element
     */
    public void unsetListOfInt()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LISTOFINT$6, 0);
        }
    }
    
    /**
     * Gets the "list_of_float" element
     */
    public java.util.List getListOfFloat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LISTOFFLOAT$8, 0);
            if (target == null)
            {
                return null;
            }
            return target.getListValue();
        }
    }
    
    /**
     * Gets (as xml) the "list_of_float" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat xgetListOfFloat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat)get_store().find_element_user(LISTOFFLOAT$8, 0);
            return target;
        }
    }
    
    /**
     * True if has "list_of_float" element
     */
    public boolean isSetListOfFloat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(LISTOFFLOAT$8) != 0;
        }
    }
    
    /**
     * Sets the "list_of_float" element
     */
    public void setListOfFloat(java.util.List listOfFloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(LISTOFFLOAT$8, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(LISTOFFLOAT$8);
            }
            target.setListValue(listOfFloat);
        }
    }
    
    /**
     * Sets (as xml) the "list_of_float" element
     */
    public void xsetListOfFloat(de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat listOfFloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat)get_store().find_element_user(LISTOFFLOAT$8, 0);
            if (target == null)
            {
                target = (de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat)get_store().add_element_user(LISTOFFLOAT$8);
            }
            target.set(listOfFloat);
        }
    }
    
    /**
     * Unsets the "list_of_float" element
     */
    public void unsetListOfFloat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(LISTOFFLOAT$8, 0);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(NAME$10);
            return target;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(NAME$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(NAME$10);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(NAME$10);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(NAME$10);
            }
            target.set(name);
        }
    }
    
    /**
     * Gets the "value" attribute
     */
    public java.lang.String getValue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VALUE$12);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "value" attribute
     */
    public org.apache.xmlbeans.XmlString xgetValue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(VALUE$12);
            return target;
        }
    }
    
    /**
     * True if has "value" attribute
     */
    public boolean isSetValue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().find_attribute_user(VALUE$12) != null;
        }
    }
    
    /**
     * Sets the "value" attribute
     */
    public void setValue(java.lang.String value)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(VALUE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(VALUE$12);
            }
            target.setStringValue(value);
        }
    }
    
    /**
     * Sets (as xml) the "value" attribute
     */
    public void xsetValue(org.apache.xmlbeans.XmlString value)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(VALUE$12);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(VALUE$12);
            }
            target.set(value);
        }
    }
    
    /**
     * Unsets the "value" attribute
     */
    public void unsetValue()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_attribute(VALUE$12);
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$14);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$14);
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
            return get_store().find_attribute_user(TYPE$14) != null;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(TYPE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(TYPE$14);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(TYPE$14);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(TYPE$14);
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
            get_store().remove_attribute(TYPE$14);
        }
    }
}
