package de.grogra.imp3d.gl20;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;

import javax.vecmath.*;

import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.graph.impl.*;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp3d.DisplayVisitor;
import de.grogra.imp3d.gl20.GL20Const;
import de.grogra.imp3d.gl20.GL20GfxServer;
import de.grogra.imp3d.gl20.GL20Node;
import de.grogra.imp3d.gl20.GL20Resource;
import de.grogra.imp3d.LineArray;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.LineSegmentizationCache;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.View3D;
import de.grogra.math.Pool;
import de.grogra.math.TMatrix4d;
import de.grogra.vecmath.Math2;

/**
 * The GL20DisplayVisitor travels through the graph and collect information
 * about it.
 *
 * @author Jack Weinert
 */
public class GL20DisplayVisitor extends DisplayVisitor implements RenderState {
	private PolygonizationCache polygonizationCache;
	private LineSegmentizationCache lineSegmentizationCache = null;
	
	/**
	 * a pool, go swimming ;-)
	 */
	private final Pool pool = new Pool();

	/**
	 * root <code>GL20Node</code>
	 */
	private GL20Node rootNode = null;

	/**
	 * the current <code>GL20GfxServer</code>
	 */
	private GL20GfxServer gfxServer = null;
	
	private ViewSelection viewSelection = null;
	
	/**
	 * current <code>GL20Node</code>
	 */
	private GL20Node currentNode = null;
	
	/**
	 * set to <code>true</code> if the current traveling is for the tool objects
	 */
	private boolean toolTravel;
	
	/**
	 * last context ID from <code>GL20GfxServer</code>
	 */
	private int lastContextID = GL20Const.INVALID_ID;
	
	/**
	 * initialize a complete new visiting
	 *
	 * @param gs the current <code>GraphState</code>
	 * @param view
	 * @param toolTravel <code>true</code> - when this run is the tool traveling run 
	 */
	public void initialize(GraphState gs, View3D view, boolean toolTravel) {
		this.init(gs, GL20Const.identityMatrix4d, view, true);
		
		this.toolTravel = toolTravel;

		if (rootNode == null)
			rootNode = new GL20Node(0);

		if (this.toolTravel == false) {
			GL20Node.startNewVisit(rootNode);
			viewSelection = ViewSelection.get(view);
		}
		
		// get current gfx server instance
		gfxServer = GL20GfxServer.getInstance();
		if (lastContextID != gfxServer.getContextID()) {
			if (polygonizationCache != null) {
				polygonizationCache.clear();
				polygonizationCache = null;
			}
			lastContextID = gfxServer.getContextID();
		}
	}

	/**
	 * this is just a wrapper
	 * when entering a <code>Node</code>, the associated <code>GL20Node</code>
	 * will be entered
	 */
	protected void visitEnterImpl(Object object, boolean asNode, Path path) {
		if (toolTravel == false) {
			if (asNode == true) {
				GL20Node.enterNode(((Node)object).getId());
			}
		}

		super.visitEnterImpl(object, asNode, path);
	}

	/**
	 * this is just a wrapper
	 * when leaving a <code>Node</code>, the associated <code>GL20Node</code>
	 * will be leaved 
	 */
	protected void visitLeaveImpl(Object object, boolean asNode, Path path) {
		if (toolTravel == false) {
			if (asNode == true) {
				ArrayList<GL20Node> restNodes = GL20Node.leaveNode();
	
				if (restNodes != null) {
					// there are nodes that wasn't visit, they must be destroyed
					int nodeCount = restNodes.size();
					for (int i=0;i < nodeCount;i++) {
						GL20Node node = restNodes.get(i);
						GL20Resource resource = node.getResource();
						if (resource != null)
							resource.destroy();
					}
				}
			}
		}
		
		super.visitLeaveImpl(object, asNode, path);
	}

