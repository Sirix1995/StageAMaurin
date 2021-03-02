
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

import java.util.*;
import javax.swing.event.*;
import de.grogra.persistence.*;
import de.grogra.xl.util.ObjectList;

import org.jfree.data.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.xy.*;

public final class DatasetAdapter extends DatasetAdapterBase implements
	ValueDataset, KeyedValueDataset,
	CategoryDataset, PieDataset, SeriesDataset,
	XYDataset, TableXYDataset, XYZDataset, StatisticalCategoryDataset
{
	
	DatasetAdapter (Dataset set)
	{
		super (set);
	}


	public int getItemCount (int series)
	{
		return dataset.seriesInRows ? dataset.getColumnCount (series) : dataset.getRowCount (series);
	}


	public Number getX (int series, int item)
	{
		Datacell c = dataset.getCell0 (item, series);
		if (!c.isNull () && (c.x == c.y))
		{
			return Integer.valueOf (item);
		}
		return c.x;
	}


	public Number getY (int series, int item)
	{
		return dataset.getCell0 (item, series).y;
	}


	public Number getZ (int series, int item)
	{
		return dataset.getCell0 (item, series).z;
	}


	public Number getValue ()
	{
		return getValue (0);
	}


	public Comparable getKey ()
	{
		return getKey (0);
	}


	public Comparable getRowKey	(int row)
	{
		return getSeriesKey (row);
	}


	public int getRowIndex (Comparable key)
	{
		return indexOf (key);
	}


	public List getRowKeys ()
	{
		return dataset.seriesInRows ? dataset.rowKeys : dataset.columnKeys;
	}


	public int getRowCount ()
	{
		return getSeriesCount ();
	}


	public Comparable getColumnKey (int column)
	{
		return dataset.seriesInRows ? dataset.getColumnKey (column) : dataset.getRowKey (column);
	}


	public int getColumnIndex (Comparable key)
	{
		return getColumnKeys ().indexOf (key);
	}


	public List getColumnKeys ()
	{
		return dataset.seriesInRows ? dataset.columnKeys : dataset.rowKeys;
	}


	public int getColumnCount ()
	{
		return getItemCount ();
	}


	public Number getValue (Comparable rowKey, Comparable columnKey)
	{
		return getValue (getRowIndex (rowKey), getColumnIndex (columnKey));
	}


	public Number getValue (int row, int column)
	{
		return getY (row, column);
	}


	public Comparable getKey (int index)
	{
		return getColumnKey (index);
	}


	public int getIndex (Comparable key)
	{
		return getColumnIndex (key);
	}


	public List getKeys ()
	{
		return getColumnKeys ();
	}


	public Number getValue (Comparable key)
	{
		return getValue (0, getColumnIndex (key));
	}


	public int getItemCount ()
	{
		return dataset.seriesInRows ? dataset.getColumnCount () : dataset.getRowCount ();
	}


	public Number getValue (int item)
	{
		return getValue (0, item);
	}


	public double getXValue (int series, int item)
	{
		return getX (series, item).doubleValue ();
	}


	public double getYValue (int series, int item)
	{
		return getY (series, item).doubleValue ();
	}


	public double getZValue (int series, int item)
	{
		return getZ (series, item).doubleValue ();
	}


	public Number getMeanValue (int row, int column)
	{
		return getX (row, column);
	}


	public Number getMeanValue (Comparable rowKey, Comparable columnKey)
	{
		return getX (getRowIndex (rowKey), getColumnIndex (columnKey));
	}


	public Number getStdDevValue (int row, int column)
	{
		return getY (row, column);
	}


	public Number getStdDevValue (Comparable rowKey, Comparable columnKey)
	{
		return getY (getRowIndex (rowKey), getColumnIndex (columnKey));
	}

}
