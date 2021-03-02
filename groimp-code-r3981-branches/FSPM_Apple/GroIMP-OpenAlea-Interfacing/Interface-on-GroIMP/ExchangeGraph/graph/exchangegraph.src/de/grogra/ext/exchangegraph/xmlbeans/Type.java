/*
 * XML Type:  Type
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Type
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans;


/**
 * An XML Type(@).
 *
 * This is a complex type.
 */
public interface Type extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Type.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s39D0E01DA061D3219FD31D6307765808").resolveHandle("typeb143type");
    
    /**
     * Gets the "extends" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ExtendsType getExtends();
    
    /**
     * Sets the "extends" element
     */
    void setExtends(de.grogra.ext.exchangegraph.xmlbeans.ExtendsType xextends);
    
    /**
     * Appends and returns a new empty "extends" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ExtendsType addNewExtends();
    
    /**
     * Gets a List of "implements" elements
     */
    java.util.List<de.grogra.ext.exchangegraph.xmlbeans.ImplementsType> getImplementsList();
    
    /**
     * Gets array of all "implements" elements
     * @deprecated
     */
    de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[] getImplementsArray();
    
    /**
     * Gets ith "implements" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ImplementsType getImplementsArray(int i);
    
    /**
     * Returns number of "implements" element
     */
    int sizeOfImplementsArray();
    
    /**
     * Sets array of all "implements" element
     */
    void setImplementsArray(de.grogra.ext.exchangegraph.xmlbeans.ImplementsType[] ximplementsArray);
    
    /**
     * Sets ith "implements" element
     */
    void setImplementsArray(int i, de.grogra.ext.exchangegraph.xmlbeans.ImplementsType ximplements);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "implements" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ImplementsType insertNewImplements(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "implements" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.ImplementsType addNewImplements();
    
    /**
     * Removes the ith "implements" element
     */
    void removeImplements(int i);
    
    /**
     * Gets a List of "property" elements
     */
    java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Property> getPropertyList();
    
    /**
     * Gets array of all "property" elements
     * @deprecated
     */
    de.grogra.ext.exchangegraph.xmlbeans.Property[] getPropertyArray();
    
    /**
     * Gets ith "property" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.Property getPropertyArray(int i);
    
    /**
     * Returns number of "property" element
     */
    int sizeOfPropertyArray();
    
    /**
     * Sets array of all "property" element
     */
    void setPropertyArray(de.grogra.ext.exchangegraph.xmlbeans.Property[] propertyArray);
    
    /**
     * Sets ith "property" element
     */
    void setPropertyArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Property property);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "property" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.Property insertNewProperty(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "property" element
     */
    de.grogra.ext.exchangegraph.xmlbeans.Property addNewProperty();
    
    /**
     * Removes the ith "property" element
     */
    void removeProperty(int i);
    
    /**
     * Gets the "name" attribute
     */
    java.lang.String getName();
    
    /**
     * Gets (as xml) the "name" attribute
     */
    org.apache.xmlbeans.XmlString xgetName();
    
    /**
     * True if has "name" attribute
     */
    boolean isSetName();
    
    /**
     * Sets the "name" attribute
     */
    void setName(java.lang.String name);
    
    /**
     * Sets (as xml) the "name" attribute
     */
    void xsetName(org.apache.xmlbeans.XmlString name);
    
    /**
     * Unsets the "name" attribute
     */
    void unsetName();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static de.grogra.ext.exchangegraph.xmlbeans.Type newInstance() {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.Type parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.Type) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
