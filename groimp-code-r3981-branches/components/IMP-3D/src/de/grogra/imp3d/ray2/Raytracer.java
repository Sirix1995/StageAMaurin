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

package de.grogra.imp3d.ray2;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.StringTokenizer;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMP;
import de.grogra.imp3d.Camera;
import de.grogra.imp3d.IMP3D;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.imp3d.objects.Box;
import de.grogra.imp3d.objects.TextLabel;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray2.Options;
import de.grogra.ray2.Renderer;
import de.grogra.ray2.antialiasing.NoAntialiasing;
import de.grogra.ray2.antialiasing.StochasticSupersampling;
import de.grogra.ray2.tracing.BiDirectionalProcessor;
import de.grogra.ray2.tracing.BidirectionalRenderer;
import de.grogra.ray2.tracing.DefaultRayProcessor;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.PathTracer;
import de.grogra.ray2.tracing.PhotonMapRayProcessor;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.Radiosity;
import de.grogra.ray2.tracing.RayProcessor;
import de.grogra.reflect.Type;
import de.grogra.util.EnumerationType;
import de.grogra.util.Map;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Octree;
import de.grogra.vecmath.geom.OctreeUnion;

public class Raytracer extends de.grogra.imp.Renderer implements Runnable,
		Options, ImageObserver
{
	public static final EnumerationType RAYPROCESSOR = new EnumerationType (
		"ray.processor", IMP3D.I18N, new String[] {"standard", "pathtracer", "photonmap","bidirectional", "radiosity", "metropolis"},
		new Class[] {DefaultRayProcessor.class, PathTracer.class, PhotonMapRayProcessor.class, BiDirectionalProcessor.class, Radiosity.class, MetropolisProcessor.class}, Type.CLASS);

	public static final EnumerationType ANTIALIASING = new EnumerationType (
		"ray.antialiasing", IMP3D.I18N, new String[] {"no", "stochastic"},
		new Class[] {NoAntialiasing.class, StochasticSupersampling.class},
		Type.CLASS);

	private Map params;

	private Renderer renderer;
	private SceneVisitor scene;
	private Matrix4d cameraTransform;
	private Camera camera;
	private ViewConfig3D view3D;

	private volatile boolean disposed;

	public Raytracer (Map params)
	{
		this.params = params;
	}

	public Raytracer (Workbench w, int width, int height)
	{
		this (Item.resolveItem (w, "/renderers/3d/ray2"));
		initialize (View3D.getDefaultView (w), width, height);
	}

	public Raytracer (Workbench w, ViewConfig3D v, int width, int height)
	{
		this (Item.resolveItem (w, "/renderers/3d/ray2"));
		initialize (null, width, height);
		view3D = v;
	}

	@Override
	public String getName ()
	{
		return "Raytracer";
	}

	public Object get (String key, Object def)
	{
		if (PixelwiseRenderer.SEED.equals (key))
		{
			String seed = Main.getProperty ("raytracerseed");
			if (seed != null)
			{
				try
				{
					return Integer.parseInt (seed);
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace ();
				}
			}
		}
		return params.get (key, def);
	}
	
	public void setRandomSeed(int value)
	{
		Main.getInstance().setProperty("raytracerseed", "" + value);
	}	

	public static boolean DEBUG = false;

	@Override
	public void render ()
	{
		if (view != null)
		{
			view3D = (View3D) view;
		}

		scene = new SceneVisitor (view3D.getWorkbench (), view3D.getGraph (), view3D.getEpsilon (), this, view3D, null, null, new Spectrum3f ());
		camera = view3D.getCamera ();
		cameraTransform = new Matrix4d ();
		cameraTransform.m33 = 1;
		Math2.invertAffine (camera.getWorldToViewTransformation (),
			cameraTransform);

		if (DEBUG)
		{
			OctreeUnion u = scene.getOctree ();
			Octree t = u.getOctree ();
			if ((t.getRoot ().getVolumeCount () > 0)
				&& (t.getRoot ().getVolume (0, null) instanceof MeshVolume))
			{
				t = ((MeshVolume) t.getRoot ().getVolume (0, null)).getOctree ();
			}
			TextLabel n = new TextLabel ("DEBUG");
			getBoxes (n, t, t.getRoot (), true, t.getDepth ());
			IMP.addNode (null, n, view);
		}

		synchronized (this)
		{
			if (disposed)
			{
				view.getWorkbench ().clearStatusAndProgress (scene);
				scene.dispose ();
				scene = null;
				return;
			}
			
			
			renderer = new PixelwiseRenderer ();
			renderer.initialize (this, scene);
			
			RayProcessor processor = (RayProcessor)((PixelwiseRenderer) renderer).getClassOption (
					PixelwiseRenderer.RAYPROCESSOR, new DefaultRayProcessor ());
			
			if(processor instanceof MetropolisProcessor){
				renderer = new MetropolisRenderer ();
				renderer.initialize (this, scene);				
			}else if(processor instanceof BiDirectionalProcessor){
				renderer = new BidirectionalRenderer ();
				renderer.initialize (this, scene);				
			}
			

			Thread t = new Thread (this, getName ());
			t.setPriority (Thread.MIN_PRIORITY);
			t.start ();
		}
	}

	private Vector3d tmp = new Vector3d ();
	private Point3d min = new Point3d ();
	private Point3d max = new Point3d ();

	private void getBoxes (Node r, Octree tree, Octree.Cell cell, boolean root,
			int depth)
	{
		if (cell.children != null)
		{
			for (int i = 0; i < 8; i++)
			{
				getBoxes (r, tree, cell.children[i], false, depth - 1);
			}
		}
		if (root || (cell.getVolumeCount () > 0))
		{
			cell.getExtent (tree, min, max);
			tmp.add (min, max);
			tmp.scale (0.5);
			if (!root && (tmp.y < 0))
			{
				//return;
			}
			Box b = new Box ();
			b.setColor ((int) (0x1000000 * Math.random ()));
			b.setTransform (tmp.x, tmp.y, min.z);
			tmp.sub (max, min);
			b.setLength ((float) tmp.z);
			b.setHeight ((float) tmp.y);
			b.setWidth ((float) tmp.x);
			r.addEdgeBitsTo (b, Graph.BRANCH_EDGE, null);
		}
	}

	public synchronized void dispose ()
	{
		disposed = true;
		if (renderer != null)
		{
			renderer.stop ();
			renderer = null;
		}
	}

	public void run ()
	{
		Renderer r;
		synchronized (this)
		{
			r = renderer;
		}
		if (r != null)
		{
			view3D.getWorkbench ().beginStatus (scene);
			r.render (scene, camera, cameraTransform, width, height, this);
			if ((r instanceof PixelwiseRenderer) && (params instanceof Item))
			{
				PixelwiseRenderer pr = (PixelwiseRenderer) r;
				if (!pr.isStopped () && pr.getAutoAdjust ())
				{
					float f = pr.getAdjustFactor ();
					if ((f > 0) && (f < Float.POSITIVE_INFINITY))
					{
						((Item) params).setOption (PixelwiseRenderer.BRIGHTNESS, pr.getBrightness () * f);
					}
				}
			}
			if (view != null)
			{
				view.getViewComponent ().disposeRenderer (this);
			}
		}
		else
		{
			view3D.getWorkbench ().clearStatusAndProgress (scene);
		}
		scene.dispose ();
		renderer = null;
		scene = null;
		camera = null;
	}

	public static void render (Item item, Object info, Context context)
	{
		StringTokenizer t = new StringTokenizer (String.valueOf (info));
		
	parseArgs:
		if (t.hasMoreTokens ())
		{
			final String file = t.nextToken ();

			if (!t.hasMoreTokens ())
			{
				break parseArgs;
			}
			final Dimension dim = Utils.parseDimension (t.nextToken ());

			if (!t.hasMoreTokens ())
			{
				break parseArgs;
			}
			final String out = t.nextToken ();

			final Workbench w = context.getWorkbench ().open (
				FileSource.createFileSource (IO.toSystemId (new File (file)), IO
					.getMimeType (file), context.getWorkbench (), null), new StringMap ());

			class Handler implements ImageObserver, Command
			{
				private Image renderedImage;

				public boolean imageUpdate (Image img, int infoflags, int x, int y, int width, int height)
				{
					if ((infoflags & ALLBITS) == 0)
					{
						return true;
					}
					renderedImage = img;
					w.getJobManager ().runLater (this, null, w, JobManager.ACTION_FLAGS);
					return false;
				}

				public String getCommandName ()
				{
					return null;
				}

				public void run (Object info, Context context)
				{
					if (renderedImage != null)
					{
						IMP.writeImage (context, (RenderedImage) renderedImage, FileTypeItem.get (w, out).getMimeType (), new File (out));
						IMP.getInstance ().exit ();
						return;
					}
					Raytracer rt = new Raytracer (w, dim.width, dim.height);
					rt.addImageObserver (this);
					rt.view3D = View3D.getDefaultViewConfig (w);
					rt.render ();
				}
			}

			w.getJobManager ().runLater (new Handler (), null, w, JobManager.ACTION_FLAGS);
			return;
		}
		System.err.println ("Invalid argument " + info + ": use '<file> <width>x<height>");
	}

}
