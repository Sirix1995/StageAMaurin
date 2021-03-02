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

package de.grogra.lignum.sky; //Package name like namespace in cLignum

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

import java.util.Vector;

import javax.vecmath.GMatrix;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.ray2.radiosity.Vector3d;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class Firmament implements Mathsym {
	
	protected static int NUM_OF_AZIM = 24;
	protected static int NUM_OF_INCL = 9;
	//private static double R_EPSILON = 1.0e-20;  // In cLignum included in mathsym.h
	//Change later to Interface or something like that.

	protected int num_of_incl;
	protected int num_of_azim;
	protected double diffuseRadScale;
	protected double directRadPlane, diffuseRadPlane, diffuseRadBall; //Originally typedef MJ
	protected double diffuseRadZenith;//Originally typedef MJ
	protected Vector<Double> sunPosition = new Vector<Double>();
	
	protected GMatrix zoneAzims = new GMatrix(1,1); //GMatrix is used instead of TMatrix (cLginum)
	protected Vector<Double> inclinations = new Vector<Double>();
	protected Vector<Integer> azimDivisions = new Vector<Integer>();
	protected GMatrix diffuseRad = new GMatrix(1,1);
	protected Vector<Integer> inclinationIndex = new Vector<Integer>();//tabulation of index of inclination in
												//diffuseRad as a function of running segment no.
	protected Vector<Integer> azimuthIndex = new Vector<Integer>();  //as inclinationIndex but for azimuths;
	protected Vector<Double> dir_x = new Vector<Double>();		 //x-component of vector pointing to center
    									//of ith sector (indexed by number of sector)
	protected Vector<Double> dir_y = new Vector<Double>();		//as dir_x but for y
	protected Vector<Double> dir_z = new Vector<Double>(); 	//as dir_x but for z
	protected Vector<Double> areasByInclination = new Vector<Double>(); //areas sectors in for each inclination
	protected double thetZ;
	protected int numOfSectors;
	protected double deltaIncl, halfDeltaIncl;
	protected double standDensity;
	
	//Instead of constructor with default values:
	public Firmament() {
		this(NUM_OF_INCL,NUM_OF_AZIM);
	}
	
	public Firmament(int no_incl, int no_azim) {
		
		sunPosition = new Vector<Double>(3);
		inclinations = new Vector<Double>(no_incl);
		azimDivisions = new Vector<Integer>(no_incl);
		inclinationIndex = new Vector<Integer>(no_incl*no_azim);
		azimuthIndex = new Vector<Integer>(no_incl*no_azim);
		dir_x = new Vector<Double>(no_incl*no_azim);
		dir_y = new Vector<Double>(no_incl*no_azim);
		dir_z = new Vector<Double>(no_incl*no_azim);
		
		num_of_incl = no_incl;
		num_of_azim =  no_azim;
		if(num_of_incl < 1) num_of_incl = 1;
		if(num_of_azim < 2) num_of_azim = 2;
		numOfSectors =  num_of_incl *  num_of_azim + 1;
		
		//Original Comment:
		//The radiation components are set as follows:
		//diffuseRadPlane = 1200 MJ(PAR)/m2,   equal to radition sum (direct+diffuse) during
		//growing period in Finland according to Stenberg 1996
		//directRadPlane = 0

		diffuseRadPlane = 1200.0;
		directRadPlane = 0.0;
		diffuseRadBall = 0.0;
		sunPosition.setSize(3);
		sunPosition.set(0,0.0);
		sunPosition.set(1,0.0);
		sunPosition.set(2,0.0);
		
		//Construct the division of the sky (see comment at the beginning)
		//Original Comment:
		//The zenith segment of the sphere is as large (= 2*PI/numOfSectors)
		//as the other sectors; its width (angle)  
		
		thetZ = acos( 1.0 - 1.0 / numOfSectors );
		deltaIncl = (PI / 2.0 - thetZ)/ num_of_incl;
		halfDeltaIncl = deltaIncl / 2.0;
		
		//Original Comment:
		//Set the right dimensions for vectors storing midpoints of inclination zones
		//and number of azimuth divisions in each inclination zone
		inclinations.setSize(num_of_incl);
		azimDivisions.setSize(num_of_incl);
		
		int i, im;
		i=im=0;
		
		for(i = 0; i < num_of_incl; i++) {
		    inclinations.set(i,(i * deltaIncl + halfDeltaIncl)); }
		//Original Comment:
		//Set up vector for storing areas of sectors in inclination zones
		//for getting equal area sectors
		
		double area[], maxA; //area was a pointer in cLignum
		area = new double[num_of_incl];
		
		areasByInclination.setSize(num_of_incl);
		
		//Original Comment:
		//Allocate sectors to zones trying to end up with as constant sector
		//area (=solid angle) as possible
		//Start off with two sectors in each zone

		for(i = 0; i < num_of_incl; i++) {
			azimDivisions.set(i,2);
			area[i] = sin(inclinations.get(i)+halfDeltaIncl) - sin(inclinations.get(i)-halfDeltaIncl);
			area[i] *= 2.0 * PI / (double)azimDivisions.get(i);
			  } 
		
		int j;
		i = numOfSectors - 2 * num_of_incl - 1;
		while(i > 0) {
			maxA = 0.0;
			for(j = 0; j < num_of_incl; j++){
				if(area[j] > maxA) {
					maxA = area[j];
					im = j;
					}}
			area[im] *= (double)azimDivisions.get(im) / (double)(azimDivisions.get(im) + 1);
			areasByInclination.set(im,area[im]);
			azimDivisions.set(im,azimDivisions.get(im)+1);
			i--;
		  }
		
		//Original Comment:
	    //Evaluate the maximum number of sectors in the inclination zones and
	    //adjust dimensions of matrices holding azimuths and (diffuse) radiant intensity
	    //of sectors accordingly
		
		im = 0;
	    for(i = 0; i < num_of_incl; i++){
	      if(im < azimDivisions.get(i)) im = azimDivisions.get(i);
	    }
	    
	    zoneAzims.setSize(num_of_incl, im);
	    diffuseRad.setSize(num_of_incl, im);
	    
	    //Original Comment:
	    //Update azimuths and radiant intensity of sectors
	    //shift creates variation in starting points of azimuths
	    
	    for(i = 0; i < num_of_incl; i++) {
	        double shift = (i)/3.0 -i/3;
	        for(j = 0; j < azimDivisions.get(i); j++) {
	        	zoneAzims.setElement(i,j,((j + shift) * 2.0 * PI/(double)azimDivisions.get(i)));
	        	diffuseRad.setElement(i,j,( diffuseRadPlane * (6.0 * (1.0 + 2.0*sin(inclinations.get(i)))/7.0) *(sin(inclinations.get(i)+halfDeltaIncl)- sin(inclinations.get(i)-halfDeltaIncl))/(double)azimDivisions.get(i)));
	        }
	      }
	    
	    //Original Comment:
	    //Diffuse radiation from zenith sector
	    
	    diffuseRadZenith = diffuseRadPlane *(6.0 * (1.0 + 2.0)/7.0) *(1 - cos(thetZ));
	    
	    //Original Comment:
	    // Note: thetZ is the radial _width_ of the zenith segment

	    //Original Comment:
	    //Discretation of the sky may cause that the radiation sum of the sectors
	    //is not 100% same as given by the theory. It is corrected here.
	    //Update also ball sensor reading
	    
	    double rsum = 0.0;
	    for(i = 0; i < num_of_incl; i++){
	      for(j = 0; j < azimDivisions.get(i); j++){
	    	  rsum = rsum+ sin( inclinations.get(i) ) * diffuseRad.getElement(i, j);
	      }		}

	    rsum += diffuseRadZenith;
	    
	    diffuseRadBall = 0.0;
	    for(i = 0; i < num_of_incl; i++) {
	      for(j = 0; j < azimDivisions.get(i); j++) {
	    	  diffuseRad.setElement(i,j,(diffuseRad.getElement(i, j) * diffuseRadPlane / rsum));
	    	  diffuseRadBall += diffuseRad.getElement(i, j);
	      } }
	
	    diffuseRadZenith  *= diffuseRadPlane / rsum;
	    diffuseRadBall += diffuseRadZenith;
	    
	    //Original Comment:
	    //store here the inclination and azimuth indexes as a function of
	    //number of the sector
	
	    inclinationIndex.setSize(numOfSectors);
	    azimuthIndex.setSize(numOfSectors);
	    int nSector = 0;
	    for(i = 0; i < num_of_incl; i++){
	      for(j = 0; j < azimDivisions.get(i); j++) {
		inclinationIndex.set(nSector, i);
		azimuthIndex.set(nSector, j);
		nSector++;
	      } }
	    
	    //Original Comment:
	    //store the components of direction vectors of sectors (midpoint)
	    
	    dir_x.setSize(numOfSectors);
	    dir_y.setSize(numOfSectors);
	    dir_z.setSize(numOfSectors);
	    int nIncl, nAzim;
	    nSector = 0;
	    for(i = 0; i < num_of_incl; i++) {
	      for(j = 0; j < azimDivisions.get(i); j++) {
	    	  nIncl  = inclinationIndex.get(nSector);
	    	  nAzim = azimuthIndex.get(nSector);
			dir_z.set(nSector,sin(inclinations.get(nIncl)));
			dir_x.set(nSector,(cos(inclinations.get(nIncl)) * cos(zoneAzims.getElement(nIncl, nAzim))));
			dir_y.set(nSector,(cos(inclinations.get(nIncl)) * sin(zoneAzims.getElement(nIncl, nAzim))));
			nSector++;
	      } }
	}
	
	public void resize(int no_incl, int no_azim, double diffuse_rad_plane)
	{
		//sunPosition = new Vector<Double>(3);
		//inclinations = new Vector<Double>(no_incl);
		//azimDivisions = new Vector<Integer>(no_incl);
		//inclinationIndex = new Vector<Integer>(no_incl*no_azim);
		//azimuthIndex = new Vector<Integer>(no_incl*no_azim);
		//dir_x = new Vector<Double>(no_incl*no_azim);
		//dir_y = new Vector<Double>(no_incl*no_azim);
		//dir_z = new Vector<Double>(no_incl*no_azim);
		
		num_of_incl = no_incl;
		num_of_azim =  no_azim;
		if(num_of_incl < 1) num_of_incl = 1;
		if(num_of_azim < 2) num_of_azim = 2;
		numOfSectors =  num_of_incl *  num_of_azim + 1;
		
		//Original Comment:
		//The radiation components are set as follows:
		//diffuseRadPlane = 1200 MJ(PAR)/m2,   equal to radition sum (direct+diffuse) during
		//growing period in Finland according to Stenberg 1996
		//directRadPlane = 0

		diffuseRadPlane = diffuse_rad_plane;
		directRadPlane = 0.0;
		diffuseRadBall = 0.0;
		sunPosition.setSize(3);
		sunPosition.set(0,0.0);
		sunPosition.set(1,0.0);
		sunPosition.set(2,0.0);
		
		//Construct the division of the sky (see comment at the beginning)
		//Original Comment:
		//The zenith segment of the sphere is as large (= 2*PI/numOfSectors)
		//as the other sectors; its width (angle)  
		
		thetZ = acos( 1.0 - 1.0 / numOfSectors );
		deltaIncl = (PI / 2.0 - thetZ)/ num_of_incl;
		halfDeltaIncl = deltaIncl / 2.0;
		
		//Original Comment:
		//Set the right dimensions for vectors storing midpoints of inclination zones
		//and number of azimuth divisions in each inclination zone
		inclinations.setSize(num_of_incl);
		azimDivisions.setSize(num_of_incl);
		
		int i, im;
		i=im=0;
		
		for(i = 0; i < num_of_incl; i++) {
		    inclinations.set(i,(i * deltaIncl + halfDeltaIncl)); }
		//Original Comment:
		//Set up vector for storing areas of sectors in inclination zones
		//for getting equal area sectors
		
		double area[], maxA; //area was a pointer in cLignum
		area = new double[num_of_incl];
		
		areasByInclination.setSize(num_of_incl);
		
		//Original Comment:
		//Allocate sectors to zones trying to end up with as constant sector
		//area (=solid angle) as possible
		//Start off with two sectors in each zone

		for(i = 0; i < num_of_incl; i++) {
			azimDivisions.set(i,2);
			area[i] = sin(inclinations.get(i)+halfDeltaIncl) - sin(inclinations.get(i)-halfDeltaIncl);
			area[i] *= 2.0 * PI / (double)azimDivisions.get(i);
			  } 
		
		int j;
		i = numOfSectors - 2 * num_of_incl - 1;
		while(i > 0) {
			maxA = 0.0;
			for(j = 0; j < num_of_incl; j++){
				if(area[j] > maxA) {
					maxA = area[j];
					im = j;
					}}
			area[im] *= (double)azimDivisions.get(im) / (double)(azimDivisions.get(im) + 1);
			areasByInclination.set(im,area[im]);
			azimDivisions.set(im,azimDivisions.get(im)+1);
			i--;
		  }
		
		//Original Comment:
	    //Evaluate the maximum number of sectors in the inclination zones and
	    //adjust dimensions of matrices holding azimuths and (diffuse) radiant intensity
	    //of sectors accordingly
		
		im = 0;
	    for(i = 0; i < num_of_incl; i++){
	      if(im < azimDivisions.get(i)) im = azimDivisions.get(i);
	    }
	    
	    zoneAzims.setSize(num_of_incl, im);
	    diffuseRad.setSize(num_of_incl, im);
	    
	    //Original Comment:
	    //Update azimuths and radiant intensity of sectors
	    //shift creates variation in starting points of azimuths
	    
	    for(i = 0; i < num_of_incl; i++) {
	        double shift = (i)/3.0 -i/3;
	        for(j = 0; j < azimDivisions.get(i); j++) {
	        	zoneAzims.setElement(i,j,((j + shift) * 2.0 * PI/(double)azimDivisions.get(i)));
	        	diffuseRad.setElement(i,j,( diffuseRadPlane * (6.0 * (1.0 + 2.0*sin(inclinations.get(i)))/7.0) *(sin(inclinations.get(i)+halfDeltaIncl)- sin(inclinations.get(i)-halfDeltaIncl))/(double)azimDivisions.get(i)));
	        }
	      }
	    
	    //Original Comment:
	    //Diffuse radiation from zenith sector
	    
	    diffuseRadZenith = diffuseRadPlane *(6.0 * (1.0 + 2.0)/7.0) *(1 - cos(thetZ));
	    
	    //Original Comment:
	    // Note: thetZ is the radial _width_ of the zenith segment

	    //Original Comment:
	    //Discretation of the sky may cause that the radiation sum of the sectors
	    //is not 100% same as given by the theory. It is corrected here.
	    //Update also ball sensor reading
	    
	    double rsum = 0.0;
	    for(i = 0; i < num_of_incl; i++){
	      for(j = 0; j < azimDivisions.get(i); j++){
	    	  rsum = rsum+ sin( inclinations.get(i) ) * diffuseRad.getElement(i, j);
	      }		}

	    rsum += diffuseRadZenith;
	    
	    diffuseRadBall = 0.0;
	    for(i = 0; i < num_of_incl; i++) {
	      for(j = 0; j < azimDivisions.get(i); j++) {
	    	  diffuseRad.setElement(i,j,(diffuseRad.getElement(i, j) * diffuseRadPlane / rsum));
	    	  diffuseRadBall += diffuseRad.getElement(i, j);
	      } }
	
	    diffuseRadZenith  *= diffuseRadPlane / rsum;
	    diffuseRadBall += diffuseRadZenith;
	    
	    //Original Comment:
	    //store here the inclination and azimuth indexes as a function of
	    //number of the sector
	
	    inclinationIndex.setSize(numOfSectors);
	    azimuthIndex.setSize(numOfSectors);
	    int nSector = 0;
	    for(i = 0; i < num_of_incl; i++){
	      for(j = 0; j < azimDivisions.get(i); j++) {
		inclinationIndex.set(nSector, i);
		azimuthIndex.set(nSector, j);
		nSector++;
	      } }
	    
	    //Original Comment:
	    //store the components of direction vectors of sectors (midpoint)
	    
	    dir_x.setSize(numOfSectors);
	    dir_y.setSize(numOfSectors);
	    dir_z.setSize(numOfSectors);
	    int nIncl, nAzim;
	    nSector = 0;
	    for(i = 0; i < num_of_incl; i++) {
	      for(j = 0; j < azimDivisions.get(i); j++) {
	    	  nIncl  = inclinationIndex.get(nSector);
	    	  nAzim = azimuthIndex.get(nSector);
			dir_z.set(nSector,sin(inclinations.get(nIncl)));
			dir_x.set(nSector,(cos(inclinations.get(nIncl)) * cos(zoneAzims.getElement(nIncl, nAzim))));
			dir_y.set(nSector,(cos(inclinations.get(nIncl)) * sin(zoneAzims.getElement(nIncl, nAzim))));
			nSector++;
	      } }
	}
	
		//Original Comment:
		//Input: vector 'direction' (length of 1) 
		// pointing to a point in the upper hemisphere.
		//	 x-axis is pointing to south, y-axis to east and z -axis to zenith
		//Return: Diffuse radiation intensity coming  from the area of
		//       the upper hemisphere pointed by
		//	  direction. If direction is pointing to lower hemisphere,
		//	  returns 0.0
	
	public double diffuseRadiationSum(Vector<Double> direction) //Original type MJ vector passed as const within cLignum
	{
		double rz, rx,ry;
		double theta, fii, cosTheta; //theta =inclination angle, fii = azimuth angle
		rz = direction.get(2);

		if( rz < 0.0 ) return 0.0;

		rx = direction.get(0);
		ry = direction.get(1);
		
		//Original Comment:
		//Change the direction vector (rx, ry, rz) to inclination and azimuth
		
		theta = asin( rz );  //inclination angle
		if(theta > PI / 2.0) theta = PI / 2.0;
		cosTheta = cos( theta );
	
		//Determine the azimuth angle phi:
		if((rx > R_EPSILON || -rx > R_EPSILON) && cosTheta > R_EPSILON)
			if(ry < 0.0)
			      fii = 2.0 * PI - acos( rx / cosTheta );
			    else
			      fii = acos( rx / cosTheta );
			  else if(ry > 0.0)
			    fii = PI / 2.0;
			  else
			    fii = 3.0 * PI / 4.0; // Why /4.0 and not /2.0?
			
		int ii, ia;
		
		//Original Comment: 
		//If ic == num_of_incl => zenith sector
		
		//Determine the index of inclination:
		for(ii = 0; ii < num_of_incl; ii++)
		    if(theta <= inclinations.get(ii) + halfDeltaIncl) break;
		
		if(ii == num_of_incl)
			    return diffuseRadZenith;
		 
		double halfDeltaAzim = PI / (double)azimDivisions.get(ii);
		//Determine the index of azimuth:
		for(ia = 0; ia < azimDivisions.get(ii); ia++)
		    if(fii <= zoneAzims.getElement(ii,ia) + halfDeltaAzim) break;
		
		if(ia ==  azimDivisions.get(ii)) ia = 0;
		
		return diffuseRad.getElement(ii,ia);

	}
	
	//in cLignum it is a const method:
	//in cLignum type MJ
	public double diffuseRegionRadiationSum(int n, Vector<Double> direction)
	{
		//Original Comment:
		// Input: # of region
		// Return: Intensity of diffuse radiation from the region and
		// vector 'direction' (length of 1)
		// pointing to the midpoint of the region.
		//x-axis is pointing to south, y-axis to east and z -axis to zenith.
		//If n < 0 or n > num_of_incl*num_of_azim - 1  returns -1.0
		
		if(n < 0 || n > numOfSectors - 1) return  -1.0;
		
		//Original Comment:
		// Numbering: azimuth is changing faster.
		// If, for example, n < azimDivisions[0]
		// nIncl = 0 and nAzim = n, aso.
		// if n == numOfSectors - 1 => zenith
		
		 if(n ==  numOfSectors - 1) {             // zenith
			    direction.set(0,0.0);
			    direction.set(1, 0.0);
			    direction.set(2,1.0);
			    return  diffuseRadZenith;
			  }
		 
		 int nIncl = inclinationIndex.get(n);
		 int nAzim = azimuthIndex.get(n);
		 
		 direction.set(2,dir_z.get(n));
		 direction.set(0, dir_x.get(n));
		 direction.set(1, dir_y.get(n));
		 
		 return diffuseRad.getElement(nIncl,nAzim);
		}
	
	//In cLignum method is const and returns type MJ:
	public double diffuseHalfRegionRadiationSum(int n, Vector<Double> direction)
	{
		//Original Comment:
		// As regionRadiationSum but regions having azimuth between
		// 0 and PI radiate nothing and for these halfRegionRadiationSum = 0

		// Input: # of region
		// Return: Radiation sum (direct & diffuse) from the region and
		// vector 'direction' (length of 1) pointing 
		// to the midpoint of the region.
		// x-axis is pointing to south, y-axis to east and z -axis to zenith.
		// If n < 0 or n > num_of_incl*num_of_azim - 1  returns -1.0
		
		if(n < 0 || n > numOfSectors - 1) return  -1.0;
		
		//Original Comment:
		// Numbering: azimuth is changing faster.
		// If, for example, n < azimDivisions[0]
		// nIncl = 0 and nAzim = n, aso.
		// if n == numOfSectors - 1 => zenith
		
		if(n ==  numOfSectors - 1) {             // zenith
		    direction.set(0,0.0);
		    direction.set(1, 0.0);
		    direction.set(2,1.0);
		    return  diffuseRadZenith;
		  }
		
		 int nIncl = inclinationIndex.get(n);
		 int nAzim = azimuthIndex.get(n);
		 
		 direction.set(2,dir_z.get(n));
		 direction.set(0, dir_x.get(n));
		 direction.set(1, dir_y.get(n));
		 
		 if(zoneAzims.getElement(nIncl,nAzim) < (PI+0.5) && zoneAzims.getElement(nIncl,nAzim) > 0.5)
			  return  0.0;
		 else
			  return diffuseRad.getElement(nIncl,nAzim);
	}
	
	
	
	/**
	 * 		
	 * This method calculates the radiation reaching a segment in a tree
	 * that is growing in a stand among identical trees (dens trees/ha).
	 * The idea here is that the tree grows in a "hole" in the stand. The
	 * "hole" is a circular cylinder, the gross-sectional area of which is
	 * 10000/dens m2 and height equal to tree height. Outside this "hole"
	 * the foliage area (calculated with the aid of tree's leaf area and
	 * dens) is evenly distributed in the crown volume (leaf area density)
	 * that is between top height and the height of the crown base.  The
	 * shading caused by the surrounding stand depends on the distance
	 * light beam travels in the stand volume on its way from a point in
	 * the sky to the segment: traveled distance * leaf area density *
	 * extinction coefficient ( extinction coefficient = 0.14 for Scots
	 * pine) The radiation coming from a point in the sky (sector) is
	 * obtained from method Firmament::regionRadiationSum(int n,
	 * vector<double>& direction).
	 * 
	 * Both height of the segment and its distance from the tree stem
	 * affect the path lenght in the surrounding canopy. The path length
	 * of the beam inside the canopy depends on the height it hits the
	 * mantle of the "hole".  Since the the segment is not in the middle
	 * of the (bottom) circle of the "hole", the distance the beam travels
	 * inside the "hole" and consequently the height at which it hits the
	 * wall depends on the direction of the beam. This effect is treated
	 * here in an average manner. For all azimuthal directions of the
	 * coming beam the mean distance from a point (inside the circle) to
	 * the circumference of it is used in calculations. It seems that as
	 * an fairly accurate approximation mean distance = r*(1 -
	 * 0.35*(x/r)^2.5), where r is the radius of the circle and x is the
	 * distance of the point from center of the circle (0 <= x <= r).
	 * 
	 * @param n number of region
	 * @param z height of the point from ground, m
	 * @param x distance of the point from the tree stem,  m
	 * @param la needle area (total area) per tree (= sf * Wf),   m2
	 * @param ke extinction cofficient  (= 0.14 for Scots pine),  unitless
	 * @param H height of tree (h. of stand),  m
	 * @param Hc height of the crown base of the tree (stand), m
	 * @param direction
	 * @param dens Density of the stand (trees/ha)
	 * 
	 * @return The annual radiation sum (MJ) from the nth region of the firmament as shaded by the neighboring stand direction  the direction of nth region, If n < 0 or n > total number of regions - 1, return -1.0
	 */
	public double diffuseForestRegionRadiationSum(int n, double z, double x,  double la, double ke, double H, double Hc,Vector<Double> direction,double dens)
	{
		//Check for suitable segment number:
		if(n < 0 || n > numOfSectors - 1) return -1.0;
		
		//Original Comment:
		// Get first unshaded radiation coming from the sector
		
		//Why do we use float variables here? Decided to use double instead:
		double Qunshaded = diffuseRegionRadiationSum(n, direction);
		
		// Inclination angle of the direction (from horizon),
		// length of direction = 1, hence z coordinate = sin(alpha)
		
		double sin_alpha = direction.get(2);
		double tan_alpha;
		//If inclination approx 0 return the unshaded radiation
		if(max(1.0-sin_alpha,sin_alpha-1.0) < R_EPSILON)
		    return Qunshaded;
		else
		    tan_alpha = tan(asin(sin_alpha));
		
		//Original Comment:
		// Area (m2) occupied by one tree = 10000/dens => radius of the opening that is
		// occupied by one tree
		
		double r_tree =  sqrt((10000.0/dens) / PI);
		
		//Original Comment:
		// The beam hits the mantle of the cylinder that is occupied by the tree at height Hh,
		// the distance of the point from the stem is considered too,
		// as mean for different directions, see explanation at the beginning
		// Obs the segment cannot be outside the cylinder

		double xcheck = min(x, r_tree);
		double avdist = r_tree * (1.0 - 0.35 * pow(xcheck/r_tree, 2.5));
		double Hh = z + tan_alpha * avdist;
		
		//Original Comment:
		// If Hh < Hc the beam goes through the whole canopy, otherwise not
		
		if(Hh < Hc)  Hh = Hc;

		double leaf_dens = dens * la / 10000.0 / (H - Hc);
		
		double distance, shading;
		
		if(Hh < H)
		    distance = (H - Hh) / sin_alpha;
		  else
		    distance = 0.0;
		
		shading =  exp(-ke * distance * leaf_dens);

		return shading * Qunshaded;
	}

	
	/**
	 * Sets the radiation of sectors to correspond input.
	 * Updates also diffuseRadPlane, diffuseRadBall, diffuseRadZenith  -variables
	 *
	 * @param rad, Diffuse radiation falling on a horizontal plane
	 */
	public void setDiffuseRadiation(double rad) {
		int i, j;
		  for(i = 0; i < num_of_incl; i++)
		    for(j = 0; j < azimDivisions.get(i); j++) {
		      diffuseRad.setElement(i, j, diffuseRad.getElement(i, j)*rad/diffuseRadPlane);
		    }
		
		diffuseRadZenith *= rad / diffuseRadPlane;
		diffuseRadBall *=  rad / diffuseRadPlane;
		diffuseRadPlane = rad;
	}
	
	
	/**
	 * Sets the the vector of Firmament that points to the sun
	 * v declared const in cLignum
	 * 
	 * @param v Vector pointing to sun
	 */
	public void setSunPosition(Vector<Double> v) {		
		for(int i = 0; i < 3; i++) sunPosition.set(i, v.get(i));
	}

	
	/**
	 * 
	 * returns the intensity of the direct (sun) radiation on the plane perpendicular
	 * to the dirction of the sun (return) direction of the sun (in vector direction)
	 *
	 * Obs, sunPosition[2] = sin(iclination)
	 * 
	 * @param direction
	 * @return Method returns type MJ in cLignum
	 */
	public double directRadiation(Vector<Double> direction) {
		  for(int i = 0; i < 3; i++)
		    direction.set(i, sunPosition.get(i));
		   if(sunPosition.get(2) <= 0.0) return 0.0;
		  return directRadPlane / sunPosition.get(2);
	}
	
	public double directHalfRegionRadiationSum(int n, Vector<Double> direction) {
		int i;
		for(i = 0; i < 3; i++)
			direction.set(i, sunPosition.get(i));
		
		if(sunPosition.get(2) <= 0.0) return 0.0;
		
		//Original Comment:
		//Change the direction vector (rx, ry, rz) to inclination and azimuth
		
		double theta = asin( sunPosition.get(2));
		if(theta > PI / 2.0) theta = PI / 2.0;
		double cosTheta = cos( theta );
		
		double rx = sunPosition.get(0);
		double ry = sunPosition.get(1);
		double fii;
		
		if((rx > R_EPSILON || -rx > R_EPSILON) && cosTheta > R_EPSILON)
		    if(ry < 0.0)
		      fii = 2.0 * PI - acos( rx / cosTheta );
		    else
		      fii = acos( rx / cosTheta );
		  else if(ry > 0.0)
		    fii = PI / 2.0;
		  else
		    fii = 3.0 * PI / 4.0; //Why divided by 4 instead of 2 ?
		
		if(fii< (PI+0.5) &&		//Why+0.5 ?
			     fii > 0.5)
			        return  0.0;
		else
	        return directRadPlane / sunPosition.get(2);
	}
	
	public double getInclination(int n) {
		
		if(n < 0 || n > numOfSectors - 1) return -1.0;
		
		//Original Comment:
		// Numbering: azimuth is changing faster. 
		//If, for example, n < azimDivisions[0]
		// nIncl = 0 and nAzim = n, aso.
		// if n == numOfSectors - 1 => zenith
		
		 if(n ==  numOfSectors - 1) {             // zenith
		       return  PI / 2.0;
		  }

		  int nIncl = inclinationIndex.get(n);

		  return inclinations.get(nIncl);
	}
	
	public double getAzimuth(int n) {
		
		if(n < 0 || n > numOfSectors - 1) return -1.0;
		
		//Original Comment:
		// Numbering: azimuth is changing faster. 
		//If, for example, n < azimDivisions[0]
		// nIncl = 0 and nAzim = n, aso.
		// if n == numOfSectors - 1 => zenith

		if(n ==  numOfSectors - 1) {             // zenith
		       return  0.0;
		  }

		  int nIncl = inclinationIndex.get(n);
		  int nAzim = azimuthIndex.get(n);

		  return zoneAzims.getElement(nIncl,nAzim);
	}
	
	//In cLignum PositionVector is used. 
	//Create a PositionVector Class later on?
	//Test this method !!!!:
	public Vector3d getDirection(int n) {
		
		//Original Comment:
		// Input: # of region
		// PositionVector pointing to the midpoint of the region.
		//x-axis is pointing to south, y-axis to east and z -axis to zenith.
		//If n < 0 or n > numOfSectors returns PositionVector(0,0,0)
		 
		if(n < 0 || n > numOfSectors - 1)
			{Vector3d v= new Vector3d(0.0,0.0,0.0);
			return v;}
		else if(n < numOfSectors -1)
			{Vector3d v= new Vector3d(dir_x.get(n), dir_y.get(n), dir_z.get(n));
		    return v;}
		else
			{Vector3d v= new Vector3d(0.0,0.0,1.0);  
		    return v;  }
	}
	
	public void setDirectRadiation(double rad) {
		directRadPlane = rad;
	}
	
	//Better not return the sunPosition Vector directly
	public Vector<Double> getSunPosition() {
		return sunPosition;
	}
	
	public int numberOfRegions() {
		return numOfSectors;
	}
	
	//In cLignum type MJ is returned
	public double diffusePlaneSensor() {
		return diffuseRadPlane;
	}
	
	//In cLignum type MJ is returned
	public double diffuseBallSensor() {
		return diffuseRadBall;
	}
	
//	public void outDiff() {
//		System.out.println(diffuseRad + "\n");
//	}
	
//	public void outAz(){
//		System.out.println(zoneAzims + "\n");
//	}
	
	public int getInclinationIndex(int n) {
		if(n < 0 || n > (numOfSectors - 2) )  // numOfSectors - 1 == zenith
		      return -1;
		    else
		      return inclinationIndex.get(n);
	}
	
	public int getAzimuthIndex(int n) {
		
		if(n < 0 || n > (numOfSectors - 2) ) // numOfSectors - 1 == zenith
		      return -1;
		    else
		      return azimuthIndex.get(n);
	}
	
	public double getSectorArea(int n) {
		
		if(n < 0 || n > num_of_incl - 1)
		      return -1.0;
		    else
		      return areasByInclination.get(n);
	}
	
	public int getAzimDivision(int n) {
		
		if(n < 0 || n > num_of_incl - 1)
		      return -1;
		    else
		      return azimDivisions.get(n);
	}
	
	public int getNoOfAzimuths() {
		return num_of_azim;
	}
	
	public int getNoOfInclinations() {
		return	num_of_incl;
	}
	
	public void outInclinations() {
		int line =1;
		for(int i = 0; i < num_of_incl; i++) {
		      System.out.println(inclinations.get(i) + " ");
		      line++;
		      if(line == 10) {
		    	  System.out.println("\n ");
			line = 1;
		      }
		    }
		    if(line != 1)
		    	  System.out.println("\n ");
	}
	
	//The method getIncAz is not implemented yet. 
	//First one has to decide which class to use instead of c++ pair
	
	
}
