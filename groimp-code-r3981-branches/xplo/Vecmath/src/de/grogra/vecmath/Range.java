/*
 * Copyright (C) 2011 GroIMP Developer Team
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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This class implements interval arithmetic.
 * Each range is an interval [a,b] with a <= b.
 * Some operations for interval arithmetic are implemented.
 * In general those operations expect an additional parameter
 * <code>out</code> to store the result.
 * 
 * For some information on interval arithmetic read:
 * T. Hickey, Q. Ju, and M. H. Van Emden. 2001. Interval arithmetic: From 
 * principles to implementation. J. ACM 48, 5 (September 2001), 1038-1068. 
 * DOI=10.1145/502102.502106 http://doi.acm.org/10.1145/502102.502106 
 * 
 * @author Reinhard Hemmerling
 *
 */
public class Range {
	
	public double a;	// lower bound
	public double b;	// upper bound
	
	public Range()
	{
		a = b = 0;
	}
	
	public Range(double d)
	{
		a = b = d;
	}
	
	public Range(double min, double max)
	{
		a = min;
		b = max;
	}
	
	public Range(Range r)
	{
		a = r.a;
		b = r.b;
	}
	
	public String toString()
	{
		return "[" + a + ", " + b + "]";
	}
	
	public void set(double d)
	{
		a = b = d;
	}

	public void set(double min, double max)
	{
		a = min;
		b = max;
	}
	
	public void set(Range r) {
		a = r.a;
		b = r.b;
	}
	
	// true if x is in the interval [a, b]
	public boolean contains(double x)
	{
		return a <= x && x <= b;
	}

	/**
	 * Returns true if the supplied Range is fully contained within this Range. 
	 * Fully contained is defined as having the minimum and maximum values of the fully contained range 
	 * lie within the range of values of the containing Range.
	 * 
	 * @param x range to check
	 * @return 
	 */
	public boolean contains(Range x)
	{
		return a <= x.a && x.b <= b;
	}
	
	// out = x + y
	public static final Range add(Range out, Range x, Range y)
	{
		out.set(x.a + y.a, x.b + y.b);
		return out;
	}
	
	// out = x - y
	public static final Range sub(Range out, Range x, Range y)
	{
		out.set(x.a - y.b, x.b - y.a);
		return out;
	}
	
	// out = x * y
	public static final Range mul(Range out, Range x, Range y)
	{
		out.set(
				min(min(x.a*y.a, x.a*y.b), min(x.b*y.a, x.b*y.b)),
				max(max(x.a*y.a, x.a*y.b), max(x.b*y.a, x.b*y.b))
		);
		return out;
	}

	// out = x / y
	public static final Range div(Range out, Range x, Range y)
	{
/*
		if (y.a > 0 || y.b < 0) {
			out.set(1 / y.b, 1 / y.a);
			mul(out, x, out);
		} else {
			// handle division by zero
			out.set(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
*/
		double a = x.a;
		double b = x.b;
		double c = y.a;
		double d = y.b;
		if (c == 0 && d == 0) {
			out.set(Double.NaN);
		} else if (a == 0 && b == 0) {
			out.set(0);
		} else if (c >= 0 && d > 0) {		// y is P
			if (a > 0 && b > 0) {				// x is P1
				out.set(a/d, c == 0 ? POSITIVE_INFINITY : b/c);
			} else if (a == 0 && b > 0) {		// x is P0
				out.set(0, c == 0 ? POSITIVE_INFINITY : b/c);
			} else if (a < 0 && b > 0) {		// x is M
				if (c == 0) {
					out.set(NEGATIVE_INFINITY, POSITIVE_INFINITY);
				} else {
					out.set(a/c, b/c);
				}
			} else if (a < 0 && b == 0) {		// x is N0
				out.set(c == 0 ? NEGATIVE_INFINITY : a/c, 0);
			} else if (a < 0 && b < 0) {		// x is N1
				out.set(c == 0 ? NEGATIVE_INFINITY : a/c, b/d);
			} else {
//				throw new ArithmeticException("division error: " + x + "/" + y);
			}
		} else if (c < 0 && d > 0) {		// y is M
			out.set(NEGATIVE_INFINITY, POSITIVE_INFINITY);
		} else if (c < 0 && d <= 0) {		// y is N
			if (a > 0 && b > 0) {				// x is P1
				out.set(d == 0 ? NEGATIVE_INFINITY : b/d, a/c);
			} else if (a == 0 && b > 0) {		// x is P0
				out.set(d == 0 ? NEGATIVE_INFINITY : b/d, 0);
			} else if (a < 0 && b > 0) {		// x is M
				if (d == 0) {
					out.set(NEGATIVE_INFINITY, POSITIVE_INFINITY);
				} else {
					out.set(b/d, a/d);
				}
			} else if (a < 0 && b == 0) {		// x is N0
				out.set(0, d == 0 ? POSITIVE_INFINITY : a/d);
			} else if (a < 0 && b < 0) {		// x is N1
				out.set(b/c, d == 0 ? POSITIVE_INFINITY : a/d);
			} else {
//				throw new ArithmeticException("division error: " + x + "/" + y);
			}
		} else {
//			throw new ArithmeticException("division error: " + x + "/" + y);
		}
		return out;
	}

	// out = sqr(x)
	public static final Range sqr(Range out, Range x)
	{
		if (x.a >= 0) 
			out.set(x.a*x.a, x.b*x.b);
		else if (x.b < 0)
			out.set(x.b*x.b, x.a*x.a);
		else	
			out.set(0, Math.max(x.a*x.a, x.b*x.b));
		return out;
	}
	
