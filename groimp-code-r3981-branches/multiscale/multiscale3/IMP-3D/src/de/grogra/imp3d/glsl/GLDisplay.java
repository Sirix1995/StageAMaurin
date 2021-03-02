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

package de.grogra.imp3d.glsl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JPanel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Point4d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.Screenshot;
import com.sun.opengl.util.j2d.Overlay;
import com.sun.opengl.util.j2d.TextRenderer;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.Path;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.View;
import de.grogra.imp.awt.ViewComponentAdapter;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp3d.Camera;
import de.grogra.imp3d.CanvasCamera;
import de.grogra.imp3d.DisplayVisitor;
import de.grogra.imp3d.IMP3D;
import de.grogra.imp3d.LineArray;
import de.grogra.imp3d.LineSegmentizable;
import de.grogra.imp3d.LineSegmentizationCache;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.LightBase;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.imp3d.objects.SpotLight;
import de.grogra.imp3d.shading.ColorMapNode;
import de.grogra.imp3d.shading.ColorMapNodeProperty;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.Phong;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.ChannelMap;
import de.grogra.math.Pool;
import de.grogra.math.RGBColor;
import de.grogra.math.TMatrix4d;
import de.grogra.pf.boot.Main;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.reflect.Type;
import de.grogra.util.Debug;
import de.grogra.util.EnumerationType;
import de.grogra.util.EventListener;
import de.grogra.util.Lock;
import de.grogra.util.LockProtectedRunnable;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.vecmath.Math2;
import de.grogra.xl.lang.ObjectConsumer;

/**
 * This class is responsible for drawing all visible objects in the GroIMP3D
 * editor. It uses OpenGL to provide hardware accelerated drawing.
 * 
 * To access OpenGL, the library JOGL has to be installed. JOGL is available
 * from: https://jogl.dev.java.net/
 * Download the jogl.jar (contains the Java API for JOGL) and the
 * jogl-natives-*.jar (contains the native library for your system).
 * When starting GroIMP, make sure that the jogl.jar is in your classpath
 * and the libraries (.dll or .so) from the jogl-natives-*.jar are in your
 * search path for system libraries.
 * 
 * Making thos files available can be done by calling GroIMP like this:
 * \code
 * (linux version)
 * javaw -cp core.jar:jogl.jar -Djava.library.path=/usr/lib/jogl de.grogra.pf.boot.Main
 * (windows version)
 * javaw -cp core.jar;jogl.jar -Djava.library.path=c:\java\jogl de.grogra.pf.boot.Main
 * \endcode
 * 
 * OpenGL acts as a state machine. To provide a consistent state for every
 * drawing function, some rules were created. There are state variables that
 * are expected to have a certain value when a drawing function is entered.
 * Those state variables are:
 *  - MatrixMode set to GL_MODELVIEW
 *  - ShadeModel set to GL_SMOOTH
 *  - GL_DEPTH_TEST enabled
 *  - GL_LIGHTING enabled
 *  - GL_TEXTURE_2D disabled
 *  - GL_CULL_FACE enabled
 *  - GL_COLOR_MATERIAL enabled
 *  - ColorMaterial is set to GL_FRONT_AND_BACK and GL_AMBIENT_AND_DIFFUSE
 *  - GL_NORMALIZE is enabled
 *  - PolygonMode is set to GL_FRONT_AND_BACK and GL_LINE
 *  - GL_ALPHA_TEST enabled
 *  - AlphaFunc set to GL_GREATER and 0.1f
 *  - GL_LIGHT_MODEL_TWO_SIDE is set to 0
 * 
 * If a drawing function changes some of those state variables, it has to
 * reset them to their previous value upon return.
 * 
 * Other state variables are not expected to have a defined value when a
 * drawing function is called. Those state variables are:
 *  - all texture related state for TEXTURE UNIT 0
 *  - the contents of the matrices MODELVIEW and TEXTURE
 * 
 * All state variables not mentioned above should be reset to their default
 * value as defined by OpenGL when they were changed.
 * 
 * This class also provides an implementation of LOD (level of detail). For
 * now lod is only applied to the sphere and frustum primitives. While the
 * sphere uses the lod to select the appropriate display list, the frustum
 * uses it to calculate the number of generated segments when drawing.
 * 
 * @author nmi
 * 
 */
