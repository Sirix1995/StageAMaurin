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

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.pow;

import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.sky.FirmamentWithMask;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public class ShadingEffectOfCfTreeSegment implements Mathsym {

	protected CfTreeSegment shaded_s;
	protected ParametricCurve K;
	protected Vector<Double> S;
	
	private FirmamentWithMask firmament = null;
	public ShadingEffectOfCfTreeSegment(FirmamentWithMask firmament, CfTreeSegment ts, ParametricCurve K_in, Vector<Double> sectors) {
		this.firmament = firmament;
		shaded_s =ts;
		K = new ParametricCurve(K_in);
		S = new Vector<Double>(sectors);
	}
	
	// Get vector for S (shadiness) 
	public Vector<Double> getS() {
		return S;
	}
	
	// ForEach functor to compute shadiness
	// Like () operator in cLignum
	public CfTreeSegment eval(CfTreeSegment tc) {
		
		// Don't compare to yourself
		if (tc ==shaded_s) return tc; //TODO: Is this correct ? Test !
		
		// Now go on computing shading
	    int i = 0, number_of_sectors = 0, result = NO_HIT;
	    Distance distance = new Distance(0.0);
	    Vector<Double> radiation_direction= new Vector<Double>(3);
	    for (int j = 0; j < 3; j++){
	    	radiation_direction.add(j, 0.0);
	    }
	    
	    Tree tt = tc.getTree();
	    
	    //FirmamentWithMask firmament = tt.GetFirmament();
	    
	    number_of_sectors = firmament.numberOfRegions();
	    
	    // Foliage density: Foliage area divided by volume. Perhaps a good idea to
	    // implement it as GetValue?
	    double af = tc.getLGAAf(); 
	    double fol_dens;
	    
	    if (af > R_EPSILON)
	    	fol_dens = af / (PI * (pow(tc.getLGARf(), 2.0) - pow(tc.getLGAR(), 2.0))
	        	* tc.getLGAL());
	    else
	        fol_dens = 0.0;
	    
	    for (i = 0; i < number_of_sectors; i++) {
	    	
	    	// If the sector is blocked by another shoot
	        // do not make computations, check the next sector instead
	    	if (S.get(i) == HIT_THE_WOOD) { 
	        	continue;
	        }
	        
	        // The radiation and its direction of sector i. We need the direction
	        firmament.diffuseRegionRadiationSum(i,radiation_direction);
	    	
	    	Point3d r_0 = new Point3d(shaded_s.getPoint());	//TODO: Could this be calculated just once
	    	//and saved as a variable of ShadingEffectOf... ?
	    	PositionVector dummy = new PositionVector(shaded_s.getDirection());
	    	dummy.mul(0.5 * shaded_s.getLGAL());
	    	// Midpoint of shaded seg
	    	r_0.add(dummy); 
			// In cLignum:
			// Point r_0 =  GetPoint(*shaded_s)+0.5*GetValue(*shaded_s,LGAL)*
			// (Point)GetDirection(*shaded_s);  
	    	
	    	PositionVector radiation_directionPV = new PositionVector(radiation_direction);
	    	result = BeamShading.CylinderBeamShading(r_0,
					   radiation_directionPV,
					   tc.getPoint(),
					   tc.getDirection(),
					   tc.getLGARf(),
					   tc.getLGAR(),
					   tc.getLGAL(),
					   distance);
	    	
	    	if (result == HIT_THE_WOOD) {
	    		// mark the sector blocked 
	    		S.set(i, (double)HIT_THE_WOOD);
	    	}
	    	else if (result == HIT_THE_FOLIAGE) {
	    		// otherwise compute Vp (the shadiness):
	    		// 1. compute the inclination of light beam and the segment
	    		// 1a. angle between segment and light beam 
	    		PositionVector dummy2 = new PositionVector(tc.getDirection());
	    		double a_dot_b = dummy2.dot(radiation_directionPV);
	    		// 1b.  inclination: Perpendicular  (PI_DIV_2) to segment minus
	    		// angle between segment and light beam
	    		double inclination = PI_DIV_2 - acos(abs(a_dot_b));	
	    		// 2.the light extinction coefficient according to inclination
	    		double extinction = K.eval(inclination);
	    		// 3.Vp = extinction*distance*foliage_density
	    		double Vp = extinction * distance.d * fol_dens;
	    		S.set(i, S.get(i) + Vp);
	    	 }
	    }
	    
	    return tc;
	}
	
	
}
