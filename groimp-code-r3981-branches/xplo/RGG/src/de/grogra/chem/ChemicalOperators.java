package de.grogra.chem;

public class ChemicalOperators {

	public static ChemicalReaction operator$cmp(ChemicalExpression lhs,
			ChemicalExpression rhs) {
		ChemicalReaction result = new ChemicalReaction();
		result.left = lhs;
		result.right = rhs;
		return result;
	}

	public static ChemicalExpression operator$add(ChemicalExpression lhs,
			ChemicalTerm rhs) {
		lhs.add(rhs);
		return lhs;
	}

	public static ChemicalTerm operator$mul(double factor, ChemicalTerm term) {
		term.factor *= factor;
		return term;
	}
	
	
	
	public static ChemicalTerm toChemicalTerm (Molecule m)
	{
		return new ChemicalTerm (m);
	}

	public static ChemicalExpression toChemicalExpression (Molecule m)
	{
		ChemicalExpression result = new ChemicalExpression ();
		result.add (new ChemicalTerm (m));
		return result;
	}

	public static ChemicalExpression toChemicalExpression (ChemicalTerm term)
	{
		ChemicalExpression result = new ChemicalExpression ();
		result.add (term);
		return result;
	}

}
