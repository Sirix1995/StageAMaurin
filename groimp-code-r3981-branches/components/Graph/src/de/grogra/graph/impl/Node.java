
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

package de.grogra.graph.impl;

import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import de.grogra.annotation.Choice;
import de.grogra.annotation.Editable;
import de.grogra.annotation.Range;
import de.grogra.graph.AccessorBase;
import de.grogra.graph.AccessorMap;
import de.grogra.graph.ArrayPath;
import de.grogra.graph.Attribute;
import de.grogra.graph.AttributeAccessor;
import de.grogra.graph.AttributeDependencies;
import de.grogra.graph.Attributes;
import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.BooleanAttributeAccessor;
import de.grogra.graph.ByteAttribute;
import de.grogra.graph.ByteAttributeAccessor;
import de.grogra.graph.CharAttribute;
import de.grogra.graph.CharAttributeAccessor;
import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.DoubleAttributeAccessor;
import de.grogra.graph.EdgePattern;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.FloatAttributeAccessor;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Instantiator;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.IntAttributeAccessor;
import de.grogra.graph.LongAttribute;
import de.grogra.graph.LongAttributeAccessor;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.ObjectAttributeAccessor;
import de.grogra.graph.Path;
import de.grogra.graph.ShortAttribute;
import de.grogra.graph.ShortAttributeAccessor;
import de.grogra.graph.SpecialEdgeDescriptor;
import de.grogra.graph.Visitor;
import de.grogra.graph.VisitorImpl;
import de.grogra.persistence.FieldAccessor;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.Observer;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.PersistenceManager;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.persistence.Transaction;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.BoundedType;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.FieldChain;
import de.grogra.reflect.FieldDecorator;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.UserFields;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XData;
import de.grogra.reflect.XObject;
import de.grogra.util.EnumerationType;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.xl.util.IntHashMap;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.XHashMap;

/**
 * This class represents the base class of nodes in a
 * {@link de.grogra.graph.impl.GraphManager}. Such a graph is used, e.g.,
 * as the project graph of the GroIMP software.
 * <p>
 * <code>Node</code> extends <code>Edge</code>: A <code>Node</code> is
 * able to represent not only itself, but also a single incoming edge.
 * The advantage of this feature is the reduced number of instances for
 * a given graph. E.g., a tree graph with <em>N</em> nodes has
 * <em>N-1</em> edges. Assuming that they point from a parent to child,
 * all these edges can be represented as part of the child node instances.
 * <p>
 * If a node is part of the graph of {@link de.grogra.graph.impl.GraphManager},
 * it can be identified by its unique {@linkplain #getId() id}. A node also
 * has a {@linkplain #getName() name}, however, this may be <code>null</code>
 * and is not unique. Nevertheless, the graph maintains a
 * {@linkplain de.grogra.graph.impl.GraphManager#getNodeForName(String) map}
 * from names to nodes which may be used to efficiently obtain nodes
 * having some specified name. 
 * 
 * @author Ole Kniemeyer
 */
