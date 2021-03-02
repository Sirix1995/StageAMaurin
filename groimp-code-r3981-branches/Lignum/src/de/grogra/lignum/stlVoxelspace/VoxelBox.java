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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sin;

import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.stlLignum.BroadLeaf;
import de.grogra.lignum.stlLignum.CfTreeSegment;
import de.grogra.lignum.stlLignum.LGMAD;
import de.grogra.lignum.stlLignum.TreeSegment;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class VoxelBox implements Mathsym {

	protected double needleArea;
	protected double leafArea;
	protected double Q_in;
	protected double Q_abs;
	protected double Qin_mean;
	protected double Qabs_mean;
	protected double star;
	protected double starSum;
	protected double weight; //weighted starSum, e.g. foliage area
    
	protected double Q_inStdDiffuse;

    // Q_absbox
	protected double interceptedRadiation;
	protected double needleMass;
	protected double leafMass;
	protected int number_of_segments;
	protected int number_of_leaves;
	protected PositionVector big_leaf_normal = new PositionVector(0,0,0);//Weighted  sum  of  directions  of
				   //leaves in a box
	protected Point3d corner1;

	protected double val_c; //val_c * l is coniferous extinction
	protected double val_b; //val_b * l is broadleaf  extinction
	
	protected Vector<VoxelObject> objects = new Vector<VoxelObject>();//vector     of     photosynthesising
				 //elements in the box

	protected double woodMass;
	protected double woodArea;         //surface area of segments (woody part) in box
	protected double number_of_segments_real;   //this is the correct number of segments
                                    //considering consiering dumping of segs in
                                    // parts (num_parts)
 
	protected PositionVector mean_direction = new PositionVector(0,0,0);
	
	
	public VoxelBox(){
	needleArea =0.0;
	leafArea =0.0;
	Q_in =0.0;
	Q_abs =0.0;
	Qin_mean =0.0;
	Qabs_mean =0.0;
	star =0.0;
	starSum =0.0;
	weight =0.0;
	Q_inStdDiffuse =0.0;
	interceptedRadiation =0.0;
	needleMass =0.0;
	leafMass =0.0;
	number_of_segments =0;
	number_of_leaves =0; 
	big_leaf_normal.set(0, 0, 0);
	val_c =0.0;
	val_b =0.0;
	woodMass =0.0;
	woodArea =0.0;
	number_of_segments_real =0.0;
	
	VoxelSpaceIF space;
	
	}
	
	public VoxelBox(VoxelSpace s){
		needleArea =0.0;
		leafArea =0.0;
		Q_in =0.0;
		Q_abs =0.0;
		Qin_mean =0.0;
		Qabs_mean =0.0;
		star =0.0;
		starSum =0.0;
		weight =0.0;
		Q_inStdDiffuse =0.0;
		interceptedRadiation =0.0;
		needleMass =0.0;
		leafMass =0.0;
		number_of_segments =0;
		number_of_leaves =0; 
		big_leaf_normal.set(0, 0, 0);
		val_c =0.0;
		val_b =0.0;
		woodMass =0.0;
		woodArea =0.0;
		number_of_segments_real =0.0;
		
		space = s;

		} 
	
	//Original Comment:
	//Recalculate star and val_c,  k_b and val_b
    //currently star and k_b hard-coded!!
	//
	//	Updates  the values after  every tree  segment being  added to
	//	this VoxelBox. 
	//
	public void updateValues(){
		
		/*LGMassert(space->Xbox>0);
		LGMassert(space->Ybox>0);
		LGMassert(space->Zbox>0);*/ //TODO: Add try catch later.
		
		star = 0.0;
		//Check DumpScotsPineSegment (or any DumpCfSegment) that there
		//the star mean is weighted  with foliage area of the segment:
		//e.g. b.addStarSum(GetValue(ts,LGAstarm)*farea);
		if (this.getNumber_of_segments_real() > 0.0){
		  if (this.getWeight() > 0.0)
		    //weighted star mean
		    star = getStarSum() / getWeight();
		  else
		    star = 0.0;
		}
		    //It might be good enough to use star 0.14
	        //star = 0.14;
		double k_b = space.getK_b();		//Instead of GetValue function !
		//star  for needles
		val_c = star * (needleArea / (space.getXbox() * space.getYbox() * space.getZbox()));
		val_b = k_b * (leafArea / (space.getXbox() * space.getYbox() * space.getZbox()));

		//Note that mean_direction is not normalized
//		if(mean_direction.length() > R_EPSILON)
//		  mean_direction.normalize();
//		else
//		  mean_direction = PositionVector(0.0,0.0,1.0);    //arbitrary direction

	}
	
	public double extinction(double l) {
		double tmp = -val_c*l - val_b*l;
		return exp(tmp);
	}
	
	public boolean isEmpty() {
		if(needleArea < R_EPSILON && leafArea < R_EPSILON )
			return true;
		else 
			return false;
	}

	public Vector<VoxelObject> getObjects() {
		return objects;
	}
	
	public Point3d getCenterPoint() {
		
		Point3d point = new Point3d(corner1);
		Point3d dummy = new Point3d(space.getXbox()/2.0, space.getYbox()/2.0, space.getZbox()/2.0);
		point.add(dummy);
		return point;
	}
	
	public Point3d getCornerPoint() {
		return corner1;
	}
	
	public double getAreaDensity() {
		updateValues();
		return star + val_b;
	}
	
	public double getFoliageMass() {
		return needleMass + leafMass;
	}
	
	public double getFoliageArea() {
		return needleArea + leafArea;
	}
	
	public PositionVector getBigLeafNormal() {
		PositionVector dummy = new PositionVector(big_leaf_normal);
		dummy.normalize();
		return dummy;
	}
	
	//Original Comment:
	//Return the extinction of the objects in the box
	//Return the  extinction of the objects  in the voxel. p0  and d are
	//the start and  the direction of the light  beam respectively. Kfun
	//is  the  extinction  function  (as  an inclination  of  the  light
	//beam). Checking the  voxel object tag. If the tag  is set to true,
	//this means  the beam  has hit the  foliage and  we do not  have to
	//(must not!) compute the extinction.
	public double getExtinction(Point3d p0, PositionVector d, ParametricCurve Kfun) {
		//Original Comment:
		// I could  use accumulate, but I  want to collect  data and break
	    // the computation if wood hit
	    double tau = 1.0;
	 
	    for (int i = 0; i<= objects.size(); i++){
	      long tag = objects.get(i).getTag();
	      boolean ray_hit = (space.getBookKeeper()).rayHit((int)tag);
	      if (ray_hit == false){//no ray hit to foliage
		//If  no ray hit yet --> compute
		double tmp_tau = objects.get(i).getExtinction(p0,d,Kfun);
		//cout << "VoxelBox::getExtinction loop " << tmp_tau << endl;
		if (objects.get(i).hit_self == true){
	          //Ray hits self, mark ray hit. A copy may be in another voxel.
		  (space.getBookKeeper()).setRayHit((int)tag);
		  space.incHitself(); 
	        }
		if (tmp_tau == 0.0){//wood
		  tau = 0.0;
		  space.incHitw();
		  //	  cout << "BLOCKED" << endl;
		  break;//sector blocked
		}
		else if (tmp_tau == 1.0){//no hit
		  space.incNohit();
	        }
		else{//Foliage hit
		  //Mark the segment computed
		  (space.getBookKeeper()).setRayHit((int)tag);
		  space.incHitfol(); 
		  tau = tau*tmp_tau;
		}
	      }//if (tag == false)
	    }
	    return tau;
	}
	
	public PositionVector getMeanDirection() {
		return mean_direction;
	}
	
	public double getNeedleArea() {
		return needleArea;
	}

	public double getLeafArea() {
		return leafArea;
	}

	public double getQ_in() {
		return Q_in;
	}

	public double getQ_abs() {
		return Q_abs;
	}

	public double getQin_mean() {
		return Qin_mean;
	}

	public double getQabs_mean() {
		return Qabs_mean;
	}

	public double getStar() {
		return star;
	}

	public double getStarSum() {
		return starSum;
	}

	public double getWeight() {
		return weight;
	}

	public double getQ_inStdDiffuse() {
		return Q_inStdDiffuse;
	}

	public double getNeedleMass() {
		return needleMass;
	}

	public double getLeafMass() {
		return leafMass;
	}

	public int getNumber_of_segments() {
		return number_of_segments;
	}

	public int getNumber_of_leaves() {
		return number_of_leaves;
	}

	public double getWoodMass() {
		return woodMass;
	}

	public double getWoodArea() {
		return woodArea;
	}

	public double getNumber_of_segments_real() {
		return number_of_segments_real;
	}
	
	
	public void setArea(double needleA, double leafA) {
		needleArea = needleA;
		leafArea = leafA;
		updateValues();
	} 
	
	public void setQ_inStdDiff(double val) {
		Q_inStdDiffuse = val;
	}
	
	public void addRadiation(double r) {
		Q_in += r;
	}
	
	public void addNeedleArea(double narea) {
		needleArea += narea;
	}
	
	public void subtractNeedleArea(double narea) {
		needleArea -= narea;
	}
	
	public void addNeedleMass(double nmass) {
		needleMass += nmass;		
	}
	
	public void subtractNeedleMass(double nmass) {
		needleMass	-= nmass;	
	}
	
	public void addLeafArea(double larea) {
		leafArea +=larea;
	}
	
	public void addLeafMass(double lmass) {
		leafMass += lmass;
	}
	
	public void addQabs(double val) {
		Q_abs += val;
	}
	
	public void addQin(double val) {
		Q_in += val;
	}
	
	public void setQinMean(double val) {
		Qin_mean = val;
	}
	
	public void setQabsMean(double val) {
		Qabs_mean = val;
	}
	
	public void addInterceptedRadiation(double rad) {
		interceptedRadiation += rad;
	}
	
	public void addStarSum(double starmean) {
		starSum += starmean;
	}
	
	public void subtractStarSum(double starmean) {
		starSum -= starmean;
	}
	
	public void addWoodMass(double mass) {
		woodMass += mass;
	}
	
	public void addWoodArea(double area) {
		woodArea += area;
	}
	
	public void subtractWoodMass(double mass) {
		woodMass -= mass;
	}
	
	public void subtractWoodArea(double area) {
		woodArea -= area;
	}
	
	public void addWeight(double w) {
		weight += w;
	}
	
	public void subtractWeight(double w) {
		weight -= w;
	}
	
	public void increaseNumberOfSegments() {
		number_of_segments++;
	}
	
	public void decreaseNumberOfSegments() {
		number_of_segments--;
	}
	
	public void addNumberOfSegmentsReal(double inc) {
		number_of_segments_real += inc;
	}
	
	public void addOneLeaf() {
		number_of_leaves++;
	}
	
	public void addVector(PositionVector v) {
		mean_direction.add(v);
	}
	
	public double S(double phi, double Sf, double Wf, double r, double l){
		if(Sf * Wf == 0)
			return 0;
		if(SAc(phi,r,l) ==0 )
			return 0;
		
		return SAc(phi,r,l)*(1-exp(-K(phi)*Sf*Wf/SAc(phi,r,l)))/(Sf*Wf);
	}
	
	//Original Comment:
	//reset  the  box to  0,  clear  the  vector of  photosynthesising
    //objects (not the objects though!!!)
	
	public void reset() {
		resetQinQabs(); 
	    resetCfData();
	    resetHwData();
	    
	    objects.clear(); //TODO: Is this sufficient ?
	    
	    woodMass = 0.0;
	    woodArea = 0.0;
	}
	
	//Original Comment:
	//Reset Qin, Qabs and  intercepedRadiation to 0, this is necessary
    //in  short time  steps, where  structural update  is  slower than
    //changing light environment.
	
	public void resetQinQabs() {
		Q_in = 0.0;
		Q_abs = 0.0;
		interceptedRadiation = 0.0;
		Qin_mean = 0.0;
		Qabs_mean = 0.0;
	}
	
	protected void resetCfData() {
		star = 0;
		starSum = 0.0;
		needleArea = 0.0;
		needleMass = 0.0;
		number_of_segments = 0;
		val_c = 0.0;
		weight = 0.0;
		number_of_segments_real = 0.0;
		mean_direction.set(0.0,0.0,0.0);
	}
	
	protected void resetHwData() {
		leafArea = 0.0;
		leafMass = 0.0;
		number_of_leaves = 0;
		val_b = 0.0;
		Q_inStdDiffuse = 0;
		big_leaf_normal.set(0, 0, 0);		
	}
	
	protected double SAc(double phi, double r, double l){
		return 2 * l * cos(phi) * r + PI * r * r * sin(phi);
	}
	
	protected double K(double phi) {
		double inclination;
		
		inclination = phi * 180 /PI;
		
		if (inclination <= 9.9)
		    return 0.23;
		if (inclination <= 19.9)
			return 0.215;
		if (inclination <= 29.9)
			return 0.2;
		if (inclination <= 39.9)
			return 0.19;
		if (inclination <= 49.9)
			return 0.18;
		if (inclination <= 59.9)
			return 0.172;
		if (inclination <= 69.9)
			return 0.168;
		if (inclination <= 79.9)
			return 0.170;
		if (inclination <= 90)
			return 0.184;
		  
		  return 0;
	}
	
	private void init() {
		needleArea = 0.0;
		leafArea = 0.0;
		Q_in = 0.0;
		Q_abs = 0.0;
		Qin_mean = 0.0;
		Qabs_mean = 0.0;
		star = 0.0;
		starSum = 0.0;
		needleMass = 0.0;

		number_of_segments = 0;
		number_of_leaves = 0;
		interceptedRadiation = 0.0;
		weight = 0.0;
		objects.clear();
		big_leaf_normal.set(0,0,0);
	}
	
	//Original Comment:
	//
	//Dumps a conifer segment to the VoxelBox given as a parameter.
	//Updates also the star value 
	//
	public void DumpCfSegmentFoliage(CfTreeSegment ts, int num_parts) {
		double fmass = ts.getLGAWf();
		
		//LGMdouble r_f = GetValue(ts, LGARf);
		
		double length = ts.getLGAL();
		double farea = ts.getLGAAf();
		
		this.addNeedleArea(farea/num_parts);
		this.addNeedleMass(fmass/num_parts);
		
		double needle_rad = ts.getLGARf();
		double S_f;
		if(fmass > R_EPSILON)
			S_f = farea/fmass;
		else
			S_f = 28.0;
		
		double starS = 0.0;
		
		//Original Comment:
		//Tarkistettu ett� for-looppi ajetaan tasan 8 kertaa (mika).
		
		for (double phi=0; phi<PI/2.0; phi+=PI/16){		
			starS += this.S(phi, S_f, fmass, needle_rad, length);
	    }
		
		starS = starS/8.0;
		this.addStarSum(starS * farea/num_parts);//Note: weighted by needle area 
        //of the part of seg that is in question.
		
		this.addWeight(farea/num_parts);
		
		this.increaseNumberOfSegments();  //This is a bit problematic with num_parts
		this.addNumberOfSegmentsReal(1.0/num_parts); 
		
		PositionVector dummy = new PositionVector(ts.getDirection());
		dummy.mul(farea/num_parts);
	    this.addVector(dummy);
	}
	
	public void DumpSegmentWood(TreeSegment ts, int num_parts) {
		double r = ts.getLGAR();
		double length = ts.getLGAL()/num_parts;
		
		// KS temp - compute the wood as for TreeSegment (not ScotsPineSegment)
		
		//double mass = ts.getLGAWood() / num_parts; 
		
		double wood = ts.getTree().getLGPrhoW() * ts.getLGAVfrustum();
		double mass = wood / num_parts;
		
		double area = 2.0 * PI * r * length;
		this.addWoodMass(mass);
		this.addWoodArea(area);
	}

	
	public void InsertVoxelObject(VoxelObject obj) {
		this.objects.add(obj); //TODO: Is the possibility to add VoxelObjects sufficient ?
	}
	
	//Original Comment:
	//Dump one leaf of a deciduous tree - corresponds to DumpSegment of
	//coniferous segments (CfTreeSegment)
	
	public void DumpLeaf(BroadLeaf leaf) {
		double xx = leaf.GetValue(LGMAD.LGAA);
		this.addLeafArea(xx);
		this.addOneLeaf(); //increase the number of leaves by one
		xx = leaf.GetValue(LGMAD.LGAWf);
		this.addLeafMass(xx);
		
		//Original Comment:
		//The weighted sum of the  leaf normals. Bigger leaf has more to
	    //say to  the direction  of the "big  leaf" normal  than smaller
	    //leaf
		PositionVector dummy = new PositionVector(leaf.GetLeafNormal());
		dummy.mul(leaf.GetValue(LGMAD.LGAA));
		this.big_leaf_normal.add(dummy); //Same as b.big_leaf_normal=b.big_leaf_normal+GetLeafNormal(leaf)*GetValue(leaf, 
										 //  LGAA);
		
	}
	
	public void SetSegmentQabs(CfTreeSegment ts, double num_parts) {
		double S_f = ts.getTree().getLGPsf();
		double farea = S_f * ts.getLGAWf() / num_parts; //TODO: Why do we calculate this ?
		
		double qabs = 0.0;
		
		//Original Comment:
		//Qabs computetd based on Qin, mean star and foliage area.
		qabs = this.getQ_in()*ts.getLGAstarm()*(ts.getTree().getLGPsf()*ts.getLGAWf());
		
		ts.setLGAQabs(ts.getLGAQabs()+qabs);
		ts.setLGAQin(ts.getLGAQin()+this.getQ_in()/num_parts);
	}
	
	public void setVoxelSpace(VoxelSpaceIF s, Point3d c) {
		space = s;
		corner1 = new Point3d(c);
		
		updateValues();
	}
	
	protected VoxelSpaceIF space;
}
