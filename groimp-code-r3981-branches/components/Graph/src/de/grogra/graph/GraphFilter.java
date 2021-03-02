
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

import java.util.*;

import de.grogra.graph.impl.GraphManager;
import de.grogra.util.*;

/**
 * A <code>GraphFilter</code> is a {@link de.grogra.graph.Graph} which
 * has another graph as source and filters the data of the source graph.
 * Filtering may include modification of structure and attributes.
 * The implementations of <code>Graph</code> methods in this class
 * just forward their invocation to the corresponding methods
 * of the source graph. 
 *
 * @author Ole Kniemeyer
 */
public abstract class GraphFilter extends GraphBase
	implements ChangeBoundaryListener, AttributeChangeListener, EdgeChangeListener
{
	/**
	 * The source graph whose data is filtered by this graph filter.
	 */
	protected final Graph source;

	
	/**
	 * This helper class delegates accessor method invocations
	 * to the corresponding <code>get</code>-, <code>set</code>-
	 * and <code>isWritable</code>-methods of the enclosing graph filter.
	 *
	 * @author Ole Kniemeyer
	 */
	public class AccessorBridge extends AccessorBase
	{
		/**
		 * Indicates if this accessor is used for nodes or for edges.
		 */
		public final boolean forNode;


		/**
		 * Creates a new <code>AccessorBridge</code> for the given
		 * <code>attribute</code>. Its field {@link #forNode} is set to
		 * the provided value.
		 * 
		 * @param attribute the attribute of this accessor bridge
		 * @param forNode indicates if this accessor is used for nodes or for edges
		 */
		public AccessorBridge (Attribute attribute, boolean forNode)
		{
			super (attribute);
			this.forNode = forNode;
		}


		public boolean isWritable (Object object, GraphState gs)
		{
			return GraphFilter.this.isWritable (object, this, gs);
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)

		public $type get$pp.Type (Object object, GraphState gs)
		{
#if ($pp.Object)
			return GraphFilter.this.get$pp.Type (object, this, null, gs);
#else
			return GraphFilter.this.get$pp.Type (object, this, gs);
#end
		}


		public $type set$pp.Type (Object object, $type value, GraphState gs)
		{
			return GraphFilter.this.set$pp.Type (object, this, value, gs);
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public boolean getBoolean (Object object, GraphState gs)
		{
			return GraphFilter.this.getBoolean (object, this, gs);
		}
// generated
// generated
		public boolean setBoolean (Object object, boolean value, GraphState gs)
		{
			return GraphFilter.this.setBoolean (object, this, value, gs);
		}
// generated
// generated
		public byte getByte (Object object, GraphState gs)
		{
			return GraphFilter.this.getByte (object, this, gs);
		}
// generated
// generated
		public byte setByte (Object object, byte value, GraphState gs)
		{
			return GraphFilter.this.setByte (object, this, value, gs);
		}
// generated
// generated
		public short getShort (Object object, GraphState gs)
		{
			return GraphFilter.this.getShort (object, this, gs);
		}
// generated
// generated
		public short setShort (Object object, short value, GraphState gs)
		{
			return GraphFilter.this.setShort (object, this, value, gs);
		}
// generated
// generated
		public char getChar (Object object, GraphState gs)
		{
			return GraphFilter.this.getChar (object, this, gs);
		}
// generated
// generated
		public char setChar (Object object, char value, GraphState gs)
		{
			return GraphFilter.this.setChar (object, this, value, gs);
		}
// generated
// generated
		public int getInt (Object object, GraphState gs)
		{
			return GraphFilter.this.getInt (object, this, gs);
		}
// generated
// generated
		public int setInt (Object object, int value, GraphState gs)
		{
			return GraphFilter.this.setInt (object, this, value, gs);
		}
// generated
// generated
		public long getLong (Object object, GraphState gs)
		{
			return GraphFilter.this.getLong (object, this, gs);
		}
// generated
// generated
		public long setLong (Object object, long value, GraphState gs)
		{
			return GraphFilter.this.setLong (object, this, value, gs);
		}
// generated
// generated
		public float getFloat (Object object, GraphState gs)
		{
			return GraphFilter.this.getFloat (object, this, gs);
		}
// generated
// generated
		public float setFloat (Object object, float value, GraphState gs)
		{
			return GraphFilter.this.setFloat (object, this, value, gs);
		}
// generated
// generated
		public double getDouble (Object object, GraphState gs)
		{
			return GraphFilter.this.getDouble (object, this, gs);
		}
// generated
// generated
		public double setDouble (Object object, double value, GraphState gs)
		{
			return GraphFilter.this.setDouble (object, this, value, gs);
		}
// generated
// generated
		public Object getObject (Object object, GraphState gs)
		{
			return GraphFilter.this.getObject (object, this, null, gs);
		}
// generated
// generated
		public Object setObject (Object object, Object value, GraphState gs)
		{
			return GraphFilter.this.setObject (object, this, value, gs);
		}
//!! *# End of generated code

		public Object getObject (Object object, Object placeIn, GraphState gs)
		{
			return GraphFilter.this.getObject (object, this, placeIn, gs);
		}
	}
	

	public GraphFilter (Graph source)
	{
		super (source);
		this.source = source;
		init ();
		WeakListenerDelegate ld = new WeakListenerDelegate (this);
		source.addAttributeChangeListener (ld);
		source.addEdgeChangeListener (ld);
		source.addChangeBoundaryListener (ld);
	}
	
	
	@Override
	protected GraphBase.State createMainState ()
	{
		return (State) createState (source.getMainState ().getContext ());
	}

	
//	public void accept (Object startNode, final Visitor visitor,
//			ArrayPath placeInPath)
//	{
//		accept (startNode, visitor, placeInPath, false);
//	}
	
	private static class DelegateState extends DelegateGraphState
	{
		final GraphState srcDelegate;
		
		DelegateState (State gs, ThreadContext tc)
		{
			super (gs, tc);
			srcDelegate = gs.sourceState.forContext (tc);
		}

		@Override
		public boolean containsInTree (Object object, boolean asNode)
		{
			return srcDelegate.containsInTree (object, asNode);

		}
	}


	protected class State extends GraphBase.State
	{
		final GraphState sourceState;

		
		public State (GraphState sourceState, ThreadContext ctx)
		{
			super (ctx);
			this.sourceState = sourceState;
		}

		
		@Override
		public boolean containsInTree (Object object, boolean asNode)
		{
			return sourceState.containsInTree (object, asNode);
		}


		@Override
		public void setEdgeBits (Object edge, int bits)
		{
			sourceState.setEdgeBits (edge, bits);
		}


		@Override
		public GraphState createDelegate (ThreadContext tc)
		{
			return new DelegateState (this, tc);
		}

	}
	
	
	public static GraphState getSourceState (GraphState gs)
	{
		return (gs instanceof State) ? ((State) gs).sourceState
			: ((DelegateState) gs).srcDelegate;
	}


	public void beginChange (GraphState gs)
	{
		support.fireBeginChange (mainState);
	}

	
	public void endChange (GraphState gs)
	{
		mainState.getQueue ().fire (mainState, false);
		support.fireEndChange (mainState);
		while (!mainState.getQueue ().isEmpty ())
		{
			mainState.getQueue ().fire (mainState, true);
		}
	}
	
	
	public int getPriority ()
	{
		return ATTRIBUTE_PRIORITY;
	}
	
	
	public void edgeChanged (Object source, Object target, Object edgeSet,
							 GraphState gs)
	{
		if ((edgeSet != null) ? getLifeCycleState (edgeSet, false) != TRANSIENT
			: (getLifeCycleState (source, true) != TRANSIENT) && (getLifeCycleState (target, true) != TRANSIENT))
		{
			support.fireEdgeChanged (source, target, edgeSet, mainState);
		}
	}


	public void attributeChanged (AttributeChangeEvent event)
	{
		if (getLifeCycleState (event.object, event.node) != TRANSIENT)
		{
			Attribute a = event.attr;
			if (a != null)
			{
				Object o = event.object;
				boolean n = event.node;
				Attribute[] dep = event.dependent;
				for (int i = event.dependent.length - 1; i >= 0; i--)
				{
					Attribute[] b = getDependentOfSource (o, n, dep[i]);
					for (int j = b.length - 1; j >= 0; j--)
					{
						if (a == b[j])
						{
							support.fireAttributeChanged (o, n, a, event.field, event.indices, mainState);
						}
						else
						{
							support.fireAttributeChanged (o, n, b[j], null, null, mainState);
						}
					}
				}
			}
			else
			{
				support.fireAttributeChanged (event.object, event.node, null, null, null, mainState);
			}
		}
	}
	

	public int getStamp ()
	{
		return source.getStamp ();
	}
	

	public int getLifeCycleState (Object object, boolean asNode)
	{
		return source.getLifeCycleState (object, asNode);
	}

	
	public abstract String getType();
	
	public void accept (Object startNode, final Visitor visitor,
			ArrayPath placeInPath)
	{
		accept (startNode, visitor, placeInPath, false, getType());
	}
	
	public void accept (Object startNode, final Visitor visitor,
			ArrayPath placeInPath, String graphType)
	{
		accept (startNode, visitor, placeInPath, false, graphType);
	}
	
	protected void accept (Object startNode, final Visitor visitor,
						   ArrayPath placeInPath, final boolean useInstancing, String graphType)
	{
		if (placeInPath == null)
		{
			placeInPath = new ArrayPath (this);
		}
		else
		{
			placeInPath.clear (this);
		}
		if (startNode == null)
		{
			startNode = getRoot (graphType);
		}
		source.accept (startNode, new Visitor ()
		{
			private boolean instancing = false;
			private int instancingCount = 0;

			
			public GraphState getGraphState ()
			{
				return visitor.getGraphState ();
			}

			
			public Object visitEnter (Path path, boolean node)
			{
				Object o = null;
				if (instancing
					|| ((getLifeCycleState (path.getObject (-1), true) == PERSISTENT)
						&& (node || ((o = path.getObject (-2)) == null)
							|| (getLifeCycleState (o, false) == PERSISTENT))))
				{
					((ArrayPath) path).graph = GraphFilter.this;
					o = visitor.visitEnter (path, node);
					((ArrayPath) path).graph = source;
					return o;
				}
				return STOP;
			}

			
			public boolean visitLeave (Object o, Path path, boolean node)
			{
				if ((o != STOP) || instancing || (getLifeCycleState (path.getObject (-1), true) == PERSISTENT))
				{
					((ArrayPath) path).graph = GraphFilter.this;
					if (visitor.visitLeave (o, path, node))
					{
						((ArrayPath) path).graph = source;
						return true;
					}
					else
					{
						((ArrayPath) path).graph = source;
						return false;
					}
				}
				return true;
			}


			public Object visitInstanceEnter ()
			{
				if (!useInstancing)
				{
					return STOP;
				}
				instancing = true;
				instancingCount++;
				return visitor.visitInstanceEnter ();
			}


			public boolean visitInstanceLeave (Object o)
			{
				if (!useInstancing)
				{
					return true;
				}
				instancing = --instancingCount > 0;
				return visitor.visitInstanceLeave (o);
			}
			
		}, placeInPath);
	}

	
	protected GraphState createState (ThreadContext tc)
	{
		return new State (GraphState.get (source, tc), tc);
	}


	public BooleanMap createBooleanMap ()
	{
		return source.createBooleanMap ();
	}

	
	public <V> ObjectMap<V> createObjectMap ()
	{
		return source.createObjectMap ();
	}

	
	public AttributeAccessor getAccessor (Object object, boolean asNode, Attribute attribute)
	{
		return source.getAccessor (object, asNode, attribute);
	}

	
	public Attribute[] getDependent (Object object, boolean asNode, Attribute a)
	{
		return source.getDependent (object, asNode, a);
	}

	
	protected Attribute[] getDependentOfSource (Object object, boolean asNode, Attribute a)
	{
		return a.toArray ();
	}


	public Attribute[] getAttributes (Object object, boolean asNode)
	{
		return source.getAttributes (object, asNode);
	}

	
	public String[] getRootKeys ()
	{
		return source.getRootKeys ();
	}

	
	public Object getRoot (String key)
	{
		return source.getRoot (key);
	}

	
	public Object getFirstEdge (Object node)
	{
		return filter (source.getFirstEdge (node), node);
	}

	
	public Object getNextEdge (Object edge, Object parentNode)
	{
		return filter (source.getNextEdge (edge, parentNode), parentNode);
	}

	
	private Object filter (Object edge, Object parentNode)
	{
		while (edge != null)
		{
			if (getLifeCycleState (edge, false) == PERSISTENT)
			{
				Object t = source.getTargetNode (edge);
				if (getLifeCycleState ((t == parentNode) ? source.getSourceNode (edge) : t, true) == PERSISTENT)
				{
					return edge;
				}
			}
			edge = source.getNextEdge (edge, parentNode);
		}
		return null;
	}
	
	
	public int getEdgeBits (Object edge)
	{
		return source.getEdgeBits (edge);
	}

	
	public Object getSourceNode (Object edge)
	{
		return source.getSourceNode (edge);
	}

	
	public Object getTargetNode (Object edge)
	{
		return source.getTargetNode (edge);
	}


	public Instantiator getInstantiator (Object node)
	{
		return source.getInstantiator (node);
	}

	
	public String getName (Object object, boolean asNode)
	{
		return source.getName (object, asNode);
	}
	

	public Object getDescription (Object object, boolean asNode, String type)
	{
		return source.getDescription (object, asNode, type);
	}


	public SpecialEdgeDescriptor[] getSpecialEdgeDescriptors (Object node, boolean asSource)
	{
		return source.getSpecialEdgeDescriptors (node, asSource);
	}


	public long getId (Object node)
	{
		return source.getId (node);
	}
	

	public Object getNodeForId (long persistentId)
	{
		return source.getNodeForId (persistentId);
	}


	public Object getObjectForName (boolean node, String name)
	{
		return source.getObjectForName (node, name);
	}

	
	public int getSymbol (Object object, boolean asNode)
	{
		return source.getSymbol (object, asNode);
	}
	
	
	public int getColor (Object object, boolean asNode)
	{
		return source.getColor (object, asNode);
	}
	
	
	public ObjectAttribute getParentAttribute ()
	{
		return source.getParentAttribute ();
	}


	public EdgePattern getTreePattern ()
	{
		return source.getTreePattern ();
	}
	

	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation
	 * of {@link AttributeAccessor#isWritable(Object, GraphState)} to
	 * this method. This method has to be overridden by subclasses which
	 * declare a writable attribute by an accessor bridge.
	 * 
	 * @param object an object
	 * @param accessor an accessor bridge
	 * @param gs current graph state
	 * @return <code>true</code> iff <code>accessor</code> represents
	 * a writable attribute 
	 */
	protected boolean isWritable (Object object, AccessorBridge accessor, GraphState gs)
	{
		return false;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ${pp.Type}AttributeAccessor#get${pp.Type}(Object, GraphState)}
#if ($pp.Object)
	 * and {@link ${pp.Type}AttributeAccessor#get${pp.Type}(Object, Object, GraphState)}
#end
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
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
	protected $type get$pp.Type (Object object, AccessorBridge accessor,
#if ($pp.Object)
								 Object placeIn,
#end
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}


	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ${pp.Type}AttributeAccessor#set${pp.Type}(Object, $type, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 $C
	protected $type set$pp.Type (Object object, AccessorBridge accessor,
								 $type value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link BooleanAttributeAccessor#getBoolean(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected boolean getBoolean (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link BooleanAttributeAccessor#setBoolean(Object, boolean, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected boolean setBoolean (Object object, AccessorBridge accessor,
								 boolean value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ByteAttributeAccessor#getByte(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected byte getByte (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ByteAttributeAccessor#setByte(Object, byte, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected byte setByte (Object object, AccessorBridge accessor,
								 byte value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ShortAttributeAccessor#getShort(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected short getShort (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ShortAttributeAccessor#setShort(Object, short, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected short setShort (Object object, AccessorBridge accessor,
								 short value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link CharAttributeAccessor#getChar(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected char getChar (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link CharAttributeAccessor#setChar(Object, char, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected char setChar (Object object, AccessorBridge accessor,
								 char value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link IntAttributeAccessor#getInt(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected int getInt (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link IntAttributeAccessor#setInt(Object, int, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected int setInt (Object object, AccessorBridge accessor,
								 int value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link LongAttributeAccessor#getLong(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected long getLong (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link LongAttributeAccessor#setLong(Object, long, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected long setLong (Object object, AccessorBridge accessor,
								 long value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link FloatAttributeAccessor#getFloat(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected float getFloat (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link FloatAttributeAccessor#setFloat(Object, float, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected float setFloat (Object object, AccessorBridge accessor,
								 float value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link DoubleAttributeAccessor#getDouble(Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected double getDouble (Object object, AccessorBridge accessor,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link DoubleAttributeAccessor#setDouble(Object, double, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected double setDouble (Object object, AccessorBridge accessor,
								 double value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ObjectAttributeAccessor#getObject(Object, GraphState)}
	 * and {@link ObjectAttributeAccessor#getObject(Object, Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * make use of accessor bridges in order to return the value for
	 * the attribute of the accessor bridge. 
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param placeIn an instance for the result may be provided by the caller 
	 * @param gs current graph state
	 * @return value of the accessor's attribute for the object
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected Object getObject (Object object, AccessorBridge accessor,
								 Object placeIn,
								 GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
// generated
	/**
	 * {@link GraphFilter.AccessorBridge} forwards the invocation of
	 * {@link ObjectAttributeAccessor#setObject(Object, Object, GraphState)}
	 * to this method. This method has to be overridden by subclasses which
	 * use accessor bridges to represent writable attributes.
	 * 
	 * @param object the object
	 * @param accessor the accessor bridge which invokes this method
	 * @param value the new value for the attribute 
	 * @param gs current graph state
	 * @return actual new value of the attribute
	 * @throws NoSuchKeyException if the attribute is not defined for the object
	 */
	protected Object setObject (Object object, AccessorBridge accessor,
								 Object value, GraphState gs)
	{
		throw new NoSuchElementException (accessor.getAttribute ().getKey ());
	}
// generated
//!! *# End of generated code

}
