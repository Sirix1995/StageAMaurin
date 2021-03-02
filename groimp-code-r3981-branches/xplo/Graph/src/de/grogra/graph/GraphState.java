
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

import java.lang.ref.WeakReference;
import de.grogra.reflect.*;
import de.grogra.util.*;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.IntHashMap;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>GraphState</code> provides a context to access and modify
 * a {@link de.grogra.graph.Graph}. It also contains state information
 * for <em>object instancing</em> and <em>object context</em>. Each
 * graph state is associated with a thread, most operations may only
 * be performed when this thread is the current thread.
 * 
 * @author Ole Kniemeyer
 * @see de.grogra.graph.Graph
 */
public abstract class GraphState
{

	private static int nextPropertyId = 0;
	
	static
	{
		assert Graph.MIN_UNUSED_EDGE == 1 << Graph.MIN_UNUSED_EDGE_BIT;
	}

	public static synchronized int allocatePropertyId ()
	{
		return nextPropertyId++;
	}

	
	/**
	 * This class represents an <em>object context</em>. A
	 * graph state has a current object context which is obtained by
	 * {@link GraphState#getObjectContext()} and set by
	 * {@link GraphState#setObjectContext(Object, boolean)} and
	 * {@link GraphState#setObjectContext(ObjectContext)}. An object
	 * context consists of two parts:
	 * <ol>
	 * <li>An object of the graph ({@link #getObject()}, {@link #isNode()})
	 * <li>A usual key-value-map which associates arbitrary information
	 * to the keys within this object context. This map should be used only
	 * for caching purposes, because it is cleared by the invocation of
	 * {@link GraphState#setObjectContext(Object, boolean)}. The map
	 * uses <code>==</code> to compare keys.
	 * </ol> 
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class ObjectContext
	{
		Object object;
		boolean asNode;
		int instanceIndex;
		int stamp;
		final EHashMap cache;
		final EHashMap.IdentityEntry<Object,Object> key;


		ObjectContext (Object object, boolean asNode, int instanceIndex,
					 EHashMap.IdentityEntry<Object,Object>[] pool,
					 EHashMap.IdentityEntry<Object,Object> key)
		{
			this.object = object;
			this.asNode = asNode;
			this.instanceIndex = instanceIndex;
			this.cache = new EHashMap (pool, 32, 0.75f);
			this.key = key;
		}
		
		
		/**
		 * Returns the object of this context.
		 * 
		 * @return an object of the graph
		 */
		public Object getObject ()
		{
			return object;
		}
		
		
		/**
		 * Defines whether this object context specifies a node or an edge.
		 * 
		 * @return <code>true</code> if {@link #getObject()} returns a node,
		 * <code>false</code> if it returns an edge
		 */
		public boolean isNode ()
		{
			return asNode;
		}


		/**
		 * Returns the value associated with <code>owner</code>.
		 * 
		 * @param owner a key for the context map
		 * @return the associated value, or <code>null</code>
		 */
		public Object getValue (Object owner)
		{
			key.setKey (owner);
			owner = cache.get (key);
			return (owner != null) ? ((EHashMap.IdentityEntry) owner).value : null;
		}


