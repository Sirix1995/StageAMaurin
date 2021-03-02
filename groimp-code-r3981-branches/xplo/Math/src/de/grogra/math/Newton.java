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

import de.grogra.pf.data.Datacell;
import de.grogra.pf.data.Dataset;
import de.grogra.xl.lang.ObjectToDouble;
import de.grogra.xl.lang.ObjectToObject;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * This class contains some numeric algorithms based on the Newton
 * algorithm.
 * 
 * @author Ole Kniemeyer
 */
public class Newton
{
	/**
	 * Finds a root of <code>function</code>. Starting at <code>x</code>,
	 * the derivative of <code>function</code> is approximated by difference
	 * quotients (using <code>eps</code> as delta) to compute a new
	 * <code>x</code> according to Newton's method, using <code>relax</code>
	 * as factor which reduces the <code>x</code>-movement computed by
	 * Newton's method. If the movement is less than <code>prec</code> in all
	 * dimensions, this method returns normally. Otherwise, after
	 * <code>maxSteps</code> an <code>ArithmeticException</code> is thrown. 
	 * <p>
	 * All arrays have to be of the same length.
	 * 
	 * @param function the function
	 * @param x the start value and (after method invocation completes normally)
	 * the final value
	 * @param prec the precision to achieve
	 * @param maxSteps maximal number of iterations
	 * @param eps the delta to approximate differential quotients
	 * @param relax relaxation factor for step
	 */
	public static void findRoot (ObjectToObject<double[], double[]> function,
			double[] x, double[] prec, int maxSteps, double[] eps, double relax)
	{
		final int n = x.length;
		double[][] diff = new double[n][n];

		int step = 0;
		boolean ok;
		do
		{
			step++;
			if (step > maxSteps)
			{
				throw new ArithmeticException ("Maximal number of steps exceeded.");
			}

			double[] y = function.evaluateObject (x);
			for (int i = 0; i < n; i++)
			{
				double t = x[i];
				x[i] += eps[i];
				double[] y2 = function.evaluateObject (x);
				for (int j = 0; j < n; j++)
				{
					diff[j][i] = (y2[j] - y[j]) / eps[i];
				}
				x[i] = t;
			}
			invMul (diff, y);
			ok = true;
			for (int i = 0; i < n; i++)
			{
				double t = y[i];
				if (Math.abs (t) > prec[i])
				{
					ok = false;
				}
				x[i] -= relax * t;
			}
		}
		while (!ok);
	}

	/**
	 * Finds a local extremum by looking for a root of an
	 * approximation of the derivative of <code>function</code>.
	 * The approximation is obtained by difference quotients, the
	 * delta being given by <code>eps</code>.
	 * 
	 * @param function the function
	 * @param x the start value and (after method invocation completes normally)
	 * the final value
	 * @param prec the precision to achieve
	 * @param maxSteps maximal number of iterations
	 * @param eps the delta to approximate differential quotients
	 * @param relax relaxation factor for step
	 * 
	 * @see #findRoot
	 */
	public static void findExtremum (final ObjectToDouble<double[]> function, double[] x, double[] prec, int maxSteps, final double[] eps, double relax)
	{
		final int n = x.length;

		ObjectToObject<double[], double[]> f = new ObjectToObject<double[], double[]> ()
		{
			public double[] evaluateObject (double[] x)
			{
				double[] diff = new double[n];
				double y = function.evaluateDouble (x);
				for (int i = 0; i < n; i++)
				{
					double t = x[i];
					x[i] += eps[i];
					double y2 = function.evaluateDouble (x);
					diff[i] = (y2 - y) / eps[i];
					x[i] = t;
				}
				return diff;
			}
		};
		findRoot (f, x, prec, maxSteps, eps, relax);
	}

	/**
	 * Fits <code>function</code> to <code>data</code>. <code>function</code>
	 * gets an array as input whose first component contains the
	 * <code>x</code>-vector and whose second component contains the
	 * parameter vector. <code>data</code> contains for each row a data set
	 * consisting of the <code>x</code>-vector and (as last entry)
	 * the function value.
	 * 
	 * @param function the function
	 * @param data the data table
	 * @param params the start value for the parameter vector and
	 * (after method invocation completes normally) the final value
	 * @param prec the precision to achieve
	 * @param maxSteps maximal number of iterations
	 * @param eps the delta to approximate differential quotients
	 * @param relax relaxation factor for step
	 *
	 * @see #findExtremum
	 * @see #findRoot
	 */
	public static void fit (final ObjectToDouble<double[][]> function, final DataTable data, final double[] params, double[] prec, int maxSteps, double[] eps, double relax)
	{
		ObjectToDouble<double[]> f = new ObjectToDouble<double[]> ()
		{
			final int rows = data.getRowCount ();
			final int n = data.getColumnCount () - 1;
			final double[] x = new double[n];
			final double[][] xp = {x, null};

			public double evaluateDouble (double[] params)
			{
				double sum = 0;
				xp[1] = params;
				for (int row = 0; row < rows; row++)
				{
					for (int i = 0; i < n; i++)
					{
						x[i] = data.getValue (row, i);
					}
					double d = function.evaluateDouble (xp) - data.getValue (row, n);
					sum += d * d;
				}
				return sum;
			}
		};
		findExtremum (f, params, prec, maxSteps, eps, relax);
	}

