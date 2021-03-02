
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

package de.grogra.imp2d.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import de.grogra.graph.AttributeAccessor;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.ObjectMap;
import de.grogra.graph.Path;
import de.grogra.graph.Visitor;
import de.grogra.imp2d.BoundedShape;
import de.grogra.imp2d.View2D;
import de.grogra.imp2d.View2DIF;
import de.grogra.imp2d.Visitor2D;
import de.grogra.imp2d.objects.Attributes;
import de.grogra.math.Pool;
import de.grogra.math.TMatrix3d;
import de.grogra.math.Transform2D;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.vecmath.Math2;
import de.grogra.xl.util.ObjectList;

/**
 * This class is the abstract base class for two-dimensional graph layouts.
 * A layout is performed for a <code>View2D</code> using the
 * {@link #invoke(View2D)}-method, the actual work is done in an instance
 * of <code>Algorithm</code> which is returned by {@link #createAlgorithm()}.
 * <p>
 * In order to simplify the task of layouting, the graph of the
 * view's scene is transformed into a layout graph consisting of
 * {@link de.grogra.imp2d.layout.Node} and {@link de.grogra.imp2d.layout.Edge}.
 * The layout is then performed on the <code>x</code>- and
 * <code>y</code>-fields of these nodes, which specify global coordinates.
 * Finally, the layouted global coordinates are written back into the
 * original graph.
 * 
 * @author Ole Kniemeyer
 */
public abstract class Layout extends ShareableBase
{
	//enh:sco SCOType

	/**
	 * This is the scaling factor between the original graph and the
	 * layout graph.
	 */
	float scale = 1;
	
	private boolean layouted = false;
	boolean isRepositioningNodes = true;

	public Node oldlayoutedList = null; 
	private Node hierarchicalPickedNode = null;
	private int hierarchicalNodeCount; // number of children
	private final Point2f title = new Point2f(-0.3f, 0.3f);
	
	private Point2f circleCerter = new Point2f(1.55f, -1.4f);
	private final double perigon = 2*Math.PI;
	private double radius = 0.75;


	public boolean isLayouted() {
		return layouted;
	}

	public void setLayouted() {
		layouted = true;
	}

	public void setLayouted(boolean value) {
		layouted = value;
	}


	private void clearFlag(){
		Node temp = oldlayoutedList; 
		while(temp != null){
			((de.grogra.graph.impl.Node)(temp.object)).h_draw_flag = false;
			((de.grogra.graph.impl.Node)(temp.object)).h_root_flag = false;
			Edge tempEdge = temp.getFirstEdge();
			while(tempEdge != null){
				((de.grogra.graph.impl.Edge)tempEdge.object).h_draw_flag = false;
				tempEdge = tempEdge.getNext(temp);
			}
			temp = temp.next;
		}
	}
	
	public de.grogra.graph.impl.Node handleGoUp(Object pickedNode){
		hierarchicalPickedNode = oldlayoutedList.nodeSearch(pickedNode);
		Edge pickedEdge = hierarchicalPickedNode.getFirstEdge();
		while(pickedEdge != null){
			Node p = pickedEdge.source;
			if(p != hierarchicalPickedNode){
				handlePickedNodeAndChildren((p.object));
				return ((de.grogra.graph.impl.Node)p.object);
			}
			pickedEdge = pickedEdge.getNext(hierarchicalPickedNode);
		}
		return null;
	}
	
