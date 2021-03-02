
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

import de.grogra.graph.GraphState;
import de.grogra.util.Int2ObjectMap;

public abstract class ExtendedSweep extends Sweep
{

	@Override
	protected BSplineCurve getTrajectory (GraphState gs)
	{
		return (BSplineCurve) ((Object[]) gs.getObjectContext ().getValue (this))[1];
	}


	@Override
	protected Object[] initCache (GraphState gs)
	{
		Object[] c = new Object[3];
		Int2ObjectMap profiles = new Int2ObjectMap ();
		c[2] = profiles;
		c[1] = init (profiles, gs);
		return c;
	}


	protected abstract BSplineCurve init (Int2ObjectMap profilesOut, GraphState gs);


	@Override
	protected int getVertexImpl
		(float[] out, int curve, int index, Object[] cache, GraphState gs)
	{
		Int2ObjectMap profiles = (Int2ObjectMap) cache[2];
		int i = profiles.findIndex (curve);
	interpolate:
		if (i < 0)
		{
			i = ~i;
			if (i == 0)
			{
				break interpolate;
			}
			if (i == profiles.size ())
			{
				--i;
				break interpolate;
			}
			int n = profiles.getKeyAt (i - 1);
			float alpha = (float) (curve - n) / (profiles.getKeyAt (i) - n);
			BSplineCurve c = (BSplineCurve) profiles.getValueAt (i);
			n = c.getVertex (out, index, gs);
			float w = c.isRational (gs) ? alpha * out[--n] : 1;
			float x = alpha * out[0], y = (n > 1) ? alpha * out[1] : 0,
				z = (n > 2) ? alpha * out[2] : 0;
			alpha = 1 - alpha;
			c = (BSplineCurve) profiles.getValueAt (i - 1);
			n = c.getVertex (out, index, gs);
			w += c.isRational (gs) ? alpha * out[--n] : alpha; 
			n = BSpline.set (out, alpha * out[0] + x,
							 (n > 1) ? alpha * out[1] + y : y,
							 (n > 2) ? alpha * out[2] + z : z, w);
			return (n == 4) ? -3 : n;
		}
		BSplineCurve c = (BSplineCurve) profiles.getValueAt (i);
		i = c.getVertex (out, index, gs);
		return c.isRational (gs) ? 1 - i : i;
	}


	public float getKnot (int curve, int index, GraphState gs)
	{
		return ((BSplineCurve) ((Int2ObjectMap) getCache (gs)[2])
				.getValueAt (0)).getKnot (0, index, gs);
	}


	public int getSize (int curve, GraphState gs)
	{
		return ((BSplineCurve) ((Int2ObjectMap) getCache (gs)[2])
				.getValueAt (0)).getSize (gs);
	}


	public int getDegree (int curve, GraphState gs)
	{
		return ((BSplineCurve) ((Int2ObjectMap) getCache (gs)[2])
				.getValueAt (0)).getDegree (gs);
	}

}
