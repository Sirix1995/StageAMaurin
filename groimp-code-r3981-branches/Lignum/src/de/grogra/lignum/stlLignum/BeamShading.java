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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class BeamShading implements Mathsym {
	
	//Original Comment:
/////////////////////////////////////////////////////////
//Cylinder beamshading for conifers
////////////////////////////////////////////////////////

//If the beam (starting from 'r0' with direction 'b') hits the shoot
//(radius including foliage 'Rs', length 'L', position 'rs' and direction 'a'), 
//but not the woody part (radius 'Rw'), 
//beamShading returns +1 and the distance (m) of the beam traversed
//in the shoot in the variable distance.
//If the beam hits the woody part of the shoot beamShading returns -1.
//In the case of no hit beamShading returns 0.
//
//NOTE:  It is assumed that |a| = 1 & |b| = 1 !!
	
	//The class Distance is used instead of double in order to be able to change the value within the method
	public static int CylinderBeamShading(Point3d r0_1, PositionVector b, Point3d rs_1,PositionVector a, 
			double Rs, double Rw, double L, Distance distance ) {
		
		distance.d = 0.0;
		
		PositionVector rs = new PositionVector(rs_1.x, rs_1.y, rs_1.z);
		PositionVector r0 = new PositionVector(r0_1.x, r0_1.y, r0_1.z);
		PositionVector dummy = new PositionVector();
		//Original Comment:
		  //	1.	Rough testing
		
		if(rs.z <= (r0.z -L)) {
			return NO_HIT; // Subject shoot is higher
		}
		
		double apu1, apu2, apu3;
		apu1 = abs(rs.x - r0.x);
		apu2  = abs(rs.y - r0.y);
		apu3  = abs(rs.z - r0.z);
		
		if(max(apu1, max(apu2, apu3))> L){
			dummy.subtract(rs,r0);
			if(b.dot(dummy) < 0.0) return NO_HIT;
		}
		// Shading shoot not in direction pointed by b
		
		
		//Intermediate dot products and calculations

		  // Dot products
		
		PositionVector rdiff = new PositionVector();
		double ab = 0.0, rdiffa = 0.0 , rdiffb = 0.0;
		double rdiff2 = 0.0;
		double p1, p2;
		PositionVector rHit = new PositionVector();
		PositionVector rd = new PositionVector();
		PositionVector rd1 = new PositionVector();
		//PositionVector rd2 = new PositionVector();
		PositionVector rs1 = new PositionVector();
		double any;
		
		rdiff.subtract(rs, r0);
		
		ab = a.dot(b);
		rdiffa = rdiff.dot(a);
		rdiffb = rdiff.dot(b);
		rdiff2 = rdiff.dot(rdiff);
		
		//Original Comment:
		//2. Test for a special case: if a || b, then the only possibility
		// that the beam hits the shoot is that the hit occurs on the end disk.
		// If the hit occurs in one disc, then it occurs in the other also.
		// The beam hits the plane containing the end disk at rHit with
		// parameter value p1 
		
		
		if(ab > (1.0 - R_EPSILON) || (-ab) > (1.0 -R_EPSILON)){
			p1 = rdiffa /ab;
			if(p1<0.0) return NO_HIT; // Not possible that shading shoot is behind (p1<0)
			dummy.mul(p1,b);
			rHit.set(0,0,0);
			rHit.add(r0);
			rHit.add(dummy);	//rHit = r0 + p1 *b;
			rd.subtract(rHit, rs); //rd = rHit - rs;
			any =rd.dot(rd);
			if(any > pow(Rs,2)) {return NO_HIT;} 	// Not inside the end disk
			else if (any > pow(Rw,2)){
				distance.d = L; 		//Through needles all the length
				return HIT_THE_FOLIAGE;
			}
			else {return HIT_THE_WOOD;}			
		}
		
		//Original Comment:
		 // 3. Does the beam hit the the cylinder with radius Rs?
		
		double c2Over2, c1, c3, discriminantOver4;
		PositionVector r1 = new PositionVector();
		PositionVector r2 = new PositionVector();
		boolean firstHits = false, secondHits = false;
		
		c2Over2 = rdiffa* ab -rdiffb;
		c1 = 1.0 - pow(ab,2);
		c3 = rdiff2 - pow(rdiffa,2) - pow(Rs,2);
		discriminantOver4 = pow(c2Over2, 2) -c1 *c3;
		
		if(discriminantOver4 <0.0) {return NO_HIT;} // Does not hit
		
		//Original Comment:
		//	4. Check here in between if the beam hits the wood cylinder inside
		//	the shoot. First check if hits the cylinder extending to infinity
		//	and then if the hit point is inside the shoot.
		//	After this point checks concerning the wood cylinder not necessary,
		//	except for end disks. 
		
		PositionVector rw1 = new PositionVector();
		PositionVector rw2 = new PositionVector();
		double c3w, discriminantOver4w;
		
		c3w = rdiff2 - pow(rdiffa, 2) - pow(Rw,2);
		discriminantOver4w = pow(c2Over2, 2) -c1 * c3w;
		
		if(discriminantOver4w > 0.0) {
			p1 = (rdiffa * ab -rdiffb + sqrt(discriminantOver4w)) / (pow(ab,2)- 1.0);
			dummy.mul(p1,b);
			rw1.set(0,0,0);
			rw1.add(r0);
			rw1.add(dummy); //rw1 = r0 +p1 *b;
			rd.subtract(rw1, rs);
			any = a.dot(rd);
			
			if(any > 0.0 && any <L) {return HIT_THE_WOOD;} // First hit point in the wood cylinder
			
			p2 = (rdiffa * ab - rdiffb - sqrt (discriminantOver4w)) / (pow(ab,2)-1.0);
			
			dummy.mul(p2, b);
			rw2.set(0,0,0);
			rw2.add(r0);
			rw2.add(dummy); //rw2 = r0 +pw*b;
			rd.subtract(rw2, rs);
			any = a.dot(rd);
			
			if(any > 0.0 && any <L) {return HIT_THE_WOOD;} //2nd hit point in the wood cylinder
							
		}
		
		//Original Comment:
		// 5	Continue with: Beam hits the cylinder extending to infinity
		
		p1 = ( rdiffa * ab - rdiffb + sqrt(discriminantOver4) ) /(pow(ab, 2) - 1.0);
			 
	    p2 = ( rdiffa * ab - rdiffb - sqrt(discriminantOver4) ) /(pow(ab, 2) - 1.0);
		
	    if( p1 < 0.0 && p2 < 0.0) {return NO_HIT;} 	// In this case p0 outside the cylinder, cannot hit
	    // the shoot cylinder in positive direction of b
	    
	    dummy.mul(p1, b);
	    r1.set(0,0,0);
	    r1.add(r0);
	    r1.add(dummy); //r1 = r0 + p1 * b;
	    
	    dummy.mul(p2, b);
	    r2.set(0,0,0);
	    r2.add(r0);
	    r2.add(dummy);//  r2 = r0 + p2 * b;
	    
	    //Original Comment:
	    // Does beam hit the shoot cylinder?
	    // that is 0 < (ri-rs)*a < L
	    // (|a| = 1 !)
	    
	   rd.subtract(r1, rs);
	   any = a.dot(rd);
	   if(any > 0.0 && any < L ) {firstHits = true;}
	   
	   rd.subtract(r2, rs);
	   any = a.dot(rd);
	   if(any > 0.0 && any < L) {secondHits = true;}
	   
	   //Original Comment:
	   //	6. One member of Cartesian product 
	   //	{mantle, end disk} x {mantle, end disk} or no hit possible 
	    
	   PositionVector rHit1 = new PositionVector();
	   PositionVector rHit2 = new PositionVector();
	   
	   if(firstHits)
		   if(secondHits){
			   rd.subtract(r1, r2);
			   distance.d = sqrt(rd.dot(rd));
			   return HIT_THE_FOLIAGE;
		   }
		   else { // Must be either of end disks
				   p1 = rdiffa / ab;
				   // N.B. Cannot come into this branch if a perpen-
				   // dicular to b (a*b = 0), since in that case both
				   // firstHits and secondHits must be true
				   dummy.mul(p1,b);
				   rHit.set(0,0,0);
				   rHit.add(r0);
				   rHit.add(dummy); //rHit = r0 + p1 * b;
				   rd.subtract(rHit, rs);
				   any = rd.dot(rd);
				   if(any <(pow(Rs,2)))
				if(any < (pow(Rw,2))) {//Yes, this, check for
					  // wood end disk
					  //Should be impossible to end up here
					  //since if (hits wood end disk) & 
					  //(hits mantle of shoot cyl) =>
					  //hits mantle of wood cyl, which has
					  //already been tested
					  return  HIT_THE_WOOD;
				}
				else{
					rd1.subtract(r1, rHit);
					distance.d = sqrt(rd1.dot(rd1));
					return HIT_THE_FOLIAGE;
				}
				// Was not the first one
				   dummy.mul(L, a);
				   rs1.set(0,0,0);
				   rs1.add(rs);
				   rs1.add(dummy); //rs1 = rs + L * a;
				   rd1.subtract(rs1, r0);
				   p1 = a.dot(rd1)/ab;
				   dummy.mul(p1, b);
				   rHit.set(0,0,0);
				   rHit.add(r0);
				   rHit.add(dummy); //rHit = r0 + p1 * b;
				   rd.subtract(rHit, rs1);
				   any = rd.dot(rd);
				   if(any <(pow(Rs,2)))
				if(any < (pow(Rw,2))){
					//impossible, see above
					return  HIT_THE_WOOD;
				}
				else	{
					rd1.subtract(r1, rHit);
					distance.d = sqrt(rd1.dot(rd1));
					return HIT_THE_FOLIAGE;
				}
				   else{//Error condition; this should not happen;
					   return HIT_THE_WOOD;
				   }
		   }
	   else //firstHits not true 
		    if(secondHits)	{	//Must be either of end disks
		        p1 = rdiffa / ab;
		        // N.B. Cannot come into this branch if a perpen-
		        // dicular to b (a*b = 0), since in that case both
		        // firstHits and secondHits must be true
		        dummy.mul(p1, b);
		        rHit.set(0,0,0);
		        rHit.add(r0);
		        rHit.add(dummy);//   rHit = r0 + p1 * b;
		        rd.subtract(rHit, rs);
		        any = rd.dot(rd);
		        if(any < (pow(Rs,2)))
		    if(any<(pow(Rw,2))){//Yes, this, check for wood end disk
		  	  //impossible, see above
		  	  return  HIT_THE_WOOD;
		    }
		    else {
		    	rd1.subtract(r2, rHit);
		    	distance.d = sqrt(rd1.dot(rd1));
		    	return HIT_THE_FOLIAGE;
		    }
		        // Was not the first one
		        dummy.mul(L,a);
		        rs1.set(0,0,0);
		        rs1.add(rs);
		        rs1.add(dummy);// rs1 = rs + L * a;
		        rd1.subtract(rs1, r0);
		        p1 = (a.dot(rd1))/ab;
		        dummy.mul(p1,b);
		        rHit.set(0,0,0);
		        rHit.add(r0);
		        rHit.add(dummy);//rHit = r0 + p1 * b;
		        rd.subtract(rHit, rs1);
		        any=(rd.dot(rd));
		        if(any < (pow(Rs,2)))
		      if(any < (pow(Rw,2))) {//Yes, this, check for wood end disk
		      	  //impossible, see above
		      	  return  HIT_THE_WOOD;
		        }
		      else {
		    	  rd1.subtract(r2, rHit);
		    	  distance.d = sqrt(rd1.dot(rd1));
		    	  return HIT_THE_FOLIAGE;
		      }
		        else //Error condition; this should not happen; return 3
		        	return  HIT_THE_WOOD;
		    }
		    else {// Only end disk-in end disk-out possible
		        p1 = rdiffa / ab;
		        if(p1 < 0.0)
		      return NO_HIT;		// Don't look back!
		        dummy.mul(p1, b);
		        rHit1.set(0,0,0);
		        rHit1.add(r0);
		        rHit1.add(dummy);//rHit1 = r0 + p1 * b;
		        rd.subtract(rHit1, rs);
		        any = rd.dot(rd);
		        if(any >(pow(Rs,2))) {
		       return NO_HIT;
		        }
		        else if(any <(pow(Rw,2)))//Yes, this, check for wood end disk
		        	return  HIT_THE_WOOD;
		        
		        // The first one yes, now the second one
		        dummy.mul(L,a);
		        rs1.set(0,0,0);
		        rs1.add(rs);
		        rs1.add(dummy);//rs1 = rs + L * a;
		        rd1.subtract(rs1, r0);
		        p1= (a.dot(rd1))/ab;
		        if(p1<0.0)
		      return NO_HIT;
		        dummy.mul(p1, b);
		        rHit2.set(0,0,0);
		        rHit2.add(r0);
		        rHit2.add(dummy);//rHit2 = r0 + p1 * b;
		        rd.subtract(rHit2, rs1);
		        any = rd.dot(rd);
		        if(any < (pow(Rs,2)))
		       if(any < (pow(Rw,2)))//Yes, this, check for wood end disk
		    		  return  HIT_THE_WOOD;
		       else {
		    	   rd.subtract(rHit2, rHit1);
			       distance.d = sqrt(rd.dot(rd));
			       return HIT_THE_FOLIAGE;
		    	   
		       }
		        else {}
		        return NO_HIT;
		       }

	}

}
