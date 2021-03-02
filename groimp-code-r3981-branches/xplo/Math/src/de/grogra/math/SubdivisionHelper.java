
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

import de.grogra.xl.util.FloatList;

public abstract class SubdivisionHelper
{
	protected final boolean rational;

	protected int dimension;
	protected int degree;

	protected float flatness = 0.001f;

	int maxDepth = 8;


	public SubdivisionHelper (boolean rational)
	{
		this.rational = rational;
	}


	protected boolean isFlat (float distSquared, float lengthSquared, int depth)
	{
		if (depth >= maxDepth)
		{
			return true;
		}
		int d = degree + 1;
		depth = 1 << depth;
		if (depth > d)
		{
			return distSquared * d <= flatness * lengthSquared * depth;
		}
		else
		{
			return distSquared <= flatness * lengthSquared;
		}
	}


	protected static void subdivide (FloatList v, int dimension, int degree,
									 int step, int blockCount, int blockStep, 
									 float[] tmp)
	{
		// tmp.length >= dimension * degree * blockCount
		int s = (blockCount - 1) * blockStep + degree * step + dimension;
		int index = v.size - s;
		v.setSize (index + 2 * s);
		float[] a = v.elements;
		for (int b = blockCount - 1; b >= 0; b--)
		{
			int x = b * blockStep;
			FloatList.arraycopy
				(a, index + x, a, index + s + x, dimension);
		}
		for (int i = degree - 1; i >= 0; i--)
		{
			for (int b = blockCount - 1; b >= 0; b--)
			{
				int x = b * blockStep + i * step;
				int tx = (i * blockCount + b) * dimension;
				for (int j = dimension - 1; j >= 0; j--)
				{
					tmp[tx + j] = 0.5f * (a[index + x + j]
										  + a[index + x + step + j]);
				}
			}
		}
		for (int b = blockCount - 1; b >= 0; b--)
		{
			int x = b * blockStep;
			FloatList.arraycopy
				(tmp, b * dimension, a, index + s + x + step, dimension);
			x += (degree - 1) * step;
			FloatList.arraycopy
				(tmp, ((degree - 1) * blockCount + b) * dimension, a, index + x, dimension);
		}
		int bcd = blockCount * dimension;
		for (int k = degree - 2; k >= 0; k--)
		{
			for (int i = 0; i <= k; i++)
			{
				int tx = i * bcd;
				for (int j = bcd - 1; j >= 0; j--)
				{
					tmp[tx + j] = 0.5f * (tmp[tx + j] + tmp[tx + bcd + j]);
				}
			}
			for (int b = blockCount - 1; b >= 0; b--)
			{
				int x = b * blockStep;
				FloatList.arraycopy
					(tmp, b * dimension, a, index + s + x + step * (degree - k), dimension);
				x += k * step;
				FloatList.arraycopy
					(tmp, (k * blockCount + b) * dimension, a, index + x, dimension);
			}
		}
	}


	protected void subdivideCurve (FloatList v, float[] tmp, int part, int depth)
	{
		int dimension = this.dimension;
		int s = dimension * (degree + 1);
		int index = v.size - s;
	checkFlatness:
		{
			if (degree > 1)
			{
				float[] a = v.elements;
				float lengthSquared = 0;
				float f = 0;
				boolean lastPoint = true;
				boolean r = rational;
				float w0 = r ? 1 / a[index + dimension - 1] : 0;
				for (int j = s - dimension; j > 0; j -= dimension)
				{
					float sum = 0;
					float dot = 0;
					float w = r ? 1 / a[index + j + dimension - 1] : 0;
					for (int k = dimension - (rational ? 2 : 1); k >= 0; k--)
					{
						float t;
						sum += rational ? (t = w * a[index + j + k] - w0 * a[index + k]) * t
							: (t = a[index + j + k] - a[index + k]) * t;
						if (lastPoint)
						{
							tmp[k] = t;
						}
						else
						{
							dot += tmp[k] * t;
						}
					}
					if (lastPoint)
					{
						lastPoint = false;
						lengthSquared = sum * 1.1f;
						f = -1 / sum;
					}
					else
					{
						if ((sum > lengthSquared)
							|| ((lengthSquared > 0)
								&& !isFlat (sum + dot * dot * f, lengthSquared, depth)))
						{
							break checkFlatness;
						}
					}
				}
			}
			visitFlat (v.elements, index);
			return;
		}
		subdivide (v, dimension, degree, dimension, 1, 0, tmp);
		subdivideCurve (v, tmp, 1, depth + 1);
		v.setSize (index + s);
		subdivideCurve (v, tmp, -1, depth + 1);
	}


	protected abstract void visitFlat (float[] v, int index);

}
