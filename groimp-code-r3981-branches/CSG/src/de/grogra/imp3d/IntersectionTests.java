
package de.grogra.imp3d;

import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class IntersectionTests
{

	static final float EPSILON = 0.00001f;
	static final double EPSILON_D = 0.0000000001d;

	/**
	 * returns the DOT of v1 and v2
	 */

	public static float DOT (Tuple3f v1, Tuple3f v2)
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;

	}
	
	public static double DOT (Tuple3d v1, Tuple3d v2)
	{
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;

	}

	/**
	 * returns the Cross of v1 and v2
	 */

	public static Tuple3f CROSS (Tuple3f v1, Tuple3f v2)
	{
		Tuple3f out = new Point3f ();
		out.x = v1.y * v2.z - v1.z * v2.y;
		out.y = v1.z * v2.x - v1.x * v2.z;
		out.z = v1.x * v2.y - v1.y * v2.x;
		return out;
	}
	
	public static Tuple3d CROSS (Tuple3d v1, Tuple3d v2)
	{
		Tuple3d out = new Point3d ();
		out.x = v1.y * v2.z - v1.z * v2.y;
		out.y = v1.z * v2.x - v1.x * v2.z;
		out.z = v1.x * v2.y - v1.y * v2.x;
		return out;
	}

	public static int SORT2 (Tuple2f ab)
	{
		int smallest;
		if (ab.x > ab.y)
		{
			float c = ab.x;
			ab.x = ab.y;
			ab.y = c;
			smallest = 1;
		}
		else
			smallest = 0;
		return smallest;
	}
	
	public static int SORT2 (Tuple2d ab)
	{
		int smallest;
		if (ab.x > ab.y)
		{
			double c = ab.x;
			ab.x = ab.y;
			ab.y = c;
			smallest = 1;
		}
		else
			smallest = 0;
		return smallest;
	}

	public static Tuple3f SUB (Tuple3f v1, Tuple3f v2)
	{
		Tuple3f out = new Point3f ();
		out.x = v1.x - v2.x;
		out.y = v1.y - v2.y;
		out.z = v1.z - v2.z;
		return out;
	}

	public static Tuple3d SUB (Tuple3d v1, Tuple3d v2)
	{
		Tuple3d out = new Point3d ();
		out.x = v1.x - v2.x;
		out.y = v1.y - v2.y;
		out.z = v1.z - v2.z;
		return out;
	}
	
	public static double ABS(Tuple3d v1){
		double out=v1.x*v1.x;
		out +=v1.y*v1.y;
		out +=v1.z*v1.z;
		
		return Math.sqrt (out); 
	}
	
	public static int compute_intervals_isectline (Tuple3f VERT0,
			Tuple3f VERT1, Tuple3f VERT2, float VV0, float VV1, float VV2,
			float D0, float D1, float D2, float D0D1, float D0D2,
			Tuple2f isect, Tuple3f isectpoint0, Tuple3f isectpoint1)
	{
		if (D0D1 > 0.0f)
		{
			/* here we know that D0D2<=0.0 */
			/*
			 * that is D0, D1 are on the same side, D2 on the other or on the
			 * plane
			 */
			isect2 (VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect,
				isectpoint0, isectpoint1);
		}
		else if (D0D2 > 0.0f)
		{
			/* here we know that d0d1<=0.0 */
			isect2 (VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect,
				isectpoint0, isectpoint1);
		}
		else if (D1 * D2 > 0.0f || D0 != 0.0f)
		{
			/* here we know that d0d1<=0.0 or that D0!=0.0 */
			isect2 (VERT0, VERT1, VERT2, VV0, VV1, VV2, D0, D1, D2, isect,
				isectpoint0, isectpoint1);
		}
		else if (D1 != 0.0f)
		{
			isect2 (VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect,
				isectpoint0, isectpoint1);
			// isect2(VERT1,VERT0,VERT2,VV1,VV0,VV2,D1,D0,D2,isect0,isect1,isectpoint0,isectpoint1);
		}
		else if (D2 != 0.0f)
		{
			isect2 (VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect,
				isectpoint0, isectpoint1);
			// isect2(VERT2,VERT0,VERT1,VV2,VV0,VV1,D2,D0,D1,isect0,isect1,isectpoint0,isectpoint1);
		}
		else
		{
			/* triangles are coplanar */
			return 1;
		}
		return 0;
		// return null;
	}
	
	public static int compute_intervals_isectline (Tuple3d VERT0,
			Tuple3d VERT1, Tuple3d VERT2, double VV0, double VV1, double VV2,
			double D0, double D1, double D2, double D0D1, double D0D2,
			Tuple2d isect, Tuple3d isectpoint0, Tuple3d isectpoint1)
	{
		if (D0D1 > 0.0f)
		{
			/* here we know that D0D2<=0.0 */
			/*
			 * that is D0, D1 are on the same side, D2 on the other or on the
			 * plane
			 */
			isect2 (VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect,
				isectpoint0, isectpoint1);
		}
		else if (D0D2 > 0.0f)
		{
			/* here we know that d0d1<=0.0 */
			isect2 (VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect,
				isectpoint0, isectpoint1);
		}
		else if (D1 * D2 > 0.0f || D0 != 0.0f)
		{
			/* here we know that d0d1<=0.0 or that D0!=0.0 */
			isect2 (VERT0, VERT1, VERT2, VV0, VV1, VV2, D0, D1, D2, isect,
				isectpoint0, isectpoint1);
		}
		else if (D1 != 0.0f)
		{
			isect2 (VERT1, VERT0, VERT2, VV1, VV0, VV2, D1, D0, D2, isect,
				isectpoint0, isectpoint1);
			// isect2(VERT1,VERT0,VERT2,VV1,VV0,VV2,D1,D0,D2,isect0,isect1,isectpoint0,isectpoint1);
		}
		else if (D2 != 0.0f)
		{
			isect2 (VERT2, VERT0, VERT1, VV2, VV0, VV1, D2, D0, D1, isect,
				isectpoint0, isectpoint1);
			// isect2(VERT2,VERT0,VERT1,VV2,VV0,VV1,D2,D0,D1,isect0,isect1,isectpoint0,isectpoint1);
		}
		else
		{
			/* triangles are coplanar */
			return 1;
		}
		return 0;
		// return null;
	}

	public static void isect2 (Tuple3f VTX0, Tuple3f VTX1, Tuple3f VTX2,
			float VV0, float VV1, float VV2, float D0, float D1, float D2,
			Tuple2f isect, Tuple3f isectpoint0, Tuple3f isectpoint1)
	{
		float tmp = D0 / (D0 - D1);
		Tuple3f diff;
		isect.x = VV0 + (VV1 - VV0) * tmp;
		diff = SUB (VTX1, VTX0);
		diff.scale (tmp);
		isectpoint0.add (diff, VTX0);
		tmp = D0 / (D0 - D2);
		isect.y = VV0 + (VV2 - VV0) * tmp;
		diff = SUB (VTX2, VTX0);
		diff.scale (tmp);
		isectpoint1.add (VTX0, diff);
	}

	public static void isect2 (Tuple3d VTX0, Tuple3d VTX1, Tuple3d VTX2,
			double VV0, double VV1, double VV2, double D0, double D1, double D2,
			Tuple2d isect, Tuple3d isectpoint0, Tuple3d isectpoint1)
	{
		double tmp = D0 / (D0 - D1);
		Tuple3d diff;
		isect.x = VV0 + (VV1 - VV0) * tmp;
		diff = SUB (VTX1, VTX0);
		diff.scale (tmp);
		isectpoint0.add (diff, VTX0);
		tmp = D0 / (D0 - D2);
		isect.y = VV0 + (VV2 - VV0) * tmp;
		diff = SUB (VTX2, VTX0);
		diff.scale (tmp);
		isectpoint1.add (VTX0, diff);
	}
	
	public static int triangleTriangleIntersection (Tuple3d tuple3d, Tuple3d tuple3d2,
			Tuple3d tuple3d3, Tuple3d tuple3d4, Tuple3d tuple3d22, Tuple3d tuple3d32, Tuple3f isectpt1,
			Tuple3f isectpt2)
	{

		boolean debug = false;

		if (debug)
		{
			System.out.println ("Triangle 1 " + tuple3d + " " + tuple3d2 + " " + tuple3d3
				+ " Triangle 2 " + tuple3d4 + " " + tuple3d22 + " " + tuple3d32);
		}

		// compute Plane equation of Triangle (V0,V1,V2)

		Tuple3d N1 = CROSS (SUB (tuple3d2, tuple3d), SUB (tuple3d3, tuple3d));
		double d1 = -DOT (N1, tuple3d);

		// put U0, U1, U2 into plane equation of triangle (V0,V1,V2)

		double du0 = DOT (N1, tuple3d4) + d1;
		double du1 = DOT (N1, tuple3d22) + d1;
		double du2 = DOT (N1, tuple3d32) + d1;

		// coplanarity robustness check
		if (du0 < EPSILON && du0 > -EPSILON)
			du0 = 0.0f;
		if (du1 < EPSILON && du1 > -EPSILON)
			du1 = 0.0f;
		if (du2 < EPSILON && du2 > -EPSILON)
			du2 = 0.0f;

		// reject if all distances have the same sign

		double du0du1 = du0 * du1;
		double du0du2 = du0 * du2;

		if (du0du1 > 0.0f && du0du2 > 0.0f)
		{
			//			System.out.println("Same sign");
			return 0;
		}

		// compute Plane equation of Triangle (U0,U1,U2)

		Tuple3d N2 = CROSS (SUB (tuple3d22, tuple3d4), SUB (tuple3d32, tuple3d4));
		double d2 = -DOT (N2, tuple3d4);

		// compute distances from vertices of triangle 2 to the plane of triangle 1

		double dv0 = DOT (N2, tuple3d) + d2;
		double dv1 = DOT (N2, tuple3d2) + d2;
		double dv2 = DOT (N2, tuple3d3) + d2;

		// reject if triangles are coplanar

		if (dv0 < EPSILON && dv0 > -EPSILON)
			dv0 = 0.0f;
		if (dv1 < EPSILON && dv1 > -EPSILON)
			dv1 = 0.0f;
		if (dv2 < EPSILON && dv2 > -EPSILON)
			dv2 = 0.0f;

		// reject if all distances have the same sign

		double dv0dv1 = dv0 * dv1;
		double dv0dv2 = dv0 * dv2;

		if (dv0dv1 > 0.0f && dv0dv2 > 0.0f)
		{
			//			System.out.println("Same sign");
			return 0;
		}

		// compute direction of the intersection line

		Tuple3d D = CROSS (N1, N2);

		// compute and index to the largest component of D

		double max = D.x;
		if (max < 0.0f)
			max = -max;
		int index = 0;
		double b = D.y;
		if (b < 0.0f)
			b = -b;
		double c = D.z;
		if (c < 0.0f)
			c = -c;

		if (b > max)
		{
			max = b;
			index = 1;
		}
		if (c > max)
		{
			max = c;
			index = 2;
		}

		/* this is the simplified projection onto L*/

		double vp0;
		double vp1;
		double vp2;

		double up0;
		double up1;
		double up2;

		if (index == 0)
		{
			vp0 = tuple3d.x;
			vp1 = tuple3d2.x;
			vp2 = tuple3d3.x;

			up0 = tuple3d4.x;
			up1 = tuple3d22.x;
			up2 = tuple3d32.x;

		}
		else if (index == 1)
		{
			vp0 = tuple3d.y;
			vp1 = tuple3d2.y;
			vp2 = tuple3d3.y;

			up0 = tuple3d4.y;
			up1 = tuple3d22.y;
			up2 = tuple3d32.y;
		}
		else
		{
			vp0 = tuple3d.z;
			vp1 = tuple3d2.z;
			vp2 = tuple3d3.z;

			up0 = tuple3d4.z;
			up1 = tuple3d22.z;
			up2 = tuple3d32.z;
		}

		int coplanar;

		Tuple2d isect1 = new Point2d ();
		Tuple3d isectpointA1 = new Point3d ();
		Tuple3d isectpointA2 = new Point3d ();

		coplanar = compute_intervals_isectline (tuple3d, tuple3d2, tuple3d3, vp0, vp1, vp2, dv0,
			dv1, dv2, dv0dv1, dv0dv2, isect1, isectpointA1, isectpointA2);

		if (coplanar == 1)
		{
			if (debug)
			{
				System.out.println ("coplanar");
			}
			return 2;
		}

		Tuple2d isect2 = new Point2d ();
		Tuple3d isectpointB1 = new Point3d ();
		Tuple3d isectpointB2 = new Point3d ();

		compute_intervals_isectline (tuple3d4, tuple3d22, tuple3d32, up0, up1, up2, du0, du1, du2,
			du0du1, du0du2, isect2, isectpointB1, isectpointB2);

		int smallest1 = SORT2 (isect1);
		int smallest2 = SORT2 (isect2);

		if (isect1.y < isect2.x || isect2.y < isect1.x)
			return 0;

		/* at this point, we know that the triangles intersect*/

		//		Tuple3f isectpt1;
		//		Tuple3f isectpt2;

		if (isect2.x < isect1.x)
		{
			if (smallest1 == 0)
			{
				isectpt1.set (isectpointA1);
			}
			else
			{
				isectpt1.set (isectpointA2);
			}

			if (isect2.y < isect1.y)
			{
				if (smallest2 == 0)
				{
					isectpt2.set (isectpointB2);
				}
				else
				{
					isectpt2.set (isectpointB1);
				}
			}
			else
			{
				if (smallest1 == 0)
				{
					isectpt2.set (isectpointA2);
				}
				else
				{
					isectpt2.set (isectpointA1);
				}
			}
		}
		else
		{
			if (smallest2 == 0)
			{
				isectpt1.set (isectpointB1);
			}
			else
			{
				isectpt1.set (isectpointB2);
			}

			if (isect2.y > isect1.y)
			{
				if (smallest1 == 0)
				{
					isectpt2.set (isectpointA2);
				}
				else
				{
					isectpt2.set (isectpointA1);
				}
			}
			else
			{
				if (smallest2 == 0)
				{
					isectpt2.set (isectpointB2);
				}
				else
				{
					isectpt2.set (isectpointB1);
				}
			}
		}

		if (isectpt1.epsilonEquals (isectpt2, EPSILON))
		{
			if (debug)
			{
				System.out.println ("point intersection");
			}
			return 0;
		}

		return 1;
	}

	
	public static double distanceToLine(Tuple3d from, Tuple3d to , Tuple3d point){
		
		Tuple3d x0 = new Point3d();
		Tuple3d x1 = new Point3d();
		Tuple3d x2 = new Point3d();
		
		x0.set (point);
		x1.set (from);
		x2.set (to);
		
		Tuple3d x0x1 = new Point3d();
		Tuple3d x0x2 = new Point3d();
		Tuple3d x2x1 = new Point3d();
		
		x0x1.sub (x0, x1);
		x0x2.sub (x0, x2);
		
		x2x1.sub (x2 , x1);
		
		x0 = IntersectionTests.CROSS(x0x1,x0x2);
		
		Tuple3d fromXto;
		
		fromXto = CROSS (from, to);
		double d = DOT (fromXto, point);
		
		d = ABS (x0)/ ABS (x2x1);
		
		return d;
	}
	
	public static float pointInPlane (Tuple3f v1, Tuple3f v2, Tuple3f v3,
			Tuple3f point)
	{
		//compute plane equation
		Tuple3f u = new Point3f ();
		Tuple3f v = new Point3f ();
		Tuple3f p = new Point3f ();
		Tuple3f normal = new Point3f ();

		u.sub (v2, v1);
		v.sub (v3, v1);

		normal = CROSS (u, v);

		p.sub (point, v1);

		return DOT (normal, p);
	}

