
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

package de.grogra.imp3d.objects;

import javax.vecmath.*;
import de.grogra.imp3d.*;
import de.grogra.imp.*;
import de.grogra.vecmath.Math2;
import de.grogra.math.Transform3D;

public abstract class FrustumBase extends Axis implements Pickable, Renderable
{
	public static final int BASE_OPEN_MASK = 1 << Axis.USED_BITS;
	public static final int TOP_OPEN_MASK = 2 << Axis.USED_BITS;

	public static final int USED_BITS = Axis.USED_BITS + 2;


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (FrustumBase.class);
		initType ();
		$TYPE.validate ();
	}

//enh:end


	public static void pick (float zheight, float br, float tr,
							 boolean bc, boolean tc,
							 Point3d origin, Vector3d direction, PickList list)
	{
		double t, bx, by, bz, ax, ay, az, tx, ty, tz, ad, ab, a2, a4, dr;
		ad = zheight * direction.z;
		bx = -origin.x;
		by = -origin.y;
		if (ad >= 0d)
		{
			bz = -origin.z;
		}
		else
		{
			bz = zheight - origin.z;
			zheight = -zheight;
			ad = -ad;
			t = br;
			br = tr;
			tr = (float) t;
			boolean tb = bc;
			bc = tc;
			tc = tb;
		}
		ab = bz * zheight;
		a2 = zheight * zheight;
		t = (ab + a2) / ad;
		if ((ad > 0) && (t < 0))
		{
			return;
		}
		if (tc)
		{
			tx = bx - t * direction.x;
			ty = by - t * direction.y;
			tz = bz + zheight - t * direction.z;
			if (tx * tx + ty * ty + tz * tz <= tr * tr)
			{
				list.add (t);
			}
		}
		t = ab / ad;
		if (bc && (t >= 0))
		{
			tx = bx - t * direction.x;
			ty = by - t * direction.y;
			tz = bz - t * direction.z;
			if (tx * tx + ty * ty + tz * tz <= br * br)
			{
				list.add (t);
			}
		}
		dr = tr - br;
		double[] l = list.getDoubleArray (0, 2);
		t = dr * dr + a2;
		a4 = a2 * a2;
		bx += origin.x;
		by += origin.y;
		bz += origin.z;
		tr = (float) ab;
		ab += origin.z * zheight;
		if (Math2.quadricIntersection (-a4, 0, 0,
									   -a4, 0, t * zheight * zheight - a4,
									   a4 * bx,
									   a4 * by,
									   a4 * bz - t * ab * zheight
									   + a2 * br * dr * zheight,
									   t * ab * ab - a4 * (bx * bx + by * by
														   + bz * bz)
									   + a4 * br * br - 2 * a2 * br * dr * ab,
									   origin, direction, l))
		{
			if ((tr <= ad * l[0]) && (ad * l[0] <= tr + a2))
			{
				list.add (l[0]);
			}
			if ((tr <= ad * l[1]) && (ad * l[1] <= tr + a2))
			{
				list.add (l[1]);
			}
		}
	}


	public FrustumBase ()
	{
		super ();
	}


	public FrustumBase (Transform3D transform)
	{
		super (transform);
	}

}
