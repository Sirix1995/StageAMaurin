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
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.util.Iterator;
import java.util.Vector;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.sky.FirmamentWithMask;

/**
 * EvaluateRadiationForCfTreeSegment evaluates shading caused by all other segments 
 * on this conifer segment. It uses ShadingEffectOfCfTreeSegment to go through all
 * segments to check the shading.
 * 
 * Translated from orignal C++ Lignum code.
 *
 * 
 */ 
public class EvaluateRadiationForCfTreeSegment implements Mathsym {
	
	private ParametricCurve K;
	
	private FirmamentWithMask firmament = null;
	private int number_of_sectors = 0;
	
	/**
	 * 
	 * @param firmament
	 */
	public EvaluateRadiationForCfTreeSegment(FirmamentWithMask firmament) {
		this.firmament = firmament;
		number_of_sectors = firmament.numberOfRegions();
	}

	/**
	 * 
	 * @param k
	 */
	public void setTreeParameters(ParametricCurve k) {
		K = new ParametricCurve(k);
	}
	
	//Like the () operator in cLignum
	public CfTreeSegment eval(CfTreeSegment tc) {
		
		tc.setLGAQin(0.0);
		tc.setLGAQabs(0.0);
		
		// Radiation conditions are not evaluated if the segment has no
	    // foliage (in practice there would be division by 0 in computing
	    // absorbed radiation)
		if (tc.getLGAWf() < R_EPSILON) {
			return tc;
		}
		
		Tree tt = tc.getTree();
		
		double a_dot_b = 0.0;
		Vector<Double> radiation_direction = new Vector<Double>(3);
		radiation_direction.add(0,0d);
		radiation_direction.add(1,0d);
		radiation_direction.add(2,0d);
		Vector<Double> v = new Vector<Double>(number_of_sectors);
		for (int i=0;i<number_of_sectors;i++) {
			v.add(i, 0d);
		}
		
		ShadingEffectOfCfTreeSegment s_e = new ShadingEffectOfCfTreeSegment(firmament, tc, K, v);
		// This  goes through the tree and computes shading based on
		// 1) distance light beam traverses in foliage, 2)foliage density
		// and 3) inclination light beam hits the segment.
		tt.forEachShadingEffectOfCfTreeSegment(tt, s_e);
		
		// implement "Ip = Iope^(-Vp)", s[i] = radiation coming from
		// direction i after this
		Vector<Double> s = new Vector<Double>(s_e.getS());
		for (int i = 0; i < number_of_sectors; i++) {
		    if (s.get(i) == HIT_THE_WOOD) {
		    	s.set(i, 0.0);
		    } else {
		    	// this is the second difference to EvaluateRadiationForCfTreeSegment!
		    	double Iop = firmament.diffuseRegionRadiationSum
		    		(i, radiation_direction);
		    	s.set(i, Iop * exp(-s.get(i)));
		   }
		}
		
		// Total incoming radiation 
		double Q_in = 0.0;
		Iterator<Double> iter = s.iterator();
		while (iter.hasNext()) {
			Q_in += iter.next();
		}
	    
	    
		// s contains now incoming radiation from each sector. Evaluate how
		// much segment absorbs from incoming radiation.
		double Lk, inclination, Rfk, Ack, extinction, sfk, Ask, Wfk;
		Lk = Rfk = Ack =  extinction = sfk = Ask = Wfk = 0.0;
		// length is > 0.0, otherwise we would not bee here
		Lk = tc.getLGAL();
		// Radius to foliage limit
		Rfk = tc.getLGARf();
		// Foliage mass
		Wfk = tc.getLGAWf();
		// Foliage m2/kg from tree
		sfk = tc.getTree().getLGPsf();
	    
	    for (int i = 0; i < number_of_sectors; i++) {
	        firmament.diffuseRegionRadiationSum(i,radiation_direction);
	        PositionVector dummy = new PositionVector(radiation_direction);
	        a_dot_b = tc.getDirection().dot(dummy);
	        inclination = PI_DIV_2 - acos(abs(a_dot_b));
	        
	        Ack = 2.0 * Lk * Rfk * cos(inclination) + PI * pow(Rfk, 2.0) * sin(inclination);
	        extinction = K.eval(inclination);
	        
	        if (Ack == 0.0) {
	        	System.out.println("ERROR EvaluateRadiationForCfTreeSegment: Ack == 0 (division by 0)");
	        }
	        
	        // implement I(k)p = Ip*Ask, Note Ack must be greater than 0 (it
	        // should if there is any foliage)
	        Ask = (1.0 - exp(-extinction * ((sfk * Wfk) / Ack))) * Ack;
	        s.set(i, s.get(i) * Ask);
	  
	    }
	    
	    double Q_abs = 0.0;
	    Iterator<Double> iter_s = s.iterator();
	    while (iter_s.hasNext()) {
	    	Q_abs += iter_s.next();
	    }
	    
	    tc.setLGAQabs(Q_abs);
	    tc.setLGAQin(Q_in);
	    
	    return tc;
	}
}