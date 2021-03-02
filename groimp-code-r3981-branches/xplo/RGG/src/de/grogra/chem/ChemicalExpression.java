package de.grogra.chem;

import java.util.ArrayList;

public class ChemicalExpression {
	final ArrayList<ChemicalTerm> terms = new ArrayList<ChemicalTerm>();

	public void add(ChemicalTerm term) {
		terms.add(term);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < terms.size(); i++) {
			if (i > 0)
				builder.append(" + ");
			builder.append(terms.get(i));
		}
		return builder.toString();
	}
}
