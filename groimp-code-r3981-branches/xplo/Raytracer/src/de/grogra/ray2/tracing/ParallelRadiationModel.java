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

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Scene;
import de.grogra.xl.util.ObjectList;

public class ParallelRadiationModel extends RadiationModel
{
	private final Scene scene;
	private final int threadCount;


	public ParallelRadiationModel (Scene scene, int[] idToIndex, int threadCount)
	{
		super (scene.createSpectrum (), new ObjectList<Spectrum> (), new ObjectList<Spectrum> (), idToIndex);
		this.scene = scene;
		if (threadCount <= 0)
		{
			threadCount = Runtime.getRuntime ().availableProcessors ();
		}
		this.threadCount = threadCount;
	}

	public void compute (long rayCount, long seed, ProgressMonitor progress, int depth, double minPower)
	{
		RadiationModelTask t = new RadiationModelTask ();
		rayCount = (rayCount + BUNDLE_SIZE - 1) / BUNDLE_SIZE;
		t.compute (scene, rayCount, seed, progress, depth, minPower, radiantPowerSum, sensedIrradianceSum, idToGroup, threadCount);
	}

}
