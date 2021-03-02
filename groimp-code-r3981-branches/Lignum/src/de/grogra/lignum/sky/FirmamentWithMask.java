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

package de.grogra.lignum.sky;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

//At this moment the class can only work with a very specific file format which is not allowed
//to include comments.

/**
 * This class tries to model a gap in a forest. 
 * The gaps are often (?) analyzed by foresters by taking photograhs  with fish-eye lenses.
 * The size and the form of the gap is further analyzed by marking the path of the sun
 * in the photograph and the effect of the direct and diffuse radiation can be estimated (somehow).
 * The idea of this class is simply to mimic such fish-eye photograph and to model the effect
 * of the blocked incoming radiation, direct and diffuse. Thus the name FirmamentWithMask.
 * Ideally the user could define the gap (i.e., the photograph) in a file, though in a somewhat simplified manner.
 * Methods:
 * 1. readMaskFile
 *    Read the mask file and adjust radiation
 * 2. setMask
 *    This naive approach: in a file for each inclination the percentage of remaining radiation is given
 *    100% means gap, less than 100% means obstacle.
 * (original Lignum comment)
 * 
 * Translated from orignal C++ Lignum code
 *
 * @author Alexander Brinkmann
 */
public class FirmamentWithMask extends Firmament {
	
	 private double ballChange;    //Change to diffuseRadBall caused by SetMask
	 private double planeChange;   //Change to diffuseRadPlane  caused by SetMask
	 private double drp_orig; //The original plane sensor radiation of the sky 
	 // list<pair<int,string> > gap_ls; //List of gap files 
	 private final StringBuffer file = new StringBuffer();
	 private final Vector<Double> v = new Vector<Double>();
	
	public FirmamentWithMask(){
		this(NUM_OF_INCL,NUM_OF_AZIM);
	}
	
	public FirmamentWithMask(int no_incl, int no_azim){
		super(no_incl,no_azim);
		drp_orig = 0.0;
	}
	
	//Original Comment:
	//Resize the firmament. To use the masks correctly save the original
	//radiation to plane. See the method configure.
	
	@Override
	public void resize(int no_incl, int no_azim, double rad_plane){
		
		drp_orig = rad_plane;
		super.resize(no_incl, no_azim, rad_plane);
	}
	
	//Original Comment:
	//Intializing the firmament from a file that contains
	//size (inclinations and azimuths), radiation intensity
	//and an optional mask for the firmament
	
	public void configure(String file_name) {
		int incl,azim;
		double drp;
		try {
			v.clear();
			file.append(file_name);
			Scanner s = new Scanner(new File(file_name));
			incl = s.nextInt();
			azim = s.nextInt();
			drp = s.nextFloat();
			this.resize(incl, azim, drp);
			//optional mask
		   //TODO: readMask(lex);
		} catch (Exception e) {
			System.err.print("Could not open file" + file_name);
		}
		
		//Original Comment:
		//Update sensor readings diffuseRadBall and diffuseRadPlne

		double sumB = 0.0, sumP = 0.0;
		Vector<Double> radiation_direction = new Vector<Double>(3);
		for(int j = 0; j<numberOfRegions();j++){
			sumB += diffuseRegionRadiationSum(j,radiation_direction);
		    double sinIn = radiation_direction.get(2);
		    sumP += diffuseRegionRadiationSum(j,radiation_direction) * sinIn;
		}
		
		ballChange = sumB/diffuseRadBall;
	    diffuseRadBall = sumB;
	    planeChange = sumP/diffuseRadPlane;
	    diffuseRadPlane = sumP;
	}
	
}