//	public static Tuple2f pointInTriangle1 ()
//	{
//
//		return new Point2f (0, 0);
//	}
//
//	public static Tuple2f pointInTriangle2 ()
//	{
//
//		return new Point2f (0, 0);
//	}

	public float cylinderIntersection (Tuple3f bottom, Tuple3f top,
			Tuple3f point)
	{

		Tuple3f rayBase = new Point3f ();
		rayBase.set (bottom);
		Tuple3f rayDir = new Point3f ();
		rayDir.sub (top, bottom);

		Tuple3f planeBase = new Point3f ();
		planeBase.set (point);
		Tuple3f planeNormal = new Point3f ();
		planeNormal.set (rayDir);

		Tuple3f x1 = new Point3f ();
		Tuple3f x2 = new Point3f ();

		float f1, f2;

		x1.sub (planeBase, rayBase);
		f1 = DOT (x1, planeNormal);

		f2 = DOT (rayDir, planeNormal);

		float t = f1 / f2;

		if (t < 0.0f || t > 1.0f)
			return -1.0f;

		Tuple3f nearestPOP = new Point3f ();
		nearestPOP.set (rayDir);
		nearestPOP.scale (t);
		nearestPOP.add (rayBase);

		if (nearestPOP.epsilonEquals (point, EPSILON))
			return t;
		else
			return -1.0f;

	}
	
	/*
	 * returns 0 if no intersection occurs
	 * else ist will return 1
	 * 
	 * origin is the starting point of the ray
	 * 
	 * direction is the direction of the ray
	 * */
	
	public static int rayTriangleIntersect (Tuple3d orig, Tuple3d dir,
			Tuple3d v0, Tuple3d v1, Tuple3d v2, Tuple3d out)
	{

		boolean debug = false;

		if (debug)
		{
			System.out.println ("origin");
			System.out.println (orig);

			System.out.println ("direction");
			System.out.println (dir);

			System.out.println ("v1");
			System.out.println (v0);

			System.out.println ("v2");
			System.out.println (v1);

			System.out.println ("v3");
			System.out.println (v2);

		}

		//	orig.sub(dest);

		Tuple3d edge1 = new Point3d ();
		Tuple3d edge2 = new Point3d ();

		edge1.sub (v1, v0);
		edge2.sub (v2, v0);

		Tuple3d pvec = new Point3d ();
		pvec = CROSS (dir, edge2);

		double det;

		det = DOT (edge1, pvec);

		if (det > -EPSILON && det < EPSILON)// segment lies in plane
		{
			if(debug)System.out.println("segment lies in plane");
			return 0;
		}
		
		double inv_det = 1.0f / det;

		// calculate distance from v1 to ray origin

		Tuple3d tvec = new Point3d ();

		tvec.sub (orig, v0);

		// calculate U parameter and test bounds

		double u;

		u = DOT(tvec , pvec) * inv_det;

		if (u < 0.0f || u > 1.0d)
		{
			if(debug)System.out.println("Out of bounds u");
			return 0;
		}
		
		//prepare to test V parameter
		
		Tuple3d qvec = new Point3d ();

		qvec = CROSS (tvec, edge1);
		
		//calculate V parameter and test bounds
		
		double v = DOT (dir, qvec) * inv_det;
		if (v < 0.0f || (u + v) > 1.0f)
		{
			if(debug)System.out.println("Out of bounds v");
			return 0;
		}

		// calculate t, ray intersects triangle

		double t = DOT (edge2, qvec) * inv_det;
		if (t < -EPSILON)
		{
			if(debug)System.out.println("Out of bounds t "+t);
			return 0;
		}
		
		out.set(dir);
		out.scale (t);
		out.add (orig);
		
		//	if(t < 0.0){
		//		t=0.0f;
		//	}
		return 1;
	}

	public static double rayTriangleIntersection (Tuple3d origin,
			Tuple3d direction, Tuple3d v1, Tuple3d v2, Tuple3d v3,
			boolean[] backfacing, boolean[] intersect)
	{

		boolean debug = false;

		if (debug)
		{
			System.out.println ("origin");
			System.out.println (origin);

			System.out.println ("direction");
			System.out.println (direction);

			System.out.println ("v1");
			System.out.println (v1);

			System.out.println ("v2");
			System.out.println (v2);

			System.out.println ("v3");
			System.out.println (v3);

		}

		//	orig.sub(dest);

		Tuple3d edge1 = new Point3d ();
		Tuple3d edge2 = new Point3d ();

		edge1.sub (v2, v1);
		edge2.sub (v3, v1);

		Tuple3d pvec = new Point3d ();
		pvec = CROSS (direction, edge2);

		double det;

		det = DOT (edge1, pvec);

		if (det > -EPSILON && det < EPSILON)// segment lies in plane
		{
			intersect[0] = false;
			return 0;
		}
		if (det < EPSILON)
			backfacing[0] = true;
		else
			backfacing[0] = false;
		double inv_det = 1.0f / det;

		// calculate distance from v1 to ray origin

		Tuple3d tvec = new Point3d ();

		tvec.sub (origin, v1);

		// calculate U parameter and test bounds

		double u;

		u = (tvec.x * pvec.x + tvec.y * pvec.y + tvec.z * pvec.z) * inv_det;

		if (u < 0.0f || u > 1.0f)
		{
			intersect[0] = false;
			return 0;
		}

		Tuple3d qvec = new Point3d ();

		qvec = CROSS (tvec, edge1);

		double v = DOT (direction, qvec) * inv_det;
		if (v < 0.0f || (u + v) > 1.0f)
		{
			intersect[0] = false;
			return 0;
		}

		// calculate t, ray intersects triangle

		double t = DOT (edge2, qvec) * inv_det;
		if ((t < EPSILON))
		{
			intersect[0] = false;
			return 0;
		}
		//	if(t < 0.0){
		//		t=0.0f;
		//	}
		intersect[0] = true;
		return t;
	}

	static public float distanceBetween2Points(Tuple3f v0, Tuple3f v1){
		
		Tuple3f temp = SUB (v0, v1);
		
		return (float) Math.sqrt ( (double)(temp.x*temp.x + temp.y*temp.y + temp.z*temp.z) );
	}
	
	static public double distanceBetween2Points(Tuple3d v0, Tuple3d v1){
		
		Tuple3d temp = SUB (v0, v1);
		
		return Math.sqrt ( (temp.x*temp.x + temp.y*temp.y + temp.z*temp.z) );
	}
	
	static public int getBiggestComponent(Tuple3f vec){
		
		int biggest=0;
		
		float x,y,z;
		
		if(vec.x<0.0f){
			x=-vec.x;
		} else{
			x=vec.x;
		}
		if(vec.y<0.0f){
			y=-vec.y;
		} else{
			y=vec.y;
		}
		if(vec.z<0.0f){
			z=-vec.z;
		} else{
			z=vec.z;
		}
		
		if(x>y){
			biggest=0;
			if(x<z){
				biggest=3;
			}
		} else{
			biggest=1;
			if(y<z){
				biggest=3;
			}
		}
		
		return biggest;
	}
	
