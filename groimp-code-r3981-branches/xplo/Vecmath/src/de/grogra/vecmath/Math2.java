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

package de.grogra.vecmath;

import javax.vecmath.*;

import java.awt.geom.*;
import java.util.Random;

/**
 * This class contains a set of mathematical functions and constants,
 * including random and noise generators, linear algebra, and
 * table-based trigonometric functions.
 *
 * @author Ole Kniemeyer
 */
public final class Math2
{
	/**
	 * The constant &pi; as <code>float</code>.
	 */
	public static float M_PI = (float) Math.PI;

	/**
	 * The constant &pi; / 2 as <code>float</code>.
	 */
	public static float M_PI_2 = (float) Math.PI / 2;

	/**
	 * The constant 2 &pi; as <code>float</code>.
	 */
	public static float M_2PI = (float) (2 * Math.PI);

	/**
	 * The constant 1 / &pi; as <code>float</code>.
	 */
	public static float M_1_PI = (float) (1 / Math.PI);

	/**
	 * The constant 1 / (2 &pi;) as <code>float</code>.
	 */
	public static float M_1_2PI = (float) (1 / (2 * Math.PI));
	
	/**
	 * The very small number epsilon. 1E-09
	 */
	public static final float EPSILON = 1E-09f;
	
	/**
	 * The very small number epsilon. 1E-05
	 */
	public static final float EPS = 1E-05f;
	
	/**
	 * Numbers of skysegments for the turtlesky
	 */
	public static int nbSkySegments = 46;
	
	/**
	 * Define the turtlesky
	 */
	public static final Vector3d turtsky[] = {
		new Vector3d(-0.00000, 0.00000, 1.00000),		// 0
		new Vector3d(0.10994, 0.33835, 0.93458),
		new Vector3d(-0.28782, 0.20911, 0.93458),
		new Vector3d(-0.28782, -0.20911, 0.93458),
		new Vector3d(0.10994, -0.33835, 0.93458),
		new Vector3d(0.35576, 0.00000, 0.93458),
		new Vector3d(0.49115, 0.35684, 0.79463),
		new Vector3d(-0.18760, 0.57739, 0.79463),
		new Vector3d(-0.60710, 0.00000, 0.79463),
		new Vector3d(-0.18760, -0.57739, 0.79463),		// 9
		new Vector3d(0.49115, -0.35684, 0.79463),
		new Vector3d(0.20913, 0.64363, 0.73622),
		new Vector3d(-0.54750, 0.39778, 0.73622),
		new Vector3d(-0.54750, -0.39778, 0.73622),
		new Vector3d(0.20913, -0.64363, 0.73622),
		new Vector3d(0.67675, 0.00000, 0.73622),
		new Vector3d(0.78678, 0.33835, 0.51623),
		new Vector3d(0.56492, 0.64371, 0.51623),
		new Vector3d(-0.07866, 0.85283, 0.51623),
		new Vector3d(-0.43764, 0.73619, 0.51623),		// 19
		new Vector3d(-0.83540, 0.18872, 0.51623),
		new Vector3d(-0.83540, -0.18872, 0.51623),
		new Vector3d(-0.43764, -0.73619, 0.51623),
		new Vector3d(-0.07866, -0.85283, 0.51623),
		new Vector3d(0.56492, -0.64371, 0.51623),
		new Vector3d(0.78678, -0.33835, 0.51623),
		new Vector3d(0.27638, 0.85061, 0.44729),
		new Vector3d(-0.72358, 0.52571, 0.44729),
		new Vector3d(-0.72358, -0.52571, 0.44729),
		new Vector3d(0.27638, -0.85061, 0.44729),		// 29
		new Vector3d(0.89439, 0.00000, 0.44729),
		new Vector3d(0.79466, 0.57735, 0.18755),
		new Vector3d(-0.30353, 0.93418, 0.18755),
		new Vector3d(-0.98225, 0.00000, 0.18755),
		new Vector3d(-0.30353, -0.93418, 0.18755),
		new Vector3d(0.79466, -0.57735, 0.18755),
		new Vector3d(0.96465, 0.20909, 0.16040),
		new Vector3d(0.49695, 0.85282, 0.16040),
		new Vector3d(0.09923, 0.98205, 0.16040),
		new Vector3d(-0.65752, 0.73617, 0.16040),		// 39
		new Vector3d(-0.90332, 0.39785, 0.16040),
		new Vector3d(-0.90332, -0.39785, 0.16040),
		new Vector3d(-0.65752, -0.73617, 0.16040),
		new Vector3d(0.09923, -0.98205, 0.16040),
		new Vector3d(0.49695, -0.85282, 0.16040),
		new Vector3d(0.96465, -0.20909, 0.16040)		// 45
	};

	private Math2 ()
	{
	}

	private static final float[] sin = new float[0x4001];

	private static final int RANDOM_SIZE = 1 << 9;
	private static final int RANDOM_MASK = RANDOM_SIZE - 1;
	private static final float[] random = new float[RANDOM_SIZE];

	private static final char[] randomPermutation = new char[0x10000];

	static
	{
		for (int i = 0; i <= Character.MAX_VALUE; i++)
		{
			randomPermutation[i] = (char) i;
		}
		Random r = new Random (123456789);
		for (int i = 0; i <= Character.MAX_VALUE; i++)
		{
			int j = r.nextInt (Character.MAX_VALUE + 1);
			char t = randomPermutation[i];
			randomPermutation[i] = randomPermutation[j];
			randomPermutation[j] = t;
		}
		for (int i = 0; i < RANDOM_SIZE; i++)
		{
			random[i] = 2 * r.nextFloat () - 1;
		}
		for (int i = 0; i <= 0x4000; i++)
		{
			sin[i] = (float) Math.sin (i * (2 * Math.PI / 0x10000));
		}
	}

	/**
	 * This method represents a random permutation of <code>char</code>
	 * values. I.e., it is a bijective map on <code>char</code>s, the
	 * mapping being defined by a pseudo-random number generator. The
	 * mapping remains the same between different invocations of the
	 * Java Virtual Machine.
	 * 
	 * @param index an index into a permutation table
	 * @return the permuted value
	 */
	public static char random (char index)
	{
		return randomPermutation[index];
	}

	private static char random1d (int a, int b)
	{
		return randomPermutation[(char) (a ^ b)];
	}

	private static char random2d (int a, int b)
	{
		return randomPermutation[randomPermutation[(char) a] ^ (char) b];
	}

	/**
	 * This method represents a Perlin-style 3D noise function. It
	 * is implemented based on POV-Ray's Noise function. 
	 * 
	 * @param x x-coordinate
	 * @param y x-coordinate
	 * @param z x-coordinate
	 * @return noise value at (x, y, z)
	 */
	public static float noise (float x, float y, float z)
	{
		int ix = (x < 0) ? (int) ((ix = 1 - (int) x) + x) - ix : (int) x;
		int iy = (y < 0) ? (int) ((iy = 1 - (int) y) + y) - iy : (int) y;
		int iz = (z < 0) ? (int) ((iz = 1 - (int) z) + z) - iz : (int) z;

		float dx = x - ix, dy = y - iy, dz = z - iz;
		float sx = dx * dx * (3 - 2 * dx), sy = dy * dy * (3 - 2 * dy), sz = dz
				* dz * (3 - 2 * dz);
		float tx = 1 - sx, ty = 1 - sy;
		float txty = tx * ty, sxty = sx * ty, txsy = tx * sy, sxsy = sx * sy;

		char xyr = random2d (ix, iy), x1yr = random2d (ix + 1, iy), xy1r = random2d (
				ix, iy + 1), x1y1r = random2d (ix + 1, iy + 1);

		x = ((1 - sz)
				* (txty * incrSum (random1d (xyr, iz), dx, dy, dz) + sxty
						* incrSum (random1d (x1yr, iz), dx - 1, dy, dz) + txsy
						* incrSum (random1d (xy1r, iz), dx, dy - 1, dz) + sxsy
						* incrSum (random1d (x1y1r, iz), dx - 1, dy - 1, dz)) + sz
				* (txty * incrSum (random1d (xyr, iz + 1), dx, dy, --dz) + sxty
						* incrSum (random1d (x1yr, iz + 1), dx - 1, dy, dz)
						+ txsy
						* incrSum (random1d (xy1r, iz + 1), dx, dy - 1, dz) + sxsy
						* incrSum (random1d (x1y1r, iz + 1), dx - 1, dy - 1, dz))) * 2;
		return (x <= -1) ? -1 : (x >= 1) ? 1 : x;
	}

	private static float incrSum (int index, float x, float y, float z)
	{
		return random[index & RANDOM_MASK] * 0.5f
				+ random[(index + 1) & RANDOM_MASK] * x
				+ random[(index + 2) & RANDOM_MASK] * y
				+ random[(index + 3) & RANDOM_MASK] * z;
	}

