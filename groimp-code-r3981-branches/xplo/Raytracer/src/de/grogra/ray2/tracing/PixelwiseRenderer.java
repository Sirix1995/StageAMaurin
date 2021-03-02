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

package de.grogra.ray2.tracing;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4d;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Renderer;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.Antialiasing;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.antialiasing.NoAntialiasing;
import de.grogra.task.PartialTask;
import de.grogra.task.Solver;
import de.grogra.task.SolverInOwnThread;
import de.grogra.task.Task;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class PixelwiseRenderer extends Task implements Renderer
{
	public static final String ANTIALIASING = "antialiasing";
	public static final String RAYPROCESSOR = "rayprocessor";
	public static final String BRIGHTNESS = "brightness";
	public static final String THREAD_COUNT = "threadcount";
	public static final String AUTO_ADJUST = "autoadjust";
	public static final String AUTO_ADJUST_MAX_VALUE = "autoadjustmaxvalue";
	public static final String REMOVE_OUTLIERS = "removeoutliers";
	public static final String HDR = "hdr";
	public static final String SEED = "seed";

	private Options opts;

	protected ProgressMonitor monitor;

	protected Sensor camera;
	protected Matrix4d cameraTransformation;

	public int threadCount;

	protected int width;
	protected int height;
	protected BufferedImage image;
	protected int[] rgbaPixels;
	protected float[][] hdrPixels;

	protected ImageObserver observer;
	protected Scene originalScene;

	protected Antialiasing antialiasing;

	protected int imageUpdateDistance = 50;
	protected int imageUpdateRate = 3000;

	protected float brightness;
	protected boolean hdr;
	protected boolean autoAdjust;
	protected boolean removeOutliers;
	protected float autoAdjustMaxValue;
	protected float maxValue;
	protected long seed;

	
	Color4f maxColor = null;
	Color4f minColor = null;
	static int changedPixels;
	
	public static class Result
	{
		public IntList lines = new IntList (500);
		public FloatList data = new FloatList (10000);
	}

	public static class RenderTask implements PartialTask
	{
		public IntList lines = new IntList (500);
	}

	public Number getNumericOption (String key, Number def)
	{
		return (opts != null) ? (Number) opts.get (key, def) : def;
	}

	public boolean getBooleanOption (String key, boolean def)
	{
		return (opts != null) ? ((Boolean) opts.get (key, Boolean.valueOf (def))).booleanValue () : def;
	}

	public Object getClassOption (String key, Object def)
	{
		if (opts != null)
		{
			Object cls = opts.get (key, null);
			if (cls instanceof Class)
			{
				try
				{
					return ((Class) cls).newInstance ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		}
		return def;
	}

	public void initialize (Options opts, ProgressMonitor progress)
	{
		this.opts = opts;
		this.monitor = progress;
		if (progress != null)
		{
			progress.setProgress (Resources.msg ("initializing"),
				ProgressMonitor.INDETERMINATE_PROGRESS);
		}
		antialiasing = (Antialiasing) getClassOption (ANTIALIASING,
			new NoAntialiasing ());
		brightness = getNumericOption (BRIGHTNESS, 0.1f)
			.floatValue ();
		threadCount = getNumericOption (THREAD_COUNT, 0)
			.intValue ();
		autoAdjust = getBooleanOption (AUTO_ADJUST, Boolean.TRUE);
		autoAdjustMaxValue = getNumericOption (AUTO_ADJUST_MAX_VALUE, 1).floatValue ();
		removeOutliers = getBooleanOption (REMOVE_OUTLIERS, Boolean.TRUE);
		hdr = getBooleanOption (HDR, Boolean.TRUE);
		seed = getNumericOption (SEED, 0).longValue ();
		maxValue = 0;
		if (threadCount <= 0)
		{
			threadCount = Runtime.getRuntime ().availableProcessors ();
		}
	}

	public Sensor getCamera ()
	{
		return camera;
	}

	public Matrix4d getCameraTransformation ()
	{
		return cameraTransformation;
	}

	public void setAntialiasing (Antialiasing a)
	{
		antialiasing = a;
	}

	public void setHDR (boolean value)
	{
		hdr = value;
	}

	public void setAutoAdjust (boolean value)
	{
		autoAdjust = value;
	}

	public boolean getAutoAdjust ()
	{
		return autoAdjust;
	}

	public void setAutoAdjustMaxValue (float value)
	{
		autoAdjustMaxValue = value;
	}

	public void setBrightness (float value)
	{
		brightness = value;
	}

	public void setThreadCount (int value)
	{
		threadCount = value;
	}

	public float getBrightness ()
	{
		return brightness;
	}

	public long getSeed ()
	{
		return seed;
	}

	public void render (Scene scene, Sensor camera,
			Matrix4d cameraTransformation, int width, int height,
			ImageObserver obs)
	{
		long startTime = System.currentTimeMillis ();
		this.originalScene = scene;
		this.camera = camera;
		this.cameraTransformation = cameraTransformation;

		antialiasing.initialize (this, scene);

		this.width = width;
		this.height = height;

		changedPixels=0;
		maxColor = new Color4f();
		minColor = new Color4f(10,10,10,10);
		
		lineState = new int[height];

		if (hdr)
		{
			hdrPixels = new float[4][width * height];
			DataBufferFloat buffer = new DataBufferFloat (hdrPixels, hdrPixels[0].length);
			BandedSampleModel sampleModel = new BandedSampleModel (buffer.getDataType (), width, height, 4);
			WritableRaster raster = Raster.createWritableRaster (sampleModel, buffer, null);
			ColorSpace cs = ColorSpace.getInstance (ColorSpace.CS_sRGB);
			ComponentColorModel cm = new ComponentColorModel (cs, true, false, Transparency.TRANSLUCENT, buffer.getDataType ());
			image = new BufferedImage (cm, raster, false, null);
		}
		else
		{
			image = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			rgbaPixels = ((DataBufferInt) image.getRaster ().getDataBuffer ()).getData ();
		}
		observer = obs;
		if (threadCount < 2)
		{
			addSolver (createLocalSolver (true));
		}
		else
		{
			for (int i = Math.min (32, threadCount); i > 0; i--)
			{
				addSolver (createLocalSolver (false));
			}
		}
		if (monitor != null)
		{
			monitor.setProgress (Resources.msg ("renderer.rendering",
				new Float (0)), 0);
		}
		solve ();
		removeSolvers ();
		if (isStopped ())
		{
			observer.imageUpdate (image, ImageObserver.ABORT
				| ImageObserver.ERROR, 0, 0, width, height);
			if (monitor != null)
			{
				monitor.setProgress (Resources.msg ("renderer.stopped"),
					ProgressMonitor.DONE_PROGRESS);
			}
		}
		else
		{
			if (hdr)
			{
				if (removeOutliers)
				{
					removeOutliers ();
				}
				if (autoAdjust && (maxValue > 0))
				{
					float f = autoAdjustMaxValue / maxValue;
					for (int s = 0; s < 3; s++)
					{
						float[] a = hdrPixels[s];
						for (int i = a.length - 1; i >= 0; i--)
						{
							a[i] *= f;
						}
					}
				}
			}
			long time = System.currentTimeMillis () - startTime;
			observer.imageUpdate (image, ImageObserver.ALLBITS, 0, 0, width,
				height);
			if (monitor != null)
			{
				monitor.setProgress (Resources.msg ("renderer.done"),
					ProgressMonitor.DONE_PROGRESS);

				StringBuffer stats = new StringBuffer ("<html><pre>");
				stats.append (Resources.msg ("raytracer.statistics",
					new Object[] {width, height, threadCount,
							(int) (time / 60000),
							(time % 60000) * 0.001f}));

				stats.append ("    Count of changed Pixels: " +changedPixels+"\n");
				stats.append ("    maxColor " +maxColor+"\n");
				stats.append ("    minColor " +minColor+"\n");
				
				originalScene.appendStatistics (stats);

				antialiasing.appendStatistics (stats);
				monitor
					.showMessage (stats.append ("</pre></html>").toString ());
			}
		}
	}


	private static final int AVG_SIZE = 5;
	private static final int MAX_SIZE = 1;
	
	protected void removeOutliers ()
	{
		if ((width <= AVG_SIZE) || (height <= AVG_SIZE))
		{
			return;
		}
		IntList outliers = new IntList ();
		FloatList newValues = new FloatList ();
		for (int y = 0; y < height; y++)
		{
			int minY = Math.max (y - AVG_SIZE, 0);
			int maxY = Math.min (y + AVG_SIZE, height - 1);
			int numSamples = 0;
			double[] sum = new double[3];
			double[] delta = new double[3];
			for (int x2 = 0; x2 < AVG_SIZE; x2++)
			{
				for (int y2 = minY; y2 <= maxY; y2++)
				{
					numSamples++;
					int i = y2 * width + x2;
					for (int j = 0; j < 3; j++)
					{
						sum[j] += hdrPixels[j][i];
					}
				}
			}
			for (int x = 0; x < width; x++)
			{
				delta[0] = 0;
				delta[1] = 0;
				delta[2] = 0;
				int x2 = x + AVG_SIZE;
				if (x2 < width)
				{
					for (int y2 = minY; y2 <= maxY; y2++)
					{
						numSamples++;
						int i = y2 * width + x2;
						for (int j = 0; j < 3; j++)
						{
							delta[j] += hdrPixels[j][i];
						}
					}
				}
				x2 = x - AVG_SIZE - 1;
				if (x2 >= 0)
				{
					for (int y2 = minY; y2 <= maxY; y2++)
					{
						numSamples--;
						int i = y2 * width + x2;
						for (int j = 0; j < 3; j++)
						{
							delta[j] -= hdrPixels[j][i];
						}
					}
				}
				for (int j = 0; j < 3; j++)
				{
					sum[j] += delta[j];
				}
				int index = y * width + x;
				float val = hdrPixels[0][index] + hdrPixels[1][index] + hdrPixels[2][index];

			testIfOutlier:
				if ((numSamples - 1) * val > 3 * (sum[0] + sum[1] + sum[2] - val))
				{
					for (int y2 = Math.max (y - 1, 0); y2 <= Math.min (y + 1, height - 1); y2++)
					{
						for (x2 = Math.max (x - 1, 0); x2 <= Math.min (x + 1, width - 1); x2++)
						{
							int i = y2 * width + x2;
							if (i != index)
							{
								if (val < 3 * (hdrPixels[0][i] + hdrPixels[1][i] + hdrPixels[2][i]))
								{
									break testIfOutlier;
								}
							}
						}
					}
					outliers.add (index);
					float f = 1f / (numSamples - 1);
					for (int j = 0; j < 3; j++)
					{
						newValues.add (f * ((float) sum[j] - hdrPixels[j][index]));
					}
				}
			}
		}
		if (outliers.isEmpty ())
		{
			return;
		}
		while (!outliers.isEmpty ())
		{
			int index = outliers.pop ();
			for (int j = 2; j >= 0; j--)
			{
				hdrPixels[j][index] = newValues.pop ();
			}
		}
		if (!autoAdjust)
		{
			return;
		}
		maxValue = 0;
		for (int i = 0; i < width * height; i++)
		{
			float t = hdrPixels[3][i];
			if (t * hdrPixels[0][i] > maxValue)
			{
				maxValue = t * hdrPixels[0][i];
			}
			if (t * hdrPixels[1][i] > maxValue)
			{
				maxValue = t * hdrPixels[1][i];
			}
			if (t * hdrPixels[2][i] > maxValue)
			{
				maxValue = t * hdrPixels[2][i];
			}
		}
	}

	public float getAdjustFactor ()
	{
		return autoAdjustMaxValue / maxValue;
	}

	static final int RENDERING = 1;
	static final int RENDERED = 2;

	protected int renderedLines;
	protected int[] lineState;

	public Solver createLocalSolver (final boolean sameThread)
	{
		final Antialiasing aa = antialiasing.dup (originalScene.dup ());

		return new SolverInOwnThread ()
		{
			private final Result result = new Result ();

			@Override
			protected void solveImpl (PartialTask task)
			{
				renderLines (aa, ((RenderTask) task).lines, result);
			}

			@Override
			protected Thread createThread ()
			{
				if (sameThread)
				{
					return null;
				}
				Thread t = new Thread (this, toString ());
				t.setPriority (Thread.MIN_PRIORITY);
				return t;
			}
		};
	}

	public synchronized void merge (Result res)
	{
		int firstContiguousY = -1;
		int nextContiguousY = -1;
		float[] data = res.data.elements;
		float maxValue = this.maxValue;
		for (int i = 0; i <= res.lines.size; i++)
		{
			int y;
			if (i < res.lines.size)
			{
				y = res.lines.elements[i];
				if (lineState[y] != RENDERED)
				{
					renderedLines++;
					lineState[y] = RENDERED;
					int f = i * width * 4;
					int j = y * width;
					for (int x = 0; x < width; x++)
					{
						if (autoAdjust)
						{
							float t = data[f + 3];
							if (t * data[f] > maxValue)
							{
								maxValue = t * data[f];
							}
							if (t * data[f + 1] > maxValue)
							{
								maxValue = t * data[f + 1];
							}
							if (t * data[f + 2] > maxValue)
							{
								maxValue = t * data[f + 2];
							}
						}
						if (hdr)
						{
							hdrPixels[0][j] = data[f];
							hdrPixels[1][j] = data[f + 1];
							hdrPixels[2][j] = data[f + 2];
							hdrPixels[3][j] = data[f + 3];
							
							if(luminance(hdrPixels[0][j],hdrPixels[1][j],hdrPixels[2][j]) > 
								luminance(maxColor.x, maxColor.y, maxColor.z)){
								maxColor.x = hdrPixels[0][j];
								maxColor.y = hdrPixels[1][j];
								maxColor.z = hdrPixels[2][j];
								maxColor.w = hdrPixels[3][j];
							}
							if(luminance(hdrPixels[0][j],hdrPixels[1][j],hdrPixels[2][j]) < 
									luminance(minColor.x, minColor.y, minColor.z)){
								minColor.x = hdrPixels[0][j];
								minColor.y = hdrPixels[1][j];
								minColor.z = hdrPixels[2][j];
								minColor.w = hdrPixels[3][j];
							}
							
						}
						else
						{
							rgbaPixels[j] = toIntColor (data[f], data[f + 1],
								data[f + 2], data[f + 3]);
						}
						f += 4;
						j++;
						changedPixels++;
					}
				}
			}
			else
			{
				y = -1;
			}
			if (firstContiguousY < 0)
			{
				firstContiguousY = y;
			}
			else if (nextContiguousY != y)
			{
				if ((nextContiguousY < height)
					&& (lineState[nextContiguousY] != RENDERED))
				{
					for (int x = 0; x < width; x++)
					{
						image.setRGB (x, nextContiguousY, -1);
					}
					nextContiguousY++;
				}
				observer
					.imageUpdate (image, ImageObserver.SOMEBITS, 0,
						firstContiguousY, width, nextContiguousY
							- firstContiguousY);
				firstContiguousY = y;
			}
			nextContiguousY = y + 1;
		}
		this.maxValue = maxValue;
		if (monitor != null)
		{
			float progress = (float) renderedLines / height;
			monitor.setProgress (Resources.msg ("renderer.rendering",
				new Float (progress)), progress);
		}
	}

	@Override
	protected synchronized boolean done ()
	{
		return renderedLines == height;
	}

	@Override
	protected PartialTask nextPartialTask (int solverIndex)
	{
		int sc = getSolverCount ();
		RenderTask task = new RenderTask ();
		int yStart = height * solverIndex / sc;
		int y = yStart;
		int count = (sc == 1) ? height : Math.max (Math.min (
			(height - renderedLines) / (2 * sc), height / (8 * sc)), 1);
		do
		{
			if (lineState[y] == 0)
			{
				task.lines.push (y);
				lineState[y] = RENDERING;
				if (--count == 0)
				{
					break;
				}
			}
			y = (y + 1) % height;
		}
		while (y != yStart);
		return (task.lines.size == 0) ? null : task;
	}

	@Override
	protected void dispose (PartialTask task)
	{
		IntList list = ((RenderTask) task).lines;
		for (int i = 0; i < list.size; i++)
		{
			lineState[list.elements[i]] &= ~RENDERING;
		}
	}

	public static int DEBUG_X = -1;
	public static int DEBUG_Y = -1;
	public static float DEBUG_LINE = -1f;

	public static boolean DEBUG_PIXEL;
	public static boolean DEBUG_SUBPIXEL;

	protected void renderLines (Antialiasing antialiasing, IntList lines,
			Result res)
	{
		double pixelSize = 2.0 / width;

		Color4f color = new Color4f ();

		int nextImageUpdateI = Math.min (imageUpdateDistance, lines.size - 1);

		long nextImageUpdateTime = System.currentTimeMillis ()
			+ imageUpdateRate;

		res.lines.setSize (0);
		res.data.setSize (0);
		MTRandom rnd = new MTRandom ();
		for (int i = 0; i < lines.size; i++)
		{
			int y = lines.elements[i];
			rnd.setSeed (seed + 0x5deece66dL * y);
			for (int x = 0; x < width; x++)
			{
				if (isStopped ())
				{
					return;
				}
//				DEBUG_PIXEL = (x == width/2/*DEBUG_X*/) && (y == height/2/*DEBUG_Y*/);				
//				DEBUG_SUBPIXEL = DEBUG_PIXEL;
				
//				DEBUG_SUBPIXEL = (y== Math.round((lines.size*DEBUG_LINE)));

				((NoAntialiasing)antialiasing).setPixelXY(x, y);
				
				antialiasing.getColorOfRectangle (x * pixelSize - 1,
					(0.5 * height - (y + 1)) * pixelSize, pixelSize, pixelSize,
					color, rnd);

				float mult = brightness / Math.max (color.w, 1e-3f);

				res.data.push (color.x * mult).push (color.y * mult).push (
					color.z * mult).push (color.w);
			}
			res.lines.push (y);

			// progress changed
			if ((i == nextImageUpdateI)
				|| (System.currentTimeMillis () >= nextImageUpdateTime))
			{
				merge (res);
				res.lines.setSize (0);
				res.data.setSize (0);
				nextImageUpdateI = Math.min (i + imageUpdateDistance,
					lines.size - 1);
				nextImageUpdateTime = System.currentTimeMillis ()
					+ imageUpdateRate;
			}
		}
	}

	private static int toByte (float f)
	{
		int b = (int) (f * 256);
		return (b < 0) ? 0 : (b > 255) ? 255 : b;
	}

	protected static int toIntColor (float r, float g, float b, float a)
	{
		return (toByte (a) << 24) + (toByte (r) << 16) + (toByte (g) << 8)
			+ toByte (b);
	}

	protected static float[] toRGBA(int intColor){
		
		float a = (float)((intColor >>24)& 255) /256f;
		float r = (float)((intColor >>16)& 255) /256f;
		float g = (float)((intColor >>8)& 255) /256f;
		float b = (float)(intColor &255)/255f;
		
		float[] ret = {r,g,b,a};
		return ret;
	}
	
	
	
	/*
	 /**
	 * The <i>ray casting function</i> of the scene geometry. It determines
	 * the first intersection of the <code>ray</code> with the scene
	 * geometry, excluding the intersection specified by
	 * <code>exclude</code>.
	 * 
	 * @param ray the ray
	 * @param list the intersection will be added to this list
	 * @param exclude intersection which has to be excluded,
	 * or <code>null</code>
	 * @return <code>true</code> if an intersection was found
	 * (and added to <code>list</code>), <code>false</code> otherwise
	 * 
	 * @see Volume#computeIntersections
	 * /
	 boolean castRay (Line ray, IntersectionList list, Intersection exclude);

	 /**
	 * The <i>visibility function</i> of the scene geometry. It checks
	 * if <code>a</code> and <code>b</code> are mutually visible,  
	 * 
	 * @param a
	 * @param b
	 * @param exclude
	 * @return
	 * /
	 Volume checkVisibility (Tuple3d a, Tuple3d b, Intersection exclude);
	 private final Line visibilityLine = new Line ();
	 private final IntersectionList visibilityList = new IntersectionList ();

	 public Volume checkVisibility (Tuple3d a, Tuple3d b, Intersection exclude)
	 {
	 visibilityLine.start = 0;
	 visibilityLine.end = 1;
	 visibilityLine.origin.set (a);
	 visibilityLine.direction.sub (b, a);
	 visibilityList.clear ();
	 sceneVolume.computeIntersections (visibilityLine, Intersection.ANY,
	 visibilityList, exclude, "");
	 return (visibilityList.size > 0) ? visibilityList.elements[0].solid
	 : null;
	 }

	 */
	
	/**
	 * Show Status message.
	 * 
	 * @param text short text to display
	 * @param progress state of progress from 0 to 1, or one of the
	 * constants {@link #INDETERMINATE_PROGRESS}, {@link #DONE_PROGRESS}
	 */
	public void setMessage(String text, float progress) {
		if (this.monitor != null) {
			this.monitor.setProgress(text, progress);
		}
	}
	
	public Scene getScene(){ 
		return originalScene;
	}
	
	
	public float luminance(double x, double y, double z){	
		return (float)(0.299f*x + 0.587f*y + 0.114f*z);
	}
}