static public int getBiggestComponent(Tuple3d vektor){
		
		Tuple3d vec= new Point3d();
		vec.set(vektor);
	
		int biggest=0;
		
		double x,y,z;
		
		if(vec.x<0.0f){
			x=-vec.x;
		} else{
			x=vec.x;
		}
		if(vec.y<0.0f){
			y=-vec.y;
		} else{
			y=vec.y;
		}
		if(vec.z<0.0f){
			z=-vec.z;
		} else{
			z=vec.z;
		}
		
		if(x>y){
			biggest=0;
			if(x<z){
				biggest=2;
			}
		} else{
			biggest=1;
			if(y<z){
				biggest=2;
			}
		}
		
		return biggest;
	}

public static int triangleTriangleIntersection (Tuple3d V0, Tuple3d V1,
		Tuple3d V2, Tuple3d U0, Tuple3d U1, Tuple3d U2, Tuple3d isectpt1,
		Tuple3d isectpt2)
{

	boolean debug = false;

	if (debug)
	{
		System.out.println ("Triangle 1 " + V0 + " " + V1 + " " + V2
			+ " Triangle 2 " + U0 + " " + U1 + " " + U2);
	}

	// compute Plane equation of Triangle (V0,V1,V2)

	Tuple3d N1 = CROSS (SUB (V1, V0), SUB (V2, V0));
	double d1 = -DOT (N1, V0);

	// put U0, U1, U2 into plane equation of triangle (V0,V1,V2)

	double du0 = DOT (N1, U0) + d1;
	double du1 = DOT (N1, U1) + d1;
	double du2 = DOT (N1, U2) + d1;

	// coplanarity robustness check
	if (du0 < EPSILON_D && du0 > -EPSILON_D)
		du0 = 0.0f;
	if (du1 < EPSILON_D && du1 > -EPSILON_D)
		du1 = 0.0f;
	if (du2 < EPSILON_D && du2 > -EPSILON_D)
		du2 = 0.0f;

	// reject if all distances have the same sign

	double du0du1 = du0 * du1;
	double du0du2 = du0 * du2;

	if (du0du1 > 0.0f && du0du2 > 0.0f)
	{
		//			System.out.println("Same sign");
		return 0;
	}

	// compute Plane equation of Triangle (U0,U1,U2)

	Tuple3d N2 = CROSS (SUB (U1, U0), SUB (U2, U0));
	double d2 = -DOT (N2, U0);

	// compute distances from vertices of triangle 2 to the plane of triangle 1

	double dv0 = DOT (N2, V0) + d2;
	double dv1 = DOT (N2, V1) + d2;
	double dv2 = DOT (N2, V2) + d2;

	// reject if triangles are coplanar

	if (dv0 < EPSILON_D && dv0 > -EPSILON_D)
		dv0 = 0.0f;
	if (dv1 < EPSILON_D && dv1 > -EPSILON_D)
		dv1 = 0.0f;
	if (dv2 < EPSILON_D && dv2 > -EPSILON_D)
		dv2 = 0.0f;

	// reject if all distances have the same sign

	double dv0dv1 = dv0 * dv1;
	double dv0dv2 = dv0 * dv2;

	if (dv0dv1 > 0.0f && dv0dv2 > 0.0f)
	{
		//			System.out.println("Same sign");
		return 0;
	}

	// compute direction of the intersection line

	Tuple3d D = CROSS (N1, N2);

	// compute and index to the largest component of D

	double max = D.x;
	if (max < 0.0f)
		max = -max;
	int index = 0;
	double b = D.y;
	if (b < 0.0f)
		b = -b;
	double c = D.z;
	if (c < 0.0f)
		c = -c;

	if (b > max)
	{
		max = b;
		index = 1;
	}
	if (c > max)
	{
		max = c;
		index = 2;
	}

	/* this is the simplified projection onto L*/

	double vp0;
	double vp1;
	double vp2;

	double up0;
	double up1;
	double up2;

	if (index == 0)
	{
		vp0 = V0.x;
		vp1 = V1.x;
		vp2 = V2.x;

		up0 = U0.x;
		up1 = U1.x;
		up2 = U2.x;

	}
	else if (index == 1)
	{
		vp0 = V0.y;
		vp1 = V1.y;
		vp2 = V2.y;

		up0 = U0.y;
		up1 = U1.y;
		up2 = U2.y;
	}
	else
	{
		vp0 = V0.z;
		vp1 = V1.z;
		vp2 = V2.z;

		up0 = U0.z;
		up1 = U1.z;
		up2 = U2.z;
	}

	int coplanar;

	Tuple2d isect1 = new Point2d ();
	Tuple3d isectpointA1 = new Point3d ();
	Tuple3d isectpointA2 = new Point3d ();

	coplanar = compute_intervals_isectline (V0, V1, V2, vp0, vp1, vp2, dv0,
		dv1, dv2, dv0dv1, dv0dv2, isect1, isectpointA1, isectpointA2);

	if (coplanar == 1)
	{
		if (debug)
		{
			System.out.println ("coplanar");
		}
		return 2;
	}

	Tuple2d isect2 = new Point2d ();
	Tuple3d isectpointB1 = new Point3d ();
	Tuple3d isectpointB2 = new Point3d ();

	compute_intervals_isectline (U0, U1, U2, up0, up1, up2, du0, du1, du2,
		du0du1, du0du2, isect2, isectpointB1, isectpointB2);

	int smallest1 = SORT2 (isect1);
	int smallest2 = SORT2 (isect2);

	if (isect1.y < isect2.x || isect2.y < isect1.x)
		return 0;

	/* at this point, we know that the triangles intersect*/

	//		Tuple3f isectpt1;
	//		Tuple3f isectpt2;

	if (isect2.x < isect1.x)
	{
		if (smallest1 == 0)
		{
			isectpt1.set (isectpointA1);
		}
		else
		{
			isectpt1.set (isectpointA2);
		}

		if (isect2.y < isect1.y)
		{
			if (smallest2 == 0)
			{
				isectpt2.set (isectpointB2);
			}
			else
			{
				isectpt2.set (isectpointB1);
			}
		}
		else
		{
			if (smallest1 == 0)
			{
				isectpt2.set (isectpointA2);
			}
			else
			{
				isectpt2.set (isectpointA1);
			}
		}
	}
	else
	{
		if (smallest2 == 0)
		{
			isectpt1.set (isectpointB1);
		}
		else
		{
			isectpt1.set (isectpointB2);
		}

		if (isect2.y > isect1.y)
		{
			if (smallest1 == 0)
			{
				isectpt2.set (isectpointA2);
			}
			else
			{
				isectpt2.set (isectpointA1);
			}
		}
		else
		{
			if (smallest2 == 0)
			{
				isectpt2.set (isectpointB2);
			}
			else
			{
				isectpt2.set (isectpointB1);
			}
		}
	}

	if (isectpt1.epsilonEquals (isectpt2, EPSILON))
	{
		if (debug)
		{
			System.out.println ("point intersection");
		}
		return 0;
	}

	return 1;
}

/**
 * Returns if point c lies on the left side of the linie from a to c
 * @return
 */

static public boolean leftOf( double ax , double ay, double bx , double by, double cx , double cy){
	return ( area(ax , ay, bx, by , cx, cy) > 0 );
}

static public boolean rightOf( double ax , double ay, double bx , double by, double cx , double cy){
	return ( area(ax , ay, bx, by , cx, cy) < 0 );
}

static public double area(double x1, double y1, double x2, double y2, double x3, double y3){
	double x2x1 = x2-x1;
	double y3y1 = y3-y1;
	
	double x3x1 = x3-x1;
	double y2y1 = y2-y1;
	
	return x2x1 * y3y1 - x3x1 * y2y1;
	
}
	
}
