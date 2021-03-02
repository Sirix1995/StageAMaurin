
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

import de.grogra.util.*;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.graph.*;

public final class BSpline
{
	public static final EnumerationType SPLINE_PLANE_TYPE = new EnumerationType
		("splinePlane", new String[] {"XY", "XZ", "YZ"});


	private BSpline ()
	{
	}


	public static boolean isValid (BSplineCurve curve, GraphState gs)
	{
		int d;
		return (curve != null)
			&& ((d = curve.getDegree (gs)) > 0) && (curve.getSize (gs) > d);
	}


	public static boolean isValid (BSplineSurface surface, GraphState gs)
	{
		int d;
		return (surface != null)
			&& ((d = surface.getUDegree (gs)) > 0) && (surface.getUSize (gs) > d)
			&& ((d = surface.getVDegree (gs)) > 0) && (surface.getVSize (gs) > d);
	}


	public static float getDefaultKnot
		(int size, int degree, boolean periodic, boolean bezier, int index)
	{
		if (!periodic && (index <= degree))
		{
			return 0;
		}
		if (!periodic && (index >= size))
		{
			return 1;
		}
		if (!bezier)
		{ 
			return (float) (index - degree) / (size - degree);
		}
		return (index > 0) ? (float) (((index - 1) / degree) * degree) / (size - 1)
			: (float) (((1 - index) / degree) * degree) / (1 - size);
	}


	public static void makeDefaultKnotVector
		(float[] knots, int offset, int size, int degree,
		 boolean periodic)
	{
		for (int i = size + degree; i >= 0; i--)
		{
			knots[offset + i] = getDefaultKnot (size, degree, periodic, false, i); 
		}
	}


	/**
	 * Determines the knot span index i for B-Spline basis functions.
	 * It is determined such that
	 * t<sub>i</sub> &lt;= u &lt; t<sub>i+1</sub>, where t is the knot
	 * vector. 
	 * 
	 * @param n the number of control points minus one
	 * @param p the degree of the basis functions.
	 * @param u the parameter
	 * @param knots the knot vector
	 * @param kd the <code>dimension</code>-argument for the knot vector
	 * @param gs the <code>GraphState</code> context
	 * @return the span index i`
	 */
	public static int findSpan
		(int n, int p, float u, KnotVector knots, int kd, GraphState gs)
	{
		if (u <= knots.getKnot (kd, p, gs))
		{
			return p;
		}
		else if (u >= knots.getKnot (kd, n + 1, gs))
		{
			return n;
		}
		else
		{
			int low = p, high = n + 1;
			while (true)
			{
				int i = (low + high) >> 1;
				if (u < knots.getKnot (kd, i, gs))
				{
					high = i;
				}
				else if (u >= knots.getKnot (kd, i + 1, gs))
				{
					low = i;
				}
				else
				{
					return i;
				}
			}
		}
	}


	public static void calculateBasisFunctions
		(float[] out, int p, KnotVector knots, int kd, int span, float u,
		 GraphState gs, float[] left, float[] right)
	{
		//(left, right).length > p 
		out[0] = 1;
		for (int j = 1; j <= p; j++)
		{
			left[j] = u - knots.getKnot (kd, span + 1 - j, gs);
			right[j - 1] = knots.getKnot (kd, span + j, gs) - u;
			float s = 0;
			for (int r = 0; r < j; r++)
			{
				float t = out[r] / (right[r] + left[j - r]);
				out[r] = s + right[r] * t;
				s = left[j - r] * t; 
			}
			out[j] = s;
		}
	}