	/**
	 * this method collect all data from <code>Node</code>s that have information
	 * for displaying
	 */
	protected void visitImpl(Object object, boolean asNode, Shader s, Path path) {
		if (asNode == true) {
			GL20Node currentNode = (toolTravel == true) ? null : GL20Node.getCurrentNode();
			
			Object lightObject = (toolTravel == true) ? null : state.getObjectDefault(object, asNode, Attributes.LIGHT, null);
			Object shapeObject = state.getObjectDefault(object, asNode, Attributes.SHAPE, null);
			
			if ((toolTravel == false) && (shapeObject != null) || (lightObject != null)) {
				// set the selection state of this shape node
				currentNode.setSelectionState(viewSelection.get(object, asNode));
			}

			if ((toolTravel == true) || (currentNode.isUpToDate(((Node)object).getStamp()) == false)) {
				// we have to update OR we are in tool traveling run
				
				// FIXME only debug
				//if ((currentNode != null) && (currentNode.isUpToDate(((Node)object).getStamp()) == false))
				//	System.out.println(".");
				
				if (toolTravel == false)
					// set current node for draw methods
					setCurrentNode(currentNode);

				// first check for attribute SHAPE
				if (shapeObject != null) {
					// we found a shape, so we must grab data
					if (shapeObject instanceof Renderable) {
						if (toolTravel == true) {
							currentNode = new GL20Node(0);
							setCurrentNode(currentNode);
						}
						
						((Renderable)shapeObject).draw(object,asNode,this);
					}
					else if (shapeObject instanceof LineSegmentizable) {
						if (toolTravel == true)
							currentNode = new GL20Node(0);
						
						GL20ResourceShapeLineStrip resource = (GL20ResourceShapeLineStrip)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_LINE_STRIP);
						
						if ((lineSegmentizationCache != null) &&
							(lineSegmentizationCache.getGraphState() != getRenderGraphState())) {
							lineSegmentizationCache.clear();
							lineSegmentizationCache = null;
						}
						
						if (lineSegmentizationCache == null)
							lineSegmentizationCache = new LineSegmentizationCache(getRenderGraphState(),1);
						
						LineArray lineArray = lineSegmentizationCache.get (object, asNode, (LineSegmentizable) shapeObject);
						
						resource.setLineArray(lineArray);
						
						Object colorObject = state.getObjectDefault (object, asNode,Attributes.COLOR, null);
						if (colorObject != null) {
							// object has a color
							resource.setColor(new Vector4f((Tuple3f)colorObject));
						}
						else {
							// object has no color so get average color from shader
							int sRGBColor = s.getAverageColor();
							resource.setColor(new Vector4f((sRGBColor & 0xFF) * (1.0f / 255.0f),
									((sRGBColor & 0xFF00) >> 8) * (1.0f / 255.0f),
									((sRGBColor & 0xFF0000) >> 16) * (1.0f / 255.0f),
									((sRGBColor & 0xFF000000) >> 24) * (1.0f / 255.0f)));
						}
						resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(null));
					}
					
					if ((toolTravel == true) && (currentNode != null)) {
						// apply all changes via 'update()'						
						currentNode.update(((Node)object).getStamp());
						
						// register node at gfx server
						gfxServer.addNodeToTool(currentNode);
					}
				}
				else {
					// handle other objects than shapes
					
				}

				if (toolTravel == false)
					// apply all changes via 'update()'					
					currentNode.update(((Node)object).getStamp());

				// FIXME only for debug reasons
				// System.out.print("U");
			}

