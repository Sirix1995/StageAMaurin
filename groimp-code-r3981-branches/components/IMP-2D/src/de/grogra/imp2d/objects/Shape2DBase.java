
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

package de.grogra.imp2d.objects;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.EdgeImpl;
import de.grogra.imp.PickList;
import de.grogra.imp2d.AWTCanvas2D;
import de.grogra.imp2d.AWTCanvas2DIF;
import de.grogra.imp2d.AWTDrawable;
import de.grogra.imp2d.BoundedShape;
import de.grogra.imp2d.Connectable;
import de.grogra.imp2d.DragEvent2D;
import de.grogra.imp2d.Pickable;
import de.grogra.imp2d.View2DIF;
import de.grogra.imp2d.edit.EditTool;
import de.grogra.imp2d.edit.Editable;
import de.grogra.math.Pool;
import de.grogra.math.Transform2D;
import de.grogra.math.Tuple3fType;
import de.grogra.pf.ui.event.EditEvent;
import de.grogra.vecmath.Math2;

public abstract class Shape2DBase extends Null
	implements AWTDrawable, Pickable, BoundedShape, Connectable
{
	private static final Color3f DEFAULT_COLOR = new Color3f (0, 0, 0);


	Color3f color = new Color3f (DEFAULT_COLOR);
	//enh:field type=Tuple3fType.COLOR attr=Attributes.COLOR getter setter

	StrokeAdapter stroke = new StrokeAdapter ();
	//enh:field type=StrokeAdapter.$TYPE attr=Attributes.STROKE getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.addAccessor (new AccessorBridge (Editable.ATTRIBUTE));
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field stroke$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Shape2DBase.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Shape2DBase) o).color = (Color3f) Tuple3fType.COLOR.setObject (((Shape2DBase) o).color, value);
					return;
				case 1:
					((Shape2DBase) o).stroke = (StrokeAdapter) StrokeAdapter.$TYPE.setObject (((Shape2DBase) o).stroke, value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Shape2DBase) o).getColor ();
				case 1:
					return ((Shape2DBase) o).getStroke ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (Shape2DBase.class);
		$TYPE.addManagedField (color$FIELD = new _Field ("color", 0 | _Field.SCO, Tuple3fType.COLOR, null, 0));
		$TYPE.addManagedField (stroke$FIELD = new _Field ("stroke", 0 | _Field.SCO, StrokeAdapter.$TYPE, null, 1));
		$TYPE.declareFieldAttribute (color$FIELD, Attributes.COLOR);
		$TYPE.declareFieldAttribute (stroke$FIELD, Attributes.STROKE);
		initType ();
		$TYPE.validate ();
	}

	public Color3f getColor ()
	{
		return color;
	}

	public void setColor (Color3f value)
	{
		color$FIELD.setObject (this, value);
	}

	public StrokeAdapter getStroke ()
	{
		return stroke;
	}

	public void setStroke (StrokeAdapter value)
	{
		stroke$FIELD.setObject (this, value);
	}

