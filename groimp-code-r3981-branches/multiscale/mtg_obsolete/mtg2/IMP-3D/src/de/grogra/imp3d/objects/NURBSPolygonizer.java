
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

import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonization;
import de.grogra.math.BSpline;
import de.grogra.math.Pool;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class NURBSPolygonizer extends NURBSSubdivisionHelper
	implements BSpline.BezierPatchVisitor
{
	int vSize;
	private final IntList patchCornerIndices = new IntList ();
	private final Pool pool;
	private final boolean normals;
	private final boolean uv;
	private boolean uvPermuted;

	private int left, right, dir, uDegree, vDegree, patchSize, uRowSize;
	private boolean rightPart;
	private final PolygonArray out;
	private final IntList vertexIndices;
	private final IntList tmpData = new IntList ();
	
	private float uMin, uMax, vMin, vMax;

	public NURBSPolygonizer (PolygonArray out, boolean rational, Pool pool, int flags)
	{
		super (out.vertices, rational);
		vertexIndices = out.polygons;
		this.out = out;
		uv = (flags & Polygonization.COMPUTE_UV) != 0;
		normals = (flags & Polygonization.COMPUTE_NORMALS) != 0;
		out.edgeCount = 4;
		out.planar = false;
		out.closed = false;
		this.pool = pool;
	}

	private int getIndex (int u, int v)
	{
		return patchCornerIndices.elements
			[(u == 0) ? v << 1 : (u == 1) ? (v << 1) + 1
			 : u * vSize + v];
	}


	private static final int IPV = 6;

	public void visit (int uIndex, int vIndex, float[] data,
					   int dimension, int uDegree, int vDegree,
					   float uLeft, float uRight, float vLeft, float vRight,
					   boolean uvPermuted)
	{
		this.dimension = dimension;
		this.uDegree = uDegree;
		this.vDegree = vDegree;
		this.uvPermuted = uvPermuted;
		int n = uRowSize = dimension * (uDegree + 1);
		patchSize = uRowSize * (vDegree + 1);
		if (uvPermuted)
		{
			uMin = vLeft;
			uMax = vRight;
			vMin = uLeft;
			vMax = uRight;
		}
		else
		{
			uMin = uLeft;
			uMax = uRight;
			vMin = vLeft;
			vMax = vRight;
		}
		final int sw, se, ne, nw;
		if (uIndex == 0)
		{
			vSize = vIndex + 2;
		}
		if ((uIndex == 0) && (vIndex == 0))
		{
			patchCornerIndices.add (sw = addVertex (data, 0));
		}
		else
		{
			sw = getIndex (uIndex, vIndex);
		}
		if (vIndex == 0)
		{
			patchCornerIndices.add (se = addVertex (data, n - dimension));
//				out.intData.set (sw * IPV + 1, se);
		}
		else
		{
			se = getIndex (uIndex + 1, vIndex);
		}
		if (uIndex == 0)
		{
			patchCornerIndices.add (nw = addVertex (data, n * vDegree));
//				out.intData.set (sw * IPV + 3, nw);
		}
		else
		{
			nw = getIndex (uIndex, vIndex + 1);
		}
		patchCornerIndices.add (ne = addVertex (data, patchSize - dimension));
//			out.intData.set (nw * IPV + 1, ne);
//			out.intData.set (se * IPV + 3, ne);

		FloatList v = pool.fv;
		float[] tmp = pool.getFloatArray
			(0, Math.max (16, dimension * (uDegree * vDegree + Math.max (uDegree, vDegree))));
		rightPart = true;

		v.setSize (n);
		dir = 0;
		degree = uDegree;

		if (vIndex == 0)
		{
			left = sw;
			right = se;
			FloatList.arraycopy (data, 0, v.elements, 0, n);
			subdivideCurve (v, tmp, 0, 0);
		}

		left = nw;
		right = ne;
		FloatList.arraycopy (data, n * vDegree, v.elements, 0, n);
		subdivideCurve (v, tmp, 0, 0);

		v.setSize (dimension * (vDegree + 1));
		dir = 1;
		degree = vDegree;

		if (uIndex == 0)
		{
			left = sw;
			right = nw;
			copy (data, 0, n, v.elements, 0, dimension, vDegree + 1);
			subdivideCurve (v, tmp, 0, 0);
		}

		left = se;
		right = ne;
		copy (data, n - dimension, n, v.elements, 0, dimension, vDegree + 1);
		subdivideCurve (v, tmp, 0, 0);

		v.clear ();
		v.addAll (data, 0, patchSize);
		subdividePatch (v, sw, se, nw, ne, 0, true, true, tmp, true, 0, 0,
						false, 1, uLeft, uRight, vLeft, vRight);
	}


	@Override
	protected void subdivideCurve (FloatList v, float[] tmp, int part, int depth)
	{
		int l = left, r = right;
		boolean p = rightPart;
		if (part > 0)
		{
			right = addVertex (v.elements, v.size - dimension);
			tmpData.set ((p ? l : r) * IPV + (dir << 1) + (p ? 1 : 0), right);
			rightPart = false;
		}
		else if (part < 0)
		{
			left = tmpData.get ((p ? l : r) * IPV + (dir << 1) + (p ? 1 : 0));
			rightPart = true;
		}
		super.subdivideCurve (v, tmp, part, depth);
		left = l;
		right = r;
		rightPart = p;
/*
			if (part == 0)
			{
				drawBoundary ();
			}
//*/
	}


	private void copyVertex (float[] src, int si, float[] dest, int di,
							 boolean applyWeights)
	{
		int d = dimension;
		if (rational)
		{
			if (applyWeights)
			{
				float w = 1 / src[si + d - 1];
				dest[di] = src[si] * w;
				dest[di + 1] = (d > 2) ? src[si + 1] * w : 0;
				dest[di + 2] = (d > 3) ? src[si + 2] * w : 0;
			}
			else
			{
				dest[di] = src[si];
				dest[di + 1] = (d > 2) ? src[si + 1] : 0;
				dest[di + 2] = (d > 3) ? src[si + 2] : 0;
				dest[di + 3] = src[si + d - 1]; 
			}
		}
		else
		{
			dest[di] = src[si];
			dest[di + 1] = (d > 1) ? src[si + 1] : 0;
			dest[di + 2] = (d > 2) ? src[si + 2] : 0;
		}
	}


	private boolean isFlat (float[] v, int index, int depth, float[] tmp)
	{
		degree = (uDegree + vDegree) >> 1;
		int d = dimension;
		int du = uDegree * d;
		int nu = du + d;
		int dv = vDegree * nu;
		copyVertex (v, index, tmp, 0, true);
		copyVertex (v, index + du, tmp, 4, true);
		copyVertex (v, index + du + dv, tmp, 8, true);
		copyVertex (v, index + dv, tmp, 12, true);
		float t;
		float s = (t = tmp[0] - tmp[4]) * t
			+ (t = tmp[1] - tmp[5]) * t
			+ (t = tmp[2] - tmp[6]) * t,
			e = (t = tmp[4] - tmp[8]) * t
			+ (t = tmp[5] - tmp[9]) * t
			+ (t = tmp[6] - tmp[10]) * t,
			n = (t = tmp[8] - tmp[12]) * t
			+ (t = tmp[9] - tmp[13]) * t
			+ (t = tmp[10] - tmp[14]) * t,
			w = (t = tmp[12] - tmp[0]) * t
			+ (t = tmp[13] - tmp[1]) * t
			+ (t = tmp[14] - tmp[2]) * t;
		int max = (s > e) ? (s > n) ? (s > w) ? 0 : 12 : (n > w) ? 8 : 12
			: (e > n) ? (e > w) ? 4 : 12 : (n > w) ? 8 : 12;
		w = (max == 0) ? s : (max == 4) ? e : (max == 8) ? n : w;
		s = tmp[max]; e = tmp[max + 1]; n = tmp[max + 2];
		max = (max + 4) & 15;
		float vx = tmp[max] - s, vy = tmp[max + 1] - e, vz = tmp[max + 2] - n;
		max = (max + 4) & 15;
		float ax = tmp[max] - s, ay = tmp[max + 1] - e, az = tmp[max + 2] - n;
		float cx1 = ay * vz - az * vy, cy1 = az * vx - ax * vz, cz1 = ax * vy - ay * vx; 
		max = (max + 4) & 15;
		ax = tmp[max] - s; ay = tmp[max + 1] - e; az = tmp[max + 2] - n;
		float cx2 = ay * vz - az * vy, cy2 = az * vx - ax * vz, cz2 = ax * vy - ay * vx;
		float ls = cx1 * cx1 + cy1 * cy1 + cz1 * cz1;
		t = cx2 * cx2 + cy2 * cy2 + cz2 * cz2;
		if (t > ls)
		{
			ls = 1 / t; cx1 = cx2; cy1 = cy2; cz1 = cz2;
			max = (max - 4) & 15;
			t = (tmp[max] - s) * cx1 + (tmp[max + 1] - e) * cy1
				+ (tmp[max + 2] - n) * cz1;
			if (!isFlat (t * t * ls, w, depth))
			{
				return false;
			}
		}
		else
		{
			ls = 1 / ls;
			t = ax * cx1 + ay * cy1 + az * cz1;
			if (!isFlat (t * t * ls, w, depth))
			{
				return false;
			}
		}
		for (int j = du - d; j > 0; j -= d)
		{
			for (int i = dv - nu + j; i > 0; i -= nu)
			{
				copyVertex (v, index + i, tmp, 0, true);
				t = (tmp[0] - s) * cx1 + (tmp[1] - e) * cy1 + (tmp[2] - n) * cz1;
				if (!isFlat (t * t * ls, w, depth))
				{
					return false;
				}
			}
		}
		return true;
	}


	private void pushVertex (int vertex, float[] a, int p, int d1, int d2,
							 float[] tmp, float u, float v)
	{
		vertexIndices.push (vertex);
		if (uv)
		{
			out.uv.set (2 * vertex, u);
			out.uv.set (2 * vertex + 1, v);
		}
		if (normals)
		{
			int n = tmpData.elements[vertex * IPV + 5]++;
			if ((n == 0) || (u == uMin) || (u == uMax) || (v == vMin) || (v == vMax))
			{
				float vx1, vy1, vz1, vx2, vy2, vz2;
				copyVertex (a, p, tmp, 0, false);
				copyVertex (a, p + d1, tmp, 4, false);
				copyVertex (a, p + d2, tmp, 8, false);
				if (rational)
				{
					u = tmp[3];
					v = -tmp[7];
					vx1 = u * tmp[4] + v * tmp[0];
					vy1 = u * tmp[5] + v * tmp[1];
					vz1 = u * tmp[6] + v * tmp[2];
					v = -tmp[11];
					vx2 = u * tmp[8] + v * tmp[0];
					vy2 = u * tmp[9] + v * tmp[1];
					vz2 = u * tmp[10] + v * tmp[2];
				}
				else
				{
					vx1 = tmp[4] - tmp[0]; vy1 = tmp[5] - tmp[1]; vz1 = tmp[6] - tmp[2];
					vx2 = tmp[8] - tmp[0]; vy2 = tmp[9] - tmp[1]; vz2 = tmp[10] - tmp[2];
				}
				int idx = vertex * 3;
				byte[] b = out.normals.elements;
				byte nx, ny, nz;
				if (n > 0)
				{
					nx = b[idx]; ny = b[idx + 1]; nz = b[idx + 2];
				}
				else
				{
					nx = 0; ny = 0; nz = 0;
				}
				out.setNormal (vertex, vy1 * vz2 - vz1 * vy2,
							   vz1 * vx2 - vx1 * vz2,
							   vx1 * vy2 - vy1 * vx2);
				if ((n > 0)
					&& (Math.abs (b[idx] - nx) + Math.abs (b[idx + 1] - ny)
						+ Math.abs (b[idx + 2] - nz) > 3))
				{
					out.setNormal (vertex,
								   n * nx + b[idx],
								   n * ny + b[idx + 1],
								   n * nz + b[idx + 2]);
				}
			}
		}
	}

	private void subdividePatch (FloatList v, int sw, int se,
								 int nw, int ne, int sdDir,
								 boolean sdRight, boolean otherRight,
								 float[] tmp, boolean subdivided,
								 int depth, int otherDepth,
								 boolean force, int lastSdDir,
								 float sleft, float sright,
								 float oleft, float oright)
	{
		IntList ints = tmpData;
		int sdsi = (sdRight ? sw : se) * IPV + (sdDir << 1) + (sdRight ? 1 : 0);
		int sds = ints.get (sdsi);
		int sdni = (sdRight ? nw : ne) * IPV + (sdDir << 1) + (sdRight ? 1 : 0);
		int sdn = ints.get (sdni);
		int n = v.size;
		if (!force && ((sds == 0) || (ints.get (sds * IPV + 4) != 0))
			&& ((sdn == 0) || (ints.get (sdn * IPV + 4) != 0)))
		{
			if (!subdivided
				&& ((((1 << depth) > (((sdDir == 0 ? uDegree : vDegree) + 1) << 1))
					 && ((1 << otherDepth) > (((sdDir == 0 ? vDegree : uDegree) + 1) << 1)))
					|| isFlat (v.elements, n - patchSize,
							   (depth + otherDepth) >> 1, tmp)))
			{
				if ((sdDir == 0) == uvPermuted)
				{
					sds = se; se = nw; nw = sds;
					float t = sleft; sleft = oleft; oleft = t;
					t = sright; sright = oright; oright = t;
				}
				n -= patchSize;
				int nextU, nextV, lastU, lastV;
				if (uvPermuted)
				{
					lastU = patchSize - uRowSize;
					lastV = uRowSize - dimension;
					nextU = uRowSize;
					nextV = dimension;
				}
				else
				{
					lastU = uRowSize - dimension;
					lastV = patchSize - uRowSize;
					nextU = dimension;
					nextV = uRowSize;
				}
				pushVertex (sw, v.elements, n, nextU, nextV, tmp, sleft, oleft);
				pushVertex (se, v.elements, n + lastU, nextV, -nextU, tmp, sright, oleft);
				pushVertex (ne, v.elements, n + lastU + lastV, -nextU, -nextV, tmp, sright, oright);
				pushVertex (nw, v.elements, n + lastV, -nextV, nextU, tmp, sleft, oright);
				return;
			}
			if (subdivided || (sdDir == lastSdDir))
			{
				subdividePatch (v, sw, nw, se, ne, sdDir ^ 1, otherRight,
								sdRight, tmp, false, otherDepth, depth,
								!subdivided, lastSdDir, oleft, oright,
								sleft, sright);
				return;
			}
		}
		if (sdDir == 0)
		{
			subdivide (v, dimension, uDegree, dimension, vDegree + 1, uRowSize, tmp);
		}
		else
		{
			subdivide (v, dimension, vDegree, uRowSize, uDegree + 1, dimension, tmp);
		}
		n = v.size;
		if (sds == 0)
		{
			sds = addInterpolated
				(sdsi, sw, se, v.elements, n - 2 * patchSize);
		}
		if (sdn == 0)
		{
			sdn = addInterpolated
				(sdni, nw, ne, v.elements, n - dimension);
		}
		if (sdDir == 0)
		{
			v.setSize (n + dimension * (vDegree + 1));
			dir = 1;
			degree = vDegree;
			copy (v.elements, n - 2 * patchSize, uRowSize,
				  v.elements, n, dimension, vDegree + 1);
		}
		else
		{
			v.setSize (n + dimension * (uDegree + 1));
			dir = 0;
			degree = uDegree;
			FloatList.arraycopy (v.elements, n - 2 * patchSize,
							 v.elements, n, uRowSize);
		}
		left = sds;
		right = sdn;
		rightPart = otherRight;
		subdivideCurve (v, tmp, 0, otherDepth);
		v.setSize (n);
		subdividePatch (v, sw, nw, sds, sdn, sdDir ^ 1, otherRight, false,
						tmp, true, otherDepth, depth + 1, false, sdDir,
						oleft, oright, sleft, (sleft + sright) * 0.5f);
		v.setSize (v.size - patchSize);
		subdividePatch (v, sds, sdn, se, ne, sdDir ^ 1, otherRight, true,
						tmp, true, otherDepth, depth + 1, false, sdDir,
						oleft, oright, (sleft + sright) * 0.5f, sright);
	}


	private int addInterpolated (int ref, int l, int r,
								 float[] v, int index)
	{
		index = addVertex (v, index);
		tmpData.set (ref, index);
		tmpData.set (index * IPV + 4, 1);
//*
		l *= 3;
		r *= 3;
		ref = index * 3;
		v = out.vertices.elements;
		float dot = 0, lengthSquared = 0;
		for (int j = 2; j >= 0; j--)
		{
			float t = v[l + j];
			dot += (v[ref + j] - t) * (t = v[r + j] - t);
			lengthSquared += t * t;
		}
		if (((dot >= 0) ? dot : -dot) < 1e5f * lengthSquared)
		{
			dot /= lengthSquared;
			lengthSquared = 1 - dot;
			v[ref] = lengthSquared * v[l] + dot * v[r];
			v[ref + 1] = lengthSquared * v[l + 1] + dot * v[r + 1];
			v[ref + 2] = lengthSquared * v[l + 2] + dot * v[r + 2];
		}
		else
		{
			v[ref] = v[l];
			v[ref + 1] = v[l + 1];
			v[ref + 2] = v[l + 2];
		}
//*/
		return index;
	}

/*
		private void drawBoundary ()
		{
			draw (left, right, rightPart, dir);
		}

		private void draw (int l, int r, boolean p, int d)
		{
			int i = out.intData.get ((p ? l : r) * IPV + (d << 1) + (p ? 1 : 0));
			if (i > 0)
			{
				draw (l, i, false, d);
				draw (i, r, true, d);
			}
			else
			{
				vertexIndices.push (l).push (0).push (r).push (0).push (-1);
			}
		}
//*/

	@Override
	int addVertex (float[] v, int i)
	{
		i = super.addVertex (v, i);
		int j = i * IPV;
		int n = IPV - 1;
		tmpData.set (j + n, 0);
		int[] a = tmpData.elements;
		while (--n >= 0)
		{
			a[j + n] = 0;
		}
		return i;
	}


	@Override
	protected void visitFlat (float[] v, int index)
	{
	}


	private static void copy (float[] src, int srcIndex, int srcBlockSize,
							  float[] dest, int destIndex, int destBlockSize,
							  int count)
	{
		while (--count >= 0)
		{
			FloatList.arraycopy (src, srcIndex, dest, destIndex, destBlockSize);
			srcIndex += srcBlockSize;
			destIndex += destBlockSize;
		}
	}

}
