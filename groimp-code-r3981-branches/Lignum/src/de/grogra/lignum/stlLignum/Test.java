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

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.PositionVector;

/**
 *
 * @author Alexander Brinkmann
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//ParametricCurve p = new ParametricCurve("C:\\GroImp-Projekt\\GroIMP\\Arbeitsordner\\SourceCode1\\trunk\\Lignum\\K.fun");
		//ParametricCurve p = new ParametricCurve(2);
		//double d = p.eval(1);
		//System.out.println(d);
		//double treeAf = 0.36191147369354415;
		//double k_forest = 0.184;
		//double pineLGAH = 0.3;
		//double Hcb = 0;
		//double dens = 15000;
		//Point3d x0 = new Point3d(0,0,0);
		//EvaluateRadiationForCfTreeSegmentForest Rad = new EvaluateRadiationForCfTreeSegmentForest(p, treeAf, k_forest, pineLGAH, Hcb, dens, x0);
		//double d = p.eval(1);
		
		Point3d r0_1 = new Point3d(0,0,3);
	    PositionVector b = new PositionVector(0,2,0);
	    Point3d rs_1 = new Point3d(1,0,0);
	    PositionVector a= new PositionVector(0,0,0);
	    double Rs=2;
	    double Rw=2;
	    double L=1;
	    Distance distance = new Distance(4);
	    
	    System.out.println(BeamShading.CylinderBeamShading(r0_1, b,rs_1,a,Rs,Rw,L,distance));
	    System.out.println(distance.d);

	}

}