	/**
	 * Computes B-Spline basis functions and their derivatives
	 * at a given parameter value. The computed values are written
	 * to <code>out</code>: The
	 * k-th derivative of the r-th basis function is written to the
	 * component with index <code>k * (p + 1) + (r + p - span)</code>.
	 * Note that only the basis functions with
	 * <code>span - p &lt;= r &lt;= span</code> are computed (because the
	 * others are zero).
	 * 
	 * @param out the computed basis functions and derivates are written
	 * to this array
	 * @param p the degree of the basis functions
	 * @param knots the knot vector
	 * @param kd the <code>dimension</code>-argument for the knot vector
	 * @param span the span index of the knot vector
	 * @param u the parameter
	 * @param n compute up to the <code>n</code>-th derivative
	 * @param gs the <code>GraphState</code> context
	 * @param left a temporary array, minimal size is <code>p</code> + 1
	 * @param right a temporary array, minimal size is <code>p</code> + 1
	 * @param ndu a temporary array, minimal size is
	 * (<code>p</code> + 1)<sup>2</sup>
	 */
	public static void calculateDerivatives
		(float[] out, int p, KnotVector knots, int kd, int span,
		 float u, int n, GraphState gs, float[] left, float[] right,
		 float[] ndu)
	{
		ndu[0] = 1;
		int p1 = p + 1;
		for (int j = 1; j <= p; j++)
		{
			left[j] = u - knots.getKnot (kd, span + 1 - j, gs);
			right[j - 1] = knots.getKnot (kd, span + j, gs) - u;
			float s = 0;
			for (int r = 0; r < j; r++)
			{
				float t = ndu[r*p1 + j - 1]
					/ (ndu[j*p1 + r] = right[r] + left[j - r]);
				ndu[r*p1 + j] = s + right[r] * t;
				s = left[j - r] * t; 
			}
			ndu[j * (p1 + 1)] = s;
		}
		for (int j = p; j >= 0; j--)
		{
			out[j] = ndu[j*p1 + p];
		}
		for (int r = 0; r <= p; r++)
		{
			left[0] = 1;
			for (int k = 1; k <= n; k++)
			{
				float d = 0;
				int rk = r - k, pk = p - k;
				if (r >= k)
				{
					d = (right[0] = left[0] / ndu[(pk+1)*p1 + rk])
						* ndu[rk*p1 + pk];
				}
				span = (r - 1 <= pk) ? k - 1 : p - r;
				for (int j = (rk >= -1) ? 1 : -rk; j <= span; j++)
				{
					d += (right[j] = (left[j] - left[j - 1])
						  / ndu[(pk+1)*p1 + rk + j]) * ndu[(rk+j)*p1 + pk];
				}
				if (r <= pk)
				{
					d += (right[k] = -left[k - 1] / ndu[(pk+1)*p1 + r])
						* ndu[r*p1 + pk];
				}
				out[k*p1 + r] = d;
				float[] t = left; left = right; right = t;
			}
		}
		span = p;
		for (int k = 1; k <= n; k++)
		{
			for (int j = p; j >= 0; j--)
			{
				out[k*p1 + j] *= span;
			}
			span *= (p - k);
		}
	}


	public static void evaluate
		(float[] out, BSplineCurve curve, float u, GraphState gs)
	{
		int p = curve.getDegree (gs);
		int n = Math.min (out.length, curve.getDimension (gs));
		for (int k = n - 1; k >= 0; k--)
		{
			out[k] = 0;
		}
		int span = findSpan (curve.getSize (gs) - 1, p, u, curve, 0, gs);
		Pool pool = Pool.get (gs);
		float[] bf = pool.getFloatArray (0, p + 1),
			left = pool.getFloatArray (1, Math.max (p + 1, n)),
			right = pool.getFloatArray (2, p + 1);
		calculateBasisFunctions (bf, p, curve, 0, span, u, gs, left, right);
		span -= p;
		while (p >= 0)
		{
			u = bf[p];
			curve.getVertex (left, span + p, gs);
			for (int k = n - 1; k >= 0; k--)
			{
				out[k] += u * left[k];
			}
			--p;
		}
	}


	public static void evaluate
		(float[] out, BSplineSurface surface, float u, float v, GraphState gs)
	{
		int n = surface.getUSize (gs) - 1, p = surface.getUDegree (gs);
		int m = surface.getVSize (gs) - 1, q = surface.getVDegree (gs);
		int uspan = findSpan (n, p, u, surface, 0, gs);
		int vspan = findSpan (m, q, v, surface, 1, gs);
		Pool pool = Pool.get (gs);
		float[] bfu = pool.getFloatArray (0, p + 1),
			bfv = pool.getFloatArray (1, q + 1),
			left = pool.getFloatArray (2, Math.max (out.length, Math.max (p, q)) + 1),
			right = pool.getFloatArray (3, Math.max (p, q) + 1);
		calculateBasisFunctions (bfu, p, surface, 0, uspan, u, gs, left, right);
		calculateBasisFunctions (bfv, q, surface, 1, vspan, v, gs, left, right);
		n = Math.min (out.length, surface.getDimension (gs));
		for (int k = n - 1; k >= 0; k--)
		{
			out[k] = 0;
		}
		uspan -= p;
		vspan -= q;
		while (q >= 0)
		{
			v = bfv[q];
			for (m = p; m >= 0; m--)
			{
				u = bfu[m] * v;
				surface.getVertex
					(left, surface.getVertexIndex (uspan + m, vspan + q, gs),
					 gs);
				for (int i = n - 1; i >= 0; i--)
				{
					out[i] += u * left[i];
				}
			}
			--q;
		}
	}


