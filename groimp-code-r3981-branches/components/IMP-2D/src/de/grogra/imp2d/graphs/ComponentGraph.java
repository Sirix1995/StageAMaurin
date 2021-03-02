/*
 * Copyright (C) 2012 GroIMP Developer Team
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

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
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
import de.grogra.imp.View;
import de.grogra.imp2d.AWTCanvas2DHierarchicalGraph;
import de.grogra.imp2d.AWTCanvas2DIF;
import de.grogra.imp2d.ClickEvent2D;
import de.grogra.imp2d.ComponentView2D;
import de.grogra.imp2d.IMP2D;
import de.grogra.imp2d.View2DIF;
import de.grogra.imp2d.edit.Editable;
import de.grogra.imp2d.objects.Arrow;
import de.grogra.imp2d.objects.Attributes;
import de.grogra.imp2d.objects.Connection;
import de.grogra.imp2d.objects.NURBSShape2D;
import de.grogra.imp2d.objects.Rhombus;
import de.grogra.imp2d.objects.Shape2D;
import de.grogra.imp2d.objects.SplineConnection;
import de.grogra.imp2d.objects.StrokeAdapter;
import de.grogra.math.BSplineCurve;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.util.Described;
import de.grogra.util.EnumValueImpl;
import de.grogra.util.EventListener;
import de.grogra.util.Int2ObjectMap;
import de.grogra.util.ThreadContext;
import de.grogra.xl.util.ObjectList;

public class ComponentGraph extends AttributeOverwritingFilter implements EventListener {
	final ObjectMapImpl<ObjectData> data;
	private final String root;

	private AccessorBridge nodeShape, nodeTransformation, nodeShape2D, nodeEditable, nodeTransform, 
		nodeCaption, nodeHAlignment, nodeFillColor, nodeRuntimeStatus, 
		edgeShape, edgeTransformation, edgeCurve, edgeArrow, edgeFilled, edgeColor, edgeStroke, edgeBits, edgeCaption,
		edgeFillColor, edgeWeight;

	private final Shape2D shapeInstance;
	private final View2DIF view;
	private AWTCanvas2DIF viewComponent;

	private final static HashSet<Object> collapsedNodes = new HashSet<Object> ();

	public ComponentGraph (Graph source, View2DIF view, String root) {
		super (source);
		initAttributeOverwritingFilter ();
		this.root = root;
		this.view = view;
		shapeInstance = new Shape2D ();
		shapeInstance.setOutlined (false);
		this.data = new ObjectMapImpl<ObjectData>();
	}

	@Override
	protected void initNodeAccessors (AccessorMap nodeAccessors) {
		nodeAccessors.add (nodeShape = new AccessorBridge (Attributes.SHAPE, true));
		nodeAccessors.add (nodeTransformation = new AccessorBridge (Attributes.TRANSFORMATION, true));
		nodeAccessors.add (nodeShape2D = new AccessorBridge (Attributes.SHAPE_2D, true));
		nodeAccessors.add (nodeFillColor = new AccessorBridge (Attributes.FILL_COLOR, true));
		nodeAccessors.add (nodeEditable = new AccessorBridge (Editable.ATTRIBUTE, true));
		nodeAccessors.add (nodeTransform = new AccessorBridge (Attributes.TRANSFORM, true));
		nodeAccessors.add (nodeCaption = new AccessorBridge (Attributes.CAPTION, true));
		nodeAccessors.add (nodeHAlignment = new AccessorBridge (Attributes.HORIZONTAL_ALIGNMENT, true));
		nodeAccessors.add (nodeRuntimeStatus = new AccessorBridge (Attributes.RUNTIMESTATUS, true));
	}

	@Override
	protected void initEdgeAccessors (AccessorMap edgeAccessors) {
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
	public Attribute[] getDependent (Object object, boolean asNode, Attribute a) {
		return a.toArray ();
	}

	@Override
	protected Attribute[] getDependentOfSource (Object object, boolean asNode, Attribute a) {
		return (a == Attributes.NAME) ? Attributes.CAPTION.toArray () : Attribute.ATTRIBUTE_0;
	}

	@Override
	protected boolean isWritable (Object object, AccessorBridge accessor, GraphState gs) {
		return (accessor == nodeTransform) || (accessor == edgeBits);
	}

	@Override
	public String getType () {
		return GraphManager.COMPONENT_GRAPH;
	}

	private static final BSplineCurve LINE_CONNECTION = new Connection ();
	private static final BSplineCurve SPLINE_CONNECTION = new SplineConnection ();

	private static final Rectangle2D RECTANGLE = new Rectangle2D.Float (-0.625f,-0.125f, 1.3f, 0.25f);
	private static final RectangularShape ROUND_RECTANGLE = new RoundRectangle2D.Float (0, 0, 0, 0, 0.2f, 0.2f);

	private static final RectangularShape ELLIPSE = new Ellipse2D.Float ();
	private static final RectangularShape RHOMBUS = new Rhombus ();

	private static final GeneralPath INPUTSLOT_SHAPE = new GeneralPath();
	private static final GeneralPath OUTPUTSLOT_SHAPE = new GeneralPath(); 

	private static final Arrow EDGE_ARROW = new Arrow ();
	private static final Arrow SLOT_EDGE_ARROW = new Arrow ();
	private static final Arrow REFINEMENT_EDGE_ARROW = new Arrow ();
	private static final Color3f STANDARD_EDGE_COLOR = new Color3f (0, 1, 0.7f);
	private static final Color3f SPECIAL_EDGE_COLOR = new Color3f (1, 0, 1);
	private static final Color3f SEND_EDGE_COLOR = new Color3f (1, 0.25f, 0);
	private static final Color3f USES_EDGE_COLOR = new Color3f (0, 0.25f, 1);
	private static final Color3f SLOT_EDGE_COLOR = new Color3f (0.5f, 0.5f,	0.5f);
	private static final Color3f REFINEMENT_EDGE_COLOR = new Color3f (0, 1, 0);

	private static final Color3f WHITE = new Color3f (1, 1, 1);
	private static final Color3f INPUTSLOT_COLOR = new Color3f (0, 0.8f, 0);
	private static final Color3f OUTPUTSLOT_COLOR = new Color3f (0.8f, 0, 0);
	private static final StrokeAdapter SOLID_STROKE = new StrokeAdapter ();
	private static final StrokeAdapter DASHED_STROKE = new StrokeAdapter ();
	private static final StrokeAdapter DASHED_DOTTED_STROKE = new StrokeAdapter ();
	private static final StrokeAdapter DASHED_TRIPLE_DOTTED_STROKE = new StrokeAdapter ();

	static
	{
		EDGE_ARROW.setType (Arrow.SIMPLE);
		SLOT_EDGE_ARROW.setType (Arrow.FILLED_TECHNICAL);
		REFINEMENT_EDGE_ARROW.setType (Arrow.TECHNICAL);
		DASHED_STROKE.setLineStyle (StrokeAdapter.DASHED);
		DASHED_DOTTED_STROKE.setLineStyle (StrokeAdapter.DASHED_DOTTED);
		DASHED_TRIPLE_DOTTED_STROKE.setLineStyle (StrokeAdapter.DASHED_TRIPLE_DOTTED);
		ROUND_RECTANGLE.setFrame (RECTANGLE);
		ELLIPSE.setFrame (RECTANGLE);
		RHOMBUS.setFrame (RECTANGLE);

		INPUTSLOT_SHAPE.moveTo(-0.1,0.0);
		INPUTSLOT_SHAPE.lineTo(0,-0.2);
		INPUTSLOT_SHAPE.lineTo(0.1,0.0);
		INPUTSLOT_SHAPE.closePath();

		OUTPUTSLOT_SHAPE.moveTo(-0.1,0.2);
		OUTPUTSLOT_SHAPE.lineTo(0,0);
		OUTPUTSLOT_SHAPE.lineTo(0.1,0.2);
		OUTPUTSLOT_SHAPE.closePath();
		
//		INPUTSLOT_SHAPE.moveTo(0, 0.1);
//		INPUTSLOT_SHAPE.lineTo(0.2, 0);
//		INPUTSLOT_SHAPE.lineTo(0,-0.1);
//		INPUTSLOT_SHAPE.closePath();
//
//		OUTPUTSLOT_SHAPE.moveTo(0, 0.1);
//		OUTPUTSLOT_SHAPE.lineTo(0.2, 0);
//		OUTPUTSLOT_SHAPE.lineTo(0,-0.1);
//		OUTPUTSLOT_SHAPE.closePath();

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

	private final Int2ObjectMap colors = new Int2ObjectMap ();

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
	protected Object getObject (Object object, AccessorBridge accessor, Object placeIn, GraphState gs) {
		View v = ComponentView2D.get (gs);
		if (v != null) {
			viewComponent = (AWTCanvas2DIF)v.getViewComponent ();
			if (viewComponent == null) return null;
			if (viewComponent instanceof AWTCanvas2DHierarchicalGraph) {
				return getObjectBodyHierarchicalGraph (object, accessor, placeIn, gs);
			} else {
				return getObjectBodyGraph (object, accessor, placeIn, gs);
			}
		}
		return getObjectBodyGraph (object, accessor, placeIn, gs);
	}


	/**
	 * implementation for the "normal" 2-d graph view
	 * 
	 * @param object
	 * @param accessor
	 * @param placeIn
	 * @param gs
	 * @return
	 */
	private Object getObjectBodyGraph (Object object, AccessorBridge accessor,
			Object placeIn, GraphState gs)
	{
		if ((accessor == nodeTransformation) || (accessor == nodeTransform))
		{
			return getData (object, true, gs);
		}
		else if ((accessor == nodeShape) || (accessor == nodeEditable))
		{
			if(object instanceof InputSlot) {
				shapeInstance.setShape (INPUTSLOT_SHAPE);
			} else
			if(object instanceof OutputSlot) {
				shapeInstance.setShape (OUTPUTSLOT_SHAPE);
			} else 
			if(object instanceof GroIMPComponent) {
				shapeInstance.setShape (getShape((GroIMPComponent) object));
			} else shapeInstance.setShape (getShape((Node) object));
			return shapeInstance;
		}
		else if (accessor == nodeShape2D)
		{
			switch (source.getSymbol (object, true))
			{
				case RECTANGLE_SYMBOL:
					if (collapsedNodes.contains (object))
					{
						return ROUND_RECTANGLE;
					}
					if(object instanceof InputSlot) {
						return INPUTSLOT_SHAPE;
					}
					if(object instanceof OutputSlot) {
						return OUTPUTSLOT_SHAPE;
					}
					if(object instanceof GroIMPComponent) {
						return getShape((GroIMPComponent) object);
					}
					return getShape((Node) object);
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
			if(object instanceof InputSlot) {
				return INPUTSLOT_COLOR;
			}
			if(object instanceof OutputSlot) {
				return OUTPUTSLOT_COLOR;
			}
			return getColor (source.getColor (object, true));
		}
		else if (accessor == nodeCaption)
		{
			return source.getDescription (object, true, Described.NAME);
		}
		else if(accessor == nodeRuntimeStatus)
		{
			if(object instanceof GroIMPComponent) return ((GroIMPComponent)object).getRuntimeStatus ();
			return GroIMPComponent.OK_STATE;
		}
		else if (accessor == edgeTransformation)
		{
			ObjectData s = getData (getSourceNode (object), true, gs), t = getData (
				getTargetNode (object), true, gs), e = getData (object, false,
				gs);
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
			int e = source.getEdgeBits (object);
			if (e == SUCCESSOR_EDGE || e == BRANCH_EDGE)
			{
				return LINE_CONNECTION;
			}
			return SPLINE_CONNECTION;
		}
		else if (accessor == edgeArrow)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case SLOT_EDGE:
					return SLOT_EDGE_ARROW;
				case REFINEMENT_EDGE:
					return REFINEMENT_EDGE_ARROW;
				default:
					return EDGE_ARROW;
			}
		}
		else if (accessor == edgeCaption)
		{
			int e = source.getEdgeBits (object);
			if ((e == SUCCESSOR_EDGE) || (e == BRANCH_EDGE))
			{
				return null;
			}
			return source.getDescription (object, false, Described.NAME);
		}
		else if (accessor == edgeStroke)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case USES_EDGE:
				case SLOT_EDGE:
				case SUCCESSOR_EDGE:
					return SOLID_STROKE;
				case BRANCH_EDGE:
					return DASHED_DOTTED_STROKE;
				case SEND_EDGE:
					return DASHED_TRIPLE_DOTTED_STROKE;
				case REFINEMENT_EDGE:
					return DASHED_DOTTED_STROKE;
				case DUMMY_EDGE:
					return SOLID_STROKE;//DASHED_TRIPLE_DOTTED_STROKE
				default:
					return ((e & SUCCESSOR_EDGE) != 0) ? SOLID_STROKE
							: DASHED_STROKE;
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
				case USES_EDGE:
					return USES_EDGE_COLOR;
				case SEND_EDGE:
					return SEND_EDGE_COLOR;
				case SLOT_EDGE:
					return SLOT_EDGE_COLOR;
				case REFINEMENT_EDGE:
					return REFINEMENT_EDGE_COLOR;
				case DUMMY_EDGE:
					return WHITE;
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
			list.addElement (new EnumValueImpl (IMP2D.I18N,
				"component.special-edge-none", list));

			Object s = source.getSourceNode (object);
			Object t = source.getTargetNode (object);
			SpecialEdgeDescriptor[] sd = source.getSpecialEdgeDescriptors (s,
				true);
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

	/**
	 * implementation for the hierarchical graph view
	 * 
	 * @param object
	 * @param accessor
	 * @param placeIn
	 * @param gs
	 * @return
	 */
	private Object getObjectBodyHierarchicalGraph (Object object, AccessorBridge accessor,
			Object placeIn, GraphState gs)
	{
		if ((accessor == nodeTransformation) || (accessor == nodeTransform))
		{
			return getData (object, true, gs);
		}
		else if ((accessor == nodeShape) || (accessor == nodeEditable))
		{
			if(object instanceof InputSlot) {
				shapeInstance.setShape (INPUTSLOT_SHAPE);
			} else
			if(object instanceof OutputSlot) {
				shapeInstance.setShape (OUTPUTSLOT_SHAPE);
			} else 
			if(object instanceof GroIMPComponent) {
				shapeInstance.setShape (getShape((GroIMPComponent) object));
			} else shapeInstance.setShape (getShape((Node) object));
			return shapeInstance;
		}
		else if (accessor == nodeShape2D)
		{
			switch (source.getSymbol (object, true))
			{
				case RECTANGLE_SYMBOL:
					if (collapsedNodes.contains (object))
					{
						return ROUND_RECTANGLE;
					}
					if(object instanceof InputSlot) {
						return INPUTSLOT_SHAPE;
					}
					if(object instanceof OutputSlot) {
						return OUTPUTSLOT_SHAPE;
					}
					if(object instanceof GroIMPComponent) {
						return getShape((GroIMPComponent) object);
					}
					return getShape((Node) object);
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
			if(object instanceof InputSlot) {
				return INPUTSLOT_COLOR;
			}
			if(object instanceof OutputSlot) {
				return OUTPUTSLOT_COLOR;
			}
			return getColor (source.getColor (object, true));
		}
		else if (accessor == nodeCaption)
		{
			return source.getDescription (object, true, Described.NAME);
		}
		else if(accessor == nodeRuntimeStatus)
		{
			if(object instanceof GroIMPComponent) return ((GroIMPComponent)object).getRuntimeStatus ();
			return GroIMPComponent.OK_STATE;
		}
		else if (accessor == edgeTransformation)
		{
			ObjectData s = getData (getSourceNode (object), true, gs), t = getData (
				getTargetNode (object), true, gs), e = getData (object, false,	gs);
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
			int e = source.getEdgeBits (object);
			if (e == SUCCESSOR_EDGE || e == BRANCH_EDGE)
			{
				return LINE_CONNECTION;
			}
			return SPLINE_CONNECTION;
		}
		else if (accessor == edgeArrow)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case SLOT_EDGE:
					return SLOT_EDGE_ARROW;
				case REFINEMENT_EDGE:
					return REFINEMENT_EDGE_ARROW;
				default:
					return EDGE_ARROW;
			}
		}
		else if (accessor == edgeCaption)
		{
			int e = source.getEdgeBits (object);
			if ((e == SUCCESSOR_EDGE) || (e == BRANCH_EDGE))
			{
				return null;
			}
			return source.getDescription (object, false, Described.NAME);
		}
		else if (accessor == edgeStroke)
		{
			int e = source.getEdgeBits (object);
			switch (e)
			{
				case USES_EDGE:
				case SLOT_EDGE:
				case SUCCESSOR_EDGE:
					return SOLID_STROKE;
				case BRANCH_EDGE:
					return DASHED_DOTTED_STROKE;
				case SEND_EDGE:
					return DASHED_TRIPLE_DOTTED_STROKE;
				case REFINEMENT_EDGE:
					return DASHED_DOTTED_STROKE;
				case DUMMY_EDGE:
					return SOLID_STROKE;//DASHED_TRIPLE_DOTTED_STROKE
				default:
					return ((e & SUCCESSOR_EDGE) != 0) ? SOLID_STROKE
							: DASHED_STROKE;
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
				case USES_EDGE:
					return USES_EDGE_COLOR;
				case SEND_EDGE:
					return SEND_EDGE_COLOR;
				case SLOT_EDGE:
					return SLOT_EDGE_COLOR;
				case REFINEMENT_EDGE:
					return REFINEMENT_EDGE_COLOR;
				case DUMMY_EDGE:
					return WHITE;
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
			list.addElement (new EnumValueImpl (IMP2D.I18N, "component.special-edge-none", list));

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

	private Area getShape (GroIMPComponent comp) {
		float w = 0.8f;
		// adapt width if the component name is longer then usual
		String s = comp.getClass ().getName ();
		int a = s.length ();
		if(s.indexOf ("$")!=-1) {
			a -= s.indexOf ("$");
		} else {
			if(s.contains ("de.grogra")) {
				a -= s.lastIndexOf ('.');
			}
		}
		if(a>10) w += (a-10)*0.2;

		Area out = new Area(new Rectangle2D.Float (-0.625f,-0.125f,w, 0.25f));
		// visualise runtime status
		if(comp.getRuntimeStatus ()>GroIMPComponent.OK_STATE) {
			out.add (new Area(new Ellipse2D.Double (-w / 2d + w / 20d,0.2, 0.031d,0.1d)));
			out.add (new Area(new Ellipse2D.Double (-w / 2d + w / 20d,0.15,  0.031d,0.031d)));
			if(comp.getRuntimeStatus ()==GroIMPComponent.ERROR_STATE) {
				out.add (new Area(new Ellipse2D.Double (-w / 2d + w / 9d,0.2, 0.031d,0.1d)));
				out.add (new Area(new Ellipse2D.Double (-w / 2d + w / 9d,0.15,  0.031d,0.031d)));
			}
		}
		return out;
	}

	private Rectangle2D getShape (Node n) {
		float w = 0.8f;
		String s = n.getClass ().getSimpleName ();
		// adapt width if the  name is longer then usual
		if(n.getName()!=null) {
			s = n.getName();
		}
		int a = s.length ();
		if(a>10) w += (a-10)*0.2;
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
	protected float getFloat (Object object, AccessorBridge accessor,
			GraphState gs)
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
	protected boolean getBoolean (Object object, AccessorBridge accessor,
			GraphState gs)
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
		ObjectData v = data.getObject (object, asNode);
		if (v == null)
		{
			synchronized (data)
			{
				v = data.getObject (object, asNode);
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
			for (Object e = getFirstEdge (object); e != null; e = getNextEdge (e, object))
			{
				q.postAttributeChanged (e, false, Attributes.TRANSFORMATION,null, null);
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

	private static final class Parent extends ObjectAttribute implements
			EdgeChangeListener, ChangeBoundaryListener
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
		protected Object getDerived (Object object, boolean asNode,
				Object placeIn, GraphState gs)
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

		@Override
		public void beginChange (GraphState gs)
		{
			((ObjectList) getAttributeState (gs)).clear ();
		}

		@Override
		public void endChange (GraphState gs)
		{
			ObjectList changed = (ObjectList) getAttributeState (gs);
			while (!changed.isEmpty ())
			{
				gs.fireAttributeChanged (changed.pop (), false, this, null, null);
			}
		}

		@Override
		public int getPriority ()
		{
			return COMPONENT_PRIORITY;
		}

		@Override
		public void edgeChanged (Object source, Object target, Object edgeSet, GraphState gs)
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

	@Override
	public EdgePattern getTreePattern ()
	{
		return EdgePatternImpl.COMPONENT;
	}

	@Override
	public Object getRoot (String key)
	{
		if(source instanceof GraphManager) {
			return ((GraphManager)source).getRootComponentGraph ();
		}
		return source.getRoot (root);
	}

	@Override
	public int getLifeCycleState (Object object, boolean asNode)
	{

		if (asNode)
		{
			if (collapsedNodes.contains (((Node) object).findAdjacent (true,
				false, Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE)))
				return Graph.TRANSIENT;
		}
		return super.getLifeCycleState (object, asNode);
	}

	@Override
	public void eventOccured (EventObject event)
	{
		// set children of clicked node to collapse list
		if (event instanceof ClickEvent2D)
		{
			ClickEvent2D ce = (ClickEvent2D) event;
			int cc = ce.getClickCount ();

			if ((cc == 2) && (ce.getEventType () == ClickEvent2D.MOUSE_CLICKED))
			{
				// retrieve array path (path to node)
				Object o = event.getSource ();

				// retrieve clicked object
				ArrayPath ap = (ArrayPath) o;
				if (!ap.endsInNode ())
					return;
				Object co = ap.getNode (ap.getNodeAndEdgeCount () - 1);
				if (collapsedNodes.contains (co))
					collapsedNodes.remove (co);
				else
					collapsedNodes.add (co);

				// TODO: redraw layout in a better way
				ce.getView ().setTransformation (
					ce.getView ().getTransformation ());
			} // if
		} // if
	} // eventOccured

	/**
	 * Method called through GUI. Set all nodes except root node to collapse list.
	 * @param item
	 * @param info
	 * @param ctx
	 */
	public static void collapseAll (Item item, Object info, Context ctx)
	{
		Node rootNode = de.grogra.pf.ui.UI.getRootOfProjectGraph (ctx);

		Visitor v = new Visitor ()
		{
			@Override
			public GraphState getGraphState ()
			{
				return null;
			}

			@Override
			public Object visitEnter (Path path, boolean node)
			{
				if (node)
				{
					collapsedNodes.add (path.getObject (-1));
				}
				return null;
			}

			@Override
			public Object visitInstanceEnter ()
			{
				return STOP;
			}

			@Override
			public boolean visitInstanceLeave (Object o)
			{
				return true;
			}

			@Override
			public boolean visitLeave (Object o, Path path, boolean node)
			{
				return true;
			}
		};
		rootNode.getGraph ().accept (rootNode, v, null,
			GraphManager.COMPONENT_GRAPH);

		View2DIF.layout (item, info, ctx);
	}

	/**
	 * Method called through GUI. Remove all nodes from collapse list.
	 * @param item
	 * @param info
	 * @param ctx
	 */
	public static void expandAll (Item item, Object info, Context ctx)
	{
		collapsedNodes.clear ();
		View2DIF.layout (item, info, ctx);
	}

}
