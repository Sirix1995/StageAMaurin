
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

package de.grogra.rgg;

import de.grogra.pf.registry.*;
import de.grogra.xl.lang.FloatToFloat;
import de.grogra.math.*;

@Deprecated
public class Function extends ItemReference
	implements FloatToFloat
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

		public Type (Function representative, de.grogra.persistence.SCOType supertype)
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
			return new Function ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Function.class);
		$TYPE.validate ();
	}

//enh:end

	Function ()
	{
		super (null);
	}


	public Function (String name)
	{
		super (name);
	}
	

	// for backward compatibility
	public float evaluate (float x)
	{
		return evaluateFloat (x);
	}


	public synchronized float evaluateFloat (float x)
	{
		return ((FloatToFloat)
				(objectResolved ? object
				: resolveObject ("/objects/math/functions", Registry.current ())))
			.evaluateFloat (x);
	}

}
