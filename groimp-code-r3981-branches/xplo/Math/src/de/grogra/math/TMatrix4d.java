
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

public final class TMatrix4d extends Matrix4d implements Transform3D
{
	public static final ManageableType $TYPE = new Matrix4dType
		(new TMatrix4d (), Matrix4dType.$TYPE).validate ();

	private transient int stamp = 0;

	public TMatrix4d ()
	{
		super ();
		setIdentity ();
	}


	public TMatrix4d (Matrix4d m)
	{
		super (m);
	}


	public TMatrix4d (Matrix3d m)
	{
		super ();
		set (m);
	}

	@ConversionConstructor
	public TMatrix4d (Tuple3d t)
	{
		super ();
		setIdentity ();
		m03 = t.x;
		m13 = t.y;
		m23 = t.z;
	}
	
	@ConversionConstructor
	public TMatrix4d(ComponentTransform ct) {
		super();
		setIdentity();
		ct.transform(this, this);
	}


	@Override
	public Object clone ()
	{
		return new TMatrix4d (this);
	}


	public void transform (Matrix4d in, Matrix4d out)
	{
		de.grogra.vecmath.Math2.mulAffine (out, in, this);
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