public class GLDisplay extends ViewComponentAdapter implements GLEventListener,
		RenderState, ImageObserver, Selectable
{
	private static final boolean DEBUG = Debug.debug ("GLDisplay");

	//! name of option, same as the one in plugin.xml file
	private static final String OPTION_NAME_SHOW_POINTS = "showPoints";

	//! name of option, same as the one in plugin.xml file
	private static final String OPTION_NAME_LIGHTING = "lighting";

	protected boolean optionShowPoints = false;

	// grid
	private static final String OPTION_NAME_SHOW_GRID = "showGrid";
	protected boolean optionShowGrid = false;

	private static final String OPTION_NAME_GRID_DIMENSION = "gridDimension";
	protected int optionGridDimension = 1;
	
	private static final String OPTION_NAME_GRID_SPACING = "gridSpacing";
	protected float optionGridSpacing = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_R = "gridColorR";
	protected float optionGridColorR = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_G = "gridColorG";
	protected float optionGridColorG = 1;
	
	private static final String OPTION_NAME_GRID_COLOR_B = "gridColorB";
	protected float optionGridColorB = 1;
	
	// axes of coordinates
	private static final String OPTION_NAME_SHOW_AXES = "showAxes";
	protected boolean optionShowAxes = false;

	private static final String OPTION_NAME_SHOW_AXES_NAMES = "showAxesNames";
	private boolean optionShowAxesNames = false;
	
	// display size
	private static final String OPTION_NAME_SHOW_DISPLAY_SIZE = "showDisplaySize";
	protected boolean optionShowDisplaySize = false;
	
	// JOGL allows to display OpenGL graphics in a GLCanvas, but this does not work well with
	// Swing applications. On one (Windows) system this caused enormous flickering making the
	// 3D-view unusable. Switching to GLJPanel (which uses a pBuffer internally for rendering)
	// solved the issue and seems to work on other systems also.
	private static final boolean USE_GLJPANEL = true;
	
	// true iff lighting state should be on
	boolean lightingOn = true;

	/**
	 * If the GL context supports the ARB extension GL_ARB_vertex_buffer_object
	 * this flag is set to true in the initialization function.
	 * When this flag is true, rendering of surfaces (i.e. NURBS) uses the
	 * faster vertex buffer objects.
	 */
	protected boolean supportsVBO = false;

	/**
	 * maximum number of levels of detail
	 */
	protected static final int LOD_LEVELS = 8;

	/**
	 * maximum number of supported lights
	 */
//	private static final int MAX_LIGHTS = 8;

	/**
	 * Number of lights supported by OpenGL.
	 * Required are at least 8 lights, but more might be available.
	 */
	private int maxLights = 8;

	public final Pool pool = new Pool ();

	/**
	 * manage textures stored in OpenGL server side memory
	 */
	final TextureManager textureManager = new TextureManager ();

	JPanel wrapper;
	GLAutoDrawable canvas;
	
	/**
	 * Set to <code>true</code> when {@link #canvas} has changed. This indicates
	 * that a new GLContext is used.
	 */
	volatile boolean canvasChanged = false;

	/**
	 * Set to <code>true</code> by {@link #reshape} in order to indicate that
	 * the size of the canvas has changed.
	 */
	volatile boolean reshaped = true;

	private volatile int repaintFlags;

	protected GLVisitor visitor = new GLVisitor ();
	protected PolygonizationCache polyCache;

	final Logger logger = Main.getLogger ();

	final Object imageLock = new Object ();

	/**
	 * background image
	 */
	Image img = null;

	boolean imgChanged = true; // if img was set to another image
	
	// used to take screenshots
	final Object callbackLock = new Object();
	volatile ObjectConsumer<? super RenderedImage> callback = null;

	// stores the opengl texture id for the background image
	int backgroundTexture;

	float backgroundWidth;

	float backgroundHeight;

	// stamp for scene graph, used in render() to check if the scene changed
	// stored in LightSource to detect deleted lights
	int oldStamp = -1;

	// set to true prior walking the scene graph,
	// to force recheck of available lights
	boolean checkLightSources = true;

	// number of lights set in OpenGL
	int lightCount = 0;

	// set to true if one of the light sources changed
	boolean lightsChanged = false;

	/**
	 * If greater than zero, lighting should be disabled.
	 */
	int disableLighting = 0;

	// contains all known light sources (LightSource as element type)
	final Hashtable<Light, LightSource> lights = new Hashtable<Light, LightSource> ();

	// if the GLContext changed, textures are gone, so recreate them
	GLContext oldContext = null;

	final HashMap<Font, TextRenderer> textRenderers = new HashMap<Font, TextRenderer>();
	
	// for drawPointCloud, maps an array containing the locations of the points
	// to a direct buffer containing the same data
	final WeakHashMap<float[], FloatBuffer> cloudToBuffer = new WeakHashMap<float[], FloatBuffer>(); 

	static
	{
		// this ensures initialization of class GLCanvas in order to detect
		// a missing or wrong JOGL installation early
		new GLCanvas ();
	}

	protected ImageObserver getObserverForRenderer ()
	{
		return this;
	}

	/**
	 * This class implements the ImageObserver interface to be notified
	 * about changes of the background image.
	 */
	public boolean imageUpdate (Image img, int infoflags, int x, int y,
			int width, int height)
	{
		if ((infoflags & (ABORT | ERROR)) != 0)
		{
			return false;
		}
		// new image data is available
		// so update the image and repaint the window

		synchronized (imageLock)
		{
			// remember the image
			this.img = img;
			imgChanged = true;

			// activate redraw of window
			repaint (RENDERED_IMAGE);
		}

		return true;
	}

	public void makeSnapshot (ObjectConsumer<? super RenderedImage> callback)
	{
		synchronized (callbackLock)
		{
			this.callback = callback;
		}
		repaint (RENDERED_IMAGE);
	}


	protected void initRender (int flags)
	{
	}

	private Lock retainedLock = null;
	private boolean disableRetain = false;
	private Object lockMutex = new Object ();

	protected void invokeRender (final int flags)
	{
		Utils.executeForcedlyAndUninterruptibly (getView ().getGraph (),
			new LockProtectedRunnable ()
			{
				public void run (boolean sameThread, Lock lock)
				{
					boolean display = false;
					synchronized (lockMutex)
					{
						repaintFlags |= flags;
						if (!disableRetain)
						{
							lock.retain ();
							retainedLock = lock;
							display = true;
						}
					}
					if (display)
					{
						canvas.display ();
					}
				}
			}, false);
	}

	public void display (GLAutoDrawable d)
	{
		Lock lock;
		synchronized (lockMutex)
		{
			lock = retainedLock;
			retainedLock = null;
			if (lock != null)
			{
				disableRetain = true;
			}
		}
		if (lock != null)
		{
			try
			{
				Workbench.setCurrent (getView ().getWorkbench ());
				Utils.executeForcedlyAndUninterruptibly (
					getView ().getGraph (), new LockProtectedRunnable ()
					{
						public void run (boolean sameThread, Lock lock)
						{
							int flags;
							synchronized (lockMutex)
							{
								disableRetain = false;
								flags = repaintFlags;
								repaintFlags = 0;
							}
							invokeRenderSync (flags);
						}
					}, lock);
			}
			finally
			{
				Workbench.setCurrent (null);
				synchronized (lockMutex)
				{
					disableRetain = false;
				}
			}
		}
		else
		{
			repaint (reshaped ? (ALL | CHANGED) : ALL);
		}
	}

	public void dispose ()
	{
		Lock lock;
		synchronized (lockMutex)
		{
			lock = retainedLock;
			retainedLock = null;
			disableRetain = true;
		}
		if (lock != null)
		{
			getView ().getGraph ().execute (new LockProtectedRunnable ()
			{
				public void run (boolean sameThread, Lock lock)
				{
					textRenderers.clear();
				}
			}, lock);
		}
		super.dispose ();
	}

	public Object getComponent ()
	{
		return wrapper;
	}

	public View3D getView3D ()
	{
		return (View3D) getView ();
	}

	public void initView (View view, EventListener listener)
	{
		if (USE_GLJPANEL) {
			// use GLJPanel to draw OpenGL stuff
			GLCapabilities glCaps = new GLCapabilities ();
			glCaps.setDoubleBuffered (true);
			glCaps.setHardwareAccelerated (true);
			canvas = new GLJPanel (glCaps) {
				public void addNotify ()
				{
					super.addNotify ();
					installListeners (wrapper);
				}
				
				public void removeNotify ()
				{
					uninstallListeners(wrapper);
					super.removeNotify ();
				}
			};
			wrapper = (GLJPanel)canvas;
			canvasChanged = true;
			canvas.addGLEventListener (GLDisplay.this);
			wrapper.setMinimumSize (new Dimension (0, 0));
			wrapper.setPreferredSize (new Dimension (640, 480));
		} else {
			// use GLCanvas to draw OpenGL stuff
			wrapper = new JPanel (new GridLayout (1, 1))
			{
				public void addNotify ()
				{
					super.addNotify ();
					if (canvas == null)
					{
						// Instantiate GLCanvas with the correct graphics device when this
						// is known. This is needed for X11 in multi-headed environments.
						GLCapabilities glCaps = new GLCapabilities ();
						glCaps.setDoubleBuffered (true);
						glCaps.setHardwareAccelerated (true);
						GraphicsConfiguration gc = getGraphicsConfiguration ();
						canvas = new GLCanvas (glCaps, null, null,
							(gc != null) ? gc.getDevice () : null);
						canvasChanged = true;
						canvas.addGLEventListener (GLDisplay.this);
						installListeners ((Component)canvas);
	
						// Now add the GLCanvas to the visible wrapper.
						wrapper.add ((Component)canvas);
					}
				}
	
				public void removeNotify ()
				{
					super.removeNotify ();
					if (canvas != null)
					{
						// Remove the GLCanvas if the component hierarchy is not displayed any more.
						// A new GLCanvas will be created in the method addNotify.
						remove ((Component)canvas);
						canvas = null;
					}
				}
			};
			wrapper.setMinimumSize (new Dimension (0, 0));
			wrapper.setPreferredSize (new Dimension (640, 480));
		}
		
		
		super.initView (view, listener);
		optionShowPoints = Boolean.TRUE.equals (getOption (
			OPTION_NAME_SHOW_POINTS, Boolean.TRUE));
		lightingOn = Boolean.TRUE.equals (getOption (OPTION_NAME_LIGHTING,
			Boolean.TRUE));
		optionShowGrid = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_GRID, Boolean.FALSE));
		optionGridDimension = (Integer) getOption(OPTION_NAME_GRID_DIMENSION, 5);
		optionGridSpacing = (Float) getOption(OPTION_NAME_GRID_SPACING, 1.0f);
		optionGridColorR = (Float) getOption(OPTION_NAME_GRID_COLOR_R, 1.0f);
		optionGridColorG = (Float) getOption(OPTION_NAME_GRID_COLOR_G, 1.0f);
		optionGridColorB = (Float) getOption(OPTION_NAME_GRID_COLOR_B, 1.0f);
		optionShowAxes = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_AXES, Boolean.FALSE));
		optionShowAxesNames = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_AXES_NAMES, Boolean.FALSE));
		optionShowDisplaySize = Boolean.TRUE.equals(getOption(OPTION_NAME_SHOW_DISPLAY_SIZE, Boolean.FALSE));
	}

	private double[] matrixArray = new double[16];

	/**
	 * @param m
	 * @return
	 */
	double[] toGLMatrix (Matrix4d m)
	{
		matrixArray[0] = m.m00;
		matrixArray[1] = m.m10;
		matrixArray[2] = m.m20;
		matrixArray[3] = m.m30;
		matrixArray[4] = m.m01;
		matrixArray[5] = m.m11;
		matrixArray[6] = m.m21;
		matrixArray[7] = m.m31;
		matrixArray[8] = m.m02;
		matrixArray[9] = m.m12;
		matrixArray[10] = m.m22;
		matrixArray[11] = m.m32;
		matrixArray[12] = m.m03;
		matrixArray[13] = m.m13;
		matrixArray[14] = m.m23;
		matrixArray[15] = m.m33;
		return matrixArray;
	}

	static ByteBuffer newByteBuffer (int length)
	{
		return ByteBuffer.allocateDirect (length).order (
			ByteOrder.nativeOrder ());
	}

	/**
	 * This class is used to traverse the graph and obtain all
	 * visible objects and the lights for visualisation.
	 * 
	 * @author Reinhard Hemmerling
	 */
	public class GLVisitor extends DisplayVisitor
	{
		private int minPathLength;
		private LineSegmentizationCache lineCache;

		Matrix4d worldToViewInv = new Matrix4d ();

		public void init (GraphState gs, Matrix4d t, int minPathLength,
				boolean checkLayer)
		{
			init (gs, t, getView3D (), checkLayer);

			Matrix4d worldToView = getView3D ().getCamera ()
				.getWorldToViewTransformation ();
			worldToViewInv.invert (worldToView);
			this.minPathLength = minPathLength;

			if (lineCache == null)
			{
				lineCache = new LineSegmentizationCache (gs, 1);
			}
		}

		protected void visitImpl (Object object, boolean asNode, Shader s,
				Path path)
		{
			if ((minPathLength > 0)
				&& (path.getNodeAndEdgeCount () - (asNode ? 0 : 1) < minPathLength))
			{
				return;
			}

			Object shape = state.getObjectDefault (object, asNode,
				de.grogra.imp3d.objects.Attributes.SHAPE, null);

			// query line color
			Color3f color = new Color3f (Color.WHITE);
			//			boolean selected = (selectionState & ViewSelection.SELECTED) != 0;
			Object c = state.getObjectDefault (object, asNode,
				de.grogra.imp3d.objects.Attributes.COLOR, this);
			if ((c != null) && (c != this))
			{
				color = (Color3f) c;
			}
			else
			{
				color = new Color3f (new Color (s.getAverageColor ()));
			}

			// check if there is an shape object
			if (shape != null)
			{
				// checkRepaintWrapException ();

				// draw objects like sphere, box, cone
				if (shape instanceof Renderable)
				{
					((Renderable) shape).draw (object, asNode, GLDisplay.this);
				}

				// draw curves
				else if (shape instanceof LineSegmentizable)
				{
					LineArray lines = lineCache.get (object, asNode,
						(LineSegmentizable) shape);
					//					setColor (object, asNode, s, state);
					int[] indices = lines.lines.elements;
					float[] vertices = lines.vertices.elements;
					int dim = lines.dimension;
					int n = lines.lines.size ();

					// draw the lines
					Point3f p0 = new Point3f ();
					Point3f p1 = new Point3f ();
					boolean newLine = true;

					// iterate through indices
					for (int i = 0; i < n; i++)
					{
						// get current index
						int index = indices[i];

						// check if line strip was finished
						if (index < 0)
						{
							newLine = true;
							continue;
						}

						// check if a line is started
						if (newLine)
						{
							// set starting point
							p0.set (dim > 0 ? vertices[dim * index + 0] : 0,
								dim > 1 ? vertices[dim * index + 1] : 0,
								dim > 2 ? vertices[dim * index + 2] : 0);
							newLine = false;
						}
						else
						{
							// set end point
							p1.set (dim > 0 ? vertices[dim * index + 0] : 0,
								dim > 1 ? vertices[dim * index + 1] : 0,
								dim > 2 ? vertices[dim * index + 2] : 0);

							// draw the line
							drawLine (p0, p1, color, 0, null);

							// endpoint is new startpoint
							p0.set (p1);
						}
					}
				}
			}

			// check if scene changed, then lights may have changed also
			// so regenerate the list of light sources
			if (checkLightSources)
			{
				// handle light sources during visit
				Object lightObject = state.getObjectDefault (object, asNode,
					de.grogra.imp3d.objects.Attributes.LIGHT, null);
				if (lightObject instanceof Light)
				{
					// checkRepaintWrapException ();
					Light light = (Light) lightObject;
					int lt = light.getLightType ();
					if ((lt != Light.NO_LIGHT) && (lt != Light.SKY))
					{
						// found another active light, remember that
						lightCount++;

						// collect information about light source
						Matrix4d lightToWorld = new Matrix4d ();
						lightToWorld.mul (worldToViewInv,
							getCurrentTransformation ());
						LightSource lightSource = convertLightToLightSource (
							light, lightToWorld);

						// check if light source changed its state
						LightSource oldLightSource = (LightSource) lights
							.get (light);
						if (!lightSource.equals (oldLightSource))
						{
							// remember that a light was changed
							lightsChanged = true;

							// set stamp of light source to current stamp
							lightSource.stamp = oldStamp;

							// remember this light
							lights.put (light, lightSource);
						}
						else
						{
							// set stamp of light source to current stamp
							oldLightSource.stamp = oldStamp;
						}
					}
				}
			}
		}
	}

	/**
	 * Decrement {@link #disableLighting}. If it reaches zero,
	 * tell opengl to enable lighting.
	 */
	void enableLighting (GL gl)
	{
		if (--disableLighting == 0)
		{
			// enable lighting
			gl.glEnable (GL.GL_LIGHTING);
		}
	}

	/**
	 * Increment {@link #disableLighting} and tell opengl to disable lighting.
	 */
	void disableLighting (GL gl)
	{
		disableLighting++;
		gl.glDisable (GL.GL_LIGHTING);
	}

	/**
	 * When the background image changed (notification via imageUpdate() was received),
	 * the grabBackground function is called to convert the image to an OpenGL texture.
	 * It is then drawn with the function drawBackground().
	 * @param gl
	 */
	void grabBackground (GL gl)
	{
		// perform conversion of image data to texture
		int width = img.getWidth (null); // width of image data
		int height = img.getHeight (null); // height of image data

		// calculate next power of two for texture size,
		// otherwise opengl will complain
		int k = Math2.roundUpNextPowerOfTwo (Math.max (width, height));
		backgroundWidth = (float) width / (float) k;
		backgroundHeight = (float) height / (float) k;

		// grab the pixel data
		boolean grabbed = false;
		int[] pixels = new int[k * k];
		PixelGrabber pg = new PixelGrabber (img, 0, 0, width, height, pixels,
			0, k);
		try
		{
			pg.grabPixels ();
			if ((pg.getStatus () & ImageObserver.ABORT) == 0)
			{
				// grabbing successful
				grabbed = true;
			}
		}
		catch (InterruptedException e)
		{
			// grabbing was interrupted, act as if grabbing failed
		}

		// handle grabbed image
		if (grabbed)
		{
			// create and set an opengl texture
			IntBuffer buf = IntBuffer.wrap (pixels);
			gl.glBindTexture (GL.GL_TEXTURE_2D, backgroundTexture);
			gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
			gl.glTexParameteri (GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
			gl.glTexImage2D (GL.GL_TEXTURE_2D,
				0 /* level of detail, needed for mip-mapping */, GL.GL_RGB, k,
				k, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, buf);

			// mark conversion as done
			imgChanged = false;
		}
	}

	/**
	 * Draw a background image previously converted to an OpenGL texture with
	 * grabBackground(). The background image has the same size as 
	 * the render window. 
	 * @param gl
	 */
	void drawBackground (GL gl)
	{
		// set and enable the texture
		gl.glDisable (GL.GL_DEPTH_TEST);
		gl.glEnable (GL.GL_TEXTURE_2D);
		gl.glColor3f (1, 1, 1);
		gl.glBindTexture (GL.GL_TEXTURE_2D, backgroundTexture);

		// set transformation matrices to identity
		gl.glPushMatrix ();
		gl.glLoadIdentity ();
		gl.glMatrixMode (GL.GL_PROJECTION);
		gl.glPushMatrix ();
		gl.glLoadIdentity ();

		// disable lighting for background picture
		disableLighting (gl);

		// draw the background
		gl.glBegin (GL.GL_QUADS);
		gl.glTexCoord2f (0.0f, backgroundHeight);
		gl.glVertex3f (-1, -1, -1);
		gl.glTexCoord2f (backgroundWidth, backgroundHeight);
		gl.glVertex3f (1, -1, -1);
		gl.glTexCoord2f (backgroundWidth, 0.0f);
		gl.glVertex3f (1, 1, -1);
		gl.glTexCoord2f (0.0f, 0.0f);
		gl.glVertex3f (-1, 1, -1);
		gl.glEnd ();

		// enable lighting
		enableLighting (gl);

		// reset transformation matrices to previous value
		gl.glPopMatrix ();
		gl.glMatrixMode (GL.GL_MODELVIEW);
		gl.glPopMatrix ();

		gl.glDisable (GL.GL_TEXTURE_2D);
		gl.glEnable (GL.GL_DEPTH_TEST);
	}

	/**
	 * Make sure that there is at least one and at most eight lights 
	 * in the lightSources array. If there was no light in the array, 
	 * then generate a default one. If there were more than 8 lights
	 * in the array, issue a warning (to the logger) since OpenGL only
	 * supports up to eight lights by default. The function returns a
	 * possibly modified array of lights.
	 * 
	 * @param lightSources
	 * @return possibly modified array of light sources
	 */
	LightSource[] checkLightSources (LightSource[] lightSources)
	{
		// make sure there is at least one light in the scene
		// if none was create by the user, create an artificial one
		if (lightSources.length == 0)
		{
			// create light if scene contains none
			lightSources = generateDefaultLight ();
		}

		// opengl supports at least 8 light sources
		// check if the array contains at most 8 light sources
		if (lightSources.length > maxLights)
		{
			// log a warning about too many lights
			logger
				.warning ("too many lights in scene, will use the first " + maxLights + " lights");
		}

		// return lightSources, in case a default light was created
		return lightSources;
	}

	/**
	 * Set active light sources in OpenGL.
	 * @param gl
	 * @param lightSources
	 */
	void setupLights (GL gl, LightSource[] lightSources)
	{
		// the model-view-matrix is applied to the light's position
		// store the old model-view-matrix, then set an identity
		// since LightSource already stores transformed light positions
		gl.glPushMatrix ();
		Matrix4d worldToView = getView3D ().getCamera ()
			.getWorldToViewTransformation ();
		gl.glLoadMatrixd (toGLMatrix (worldToView), 0);

		// activate at most 8 lights
		for (int i = 0; i < lightSources.length && i < maxLights; i++)
		{
			// get current light source
			LightSource light = lightSources[i];

			// set light's position
			gl.glLightfv (GL.GL_LIGHT0 + i, GL.GL_POSITION, Math2
				.toFloatArray (light.lightPos), 0);

			// set light's direction
			gl.glLightfv (GL.GL_LIGHT0 + i, GL.GL_SPOT_DIRECTION, Math2
				.toFloatArray (light.lightDir), 0);

			// set light's ambient color
			gl.glLightfv (GL.GL_LIGHT0 + i, GL.GL_AMBIENT, Math2
				.toFloatArray (light.ambientColor), 0);

			// set light's diffuse color
			gl.glLightfv (GL.GL_LIGHT0 + i, GL.GL_DIFFUSE, Math2
				.toFloatArray (light.diffuseColor), 0);

			// set light's specular color
			gl.glLightfv (GL.GL_LIGHT0 + i, GL.GL_SPECULAR, Math2
				.toFloatArray (light.specularColor), 0);

			// set light's spotlight exponent
			gl.glLightf (GL.GL_LIGHT0 + i, GL.GL_SPOT_EXPONENT,
				light.spotExponent);

			// set light's spotlight cutoff angle
			gl.glLightf (GL.GL_LIGHT0 + i, GL.GL_SPOT_CUTOFF, light.spotCutoff);

			// set light's constant attenuation
			gl.glLightf (GL.GL_LIGHT0 + i, GL.GL_CONSTANT_ATTENUATION,
				light.constantAttenuation);

			// set light's linear attenuation
			gl.glLightf (GL.GL_LIGHT0 + i, GL.GL_LINEAR_ATTENUATION,
				light.linearAttenuation);

			// set light's quadratic attenuation
			gl.glLightf (GL.GL_LIGHT0 + i, GL.GL_QUADRATIC_ATTENUATION,
				light.quadraticAttenuation);

			// enable the light
			gl.glEnable (GL.GL_LIGHT0 + i);
		}

		// restore old model-view-matrix
		gl.glPopMatrix ();
	}

	/**
	 * Retrieve array of active light sources from lights hashtable.
	 * @return
	 */
	LightSource[] getLightSources ()
	{
		return (LightSource[]) lights.values ().toArray (new LightSource[0]);
	}

	/**
	 * Render the scene graph.
	 * 
	 * This function will traverse the scene graph and render all objects of it.
	 * These steps are performed:<br>
	 * <ol>
	 * <li>get GL
	 * <li>if GL changed, delete textures 
	 * <li>clear the frame buffer
	 * <li>draw background image (i.e. from povray)
	 * <li>cleaar depth buffer
	 * <li>set projection matrix
	 * <li>check if the scene graph changed since last render, than remember that
	 *     and perform an update of the light sources
	 * <li>walk graph first time, visit every object, update lights if necessary
	 * <li>if lights have changed, draw the scene again
	 * <li>walk graph second time, visit highlighted objects
	 * <li>disable lighting and depth buffer
	 * <li>walk graph third time, visit tools (i.e. arrows to move objects)
	 * <li>enable lighting and depth buffer
	 * </ol>
	 * 
	 */
	protected void render (int flags) throws InterruptedException
	{
		// obtain callback that was last set
		ObjectConsumer<? super RenderedImage> callback;
		synchronized (callbackLock)
		{
			callback = this.callback;
			this.callback = null;
		}

		// reset reshaped
		reshaped = false;

		getView3D ().setExtent (null, Float.NaN);

		// obtain current GL instance
		GL gl = canvas.getGL ();

		// check if the canvas changed, i.e. in dual-head configurations
		// when the window is moved from one monitor to another one
		if (canvasChanged)
		{
			canvasChanged = false;
			dlSphere = 0;
			lights.clear ();
			checkLightSources = true;

			// check if VBOs are supported
			supportsVBO = gl
				.isExtensionAvailable ("GL_ARB_vertex_buffer_object");
			if (DEBUG)
			{
				System.err.println ("supportsVBO = " + supportsVBO);
			}
			supportsVBO = false;
		}

		// compare with old GL context
		GLContext context = canvas.getContext();
		if (context != oldContext)
		{
			// GL context changed
			// this results in all textures being lost
			// so recreate them
			textureManager.deleteTextures (gl);
			// and reset OpenGL error code
			gl.glGetError();
			oldContext = context;
		}

		// set clear color for frame buffer
		gl.glClearColor (0.75f, 0.75f, 0.75f, 0);

		// get viewport size
		int w = canvas.getWidth ();
		int h = canvas.getHeight ();
		//		gl.glViewport (0, 0, w, h);
		((View3D) getView ()).getCanvasCamera ().setDimension (w, h);

		// clear frame buffer
		gl.glClear (GL.GL_COLOR_BUFFER_BIT);

		// check if there is a rendered background image to draw
		synchronized (imageLock)
		{
			if (img != null)
			{
				// check if image was modified since last conversion
				if (imgChanged)
				{
					grabBackground (gl);
				}

				// draw the background image to the screen
				drawBackground (gl);

				// if a rendered image was available, then send that one
				// to the callback as a buffered image
				if (callback != null)
				{
					callback.consume(convert(img));
				}

				// delete rendered image if scene was changed,
				// otherwise prevent drawing the scene on top of the image
				if ((flags & (SCENE | CHANGED)) == (SCENE | CHANGED))
				{
					img = null;
				}
				else
				{
					return;
				}
			}
		}

		disableLighting = 0;
		gl.glEnable (GL.GL_LIGHTING);
		if (!lightingOn)
		{
			disableLighting (gl);
		}

		// clear depth buffer
		gl.glClear (GL.GL_DEPTH_BUFFER_BIT);

		// set viewport transformation
		gl.glMatrixMode (GL.GL_PROJECTION);
		Camera c = ((View3D) getView ()).getCamera ();
		Matrix4d m = new Matrix4d ();
		c.getViewToClipTransformation (m);
		double aspect = (double) w / (double) h;
		m.mul (new Matrix4d (1, 0, 0, 0, 0, aspect, 0, 0, 0, 0, 1, 0, 0, 0, 0,
			1));
		gl.glLoadMatrixd (toGLMatrix (m), 0);
		gl.glMatrixMode (GL.GL_MODELVIEW);

		// check if the scene graph changed
		int newStamp = getView ().getGraph ().getStamp ();
		if (newStamp != oldStamp)
		{
			// if scene graph changed, then lights may have changed also
			// so generate a new list of lights
			checkLightSources = true;

			// remeber new stamp value
			oldStamp = newStamp;
		}

		// scene graph was not changed, so set new light sources //
		// get list of LightSource objects
		LightSource[] lightSources = getLightSources ();

		// make sure that there is at least one and at most 8 lights
		lightSources = checkLightSources (lightSources);

		// activate lights
		setupLights (gl, lightSources);

		// reset number of lights
		// will be updated by graph traversal
		lightCount = 0;

		// reset that lights changed
		// will be updated by graph traversal
		lightsChanged = false;

		// first walk of scene graph, visit all objects
		curHighlight = 0;
		visitor.init (getRenderGraphState (),
			c.getWorldToViewTransformation (), 0, true);
		
		// draws the grid (groundplane)
		if (optionShowGrid)
			drawGrid(gl);
		
		try
		{
			// first walk, visit every object
			getView ().getGraph ().accept (null, visitor, null);

			// disable checking for lights
			checkLightSources = false;

			// check if the number of lights or the state of any light changed
			if (lightsChanged
				|| (lightCount != 0 && lightSources.length != lightCount))
			{
				// a light was added, removed or changed its state
				// so draw the scene again with correct lighting
				//				System.out.println ("lights changed: "+lightsChanged+" lightCount: "+lightCount+" length: "+lightSources.length);

				// get list of keys into light table
				Object[] keys = lights.keySet ().toArray ();

				// remove all lights from light table that were deleted from graph, 
				// i.e. those lights with a wrong stamp in light table
				for (int i = 0; i < keys.length; i++)
				{
					Object key = keys[i];
					LightSource lightSource = (LightSource) lights.get (key);
					// check stamp
					if (lightSource.stamp != oldStamp)
					{
						// wrong stamp, so delete light
						lights.remove (key);
					}
				}

				// check if lights were deleted
				// that is if there are more lights in the light table
				// (lightSources.length) than can be found in the graph (lightCount)
				if (lightSources.length > lightCount)
				{
					// then disable lights in opengl //

					// there should be lightCount lights active, 
					// but at most eight lights
					for (int i = lightCount; i < maxLights
						&& i < lightSources.length; i++)
					{
						gl.glDisable (GL.GL_LIGHT0 + i);
					}
				}

				// get list of LightSource objects again
				lightSources = getLightSources ();

				// make sure that there is at least one and at most 8 lights
				lightSources = checkLightSources (lightSources);

				// activate lights
				setupLights (gl, lightSources);

				// clear depth buffer
				gl.glClear (GL.GL_DEPTH_BUFFER_BIT);

				// walk the graph again to perform correct lighting
				getView ().getGraph ().accept (null, visitor, null);
			}

			// second walk, visit highlighted objects
			// so they can be overdrawn with wireframe version
			ArrayPath path = new ArrayPath (getView ().getGraph ());
			ViewSelection.Entry[] s = ViewSelection.get (getView ())
				.getAll (-1);
			for (int i = 0; i < s.length; i++)
			{
				Path p = s[i].getPath ();
				curHighlight = s[i].getValue ();
				visitor.init (getRenderGraphState (), c
					.getWorldToViewTransformation (), p.getNodeAndEdgeCount (),
					true);
				GraphUtils.acceptPath (p, visitor, path);
			}

			// disable lighting for tools
			disableLighting (gl);

			// disable depth buffer for tools
			gl.glDisable (GL.GL_DEPTH_TEST);

			// third walk through the tools subgraph (i.e. the arrows to move
			// objects)
			de.grogra.imp.edit.Tool tool = getView ().getActiveTool ();
			if (tool != null)
			{
				curHighlight = 0;
				visitor.init (GraphManager.STATIC_STATE, c
					.getWorldToViewTransformation (), 0, false);
				path.clear (GraphManager.STATIC);
				for (int i = 0; i < tool.getToolCount (); i++)
				{
					GraphManager.acceptGraph (tool.getRoot (i), visitor, path);
				}
			}

			// enable lighting
			enableLighting (gl);
			
			// enable depth buffer
			gl.glEnable (GL.GL_DEPTH_TEST);
			
			// draw axes of coordinates
			if (optionShowAxes)
				drawAxes(gl, c);
			
			if (optionShowDisplaySize)
				drawDisplaySize(gl, c);
			
		}
		catch (WrapException e)
		{
			if (e.getCause () instanceof InterruptedException)
			{
				throw (InterruptedException) e.getCause ();
			}
			throw e;
		}
		finally
		{
			gl.glFlush ();
		}

		/*
		 // repaint the scene if it was modified, to use the new light info
		 if (updateLightSources)
		 {
		 // make sure lights are not updated in the second walk
		 updateLightSources = false;

		 canvas.repaint ();
		 }
		 */

		//		long t1 = System.nanoTime ();
		//		long delta = t1 - t0;
		//		long delay = t0 - oldTime;
		//		oldTime = t1;
		//		System.out.println ("nanotime: " + t1 + "  \tdelta: " + (delta / 1000)
		//				+ " us\tdelay: " + (delay / 1000) + " us");

		// if a screenshot was requested, read pixels from framebuffer
		// and send them to the callback
		if (callback != null)
		{
			callback.consume(Screenshot.readToBufferedImage(w, h));
		}
	}

	/**
	 * Convert a light from the scene graph to a light suited for OpenGL.
	 * @param light
	 * @param lightToWorld
	 * @return
	 */
	static LightSource convertLightToLightSource (Light light,
			Matrix4d lightToWorld)
	{
		// collect information about light source
		LightSource lightSource = new LightSource ();

		// get light's location in view coordinates
		Point4d lightPos = new Point4d (0, 0, 0, 1);
		lightToWorld.transform (lightPos);

		// store position of light source in world coordinates
		lightSource.lightPos = lightPos;

		// store direction of light source (i.e. z-transformation)
		lightSource.lightDir = new Vector3d (lightToWorld.m02,
			lightToWorld.m12, lightToWorld.m22);

		// get general light properties
		if (light instanceof LightBase)
		{
			LightBase lightBase = (LightBase) light;
			RGBColor color = lightBase.getColor ();
			lightSource.diffuseColor = new Color4f (color.x, color.y, color.z,
				1);
			lightSource.specularColor = new Color4f (color.x, color.y, color.z,
				1);

			// depending on light source, obtain additional info about the light
			if (light instanceof PointLight)
			{
				lightSource.lightType = LightSource.LIGHT_TYPE_POINT;
			}
			// else if (light instanceof SpotLight)
			// note that SpotLight inherits from PointLight
			if (light instanceof SpotLight)
			{
				lightSource.lightType = LightSource.LIGHT_TYPE_SPOT;
				SpotLight spotLight = (SpotLight) light;

				// get spotlight parameters
//				float innerAngle = spotLight.getInnerAngle ();
				float outerAngle = spotLight.getOuterAngle ();
//				float attenuationDistance = spotLight.getAttenuationDistance ();
//				float attenuationExponent = spotLight.getAttenuationExponent ();

				// calculate parameters for opengl spotlight
				lightSource.spotCutoff = (float) (outerAngle * 180.0f / Math.PI);
				lightSource.spotExponent = 8;
			}
			// else if (light instanceof DirectionalLight)
			if (light instanceof DirectionalLight)
			{
				lightSource.lightType = LightSource.LIGHT_TYPE_DIRECTIONAL;

				// for directional light, modify the light's position
				// if w is set to 0, the light is infinitely far away (i.e.
				// directional)
				// the position of the light source depends on the direction of
				// the light
				// lightSource.lightPos.w = 0;
				lightSource.lightPos.x = -lightSource.lightDir.x;
				lightSource.lightPos.y = -lightSource.lightDir.y;
				lightSource.lightPos.z = -lightSource.lightDir.z;
				lightSource.lightPos.w = 0;
			}
		}

		return lightSource;
	}

	/**
	 * In case there is no used-defined light source, generate a default one.
	 */
	LightSource[] generateDefaultLight ()
	{
		// get default light
		Matrix4d lightToWorld = new Matrix4d ();
		Light light = getView3D ().getDefaultLight (lightToWorld);

		// convert from Light to LightSource
		LightSource[] lightSources = new LightSource[1];
		lightSources[0] = convertLightToLightSource (light, lightToWorld);

		return lightSources;
	}

	/**
	 * Initialise opengl state and set default values. All functions can assume
	 * those values being set upon call (i.e. MatrixMode as GL_MODELVIEW). If a
	 * function changes any of those values, it has to restore its state before
	 * calling another function or exiting.
	 * 
	 */
	public void init (GLAutoDrawable d)
	{
		// TODO disable for release build
		//		d.setGL (new DebugGL (d.getGL ()));

		//		
		// initialize default state for OpenGL
		//

		// get gl instance
		GL gl = d.getGL ();
		
		// query some values which may help debugging
		int[] iv = new int[10];
		gl.glGetIntegerv (GL.GL_MAX_TEXTURE_SIZE, iv, 0);
		logger.info ("GL_MAX_TEXTURE_SIZE = " + iv[0]);
		gl.glGetIntegerv (GL.GL_DEPTH_BITS, iv, 0);
		logger.info ("GL_DEPTH_BITS = " + iv[0]);
		// log a warning about too little depth buffer precision
		if (iv[0] <= 16)
		{
			logger
				.warning ("depth buffer precision is too low and may result in visible artifacts");
		}
		gl.glGetIntegerv (GL.GL_RED_BITS, iv, 0);
		logger.info ("GL_RED_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_GREEN_BITS, iv, 0);
		logger.info ("GL_GREEN_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_BLUE_BITS, iv, 0);
		logger.info ("GL_BLUE_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_ALPHA_BITS, iv, 0);
		logger.info ("GL_ALPHA_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_DEPTH_BITS, iv, 0);
		logger.info ("GL_DEPTH_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_STENCIL_BITS, iv, 0);
		logger.info ("GL_STENCIL_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_ACCUM_RED_BITS, iv, 0);
		logger.info ("GL_ACCUM_RED_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_ACCUM_GREEN_BITS, iv, 0);
		logger.info ("GL_ACCUM_GREEN_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_ACCUM_BLUE_BITS, iv, 0);
		logger.info ("GL_ACCUM_BLUE_BITS = " + iv[0]);
		gl.glGetIntegerv (GL.GL_ACCUM_ALPHA_BITS, iv, 0);
		logger.info ("GL_ACCUM_ALPHA_BITS = " + iv[0]);
		gl.glGetIntegerv(GL.GL_MAX_LIGHTS, iv, 0);
		logger.info ("GL_ACCUM_ALPHA_BITS = " + iv[0]);
		maxLights = iv[0];

		// set global scene settings (default values)
		// gl.glColor3d (1.0, 0.8, 0.3);
		gl.glEnable (GL.GL_DEPTH_TEST);
		gl.glShadeModel (GL.GL_SMOOTH);
		gl.glDisable (GL.GL_TEXTURE_2D);
		gl.glMatrixMode (GL.GL_MODELVIEW);
		// gl.glPolygonMode (GL.GL_FRONT_AND_BACK, GL.GL_FILL);

		// allow setting ambient and diffuse material color with glColor()
		gl.glColorMaterial (GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable (GL.GL_COLOR_MATERIAL);

		// enable back-face culling
		gl.glEnable (GL.GL_CULL_FACE);

		// enable alpha test for use of transparent textures
		gl.glEnable (GL.GL_ALPHA_TEST);
		gl.glAlphaFunc (GL.GL_GREATER, 0.1f);

		// generate id for background texture
		int[] texId = new int[1];
		gl.glGenTextures (1, texId, 0);
		backgroundTexture = texId[0];

		// automatically normalize normal vectors
		gl.glEnable (GL.GL_NORMALIZE);
		//		gl.glEnable(GL.GL_RESCALE_NORMAL);
		
		// force (re)creation of display list
		dlSphere = 0;
	}

	public void reshape (GLAutoDrawable d, int x, int y, int w, int h)
	{
		reshaped = true;

		// fix for bug #3184923
		// according to the documentation of TextRenderer it should not be
		// necessary to recreate the text renderer upon change of the window size,
		// but this line seems to fix the issues with missing text
		textRenderers.clear();
		
		// on some systems resizing the render window caused loosing all textures
		// fix is to always remove all textures from the manager so they have
		// to be recreated on demand
		final GL gl = d.getGL();
		textureManager.deleteTextures(gl);
	}

	public void displayChanged (GLAutoDrawable d, boolean modeChanged,
			boolean deviceChanged)
	{
		//		System.out.println ("GLDisplay.displayChanged was called");
	}

	public Pool getPool ()
	{
		return pool;
	}

	public Shader getCurrentShader ()
	{
		return visitor.getCurrentShader ();
	}

	protected int curHighlight = 0;

	public int getCurrentHighlight ()
	{
		return curHighlight;
	}

	public float estimateScaleAt (Tuple3f point)
	{
		return estimateScaleAt (point, visitor.getCurrentTransformation ());
	}

	/**
	 * Returns the size of an object of size 1.0 (in object coordinates) on the
	 * screen (in pixels).
	 * 
	 * @param point
	 * @param t
	 * @return
	 */
	public float estimateScaleAt (Tuple3f point, Matrix4d t)
	{
		return getView3D ().estimateScaleAt (point, t);
	}

	/**
	 * Calculate level of detail. The returned value is 0.0f for lowest detail
	 * and 1.0f for highest detail. This lod depends on global lod (i.e.
	 * decreases if the frame rate drops) and the scale (estimated size of a
	 * line of length 1.0 in pixels).
	 * 
	 * The lod is calculated as: lod = globalLod * (scale - s_min) / (s_max -
	 * s_min)
	 * 
	 * where s_min is the size of a line of length 1.0 in pixels for the
	 * smallest lod and s_max similar for the biggest lod, globalLod is a value
	 * from 0.0 to 1.0
	 * 
	 * Smallest scale is 5 pixels, biggest scale is 100 pixels
	 * 
	 * @return
	 */
	protected float getLOD (Matrix4d t, float r)
	{
		// get global LOD, depends on frequency of window updates
		int globalLod = getGlobalLOD ();

		// calculate LOD value from global LOD, will be between 0.0f and 1.0f
		float lod = (float) (globalLod - View.LOD_MIN)
			/ (float) (View.LOD_MAX - View.LOD_MIN);
		lod = Math.min (1.0f, Math.max (0.0f, lod));

		// estimate size of object (if object was of size 1.0 before
		// transformation)
		float scale = estimateScaleAt (new Point3f (), t);

		scale *= r;
		/*
		 * // if scale is too small, set LOD to minimum if (scale < 0.0001f) {
		 * lod = 0; } else { // calculate lod based on how big the object will
		 * appear on the screen lod = (float) Math.pow (lod, 50 / scale); }
		 */
		lod *= (float) (scale - SCALE_MIN) / (float) (SCALE_MAX - SCALE_MIN);
		lod = Math.min (1.0f, Math.max (0.0f, lod));

		return lod;
	}

	static final int SCALE_MIN = 5;

	static final int SCALE_MAX = 45;

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

	private final Matrix4d xform = new TMatrix4d ();

	protected Shader getShader (Shader s)
	{
		if (s == null)
		{
			s = visitor.getCurrentShader ();
		}
		return s;
	}

	protected Matrix4d getTransformation (Matrix4d t)
	{
		if (t == null)
		{
			t = visitor.getCurrentTransformation ();
		}
		else
		{
			Math2.mulAffine (xform, visitor.getCurrentTransformation (), t);
			t = xform;
		}
		return t;
	}

	/**
	 * Issue a call to glNormal3f() and glVertex3f() on gl.
	 * @param gl
	 * @param p vertex
	 * @param n normal
	 */
	static void glVertex (GL gl, Tuple3f p, Tuple3f n)
	{
		gl.glNormal3f (n.x, n.y, n.z);
		gl.glVertex3f (p.x, p.y, p.z);
	}

	/**
	 * Draw a box on gl.
	 * The box will be axis-aligned and surrounded by the planes x = x0, x = x1,
	 * y = y0, y = y1, z = z0, z = z1. The function will generate the vertices,
	 * normals and texture coordinates for the box. 
	 * @param gl
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param x1
	 * @param y1
	 * @param z1
	 */
	void drawBoxImpl (GL gl, float x0, float y0, float z0, float x1, float y1,
			float z1)
	{
		gl.glBegin (GL.GL_QUADS);

		gl.glNormal3f (-1, 0, 0);
		gl.glTexCoord2f (0, 1f/3);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);
		gl.glTexCoord2f (0, 2f/3);
		gl.glVertex3f (x0, y1, z0);

		gl.glNormal3f (1, 0, 0);
		gl.glTexCoord2f (0.75f, 1f/3);
		gl.glVertex3f (x1, y0, z0);
		gl.glTexCoord2f (0.75f, 2f/3);
		gl.glVertex3f (x1, y1, z0);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);

		gl.glNormal3f (0, -1, 0);
		gl.glTexCoord2f (0.25f, 0);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (0.5f, 0);
		gl.glVertex3f (x1, y0, z0);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);

		gl.glNormal3f (0, 1, 0);
		gl.glTexCoord2f (0.25f, 1);
		gl.glVertex3f (x0, y1, z0);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.5f, 1);
		gl.glVertex3f (x1, y1, z0);

		gl.glNormal3f (0, 0, -1);
		gl.glTexCoord2f (1, 1f/3);
		gl.glVertex3f (x0, y0, z0);
		gl.glTexCoord2f (1, 2f/3);
		gl.glVertex3f (x0, y1, z0);
		gl.glTexCoord2f (0.75f, 2f/3);
		gl.glVertex3f (x1, y1, z0);
		gl.glTexCoord2f (0.75f, 1f/3);
		gl.glVertex3f (x1, y0, z0);

		gl.glNormal3f (0, 0, 1);
		gl.glTexCoord2f (0.25f, 1f/3);
		gl.glVertex3f (x0, y0, z1);
		gl.glTexCoord2f (0.5f, 1f/3);
		gl.glVertex3f (x1, y0, z1);
		gl.glTexCoord2f (0.5f, 2f/3);
		gl.glVertex3f (x1, y1, z1);
		gl.glTexCoord2f (0.25f, 2f/3);
		gl.glVertex3f (x0, y1, z1);

		gl.glEnd ();
	}

	/**
	 * Prepare settings before doing any drawing calls.
	 * This function will set a texture and/or a color.
	 * If the object should be highlighted, it will switch to
	 * drawing lines instead of surfaces.
	 * 
	 * Note:
	 * Make sure that epilogue is called with the texture set
	 * to the result of this function. Otherwise errors may
	 * occur due to matrix stack overflows.
	 * 
	 * @param gl
	 * @param s
	 * @param highlight one of the constants defined in class ViewSelection
	 * @return the texture that is used to draw the object
	 */
	Texture prologue (GL gl, Shader s, int highlight)
	{
		// activate texture mapping
		Texture tex = setupTexture (gl, s);

		// get vertex color
		int color = s.getAverageColor ();

		// if texturing is enabled, use white as vertex color instead
		if (tex != null)
		{
			color = -1;
		}

		// get the drawing color and set it in opengl
		// color is an int encoding ARGB, which is separated into red, green,
		// blue and alpha
		byte red = (byte) ((color >> 16) & 0xFF);
		byte green = (byte) ((color >> 8) & 0xFF);
		byte blue = (byte) ((color >> 0) & 0xFF);
		byte alpha = (byte) ((color >> 24) & 0xFF);

		highlightPrologue (highlight, gl, red, green, blue, alpha);

		return tex;
	}

	/**
	 * Reset settings changed by prologue() to their defaults.
	 * @param gl
	 * @param s
	 * @param tex texture that was returned by prologue()
	 * @param highlight one of the constants defined in class ViewSelection
	 */
	void epilogue (GL gl, Shader s, Texture tex, int highlight)
	{
		cleanupTexture (gl, s, tex);
		highlightEpilogue (highlight, gl);
	}

	/**
	 * Draw a box from -axis to +axis using the transformation matrix t.
	 * @param axis defines size of box
	 */
	public void drawBox (float halfWidth, float halfLength, float height,
			Shader s, int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (t), 0);

		// extract axis values
		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// draw the box
		drawBoxImpl (gl, -halfWidth, -halfLength, 0, halfWidth, halfLength,
			height);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();
	}

	/**
	 * Draw a single point with the specified pixel size and color.
	 * Although OpenGL provides a function to draw points, 
	 * the point is represented by a sphere, because some graphics 
	 * cards/drivers seem to have problems setting the size for the point.
	 * @param pixelSize size of point on screen in pixels
	 */
	public void drawPoint (Tuple3f origin, int pixelSize, Tuple3f color,
			int highlight, Matrix4d t)
	{
		// check if drawing points is enabled
		if (optionShowPoints)
		{
			// get correct (and probibly derived) transformation
			t = getTransformation (t);

			// get opengl context
			GL gl = canvas.getGL ();

			// calculate scale factor for octahedron (represents point)
			// so that the requested pixel size will result
			float scale = (float) pixelSize / (float) estimateScaleAt (origin);
			Matrix4d m = new Matrix4d ();
			m.set ((double) scale, new Vector3d (origin));
			m.mul (t, m);

			// apply transformation
			gl.glPushMatrix ();
			gl.glLoadMatrixd (toGLMatrix (m), 0);

			// set the drawing color for opengl to the point color
			gl.glColor3f (color.x, color.y, color.z);

			// disable lighting for points
			// (points are used to display point lights)
			disableLighting (gl);

			// draw a sphere instead of a point
			// the problem with gl.glBegin(GL.GL_POINTS) is, that some
			// implementations
			// of opengl only support point sizes of just 1.0
			//		drawSphereImpl (gl, 4, 2);
			drawSphereImpl (gl, 8, 8);

			// enable lighting
			enableLighting (gl);

			// restore previous state
			gl.glPopMatrix ();
		}
	}

	/* (non-Javadoc)
	 * @see de.grogra.imp3d.RenderState#drawPointCloud(float[], javax.vecmath.Tuple3f, int, javax.vecmath.Matrix4d)
	 */
//	public void drawPointCloud(float[] locations, float pointSize, Tuple3f color,
//			int highlight, Matrix4d t)
//	{
//		int N = locations.length / 3;
//		final Point3f p = new Point3f();
//		if (pointSize <= 0)
//			pointSize = 3;
//		for (int i = 0; i < N; i++) {
//			p.set(locations[3*i+0], locations[3*i+1], locations[3*i+2]);
//			drawPoint(p, Math.max((int)pointSize, 1), color, RenderState.CURRENT_HIGHLIGHT, null);
//		}
//	}

	/* (non-Javadoc)
	 * @see de.grogra.imp3d.RenderState#drawPointCloud(float[], javax.vecmath.Tuple3f, int, javax.vecmath.Matrix4d)
	 */
	public void drawPointCloud(float[] locations, float pointSize, Tuple3f color,
			int highlight, Matrix4d t)
	{
		// get opengl context
		final GL gl = canvas.getGL ();
		
		// calculate number of points
		final int N = locations.length / 3;
		
		// make sure there is anything to draw
		if (N == 0)
			return;

		// obtain the FloatBuffer for the point locations
		// or create such a buffer if not found in the mapping
		FloatBuffer fb = cloudToBuffer.get(locations);
		if (fb == null) {
			fb = BufferUtil.newFloatBuffer(locations.length);
			fb.put(locations);
			fb.rewind();
			cloudToBuffer.put(locations, fb);
		}

		// get correct (and probably derived) transformation
		t = getTransformation (t);

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (t), 0);

		// set the drawing color for opengl to the point color
		if (highlight == RenderState.CURRENT_HIGHLIGHT)
		{
			highlight = curHighlight;
		}
		if (highlight != 0) {
			gl.glDisable (GL.GL_DEPTH_TEST);
			color = calculateHighlightColor(color);
		}
		gl.glColor3f (color.x, color.y, color.z);

		// disable lighting for point cloud
		disableLighting (gl);

		// draw the points from a vertex array
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, fb);
		gl.glPointSize(pointSize <= 0 ? 3 : pointSize);
		gl.glDrawArrays(GL.GL_POINTS, 0, N);
		gl.glPointSize(1.0f);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);

		// enable lighting
		enableLighting (gl);

		if (highlight != 0)
			gl.glEnable (GL.GL_DEPTH_TEST);
		
		// restore previous state
		gl.glPopMatrix ();
	}

	/**
	 * Connect the two points with a line and apply the transformation matrix.
	 */
	public void drawLine (Tuple3f origin, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) transformation
		t = getTransformation (t);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (t), 0);

		// set the drawing color for opengl to the line color
		gl.glColor3f (color.x, color.y, color.z);

		// disable lighting for lines
		disableLighting (gl);

		// draw the line
		gl.glBegin (GL.GL_LINES);
		gl.glVertex3f (origin.x, origin.y, origin.z);
		gl.glVertex3f (end.x, end.y, end.z);
		gl.glEnd ();

		// enable lighting
		enableLighting (gl);

		// restore previous state
		gl.glPopMatrix ();
	}

	// map a shader to the image it generates
	final HashMap<Object, ImageCacheItem> imageCache = new HashMap<Object, ImageCacheItem> ();

	// generated images are of that size
	static final int CACHED_IMAGE_WIDTH = 512;
	static final int CACHED_IMAGE_HEIGHT = 512;

	/**
	 * Derive information about texturing from the shader.
	 * If the shader was a Lambert shader and its diffuse ChannelMap
	 * was an ImageMap, then use it to create a texture from it.
	 * The texture that was created from the shader is returned.
	 * This texture has to be passed to cleanupTexture() to reset
	 * the OpenGL state and avoid texture matrix stack overflows.
	 * 
	 * @param s
	 * @return the texture from the shader, or null if there was none
	 */
	Texture setupTexture (GL gl, Shader s)
	{
		Texture result = null;

		// valid shaders for now are: MappedShader, Material and RGBAShader
		// where a Material is subclassed by Lambert which in turn is
		// subclassed by Phong

		// check if the shader is of type Phong or has a ImageMapProperty implementation
		if (s instanceof Phong || s instanceof ColorMapNodeProperty)
		{
			ChannelMap diffuse;
			
			// obtain the diffuse channel of a certain image-owner-candidate
			if(s instanceof Phong)
			{
				Phong lambert 				= (Phong) s;
				diffuse 					= lambert.getDiffuse ();
				
			} else {
				
				ColorMapNodeProperty lambert 	= (ColorMapNodeProperty) s;
				diffuse 					= lambert.getImageChannel ();
			}
				
			Image image = null;

			// an ImageMap just wraps an Image, so get that image
			/*if (diffuse instanceof ImageMap)
			 {
			 ImageMap diffuseImageMap = (ImageMap) diffuse;

			 ImageAdapter imageAdapter = diffuseImageMap != null ? diffuseImageMap
			 .getImageAdapter ()
			 : null;

			 // get the image from the adapter
			 image = imageAdapter != null ? imageAdapter.getImage ()
			 : null;
			 }
			 else 
			 */
			if (diffuse instanceof ColorMapNode)
			{
				// check imageCache if the shader already generated an image
				// and if that image is up to date
				//					System.out.println ("checking imageCache");
				ImageCacheItem ici = (ImageCacheItem) imageCache.get (diffuse);

				if (ici == null || diffuse.getStamp () != ici.stamp)
				{
					// cache did not contain that image
					// or it is not current anymore

					// create a new cache item if necessary
					if (ici == null)
					{
						//							System.out.println ("not found in cache");
						ici = new ImageCacheItem ();
					}
					else
					// or delete the old texture
					{
						//							System.out.println ("stamp too old");
						textureManager.deleteTexture (gl, ici.image);
					}

					// store new stamp
					ici.stamp = diffuse.getStamp ();

					// generate new image data
					ColorMapNode cmn = (ColorMapNode) diffuse;
					BufferedImage bimage = new BufferedImage (
						CACHED_IMAGE_WIDTH, CACHED_IMAGE_HEIGHT,
						BufferedImage.TYPE_INT_ARGB);
					cmn.drawImage (bimage, 1, true);
					ici.image = bimage;

					// (re)insert image into imageCache
					imageCache.put (diffuse, ici);
				}

				image = ici.image;
			}

			// check if an image was set
			if (image != null)
			{
				// convert image to an integer array of RGBA values //
				Texture texture = textureManager.getTexture (gl, image);

				// check if texture was generated
				if (texture != null)
				{
					// enable texture
					gl.glEnable (GL.GL_TEXTURE_2D);
					gl.glBindTexture (GL.GL_TEXTURE_2D, texture.index);

					// modulate rgb value with light, use alpha value from texture
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
						GL.GL_COMBINE);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB,
						GL.GL_MODULATE);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_SOURCE0_RGB,
						GL.GL_TEXTURE);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_RGB,
						GL.GL_SRC_COLOR);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_SRC1_RGB,
						GL.GL_PREVIOUS);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_OPERAND1_RGB,
						GL.GL_SRC_COLOR);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_COMBINE_ALPHA,
						GL.GL_REPLACE);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_SRC0_ALPHA,
						GL.GL_TEXTURE);
					gl.glTexEnvf (GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_ALPHA,
						GL.GL_SRC_ALPHA);

					// set texture matrix to provide correct u/v-mapping
					gl.glMatrixMode (GL.GL_TEXTURE);
					gl.glPushMatrix ();
					Matrix4d m = new Matrix4d (1, 0, 0, 0, 0, -1, 0, 1, 0, 0,
						1, 0, 0, 0, 0, 1);
					gl.glLoadMatrixd (toGLMatrix (m), 0);
					gl.glMatrixMode (GL.GL_MODELVIEW);
				}

				result = texture;
			}
		}

		return result;
	}

	/**
	 * Reset the texturing of OpenGL that was set by setupTexture().
	 * The paramter tex has to be the same value that was returned by
	 * setupTexture() previously.
	 * 
	 * @param gl
	 * @param s
	 * @param tex texture paramter that was returned by setupTexture()
	 */
	void cleanupTexture (GL gl, Shader s, Texture tex)
	{
		if (tex != null)
		{
			// restore texture matrix
			gl.glMatrixMode (GL.GL_TEXTURE);
			gl.glPopMatrix ();
			gl.glMatrixMode (GL.GL_MODELVIEW);

			// disable texturing
			gl.glDisable (GL.GL_TEXTURE_2D);
		}
	}

	public void drawParallelogram (float length, Vector3f axis, float scaleU,
			float scaleV, Shader s, int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (t), 0);

		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// calculate normal vector for surface
		Vector3f normal = new Vector3f (0, 0, length);
		normal.cross (axis, normal);

		gl.glDisable (GL.GL_CULL_FACE);
		gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 1);

		// draw the parallelogram
		gl.glBegin (GL.GL_QUADS);

		gl.glNormal3f (normal.x, normal.y, normal.z);
		gl.glTexCoord2f (0, 0);
		gl.glVertex3f (-axis.x, -axis.y, -axis.z);
		gl.glTexCoord2f (scaleU, 0);
		gl.glVertex3f (axis.x, axis.y, axis.z);
		gl.glTexCoord2f (scaleU, scaleV);
		gl.glVertex3f (axis.x, axis.y, length + axis.z);
		gl.glTexCoord2f (0, scaleV);
		gl.glVertex3f (-axis.x, -axis.y, length - axis.z);

		gl.glEnd ();

		gl.glEnable (GL.GL_CULL_FACE);
		gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 0);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();
	}

	// static final float PLANE_SIZE = 100000.0f;
	static final float PLANE_SIZE = 100.0f;

	void drawPlaneImpl (GL gl, int size, int uc, int vc)
	{
		gl.glBegin (GL.GL_QUAD_STRIP);
		gl.glNormal3f (0, 0, 1);
		for (int v = 0; v < vc; v++)
		{
			float y0 = (float) size * ((float) v / (float) vc - 0.5f);
			float y1 = (float) size * ((float) (v + 1) / (float) vc - 0.5f);
			for (int u = 0; u <= uc; u++)
			{
				float x = (float) size * ((float) u / (float) uc - 0.5f);
				gl.glTexCoord2f (u, v + 1);
				gl.glVertex3f (x, y1, 0);
				gl.glTexCoord2f (u, v);
				gl.glVertex3f (x, y0, 0);
			}
		}
		gl.glEnd ();
	}

	/**
	 * Draw an x/y plane.
	 * 
	 */
	public void drawPlane (Shader s, int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (t), 0);

		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// draw the plane
		drawPlaneImpl (gl, (int) PLANE_SIZE, 10, 10);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();
	}

	/**
	 * Display list number for spheres. There will be display lists generated
	 * for numbers dlSphere to (dlSphere + LOD_LEVELS - 1). A value of 0 for
	 * dlSphere means, that no display list is available.
	 */
	protected int dlSphere = 0;

	/**
	 * Generate a vertex on a unit sphere (radius is 1.0f). The angle phi
	 * selects the direction in the x/y-plane, if the angle is zero the
	 * direction is (1/0/0). The angle theta then selects the direction in the
	 * plane made of the direction and the z-axis. Possible angles for phi are
	 * -pi to +pi and for theta are -pi/2 to +pi/2.
	 * 
	 * @param phi
	 * @param theta
	 * @return
	 */
	private Vector3f genVertexSphere (float phi, float theta)
	{
		float cosPhi = (float) Math.cos (phi);
		float sinPhi = (float) Math.sin (phi);
		float cosTheta = (float) Math.cos (theta);
		float sinTheta = (float) Math.sin (theta);
		float x = cosPhi * cosTheta;
		float y = sinPhi * cosTheta;
		float z = sinTheta;
		return new Vector3f (x, y, z);
	}

	/**
	 * 
	 * Generate the opengl commands to generate the sphere data. This is used to
	 * create display list or to directly draw the sphere.
	 * 
	 * @param gl
	 * @param uCount
	 * @param vCount
	 */
	void drawSphereImpl (GL gl, int uCount, int vCount)
	{
		// for each strip
		for (int v = -vCount; v < vCount; v += 2)
		{

			float theta1 = (float) (Math.PI * (float) (v + 2) / (float) vCount / 2.0f);
			float theta2 = (float) (Math.PI * (float) v / (float) vCount / 2.0f);

			// start quad strip
			gl.glBegin (GL.GL_QUAD_STRIP);
			// for each vertical slice
			for (int u = 0; u <= uCount; u++)
			{
				// calculate next vertex
				float phi = (float) (Math.PI * 2 * (float) u / (float) uCount);
				Vector3f v1 = genVertexSphere (phi, theta1);
				Vector3f v2 = genVertexSphere (phi, theta2);

				// generate strip data
				gl.glNormal3f (v1.x, v1.y, v1.z);
				gl.glTexCoord2f ((float) u / (float) uCount, 0.5f
					+ (float) (v + 2) / (float) vCount / 2f);
				gl.glVertex3f (v1.x, v1.y, v1.z);
				gl.glNormal3f (v2.x, v2.y, v2.z);
				gl.glTexCoord2f ((float) u / (float) uCount, 0.5f + (float) v
					/ (float) vCount / 2f);
				gl.glVertex3f (v2.x, v2.y, v2.z);
			}
			gl.glEnd ();
		}
	}

	class SphereDisplayListRenderable extends DisplayListRenderable
	{
		GL gl;

		SphereDisplayListRenderable (GL gl)
		{
			this.gl = gl;
		}

		void render (float lod)
		{
			int uCount = 8 + (int) (24 * lod);
			int vCount = uCount;
			drawSphereImpl (gl, uCount, vCount);
		}
	}

	/**
	 * Draw a sphere with the specified radius around the origin (0/0/0).
	 * 
	 */
	public void drawSphere (float radius, Shader s, int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// calculate level of detail
		float lod = getLOD (t, radius);

		// calculate scale matrix for sphere
		Matrix4d m = new Matrix4d ();
		m.set (radius);
		m.mul (t, m);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (m), 0);

		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// draw the sphere
		dlSphere = drawWithDisplayList (dlSphere, gl,
			new SphereDisplayListRenderable (gl), LOD_LEVELS, lod);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();

	}
	
	
	/**
	 * Display list number for supershapes. There will be display lists generated
	 * for numbers dlSupershape to (dlSupershape + LOD_LEVELS - 1). A value of 0 for
	 * dlSupershape means, that no display list is available.
	 */
	protected int dlSupershape = 0;	
		
	
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
	 * 
	 * Generate the opengl commands to generate the supershape data. This is used to
	 * create display list or to directly draw the supershape.
	 * 
	 * @param gl
	 * @param uCount
	 * @param vCount
	 */
	private void drawSupershapeImpl (GL gl, int uCount, int vCount, float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23)
	{	
		final Vector3f v1 = new Vector3f();
		final Vector3f v2 = new Vector3f();
		
		// for each strip
		for (int v = -vCount; v < vCount; v += 2)
		{

			float theta1 = (float) (Math.PI * (float) (v + 2) / (float) vCount / 2.0f);
			float theta2 = (float) (Math.PI * (float) v / (float) vCount / 2.0f);

			// start quad strip
			gl.glBegin (GL.GL_QUAD_STRIP);
			// for each vertical slice
			for (int u = 0; u <= uCount; u++)
			{
				// calculate next vertex
				float phi = (float) (Math.PI * 2 * (float) u / (float) uCount);
				genVertexSupershape (v1, phi, theta1, a, b, m1, n11, n12, n13, m2, n21, n22, n23);
				genVertexSupershape (v2, phi, theta2, a, b, m1, n11, n12, n13, m2, n21, n22, n23);

				// generate strip data
				gl.glNormal3f (v1.x, v1.y, v1.z);
				gl.glTexCoord2f ((float) u / (float) uCount, 0.5f
					+ (float) (v + 2) / (float) vCount / 2f);
				gl.glVertex3f (v1.x, v1.y, v1.z);
				gl.glNormal3f (v2.x, v2.y, v2.z);
				gl.glTexCoord2f ((float) u / (float) uCount, 0.5f + (float) v
					/ (float) vCount / 2f);
				gl.glVertex3f (v2.x, v2.y, v2.z);
			}
			gl.glEnd ();
		}
	}

	
	class SupershapeDisplayListRenderable extends DisplayListRenderable
	{
		GL gl;
		float a, b;
		float m1, n11, n12, n13, m2, n21, n22, n23;
		
		SupershapeDisplayListRenderable (GL gl, float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23)
		{
			this.gl = gl;
			this.a = a;
			this.b = b;
			this.m1 = m1;
			this.n11 = n11;
			this.n12 = n12;
			this.n13 = n13;
			this.m2 = m2;
			this.n21 = n21;
			this.n22 = n22;
			this.n23 = n23;
		}

		void render (float lod)
		{
			// calculate segment count depending on level of detail
			float factor = (m1+m2)/2f;
			final int uvCount = (int)(factor + factor*3 * lod);

			// draw the supershape
			drawSupershapeImpl (gl, uvCount, uvCount, a, b, m1, n11, n12, n13, m2, n21, n22, n23);
		}
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
	public void drawSupershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23, Shader s, int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// calculate level of detail
		float lod = getLOD (t, Math.max (a, b));

		// rotate axis vector to z-axis
		Matrix3f ma1 = new Matrix3f ();
		ma1.m00 = 1;
		ma1.m11 = (Math.max (a, b) > 0) ? 1 : -1;
		ma1.m22 = Math.max (a, b);
		Matrix4d ma2 = new Matrix4d ();
		ma2.set (ma1);
		ma2.mul (t, ma2);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (ma2), 0);

		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// draw the supershape
		dlSupershape = drawWithDisplayList (dlSupershape, gl,
			new SupershapeDisplayListRenderable (gl, a, b, m1, n11, n12, n13, m2, n21, n22, n23), 
			LOD_LEVELS, lod);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();
	}	

	
	void drawFrustumImpl (GL gl, int uCount, boolean topClosed,
			float topRadius, boolean baseClosed, float baseRadius)
	{ drawFrustumImpl (gl, uCount, topClosed, topRadius, baseClosed, baseRadius, 1); }
	
	void drawFrustumImpl (GL gl, int uCount, boolean topClosed,
			float topRadius, boolean baseClosed, float baseRadius, float scaleV)
	{
		// draw connection from top to bottom
		gl.glBegin (GL.GL_QUAD_STRIP);
		for (int u = 0; u <= uCount; u++)
		{
			float phi = (float) (2.0f * Math.PI * (float) u / (float) uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);
			gl.glNormal3f (cosPhi, sinPhi, 0);
			gl.glTexCoord2f (phi / (float) (2.0f * Math.PI), scaleV);
			gl.glVertex3f ((topRadius * cosPhi),
				(topRadius * sinPhi), 1);
			gl.glTexCoord2f (phi / (float) (2.0f * Math.PI), 0);
			gl.glVertex3f ((float) (baseRadius * cosPhi),
				(float) (baseRadius * sinPhi), 0);
		}
		gl.glEnd ();

		// draw base circle
		if (baseClosed)
		{
			gl.glBegin (GL.GL_TRIANGLE_FAN);
			gl.glNormal3f (0, 0, -1);
			gl.glTexCoord2f (0.5f, 0.5f);
			gl.glVertex3f (0, 0, 0);
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * (float) -u / (float) uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				gl.glTexCoord2f (cosPhi / 2 + 0.5f, sinPhi / 2 + 0.5f);
				gl.glVertex3f ((float) (baseRadius * cosPhi),
					(float) (baseRadius * sinPhi), 0);
			}
			gl.glEnd ();
		}

		// draw top circle
		if (topClosed)
		{
			gl.glBegin (GL.GL_TRIANGLE_FAN);
			gl.glNormal3f (0, 0, +1);
			gl.glTexCoord2f (0.5f, 0.5f);
			gl.glVertex3f (0, 0, 1);
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * (float) u / (float) uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				gl.glTexCoord2f (cosPhi / 2 + 0.5f, sinPhi / 2 + 0.5f);
				gl.glVertex3f ((float) (topRadius * cosPhi),
					(float) (topRadius * sinPhi), 1);
			}
			gl.glEnd ();
		}
	}

	/**
	 * Can be used to draw cylinders and the like. The frustum has a cirle at
	 * the top and bottom, which are connected with a surface. The center of the
	 * bottom circle is (0/0/0), the center of the top circle is axis. Both
	 * circles are parallel to the x/y plane.
	 * 
	 */
	public void drawFrustum (float length, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader s,
			int highlight, Matrix4d t)
	{
		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		// calculate level of detail
		float lod = getLOD (t, Math.max (baseRadius, topRadius));

		// rotate axis vector to z-axis
		Matrix3f m = new Matrix3f ();
		m.m00 = 1;
		m.m11 = (length > 0) ? 1 : -1;
		m.m22 = length;
		Matrix4d m2 = new Matrix4d ();
		m2.set (m);
		m2.mul (t, m2);

		// get opengl context
		GL gl = canvas.getGL ();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (toGLMatrix (m2), 0);

		// calculate segment count depending on level of detail
		int uCount = 5 + (int) (25 * lod);

		// setup color/texture
		Texture tex = prologue (gl, s, highlight);

		// draw the frustum //
		drawFrustumImpl (gl, uCount, topClosed, topRadius, baseClosed,
			baseRadius, scaleV);

		// cleanup color/texture
		epilogue (gl, s, tex, highlight);

		// restore previous state
		gl.glPopMatrix ();
	}

	public int drawWithDisplayList (int index, GL gl,
			DisplayListRenderable dlr, int levels, float lod)
	{
		// check if display list was already created, if not (i.e. dlr.index ==
		// 0) then create one
		if (index == 0)
		{
			// allocate display list numbers
			index = gl.glGenLists (levels);

			// check if allocation failed
			if (index == 0)
			{
				// log a warning about failed allocation
				Logger logger = getView ().getWorkbench ().getLogger ();
				logger.warning ("failed to allocate display list indices");
			}
			else
			{
				// generate display lists for all levels of detail
				for (int i = 0; i < levels; i++)
				{
					// create a new display list for each lod
					gl.glNewList (index + i, GL.GL_COMPILE);

					// generate data
					dlr.render ((float) i / (float) levels);

					gl.glEndList ();
				}
			}
		}

		// check if display list was created, then use it to draw
		if (index != 0)
		{
			int list = index + (int) Math.min (levels - 1, lod * levels);
			gl.glCallList (list);
		}
		else
		{
			// display list was not available, so perform manual draw

			// decrease detail level
			lod /= 2;

			dlr.render (lod);
		}

		return index;
	}

	public void drawPolygons (Polygonizable pz, Object obj, boolean asNode,
			Shader s, int highlight, Matrix4d t)
	{
		if ((polyCache != null)
			&& (polyCache.getGraphState () != getRenderGraphState ()))
		{
			polyCache.clear ();
			polyCache = null;
		}
		if (polyCache == null)
		{
			polyCache = new PolygonizationCache (getRenderGraphState (),
				Polygonization.COMPUTE_NORMALS | Polygonization.COMPUTE_UV, 10,
				true);
		}

		// get correct (and probibly derived) shader and transformation
		s = getShader (s);
		t = getTransformation (t);

		PolygonArray polys = polyCache.get (obj, asNode, pz);

		// obtain the buffer(s) for the polygons
		// or create such buffers if not already done so
		CacheData data;
		if (polys.wasCleared () || !(polys.userObject instanceof CacheData))
		{
			data = new CacheData ();
			data.polygonSize = polys.polygons.size;

			if (data.polygonSize > 0)
			{
				if (supportsVBO)
				{
					// get GL instance
					GL gl = canvas.getGL ();

					// create new buffer ID
					data.id = new int[1];
					gl.glGenBuffersARB (1, data.id, 0);

					// bind the buffer
					gl.glBindBufferARB (GL.GL_ARRAY_BUFFER_ARB, data.id[0]);

					// allocate memory for the buffer
					int bufferSize = 0; // size of the buffer in bytes
					data.vsize = polys.vertices.size * BufferUtil.SIZEOF_FLOAT;
					bufferSize += data.vsize;
					data.nsize = polys.normals.size * BufferUtil.SIZEOF_BYTE;
					bufferSize += data.nsize;
					data.tsize = polys.uv.size * BufferUtil.SIZEOF_FLOAT;
					bufferSize += data.tsize;
					gl.glBufferDataARB (GL.GL_ARRAY_BUFFER_ARB, bufferSize,
						null, GL.GL_STATIC_DRAW_ARB);

					// map the buffer
					ByteBuffer bb = gl.glMapBufferARB (GL.GL_ARRAY_BUFFER_ARB,
						GL.GL_WRITE_ONLY_ARB);

					// fill in vertices and texcoords as floats 
					bb.position (bb.asFloatBuffer ().put (
						polys.vertices.elements, 0, polys.vertices.size).put (
						polys.uv.elements, 0, polys.uv.size).position () * 4);
					// fill in normals 
					bb.put (polys.normals.elements, 0, polys.normals.size);

					// unmap the buffer
					gl.glUnmapBufferARB (GL.GL_ARRAY_BUFFER_ARB);

					// unbind the buffer
					gl.glBindBufferARB (GL.GL_ARRAY_BUFFER_ARB, 0);

					// fill index buffer
					data.ib = newByteBuffer (polys.polygons.size * 4)
						.asIntBuffer ();
					polys.polygons.writeTo (data.ib);

				}
				else
				{
					// convert arrays to buffers
					data.ib = newByteBuffer (polys.polygons.size * 4)
						.asIntBuffer ();
					polys.polygons.writeTo (data.ib);

					data.vb = newByteBuffer (polys.vertices.size * 4)
						.asFloatBuffer ();
					polys.vertices.writeTo (data.vb);

					data.nb = newByteBuffer (polys.normals.size);
					polys.normals.writeTo (data.nb);

					data.uvb = newByteBuffer (polys.uv.size * 4)
						.asFloatBuffer ();
					polys.uv.writeTo (data.uvb);
				}
			}
			polys.userObject = data;
		}
		else
		{
			data = (CacheData) polys.userObject;
		}

		if (data.polygonSize > 0)
		{
			if (supportsVBO)
			{
				data.ib.rewind ();
			}
			else
			{
				data.ib.rewind ();
				data.vb.rewind ();
				data.nb.rewind ();
				data.uvb.rewind ();
			}

			// get GL instance
			GL gl = canvas.getGL ();

			switch (polys.visibleSides)
			{
				case Attributes.VISIBLE_SIDES_BACK:
					gl.glCullFace (GL.GL_FRONT);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 1);
					break;
				case Attributes.VISIBLE_SIDES_BOTH:
					gl.glDisable (GL.GL_CULL_FACE);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 1);
					break;
			}

			// apply transformation
			gl.glMatrixMode (GL.GL_MODELVIEW);
			gl.glPushMatrix ();
			gl.glLoadMatrixd (toGLMatrix (t), 0);

			// setup highlight/texture
			Texture tex = prologue (gl, s, highlight);

			// enable client states for vertex, normal und texcoord
			gl.glEnableClientState (GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState (GL.GL_NORMAL_ARRAY);
			gl.glEnableClientState (GL.GL_TEXTURE_COORD_ARRAY);

			if (supportsVBO)
			{
				// render using VBO

				// bind the VBO
				gl.glBindBufferARB (GL.GL_ARRAY_BUFFER_ARB, data.id[0]);

				// set offsets into VBO for vertex, normal and texcoord
				int offset = 0;
				gl.glVertexPointer (polys.dimension, GL.GL_FLOAT, 0, offset);
				offset += data.vsize;
				gl.glTexCoordPointer (2, GL.GL_FLOAT, 0, offset);
				offset += data.tsize;
				gl.glNormalPointer (GL.GL_BYTE, 0, offset);
				offset += data.nsize;

				// draw the triangles/quads using the index list
				gl.glDrawElements ((polys.edgeCount == 3) ? GL.GL_TRIANGLES
						: GL.GL_QUADS, data.polygonSize, GL.GL_UNSIGNED_INT,
					data.ib);

				// unbind the buffer
				gl.glBindBufferARB (GL.GL_ARRAY_BUFFER_ARB, 0);
			}
			else
			{
				// render using vertex arrays

				// draw the object
				gl.glVertexPointer (polys.dimension, GL.GL_FLOAT, 0, data.vb);
				gl.glNormalPointer (GL.GL_BYTE, 0, data.nb);
				gl.glTexCoordPointer (2, GL.GL_FLOAT, 0, data.uvb);

				gl.glDrawElements ((polys.edgeCount == 3) ? GL.GL_TRIANGLES
						: GL.GL_QUADS, data.polygonSize, GL.GL_UNSIGNED_INT,
					data.ib);
			}

			// disable client states
			gl.glDisableClientState (GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState (GL.GL_NORMAL_ARRAY);
			gl.glDisableClientState (GL.GL_TEXTURE_COORD_ARRAY);

			epilogue (gl, s, tex, highlight);

			switch (polys.visibleSides)
			{
				case Attributes.VISIBLE_SIDES_BACK:
					gl.glCullFace (GL.GL_BACK);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 0);
					break;
				case Attributes.VISIBLE_SIDES_BOTH:
					gl.glEnable (GL.GL_CULL_FACE);
					gl.glLightModeli (GL.GL_LIGHT_MODEL_TWO_SIDE, 0);
					break;
			}

			// restore previous state
			gl.glMatrixMode (GL.GL_MODELVIEW);
			gl.glPopMatrix ();
		}
	}

	/**
	 * Draw a rectangle from (x/y) to (x+w/y+h) with the specified color.
	 * 
	 */
	public void drawRectangle (int x, int y, int w, int h, Tuple3f color)
	{
		Overlay overlay = new Overlay(canvas);
		Graphics2D g = overlay.createGraphics();
		
		g.setColor(new Color(color.x, color.y, color.z));
		g.drawRect(x, y, w, h);
		
		overlay.beginRendering();
		overlay.draw(0, 0, canvas.getWidth(), canvas.getHeight());
		overlay.endRendering();
		
//		Graphics g = canvas.getGraphics ();
//		g.setColor (new Color (color.x, color.y, color.z));
//		g.drawRect (x, y, w, h);
	}

	/**
	 * Draw a filled rectangle from (x/y) to (x+w/y+h) with the specified color.
	 * 
	 */
	public void fillRectangle (int x, int y, int w, int h, Tuple3f color)
	{
		Overlay overlay = new Overlay(canvas);
		Graphics2D g = overlay.createGraphics();
		
		g.setColor(new Color(color.x, color.y, color.z));
		g.fillRect(x, y, w, h);
		
		overlay.beginRendering();
		overlay.draw(0, 0, canvas.getWidth(), canvas.getHeight());
		overlay.endRendering();
		
//		Graphics g = canvas.getGraphics ();
//		g.setColor (new Color (color.x, color.y, color.z));
//		g.fillRect (x, y, w, h);
	}

	/**
	 * Draw a string with the selected font and color at location (x/y).
	 * 
	 */
	public void drawString (int x, int y, String text, Font font, Tuple3f color)
	{
		// look up text renderer
		TextRenderer textRenderer = textRenderers.get(font);
		
		// if none was created yet for that font then create it on demand
		if (textRenderer == null)
		{
			textRenderer = new TextRenderer(font, true, true);
			textRenderers.put(font, textRenderer);
		}
		
		int w = canvas.getWidth();
		int h = canvas.getHeight();
	    textRenderer.beginRendering(w, h);
	    textRenderer.setColor(color.x, color.y, color.z, 1.0f);
	    textRenderer.draw(text, x, h - y);
	    textRenderer.endRendering();

//		Graphics g = canvas.getGraphics ();
//		g.setFont (font);
//		g.setColor (new Color (color.x, color.y, color.z));
//		g.drawString (text, x, y);
	}

	public static final Type RENDER_MODE = new EnumerationType (
		"gl.rendermode", IMP3D.I18N, 3);

	protected void optionValueChanged (String name, Object value)
	{
		// System.out.println (name + " = " + value);

		// check if "Show Points" changed
		if (OPTION_NAME_SHOW_POINTS.equals (name))
		{
			optionShowPoints = Boolean.TRUE.equals (value);
			repaint (ALL);
		}
		else if (OPTION_NAME_LIGHTING.equals (name))
		{
			lightingOn = Boolean.TRUE.equals (value);
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
		else
		{
			super.optionValueChanged (name, value);
		}
	}

	void highlightPrologue (int highlight, GL gl, byte red, byte green,
			byte blue, byte alpha)
	{
		if (highlight == RenderState.CURRENT_HIGHLIGHT)
		{
			highlight = curHighlight;
		}

		// check if object was selected or mouse is over object
		// then switch to wireframe and select inverted color
		if (highlight != 0)
		{
			gl.glPolygonMode (GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			gl.glDisable (GL.GL_DEPTH_TEST);
			gl.glColor4ub ((byte) (0xCF - red), (byte) (0xCF - green),
				(byte) (0xCF - blue), alpha);
		}
		else
		{
			gl.glColor4ub (red, green, blue, alpha);
		}
	}

	void highlightEpilogue (int highlight, GL gl)
	{
		if (highlight == RenderState.CURRENT_HIGHLIGHT)
		{
			highlight = curHighlight;
		}

		// check if object was selected or mouse is over object
		// then restore polygon mode
		if (highlight != 0)
		{
			gl.glPolygonMode (GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			gl.glEnable (GL.GL_DEPTH_TEST);
		}
	}

	public static boolean isExtensionSupported (GL gl, String extension)
	{
		boolean result = false;

		// Extension names should not have spaces
		if (extension.indexOf (' ') < 0)
		{

			// get extensions
			String extensions = gl.glGetString (GL.GL_EXTENSIONS);

			// check if extension is contained in extensions string
			// it must be surrounded by spaces or string borders
			int index = extensions.indexOf (extension);
			if (index >= 0)
			{
				if (index == 0 || extensions.charAt (index - 1) == ' ')
				{
					int index2 = index + extension.length ();
					if (index2 >= extensions.length ()
						|| extensions.charAt (index2) == ' ')
					{
						result = true;
					}
				}
			}

		}

		return result;
	}

	static BufferedImage convert(Image im) {
		BufferedImage bi;
		if (im instanceof BufferedImage) {
			bi = (BufferedImage)im;
		} else {
			bi = new BufferedImage(im.getWidth(null), im.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			Graphics bg = bi.createGraphics();
			bg.drawImage(im, 0, 0, null);
			bg.dispose();
		}
		return bi;
	}
	
	private static final float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	private static final float green[] = { 0.0f, 1.0f, 0.0f, 1.0f };
	private static final float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };
	private static final float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	protected final int[] lineWidth = new int[1];
	protected final Color3f gridColor = new Color3f();
	protected final Point3f startPoint = new Point3f(), endPoint = new Point3f();
	private final GLUT glut = new GLUT();
	
	void drawGrid(GL gl) {
		gridColor.set(optionGridColorR, optionGridColorG, optionGridColorB);
		float gridSize = optionGridDimension * optionGridSpacing;
		for (float i = -gridSize; i <= gridSize; i+=optionGridSpacing) {
			for (float j = -gridSize; j <= gridSize; j+=optionGridSpacing) {
				startPoint.set(i,j,0); endPoint.set(-i,j,0);
				drawLine (startPoint, endPoint, gridColor, 0, null);
				startPoint.set(i,j,0); endPoint.set(i,-j,0);
				drawLine (startPoint, endPoint, gridColor, 0, null);
			}
		}
		gl.glGetIntegerv(GL.GL_LINE_WIDTH, lineWidth, 0);
		gl.glLineWidth(3);
		startPoint.set(gridSize,0,0); endPoint.set(-gridSize,0,0);
		drawLine (startPoint, endPoint, gridColor, 0, null);
		startPoint.set(0,gridSize,0); endPoint.set(0,-gridSize,0);
		drawLine (startPoint, endPoint, gridColor, 0, null);
		gl.glLineWidth(lineWidth[0]);
	}

	private final byte[] lightParams = new byte[1];
	private final float[] lightPositionAxes = new float[]{0.0f, 0.0f, 5.0f, 1.0f};
	private final float[] lightPositionAxesNames = new float[]{0.0f, 5.0f, 5.0f, 1.0f};
	private final AxisAngle4d rot = new AxisAngle4d();
	
	protected void drawAxes(GL gl, Camera c) {
		
		// set new view
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-5, 5, -5, 5, -100, 100);
		gl.glViewport(0, 0, 100, 100);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		// set other parameters
		gl.glDisable(GL.GL_COLOR_MATERIAL);		
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glGetBooleanv(GL.GL_LIGHTING, lightParams, 0);
		gl.glEnable (GL.GL_LIGHTING);
		gl.glPushMatrix();
		
		// set light for axes
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPositionAxes, 0);
		gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, 180.0f);
		
		// calculate view rotation	
		c = ((View3D) getView ()).getCamera ();
		
		// set rotation to axes scene
		rot.set(c.getWorldToViewTransformation());
		gl.glRotated(rot.angle * 180.0 / Math.PI, rot.x, rot.y, rot.z);

		// draw x-axis
		gl.glPushMatrix();
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, red, 0);
		gl.glRotatef(90, 0, 1, 0);
		gl.glPushMatrix();
		gl.glScalef(1, 1, 2.5f);
		drawFrustumImpl(gl, 16, true, 0.1f, true, 0.1f);
		gl.glPopMatrix();
		gl.glTranslatef(0, 0, 2.5f);
		drawFrustumImpl(gl, 16, true, 0, true, 0.5f);
		gl.glPopMatrix();			
		
		// draw y-axis
		gl.glPushMatrix();
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, blue, 0);
		gl.glRotatef(-90, 1, 0, 0);
		gl.glPushMatrix();
		gl.glScalef(1, 1, 2.5f);
		drawFrustumImpl(gl, 16, true, 0.1f, true, 0.1f);
		gl.glPopMatrix();
		gl.glTranslatef(0, 0, 2.5f);
		drawFrustumImpl(gl, 16, true, 0, true, 0.5f);
		gl.glPopMatrix();
		
		// draw z-axis
		gl.glPushMatrix();
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, green, 0);
		gl.glPushMatrix();
		gl.glScalef(1, 1, 2.5f);
		drawFrustumImpl(gl, 16, true, 0.1f, true, 0.1f);
		gl.glPopMatrix();
		gl.glTranslatef(0, 0, 2.5f);
		drawFrustumImpl(gl, 16, true, 0, true, 0.5f);
		gl.glPopMatrix();

		if (optionShowAxesNames) {
			// set light for text at axes
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPositionAxesNames, 0);
			
			// text at x-axis
			gl.glPushMatrix(); 
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, red, 0);
			gl.glTranslatef(4f, 0, -0.3f);
			gl.glRasterPos3d(0, 0, 0);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "x");
			gl.glPopMatrix();
	
			// text at y-axis
			gl.glPushMatrix(); 
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, blue, 0);
			gl.glTranslatef(0, 4f, -0.3f);
			gl.glRasterPos3d(0, 0, 0);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "y");
			gl.glPopMatrix();
			
			// text at z-axis
			gl.glPushMatrix(); 
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, green, 0);
			gl.glTranslatef(0, 0, 4f);
			gl.glRasterPos3d(0, 0, 0);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "z");
			gl.glPopMatrix();
		}
		
		// restore old parameters
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glPopMatrix();
		if (lightParams[0] == 0)
			gl.glDisable (GL.GL_LIGHTING);
		else
			gl.glEnable (GL.GL_LIGHTING);
		
		// restore old view
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, getView().getSize().width, getView().getSize().height);
		float hh = (float) getView().getSize().height / (float) getView().getSize().width;
		gl.glFrustum(-1.0f, 1.0f, -hh, hh, 5.0f, 600.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	
	protected void drawDisplaySize(GL gl, Camera c) {
		int width = getView().getSize().width;
		int height = getView().getSize().height;
		

		
		// set new view
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-5, 5, -5, 5, 0, 100);
		gl.glViewport(0, height - 30, 100, 30);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		// set other parameters
		gl.glDisable(GL.GL_COLOR_MATERIAL);		
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
					    
		// text
		gl.glPushMatrix(); 
		gl.glLoadIdentity();
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, black, 0);
		gl.glRasterPos3d(-4, 0, 0);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, width + " x " + height);
				
		// restore old parameters
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glPopMatrix();
		
		// restore old view
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width, height);
		float hh = (float) height / (float) width;
		gl.glFrustum(-1.0f, 1.0f, -hh, hh, 5.0f, 600.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
	}
	
	
	private static Tuple3f calculateHighlightColor(Tuple3f color)
	{
//		java.awt.Color c = new Color(color.x, color.y, color.z);
//		float[] hsb = java.awt.Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
//		hsb[0] += 0.5f;
//		c = java.awt.Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
//		return new Color3f(c);
		
		int red = (int) (255 * color.x + 0.5);
		int green = (int) (255 * color.y + 0.5);
		int blue = (int) (255 * color.z + 0.5);
		red ^= -1;
		green ^= -1;
		blue ^= -1;
		return new Color3f((red & 0xFF) / 255f, (green & 0xFF) / 255f, (blue & 0xFF) / 255f);
	}
	
	public void drawFrustumIrregular(float length, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topclosed, 
			float scaleV, Shader s, int highlight, Matrix4d t)
	{
	}

}


