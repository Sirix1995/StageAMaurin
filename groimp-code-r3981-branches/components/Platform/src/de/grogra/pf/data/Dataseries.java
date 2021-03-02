
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


/**
 * A <code>Dataseries</code> represents a row or a column of a
 * {@link de.grogra.pf.data.Dataset}. I.e., is consists of a series
 * of {@link de.grogra.pf.data.Datacell}s. 
 *
 * @author Ole Kniemeyer
 */
public final class Dataseries
{
	private final Dataset dataset;
	private final boolean column;
	private final int index;
	

	Dataseries (Dataset ds, boolean column, int index)
	{
		dataset = ds;
		this.column = column;
		this.index = index;
	}

	
	/**
	 * Returns the <code>i</code>-th cell in this dataseries. If such a cell
	 * does not yet exist, it is created implicitly.
	 * 
	 * @param i index of cell
	 * @return <code>i</code>-th cell of dataseries
	 */
	public Datacell getCell (int i)
	{
		return dataset.getCell (column ? i : index, column ? index : i);
	}

	public int size ()
	{
		return column ? dataset.getRowCount (index) : dataset.getColumnCount (index);
	}
	
	public Datacell addCell ()
	{
		return getCell (column ? dataset.getRowCount (index) : dataset.getColumnCount (index));
	}
	
	/**
	 * Sets the x-value of cell <code>i</code> to <code>v</code>.
	 * 
	 * @param i cell index
	 * @param v new x-value
	 * @return this dataseries
	 * @see Datacell#setX(Number)
	 */
	public Dataseries setX (int i, Number v)
	{
		getCell (i).setX (v);
		return this;
	}

	
	/**
	 * Sets the y-value of cell <code>i</code> to <code>v</code>.
	 * 
	 * @param i cell index
	 * @param v new y-value
	 * @return this dataseries
	 * @see Datacell#setY(Number)
	 */
	public Dataseries setY (int i, Number v)
	{
		getCell (i).setY (v);
		return this;
	}

	
	/**
	 * Sets the z-value of cell <code>i</code> to <code>v</code>.
	 * 
	 * @param i cell index
	 * @param v new z-value
	 * @return this dataseries
	 * @see Datacell#setZ(Number)
	 */
	public Dataseries setZ (int i, Number v)
	{
		getCell (i).setZ (v);
		return this;
	}

	
	/**
	 * Sets the value of cell <code>i</code> to <code>v</code>.
	 * 
	 * @param i cell index
	 * @param v new value
	 * @return this dataseries
	 * @see Datacell#set(Number)
	 */
	public Dataseries set (int i, Number v)
	{
		getCell (i).set (v);
		return this;
	}

	
	/**
	 * Sets the value of cell <code>i</code> to <code>v</code>.
	 * 
	 * @param i cell index
	 * @param v new text value
	 * @return this dataseries
	 * @see Datacell#setText(String)
	 */
	public Dataseries setText (int i, String v)
	{
		getCell (i).setText (v);
		return this;
	}

	
	/**
	 * Sets the value of cell <code>i</code> to the pair <code>(x, y)</code>.
	 * 
	 * @param i cell index
	 * @param x new x-value
	 * @param y new y-value
	 * @return this dataseries
	 * @see Datacell#set(Number, Number)
	 */
	public Dataseries set (int i, Number x, Number y)
	{
		getCell (i).set (x, y);
		return this;
	}

	
	/**
	 * Sets the value of cell <code>i</code> to the triple <code>(x, y, z)</code>.
	 * 
	 * @param i cell index
	 * @param x new x-value
	 * @param y new y-value
	 * @param z new z-value
	 * @return this dataseries
	 * @see Datacell#set(Number, Number, Number)
	 */
	public Dataseries set (int i, Number x, Number y, Number z)
	{
		getCell (i).set (x, y, z);
		return this;
	}

	public Dataseries add (Number v)
	{
		return set (size (), v);
	}

	public Dataseries add (Number x, Number y)
	{
		return set (size (), x, y);
	}

	public Dataseries add (Number x, Number y, Number z)
	{
		return set (size (), x, y, z);
	}

	public Dataseries operator$shl (Number v)
	{
		return add (v);
	}

	public Dataseries operator$shl (double[] v)
	{
		switch (v.length)
		{
			case 1:
				set (size (), v[0]);
				break;
			case 2:
				set (size (), v[0], v[1]);
				break;
			case 3:
				set (size (), v[0], v[1], v[2]);
				break;
		}
		return this;
	}

}
