
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

import javax.vecmath.*;
import de.grogra.persistence.*;

public final class TMatrix3d extends Matrix3d implements Transform2D
{
	public static final ManageableType $TYPE = new Matrix3dType
		(new TMatrix3d (), Matrix3dType.$TYPE).validate ();

	private transient int stamp = 0;

	public TMatrix3d ()
	{
		super ();
		setIdentity ();
	}


	public TMatrix3d (Matrix3d m)
	{
		super (m);
	}


	public TMatrix3d (Tuple2d t)
	{
		super ();
		setIdentity ();
		m02 = t.x;
		m12 = t.y;
	}


	@Override
	public Object clone ()
	{
		return new TMatrix3d (this);
	}


	public void transform (Matrix3d in, Matrix3d out)
	{
		de.grogra.vecmath.Math2.mulAffine (out, in, this);
	}


	public ManageableType getManageableType ()
	{
		return $TYPE;
	}


	public static Transform2D createTransform (Matrix3d t)
	{ 
		double d;
		if ((d = t.m00 - 1) * d + (d = t.m01) * d
			+ (d = t.m10) * d + (d = t.m11 - 1) * d < 1e-8)
		{
			if ((d = t.m02) * d + (d = t.m12) * d < 1e-16)
			{
				return null;
			}
			else
			{
				return new TVector2d (t.m02, t.m12);
			}
		}
		else
		{
			return new TMatrix3d (t);
		}
	}


	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
	}
	
	
	public int getStamp ()
	{
		return stamp;
	}


	public Manageable manageableReadResolve ()
	{
		return this;
	}

	public Object manageableWriteReplace ()
	{
		return this;
	}

}