	public void handlePickedNodeAndChildren(Object pickedNode)
	{
		clearFlag();
		hierarchicalPickedNode = oldlayoutedList.nodeSearch(pickedNode);
		if(hierarchicalPickedNode==null) {
			hierarchicalPickedNode = oldlayoutedList.nodeSearchByName(pickedNode);
		}
		Edge pickedEdge = hierarchicalPickedNode.getFirstEdge();
		Node p1, p2;
		p1 = hierarchicalPickedNode;
		hierarchicalNodeCount = 0;
		((de.grogra.graph.impl.Node)p1.object).h_draw_flag = true;
		((de.grogra.graph.impl.Node)p1.object).h_root_flag = true;
		while(pickedEdge != null){
			p2 = pickedEdge.target;
			if(p2 != null){
				if(p2 == hierarchicalPickedNode){
					// pickedEdge.h_draw_flag = false;
				}else{
					hierarchicalNodeCount++;
					p1.nextForHierarchicalView = p2;
					p2.nextForHierarchicalView = null;
					((de.grogra.graph.impl.Node)p2.object).h_draw_flag = true;
					Edge pickedEdge2 = p2.getFirstEdge();
					while(pickedEdge2 != null){
						if(((de.grogra.graph.impl.Node)pickedEdge2.target.object).h_draw_flag 
								&& ((de.grogra.graph.impl.Node)pickedEdge2.source.object).h_draw_flag
								&& !((de.grogra.graph.impl.Node)pickedEdge2.target.object).h_root_flag
								&& !((de.grogra.graph.impl.Node)pickedEdge2.source.object).h_root_flag)
							((de.grogra.graph.impl.Edge)pickedEdge2.object).h_draw_flag = true;
						pickedEdge2 = pickedEdge2.getNext(p2);
					}
					
					p1 = p2;
				}
			}
			pickedEdge = pickedEdge.getNext(hierarchicalPickedNode);
		}
		hierarchicalCoordGenerate();
	}
	
	private void hierarchicalCoordGenerate()
	{
		double step = perigon / hierarchicalNodeCount;
		double pointer = Math.PI/2;
		hierarchicalPickedNode.set(title);
		Node p1 = hierarchicalPickedNode.nextForHierarchicalView;
		for (int i = 1; i<=hierarchicalNodeCount; i++)
		{
			Point2f tmp = new Point2f((float)(radius*Math.cos(pointer)),(float)(radius*Math.sin(pointer)));
			tmp.add(circleCerter);
			p1.set(tmp);
			pointer = pointer + step;
			p1 = p1.nextForHierarchicalView;
		}
	}
	
	public void setBoxSize (double xMax, double yMax)
	{
		circleCerter = new Point2f((float)(0.5*xMax), (float)(0.4*yMax));
		radius = 0.85f*Math.min (xMax, yMax);
	}
	
	boolean fit = true;
	//enh:field
	
	int transformationSteps = 1;
	//enh:field
	
	//for the style of fitting into the drawing window
	int param = -1;
	
	//text output in case of success or error messages for the user
	String progressText = "";
	
	boolean redraw = true;
	/**
	 * An <code>Algorithm</code> is created by a {@link Layout} in order
	 * to perform the actual layout task. It may use the fields
	 * of the layout instance as algorithm parameters. 
	 * 
	 * @author Ole Kniemeyer
	 */
	public abstract class Algorithm
	{
		protected GraphState state;
		protected Graph graph;
		protected Graph finalGraph;
		protected View2DIF view;
		
		float epsilon = 1e-5f;		

		final Point2f globalCenter = new Point2f ();
		int globalNodeCount;
		
		private boolean statusSet;
		
		private int count = transformationSteps;

		/**
		 * This method has to be implemented to perform the layout.
		 * 
		 * @param nodes the first node of the linked list of all nodes
		 * @see Node 
		 */
		protected abstract void layout (Node nodes);


		/**
		 * Determines whether a given <code>node</code> of the original
		 * graph is to be included in the layout graph.
		 * 
		 * @param node a node of the original graph
		 * @return <code>true</code> iff the node is to be included in layout
		 */
		protected boolean acceptNode (Object node)
		{
			int n = 0;
			for (Object e = graph.getFirstEdge (node); e != null;
				 e = graph.getNextEdge (e, node))
			{
				if (GraphUtils.testEdgeBits (graph.getEdgeBits (e),
											 (node == graph.getSourceNode (e))
											 ? Graph.EDGENODE_OUT_EDGE
											 : Graph.EDGENODE_IN_EDGE))
				{
					return false;
				}
				if (++n == 2)
				{
					break;
				}
			}
			AttributeAccessor a = graph.getAccessor (node, true, Attributes.TRANSFORM);			
			return (graph.getAccessor (node, true, Attributes.SHAPE) != null)
				&& ((a = graph.getAccessor (node, true, Attributes.TRANSFORM)) != null)
				&& a.isWritable (node, state)
				&& (graph.getLifeCycleState(node, true) != Graph.TRANSIENT)
				;
		}


