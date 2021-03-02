
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

import de.grogra.pf.registry.*;

public final class DatasetRef extends ItemReference<Dataset>
{
	//enh:sco
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ItemReference.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (DatasetRef representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ItemReference.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new DatasetRef ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (DatasetRef.class);
		$TYPE.validate ();
	}

//enh:end

	DatasetRef ()
	{
		super (null);
	}

	
	public DatasetRef (String name)
	{
		super (name);
	}


	public synchronized Dataset resolve ()
	{
		return objectResolved ? object
			 : resolveObject ("/objects/datasets", Registry.current ());
	}

	public Dataset toDataset ()
	{
		return resolve ();
	}
	
	@Override
	protected Item createItem (RegistryContext ctx, String dir, String name)
	{
		Dataset ds = new Dataset ();
		ds.setTitle (name);
		Item i = new SharedValue (name, ds);
		ctx.getRegistry ().getDirectory (dir, null).add (i);
		i.makeUserItem (false);
		return i;
	}


	public Dataseries addRow ()
	{
		return resolve ().addRow ();
	}


	public Dataseries addColumn ()
	{
		return resolve ().addColumn ();
	}


	public Dataseries getRow (int row)
	{
		return resolve ().getRow (row);
	}


	public Dataseries getColumn (int column)
	{
		return resolve ().getColumn (column);
	}


	public Dataseries addRow (Comparable key)
	{
		return resolve ().addRow (key);
	}


	public Dataseries addColumn (Comparable key)
	{
		return resolve ().addColumn (key);
	}


	public Dataset clear ()
	{
		return resolve ().clear ();
	}


	public Dataset setRowKey (int item, Comparable key)
	{
		return resolve ().setRowKey (item, key);
	}


	public Dataset setColumnKey (int item, Comparable key)
	{
		return resolve ().setColumnKey (item, key);
	}


	public Dataset setTitle (String s)
	{
		return resolve ().setTitle (s);
	}


	public Dataset setSeriesInRows (boolean value)
	{
		return resolve ().setSeriesInRows (value);
	}


	public Dataset setRowLabel (String s)
	{
		return resolve ().setCategoryLabel (s);
	}


	public Dataset setColumnLabel (String s)
	{
		return resolve ().setValueLabel (s);
	}

	
	public Dataset setHistogramBins (int col, double min, double max, int count)
	{
		return resolve ().setHistogramBins (col, min, max, count);
	}


	public Dataseries operator$shl (Number v)
	{
		return resolve ().operator$shl (v);
	}

}
