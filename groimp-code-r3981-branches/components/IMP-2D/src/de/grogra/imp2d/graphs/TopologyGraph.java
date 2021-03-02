
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

package de.grogra.imp2d.graphs;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.EventObject;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.vecmath.Color3f;
import javax.vecmath.Tuple2d;

import de.grogra.graph.AccessorMap;
import de.grogra.graph.ArrayPath;
import de.grogra.graph.Attribute;
import de.grogra.graph.AttributeOverwritingFilter;
import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.EdgePattern;
import de.grogra.graph.EdgePatternImpl;
import de.grogra.graph.EventSupport;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.ObjectMapImpl;
import de.grogra.graph.Path;
import de.grogra.graph.SpecialEdgeDescriptor;
import de.grogra.graph.Visitor;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp2d.ClickEvent2D;
import de.grogra.imp2d.IMP2D;
import de.grogra.imp2d.View2DIF;
import de.grogra.imp2d.edit.Editable;
import de.grogra.imp2d.objects.Arrow;
import de.grogra.imp2d.objects.Attributes;
import de.grogra.imp2d.objects.Connection;
import de.grogra.imp2d.objects.NURBSShape2D;
import de.grogra.imp2d.objects.Rhombus;
import de.grogra.imp2d.objects.Shape2D;
import de.grogra.imp2d.objects.StrokeAdapter;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.util.Described;
import de.grogra.util.EnumValueImpl;
import de.grogra.util.EventListener;
import de.grogra.util.Int2ObjectMap;
import de.grogra.util.ThreadContext;
import de.grogra.xl.util.ObjectList;


public class TopologyGraph extends AttributeOverwritingFilter implements EventListener
{
	final ObjectMapImpl data;
	private final String root;

	private AccessorBridge nodeShape, nodeTransformation, nodeShape2D,
		nodeEditable, nodeTransform, nodeCaption, nodeHAlignment,
		nodeFillColor,
		edgeShape, edgeTransformation, edgeCurve, edgeArrow, edgeFilled,
		edgeColor, edgeStroke, edgeBits, edgeCaption, edgeFillColor,
		edgeWeight;

	private final Shape2D shapeInstance;
	
	private final static HashSet<Object> collapsedNodes = new HashSet<Object> ();
	
	public TopologyGraph (Graph source, String root)
	{
		super (source);
		initAttributeOverwritingFilter ();
		this.root = root;
		shapeInstance = new Shape2D ();
		shapeInstance.setOutlined (false);
		this.data = new ObjectMapImpl ();
	}

	
	@Override
	protected void initNodeAccessors (AccessorMap nodeAccessors)
	{
		nodeAccessors.add (nodeShape = new AccessorBridge (Attributes.SHAPE, true));
		nodeAccessors.add (nodeTransformation = new AccessorBridge (Attributes.TRANSFORMATION, true));
		nodeAccessors.add (nodeShape2D = new AccessorBridge (Attributes.SHAPE_2D, true));
		nodeAccessors.add (nodeFillColor = new AccessorBridge (Attributes.FILL_COLOR, true));
		nodeAccessors.add (nodeEditable = new AccessorBridge (Editable.ATTRIBUTE, true));
		nodeAccessors.add (nodeTransform = new AccessorBridge (Attributes.TRANSFORM, true));
		nodeAccessors.add (nodeCaption = new AccessorBridge (Attributes.CAPTION, true));
		nodeAccessors.add (nodeHAlignment = new AccessorBridge (Attributes.HORIZONTAL_ALIGNMENT, true));
	}

	
	@Override
	protected void initEdgeAccessors (AccessorMap edgeAccessors)
	{
		edgeAccessors.add (edgeShape = new AccessorBridge (Attributes.SHAPE, false));
		edgeAccessors.add (edgeCurve = new AccessorBridge (Attributes.CURVE, false));
		edgeAccessors.add (edgeTransformation = new AccessorBridge (Attributes.TRANSFORMATION, false));
		edgeAccessors.add (edgeArrow = new AccessorBridge (Attributes.END_ARROW, false));
		edgeAccessors.add (edgeFilled = new AccessorBridge (Attributes.FILLED, false));
		edgeAccessors.add (edgeColor = new AccessorBridge (Attributes.COLOR, false));
		edgeAccessors.add (edgeFillColor = new AccessorBridge (Attributes.FILL_COLOR, false));
		edgeAccessors.add (edgeStroke = new AccessorBridge (Attributes.STROKE, false));
		edgeAccessors.add (edgeBits = new AccessorBridge (EdgeBits.ATTRIBUTE, false));
		edgeAccessors.add (edgeCaption = new AccessorBridge (Attributes.CAPTION, false));
		edgeAccessors.add (edgeWeight = new AccessorBridge (Attributes.WEIGHT, false));
	}

	
	@Override
	public Attribute[] getDependent (Object object, boolean asNode, Attribute a)
	{
		return a.toArray ();
	}

	
	@Override
	protected Attribute[] getDependentOfSource (Object object, boolean asNode, Attribute a)
	{
		return (a == Attributes.NAME) ? Attributes.CAPTION.toArray ()
			: Attribute.ATTRIBUTE_0;
	}


