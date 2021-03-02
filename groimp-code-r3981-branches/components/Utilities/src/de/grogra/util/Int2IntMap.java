

// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.util;

import de.grogra.xl.util.IntList;

public class Int2IntMap
{
	private int[] keys;
	private int[] values;
	private int size;


	public Int2IntMap (int capacity)
	{
		if (capacity < 4)
		{
			capacity = 4;
		}
		keys = new int[capacity];
		values = new int[capacity];
	}


	public Int2IntMap ()
	{
		this (16);
	}


	public Int2IntMap (int[] keys, int off, int len)
	{
		this (len);
		System.arraycopy (keys, off, this.keys, 0, len);
		size = len;
	}


	public Int2IntMap dup ()
	{
		Int2IntMap d = new Int2IntMap (size);
		System.arraycopy (keys, 0, d.keys, 0, size);
		System.arraycopy (values, 0, d.values, 0, size);
		d.size = size;
		return d;
	}


	public int[] getKeys (int[] array)
	{
		if ((array == null) || (array.length < size))
		{
			array = new int[size];
		}
		System.arraycopy (keys, 0, array, 0, size);
		return array;
	}


	public int[] getValues (int[] array)
	{
		if (array == null)
		{
			array = new int[size];
		}
		else if (array.length < size)
		{
			array = new int[size];
		}
		System.arraycopy (values, 0, array, 0, size);
		return array;
	}


	public final synchronized int synchronizedPut (int key, int value)
	{
		return put (key, value);
	}


	public final int put (int key, int value)
	{
		int i = IntList.binarySearch (keys, key, 0, size);
		if (i >= 0)
		{
			int prev = values[i];
			values[i] = value;
			return prev;
		}
		i = ~i;
		if (keys.length == size)
		{
			System.arraycopy (keys, 0, keys = new int[2 * size], 0, size);
			System.arraycopy (values, 0, values = new int[2 * size], 0,
							  size);
		}
		if (i < size)
		{
			System.arraycopy (keys, i, keys, i + 1, size - i);
			System.arraycopy (values, i, values, i + 1, size - i);
		}
		keys[i] = key;
		values[i] = value;
		size++;
		return 0;
	}


	public final int get (int key)
	{
		int i = IntList.binarySearch (keys, key, 0, size);
		return (i >= 0) ? values[i] : 0;
	}


	public final synchronized int synchronizedGet (int key)
	{
		return get (key);
	}


	public final int remove (int key)
	{
		int i = IntList.binarySearch (keys, key, 0, size);
		if (i >= 0)
		{
			int o = values[i];
			size--;
			System.arraycopy (keys, i + 1, keys, i, size - i);
			System.arraycopy (values, i + 1, values, i, size - i);
			return o;
		}
		return 0;
	}


	public final synchronized int synchronizedRemove (int key)
	{
		return remove (key);
	}


	public int size ()
	{
		return size;
	}


	public void clear ()
	{
		size = 0;
	}


	public int getKeyAt (int index)
	{
		return keys[index];
	}


	public int getValueAt (int index)
	{
		return values[index];
	}


	public int findIndex (int key)
	{
		return IntList.binarySearch (keys, key, 0, size);
	}


	public void setValueAt (int index, int value)
	{
		values[index] = value;
	}

}
