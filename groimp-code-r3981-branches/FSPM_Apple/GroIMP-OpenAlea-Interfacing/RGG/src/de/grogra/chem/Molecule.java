package de.grogra.chem;

public class Molecule {
	
	public static final Molecule H2 = new Molecule("H2");
	public static final Molecule H2O = new Molecule("H2O");
	public static final Molecule O2 = new Molecule("O2");
	
	String name;

	public Molecule(String name) {
		this.name = name;
	}
	public String toString() {
		return name;
	}
}
