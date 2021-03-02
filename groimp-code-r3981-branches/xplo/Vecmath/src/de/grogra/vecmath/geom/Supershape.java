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

package de.grogra.vecmath.geom;

import static de.grogra.vecmath.Range.abs;
import static de.grogra.vecmath.Range.add;
import static de.grogra.vecmath.Range.atan;
import static de.grogra.vecmath.Range.atan2;
import static de.grogra.vecmath.Range.cos;
import static de.grogra.vecmath.Range.div;
import static de.grogra.vecmath.Range.mul;
import static de.grogra.vecmath.Range.pow;
import static de.grogra.vecmath.Range.sin;
import static de.grogra.vecmath.Range.sqr;
import static de.grogra.vecmath.Range.sqrt;
import static de.grogra.vecmath.Range.sub;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

import javax.vecmath.Tuple3d;

import de.grogra.vecmath.Range;

 
/**
 * This class represents the geometry of a supershape. In local object
 * coordinates, its center is the origin, its radius is 1.
 *
 * An implementation of Johan Gielis's Superformula which was published in the
 * American Journal of Botany 90(3): 333–338. 2003.
 * INVITED SPECIAL PAPER A GENERIC GEOMETRIC TRANSFORMATION 
 * THAT UNIFIES A WIDE RANGE OF NATURAL AND ABSTRACT SHAPES
 * 
 * @author MH
 */
public class Supershape extends ImplicitVolume {

	protected double a;
	protected double b; 
	protected double m1;
	protected double n11; 
	protected double n12;
	protected double n13;
	protected double m2;
	protected double n21; 
	protected double n22;
	protected double n23;

	public Supershape(float a, float b, float m1, float n11, float n12,
			float n13, float m2, float n21, float n22, float n23) {
		this.a = a;
		this.b = b;
		this.m1 = m1;
		this.n11 = n11;
		this.n12 = n12;
		this.n13 = n13;
		this.m2 = m2;
		this.n21 = n21;
		this.n22 = n22;
		this.n23 = n23;
	}

	/**
	 * implicit function of the supershape 
	 *      
	 * @param x, y, z 
	 * @return 	>0 outside, =0 surface, <0 inside
	 */
	protected Range f(Range x, Range y, Range z) {
		Locals l = locals.get();
		atan2(l.r0, y, x);							// r0 = theta
//		r1(l.r1, l.r0);								// r1 = r1(theta)
//		sample_r1(l.r1, l.r0, (int)m1/2);
		sample_r1(l.r1, l.r0, 8);
		sqr(l.r2, l.r1);							// r2 = r1^2
		add(l.r3, sqr(l.tr0, x), sqr(l.tr1, y));	// r3 = x*x + y*y
		atan(l.r4, 									// r4 = phi
			div(l.tr0, 								// tr0 = z / sqrt(x*x+y*y)
				z, 
				sqrt(l.tr1,	l.r3)
			)
		);
//		r2(l.r5, l.r4);								// r5 = r2(phi)
//		sample_r2(l.r5, l.r4, (int)m2/2);
		sample_r2(l.r5, l.r4, 8);
		sqr(l.r6, l.r5);							// r6 = r5^2
		l.r7.set(1);								// r7 = 1
		
		sub(l.r0, 
			div(l.tr0,
				add(l.tr1,
					l.r3, 
					mul(l.tr5,						// tr5 = r1(theta)^2 * z^2
						l.r2,
						sqr(l.tr6, z)
					)
				),
				mul(l.tr7,							// tr7 = r1(theta)^2 * r2(phi)^2
					l.r2,
					l.r6
				)
			),
			l.r7
		);
		
		return l.r0;
	}

	// implement the same implicit function as above, but for values instead of ranges
	protected double f(double x, double y, double z) {
		double theta = atan2(y, x);
		double r1 = r1(theta);
		double r1_2 = r1*r1;
		double phi = atan(z / sqrt(x*x + y*y));
		double r2 = r2(phi);
		double r2_2 = r2*r2;
    	return (x*x + y*y + r1_2*z*z) / ( r1_2 * r2_2) - 1;
	}



