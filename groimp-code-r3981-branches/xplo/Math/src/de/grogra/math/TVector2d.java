
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

public class TVector2d extends Vector2d implements Transform2D
{
	public static final ManageableType $TYPE = new Tuple2dType
		(new TVector2d (), Tuple2dType.$TYPE).validate ();

	private transient int stamp = 0;

	public TVector2d ()
	{
		super ();
	}


	public TVector2d (Tuple2d t)
	{
		super (t);
	}


	public TVector2d (double x, double y)
	{
		super (x, y);
	}


	@Override
	public Object clone ()
	{
		return new TVector2d (this);
	}


	public void transform (Matrix3d in, Matrix3d out)
	{
		if (in != out)
		{
			out.set (in);
		}
		out.m02 += out.m00 * x + out.m01 * y;
		out.m12 += out.m10 * x + out.m11 * y;
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
