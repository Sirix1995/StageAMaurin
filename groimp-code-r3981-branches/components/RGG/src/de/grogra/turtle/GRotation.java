
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

package de.grogra.turtle;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;

public abstract class GRotation extends Tropism
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (GRotation.class);
		$TYPE.validate ();
	}

//enh:end

	public abstract float getTropismStrength (Object node, GraphState gs);


	public void preTransform (Object object, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object object, boolean asNode,
							   Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		// transformation from local to global coordinates before GRotation node
		Matrix34d r = GlobalTransformation.getParentValue (object, asNode, gs, true);

		// v : local growth direction (x, y, z)
		double v0 = r.m02, v1 = r.m12, v2 = r.m22;
		double q = 1 / Math.sqrt (v0 * v0 + v1 * v1 + v2 * v2);
		v0 *= q;
		v1 *= q;
		v2 *= q;
		
		double v02 = v0 * v0;
		double v12 = v1 * v1;
		
		// q : x^2 + y^2
		q = v02 + v12;

		// rotation matrix: intended global rotation matrix after GRotation
		double m00, m10, m20, m01, m11, m21, m02, m12, m22;

		double g = getTropismStrength (object, gs);
		if ((q < 1e-10) || (v2 * v2 > 0.99999))
		{
			// growth direction is parallel to global z-axis
			if (v2 * (v2 - g) < 0)
			{
				// flip z-axis
				m00 = r.m00; m10 = -r.m10; m20 = -r.m20;
				m01 = r.m01; m11 = -r.m11; m21 = -r.m21;
				m02 = r.m02; m12 = -r.m12; m22 = -r.m22;
			}
			else
			{
				if (out != pre)
				{
					out.set (pre);
				}
				return;
			}
		}
		else
		{
			// at first, compute m as rotation matrix which rotates
			// r to the desired new global transformation
			
			// we want  m v = w  with w = normalized direction v - g e2
			// where e2 is unit vector in z direction

			// 1 / n = length of v - g e2  so that  w = n (v - g e2)  
			double n = 1 / Math.sqrt (1 - 2 * g * v2 + g * g);
			// we have  m22 = e2 m e2 = v m v = v w
			m22 = (1 - g * v2) * n;

			// if  a = v x w,  we have  a ~ (v1, -v0, 0)
			// and  m a = a  and  m^T a = a
			// it follows m10 = m01, m20 = -m02, m21 = -m21
			
			m20 = -(m02 = g * v0 * n);
			m21 = -(m12 = g * v1 * n);

			q = 1 / q;
			m00 = (v12 + v02 * m22) * q;
			m11 = (v02 + v12 * m22) * q;
			m01 = m10 = v0 * v1 * (m22 - 1) * q;

			// m = m * r
			m00 = (v0 = m00) * r.m00 + (v1 = m01) * r.m10 + m02 * r.m20;
			m01 = v0 * r.m01 + v1 * r.m11 + m02 * r.m21;
			m02 = v0 * r.m02 + v1 * r.m12 + m02 * r.m22;
			m10 = (v0 = m10) * r.m00 + (v1 = m11) * r.m10 + m12 * r.m20;
			m11 = v0 * r.m01 + v1 * r.m11 + m12 * r.m21;
			m12 = v0 * r.m02 + v1 * r.m12 + m12 * r.m22;
			m20 = (v0 = m20) * r.m00 + (v1 = m21) * r.m10 + m22 * r.m20;
			m21 = v0 * r.m01 + v1 * r.m11 + m22 * r.m21;
			m22 = v0 * r.m02 + v1 * r.m12 + m22 * r.m22;
		}

		// out = r^-1 * m
		Math2.invertAffine (r, out);
		out.m00 = (v0 = out.m00) * m00 + (v1 = out.m01) * m10 + (v2 = out.m02) * m20;
		out.m01 = v0 * m01 + v1 * m11 + v2 * m21;
		out.m02 = v0 * m02 + v1 * m12 + v2 * m22;
		out.m03 += (v0 * r.m03 + v1 * r.m13 + v2 * r.m23);
		out.m10 = (v0 = out.m10) * m00 + (v1 = out.m11) * m10 + (v2 = out.m12) * m20;
		out.m11 = v0 * m01 + v1 * m11 + v2 * m21;
		out.m12 = v0 * m02 + v1 * m12 + v2 * m22;
		out.m13 += (v0 * r.m03 + v1 * r.m13 + v2 * r.m23);
		out.m20 = (v0 = out.m20) * m00 + (v1 = out.m21) * m10 + (v2 = out.m22) * m20;
		out.m21 = v0 * m01 + v1 * m11 + v2 * m21;
		out.m22 = v0 * m02 + v1 * m12 + v2 * m22;
		out.m23 += (v0 * r.m03 + v1 * r.m13 + v2 * r.m23);

		// set out to in * out = in * r^-1 * m
		Math2.mulAffine (out, in, out);
	}

}
