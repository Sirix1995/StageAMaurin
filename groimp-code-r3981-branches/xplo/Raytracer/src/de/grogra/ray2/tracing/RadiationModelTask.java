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

import java.util.Random;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.task.PartialTask;
import de.grogra.task.Solver;
import de.grogra.task.SolverInOwnThread;
import de.grogra.task.Task;
import de.grogra.xl.util.ObjectList;

class RadiationModelTask extends Task
{
	ProgressMonitor monitor;

	long seed;

	long totalRayCount;
	long pendingRayCount;
	long[] totalRaysFromLight;
	double[] lightFactors;
	long[] sentRaysFromLight;
	
	int threadCount;
	int depth;
	double minPower;


	Scene originalScene;
	LightModelProcessor processor;


	public static class RadiationTask implements PartialTask
	{
		long rayCount;
		long[] raysFromLight;
		long[] seedOffsets;
	}


	void compute (Scene scene, long rayCount, long seed, ProgressMonitor progress, int depth, double minPower,
			ObjectList<Spectrum> radiantPowerSum, ObjectList<Spectrum> sensedIrradianceSum, int[] idToIndex,
			int threadCount)
	{
		this.depth = depth;
		this.minPower = minPower;
		this.seed = seed;
		this.totalRayCount = rayCount;
		this.monitor = progress;
		if (threadCount <= 0)
		{
			threadCount = Runtime.getRuntime ().availableProcessors ();
		}
		this.threadCount = threadCount;
		this.originalScene = scene;
		
		final int lightCount = scene.getLights ().length;
		final Light[] lights = scene.getLights ();

		if (lightCount == 0)
		{
			if (progress != null)
			{
				progress.showMessage (Resources.msg ("radiation.no-lights"));
			}
			return;
		}

		// continue if there is light in the scene
		Environment e = new Environment (scene.getBoundingBox (),
			scene.createSpectrum (), Environment.RADIATION_MODEL);

		// collect the power of the light sources
		final double[] powerOfLight = new double[lightCount];
		double totalPower = 0;
		for (int i = 0; i < lightCount; i++)
		{
			powerOfLight[i] = lights[i].getTotalPower (e);
			totalPower += powerOfLight[i];
		}
		if (!(totalPower > 0))
		{
			progress.showMessage (Resources.msg ("radiation.total-power-0"));
			return;
		}

		// calculate how many rays originate from each light source
		totalRaysFromLight = new long[lightCount];
		long totalRays = 0;
		for (int i = 0; i < lightCount; i++)
		{
			totalRaysFromLight[i] = (long) (totalRayCount * powerOfLight[i] / totalPower);
			totalRays += totalRaysFromLight[i];
		}

		MTRandom rnd = new MTRandom (seed);

		// randomly distribute remaining rays (because of rounding)
		while (totalRayCount > totalRays)
		{
			int light = rnd.nextInt (lightCount);
			if (powerOfLight[light] > 0)
			{
				totalRaysFromLight[light]++;
				totalRays++;
			}
		}
		pendingRayCount = totalRayCount;

		sentRaysFromLight = new long[lightCount];
		lightFactors = new double[lightCount];
		for (int i = 0; i < lightCount; i++)
		{
			lightFactors[i] = 1.0 / totalRaysFromLight[i];
		}

		processor = new LightModelProcessor (scene, radiantPowerSum, sensedIrradianceSum, idToIndex);
		
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
		solve ();
		removeSolvers ();
	}


	public Solver createLocalSolver (final boolean sameThread)
	{
		final LightModelProcessor lp = processor.dup (originalScene.dup ());

		return new SolverInOwnThread ()
		{
			private final Random rnd = new MTRandom ();

			@Override
			protected void solveImpl (PartialTask task)
			{
				RadiationTask t = (RadiationTask) task;
				lp.computeImpl (t.raysFromLight, t.seedOffsets, lightFactors, rnd, null, depth, minPower);
				t.raysFromLight = null;
				raysProcessed (t.rayCount);
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

	synchronized void raysProcessed (long count)
	{
		pendingRayCount -= count;
		if (monitor != null)
		{
			long done = totalRayCount - pendingRayCount;
			monitor.setProgress (Resources.msg ("radiation.progress", done * RadiationModel.BUNDLE_SIZE,
				totalRayCount * RadiationModel.BUNDLE_SIZE, threadCount), (float) done / totalRayCount);
		}
	}

	@Override
	protected synchronized boolean done ()
	{
		return pendingRayCount == 0;
	}

	@Override
	protected PartialTask nextPartialTask (int solverIndex)
	{
		int sc = getSolverCount ();
		RadiationTask task = new RadiationTask ();
		task.raysFromLight = new long[totalRaysFromLight.length];
		task.seedOffsets = new long[totalRaysFromLight.length];
		long total = 0;
		for (int i = 0; i < totalRaysFromLight.length; i++)
		{
			long n = totalRaysFromLight[i] - sentRaysFromLight[i];
			if (n > 0)
			{
				n = Math.min (Math.max (n / (3 * sc) + 1, Math.min (n, totalRaysFromLight[i] / (5 * sc))), 100000 / RadiationModel.BUNDLE_SIZE);
			}
			task.raysFromLight[i] = n;
			task.seedOffsets[i] = (seed * totalRaysFromLight.length + i) * totalRayCount + sentRaysFromLight[i];
			total += n;
			sentRaysFromLight[i] += n;
		}
		task.rayCount = total;
		return (total == 0) ? null : task;
	}

	@Override
	protected void dispose (PartialTask task)
	{
		long[] rays = ((RadiationTask) task).raysFromLight;
		if (rays != null)
		{
			for (int i = 0; i < rays.length; i++)
			{
				sentRaysFromLight[i] -= rays[i];
			}
		}
	}

}
