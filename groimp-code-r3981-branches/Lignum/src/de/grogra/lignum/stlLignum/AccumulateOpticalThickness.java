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

import static java.lang.Math.max;
import static java.lang.Math.pow;
import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.stlVoxelspace.VoxelMovement;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class AccumulateOpticalThickness implements Mathsym {

	
	public AccumulateOpticalThickness(double side, double a, double b) {
		box_side_length = side;
		par_a = a;
		par_b = b;
		box_volume = pow(box_side_length,3.0);
	}
	
	private final double box_side_length;
	private final double box_volume;
	private final double par_a, par_b;
	
	public double eval(double o_d, VoxelMovement vm) {
		
		if(vm.af > R_EPSILON && vm.n_segs_real > 0.0){
			//Original Comment:
			//	      LGMdouble k = pow(box_side_length,par_a)*pow(vm.n_segs_real,par_b)*
		    //      LGMdouble k = par_a*pow((vm.af/box_volume),par_b)*
		    //	max(0.0,-0.014+1.056*vm.STAR_mean);     //NOTE: here transformation STAR_eq
			double k = max(0.0,-0.014+1.056*vm.STAR_mean);
			//Original Comment:
			// --> STAR; documented in
		    //~/Riston-D/E/LIGNUM/Light/summer-09-test/STAR-vs-STAR_eq.pdf
			 o_d += k * vm.af * vm.l / box_volume;
		      /* cout << "xn yn zn Af l nseg boxs boxv sST_M k " << vm.x << " " << vm.y << " " << vm.z << " " << vm.af */
		      /*      << " " <<  vm.l << " " << vm.n_segs_real << " " << box_side_length << " " << box_volume << " " << */
		      /*   vm.STAR_mean << " " << k << endl; */
		}
		return o_d;
	}
}
