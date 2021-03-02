
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

package de.grogra.ray.physics;

import javax.vecmath.Vector3f;

public final class Tests
{
	public static void computeAlbedo (Scattering s, int angleSteps, int n)
	{
		Environment env = new Environment (null, new Spectrum3d (), Environment.PATH_TRACER);
		env.iorRatio = 1;
		env.normal.set (0, 0, 1);
		Vector3f in = new Vector3f ();
		Vector3f out = new Vector3f ();
		Spectrum3d spec = new Spectrum3d (1, 1, 1);
		Spectrum3d res = new Spectrum3d ();
		double dphi = 2 * Math.PI / n;
		double dtheta = Math.PI / (2 * n);
		for (int k = 0; k < angleSteps; k++)
		{
			double sum = 0;
			double angle = k * Math.PI / (2 * angleSteps);
			in.x = (float) Math.sin (angle);
			in.z = (float) Math.cos (angle);
			for (int i = 0; i < n; i++)
			{
				double theta = (i + 0.5) * dtheta;
				double cost = Math.cos (theta);
				double sint = Math.sin (theta);
				out.z = (float) cost;
				for (int j = 0; j < n; j++)
				{
					double phi = (j + 0.5) * dphi;
					out.x = (float) (sint * Math.cos (phi));
					out.y = (float) (sint * Math.sin (phi));
					s.computeBSDF (env, in, spec, out, false, res);
					sum += res.x * sint;
				}
			}
			sum *= dphi * dtheta;
			System.out.println (angle + "\t" + sum);
		}
	}

}