		/**
		 * Associates a value with an owner in the context map.
		 * 
		 * @param owner a key for the context map
		 * @param value the new value to associate with <code>owner</code> 
		 */
		public void setValue (Object owner, Object value)
		{
			EHashMap.IdentityEntry<Object,Object> e = (EHashMap.IdentityEntry<Object,Object>) cache.popEntryFromPool ();
			if (e == null)
			{
				e = new EHashMap.IdentityEntry<Object,Object> ();
			}
			e.setKey (owner);
			e.value = value;
			cache.put (e);
		}
	}

	final ObjectList treeAttributeStack = new ObjectList ();

	final IntHashMap attributeMap;

	private Graph graph;
	private ThreadContext ctx;
	private boolean disposed;

	private final Int2ObjectMap userProperties = new Int2ObjectMap ();

	private int instancingCount = 0;
	private boolean instancingBegin = false;
	private ArrayPath instancingPath;
	private final ObjectList instanceAttributes = new ObjectList (32);
	private final IntList instanceAttributesIndex = new IntList (32);
	private final IntList instancingPoints = new IntList (32);

	private int curInstanceIndex = -1;
	private Object curInstance = null;
	private int curAttributeBegin = -1;
	private int curAttributeEnd = -1;


	protected GraphState ()
	{
		this.attributeMap = new IntHashMap (64, 0.2f);
	}


	GraphState (GraphState state)
	{
		this.attributeMap = state.attributeMap;
	}


	protected void initialize (Graph graph, ThreadContext ctx)
	{
		this.graph = graph;
		this.ctx = ctx;
		instancingPath = new ArrayPath (graph);
		if (graph.getStateMap ().put (ctx, new WeakReference (this)) != null)
		{
			throw new IllegalStateException ();
		}
	}


	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		graph.getStateMap ().remove (ctx);
		graph = null;
		ctx = null;
	}


	@Override
	protected void finalize ()
	{
		dispose ();
	}

	public Object setUserProperty (int id, Object value)
	{
		return userProperties.synchronizedPut (id, value);
	}


	public Object getUserProperty (int id)
	{
		return userProperties.synchronizedGet (id);
	}

	
	public GraphState forContext (ThreadContext tc)
	{
		GraphState gs = get (graph, tc);
		return (gs != null) ? gs : createDelegate (tc);
	}


	protected abstract GraphState createDelegate (ThreadContext tc);


	/**
	 * Returns the graph state of the <code>graph</code> which is
	 * valid within the given <code>ThreadContext</code>.
	 * 
	 * @param graph a graph
	 * @param tc a thread context
	 * @return the graph state for the context, or <code>null</code> if such
	 * a graph state has not yet been created
	 */
	public static GraphState get (Graph graph, ThreadContext tc)
	{
		WeakReference ref = (WeakReference) graph.getStateMap ().get (tc);
		return (ref != null) ? (GraphState) ref.get () : null;
	}


	/**
	 * Returns the graph state of the <code>graph</code> which is
	 * valid within the current thread.
	 * 
	 * @param graph a graph
	 * @return the graph state for current thread, or <code>null</code> if such
	 * a graph state has not yet been created
	 */
	public static GraphState current (Graph graph)
	{
		return get (graph, ThreadContext.current ());
	}


	/**
	 * Returns the graph of this graph state.
	 * 
	 * @return graph of this state
	 */
	public final Graph getGraph ()
	{
		return graph;
	}


	/**
	 * Returns the <code>ThreadContext</code> within which operations
	 * on this graph state are valid.
	 * 
	 * @return <code>ThreadContext</code> of this graph state
	 */
	public final ThreadContext getContext ()
	{
		return ctx;
	}


	private final EHashMap.IdentityEntry[] cachePool = new EHashMap.IdentityEntry[1];
	private final EHashMap.IdentityEntry cacheKey = new EHashMap.IdentityEntry ();
	private ObjectContext objectContext = new ObjectContext (null, false, 0, cachePool, cacheKey);


	public void setObjectContext (Object object, boolean asNode)
	{
		objectContext.object = object;
		objectContext.asNode = asNode;
		if (curInstanceIndex > 0)
		{
			if ((object != null)
				&& (asNode != ((curInstanceIndex & 1) == 0)))
			{
				throw new IllegalStateException ();
			}
			objectContext.instanceIndex = curInstanceIndex;
		}
		else
		{
			objectContext.instanceIndex = 0;
		}
		objectContext.cache.clear ();
		objectContext.stamp = graph.getStamp ();
	}

	
	public ObjectContext getObjectContext ()
	{
		if (objectContext.stamp != graph.getStamp ())
		{
			objectContext.stamp = graph.getStamp ();
			objectContext.cache.clear ();
		}
		return objectContext;
	}


	public void setObjectContext (ObjectContext newState)
	{
		objectContext = newState;
		objectContext.stamp = graph.getStamp ();
		if (instancingCount > 0)
		{
			moveToInstance (newState.instanceIndex);
		}
	}


	public ObjectContext createObjectState (Object object, boolean asNode)
	{
		if ((curInstanceIndex > 0)
			&& (asNode != ((curInstanceIndex & 1) == 0)))
		{
			throw new IllegalStateException ();
		}
		return new ObjectContext (object, asNode, curInstanceIndex, cachePool, cacheKey);
	}

	
	public abstract void setEdgeBits (Object edge, int bits);

	
	public abstract void fireAttributeChanged
		(Object object, boolean asNode, Attribute a, FieldChain field,
		 int[] indices);

	
	protected abstract void fireEdgeChanged
		(Object source, Object target, Object edge);

	
	/**
	 * Tests whether an object is contained in the tree of the graph.
	 * The tree is defined by {@link Graph#getTreePattern()}.
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @return <code>true</code> iff the object is part of the tree
	 */
	public abstract boolean containsInTree (Object object, boolean asNode);


	public Path getInstancingPath ()
	{
		return (instancingCount > 0) ? instancingPath : null;
	}
	
	
	public int getInstancingPathIndex ()
	{
		return curInstanceIndex;
	}


	void setInstancingTreeAttribute (Attribute a, int index, Object value)
	{
	}


	Object getInstancingTreeAttribute (Attribute a, int index)
	{
		return null;
	}


	boolean wasTreeAttributeValid ()
	{
		return false;
	}


	public void beginInstancing (Object refNode, long id)
	{
		if (++instancingCount == 1)
		{
			instancingPath.pushNode (refNode, id);
			instanceAttributes.clear ();
			curInstanceIndex = 0;
			curAttributeEnd = 0;
		}
		instancingPoints.push (instancingPath.getNodeAndEdgeCount ());
		instancingBegin = true;
	}


	public void endInstancing ()
	{
		instancingBegin = false;
		int c = instancingPath.getNodeAndEdgeCount () - instancingPoints.pop ();
		if (c > 0)
		{
			while (c > 0)
			{
				instancingPath.popNode ();
				instancingPath.popEdgeSet ();
				c -= 2;
			}
			curAttributeBegin = instanceAttributesIndex.get
				((instancingPath.getNodeAndEdgeCount () + 2) >> 1);
		}
		int index = instancingPath.getNodeAndEdgeCount () - 1;
		if (index > 0)
		{
			moveToInstance (index);
			instanceAttributes.setSize (curAttributeEnd);
		}
		else
		{
			curInstanceIndex = -1;
			curInstance = null;
			instanceAttributes.clear ();
		}
		if (--instancingCount == 0)
		{
			instancingPath.popNode ();
		}
	}


	public void instantiate (Object object, boolean asNode, long id)
	{
		if ((curInstanceIndex + 1 != instancingPath.getNodeAndEdgeCount ())
			|| (((curInstanceIndex & 1) == 0) == asNode))
		{
			throw new IllegalStateException ();
		}
		if (asNode)
		{
			instancingPath.pushNode (object, id);
		}
		else
		{
			instancingPath.pushEdgeSet (object, id, instancingBegin);
			instancingBegin = false;
		}
		curInstance = object;
		curAttributeBegin = curAttributeEnd;
		instanceAttributesIndex.set (++curInstanceIndex, curAttributeBegin);
	}


	public void instantiateEdge (int edges, boolean edgeDirection, long id)
	{
		if ((curInstanceIndex + 1 != instancingPath.getNodeAndEdgeCount ())
			|| ((curInstanceIndex & 1) != 0))
		{
			throw new IllegalStateException ();
		}
		instancingPath.pushEdges (edges, edgeDirection, id, instancingBegin);
		instancingBegin = false;
		curInstance = null;
		curAttributeBegin = curAttributeEnd;
		instanceAttributesIndex.set (++curInstanceIndex, curAttributeBegin);
	}
	

	public void deinstantiate ()
	{
		if (curInstanceIndex + 1 != instancingPath.getNodeAndEdgeCount ())
		{
			throw new IllegalStateException ();
		}
		if ((curInstanceIndex & 1) == 0)
		{
			instancingPath.popNode ();
		}
		else
		{
			instancingPath.popEdgeSet ();
		}
		moveToInstance (curInstanceIndex - 1);
	}


	public void setInstanceAttribute (Attribute a, Object value)
	{
		if (instanceAttributes.size () != curAttributeEnd)
		{
			throw new IllegalStateException ();
		}
		instanceAttributes.push (a).push (value);
		curAttributeEnd += 2;
	}


	public void moveToPreviousInstance ()
	{
		int index = curInstanceIndex - 1;
		if (index < 0)
		{
			throw new IllegalStateException ();
		}
		curAttributeEnd = curAttributeBegin;
		curAttributeBegin = instanceAttributesIndex.get (index);
		curInstanceIndex = index;
		curInstance = instancingPath.getObject (index);
	}


	public void moveToNextInstance ()
	{
		int index = curInstanceIndex + 1;
		if (index >= instancingPath.getNodeAndEdgeCount ())
		{
			throw new IllegalStateException ();
		}
		curAttributeBegin = curAttributeEnd;
		curAttributeEnd = (index + 1 == instancingPath.getNodeAndEdgeCount ())
			? instanceAttributes.size ()
			: instanceAttributesIndex.get (index + 1);
		curInstanceIndex = index;
		curInstance = instancingPath.getObject (index);
	}


	public void moveToInstance (int index)
	{
		if ((index < 0) || (index >= instancingPath.getNodeAndEdgeCount ()))
		{
			throw new IllegalStateException ();
		}
		curAttributeBegin = instanceAttributesIndex.get (index);
		curAttributeEnd = (index + 1 == instancingPath.getNodeAndEdgeCount ())
			? instanceAttributes.size ()
			: instanceAttributesIndex.get (index + 1);
		curInstanceIndex = index;
		curInstance = instancingPath.getObject (index);
	}


	public <T> T getObject (Object object, boolean asNode, ObjectAttribute<T> a)
	{
		return getObject (object, asNode, null, a);
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

#if ($pp.Object)
	#set ($sig = "<T>")
	#set ($esig = "<? extends T>")
	#set ($T = "T")
#else
	#set ($sig = "")
	#set ($esig = "")
	#set ($T = "$type")
#end

#macro (VALUE_OF $x)
	#if ($pp.Object)
		a.valueOf ($x)
	#else
		$x
	#end
#end


	public $sig $T check${pp.Type} (Object object, boolean asNode, ${pp.Type}Attribute$esig a,
								  $T value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
#if ($pp.Object)
				return a.valueOf (e[i + 1]);
#else
				return $pp.unwrap("e[i + 1]");
#end
			}
		}
		return value;
	}


	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 $C
	public $sig $T get${pp.Type}Default (Object object, boolean asNode, ${pp.Type}Attribute$esig a,
									   $T defaultValue)
	{
		if (a.isDerived ())
		{
#if ($pp.Object)
			return a.getDerived (object, asNode, null, this);
#else
			return a.getDerived (object, asNode, this);
#end
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
#if ($pp.Object)
					return a.valueOf (e[i + 1]);
#else
					return $pp.unwrap("e[i + 1]");
#end
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return #VALUE_OF("((${pp.Type}AttributeAccessor) ac).get$pp.Type (object, this)");
			}
		}
		return defaultValue;
	}


	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
