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

import de.grogra.ray.physics.Collector;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.ObjectList;

/**
 * Abstract base class for radiation model. 
 * 
 * @author Ole Kniemeyer
 *
 */
public abstract class RadiationModel implements Cloneable
{
	/**
	 * Implementation groups rays into bundles of <code>BUNDLE_SIZE</code>
	 * rays which are computed at once.
	 */
	public static final int BUNDLE_SIZE = 1000;

	/**
	 * This field is set in the constructor to a completely black spectrum.
	 */
	final Spectrum black;

	/**
	 * For each volume group, the radiant power which is absorbed by its surface
	 * is stored in this array using the index of the volume group.
	 * 
	 * @see #idToGroup
	 */
	final ObjectList<Spectrum> radiantPowerSum;

	/**
	 * For each volume group, the irradiance which is sensed by its surface
	 * is stored in this array using the index of the volume group.
	 * 
	 * @see #idToGroup
	 */
	final ObjectList<Spectrum> sensedIrradianceSum;

	/**
	 * Specifies the grouping of volumes into groups. For each volume, the
	 * group to which it belongs is given by the value of this array when
	 * indexed with the ID of the volume.
	 * 
	 * @see Volume#getId()
	 */
	final int[] idToGroup;

	/**
	 * Create a new radiation model that
	 * adds collected radiation values to the lists.
	 * 
	 * @param spectrumFactory factory to create spectra
	 * @param radiantPowerSum list to which radiant powers are added
	 * @param sensedIrradianceSum list to which sensed irradiances are added
	 * @param idToGroup mapping from volume id to group index
	 */
	public RadiationModel (Spectrum spectrumFactory, ObjectList<Spectrum> radiantPowerSum,
			ObjectList<Spectrum> sensedIrradianceSum, int[] idToGroup)
	{
		this.black = spectrumFactory.newInstance ();
		this.black.setZero ();
		this.radiantPowerSum = radiantPowerSum;
		this.sensedIrradianceSum = sensedIrradianceSum;
		this.idToGroup = idToGroup;
	}


	static void addAndClear (double factor, ObjectList<Spectrum> add, ObjectList<Spectrum> sum)
	{
		synchronized (sum)
		{
			for (int i = add.size - 1; i >= 0; i--)
			{
				Spectrum s = add.get (i);
				if (s != null)
				{
					s.scale (factor);
					Spectrum t = sum.get (i);
					if (t == null)
					{
						t = s.clone ();
						sum.set (i, t);
					}
					else
					{
						t.add (s);
					}
					s.setZero ();
				}
			}
		}
	}


	public abstract void compute (long rayCount, long seed, ProgressMonitor progress, int depth, double minPower);


	/**
	 * Obtain the radiation power that was absorbed by a volume.
	 * @param volumeIndex index into {@link #idToGroup}
	 * @return 
	 */
	public Spectrum getAbsorbedPower (int volumeIndex)
	{
		Spectrum pwr = radiantPowerSum.get (volumeIndex);
		if (pwr == null)
		{
			pwr = black;
		}
		return pwr;
	}

	/**
	 * Obtain the irradiance that was sensed by a volume.
	 * @param volumeIndex index into {@link #idToGroup}
	 * @return
	 */
	public Spectrum getSensedIrradiance (int volumeIndex)
	{
		Spectrum pwr = sensedIrradianceSum.get (volumeIndex);
		if (pwr == null)
		{
			pwr = black;
		}
		return pwr;
	}


	public Collector getSensedIrradianceCollector(int volumeIndex) 
	{
		Spectrum col = sensedIrradianceSum.get (volumeIndex);
		
		if (!(col instanceof Collector))
		{
			col = black;
		}
		
		return (Collector) col;
	}


	public Collector getAbsorbedPowerCollector(int volumeIndex) 
	{
		Spectrum col = radiantPowerSum.get (volumeIndex);
		
		if (!(col instanceof Collector))
		{
			col = black;
		}
		
		return (Collector) col;
	}

}
