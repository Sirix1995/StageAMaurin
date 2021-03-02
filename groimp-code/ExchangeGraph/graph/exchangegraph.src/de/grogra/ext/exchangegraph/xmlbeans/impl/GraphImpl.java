/*
 * XML Type:  Graph
 * Namespace: 
 * Java type: de.grogra.ext.exchangegraph.xmlbeans.Graph
 *
 * Automatically generated - do not modify.
 */
package de.grogra.ext.exchangegraph.xmlbeans.impl;
/**
 * An XML Graph(@).
 *
 * This is a complex type.
 */
public class GraphImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements de.grogra.ext.exchangegraph.xmlbeans.Graph
{
    
    public GraphImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TYPE$0 = 
        new javax.xml.namespace.QName("", "type");
    private static final javax.xml.namespace.QName ROOT$2 = 
        new javax.xml.namespace.QName("", "root");
    private static final javax.xml.namespace.QName NODE$4 = 
        new javax.xml.namespace.QName("", "node");
    private static final javax.xml.namespace.QName EDGE$6 = 
        new javax.xml.namespace.QName("", "edge");
    
    
    /**
     * Gets a List of "type" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Type> getTypeList()
    {
        final class TypeList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Type>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Type get(int i)
                { return GraphImpl.this.getTypeArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Type set(int i, de.grogra.ext.exchangegraph.xmlbeans.Type o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Type old = GraphImpl.this.getTypeArray(i);
                GraphImpl.this.setTypeArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Type o)
                { GraphImpl.this.insertNewType(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Type remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Type old = GraphImpl.this.getTypeArray(i);
                GraphImpl.this.removeType(i);
                return old;
            }
            
            public int size()
                { return GraphImpl.this.sizeOfTypeArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new TypeList();
        }
    }
    
    /**
     * Gets array of all "type" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Type[] getTypeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(TYPE$0, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.Type[] result = new de.grogra.ext.exchangegraph.xmlbeans.Type[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "type" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Type getTypeArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Type target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Type)get_store().find_element_user(TYPE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "type" element
     */
    public int sizeOfTypeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TYPE$0);
        }
    }
    
    /**
     * Sets array of all "type" element
     */
    public void setTypeArray(de.grogra.ext.exchangegraph.xmlbeans.Type[] typeArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(typeArray, TYPE$0);
        }
    }
    
    /**
     * Sets ith "type" element
     */
    public void setTypeArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Type type)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Type target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Type)get_store().find_element_user(TYPE$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(type);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "type" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Type insertNewType(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Type target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Type)get_store().insert_element_user(TYPE$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "type" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Type addNewType()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Type target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Type)get_store().add_element_user(TYPE$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "type" element
     */
    public void removeType(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TYPE$0, i);
        }
    }
    
    /**
     * Gets a List of "root" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Root> getRootList()
    {
        final class RootList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Root>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Root get(int i)
                { return GraphImpl.this.getRootArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Root set(int i, de.grogra.ext.exchangegraph.xmlbeans.Root o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Root old = GraphImpl.this.getRootArray(i);
                GraphImpl.this.setRootArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Root o)
                { GraphImpl.this.insertNewRoot(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Root remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Root old = GraphImpl.this.getRootArray(i);
                GraphImpl.this.removeRoot(i);
                return old;
            }
            
            public int size()
                { return GraphImpl.this.sizeOfRootArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new RootList();
        }
    }
    
    /**
     * Gets array of all "root" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Root[] getRootArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(ROOT$2, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.Root[] result = new de.grogra.ext.exchangegraph.xmlbeans.Root[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "root" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Root getRootArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Root target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Root)get_store().find_element_user(ROOT$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "root" element
     */
    public int sizeOfRootArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(ROOT$2);
        }
    }
    
    /**
     * Sets array of all "root" element
     */
    public void setRootArray(de.grogra.ext.exchangegraph.xmlbeans.Root[] rootArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(rootArray, ROOT$2);
        }
    }
    
    /**
     * Sets ith "root" element
     */
    public void setRootArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Root root)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Root target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Root)get_store().find_element_user(ROOT$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(root);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "root" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Root insertNewRoot(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Root target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Root)get_store().insert_element_user(ROOT$2, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "root" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Root addNewRoot()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Root target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Root)get_store().add_element_user(ROOT$2);
            return target;
        }
    }
    
    /**
     * Removes the ith "root" element
     */
    public void removeRoot(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(ROOT$2, i);
        }
    }
    
    /**
     * Gets a List of "node" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Node> getNodeList()
    {
        final class NodeList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Node>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Node get(int i)
                { return GraphImpl.this.getNodeArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Node set(int i, de.grogra.ext.exchangegraph.xmlbeans.Node o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Node old = GraphImpl.this.getNodeArray(i);
                GraphImpl.this.setNodeArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Node o)
                { GraphImpl.this.insertNewNode(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Node remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Node old = GraphImpl.this.getNodeArray(i);
                GraphImpl.this.removeNode(i);
                return old;
            }
            
            public int size()
                { return GraphImpl.this.sizeOfNodeArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new NodeList();
        }
    }
    
    /**
     * Gets array of all "node" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Node[] getNodeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(NODE$4, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.Node[] result = new de.grogra.ext.exchangegraph.xmlbeans.Node[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "node" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Node getNodeArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Node target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Node)get_store().find_element_user(NODE$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "node" element
     */
    public int sizeOfNodeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(NODE$4);
        }
    }
    
    /**
     * Sets array of all "node" element
     */
    public void setNodeArray(de.grogra.ext.exchangegraph.xmlbeans.Node[] nodeArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(nodeArray, NODE$4);
        }
    }
    
    /**
     * Sets ith "node" element
     */
    public void setNodeArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Node node)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Node target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Node)get_store().find_element_user(NODE$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(node);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "node" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Node insertNewNode(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Node target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Node)get_store().insert_element_user(NODE$4, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "node" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Node addNewNode()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Node target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Node)get_store().add_element_user(NODE$4);
            return target;
        }
    }
    
    /**
     * Removes the ith "node" element
     */
    public void removeNode(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(NODE$4, i);
        }
    }
    
    /**
     * Gets a List of "edge" elements
     */
    public java.util.List<de.grogra.ext.exchangegraph.xmlbeans.Edge> getEdgeList()
    {
        final class EdgeList extends java.util.AbstractList<de.grogra.ext.exchangegraph.xmlbeans.Edge>
        {
            public de.grogra.ext.exchangegraph.xmlbeans.Edge get(int i)
                { return GraphImpl.this.getEdgeArray(i); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Edge set(int i, de.grogra.ext.exchangegraph.xmlbeans.Edge o)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Edge old = GraphImpl.this.getEdgeArray(i);
                GraphImpl.this.setEdgeArray(i, o);
                return old;
            }
            
            public void add(int i, de.grogra.ext.exchangegraph.xmlbeans.Edge o)
                { GraphImpl.this.insertNewEdge(i).set(o); }
            
            public de.grogra.ext.exchangegraph.xmlbeans.Edge remove(int i)
            {
                de.grogra.ext.exchangegraph.xmlbeans.Edge old = GraphImpl.this.getEdgeArray(i);
                GraphImpl.this.removeEdge(i);
                return old;
            }
            
            public int size()
                { return GraphImpl.this.sizeOfEdgeArray(); }
            
        }
        
        synchronized (monitor())
        {
            check_orphaned();
            return new EdgeList();
        }
    }
    
    /**
     * Gets array of all "edge" elements
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Edge[] getEdgeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(EDGE$6, targetList);
            de.grogra.ext.exchangegraph.xmlbeans.Edge[] result = new de.grogra.ext.exchangegraph.xmlbeans.Edge[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "edge" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Edge getEdgeArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Edge target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Edge)get_store().find_element_user(EDGE$6, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "edge" element
     */
    public int sizeOfEdgeArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(EDGE$6);
        }
    }
    
    /**
     * Sets array of all "edge" element
     */
    public void setEdgeArray(de.grogra.ext.exchangegraph.xmlbeans.Edge[] edgeArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(edgeArray, EDGE$6);
        }
    }
    
    /**
     * Sets ith "edge" element
     */
    public void setEdgeArray(int i, de.grogra.ext.exchangegraph.xmlbeans.Edge edge)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Edge target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Edge)get_store().find_element_user(EDGE$6, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(edge);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "edge" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Edge insertNewEdge(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Edge target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Edge)get_store().insert_element_user(EDGE$6, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "edge" element
     */
    public de.grogra.ext.exchangegraph.xmlbeans.Edge addNewEdge()
    {
        synchronized (monitor())
        {
            check_orphaned();
            de.grogra.ext.exchangegraph.xmlbeans.Edge target = null;
            target = (de.grogra.ext.exchangegraph.xmlbeans.Edge)get_store().add_element_user(EDGE$6);
            return target;
        }
    }
    
    /**
     * Removes the ith "edge" element
     */
    public void removeEdge(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(EDGE$6, i);
        }
    }
}