	// define a thread-local variable to access helper variables per thread
	protected final ThreadLocal<Locals> locals2 = new ThreadLocal<Locals>() {
		protected Locals initialValue() { return new Locals(); }
	};
	
	// divide the theta range into sampleCount equal subintervals
	// calculate the range of radii for these subintervals and merge
	// the resulting ranges
	// effectively a closer fit for the radius is calculated this way
	private Range sample_r1(Range out, Range theta, int sampleCount) {
		double a = Double.POSITIVE_INFINITY;
		double b = Double.NEGATIVE_INFINITY;
		Locals l = locals2.get();
		l.r0.set(this.m1/4);							// r0 = m/4
		mul(l.r1, l.r0, theta);							// r1 = theta*m/4
		double ta = l.r1.a;
		double tb = Math.min(l.r1.b, l.r1.a + 2*PI);
		for (int i = 0; i < sampleCount; i++) {
			double theta0 = (tb - ta) * (i+0) / sampleCount + ta;
			double theta1 = (tb - ta) * (i+1) / sampleCount + ta;
			l.r1.set(theta0, theta1);
			l.r4.set(this.a);	
			abs(l.r2, div(l.tr1, cos(l.tr0, l.r1), l.r4));	// r2 = |cos(theta*m/4)/a|
			l.r4.set(this.b);
			abs(l.r3, div(l.tr1, sin(l.tr0, l.r1), l.r4));	// r3 = |sin(theta*m/4)/b|
			pow(l.r4, l.r2, this.n12);						// r4 = r2^n2
			pow(l.r5, l.r3, this.n13);						// r5 = r3^n3
			add(l.r6, l.r4, l.r5);							// r6 = r4 + r5
			pow(out, l.r6, -1/this.n11);					// out = r6^(-1/n1)
			a = Math.min(a, out.a);
			b = Math.max(b, out.b);
		}
		out.set(a, b);
		return out;
	}

	private Range sample_r2(Range out, Range phi, int sampleCount) {
		double a = Double.POSITIVE_INFINITY;
		double b = Double.NEGATIVE_INFINITY;
		Locals l = locals2.get();
		l.r0.set(this.m2/4);							// r0 = m/4
		mul(l.r1, l.r0, phi);							// r1 = phi*m/4
		double pa = l.r1.a;
		double pb = Math.min(l.r1.b, l.r1.a + 2*PI);
		for (int i = 0; i < sampleCount; i++) {
			double phi0 = (pb - pa) * (i+0) / sampleCount + pa;
			double phi1 = (pb - pa) * (i+1) / sampleCount + pa;
			l.r1.set(phi0, phi1);
			l.r4.set(this.a);	
			abs(l.r2, div(l.tr1, cos(l.tr0, l.r1), l.r4));	// r2 = |cos(phi*m/4)/a|
			l.r4.set(this.b);
			abs(l.r3, div(l.tr1, sin(l.tr0, l.r1), l.r4));	// r3 = |sin(phi*m/4)/b|
			pow(l.r4, l.r2, this.n22);						// r4 = r2^n2
			pow(l.r5, l.r3, this.n23);						// r5 = r3^n3
			add(l.r6, l.r4, l.r5);							// r6 = r4 + r5
			pow(out, l.r6, -1/this.n21);					// out = r6^(-1/n1)
			a = Math.min(a, out.a);
			b = Math.max(b, out.b);
		}
		out.set(a, b);
		return out;
	}

	private Range r1(Range out, Range theta) {
		Locals l = locals2.get();
		l.r0.set(m1/4);									// r0 = m/4
		mul(l.r1, l.r0, theta);							// r1 = theta*m/4
		l.r4.set(a);	
		abs(l.r2, div(l.tr1, cos(l.tr0, l.r1), l.r4));	// r2 = |cos(theta*m/4)/a|
		l.r4.set(b);
		abs(l.r3, div(l.tr1, sin(l.tr0, l.r1), l.r4));	// r3 = |sin(theta*m/4)/b|
		pow(l.r4, l.r2, n12);							// r4 = r2^n2
		pow(l.r5, l.r3, n13);							// r5 = r3^n3
		add(l.r6, l.r4, l.r5);							// r6 = r4 + r5
		pow(out, l.r6, -1/n11);							// out = r6^(-1/n1)
		return out;
	}

