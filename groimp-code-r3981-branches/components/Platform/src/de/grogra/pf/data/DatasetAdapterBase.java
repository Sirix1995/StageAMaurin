
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
import org.jfree.data.xy.*;

public abstract class DatasetAdapterBase implements org.jfree.data.general.Dataset
{
	final Dataset dataset;
	ArrayList listeners = null;

	
	DatasetAdapterBase (Dataset set)
	{
		this.dataset = set;
	}


	public void addChangeListener (DatasetChangeListener listener)
	{
		synchronized (dataset)
		{
			if (listeners == null)
			{
				listeners = new ArrayList ();			
			}
			listeners.add (listener);
		}
	}


	public void removeChangeListener (DatasetChangeListener listener)
	{
		synchronized (dataset)
		{
			if (listeners != null)
			{
				listeners.remove (listener);
			}
		}
	}


	public void setGroup (DatasetGroup group)
	{
		dataset.setGroup (group);
	}


	public DatasetGroup getGroup ()
	{
		return dataset.getGroup ();
	}


	public DomainOrder getDomainOrder ()
	{
		return dataset.getDomainOrder ();
	}


	public int getSeriesCount ()
	{
		return dataset.seriesInRows ? dataset.getRowCount () : dataset.getColumnCount ();
	}


	public Comparable getSeriesKey (int series)
	{
		return dataset.seriesInRows ? dataset.getRowKey (series) : dataset.getColumnKey (series);
	}


	public int indexOf (Comparable seriesKey)
	{
		return (dataset.seriesInRows ? dataset.columnKeys : dataset.rowKeys).indexOf (seriesKey);
	}

}
