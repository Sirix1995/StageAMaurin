
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

public class XData
{
	int[] ivals = null;
	long[] lvals = null;
	float[] fvals = null;
	double[] dvals = null;
	Object[] avals = null;


	public void init (XClass<? extends XObject> c)
	{
		if (c.isize > 0)
		{
			ivals = new int[c.isize];
		}
		if (c.lsize > 0)
		{
			lvals = new long[c.lsize];
		}
		if (c.fsize > 0)
		{
			fvals = new float[c.fsize];
		}
		if (c.dsize > 0)
		{
			dvals = new double[c.dsize];
		}
		if (c.asize > 0)
		{
			avals = new Object[c.asize];
		}
	}

/*!!
#foreach ($type in $vmtypes)
$pp.setType($type)

	int add${pp.PREFIX}Field ()
	{
		if (${pp.prefix}vals == null)
		{
			${pp.prefix}vals = new $type[1];
			return 0;
		}
		int l = ${pp.prefix}vals.length;
		$type[] a = new $type[l + 1];
		System.arraycopy (${pp.prefix}vals, 0, a, 0, l);
		${pp.prefix}vals = a;
		return l;
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	int addIField ()
	{
		if (ivals == null)
		{
			ivals = new int[1];
			return 0;
		}
		int l = ivals.length;
		int[] a = new int[l + 1];
		System.arraycopy (ivals, 0, a, 0, l);
		ivals = a;
		return l;
	}
// generated
// generated
	int addLField ()
	{
		if (lvals == null)
		{
			lvals = new long[1];
			return 0;
		}
		int l = lvals.length;
		long[] a = new long[l + 1];
		System.arraycopy (lvals, 0, a, 0, l);
		lvals = a;
		return l;
	}
// generated
// generated
	int addFField ()
	{
		if (fvals == null)
		{
			fvals = new float[1];
			return 0;
		}
		int l = fvals.length;
		float[] a = new float[l + 1];
		System.arraycopy (fvals, 0, a, 0, l);
		fvals = a;
		return l;
	}
// generated
// generated
	int addDField ()
	{
		if (dvals == null)
		{
			dvals = new double[1];
			return 0;
		}
		int l = dvals.length;
		double[] a = new double[l + 1];
		System.arraycopy (dvals, 0, a, 0, l);
		dvals = a;
		return l;
	}
// generated
// generated
	int addAField ()
	{
		if (avals == null)
		{
			avals = new Object[1];
			return 0;
		}
		int l = avals.length;
		Object[] a = new Object[l + 1];
		System.arraycopy (avals, 0, a, 0, l);
		avals = a;
		return l;
	}
//!! *# End of generated code

}