#if ($pp.Object)
	 * @param placeIn an instance for the result may be provided by the caller 
#end
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 $C
	public $sig $T get$pp.Type (Object object, boolean asNode,
#if ($pp.Object)
	#set ($placeIn = "placeIn, ")
							  $T placeIn,
#else
	#set ($placeIn = "")
#end
							  ${pp.Type}Attribute$sig a)
	{
		if (a.isDerived ())
		{
#if ($pp.Object)
			return a.getDerived (object, asNode, null, this);
#else
			return a.getDerived (object, asNode, this);
#end
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
#if ($pp.Object)
					return a.valueOf (e[i + 1]);
#else
					return $pp.unwrap("e[i + 1]");
#end
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return #VALUE_OF("((${pp.Type}AttributeAccessor) ac).get$pp.Type (object, ${placeIn}this)");
			}
		}
		throw new NoSuchKeyException (object, a);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
// generated
// generated
	public  boolean checkBoolean (Object object, boolean asNode, BooleanAttribute a,
								  boolean value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Boolean) (e[i + 1])).booleanValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  boolean getBooleanDefault (Object object, boolean asNode, BooleanAttribute a,
									   boolean defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Boolean) (e[i + 1])).booleanValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((BooleanAttributeAccessor) ac).getBoolean (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  boolean getBoolean (Object object, boolean asNode,
							  BooleanAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Boolean) (e[i + 1])).booleanValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((BooleanAttributeAccessor) ac).getBoolean (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  byte checkByte (Object object, boolean asNode, ByteAttribute a,
								  byte value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).byteValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  byte getByteDefault (Object object, boolean asNode, ByteAttribute a,
									   byte defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).byteValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((ByteAttributeAccessor) ac).getByte (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  byte getByte (Object object, boolean asNode,
							  ByteAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).byteValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((ByteAttributeAccessor) ac).getByte (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  short checkShort (Object object, boolean asNode, ShortAttribute a,
								  short value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).shortValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  short getShortDefault (Object object, boolean asNode, ShortAttribute a,
									   short defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).shortValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((ShortAttributeAccessor) ac).getShort (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  short getShort (Object object, boolean asNode,
							  ShortAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).shortValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((ShortAttributeAccessor) ac).getShort (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  char checkChar (Object object, boolean asNode, CharAttribute a,
								  char value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Character) (e[i + 1])).charValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  char getCharDefault (Object object, boolean asNode, CharAttribute a,
									   char defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Character) (e[i + 1])).charValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((CharAttributeAccessor) ac).getChar (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  char getChar (Object object, boolean asNode,
							  CharAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Character) (e[i + 1])).charValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((CharAttributeAccessor) ac).getChar (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  int checkInt (Object object, boolean asNode, IntAttribute a,
								  int value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).intValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  int getIntDefault (Object object, boolean asNode, IntAttribute a,
									   int defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).intValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((IntAttributeAccessor) ac).getInt (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  int getInt (Object object, boolean asNode,
							  IntAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).intValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((IntAttributeAccessor) ac).getInt (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  long checkLong (Object object, boolean asNode, LongAttribute a,
								  long value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).longValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  long getLongDefault (Object object, boolean asNode, LongAttribute a,
									   long defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).longValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((LongAttributeAccessor) ac).getLong (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  long getLong (Object object, boolean asNode,
							  LongAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).longValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((LongAttributeAccessor) ac).getLong (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  float checkFloat (Object object, boolean asNode, FloatAttribute a,
								  float value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).floatValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  float getFloatDefault (Object object, boolean asNode, FloatAttribute a,
									   float defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).floatValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((FloatAttributeAccessor) ac).getFloat (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  float getFloat (Object object, boolean asNode,
							  FloatAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).floatValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((FloatAttributeAccessor) ac).getFloat (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public  double checkDouble (Object object, boolean asNode, DoubleAttribute a,
								  double value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return (((Number) (e[i + 1])).doubleValue ());
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public  double getDoubleDefault (Object object, boolean asNode, DoubleAttribute a,
									   double defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).doubleValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((DoubleAttributeAccessor) ac).getDouble (object, this)
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public  double getDouble (Object object, boolean asNode,
							  DoubleAttribute a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return (((Number) (e[i + 1])).doubleValue ());
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			((DoubleAttributeAccessor) ac).getDouble (object, this)
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
// generated
// generated
// generated
// generated
// generated
	public <T> T checkObject (Object object, boolean asNode, ObjectAttribute<? extends T> a,
								  T value)
	{
		if ((instancingCount == 0) || (object != curInstance)
			|| (asNode != ((curInstanceIndex & 1) == 0)))
		{
			return value;
		}
		Object[] e = instanceAttributes.elements;
		for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
		{
			if (a == e[i])
			{
				return a.valueOf (e[i + 1]);
			}
		}
		return value;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. If no such value is defined,
	 * <code>defaultValue</code> is used instead.
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param defaultValue default value to return if the attribute is not defined
	 * on <code>object</code>
	 * @return value of the attribute for the object, or <code>defaultValue</code>
	 */
	public <T> T getObjectDefault (Object object, boolean asNode, ObjectAttribute<? extends T> a,
									   T defaultValue)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, null, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return a.valueOf (e[i + 1]);
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			a.valueOf (((ObjectAttributeAccessor) ac).getObject (object, this))
	;
			}
		}
		return defaultValue;
	}
// generated
// generated
	/**
	 * Returns the value of attribute <code>a</code> for <code>object</code>
	 * in this graph state. 
	 * This method respects	derived attributes and attribute values
	 * of instantiated objects which
	 * are set by {@link #setInstanceAttribute(Attribute, Object)}. 
	 * 
	 * @param object the object
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param a the attribute to read
	 * @param placeIn an instance for the result may be provided by the caller 
	 * @return value of the attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	public <T> T getObject (Object object, boolean asNode,
							  T placeIn,
							  ObjectAttribute<T> a)
	{
		if (a.isDerived ())
		{
			return a.getDerived (object, asNode, null, this);
		}
		if ((instancingCount > 0) && (object == curInstance)
			&& (asNode == ((curInstanceIndex & 1) == 0)))
		{
			Object[] e = instanceAttributes.elements;
			for (int i = curAttributeBegin; i < curAttributeEnd; i += 2)
			{
				if (a == e[i])
				{
					return a.valueOf (e[i + 1]);
				}
			}
		}
		if (object != null)
		{
			AttributeAccessor ac;
			if ((ac = graph.getAccessor (object, asNode, a)) != null)
			{
				return 			a.valueOf (((ObjectAttributeAccessor) ac).getObject (object, placeIn, this))
	;
			}
		}
		throw new NoSuchKeyException (object, a);
	}
// generated
//!! *# End of generated code

}