		/**
		 * Delegates layouting to another <code>layout</code>. This method
		 * initializes an algorithm created by <code>layout</code> with the
		 * current context and graph information and
		 * invokes {@link #layout(Node)} on the created algorithm. This
		 * method may only be invoked by an active algorithm, i.e.,
		 * only within {@link #layout(Node)}.
		 * 
		 * @param layout another layout to be used
		 * @param nodes the linked list of layout nodes
		 */
		public final void invokeLayout (Layout layout, Node nodes)
		{
			Algorithm a = layout.createAlgorithm ();
			a.state = state;
			a.graph = graph;
			a.view = view;
			a.layout (nodes);
		}


		/**
		 * This method sets the status bar of the UI.
		 * 
		 * @param progress a value indicating the progress of the algorithm
		 * @param text a text to show in the status bar
		 */
		protected void setProgress (float progress, String text)
		{
			if (!statusSet)
			{
				view.getWorkbench ().beginStatus (this);
				statusSet = true;
			}
			view.getWorkbench ().setStatus (this, text, progress);
		}

		
		HashSet<Object> acceptedNodes = null;
		
		class GraphVisitor implements Visitor {
			@Override
			public GraphState getGraphState() {
				return graph.getMainState();
			}
			@Override
			public Object visitEnter(Path path, boolean node) {
				if (node)
				{
					acceptedNodes.add(path.getObject (-1));
				}
				return null;
			}
			@Override
			public Object visitInstanceEnter() {
				return STOP;
			}
			@Override
			public boolean visitInstanceLeave(Object o) {
				return true;
			}
			@Override
			public boolean visitLeave(Object o, Path path, boolean node) {
				return true;
			}
		}
		
		/**
		 * Getting the highest value (absolute) of height and width of the nodes
		 * @param nodes
		 * @return
		 */
		protected Vector2d getGlobalMaxBounds(Node nodes)
		{
			double maxXBound = 0;
			double maxYBound = 0;
			
			for(Node nodeTemp = nodes; nodeTemp != null; nodeTemp = nodeTemp.next)
			{
				if (!acceptedNodes.contains(nodeTemp.object))
					continue;
				
				Vector3d globalBound = new Vector3d(nodeTemp.width, nodeTemp.height, 0);
				if (Math.abs(globalBound.x) > maxXBound)
				{
					maxXBound = Math.abs(globalBound.x);
				}
				if (Math.abs(globalBound.y) > maxYBound)
				{
					maxYBound = Math.abs(globalBound.y);
				}
			}
			return new Vector2d(maxXBound, maxYBound);
		}
		
		/**
		 * Getting the longest distance (absolute) from nodes of the graph
		 * @param nodes
		 * @return
		 */
		protected Vector2d getGlobalMaxDistances(Node nodes)
		{
			double maxXDistance = 0;
			double maxYDistance = 0;
			
			for(Node nodeTemp = nodes; nodeTemp!= null; nodeTemp = nodeTemp.next)
			{
				if (!acceptedNodes.contains(nodeTemp.object))
					continue;
				
				for (Node nodeTemp2 = nodeTemp.next; nodeTemp2 != null; nodeTemp2 = nodeTemp2.next)
				{
					if (!acceptedNodes.contains(nodeTemp2.object))
						continue;
					Vector3d globalCoord = new Vector3d(nodeTemp.x, nodeTemp.y, 1);
					Vector3d globalCoord2 = new Vector3d(nodeTemp2.x, nodeTemp2.y, 1);
				
					if (Math.abs(globalCoord.x - globalCoord2.x) > maxXDistance)
					{
						maxXDistance = Math.abs(globalCoord.x - globalCoord2.x);
					}
					if (Math.abs(globalCoord.y - globalCoord2.y) > maxYDistance)
					{
						maxYDistance = Math.abs(globalCoord.y - globalCoord2.y);
					}
				}
			}
     
			return new Vector2d(maxXDistance, maxYDistance);
		}
		
