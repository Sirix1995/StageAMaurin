/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth,
 * University of Göttingen, Germany
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */ 

package de.grogra.lignum.stlLignum;

import java.util.Collections;
import java.util.Vector;

import de.grogra.lignum.jadt.EllipseL;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
//cLignum uses templates for the SHAPE of the BroadLeaf. Javas generics do not work because one has
// to instantiate SHAPE within the class. Therefore I decided to define SHAPE as an Ellipse.
//If later on other SHAPES are needed one has to copy the code and change Ellipse with the new SHAPE.
//Later on one could also think of using generics with either a factory interface or other work arounds.
public class BroadLeafAttributes {
	
	public double degree_of_filling;  //The real leaf is only  part of the shape
	public double sf; //specific leaf area
	public double tauL;  //transmission coefficient
	//the following double variables are all listed as KGC (typedef of double) in cLignum
	public double P; //photosynthetic production
	public double M;                    //respiration 
	public double Qin;                  //incoming radiation
	public double Qabs;                 //absorbed radiation
	public Petiole petiole; //leaf is at the end of petiole in 3D space
	public EllipseL e; //the form  of the  leaf is modeled  as some
				 //SHAPE, see e.g Triangle and Ellipse 
				 //vector for shading (must be synchronized with firmament)
	Vector<Double> sv = new Vector<Double>();
	
	public BroadLeafAttributes (double sf1, double tauL1, double dof1, Petiole petiole1, 
			EllipseL e1, int number_of_sectors){
		degree_of_filling = dof1;
		sf = sf1;
		tauL = tauL1;
		P = 0.0;
		M= 0.0;
		Qin = 0.0;
		Qabs =0.0;
		petiole = new Petiole(petiole1);
		e = new EllipseL(e1);
		sv.setSize(number_of_sectors);		
	}
	
	
	public BroadLeafAttributes (BroadLeafAttributes bla){
		degree_of_filling = bla.degree_of_filling;
		sf = bla.sf;
		tauL = bla.tauL;
		P = bla.P;
		M = bla.M;
		Qin = bla.Qin;
		Qabs = bla.Qabs;
		petiole = new Petiole(bla.petiole);
		e = new EllipseL(bla.e);
		sv.clear();
		Collections.copy(bla.sv, sv);
	}

}