	// out = sqrt(x)
	public static final Range sqrt(Range out, Range x)
	{
		out.set(Math.sqrt(x.a), Math.sqrt(x.b));
		return out;
	}

	// out = pow(x, e)
	public static final Range pow(Range out, Range x, double e)
	{
		// TODO handle all special cases; for now x must be positive
		double c = Math.pow(x.a, e);
		double d = Math.pow(x.b, e);
		out.set(Math.min(c, d), Math.max(c, d));
		return out;
	}
	
	// out = abs(x)
	public static final Range abs(Range out, Range x)
	{
		if (x.b <= 0) {
			out.set(Math.abs(x.b), Math.abs(x.a)); 
		} 
		else if (x.a < 0) {
			out.set(0, Math.max(Math.abs(x.a), x.b));
		} else {
			out.set(x);
		}
		return out;
	}
	
	// out = cos(x), x in radians
	public static final Range cos(Range out, Range x)
	{
		// make copy of input values
		double xa = x.a;
		double xb = x.b;
		// calculate cosine for interval bounds
		double ca = Math.cos(xa);
		double cb = Math.cos(xb);
		out.set(min(ca, cb), max(ca, cb));
		// check if maximum of cosine curve is inside interval
		if (ceil(xa / 2 / PI) < (xb / 2 / PI))
			out.b = 1;
		// check if minimum of cosine curve is inside interval
		if (ceil(xa / 2 / PI - 0.5) <= (xb / 2 / PI - 0.5))
			out.a = -1;
		return out;
	}

	// out = sin(x) = cos(x - PI/2), x in radians
	public static final Range sin(Range out, Range x)
	{
		out.a = x.a - PI/2;
		out.b = x.b - PI/2;
		return cos(out, out);
	}

	// out = tan(x), x in radians
	public static final Range tan(Range out, Range x)
	{
		double ta = Math.tan(x.a);
		double tb = Math.tan(x.b);
		out.set(min(ta, tb), max(ta, tb));
		// check if (PI/2 + k*PI) is inside interval
		if (floor(x.a / PI - 0.5) <= (x.b / PI - 0.5)) {
			out.a = Double.NEGATIVE_INFINITY;
			out.b = Double.POSITIVE_INFINITY;
		}
		return cos(out, out);
	}

	// out = acos(x), out in radians
	public static final Range acos(Range out, Range x)
	{
		// TODO handle cases when |x| > 1
		double aca = Math.acos(x.a);
		double acb = Math.acos(x.b);
		out.set(min(aca, acb), max(aca, acb));
		return out;
	}

	// out = asin(x), out in radians
	public static final Range asin(Range out, Range x)
	{
		// TODO handle cases when |x| > 1
		double asa = Math.asin(x.a);
		double asb = Math.asin(x.b);
		out.set(min(asa, asb), max(asa, asb));
		return out;
	}

	// out = atan(x), out in radians
	public static final Range atan(Range out, Range x)
	{
		double ata = Math.atan(x.a);
		double atb = Math.atan(x.b);
		out.set(min(ata, atb), max(ata, atb));
		return out;
	}

	// out = atan2(y, x), out in radians
	public static final Range atan2(Range out, Range y, Range x)
	{
		double ataa = Math.atan2(y.a, x.a);
		double atab = Math.atan2(y.a, x.b);
		double atba = Math.atan2(y.b, x.a);
		double atbb = Math.atan2(y.b, x.b);

		if (x.a == 0 && x.b == 0) {	// check for vertical line
			if (y.b < 0) {			// check if completely negative
				out.set(-PI/2);
			} else if (y.a > 0) {	// check if completely positive
				out.set(PI/2);
			} else {				// crosses origin
				// actually only the two angles are needed
				out.set(-PI/2, PI/2);	
			}
		} else if (y.a == 0 && y.b == 0) {	// check for horizontal line
			if (x.b < 0) {
				out.set(PI);
			} else if (x.a > 0) {
				out.set(0);
			} else {
				out.set(0, PI);
			}
		} else if (x.a < 0 && x.b > 0 && y.a < 0 && y.b > 0) {
			out.set(-PI, PI);
		} else if (x.a < 0 && x.b == 0 && y.a < 0 && y.b > 0) {
			out.set(PI/2, 3*PI/2);
		} else if (x.a == 0 && x.b > 0 && y.a < 0 && y.b > 0) {
			out.set(-PI/2, PI/2);
		} else if (x.a < 0 && x.b > 0 && y.a < 0 && y.b == 0) {
			out.set(-PI, 0);
		} else if (x.a < 0 && x.b > 0 && y.a == 0 && y.b > 0) {
			out.set(0, PI);
		} else if (x.a < 0 && x.b == 0 && y.a < 0 && y.b == 0) {
			out.set(-PI, -PI/2);
		} else if (x.a == 0 && x.b > 0 && y.a == 0 && y.b > 0) {
			out.set(0, PI/2);
		} else if (x.a < 0 && x.b == 0 && y.a == 0 && y.b > 0) {
			out.set(PI/2, PI);
		} else if (x.a == 0 && x.b > 0 && y.a < 0 && y.b == 0) {
			out.set(-PI/2, 0);
		} else if (x.b < 0 && y.contains(0)) {
			if (y.b == 0) {
				out.set(-PI, atab);
			} else if (y.a == 0) {
				out.set(atbb, PI);
			} else {
				out.set(atbb, atab + 2*PI);
			}
		} else {
			out.set(
					min(min(ataa, atab), min(atba, atbb)),
					max(max(ataa, atab), max(atba, atbb))
			);
		}
		
		return out;
	}

}