	public static void calculateKnotsAndParameters
		(float[] points, int n, int d, int p, boolean centripetal,
		 KnotVectorImpl knots, float[] params)
	{
		int m = n + p + 1;
		float sum = 0;
		for (int k = n; k > 0; k--)
		{
			float dist = 0;
			for (int i = 0; i < d; i++)
			{
				float t;
				dist += (t = points[k * d + i] - points[(k - 1) * d + i]) * t;
			}
			sum += (params[k] = centripetal
					? (float) Math.sqrt (Math.sqrt (dist))
					: (float) Math.sqrt (dist));
		}
		for (int k = p; k >= 0; k--)
		{
			knots.data[k] = 0;
			knots.data[m - k] = 1;
		}
		params[0] = 0;
		params[n] = 1;
		sum = 1 / sum;
		for (int k = 1; k < n; k++)
		{
			params[k] = params[k - 1] + sum * params[k];
		}
		sum = 0;
		for (int k = 1; k < p; k++)
		{
			sum += params[k];
		}
		float t = 1f / p;
		for (int k = 0; k < n - p; k++)
		{
			sum += (params[k + p] - params[k]);
			knots.data[p + k + 1] = t * sum;
		}
	}


	public static void interpolate
		(float[] points, int n, int d, int p,
		 KnotVectorImpl knots, float[] params,
		 float[] tmp, float[] left, float[] right)
	{
		//tmp.length > n; (left, right).length > p
		GMatrix a = new GMatrix (n + 1, n + 1);
		for (int k = 0; k <= n; k++)
		{
			int span = findSpan (n, p, params[k], knots, 0, null);
			calculateBasisFunctions (tmp, p, knots, 0, span, params[k], null, left, right);
			for (int i = 0; i <= p; i++)
			{
				a.setElement (k, span - p + i, tmp[i]);
			}
		}
		GMatrix b = new GMatrix (n + 1, n + 1);
		GVector perm = new GVector (n + 1);
		a.LUD (b, perm);
		GVector rhs = new GVector (n + 1), sol = new GVector (n + 1);
		for (int i = 0; i < d; i++)
		{
			for (int j = 0; j <= n; j++)
			{
				rhs.setElement (j, points[j * d + i]);
			}
			sol.LUDBackSolve (b, rhs, perm);
			for (int j = 0; j <= n; j++)
			{
				points[j * d + i] = (float) sol.getElement (j);
			}
		}
	}


	public static void refineKnotVector
		(BSplineCurve curve, float[] newKnots, float[] knotsOut, int ki,
		 float[] verticesOut, int vi, GraphState gs, float[] tmp)
	{
		int n = curve.getSize (gs) - 1;
		int p = curve.getDegree (gs);
		int m = n + p + 1;
		int r = newKnots.length - 1;
		int a = findSpan (n, p, newKnots[0], curve, 0, gs);
		int b = findSpan (n, p, newKnots[r], curve, 0, gs) + 1;
		int d = curve.getDimension (gs);
		int j;
		for (j = a - p; j >= 0; j--)
		{
			curve.getVertex (tmp, j, gs);
			FloatList.arraycopy (tmp, 0, verticesOut, vi + j * d, d);
		}
		for (j = b - 1; j <= n; j++)
		{
			curve.getVertex (tmp, j, gs);
			FloatList.arraycopy (tmp, 0, verticesOut, vi + (j + r + 1) * d, d);
		}
		for (j = a; j >= 0; j--)
		{
			knotsOut[ki + j] = curve.getKnot (0, j, gs);
		}
		for (j = b + p; j <= m; j++)
		{
			knotsOut[ki + j + r + 1] = curve.getKnot (0, j, gs);
		}
		int i = b + p - 1, k = b + p + r;
		for (j = r; j >= 0; j--)
		{
			float t;
			while ((i > a) && (newKnots[j] <= (t = curve.getKnot (0, i, gs))))
			{
				knotsOut[ki + k] = t;
				curve.getVertex (tmp, --i - p, gs);
				FloatList.arraycopy (tmp, 0, verticesOut, vi + (--k - p) * d, d);
			}
			int l = vi + (k - p) * d;
			FloatList.arraycopy (verticesOut, l, verticesOut, l - d, d);
			for (l = 1; l <= p; l++)
			{
				int ind = vi + (k - p + l) * d;
				t = knotsOut[ki + k + l] - newKnots[j];
				if (t == 0)
				{
					FloatList.arraycopy (verticesOut, ind, verticesOut, ind - d, d);
				}
				else
				{
					t /= knotsOut[ki + k + l] - curve.getKnot (0, i - p + l, gs);
					for (int c = d - 1; c >= 0; c--)
					{
						verticesOut[ind - d + c] = t * verticesOut[ind - d + c]
							+ (1 - t) * verticesOut[ind + c];
					}
				}
			}
			knotsOut[ki + k--] = newKnots[j];
		}
	}


