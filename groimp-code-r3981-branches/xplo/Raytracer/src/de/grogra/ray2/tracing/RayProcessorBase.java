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

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import net.goui.util.MTRandom;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Interior;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray2.Scene;
import de.grogra.ray2.light.LightProcessor;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.Volume;

public abstract class RayProcessorBase extends ProcessorBase implements
		RayProcessor
{
	public static final String RECURSION_DEPTH = "rayprocessor.depth";

	int maxDepth = 5;

	protected PixelwiseRenderer renderer;
	LightProcessor lightProcessor = null;

	public /*protected*/ Scene scene;

	IntersectionList ilist = new IntersectionList ();
	Tuple3d tmpColor = new Point3d ();
	protected Tuple3d sumColor = new Point3d ();

	public ArrayList<Volume> enteredSolids = new ArrayList<Volume> ();

	long primaryCount = 0;

	public void setLightProcessor (LightProcessor proc)
	{
		lightProcessor = proc;
	}

	public LightProcessor getLightProcessor(){
		return lightProcessor;
	}
	
	public void setRecursionDepth (int value)
	{
		if (value >= 0)
		{
			maxDepth = value;
		}
	}

	public RayProcessor dup (Scene scene)
	{
		RayProcessorBase p = (RayProcessorBase) clone ();
		p.lightProcessor = lightProcessor.dup (scene);
		p.scene = scene;
		p.initLocals ();
		return p;
	}

	@Override
	protected void mergeStatistics (ProcessorBase src)
	{
		primaryCount += ((RayProcessorBase) src).primaryCount;
	}

	@Override
	protected void initLocals ()
	{
		ilist = new IntersectionList ();
		enteredSolids = new ArrayList<Volume> ();
		locals = new Locals ();
		tmpColor = new Point3d ();
		sumColor = new Point3d ();
	}

	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		this.renderer = renderer;
		this.scene = scene;
		initLocals ();
		lightProcessor.initialize (scene, getEnvironmentType (), new MTRandom (renderer.getSeed ()));
		setRecursionDepth (Math.min (Math.max (renderer.getNumericOption (
			RECURSION_DEPTH, maxDepth).intValue (), 0), 10));
	}


	public void initializeBeforeTracing (Random random){
		// to be overwritten
	}


	Locals locals;


	private double getIOR (Volume v, Spectrum spectrum)
	{
		Interior i = scene.getInterior (v);
		return (i != null) ? i.getIndexOfRefraction (spectrum) : 1;
	}

	public double getIOR (Intersection is, Spectrum spec)
	{
		switch (is.type)
		{
			case Intersection.PASSING:
				return 1;
			case Intersection.ENTERING:
				double outer = enteredSolids.isEmpty () ? 1
					: getIOR (enteredSolids.get (enteredSolids.size () - 1), spec);
				double inner = getIOR (is.solid, spec);
				return outer / inner;
			case Intersection.LEAVING:
				int s = enteredSolids.size ();
				if (s == 0)
				{
					return 1;
				}
				if (enteredSolids.get (s - 1) != is.solid)
				{
					return 1;
				}
				outer = (s == 1) ? 1 : getIOR (enteredSolids.get (s - 2), spec);
				inner = getIOR (is.solid, spec);
				return outer / inner;
			default:
				throw new AssertionError ();
		}
	}

	public int record (Intersection is, boolean reflected)
	{
		switch (is.type)
		{
			case Intersection.PASSING:
				return -2;
			case Intersection.ENTERING:
				if (reflected)
				{
					return -2;
				}
				enteredSolids.add (is.solid);
				return -1;
			case Intersection.LEAVING:
				if (reflected)
				{
					return -2;
				}
				int i = enteredSolids.lastIndexOf (is.solid);
				if (i >= 0)
				{
					enteredSolids.remove (i);
					return i;
				}
//				System.err.println ("Volume " + is.solid + " not found");
				return -2;
			default:
				throw new AssertionError ();
		}
	}

	public void unrecord (Intersection is, int index)
	{
		if (index >= 0)
		{
			enteredSolids.add (index, is.solid);
		}
		else if (index == -1)
		{
			enteredSolids.remove (enteredSolids.size () - 1);
		}
	}

	@Deprecated
	public double record (Intersection is, boolean push, Spectrum spec)
	{
		if(is == null) return 1;
		switch (is.type)
		{
			case Intersection.PASSING:
				return 1;
			case Intersection.LEAVING:
				push = !push;
			// no break
			case Intersection.ENTERING:
				if (!push)
				{
					int i = enteredSolids.lastIndexOf (is.solid);
					if (i < 0)
					{
//						System.err.println ("volume " + is.solid + " not found");
					}
					else
					{
						enteredSolids.remove (i);
					}
				}
				Interior interior = enteredSolids.isEmpty () ? null
						: scene.getInterior (enteredSolids.get (enteredSolids
							.size () - 1));
				double oldIor = (interior != null) ? interior
					.getIndexOfRefraction (spec) : 1;
				if (push)
				{
					enteredSolids.add (is.solid);
				}
				interior = scene.getInterior (is.solid);
				double newIor = (interior != null) ? interior
					.getIndexOfRefraction (spec) : 1;
				return oldIor / newIor;
			default:
				throw new AssertionError ();
		}
	}

	float getBrightness ()
	{
		return 1;
	}

	public void getColorFromRay (Line ray, Spectrum resp, Color4f color, Random random)
	{
		primaryCount++;
		enteredSolids.clear ();
		ilist.clear ();

		scene.computeIntersections (ray, Intersection.CLOSEST, ilist, null,
			null);
		if (ilist.size == 0)
		{
			// no intersection for this ray -> return transparency
			color.set (0, 0, 0, 0);
			return;
		}

		Intersection is = ilist.elements[0];

		sumColor.set (0, 0, 0);

		// calculate color recursively
		float transparency = traceRay (1, is, resp, sumColor,
			locals.nextReflected (), random);

		sumColor.scale (getBrightness ());
		color.x = (float) sumColor.x;
		color.y = (float) sumColor.y;
		color.z = (float) sumColor.z;
		color.w = (transparency < 1) ? 1 - transparency : 0;

	}

	public class Locals
	{
		public Locals nextTransmitted;
		public Locals nextReflected;

		public Spectrum newWeight = scene.createSpectrum ();
		public Ray reflected = new Ray (newWeight);
		public Ray transmitted = new Ray (newWeight);
		public Spectrum tmpSpectrum = scene.createSpectrum ();
		public Line tmpRay = new Line ();

		public Environment env = new Environment (scene.getBoundingBox (), newWeight, getEnvironmentType ());

		public ArrayList lightCache = new ArrayList ();

		public Locals ()
		{
			tmpRay.start = 0;
			tmpRay.end = Double.POSITIVE_INFINITY;
		}

		public Locals nextTransmitted ()
		{
			if (nextTransmitted == null)
			{
				nextTransmitted = new Locals ();
			}
			return nextTransmitted;
		}

		public Locals nextReflected ()
		{
			if (nextReflected == null)
			{
				nextReflected = new Locals ();
			}
			return nextReflected;
		}
	}
	
	abstract int getEnvironmentType ();

	abstract float traceRay (int depth, Intersection desc, Spectrum weight,
			Tuple3d color, Locals loc, Random random);

}
