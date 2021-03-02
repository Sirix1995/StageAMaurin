package de.grogra.imp3d.math.delaunay;

import javax.vecmath.Point3d;

import de.grogra.math.Degree;
import de.grogra.xl.util.Operators;

public final class VoronoiMeshExsamples {

	// help vari
	private static double phi_i = 0;

	public static final Point3d[] DEFAULT1 = new Point3d[] {
			new Point3d(0.000021d, 0.0000001d, 0d),
			new Point3d(-6.00002E-06D, -0.900007D, -6.0000002E-06D),
			new Point3d(3.99998E-06D, 0.0D, -0.899993996D),
			new Point3d(-0.9D, 0.0D, 0.0D), new Point3d(0.9D, 0.0D, 0.0D),
			new Point3d(-6.000002E-06D, -1.99999E-06D, 0.8999979996D),
			new Point3d(1.0000001E-05D, 0.9000010005D, 3.99999998E-06D),
			new Point3d(-0.44999499998D, -0.4499909997D, -0.45001002D),
			new Point3d(-0.450002000001D, -0.450004999D, 0.449994998D),
			new Point3d(-0.449992D, 0.4499909997D, -0.449992D),
			new Point3d(-0.4499949998D, 0.4500060002D, 0.449990997D),
			new Point3d(0.45000098D, -0.4499960001D, -0.44999600001D),
			new Point3d(0.449998001D, -0.450089999D, 0.449992999998D),
			new Point3d(0.449994001D, 0.4499499998D, -0.45000800002D),
			new Point3d(0.450008002D, 0.4499600001D, 0.4500069999D) };

	public static final Point3d[] DEFAULT2 = new Point3d[] {
			new Point3d(0.000021d, 0.0000001d, 0d),

			new Point3d(-6.00002E-06D, -0.900007D, -6.0000002E-06D),
			new Point3d(3.99998E-06D, 0.0D, -0.899993996D),
			new Point3d(-0.9D, 0.0D, 0.0D), new Point3d(0.9D, 0.0D, 0.0D),
			new Point3d(-6.000002E-06D, -1.99999E-06D, 0.8999979996D),
			new Point3d(1.0000001E-05D, 0.9000010005D, 3.99999998E-06D),
			new Point3d(-0.44999499998D, -0.4499909997D, -0.45001002D),
			new Point3d(-0.450002000001D, -0.450004999D, 0.449994998D),
			new Point3d(-0.449992D, 0.4499909997D, -0.449992D),
			new Point3d(-0.4499949998D, 0.4500060002D, 0.449990997D),
			new Point3d(0.45000098D, -0.4499960001D, -0.44999600001D),
			new Point3d(0.449998001D, -0.450089999D, 0.449992999998D),
			new Point3d(0.449994001D, 0.4499499998D, -0.45000800002D),
			new Point3d(0.450008002D, 0.4499600001D, 0.4500069999D),

			new Point3d(-0.449992D, 1.499909997D, -0.449992D),
			new Point3d(-0.4499949998D, 1.500060002D, 0.449990997D),
			new Point3d(0.449994001D, 1.499499998D, -0.44990800002D),
			new Point3d(0.451008002D, 1.5100001D, 0.45001069999D),
			new Point3d(-0.988889D, 1.000002D, 0.001D),
			new Point3d(0.9889D, 1.0001D, 0.000000001D),
			new Point3d(3.99998E-06D, 1.0001D, -0.99993996D),
			new Point3d(3.99998E-06D, 1.0001D, 0.899993996D),
			new Point3d(-6.00002E-06D, 1.900007D, -6.0000002E-06D),

			new Point3d(-0.0000000001, .489898902D, -0.00001D),
			new Point3d(-0.9992D, 0.499909997D, -0.9992D),
			new Point3d(-0.99949998D, 0.49879870002D, 0.9990997D),
			new Point3d(0.9992D, 0.499909997D, -0.9992D),
			new Point3d(0.99949998D, 0.49879870002D, 0.9990997D) };