	public static void raiseDegreeInsertKnots
		(BSplineCurve curve, boolean leftclamped, int[] multiplicity,
		 int resDegree, float[] resKnots, int[] resMultiplicity,
		 float[] verticesOut, GraphState gs, float[] tmp)
	{
		//tmp.length >= d * (2 + k + (k - 1) + curve.getSize)
		int r = 0, s;
		int i = curve.getSize (gs), k = curve.getDegree (gs) + 1;
		int d = curve.getDimension (gs);
		int oi = tmp.length;
		for (int p = multiplicity.length - 2; p >= 0; p--)
		{
			if (multiplicity[p] > 0)
			{
				while (r < k)
				{
					curve.getVertex (tmp, --i, gs);
					FloatList.arraycopy (tmp, 2 * d, tmp, d, d);
					FloatList.arraycopy (tmp, 0, tmp, 2 * d, d);
					for (s = 1; s <= r; s++)
					{
						float f = (k - s) / (curve.getKnot (0, i + k, gs)
											 - curve.getKnot (0, i + s, gs));
						for (int j = d - 1; j >= 0; j--)
						{
							float t;
							tmp[j] = t = f * (tmp[d + j] - tmp[j]);
							tmp[d + j] = tmp[(2 + s) * d + j];
							tmp[(2 + s) * d + j] = t;
						}
					}
					if (!leftclamped && ((s = k - 2 - i) >= 0))
					{
						int u =  (2 + k + s) * d;
						FloatList.arraycopy (tmp, (2 + s) * d, tmp, u, d);
						while (--s >= 0)
						{
							float f = (curve.getKnot (0, k - 1, gs)
									   - curve.getKnot (0, i + k, gs))
								/ (k - s - 1);
							for (int j = d - 1; j >= 0; j--)
							{
								tmp[--u] += f * tmp[u + d];
							}
						}
					}
					r++;
				}
				s = multiplicity[p];
				r -= multiplicity[p];
				FloatList.arraycopy (tmp, (r + 2) * d, tmp, oi -= d * s, d * s);
			}
		}
		if (!leftclamped)
		{
			FloatList.arraycopy (tmp, (2 + k) * d, tmp, oi, (k - 2) * d);
		}
		int copy = 0;
		for (int p = 0; p < multiplicity.length - 1; p++)
		{
			copy = multiplicity[p];
			for (int q = resDegree; q > resDegree - resMultiplicity[p + 1]; q--)
			{
				for (s = 1; s <= r; s++)
				{
					float f = (resKnots[i + resDegree] - resKnots[i - 1 + s])
						/ (resDegree + 1 - s);
					for (int j = d - 1; j >= 0; j--)
					{
						tmp[(1 + s) * d + j] += f * tmp[(2 + s) * d + j];
					}
				}
				if (copy > 0)
				{
					FloatList.arraycopy (tmp, oi, tmp, (k - copy + 2) * d, d * copy);
					oi += d * copy;
					copy = 0;
				} 
				r = (q < k) ? q : k - 1;
				FloatList.arraycopy (tmp, 2 * d, verticesOut, d * i++, d);
			}
		}
	}


