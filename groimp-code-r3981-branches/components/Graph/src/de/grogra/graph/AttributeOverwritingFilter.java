
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.graph;

/**
 * This graph filter may be used as superclass for graph filters
 * which overwrite attribute values of their source graphs.
 * It implements the attribute-related methods such that the attributes
 * of the filtered graph are the attributes of the source graph plus
 * the additionally defined attributes (which may be overwritten
 * attributes of the source graph). The additional attributes have to be
 * declared by overriding the methods {@link #initNodeAccessors(AccessorMap)}
 * and {@link #initEdgeAccessors(AccessorMap)}.
 *
 * @author Ole Kniemeyer
 */
public abstract class AttributeOverwritingFilter extends GraphFilter
{
	private final AccessorMap nodeAccessors;
	private final AccessorMap edgeAccessors;
	
	public AttributeOverwritingFilter (Graph source)
	{
		super (source);
		nodeAccessors = new AccessorMap ();
		edgeAccessors = new AccessorMap ();
	}
	
	
	protected void initAttributeOverwritingFilter ()
	{
		initNodeAccessors (nodeAccessors);
		nodeAccessors.setReadOnly ();
		initEdgeAccessors (edgeAccessors);
		edgeAccessors.setReadOnly ();
	}
	
	
	/**
	 * Additional (possibly overwritten) attributes for nodes have
	 * to be declared in subclasses by overriding this method.
	 * For each overwritten attribute, a suitable accessor has to be added
	 * to <code>accessors</code>. Instances of
	 * {@link GraphFilter.AccessorBridge} may be used for this purpose.
	 * 
	 * @param accessors set of node accessors 
	 */
	protected void initNodeAccessors (AccessorMap accessors)
	{
	}

	
	/**
	 * Additional (possibly overwritten) attributes for edges have
	 * to be declared in subclasses by overriding this method.
	 * For each overwritten attribute, a suitable accessor has to be added
	 * to <code>accessors</code>. Instances of
	 * {@link GraphFilter.AccessorBridge} may be used for this purpose.
	 * 
	 * @param accessors set of node accessors 
	 */
	protected void initEdgeAccessors (AccessorMap accessors)
	{
	}

	@Override
	public Attribute[] getAttributes (Object object, boolean asNode)
	{
		return (asNode ? nodeAccessors : edgeAccessors)
			.getAttributes (super.getAttributes (object, asNode));
	}

	
	@Override
	public AttributeAccessor getAccessor
		(Object object, boolean asNode, Attribute attribute)
	{
		AttributeAccessor a = (asNode ? nodeAccessors : edgeAccessors).getAccessor (attribute);
		return (a != null) ? a : super.getAccessor (object, asNode, attribute);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
#if ($pp.Object)
	 * @param placeIn an instance for the result may be provided by the caller 
#end
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 $C
	@Override
	protected $type get$pp.Type (Object object, AccessorBridge accessor,
#if ($pp.Object)
								 Object placeIn,
#end
								 GraphState gs)
	{
		return getSourceState (gs).get$pp.Type
			(object, accessor.forNode,
#if ($pp.Object)
			 placeIn,
#end
			 (${pp.Type}Attribute) accessor.getAttribute ());
	}


	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 $C
	@Override
	protected $type set$pp.Type (Object object, AccessorBridge accessor,
								 $type value, GraphState gs)
	{
		return ((${pp.Type}Attribute) accessor.getAttribute ())
			.set$pp.Type (object, accessor.forNode, value, getSourceState (gs));
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected boolean getBoolean (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getBoolean
			(object, accessor.forNode,
			 (BooleanAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected boolean setBoolean (Object object, AccessorBridge accessor,
								 boolean value, GraphState gs)
	{
		return ((BooleanAttribute) accessor.getAttribute ())
			.setBoolean (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected byte getByte (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getByte
			(object, accessor.forNode,
			 (ByteAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected byte setByte (Object object, AccessorBridge accessor,
								 byte value, GraphState gs)
	{
		return ((ByteAttribute) accessor.getAttribute ())
			.setByte (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected short getShort (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getShort
			(object, accessor.forNode,
			 (ShortAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected short setShort (Object object, AccessorBridge accessor,
								 short value, GraphState gs)
	{
		return ((ShortAttribute) accessor.getAttribute ())
			.setShort (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected char getChar (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getChar
			(object, accessor.forNode,
			 (CharAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected char setChar (Object object, AccessorBridge accessor,
								 char value, GraphState gs)
	{
		return ((CharAttribute) accessor.getAttribute ())
			.setChar (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected int getInt (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getInt
			(object, accessor.forNode,
			 (IntAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected int setInt (Object object, AccessorBridge accessor,
								 int value, GraphState gs)
	{
		return ((IntAttribute) accessor.getAttribute ())
			.setInt (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected long getLong (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getLong
			(object, accessor.forNode,
			 (LongAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected long setLong (Object object, AccessorBridge accessor,
								 long value, GraphState gs)
	{
		return ((LongAttribute) accessor.getAttribute ())
			.setLong (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected float getFloat (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getFloat
			(object, accessor.forNode,
			 (FloatAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected float setFloat (Object object, AccessorBridge accessor,
								 float value, GraphState gs)
	{
		return ((FloatAttribute) accessor.getAttribute ())
			.setFloat (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected double getDouble (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		return getSourceState (gs).getDouble
			(object, accessor.forNode,
			 (DoubleAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected double setDouble (Object object, AccessorBridge accessor,
								 double value, GraphState gs)
	{
		return ((DoubleAttribute) accessor.getAttribute ())
			.setDouble (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * obtain the value of an additional attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * returns the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param placeIn an instance for the result may be provided by the caller 
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected Object getObject (Object object, AccessorBridge accessor,
								 Object placeIn,
								 GraphState gs)
	{
		return getSourceState (gs).getObject
			(object, accessor.forNode,
			 placeIn,
			 (ObjectAttribute) accessor.getAttribute ());
	}
// generated
// generated
	/**
	 * This method has to be implemented by subclasses in order to
	 * set value of an additional writable attribute which is defined
	 * by an {@link GraphFilter.AccessorBridge}. The default implementation
	 * sets the value of <code>accessor</code>'s attribute 
	 * in the source graph.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	@Override
	protected Object setObject (Object object, AccessorBridge accessor,
								 Object value, GraphState gs)
	{
		return ((ObjectAttribute) accessor.getAttribute ())
			.setObject (object, accessor.forNode, value, getSourceState (gs));
	}
// generated
//!! *# End of generated code
}