	public static Point3d[] getPhyllotaxisSphere() {
		// cell sphere (phyllotaxis)
		Point3d[] tmp = new Point3d[30 + 60 + 120 + 250];
		tmp[0] = new Point3d(0, 0, 0);

		int max = 30;
		for (int i = 1; i < max; i++) {
			tmp[i] = getNextP(i, max, 0.75);
		}
		phi_i = 0;
		max = 60;
		for (int i = 0; i < max; i++) {
			tmp[30 + i] = getNextP(i, max, 1.75);
		}
		phi_i = 0;
		max = 120;
		for (int i = 0; i < max; i++) {
			tmp[30 + 60 + i] = getNextP(i, max, 2.85);
		}

		phi_i = 0;
		max = 250;
		for (int i = 0; i < max; i++) {
			tmp[30 + 60 + 120 + i] = getNextP(i, max, 4.0);
			;
		}
		return tmp;
	}

	// cell tissue (layered)
	public static final Point3d[] getCellTissue() {
		float maxX = 1.5f;
		float maxY = 3.0f;
		float[] levelZ = {0f, 0.2f, 0.5f, 0.75f, 1.2f, 1.4f, 1.6f};
		int[] nX = {10, 9, 6, 10, 15, 20, 25};
		int[] nY = {10, 9, 6, 10, 15, 20, 30};
		int j = 0;
		for (int i = 0; i < nX.length; i++)
			j += nX[i] * nY[i];
		Point3d[] tmp = new Point3d[j];

		j = 0;
		float maxN = 0.000025f;
		for (int z = 0; z < levelZ.length; z++) {
			float deltaY = maxY / (nY[z]-1);
			for (int y = 0; y < nY[z]; y++) {
				float deltaX = maxX / (nX[z]-1);
				for (int x = 0; x < nX[z]; x++) {
					tmp[j] = new Point3d(x * deltaX + random(-maxN, maxN), y
							* deltaY + random(-maxN, maxN), levelZ[z]
							+ random(-maxN, maxN));
					j++;
				}
			}
		}
		return tmp;
	}

	public static final Point3d[] CUBIC_CRYSTAL = new Point3d[] {
			new Point3d(-0.89999799999999996D, -0.89999200000000001D,-0.900003D),
			new Point3d(-0.90000199999999997D, -0.90000800000000003D,-6.9999999999999999E-06D),
			new Point3d(-0.89999499999999999D, -0.90000199999999997D, 0.900003D),
			new Point3d(-0.89999099999999999D, 5.0000000000000004E-06D,-0.89999099999999999D),
			new Point3d(-0.89999399999999996D, 9.9999999999999995E-07D,5.0000000000000004E-06D),
			new Point3d(-0.90000800000000003D, 5.0000000000000004E-06D,0.90000500000000005D),
			new Point3d(-0.89999499999999999D, 0.89999099999999999D,-0.89999600000000002D),
			new Point3d(-0.90000100000000005D, 0.90000000000000002D,-1.9999999999999999E-06D),
			new Point3d(-0.89999499999999999D, 0.90000199999999997D,0.89999899999999999D),
			new Point3d(-6.0000000000000002E-06D, -0.89999600000000002D,-0.90000599999999997D),
			new Point3d(-6.0000000000000002E-06D, -0.900007D,-6.0000000000000002E-06D),
			new Point3d(-3.0000000000000001E-06D, -0.90000100000000005D,0.89999600000000002D),
			new Point3d(3.9999999999999998E-06D, 0.0D, -0.89999399999999996D),
			new Point3d(-9.0000000000000002E-06D, 5.0000000000000004E-06D,1.0000000000000001E-05D),
			new Point3d(-6.0000000000000002E-06D, -1.9999999999999999E-06D,0.89999799999999996D),
			new Point3d(9.0000000000000002E-06D, 0.89999600000000002D,-0.90000599999999997D),
			new Point3d(1.0000000000000001E-05D, 0.90000100000000005D,3.9999999999999998E-06D),
			new Point3d(-6.9999999999999999E-06D, 0.90000899999999995D,0.89999600000000002D),
			new Point3d(0.89999799999999996D, -0.90000800000000003D,-0.89999600000000002D),
			new Point3d(0.90000500000000005D, -0.89999899999999999D,-1.9999999999999999E-06D),
			new Point3d(0.89999600000000002D, -0.89999099999999999D,0.90000199999999997D),
			new Point3d(0.89999499999999999D, 9.9999999999999995E-07D,-0.89999700000000005D),
			new Point3d(0.90000500000000005D, -5.0000000000000004E-06D,-3.9999999999999998E-06D),
			new Point3d(0.89999799999999996D, -3.9999999999999998E-06D,0.89999899999999999D),
			new Point3d(0.90000599999999997D, 0.900003D, -0.89999600000000002D),
			new Point3d(0.89999600000000002D, 0.89999899999999999D,-6.9999999999999999E-06D),
			new Point3d(0.90000400000000003D, 0.89999499999999999D, 0.900003D),
			new Point3d(-0.44999499999999998D, -0.44999099999999997D,-0.45001000000000002D),
			new Point3d(-0.45000200000000001D, -0.45000499999999999D,0.44999499999999998D),
			new Point3d(-0.449992D, 0.44999099999999997D, -0.449992D),
			new Point3d(-0.44999499999999998D, 0.45000600000000002D,0.44999099999999997D),
			new Point3d(0.45000099999999998D, -0.44999600000000001D,-0.44999600000000001D),
			new Point3d(0.44999800000000001D, -0.45000899999999999D,0.44999299999999998D),
			new Point3d(0.44999400000000001D, 0.44999499999999998D,-0.45000800000000002D),
			new Point3d(0.45000800000000002D, 0.44999600000000001D,0.45000699999999999D) };