	/**
	 * This method represents the derivative of a Perlin-style
	 * 3D noise function. It is implemented based on POV-Ray's
	 * DNoise function.
	 * 
	 * @param out the computed derivate at (x, y, z) is placed in here
	 * @param x x-coordinate
	 * @param y x-coordinate
	 * @param z x-coordinate
	 */
	public static void dNoise (Tuple3f out, float x, float y, float z)
	{
		int ix = (x < 0) ? (int) ((ix = 1 - (int) x) + x) - ix : (int) x;
		int iy = (y < 0) ? (int) ((iy = 1 - (int) y) + y) - iy : (int) y;
		int iz = (z < 0) ? (int) ((iz = 1 - (int) z) + z) - iz : (int) z;

		float dx = x - ix, dy = y - iy, dz = z - iz;
		float sx = dx * dx * (3 - 2 * dx), sy = dy * dy * (3 - 2 * dy), sz = dz
				* dz * (3 - 2 * dz);
		float tx = 1 - sx, ty = 1 - sy, tz = 1 - sz;
		float dxm1 = dx - 1, dym1 = dy - 1;
		float txty = tx * ty, sxty = sx * ty, txsy = tx * sy, sxsy = sx * sy;

		char xyr = random2d (ix, iy), x1yr = random2d (ix + 1, iy), xy1r = random2d (
				ix, iy + 1), x1y1r = random2d (ix + 1, iy + 1);

		int m = random1d (xyr, iz);
		float s = tz * txty;
		out.x = s * incrSum (m, dx, dy, dz);
		out.y = s * incrSum (m + 8, dx, dy, dz);
		out.z = s * incrSum (m + 16, dx, dy, dz);

		m = random1d (x1yr, iz);
		s = tz * sxty;
		out.x += s * incrSum (m, dxm1, dy, dz);
		out.y += s * incrSum (m + 8, dxm1, dy, dz);
		out.z += s * incrSum (m + 16, dxm1, dy, dz);

		m = random1d (xy1r, iz);
		s = tz * txsy;
		out.x += s * incrSum (m, dx, dym1, dz);
		out.y += s * incrSum (m + 8, dx, dym1, dz);
		out.z += s * incrSum (m + 16, dx, dym1, dz);

		m = random1d (x1y1r, iz);
		s = tz * sxsy;
		out.x += s * incrSum (m, dxm1, dym1, dz);
		out.y += s * incrSum (m + 8, dxm1, dym1, dz);
		out.z += s * incrSum (m + 16, dxm1, dym1, dz);

		m = random1d (xyr, ++iz);
		s = sz * txty;
		out.x += s * incrSum (m, dx, dy, --dz);
		out.y += s * incrSum (m + 8, dx, dy, dz);
		out.z += s * incrSum (m + 16, dx, dy, dz);

		m = random1d (x1yr, iz);
		s = sz * sxty;
		out.x += s * incrSum (m, dxm1, dy, dz);
		out.y += s * incrSum (m + 8, dxm1, dy, dz);
		out.z += s * incrSum (m + 16, dxm1, dy, dz);

		m = random1d (xy1r, iz);
		s = sz * txsy;
		out.x += s * incrSum (m, dx, dym1, dz);
		out.y += s * incrSum (m + 8, dx, dym1, dz);
		out.z += s * incrSum (m + 16, dx, dym1, dz);

		m = random1d (x1y1r, iz);
		s = sz * sxsy;
		out.x += s * incrSum (m, dxm1, dym1, dz);
		out.y += s * incrSum (m + 8, dxm1, dym1, dz);
		out.z += s * incrSum (m + 16, dxm1, dym1, dz);
	}

	public static void dTurbulence (Tuple3f out, float x, float y, float z,
			int octaves, float lambda, float omega)
	{
		dNoise (out, x, y, z);
		float vx = out.x, vy = out.y, vz = out.z;
		float l = lambda, o = omega;
		while (--octaves > 0)
		{
			dNoise (out, x * l, y * l, z * l);
			vx += out.x * o;
			vy += out.y * o;
			vz += out.z * o;
			if (octaves > 1)
			{
				l *= lambda;
				o *= omega;
			}
		}
		out.x = vx;
		out.y = vy;
		out.z = vz;
	}

	public static float turbulence (float x, float y, float z, int octaves,
			float lambda, float omega)
	{
		float v = noise (x, y, z);
		float l = lambda, o = omega;
		while (--octaves > 0)
		{
			v += o * noise (x * l, y * l, z * l);
			if (octaves > 1)
			{
				l *= lambda;
				o *= omega;
			}
		}
		return v;
	}

	/**
	 * Returns the sine of the argument. The range of <code>char</code>
	 * values is mapped to [0, 2 &pi;), i.e., the angle in radians
	 * is 2 &pi; <code>c</code> / 0x10000. This method uses a pre-computed
	 * table.
	 * 
	 * @param c an angle
	 * @return the sine of the angle
	 */
	public static float csin (char c)
	{
		return (c <= 0x8000) ? ((c <= 0x4000) ? sin[c] : sin[0x8000 - c])
			: (c <= 0xc000) ? -sin[c - 0x8000] : -sin[0x10000 - c];
	}

	/**
	 * Returns the cosine of the argument. The range of <code>char</code>
	 * values is mapped to [0, 2 &pi;), i.e., the angle in radians
	 * is 2 &pi; <code>c</code> / 0x10000. This method uses a pre-computed
	 * table.
	 * 
	 * @param c an angle
	 * @return the cosine of the angle
	 */
	public static float ccos (char c)
	{
		return csin ((char) (c + 0x4000));
	}

	/**
	 * Returns the sine of the argument. This method uses a pre-computed
	 * table.
	 * 
	 * @param x an angle, in radians
	 * @return the sine of the angle
	 */
	public static float sin (float x)
	{
		x = (x *= M_1_2PI) - (int) x;
		return csin ((x >= 0) ? (char) (x * 0x10000) : (char) ((x + 1) * 0x10000));
	}