	public static int[] makeCompatible
		(FloatList out, BSplineCurveList curves, float knotEps, int dimension,
		 boolean makeRational, GraphState gs)
	{
		int c = curves.getSize (gs);
		int p, n;
		int maxdim = 0, sumdim = 0;
		int valid = 0;
		for (int ci = 0; ci < c; ci++)
		{
			p = curves.getDegree (ci, gs);
			if ((p > 0) && (curves.getSize (ci, gs) > p))
			{
				valid++;
				int d = curves.getDimension (ci, gs);
				sumdim += d;
				if (d > maxdim)
				{
					maxdim = d;
				}
			}
		}
		if (valid == 0)
		{
			return null;
		}
		int[] ret;
		if (curves.areCurvesCompatible (gs))
		{
			p = curves.getDegree (0, gs);
			n = curves.getSize (0, gs);
			ret = new int[3];
			float[] tmp = new float[maxdim];
			for (int ci = 0; ci < c; ci++)
			{
				int dm1 = ((dimension > 0) ? dimension
						   : curves.getDimension (ci, gs)) - 1;
				boolean r = curves.isRational (ci, gs);
				for (int i = 0; i < n; i++)
				{
					int s = curves.getVertex (tmp, ci, i, gs);
					if (r && makeRational)
					{
						s--;
					}
					for (int j = 0; j <= dm1; j++)
					{
						if (makeRational)
						{
							out.push ((j < s) ? tmp[j]
									  : (j < dm1) ? 0
									  : r ? tmp[s] : 1);
						}
						else
						{
							out.push ((j < s) ? tmp[j] : 0);
						}
					}
				}
			}
			for (int i = 0; i <= n + p; i++)
			{
				out.push (curves.getKnot (0, i, gs));
			}
		}
		else
		{
			boolean[] lc = new boolean[c];
			float[] knotA = new float[c], knotB = new float[c];
			int[] size = new int[Math.max (3, c)];
			ret = size;
			int[] deg = new int[c];
			int[] knotIndex = new int[c];
			IntList mult = new IntList ();
			IntList resMult = new IntList ();
			FloatList resKnots = new FloatList ();
			FloatList minKnots = new FloatList ();
			IntList minKnotsCurve = new IntList ();
			p = 0;
			for (int i = 0; i < c; i++)
			{
				size[i] = curves.getSize (i, gs);
				int t = deg[i] = curves.getDegree (i, gs);
				if ((t <= 0) || (size[i] <= t))
				{
					continue;
				}
				if (t > p)
				{
					p = t;
				}
				float v = curves.getKnot (i, t, gs);
				float f = 1 / (curves.getKnot (i, size[i], gs) - v);
				knotA[i] = f;
				knotB[i] = -f * v;
				lc[i] = (v - curves.getKnot (i, 0, gs)) * f < knotEps;

				if (t + 1 < size[i])
				{
					v = (curves.getKnot (i, t + 1, gs) - v) * f;
					knotIndex[i] = t + 1;
					t = minKnots.binarySearch (v);
					if (t < 0)
					{
						t = ~t;
					}
					minKnots.add (t, v);
					minKnotsCurve.add (t, i);
				}
			}
			if (p == 0)
			{
				ret = null;
				n = 0;
			}
			else
			{
				writeKnot (0, p + 1, resMult, resKnots);
				float lastKnot = -1, nextKnot = -1;
				while (true)
				{
					boolean empty = minKnots.isEmpty ();
					float v = empty ? 0 : minKnots.removeAt (0);
					if (empty || (v >= nextKnot))
					{
						if (lastKnot >= 0)
						{
							int maxMult = 0, h = mult.size - c;
							for (int i = 0; i < c; i++)
							{
								int t = mult.elements[h + i] + p - deg[i];
								if (t > maxMult)
								{
									maxMult = t;
								}
							}
							writeKnot (lastKnot, maxMult, resMult, resKnots);
						}
						if (empty)
						{
							break;
						}
						lastKnot = v;
						nextKnot = v + knotEps;
						for (int i = 0; i < c; i++)
						{
							mult.add (0);
						}
					}
					int ci = minKnotsCurve.removeAt (0);
					int e = knotIndex[ci];
					while ((++e < size[ci])
						   && ((v = knotA[ci] * curves.getKnot (ci, e, gs)
								+ knotB[ci]) < nextKnot)) 
					{
					}
					mult.elements[mult.size - c + ci] += (e - knotIndex[ci]);
					knotIndex[ci] = e;
					if (e < size[ci])
					{
						e = minKnots.binarySearch (v);
						if (e < 0)
						{
							e = ~e;
						}
						minKnots.add (e, v);
						minKnotsCurve.add (e, ci);
					}
				}
				writeKnot (1, p + 1, resMult, resKnots);
				int[] resMultArray = resMult.toArray ();
				float[] resKnotsArray = resKnots.toArray ();
				int[] multArray = new int[resMultArray.length];

				n = resKnotsArray.length - p - 1;
				float[] tmp = new float[sumdim * (2 + p + resKnotsArray.length)];

				VertexListImpl vertices
					= new VertexListImpl (new float[maxdim * n], 0);
				float[] v = vertices.data;
				BSplineOfVertices curve = new BSplineOfVertices (vertices, 0, false, false);
				curve.knots = new float[resKnotsArray.length];

				for (int ci = 0; ci < c; ci++)
				{
					if ((deg[ci] <= 0) || (size[ci] <= deg[ci]))
					{
						continue;
					}
					int d = vertices.dimension = curves.getDimension (ci, gs);
					for (int k = size[ci] - 1; k >= 0; k--)
					{
						curves.getVertex (tmp, ci, k, gs);
						for (int i = d - 1; i >= 0; i--)
						{
							v[k * d + i] = tmp[i];
						}
					}
					boolean knotsDiffer = false;
					for (int k = multArray.length - 2; k > 0; k--)
					{
						multArray[k] = mult.elements[ci + (k - 1) * c];
						knotsDiffer |= multArray[k] != resMultArray[k];
					}
					if (knotsDiffer || !lc[ci] || (deg[ci] != p))
					{
						curve.degree = deg[ci];
						curve.size = size[ci];
						for (int k = size[ci] + deg[ci]; k >= 0; k--)
						{
							curve.knots[k] = knotA[ci] * curves.getKnot (ci, k, gs)
								+ knotB[ci];
						}
						multArray[0] = multArray[multArray.length - 1] = deg[ci] + 1;
						BSpline.raiseDegreeInsertKnots
							(curve, lc[ci], multArray, p, resKnotsArray, resMultArray,
							 v, gs, tmp);
					}

					int dm1 = ((dimension > 0) ? dimension : d) - 1;
					boolean r = curves.isRational (ci, gs);
					int s = (r && makeRational) ? d - 1 : d;
					for (int i = 0; i < n; i++)
					{
						int di = i * d;
						for (int j = 0; j <= dm1; j++)
						{
							if (makeRational)
							{
								out.push ((j < s) ? v[di + j]
										  : (j < dm1) ? 0
										  : r ? v[di + s] : 1);
							}
							else
							{
								out.push ((j < s) ? v[di + j] : 0);
							}
						}
					}
				}
				for (int i = 0; i <= n + p; i++)
				{
					out.push (resKnotsArray[i]);
				}
			}
		}
		if (ret != null)
		{
			ret[0] = n;
			ret[1] = p;
			ret[2] = valid;
		}
		return ret;
	}