		/**
		 * Getting the most differing x- and y-coordinates from point (0,0)
		 * @param nodes
		 * @return
		 */
		protected Vector2d getGlobalMaxCoordinates(Node nodes)
		{
			double maxX = 0;
			double maxY = 0;
		
			for(Node nodeTemp = nodes; nodeTemp!= null; nodeTemp = nodeTemp.next)
			{
				if (!acceptedNodes.contains(nodeTemp.object))
						continue;
			
				if (Math.abs(nodeTemp.x) > Math.abs(maxX))
				{
					maxX = nodeTemp.x;
				}
				if (Math.abs(nodeTemp.y) > Math.abs(maxY))
				{
					maxY = nodeTemp.y;
				}
			}
			return new Vector2d(maxX, maxY);
		}
		
		/**
		 * Getting the most differing x- and y-coordinates (absolute) from rootNode
		 * @param nodes
		 * @return
		 */
		protected Vector2d getGlobalMaxCoordinates(Node nodes, Node rootNode)
		{
			double maxX = 0;
			double maxY = 0;
		
			for(Node nodeTemp = nodes; nodeTemp!= null; nodeTemp = nodeTemp.next)
			{
				if (!acceptedNodes.contains(nodeTemp.object))
						continue;
			
				if (Math.abs(nodeTemp.x - rootNode.x) > Math.abs(maxX))
				{
					maxX = Math.abs(nodeTemp.x - rootNode.x);
				}
				if (Math.abs(nodeTemp.y - rootNode.y) > Math.abs(maxY))
				{
					maxY = Math.abs(nodeTemp.y - rootNode.y);
				}
			}
			return new Vector2d(maxX, maxY);
		}
		
		/**
		 * rotates and scales a graph to fit in the drawing window
		 * possibility for choosing the param:
		 * 0 -> the first node will be arranged in the centre 
		 * of the drawing window, then zooming
		 * else -> calculating the max distance in the graph,
		 * then zooming
		 * @param nodes
		 * @param param
		 */
		protected void fitLayoutToWindow(Node nodes, int param, String graphType)
		{
			Visitor visitor = new GraphVisitor();
			acceptedNodes = new HashSet<Object>();
			if (graph != null) {
				graph.accept(null, visitor, null, graphType);
			}

			Dimension dim = view.getSize();
			Matrix3d transformationMatrix = new Matrix3d();
			
			double scaleFactor = 0;
			Matrix3d scaleMatrix;
			Vector2d maxCoordinates;
			Vector2d maxBounds;
			
			transformationMatrix.setIdentity();
			
			//first zooming, max distance
			//movement whole graph to be visible in the drawing window
			Vector2d maxDistances = getGlobalMaxDistances(nodes);
			maxBounds = getGlobalMaxBounds(nodes);
			
			maxDistances.x += maxBounds.x;
			maxDistances.y += maxBounds.y;
			
			scaleFactor = 0;
			if ((maxDistances.x / dim.width) > (maxDistances.y / dim.height))
			{
				scaleFactor = (maxDistances.x) / (dim.width);
			} else
			{
				scaleFactor = (maxDistances.y) / (dim.height);
			}
			scaleMatrix = new Matrix3d(scaleFactor, 0, 0, 0, -scaleFactor, 0, 0, 0, 1);
			
			transformationMatrix.mul(scaleMatrix);
		
			maxCoordinates = getGlobalMaxCoordinates(nodes);
			Vector2d newMaxDistances = getGlobalMaxDistances(nodes);
			
			double movementX = 0;
			double movementY = 0;
			
			if (maxCoordinates.x > 0)
			{
				movementX = maxCoordinates.x - newMaxDistances.x/2;
			} else
			{
				movementX = maxCoordinates.x + newMaxDistances.x/2;
			}
			if (maxCoordinates.y > 0)
			{
				movementY = maxCoordinates.y - newMaxDistances.y/2;
			} else
			{
				movementY = maxCoordinates.y + newMaxDistances.y/2;
			}

			transformationMatrix.setColumn(2, movementX, movementY, 1);
			transformationMatrix.invert();
			view.setTransformation(transformationMatrix);
		}
		
		public String getCommandName ()
		{
			return null;
		}

		
		/**
		 * Indicates whether node positions should be post-processed
		 * automatically after layout computation has finished such that
		 * the center and overall size of node positions remain similar to
		 * the positions before layouting.
		 * 
		 * @return <code>true</code> iff automatic post-processing shall
		 * be applied
		 */
	//	protected boolean isRepositioningNodes ()
	//	{
	//		return true;
	//	}


