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

package de.grogra.pf.data;

import java.util.ArrayList;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.xy.IntervalXYDataset;


public final class HistogramAdapter extends DatasetAdapterBase implements IntervalXYDataset
{
	public HistogramAdapter (Dataset ds)
	{
		super (ds);
	}

	public double getXValue (int series, int item)
	{
		ArrayList<HistogramBin> bins = dataset.getBins (series);
		HistogramBin bin = bins.get (item);
		return 0.5 * (bin.getStartBoundary () + bin.getEndBoundary ());
	}

	public double getStartXValue (int series, int item)
	{
		ArrayList<HistogramBin> bins = dataset.getBins (series);
		return bins.get (item).getStartBoundary ();
	}

	public double getEndXValue (int series, int item)
	{
		ArrayList<HistogramBin> bins = dataset.getBins (series);
		return bins.get (item).getEndBoundary ();
	}

	public double getYValue (int series, int item)
	{
		HistogramBin bin = dataset.getBins (series).get (item);
		double v = 0;
		for (int i = 0; dataset.hasCell0 (i, series); i++)
		{
			Datacell c = dataset.getCell0 (i, series);
			if (!c.isXNull ())
			{
				double x = c.getX ();
				if (x <= bin.getEndBoundary ())
				{
					if ((item == 0) ? (x >= bin.getStartBoundary ()) : (x > bin.getStartBoundary ()))
					{
						if (c.isScalar ())
						{
							v++;
						}
						else if (!c.isYNull ())
						{
							v += c.getY ();
						}
					}
				}
			}
		}
		return v;
	}

	public int getItemCount (int series)
	{
		ArrayList<HistogramBin> bins = dataset.getBins (series);
		return bins.size ();
	}

	public Number getEndX (int series, int item)
	{
		return getEndXValue (series, item);
	}

	public Number getEndY (int series, int item)
	{
		return getY (series, item);
	}

	public double getEndYValue (int series, int item)
	{
		return getYValue (series, item);
	}

	public Number getStartX (int series, int item)
	{
		return getStartXValue (series, item);
	}

	public Number getStartY (int series, int item)
	{
		return getY (series, item);
	}

	public double getStartYValue (int series, int item)
	{
		return getYValue (series, item);
	}

	public Number getX (int series, int item)
	{
		return getXValue (series, item);
	}

	public Number getY (int series, int item)
	{
		return getYValue (series, item);
	}
}
