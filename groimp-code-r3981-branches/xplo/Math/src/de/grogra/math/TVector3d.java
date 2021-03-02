
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
import de.grogra.xl.lang.ConversionConstructor;

/**
 * A <code>TVector3d</code> is a <code>Vector3d</code> which implements
 * the interface {@link de.grogra.math.Transform3D}. It represents
 * a coordinate translation.
 * 
 * @author Ole Kniemeyer
 */
public final class TVector3d extends Vector3d implements Transform3D
{
	public static final ManageableType $TYPE = new Tuple3dType
		(new TVector3d (), Tuple3dType.$TYPE).validate ();

	private transient int stamp = 0;

	public TVector3d ()
	{
		super ();
	}


	@ConversionConstructor
	public TVector3d (Tuple3d t)
	{
		super (t);
	}


	public TVector3d (double x, double y, double z)
	{
		super (x, y, z);
	}


	@Override
	public Object clone ()
	{
		return new TVector3d (this);
	}


	public void transform (Matrix4d in, Matrix4d out)
	{
		if (in != out)
		{
			out.set (in);
		}
		out.m03 += out.m00 * x + out.m01 * y + out.m02 * z;
		out.m13 += out.m10 * x + out.m11 * y + out.m12 * z;
		out.m23 += out.m20 * x + out.m21 * y + out.m22 * z;
	}


	public ManageableType getManageableType ()
	{
		return $TYPE;
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
