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

package de.grogra.imp3d;

import static de.grogra.vecmath.Math2.M_2PI;
import static de.grogra.vecmath.Math2.M_PI;
import static de.grogra.vecmath.Math2.M_PI_2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.jibble.epsgraphics.EpsGraphics2D;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.Path;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.View;
import de.grogra.imp.awt.CanvasAdapter;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.anaglyph.ImageTool;
import de.grogra.imp3d.anaglyph.ImageToolException;
import de.grogra.imp3d.anaglyph.StereoImage;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.BSpline;
import de.grogra.math.BSplineSurface;
import de.grogra.math.Pool;
import de.grogra.math.TMatrix4d;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.util.LockProtectedCommand;
import de.grogra.util.EventListener;
import de.grogra.util.Lock;
import de.grogra.util.WrapException;
import de.grogra.vecmath.Math2;

public class WireframeCanvas extends CanvasAdapter implements RenderState,
		Selectable
{
	static final boolean DRAW_NURBS_UNIFORMLY = false;

	public static final int OVERLAY = -1;

	protected static final int COLOR = 0, FONT = 1, LINE = 2, STRING = 3,
			DRAW_RECT = 4, FILL_RECT = 5, DRAW_POLY = 6, FILL_POLY = 7,
			ICON = 8;

	//! name of option, same as the one in plugin.xml file
	private static final String OPTION_NAME_SHOW_POINTS = "showPoints";

	private boolean optionShowPoints = false;

	// grid
	private static final String OPTION_NAME_SHOW_GRID = "showGrid";
	private boolean optionShowGrid = false;
	
	private static final String OPTION_NAME_GRID_DIMENSION = "gridDimension";
	private int optionGridDimension = 1;
	
	private static final String OPTION_NAME_GRID_SPACING = "gridSpacing";
	private float optionGridSpacing = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_R = "gridColorR";
	private float optionGridColorR = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_G = "gridColorG";
	private float optionGridColorG = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_B = "gridColorB";
	private float optionGridColorB = 1;
	
	private static final String OPTION_NAME_BACKGROUND_COLOR_R = "backgroundColorR";
	private float optionBackgroundColorR = 1;
	
	private static final String OPTION_NAME_BACKGROUND_COLOR_G = "backgroundColorG";
	private float optionBackgroundColorG = 1;
	
	private static final String OPTION_NAME_BACKGROUND_COLOR_B = "backgroundColorB";
	private float optionBackgroundColorB = 1;
	
	private static final String OPTION_NAME_BACKGROUND_ALPHA = "backgroundAlpha";
	private float optionBackgroundAlpha = 1;
	
	// axes of coordinates
	private static final String OPTION_NAME_SHOW_AXES = "showAxes";
	private boolean optionShowAxes = false;	

	private static final String OPTION_NAME_SHOW_AXES_NAMES = "showAxesNames";
	private boolean optionShowAxesNames = false;
	
	// display size
	private static final String OPTION_NAME_SHOW_DISPLAY_SIZE = "showDisplaySize";
	private boolean optionShowDisplaySize = false;
	
	// split view
	private static final String OPTION_SHOW_PREVIEW1 = "showPreView1";
	private static final String OPTION_SHOW_PREVIEW2 = "showPreView2";
	//size of the split view windows in percent of total view size
	private static final String OPTION_SPLIT_VIEW_SIZE = "splitViewSize";
	private float optionSplitViewSize = 1;

	//stereo view
	private static final String OPTION_STEREO_VIEW = "stereoView";
	private boolean optionStereoView = false;
//  optionStereoMode

	//anaglyph view
	private static final String OPTION_ANAGLYPH_VIEW = "anaglyphView";
	private boolean optionAnaglyphView = false;
//  optionAnaglyphMode

	//stereo and anaglyph view
	private static final String OPTION_EYE_SEPARATION = "eyeSeparation";
	private float optionEyeSeparation = 15;	

	
	public final Pool pool = new Pool ();

	private float[] cx = new float[10], cy = new float[10], cz = new float[10];

	private final Layer[] layers;

	private Layer[] layerStack = new Layer[10];
	private Layer currentLayer;
	private int lsSize;
	private Graphics bufferGraphics;

	private final WFVisitor visitor = new WFVisitor ();
	private PolygonizationCache polyCache;

	private final Point3d q0 = new Point3d ();
	private final Point3f q0f = new Point3f ();

	private boolean computeMinMax;
	private float minX;
	private float minY;
	private float minZ;
	private float maxX;
	private float maxY;
	private float maxZ;

	private final boolean justFitCamera;

	private class WFVisitor extends DisplayVisitor
	{
		private int selectionState;
		private int minPathLength;
		private LineSegmentizationCache lineCache;

		void init (GraphState gs, int selectionState, int minPathLength, boolean checkLayer)
		{
			init (gs, ((View3D) getView ()).getCanvasCamera ()
				.getWorldToViewTransformation (), getView3D (), checkLayer);
			this.selectionState = selectionState;
			this.minPathLength = minPathLength;
			if (lineCache == null)
			{
				lineCache = new LineSegmentizationCache (gs, 1);
			}
		}

		private void setColor (Object object, boolean asNode, Shader s,
				GraphState state)
		{
			if (justFitCamera)
			{
				return;
			}
			Object c = state.getObjectDefault (object, asNode,
				de.grogra.imp3d.objects.Attributes.COLOR, this);
			if ((c != null) && (c != this))
			{
				WireframeCanvas.this.setColor ((Color3f) c, selectionState,
					false);
			}
			else
			{
				WireframeCanvas.this.setColor (s.getAverageColor (),
					selectionState, false);
			}
		}

		@Override
		protected void visitImpl (Object object, boolean asNode, Shader s,
				Path path)
		{
			if (path.getNodeAndEdgeCount () - (asNode ? 0 : 1) >= minPathLength)
			{
				Object shape = state.getObjectDefault (object, asNode,
					de.grogra.imp3d.objects.Attributes.SHAPE, null);
				if (shape != null)
				{
					if (DRAW_NURBS_UNIFORMLY
						&& (object instanceof NURBSSurface))
					{
						drawNURBSUniformly ((NURBSSurface) object, asNode,
							null, RenderState.CURRENT_HIGHLIGHT, null);
					}
					else if (shape instanceof Renderable)
					{
						((Renderable) shape).draw (object, asNode,
							WireframeCanvas.this);
					}
					else if (shape instanceof LineSegmentizable)
					{
						LineArray lines = lineCache.get (object, asNode,
							(LineSegmentizable) shape);
						boolean selected = (selectionState & ViewSelection.SELECTED) != 0;
						setColor (object, asNode, s, state);
						int[] vertices = lines.lines.elements;
						float[] data = lines.vertices.elements;
						int dim = lines.dimension;
						int n = lines.lines.size;
						int i = 0;
						int idx = 0;
						while (i < n)
						{
							int v = vertices[i++];
							if (v >= 0)
							{
								v *= dim;
								setVertex (idx & 1, data[v],
									(dim > 1) ? data[v + 1] : 0,
									(dim > 2) ? data[v + 2] : 0, transformation);
								if (++idx >= 2)
								{
									drawLine (0, 1, selected);
								}
							}
							else
							{
								idx = 0;
							}
						}
					}
				}
			}
		}

	}

	private class Layer
	{
		private int[] intFifo = new int[100];
		private Object[] objFifo = new Object[20];
		private int intHead, intTail, objHead, objTail;
		Color color;
		private Font font;

		Layer ()
		{
			reset ();
		}

		void putInt (int i)
		{
			if (intHead == intFifo.length)
			{
				System.arraycopy (intFifo, 0, intFifo = new int[intHead + 50],
					0, intHead);
			}
			intFifo[intHead++] = i;
		}

		void putInt (int i0, int i1, int i2)
		{
			if (intHead + 3 > intFifo.length)
			{
				System.arraycopy (intFifo, 0, intFifo = new int[intHead + 50],
					0, intHead);
			}
			intFifo[intHead++] = i0;
			intFifo[intHead++] = i1;
			intFifo[intHead++] = i2;
		}

		void putInt (int i0, int i1, int i2, int i3, int i4)
		{
			if (intHead + 5 > intFifo.length)
			{
				System.arraycopy (intFifo, 0, intFifo = new int[intHead + 50],
					0, intHead);
			}
			intFifo[intHead++] = i0;
			intFifo[intHead++] = i1;
			intFifo[intHead++] = i2;
			intFifo[intHead++] = i3;
			intFifo[intHead++] = i4;
		}

		int getInt ()
		{
			return intFifo[intTail++];
		}

		void putObject (Object obj)
		{
			if (objHead == objFifo.length)
			{
				System.arraycopy (objFifo, 0,
					objFifo = new Object[objHead + 50], 0, objHead);
			}
			objFifo[objHead++] = obj;
		}

		Object getObject ()
		{
			return objFifo[objTail++];
		}

		void setColor (Color color)
		{
			if (this.color != color)
			{
				this.color = color;
				putInt (COLOR);
				putObject (color);
			}
		}

		void setFont (Font font)
		{
			if (this.font != font)
			{
				this.font = font;
				putInt (FONT);
				putObject (font);
			}
		}

		void reset ()
		{
			intHead = 0;
			intTail = 0;
			objHead = 0;
			objTail = 0;
			color = null;
			font = null;
			for (int i = 0; i < objFifo.length; i++)
			{
				objFifo[i] = null;
			}
		}

		void draw (Graphics g)
		{
			int t, x, y;
			while (intTail < intHead)
			{
				switch (t = getInt ())
				{
					case COLOR:
						g.setColor ((Color) getObject ());
						break;
					case FONT:
						g.setFont ((Font) getObject ());
						break;
					case LINE:
						g.drawLine (getInt (), getInt (), getInt (), getInt ());
						break;
					case STRING:
						g.drawString ((String) getObject (), getInt (),
							getInt ());
						break;
					case DRAW_RECT:
						g.drawRect (getInt (), getInt (), getInt (), getInt ());
						break;
					case FILL_RECT:
						g.fillRect (getInt (), getInt (), getInt (), getInt ());
						break;
					case DRAW_POLY:
						g.translate (x = getInt (), y = getInt ());
						g.drawPolygon ((Polygon) getObject ());
						g.translate (-x, -y);
						break;
					case FILL_POLY:
						g.translate (x = getInt (), y = getInt ());
						g.fillPolygon ((Polygon) getObject ());
						g.translate (-x, -y);
						break;
					case ICON:
						((Icon) getObject ()).paintIcon (null, g, getInt (),
							getInt ());
						break;
					default:
						drawOverlay (t);
						break;
				}
			}
		}

	}

	public WireframeCanvas ()
	{
		this (false);
	}

	private WireframeCanvas (boolean justComputeMinMax)
	{
		this.justFitCamera = justComputeMinMax;
		initCanvas (new CanvasComponent (640, 480));
		layers = new Layer[3];
		for (int i = 0; i < layers.length; i++)
		{
			layers[i] = new Layer ();
		}
	}

	static void fitCamera (View3D view)
	{
		WireframeCanvas c = new WireframeCanvas (true);
		c.initFactory (Item.resolveItem (view.getWorkbench (),
			View3D.DISPLAY_PATH + "/wireframe"));
		c.initView (view, new EventListener.Bicast ());
		c.checkBuffers ();
	}

	public View3D getView3D ()
	{
		return (View3D) getView ();
	}

	@Override
	public void initView (View view, EventListener listener)
	{
		super.initView (view, listener);
		optionShowPoints = Boolean.TRUE.equals (getOption (
			OPTION_NAME_SHOW_POINTS, Boolean.TRUE));
		lineWidth = ((Number) getOption ("lineWidth", new Float (1)))
			.floatValue ();
		optionShowGrid = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_GRID, Boolean.FALSE));
		optionGridDimension = (Integer) getOption(OPTION_NAME_GRID_DIMENSION, 5);
		optionGridSpacing = (Float) getOption(OPTION_NAME_GRID_SPACING, 1.0f);
		optionGridColorR = (Float) getOption(OPTION_NAME_GRID_COLOR_R, 1.0f);
		optionGridColorG = (Float) getOption(OPTION_NAME_GRID_COLOR_G, 1.0f);
		optionGridColorB = (Float) getOption(OPTION_NAME_GRID_COLOR_B, 1.0f);
		optionBackgroundColorR = (Float) getOption(OPTION_NAME_BACKGROUND_COLOR_R, 1.0f);
		optionBackgroundColorG = (Float) getOption(OPTION_NAME_BACKGROUND_COLOR_G, 1.0f);
		optionBackgroundColorB = (Float) getOption(OPTION_NAME_BACKGROUND_COLOR_B, 1.0f);
		optionBackgroundAlpha = (Float) getOption(OPTION_NAME_BACKGROUND_ALPHA, 1.0f);
		optionShowAxes = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_AXES, Boolean.FALSE));
		optionShowAxesNames = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_AXES_NAMES, Boolean.FALSE));
		optionShowDisplaySize = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_DISPLAY_SIZE, Boolean.FALSE));
		optionSplitViewSize = (Float) getOption(OPTION_SPLIT_VIEW_SIZE, 1.0f);
		// stereo and anaglyph view
		optionStereoView = Boolean.TRUE.equals(getOption(OPTION_STEREO_VIEW, Boolean.FALSE));