	/**
	 * Returns the sine of the argument. The range of [0, 1] for <code>x</code>
	 * is mapped to [0, 2 &pi;], i.e., the angle in radians
	 * is 2 &pi; <code>x</code>. This method uses a pre-computed
	 * table.
	 * 
	 * @param x an angle
	 * @return the sine of the angle
	 */
	public static float sin01 (float x)
	{
		x -= (int) x;
		return csin ((x >= 0) ? (char) (x * 0x10000) : (char) ((x + 1) * 0x10000));
	}

	
	/**
	 * Returns the next 48-bit pseudo-random number based on the previous
	 * pseudo-random number <code>seed</code>. The sequence of numbers is the
	 * same as the sequence computed by <code>java.util.Random</code>:
	 * <pre>
	 * nextRandom = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
	 * </pre>
	 * However, this method should be slightly faster because it does not
	 * have to deal with multi-threading issues.
	 * <p>
	 * Pseudo-random <code>int</code>s of <code>n</code> bits should be
	 * obtained from the returned value by the formula
	 * <code>(int) (nextRandom >>> (48 - n))</code>.
	 * 
	 * @param seed previous number of sequence
	 * @return next number of sequence
	 */
	public static long nextRandom (long seed)
	{
		return (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
	}

	public static int floor (double x)
	{
		int i = (int) x;
		return ((x >= 0) || (i == x)) ? i : i - 1;
	}


	public static float gamma (float x)
	{
		float g = 1f;
		while (x > 3)
		{
			x -= 1f;
			g *= x;
		}
		while (x < 2)
		{
			g /= x;
			x += 1f;
		}
		x -= 2f;
		return g
				* (1.0f + x
						* (0.422784f + x
								* (0.41184f + x
										* (0.0815769f + x
												* (0.074249f + x
														* (-0.000266982f + x
																* (0.011154f + x
																		* (-0.00285265f + x
																				* (0.00210393f - x * 0.000919574f)))))))));
	}

	public static boolean quadricIntersection (double a00, double a01,
			double a02, double a11, double a12, double a22, double b0,
			double b1, double b2, double c, Point3d p, Vector3d d,
			double[] lambda)
	{
		// x^T A x + 2 b^T x + c = 0, x = p + lambda d
		double b = b0 * d.x + b1 * d.y + b2 * d.z + a00 * p.x * d.x + a11 * p.y
				* d.y + a22 * p.z * d.z + a01 * (p.x * d.y + p.y * d.x) + a02
				* (p.x * d.z + p.z * d.x) + a12 * (p.y * d.z + p.z * d.y),

		a = a00 * d.x * d.x + a11 * d.y * d.y + a22 * d.z * d.z + 2
				* (a01 * d.x * d.y + a02 * d.x * d.z + a12 * d.y * d.z);

		c = b
				* b
				- a
				* (c + a00 * p.x * p.x + a11 * p.y * p.y + a22 * p.z * p.z + 2 * (a01
						* p.x
						* p.y
						+ a02
						* p.x
						* p.z
						+ a12
						* p.y
						* p.z
						+ b0
						* p.x + b1 * p.y + b2 * p.z));
		if ((c < 0) || (a == 0))
		{
			return false;
		}
		if (c > 0)
		{
			c = Math.sqrt (c);
		}
		lambda[0] = -(b + c) / a;
		lambda[1] = (-b + c) / a;
		return true;
	}

	public static double distance (Tuple3d p, Vector3d v, Tuple3d q)
	{
		double dx = p.x - q.x, dy = p.y - q.y, dz = p.z - q.z;
		double t = dx * v.x + dy * v.y + dz * v.z;
		t = dx * dx + dy * dy + dz * dz - t * t / v.lengthSquared ();
		return (t <= 0d) ? 0d : Math.sqrt (t);
	}

	public static double closestConnection (Tuple3d p, Vector3d v, Tuple3d q)
	{
		double dx = q.x - p.x, dy = q.y - p.y, dz = q.z - p.z;
		return (dx * v.x + dy * v.y + dz * v.z) / v.lengthSquared ();
	}

	/**
	 * Computes the shortest straight connection between two lines.
	 * This method computes two scalars <code>lambda[0]</code>
	 * and <code>lambda[1]</code> such that the line from
	 * <code>p1 + lambda[0] v1</code> to <code>p2 + lambda[1] v2</code>
	 * is the shortest connection line between these lines.
	 * 
	 * @param p1 a point on the first line
	 * @param v1 the direction of the first line
	 * @param p2 a point on the second line
	 * @param v2 the direction of the second line
	 * @param lambda the computed scalars are written into this array
	 * @return the length of the shortest connection
	 */
	public static double shortestConnection (Tuple3d p1, Vector3d v1,
			Tuple3d p2, Vector3d v2, double[] lambda)
	{
		double vx, vy, vz, v1v1, v2v2, v1v2, v1v, v2v;
		vx = p1.x - p2.x;
		vy = p1.y - p2.y;
		vz = p1.z - p2.z;
		v1v1 = v1.lengthSquared ();
		v2v2 = v2.lengthSquared ();
		v1v2 = v1.dot (v2);
		v1v = v1.x * vx + v1.y * vy + v1.z * vz;
		v2v = v2.x * vx + v2.y * vy + v2.z * vz;
		vx = vx * vx + vy * vy + vz * vz;
		vy = v1v2 * v2v - v1v * v2v2;
		vz = vy / (v1v1 * v2v2 - v1v2 * v1v2);
		lambda[0] = vz;
		lambda[1] = (v2v + vz * v1v2) / v2v2;
		vx -= (v2v * v2v + vy * vz) / v2v2;
		return (vx > 0d) ? Math.sqrt (vx) : 0d;
	}

	/**
	 * Computes the shortest straight connection between two lines.
	 * This method computes two scalars <code>lambda[0]</code>
	 * and <code>lambda[1]</code> such that the line from
	 * <code>p1 + lambda[0] v1</code> to <code>p2 + lambda[1] v2</code>
	 * is the shortest connection line between these lines.
	 * 
	 * @param p1 a point on the first line
	 * @param v1 the direction of the first line
	 * @param p2 a point on the second line
	 * @param v2 the direction of the second line
	 * @param lambda the computed scalars are written into this array
	 * @return the length of the shortest connection
	 */
	public static double shortestConnection (Tuple3d p1, Vector3d v1,
			Tuple3f p2, Vector3f v2, double[] lambda)
	{
		double vx, vy, vz, v1v1, v2v2, v1v2, v1v, v2v;
		vx = p1.x - p2.x;
		vy = p1.y - p2.y;
		vz = p1.z - p2.z;
		v1v1 = v1.lengthSquared ();
		v2v2 = v2.lengthSquared ();
		v1v2 = v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
		v1v = v1.x * vx + v1.y * vy + v1.z * vz;
		v2v = v2.x * vx + v2.y * vy + v2.z * vz;
		vx = vx * vx + vy * vy + vz * vz;
		vy = v1v2 * v2v - v1v * v2v2;
		vz = vy / (v1v1 * v2v2 - v1v2 * v1v2);
		lambda[0] = vz;
		lambda[1] = (v2v + vz * v1v2) / v2v2;
		vx -= (v2v * v2v + vy * vz) / v2v2;
		return (vx > 0d) ? Math.sqrt (vx) : 0d;
	}

	/**
	 * Computes the shortest straight connection between two lines.
	 * This method computes two scalars <code>lambda[0]</code>
	 * and <code>lambda[1]</code> such that the line from
	 * <code>p1 + lambda[0] v1</code> to <code>p2 + lambda[1] v2</code>
	 * is the shortest connection line between these lines.
	 * 
	 * @param p1 a point on the first line
	 * @param v1 the direction of the first line
	 * @param p2 a point on the second line
	 * @param v2 the direction of the second line
	 * @param lambda the computed scalars are written into this array
	 * @return the length of the shortest connection
	 */
	public static float shortestConnection (Tuple3f p1, Vector3f v1,
			Tuple3f p2, Vector3f v2, float[] lambda)
	{
		float vx, vy, vz, v1v1, v2v2, v1v2, v1v, v2v;
		vx = p1.x - p2.x;
		vy = p1.y - p2.y;
		vz = p1.z - p2.z;
		v1v1 = v1.lengthSquared ();
		v2v2 = v2.lengthSquared ();
		v1v2 = v1.dot (v2);
		v1v = v1.x * vx + v1.y * vy + v1.z * vz;
		v2v = v2.x * vx + v2.y * vy + v2.z * vz;
		vx = vx * vx + vy * vy + vz * vz;
		vy = v1v2 * v2v - v1v * v2v2;
		vz = vy / (v1v1 * v2v2 - v1v2 * v1v2);
		lambda[0] = vz;
		lambda[1] = (v2v + vz * v1v2) / v2v2;
		vx -= (v2v * v2v + vy * vz) / v2v2;
		return (vx > 0f) ? (float) Math.sqrt (vx) : 0f;
	}

	public static void mulAffine (Matrix3d out, Matrix3d l, Matrix3d r)
	{
		double m00, m01, m10, m11, v0, v1;
		out.m02 = (m00 = l.m00) * (v0 = r.m02) + (m01 = l.m01) * (v1 = r.m12)
				+ l.m02;
		out.m12 = (m10 = l.m10) * v0 + (m11 = l.m11) * v1 + l.m12;

		out.m00 = m00 * (v0 = r.m00) + m01 * (v1 = r.m10);
		out.m10 = m10 * v0 + m11 * v1;

		out.m01 = m00 * (v0 = r.m01) + m01 * (v1 = r.m11);
		out.m11 = m10 * v0 + m11 * v1;
	}

	public static void invertAffine (Matrix3f in, Matrix3f out)
	{
		float x = in.m02, y = in.m12, d, m00, m01, m10, m11;
		d = 1 / ((m00 = in.m00) * (m11 = in.m11) - (m01 = in.m01)
				* (m10 = in.m10));
		out.m02 = -((out.m00 = m11 * d) * x + (out.m01 = -m01 * d) * y);
		out.m12 = -((out.m10 = -m10 * d) * x + (out.m11 = m00 * d) * y);
	}

	public static void invertAffine (Matrix3d in, Matrix3d out)
	{
		double x = in.m02, y = in.m12, d, m00, m01, m10, m11;
		d = 1 / ((m00 = in.m00) * (m11 = in.m11) - (m01 = in.m01)
				* (m10 = in.m10));
		out.m02 = -((out.m00 = m11 * d) * x + (out.m01 = -m01 * d) * y);
		out.m12 = -((out.m10 = -m10 * d) * x + (out.m11 = m00 * d) * y);
	}

	public static void invTransformVector (Matrix3f t, Tuple2f v)
	{
		float x = v.x, y = v.y, d, m00, m01, m10, m11;
		d = 1 / ((m00 = t.m00) * (m11 = t.m11) - (m01 = t.m01) * (m10 = t.m10));
		v.x = (m11 * x - m01 * y) * d;
		v.y = (m00 * y - m10 * x) * d;
	}

	public static void invTransformPoint (Matrix3f t, Tuple2f p)
	{
		p.x -= t.m02;
		p.y -= t.m12;
		invTransformVector (t, p);
	}

	public static void invTransformVector (Matrix3d t, Tuple2d v)
	{
		double x = v.x, y = v.y, d, m00, m01, m10, m11;
		d = 1 / ((m00 = t.m00) * (m11 = t.m11) - (m01 = t.m01) * (m10 = t.m10));
		v.x = (m11 * x - m01 * y) * d;
		v.y = (m00 * y - m10 * x) * d;
	}

	public static void invTransformPoint (Matrix3d t, Tuple2d p)
	{
		p.x -= t.m02;
		p.y -= t.m12;
		invTransformVector (t, p);
	}

	public static void transformVector (Matrix3d t, Tuple2d v)
	{
		v.set (t.m00 * v.x + t.m01 * v.y, t.m10 * v.x + t.m11 * v.y);
	}

	public static void transformPoint (Matrix3d t, Tuple2d p)
	{
		p.set (t.m00 * p.x + t.m01 * p.y + t.m02, t.m10 * p.x + t.m11 * p.y
				+ t.m12);
	}

	public static void setAffineTransform (AffineTransform out, Matrix3d in)
	{
		out.setTransform (in.m00, in.m10, in.m01, in.m11, in.m02, in.m12);
	}

	public static void setMatrix3d (Matrix3d out, AffineTransform in)
	{
		out.m00 = in.getScaleX ();
		out.m01 = in.getShearX ();
		out.m02 = in.getTranslateX ();
		out.m10 = in.getShearY ();
		out.m11 = in.getScaleY ();
		out.m12 = in.getTranslateY ();
		out.m20 = 0;
		out.m21 = 0;
		out.m22 = 1;
	}

	public static void invertAffine (Matrix4f in, Matrix4f out)
	{
		float x = in.m03, y = in.m13, z = in.m23, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = in.m11) * (m22 = in.m22) - (m12 = in.m12) * (m21 = in.m21);
		d1 = m12 * (m20 = in.m20) - (m10 = in.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1f / ((m00 = in.m00) * d0 + (m01 = in.m01) * d1 + (m02 = in.m02)
				* d2);
		out.m03 = -((out.m00 = d0 * d) * x
				+ (out.m01 = (m21 * m02 - m01 * m22) * d) * y + (out.m02 = (m01
				* m12 - m02 * m11)
				* d)
				* z);
		out.m13 = -((out.m10 = d1 * d) * x
				+ (out.m11 = (m00 * m22 - m02 * m20) * d) * y + (out.m12 = (m10
				* m02 - m00 * m12)
				* d)
				* z);
		out.m23 = -((out.m20 = d2 * d) * x
				+ (out.m21 = (m20 * m01 - m00 * m21) * d) * y + (out.m22 = (m00
				* m11 - m01 * m10)
				* d)
				* z);
	}

	public static void invertAffine (Matrix4d in, Matrix4d out)
	{
		double x = in.m03, y = in.m13, z = in.m23, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = in.m11) * (m22 = in.m22) - (m12 = in.m12) * (m21 = in.m21);
		d1 = m12 * (m20 = in.m20) - (m10 = in.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = in.m00) * d0 + (m01 = in.m01) * d1 + (m02 = in.m02)
				* d2);
		out.m03 = -((out.m00 = d0 * d) * x
				+ (out.m01 = (m21 * m02 - m01 * m22) * d) * y + (out.m02 = (m01
				* m12 - m02 * m11)
				* d)
				* z);
		out.m13 = -((out.m10 = d1 * d) * x
				+ (out.m11 = (m00 * m22 - m02 * m20) * d) * y + (out.m12 = (m10
				* m02 - m00 * m12)
				* d)
				* z);
		out.m23 = -((out.m20 = d2 * d) * x
				+ (out.m21 = (m20 * m01 - m00 * m21) * d) * y + (out.m22 = (m00
				* m11 - m01 * m10)
				* d)
				* z);
	}

	public static void invertAffine (Matrix34d in, Matrix4d out)
	{
		double x = in.m03, y = in.m13, z = in.m23, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = in.m11) * (m22 = in.m22) - (m12 = in.m12) * (m21 = in.m21);
		d1 = m12 * (m20 = in.m20) - (m10 = in.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = in.m00) * d0 + (m01 = in.m01) * d1 + (m02 = in.m02)
				* d2);
		out.m03 = -((out.m00 = d0 * d) * x
				+ (out.m01 = (m21 * m02 - m01 * m22) * d) * y + (out.m02 = (m01
				* m12 - m02 * m11)
				* d)
				* z);
		out.m13 = -((out.m10 = d1 * d) * x
				+ (out.m11 = (m00 * m22 - m02 * m20) * d) * y + (out.m12 = (m10
				* m02 - m00 * m12)
				* d)
				* z);
		out.m23 = -((out.m20 = d2 * d) * x
				+ (out.m21 = (m20 * m01 - m00 * m21) * d) * y + (out.m22 = (m00
				* m11 - m01 * m10)
				* d)
				* z);
	}

	public static void makeAffine (Matrix3d m)
	{
		m.m20 = m.m21 = 0;
		m.m22 = 1;
	}

	public static void makeAffine (Matrix4d m)
	{
		m.m30 = m.m31 = m.m32 = 0;
		m.m33 = 1;
	}

	public static void makeAffine (Matrix3f m)
	{
		m.m20 = m.m21 = 0;
		m.m22 = 1;
	}

	public static void makeAffine (Matrix4f m)
	{
		m.m30 = m.m31 = m.m32 = 0;
		m.m33 = 1;
	}

	public static void mulAffine (Matrix4d out, Matrix4d l, Matrix4d r)
	{
		double m00, m01, m02, m10, m11, m12, m20, m21, m22, v0, v1, v2;
		out.m03 = (m00 = l.m00) * (v0 = r.m03) + (m01 = l.m01) * (v1 = r.m13)
				+ (m02 = l.m02) * (v2 = r.m23) + l.m03;
		out.m13 = (m10 = l.m10) * v0 + (m11 = l.m11) * v1 + (m12 = l.m12) * v2
				+ l.m13;
		out.m23 = (m20 = l.m20) * v0 + (m21 = l.m21) * v1 + (m22 = l.m22) * v2
				+ l.m23;

		out.m00 = m00 * (v0 = r.m00) + m01 * (v1 = r.m10) + m02 * (v2 = r.m20);
		out.m10 = m10 * v0 + m11 * v1 + m12 * v2;
		out.m20 = m20 * v0 + m21 * v1 + m22 * v2;

		out.m01 = m00 * (v0 = r.m01) + m01 * (v1 = r.m11) + m02 * (v2 = r.m21);
		out.m11 = m10 * v0 + m11 * v1 + m12 * v2;
		out.m21 = m20 * v0 + m21 * v1 + m22 * v2;

		out.m02 = m00 * (v0 = r.m02) + m01 * (v1 = r.m12) + m02 * (v2 = r.m22);
		out.m12 = m10 * v0 + m11 * v1 + m12 * v2;
		out.m22 = m20 * v0 + m21 * v1 + m22 * v2;
	}

	public static void setAffine (Matrix4d out, Matrix4d in)
	{
		if (in != out)
		{
			out.m00 = in.m00;
			out.m01 = in.m01;
			out.m02 = in.m02;
			out.m03 = in.m03;

			out.m10 = in.m10;
			out.m11 = in.m11;
			out.m12 = in.m12;
			out.m13 = in.m13;

			out.m20 = in.m20;
			out.m21 = in.m21;
			out.m22 = in.m22;
			out.m23 = in.m23;
		}
	}

	public static void invMul (Matrix3d t, Tuple3d v)
	{
		double x = v.x, y = v.y, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = t.m11) * (m22 = t.m22) - (m12 = t.m12) * (m21 = t.m21);
		d1 = m12 * (m20 = t.m20) - (m10 = t.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = t.m00) * d0 + (m01 = t.m01) * d1 + (m02 = t.m02) * d2);
		v.x = d0 * d * x + (m21 * m02 - m01 * m22) * d * y
				+ (m01 * m12 - m02 * m11) * d * v.z;
		v.y = d1 * d * x + (m00 * m22 - m02 * m20) * d * y
				+ (m10 * m02 - m00 * m12) * d * v.z;
		v.z = d2 * d * x + (m20 * m01 - m00 * m21) * d * y
				+ (m00 * m11 - m01 * m10) * d * v.z;
	}

	public static void invTransformVector (Matrix4d t, Tuple3d v)
	{
		double x = v.x, y = v.y, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = t.m11) * (m22 = t.m22) - (m12 = t.m12) * (m21 = t.m21);
		d1 = m12 * (m20 = t.m20) - (m10 = t.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = t.m00) * d0 + (m01 = t.m01) * d1 + (m02 = t.m02) * d2);
		v.x = d0 * d * x + (m21 * m02 - m01 * m22) * d * y
				+ (m01 * m12 - m02 * m11) * d * v.z;
		v.y = d1 * d * x + (m00 * m22 - m02 * m20) * d * y
				+ (m10 * m02 - m00 * m12) * d * v.z;
		v.z = d2 * d * x + (m20 * m01 - m00 * m21) * d * y
				+ (m00 * m11 - m01 * m10) * d * v.z;
	}

	public static void invTransformVector (Matrix34d t, Tuple3d v)
	{
		double x = v.x, y = v.y, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = t.m11) * (m22 = t.m22) - (m12 = t.m12) * (m21 = t.m21);
		d1 = m12 * (m20 = t.m20) - (m10 = t.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = t.m00) * d0 + (m01 = t.m01) * d1 + (m02 = t.m02) * d2);
		v.x = d0 * d * x + (m21 * m02 - m01 * m22) * d * y
				+ (m01 * m12 - m02 * m11) * d * v.z;
		v.y = d1 * d * x + (m00 * m22 - m02 * m20) * d * y
				+ (m10 * m02 - m00 * m12) * d * v.z;
		v.z = d2 * d * x + (m20 * m01 - m00 * m21) * d * y
				+ (m00 * m11 - m01 * m10) * d * v.z;
	}

	public static void invTransformPoint (Matrix4d t, Tuple3d p)
	{
		p.x -= t.m03;
		p.y -= t.m13;
		p.z -= t.m23;
		invTransformVector (t, p);
	}

	public static void invTransformPoint (Matrix34d t, Tuple3d p)
	{
		p.x -= t.m03;
		p.y -= t.m13;
		p.z -= t.m23;
		invTransformVector (t, p);
	}

	public static void invTransformPointAndVector (Matrix4d t, Tuple3d p,
			Tuple3d v)
	{
		double x = v.x, y = v.y, z = v.z, d0, d1, d2, d, m00, m01, m02, m10, m11, m12, m20, m21, m22;
		d0 = (m11 = t.m11) * (m22 = t.m22) - (m12 = t.m12) * (m21 = t.m21);
		d1 = m12 * (m20 = t.m20) - (m10 = t.m10) * m22;
		d2 = m10 * m21 - m11 * m20;
		d = 1d / ((m00 = t.m00) * d0 + (m01 = t.m01) * d1 + (m02 = t.m02) * d2);
		v.x = d0 * d * x + (m21 * m02 - m01 * m22) * d * y
				+ (m01 * m12 - m02 * m11) * d * z;
		v.y = d1 * d * x + (m00 * m22 - m02 * m20) * d * y
				+ (m10 * m02 - m00 * m12) * d * z;
		v.z = d2 * d * x + (m20 * m01 - m00 * m21) * d * y
				+ (m00 * m11 - m01 * m10) * d * z;
		x = p.x - t.m03;
		y = p.y - t.m13;
		z = p.z - t.m23;
		p.x = d0 * d * x + (m21 * m02 - m01 * m22) * d * y
				+ (m01 * m12 - m02 * m11) * d * z;
		p.y = d1 * d * x + (m00 * m22 - m02 * m20) * d * y
				+ (m10 * m02 - m00 * m12) * d * z;
		p.z = d2 * d * x + (m20 * m01 - m00 * m21) * d * y
				+ (m00 * m11 - m01 * m10) * d * z;
	}

	public static void transformVector (Matrix4d t, Tuple3d v)
	{
		v.set (t.m00 * v.x + t.m01 * v.y + t.m02 * v.z, t.m10 * v.x + t.m11
				* v.y + t.m12 * v.z, t.m20 * v.x + t.m21 * v.y + t.m22 * v.z);
	}

	public static void transformPoint (Matrix4d t, Tuple3d p)
	{
		p.set (t.m00 * p.x + t.m01 * p.y + t.m02 * p.z + t.m03, t.m10 * p.x
				+ t.m11 * p.y + t.m12 * p.z + t.m13, t.m20 * p.x + t.m21 * p.y
				+ t.m22 * p.z + t.m23);
	}

	public static void transformPoint (Matrix4d t, Tuple3d p, Tuple3d out)
	{
		out.set (t.m00 * p.x + t.m01 * p.y + t.m02 * p.z + t.m03, t.m10 * p.x
				+ t.m11 * p.y + t.m12 * p.z + t.m13, t.m20 * p.x + t.m21 * p.y
				+ t.m22 * p.z + t.m23);
	}

	public static void transformTranspose (Matrix3d t, Tuple3d v)
	{
		v.set (t.m00 * v.x + t.m10 * v.y + t.m20 * v.z, t.m01 * v.x + t.m11
				* v.y + t.m21 * v.z, t.m02 * v.x + t.m12 * v.y + t.m22 * v.z);
	}

	public static void transformVector (Matrix4f t, Tuple3f v)
	{
		v.set (t.m00 * v.x + t.m01 * v.y + t.m02 * v.z, t.m10 * v.x + t.m11
				* v.y + t.m12 * v.z, t.m20 * v.x + t.m21 * v.y + t.m22 * v.z);
	}

	public static void transformPoint (Matrix4f t, Tuple3f p)
	{
		p.set (t.m00 * p.x + t.m01 * p.y + t.m02 * p.z + t.m03, t.m10 * p.x
				+ t.m11 * p.y + t.m12 * p.z + t.m13, t.m20 * p.x + t.m21 * p.y
				+ t.m22 * p.z + t.m23);
	}

	public static void lmul (Matrix3d rot, Matrix34d m)
	{
		double m00 = m.m00 * rot.m00 + m.m01 * rot.m10 + m.m02 * rot.m20;
		double m10 = m.m10 * rot.m00 + m.m11 * rot.m10 + m.m12 * rot.m20;
		double m20 = m.m20 * rot.m00 + m.m21 * rot.m10 + m.m22 * rot.m20;
		double m01 = m.m00 * rot.m01 + m.m01 * rot.m11 + m.m02 * rot.m21;
		double m11 = m.m10 * rot.m01 + m.m11 * rot.m11 + m.m12 * rot.m21;
		double m21 = m.m20 * rot.m01 + m.m21 * rot.m11 + m.m22 * rot.m21;
		double m02 = m.m00 * rot.m02 + m.m01 * rot.m12 + m.m02 * rot.m22;
		double m12 = m.m10 * rot.m02 + m.m11 * rot.m12 + m.m12 * rot.m22;
		double m22 = m.m20 * rot.m02 + m.m21 * rot.m12 + m.m22 * rot.m22;
		rot.m00 = m00;
		rot.m10 = m10;
		rot.m20 = m20;
		rot.m01 = m01;
		rot.m11 = m11;
		rot.m21 = m21;
		rot.m02 = m02;
		rot.m12 = m12;
		rot.m22 = m22;
	}

	/**
	 * Computes an orthogonal vector to <code>in</code>. The computed vector
	 * is written to <code>out</code>, which may be the same
	 * reference as <code>in</code>.
	 * 
	 * @param in an input vector
	 * @param out the computed orthogonal vector is placed in here
	 */
	public static void getOrthogonal (Tuple3d in, Tuple3d out)
	{
		if (Math.abs (in.x) > Math.abs (in.y))
		{
			out.set (in.z, 0, -in.x);
		}
		else
		{
			out.set (0, in.z, -in.y);
		}
	}

	/**
	 * Computes an orthogonal vector to <code>in</code>. The computed vector
	 * is written to <code>out</code>, which may be the same
	 * reference as <code>in</code>.
	 * 
	 * @param in an input vector
	 * @param out the computed orthogonal vector is placed in here
	 */
	public static void getOrthogonal (Tuple3f in, Tuple3f out)
	{
		if (Math.abs (in.x) > Math.abs (in.y))
		{
			out.set (in.z, 0, -in.x);
		}
		else
		{
			out.set (0, in.z, -in.y);
		}
	}

	/**
	 * Computes an orthogonal basis. The first two columns of the
	 * computed basis <code>out</code> are unit vectors, the third
	 * column equals <code>in</code> if <code>orthonormal</code>
	 * is <code>false</code>, otherwise it equals the unit vector
	 * in the direction of <code>in</code>. Thus, <code>out</code>
	 * represents a coordinate transformation which transforms the local
	 * z-axis into the direction of <code>in</code>. 
	 * 
	 * @param in the direction of the local z-axis
	 * @param out the computed matrix
	 * @param orthonormal compute an orthonormal matrix?
	 */
	public static void getOrthogonalBasis (Tuple3f in, Matrix3f out,
			boolean orthonormal)
	{
		float s = 1 / (float) Math.sqrt (in.x * in.x + in.y * in.y + in.z
				* in.z);
		if (orthonormal)
		{
			out.m02 = in.x * s;
			out.m12 = in.y * s;
			out.m22 = in.z * s;
		}
		else
		{
			out.m02 = in.x;
			out.m12 = in.y;
			out.m22 = in.z;
		}
		if (Math.abs (in.x) > Math.abs (in.y))
		{
			float f = 1 / (float) Math.sqrt (in.x * in.x + in.z * in.z);
			out.m00 = in.z * f;
			out.m10 = 0;
			out.m20 = -in.x * f;
		}
		else
		{
			float f = 1 / (float) Math.sqrt (in.y * in.y + in.z * in.z);
			out.m00 = 0;
			out.m10 = in.z * f;
			out.m20 = -in.y * f;
		}
		if (orthonormal)
		{
			out.m01 = out.m12 * out.m20 - out.m22 * out.m10;
			out.m11 = out.m22 * out.m00 - out.m02 * out.m20;
			out.m21 = out.m02 * out.m10 - out.m12 * out.m00;
		}
		else
		{
			out.m01 = s * (out.m12 * out.m20 - out.m22 * out.m10);
			out.m11 = s * (out.m22 * out.m00 - out.m02 * out.m20);
			out.m21 = s * (out.m02 * out.m10 - out.m12 * out.m00);
		}
	}

	/**
	 * Computes an orthogonal basis. The first two columns of the
	 * computed basis <code>out</code> are unit vectors, the third
	 * column equals <code>in</code> if <code>orthonormal</code>
	 * is <code>false</code>, otherwise it equals the unit vector
	 * in the direction of <code>in</code>. Thus, <code>out</code>
	 * represents a coordinate transformation which transforms the local
	 * z-axis into the direction of <code>in</code>. 
	 * 
	 * @param in the direction of the local z-axis
	 * @param out the computed matrix
	 * @param orthonormal compute an orthonormal matrix?
	 */
	public static void getOrthogonalBasis (Tuple3d in, Matrix3d out,
			boolean orthonormal)
	{
		double s = 1 / Math.sqrt (in.x * in.x + in.y * in.y + in.z * in.z);
		if (orthonormal)
		{
			out.m02 = in.x * s;
			out.m12 = in.y * s;
			out.m22 = in.z * s;
		}
		else
		{
			out.m02 = in.x;
			out.m12 = in.y;
			out.m22 = in.z;
		}
		if (Math.abs (in.x) > Math.abs (in.y))
		{
			double f = 1 / Math.sqrt (in.x * in.x + in.z * in.z);
			out.m00 = in.z * f;
			out.m10 = 0;
			out.m20 = -in.x * f;
		}
		else
		{
			double f = 1 / Math.sqrt (in.y * in.y + in.z * in.z);
			out.m00 = 0;
			out.m10 = in.z * f;
			out.m20 = -in.y * f;
		}
		if (orthonormal)
		{
			out.m01 = out.m12 * out.m20 - out.m22 * out.m10;
			out.m11 = out.m22 * out.m00 - out.m02 * out.m20;
			out.m21 = out.m02 * out.m10 - out.m12 * out.m00;
		}
		else
		{
			out.m01 = s * (out.m12 * out.m20 - out.m22 * out.m10);
			out.m11 = s * (out.m22 * out.m00 - out.m02 * out.m20);
			out.m21 = s * (out.m02 * out.m10 - out.m12 * out.m00);
		}
	}

	
	/**
	 * Computes a QR decomposition of <code>r</code>. The non-singular matrix
	 * <code>a</code> to be composed is the parameter <code>r</code>. 
	 * This method computes <code>r</code> and <code>q</code> such that
	 * <code>a = q * r</code>, <code>q</code> is an orthogonal matrix,
	 * and <code>r</code> an upper triangular matrix. The signs of
	 * <code>r.m00</code> and <code>r.m11</code> are positive. The sign
	 * of <code>r.m22</code> is the sign of <code>det(a)</code>. As a
	 * consequence, <code>det(q) == 1</code>, so <code>q</code> is a
	 * pure rotation.
	 *  
	 * @param r the input for the matrix to compose and the output for r
	 * @param q the output for q
	 */
	public static void decomposeQR (Matrix3d r, Matrix3d q)
	{
		// first Householder transformation
		double s = Math.sqrt (r.m00 * r.m00 + r.m10 * r.m10 + r.m20 * r.m20);
		double sign;
		int detQ;
		if (r.m00 < 0)
		{
			// use Householder transformation (det == -1)
			detQ = -1;
			sign = 1;
		}
		else
		{
			// use Householder transformation multiplied by -1 (det == 1)
			// this ensures r.m00 > 0
			detQ = 1;
			sign = -1;
			s = -s;
		}
		double h0 = r.m00 - s;
		double h1 = r.m10;
		double h2 = r.m20;

		double w = sign / (s * h0);
		q.m00 = sign + h0 * h0 * w;
		q.m01 = q.m10 = h0 * h1 * w;
		q.m02 = q.m20 = h0 * h2 * w;
		q.m11 = sign + h1 * h1 * w;
		q.m12 = q.m21 = h1 * h2 * w;
		q.m22 = sign + h2 * h2 * w;
		r.mul (q, r);
		r.m10 = r.m20 = 0;
		
		// second Householder transformation
		s = Math.sqrt (r.m11 * r.m11 + r.m21 * r.m21);
		if (r.m11 < 0)
		{
			// use Householder transformation
			sign = 1;
		}
		else
		{
			// use Householder transformation multiplied by -1
			// this ensures r.m11 > 0
			sign = -1;
			s = -s;
		}
		// det(Householder) == -1, whether multiplied by -1 or not
		detQ = -detQ;
		h1 = r.m11 - s;
		h2 = r.m21;
		w = sign / (s * (r.m11 - s));
		double h11 = sign + h1 * h1 * w;
		double h12 = h1 * h2 * w;
		double h22 = sign + h2 * h2 * w;

		// multiply r from left by H
		double r12 = r.m12;
		r.m11 = s * sign;
		r.m21 = 0;
		r.m12 = h11 * r12 + h12 * r.m22;
		r.m22 = h12 * r12 + h22 * r.m22;
			
		// multiply q from right by H
		double q01 = q.m01, q11 = q.m11, q21 = q.m21;
		q.m01 = q01 * h11 + q.m02 * h12;
		q.m02 = q01 * h12 + q.m02 * h22;
		q.m11 = q11 * h11 + q.m12 * h12;
		q.m12 = q11 * h12 + q.m12 * h22;
		q.m21 = q21 * h11 + q.m22 * h12;
		q.m22 = q21 * h12 + q.m22 * h22;

		if (detQ < 0)
		{
			// ensure det(q) == 1. As a consequence, sign(det(a)) == sign(r.m22)
			
			// multiply r from left by diag(1,1,-1)
			r.m22 = -r.m22;

			// multiply q from right by diag(1,1,-1)
			q.m02 = -q.m02;
			q.m12 = -q.m12;
			q.m22 = -q.m22;
		}
	}


	public static boolean isInsideConeT (double px, double py, double pz,
			double ox, double oy, double oz, double ax, double ay, double az,
			double tipDistance, double capDistance, double tan)
	{
		return isInsideConeT (px - ox, py - oy, pz - oz, ax, ay, az,
							  tipDistance, capDistance, tan);
	}
	

	/**
	 * Determines if a point <i>p</i> lies inside a (part of a) cone.
	 * The cone's tip is located at the origin, its axis is given by
	 * <i>a</i> and points to the center of the cone's cap which is located
	 * at axis * capDistance. The small cone from the tip to axis * tipDistance
	 * is removed, i.e., this tests actually checks againts a frustum.
	 * The half-angle of the cone is specified by its tangent. 
	 *  
	 * @param px x coordinate of point <i>p</i>
	 * @param py y coordinate of point <i>p</i>
	 * @param pz z coordinate of point <i>p</i>
	 * @param ax x coordinate of cone axis <i>a</i>
	 * @param ay y coordinate of cone axis <i>a</i>
	 * @param az z coordinate of cone axis <i>a</i>
	 * @param tipDistance distance to the tip (in units of axis)
	 * @param capDistance distance of the cap (in units of axis)
	 * @param tan tangent of cone's half angle
	 * @return <tt>true</tt> iff <i>p</i> lies within or on the cone 
	 */
	public static boolean isInsideConeT (double px, double py, double pz,
			double ax, double ay, double az,
			double tipDistance, double capDistance, double tan)
	{
		double pd = px * ax + py * ay + pz * az;
		double a2 = ax * ax + ay * ay + az * az;
		if ((pd < a2 * tipDistance) || (pd > a2 * capDistance))
		{
			return false;
		}
		return a2 * (px * px + py * py + pz * pz) <= pd * pd * (1 + tan * tan);
	}

	public static double intersectLineWithFrustum (Point3d point,
			Vector3d direction, Point3d origin, Vector3d axis,
			double top, double base, double tan)
	{
		return intersectLineWithFrustum (point.x - origin.x, point.y - origin.y,
				point.z - origin.z, direction.x, direction.y, direction.z,
				axis.x, axis.y, axis.z, top, base, tan);
	}

	/**
	 * Determines the fraction of a line <i>(p,d)</i> which intersects a frustum.
	 * <p>
	 * The frustum
	 * is specified by the cone from which it is obtained by cutting away the
	 * top: The cone's tip is located at the origin, its axis is given by
	 * <i>a</i> and points to the center of the cone's cap which is located
	 * at <tt>base * </tt><i>a</i>.
	 * The half-angle of the cone is specified by its tangent.
	 * The cutting plane for obtaining the frustum is located at the
	 * position <tt>top * </tt><i>a</i>.
	 * <p>
	 * The line is specified by its origin <i>p</i> and its vector <i>d</i>
	 * from the origin to the end.
	 *  
	 * @param px x coordinate of line origin <i>p</i>
	 * @param py y coordinate of line origin <i>p</i>
	 * @param pz z coordinate of line origin <i>p</i>
	 * @param dx x coordinate of line vector <i>d</i>
	 * @param dy y coordinate of line vector <i>d</i>
	 * @param dz z coordinate of line vector <i>d</i>
	 * @param ax x coordinate of cone axis <i>a</i>
	 * @param ay y coordinate of cone axis <i>a</i>
	 * @param az z coordinate of cone axis <i>a</i>
	 * @param top relative position of frustum's top cap along axis
	 * @param base relative position of frustum's base cap along axis
	 * @param tan tangent of cone's half angle
	 * @return fraction of line which intersects the frustum 
	 */
	public static double intersectLineWithFrustum (double px, double py, double pz,
			double dx, double dy, double dz, double ax, double ay, double az, double top,
			double base, double tan)
	{
		double ad = ax * dx + ay * dy + az * dz;
		double ap = ax * px + ay * py + az * pz;
		double a2 = ax * ax + ay * ay + az * az;
		
		double u0;
		double u1;
		if (ad == 0)
		{
			// d and a are parallel
			if (ap > base * a2)
			{
				// p below cone's cap
				return -0;
			}
			if (ap < top * a2)
			{
				// p above frustum's top
				return -0;
			}
			// p between planes of cap and top
			u0 = Double.NEGATIVE_INFINITY;
			u1 = Double.POSITIVE_INFINITY;
		}
		else
		{
			u0 = (base * a2 - ap) / ad;
			u1 = (top * a2 - ap) / ad;
			if (u1 < u0)
			{
				double t = u0; u0 = u1; u1 = t;
			}
		}
		// now [u0, u1] is the range such that p + ud lies between the planes of cap and top 

		// equation of cone: (x - (x.a)/(a.a) a)^2 = tan^2 (x.a)^2 / (a.a)
		// <=> (a.a) (x.x) = (a.x)^2 (1 + tan^2)
		// here x = p + ud, we have to solve for u
		
		tan = 1 + tan * tan;
		double dp = dx * px + dy * py + dz * pz;
		double d2 = dx * dx + dy * dy + dz * dz;
		double p2 = px * px + py * py + pz * pz;
		double a = a2 * d2 - tan * ad * ad;
		double b = a2 * dp - tan * ad * ap;
		double c = a2 * p2 - tan * ap * ap;
		
		// equation to solve is: a u^2 + 2bu + c = 0
		
		double v0;
		double v1;

		if (a == 0)
		{
			if (b == 0)
			{
				if (c == 0)
				{
					v0 = Double.NEGATIVE_INFINITY;
					v1 = Double.POSITIVE_INFINITY;
				}
				else
				{
					return -0;
				}
			}
			else
			{
				v0 = v1 = c / (-2 * b);
			}
		}
		else
		{
			px = b * b - a * c;
			if (px < 0)
			{
				return -0;
			}
			px = (px <= 0) ? 0 : Math.sqrt (px);

			if ((ad > 0) == (a > 0))
			{
				v0 = (px - b) / a;
			}
			else
			{
				v0 = (-px - b) / a;
			}
			v1 = Double.POSITIVE_INFINITY;
		}
		
		if (v0 > u0)
		{
			u0 = v0;
		}
		if (v1 < u1)
		{
			u1 = v1;
		}
		if (u0 > u1)
		{
			return -0;
		}
		if (u1 < 0)
		{
			return 0;
		}
		if (u0 > 1)
		{
			return 0;
		}
		if (u0 <= 0)
		{
			u0 = 0;
		}
		if (u1 >= 1)
		{
			u1 = 1;
		}
		return u1 - u0;
	}

	public static long pow (int a, int b)
	{
		if (b < 0)
		{
			throw new IllegalArgumentException (Integer.toString (b));
		}
		long p = 1;
		while (--b >= 0)
		{
			p *= a;
		}
		return p;
	}

	public static int factorial (int n)
	{
		if (n < 0)
		{
			throw new IllegalArgumentException (Integer.toString (n));
		}
		int f = 1;
		while (n > 1)
		{
			f *= n--;
		}
		return f;
	}

	public static int binomial (int n, int k)
	{
		if (n < 0)
		{
			throw new IllegalArgumentException (Integer.toString (n));
		}
		if ((k < 0) || (k > n))
		{
			throw new IllegalArgumentException (Integer.toString (k));
		}
		if (2 * k > n)
		{
			k = n - k;
		}
		int f = 1;
		for (int i = n - k + 1; i <= n; i++)
		{
			f *= i;
		}
		return f / factorial (k);
	}

	public static void min (Tuple2d min, Tuple2d t)
	{
		min.x = Math.min (min.x, t.x);
		min.y = Math.min (min.y, t.y);
	}

	public static void max (Tuple2d max, Tuple2d t)
	{
		max.x = Math.max (max.x, t.x);
		max.y = Math.max (max.y, t.y);
	}

	public static void min (Tuple2f min, Tuple2f t)
	{
		min.x = Math.min (min.x, t.x);
		min.y = Math.min (min.y, t.y);
	}

	public static void max (Tuple2f max, Tuple2f t)
	{
		max.x = Math.max (max.x, t.x);
		max.y = Math.max (max.y, t.y);
	}

	public static void min (Tuple3d min, Tuple3d t)
	{
		min.x = Math.min (min.x, t.x);
		min.y = Math.min (min.y, t.y);
		min.z = Math.min (min.z, t.z);
	}

	public static void max (Tuple3d max, Tuple3d t)
	{
		max.x = Math.max (max.x, t.x);
		max.y = Math.max (max.y, t.y);
		max.z = Math.max (max.z, t.z);
	}

	public static void min (Tuple3f min, Tuple3f t)
	{
		min.x = Math.min (min.x, t.x);
		min.y = Math.min (min.y, t.y);
		min.z = Math.min (min.z, t.z);
	}

	public static void max (Tuple3f max, Tuple3f t)
	{
		max.x = Math.max (max.x, t.x);
		max.y = Math.max (max.y, t.y);
		max.z = Math.max (max.z, t.z);
	}

	/**
	 * Computes the scalar product <code>(p - q) v</code>
	 * @param p a point
	 * @param q point which is subtracted from <code>p</code>
	 * @param v a vector
	 * @return scalar product <code>(p - q) v</code>
	 */
	public static double dot (Tuple3d p, Tuple3d q, Tuple3d v)
	{
		return (p.x - q.x) * v.x + (p.y - q.y) * v.y + (p.z - q.z) * v.z;
	}

	public static double estimateScaleSquared (Matrix4d transformation)
	{
		return (transformation.m00 * transformation.m00 + transformation.m01
				* transformation.m01 + transformation.m02 * transformation.m02
				+ transformation.m10 * transformation.m10 + transformation.m11
				* transformation.m11 + transformation.m12 * transformation.m12
				+ transformation.m20 * transformation.m20 + transformation.m21
				* transformation.m21 + transformation.m22 * transformation.m22) * 0.57735;
	}

	/**
	 * Computes reflected and transmitted directions according to
	 * Fresnel's formulas.
	 * 
	 * @param normal the normal unit vector of the surface
	 * @param in the negated direction unit vector of the incoming ray
	 * @param iorRatio the index of refraction of the surface side where
	 *   the normal vector points into, divided by the index of refraction
	 *   of the opposite side
	 * @param reflectedOut the computed unit vector of the reflection direction
	 * @param transmittedOut the computed unit vector of the transmission direction
	 * @return the reflection coefficient
	 */
	public static float fresnel (Vector3f normal, Vector3f in, float iorRatio,
			Vector3f reflectedOut, Vector3f transmittedOut)
	{
		transmittedOut.negate (in);

		float cos = normal.dot (in);
		reflectedOut.scaleAdd (2 * cos, normal, transmittedOut);
		int sign;
		if (cos < 0)
		{
			cos = -cos;
			sign = -1;
			iorRatio = 1 / iorRatio;
		}
		else
		{
			sign = 1;
		}
		float t = (1 - iorRatio * iorRatio) + (t = iorRatio * cos) * t;
		if (t <= 0)
		{
			return 1;
		}
		else
		{
			transmittedOut.scale (-iorRatio, in);
			float cost = (float) Math.sqrt (t);
			transmittedOut.scaleAdd (sign * (iorRatio * cos - cost), normal,
					transmittedOut);
			return ((t = (cost - iorRatio * cos) / (cost + iorRatio * cos)) * t + (t = (cos - iorRatio
					* cost)
					/ (cos + iorRatio * cost))
					* t) * 0.5f;
		}
	}

	
	public static float dot (Tuple3f a, Tuple3f b)
	{
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	
	public static float dot (Tuple3f a, Tuple3d b)
	{
		return (float) (a.x * b.x + a.y * b.y + a.z * b.z);
	}

	
	public static double dot (Tuple3d a, Tuple3d b)
	{
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	
	public static void mul (Tuple3f out, Tuple3f a, Tuple3f b)
	{
		out.x = a.x * b.x;
		out.y = a.y * b.y;
		out.z = a.z * b.z;
	}

	
	public static void mul (Tuple3d out, Tuple3d a, Tuple3d b)
	{
		out.x = a.x * b.x;
		out.y = a.y * b.y;
		out.z = a.z * b.z;
	}

	
	public static boolean lessThan (Tuple3d a, Tuple3d b)
	{
		return (a.x < b.x) && (a.y < b.y) && (a.z < b.z);
	}
	
	public static boolean lessThanOrEqual (Tuple3d a, Tuple3d b)
	{
		return (a.x <= b.x) && (a.y <= b.y) && (a.z <= b.z);
	}


	/**
	 * Calculate the next power of two greater or equal than v.
	 * (see "Bit Twiddling Hacks")
	 * 
	 * @param v
	 * @return
	 */
	public static int roundUpNextPowerOfTwo (int v)
	{
		v--;
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		v++;
		return v;
	}

	public static float[] toFloatArray (Tuple3d p)
	{
		float[] a = new float[3];
		a[0] = (float) p.x;
		a[1] = (float) p.y;
		a[2] = (float) p.z;
		return a;
	}

	public static float[] toFloatArray (Tuple4d p)
	{
		float[] a = new float[4];
		a[0] = (float) p.x;
		a[1] = (float) p.y;
		a[2] = (float) p.z;
		a[3] = (float) p.w;
		return a;
	}

	public static float[] toFloatArray (Tuple4f p)
	{
		float[] a = new float[4];
		a[0] = (float) p.x;
		a[1] = (float) p.y;
		a[2] = (float) p.z;
		a[3] = (float) p.w;
		return a;
	}
	
	/**
	 * Normalizes this vector in place. If the length is 0, then the complett vector will be (0, 0, 0).
	 * @param aVector The normalized vector
	 */
	public static void normalize(Vector3d aVector) {
		if(aVector.length() == 0.0f)
			aVector.x = aVector.y = aVector.z = 0.0;
		else 
			aVector.normalize();
	}
	
	/**
	 * Normalizes this vector in place. If the length is 0, then the complett vector will be (0, 0, 0).
	 * @param aVector The normalized vector
	 */
	public static void normalize(Vector3f aVector) {
		if(aVector.length() == 0.0f)
			aVector.x = aVector.y = aVector.z = 0.0f;
		else 
			aVector.normalize();
	}
	
	/**
	 * Computated the distance between two vectors
	 * @param av first vector
	 * @param ev second vector
	 * @return distance between the two vectors
	 */
	public static float abstpp(Vector3d av, Vector3d ev) {
		double deltax = ev.x - av.x;
		double deltay = ev.y - av.y;
		double deltaz = ev.z - av.z;
		return (float) Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2) + Math.pow(deltaz, 2));
	}

	/**
	 * Test of all parts of the vector are null
	 * @param a the vector to test
	 * @return true, if all parts of the vector are null
	 */
	public static boolean isNullVector(Vector3d a) {
		double depsilon = EPSILON;
		if((a.x < depsilon) && (a.y < depsilon) && (a.z < depsilon))
			return true;
		else
			return false;
	}

	/**
	 * Calculates the distance between the point pp and the piece of
	 * straight line delimited by sa and se. If sa == se, the distance
	 * to this point is calculated.
	 * @param pp Point
	 * @param sa Begin of the straight line
	 * @param se End of the straight line
	 * @return Distance between the point pp and the piece of straight line
	 */
	public static float abstps(Vector3d pp, Vector3d sa,Vector3d se) {
		Vector3d samse = new Vector3d();
		Vector3d semsa = new Vector3d();
		double psa, pse;
		
		samse.sub(sa, se);
		
		if(isNullVector(samse))
			return abstpp(sa, pp);
		
		Vector3d sampp = (Vector3d) sa.clone();
		Vector3d sempp = (Vector3d) se.clone();
		sampp.sub(pp);
		sempp.sub(pp);
		if(isNullVector(sampp) || isNullVector(sempp))
			return 0.0f;
		
		semsa.sub(se, sa);
		Vector3d ppmsa = (Vector3d) pp.clone();
		Vector3d ppmse = (Vector3d) pp.clone();
		ppmsa.sub(sa);
		ppmse.sub(se);
		psa = ppmsa.dot(semsa);
		pse = ppmsa.dot(samse);
		
		if((psa < -EPSILON) && (pse < -EPSILON)) {
			System.out.println("Alert: Error in function abstps!");
			return 0.0f;
		}
		if((psa < -EPSILON) && (pse >= -EPSILON))
			return abstpp(sa, pp);
		if((psa >= -EPSILON) && (pse < 0.0f))
			return abstpp(se, pp);
		if((psa >= -EPSILON) && (pse >= 0.0f)) {
			Vector3d hv = (Vector3d) semsa.clone();
			normalize(hv);
			hv.scale(psa/semsa.length());
			pp.sub(sa);
			hv.sub(pp, hv);
			return (float) hv.length();
		}
		System.out.println("This can't happen! (VectorMath.abstps)");
		return 0.0f;
	}
	
	public static void cutCone(Vector3d ss, float alpha, Vector3d pp, Vector3d qq, CutConeParameter ccp) {
		int signal, cas;
		Vector3d qqmpp = (Vector3d) qq.clone();
		
		signal = cas = 0;
		qqmpp.sub(pp);
		
		double ppzmssz = pp.z - ss.z;
		double qqzmssz = qq.z - ss.z;
		double cosa = Math.cos(alpha * Math.PI / 180.0);
		Vector3d ppmss = (Vector3d) pp.clone();
		ppmss.sub(ss);
		Vector3d qqmss = (Vector3d) qq.clone();
		qqmss.sub(ss);
		double pps = ppmss.length();
		double cosapps = cosa * pps;
		double qqs = qqmss.length();
		double cosaqqs = cosa * qqs;
		double spec, lam1, lam2, wrz;
		spec = lam1 = lam2 = wrz = 0.0;
		
		if((alpha < 0.-EPSILON) || (alpha > 90.+EPSILON) || (isNullVector(qqmpp))) {
			ccp.setCorrect(false);
			signal = 1;
			cas = 4;
		} else {
			ccp.setCorrect(true);
			if(alpha < 0.0f)
				alpha = 0.0f;
			else if(alpha > 90.0f)
				alpha = 90.0f;
			
			double sina = Math.sin(alpha * Math.PI / 180.0);
			double ppzmqqz= pp.z - qq.z;
			double pq = qqmpp.length();
			double uu = Math.pow(sina, 2) * Math.pow((ppzmqqz), 2) - Math.pow(cosa, 2) * Math.pow((pp.x - qq.x), 2) - Math.pow(cosa, 2) * Math.pow((pp.y - qq.y), 2);
			double vv = Math.pow(sina, 2) * (ppzmqqz) * qqzmssz - Math.pow(cosa, 2) * (pp.x - qq.x) * (qq.x - ss.x) - Math.pow(cosa, 2) * (pp.y - qq.y) * (qq.y - ss.y);
			double ww = Math.pow(sina, 2) * Math.pow(qqzmssz, 2) - Math.pow(cosa, 2) * Math.pow((qq.x - ss.x), 2) - Math.pow(cosa, 2) * Math.pow((qq.y - ss.y), 2);
			if(Math.abs(Math.abs(qq.z - pp.z) - cosa * pq) < EPSILON) {
				if(Math.abs(uu) > 0.01) {
					ccp.setCorrect(false);
					signal = 2;
					System.out.println("< u = "+uu+" >");
					uu = 0.0;
				}
				Vector3d temp = new Vector3d();
				temp.cross(qqmss, qqmpp);
				
				if(isNullVector(temp))
					lam1 = lam2 = qqs / pq;
				else {
					if(Math.abs(10.0 * vv) < EPSILON) {
						ccp.setCorrect(false);
						signal = 3;
					} else
						lam1 = lam2 = -ww / (2.0 * vv);
					
				}
			} else {
				double vv2 = Math.pow(vv, 2);
				if(Math.abs(10.0 * uu) < EPSILON) {
					if(uu <= 0.0) {
						ccp.setCorrect(false);
						signal = 5;
					}
					if(vv2 < ww * uu) {
						if(vv2 < ww * uu - 0.2 * Math.abs(vv2) * EPS) {
							ccp.setCorrect(false);
							signal = 6;
						} else {
							wrz = 0.0;
							lam1 = lam2 = -vv / uu;
						}
					} else {
						wrz = Math.sqrt(vv2 - ww * uu) / uu;
						lam1 = -vv / uu - wrz;
						lam2 = -vv / uu + wrz;
					}
				} else {
					if(uu >= 0.0) {
						if(uu >= EPSILON) {
							ccp.setCorrect(false);
							signal = 7;
						} else
							uu = 0.0;
					}
					if(vv2 < ww * uu)
						cas = 4;
					else {
						wrz = Math.sqrt(vv2 - ww * uu) / uu;
						lam1 = -vv / uu - wrz;
						lam2 = -vv / uu + wrz;
						if(lam1 * (ppzmqqz) < -qqzmssz) {
							if(lam2 * (ppzmqqz) > -qqzmssz + 0.001 * Math.abs(ppzmqqz)) {
								ccp.setCorrect(false);
								signal = 8;
								System.out.println("lam1="+lam1+", lam2="+lam2+", pz-qz="+(ppzmqqz)+", qqzmssz="+qqzmssz);
							}
							cas = 4;
						} else {
							if(lam2 * (ppzmqqz) < - qqzmssz - 50.0 * Math.abs(lam2) * EPS) {
								ccp.setCorrect(false);
								signal = 9;
							}
							spec = 1;
						}
					}
				}
			}
		}
		if(ccp.isCorrect()) {
			System.out.println("Warning: Error occured in function cutCone (VectorMathExt)");
			System.out.println("Inconsistency number: "+signal);
		}
		if(cas != 4) {
			if(ppzmssz >= cosapps) {
				if(qqzmssz >= cosaqqs)
					cas = 1;
				else
					cas = 2;
			} else {
				if(qqzmssz >= cosaqqs)
					cas = 3;
				else {
					if((spec == 1) && (lam2 > 0.0) && (lam1 < 1.0))
						cas = 5;
					else
						cas = 4;
				}
			}
		}
		switch(cas) {
			case 1:	ccp.setLowLim(0.0f);
					ccp.setUpLim(1.0f);
					ccp.setA(1.0f);
					ccp.setExists(true);
					break;
			case 2:	ccp.setLowLim((float) lam2);
					ccp.setUpLim(1.0f);
					ccp.setA((float) (1.0 - lam2));
					ccp.setExists(true);
					break;
			case 3:	ccp.setLowLim(0.0f);
					ccp.setUpLim((float) lam1);
					ccp.setA((float) lam1);
					ccp.setExists(true);
					break;
			case 4:	ccp.setLowLim(1.0f);
					ccp.setUpLim(0.0f);
					ccp.setA(0.0f);
					ccp.setExists(false);
					break;
			case 5:	ccp.setLowLim((float) lam2);
					ccp.setUpLim((float) lam1);
					ccp.setA((float) (-2.0 * wrz));
					ccp.setExists(true);
					break;
		}
	}

	public static void cutRay2(float a1, float a2, float r1, float r2, float u1, float u2, float v1, float v2, CutRay2Parameter crp) {
		double detm, detu, lambda, mu, t1, t2;
		t1 = v1 - u1;
		t2 = v2 - u2;
		
		if((Math.pow(t1, 2) + Math.pow(t2, 2) < EPSILON) || (Math.pow(r1, 2) + Math.pow(r2, 2) < EPSILON)) {
			crp.setCorrect(false);
			crp.setExists(false);
			crp.setS1(0.0f);
			crp.setS2(0.0f);
		} else {
			crp.setCorrect(true);
			detm = t1 * r2 - t2 * r1;
			detu = r1 * (u2 - a2) - r2 * (u1 - a1);
			if((detm > EPSILON) || (detm < -EPSILON)) {
				lambda = (t1 * (u2 - a2) - t2 * (u1 - a1)) / detm;
				mu = detu / detm;
				if((lambda > EPSILON) && (mu > EPSILON) && (mu < 1.0 - EPSILON)) {
					crp.setExists(true);
					crp.setS1((float) (lambda * r1 + a1));
					crp.setS2((float) (lambda * r2 + a2));
				} else {
					crp.setExists(false);
					crp.setS1(0.0f);
					crp.setS2(0.0f);
				}
			} else {
				if((detu > EPSILON) || (detu < -EPSILON)) {
					crp.setExists(false);
					crp.setS1(0.0f);
					crp.setS2(0.0f);
				} else {
					if(r1 * (u1 - a1) + r2 * (u2 - a2) > EPSILON) {
						crp.setExists(true);
						crp.setS1(u1);
						crp.setS2(u2);
					} else {
						if(r1 * (v1 - a1) + r2 * (v2 - a2) > EPSILON) {
							crp.setExists(true);
							crp.setS1(v1);
							crp.setS2(v2);
						} else {
							crp.setExists(false);
							crp.setS1(0.0f);
							crp.setS2(0.0f);
						}
					}
				}
			}
		}
	}
	
	public static int skySegment(Vector3d rv) {
		int mii;
		Vector3d nv;
		float abst, minabst;
		if ((rv.z < 0) || (isNullVector(rv)))
			return -1;
		
		nv = (Vector3d) rv.clone();
		normalize(nv);
		mii = -1;
		minabst = 3.0f;
		
		for (int i = 0; i < nbSkySegments; i++) {
			abst = abstpp(nv, turtsky[i]);
			if (abst < minabst) {
				minabst = abst;
				mii = i;
			}
		}
		
		return mii;
	}
	
	public static float absthgs(Vector3d ss, Vector3d rr, Vector3d sa, Vector3d se) {
		Vector3d p1;
		Vector3d p2;
		Vector3d u;
		Vector3d v;
		Vector3d temp;
		Vector3d semsa = (Vector3d) se.clone();
		Vector3d ssmsa = (Vector3d) ss.clone();
		float detn, ab1, ab2, ab3, lambda, my;
		semsa.sub(sa);
		if (isNullVector(semsa))
			return abstphg(se, ss, rr);
		ssmsa.sub(sa);
		
		if (parallelVector(semsa, rr)) {
			u = (Vector3d) semsa.clone();
			normalize(u);
			lambda = (float) ssmsa.dot(u);
			u.scale(lambda);
			v = (Vector3d) ssmsa.clone();
			v.sub(u);
			p1 = (Vector3d) se.clone();
			p1.add(v);
			p1.sub(ss);
			p2 = (Vector3d) sa.clone();
			p2.add(v);
			p2.sub(ss);
			if ((rr.dot(p1) < 0.) && (rr.dot(p2) < 0.))
				return (abstps(ss, sa, se));
			return (float) v.length();
		}
		
		v = (Vector3d) rr.clone();
		v.cross(v, semsa);
		normalize(v);
		detn = determinate3(semsa, rr, v);
		if ((detn < EPSILON) && (detn > -EPSILON))
			return 0.0f;
		my = -determinate3(semsa, ssmsa, v)/detn;
		p1 = (Vector3d) rr.clone();
		p1.scale(my);
		p1.add(ss);
		
		temp = (Vector3d) p1.clone();
		temp.sub(ss);
		if (rr.dot(temp) < 0.) {
			ab1 = abstps(ss, sa, se);
			ab2 = abstphg(sa, ss, rr);
			ab3 = abstphg(se, ss, rr);
			if (ab1 < ab2) {
				if (ab1 < ab3)
					return ab1;
				return ab3;
			}
			if (ab2 < ab3)
				return ab2;
			return ab3;
		}
		
		lambda = determinate3(ssmsa, rr, v)/detn;
		p2 = (Vector3d) semsa.clone();
		p2.scale(lambda);
		p2.add(sa);
		temp = (Vector3d) p2.clone();
		temp.sub(sa);
		u = (Vector3d) p2.clone();
		u.sub(se);
		if ((temp.dot(semsa) > 0.) && (u.dot(semsa) < 0.)) {
			p1.sub(p2);
			return (float) p1.length();
		}
		ab1 = abstphg(sa, ss, rr);
		ab2 = abstphg(se, ss, rr);
		if (ab1 < ab2)
			return ab1;
		return ab2;
	}
	
	public static float determinate3(Vector3d a1, Vector3d a2, Vector3d a3) {
		return (float) (a1.x * (a2.y * a3.z - a2.z * a3.y) - a1.y * (a2.x * a3.z - a2.z * a3.x) + a1.z * (a2.x * a3.y - a2.y * a3.x));
	}
	
	public static boolean parallelVector(Vector3d a1, Vector3d a2) {
		normalize(a1);
		normalize(a2);
		a1.cross(a1, a2);
		if (isNullVector(a1))
			return true;
		return false;
	}
	
	public static float abstphg(Vector3d pp, Vector3d ss, Vector3d rr) {
		Vector3d hv;
		Vector3d ppmss = (Vector3d) pp.clone();
		float pss;
		if(isNullVector(rr))
			return 0.0f;
		ppmss.sub(ss);
		if(isNullVector(ppmss))
			return 0.0f;
		pss = (float) rr.dot(ppmss);
		if (pss < 0.)
			return abstpp(ss, pp);
		hv = (Vector3d) rr.clone();
		normalize(hv);
		hv.scale(hv.dot(ppmss));
		ppmss.sub(hv);
		return (float) ppmss.length();
	}
	
	public static void onbco(Vector3d result, Vector3d a, Vector3d ba1, Vector3d ba2, Vector3d ba3) {
		result.x = a.dot(ba1);
		result.y = a.dot(ba2);
		result.z = a.dot(ba3);
	}
	
	public static void getBeginAndEndOfShoot(Matrix4d m, double length, Vector3d beginOfShoot, Vector3d endOfShoot) {
		beginOfShoot.x = m.m03;
		beginOfShoot.y = m.m13;
		beginOfShoot.z = m.m23;
		
		endOfShoot.x = beginOfShoot.x + length * m.m02;
		endOfShoot.y = beginOfShoot.y + length * m.m12;
		endOfShoot.z = beginOfShoot.z + length * m.m22;
	}
	
	public static void getBeginOfShoot(Matrix4d m, Vector3d beginOfShoot) {
		beginOfShoot.x = m.m03;
		beginOfShoot.y = m.m13;
		beginOfShoot.z = m.m23;
	}
	
	public static void getEndOfShoot(Matrix4d m, double length, Vector3d endOfShoot) {
		endOfShoot.x = m.m03 + length * m.m02;
		endOfShoot.y = m.m13 + length * m.m12;
		endOfShoot.z = m.m23 + length * m.m22;
	}

/*!!
#foreach ($type in ["int", "long", "float", "double"])
$pp.setType($type)

	public static $type clamp($type value, $type min, $type max)
	{
		return Math.min(Math.max(value, min), max);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static int clamp(int value, int min, int max)
	{
		return Math.min(Math.max(value, min), max);
	}
// generated
// generated
// generated
	public static long clamp(long value, long min, long max)
	{
		return Math.min(Math.max(value, min), max);
	}
// generated
// generated
// generated
	public static float clamp(float value, float min, float max)
	{
		return Math.min(Math.max(value, min), max);
	}
// generated
// generated
// generated
	public static double clamp(double value, double min, double max)
	{
		return Math.min(Math.max(value, min), max);
	}
// generated
//!! *# End of generated code
	
		
}