	public static void fit (ObjectToDouble<double[][]> function, DataTable data, double[] params, double[] prec)
	{
		fit (function, data, params, prec, 100, prec, 0.3);
	}

	public static void fitParameters (final ObjectToDouble<double[]> function, DataTable data, final double[] params, double[] prec, int maxSteps, double[] eps, double relax)
	{
		ObjectToDouble<double[][]> f = new ObjectToDouble<double[][]> ()
		{
			public double evaluateDouble (double[][] x)
			{
				if (x[1] != params)
				{
					DoubleList.arraycopy (x[1], 0, params, 0, x[1].length);
				}
				return function.evaluateDouble (x[0]);
			}
		};
		fit (f, data, params, prec, maxSteps, eps, relax);
	}

	public static void fitParameters (ObjectToDouble<double[]> function, DataTable data, double[] params, double[] prec)
	{
		fitParameters (function, data, params, prec, 100, prec, 0.3);
	}
	
	public static DataTable toDataTable (final Dataset set)
	{
		return new DataTable ()
		{
			final int[] colStart;
			final int rowCount;

			{
				if ((getRowCount0 () > 0) && (getColumnCount0 () > 1)
					&& !getCell0 (0, 0).isScalar ())
				{
					int n1 = getColumnCount0 () - 1;
					colStart = new int[n1 + 1];
					for (int i = 0; i < n1; i++)
					{
						colStart[i + 1] = colStart[i] + getRowCount0 (i);
					}
					rowCount = colStart[n1] + getRowCount0 (n1); 
				}
				else
				{
					colStart = null;
					rowCount = 0;
				}
			}

			private int getColumnCount0 ()
			{
				return set.hasSeriesInRows () ? set.getRowCount () : set.getColumnCount ();
			}

			private int getRowCount0 ()
			{
				return set.hasSeriesInRows () ? set.getColumnCount () : set.getRowCount ();
			}

			private int getRowCount0 (int c)
			{
				return set.hasSeriesInRows () ? set.getColumnCount (c) : set.getRowCount (c);
			}

			private Datacell getCell0 (int r, int c)
			{
				return set.hasSeriesInRows () ? set.getCell (c, r) : set.getCell (r, c);
			}

			public int getColumnCount ()
			{
				if (colStart != null)
				{
					return 2;
				}
				int c = getColumnCount0 ();
				return (c == 1) ? 2 : c;
			}

			public int getRowCount ()
			{
				return (colStart != null) ? rowCount : getRowCount0 ();
			}

			public double getValue (int row, int column)
			{
				Datacell c;
				if (colStart != null)
				{
					c = null;
					for (int i = colStart.length - 1; i >= 0; i--)
					{
						if (row >= colStart[i])
						{
							c = getCell0 (row - colStart[i], i);
							break;
						}
					}
				}
				else
				{
					if (getColumnCount0 () > 1)
					{
						return getCell0 (row, column).getY ();
					}
					c = getCell0 (row, 0);
				}
				return (column == 0) ? c.getX () : c.getY ();
			}
		};
	}

	/**
	 * Computes m<sup>-1</sup> v and writes the result to
	 * <code>v</code>.
	 * 
	 * @param m matrix (gets overwritten by this method)
	 * @param v vector (input and output)
	 */
	private static void invMul (double[][] m, double[] v)
	{
		int i, j, k;
		double a, d, max;
		int n = v.length;

		for (j = 0; j < n; j++)
		{
			k = -1;
			max = 0.0f;
			for (i = j; i < n; i++)
			{
				d = Math.abs (m[i][j]);
				if (d > max)
				{
					k = i;
					max = d;
				}
			}
			if (k < 0)
				throw new ArithmeticException ();
			if (k != j)
			{
				d = v[j];
				v[j] = v[k];
				v[k] = d;
				for (i = j; i < n; i++)
				{
					d = m[j][i];
					m[j][i] = m[k][i];
					m[k][i] = d;
				}
			}
			d = 1.0 / m[j][j];
			for (i = j + 1; i < n; i++)
			{
				a = m[i][j] * d;
				m[i][j] = 0.0f;
				for (k = j + 1; k < n; k++)
					m[i][k] -= a * m[j][k];
				v[i] -= a * v[j];
			}
		}
		for (j = n - 1; j >= 0; j--)
		{
			d = v[j];
			for (i = j + 1; i < n; i++)
				d -= m[j][i] * v[i];
			v[j] = d / m[j][j];
		}
	}

}
