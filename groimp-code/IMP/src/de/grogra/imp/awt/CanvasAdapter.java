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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;

import javax.vecmath.Tuple3f;

import org.jibble.epsgraphics.EpsGraphics2D;

import de.grogra.imp.IMP;
import de.grogra.imp.View;
import de.grogra.imp.ViewComponent;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.io.ImageWriter;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.FileChooserResult;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.util.EventListener;
import de.grogra.util.ImageAndGraphics;
import de.grogra.util.Lock;
import de.grogra.util.LockProtectedRunnable;
import de.grogra.util.MimeType;
import de.grogra.util.Utils;
import de.grogra.xl.lang.ObjectConsumer;

public abstract class CanvasAdapter extends ViewComponentAdapter implements
		ImageObserver
{
	private static final String ANTIALIASING_OPTION = "antialiasing";

	protected static final AffineTransform IDENTITY = new AffineTransform ();

	private Component canvas;

	private Graphics2D graphics;

	private ImageAndGraphics canvasBuffer = null, newCanvasBuffer = null;
	protected ImageAndGraphics sceneBuffer = null;
	private ImageAndGraphics newSceneBuffer = null;
	private ImageAndGraphics drawBuffer = null, newDrawBuffer = null;

	private EpsGraphics2D epsGraphics;

	private int currentWidth = -1, currentHeight, bufferWidth, bufferHeight,
			paintWidth, paintHeight;

	private final Object canvasBufferLock = new Object ();

	private RenderedImage imageFromRenderer;

	protected boolean antialiasing = false;

	public class CanvasComponent extends Component
	{
		private final Dimension preferredSize;

		public CanvasComponent (int preferredWidth, int preferredHeight)
		{
			this.preferredSize = new Dimension (preferredWidth, preferredHeight);
			setSize (this.preferredSize);
			/*			this.setLayout (null);
			 this.setDoubleBuffered (true);
			 this.setOpaque (true); */
		}

		@Override
		public Dimension getPreferredSize ()
		{
			return preferredSize;
		}

		@Override
		public void paint (Graphics g)
		{
			CanvasAdapter.this.paint (g);
		}
	}

	public void initCanvas (Component canvas)
	{
		this.canvas = canvas;
	}

	@Override
	public void initView (View view, EventListener listener)
	{
		antialiasing = Boolean.TRUE.equals (getOption (ANTIALIASING_OPTION,
			Boolean.FALSE));
		super.initView (view, listener);
		installListeners (canvas);
	}

	@Override
	protected void optionValueChanged (String name, Object object)
	{
		if (ANTIALIASING_OPTION.equals (name))
		{
			antialiasing = Boolean.TRUE.equals (object);
			repaint (ALL);
		}
		else
		{
			super.optionValueChanged (name, object);
		}
	}

	@Override
	protected ImageObserver getObserverForRenderer ()
	{
		return this;
	}

	protected void checkBuffers ()
	{
		int width = canvas.getWidth (), height = canvas.getHeight ();
		if ((width != currentWidth) || (height != currentHeight))
		{
			synchronized (renderFlagsLock)
			{
				if ((width > bufferWidth) || (height > bufferHeight)
					|| ((width * height << 2) < bufferWidth * bufferHeight))
				{
					bufferWidth = width * 5 >> 2;
					bufferHeight = height * 5 >> 2;
					newCanvasBuffer = createIG (newCanvasBuffer, bufferWidth,
						bufferHeight);
					newSceneBuffer = createIG (newSceneBuffer, bufferWidth,
						bufferHeight);
					newDrawBuffer = createIG (newDrawBuffer, bufferWidth,
						bufferHeight);
				}
				currentWidth = width;
				currentHeight = height;
				repaint (SCENE);
			}
		}
	}

	void paint (Graphics g)
	{
		checkBuffers ();
		Image i;
		synchronized (canvasBufferLock)
		{
			i = (canvasBuffer != null) ? canvasBuffer.getImage () : null;
		}
		if ((g != null) && (i != null))
		{
			g.drawImage (i, 0, 0, null);
		}
	}

	@Override
	protected void initRender (int flags)
	{
		if (newCanvasBuffer != null)
		{
			synchronized (canvasBufferLock)
			{
				canvasBuffer = replace (canvasBuffer, newCanvasBuffer);
			}
		}
		newCanvasBuffer = null;
		sceneBuffer = replace (sceneBuffer, newSceneBuffer);
		newSceneBuffer = null;
		drawBuffer = replace (drawBuffer, newDrawBuffer);
		newDrawBuffer = null;
		paintWidth = currentWidth;
		paintHeight = currentHeight;
	}

	public Object getComponent ()
	{
		return canvas;
	}

	private BufferedImage copyBuffer;
	private Graphics2D copyBufferGraphics;
	private int[] copyBufferSamples;
	private static final int COPY_BUFFER_HEIGHT = 32;

	private void copyImage (Image img, int x, int y, int width, int height,
			ImageAndGraphics dest)
	{
		if ((copyBuffer == null) || (copyBuffer.getWidth () < width))
		{
			copyBuffer = new BufferedImage (width, COPY_BUFFER_HEIGHT,
				BufferedImage.TYPE_INT_ARGB);
			copyBufferGraphics = copyBuffer.createGraphics ();
			copyBufferGraphics.setComposite (AlphaComposite.Src);
			copyBufferSamples = new int[width * COPY_BUFFER_HEIGHT * 4];
		}
		int yMax = y + height;
		while (y < yMax)
		{
			height = Math.min (yMax - y, COPY_BUFFER_HEIGHT);
			copyBufferGraphics.drawImage (img, 0, 0, width, height, x, y, x
				+ width, y + height, null);
			copyBufferSamples = copyBuffer.getRaster ().getPixels (0, 0, width,
				height, copyBufferSamples);
			int index = 0;
			for (int iy = y; iy < y + height; iy++)
			{
				for (int ix = 0; ix < width; ix++)
				{
					int alpha = copyBufferSamples[index + 3];
					int background = ((255 - alpha) * (410 + ((ix ^ iy) & 8) * 26)) >> 10;
					copyBufferSamples[index] = copyBufferSamples[index++]
						* alpha / 255 + background;
					copyBufferSamples[index] = copyBufferSamples[index++]
						* alpha / 255 + background;
					copyBufferSamples[index] = copyBufferSamples[index++]
						* alpha / 255 + background;
					copyBufferSamples[index++] = 255;
				}
			}
			copyBuffer.getRaster ().setPixels (0, 0, width, height,
				copyBufferSamples);
			dest.getGraphics ().drawImage (copyBuffer, x, y, x + width,
				y + height, 0, 0, width, height, null);
			y += height;
		}
	}

	public boolean imageUpdate (Image img, int infoflags, int x, int y,
			int width, int height)
	{
		if ((width <= 0) || (height <= 0))
		{
			return true;
		}
		if ((infoflags & (ImageObserver.ALLBITS | ImageObserver.ABORT | ImageObserver.ERROR)) != 0)
		{
			if ((infoflags & ImageObserver.ALLBITS) != 0)
			{
				synchronized (renderFlagsLock)
				{
					copyImage (img, x, y, width, height, sceneBuffer);
					repaint (RENDERED_IMAGE);
					if (img instanceof RenderedImage)
					{
						imageFromRenderer = (RenderedImage) img;
					}
				}
			}
			return false;
		}
		else if ((infoflags & ImageObserver.SOMEBITS) != 0)
		{
			synchronized (renderFlagsLock)
			{
				copyImage (img, x, y, width, height, sceneBuffer);
				repaint (RENDERED_IMAGE);
			}
		}
		return true;
	}

	private static ImageAndGraphics replace (ImageAndGraphics old,
			ImageAndGraphics n)
	{
		if (n == null)
		{
			return old;
		}
		else
		{
			if (old != null)
			{
				old.dispose ();
			}
			return n;
		}
	}

	private ImageAndGraphics createIG (ImageAndGraphics old, int w, int h)
	{
		if (old != null)
		{
			old.dispose ();
		}
		Image i = canvas.createImage (w, h);
		if (i == null)
		{
			i = new BufferedImage (w, h, BufferedImage.TYPE_INT_RGB);
		}
		Graphics2D g = (Graphics2D) i.getGraphics ();
		g.setComposite (AlphaComposite.Src);
		return new ImageAndGraphics (i, g);
	}

	@Override
	protected void invokeRender (int flags)
	{
		if (sceneBuffer == null)
		{
			return;
		}
		if (epsGraphics != null)
		{
			flags |= SCENE;
		}
		final int f = flags;

		class Task implements LockProtectedRunnable, Command
		{
			public void run (boolean sameThread, Lock lock)
			{
				invokeRenderSync (f);
				if (epsGraphics != null)
				{
					UI.getJobManager (getView ()).runLater (this, epsGraphics,
						getView (), JobManager.ACTION_FLAGS);
					epsGraphics = null;
				}
				else
				{
					canvas.repaint ();
				}
			}

			public String getCommandName ()
			{
				return null;
			}

			public void run (Object info, Context ctx)
			{
				ObjectSourceImpl s = new ObjectSourceImpl (info, "eps",
					new IOFlavor (MimeType.POSTSCRIPT, IOFlavor.WRITER, null),
					ctx.getWorkbench ().getRegistry (), null);
				FileChooserResult fr = ctx.getWorkbench ().chooseFileToSave (
					null, s.getFlavor (), null);
				if (fr == null)
				{
					return;
				}
				FilterSource fs = IO.createPipeline (s, new IOFlavor (
					MimeType.POSTSCRIPT, IOFlavor.FILE_WRITER, null));
				try
				{
					((FileWriterSource) fs).write (fr.file);
				}
				catch (java.io.IOException ex)
				{
					ctx.getWorkbench ().logGUIInfo (
						IMP.I18N.msg ("snapshot.failed", fr.file), ex);
				}
			}
		}

		Utils.executeForcedlyAndUninterruptibly (getView ().getGraph (),
			new Task (), false);
	}

	@Override
	protected void render (int flags) throws InterruptedException
	{
		if (epsGraphics != null)
		{
			renderUninterruptibly ();
		}
		ImageAndGraphics ing = sceneBuffer;
		initPaint (flags, paintWidth, paintHeight);
		try
		{
			if ((flags & SCENE) != 0)
			{
				imageFromRenderer = null;
				if (epsGraphics != null)
				{
					graphics = epsGraphics;
					paintScene (flags, graphics);
				}
				else
				{
					graphics = (Graphics2D) ing.getGraphics ();
					if (antialiasing)
					{
						graphics.setRenderingHint (
							RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					}
					paintScene (flags, graphics);
					graphics.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
				}
			}
		}
		finally
		{
			if (epsGraphics == null)
			{
				renderUninterruptibly ();
				if (drawBuffer != null)
				{
					Graphics bg = drawBuffer.getGraphics ();
					((Graphics2D) bg).setTransform (IDENTITY);
					bg.drawImage (sceneBuffer.getImage (), 0, 0, null);
					paintHighlight (flags, graphics = (Graphics2D) bg);
				}

				synchronized (canvasBufferLock)
				{
					ing = drawBuffer;
					drawBuffer = canvasBuffer;
					canvasBuffer = ing;
				}
			}
		}
	}

	protected abstract void initPaint (int flags, int width, int height);

	protected abstract void paintScene (int flags, Graphics2D g)
			throws InterruptedException;

	protected abstract void paintHighlight (int flags, Graphics2D g);

	public void makeSnapshot (ObjectConsumer<? super RenderedImage> callback)
	{
		RenderedImage ri = imageFromRenderer;
		if (ri != null)
		{
			callback.consume (ri);
			return;
		}
		Image i = sceneBuffer.getImage ();
		if (!(i instanceof BufferedImage))
		{
			return;
		}
		callback.consume (((BufferedImage) i).getSubimage (0, 0, currentWidth,
			currentHeight));
	}

	public static void writeEPS (Item item, Object info, Context ctx)
	{
		View v = View.get (ctx);
		if (v != null)
		{
			ViewComponent a = v.getViewComponent ();
			if (a instanceof CanvasAdapter)
			{
				((CanvasAdapter) a).epsGraphics = new EpsGraphics2D (ctx
					.getWorkbench ().getName ());
				a.repaint (SCENE);
			}
		}
	}

	public final Graphics2D getGraphics ()
	{
		return graphics;
	}

	public void resetGraphicsTransform ()
	{
		getGraphics ().setTransform (IDENTITY);
	}

	public abstract void setColor (Color color);

	public void setColor (int color)
	{
		setColor (Utils.getApproximateColor (color));
	}

	public void setColor (Tuple3f color)
	{
		setColor (getIntColor (color));
	}

	public void setColor (Color color, int state, boolean showSel)
	{
		int c = ViewSelection.getColor (color.getRGB (), state, showSel);
		setColor ((c == color.getRGB ()) ? color : Utils
			.getApproximateColor (c));
	}

	public void setColor (int color, int state, boolean showSel)
	{
		int c = ViewSelection.getColor (color, state, showSel);
		if (c == color)
		{
			setColor (c);
		}
		else
		{
			setColor (Utils.getApproximateColor (c));
		}
	}

	public void setColor (Tuple3f color, int state, boolean showSel)
	{
		int rgb = getIntColor (color);
		int c = ViewSelection.getColor (rgb, state, showSel);
		if (c == rgb)
		{
			setColor (c);
		}
		else
		{
			setColor (Utils.getApproximateColor (c));
		}
	}

}
