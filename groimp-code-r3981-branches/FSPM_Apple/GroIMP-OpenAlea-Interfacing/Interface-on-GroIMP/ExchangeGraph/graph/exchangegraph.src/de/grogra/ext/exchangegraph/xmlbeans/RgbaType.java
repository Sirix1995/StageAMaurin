/*
 * XML Type:  rgba_type
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.RgbaType
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans;


/**
 * An XML rgba_type(@).
 *
 * This is a list type whose items are de.grogra.ext.exchangegraph.xmlbeans.FloatType.
 */
public interface RgbaType extends de.grogra.ext.exchangegraph.xmlbeans.Float4X1Type
{
    java.util.List getListValue();
    java.util.List xgetListValue();
    void setListValue(java.util.List list);
    /** @deprecated */
    java.util.List listValue();
    /** @deprecated */
    java.util.List xlistValue();
    /** @deprecated */
    void set(java.util.List list);
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(RgbaType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s39D0E01DA061D3219FD31D6307765808").resolveHandle("rgbatype231ctype");
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType newValue(java.lang.Object obj) {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) type.newValue( obj ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType newInstance() {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static de.grogra.ext.exchangegraph.xmlbeans.RgbaType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (de.grogra.ext.exchangegraph.xmlbeans.RgbaType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
