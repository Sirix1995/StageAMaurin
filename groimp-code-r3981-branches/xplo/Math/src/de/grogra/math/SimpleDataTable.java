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

package de.grogra.math;

import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.ObjectList;

public class SimpleDataTable implements DataTable
{
	private ObjectList<DoubleList> rows = new ObjectList<DoubleList> ();

	private final int columnCount;

	public SimpleDataTable (int columnCount)
	{
		this.columnCount = columnCount;
	}

	public void clear ()
	{
		rows.clear ();
	}

	public int getRowCount ()
	{
		return rows.size ();
	}

	public int getColumnCount ()
	{
		return columnCount;
	}

	public double getValue (int row, int column)
	{
		DoubleList list = rows.get (row);
		return (list != null) ? list.get (column) : 0;
	}

	public DoubleList getRow (int row)
	{
		return rows.get (row);
	}

	public DoubleList addRow ()
	{
		DoubleList list = new DoubleList (columnCount);
		rows.add (list);
		return list;
	}

	public DoubleList operator$shl (double value)
	{
		DoubleList list = addRow ();
		list.add (value);
		return list;
	}
}