	private static void writeKnot
		(float u, int m, IntList mult, FloatList resKnots)
	{
		mult.add (m);
		while (--m >= 0)
		{
			resKnots.add (u);
		}
	}


	public interface BezierSegmentVisitor
	{
		void visit (int index, float[] data, int dimension, int degree,
					float uLeft, float uRight);
	}
	
	
	public static void decomposeSplineConnection 
		(BezierSegmentVisitor v, BSplineCurve curve, final Pool pool2, GraphState gs)
	{
		
		//int n = curve.getSize (gs) - 1;
		int p = curve.getDegree (gs);
		int d = curve.getDimension (gs);
		float k;
		Pool pool = Pool.push (gs);
		float[] q = pool.getFloatArray (0, (p+1) * d);
		float[] a = pool.getFloatArray (1, d);
		float[] b = pool.getFloatArray (2, d);
		float[] c = pool.getFloatArray (3, d);
		float[] m = pool.getFloatArray (4, d);//For 2D calculation
		
		float[] params = pool2.getFloatArray (0, 3);
		
		float weight;
		double distance;
		curve.getVertex (a, 0, gs);
		curve.getVertex (c, 1, gs);	
		distance = Math.sqrt((a[0] - c[0]) * (a[0] - c[0]) + (a[1] - c[1]) * (a[1] - c[1]));
		weight = (float) (params[0] * Math.sqrt(distance) / 6);
		m[0] = (a[0]+c[0])/2;
		m[1] = (a[1]+c[1])/2;
		if (a[0] != c[0])
		{
			k = (c[1] - a[1]) / (c[0] - a[0]);
			if (a[0]<c[0])
			{
				// //b[1]should be larger
				b[1] =  m[1] + (float) (weight / Math.sqrt(1 + k * k));
				b[0] = m[0] - (b[1] - m[1]) * k;
			}
			else
			{
				// //b[1]should be smaller
				b[1] =  m[1] - (float) (weight / Math.sqrt(1 + k * k));
				b[0] = m[0] - (b[1] - m[1]) * k;
			}
		}
		else
		{
			k = (c[0] - a[0]) / (c[1] - a[1]);
			if (a[1]<c[1])
			{
				//b[0]should be smaller
				b[0] =  m[0] - (float) (weight / Math.sqrt(1 + k * k));
				b[1] = m[1] - (b[0] - m[0]) * k;
			}
			else
			{
				//b[0]should be larger
				b[0] =  m[0] + (float) (weight / Math.sqrt(1 + k * k));
				b[1] = m[1] - (b[0] - m[0]) * k;
			}		
		}
		params[1] = b[0]-m[0];
		params[2] = b[1]-m[1];
/*		for (int i = 1; i >= 0; i--)
		{
			curve.getVertex (q, i, gs);
			if (i == 1)
			{
				FloatList.arraycopy (q, 0, q, i * d, d);
			}
		}
*/
		
		
		FloatList.arraycopy (a, 0, q, 0, d);
		FloatList.arraycopy (b, 0, q, 1 * d, d);
		FloatList.arraycopy (c, 0, q, 2 * d, d);
		v.visit (100, q, d, p, 100, 100);//100 is dummy parameter
		pool.pop (gs);
	}