	@Override
	protected boolean isWritable (Object object, AccessorBridge accessor, GraphState gs)
	{
		return (accessor == nodeTransform) || (accessor == edgeBits);
	}
	
	@Override
	public String getType() {
		return GraphManager.MAIN_GRAPH;
	}	
	
	private static final Connection CONNECTION = new Connection ();

	private static final Rectangle2D RECTANGLE = new Rectangle2D.Float (-0.5f, -0.12f, 1f, 0.24f);
	private static final RectangularShape ROUND_RECTANGLE = new RoundRectangle2D.Float (0, 0, 0, 0, 0.2f, 0.2f);

	private static final RectangularShape ELLIPSE = new Ellipse2D.Float ();
	private static final RectangularShape RHOMBUS = new Rhombus ();

	private static final Arrow EDGE_ARROW = new Arrow ();
	private static final Color3f STANDARD_EDGE_COLOR = new Color3f (0, 1, 0.7f);
	private static final Color3f WHITE = new Color3f (1, 1, 1);
	private static final Color3f SPECIAL_EDGE_COLOR = new Color3f (1, 0, 1);
	private static final StrokeAdapter SOLID_STROKE = new StrokeAdapter ();
	private static final StrokeAdapter DASHED_STROKE = new StrokeAdapter ();
	private static final StrokeAdapter DASHED_DOTTED_STROKE = new StrokeAdapter ();
	
	static
	{
		EDGE_ARROW.setType (Arrow.SIMPLE);
		DASHED_STROKE.setLineStyle (StrokeAdapter.DASHED);
		DASHED_DOTTED_STROKE.setLineStyle (StrokeAdapter.DASHED_DOTTED);
		ROUND_RECTANGLE.setFrame (RECTANGLE);
		ELLIPSE.setFrame (RECTANGLE);
		RHOMBUS.setFrame (RECTANGLE);
	}


	@Override
	protected GraphState createState (ThreadContext tc)
	{
		return new State (GraphState.get (source, tc), tc)
		{
			@Override
			public boolean containsInTree (Object object, boolean asNode)
			{
				return true;
			}
		};
	}

	
	private Int2ObjectMap colors = new Int2ObjectMap ();

	private synchronized Color3f getColor (int rgb)
	{
		Color3f c = (Color3f) colors.get (rgb &= 0x00ffffff);
		if (c == null)
		{
			colors.put (rgb, de.grogra.imp.IMP.setColor (c = new Color3f (), rgb));
		}
		return c;
	}