//enh:end

	public Shape2DBase ()
	{
		super ();
	}


	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Editable.ATTRIBUTE)
		{
			return getEditable ();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}
	

	protected abstract Editable getEditable ();


	protected abstract Shape getShape (Object object, boolean asNode, Pool pool, GraphState gs);


	@Override
	public void getBounds (Object object, boolean asNode, Rectangle2D out, Pool pool, GraphState gs)
	{
		out.setFrame (getShape (object, asNode, pool, gs).getBounds2D ());
	}


	protected void drawShape (Object object, boolean asNode, AWTCanvas2DIF canvas, Shape s)
	{
		canvas.getGraphics ().draw (s);
	}


	private static final StrokeAdapter DEFAULT_STROKE = new StrokeAdapter ();

	private transient Color awtColor = Color.BLACK;

	@Override
	public void draw (Object object, boolean asNode, AWTCanvas2DIF canvas,
					  Matrix3d transformation, int state)
	{
		StrokeAdapter a;
		Tuple3f c;
		GraphState gs = canvas.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				c = color;
				a = stroke;
			}
			else
			{
				c = gs.checkObject (this, true, Attributes.COLOR, color);
				a = gs.checkObject (this, true, Attributes.STROKE, stroke);
			}
		}
		else
		{
			if (!canvas.isCurrentLayer (gs.getIntDefault (object, asNode, Attributes.LAYER, 0)))
			{
				return;
			}
			c = gs.getObjectDefault (object, asNode, Attributes.COLOR, DEFAULT_COLOR);
			a = gs.getObjectDefault (object, asNode, Attributes.STROKE, DEFAULT_STROKE);
		}
		
		
		if(canvas.isBendedCurve () && !asNode) 
		{
			if(object instanceof EdgeImpl) 
			{
//System.err.println ("yes "+ ((EdgeImpl)object).getEdgebits()+"\n"+sh);
				// for number of edges do: ..draw each edge separately 
				int counter = 0;
				EdgeImpl originEdge = (EdgeImpl) object;
				for (int i = Graph.MIN_NORMAL_BIT_INDEX; i < Graph.MAX_NORMAL_BIT_INDEX; i++)
				{
					int mask = 1 << i;
					if (originEdge.testEdgeBits(mask))
					{
						// fetch slot edges if they should not be drawn
						if (!canvas.isShowSlotEdges () && originEdge.testEdgeBits(mask) && mask == Graph.SLOT_EDGE) {
							return;
						}
						
						Edge tEdge = new EdgeImpl(originEdge.getSource (), originEdge.getTarget ());
						tEdge.setEdgeBits (mask, null);
						
						if (mask == Graph.SUCCESSOR_EDGE || mask == Graph.BRANCH_EDGE || mask == Graph.DUMMY_EDGE)
						{
							canvas.setMENinPool(0);
						}
						else
						{
							counter++;
							canvas.setMENinPool(counter);
						}
						if (object == this) 
						{
							if (gs.getInstancingPathIndex () <= 0)
							{
								c = color;
								a = stroke;
							}
							else
							{
								c = gs.checkObject (this, true, Attributes.COLOR, color);
								a = gs.checkObject (this, true, Attributes.STROKE, stroke);
							}
						}
						else
						{
							if (!canvas.isCurrentLayer (gs.getIntDefault (object, asNode, Attributes.LAYER, 0)))
							{
								return;
							}
							c = gs.getObjectDefault (tEdge, asNode, Attributes.COLOR, DEFAULT_COLOR);
							a = gs.getObjectDefault (tEdge, asNode, Attributes.STROKE, DEFAULT_STROKE);
						}
						Shape sh = getShape (tEdge, asNode, canvas.getPool(), gs);
						canvas.setColor (awtColor = AWTCanvas2D.getColor (c, awtColor), state, true);
						if (state == 0)
						{
							canvas.setGraphicsTransform (transformation);
							canvas.getGraphics ().setStroke (a.getStroke ());
							drawShape (tEdge, asNode, canvas, sh);
							
						}
						else
						{
							canvas.draw (sh, transformation, AWTCanvas2D.STROKE_3);
						}
						canvas.setMENinPool(0);
					}
				}
				return;
			}
		}
		
		// fetch slot edges if they should not be drawn
		if(!canvas.isShowSlotEdges () && !asNode && object instanceof EdgeImpl) {
			EdgeImpl originEdge = (EdgeImpl) object;
			for (int i = Graph.MIN_NORMAL_BIT_INDEX; i < Graph.MAX_NORMAL_BIT_INDEX; i++) {
				int mask = 1 << i;
				if (originEdge.testEdgeBits(mask) && mask == Graph.SLOT_EDGE) {
					return;
				}
			}
		}
		// fetch component input and output slot edges (they are never shown)
		if(!asNode && object instanceof EdgeImpl) {
			EdgeImpl originEdge = (EdgeImpl) object;
			if( originEdge.getEdgeBits () == Graph.COMPONENT_INPUT_SLOT_EDGE || 
				originEdge.getEdgeBits () == Graph.COMPONENT_INPUT_SLOT_EDGE) return;
		}
		
		Shape sh = getShape (object, asNode, canvas.getPool(), gs);
		canvas.setColor (awtColor = AWTCanvas2D.getColor (c, awtColor), state, true);

		int componentStatus = gs.getObjectDefault (object, asNode, Attributes.RUNTIMESTATUS, 0);

		if (state == 0) {
			// draw object
			canvas.setGraphicsTransform (transformation);
			canvas.getGraphics ().setStroke (a.getStroke ());
			if (componentStatus==1) {
				canvas.setColor (AWTCanvas2D.getColor (new Color3f(1,0.4f,0), awtColor), state, false);
			}
			if (componentStatus==2) {
				canvas.setColor (AWTCanvas2D.getColor (new Color3f(1,0,0), awtColor), state, false);
			}
			drawShape (object, asNode, canvas, sh);
		} else {
			// draw selected object
			canvas.draw (sh, transformation, AWTCanvas2D.STROKE_3);
		}
	}

	@Override
	public void pick (Object object, boolean asNode, Point2d point,
					  Matrix3d transformation, de.grogra.imp.PickList list)
	{
		float[] tmp = list.getFloatArray (0, 3);
		if (object instanceof EdgeImpl)
		{
			int counter = 0;
		
			EdgeImpl originEdge = (EdgeImpl) object;
			for (int i = Graph.MIN_NORMAL_BIT_INDEX; i < 25; i++)
			{
				int mask = 1 << i;
				if (originEdge.testEdgeBits(mask))
				{				
					if (mask == Graph.SUCCESSOR_EDGE || mask == Graph.BRANCH_EDGE || mask == Graph.DUMMY_EDGE);
					else
					{
						counter++;
					}
				}
			}
			tmp[0] = counter;
		}
		GraphState gs = list.getGraphState ();
		if (gs==null) return;
		Shape s = getShape (object, asNode, list, gs);
		tmp[0] = 0;
		if ((pickBoundary () && testBoundary (s, transformation, 25, list))
			|| s.contains (point.x, point.y))
		{
			list.add ();
		}
	}


	protected boolean pickBoundary ()
	{
		return false;
	}


	public static boolean testBoundary (Shape s, Matrix3d m, int epsSquared,
										PickList list)
	{
		double[] a = new double[6];
		double sx = 0, sy = 0;
		Point2d p = list.q2d0, q = list.q2d1, r = list.q2d2;
		Vector2d v = list.w2d0, w = list.w2d1;
		r.set (list.getViewX (), list.getViewY ());
		Matrix3d t = ((View2DIF) list.getView ()).getCanvasTransformation ();
		for (PathIterator pi = s.getPathIterator (null); !pi.isDone (); pi.next ())
		{
			int st = pi.currentSegment (a);
			switch (st)
			{
				case PathIterator.SEG_MOVETO:
					q.set (sx = a[0], sy = a[1]);
					break;
				case PathIterator.SEG_CLOSE:
					q.set (sx, sy);
					break;
				case PathIterator.SEG_LINETO:
					q.set (a[0], a[1]);
					break;
				case PathIterator.SEG_QUADTO:
					q.set (a[2], a[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					q.set (a[4], a[5]);
					break;
				default:
					throw new AssertionError (st);
			}
			Math2.transformPoint (m, q);
			Math2.transformPoint (t, q);
			if (st != PathIterator.SEG_MOVETO)
			{
				if ((r.distanceSquared (p) < epsSquared)
					|| (r.distanceSquared (q) < epsSquared))
				{
					return true;
				}
				w.sub (r, p);
				v.sub (q, p);
				double lambda = w.dot (v) / v.lengthSquared ();
				if ((lambda >= 0) && (lambda <= 1))
				{
					p.scaleAdd (lambda, v, p);
					if (r.distanceSquared (p) < epsSquared)
					{
						return true;
					}
				}
			}
			Point2d tmp = p; p = q; q = tmp;
		}
		return false;
	}


	@Override
	public void getConnectionPoint (Object object, boolean asNode, Tuple2d out,
									Object target, boolean tAsNode, Tuple2d targetPoint,
									Pool pool, GraphState gs)
	{
		getConnectionPoint (getShape (object, asNode, pool, gs).getPathIterator (null),
							false, true, targetPoint, out);
	}


	public static void getConnectionPoint (PathIterator path, boolean checkPoints,
										   boolean checkSegments,
										   Tuple2d target, Tuple2d out)
	{
		out.set (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		double[] a = new double[6];
		double sx = 0, sy = 0, cx = 0, cy = 0;
		while (!path.isDone ())
		{
			double px = cx, py = cy;
			int st = path.currentSegment (a);
			switch (st)
			{
				case PathIterator.SEG_MOVETO:
					cx = sx = a[0];
					cy = sy = a[1];
					break;
				case PathIterator.SEG_CLOSE:
					cx = sx;
					cy = sy;
					st = PathIterator.SEG_LINETO;
					break;
				case PathIterator.SEG_LINETO:
					cx = a[0];
					cy = a[1];
					break;
				case PathIterator.SEG_QUADTO:
					cx = a[2];
					cy = a[3];
					break;
				case PathIterator.SEG_CUBICTO:
					cx = a[4];
					cy = a[5];
					break;
			}
			if (st != PathIterator.SEG_MOVETO)
			{
				if (checkPoints)
				{
					setIfCloser (out, cx, cy, target);
				}
				if (checkSegments)
				{
					switch (st)
					{
						case PathIterator.SEG_LINETO:
							findClosest (px, py, cx, cy, target, out);
							break;
						case PathIterator.SEG_QUADTO:
							findClosest (px, py, a[0], a[1], cx, cy, target, out);
							break;
						case PathIterator.SEG_CUBICTO:
							findClosest (px, py, a[0], a[1], a[2], a[3], cx, cy, target, out);
							break;
					}
				}
			}
			path.next ();
		}
	}
	
	
	private static void findClosest (double x0, double y0, double x1, double y1,
									 Tuple2d target, Tuple2d out)
	{
		x1 -= x0;
		y1 -= y0;
		double alpha = (target.x * y0 - target.y * x0) / (x1 * target.y - y1 * target.x);
		if ((alpha >= 0) && (alpha <= 1))
		{
			setIfCloser (out, x0 + alpha * x1, y0 + alpha * y1, target);
		}
	}
	
	
	private static void findClosest (double x0, double y0, double x1, double y1,
									 double x2, double y2, Tuple2d target, Tuple2d out)
	{
		double maxx = (x0 > x1) ? (x0 > x2) ? x0 : x2 : (x1 > x2) ? x1 : x2;
		double maxy = (y0 > y1) ? (y0 > y2) ? y0 : y2 : (y1 > y2) ? y1 : y2;
		double minx = (x0 < x1) ? (x0 < x2) ? x0 : x2 : (x1 < x2) ? x1 : x2;
		double miny = (y0 < y1) ? (y0 < y2) ? y0 : y2 : (y1 < y2) ? y1 : y2;
		maxx = 0.02 * (maxx + maxy - minx - miny);
		if (Line2D.ptLineDistSq (x0, y0, x2, y2, x1, y1) <= maxx * maxx)
		{
			findClosest (x0, y0, x2, y2, target, out);
		}
		else
		{
			findClosest (x0, y0, x0 = 0.5 * (x0 + x1), y0 = 0.5 * (y0 + y1),
						 x1 = 0.5 * (x0 + (maxx = 0.5 * (x2 + x1))),
						 y1 = 0.5 * (y0 + (maxy = 0.5 * (y2 + y1))),
						 target, out);
			findClosest (x1, y1, maxx, maxy, x2, y2, target, out);
		}
	}

	
	private static void findClosest (double x0, double y0, double x1, double y1,
									 double x2, double y2, double x3, double y3,
									 Tuple2d target, Tuple2d out)
	{
		double maxx = (x0 > x1) ? (x0 > x2) ? x0 : x2 : (x1 > x2) ? x1 : x2;
		if (x3 > maxx)
		{
			maxx = x3;
		}
		double maxy = (y0 > y1) ? (y0 > y2) ? y0 : y2 : (y1 > y2) ? y1 : y2;
		if (y3 > maxy)
		{
			maxy = y3;
		}
		double minx = (x0 < x1) ? (x0 < x2) ? x0 : x2 : (x1 < x2) ? x1 : x2;
		if (x3 < minx)
		{
			minx = x3;
		}
		double miny = (y0 < y1) ? (y0 < y2) ? y0 : y2 : (y1 < y2) ? y1 : y2;
		if (y3 < miny)
		{
			miny = y3;
		}
		maxx = 0.02 * (maxx + maxy - minx - miny);
		if ((Line2D.ptLineDistSq (x0, y0, x3, y3, x1, y1) <= (maxx *= maxx))
			&& (Line2D.ptLineDistSq (x0, y0, x3, y3, x2, y2) <= maxx))
		{
			findClosest (x0, y0, x3, y3, target, out);
		}
		else
		{
			findClosest (x0, y0, x0 = 0.5 * (x0 + x1), y0 = 0.5 * (y0 + y1),
						 x1 = 0.5 * (x0 + (x0 = 0.5 * (x1 + x2))),
						 y1 = 0.5 * (y0 + (y0 = 0.5 * (y1 + y2))),
						 x2 = 0.5 * (x1 + (minx = 0.5 * (x0 + (maxx = 0.5 * (x2 + x3))))),
						 y2 = 0.5 * (y1 + (miny = 0.5 * (y0 + (maxy = 0.5 * (y2 + y3))))),
						 target, out);
			findClosest (x2, y2, minx, miny, maxx, maxy, x3, y3, target, out);
		}
	}


	public static void setIfCloser (Tuple2d out, double x, double y, Tuple2d ref)
	{
		double t;
		if ((t = out.x - ref.x) * t + (t = out.y - ref.y) * t
			> (t = x - ref.x) * t + (t = y - ref.y) * t)
		{
			out.x = x;
			out.y = y;
		}
	}


	public void pickTool (Point2d point,
						  Matrix3d transformation, de.grogra.imp.PickList list,
						  EditTool tool)
	{
		pick (tool.getToolTarget (), tool.isTargetNode (), point, transformation, list);
		if (list.containsCurrent ())
		{
			tool.setPickId (EditTool.PICK_CENTER);
		}
	}


	public void drawTool (AWTCanvas2DIF canvas, Matrix3d transformation, EditTool tool)
	{
	}


	public void toolEventOccured (EditEvent e, EditTool tool)
	{
		int id = tool.getPickId ();
		if (id == EditTool.PICK_CENTER)
		{
			if (e instanceof DragEvent2D)
			{
				DragEvent2D d = (DragEvent2D) e;
				if (d.draggingStarted ())
				{
					Point2d p = new Point2d (d.point);
					Math2.invTransformPoint
						(tool.getTargetTransformation (), p);
					tool.setObject (p);
				}
				else if (d.draggingContinued ())
				{
					Point2d p = tool.pool.p2d0, q = (Point2d) tool.getObject ();
					if (q == null)
					{
						return;
					}
					p.set (d.point);
					Math2.invTransformPoint
						(tool.getTargetTransformation (), p);
					p.sub (q);
					Matrix3d t = tool.pool.m3d1;
					Transform2D x = (Transform2D) tool.getTool ()
						.getObjectOfObject (Attributes.TRANSFORM, null);
					if (x != null)
					{
						tool.pool.m3d0.setIdentity ();
						x.transform (tool.pool.m3d0, t);
					}
					else
					{
						t.setIdentity ();
					}
					Math2.transformVector (t, p);
					t.m02 += p.x;
					t.m12 += p.y;
					tool.setTransform (t);
				}
			}
		}
	}


}