public class Node extends Edge
	implements PersistenceCapable, Shareable, Map, XObject, UserFields
{
	public static final NType $TYPE;
	public static final NType.Field name$FIELD;
	public static final NType.Field layer$FIELD;
	public static final NType.Field extentIndex$FIELD;
	public static final NType.Field mark$FIELD;
	public static final NType.Field isInterpretive$FIELD;

	// for backward compatibility
	public static final NType.Field extentTail$FIELD;

	public static final MimeType MIME_TYPE
		= new MimeType ("application/x-grogra-graph");

	
	public static final int MIN_UNUSED_SPECIAL_OF_TARGET = Graph.EDGENODE_IN_EDGE + 1;
	public static final int MIN_UNUSED_SPECIAL_OF_SOURCE = Graph.EDGENODE_OUT_EDGE + 1;
	
	
	private static final int LAYER_MASK = 15;
	public static final int DELETED = 1 << 4;
	public static final int HAS_OBSERVERS = 1 << 5;
	public static final int IS_INTERPRETIVE = 1 << 6;
	public static final int EXTENT_BIT = 7;
	public static final int LAST_EXTENT_INDEX = 7;
	public static final int EXTENT_MASK = LAST_EXTENT_INDEX << EXTENT_BIT;
	public static final int MARK = 1 << 10;

	public static final int USED_BITS = 11;

	public static final ObjectAttribute ADDITIONAL_FIELDS;
	
	static
	{
		$TYPE = new NType (new Node ());
		name$FIELD = new NType.Field ($TYPE, "name", NType.Field.SCO, Type.STRING, null, true, null);
		$TYPE.addManagedField (name$FIELD);
		$TYPE.declareFieldAttribute (name$FIELD, Attributes.NAME);
		layer$FIELD = new NType.BitField ($TYPE, "layer", NType.Field.SCO, Type.INT, LAYER_MASK);
		$TYPE.addManagedField (layer$FIELD);
		$TYPE.declareFieldAttribute (layer$FIELD, Attributes.LAYER);
		extentIndex$FIELD = new NType.BitField ($TYPE, "extentIndex", NType.Field.SCO | NType.Field.HIDDEN, Type.INT, EXTENT_MASK);
		$TYPE.addManagedField (extentIndex$FIELD);
		extentTail$FIELD = new NType.BitField ($TYPE, "extentTail", NType.Field.SCO | NType.Field.HIDDEN, Type.BOOLEAN, EXTENT_MASK);
		mark$FIELD = new NType.BitField ($TYPE, "mark", NType.Field.SCO | NType.Field.HIDDEN, Type.BOOLEAN, MARK);
		$TYPE.addManagedField (mark$FIELD);
		isInterpretive$FIELD = new NType.BitField ($TYPE, "isInterpretive", NType.Field.SCO | NType.Field.HIDDEN, Type.BOOLEAN, IS_INTERPRETIVE);
		$TYPE.addManagedField (isInterpretive$FIELD);
		
		class FieldsAccessor extends ObjectAttribute.IdentityAccessor
		{
			FieldsAccessor (ObjectAttribute a)
			{
				a.super ();
			}

			@Override
			public Object setSubfield (Object object, FieldChain fields,
									   int[] indices, Object value, GraphState gs)
			{
				new IndirectField ().add (fields)
					.set (object, indices, value,
						  ((State) gs).getActiveTransaction ());
				return value;
			}

			@Override
			public boolean isWritable (Object object, GraphState gs)
			{
				return true;
			}
		}

		$TYPE.addAccessor (new FieldsAccessor (Attributes.USER_FIELDS));

		ADDITIONAL_FIELDS = Attributes.init
			(new ObjectAttribute ($TYPE.getClass (), false, null), "additionalFields", GraphManager.I18N);

		$TYPE.addAccessor (new FieldsAccessor (ADDITIONAL_FIELDS));

		$TYPE.declareSpecialEdge (Graph.EDGENODE_IN_EDGE, "edgenode.in", new Node[0]);
		$TYPE.declareSpecialEdge (Graph.EDGENODE_OUT_EDGE, "edgenode.out", new Node[0]);
		$TYPE.validate ();
	}


	/**
	 * This field packs the information of some <code>boolean</code>
	 * and integral attributes into a single 32-bit value. The bits defined
	 * in the base class <code>Node</code> are the first
	 * {@link #USED_BITS} bits, starting at bit 0.  
	 */
	protected int bits = 0;

	/**
	 * Used for additional data, see {@link #getXData()}.
	 */
	NodeData data = null;

	/**
	 * If this node additionally represents an incoming edge to itself,
	 * this is the source node of the incoming edge.
	 */
	private Node parent = null;

	/**
	 * The id of this node.
	 * 
	 * @see #getId()
	 */
	transient long id = -1L;

	/**
	 * The graph to which this node belongs.
	 */
	transient GraphManager manager = null;
	
	/**
	 * Previous node in doubly-linked list of type extent.
	 * 
	 * @see Extent
	 */
	transient Node extentPrev;
	
	/**
	 * Next node in doubly-linked list of type extent.
	 * 
	 * @see Extent
	 */
	transient Node extentNext;

	/**
	 * Next node in same hash bucket.
	 * 
	 * @see GraphManager
	 */
	transient Node hashBucketNext;

	/**
	 * Modificaton stamp of this node.
	 *
	 * @see #getStamp()
	 */
	private transient int stamp = 0;

	public static class FieldAttributeAccessor extends FieldAccessor
		implements
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
		${pp.Type}AttributeAccessor,
#end
!!*/
//!! #* Start of generated code
// generated
		BooleanAttributeAccessor,
// generated
		ByteAttributeAccessor,
// generated
		ShortAttributeAccessor,
// generated
		CharAttributeAccessor,
// generated
		IntAttributeAccessor,
// generated
		LongAttributeAccessor,
// generated
		FloatAttributeAccessor,
// generated
		DoubleAttributeAccessor,
//!! *# End of generated code
		ObjectAttributeAccessor
	{
		private final Attribute attr;
		private final boolean hasFactor;
		private final double field2Attr, attr2Field;


		public FieldAttributeAccessor (Attribute attr, NType.Field field)
		{
			super (field);
			this.attr = attr;
			this.hasFactor = false;
			this.field2Attr = 0;
			this.attr2Field = 0;
		}


		public FieldAttributeAccessor (Attribute attr, NType.Field field, double field2Attr)
		{
			super (field);
			this.attr = attr;
			this.hasFactor = true;
			this.field2Attr = field2Attr;
			this.attr2Field = 1 / field2Attr;
		}


		@Override
		public final Attribute getAttribute ()
		{
			return attr;
		}


		@Override
		public final Field getField ()
		{
			return Reflection.equal (attr.getType (), field.getType ())
				? field : null;
		}


		@Override
		public boolean isWritable (Object object, GraphState gs)
		{
			return !(field instanceof PersistenceField)
				|| ((PersistenceField) field).isWritable (object);
		}


		@Override
		public String toString ()
		{
			return "Accessor[" + attr + ',' + field + ']';
		}


		@Override
		public Object getObject (Object object, Object placeIn, GraphState gs)
		{
			try
			{
				return getObject (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)

		public
#if ($pp.object)
		final
#end
		$type get$pp.Type (Object object, GraphState gs)
		{
#if ($pp.object)
			return getObject (object, null, gs);
#else
			try
			{
#if ($pp.float)
				if (hasFactor)
				{
					return (float) field2Attr * getFloat (object);
				}
#elseif ($pp.double)
				if (hasFactor)
				{
					return field2Attr * getDouble (object);
				}
#end
				return get$pp.Type (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
#end
		}

		public $type set$pp.Type (Object object, $type value,
								  GraphState gs)
		{
			try
			{
#if ($pp.float)
				if (hasFactor)
				{
					setFloat (object, value * (float) attr2Field, ((State) gs).getActiveTransaction ());
					return value;
				}
#elseif ($pp.double)
				if (hasFactor)
				{
					setDouble (object, value * attr2Field, ((State) gs).getActiveTransaction ());
					return value;
				}
#end
				return set$pp.Type (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		@Override
		public
		boolean getBoolean (Object object, GraphState gs)
		{
			try
			{
				return getBoolean (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public boolean setBoolean (Object object, boolean value,
								  GraphState gs)
		{
			try
			{
				return setBoolean (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		byte getByte (Object object, GraphState gs)
		{
			try
			{
				return getByte (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public byte setByte (Object object, byte value,
								  GraphState gs)
		{
			try
			{
				return setByte (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		short getShort (Object object, GraphState gs)
		{
			try
			{
				return getShort (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public short setShort (Object object, short value,
								  GraphState gs)
		{
			try
			{
				return setShort (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		char getChar (Object object, GraphState gs)
		{
			try
			{
				return getChar (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public char setChar (Object object, char value,
								  GraphState gs)
		{
			try
			{
				return setChar (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		int getInt (Object object, GraphState gs)
		{
			try
			{
				return getInt (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public int setInt (Object object, int value,
								  GraphState gs)
		{
			try
			{
				return setInt (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		long getLong (Object object, GraphState gs)
		{
			try
			{
				return getLong (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public long setLong (Object object, long value,
								  GraphState gs)
		{
			try
			{
				return setLong (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		float getFloat (Object object, GraphState gs)
		{
			try
			{
				if (hasFactor)
				{
					return (float) field2Attr * getFloat (object);
				}
				return getFloat (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public float setFloat (Object object, float value,
								  GraphState gs)
		{
			try
			{
				if (hasFactor)
				{
					setFloat (object, value * (float) attr2Field, ((State) gs).getActiveTransaction ());
					return value;
				}
				return setFloat (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		double getDouble (Object object, GraphState gs)
		{
			try
			{
				if (hasFactor)
				{
					return field2Attr * getDouble (object);
				}
				return getDouble (object);
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
		@Override
		public double setDouble (Object object, double value,
								  GraphState gs)
		{
			try
			{
				if (hasFactor)
				{
					setDouble (object, value * attr2Field, ((State) gs).getActiveTransaction ());
					return value;
				}
				return setDouble (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
// generated
// generated
		@Override
		public
		final
		Object getObject (Object object, GraphState gs)
		{
			return getObject (object, null, gs);
		}
// generated
		@Override
		public Object setObject (Object object, Object value,
								  GraphState gs)
		{
			try
			{
				return setObject (object, value, ((State) gs).getActiveTransaction ());
			}
			catch (IllegalAccessException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
//!! *# End of generated code

		@Override
		public Object setSubfield (Object object, FieldChain field,
								   int[] indices, Object value, GraphState gs)
		{
			return setSubfield (object, field, indices, value, ((State) gs).getActiveTransaction ());
		}
	}


	public static class AccessorBridge extends AccessorBase
	{

		public AccessorBridge (Attribute attribute)
		{
			super (attribute);
		}


		@Override
		public boolean isWritable (Object object, GraphState gs)
		{
			return false;
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)

		public $type get$pp.Type (Object object, GraphState gs)
		{
#if ($pp.Object)
			return ((Node) object).get$pp.Type
				((${pp.Type}Attribute) attribute, null, gs);
#else
			return ((Node) object).get$pp.Type
				((${pp.Type}Attribute) attribute, gs);
#end
		}

#if ($pp.Object)

		public $type get$pp.Type (Object object, Object placeIn, GraphState gs)
		{
			return ((Node) object).get$pp.Type
				((${pp.Type}Attribute) attribute, placeIn, gs);
		}
#end

		public $type set$pp.Type (Object object, $type value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		@Override
		public boolean getBoolean (Object object, GraphState gs)
		{
			return ((Node) object).getBoolean
				((BooleanAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public boolean setBoolean (Object object, boolean value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public byte getByte (Object object, GraphState gs)
		{
			return ((Node) object).getByte
				((ByteAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public byte setByte (Object object, byte value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public short getShort (Object object, GraphState gs)
		{
			return ((Node) object).getShort
				((ShortAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public short setShort (Object object, short value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public char getChar (Object object, GraphState gs)
		{
			return ((Node) object).getChar
				((CharAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public char setChar (Object object, char value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public int getInt (Object object, GraphState gs)
		{
			return ((Node) object).getInt
				((IntAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public int setInt (Object object, int value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public long getLong (Object object, GraphState gs)
		{
			return ((Node) object).getLong
				((LongAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public long setLong (Object object, long value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public float getFloat (Object object, GraphState gs)
		{
			return ((Node) object).getFloat
				((FloatAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public float setFloat (Object object, float value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public double getDouble (Object object, GraphState gs)
		{
			return ((Node) object).getDouble
				((DoubleAttribute) attribute, gs);
		}
// generated
// generated
		@Override
		public double setDouble (Object object, double value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
// generated
// generated
		@Override
		public Object getObject (Object object, GraphState gs)
		{
			return ((Node) object).getObject
				((ObjectAttribute) attribute, null, gs);
		}
// generated
// generated
		@Override
		public Object getObject (Object object, Object placeIn, GraphState gs)
		{
			return ((Node) object).getObject
				((ObjectAttribute) attribute, placeIn, gs);
		}
// generated
		@Override
		public Object setObject (Object object, Object value, GraphState gs)
		{
			throw new UnsupportedOperationException ();
		}
//!! *# End of generated code

	}


	public static class NType extends ManageableType
	{
		int usedBitMasks;

		Field[] specialEdgeFields = null;

		final AccessorMap accessors;
		final AttributeDependencies dependencies;
		final Method getInstantiatorMethod;
		private final Node representative;
		private StringMap aliasFields; 
		private IntHashMap specialEdgeDescriptors = null;
		private XHashMap specialEdgeDescriptorsForKey = null;
		private ObjectList specialEdgeDescOfSource = null;
		private ObjectList specialEdgeDescOfTarget = null;

		private de.grogra.reflect.Field[] userFields;


		public static class Field extends ManageableType.Field implements XObject
		{
			Attribute attribute = null;
			Attribute dependentAttribute = null;

			int specialEdge = 0;
			private final de.grogra.reflect.Field wrappedField;
			private final boolean nameField;

			private transient XClass cls;
			private transient XData data;
			
			
			@Override
			public final void initXClass (XClass cls)
			{
				if (this.cls != null)
				{
					throw new IllegalStateException ();
				}
				this.cls = cls;
				data = new XData ();
				data.init (cls);
			}

			
			@Override
			public final XClass getXClass ()
			{
				return cls;
			}
			

			@Override
			public final XData getXData ()
			{
				return data;
			}


			public Field (NType declaring, String name, int modifiers,
						  Type type, Type componentType)
			{
				this (declaring, name, modifiers, type, componentType, false, null);
			}


			public Field (NType declaring, String name, int modifiers,
						  Class type, Class componentType, boolean wrapper)
			{
				this (declaring, name, modifiers, ClassAdapter.wrap (type),
					  (componentType == null) ? null
					  : ClassAdapter.wrap (componentType), false,
					  wrapper ? Reflection.getDeclaredField (declaring, name) : null);
			}


			public Field (NType declaring, de.grogra.reflect.Field wrapped, int modifiers)
			{
				this (declaring, wrapped.getSimpleName (), modifiers,
					  wrapped.getType (), null, false, wrapped);
			}


			Field (NType declaring, String name, int modifiers,
				   Type type, Type componentType, boolean nameField,
				   de.grogra.reflect.Field wrappedField)
			{
				declaring.super (name, modifiers, type, componentType);
				this.nameField = nameField;
				this.wrappedField = wrappedField;
			}


			public final Attribute getAttribute ()
			{
				return attribute;
			}


			public final Attribute getDependentAttribute ()
			{
				return dependentAttribute;
			}

/*!!
#foreach ($type in $types)
$pp.setType($type)

#if ($pp.Object)
			@Override
			protected void setObjectImpl (Object object, $type value)
			{
				if (nameField)
				{
					((Node) object).setName ((String) value);
				}
				else
#else
			@Override
			public void set$pp.Type (Object object, $type value)
			{
#end
				if (wrappedField != null)
				{
					try
					{
						wrappedField.set$pp.Type (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
#if ($pp.Object)
					super.setObjectImpl (object, value);
#else
					super.set$pp.Type (object, value);
#end
				}
			}


			@Override
			public $type get$pp.Type (Object object)
			{
#if ($pp.Object)
				if (nameField)
				{
					return ((Node) object).getName ();
				}
#end
				if (wrappedField == null)
				{
					return super.get$pp.Type (object);
				}
				try
				{
					return wrappedField.get$pp.Type (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
			@Override
			public void setBoolean (Object object, boolean value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setBoolean (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setBoolean (object, value);
				}
			}
// generated
// generated
			@Override
			public boolean getBoolean (Object object)
			{
				if (wrappedField == null)
				{
					return super.getBoolean (object);
				}
				try
				{
					return wrappedField.getBoolean (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setByte (Object object, byte value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setByte (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setByte (object, value);
				}
			}
// generated
// generated
			@Override
			public byte getByte (Object object)
			{
				if (wrappedField == null)
				{
					return super.getByte (object);
				}
				try
				{
					return wrappedField.getByte (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setShort (Object object, short value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setShort (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setShort (object, value);
				}
			}
// generated
// generated
			@Override
			public short getShort (Object object)
			{
				if (wrappedField == null)
				{
					return super.getShort (object);
				}
				try
				{
					return wrappedField.getShort (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setChar (Object object, char value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setChar (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setChar (object, value);
				}
			}
// generated
// generated
			@Override
			public char getChar (Object object)
			{
				if (wrappedField == null)
				{
					return super.getChar (object);
				}
				try
				{
					return wrappedField.getChar (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setInt (Object object, int value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setInt (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setInt (object, value);
				}
			}
// generated
// generated
			@Override
			public int getInt (Object object)
			{
				if (wrappedField == null)
				{
					return super.getInt (object);
				}
				try
				{
					return wrappedField.getInt (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setLong (Object object, long value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setLong (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setLong (object, value);
				}
			}
// generated
// generated
			@Override
			public long getLong (Object object)
			{
				if (wrappedField == null)
				{
					return super.getLong (object);
				}
				try
				{
					return wrappedField.getLong (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setFloat (Object object, float value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setFloat (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setFloat (object, value);
				}
			}
// generated
// generated
			@Override
			public float getFloat (Object object)
			{
				if (wrappedField == null)
				{
					return super.getFloat (object);
				}
				try
				{
					return wrappedField.getFloat (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			public void setDouble (Object object, double value)
			{
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setDouble (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setDouble (object, value);
				}
			}
// generated
// generated
			@Override
			public double getDouble (Object object)
			{
				if (wrappedField == null)
				{
					return super.getDouble (object);
				}
				try
				{
					return wrappedField.getDouble (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
// generated
// generated
			@Override
			protected void setObjectImpl (Object object, Object value)
			{
				if (nameField)
				{
					((Node) object).setName ((String) value);
				}
				else
				if (wrappedField != null)
				{
					try
					{
						wrappedField.setObject (object, value);
					}
					catch (Exception e)
					{
						throw new WrapException (e);
					}
				}
				else
				{
					super.setObjectImpl (object, value);
				}
			}
// generated
// generated
			@Override
			public Object getObject (Object object)
			{
				if (nameField)
				{
					return ((Node) object).getName ();
				}
				if (wrappedField == null)
				{
					return super.getObject (object);
				}
				try
				{
					return wrappedField.getObject (object);
				}
				catch (Exception e)
				{
					throw new WrapException (e);
				}
			}
//!! *# End of generated code

		}


		public static final class BitField extends Field
		{
			private final int mask, position;

			public BitField (NType declaring, String name, int modifiers,
							 Type type, int mask)
			{
				super (declaring, name, modifiers, type, null, false, null);
				if (!"extentTail".equals (name) && (declaring.usedBitMasks & mask) != 0)
				{
					throw new AssertionError ("Bitmasks overlap: " + declaring + '.' + name);
				}
				declaring.usedBitMasks |= mask;
				this.mask = mask;
				for (int i = 0; i < Graph.MAX_NORMAL_BIT_INDEX; i++)
				{
					if ((mask & 1) != 0)
					{
						position = i;
						return;
					}
					mask >>>= 1;
				}
				throw new AssertionError ("Bitmask is 0: " + declaring + '.' + name);
			}


			@Override
			public void setBoolean (Object object, boolean value)
			{
				if (value)
				{
					((Node) object).bits |= mask;
				}
				else
				{
					((Node) object).bits &= ~mask;
				}
			}


			@Override
			public boolean getBoolean (Object object)
			{
				return (((Node) object).bits & mask) != 0;
			}


			@Override
			public void setInt (Object object, int value)
			{
				((Node) object).bits = (((Node) object).bits & ~mask)
					| ((value << position) & mask);
			}


			@Override
			public int getInt (Object object)
			{
				return (((Node) object).bits & mask) >>> position;
			}
		}


		public NType (Node representative)
		{
			this ((representative.getXClass () != null) ? representative.getXClass ()
				  : (Type) ClassAdapter.wrap (representative.getClass ()),
				  representative);
		}


		public NType (Class type)
		{
			this (ClassAdapter.wrap (type), null);
		}

		
		public static NType create (Type type, NType supertype)
		{
			return new NType (type, supertype, null);
		}


		private NType (Type type, Node representative)
		{
			this (type, (NType) forType (type.getSupertype ()), representative);
		}


		private NType (Type type, NType supertype, Node representative)
		{
			super (type, supertype, false);
			accessors = (supertype == null) ? new AccessorMap ()
				: new AccessorMap (supertype.accessors);
			dependencies = (supertype == null) ? new AttributeDependencies ()
				: new AttributeDependencies (supertype.dependencies);
			usedBitMasks = (supertype == null) ? HAS_OBSERVERS
				: supertype.usedBitMasks;
			specialEdgeDescOfSource = (supertype == null) ? new ObjectList ()
				: supertype.specialEdgeDescOfSource;
			specialEdgeDescOfTarget = (supertype == null) ? new ObjectList ()
				: supertype.specialEdgeDescOfTarget;
			this.representative = representative;
			dup (supertype);
			Method m = Reflection.findMethodWithPrefixInTypes (type, "mgetInstantiator;()", false, true);
			if ((m != null)
				&& Node.class.getName ().equals (m.getDeclaringType ().getBinaryName ()))
			{
				m = null;
			}
			getInstantiatorMethod = m;
		}


		private void dup (NType s)
		{
			if (s != null)
			{
				if (s.specialEdgeFields != null)
				{
					specialEdgeFields = s.specialEdgeFields.clone ();
				}
			}
		}


		@Override
		public final Object getRepresentative ()
		{
			return representative;
		}


		@Override
		public Object newInstance () throws InvocationTargetException,
			InstantiationException, IllegalAccessException
		{
			if (representative != null)
			{
				try
				{
					Node n = representative.newInstance ();
					XClass c = representative.getXClass ();
					if (c != null)
					{
						n.initXClass (c);
					}
					return n;
				}
				catch (RuntimeException e)
				{
					throw new InvocationTargetException (e);
				}
			}
			else
			{
				return super.newInstance ();
			}
		}


		public final void addDependency (Attribute src, Attribute dest)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			dependencies.add (src, dest);
		}


		public final void addAccessor (AttributeAccessor a)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			accessors.add (a);
		}


		public final void addIdentityAccessor (ObjectAttribute a)
		{
			addAccessor (a.new IdentityAccessor ());
		}


		public final void declareFieldAttribute (Field f, Attribute a)
		{
			addAccessor (new FieldAttributeAccessor (a, f));
			setAttribute (f, a);
		}


		public final void declareFieldAttribute (Field f, Attribute a, double field2Attr)
		{
			addAccessor (new FieldAttributeAccessor (a, f, field2Attr));
			setAttribute (f, a);
		}


		public final void setDependentAttribute (Field f, Attribute a)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			f.dependentAttribute = a;
		}


		public final void setAttribute (Field f, Attribute a)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			f.dependentAttribute = a;
			f.attribute = a;
		}


		public final int getAttributeCount ()
		{
			return accessors.size ();
		}


		public final AttributeAccessor getAccessor (int index)
		{
			return accessors.getAccessor (index);
		}


		public final AttributeAccessor getAccessorById (int id)
		{
			return accessors.getAccessorById (id);
		}

		
		private final HashMap methods = new HashMap ();
		
		public final synchronized Method getMethod (String descriptor)
		{
			Object m = methods.get (descriptor);
			if (m == this)
			{
				return null;
			}
			if (m == null)
			{
				m = Reflection.findMethodWithPrefixInTypes (this, descriptor, false, true);
				methods.put (descriptor, (m != null) ? m : this);
			}
			return (Method) m;
		}


		public final void setSpecialEdgeField (Field field, int edge)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			if (((edge & Graph.SPECIAL_EDGE_MASK) == 0) || ((edge & ~Graph.SPECIAL_EDGE_MASK) != 0))
			{
				throw new IllegalArgumentException ("Special edge " + edge);
			}
			field.specialEdge = edge;
			if (specialEdgeFields == null)
			{
				specialEdgeFields = new Field[1 << Graph.MIN_NORMAL_BIT_INDEX];
			}
			specialEdgeFields[edge] = field;
		}

		
		public SpecialEdgeDescriptor getSpecialEdgeDescriptor (int bits)
		{
			for (NType t = this; t != null; t = (NType) t.getManageableSupertype ())
			{
				Object d;
				if ((t.specialEdgeDescriptors != null)
					&& ((d = t.specialEdgeDescriptors.get (bits, null)) != null))
				{
					return (SpecialEdgeDescriptor) d;
				}
			}
			return null;
		}

		
		public SpecialEdgeDescriptor getSpecialEdgeDescriptor (String key)
		{
			for (NType t = this; t != null; t = (NType) t.getManageableSupertype ())
			{
				Object d;
				if ((t.specialEdgeDescriptorsForKey != null)
					&& ((d = t.specialEdgeDescriptorsForKey.get (key, null)) != null))
				{
					return (SpecialEdgeDescriptor) d;
				}
			}
			return null;
		}


		public void declareSpecialEdge (int bits, String key, Object[] nodeClass)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			if (specialEdgeDescriptors == null)
			{
				specialEdgeDescriptors = new IntHashMap (4);
				specialEdgeDescriptorsForKey = new XHashMap (4);
				specialEdgeDescOfSource
					= new ObjectList ().addAll (specialEdgeDescOfSource);
				specialEdgeDescOfTarget
					= new ObjectList ().addAll (specialEdgeDescOfTarget);
			}
			I18NBundle bundle;
			try
			{
				bundle = I18NBundle.getInstance (getImplementationClass ());
			}
			catch (java.util.MissingResourceException e)
			{
				bundle = null;
			}
			SpecialEdgeDescriptor sd = new SpecialEdgeDescriptor
				(bundle, key, bits, nodeClass.getClass ().getComponentType ()); 
			if (specialEdgeDescriptors.put (bits, sd) != null)
			{
				throw new IllegalArgumentException (bits + " already declared");
			}
			if (specialEdgeDescriptorsForKey.put (key, sd) != null)
			{
				throw new IllegalArgumentException (key + " already declared");
			}
			(((bits & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0)
			 ? specialEdgeDescOfSource : specialEdgeDescOfTarget).add (sd);
		}

		SpecialEdgeDescriptor[] getSpecialEdgeDescriptors (boolean source)
		{
			ObjectList list = source ? specialEdgeDescOfSource : specialEdgeDescOfTarget;
			return (SpecialEdgeDescriptor[]) list.toArray (new SpecialEdgeDescriptor[list.size ()]);
		}
		
		public void declareAlias (String name, PersistenceField field)
		{
			if (finished)
			{
				throw new IllegalStateException ();
			}
			if (aliasFields == null)
			{
				aliasFields = new StringMap (4);
			}
			aliasFields.put (name, field);
		}


		@Override
		public PersistenceField resolveAliasField (String name)
		{
			if (aliasFields != null)
			{
				Object f = aliasFields.get (name);
				if (f != null)
				{
					return (PersistenceField) f;
				}
			}
			return super.resolveAliasField (name);
		}


		@Override
		public ManageableType validate ()
		{
			dependencies.validate ();
			return super.validate ();
		}
		
		
		synchronized de.grogra.reflect.Field[] getUserFields ()
		{
			if (userFields == null)
			{
				ObjectList list = new ObjectList ();
				for (int i = 0; i < getManagedFieldCount (); i++)
				{
					ManageableType.Field mf = getManagedField (i);
					de.grogra.reflect.Field f = Reflection.getDeclaredField
						(mf.getDeclaringType (), mf.getDescriptor());
					if ((f != null) && Reflection.isPublic (f)
						&& (Reflection.getDeclaredAnnotation (f, Editable.class) != null))
					{
						for (int j = 0; j < f.getDeclaredAnnotationCount (); j++)
						{
							Annotation a = f.getDeclaredAnnotation (j);
							Type t = null;
							if (Reflection.equal (Range.class, a.annotationType ()))
							{
								if (((1 << f.getType ().getTypeId ()) & TypeId.NUMERIC_NONCHAR_MASK) != 0)
								{
									t = new BoundedType ("bounded", f.getType ().getTypeId (),
										(Number) a.value ("min"),
										(Number) a.value ("max"));
								}
							}
							else if (Reflection.equal (Choice.class, a.annotationType ()))
							{
								if (Reflection.isIntegral (f.getType ()))
								{
									t = new EnumerationType ("choice", (String[]) a.value ("value"));
								}
							}
							if (t != null)
							{
								f = new FieldDecorator (f, t);
							}
						}
						list.add (f);
					}
				}
				list.toArray (userFields = new de.grogra.reflect.Field[list.size ()]);
			}
			return userFields;
		}
	}

	public Node ()
	{
		super ();
	}


	@Override
	public final ManageableType getManageableType ()
	{
		if ((data == null) || (data.ntype == null))
		{	
			return getNTypeImpl ();
		}
		return data.ntype;
	}

	
	@Override
	public Manageable manageableReadResolve ()
	{
		return this;
	}

	
	@Override
	public Object manageableWriteReplace ()
	{
		return this;
	}


	public final NType getNType ()
	{
		if ((data == null) || (data.ntype == null))
		{	
			return getNTypeImpl ();
		}
		return data.ntype;
	}


	/**
	 * This method returns the {@link NType} which describes the managed
	 * fields of the class of this node. This method has to be implemented
	 * in every concrete subclass.
	 * 
	 * @return type describing the managed fields of the class of this node
	 */
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}


	public boolean isManagingInstance ()
	{
		NType t = getNTypeImpl ();
		return (t == null) || (t.getRepresentative () == this);
	}

	/**
	 * This method returns a new instance of the class of this
	 * node. This method has to be implemented in every concrete subclass.
	 * 
	 * @return new instance of class of this node
	 */
	protected Node newInstance ()
	{
		return new Node ();
	}


	@Override
	protected Object clone () throws CloneNotSupportedException
	{
		Node c = (Node) super.clone ();
		c.next = null;
		c.targetNext = null;
		c.edgeBits = 0;
		c.data = null;
		c.parent = null;
		c.id = -1;
		c.manager = null;
		c.bits &= ~HAS_OBSERVERS;
		c.bitMarks = 0;
		c.marks = Utils.OBJECT_0;
		return c;
	}


	/**
	 * Creates a clone of this node using the methods
	 * {@link #newInstance()} and {@link #dup}. The clone is not part of
	 * the graph.
	 * 
	 * @param cloneFields perform a deep clone of field values?
	 * @return clone of this node
	 * @throws CloneNotSupportedException
	 */
	public Node clone (boolean cloneFields)
		throws CloneNotSupportedException
	{
		Node c = newInstance ();
		assert c.getClass () == getClass ()
			: "newInstance not implemented in " + getClass ();
		c.dup (this, cloneFields, null);
		return c;
	}

	
	public Node cloneGraph (EdgePattern edges, final boolean cloneFields)
		throws CloneNotSupportedException
	{
		class CloneVisitor extends VisitorImpl
		{
			XHashMap clones = new XHashMap ();

			@Override
			public Object visitEnter (Path path, boolean node)
			{
				if (node)
				{
					getClone ((Node) path.getObject (-1));
					return null;
				}
				else
				{
					Object result = super.visitEnter (path, node);
					if (result != STOP)
					{
						Node s = getClone ((Node) path.getObject (-3));
						Node t = getClone ((Node) path.getObject (-1));
						if (!path.isInEdgeDirection (-2))
						{
							Node a = s; s = t; t = a;
						}
						s.addEdgeBitsTo (t, path.getEdgeBits (-2), null);
					}
					return result;
				}
			}
			
			@Override
			public Object visitInstanceEnter ()
			{
				return STOP;
			}
			
			private Node getClone (Node src)
			{
				Node c = (Node) clones.get (src);
				if (c == null)
				{
					try
					{
						c = src.clone (cloneFields);
					}
					catch (CloneNotSupportedException e)
					{
						throw new WrapException (e);
					}
					clones.put (src, c);
				}
				return c;
			}
		}
		
		CloneVisitor cv = new CloneVisitor ();
		cv.init (getCurrentGraphState (), edges);

		try
		{
			if (manager != null)
			{
				manager.accept (this, cv, null);
			}
			else
			{
				GraphManager.acceptGraph (this, cv, new ArrayPath (GraphManager.STATIC));
			}
		}
		catch (WrapException e)
		{
			if (e.getCause () instanceof CloneNotSupportedException)
			{
				throw (CloneNotSupportedException) e.getCause ();
			}
			throw e;
		}
		
		return (Node) cv.clones.get (this);
	}


	public void dup (Node original, boolean cloneFields, Transaction t)
		throws CloneNotSupportedException
	{
		NType type = getNType ();
		for (int i = type.getManagedFieldCount () - 1; i >= 0; i--)
		{
			ManageableType.Field f = type.getManagedField (i);
			switch (f.getType ().getTypeId ())
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					f.set$pp.Type (this, null, f.get$pp.Type (original), t);
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					f.setBoolean (this, null, f.getBoolean (original), t);
					break;
// generated
				case TypeId.BYTE:
					f.setByte (this, null, f.getByte (original), t);
					break;
// generated
				case TypeId.SHORT:
					f.setShort (this, null, f.getShort (original), t);
					break;
// generated
				case TypeId.CHAR:
					f.setChar (this, null, f.getChar (original), t);
					break;
// generated
				case TypeId.INT:
					f.setInt (this, null, f.getInt (original), t);
					break;
// generated
				case TypeId.LONG:
					f.setLong (this, null, f.getLong (original), t);
					break;
// generated
				case TypeId.FLOAT:
					f.setFloat (this, null, f.getFloat (original), t);
					break;
// generated
				case TypeId.DOUBLE:
					f.setDouble (this, null, f.getDouble (original), t);
					break;
//!! *# End of generated code
				case TypeId.OBJECT:
					f.setObject (this, null, cloneFields ? f.getCloned (original)
								 : f.getObject (original), t);
					break;
			}
		}
		dupUnmanagedFields (original);
	}


	protected void dupUnmanagedFields (Node original)
	{
	}


	@Override
	public final PersistenceManager getPersistenceManager ()
	{
		return manager;
	}


	@Override
	public final GraphManager getGraph ()
	{
		return manager;
	}
	
	
	public final Transaction getTransaction (boolean create)
	{
		return (manager != null) ? manager.getTransaction (create) : null;
	}

	
	public final GraphState getCurrentGraphState ()
	{
		return (manager != null) ? GraphState.current (manager) : null;
	}


	void setGraphManager (GraphManager manager, long id)
	{
		resetMarks ();
		this.manager = manager;
		this.id = id;
		bits &= ~DELETED;
	}


	public boolean isRoot()
	{
		for (Edge e = getFirstEdge(); e != null; e = e.getNext(this)){
			if (e.isTarget(this)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Counts the number of all direct targets (children) of this node.
	 * 
	 * @return number of direct children
	 */
	public int getDirectChildCount() {
		int result=0;
		for (Edge e = getFirstEdge(); e != null; e = e.getNext(this)) {
			if (e.isSource(this)) {
				result++;
			}
		}
		return result;
	}

	public List<Node> getInputSlotNodes() {
		return null;
	}

	public List<Node> getOutputSlotNodes() {
		return null;
	}
	/**
	 * Counts the number of all direct sources of this node.
	 * 
	 * @return number of direct predecessors
	 */
	public int getDirectPredecessorCount() {
		int result=0;
		for (Edge e = getFirstEdge(); e != null; e = e.getNext(this)) {
			if (e.isTarget(this)) {
				result++;
			}
		}
		return result;
	}	
	
	/**
	 * Returns a list of all direct sources and targets (children) of this node.
	 * 
	 * @return String
	 */
	public String getEdgeInfo() {
		StringBuffer result = new StringBuffer();
		result.append ("Node: "+getName()+"["+paramString()+"]\n");
		StringBuffer sbOut = new StringBuffer();
		StringBuffer sbIn = new StringBuffer();
		for (Edge e = getFirstEdge(); e != null; e = e.getNext(this)) {
			if (e.isSource(this)) {
				sbOut.append ("\t"+e+"\n");
			}
			if (e.isTarget(this)) {
				sbIn.append ("\t"+e+"\n");
			}			
		}
		result.append ("Outgoing edges ("+getDirectChildCount()+"):\n"+sbOut+"\n");
		result.append ("Incoming edges ("+getDirectPredecessorCount()+"):\n"+sbIn+"\n");
		return result.toString ();
	}
	
	/**
	 * Returns a unique ID for this node.
	 * The IDs are serially counted up from zero for each node that is created
	 * and inserted into the graph. Nodes that are created but not inserted into
	 * the graph have an ID of -1. Nodes that were part of the graph, but were 
	 * removed later on have an ID of -2.
	 */
	@Override
	public final long getId ()
	{
		return id;
	}

	public AttributeAccessor getAccessor (Attribute attribute)
	{
		return getNType ().accessors.getAccessorById (attribute.getId ());
	}


	public AttributeAccessor getAccessor (String name)
	{
		return getNType ().accessors.find (name);
	}


	public Attribute[] getAttributes ()
	{
		AccessorMap m = getNType ().accessors;
		return m.getAttributes (null);
	}


	public Attribute[] getEdgeAttributes (Edge edge)
	{
		return Attribute.ATTRIBUTE_0;
	}

	public AttributeAccessor getEdgeAttributeAccessor (Attribute attribute)
	{
		return null;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	protected $type get$pp.Type (${pp.Type}Attribute a,
#if ($pp.Object)
								 Object placeIn,
#end
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	protected boolean getBoolean (BooleanAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected byte getByte (ByteAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected short getShort (ShortAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected char getChar (CharAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected int getInt (IntAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected long getLong (LongAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected float getFloat (FloatAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected double getDouble (DoubleAttribute a,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
// generated
// generated
	protected Object getObject (ObjectAttribute a,
								 Object placeIn,
								 GraphState gs)
	{
		throw new NoSuchElementException (a.getKey ());
	}
// generated
//!! *# End of generated code

	@Override
	public final void initXClass (XClass cls)
	{
		getXData ().init (cls);
	}


	@Override
	public final XClass getXClass ()
	{
		return (data != null) ? data.cls : null;
	}


	@Override
	public final synchronized NodeData getXData ()
	{
		if (data == null)
		{
			data = new NodeData ();
		}
		return data;
	}


	public Object getOrNull (Object key)
	{
		return get (key, null);
	}


	@Override
	public Object get (Object key, Object defaultValue)
	{
		AttributeAccessor a;
		if (key instanceof Attribute)
		{
			a = getAccessor ((Attribute) key);
		}
		else if (key instanceof String)
		{
			a = getAccessor ((String) key);
			if (a == null)
			{
				ManageableType.Field f;
				if ((f = getNType ().getManagedField ((String) key)) != null)
				{
					return f.get (this, null);
				}
			}
		}
		else
		{
			a = null;
		}
		return (a == null) ? defaultValue
			: a.getAttribute ().get (this, true, GraphState.current (manager));
	}


	@Override
	public final Edge getNext (Node parent)
	{
		return parent == this ? targetNext : next;
	}


	@Override
	public final Node getSource ()
	{
		return parent;
	}


	@Override
	public final Node getTarget ()
	{
		return this;
	}


	@Override
	public boolean isSource (Node node)
	{
		return node == parent;
	}


	@Override
	public boolean isTarget (Node node)
	{
		return node == this;
	}


	@Override
	public boolean isDirection (Node source, Node target)
	{
		return (source == parent) || (target == this);
	}


	@Override
	public final Node getNeighbor (Node start)
	{
		return start == this ? parent : this;
	}


	public final Edge getFirstEdge ()
	{
		return parent == null ? targetNext : this;
	}
	

	public final Edge getEdgeTo (Node target)
	{
		for (Edge e = targetNext; e != null; e = e.getNext (this))
		{
			if (e.getTarget () == target)
			{
				return e;
			}
		}
		return null;
	}


	public final int getEdgeBitsTo (Node target)
	{
		for (Edge e = targetNext; e != null; e = e.getNext (this))
		{
			if (e.getTarget () == target)
			{
				return e.edgeBits;
			}
		}
		return 0;
	}


	public final Edge getOrCreateEdgeTo (Node target)
	{
		if (target == this)
		{
			throw new IllegalArgumentException
				("Cannot create an edge from a node to itself");
		}
		if (id < target.id)
		{
			synchronized (this)
			{
				synchronized (target)
				{
					return getOrCreateEdgeSync (target);
				}
			}
		}
		else if (target.id < 0)
		{
			return getOrCreateEdgeSync (target);
		}
		else
		{
			synchronized (target)
			{
				synchronized (this)
				{
					return getOrCreateEdgeSync (target);
				}
			}
		}
	}


	private Edge getOrCreateEdgeSync (Node target)
	{
		Edge previous = this, e;
		for (e = targetNext; e != null; e = e.getNext (this))
		{
			if (e.getTarget () == target)
			{
				return e;
			}
			previous = e;
		}
		if (target.parent == null)
		{
			target.parent = this;
			e = target;
		}
		else
		{
			EdgeImpl set = new EdgeImpl (this, target);
			set.targetNext = target.targetNext;
			target.setNext (set, target);
			e = set;
		}
		e.setNext (previous.getNext (this), this);
		previous.setNext (e, this);
		return e;
	}


	public void removeEdge (Edge set)
	{
		if (set == this)
		{
			parent = null;
			return;
		}
		Edge previous = this;
		for (Edge e = targetNext; e != null; e = e.getNext (this))
		{
			if (e == set)
			{
				previous.setNext (e.getNext (this), this);
				return;
			}
			previous = e;
		}
	}


	public final void removeAll (Transaction t)
	{
		if (parent != null)
		{
			remove (t);
		}
		while (targetNext != null)
		{
			targetNext.remove (t);
		}
	}


	public final void addEdgeBitsTo (Node target, int edges, Transaction t)
	{
		getOrCreateEdgeTo (target).addEdgeBits (edges, t);
	}


	public final void removeEdgeBitsTo (Node target, int edges, Transaction t)
	{
		Edge e = getEdgeTo (target);
		if (e != null)
		{
			e.removeEdgeBits (edges, t);
		}
	}


	@Override
	public String toString ()
	{
		return getNType ().getName () + '[' + paramString () + "]@"
			+ Integer.toHexString (System.identityHashCode (this));
	}


	/**
	 * Computes the class-specific part of the string returned by
	 * {@link #toString()} which shall be enclosed in brackets. This
	 * should be used to show important properties of this node. 
	 * 
	 * @return string describing some important properties of this node
	 */
	protected String paramString ()
	{
		return "id=" + id;
	}


	/**
	 * Sets the name of this node.
	 * 
	 * @param name new name of node, may be <code>null</code>
	 * 
	 * @see #getName()
	 */
	public void setName (String name)
	{
		String n = (data != null) ? data.name : null;
		if (Utils.equal (name, n))
		{
			return;
		}
		NodeData d = getXData ();
		if (manager != null)
		{
			XHashMap<String,Node> m = manager.nodeForName;
			synchronized (m)
			{
				if (n != null)
				{
					m.remove (n, this);
				}
				if (name != null)
				{
					m.add (name, this);
				}
				d.name = name;
			}
		}
		else
		{
			d.name = name;
		}
	}


	/**
	 * Returns the name of this node. Names are not unique and may be
	 * <code>null</code>. The graph of this node maintains a map from names
	 * to nodes, see {@link GraphManager#getNodeForName(String)}.
	 * 
	 * @return name of this node, may be <code>null</code>
	 */
	public final String getName ()
	{
		return (data != null) ? data.name : null;
	}

	
	/**
	 * Tests if this node has the specified <code>name</code>. I.e., both
	 * <code>name</code> and this node's name are <code>null</code>, or
	 * they consist of the same sequence of characters.
	 * 
	 * @param name a name to test
	 * @return has this node the specified <code>name</code>?
	 */
	public final boolean hasName (String name)
	{
		return Utils.equal (name, (data != null) ? data.name : null);
	}


	public void setLayer (int layer)
	{
		bits = (bits & ~LAYER_MASK) | (layer & LAYER_MASK);
	}


	public int getLayer ()
	{
		return bits & LAYER_MASK;
	}


	/**
	 * Sets the <code>extentIndex</code> property of this node
	 * 
	 * @param index new value for property
	 * 
	 * @see #getExtentIndex()
	 */
	public void setExtentIndex (int index)
	{
		bits = (bits & ~EXTENT_MASK) | ((index << EXTENT_BIT) & EXTENT_MASK);
	}


	/**
	 * Returns the <code>extentIndex</code> property of this node.
	 * This determines the index of the list of the
	 * {@link Extent} in which this node is inserted.
	 * 
	 * @return insert node at tail or head of linked list of extent?
	 */
	public int getExtentIndex ()
	{
		return (bits & EXTENT_MASK) >> EXTENT_BIT;
	}


	public void setMark (boolean value)
	{
		if (value)
		{
			bits |= MARK;
		}
		else
		{
			bits &= ~MARK;
		}
	}


	public boolean isMarked ()
	{
		return (bits & MARK) != 0;
	}

	
	public int getSymbol ()
	{
		return Graph.RECTANGLE_SYMBOL;
	}
	
	
	public int getSymbolColor ()
	{
		return 0x00b2b300;
	}


	@Override
	public int getUserFieldCount ()
	{
		return getNType ().getUserFields ().length;
	}

	
	@Override
	public Field getUserField (int index)
	{
		return getNType ().getUserFields ()[index];
	}


	public Instantiator getInstantiator ()
	{
		Method m = getNType ().getInstantiatorMethod;
		if (m == null)
		{
			return null;
		}
		try
		{
			return (Instantiator) m.invoke (this, null);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return null;
		}
	}


	public boolean instantiateGraph (int edges, ArrayPath path, Visitor v)
	{
		GraphState gs = v.getGraphState ();
		gs.instantiateEdge (edges, true, -1);
		gs.instantiate (this, true, (manager != null) ? id : hashCode ());
		path.pushEdges (edges, true, -1, true);
		path.pushNode (this, (manager != null) ? id : hashCode ());
		Object p;
		boolean b = true;
		if ((p = v.visitEnter (path, false)) != Visitor.STOP)
		{
			b = instantiate0 (gs, path, v);
		}
		b &= v.visitLeave (p, path, false);
		if (path != null)
		{
			path.popNode ();
			path.popEdgeSet ();
		}
		gs.deinstantiate ();
		gs.deinstantiate ();
		return b;
	}


	private boolean instantiate0 (GraphState gs, ArrayPath path, Visitor v)
	{
		Object o;
		Graph g = gs.getGraph ();

	traverseEdges:
		if ((o = v.visitEnter (path, true)) != Visitor.STOP)
		{
			Instantiator i;
			if ((i = g.getInstantiator (this)) != null)
			{
				Object ie;
				boolean b = true;
				if ((ie = v.visitInstanceEnter ()) != Visitor.STOP)
				{
					try
					{
						gs.beginInstancing (this, (manager != null) ? id : hashCode ());
						b = i.instantiate (path, v);
					}
					finally
					{
						gs.endInstancing ();
					}
				}
				if (!(b & v.visitInstanceLeave (ie)))
				{
					break traverseEdges;
				}
			}
			for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
			{
				Node n = e.getTarget ();
				if (n != this)
				{
					gs.instantiate (e, false, -1);
					gs.instantiate (n, true, (manager != null) ? id : hashCode ());
					path.pushEdgeSet (e, -1, false);
					path.pushNode (n, (manager != null) ? n.id : n.hashCode ());
					Object p;
					boolean b = ((p = v.visitEnter (path, false)) == Visitor.STOP)
						|| n.instantiate0 (gs, path, v);
					b &= v.visitLeave (p, path, false);
					if (path != null)
					{
						path.popNode ();
						path.popEdgeSet ();
					}
					gs.deinstantiate ();
					gs.deinstantiate ();
					if (!b)
					{
						break traverseEdges;
					}
				}
			}
		}
		return v.visitLeave (o, path, true);
	}


	/**
	 * Find an adjacent node n to this one. The edgeBits are used to determine
	 * if a relation between those two nodes exists ({@link #testEdgeBits(int)}).
	 * If out is true and this node is the source or if in is true and this node
	 * is the target, n is returned. Thus, if in and out is true, any adjacent
	 * node where the connecting edge matches the edgeBits is considered.
	 * @param in true if edges incoming to this node should be considered
	 * @param out true if edges outgoing from this node should be considered
	 * @param edgeBits the type/types of edges to consider
	 * @return an adjacent node that matches the criteria or null if none found
	 */
	public Node findAdjacent (boolean in, boolean out, int edgeBits)
	{
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			if (e.testEdgeBits (edgeBits))
			{
				if (out && (e.getSource () == this))
				{
					return e.getTarget ();
				}
				if (in && (e.getTarget () == this))
				{
					return e.getSource ();
				}
			}
		}
		return null;
	}

	
	public Node getFirst (int edgeBits)
	{
		return findAdjacent (false, true, edgeBits);
	}


	/**
	 * Returns the first child of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}:
	 * The first child of a node is reached by traversing a
	 * {@link Graph#BRANCH_EDGE} in forward direction, its siblings
	 * are reached by traversing {@link Graph#SUCCESSOR_EDGE} in forward
	 * direction from sibling to sibling.
	 * 
	 * @return the first child, or <code>null</code>
	 */
	public Node getBranch ()
	{
		return findAdjacent (false, true, Graph.BRANCH_EDGE);
	}


	public Node getBranchTail ()
	{
		Node l = null;
		Node c = findAdjacent (false, true, Graph.BRANCH_EDGE);
		while (c != null)
		{
			l = c;
			c = c.getSuccessor ();
		}
		return l;
	}


	public int getBranchLength ()
	{
		int i = 0;
		for (Node c = getBranch (); c != null; c = c.getSuccessor ())
		{
			i++;
		}
		return i;
	}


	/**
	 * Returns the parent node of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}.
	 * 
	 * @return the parent
	 * @see #getBranch()
	 */
	public Node getAxisParent ()
	{
		Node n = this;
	findParent:
		while (true)
		{
			for (Edge e = n.getFirstEdge (); e != null; e = e.getNext (n))
			{
				Node s;
				if ((s = e.getSource ()) != n)
				{
					int b = e.getEdgeBits ();
					if ((b & Graph.SUCCESSOR_EDGE) != 0)
					{
						n = s;
						continue findParent;
					}
					else if ((b & Graph.BRANCH_EDGE) != 0)
					{
						return s;
					}
				}
			}
			return null;
		}
	}


	/**
	 * Returns the next sibling of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}.
	 * 
	 * @return the next sibling
	 * @see #getBranch()
	 */
	public Node getSuccessor ()
	{
		return findAdjacent (false, true, Graph.SUCCESSOR_EDGE);
	}


	/**
	 * Returns the previous sibling of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}.
	 * 
	 * @return the previous sibling
	 * @see #getBranch()
	 */
	public Node getPredecessor ()
	{
		return findAdjacent (true, false, Graph.SUCCESSOR_EDGE);
	}


	/**
	 * Returns the <code>index</code>-th child of this node.
	 * The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}.
	 * 
	 * @param index an index
	 * @return the <code>index</code>-th child
	 * @see #getBranch()
	 */
	public Node getBranchNode (int index)
	{
		if (index < 0)
		{
			return null;
		}
		Node c = getBranch ();
		while ((c != null) && (index-- > 0))
		{
			c = c.getSuccessor ();
		}
		return c;
	}


	/**
	 * Returns the index of this node in the list of children of its
	 * parent. The underlying tree structure is defined by the edges
	 * {@link Graph#BRANCH_EDGE} and {@link Graph#SUCCESSOR_EDGE}.
	 * 
	 * @return the child index of this node
	 * @see #getBranch()
	 */
	public int getIndex ()
	{
		int i = 0;
		Node child = this;
		while ((child = child.findAdjacent (true, false, Graph.SUCCESSOR_EDGE)) != null)
		{
			i++;
		}
		return i;
	}


	public void appendBranchNode (Node node, Transaction xa)
	{
		if (node == null)
		{
			return;
		}
		Node l = getBranchTail ();
		if (l != null)
		{
			l.getOrCreateEdgeTo (node).addEdgeBits (Graph.SUCCESSOR_EDGE, xa);
		}
		else
		{
			getOrCreateEdgeTo (node).addEdgeBits (Graph.BRANCH_EDGE, xa);
		}
	}


	public void appendBranchNode (Node node)
	{
		appendBranchNode (node, null);
	}


	public void insertBranchNode (int index, Node node, Transaction xa)
	{
		Node n = getBranch ();
		if (n == null)
		{
			getOrCreateEdgeTo (node).addEdgeBits (Graph.BRANCH_EDGE, xa);
		}
		else if (index == 0)
		{
			setBranch (node, xa);
			node.setSuccessor (n);
		}
		else
		{
			Node p;
			do
			{
				p = n;
				n = p.getSuccessor ();
				if (--index == 0)
				{
					p.setSuccessor (node, xa);
					node.setSuccessor (n, xa);
					return;
				}
			} while (n != null);
			p.setSuccessor (node, xa);
		}
	}


	public void insertBranchNode (int index, Node node)
	{
		insertBranchNode (index, node, null);
	}


	public void setBranch (Node c, Transaction xa)
	{
		Node n = getBranch ();
		if (n != c)
		{
			if (n != null)
			{
				getEdgeTo (n).removeEdgeBits (Graph.BRANCH_EDGE, xa);
			}
			if (c != null)
			{
				getOrCreateEdgeTo (c).addEdgeBits (Graph.BRANCH_EDGE, xa);
			}
		}
	}


	public void setBranch (Node c)
	{
		setBranch (c, null);
	}


	public void setSuccessor (Node s, Transaction xa)
	{
		Node n = getSuccessor ();
		if (n != s)
		{
			if (n != null)
			{
				getEdgeTo (n).removeEdgeBits (Graph.SUCCESSOR_EDGE, xa);
			}
			if (s != null)
			{
				getOrCreateEdgeTo (s).addEdgeBits (Graph.SUCCESSOR_EDGE, xa);
			}
		}
	}


	public void setSuccessor (Node s)
	{
		setSuccessor (s, null);
	}

	
	public boolean isAncestorOf (Node n)
	{
		while (n != null)
		{
			if (n == this)
			{
				return true;
			}
			n = n.getAxisParent ();
		}
		return false;
	}

	
	public Node getCommonAncestor (Node n)
	{
		for (Node p = this; p != null; p = p.getAxisParent ())
		{
			if (p.isAncestorOf (n))
			{
				return p;
			}
		}
		return null;
	}


	public void removeFromChain ()
	{
		removeFromChain (null);
	}


	public void removeFromChain (Transaction t)
	{
		Node p = getPredecessor (), n = getSuccessor ();
		setSuccessor (null);
		if (p != null)
		{
			p.setSuccessor (n, t);
		}
		else if ((p = getAxisParent ()) != null)
		{
			p.setBranch (n, t);
		}
	}

	
	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
		if (((bits & HAS_OBSERVERS) != 0) && !Transaction.isApplying (t))
		{
			boolean notFound = true;
			for (Edge e = getFirstEdge (); e != null;
				 e = e.getNext (this))
			{
				Node n;
				int eb;
				if (((eb = e.edgeBits) & Graph.SPECIAL_EDGE_MASK) != 0)
				{
					if ((n = (((eb & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0)
							  ? e.getSource () : e.getTarget ())) != this)
					{
						NType.Field[] a;
						if (((a = n.getNTypeImpl ().specialEdgeFields) != null)
							&& (a[eb & Graph.SPECIAL_EDGE_MASK] != null))
						{
							notFound = false;
							n.specialEdgeRefModified (this, a[eb & Graph.SPECIAL_EDGE_MASK], field, indices, t);
						}
					}
				}
				if (((eb & Graph.NOTIFIES_EDGE) != 0) && ((n = e.getTarget ()) != this))
				{
					notFound = false;
					((Observer) n).fieldModified (this, field, indices, t);
				}
			}
			if (notFound)
			{
				bits &= ~HAS_OBSERVERS;
			}
		}
		NType.Field f;
		int edge;
		if ((field.length () == 1)
			&& ((edge = (f = (NType.Field) field.getSubfield (0)).specialEdge)
				!= 0))
		{
			boolean syncTarget = (edge & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0;
			Object v = f.getObject (this);
			for (Edge e = getFirstEdge (); e != null;
				 e = e.getNext (this))
			{
				Node n;
				if (((e.edgeBits & Graph.SPECIAL_EDGE_MASK) == edge)
					&& (this != (n = (syncTarget ? e.getTarget ()
									  : e.getSource ()))))
				{
					if (n == v)
					{
						f = null;
					}
					else
					{
						e.removeEdgeBits (edge, t);
					}
				}
			}
			if ((f != null) && (v instanceof Node))
			{
				(syncTarget ? getOrCreateEdgeTo ((Node) v)
				 : ((Node) v).getOrCreateEdgeTo (this)).addEdgeBits (edge, t);
			}
		}
		if ((t != null) && (getProvider () != null))
		{
			t.fireSharedObjectModified (this);
		}
		if ((manager != null) && (field.length () == 1)
			&& (field.getSubfield (0) == extentIndex$FIELD))
		{
			if (t != null)
			{
				((GraphTransaction) t).extentIndexChanged.add (this);
			}
			else
			{
				manager.getExtent (getNType ()).reenqueue (this);
			}
		}
	}

	
	@Override
	public int getStamp ()
	{
		return stamp;
	}


	protected void specialEdgeRefModified (Node ref, NType.Field edgeField,
										   PersistenceField field,
										   int[] indices, Transaction t)
	{
		fieldModified (edgeField, null, t);
	}


	protected void edgeChanged (Edge set, int old, Transaction t)
	{
		int spec = set.edgeBits & Graph.SPECIAL_EDGE_MASK;
		if ((spec != 0)
			|| (((set.edgeBits & Graph.NOTIFIES_EDGE) != 0) && (set.getSource () == this)))
		{
			bits |= HAS_OBSERVERS;
		}
		old &= Graph.SPECIAL_EDGE_MASK;
		if ((spec != old) && Transaction.isNotApplying (t))
		{
			if (old != 0)
			{
				specialEdgeRemoved (set, old, t);
			}
			if (spec != 0)
			{
				specialEdgeAdded (set, t);
			}
		}
	}


	protected void specialEdgeRemoved (Edge set, int old, Transaction t)
	{
		NType nt = getNTypeImpl ();
		NType.Field[] a;
		NType.Field f;
		Node n;
		if (((a = nt.specialEdgeFields) != null)
			&& ((f = a[old]) != null)
			&& (this != (n = ((old & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0) ? set.getTarget ()
						 : set.getSource ()))
			&& (f.getObject (this) == n))
		{
			f.setObject (this, null, null, t);
		}
	}


	protected void specialEdgeAdded (Edge set, Transaction t)
	{
		int i = set.edgeBits & Graph.SPECIAL_EDGE_MASK;
		NType nt = getNTypeImpl ();
		NType.Field[] a;
		NType.Field f;
		Node n;
		if (((a = nt.specialEdgeFields) != null)
			&& ((f = a[i]) != null)
			&& (this != (n = ((i & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0) ? set.getTarget ()
						 : set.getSource ()))
			&& (f.getObject (this) != n))
		{
			f.setObject (this, null, n, t);
		}
	}


	@Override
	public void initProvider (SharedObjectProvider provider)
	{
		NodeData i = getXData ();
		if (i.sop != null)
		{
			throw new IllegalStateException ();
		}
		i.sop = provider;
	}


	@Override
	public SharedObjectProvider getProvider ()
	{
		return (data != null) ? data.sop : null;
	}


	@Override
	public synchronized void addReference (SharedObjectReference ref)
	{
		NodeData d = getXData ();
		if (d.refs == null)
		{
			d.refs = new ObjectList<SharedObjectReference> (4, false);
		}
		d.refs.add (ref);
	}

	
	@Override
	public synchronized void removeReference (SharedObjectReference ref)
	{
		NodeData d = data;
		if ((d != null) && (d.refs != null))
		{
			d.refs.remove (ref);
		}
	}

	
	@Override
	public synchronized void appendReferencesTo (java.util.List out)
	{
		NodeData d = data;
		if ((d != null) && (d.refs != null))
		{
			out.addAll (d.refs);
		}
	}


	public void dumpTree ()
	{
		dumpTree (0);
	}


	private void dumpTree (int depth)
	{
		for (int i = depth * 2; i > 0; i--)
		{
			System.out.print (' ');
		}
		System.out.println (this);
		for (Node n = getBranch (); n != null; n = n.getSuccessor ())
		{
			n.dumpTree (depth + 1);
		}
	}

	
	public void dump ()
	{
		dump (0, new StringBuffer (), null);
	}
	
	
	private void dump (int depth, StringBuffer buf, Edge edge)
	{
		for (int i = depth * 2; i > 0; i--)
		{
			System.out.print (' ');
		}
		System.out.print (this);
		if (edge != null)
		{
			System.out.print (' ');
			buf.setLength (0);
			edge.getEdgeKeys (buf, false, false);
			System.out.print (buf);
		}
		System.out.println ();
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			Node n = e.getTarget ();
			if (n != this)
			{
				n.dump (depth + 1, buf, e);
			}
		}
	}
	
	/**
	 * Stores the graph to use for deserialization in a thread.
	 * 
	 * @see #setGraphForDeserialization(GraphManager)
	 */
	static final ThreadLocal<WeakReference<GraphManager>> DESERIALIZATION_MANAGER
		= new ThreadLocal<WeakReference<GraphManager>> ();


	/**
	 * When a serialized stream containing references to already existing nodes
	 * is deserialized, the graph to use for resolving the id-based references
	 * has to be specified by this method. The invocation has to be in the
	 * thread which will be used for deserialization.  
	 * 
	 * @param mgr the graph to use for id-resolution within deserialization
	 * @return graph which has been used previously, possibly <code>null</code> 
	 */
	public GraphManager setGraphForDeserialization (GraphManager mgr)
	{
		WeakReference<GraphManager> ref = DESERIALIZATION_MANAGER.get ();
		DESERIALIZATION_MANAGER.set (new WeakReference<GraphManager> (mgr));
		return (ref != null) ? ref.get () : null;
	}

	
	/**
	 * This class is used as a handle in a serialized stream which points
	 * to a node by its node id. The stream does not contain the
	 * data of the node itself, but just its id. This is useful when the node
	 * data itself is not represented by the serialized stream, but some
	 * other non-node objects making references to nodes are to be
	 * serialized. For deserialization, the graph to use has to be set via
	 * {@link Node#setGraphForDeserialization(GraphManager)} so that ids can
	 * be translated to nodes.  
	 * 
	 * @author Ole Kniemeyer
	 */
	private static final class SerializationHandle implements Serializable
	{
		private static final long serialVersionUID = 5434810166185154007L;

		private final long id;

		SerializationHandle (Node node)
		{
			id = node.id;
		}

		Object readResolve () throws ObjectStreamException
		{
			WeakReference<GraphManager> ref = DESERIALIZATION_MANAGER.get ();
			if (ref == null)
			{
				throw new NotActiveException ("GraphManager not set");
			}
			GraphManager g = ref.get ();
			if (g == null)
			{
				throw new NotActiveException ("GraphManager has been garbage-collected");
			}
			Node n = g.getObject (id);
			if (n == null)
			{
				throw new InvalidObjectException ("GraphManager has no node with id " + id);
			}
			return n;
		}
	}

	/**
	 * This method is used by object serialization.
	 * 
	 * @return this node if it has no valid id, or a handle for the id  
	 * 
	 * @see Serializable
	 */
	protected Object writeReplace ()
	{
		return (id >= 0) ? new SerializationHandle (this) : this;
	}

}
