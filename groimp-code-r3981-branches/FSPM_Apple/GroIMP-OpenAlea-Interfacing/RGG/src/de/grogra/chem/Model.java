package de.grogra.chem;

import java.util.ArrayList;
import java.util.HashMap;

public class Model implements ISlope {

	final ArrayList<ISlope> slopes = new ArrayList<ISlope>();

	public Model addSlope(ISlope slope) {
		slopes.add(slope);
		return this;
	}

	public Model add(ChemicalReaction r, double kf) {
		return add(r, kf, 0);
	}

	public Model add(ChemicalReaction r, double kf, double kr) {
		r.setForwardRateConstant(kf);
		r.setBackwardRateConstant(kr);
		return addSlope(r);
	}

	@Override
	public int assignIndices(int base, HashMap<Object, Integer> indices) {
		for (ISlope slope : slopes) {
			base = slope.assignIndices(base, indices);
		}
		return base;
	}

	@Override
	public void eval(double[] out, double t, double[] y) {
		// iterate through every entry of the slopes vector
		for (ISlope slope : slopes) {
			// apply this slope to the result vector
			slope.eval(out, t, y);
		}
	}
}
