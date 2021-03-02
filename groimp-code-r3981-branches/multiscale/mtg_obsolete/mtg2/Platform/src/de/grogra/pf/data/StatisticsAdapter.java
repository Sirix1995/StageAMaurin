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

import java.util.Collections;
import java.util.List;

import org.jfree.data.statistics.StatisticalCategoryDataset;


public final class StatisticsAdapter extends DatasetAdapterBase implements StatisticalCategoryDataset
{
	public StatisticsAdapter (Dataset ds)
	{
		super (ds);
	}

	public Number getMeanValue (int row, int column)
	{
		if (row < 0)
		{
			return 0;
		}
		int n = 0;
		double sum = 0;
		while (dataset.hasCell0 (n, row))
		{
			sum += dataset.getCell0 (n++, row).getY ();
		}
		return (n > 0) ? sum / n : 0;
	}

	public Number getMeanValue (Comparable rowKey, Comparable columnKey)
	{
		return getMeanValue (getRowIndex (rowKey), 0);
	}

	public Number getStdDevValue (int row, int column)
	{
		if (row < 0)
		{
			return 0;
		}
		int n = 0;
		double sum = 0;
		double squareSum = 0;
		while (dataset.hasCell0 (n, row))
		{
			double v = dataset.getCell0 (n++, row).getY ();
			sum += v;
			squareSum += v * v;
		}
		if (n == 0)
		{
			return 0;
		}
		sum = (squareSum * n - sum * sum) / (n * n);
		return (sum > 0) ? Math.sqrt (sum) : 0;
	}

	public Number getStdDevValue (Comparable rowKey, Comparable columnKey)
	{
		return getStdDevValue (getRowIndex (rowKey), 0);
	}

	public int getColumnIndex (Comparable key)
	{
		return 0;
	}

	public Comparable getColumnKey (int column)
	{
		return "";
	}

	private static final List<String> COLUMN_KEYS = Collections.singletonList ("");

	public List getColumnKeys ()
	{
		return COLUMN_KEYS;
	}

	public int getRowIndex (Comparable key)
	{
		return indexOf (key);
	}

	public Comparable getRowKey (int row)
	{
		return getSeriesKey (row);
	}

	public List getRowKeys ()
	{
		return dataset.seriesInRows ? dataset.rowKeys : dataset.columnKeys;
	}

	public Number getValue (Comparable rowKey, Comparable columnKey)
	{
		return getMeanValue (rowKey, columnKey);
	}

	public int getColumnCount ()
	{
		return 1;
	}

	public int getRowCount ()
	{
		return getSeriesCount ();
	}

	public Number getValue (int row, int column)
	{
		return getMeanValue (row, column);
	}

}
