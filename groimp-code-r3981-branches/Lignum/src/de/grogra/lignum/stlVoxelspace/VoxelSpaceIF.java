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

import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.PairMinMax;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public interface VoxelSpaceIF {

	public int getXindex(double local_xcoord);

	public int getYindex(double local_ycoord);

	public int getZindex(double local_zcoord);

	public void reset();

	public void resetQinQabs();

	public void resize(int x, int y, int z);

	//Original Comment:

	public void resize(double lX, double lY, double lZ);

	//Original Comment:
	//Change both number and size of
	//VoxelBoxes. The extent of
	//VoxelSpace changes. Contents are
	//lost.
	//Change sizes & numbers of VoxelBoxes => the extent of VoxelSpace
	//changes (i.e. corner2)
	public void resize(double lX, double lY, double lZ, int nX, int nY, int nZ);

	public void resize(Point3d lower_left, Point3d upper_right);

	//Original Comment:
	//Move Voxelspace so that its
	//lower left corner is set at
	//corner1
	public void move(Point3d corner);

	public VOBookKeeper getBookKeeper();

	public ForestDescriptor getForestDescriptor();

	public double getArea();

	public Point3d getLowerLeftCorner();

	public Point3d getUpperRightCorner();

	public int getNumberOfBoxes();

	public int getNumberOfFilledBoxes();

	public int getNumberOfTreeSegments();

	public double getBoxVolume();

	public double getXSideLength();

	public double getYSideLength();

	public double getZSideLength();

	public double getQabs();

	public double getQin();
	
    public double getXbox();

	public double getYbox();

	public double getZbox();

	public void incHitfol();

	public void incNohit();

	public void incHitself();

	public void incHitw();

	public double getK_b();


	//Original Comment:
	//Return Min and Max foliage mass in the voxel boxes. The first holds
	//the minimum value and the second the maximum value
	public PairMinMax getMinMaxNeedleMass();

	//Original Comment:
	// returns the total foliage mass of the tree segments dumped into
	// the VoxelSpace
	// This sums up needle mass + leaf mass
	public double getFoliageMass();

	//Original Comment:
	//
	// Returns the VoxelBox where the global Point p belongs
	//
	public VoxelBox getVoxelBox(Point3d p);

	//Original Comment:
	//
	// Returns the indexes of box  where the global Point p belongs
	//
	public Vector<Integer> getBoxIndexes(Point3d p);

	public Vector<VoxelMovement> getRoute(Vector<VoxelMovement> vec,
			int startx, int starty, int startz, PositionVector dir);

	//Original Comment:
	//The method  calculates the route through the  voxel space from
	//start point to the direction given. 
	//Input/Output: vec   the route, includes extincion in each voxel
	//Input:        p0  start point (global)
	//              dir direction, |dir| == 1 (!!!)
	//              K   extinction
	//              pairwise if true use the voxel objects in voxels to 
	//                       calculate extinction, if false calculate only the 
	//                       path lengths in voxels 
	//
	// The function  calculates the route  through the voxel  space from
	// the start point  to the direction given as  parameter.  The route
	// is stored into a vector
	//
	// Parametres:
	// vec:     the vector where the route is stored
	// p0:      the start position of the ray (e.g. segment midpoint)
	// dir:     direction of the light beam, NOTE: |dir| == 1 (!!!!)
	// K:       extinction 
	// pairwise: if true calcuate the extinction using pairwise comparison 
	//           to voxel objects, if false calculate only the path lengths 
	//           in voxels  
	// returns : the route stored in a vector, 
	//           extinction of the objects in the voxels 
	//
	//This getRoute is  as getRoute above but uses  user defined 'p0' as
	//the ray starting point.
	public Vector<VoxelMovement> getRoute(Vector<VoxelMovement> vec,
			Point3d p0, PositionVector dir, ParametricCurve K, boolean pairwise);

	public double getBorderStandExtinction(Point3d p0, PositionVector dir);

	//Original Comment:
	//Runs updateValues() of voxelboxes (whatever it does)
	public void updateBoxValues();

	public double calculateTurbidLight();

	//default with true as argument
	//
	//	The function calculates the Qin and Qabs-values to every VoxelBox.
	//    self_shading determines if the box shades itself or not
	public double calculateTurbidLight(boolean self_shading);

	//Original Comment:
	//
	// A function used to fill all the VoxBoxes with a initial
	// value
	//
	public void fillVoxelBoxes(double needleA, double leafA);

	public void fillVoxelBoxes(double inivalue, int beginZ, int endZ);

	//Original Comment:
	//First four arguments are for box.S() STAR sum
	public void FillVoxelBoxesWithNeedles(double Sf, double Wf, double Rf,
			double L, int beginZ, int endZ);

	public double getMeanFoliageAreaDensity();

	public void calculateMeanQabsQin();

	public double getNeedleArea();

	public double getLeafArea();

	public double getFoliageArea();

	//Original Comment:
	//Evaluate needle area of every (horizontal) layer of voxelboxes and return them in a vector
	//specifying height of the layer center and needle area index in that layer.
	//First element of vector = lowest layer.
	//Unit = leaf area / ground area (thus unitless, LAI = sum of vector)
	//Hmax and Hmin give the top and bottom of the space and n is no. of boxes in z direction
	//TODO: Use different Pair-Class. PairMinMax does not make sense here.
	public void evaluateVerticalNeedleAreaDensity(double Hmax, double Hmin,
			int n, Vector<PairMinMax> NAD);

}