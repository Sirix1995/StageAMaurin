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

package de.grogra.imp2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.Path;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.View;
import de.grogra.imp.awt.CanvasAdapter;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.math.Pool;
import de.grogra.util.EventListener;
import de.grogra.vecmath.Math2;

public class AWTCanvas2D extends CanvasAdapter implements AWTCanvas2DIF
{
	
	//! name of option, same as the one in plugin.xml file
	private static final String OPTION_BENDED_EDGES = "bendedEdges";
	private boolean optionBendedEdges = false;
	
	private static final String OPTION_NAME_SHOW_SLOT_EDGES = "showSlotEdges";
	private boolean optionShowSlotEdges = false;
	private static final String OPTION_NAME_SHOW_SLOT_LABELS = "showSlotLabels";
	private boolean optionShowSlotLabels = false;

	public static final Stroke STROKE_1 = new BasicStroke ();
	public static final Stroke STROKE_3 = new BasicStroke (3);
	
	public final Pool pool = new Pool ();
	
	private final DrawVisitor visitor = new DrawVisitor ();
	
	private final float[] MixedEdgeStep = pool.getFloatArray (0, 3);
	
	@Override
	public void setMENinPool(float i)
	{	
		MixedEdgeStep[0] = i;
	}

	@Override
	public boolean isBendedCurve() {
		return optionBendedEdges;
	}
	
	@Override
	public boolean isShowSlotEdges() {
		return optionShowSlotEdges;
	}
	
	@Override
	public boolean isShowSlotLabels() {
		return optionShowSlotLabels;
	}

	private class DrawVisitor extends Visitor2D
	{
		private int selectionState;
		private int minPathLength;

		void init (GraphState gs, int selectionState, int minPathLength) {
			init (gs, gs.getGraph ().getTreePattern (), ((View2DIF) getView ()).getCanvasTransformation ());
			this.selectionState = selectionState;
			this.minPathLength = minPathLength;
		}

		@Override
		protected void visitImpl (Object object, boolean asNode, Path path) {
			if (path.getNodeAndEdgeCount () - (asNode ? 0 : 1) >= minPathLength) {
				Object d = state.getObjectDefault(object, asNode, de.grogra.imp2d.objects.Attributes.SHAPE, null);
				if (d instanceof AWTDrawable) {
					if(asNode) {
						((AWTDrawable) d).draw (object, asNode, AWTCanvas2D.this, transformation, selectionState);
					} else {
						//only for the ComponentView2D
						if(getView().getClass ().getSimpleName ().equals ("ComponentView2D")) {
							// check if this age is an invisible edge (COMPONENT_INPUT_SLOT_EDGE, COMPONENT_OUTPUT_SLOT_EDGE)
							int edge = ((de.grogra.graph.impl.Edge) object).getEdgeBits ();
							if (edge == Graph.COMPONENT_INPUT_SLOT_EDGE || edge == Graph.COMPONENT_OUTPUT_SLOT_EDGE) {
								return;
							}
							// check if there is a dummy edge involved
							if(((de.grogra.graph.impl.Edge) object).testEdgeBits (Graph.DUMMY_EDGE)) {
								//check if the dummy edge is the only edge
								if(((de.grogra.graph.impl.Edge) object).getEdgeBits ()==Graph.DUMMY_EDGE) return;
								//draw without dummy edge
								((de.grogra.graph.impl.Edge) object).removeEdgeBits (Graph.DUMMY_EDGE, null);
								((AWTDrawable) d).draw (object, asNode, AWTCanvas2D.this, transformation, selectionState);
								((de.grogra.graph.impl.Edge) object).addEdgeBits (Graph.DUMMY_EDGE, null);
								return;
							}
						}
						
						if(getView() instanceof View2D || getView().isInVisibleEdge(object, asNode, getGraphState ())) {
							((AWTDrawable) d).draw (object, asNode, AWTCanvas2D.this, transformation, selectionState);
						}
					}
				}
			}
		}
	}


	public AWTCanvas2D () {
		initCanvas (new CanvasComponent (640, 480));
	}


	@Override
	public View2DIF getView2D ()
	{
		return (View2DIF) getView ();
	}

	
	@Override
	public void setColor (Color3f value) {}

	private int canvasWidth, canvasHeight;

	@Override
	public GraphState getGraphState () {
		return getRenderGraphState();
	}

	@Override
	protected void initPaint (int flags, int width, int height)
	{
		this.canvasWidth = width;
		this.canvasHeight = height;
	}


