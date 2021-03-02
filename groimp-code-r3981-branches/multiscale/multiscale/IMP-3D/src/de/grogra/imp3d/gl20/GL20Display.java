package de.grogra.imp3d.gl20;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JPanel;
import javax.vecmath.*;

import de.grogra.graph.*;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.awt.ViewComponentAdapter;
import de.grogra.imp.edit.Tool;
import de.grogra.imp.View;
import de.grogra.imp3d.Camera;
import de.grogra.imp3d.gl20.GL20DisplayVisitor;
import de.grogra.imp3d.View3D;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.EventListener;
import de.grogra.util.Lock;
import de.grogra.util.LockProtectedRunnable;
import de.grogra.util.Utils;
import de.grogra.xl.lang.ObjectConsumer;

public class GL20Display extends ViewComponentAdapter implements GLEventListener {
	private JPanel wrapperPanel;
	private GLCanvas glCanvas;
	
	private Lock retainedLock = null;
	private boolean disableRetain = false;
	private Object lockMutex = new Object ();
	
	private volatile int repaintFlags;
	
	/**
	 * Set to <code>true</code> by {@link #reshape} in order to indicate that
	 * the size of the canvas has changed.
	 */
	private volatile boolean reshaped = true;		

	private GL20DisplayVisitor displayVisitor = new GL20DisplayVisitor();

	public void initView(View view,EventListener listener) {
		wrapperPanel = new JPanel (new GridLayout (1, 1))
		{
			// just for 'warning free' code
			private static final long serialVersionUID = 1L;

			public void addNotify ()
			{
				super.addNotify ();
				if (glCanvas == null)
				{
					GLCapabilities glCaps = new GLCapabilities ();
					glCaps.setDoubleBuffered (true);
					glCaps.setHardwareAccelerated (true);
					GraphicsConfiguration gc = getGraphicsConfiguration ();
					glCanvas = new GLCanvas (glCaps, null, null,
						(gc != null) ? gc.getDevice () : null);

					glCanvas.addGLEventListener (GL20Display.this);
					installListeners(glCanvas);

					// Now add the GLCanvas to the visible wrapper.
					wrapperPanel.add (glCanvas);
				}
			}

			public void removeNotify ()
			{
				super.removeNotify ();
				if (glCanvas != null)
				{
					// TODO un-install listener
					wrapperPanel.remove(glCanvas);
					glCanvas = null;
				}
			}
		};
		wrapperPanel.setMinimumSize (new Dimension (0, 0));
		wrapperPanel.setPreferredSize (new Dimension (640, 480));
		super.initView(view, listener);
	}

	public void initRender(int flags) {
		// we have nothing to initialize
	}
		
	public void invokeRender(final int flags) {
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
							glCanvas.display ();
						}
					}
				}, false);
	}

	public View3D getView3D() {
		return (View3D) getView();
	}

	@Override
	protected ImageObserver getObserverForRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void render(int flags) throws InterruptedException {
		GL20GfxServer gfxServer = GL20GfxServer.getInstance();
		GL currentGL = glCanvas.getGL();

		View3D view3D = getView3D();
		int canvasWidth = glCanvas.getWidth();
		int canvasHeight = glCanvas.getHeight();
		view3D.getCanvasCamera().setDimension(canvasWidth, canvasHeight);

		// set up projection matrices
		Camera camera = view3D.getCamera();
		double canvasAspectRatio = (double)canvasWidth / (double)canvasHeight;
		Matrix4d viewToClipMatrix = new Matrix4d();
		camera.getViewToClipTransformation(viewToClipMatrix);
		viewToClipMatrix.mul(new Matrix4d(	1.0, 0.0, 0.0, 0.0,
											0.0, canvasAspectRatio, 0.0, 0.0,
											0.0, 0.0, 1.0, 0.0,
											0.0, 0.0, 0.0, 1.0));
		gfxServer.setViewToClipMatrix(viewToClipMatrix);
		gfxServer.setWorldToViewMatrix(camera.getWorldToViewTransformation());
		
		gfxServer.beginScene(currentGL);		

		// first walk through the scene graph and collect all objects without tools
		Graph sceneGraph = getView().getGraph();
		displayVisitor.initialize(getRenderGraphState(), getView3D(), false);
		sceneGraph.accept(null, displayVisitor, null);
		
		// second walk through the scene graph to collect all active tools
		Tool tools = getView ().getActiveTool ();
		if (tools != null)
		{
			displayVisitor.initialize(GraphManager.STATIC_STATE, getView3D(), true);
			ArrayPath path = new ArrayPath(getView().getGraph());
			path.clear (GraphManager.STATIC);
			for (int i = 0; i < tools.getToolCount (); i++)
			{
				GraphManager.acceptGraph (tools.getRoot (i), displayVisitor, path);
			}
		}

		gfxServer.endScene();
	}

	public RenderedImage getSnapshot() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getComponent() {
		// TODO Auto-generated method stub
		return wrapperPanel;
	}

	// ------------------------------------------------------------------------
	// GLEventListener methods
	// ------------------------------------------------------------------------
	public void display(GLAutoDrawable arg0) {
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

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		reshaped = true;
	}

	public void makeSnapshot(ObjectConsumer<? super RenderedImage> callback) {
		// TODO Auto-generated method stub
		
	}
}