package de.grogra.chem;

import java.util.HashMap;

public interface ISlope {
	int assignIndices(int base, HashMap<Object, Integer> indices);
	void eval(double[] out, double t, double[] y);
}