		/**
		 * If this algorithm is invoked multiply for animation purposes,
		 * the result of the first invocation is stored in this list of
		 * layouted nodes. This result may be used by further invocations
		 * for interpolation. 
		 */
		Node layoutedList;

		/**
		 * This method manages layouting. It is invoked by
		 * {@link Layout#invoke(View2DIF)} and should not be invoked by
		 * user code. If this method returns <code>true</code>, it will be re-invoked. 
		 * @param graphType 
		 */
		final boolean run (View2DIF view, String graphType)
		{
			if (!redraw)
			{
				return false;
			}
////////////////////////////////////////////////
			
			//for (int i = 0; i < transformationSteps; i++)
			//{
//				try {
//					Thread.sleep(1000);
//				}catch (Exception e)
//				{
//					
//				}
			//	System.out.println("sleep: "+i+" " +counter);
			//	view.setTransformation(view.getTransformation());
				
				
			this.view = view;
			state = view.getWorkbenchGraphState ();
			graph = state.getGraph ();
		
			final ObjectMap lo = graph.createObjectMap ();

			class Visitor extends Visitor2D
			{
				private boolean getPositions;

				private int nodeCount = 0;
				
				private final Point2d tmpPoint = new Point2d ();
				private final Point2f tmpPoint2f = new Point2f ();
				private final Point2f min = new Point2f ();
				private final Point2f max = new Point2f ();
				private final Point2f center = new Point2f ();
				private final Matrix3d tmpIn = new Matrix3d ();
				private final Matrix3d tmpOut = new Matrix3d ();
				private final Pool pool = new Pool ();
				private final Rectangle2D bounds = new Rectangle2D.Double ();
				private final ObjectList modifications = new ObjectList ();
				private String graphType = "";
				
				private Node list = null;

				public Visitor(String graphType) {
					this.graphType = graphType;
				}

				void init (Node list)
				{
					this.list = list;
					for (Node n = list; n != null; n = n.next)
					{
						lo.putObject (n.object, true, n);
					}
					
				}

				Node createLayoutObjects ()
				{
					getPositions = true;
					Matrix3d m = new Matrix3d ();
					m.setIdentity ();
					init (Algorithm.this.state, Algorithm.this.graph.getTreePattern (), m);
					graph.accept (null, this, null, graphType);
					if (nodeCount > 0)
					{
						globalCenter.scale (1f / nodeCount);
					}
					
					return list;
				}

				/**
				 * Writes the computed positions to TRANSFORM-attributes of the target graph
				 */
				void write ()
				{
					getPositions = false;
					Matrix3d m = new Matrix3d ();
					m.setIdentity ();
					init (Algorithm.this.state, Algorithm.this.graph.getTreePattern (), m);
					graph.accept (null, this, null, graphType);
					while (!modifications.isEmpty ())
					{
						Attributes.TRANSFORM.setObject
							(modifications.pop (), true, modifications.pop (), state);
					}
					
				}

				@Override
				protected void visitImpl (Object node, boolean asNode, Path path)
				{
					if (fit && redraw)
					{
						isRepositioningNodes = false;
					}
					
					if (asNode && acceptNode (node))
					{
						Object s = state.getObjectDefault (node, true, Attributes.SHAPE, null);
						if (s instanceof BoundedShape)
						{
							((BoundedShape) s).getBounds (node, true, bounds, pool, state);
							tmpPoint.set (bounds.getMinX (), bounds.getMinY ());
							Math2.transformPoint (transformation, tmpPoint);
							min.set (tmpPoint);
							max.set (tmpPoint);

							tmpPoint.set (bounds.getMinX (), bounds.getMaxY ());
							Math2.transformPoint (transformation, tmpPoint);
							tmpPoint2f.set (tmpPoint);
							Math2.min (min, tmpPoint2f);
							Math2.max (max, tmpPoint2f);

							tmpPoint.set (bounds.getMaxX (), bounds.getMinY ());
							Math2.transformPoint (transformation, tmpPoint);
							tmpPoint2f.set (tmpPoint);
							Math2.min (min, tmpPoint2f);
							Math2.max (max, tmpPoint2f);

							tmpPoint.set (bounds.getMaxX (), bounds.getMaxY ());
							Math2.transformPoint (transformation, tmpPoint);
							tmpPoint2f.set (tmpPoint);
							Math2.min (min, tmpPoint2f);
							Math2.max (max, tmpPoint2f);
							
							center.interpolate (min, max, 0.5f);
						}
						else
						{
							min.set ((float) transformation.m02, (float) transformation.m12);
							max.set (min);
							center.set (min);
						}
						if (getPositions)
						{
							list = new Node (list, node);
							list.set (center);
							list.width = max.x - min.x;
							list.height = max.y - min.y;
							lo.putObject (node, true, list);
					
							globalCenter.add (center);
							nodeCount++;
						}
						else
						{
							Node n = (Node) lo.getObject (node, true);
							//if (isRepositioningNodes ())
							if (isRepositioningNodes)
							{
								tmpPoint2f.scaleAdd (scale, n, globalCenter);
							}
							else
							{
								tmpPoint2f.set (n);
							}
							// set transformation of node to tmpPoint2f
							if (!tmpPoint2f.epsilonEquals (center, epsilon))
							{
								tmpPoint2f.sub (center);
								// now tmpPoint2f contains difference vector in global coordinates
								//tmpPoint2f.scale (1); // this could be scaled for animation
								
								tmpPoint2f.scale(1/(float)count);
								
								tmpPoint.set (tmpPoint2f);
								Math2.invTransformVector (transformation, tmpPoint);
								// tmpPoint: difference in coordinates of parent of node

								Transform2D t = (Transform2D) state.getObject
									(node, true, Attributes.TRANSFORM);
								tmpIn.setIdentity ();
								tmpOut.setIdentity ();
								if (t != null)
								{
									t.transform (tmpIn, tmpOut);
									// tmpOut: transformation matrix of t

									Math2.transformPoint (tmpOut, tmpPoint);
									// transform tmpPoint to coordinates after t
									Math2.invertAffine (tmpOut, tmpIn);
								}
								// tmpOut contains local transformation from coordinates of
								// node to coordinates of parents
								// tmpIn contains inverse

								tmpOut.m02 = tmpPoint.x;
								tmpOut.m12 = tmpPoint.y;
								transformation.mul (tmpOut);
								t = TMatrix3d.createTransform (tmpOut);
								//t.transform (tmpIn, tmpOut);
								transformation.mul (tmpOut);
								modifications.push (t).push (node);
								
							}
						}
					}
					//test
					
					//ende test
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
			}

			Visitor v = new Visitor (graphType);
			
			try
			{
				if (oldlayoutedList != null)
				{
					v.init (oldlayoutedList);
					v.write ();
				}
				else
				{
				
					if (layoutedList != null)
					{
						// not the first time of invocation, re-use previously
						// computed values for interpolation
						v.init (layoutedList);
						v.write ();
						
					}
					else
					{
						progressText = "";
						this.setProgress(0.5f, "calculating..");
						
						Node r = v.createLayoutObjects ();
						if (r != null)
						{
							try
							{
								createEdges (r, lo);
							}
							catch (CloneNotSupportedException e) {}
							
							
							layout (r);
							
							if (fit)
							{
								fitLayoutToWindow(r, param, graphType);
							}
							double gx = globalCenter.x, gy = globalCenter.y;
							globalCenter.set (0, 0);
							globalNodeCount = 0;
							for (Node n = r; n != null; n = n.next)
							{
								globalCenter.add (n);
								globalNodeCount++;
							}
							if ((globalNodeCount > 1)
								&& !Float.isNaN (globalCenter.x)
								&& !Float.isNaN (globalCenter.y)
								&& !Float.isInfinite (globalCenter.x)
								&& !Float.isInfinite (globalCenter.y))
							{
								globalCenter.scale (-scale / globalNodeCount);
								globalCenter.x += gx;
								globalCenter.y += gy;
								epsilon = (float) (scale * 1e-5);
								v.write ();
							}
						
							this.setProgress(0, progressText);
						}
						layoutedList = r;
						oldlayoutedList = r;

					}
				}
			}
			finally
			{
				lo.dispose ();
			}
			
		
//////////////////////////////////////////////////////
			//}//endfor
			try {
				
					
				Thread.sleep(100);
			} catch (Exception e)
			{
				
			}
			//fitLayoutToWindow(layoutedList);
			return --count > 0;
			//return true;
		}


		private void createEdges (Node n, ObjectMap visited) throws CloneNotSupportedException {
			while (n != null)
			{
				n.sub (globalCenter);
				n.scale (1 / scale);
				for (Object e = graph.getFirstEdge (n.object); e != null;
					 e = graph.getNextEdge (e, n.object))
				{
					Object t = graph.getTargetNode (e);
					Object o;
					boolean edgeNode;
					if (t != n.object)
					{
					findEdge:
						if (GraphUtils.testEdgeBits (graph.getEdgeBits (e), Graph.EDGENODE_IN_EDGE))
						{
							o = t;
							edgeNode = true;
							for (Object f = graph.getFirstEdge (o); f != null;
								 f = graph.getNextEdge (f, o))
							{
								t = graph.getTargetNode (f);
								if ((t != o)
									&& GraphUtils.testEdgeBits (graph.getEdgeBits (f), Graph.EDGENODE_OUT_EDGE))
								{
									break findEdge;
								}
							}
							continue;
						}
						else
						{
							 o = e;
							 edgeNode = false;
						}
						float w = state.getFloatDefault (o, edgeNode, Attributes.WEIGHT, edgeNode ? 1 : 0);
						if (w > 0)
						{
							Node m = (Node) visited.getObject (t, true);
							if (m != null)
							{
								Edge a = (Edge) visited.getObject (o, edgeNode);
								if (a == null)
								{
									a = n.getOrCreateEdgeTo (m, o, edgeNode, w);
									visited.putObject (o, edgeNode, a);
									
									/*
									Edge[] aa = n.getOrCreateEdgeListTo (m, o, edgeNode, w);
									for (Edge edge : aa)
									{
										visited.putObject (o, edgeNode, edge);
									}
									*/
								}
							}
						}
					}
				}
				n = n.next;
			}
		}
		
		

	}