			// register node at gfx server
			if (toolTravel == false)
				gfxServer.addNodeToScene(currentNode);
		}	//	if (asNode == true)
	}
	
	/**
	 * set the current node for any draw method
	 * 
	 * @param currentNode
	 */
	final private void setCurrentNode(GL20Node currentNode) {
		this.currentNode = currentNode;
	}
	
	/**
	 * get the current node
	 * 
	 * @return the current node
	 */
	final private GL20Node getCurrentNode() {
		return currentNode;
	}

	// ------------------------------------------------------------------------
	// RenderState methods
	// ------------------------------------------------------------------------
	public void drawBox(float halfWidth, float halfLength, float height,
			Shader s, int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapeBox resource = (GL20ResourceShapeBox)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_BOX);

		resource.setHalfWidth(halfWidth);
		resource.setHalfLength(halfLength);
		resource.setHeight(height);
		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawFrustum(float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader s,
			int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapeFrustum resource = (GL20ResourceShapeFrustum)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_FRUSTUM);

		resource.setHeight(height);
		resource.setBaseRadius(baseRadius);
		resource.setTopRadius(topRadius);
		resource.setBaseClosed(baseClosed);
		resource.setTopClosed(topClosed);
		// TODO what is the sense of this parameter
		// resource.setScaleV(scaleV);
		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}
	
	public void drawFrustumIrregular(float height, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topclosed, 
			float scaleV, Shader s, int highlight, Matrix4d t)
	{
		return;
	}

	public void drawLine(Tuple3f start, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapeLine resource = (GL20ResourceShapeLine)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_LINE);
		
		resource.setColor(new Vector4f(color));
		resource.setLineCoordinates(new Vector3f(start), new Vector3f(end));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawParallelogram(float axis, Vector3f secondAxis,
			float scaleU, float scaleV, Shader s, int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapeParallelogram resource = (GL20ResourceShapeParallelogram)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_PARALLELOGRAM);

		resource.setAxis(axis);
		resource.setSecondAxis(secondAxis);
		resource.setScaleU(scaleU);
		resource.setScaleV(scaleV);
		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawPlane(Shader s, int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapePlane resource = (GL20ResourceShapePlane)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_PLANE);

		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawPoint(Tuple3f location, int pixelSize, Tuple3f color,
			int highlight, Matrix4d t) {
	}

	public void drawPointCloud(float[] locations, float pointSize, Tuple3f color,
			int highlight, Matrix4d t)
	{
		int N = locations.length / 3;
		final Point3f p = new Point3f();
		if (pointSize <= 0)
			pointSize = 3;
		for (int i = 0; i < N; i++) {
			p.set(locations[3*i+0], locations[3*i+1], locations[3*i+2]);
			drawPoint(p, Math.max((int)pointSize, 1), color, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}

	public void drawPolygons(Polygonizable polygons, Object obj,
			boolean asNode, Shader s, int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapePolygons resource = (GL20ResourceShapePolygons)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_POLYGONS);

		if ((polygonizationCache != null) &&
			(polygonizationCache.getGraphState() != getRenderGraphState())) {
			polygonizationCache.clear();
			polygonizationCache = null;
		}

		if (polygonizationCache == null)
			polygonizationCache = new PolygonizationCache(getRenderGraphState(),
					Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV,
					10,	// TODO WTF what is the meaning of this parameter ?
					true);

		PolygonArray polygonArray = polygonizationCache.get(obj, asNode, polygons);

		resource.setPolygonArray(polygonArray);
		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawRectangle(int x, int y, int w, int h, Tuple3f color) {
	}

	public void drawSphere(float radius, Shader s, int highlight, Matrix4d t) {
		GL20Node currentNode = getCurrentNode();
		GL20ResourceShapeSphere resource = (GL20ResourceShapeSphere)currentNode.createResource(GL20Resource.GL20RESOURCE_SHAPE_SPHERE);

		resource.setRadius(radius);
		resource.setShader(GL20ShaderServer.getShader(getCurrentShader(s)));
		resource.setWorldTransformationMatrix(getCurrentTransformationMatrix(t));
	}

	public void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader sh, int highlight, Matrix4d t)
	{
	}
	
	public void drawString(int x, int y, String text, Font font, Tuple3f color) {
	}

	public float estimateScaleAt(Tuple3f point) {
		return 0;
	}

	public void fillRectangle(int x, int y, int w, int h, Tuple3f color) {
	}

	public int getCurrentHighlight() {
		return 0;
	}

	public FontMetrics getFontMetrics(Font font) {
		return null;
	}

	public Pool getPool() {
		return pool;
	}

	public GraphState getRenderGraphState() {
		return state;
	}

	public boolean getWindowPos(Tuple3f location, Tuple2f out) {
		return false;
	}

	/**
	 * calculate the current transformation matrix
	 *
	 * @param t the transformation matrix from an object OR <code>null</code>
	 * @return the current transformation matrix
	 */
	private Matrix4d getCurrentTransformationMatrix(Matrix4d t) {
		if (t == null)
			return getCurrentTransformation();
		else {
			Matrix4d temp = new TMatrix4d();
			Math2.mulAffine(temp, getCurrentTransformation(), t);
			return temp;
		}
	}
	
	private Shader getCurrentShader(Shader s) {
		if (s == null)
			return getCurrentShader();
		else
			return s;
	}
}