	public static void decompose
		(BezierSegmentVisitor v, BSplineCurve curve, boolean normalizeU,
		 GraphState gs)
	{
		int n = curve.getSize (gs) - 1, p = curve.getDegree (gs);
		int d = curve.getDimension (gs);
		int m = n + p + 1;
		int a = p;
		int b = p + 1;
		Pool pool = Pool.push (gs);
		float[] q = pool.getFloatArray (0, b * d);
		float[] nextQ = pool.getFloatArray (1, p * d);
		float[] alpha = pool.getFloatArray (2, b);
		for (int i = p; i >= 0; i--)
		{
			curve.getVertex (q, i, gs);
			if (i > 0)
			{
				FloatList.arraycopy (q, 0, q, i * d, d);
			}
		}
		if (curve.getKnot(0, a, gs)==10000)
		//if (curve instanceof SplineConnection)
		{
			v.visit (100, q, d, p,
					 100,
					 100);
		}
		else
		{
			float knotA = curve.getKnot (0, a, gs);
			float minKnot = knotA,
				knotScale = 1 / (curve.getKnot (0, n + 1, gs) - knotA); //
			if ((a > 1) && (curve.getKnot (0, 1, gs) < knotA)) ///
			{
				int r = p;
				while (curve.getKnot (0, --r, gs) >= knotA)
				{
				}
				FloatList.arraycopy (q, 0, nextQ, 0, d * (r + 1));
				for (int j = 1; j <= r; j++)
				{
					for (int i = 0; i <= r - j; i++)
					{
						float al = curve.getKnot (0, i + j, gs);
						al = (knotA - al)
							/ (curve.getKnot (0, i + p + 1, gs) - al);
						for (int k = d - 1; k >= 0; k--)
						{
							nextQ[i * d + k] = al * nextQ[(i + 1) * d + k]
								+ (1 - al) * nextQ[i * d + k];
						}
					}
				}
				FloatList.arraycopy (nextQ, 0, q, 0, d * r);
			}
			int cindex = -1; //
			while (b <= n + 1)// 1 time
			{
				int i = b;
				float knotB = curve.getKnot (0, b, gs);
				while ((b < m) && (curve.getKnot (0, b + 1, gs) <= knotB))
				{
					b++;
				}
				int mult = b - i + 1;//
				if (mult < p)///
				{
					float numer = knotB - knotA;
					for (i = p; i > mult; i--)
					{
						alpha[i - mult - 1]
							= numer / (curve.getKnot (0, a + i, gs) - knotA);  //linear transform
					}
					int r = p - mult;
					for (int j = 1; j <= r; j++)
					{
						int s = mult + j;
						int index = (p + 1) * d;
						for (int k = p; k >= s; k--)
						{
							float al = alpha[k - s];
							for (i = d; i > 0; i--)
							{
								q[--index]
									= al * q[index] + (1 - al) * q[index - d]; 
							}
						}
						if (b < m)
						{
							FloatList.arraycopy (q, p * d, nextQ, (r - j) * d, d);
						}
					}
				}
				v.visit (++cindex, q, d, p,
						 normalizeU ? (knotA - minKnot) * knotScale : knotA,
						 normalizeU ? (knotB - minKnot) * knotScale : knotB);
				if (b > n)
				{
					break;
				}
				a = p - mult;
				for (i = p; i >= a; i--)
				{
					curve.getVertex (q, b - p + i, gs);
					if (i > 0)
					{
						FloatList.arraycopy (q, 0, q, i * d, d);
					}
				}
				FloatList.arraycopy (nextQ, 0, q, 0, a * d);
				a = b;
				knotA = knotB;
				b++;
			}
		}
		pool.pop (gs);
	}


	public interface BezierPatchVisitor
	{
		void visit (int uIndex, int vIndex, float[] data, int dimension,
					int uDegree, int vDegree, float uLeft, float uRight,
					float vLeft, float vRight, boolean uvPermuted);
	}


	public static void decompose
		(final BezierPatchVisitor v, final BSplineSurface surface,
		 final boolean normalizeUV, final GraphState state,
		 final VertexGridImpl controlPointsOut)
	{
		class Helper implements BSplineCurve, BezierSegmentVisitor
		{
			private final boolean uvPermuted;
			private final int firstDir, d;
			private final boolean rational;
			private int dir;
			private float[] data;
			private final int[] deg, size;
			private int findex;
			private float fLeft, fRight;
			private final float[] cpoData;
			
			Helper ()
			{
				deg = new int[] {surface.getUDegree (state), surface.getVDegree (state)};
				size = new int[] {surface.getUSize (state), surface.getVSize (state)};
				uvPermuted = size[1] > size[0];
				dir = firstDir = uvPermuted ? 1 : 0;
				d = surface.getDimension (state);
				rational = surface.isRational (state);
				if (controlPointsOut != null)
				{
					int cd = d - (rational ? 1 : 0);
					controlPointsOut.setDimension (cd);
					cpoData = new float[cd * size[0] * size[1]];
					controlPointsOut.setData (cpoData);
					controlPointsOut.setUCount (size[0]);
				}
				else
				{
					cpoData = null;
				}
			}

			public boolean dependsOnContext ()
			{
				return true;
			}

			public void writeStamp (Cache.Entry cache, GraphState gs)
			{
			}

			public int getDegree (GraphState gs)
			{
				return deg[dir];
			}

			public boolean isRational (GraphState gs)
			{
				return rational;
			}

			public int getSize (GraphState gs)
			{
				return size[dir];
			}

			public int getDimension (GraphState gs)
			{
				return d
					* ((dir == firstDir) ? size[dir ^ 1] : deg[firstDir] + 1);
			}

			public int getVertex (float[] out, int index, GraphState gs)
			{
				if (dir == firstDir)
				{
					if (dir == 0)
					{
						for (int i = size[1] - 1; i >= 0; i--)
						{
							surface.getVertex
								(out, surface.getVertexIndex (index, i, gs), gs);
							if (cpoData != null)
							{
								if (rational)
								{
									int b = (index + i * size[0]) * (d - 1);
									float w = 1 / out[d - 1];
									for (int j = d - 2; j >= 0; j--)
									{
										cpoData[b + j] = out[j] * w;
									}									
								}
								else
								{
									FloatList.arraycopy (out, 0, cpoData, (index + i * size[0]) * d, d);
								}
							}
							if (i > 0)
							{
								FloatList.arraycopy (out, 0, out, i * d, d);
							}
						}
					}
					else
					{
						for (int i = size[0] - 1; i >= 0; i--)
						{
							surface.getVertex
								(out, surface.getVertexIndex (i, index, gs), gs);
							if (cpoData != null)
							{
								if (rational)
								{
									int b = (i + index * size[0]) * (d - 1);
									float w = 1 / out[d - 1];
									for (int j = d - 2; j >= 0; j--)
									{
										cpoData[b + j] = out[j] * w;
									}									
								}
								else
								{
									FloatList.arraycopy (out, 0, cpoData, (i + index * size[0]) * d, d);
								}
							}
							if (i > 0)
							{
								FloatList.arraycopy (out, 0, out, i * d, d);
							}
						}
					}
					return size[dir ^ 1] * d;
				}
				else
				{
					index *= d;
					int k = deg[firstDir] + 1;
					int sd = size[dir] * d;
					for (int i = 0; i < k; i++)
					{ 
						FloatList.arraycopy (data, index + sd * i, out, d * i, d);
					}
					return d * k;
				}
			}

			public float getKnot (int dim, int index, GraphState gs)
			{
				return surface.getKnot (dir, index, gs);
			}

			public void visit (int index, float[] data, int dimension, int degree,
							   float uLeft, float uRight)
			{
				if (dir == firstDir)
				{
					this.data = data;
					dir = firstDir ^ 1;
					findex = index;
					fLeft = uLeft;
					fRight = uRight;
					decompose (this, this, normalizeUV, state);
					dir = firstDir;
				}
				else
				{
					v.visit (findex, index, data, d, deg[firstDir], degree,
							 fLeft, fRight, uLeft, uRight, uvPermuted);
				}
			}
		}

		Helper h = new Helper ();
		decompose (h, h, normalizeUV, state);
	}


