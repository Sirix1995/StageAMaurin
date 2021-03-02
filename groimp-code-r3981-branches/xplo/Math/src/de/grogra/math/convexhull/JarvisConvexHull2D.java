package de.grogra.math.convexhull;

import javax.vecmath.Point2d;

/**
 * Computes the convex hull of a set of p using the Jarvis algorithm.</p>
 * 
 * R.A. Jarvis: On the Identification of the Convex Hull of a Finite Set of Points in the Plane. 
 * Information Processing Letters 2, 18-22 (1973)
 * 
 * @author mh
 */
public final class JarvisConvexHull2D {

	private static Point2d[] p;
	private static int n;
	private static int h;

	/**
	 * Computes the convex hull of a set of p as a single Polygon2D. Current
	 * implementation start at the point with lowest x-coordinate. The p are
	 * considered in counter-clockwise order. Result is an set of Points.
	 * 
	 * @param p
	 *            set of input p
	 * @return set of p describing the convex hull
	 */
	public static Point2d[] convexHull(Point2d[] points) {
		p = new Point2d[points.length];
		System.arraycopy(points, 0, p, 0, points.length);
		n = p.length;
		h = 0;
		jarvisMarch();
		Point2d[] tmp = new Point2d[h];
		for (int i = 0; i < h; i++) {
			tmp[i] = p[i];
		}
		return tmp;
	}

	private static void jarvisMarch() {
		int i = indexOfLowestPoint();
		do {
			exchange(h, i);
			i = indexOfRightmostPointFrom(p[h]);
			h++;
		} while (i > 0);
	}

	private static int indexOfLowestPoint() {
		int i, min = 0;
		for (i = 1; i < n; i++)
			if (p[i].y < p[min].y || p[i].y == p[min].y && p[i].x < p[min].x)
				min = i;
		return min;
	}

	private static int indexOfRightmostPointFrom(Point2d q) {
		int i = 0, j;
		for (j = 1; j < n; j++)
			if (isLess(relTo(p[j], q), relTo(p[i], q)))
				i = j;
		return i;
	}

	private static Point2d relTo(Point2d p0, Point2d p1) {
		return new Point2d(p0.x - p1.x, p0.y - p1.y);
	}

	private static boolean isLess(Point2d p0, Point2d p1) {
		double f = cross(p0, p1);
		return f > 0 || f == 0 && isFurther(p0, p1);
	}

	private static boolean isFurther(Point2d p0, Point2d p1) {
		return mdist(p0) > mdist(p1);
	}

	// Manhattan-Distanz
	private static double mdist(Point2d p) {
		return Math.abs(p.x) + Math.abs(p.y);
	}

	private static double cross(Point2d p0, Point2d p1) {
		return p0.x * p1.y - p1.x * p0.y;
	}

	private static void exchange(int i, int j) {
		Point2d t = p[i];
		p[i] = p[j];
		p[j] = t;
	}

	public static Point2d getCentroid(Point2d[] p) {
		double xc = 0;
		double yc = 0;
		double x, y;
		double xp, yp;
		double tmp = 0;
		Point2d pp = p[p.length - 1];
		xp = pp.x;
		yp = pp.y;
		// iterate on vertices
		for (Point2d pi : p) {
			x = pi.x;
			y = pi.y;
			tmp = xp * y - yp * x;
			xc += (x + xp) * tmp;
			yc += (y + yp) * tmp;
			pp = pi;
			xp = x;
			yp = y;
		}
		double denom = computeArea(p) * 6;
		return new Point2d(xc / denom, yc / denom);
	}

	private final static double computeArea(Point2d[] p) {
		double area = 0;
		Point2d pp = p[p.length - 1];
		for (Point2d pi : p) {
			area += pp.x * pi.y - pp.y * pi.x;
			pp = pi;
		}
		return area /= 2;
	}

}