	private final static double PHI = 1 + Math.sqrt(5) / 2d;

	public static final Point3d[] ICOSAHEDRON = new Point3d[] {
			new Point3d(0, 0, 0), new Point3d(0, -PHI, 1f),
			new Point3d(0, PHI, 1f), new Point3d(0, PHI, -1f),
			new Point3d(0, -PHI, -1f), new Point3d(1f, 0, PHI),
			new Point3d(-1f, 0, PHI), new Point3d(-1f, 0, -PHI),
			new Point3d(1f, 0, -PHI), new Point3d(PHI, 1f, 0),
			new Point3d(-PHI, 1f, 0), new Point3d(-PHI, -1f, 0),
			new Point3d(PHI, -1f, 0) };

	public static final Point3d[] DODECAHEDRON = new Point3d[] {
			new Point3d(0, 0, 0), new Point3d(PHI, 0, 1f / PHI),
			new Point3d(-PHI, 0, 1f / PHI), new Point3d(-PHI, 0, -1f / PHI),
			new Point3d(PHI, 0, -1f / PHI), new Point3d(1f / PHI, PHI, 0),
			new Point3d(1f / PHI, -PHI, 0), new Point3d(-1f / PHI, -PHI, 0),
			new Point3d(-1f / PHI, PHI, 0), new Point3d(0, 1f / PHI, PHI),
			new Point3d(0, 1f / PHI, -PHI), new Point3d(0, -1f / PHI, -PHI),
			new Point3d(0, -1f / PHI, PHI), new Point3d(1f, 1f, 1f),
			new Point3d(1f, -1f, 1f), new Point3d(-1f, -1f, 1f),
			new Point3d(-1f, 1f, 1f), new Point3d(-1f, 1f, -1f),
			new Point3d(1f, 1f, -1f), new Point3d(1f, -1f, -1f),
			new Point3d(-1f, -1f, -1f) };

	private static float random(float min, float max) {
		return Operators.getRandomGenerator().nextFloat() * (max - min) + min;
	}

	// calculates the next position of a cell on the surface of a sphere of
	// radius radius
	public static Point3d getNextP(int i, int number, double radius) {
		double z1 = 1 - i * 2 / (double) number;
		double z = radius * z1;
		double rz = Math.sqrt(1 - z1 * z1);

		double f_angle = 1d / number;
		double ii = i * f_angle + (number - i) * f_angle;
		phi_i = phi_i + 360d / (ii * (Math.sqrt(5) + 1) / 2d);

		double x = radius * Degree.cos(phi_i) * rz;
		double y = radius * Degree.sin(phi_i) * rz;

		return new Point3d(x, y, z);
	}

	public static Point3d[] getExample(int value) {
		switch (value) {
		case 0:
			return DEFAULT1;
		case 1:
			return DEFAULT2;
		case 2:
			return CUBIC_CRYSTAL;
		case 3:
			return ICOSAHEDRON;
		case 4:
			return DODECAHEDRON;
		case 5:
			return getCellTissue();
		case 6:
			return getPhyllotaxisSphere();
		}
		return DEFAULT1;
	}

}