	@Override
	protected void paintScene (int flags, Graphics2D g)
	{
		g.setColor (Color.BLACK);
		g.setTransform (IDENTITY);
		g.fillRect (0, 0, canvasWidth, canvasHeight);
		visitor.init (getRenderGraphState (), 0, 0);
		for (currentLayer = getMaxLayer (); currentLayer >= getMinLayer ();
			 currentLayer--)
		{
			getView ().getGraph ().accept (null, visitor, null);
		}
	}


	@Override
	protected void paintHighlight (int flags, Graphics2D g)
	{
		if (ViewSelection.get (getView ()) == null)
		{
			System.err.println ("NULL");
			return;
		}
		ArrayPath path = new ArrayPath (getView ().getGraph ());
		ViewSelection.Entry[] s = ViewSelection.get (getView ()).getAll (-1);
		for (currentLayer = getMaxLayer (); currentLayer >= getMinLayer ();
			 currentLayer--)
		{
			for (int i = 0; i < s.length; i++)
			{
				Path p = s[i].getPath ();
				visitor.init (getRenderGraphState (), s[i].getValue (),
							  p.getNodeAndEdgeCount ());
				GraphUtils.acceptPath (p, visitor, path);
			}
		}

		de.grogra.imp.edit.Tool tool = getView ().getActiveTool ();
		if (tool != null)
		{
			visitor.init (GraphManager.STATIC_STATE, 0, 0);
			path.clear (GraphManager.STATIC);
			for (int i = 0; i < tool.getToolCount (); i++)
			{
				de.grogra.graph.impl.GraphManager
					.acceptGraph (tool.getRoot (i), visitor, path);
			}
		}
	}


	protected void finalizePaint ()
	{
	}


	@Override
	public int getWidth ()
	{
		return canvasWidth;
	}


	@Override
	public int getHeight ()
	{
		return canvasHeight;
	}


	private int currentLayer;
	private final int minLayer = 0;
	private final int maxLayer = 2;

	@Override
	public int getCurrentLayer ()
	{
		return currentLayer;
	}


	@Override
	public int getMinLayer ()
	{
		return minLayer;
	}


	@Override
	public int getMaxLayer ()
	{
		return maxLayer;
	}


	@Override
	public boolean isCurrentLayer (int layer)
	{
		return (layer == currentLayer)
			|| ((currentLayer == minLayer) && (layer < minLayer))
			|| ((currentLayer == maxLayer) && (layer > maxLayer));
	}


	private final AffineTransform transform = new AffineTransform ();

	@Override
	public void setGraphicsTransform (Matrix3d t)
	{
		Math2.setAffineTransform (transform, t);
		getGraphics ().setTransform (transform);
	}


	private final GeneralPath drawPath = new GeneralPath ();

	@Override
	public void draw (Shape s, Matrix3d t, Stroke stroke)
	{
		Math2.setAffineTransform (transform, t);
		PathIterator i = s.getPathIterator (transform);

		drawPath.reset ();
		drawPath.setWindingRule (i.getWindingRule ());
		drawPath.append (i, false);

		resetGraphicsTransform ();
		Graphics2D g = getGraphics ();
		g.setStroke (stroke);
		g.draw (drawPath);
	}
	
	
	@Override
	public void setColor (Color color)
	{
		getGraphics ().setColor (color);
	}
	
	@Override
	public Pool getPool() {
		return pool;
	}
	
	@Override
	public void initView (View view, EventListener listener)
	{
		super.initView (view, listener);
		optionBendedEdges = Boolean.TRUE.equals (getOption (OPTION_BENDED_EDGES, Boolean.TRUE));
		optionShowSlotEdges = Boolean.TRUE.equals (getOption (OPTION_NAME_SHOW_SLOT_EDGES, Boolean.TRUE));
		optionShowSlotLabels = Boolean.TRUE.equals (getOption (OPTION_NAME_SHOW_SLOT_LABELS, Boolean.TRUE));

	}

	@Override
	protected void optionValueChanged (String name, Object value)
	{
		// check if "bended edges" changed
		if (OPTION_BENDED_EDGES.equals (name))
		{
			optionBendedEdges = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		if (OPTION_NAME_SHOW_SLOT_EDGES.equals (name))
		{
			optionShowSlotEdges = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		if (OPTION_NAME_SHOW_SLOT_LABELS.equals (name))
		{
			optionShowSlotLabels = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else
		{
			super.optionValueChanged (name, value);
		}
	}
}