	public static void set (Tuple4f out, float[] in, int n, boolean w)
	{
		out.w = w ? in[--n] : 1;
		out.x = in[0];
		out.y = (n > 1) ? in[1] : 0;
		out.z = (n > 2) ? in[2] : 0;
	}


	public static void set (Tuple4f out, float[] in, int offset, int n, boolean w)
	{
		out.w = w ? in[--n + offset] : 1;
		out.x = in[offset];
		out.y = (n > 1) ? in[offset + 1] : 0;
		out.z = (n > 2) ? in[offset + 2] : 0;
	}


	public static int set (float[] out, float c0, float c1)
	{
		switch (out.length)
		{
			case 0:
				return 0;
			case 1:
				out[0] = c0;
				return 1;
			default:
				out[0] = c0;
				out[1] = c1;
				return 2;
		}
	}


	public static int set (float[] out, float c0, float c1, float c2)
	{
		switch (out.length)
		{
			case 0:
				return 0;
			case 1:
				out[0] = c0;
				return 1;
			case 2:
				out[0] = c0;
				out[1] = c1;
				return 2;
			default:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				return 3;
		}
	}


	public static int set (float[] out, float c0, float c1, float c2, float c3)
	{
		switch (out.length)
		{
			case 0:
				return 0;
			case 1:
				out[0] = c0;
				return 1;
			case 2:
				out[0] = c0;
				out[1] = c1;
				return 2;
			case 3:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				return 3;
			default:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				return 4;
		}
	}


	public static int set (float[] out, float c0, float c1, float c2, float c3,
						   float c4, float c5)
	{
		switch (out.length)
		{
			case 0:
				return 0;
			case 1:
				out[0] = c0;
				return 1;
			case 2:
				out[0] = c0;
				out[1] = c1;
				return 2;
			case 3:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				return 3;
			case 4:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				return 4;
			case 5:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				out[4] = c4;
				return 5;
			default:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				out[4] = c4;
				out[5] = c5;
				return 6;
		}
	}


	public static int set (float[] out, float c0, float c1, float c2, float c3,
						   float c4, float c5, float c6)
	{
		switch (out.length)
		{
			case 0:
				return 0;
			case 1:
				out[0] = c0;
				return 1;
			case 2:
				out[0] = c0;
				out[1] = c1;
				return 2;
			case 3:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				return 3;
			case 4:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				return 4;
			case 5:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				out[4] = c4;
				return 5;
			case 6:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				out[4] = c4;
				out[5] = c5;
				return 6;
			default:
				out[0] = c0;
				out[1] = c1;
				out[2] = c2;
				out[3] = c3;
				out[4] = c4;
				out[5] = c5;
				out[6] = c6;
				return 7;
		}
	}

}