	@Override
	protected Object getObject (Object object, AccessorBridge accessor,
								Object placeIn, GraphState gs)
	{
		if ((accessor == nodeTransformation) || (accessor == nodeTransform))
		{
			return getData (object, true, gs);
		}
		else if ((accessor == nodeShape) || (accessor == nodeEditable))
		{
			shapeInstance.setShape (getShape((Node) object));
			return shapeInstance;
		}
		else if (accessor == nodeShape2D)
		{
			switch (source.getSymbol (object, true))
			{
				case RECTANGLE_SYMBOL:
					if (collapsedNodes.contains(object)) {
						return ROUND_RECTANGLE;
					}
					return getShape((Node)object);
				case ROUND_RECTANGLE_SYMBOL:
					return ROUND_RECTANGLE;
				case ELLIPSE_SYMBOL:
					return ELLIPSE;
				default:
					return RHOMBUS;
			}
		}
		else if (accessor == nodeFillColor)
		{
			return getColor (source.getColor (object, true));
		}
		else if (accessor == nodeCaption)
		{
			return source.getDescription (object, true, Described.NAME);
		}
		else if (accessor == edgeTransformation)
		{
			ObjectData s = getData (getSourceNode (object), true, gs),
				t = getData (getTargetNode (object), true, gs),
				e = getData (object, false, gs);
			e.x = 0.5f * (s.x + t.x);
			e.y = 0.5f * (s.y + t.y);
			return e;
		}
		else if (accessor == edgeShape)
		{
			return NURBSShape2D.$TYPE.getRepresentative ();
		}
		else if (accessor == edgeCurve)
		{
			return CONNECTION;
		}
		else if (accessor == edgeArrow)
		{
			return EDGE_ARROW;
		}
		else if (accessor == edgeCaption)
		{
			int e = source.getEdgeBits (object);
			if ((e == SUCCESSOR_EDGE) || (e == BRANCH_EDGE))
			{
				return null;
			}
			Object o = source.getDescription (object, false, Described.NAME);
			return o;
//			return source.getDescription (object, false, Described.NAME);
		}
		else if (accessor == edgeStroke)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case SUCCESSOR_EDGE:
					return SOLID_STROKE;
				case BRANCH_EDGE:
					return DASHED_DOTTED_STROKE;
				default:
					return ((e & SUCCESSOR_EDGE) != 0)
						? SOLID_STROKE : DASHED_STROKE;
			}
		}
		else if (accessor == edgeColor)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case SUCCESSOR_EDGE:
				case BRANCH_EDGE:
					return STANDARD_EDGE_COLOR;
				default:
					return SPECIAL_EDGE_COLOR;
			}
		}
		else if (accessor == edgeFillColor)
		{
			return WHITE;
		}
		else if (accessor == edgeBits)
		{
			DefaultListModel list = new DefaultListModel ();
			list.addElement (new EnumValueImpl (IMP2D.I18N, "topology.special-edge-none", list));

			Object s = source.getSourceNode (object);
			Object t = source.getTargetNode (object);
			SpecialEdgeDescriptor[] sd = source.getSpecialEdgeDescriptors (s, true);
			for (int i = 0; i < sd.length; i++)
			{
				if (sd[i].getNodeClass ().isInstance (t))
				{
					list.addElement (new EnumValueImpl (sd[i], list));
				}
			}

			sd = source.getSpecialEdgeDescriptors (t, false);
			for (int i = 0; i < sd.length; i++)
			{
				if (sd[i].getNodeClass ().isInstance (s))
				{
					list.addElement (new EnumValueImpl (sd[i], list));
				}
			}

			EdgeBits bits = new EdgeBits (list);
			bits.setBits (source.getEdgeBits (object));
			return bits;
		}
		else
		{
			return super.getObject (object, accessor, placeIn, gs);
		}
	}

	private Rectangle2D getShape (Node n) {
		float w = 0.9f;
		String s = n.getClass ().getSimpleName ();
		// adapt width if the  name is longer then usual
		if(n.getName()!=null) {
			s = n.getName();
		}
		int a = s.length ()+4;
		if(a>10) w += (a-10)*0.08f;
		return new Rectangle2D.Float (-0.625f,-0.125f,w, 0.25f);
	}

	@Override
	protected int getInt (Object object, AccessorBridge accessor, GraphState gs)
	{
		if (accessor == nodeHAlignment)
		{
			return Attributes.H_ALIGN_CENTER;
		}
		else
		{
			return super.getInt (object, accessor, gs);
		}
	}


	@Override
	protected float getFloat (Object object, AccessorBridge accessor, GraphState gs)
	{
		if (accessor == edgeWeight)
		{
			return 1;
		}
		else
		{
			return super.getFloat (object, accessor, gs);
		}
	}


	@Override
	protected boolean getBoolean (Object object, AccessorBridge accessor, GraphState gs)
	{
		if (accessor == edgeFilled)
		{
			return false;
		}
		else
		{
			return super.getBoolean (object, accessor, gs);
		}
	}


	protected ObjectData getData (Object object, boolean asNode, GraphState gs)
	{
		ObjectData v = (ObjectData) data.getObject (object, asNode);
		if (v == null)
		{
			synchronized (data)
			{
				v = (ObjectData) data.getObject (object, asNode);
				if (v == null)
				{
					v = new ObjectData ();
					data.putObject (object, asNode, v);
				}
			}
		}
		return v;
	}

	
	@Override
	protected Object setObject (Object object, AccessorBridge accessor,
								Object value, GraphState gs)
	{
		if (accessor == nodeTransform)
		{
			getData (object, true, gs).set ((Tuple2d) value);
			EventSupport.Queue q = ((State) gs).getQueue ();
			q.postAttributeChanged (object, true, Attributes.TRANSFORM, null, null);
			q.postAttributeChanged (object, true, Attributes.TRANSFORMATION, null, null);
			for (Object e = getFirstEdge (object); e != null;
				 e = getNextEdge (e, object))
			{
				q.postAttributeChanged (e, false, Attributes.TRANSFORMATION, null, null);
			}
			return value;
		}
		else if (accessor == edgeBits)
		{
			gs.setEdgeBits (object, ((EdgeBits) value).bits);
			EventSupport.Queue q = ((State) gs).getQueue ();
			q.postAttributeChanged (object, false, EdgeBits.ATTRIBUTE, null, null);
			q.postAttributeChanged (object, false, Attributes.COLOR, null, null);
			q.postAttributeChanged (object, false, Attributes.STROKE, null, null);
			q.postAttributeChanged (object, false, Attributes.CAPTION, null, null);
			return value;
		}
		else
		{
			return super.setObject (object, accessor, value, gs);
		}
	}
	

	@Override
	public int getEdgeBits (Object edge)
	{
		return BRANCH_EDGE;
	}


	private static final class Parent extends ObjectAttribute
		implements EdgeChangeListener, ChangeBoundaryListener
	{
		Parent ()
		{
			super (de.grogra.reflect.Type.OBJECT, false, null);
		}


		@Override
		public boolean isDerived ()
		{
			return true;
		}


		@Override
		protected Object getDerived (Object object, boolean asNode, Object placeIn,
									 GraphState gs)
		{
			synchronized (this)
			{
				if (getAttributeState (gs) == null)
				{
					setAttributeState (gs, new ObjectList ());
					gs.getGraph ().addChangeBoundaryListener (this);
					gs.getGraph ().addEdgeChangeListener (this);
				}
			}
			return asNode ? null : gs.getGraph ().getSourceNode (object);
		}


		public void beginChange (GraphState gs)
		{
			((ObjectList) getAttributeState (gs)).clear ();
		}


		public void endChange (GraphState gs)
		{
			ObjectList changed = (ObjectList) getAttributeState (gs);
			while (!changed.isEmpty ())
			{
				gs.fireAttributeChanged (changed.pop (), false, this, null, null);
			}
		}


		public int getPriority ()
		{
			return TOPOLOGY_PRIORITY;
		}


		public void edgeChanged (Object source, Object target, Object edgeSet,
								 GraphState gs)
		{
			if (edgeSet != null)
			{
				((ObjectList) getAttributeState (gs)).push (edgeSet);
			}
		}

	}


	private static final ObjectAttribute PARENT = new Parent ();

	@Override
	public ObjectAttribute getParentAttribute ()
	{
		return PARENT;
	}


	private static final EdgePatternImpl PATTERN = new EdgePatternImpl
		(BRANCH_EDGE, 0, false, true);

	@Override
	public EdgePattern getTreePattern ()
	{
		return PATTERN;
	}
	
	
	@Override
	public Object getRoot (String key)
	{
		return source.getRoot (root);
	}

	@Override
	public int getLifeCycleState(Object object, boolean asNode) {
	
		if (asNode) {
			if (collapsedNodes.contains(((Node)object).findAdjacent(true, false, Graph.BRANCH_EDGE|Graph.SUCCESSOR_EDGE)))
				return Graph.TRANSIENT;
		}
		return super.getLifeCycleState (object, asNode);
	}

	public void eventOccured(EventObject event) {
		// set children of clicked node to collapse list
		if (event instanceof ClickEvent2D) {
			ClickEvent2D ce = (ClickEvent2D) event;
			int cc = ce.getClickCount();

			if ((cc == 2) && (ce.getEventType()== ClickEvent2D.MOUSE_CLICKED)) {
				// retrieve array path (path to node)
				Object o = event.getSource();
				
				// retrieve clicked object
				ArrayPath ap = (ArrayPath) o;
				if (!ap.endsInNode())
					return;
				Object co = ap.getNode(ap.getNodeAndEdgeCount()-1);
				if (collapsedNodes.contains(co))
					collapsedNodes.remove(co);
				else
					collapsedNodes.add(co);
				
			// TODO: redraw layout in a better way
			ce.getView().setTransformation(ce.getView().getTransformation());
			} // if
		} // if
	} // eventOccured
	
	/**
	 * Method called through GUI. Set all nodes except root node to collapse list.
	 * @param item
	 * @param info
	 * @param ctx
	 */
	public static void collapseAll (Item item, Object info, Context ctx)	{
		Node rootNode = de.grogra.pf.ui.UI.getRootOfProjectGraph(ctx);
		
		Visitor v = new Visitor() {
			public GraphState getGraphState() {
				return null;
			}
			public Object visitEnter(Path path, boolean node) {
				if (node)
				{
					collapsedNodes.add(path.getObject (-1));
				}
				return null;
			}
			public Object visitInstanceEnter() {
				return STOP;
			}
			public boolean visitInstanceLeave(Object o) {
				return true;
			}
			public boolean visitLeave(Object o, Path path, boolean node) {
				return true;
			}
		};
		rootNode.getGraph().accept(rootNode, v, null, GraphManager.MAIN_GRAPH);
		
		View2DIF.layout(item, info, ctx);
	}
	
	/**
	 * Method called through GUI. Remove all nodes from collapse list.
	 * @param item
	 * @param info
	 * @param ctx
	 */
	public static void expandAll (Item item, Object info, Context ctx)	{
		collapsedNodes.clear();
		View2DIF.layout(item, info, ctx);
	}

}
