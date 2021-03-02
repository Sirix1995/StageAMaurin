
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

package de.grogra.imp.awt;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import javax.vecmath.Tuple3f;
import de.grogra.graph.GraphState;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.awt.*;
import de.grogra.pf.ui.edit.*;
import de.grogra.util.*;
import de.grogra.imp.*;

public abstract class ViewComponentAdapter
	implements ViewComponent, Runnable, Selectable
{
	/**
	 * Bit mask for {@link #renderFlags} indicating that this
	 * view is currently rendering.
	 */
	protected static final int RENDERING = ViewComponent.MIN_USER_FLAG;
	
	/**
	 * Bit mask for {@link #renderFlags} indicating that this
	 * view is currently disposing.
	 */
	protected static final int DISPOSING = RENDERING << 1;
	
	/**
	 * Bit mask for {@link #renderFlags} indicating that this
	 * view is disposed.
	 */
	protected static final int DISPOSED = DISPOSING << 1;

	protected static final int RENDERED_IMAGE = DISPOSED << 1;
	
	
	protected static final int REPAINT_MASK = ALL | RENDERED_IMAGE | CHANGED;


	public static Color getColor (int argb, Color old)
	{
		if ((old == null) || (argb != old.getRGB ()))
		{
			old = new Color (argb, true);
		}
		return old;
	}


	private static int f2i (float v)
	{
		int i = (int) (256 * v);
		return (i <= 0) ? 0 : (i >= 255) ? 255 : i;
	}


	public static int getIntColor (Tuple3f c)
	{
		return (f2i (c.x) << 16) + (f2i (c.y) << 8) + f2i (c.z)
			+ 0xff000000;
	}


	public static Color getColor (Tuple3f c, Color old)
	{
		int i = getIntColor (c);
		if ((old != null) && (old.getRGB () == i))
		{
			return old;
		}
		return new Color (i);
	}


	/**
	 * A lock to synchronize on when writing to {@link #renderFlags}.
	 */
	final Object renderFlagsLock = new Object ();


	/**
	 * Combination of bit masks for repaint requests and status.
	 * Write access has to be synchronized on {@link #renderFlagsLock},
	 * terminated by an invocation of <code>notifyAll</code> on the lock.
	 */
	private volatile int renderFlags = 0;


	private View view;
	private EventAdapter eventAdapter;
	private ThreadContext controlContext;
	private GraphState renderGraphState;
	private Item factory;

	private int maxLod = View.LOD_MAX - 1;
	private int lod = maxLod;
	private int drawLod;

	private int lodIncrementTime = 500;

	private boolean checkForInterruption;
	private boolean interruptionRequested;
	private long drawStartTime;
	private long renderAbortTime = 300;
	private int drawCount;
	
	private Renderer currentRenderer = null;

	
	public void initFactory (Item factory)
	{
		this.factory = factory;
	}


	public void initView (View view, EventListener listener)
	{
		eventAdapter = new EventAdapter (view, listener, JobManager.UPDATE_FLAGS);
		this.view = view;
		maxLod = ((Number) getOption ("lod", 1)).intValue ();
		lod = maxLod;
		Thread t = new Thread (this, "ViewThread@" + this);
		t.setPriority (Thread.MIN_PRIORITY + 1);
		t.start ();
	}
	
	
	protected void installListeners (Component canvas)
	{
		eventAdapter.install (canvas);
	}


	protected void uninstallListeners (Component canvas)
	{
		eventAdapter.uninstall (canvas);
	}


	public View getView ()
	{
		return view;
	}


	public Item getFactory ()
	{
		return factory;
	}

	
	public Object getOption (String name, Object defaultValue)
	{
		return getFactory ().get (name, defaultValue);
	}


	public Selection toSelection (de.grogra.pf.ui.Context ctx)
	{
		return new OptionsSelection (ctx, getFactory (), false)
		{
			@Override
			protected void valueChanged (String name, Object value)
			{
				optionValueChanged (name, value);
			}
		};
	}

	
	protected void optionValueChanged (String name, Object object)
	{
		if ("lod".equals (name))
		{
			maxLod = ((Number) object).intValue ();
			lod = maxLod;
			repaint (ALL);
		}
	}


	public GraphState getRenderGraphState ()
	{
		if ((renderGraphState == null)
			|| (renderGraphState.getGraph () != view.getGraph ())
			|| (renderGraphState.getContext () != ThreadContext.current ()))
		{
			renderGraphState = view.getWorkbenchGraphState ()
				.forContext (ThreadContext.current ());
			renderGraphState.getContext ().setPriority (ThreadContext.MAX_PRIORITY);
		}
		return renderGraphState;
	}


	public void dispose ()
	{
		disposeRenderer (null);
		synchronized (renderFlagsLock)
		{
			renderFlags |= DISPOSING;
			while ((renderFlags & RENDERING) != 0)
			{
				try 
				{
					renderFlagsLock.wait ();
				}
				catch (InterruptedException e)
				{
				}
			}
			renderFlags |= DISPOSED;
			renderFlagsLock.notifyAll ();
		}
	}


	public FontMetrics getFontMetrics (Font font)
	{
		return ((Component) getComponent ()).getFontMetrics (font);
	}


	public void repaint (int flags)
	{
		if ((flags & (SCENE | CHANGED)) == (SCENE | CHANGED))
		{
			disposeRenderer (null);
		}
		if ((flags & REPAINT_MASK & ~renderFlags) != 0)
		{
			synchronized (renderFlagsLock)
			{
				renderFlags |= flags & REPAINT_MASK;
				renderFlagsLock.notifyAll ();
			}
		}
	}


	/**
	 * Controls rendering and the global level of detail in an own thread. This
	 * method should not be invoked by user code.
	 */
	public final void run ()
	{
		controlContext = ThreadContext.current ();
		controlContext.setPriority (ThreadContext.MAX_PRIORITY);
		Workbench.setCurrent (view.getWorkbench ());

		while (true)
		{
			int f;
			synchronized (renderFlagsLock)
			{
				if ((renderFlags & (DISPOSED | REPAINT_MASK)) == 0)
				{
					try 
					{
						long t = System.currentTimeMillis ();
						renderFlagsLock.wait (Math.max (lodIncrementTime, 300));
						if ((lod < maxLod)
							&& ((System.currentTimeMillis () - t)
								>= (lodIncrementTime * 9 / 10))
							&& ((renderFlags & (DISPOSED | REPAINT_MASK)) == 0))
						{
							lod++;
							repaint (ALL);
						}
					}
					catch (InterruptedException e)
					{
					}
				}
				f = renderFlags;
			}
			if ((f & DISPOSED) != 0)
			{
				break;
			}
			if ((f & REPAINT_MASK) != 0)
			{
				try 
				{
					synchronized (renderFlagsLock)
					{
						f = renderFlags;
						renderFlags = (f & ~REPAINT_MASK) | RENDERING;
						if ((f & DISPOSING) == 0)
						{
							initRender (f);
						}
					}
					if ((f & DISPOSING) == 0)
					{
						invokeRender (f);
					}
				}
				finally
				{
					synchronized (renderFlagsLock)
					{
						renderFlags &= ~RENDERING;
						renderFlagsLock.notifyAll ();
					}
				}
			}
		}
		Workbench.setCurrent (null);
	}


	public int getGlobalLOD ()
	{
		return drawLod;
	}


	private static final InterruptedException ABORT_RENDER
		= new InterruptedException ();

	private static final WrapException ABORT_RENDER_WRAPPED
		= new WrapException (ABORT_RENDER);


	public void checkRepaint () throws InterruptedException
	{
		InterruptedException e = getInterruptedException ();
		if (e != null)
		{
			throw e;
		}
	}


	public void checkRepaintWrapException ()
	{
		InterruptedException e = getInterruptedException ();
		if (e != null)
		{
			throw (e == ABORT_RENDER) ? ABORT_RENDER_WRAPPED
				: new WrapException (e);
		}
	}


	public InterruptedException getInterruptedException ()
	{
		if (checkForInterruption
			&& (((renderFlags & ALL) != 0)
				|| (((++drawCount & 63) == 0)
					&& controlContext.getThread ().isInterrupted ())))
		{
			interruptionRequested = true;
			checkForInterruption = false;
			drawCount = 0;
		}
		else if (interruptionRequested)
		{
			if (++drawCount > 100)
			{
				if (System.currentTimeMillis () - drawStartTime
					> renderAbortTime)
				{
					return ABORT_RENDER;
				}
				drawCount = 0;
			}
		}
		return null;
	}


	public void disposeRenderer (Renderer r)
	{
		Renderer c = currentRenderer;
		if ((c != null) && ((c == r) || (r == null)))
		{
			currentRenderer = null;
			c.dispose ();
		}
	}
	
    static class MyEventQueue extends EventQueue {
    	boolean active = false;
    	boolean set = false;
    	View view = null;
    	public void setActive(boolean active) {
    		this.active = active;
    	}        	
        protected void dispatchEvent(AWTEvent event) {
            if ((event instanceof KeyEvent) && active) {
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED
                        && (keyEvent).getKeyCode() == KeyEvent.VK_ESCAPE) {
                	view.getViewComponent().disposeRenderer(null);
                }
            }
            super.dispatchEvent(event);
        }
        void set(View view) {
        	if (!set) {
        		Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
        		this.set = true;
        	}
        	this.view = view;
        }
        
    };
    
    static MyEventQueue eq = new MyEventQueue();

	public void render (Renderer r)
	{
		ImageObserver o = getObserverForRenderer ();
		if (o == null)
		{
			return;
		}
		disposeRenderer (null);
		currentRenderer = r;
		Component c = (Component) getComponent ();
		r.initialize (view, c.getWidth (), c.getHeight ());
		r.addImageObserver (o);

		eq.set(view);
		
		// thread to observe the finish of rendering
		Runnable rf = new Runnable() {
			public void run() {
				try {
					currentRenderer.waitForImage();
				} catch (InterruptedException e) {e.printStackTrace();}
				// remove keyhook
				finally {eq.setActive(false);}
			}
		};
		Thread t = new Thread(rf);
		t.start();
		
		try
		{
			eq.setActive(true);
			r.render ();
		}
		catch (java.io.IOException e)
		{
			view.getWorkbench ().logGUIInfo
				(IMP.I18N.msg ("renderer.error", r.getName ()), e);
			eq.setActive(false);
		}
	}

	public void render (Renderer r, int width, int height)
	{
		ImageObserver o = getObserverForRenderer ();
		if (o == null)
		{
			return;
		}
		disposeRenderer (null);
		currentRenderer = r;
		r.initialize (view, width, height);
		r.addImageObserver (o);
		try
		{
			r.render ();
		}
		catch (java.io.IOException e)
		{
			view.getWorkbench ().logGUIInfo
				(IMP.I18N.msg ("renderer.error", r.getName ()), e);
		}
	}

	/**
	 * Returns an observer which receives the information about the
	 * rendered image from a {@link Renderer}. The returned observer
	 * has to manage the drawing of the (partially) rendered image
	 * on the view component.
	 * 
	 * @return an observer receiving the image, or <code>null</code> if
	 *   this is not supported by this component 
	 */
	protected abstract ImageObserver getObserverForRenderer ();

	
	/**
	 * Performs initialization tasks in preparation for rendering.
	 * This method is invoked by {@link #run()} in this
	 * <code>ViewComponent</code>'s own thread.
	 * 
	 * @param flags combination of bit masks
	 */
	protected abstract void initRender (int flags);

	
	/**
	 * Invoked to perform rendering. This method is invoked
	 * by {@link #run()} in this <code>ViewComponent</code>'s own thread.
	 * Its sole task is to invoke {@link #invokeRenderSync(int)} in
	 * the rendering thread (which may be this
	 * <code>ViewComponent</code>'s thread, the AWT-thread, or another
	 * thread, depending on the implementation) in a write-protected
	 * context. The invocation has to be
	 * synchronously, i.e., if it is in another thread, the current thread
	 * has to wait until {@link #invokeRenderSync(int)} has completed.
	 * 
	 * @param flags the flags to pass to {@link #invokeRenderSync(int)}
	 */
	protected abstract void invokeRender (int flags);


	/**
	 * Invokes {@link #render(int)}, ensuring that
	 * {@link Workbench#current()} returns the correct workbench.
	 * If {@link #render(int)} throws an <code>InterruptedException</code>,
	 * a repaint is posted. The write-lock of this view's graph has to be
	 * acquired by the invoker (see {@link Lockable}).  
	 * 
	 * @param flags the flags to pass to {@link #render(int)}
	 */
	protected void invokeRenderSync (final int flags)
	{
		if (view.getGraph () == null)
		{
			return;
		}
		drawLod = lod;
		checkForInterruption = true;
		interruptionRequested = false;
		drawStartTime = System.currentTimeMillis ();
		boolean interrupted = false;
		if (Workbench.current () == getView ().getWorkbench ())
		{
			try
			{
				View.set (getRenderGraphState (), getView ());
				render (flags);
			}
			catch (InterruptedException e)
			{
				interrupted = true;
			}
			View.set (getRenderGraphState (), null);
		}
		else
		{
			class Run implements Runnable
			{
				InterruptedException iex = null;

				public void run ()
				{
					try
					{
						View.set (getRenderGraphState (), getView ());
						render (flags);
					}
					catch (InterruptedException e)
					{
						iex = e;
					}
					View.set (getRenderGraphState (), null);
				}
			}
			
			Run r = new Run ();
			getView ().getWorkbench ().runAsCurrent (r);
			interrupted = r.iex != null;
		}
		if (interrupted)
		{
			if ((lod > View.LOD_MIN) && ((flags & SCENE) != 0))
			{
				lod--;
			}
			repaint (flags);
		}
	}


	/**
	 * Performs rendering. This method is invoked by
	 * {@link #invokeRenderSync(int)} in a context where
	 * {@link Workbench#current()} returns the workbench of this
	 * view.
	 * 
	 * @param flags combination of bit masks
	 * @throws InterruptedException if the rendering has been interrupted
	 */
	protected abstract void render (int flags) throws InterruptedException;

	
	protected void renderUninterruptibly ()
	{
		checkForInterruption = false;
		interruptionRequested = false;
	}
}
