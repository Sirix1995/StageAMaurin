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

package de.grogra.lignum.stlVoxelspace;

import static java.lang.Math.abs;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.EllipseL;
import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.stlLignum.BroadLeaf;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class HwEllipse extends VoxelObject implements Mathsym {
	
	private final EllipseL e; //Leaf ellipse
	private final double dof; //Degree of filling of the leaf shape model
	private final double tauL; //Extinction coefficient if beam hits the leaf shape
	BroadLeaf leaf; //the leaf itself

	
	public HwEllipse(EllipseL e1, double dof1, double tauL1, long tag1, BroadLeaf leaf1){
		super(tag1);
		e = new EllipseL(e1);
		dof = dof1; 
		tauL = tauL1;
		leaf = new BroadLeaf(leaf1);
	}
	
	public HwEllipse(HwEllipse hwe){
		super(hwe.getTag());
		e = new EllipseL(hwe.e);
		dof = hwe.dof;
		tauL = hwe.tauL;
		leaf = new BroadLeaf(hwe.leaf);
	}
	
	//Original Comment:
	//Check if the beam hits the ellipse
	//p: start point of the beam
	//d: direction of the light beam
	//length: returns the length light beam travels in foliage
	//getRoute is virtual and for hardwood species 'length' is set to zero
	
	@Override
	public int getRoute(Point3d p, PositionVector d, Distance length){
		length.d =0.0;
		//Use the geometric information
		EllipseL ellipse = new EllipseL(e);
		//Use the leaf itself
		//if(leaf){
			ellipse.set(leaf.getShape());
		//}
			
	    //Check for self hit
		if(abs(ellipse.getCenterPoint().distance(p)) <R_EPSILON){
			hit_self = true;
			return 0;//self hit
		}
		else if(ellipse.intersectShape(p, d)){
			return 1; //hit
		}
		else{
			return 0; //no hit
		}
	}
	
	//Original Comment:
	//Calculate the extinction for this  leaf 
	//p: start point of the beam
	//d:  direction  of  the  light  beam 
	//K:  Extinction  for conifers,  getExtinction  is  virtual and  for
	//hardwood species K is not used
	@Override
	public double getExtinction(Point3d p, PositionVector d, ParametricCurve K){
		K.eval(0.0);
		double vp = 1.0; //clear sector
		Distance length = new Distance(0.0);
		int result = getRoute(p, d, length);
		if(result == 1){
			vp = 1.0 -dof+dof * tauL; //extinction if hit
		}
		return vp;
	}
}