	private Range r2(Range out, Range phi) {
		Locals l = locals2.get();
		l.r0.set(m2/4);									// r0 = m/4
		mul(l.r1, l.r0, phi);							// r1 = phi*m/4
		l.r4.set(a);	
		abs(l.r2, div(l.tr1, cos(l.tr0, l.r1), l.r4));	// r2 = |cos(phi*m/4)/a|
		l.r4.set(b);
		abs(l.r3, div(l.tr1, sin(l.tr0, l.r1), l.r4));	// r3 = |sin(phi*m/4)/b|
		pow(l.r4, l.r2, n22);							// r4 = r2^n2
		pow(l.r5, l.r3, n23);							// r5 = r3^n3
		add(l.r6, l.r4, l.r5);							// r6 = r4 + r5
		pow(out, l.r6, -1/n21);							// out = r6^(-1/n1)
		return out;
	}

	private double r1(double theta) {
		double raux1 = Math.pow(Math.abs(Math.cos(m1*theta/4)/a ), n12) + 
				  	   Math.pow(Math.abs(Math.sin(m1*theta/4)/b ), n13);
		return Math.pow(raux1, -1/n11);
	}

	private double r2(double phi) {
		double raux2 = Math.pow(Math.abs(Math.cos(m2*phi/4)/a ), n22) + 
				  	   Math.pow(Math.abs(Math.sin(m2*phi/4)/b ), n23);
		return Math.pow(raux2, -1/n21);
	}
	
	public void getExtent(Tuple3d min, Tuple3d max, Variables temp) {

		// init AABB so that it can be shrinked
		min.set(Double.POSITIVE_INFINITY, 
				Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		max.set(Double.NEGATIVE_INFINITY, 
				Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY);

		final int NUM_THETA_SEGMENTS = 32;
		final int NUM_PHI_SEGMENTS = 32;
		Locals l = locals.get();

		// sample intervals of phi and theta
		for (int i = 0; i < NUM_THETA_SEGMENTS; i++) {
			double theta0 = 2 * PI * (i+0) / NUM_THETA_SEGMENTS - PI;
			double theta1 = 2 * PI * (i+1) / NUM_THETA_SEGMENTS - PI;
			l.r0.set(theta0, theta1);			// theta
			r1(l.r2, l.r0);						// r1(theta)
			for (int j = 0; j < NUM_PHI_SEGMENTS; j++) {
				double phi0 = PI * (j+0) / NUM_PHI_SEGMENTS - PI/2;
				double phi1 = PI * (j+1) / NUM_PHI_SEGMENTS - PI/2;
				l.r1.set(phi0, phi1);			// phi
				r2(l.r3, l.r1);					// r2(phi)
				
				// compute bounds of AABB
				abs(l.r4, mul(l.tr0, l.r2, l.r3));
				abs(l.r5, l.r3);
				abs(l.x, mul(l.tr0, mul(l.tr1, cos(l.tr2, l.r0), cos(l.tr3, l.r1)), l.r4));
				abs(l.y, mul(l.tr0, mul(l.tr1, sin(l.tr2, l.r0), cos(l.tr3, l.r1)), l.r4));
				abs(l.z, mul(l.tr0, l.r3, sin(l.tr1, l.r1)));
				min.x = Math.min(min.x, -l.x.b);
				min.y = Math.min(min.y, -l.y.b);
				min.z = Math.min(min.z, -l.z.b);
				max.x = Math.max(max.x, +l.x.b);
				max.y = Math.max(max.y, +l.y.b);
				max.z = Math.max(max.z, +l.z.b);
			}
		}

		// extend the AABB slightly to prevent numerical issues
		min.scale(1.0001);
		max.scale(1.0001);
		
//		System.out.println("min = " + min + "   max = " + max);
	}
}
