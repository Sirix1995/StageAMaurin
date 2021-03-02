
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

package de.grogra.reflect;

public class BoundedType<T extends Number> extends TypeImpl<T>
{
	public static final BoundedType<Float> FLOAT_0_1 = new BoundedType<Float>
		("float[0,1]", TypeId.FLOAT, new Float (0), new Float (1));

	public static final BoundedType<Double> DOUBLE_0_1 = new BoundedType<Double>
		("double[0,1]", TypeId.DOUBLE, new Double (0), new Double (1));


	protected final Number min, max;


	public BoundedType (String name, int typeId, Number min, Number max)
	{
		super (name, Reflection.getType (typeId));
		this.min = min;
		this.max = max;
	}


	public Number getMin ()
	{
		return min;
	}


	public Number getMax ()
	{
		return max;
	}

	public String getBinaryName ()
	{
		return getSupertype ().getBinaryName ();
	}
}
