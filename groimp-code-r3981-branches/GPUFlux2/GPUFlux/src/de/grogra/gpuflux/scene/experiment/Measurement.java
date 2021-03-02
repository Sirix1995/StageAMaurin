package de.grogra.gpuflux.scene.experiment;

/**
 * @author Dietger van Antwerpen <dietger@xs4all.nl>
 * @version 1.0
 * @since 2011.0824
 *
 *        The Measurement class contains a matrix of measurements applying to a
 *        single object or group of objects. The dimension and units for the
 *        measurements are not defined and depend on the context.
 */

public class Measurement {

	/**
	 * Constructor
	 * 
	 * @param length
	 *            the dimension of the measurement matrix
	 * */
	public Measurement(int depth, int length) {
		data = new double[depth][length];
	}

	/**
	 * Constructor
	 * */
	public Measurement() {
	}

	/**
	 * Constructor
	 * 
	 * @param data
	 *            the measurement matrix data
	 * */
	public Measurement(double[][] data) {
		this.data = data;
	}

	/**
	 * Adds a measurement matrix to this measurement. The measurements are
	 * assumed to have the same dimensions and units.
	 * 
	 * @param m
	 *            measurement to add
	 * */
	public void add(Measurement m) {
		if ((m == null) || (m.data == null) != (data == null))
			throw new NullPointerException();
		if (data != null) {
			if (m.data.length != data.length)
				throw new IllegalArgumentException("Measurement dimensions must agree");
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++)
					data[i][j] += m.data[i][j];
		}
	}

	/**
	 * Scales a measurement matrix by a single factor.
	 * 
	 * @param scale
	 *            the scaling factor
	 * */
	public void mul(double scale) {
		if (data != null) {
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++)
					data[i][j] *= scale;
		}
	}

	/**
	 * Scaled a measurement matrix and adds it to this measurement. The
	 * measurements are assumed to have the same dimensions and units.
	 * 
	 * @param m
	 *            measurement to scale and add
	 * @param scale
	 *            scale factor
	 * */
	public void mad(Measurement m, double scale) {
		if ((m == null) || (m.data == null) != (data == null))
			throw new NullPointerException();
		if (data != null) {
			if (m.data.length != data.length)
				throw new IllegalArgumentException("Measurement dimensions must agree");
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++)
					data[i][j] += m.data[i][j] * scale;
		}
	}

	/**
	 * Returns the sum of the measurement vector elements. All dimensions are
	 * assumed to be of the same unit.
	 * 
	 * @return sum of all vector elements
	 * */
	public double integrate() {
		double ret = 0.0;
		if (data != null) {
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++)
					ret += data[i][j];
		}
		return ret;
	}

	/**
	 * Returns the sum of the measurement matrix at a stain depth. All
	 * dimensions are assumed to be of the same unit.
	 * 
	 * @return sum of all matrix elements
	 * */
	public double integrateDepth(int depth) {
		if (depth >= data.length) return 0;
		double ret = 0.0;
		for (int j = 0; j < data[0].length; j++)
			ret += data[depth][j];
		return ret;
	}

	/**
	 * Returns the sum of the measurement matrix for a stain bucked. All
	 * dimensions are assumed to be of the same unit.
	 * 
	 * @return sum of all matrix elements
	 * */
	public double integrateBucket(int bucket) {
		if (bucket >= data[0].length) return 0;
		double ret = 0.0;
		for (int i = 0; i < data.length; i++)
			ret += data[i][bucket];
		return ret;
	}

	@Override
	public String toString() {
		if (data == null || data.length == 0) {
			String s = "Sorry, something went wrong.\n";
			if (data == null)
				s += "Measurement.toString(): data = null";
			if (data.length == 0)
				s += "Measurement.toString(): |data| = 0";
			return s;
		}

		String str = "[ ";
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length - 1; j++) {
				str += data[i][j] + " , ";
			}
			str += data[i][data.length - 1] + " ]";
		}
		str += " ]";

		return str;
	}

	/**
	 * @return true if this measurement contains no data
	 * */
	public boolean isEmpty() {
		return data == null || data.length == 0;
	}

	@Override
	public Object clone() {
		if (data != null)
			return new Measurement(data.clone());
		else
			return new Measurement();
	}

	/**
	 * Measurement matrix for data
	 * */
	public double[][] data;

}
