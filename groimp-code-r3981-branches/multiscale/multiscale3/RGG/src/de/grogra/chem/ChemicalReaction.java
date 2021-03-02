package de.grogra.chem;

import static java.lang.Math.pow;

import java.util.HashMap;

public class ChemicalReaction implements ISlope {

	ChemicalExpression left;
	ChemicalExpression right;

	double kf;
	double kr;

	public double getForwardRateConstant() {
		return kf;
	}

	public void setForwardRateConstant(double kf) {
		this.kf = kf;
	}

	public double getBackwardRateConstant() {
		return kr;
	}

	public void setBackwardRateConstant(double kr) {
		this.kr = kr;
	}

	public String toString() {
		return left.toString() + " <=> " + right.toString();
	}

	@Override
	public int assignIndices(int base, HashMap<Object, Integer> indices) {

		for (ChemicalTerm term : left.terms) {
			Integer index = indices.get(term.m);
			if (index == null) {
				// assign new index
				term.index = base;
				index = base++;
				indices.put(term.m, index);
			} else {
				// index was already assigned, so set it for the term
				term.index = index;
			}
		}

		for (ChemicalTerm term : right.terms) {
			Integer index = indices.get(term.m);
			if (index == null) {
				// assign new index
				term.index = base;
				index = base++;
				indices.put(term.m, index);
			} else {
				// index was already assigned, so set it for the term
				term.index = index;
			}
		}

		return base;
	}

	@Override
	public void eval(double[] out, double t, double[] y) {
		double delta;

		// calculate effect of forward reaction
		// delta = kf * [A0]^fA0 * [A1]^fA1 * ...
		delta = getForwardRateConstant();
		for (ChemicalTerm term : left.terms) {
			delta *= pow(y[term.index], term.factor);
		}

		// apply effect of forward reaction
		// d[Ai] / dt = - delta * fAi and d[Bi] / dt = + delta * fBi
		for (ChemicalTerm term : left.terms) {
			out[term.index] -= delta * term.factor;
		}
		for (ChemicalTerm term : right.terms) {
			out[term.index] += delta * term.factor;
		}

		// calculate effect of backward reaction
		// delta = kr * [B0]^fB0 * [B1]^fB1 * ...
		delta = getBackwardRateConstant();
		for (ChemicalTerm term : right.terms) {
			delta *= pow(y[term.index], term.factor);
		}

		// apply effect of forward reaction
		// d[Ai] / dt = + delta * fAi and d[Bi] / dt = - delta * fBi
		for (ChemicalTerm term : left.terms) {
			out[term.index] += delta * term.factor;
		}
		for (ChemicalTerm term : right.terms) {
			out[term.index] -= delta * term.factor;
		}
	}
}
