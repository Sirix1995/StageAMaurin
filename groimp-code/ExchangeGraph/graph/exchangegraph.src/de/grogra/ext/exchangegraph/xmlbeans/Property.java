/*
 * XML Type:  Property
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Property
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans;


/**
 * An XML Property(@).
 *
 * This is a complex type.
 */
public interface Property extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Property.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s39D0E01DA061D3219FD31D6307765808").resolveHandle("propertyd35etype");
    
    /**
     * Gets the "matrix" element
     */
    java.util.List getMatrix();
    
    /**
     * Gets (as xml) the "matrix" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.MatrixType xgetMatrix();
    
    /**
     * True if has "matrix" element
     */
    boolean isSetMatrix();
    
    /**
     * Sets the "matrix" element
     */
    void setMatrix(java.util.List matrix);
    
    /**
     * Sets (as xml) the "matrix" element
     */
    void xsetMatrix(de.grogra.ext.exchangegraph.xmlbeans.MatrixType matrix);
    
    /**
     * Unsets the "matrix" element
     */
    void unsetMatrix();
    
    /**
     * Gets the "rgb" element
     */
    java.util.List getRgb();
    
    /**
     * Gets (as xml) the "rgb" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.RgbType xgetRgb();
    
    /**
     * True if has "rgb" element
     */
    boolean isSetRgb();
    
    /**
     * Sets the "rgb" element
     */
    void setRgb(java.util.List rgb);
    
    /**
     * Sets (as xml) the "rgb" element
     */
    void xsetRgb(de.grogra.ext.exchangegraph.xmlbeans.RgbType rgb);
    
    /**
     * Unsets the "rgb" element
     */
    void unsetRgb();
    
    /**
     * Gets the "rgba" element
     */
    java.util.List getRgba();
    
    /**
     * Gets (as xml) the "rgba" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.RgbaType xgetRgba();
    
    /**
     * True if has "rgba" element
     */
    boolean isSetRgba();
    
    /**
     * Sets the "rgba" element
     */
    void setRgba(java.util.List rgba);
    
    /**
     * Sets (as xml) the "rgba" element
     */
    void xsetRgba(de.grogra.ext.exchangegraph.xmlbeans.RgbaType rgba);
    
    /**
     * Unsets the "rgba" element
     */
    void unsetRgba();
    
    /**
     * Gets the "list_of_int" element
     */
    java.util.List getListOfInt();
    
    /**
     * Gets (as xml) the "list_of_int" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ListOfInt xgetListOfInt();
    
    /**
     * True if has "list_of_int" element
     */
    boolean isSetListOfInt();
    
    /**
     * Sets the "list_of_int" element
     */
    void setListOfInt(java.util.List listOfInt);
    
    /**
     * Sets (as xml) the "list_of_int" element
     */
    void xsetListOfInt(de.grogra.ext.exchangegraph.xmlbeans.ListOfInt listOfInt);
    
    /**
     * Unsets the "list_of_int" element
     */
    void unsetListOfInt();
    
    /**
     * Gets the "list_of_float" element
     */
    java.util.List getListOfFloat();
    
    /**
     * Gets (as xml) the "list_of_float" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat xgetListOfFloat();
    
    /**
     * True if has "list_of_float" element
     */
    boolean isSetListOfFloat();
    
    /**
     * Sets the "list_of_float" element
     */
    void setListOfFloat(java.util.List listOfFloat);
    
    /**
     * Sets (as xml) the "list_of_float" element
     */
    void xsetListOfFloat(de.grogra.ext.exchangegraph.xmlbeans.ListOfFloat listOfFloat);
    
    /**
     * Unsets the "list_of_float" element
     */
    void unsetListOfFloat();
    
    /**
     * Gets the "name" attribute
     */
    java.lang.String getName();
    
    /**
     * Gets (as xml) the "name" attribute
     */
    org.apache.xmlbeans.XmlString xgetName();
    
    /**
     * Sets the "name" attribute
     */
    void setName(java.lang.String name);
    
    /**
     * Sets (as xml) the "name" attribute
     */
    void xsetName(org.apache.xmlbeans.XmlString name);
    
    /**
     * Gets the "value" attribute
     */
    java.lang.String getValue();
    
    /**
     * Gets (as xml) the "value" attribute
     */
    org.apache.xmlbeans.XmlString xgetValue();
    
    /**
     * True if has "value" attribute
     */
    boolean isSetValue();
    
    /**
     * Sets the "value" attribute
     */
    void setValue(java.lang.String value);
    
    /**
     * Sets (as xml) the "value" attribute
     */
    void xsetValue(org.apache.xmlbeans.XmlString value);
    
    /**
     * Unsets the "value" attribute
     */
    void unsetValue();
    
    /**
     * Gets the "type" attribute
     */
    java.lang.String getType();
    
    /**
     * Gets (as xml) the "type" attribute
     */
    org.apache.xmlbeans.XmlString xgetType();
    
    /**
     * True if has "type" attribute
     */
    boolean isSetType();
    
    /**
     * Sets the "type" attribute
     */
    void setType(java.lang.String type);
    
    /**
     * Sets (as xml) the "type" attribute
     */
    void xsetType(org.apache.xmlbeans.XmlString type);
    
    /**
     * Unsets the "type" attribute
     */
    void unsetType();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static de.grogra.ext.exchangegraph.xmlbeans.Property newInstance() {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.Property parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Property) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