//        optionStereoMode
        optionAnaglyphView = Boolean.TRUE.equals(getOption(OPTION_ANAGLYPH_VIEW, Boolean.FALSE));
//        optionAnaglyphMode                    
        optionEyeSeparation = (Float) getOption(OPTION_EYE_SEPARATION, 15.0f);	
	}

	private int canvasWidth, canvasHeight;

	@Override
	protected void initPaint (int flags, int width, int height)
	{
		((View3D) getView ()).getCanvasCamera ().setDimension (width, height);
		this.canvasWidth = width;
		this.canvasHeight = height;
	}

	private float lineWidth = 1;

	@Override
	protected void paintScene (int flags, Graphics2D g)
			throws InterruptedException
	{
		computeMinMax = true;
		minX = minY = minZ = Float.POSITIVE_INFINITY;
		maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
		if (!(justFitCamera || (g instanceof EpsGraphics2D)))
		{
			g.setColor (new Color(optionBackgroundColorR, optionBackgroundColorG, optionBackgroundColorB, optionBackgroundAlpha));
			g.fillRect (0, 0, canvasWidth, canvasHeight);
		}
		currentLayer = null;
		lsSize = 0;
		Stroke oldStroke = g.getStroke ();
		try
		{
			if (antialiasing && !justFitCamera)
			{
				g.setStroke (new BasicStroke (lineWidth));
			}
			bufferGraphics = g;
			
			// draw grid behind scene objects
			if (optionShowGrid)
				drawGrid();
			
			visitor.init (getRenderGraphState (), 0, 0, true);
			try
			{
				//multiscale begin
				Graph graph = getView ().getGraph();
				if(graph instanceof GraphManager)
				{
					((GraphManager)graph).setVisibleScales((boolean[])(getView().getWorkbench().getProperty(View.SCALES_VISIBLE)));
				}
				//multiscale end
				getView ().getGraph ().accept (null, visitor, null);
				if (polyCache != null)
				{
					polyCache.clearUnused ();
				}
			}
			catch (WrapException e)
			{
				if (e.getCause () instanceof InterruptedException)
				{
					throw (InterruptedException) e.getCause ();
				}
				throw e;
			}
			
			// draw axes in front of all objects
			if (optionShowAxes)
				drawAxes();
			
			if (optionShowDisplaySize)
				drawDisplaySize();
			
			if (!justFitCamera)
			{
				for (int i = 0; i < layers.length; i++)
				{
					currentLayer = layers[i];
					currentLayer.draw (g);
					currentLayer.reset ();
				}
			}
			Point3f min = new Point3f (minX, minY, minZ);
			final Point3f max = new Point3f (maxX, maxY, maxZ);

			final float radius = 0.5f * max.distance (min);

			max.add (min);
			max.scale (0.5f);

			if (justFitCamera)
			{
				getView ().getWorkbench ().getJobManager ().runLater (
					new LockProtectedCommand (getView ().getGraph (), true,
						JobManager.UPDATE_FLAGS)
					{

						@Override
						public String getCommandName ()
						{
							return null;
						}

						@Override
						protected void runImpl (Object info, Context context, Lock lock)
						{
							getView3D ().fitCamera (new Vector3f (max), radius);
							dispose ();
						}
					}, null, getView (), JobManager.UPDATE_FLAGS);
			}
			else
			{
				getView3D ().setExtent (max, radius);
			}
		}
		finally
		{
			g.setStroke (oldStroke);
			
		}
	}

	/*
	 * version with split screen and anaglyth view
	 * problem: conflicts with repaints of the different views (probably threds not synchronised)
	 *
	 */
	protected void paintSceneNew (int flags, Graphics2D g) throws InterruptedException {
		if(mainIamge!=null) {
			mainIamge=null;
			return;
		}
		// if stereo or anaglyph view is turned on
		if(optionStereoView || optionAnaglyphView) {
			Matrix4d currentCameraTransformation = (Matrix4d)getView3D().getCamera ().getTransformation ().clone ();
			Matrix4d oldCameraTransformation = (Matrix4d)currentCameraTransformation.clone ();
			
			// calculate left eye image
			currentCameraTransformation.m03 -= optionEyeSeparation/2f;
			getView3D().setCameraTransformation(currentCameraTransformation);
			BufferedImage leftEyeImage = new BufferedImage(canvasWidth,canvasHeight, BufferedImage.TYPE_INT_ARGB);
			paintSceneBobyNew(flags, (Graphics2D)leftEyeImage.getGraphics (), false,false, true);
			// calculate right eye image
			currentCameraTransformation.m03 += optionEyeSeparation;
			getView3D().setCameraTransformation(currentCameraTransformation);
			BufferedImage rightEyeImage = new BufferedImage(canvasWidth,canvasHeight, BufferedImage.TYPE_INT_ARGB);
			paintSceneBobyNew(flags, (Graphics2D)rightEyeImage.getGraphics (), false,false, true);
			
			//combine images to the final 3D image
			if(optionAnaglyphView) {
				mainIamge = ImageTool.getAnaglyphImage (leftEyeImage, rightEyeImage,
					ImageTool.ANAGLYPH_LIST[((Number) getOption ("anaglyphMode", 0)).intValue ()]);
			} else {
				StereoImage simg = new StereoImage(leftEyeImage, rightEyeImage);
				try {
					mainIamge = simg.getResultImage(StereoImage.STEREO_LIST[((Number) getOption ("stereoMode", 0)).intValue ()]);
				}
				catch (ImageToolException e) {
					e.printStackTrace();
				}
			}
			//draw final image
			g.drawImage(mainIamge, 0,0,canvasWidth,canvasHeight, null);
//			g.drawImage(mainIamge, 0,0,200,150, 0,0,canvasWidth,canvasHeight, null);
//			g.drawImage(leftEyeImage, 0,150,200,300, 0,0,canvasWidth,canvasHeight, null);
//			g.drawImage(rightEyeImage, 0,300,200,450, 0,0,canvasWidth,canvasHeight, null);

			// set camera position back
			getView3D().setCameraTransformation(oldCameraTransformation);
		} else {
			paintSceneBobyNew(flags, g, true,false, true);
		}

		// one of the previews is turned on AND either stereo or anaglyph view is on
		int preview1 = ((Number) getOption ("showPreView1", 0)).intValue ();
		int preview2 = ((Number) getOption ("showPreView2", 0)).intValue ();
		if((preview1!=0 || preview2!=0) && !(optionStereoView || optionAnaglyphView) ) {
			//save current camera position
			Matrix4d oldCameraTransformation = (Matrix4d)getView3D().getCamera().getTransformation().clone();
			//save current view image
			mainIamge = sceneBuffer.getImage ();

			//set new camera
//			ViewConfig3D v = View3D.withCamera(View3D.getDefaultViewConfig(getView3D().getWorkbench()), Camera.createTopView());			
//			//paintScene
//			//rendered
//			Raytracer raytracer = new Raytracer(getView3D().getWorkbench(), v, x10,y10);
//			try {
//			g.drawImage(raytracer.computeImage(), 
//					xd,0,width-1,y10-1, 0,0,x10,y10, null);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			// draw main image as "background"
			g.drawImage(mainIamge, 0,0,sceneBuffer.getImage ().getWidth (null),sceneBuffer.getImage ().getHeight (null), null);

			int x10 = (int)(optionSplitViewSize*canvasWidth);
			int y10 = (int)(optionSplitViewSize*canvasHeight);
			int xd = canvasWidth - x10;

			// generate first split view
			Image perviewViewImage1 = null;
			if(preview1!=0) {
				Matrix4d transformation = getCameraTransformation(preview1);
				if(preview1==3 || preview1==4 || preview1==5 || preview1==6) {
					// correction of view (shift it a bit down to put the view more into the center)
					transformation.m13 -= 2;
				}
				getView3D().setCameraTransformation(transformation);
				perviewViewImage1 = new BufferedImage(canvasWidth,canvasHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics gx2 = perviewViewImage1.getGraphics ();

				paintSceneBobyNew(flags, (Graphics2D)gx2, false,true, false);

				//draw subimage
				g.drawImage(perviewViewImage1, xd,0,canvasWidth-1,y10-1, 0,0,canvasWidth,canvasHeight, null);
				//upper border 
				g.setColor(new Color (0.2f,0.2f,0.2f));
				g.drawRect(xd, 0, x10-1, y10-1);
				g.setColor(new Color (1f,0.2f,0.2f));
				g.drawRect(xd+1, 1, x10-3, y10-3);
			}
			// generate second split view
			if(preview2!=0) {
				Matrix4d transformation = getCameraTransformation(preview2);
				if(preview2==3 || preview2==4 || preview2==5 || preview2==6) {
					// correction of view (shift it a bit down to put the view more into the centre)
					transformation.m13 -= 2;
				}
				getView3D().setCameraTransformation(transformation);
				Image sideViewImage = new BufferedImage(canvasWidth,canvasHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics gx2 = sideViewImage.getGraphics ();
				
				paintSceneBobyNew(flags, (Graphics2D)gx2, false,true, false);
				
				if(perviewViewImage1==null) {
					//draw subimage
					g.drawImage(sideViewImage, xd,0,canvasWidth-1,y10-1, 0,0,canvasWidth,canvasHeight, null);
					//upper border 
					g.setColor(new Color (0.2f,0.2f,0.2f));
					g.drawRect(xd, 0, x10-1, y10-1);
					g.setColor(new Color (1f,0.2f,0.2f));
					g.drawRect(xd+1, 1, x10-3, y10-3);
				} else {
					//draw subimage
					g.drawImage(sideViewImage, xd,y10,canvasWidth-1,2*y10-1, 0,0,canvasWidth,canvasHeight, null);
					//upper border 
					g.setColor(new Color (0.2f,0.2f,0.2f));
					g.drawRect(xd, y10, x10-1, y10-1);
					g.setColor(new Color (1f,0.2f,0.2f));
					g.drawRect(xd+1, y10+1, x10-3, y10-3);
				}
			}
			// set camera position back
			getView3D().setCameraTransformation(oldCameraTransformation);
		}
	}

	private Matrix4d getCameraTransformation (int x) {
		Camera tmp = Camera.createTopView();
		switch (x) {
			case 1: tmp = Camera.createTopView(); break;
			case 2: tmp = Camera.createBottomView(); break;
			case 3: tmp = Camera.createLeftView(); break;
			case 4: tmp = Camera.createRightView(); break;
			case 5: tmp = Camera.createFrontView(); break;
			case 6: tmp = Camera.createBackView(); break;
		}
		return (Matrix4d)tmp.getTransformation().clone();
	}

	private Image mainIamge = null;

	private void paintSceneBobyNew (int flags, Graphics2D g, boolean showGimmicks, boolean isSplit, boolean drawGrid) throws InterruptedException {
		computeMinMax = true;
		
		minX = minY = minZ = Float.POSITIVE_INFINITY;
		maxX = maxY = maxZ = Float.NEGATIVE_INFINITY;
		if (!(justFitCamera || (g instanceof EpsGraphics2D)))
		{
			g.setColor (new Color(optionBackgroundColorR, optionBackgroundColorG, optionBackgroundColorB, optionBackgroundAlpha));
			g.fillRect (0, 0, canvasWidth, canvasHeight);
		}
		currentLayer = null;
		lsSize = 0;
		Stroke oldStroke = g.getStroke ();
		try
		{
			if (antialiasing && !justFitCamera)
			{
				g.setStroke (new BasicStroke (lineWidth));
			}
			
			// draw grid behind scene objects
			if (optionShowGrid && isSplit) drawGrid();

			bufferGraphics = g;

			if (drawGrid && !isSplit) {
				drawGrid();
			}

			
			visitor.init (getRenderGraphState (), 0, 0, true);
			try
			{
				//multiscale begin
				Graph graph = getView ().getGraph();
				if(graph instanceof GraphManager)
				{
					((GraphManager)graph).setVisibleScales((boolean[])(getView().getWorkbench().getProperty(View.SCALES_VISIBLE)));
				}
				//multiscale end
				getView ().getGraph ().accept (null, visitor, null);
				if (polyCache != null)
				{
					polyCache.clearUnused ();
				}
			}
			catch (WrapException e)
			{
				if (e.getCause () instanceof InterruptedException)
				{
					throw (InterruptedException) e.getCause ();
				}
				throw e;
			}
			
			// draw axes in front of all objects
			if (optionShowAxes && showGimmicks)
				drawAxes();
			
			if (optionShowDisplaySize && showGimmicks)
				drawDisplaySize();
			
			if (!justFitCamera)
			{
				for (int i = 0; i < layers.length; i++)
				{
					currentLayer = layers[i];
					currentLayer.draw (g);
					currentLayer.reset ();
				}
			}
			Point3f min = new Point3f (minX, minY, minZ);
			final Point3f max = new Point3f (maxX, maxY, maxZ);

			final float radius = 0.5f * max.distance (min);

			max.add (min);
			max.scale (0.5f);

			if (justFitCamera)
			{
				getView ().getWorkbench ().getJobManager ().runLater (
					new LockProtectedCommand (getView ().getGraph (), true,
						JobManager.UPDATE_FLAGS)
					{

						@Override
						public String getCommandName ()
						{
							return null;
						}

						@Override
						protected void runImpl (Object info, Context context, Lock lock)
						{
							getView3D ().fitCamera (new Vector3f (max), radius);
							dispose ();
						}
					}, null, getView (), JobManager.UPDATE_FLAGS);
			}
			else
			{
				getView3D ().setExtent (max, radius);
			}
		}
		finally
		{
			g.setStroke (oldStroke);
			
		}
	}
	

	@Override
	protected void paintHighlight (int flags, Graphics2D g)
	{
		if (justFitCamera)
		{
			return;
		}
		computeMinMax = false;
		currentLayer = null;
		lsSize = 0;
		bufferGraphics = g;

		if (ViewSelection.get (getView ()) == null)
		{
			System.err.println ("NULL");
			return;
		}
		ArrayPath path = new ArrayPath (getView ().getGraph ());
		ViewSelection.Entry[] s = ViewSelection.get (getView ()).getAll (-1);
		for (int i = 0; i < s.length; i++)
		{
			Path p = s[i].getPath ();
			visitor.init (getRenderGraphState (), s[i].getValue (), p
				.getNodeAndEdgeCount (), true);
			GraphUtils.acceptPath (p, visitor, path);
		}

		de.grogra.imp.edit.Tool tool = getView ().getActiveTool ();
		if (tool != null)
		{
			visitor.init (GraphManager.STATIC_STATE, 0, 0, false);
			path.clear (GraphManager.STATIC);
			for (int i = 0; i < tool.getToolCount (); i++)
			{
				GraphManager.acceptGraph (tool.getRoot (i), visitor, path);
			}
		}

		for (int i = 0; i < layers.length; i++)
		{
			currentLayer = layers[i];
			currentLayer.draw (g);
			currentLayer.reset ();
		}
	}

	protected void finalizePaint ()
	{
	}

	@Override
	public void setColor (Color color)
	{
		if (currentLayer == null)
		{
			if(bufferGraphics!=null) bufferGraphics.setColor (color);
		}
		else
		{
			currentLayer.setColor (color);
		}

	}

	public final Color getColor ()
	{
		return (currentLayer == null) ? bufferGraphics.getColor ()
				: currentLayer.color;
	}

	public final void setFont (Font font)
	{
		if (currentLayer == null)
		{
			bufferGraphics.setFont (font);
		}
		else
		{
			currentLayer.setFont (font);
		}

	}

	public final void drawLine (int v1, int v2)
	{
		drawLine (v1, v2, false);
	}

	private final Point lineStart = new Point (), lineEnd = new Point ();

	public final void drawLine (int v1, int v2, boolean accentuated)
	{
		if (justFitCamera)
		{
			return;
		}
		checkRepaintWrapException ();
		float z1 = cz[v1], z2 = cz[v2];
		if (z1 < z2)
		{
			if (!((View3D) getView ()).getCanvasCamera ().projectLine (cx[v2],
				cy[v2], z2, cx[v1], cy[v1], z1, lineStart, lineEnd))
			{
				return;
			}
		}
		else
		{
			if (!((View3D) getView ()).getCanvasCamera ().projectLine (cx[v1],
				cy[v1], z1, cx[v2], cy[v2], z2, lineStart, lineEnd))
			{
				return;
			}
		}
		Color c = null;
		if (accentuated)
		{
			c = getColor ();
			setColor (Color.white);
		}
		int xc1, yc1, xc2, yc2;
		drawLine (xc1 = lineStart.x, yc1 = lineStart.y, xc2 = lineEnd.x,
			yc2 = lineEnd.y);
		if (accentuated)
		{
			setColor (c);
			int dx = xc2 - xc1, dy = yc2 - yc1, i = dx * dx + dy * dy;
			if (i > 2)
			{
				float t = (float) (1.2 / Math.sqrt (i));
				dx = Math.round (t * dx);
				dy = Math.round (t * dy);
				drawLine (xc1 + dy, yc1 - dx, xc2 + dy, yc2 - dx);
				drawLine (xc1 - dy, yc1 + dx, xc2 - dy, yc2 + dx);
			}
		}
	}

	private void drawLine (int x1, int y1, int x2, int y2)
	{
		if (currentLayer == null)
		{
			bufferGraphics.drawLine (x1, y1, x2, y2);
		}
		else
		{
			currentLayer.putInt (LINE, x1, y1, x2, y2);
		}
	}

	private final Point2f retPoint = new Point2f ();

	private boolean getXY (int vertex)
	{
		if (vertex < 0)
		{
			retPoint.x = 0;
			retPoint.y = 0;
			return true;
		}
		if (((View3D) getView ()).getCanvasCamera ().projectView (cx[vertex],
			cy[vertex], cz[vertex], retPoint, true) != CanvasCamera.INSIDE_CLIPPING)
		{
			return false;
		}
		return true;
	}

	public final void drawString (int vertex, int dx, int dy, String s)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				bufferGraphics.drawString (s, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy));
			}
			else
			{
				currentLayer.putInt (STRING, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy));
				currentLayer.putObject (s);
			}
		}
	}

	public final void drawRect (int vertex, int dx, int dy, int width,
			int height)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				bufferGraphics.drawRect ((int) (retPoint.x + dx),
					(int) (retPoint.y + dy), width, height);
			}
			else
			{
				currentLayer.putInt (DRAW_RECT, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy), width, height);
			}
		}
	}

	public final void fillRect (int vertex, int dx, int dy, int width,
			int height)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				bufferGraphics.fillRect ((int) (retPoint.x + dx),
					(int) (retPoint.y + dy), width, height);
			}
			else
			{
				currentLayer.putInt (FILL_RECT, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy), width, height);
			}
		}
	}

	public final void drawPolygon (int vertex, Polygon polygon)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				bufferGraphics.translate ((int) retPoint.x, (int) retPoint.y);
				bufferGraphics.drawPolygon (polygon);
				bufferGraphics.translate (-(int) retPoint.x, -(int) retPoint.y);
			}
			else
			{
				currentLayer.putInt (DRAW_POLY, (int) retPoint.x,
					(int) retPoint.y);
				currentLayer.putObject (polygon);
			}
		}
	}

	public final void fillPolygon (int vertex, Polygon polygon)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				bufferGraphics.translate ((int) retPoint.x, (int) retPoint.y);
				bufferGraphics.fillPolygon (polygon);
				bufferGraphics.translate (-(int) retPoint.x, -(int) retPoint.y);
			}
			else
			{
				currentLayer.putInt (FILL_POLY, (int) retPoint.x,
					(int) retPoint.y);
				currentLayer.putObject (polygon);
			}
		}
	}

	public final void drawIcon (int vertex, int dx, int dy, Icon icon)
	{
		checkRepaintWrapException ();
		if (getXY (vertex))
		{
			if (currentLayer == null)
			{
				icon.paintIcon (null, bufferGraphics, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy));
			}
			else
			{
				currentLayer.putInt (ICON, (int) (retPoint.x + dx),
					(int) (retPoint.y + dy));
				currentLayer.putObject (icon);
			}
		}
	}

	public final void openLayer (int layer)
	{
		if (lsSize == layerStack.length)
		{
			System.arraycopy (layerStack, 0,
				layerStack = new Layer[lsSize + 10], 0, lsSize);
		}
		layerStack[lsSize++] = currentLayer;
		if (layer == OVERLAY)
		{
			layer = layers.length;
		}
		else if (layer >= layers.length)
		{
			layer = layers.length - 1;
		}
		currentLayer = (layer == 0) ? null : layers[layer - 1];
	}

	public final void closeLayer ()
	{
		currentLayer = layerStack[--lsSize];
	}

	protected void drawOverlay (int type)
	{
		throw new Error ("Unknown overlay code " + type);
	}

	private void ensureVertexSize (int size)
	{
		int n = cx.length;
		if (n < size)
		{
			size = (size * 3) >> 1;
			System.arraycopy (cx, 0, cx = new float[size], 0, n);
			System.arraycopy (cy, 0, cy = new float[size], 0, n);
			System.arraycopy (cz, 0, cz = new float[size], 0, n);
		}
	}

	public final void setVertex (int index, Tuple3d v, Matrix4d t)
	{
		q0.set (v);
		t.transform (q0);
		setVertex (index, (float) q0.x, (float) q0.y, (float) q0.z);
	}

	public final void setVertex (int index, Tuple3f v, Matrix4d t)
	{
		q0f.set (v);
		t.transform (q0f);
		setVertex (index, q0f.x, q0f.y, q0f.z);
	}

	public final void setVertex (int index, Tuple3d v)
	{
		setVertex (index, (float) v.x, (float) v.y, (float) v.z);
	}

	public final void setVertex (int index, Tuple3f v)
	{
		setVertex (index, v.x, v.y, v.z);
	}

	public final void setVertex (int index, float x, float y, float z)
	{
		ensureVertexSize (index + 1);
		cx[index] = x;
		cy[index] = y;
		cz[index] = z;

		if (!computeMinMax)
		{
			return;
		}

		if (x < minX)
		{
			minX = x;
		}
		else if (x > maxX)
		{
			maxX = x;
		}

		if (y < minY)
		{
			minY = y;
		}
		else if (y > maxY)
		{
			maxY = y;
		}

		if (z < minZ)
		{
			minZ = z;
		}
		else if (z > maxZ)
		{
			maxZ = z;
		}
	}

	public final void setVertex (int index, double x, double y, double z,
			Matrix4d t)
	{
		q0.set (x, y, z);
		t.transform (q0);
		setVertex (index, (float) q0.x, (float) q0.y, (float) q0.z);
	}

	@Override
	public Pool getPool ()
	{
		return pool;
	}

	@Override
	public Shader getCurrentShader ()
	{
		return visitor.getCurrentShader ();
	}

	@Override
	public int getCurrentHighlight ()
	{
		return visitor.selectionState;
	}

	@Override
	public float estimateScaleAt (Tuple3f point)
	{
		return getView3D ().estimateScaleAt (point,
			visitor.getCurrentTransformation ());
	}

	private final Matrix4d xform = new TMatrix4d ();

	@Override
	public void drawPoint (Tuple3f origin, int size, Tuple3f color,
			int highlight, Matrix4d t)
	{
		// check if drawing points is enabled
		if (optionShowPoints)
		{
			if (highlight == CURRENT_HIGHLIGHT)
			{
				highlight = visitor.selectionState;
			}
			if (t == null)
			{
				t = visitor.getCurrentTransformation ();
			}
			else
			{
				Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
				t = xform;
			}
			if (origin != null)
			{
				setVertex (0, origin, t);
			}
			else
			{
				setVertex (0, (float) t.m03, (float) t.m13, (float) t.m23);
			}
			openLayer (1);
			setColor (color, highlight, true);
			fillRect (0, size / -2, size / -2, size + 1, size + 1);
			closeLayer ();
		}
	}

	/* (non-Javadoc)
	 * @see de.grogra.imp3d.RenderState#drawPointCloud(float[], javax.vecmath.Tuple3f, int, javax.vecmath.Matrix4d)
	 */
	@Override
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

	@Override
	public void drawLine (Tuple3f origin, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t)
	{
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		if (origin != null)
		{
			setVertex (0, origin, t);
			setVertex (1, end, t);
		}
		else
		{
			setVertex (0, (float) t.m03, (float) t.m13, (float) t.m23);
			setVertex (1, end, t);
		}
		setColor (color, highlight, false);
		drawLine (0, 1, (highlight & ViewSelection.SELECTED) != 0);
	}

	@Override
	public void drawPlane (Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		boolean selected = (highlight & de.grogra.imp.edit.ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		int i, k;
		float c;
		int n = 10 * (getGlobalLOD () - View.LOD_MIN)
			/ (View.LOD_MAX - View.LOD_MIN) + 1;
		Point3d p0 = pool.q3d0, p1 = pool.p3d1;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1;
		c = 1000 / getView3D ().estimateScaleAt (0, 0, 0, t);
		v0.x = t.m00 * c;
		v0.y = t.m10 * c;
		v0.z = t.m20 * c;
		v1.x = t.m01 * c;
		v1.y = t.m11 * c;
		v1.z = t.m21 * c;
		p0.x = t.m03 - 0.5 * (v0.x + v1.x);
		p0.y = t.m13 - 0.5 * (v0.y + v1.y);
		p0.z = t.m23 - 0.5 * (v0.z + v1.z);
		k = 0;
		c = 1f / n;

		boolean cmm = computeMinMax;
		computeMinMax = false;

		for (i = 0; i <= n; i++)
		{
			p1.scaleAdd (i * c, v0, p0);
			setVertex (k++, p1);
			p1.add (v1);
			setVertex (k, p1);
			drawLine (k - 1, k++, selected);
		}
		for (i = 1; i < n; i++)
		{
			p1.scaleAdd (i * c, v1, p0);
			setVertex (k++, p1);
			p1.add (v0);
			setVertex (k, p1);
			drawLine (k - 1, k++, selected);
		}
		drawLine (0, 2 * n, selected);
		drawLine (1, 2 * n + 1, selected);

		computeMinMax = cmm;
	}

	@Override
	public void drawBox (float halfWidth, float halfLength, float height,
			Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		double z = 0;
		for (int i = 0; i <= 4; i += 4)
		{
			setVertex (i, -halfWidth, -halfLength, z, t);
			setVertex (i + 1, halfWidth, -halfLength, z, t);
			setVertex (i + 2, halfWidth, halfLength, z, t);
			setVertex (i + 3, -halfWidth, halfLength, z, t);
			for (int j = 0; j < 4; j++)
			{
				drawLine (i + j, i + ((j + 1) & 3), selected);
				if (i > 0)
				{
					drawLine (j, i + j, selected);
				}
			}
			z = height;
		}
	}

	@Override
	public void drawLamella (float halfWidth, float halfLength, float height, float a, float b,
			Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		double z = 0;
		for (int i = 0; i <= 4; i += 4)
		{
			setVertex (i, -halfWidth, -halfLength, z, t);
			setVertex (i + 1, halfWidth, -halfLength, z, t);
			setVertex (i + 2, halfWidth, halfLength, z, t);
			setVertex (i + 3, -halfWidth, halfLength, z, t);
			for (int j = 0; j < 4; j++)
			{
				drawLine (i + j, i + ((j + 1) & 3), selected);
				if (i > 0)
				{
					drawLine (j, i + j, selected);
				}
			}
			z = height;
		}
		
		int lod = getGlobalLOD ();
		int uCount = 5 + 25 * lod;
		int resolution = 25*uCount;
		float dx = (2*halfWidth) / resolution;
		int i = 16;
		for (int u = 0; u <= resolution; u++) {
			float sinPhi = (float) (a* Math.sin (b*u));
			setVertex(i  , -halfWidth+u*dx, -halfLength, z + sinPhi-a,t);
			setVertex(i+1, -halfWidth+u*dx,  halfLength, z + sinPhi-a,t);
			drawLine (i, i+1, selected);
			i+=2;
		}
	}

	@Override
	public void drawSphere (float radius, Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		int lod = getGlobalLOD ();
		if (lod <= View.LOD_MIN)
		{
			setColor (sh.getAverageColor ());
			setVertex (0, 0, 0, 0, t);
			fillRect (0, 0, 0, 2, 2);
			return;
		}
		int n, i, j, k;
		if (lod <= View.LOD_MIN + 1)
		{
			n = 4;
		}
		else
		{
			n = ((int) (getView3D ().estimateScaleAt (0, 0, 0, t) * radius) / 20 + 2) * 2;
			if (lod >= View.LOD_MAX)
			{
				n *= 2;
			}
			if (n > 32)
			{
				n = 32;
			}
			else if (n < 4)
			{
				n = 4;
			}
		}

		double c, s, dPhi = (2d * Math.PI / n), dx, dy, dz, r, z;
		Point3d p0 = pool.q3d0;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1, v2 = pool.w3d2;

		v0.set (0, 0, radius);
		de.grogra.vecmath.Math2.getOrthogonal (v0, v1);
		c = v0.length ();
		v1.scale (c / v1.length ());
		v2.cross (v0, v1);
		v2.scale (1 / c);
		p0.set (0, 0, 0);
		t.transform (v0);
		t.transform (v1);
		t.transform (v2);
		t.transform (p0);
		k = 0;
		for (i = 0; i < n; i++)
		{
			c = Math.cos (i * dPhi);
			s = Math.sin (i * dPhi);
			dx = c * v1.x + s * v2.x;
			dy = c * v1.y + s * v2.y;
			dz = c * v1.z + s * v2.z;
			for (j = n / 2; j >= 0; j--)
			{
				r = Math.sin (j * dPhi);
				z = Math.cos (j * dPhi);
				setVertex (k++, (float) (p0.x + r * dx + z * v0.x),
					(float) (p0.y + r * dy + z * v0.y),
					(float) (p0.z + r * dz + z * v0.z));
			}
		}
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		k = 0;
		for (i = n - 1; i >= 0; i--)
		{
			for (j = 0; j < n / 2; j++)
			{
				drawLine (k, k + 1, selected);
				if (j > 0)
				{
					if (i > 0)
					{
						drawLine (k, k + n / 2 + 1, selected);
					}
					else
					{
						drawLine (k, j, selected);
					}
				}
				k++;
			}
			k++;
		}
	}
	
	/**
	 * Generate a vertex on a unit sphere segment (radius is 1.0f). The angle phi
	 * selects the direction in the x/y-plane, if the angle is zero the
	 * direction is (1/0/0). The angle theta then selects the direction in the
	 * plane made of the direction and the z-axis. Possible angles for phi are
	 * -pi to +pi and for theta are -pi/2 to +pi/2.
	 * 
	 * @param phi
	 * @param theta
	 * @return
	 */
	private Vector3f genVertexSphereSegmentSolid (float radius, float phi, float theta) {
		float x = (float)(radius*Math.cos (phi) * Math.cos (theta));
		float y = (float)(radius*Math.sin (phi) * Math.cos (theta));
		float z = (float)(radius*Math.sin (theta));
		return new Vector3f (x, y, z);
	}

	@Override
	public void drawSphereSegmentSolid (float radius, float theta1M, float theta2M, float phiM, Shader s, int highlight, boolean asWireframe, Matrix4d t) {
		if (s == null)
		{
			s = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		
		int lod = getGlobalLOD ();
		// calculate segment count depending on level of detail
		final int uCount = 8 + (int) ((phiM/Math.PI)*24 * lod);
		final int vCount = uCount;
		int k = 0;
		
		float delta = (float)Math.PI;
		if(theta1M>0 & theta2M>=0) {
			delta = theta1M-theta2M;
		}
		if(theta1M>0 & theta2M<0) {
			delta = theta1M+Math.abs(theta2M);
		}
		if(theta1M<=0 & theta2M<0) {
			delta = Math.abs(theta2M)-Math.abs(theta1M);
		}

		float theta1,theta2, phi;
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		final Point3f p = new Point3f();

		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		
		// for each strip
		for (int v = 0; v < 2*vCount; v += 2) {
			theta1 = theta2M + delta * (v + 2) / vCount / 2.0f;
			theta2 = theta2M + delta * v / vCount / 2.0f;

			// mantel
			for (int u = 0; u <= uCount; u++) {
				// calculate next vertex
				phi = phiM * 2 * u / uCount;
				v1 = genVertexSphereSegmentSolid (radius, phi, theta1);
				v2 = genVertexSphereSegmentSolid (radius, phi, theta2);
				
				p.set(v1);
				t.transform(p);
				setVertex (k, p);
				p.set(v2);
				t.transform(p);
				setVertex (k+1, p);
				
				//longitude
				drawLine (k, k+1, selected);
				
				if(u>0) {
					//lattitude
					drawLine (k, k-2, selected);
					if(v==0) {
						drawLine (k+1, k-1, selected);
					}
				}
				
				//bottom 'N top
				if(v==2*vCount-2) {
					p.set(0,0,v1.z);
					t.transform(p);
					setVertex (k+1, p);
					drawLine (k, k+1, selected);
				}
				if(v==0) {
					p.set(0,0,v2.z);
					t.transform(p);
					setVertex (k+2, p);
					drawLine (k+1, k+2, selected);
				}
				
				k+=2;
			}
			
			//walls
			p.set(0,0,v1.z);
			t.transform(p);
			setVertex (k+1, p);
			drawLine (k+1, k-2*vCount-1, selected);
			drawLine (k+1, k-2, selected);
			p.set(0,0,v2.z);
			t.transform(p);
			setVertex (k+2, p);
			drawLine (k+1, k+2, selected);
			k+=2;
		}
	}

	@Override
	public void drawTextBlock(String caption, Font font, float depth, Shader s, int highlight, boolean asWireframe, Matrix4d t) {}

	/**
	 * Generate a vertex on a supershape. The angle phi
	 * selects the direction in the x/y-plane, if the angle is zero the
	 * direction is (1/0/0). The angle theta then selects the direction in the
	 * plane made of the direction and the z-axis. Possible angles for φ varies 
	 * between -π/2 and π/2 (latitude) and θ between -π and π (longitude).
	 * 
	 * @param phi
	 * @param theta
	 * @return
	 */
    private Vector3f genVertexSupershape(Vector3f out,
    		float theta, float phi, float a, float b, 
    		float m1, float n11, float n12, float n13, 
    		float m2, float n21, float n22, float n23)
    {        
		float raux1 = (float)(Math.pow(Math.abs(Math.cos(m1*theta/4d)/a ), n12) + 
							  Math.pow(Math.abs(Math.sin(m1*theta/4d)/b ), n13));
		float raux2 = (float)(Math.pow(Math.abs(Math.cos(m2*phi/4d)/a ), n22) + 
							  Math.pow(Math.abs(Math.sin(m2*phi/4d)/b ), n23));
		
		float r1 = (float)Math.pow(Math.abs(raux1), -1f/n11);
		float r2 = (float)Math.pow(Math.abs(raux2), -1f/n21);
                
        float x = (float)(r1*Math.cos(theta) * r2*Math.cos(phi));
	    float y = (float)(r1*Math.sin(theta) * r2*Math.cos(phi));
	    float z = (float)(r2*Math.sin(phi));
	    
	    out.set(x, y, z);
        return out;
    }
	
	/**
	 * Draw a supershape around the origin (0/0/0).
	 * 
	 * An implementation of Johan Gielis's Superformula which was published in the
	 * American Journal of Botany 90(3): 333–338. 2003.
     * INVITED SPECIAL PAPER A GENERIC GEOMETRIC TRANSFORMATION 
     * THAT UNIFIES A WIDE RANGE OF NATURAL AND ABSTRACT SHAPES
     *      
     * @param a, b length of curves 
     * @param m, n shape parameters
     * @param shader
     * @param highlight
	 * @param t transformation of the point cloud
	 */    
	@Override
	public void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		int lod = getGlobalLOD ();
		// calculate segment count depending on level of detail
		float factor = (m1+m2)/2f;
		final int uvCount = (int)(factor + factor*3 * lod);
		int k = 0;
		
		float theta, phi;
		final Vector3f v1 = new Vector3f();
		final Point3f p = new Point3f();
		
		// for each strip
		for (int u = 0; u < uvCount; u++)
		{
			theta = -M_PI + M_2PI * u / uvCount;
			// for each vertical slice
			for (int v = 0; v <= uvCount; v++)
			{
				// calculate next vertex
				phi = -M_PI_2 + M_PI * v / uvCount;
				genVertexSupershape (v1, theta, phi, a, b, m1, n11, n12, n13, m2, n21, n22, n23);
				p.set(v1);
				t.transform(p);
				setVertex (k++, p);
			}
		}		
		
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		final int delta = uvCount + 1;
		k = 0;
		for (int v = 0; v < uvCount; v++)
		{
			// longitude				
			drawLine (k, ++k, selected);
			// latitude
			drawLine ((uvCount-1)*delta+v, v, selected);
		}
		k++;
		for (int u = 1; u < uvCount; u++)
		{
			int base = (u-1) * (uvCount+1);
			for (int v = 0; v < uvCount; v++)
			{
				// longitude				
				drawLine (k, ++k, selected);
				// latitude
				drawLine (base + v, base + delta + v, selected);
			}
			k++;
		}
	}
	

	@Override
	public void drawFrustum (float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader sh,
			int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		int lod = getGlobalLOD ();
		if ((lod <= View.LOD_MIN)
			|| (Math.abs (baseRadius) + Math.abs (topRadius) < getView ()
				.getEpsilon ()))
		{
			setColor (sh.getAverageColor ());
			setVertex (0, 0, 0, 0, t);
			setVertex (1, 0, 0, height, t);
			drawLine (0, 1, false);
			return;
		}
		int n, i, k;
		if (lod <= View.LOD_MIN + 1)
		{
			n = 4;
		}
		else
		{
			n = ((int) (getView3D ().estimateScaleAt (0, 0, 0, t) * Math.max (
				Math.max (baseRadius, topRadius), Math.abs (height))) / 20 + 2) * 2;
			if (lod >= View.LOD_MAX)
			{
				n *= 2;
			}
			if (n > 16)
			{
				n = 16;
			}
			else if (n < 4)
			{
				n = 4;
			}
		}
		double c, s, dPhi = (2d * Math.PI / n), dx, dy, dz;
		Point3d p0 = pool.q3d0;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1, v2 = pool.w3d2;

		p0.set (0, 0, 0);
		v0.set (0, 0, height);
		Math2.getOrthogonal (v0, v1);
		v1.normalize ();
		v2.cross (v0, v1);
		v2.normalize ();
		t.transform (v0);
		t.transform (v1);
		t.transform (v2);
		t.transform (p0);
		v0.add (p0);
		k = 0;
		for (i = 0; i < n; i++)
		{
			c = Math.cos (i * dPhi);
			s = Math.sin (i * dPhi);
			dx = c * v1.x + s * v2.x;
			dy = c * v1.y + s * v2.y;
			dz = c * v1.z + s * v2.z;
			setVertex (k++, (float) (p0.x + baseRadius * dx),
				(float) (p0.y + baseRadius * dy), (float) (p0.z + baseRadius
					* dz));
			setVertex (k++, (float) (v0.x + topRadius * dx),
				(float) (v0.y + topRadius * dy),
				(float) (v0.z + topRadius * dz));
		}
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		if (baseClosed)
		{
			for (i = 0; i < n; i += 2)
			{
				drawLine (i, i + n, selected);
			}
		}
		if (topClosed)
		{
			for (i = 1; i < n; i += 2)
			{
				drawLine (i, i + n, selected);
			}
		}
		n = 2 * n - 2;
		for (i = 0; i < n; i += 2)
		{
			drawLine (i, i + 1, selected);
			drawLine (i, i + 2, selected);
			drawLine (i + 1, i + 3, selected);
		}
		drawLine (n, n + 1, selected);
		drawLine (n, 0, selected);
		drawLine (n + 1, 1, selected);
	}

	@Override
	public void drawParallelogram (float height, Vector3f secondAxis,
			float scaleU, float scaleV, Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		boolean selected = (highlight & de.grogra.imp.edit.ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		int lod = getGlobalLOD ();
		int n, n2, i, k;
		float c;
		if (lod <= View.LOD_MIN + 1)
		{
			n = 1;
			n2 = 1;
		}
		else
		{
			c = getView3D ().estimateScaleAt (0, 0, 0, t);
			n = ((int) (c * Math.abs (height)) >> 7) + 1;
			n2 = ((int) (c * secondAxis.length ()) >> 7) + 1;
			if (lod >= View.LOD_MAX)
			{
				n *= 2;
				n2 *= 2;
			}
			if (n > 8)
			{
				n = 8;
			}
			if (n2 > 8)
			{
				n2 = 8;
			}
		}
		Point3d p0 = pool.q3d0, p1 = pool.p3d1;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1;
		p0.set (0, 0, 0);
		t.transform (p0);
		v0.set (secondAxis);
		t.transform (v0);
		v1.set (0, 0, height);
		t.transform (v1);
		k = 0;
		c = 1f / n;
		for (i = -n; i <= n; i++)
		{
			p1.scaleAdd (i * c, v0, p0);
			setVertex (k++, p1);
			p1.add (v1);
			setVertex (k, p1);
			drawLine (k - 1, k++, selected);
		}
		n2 <<= 1;
		c = 1f / n2;
		for (i = 1; i < n2; i++)
		{
			p1.scaleAdd (i * c, v1, p0);
			p1.sub (v0);
			setVertex (k++, p1);
			p1.scaleAdd (2, v0, p1);
			setVertex (k, p1);
			drawLine (k - 1, k++, selected);
		}
		drawLine (0, 4 * n, selected);
		drawLine (1, 4 * n + 1, selected);
	}

	@Override
	public void drawPolygons (Polygonizable pz, Object obj, boolean asNode, Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if ((polyCache != null) && (polyCache.getGraphState () != getRenderGraphState ()))
		{
			polyCache.clear ();
			polyCache = null;
		}
		if (polyCache == null)
		{
			polyCache = new PolygonizationCache (getRenderGraphState (), 0, 100, false);
		}
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		PolygonArray polys = polyCache.get (obj, asNode, pz);
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		float[] data = polys.vertices.elements;
		int dim = polys.dimension;
		/*
		 int kk = polys.vertices.size / dim;
		 Vector3f normal = new Vector3f ();
		 float[] ff = new float[3];
		 //*/
		for (int n = polys.vertices.size / dim - 1; n >= 0; n--)
		{
			int v = n * dim;
			setVertex (n, data[v], (dim > 1) ? data[v + 1] : 0,
				(dim > 2) ? data[v + 2] : 0, t);
			/*
			 polys.getNormal (ff, n);
			 normal.set (ff);
			 normal.normalize ();
			 normal.scale (0.3f);
			 normal.x += data[v];
			 normal.y += data[v+1];
			 normal.z += data[v+2];
			 setVertex (kk, normal, transformation);
			 drawLine (n, kk);
			 //*/
		}
		if (justFitCamera)
		{
			return;
		}
		int[] polygons = polys.polygons.elements;
		int n = polys.polygons.size;
		int i = -1;
		while (++i < n)
		{
			int firstVertex = polygons[i];
			int lastVertex = firstVertex;
			for (int k = polys.edgeCount; k > 1; k--)
			{
				drawLine (lastVertex,
					lastVertex = polygons[++i], selected);
			}
			drawLine (lastVertex, firstVertex, selected);
		}
	}

	void drawNURBSUniformly (NURBSSurface s, boolean asNode, Shader sh,
			int highlight, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		boolean selected = (highlight & de.grogra.imp.edit.ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);

		int lod = getGlobalLOD ();
		lod = (lod <= View.LOD_MIN) ? 4 : (lod <= View.LOD_MIN + 1) ? 2 : 1;

		Color c = bufferGraphics.getColor ();
		int vv;
		if (c.getGreen () > 1.2f * Math.max (c.getRed (), c.getBlue ()))
		{
			vv = 8;
			if (antialiasing)
			{
				((Graphics2D) bufferGraphics).setStroke (new BasicStroke (
					3 * lineWidth));
			}
		}
		else
		{
			vv = 3;
		}
		GraphState gs = getRenderGraphState ();
		gs.setObjectContext (s, asNode);
		int un = Math.max (Math.round (3 / (lod * s.getFlatness ())), 2), vn = Math
			.max (Math.round (vv / (lod * s.getFlatness ())), 2);
		BSplineSurface surface = s.getSurface ();
		if (!BSpline.isValid (surface, gs))
		{
			return;
		}
		float umin = surface.getKnot (0, surface.getUDegree (gs), gs), uf = (surface
			.getKnot (0, surface.getUSize (gs), gs) - umin)
			/ un;
		float vmin = surface.getKnot (1, surface.getVDegree (gs), gs), vf = (surface
			.getKnot (1, surface.getVSize (gs), gs) - vmin)
			/ vn;
		int dim = surface.getDimension (gs);
		float[] p = pool.getFloatArray (0, dim);
		int wi = surface.isRational (gs) ? dim - 1 : -1;
		int k = 0, kPrev;
		for (int u = 0; u <= un; u++)
		{
			kPrev = k;
			k = (u & 1) * (vn + 1);
			for (int v = 0; v <= vn; v++)
			{
				BSpline.evaluate (p, surface, umin + u * uf, vmin + v * vf, gs);
				if (wi > 0)
				{
					float w = 1 / p[wi];
					p[wi] = 0;
					setVertex (k + v, w * p[0], w * p[1], w * p[2], t);
				}
				else
				{
					setVertex (k + v, p[0], p[1], p[2], t);
				}
			}
			for (int v = 0; v <= vn; v++)
			{
				int i = k + v;
				if (u > 0)
				{
					drawLine (i, kPrev + v, selected);
				}
				if (v > 0)
				{
					drawLine (i, i - 1, selected);
				}
			}
		}
		if (antialiasing)
		{
			((Graphics2D) bufferGraphics)
				.setStroke (new BasicStroke (lineWidth));
		}
	}

	@Override
	public boolean getWindowPos (Tuple3f origin, Tuple2f out)
	{
		Matrix4d m = visitor.getCurrentTransformation ();
		return getView3D ()
			.getCanvasCamera ()
			.projectView (
				(float) (origin.x * m.m00 + origin.y * m.m01 + origin.z * m.m02 + m.m03),
				(float) (origin.x * m.m10 + origin.y * m.m11 + origin.z * m.m12 + m.m13),
				(float) (origin.x * m.m20 + origin.y * m.m21 + origin.z * m.m22 + m.m23),
				out, true) == CanvasCamera.INSIDE_CLIPPING;
	}

	@Override
	public void drawRectangle (int x, int y, int w, int h, Tuple3f color)
	{
		openLayer (1);
		setColor (color);
		drawRect (-1, x, y, w, h);
		closeLayer ();
	}

	@Override
	public void fillRectangle (int x, int y, int w, int h, Tuple3f color)
	{
		openLayer (1);
		setColor (color);
		fillRect (-1, x, y, w, h);
		closeLayer ();
	}

	@Override
	public void drawString (int x, int y, String text, Font font, Tuple3f color)
	{
		openLayer (1);
		setColor (color);
		setFont (font);
		drawString (-1, x, y, text);
		closeLayer ();
	}

	@Override
	protected void optionValueChanged (String name, Object value)
	{
		// System.out.println (name + " = " + value);

		// check if "Show Points" changed
		if (OPTION_NAME_SHOW_POINTS.equals (name))
		{
			optionShowPoints = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if ("lineWidth".equals (name))
		{
			lineWidth = ((Number) value).floatValue ();
			repaint (ALL);
		}
		else if (OPTION_NAME_SHOW_GRID.equals (name))
		{
			optionShowGrid = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_GRID_DIMENSION.equals (name))
		{
			optionGridDimension = (Integer) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_GRID_SPACING.equals (name))
		{
			optionGridSpacing = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_GRID_COLOR_R.equals (name))
		{
			optionGridColorR = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_GRID_COLOR_G.equals (name))
		{
			optionGridColorG = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_GRID_COLOR_B.equals (name))
		{
			optionGridColorB = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_BACKGROUND_COLOR_R.equals (name))
		{
			optionBackgroundColorR = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_BACKGROUND_COLOR_G.equals (name))
		{
			optionBackgroundColorG = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_BACKGROUND_COLOR_B.equals (name))
		{
			optionBackgroundColorB = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_BACKGROUND_ALPHA.equals (name))
		{
			optionBackgroundAlpha = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_SHOW_AXES.equals (name))
		{
			optionShowAxes = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_SHOW_AXES_NAMES.equals (name))
		{
			optionShowAxesNames = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_SHOW_DISPLAY_SIZE.equals (name))
		{
			optionShowDisplaySize = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if (OPTION_SPLIT_VIEW_SIZE.equals (name))
		{
			optionSplitViewSize = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_STEREO_VIEW.equals (name))
		{
			optionStereoView = Boolean.TRUE.equals (value);
			repaint (ALL);
		}		
		else if (OPTION_ANAGLYPH_VIEW.equals (name))
		{
			optionAnaglyphView = Boolean.TRUE.equals (value);
			repaint (ALL);
		}		
		else if (OPTION_EYE_SEPARATION.equals (name))
		{
			optionEyeSeparation = (Float) (value);
			repaint (ALL);
		}
		else if (OPTION_SHOW_PREVIEW1.equals (name) || OPTION_SHOW_PREVIEW2.equals (name))
		{
			repaint (ALL);
		}
		else
		{
			super.optionValueChanged (name, value);
		}
	}

	private void drawGrid() {
		Color3f gridColor = new Color3f (optionGridColorR, optionGridColorG, optionGridColorB);
		float gridSize = optionGridDimension * optionGridSpacing;
		for (float i = -gridSize; i <= gridSize; i+=optionGridSpacing) {
			for (float j = -gridSize; j <= gridSize; j+=optionGridSpacing) {
				drawLine (new Point3f(i,j,0), new Point3f(-i,j,0), gridColor, 0, null);
				drawLine (new Point3f(i,j,0), new Point3f(i,-j,0), gridColor, 0, null);
			}
		}		
	}
	
	private void drawGridMainView() {
		Color3f gridColor = new Color3f (optionGridColorR, optionGridColorG, optionGridColorB);
		float gridSize = optionGridDimension * optionGridSpacing;
		for (float i = -gridSize; i <= gridSize; i+=optionGridSpacing) {
			for (float j = -gridSize; j <= gridSize; j+=optionGridSpacing) {
				drawLine (new Point3f(i,j,0), new Point3f(-i,j,0), gridColor, 0, null);
				drawLine (new Point3f(i,j,0), new Point3f(i,-j,0), gridColor, 0, null);
			}
		}		
	}
	
	private void drawAxes() {
		Color3f axesColorR = new Color3f (1,0,0);
		Color3f axesColorG = new Color3f (0,1,0);
		Color3f axesColorB = new Color3f (0,0,1);
		
		// calculate view rotation
		Matrix4d mm = new Matrix4d ();
		Camera c = ((View3D) getView ()).getCamera ();
		mm = c.getWorldToViewTransformation();
		
		// set rotation to axes scene
		AxisAngle4d rot = new AxisAngle4d();
		rot.set(mm);

		Matrix4d m = new Matrix4d();
		m.setIdentity();
		m.set(rot);
		int dist = 50; // distance from canvas border
		
		// x-axis
		setColor (axesColorR, 0, false);
		double[] axisX = new double[]{25, 0, 0};
		double[] newAxisX = new double[]{
				m.m00*axisX[0] + m.m01*axisX[1] + m.m02*axisX[2],
				m.m10*axisX[0] + m.m11*axisX[1] + m.m12*axisX[2],
				m.m20*axisX[0] + m.m21*axisX[1] + m.m22*axisX[2]
		};
		drawLine(dist, dist, dist + (int) newAxisX[0], dist - (int) newAxisX[1]);
		
		// y-axis
		setColor (axesColorB, 0, false);
		double[] axisY = new double[]{0, 25, 0};
		double[] newAxisY = new double[]{
				m.m00*axisY[0] + m.m01*axisY[1] + m.m02*axisY[2],
				m.m10*axisY[0] + m.m11*axisY[1] + m.m12*axisY[2],
				m.m20*axisY[0] + m.m21*axisY[1] + m.m22*axisY[2]
		};
		drawLine(dist, dist, dist + (int) newAxisY[0], dist - (int) newAxisY[1]);
		
		// z-axis
		setColor (axesColorG, 0, false);
		double[] axisZ = new double[]{0, 0, 25};
		double[] newAxisZ = new double[]{
				m.m00*axisZ[0] + m.m01*axisZ[1] + m.m02*axisZ[2],
				m.m10*axisZ[0] + m.m11*axisZ[1] + m.m12*axisZ[2],
				m.m20*axisZ[0] + m.m21*axisZ[1] + m.m22*axisZ[2]
		};
		drawLine(dist, dist, dist + (int) newAxisZ[0], dist - (int) newAxisZ[1]);

		if (optionShowAxesNames) {
			// x-axis text
			double[] axisXFont = new double[]{31, 0, 0};
			double[] newAxisXFont = new double[]{
					m.m00*axisXFont[0] + m.m01*axisXFont[1] + m.m02*axisXFont[2],
					m.m10*axisXFont[0] + m.m11*axisXFont[1] + m.m12*axisXFont[2],
					m.m20*axisXFont[0] + m.m21*axisXFont[1] + m.m22*axisXFont[2]
			};		
			Font font = FontAdapter.getFont(null);
			drawString(dist + (int) newAxisXFont[0] - 3, dist - (int) newAxisXFont[1] + 4, "x", font, axesColorR);
	
			// y-axis text
			double[] axisYFont = new double[]{0, 31, 0};
			double[] newAxisYFont = new double[]{
					m.m00*axisYFont[0] + m.m01*axisYFont[1] + m.m02*axisYFont[2],
					m.m10*axisYFont[0] + m.m11*axisYFont[1] + m.m12*axisYFont[2],
					m.m20*axisYFont[0] + m.m21*axisYFont[1] + m.m22*axisYFont[2]
			};
			drawString(dist + (int) newAxisYFont[0] - 3, dist - (int) newAxisYFont[1] + 4, "y", font, axesColorB);
			
			// z-axis text
			double[] axisZFont = new double[]{0, 0, 31};
			double[] newAxisZFont = new double[]{
					m.m00*axisZFont[0] + m.m01*axisZFont[1] + m.m02*axisZFont[2],
					m.m10*axisZFont[0] + m.m11*axisZFont[1] + m.m12*axisZFont[2],
					m.m20*axisZFont[0] + m.m21*axisZFont[1] + m.m22*axisZFont[2]
			};
			drawString(dist + (int) newAxisZFont[0] - 3, dist - (int) newAxisZFont[1] + 4, "z", font, axesColorG);
		}
	}
	
	private void drawDisplaySize() {
		Component canvas = (Component) getComponent();
		int width = canvas.getWidth();
		int height = canvas.getHeight();
				
		// text
		Color3f color = new Color3f (0,0,0);
		Font font = FontAdapter.getFont(null);
		drawString(5, 15, width + " x " + height, font, color);
	}
	
	@Override
	public void drawFrustumIrregular(float height, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topClosed, 
			float scaleV, Shader sh, int highlight, boolean asWireframe, Matrix4d t)
	{
		if (sh == null)
		{
			sh = visitor.getCurrentShader ();
		}
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		
		/*
		int lod = getGlobalLOD ();
		if ((lod <= View.LOD_MIN)
			|| (Math.abs (baseRadius) + Math.abs (topRadius) < getView ()
				.getEpsilon ()))
		{
			setColor (sh.getAverageColor ());
			setVertex (0, 0, 0, 0, t);
			setVertex (1, 0, 0, height, t);
			drawLine (0, 1, false);
			return;
		}
		*/
		int n, i, k;
		/*
		if (lod <= View.LOD_MIN + 1)
		{
			n = 4;
		}
		else
		{
			n = ((int) (getView3D ().estimateScaleAt (0, 0, 0, t) * Math.max (
				Math.max (baseRadius, topRadius), Math.abs (height))) / 20 + 2) * 2;
			if (lod >= View.LOD_MAX)
			{
				n *= 2;
			}
			if (n > 16)
			{
				n = 16;
			}
			else if (n < 4)
			{
				n = 4;
			}
		}
		*/
		n = sectorCount;
		
		double c, s, dPhi = (2d * Math.PI / n), dx, dy, dz;
		Point3d p0 = pool.q3d0;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1, v2 = pool.w3d2;

		p0.set (0, 0, 0);
		v0.set (0, 0, height);
		Math2.getOrthogonal (v0, v1);
		v1.normalize ();
		v2.cross (v0, v1);
		v2.normalize ();
		t.transform (v0);
		t.transform (v1);
		t.transform (v2);
		t.transform (p0);
		v0.add (p0);
		k = 0;
		for (i = 0; i < n; i++)
		{
			c = Math.cos (i * dPhi);
			s = Math.sin (i * dPhi);
			dx = c * v1.x + s * v2.x;
			dy = c * v1.y + s * v2.y;
			dz = c * v1.z + s * v2.z;
			setVertex (k++, (float) (p0.x + baseRadii[i] * dx),
				(float) (p0.y + baseRadii[i] * dy), (float) (p0.z + baseRadii[i]
					* dz));
			setVertex (k++, (float) (v0.x + topRadii[i] * dx),
				(float) (v0.y + topRadii[i] * dy),
				(float) (v0.z + topRadii[i] * dz));
		}
		boolean selected = (highlight & ViewSelection.SELECTED) != 0;
		setColor (sh.getAverageColor (), highlight, false);
		if (baseClosed)
		{
			for (i = 0; i < n; i += 2)
			{
				drawLine (i, i + n, selected);
			}
		}
		if (topClosed)
		{
			for (i = 1; i < n; i += 2)
			{
				drawLine (i, i + n, selected);
			}
		}
		n = 2 * n - 2;
		for (i = 0; i < n; i += 2)
		{
			drawLine (i, i + 1, selected);
			drawLine (i, i + 2, selected);
			drawLine (i + 1, i + 3, selected);
		}
		drawLine (n, n + 1, selected);
		drawLine (n, 0, selected);
		drawLine (n + 1, 1, selected);
	}

	@Override
	public void drawPrismRectangular (float y, float xPos, float xNeg, float zPos, float zNeg, int highlight, boolean asWireframe, Matrix4d t)
	{
		Shader sh = visitor.getCurrentShader ();
		if (highlight == CURRENT_HIGHLIGHT)
		{
			highlight = visitor.selectionState;
		}
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		
		boolean selected = (highlight & de.grogra.imp.edit.ViewSelection.SELECTED) != 0;
		
		setColor (sh.getAverageColor (), highlight, false);
		
		
		Point3d p0 = pool.q3d0;
		Vector3d v0 = pool.w3d0, v1 = pool.w3d1, v2 = pool.w3d2;

		p0.set (0, 0, 0);
		v0.set (0, 0, y);
		Math2.getOrthogonal (v0, v1);
		v1.normalize ();
		v2.cross (v0, v1);
		v2.normalize ();
		t.transform (v0);
		t.transform (v1);
		t.transform (v2);
		t.transform (p0);
		v0.add (p0);
		
		
		int k = 0;
		//set bottom vertices
		setVertex(k++, (float)(p0.x + v2.x * xNeg + v1.x * zPos), (float)(p0.y + v2.y * xNeg + v1.y * zPos), (float)(p0.z + v2.z * xNeg + v1.z * zPos));
		setVertex(k++, (float)(p0.x + v2.x * xNeg - v1.x * zNeg), (float)(p0.y + v2.y * xNeg - v1.y * zNeg), (float)(p0.z + v2.z * xNeg - v1.z * zNeg));
		setVertex(k++, (float)(p0.x - v2.x * xPos - v1.x * zNeg), (float)(p0.y - v2.y * xPos - v1.y * zNeg), (float)(p0.z - v2.z * xPos - v1.z * zNeg));
		setVertex(k++, (float)(p0.x - v2.x * xPos + v1.x * zPos), (float)(p0.y - v2.y * xPos + v1.y * zPos), (float)(p0.z - v2.z * xPos + v1.z * zPos));

		//set top vertices
		setVertex(k++, (float)(v0.x + v2.x * xNeg + v1.x * zPos), (float)(v0.y + v2.y * xNeg + v1.y * zPos), (float)(v0.z + v2.z * xNeg + v1.z * zPos));
		setVertex(k++, (float)(v0.x + v2.x * xNeg - v1.x * zNeg), (float)(v0.y + v2.y * xNeg - v1.y * zNeg), (float)(v0.z + v2.z * xNeg - v1.z * zNeg));
		setVertex(k++, (float)(v0.x - v2.x * xPos - v1.x * zNeg), (float)(v0.y - v2.y * xPos - v1.y * zNeg), (float)(v0.z - v2.z * xPos - v1.z * zNeg));
		setVertex(k++, (float)(v0.x - v2.x * xPos + v1.x * zPos), (float)(v0.y - v2.y * xPos + v1.y * zPos), (float)(v0.z - v2.z * xPos + v1.z * zPos));
	
		//draw lines
		drawLine (0, 1, selected);
		drawLine (1, 2, selected);
		drawLine (2, 3, selected);
		drawLine (3, 0, selected);
		
		drawLine (4, 5, selected);
		drawLine (5, 6, selected);
		drawLine (6, 7, selected);
		drawLine (7, 4, selected);
		
		drawLine (0, 4, selected);
		drawLine (1, 5, selected);
		drawLine (2, 6, selected);
		drawLine (3, 7, selected);
	}
}