	public void setParam(int param)
	{
		this.param = param;
	}
	
	public int getParam()
	{
		return this.param;
	}
	
	/**
	 * Creates an algorithm for this layout.
	 * 
	 * @return a new <code>Algorithm</code> instance
	 */
	protected abstract Algorithm createAlgorithm ();


	/**
	 * This method is invoked by a {@link View2DIF}.
	 * It invokes the <code>run</code>-method of the algorithm of this layout,
	 * or of <code>algo</code> if this is not <code>null</code>. If this
	 * invocation returns <code>true</code>, the algorithm is returned by this
	 * method, otherwise it returns <code>null</code>. If the returned value is not
	 * <code>null</code>, the invoking <code>View2DIF</code> re-invokes this
	 * method with the returned value as <code>algo</code>.
	 * 
	 * @param view invoking view
	 * @param algo existing algorithm instance to use, or <code>null</code>
	 * @param graphType 
	 * @return a non-null instance if this method should be re-invoked by
	 * <code>view</code>, this is then passes as <code>algo</code>-parameter
	 */
	public Algorithm invoke (View2DIF view, Algorithm algo, String graphType)
	{
		if (algo == null)
		{
			algo = createAlgorithm ();
		}
		return algo.run (view, graphType) ? algo : null;
	}
	
	public void setRedraw (boolean value)
	{
		this.redraw = value;
	}
	
	public void setTransformationSteps (int value)
	{
		this.transformationSteps = value;
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field fit$FIELD;
	public static final Type.Field transformationSteps$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Layout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Layout) o).fit = value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Layout) o).fit;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((Layout) o).transformationSteps = value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Layout) o).transformationSteps;
			}
			return super.getInt (o, id);
		}
	}

	static
	{
		$TYPE = new Type (Layout.class);
		fit$FIELD = Type._addManagedField ($TYPE, "fit", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		transformationSteps$FIELD = Type._addManagedField ($TYPE, "transformationSteps", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}
