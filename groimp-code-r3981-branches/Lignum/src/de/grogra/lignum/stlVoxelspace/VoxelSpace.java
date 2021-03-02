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
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.max;

import java.util.Collections;
import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.PairMinMax;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.sky.Firmament;
import de.grogra.lignum.stlLignum.BoundingBox;
import de.grogra.lignum.stlLignum.LGMAD;
import de.grogra.lignum.stlLignum.Tree;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class VoxelSpace implements Mathsym, VoxelSpaceIF {

	 //Original Comment:
	  // Constructor
	  //
	  // parametres
	  //	corner1 : the corner from the VoxelSpace where x,y and z have their minimum values
	  //  corner2 : opposite corner to corner1
	  //	xn		: number of VoxBoxescd  in x direction
	  //	yn		: number of VoxBoxes in y direction
	  //  zn		: number of VoxBoxes in z direction
	  //	f		: Firmament
	  //
	
	public VoxelSpace(Point3d c1, Point3d c2, int xn, int yn, int zn, Firmament f) {
		Xn = xn;
		Yn = yn;
		Zn = zn;
		voxboxes = new VoxelBox[xn][yn][zn];
		corner1 = new Point3d(c1);
		corner2 = new Point3d(c2);
		k_b = 0.50;
		
		Xbox = (corner2.x - corner1.x)/(Xn);
		Ybox = (corner2.y - corner1.y)/(Yn);
		Zbox = (corner2.z - corner1.z)/(Zn);
		
		for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox,j*Ybox,k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k] = new VoxelBox();
					voxboxes[i][j][k].setVoxelSpace(this,corner);
				}
		
		sky = f;
		
	}
	
	//Original Comment:
	//Constructor: 
	//c1, c2: the corner points
	//xsize, ysize, zsize: voxel box size
	//xn, yn, zn: number of voxel boxes (size of the matrix)
	//f: the firmament
	//kb: angle of incidence of a broad leaf (c.f star for coniferous)
	
	public VoxelSpace(Point3d c1, Point3d c2, double xsize, double ysize, double zsize, int xn, int yn, int zn, 
			Firmament f, double kb) {
		Xbox = xsize;
		Ybox = ysize;
		Zbox = zsize;
		Xn = xn;
		Yn = yn;
		Zn = zn;
		voxboxes = new VoxelBox[xn][yn][zn];
		corner1 = new Point3d(c1);
		corner2 = new Point3d(c2);
		k_b = kb;
		
		for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox,j*Ybox,k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k] = new VoxelBox();
					voxboxes[i][j][k].setVoxelSpace(this, corner);
				}
		
		sky = f;
		
	}
	
	
	public double Xbox, Ybox, Zbox;
    public int Xn, Yn, Zn;
    public VoxelBox voxboxes[][][]; //TODO: Must work with VoxelBox as element
    //debug
    public int sgmnt;//segments inserted (to compare with)
    public int hitw;//wood hits;
    public int hitfol;//foliage hits
    public int nohit;//no hits
    public int hitself; //Self comparison

  
   

    private final BoundingBox bbox = new BoundingBox();

    private final Point3d corner1;  //origo
    private final Point3d corner2;  //diagonally opposite corner(??)
	
    Firmament sky;

    double k_b; //impact angle of a broad  leaf (c.f. star mean for
		   //coniferous)
    VOBookKeeper book_keeper = new VOBookKeeper(); //maintains   information  if   a  voxel
			      //object has been hit by a light beam
    ForestDescriptor forest_descriptor = new ForestDescriptor();//maintains descripitive
				       //statistics and indices of the
				       //forest in the voxel space
	
    public double getXbox() {
		return Xbox;
	}

	public double getYbox() {
		return Ybox;
	}

	public double getZbox() {
		return Zbox;
	}

	public void incHitfol() {
		hitfol++;
	}

	public void incNohit() {
		nohit++;
	}

	public void incHitself() {
		hitself++;
	}

	public void incHitw() {
		hitw++;
	}

	public double getK_b() {
		return k_b;
	}
	
    public int getXindex(double local_xcoord){
    	return (int)((local_xcoord-corner1.x)/Xbox);
    }
    
    public int getYindex(double local_ycoord){
    	return (int)((local_ycoord-corner1.y)/Ybox);
    }
    
    public int getZindex(double local_zcoord){
    	return (int)((local_zcoord-corner1.z)/Zbox);
    }
    
    public void reset() {
    	 for(int i1=0; i1<Xn; i1++){
    	      for(int i2=0; i2<Yn; i2++){
    	    	  for(int i3=0; i3<Zn; i3++){
    	    		  voxboxes[i1][i2][i3].reset(); 
    	    	  }
    	      	}
    	 	}
    	//Reset the book keeping of tags for voxel objects
    	    book_keeper.reset();
    	    //Reset the descriptive data for forest 
    	    forest_descriptor.reset();
    	    sgmnt = 0;
    	    hitw = 0;
    	    hitfol = 0;
    	    nohit = 0;
    	    hitself = 0;
	}
    
    public void resetQinQabs() {
    	for(int i1=0; i1<Xn; i1++){
    	      for(int i2=0; i2<Yn; i2++){
    		for(int i3=0; i3<Zn; i3++){
    		    voxboxes[i1][i2][i3].resetQinQabs(); 
    		}
    	      }
    	    }
	}
    
    //Original Comment:
    //change number of VoxelBoxes
	//in x, y, and
	//z-directions. The extent
	//of VoxelSpace does not
    //change.Contents are lost.
  //Change number (=input) of VoxelBoxes in x, y, and z-directions. The whole
    //VoxelSpace, that is, the big box from corner1 to corner2 retains
    //its size => size of VoxelBox changes.
    
    public void resize(int x, int y, int z) {
    	if(x < 1) x = 1;
        if(y < 1) y = 1;
        if(z < 1) z = 1;
        //voxboxes.resize(x, y, z);
        voxboxes = new VoxelBox[x][y][z]; //TODO : Better solution ? Does this even work ?
        Xn = x;
        Yn = y;
        Zn = z;
        
        //Update now the size of VoxelBox
        Xbox = Xn*Xbox / Xn;
        Ybox = Yn*Ybox / Yn;
        Zbox = Zn*Zbox / Zn;
        
        for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox,j*Ybox,k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k].setVoxelSpace(this,corner);
				}
        
	}
    //Original Comment:
    
    //Change sizes of VoxelBoxes => number of VoxelBoxes in x, y, and
    //z-direction change, since the the big box from corner1 to corner2
    //retains (approximately) its size (slight change due to change of
    //voxelbox size; corner2 changes). 
    //Change sizes of VoxelBoxes
	 //in x, y, and
	 //z-directions. The extent of
	 //VoxelSpace does not change
	 //(only by size of the
	 //VoxelBox). Contents are lost.
    
    public void resize(double lX, double lY,
    		double lZ) {
    	if(lX < R_EPSILON) lX = 1.0;
        if(lY < R_EPSILON) lY = 1.0;
        if(lZ < R_EPSILON) lZ = 1.0;

        Xn = (int)(Xn*Xbox / lX) + 1;
        Yn = (int)(Yn*Ybox / lY) + 1;
        Zn = (int)(Zn*Zbox / lZ) + 1;
        
        //voxboxes.resize(Xn, Yn, Zn);
        voxboxes = new VoxelBox[Xn][Yn][Zn]; //TODO: Like above:Does this work?
        Xbox = lX;
        Ybox = lY; 
        Zbox = lZ;

        //Update also the other corner. Lover left corner1 = origo,
        //corner2 is the opposite corner.It may have changed slightly.
        corner2.set(corner1);
        corner2.add(new Point3d(Xn*Xbox, Yn*Ybox, Zn*Zbox));
        
      //The coordinates (=lower left corners) of the VoxelBoxes have to
        //be set and also the Voxelspace
        
        for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox,j*Ybox,k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k].setVoxelSpace(this,corner);
				}
	}
    
    //Original Comment:
    //Change both number and size of
    //VoxelBoxes. The extent of
    //VoxelSpace changes. Contents are
    //lost.
    //Change sizes & numbers of VoxelBoxes => the extent of VoxelSpace
    //changes (i.e. corner2)
    public void resize(double lX, double lY, double lZ,
		    int nX, int nY, int nZ ) {
		
    	if(nX < 1) nX = 1;
        if(nY < 1) nY = 1;
        if(nZ < 1) nZ = 1;
        if(lX < R_EPSILON) lX = 1.0;
        if(lY < R_EPSILON) lY = 1.0;
        if(lZ < R_EPSILON) lZ = 1.0;
        
        Xn = nX;
        Yn = nY;
        Zn = nZ;
        //voxboxes.resize(Xn, Yn, Zn);
        voxboxes = new VoxelBox[Xn][Yn][Zn];//TODO: Same question as above
        
        Xbox = lX;
        Ybox = lY;
        Zbox = lZ;
        
        //Update the other corner. Lover left corner1 = origo,
        //corner2 is the opposite corner.It may have changed slightly.
        corner2.set(corner1);
        corner2.add(new Point3d(Xn*Xbox, Yn*Ybox, Zn*Zbox));
        
      //The coordinates (=lower left corners) of the VoxelBoxes have to
        //be set and also the Voxelspace
        for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox, j*Ybox, k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k].setVoxelSpace(this,corner);
				}
	}
    
    //Original Comment:
    //Change physical dimensions of voxelspace (i.e. extent, i.e. opposite
    //corners) The number of VoxelBoxes may change, their dimensions
    //remain the same. Contents are lost (because TMatrix3D resize
    //destroys contents). Note that upper right corner of the resized
    //voxelspace is the same as the given corner; it is adjusted to match
    //number of voxboxes.
    
    public void resize(Point3d lower_left, Point3d upper_right) {
		
    	corner1.set(lower_left);
    	
    	Xn = (int)((upper_right.x-lower_left.x)/Xbox) + 1;
        Yn = (int)((upper_right.y-lower_left.y)/Ybox) + 1;
        Zn = (int)((upper_right.z-lower_left.z)/Zbox) + 1;
        
        //voxboxes.resize(Xn, Yn, Zn);
        voxboxes = new VoxelBox[Xn][Yn][Zn]; //TODO: see above
        
        corner2.set(corner1.x+Xn*Xbox, corner1.y+Yn*Ybox, 
        		corner1.z+Zn*Zbox);
      //The coordinates (=lower left corners) of the VoxelBoxes have to
        //be set and also the Voxelspace
        
        for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d corner = new Point3d(i*Xbox, j*Ybox, k*Zbox);
					corner.add(corner1);
					voxboxes[i][j][k] = new VoxelBox();
					voxboxes[i][j][k].setVoxelSpace(this,corner);
				}
	}
    
    //Original Comment:
    //Move Voxelspace so that its
    //lower left corner is set at
    //corner1
    public void move(Point3d corner) {
		corner1.set(corner);
		
		//Update also the other corner
		corner2.set(corner1);
        corner2.add(new Point3d(Xn*Xbox, Yn*Ybox, Zn*Zbox));
        
       //Set also the coordinates (=lower left corners) of the VoxelBoxes
        for(int i = 0; i < Xn; i++)
			for(int j = 0; j < Yn; j++)
				for(int k = 0; k <Zn; k++)
				{
					Point3d c = new Point3d(i*Xbox,j*Ybox,k*Zbox);
					c.add(corner1);
					voxboxes[i][j][k].setVoxelSpace(this,c);
				}
	}
    
    public VOBookKeeper getBookKeeper() {
		return book_keeper;
	}
    
    public ForestDescriptor getForestDescriptor() {
		return forest_descriptor;
	}
    
    public double getArea() {
		return Xn*Xbox*Yn*Ybox;
	}
    
    public Point3d getLowerLeftCorner() {
		return corner1;
	}
    
    public Point3d getUpperRightCorner() {
		return corner2;
	}
    
    public int getNumberOfBoxes() {
		return Xn*Yn*Zn;
	}
    
    public int getNumberOfFilledBoxes() {
		 int count = 0;
		    for(int i1=0; i1<Xn; i1++)
		      for(int i2=0; i2<Yn; i2++)
			for(int i3=0; i3<Zn; i3++)
			  {
			    if(voxboxes[i1][i2][i3].isEmpty() == false)
			      {
				count++;
			      }
			  }
		    return count;
	}
    
    public int getNumberOfTreeSegments() {
    	int count=0;
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    if(voxboxes[i1][i2][i3].isEmpty() == false)
    	      {
    		count += voxboxes[i1][i2][i3].getNumber_of_segments();
    	      }
    	  }
        return count;
	}
    
    public double getBoxVolume() {
		return Xbox*Ybox*Zbox;
	}
    
    public double getXSideLength() {
		return Xbox;
	}
    
    public double getYSideLength() {
		return Ybox;
	}
    
    public double getZSideLength() {
		return Zbox;
	}
    
    public double getQabs() {
		double qabs = 0.0;
		
		for(int i1=0; i1<Xn; i1++)
		      for(int i2=0; i2<Yn; i2++)
			for(int i3=0; i3<Zn; i3++)
			  {
			    qabs = qabs + voxboxes[i1][i2][i3].getQ_abs(); 
			  }
		    return qabs;
	}
    
    public double getQin() {
    	 double qin = 0.0;
    	    for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    qin = qin + voxboxes[i1][i2][i3].getQ_in(); 
    		  }
    	    return qin;
	}
    //Original Comment:
    //Return Min and Max foliage mass in the voxel boxes. The first holds
    //the minimum value and the second the maximum value
    public PairMinMax getMinMaxNeedleMass() {
    	PairMinMax p = new PairMinMax();
    	
    	for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    double fm = voxboxes[i1][i2][i3].getNeedleMass();
    		    //new minimum
    		    if (fm < p.min)
    		      p.min = fm;
    		    //new maximum
    		    if (fm > p.max)
    		      p.max = fm;
    		  }
    	    return p;
		
	}
    
    //Original Comment:
    // returns the total foliage mass of the tree segments dumped into
    // the VoxelSpace
    // This sums up needle mass + leaf mass
    public double getFoliageMass() {
	
    	double ret = 0.0;
    	for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    ret += voxboxes[i1][i2][i3].getFoliageMass();
    		  }
    	    return ret;
    	
	}
     //Original Comment:
    //
    // Returns the VoxelBox where the global Point p belongs
    //
    public VoxelBox getVoxelBox(Point3d p) {
		
    	
			Point3d localP = new Point3d(p);
			localP.sub(p, corner1);
			int Xi = (int)(localP.x/Xbox);
			int Yi = (int)(localP.y/Ybox);
			int Zi = (int)(localP.z/Zbox);
			//Exception e = new Exception();
			if (Xi < 0 || Yi < 0 || Zi < 0 || Xi >= Xn || Yi >= Yn || Zi >= Zn){
			   // cout << "getVoxelBox for " << p << flush;
			    //cout << "voxel " << Point(Xi,Yi,Zi) << flush;
			    //cout << "voxel space " << Point(Xn,Yn,Zn) << flush;
			    //throw OutOfVoxelSpaceException(Point(Xi,Yi,Zi),p);
				
			  } 

			  return voxboxes[Xi][Yi][Zi];
		
	}
    
    //Original Comment:
    //
    // Returns the indexes of box  where the global Point p belongs
    //
    public Vector<Integer> getBoxIndexes(Point3d p) {
		
    	Point3d localP = new Point3d(p);
		localP.sub(p, corner1);
		int Xi = (int)(localP.x/Xbox);
		int Yi = (int)(localP.y/Ybox);
		int Zi = (int)(localP.z/Zbox);
		
		Vector<Integer> vec = new Vector<Integer>(3);
		vec.set(0, Xi);
		vec.set(1, Yi);
		vec.set(2, Zi);
		return vec;
	}
    
    //Original Comment:
    //
    // The Function calculates the root from start point to the direction given as parameter.
    // The route is stored to a vector
    //
    // Parametres:
    // vec	:	the vector where the route is stored
    // startx : the x-index of the starting VoxelBox
    // starty : the y-index of the starting VoxelBox
    // startz : the z-index of the starting VoxelBox
    // dir	  : direction
    // returns : the route stored in a vector
    //
    //This getRoute assumes that the initial point is at the center of the box.
    
    public Vector<VoxelMovement> getRoute(Vector<VoxelMovement> vec, int startx, int starty, int startz, PositionVector dir) {
    	
    	dir.normalize();
    	
    	int x_jump = +1;
        int y_jump = +1;
        int z_jump = +1;
        
        if (dir.x<0)
            x_jump = -1;
        if (dir.y<0)
            y_jump = -1;
        if (dir.z<0)
            z_jump = -1;
          
        double xmove=9999;
        double ymove=9999;
        double zmove=9999;
        
        if (dir.x != R_EPSILON)
            xmove = abs(Xbox / dir.x);

        if (dir.y != R_EPSILON)
          ymove = abs(Ybox / dir.y);
      	
        if (dir.z != R_EPSILON)
          zmove = abs(Zbox / dir.z);
        
        double next_x = xmove / 2.0;
        double next_y = ymove / 2.0;
        double next_z = zmove / 2.0;
        
        double dist = 0;
        
        while(startx>=0 && starty>=0 && startz>=0 && startx<Xn &&
        		  starty<Yn && startz<Zn){
        	
        	VoxelMovement vm = new VoxelMovement();
        	vm.x = startx;
        	vm.y = starty;
        	vm.z = startz;
        	
        	if (next_x <= next_y && next_x<= next_z){
        		startx = startx + x_jump;
        	    vm.l = next_x - dist;
        	    dist = next_x;
        	    next_x = next_x + xmove;
        	}
        	else if (next_y <= next_x && next_y<= next_z)
      	  {
      	    starty = starty + y_jump;
      	    vm.l = next_y - dist;
      	    dist = next_y;
      	    next_y = next_y + ymove;
      			
      	  }
        	else if (next_z <= next_y && next_z<= next_x)
      	  {
      	    startz = startz + z_jump;
      	    vm.l = next_z - dist;
      	    dist = next_z;
      	    next_z = next_z + zmove;
      	  }
        	if (startx>=-1 && starty>=-1 && startz>=-1 && startx<Xn+1 &&
        		    starty<Yn+1 && startz<Zn+1)
        		  {
        		    //vm.x = startx;
        		    //vm.y = starty;
        		    //vm.z = startz;
        		    vec.add(vm);
        		  }
        	
        }
        return vec;
		
	}
    
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
    public Vector<VoxelMovement> getRoute(Vector<VoxelMovement> vec, Point3d p0, PositionVector dir,
    							ParametricCurve K, boolean pairwise) {
		
    	PositionVector d0 = new PositionVector(p0);
    	
    	int x_jump = +1;
        int y_jump = +1;
        int z_jump = +1;
    	
        //The x,y,z indices of the box the point belongs to 
        int startx = getXindex(p0.x);
        int starty = getYindex(p0.y);
        int startz = getZindex(p0.z);
    	
        if (dir.x<0)
          x_jump = -1;
        if (dir.y<0)
          y_jump = -1;
        if (dir.z<0)
          z_jump = -1;
                
        //Original Comment:
        //Normals to faces of the box
        //PositionVector n1(1,0,0);//normal of the front face of the box
        //PositionVector n2(0,1,0);//normal of the left face of the box
        //PositionVector n3(0,0,1);//normal of the bottom face of the box
        //PositionVector n4(-1,0,0);//normal of the back face of the box
        //PositionVector n5(0,-1,0);//normal of the right face of the box
        //PositionVector n6(0,0,-1);//normal of the top face of the box
        //LGMdouble boxx0 = startx*Xbox;//corner coordinates (not indices) of the box
        //LGMdouble boxy0 = starty*Ybox;//global   coordinates:
                                      //e.g. (3.7 div 2)*2 =1*2  =  2, 
                                      //(3.7  div 0.5)*0.5=7*0.5=3.5
        //LGMdouble boxz0 = startz*Zbox;
        //origo of the box in global (segment) coordinates, i.e. the point
        //on the front, left and bottom faces of the box
        //Point p1(boxx0,boxy0,boxz0);
        
        Point3d p1 = new Point3d(voxboxes[startx][starty][startz].getCornerPoint());
        //opposite point  to origo  in global (segment)  coordinates, i.e.
        //the point on the back, right and top faces of the box
        //Point p2(boxx0+Xbox,boxy0+Ybox,boxz0+Zbox);
        
        Point3d p2 = new Point3d(p1.x+Xbox,p1.y+Ybox,p1.z+Zbox);
        double xmove=R_HUGE;
        double ymove=R_HUGE;
        double zmove=R_HUGE;
        
        //Calculate  the distances  one has  to  move to  cross voxel  box
        //boundaries in x,y and z directions
        if (abs(dir.x) > R_EPSILON)
            xmove = abs(Xbox / dir.x);
        if (abs(dir.y) > R_EPSILON)
            ymove = abs(Ybox / dir.y);
        if (abs(dir.z) > R_EPSILON)
            zmove = abs(Zbox / dir.z);
        
        //Initialize: calculate the distances light beam can travel before
        //crossing the box in x,y and z directions. This is the problem of
        //deciding  if  a  ray  intersects  with  a  plane.   The  ray  is
        //represented as r0+t*r1, where r0 is the starting point and r1 is
        //the direction (unit  vector) of the ray. 't'  is the distance to
        //the plane. The plane is  represented as Ax+By+Cz+D=0, where A, B
        //and C  is the  normal to the  plane (unit  vector) and D  is the
        //(shortest)  distance of  the plane  to  origo. At  the point  of
        //intersection   the    ray   satisfies   the    plane   equation:
        //A*(r0.x+t*r1.x)+B*(r0.y+t*r1.y)+C*(r0.z+t*r1.z)+D=0   Solve  the
        //equation for t:
        //t=-(A*r0.x+B*r0.y+C*r0.z+D)/(A*r1.x+B*r1.y+C*r1.z) Note the sign
        //of  D; it is  a positive  number in  Ax+By+Cz=D and  negative in
        //Ax+By+Cz+D=0. Note also that the  normals are simple and we know
        //the D, so the equation for t simplifies quite a lot.
        double t1,t2,t3,t4,t5,t6;
        t1=t2=t3=t4=t5=t6=-1.0;//initialize to negative (i.e. no  intersection)
        if (abs(dir.x) > R_EPSILON){
          //A=1,B=C=0,D=-p1.x
          t1 = -(d0.x + (-p1.x))/(dir.x);//front face
          //A=1,B=C=0,D=-p2.x
          t4 = -(d0.x + (-p2.x))/(dir.x);//back face
        }
        if (abs(dir.y) > R_EPSILON){
          t2 = -(d0.y + (-p1.y))/(dir.y);//left face
          t5 = -(d0.y + (-p2.y))/(dir.y);//right face
        }
        if  (abs(dir.z) > R_EPSILON){
          t3 = -(d0.z + (-p1.z))/(dir.z);//bottom face
          t6 = -(d0.z + (-p2.z))/(dir.z);//top face
        }
        //For  each t>=0  check in  which direction  (x, y  or z)  the box
        //boundary was crossed.   (If t < 0 the plane  was in the opposite
        //direction). Set  it as  the value of  next_[x,y,z]. That  is the
        //distance the ray can travel  before crossing the box boundary in
        //x,y and z direction.  The "Fast voxel space traversal algorithm"
        //then in its incremental  phase calculates the total distance the
        //ray traverses in the boxes.
        double next_x,next_y,next_z;
        next_x=next_y=next_z=R_HUGE;
        if (t1 >=0.0){
          //If the  direction component is  positive, the ray  crosses the
          //box   boundary   in   index*box_size+box_size,  if   direction
          //component  is negative  the ray  crosses the  box  boundary in
          //index*box_size.
          if (abs(d0.x+t1*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t1*dir.x - p2.x) <= R_EPSILON){
    	//Check the special case: X does have a direction
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t1;
          }
          if (abs(d0.y+t1*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t1*dir.y - p2.y) <= R_EPSILON){
    	//Check the special case: Y does have a direction
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t1;
          }
          if (abs(d0.z+t1*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t1*dir.z - p2.z) <= R_EPSILON){
    	//Check the special case: Z does have a direction
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t1;
          }
        }
        //The same for t2-->t6
        if (t2 >=0.0){
          if (abs(d0.x+t2*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t2*dir.x - p2.x) <= R_EPSILON){
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t2;
          }
          if (abs(d0.y+t2*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t2*dir.y - p2.y) <= R_EPSILON){
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t2;
          }
          if (abs(d0.z+t2*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t2*dir.z - p2.z) <= R_EPSILON){
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t2;
          }
        }
        if (t3 >=0.0){
          if (abs(d0.x+t3*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t3*dir.x - p2.x) < R_EPSILON){
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t3;
          }
          if (abs(d0.y+t3*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t3*dir.y - p2.y) <= R_EPSILON){
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t3;
          }
          if (abs(d0.z+t3*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t3*dir.z - p2.z) <= R_EPSILON){
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t3;
          }
        }
        if (t4 >=0.0){
          if (abs(d0.x+t4*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t4*dir.x - p2.x) <= R_EPSILON){
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t4;
          }
          if (abs(d0.y+t4*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t4*dir.y - p2.y) <= R_EPSILON){
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t4;
          }
          if (abs(d0.z+t4*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t4*dir.z - p2.z) <= R_EPSILON){
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t4;
          }
        }
        if (t5 >=0.0){
          if (abs(d0.x+t5*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t5*dir.x - p2.x) <= R_EPSILON){
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t5;
          }
          if (abs(d0.y+t5*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t5*dir.y - p2.y) <= R_EPSILON){
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t5;
          }
          if (abs(d0.z+t5*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t5*dir.z - p2.z) <= R_EPSILON){
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t5;
          }
        }
        if (t6 >=0.0){
          if (abs(d0.x+t6*dir.x - p1.x) <= R_EPSILON || 
    	  abs(d0.x+t6*dir.x - p2.x) <= R_EPSILON){
    	if (abs(dir.x) > R_EPSILON)
    	  next_x = t6;
          }
          if (abs(d0.y+t6*dir.y - p1.y) <= R_EPSILON || 
    	  abs(d0.y+t6*dir.y - p2.y) <= R_EPSILON){
    	if (abs(dir.y) > R_EPSILON)
    	  next_y = t6;
          }
          if (abs(d0.z+t6*dir.z - p1.z) <= R_EPSILON || 
    	  abs(d0.z+t6*dir.z - p2.z) <= R_EPSILON){
    	if (abs(dir.z) > R_EPSILON)
    	  next_z = t6;
          }
        }

        //At this point  we should have exactly 3  crossings, one for each
        //x,y and  z plane. The  special cases are  when some of x,y  or z
        //directions are 0.  But these  special cases should be taken care
        //above before the next_[x,y,z] are initialized. If some direction
        //is  not possible,  next_[x,y,z]  is FLT_MAX.   Otherwise one  of
        //next_x, next_y  and next_z contains  the minimum t that  the ray
        //can travel in the box 
        
        // cout << "T: " << t1 << " "  << t2 << " " << t3 << " " <<  t4 << " " << t5  //!!!!!!!!!!!!!!!!!!!!!!!!
        //  << " " << t6 <<endl;
        double dist = 0.0;
        //cout << "P: " << p0 << dir <<endl;
        while(startx>=0 && starty>=0 && startz>=0 && startx<Xn &&
    	  starty<Yn && startz<Zn)
          {
    	VoxelMovement vm = new VoxelMovement();
    	vm.x = startx;
    	vm.y = starty;
    	vm.z = startz;

    	vm.STAR_mean = voxboxes[vm.x][vm.y][vm.z].getStar();
    	vm.n_segs_real = voxboxes[vm.x][vm.y][vm.z].getNumber_of_segments_real();
    	vm.mean_direction = voxboxes[vm.x][vm.y][vm.z].getMeanDirection();

    	//	cout << "index: " << vm.x << " " << vm.y << " " << vm.z << endl;  //!!!!!!!!!!!!!!!!!!!!!!
    	//	cout << "Next: " << next_x << " " << next_y << " " << next_z << endl;
           
    	vm.tau = 1.0;//Initalize tau to 1 so we do not exit with DiffuseVoxelSpaceRadiation
    	//Set foliage area,  needle area + leaf area
    	vm.af = voxboxes[vm.x][vm.y][vm.z].getFoliageArea(); 
    	//Get extinction  caused by objects  in the box Avoid  the box
    	//where the shaded segment is cout << vm.x << " " << vm.y << "
    	//" << vm.z << " " <<endl << next_x << " " << next_y << " " <<
    	//next_z  << "  "  << dist  <<  endl; If  the  user wants  the

    	//In pairwise comparison  we need to go through  the vector of
    	//voxel objects in each box. Otherwise the lengths of the beam
    	//paths in voxels is enough
    	if (pairwise == true){
    		vm.tau =  voxboxes[vm.x][vm.y][vm.z].getExtinction(p0,dir,K);
    	}
    	if (next_x <= next_y && next_x <= next_z)
    	  {
    		startx = startx + x_jump;
    	    vm.l = next_x - dist;
    	    dist = next_x;
    	    next_x = next_x + xmove;
    	  }
    	else if (next_y <= next_x && next_y <= next_z)
    	  {
    		starty = starty + y_jump;
    	    vm.l = next_y - dist;
    	    dist = next_y;
    	    next_y = next_y + ymove;
    	  }
    	else if (next_z <= next_y && next_z <= next_x)
    	  {
    		startz = startz + z_jump;
    	    vm.l = next_z - dist;
    	    dist = next_z;
    	    next_z = next_z + zmove;
    	  }
    		
    	if (startx>=-1 && starty>=-1 && startz>=-1 && startx<Xn+1 &&
    	    starty<Yn+1 && startz<Zn+1)
    	  {
    		vec.add(vm);
    	  }
    	//break the voxel traversal if wood hit
    	if (vm.tau == 0.0 && !pairwise){
    	  //cout << "vm.tau should not be 0 in DiffuseVoxelSpace Radiation " << vm.tau <<endl;;
    	  return vec;
    	}
          }
        return vec; 
	}
    
    //Original Comment:
    //Return the extinction caused by the border stand
    //Input: p0   start point of the light beam
    //       dir  direction of the light beam, |dir| == 1 (!!!)
    //Calculate the  point where  the light beam  exits the  voxel space
    //(there  must be  one). NearByShading  then returns  the extinction
    //coeffcient
    
    public double getBorderStandExtinction(Point3d p0, PositionVector dir) {
    	//Original Comment:
    	//Start point of the light beam
        PositionVector d0 = new PositionVector(p0);
        //Original Comment:
        //Normals to the faces of the voxel space
        //PositionVector n1(1,0,0);//normal of the front face of the voxel space
        //PositionVector n2(0,1,0);//normal of the left face of the voxel space
        //PositionVector n3(0,0,1);//normal of the bottom face of the voxel space
        //PositionVector n4(-1,0,0);//normal of the back face of the voxel space
        //PositionVector n5(0,-1,0);//normal of the right face of the voxel space
        //PositionVector n6(0,0,-1);//normal of the top face of the voxel space
        //Origo  of  the  voxel  space in  global  (segment)  coordinates,
        //i.e. the point on the front,  left and bottom faces of the voxel
        //space
        Point3d p1 = new Point3d(corner1);
        //opposite point  to origo  in global (segment)  coordinates, i.e.
        //the point on the back, right and top faces of the voxel space
        Point3d p2 = new Point3d(corner2);
        //Calculate the  distances light  beam can travel  before crossing
        //the voxel space in x,y and  z directions. This is the problem of
        //deciding  if  a  ray  intersects  with  a  plane.   The  ray  is
        //represented as d0+t*dir, where d0  is the starting point and dir
        //is the direction  (unit vector) of the ray.  't' is the distance
        //to the plane. The plane is represented as Ax+By+Cz+D=0, where A,
        //B and C  is the normal to  the plane (unit vector) and  D is the
        //(shortest)  distance of  the plane  to  origo. At  the point  of
        //intersection   the    ray   satisfies   the    plane   equation:
        //A*(d0.x+t*dir.x)+B*(d0.y+t*dir.y)+C*(d0z+t*dir.z)+D=0  Solve the
        //equation for t:
        //t=-(A*d0.x+B*d0.y+C*d0.z+D)/(A*dir.x+B*dir.y+C*dir.z)  Note  the
        //sign of D; it is a positive number in Ax+By+Cz=D and negative in
        //Ax+By+Cz+D=0. Note also that the  normals are simple and we know
        //the D, so the equation for t simplifies quite a lot.
        double t1,t2,t3,t4,t5,t6;
        t1=t2=t3=t4=t5=t6=-1.0;//initialize to negative (i.e. no  intersection)
        if (abs(dir.x) > R_EPSILON){
          //A=1,B=C=0,D=-corner1.X 
          t1 = -(d0.x + (-p1.x))/(dir.x);//front face
          //A=1,B=C=0,D=-corner2.X
          t4 = -(d0.x + (-p2.x))/(dir.x);//back face
        }
        if (abs(dir.y) > R_EPSILON){
          t2 = -(d0.y + (-p1.y))/(dir.y);//left face
          t5 = -(d0.y + (-p2.y))/(dir.y);//right face
        }
        if  (abs(dir.z) > R_EPSILON){
          t3 = -(d0.z + (-p1.z))/(dir.z);//bottom face
          t6 = -(d0.z + (-p2.z))/(dir.z);//top face
        }
        Vector<Double> v= new Vector<Double>(6);
        v.set(0, t1);
        v.set(1, t2);
        v.set(2, t3);
        v.set(3, t4);
        v.set(4, t5);
        v.set(5, t6);
        //Sort in ascending order
        Collections.sort(v);
        //Take  the first nonnegative  t, i.e.  the shortest  distance the
        //beam can travel in the voxel space before crossing some wall
        //vector<double>::iterator it = find_if(v.begin(),v.end(),
    		//			  bind2nd(greater_equal<double>(),0.0));
        double tdist = R_HUGE; //TODO: Catch if no exit point available.
        for(int i =0; i<6;i++){
        	if(v.get(i)>0) tdist = v.get(i);
        }
      /*  if (it == v.end()){
          cerr << "No Exit point from voxel space (All t < 0). Error!!!" << endl;
          cerr << "Start point " << d0 <<endl;
          cerr << "Beam direction " << dir <<endl;
          cerr << "t1,....,t6 " << flush;
          copy(v.begin(),v.end(),ostream_iterator<double>(cerr," "));
          cerr << endl;
        }
        else{
          tdist = *it;
        }*/ //TODO: Check whether this is correctly implemented now.
        
        //The exit point from the voxel space
        PositionVector exit = new PositionVector(d0);
        exit.add(PositionVector.mul(dir,tdist));
        Point3d exit2 = new Point3d(exit);
        double tau = NearbyShading(exit2,dir,
    			       forest_descriptor.GetValue(LGMAD.LGAH),
    			       forest_descriptor.GetValue(LGMAD.LGAcbase),
    			       forest_descriptor.GetValue(LGMAD.LGALAIc),
    			       forest_descriptor.GetValue(LGMAD.LGALAIb));
        return tau;
	}
    
    //Nearby Shading included in VoxelSpace. In cLignum NearbyShading is a seperated function:
    
    //Original Comment:
    //NearbyShading  calculates shading caused  by forest  surrounding the
    //voxelspace.  The  extinction is  exponetial in a  homogeneous turbid
    //medium.   It  is assumed  that  the  surrounding  forest extends  to
    //infinity.   The  optical  thickness  is  calculated  separately  for
    //conifers and broadleved  trees on the basis of  their respective LAI
    //values (total  needle surface area for conifers,  one-sided area for
    //deciduous).  The  extinction coefficients  are 0.14 and  0.5 (random
    //leaf  orientation) [maybe  should be  given as  input?]. If  the ray
    //(from a point in voxel space to  a sector in the sky) comes out from
    //ceiling  (i.e.   z-component  of  out  >  Htop  -  R_EPSILON)  =  no
    //extinction.
    //Input  out = point where the beam leaves the voxelspace
    //  direction = direction of the beam	
    //  Htop z-coordinate of top of canopy m
    //  Hbot z-coordinate of bottom of canopy , m
    //  LAIc LAI of conifers (total needle area)
    //  LAIb LAI of broadleaves (one-sided leaf area)
    //  k_conifer Extinction coefficient of conifer foliage area (total)
    //  k_decidious Extinction coefficient of deciduous foliage area (one-sided)
    //NOTE: It is assumed that direction is normalized, i.e. ||direction|| = 1
    
    //Output: proportion  left of radiant intensity  after passing through
    //surrounding forest (i.e. no shading = 1)
    
    //This is for some applications that that call NearbyShading withoout values for
    //extincion coefficients (and assume these values for them)
    
    public static double NearbyShading(Point3d out, PositionVector direction, double Htop,
    						double Hbot, double LAIc, double LAIb) {
    	
    	double k_conifer = 0.14;
    	double k_deciduous = 0.5;

    	double r_turn = NearbyShading(out, direction, Htop, Hbot, LAIc, LAIb, k_conifer, k_deciduous);
    	return r_turn;
		
	}
    
    public static double NearbyShading(Point3d out, PositionVector direction, double Htop, double Hbot,
    								 double LAIc, double LAIb, double k_conifer, double k_deciduous){
    	
    	if(out.z >= Htop - R_EPSILON)
    	    return 1.0;    //no shading if out from ceiling

    	  // Inclination angle of the direction (from horizon),
    	  // length of direction = 1, hence z coordinate = sin(alpha)
    	  double sin_alpha = direction.z;
    	  if(max(1.0-sin_alpha,sin_alpha-1.0) < R_EPSILON)
    	    return 1.0;   // vertical beam, cannot travel in surrounding 
    	                  // (this check may be unnecessary)

    	  if(max(sin_alpha, -sin_alpha) < R_EPSILON)
    	    return 0.0;       //horizontal ray
    	 
    	  double Hpoint = out.z;
    	  if(Hpoint < Hbot)  Hpoint = Hbot;  //Allows for possibility that the ray
    	                   //comes out lower than bottom of surrounding canopy


    	  double distance = (Htop - Hpoint) / sin_alpha;

    	  double dens_c  = LAIc/(Htop-Hbot);
    	  double dens_b  = LAIb/(Htop-Hbot);

    	  double optTh = (k_conifer*dens_c + k_deciduous*dens_b)*distance;
    	  double ext;
    	  if(optTh < R_HUGE)
    	    ext = exp(-optTh);
    	  else
    	    ext = 0.0;

    	  // if(lage > 18)
    	  // return 1.0;
    	  //else 
    	      return ext;
    	
    }
    //Original Comment:
    //Runs updateValues() of voxelboxes (whatever it does)
    public void updateBoxValues() {
    	 for(int i1=0; i1<Xn; i1++){
    	   for(int i2=0; i2<Yn; i2++){
    		for(int i3=0; i3<Zn; i3++){
    			voxboxes[i1][i2][i3].updateValues();
    		}
    	   }
    	 }
	}
    
    public double calculateTurbidLight() {
    	return this.calculateTurbidLight(true);
	}
    //default with true as argument
    //
    //	The function calculates the Qin and Qabs-values to every VoxelBox.
    //    self_shading determines if the box shades itself or not
    public double calculateTurbidLight(boolean self_shading) {
    	updateBoxValues();
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    int num_dirs = sky.numberOfRegions();
    	    //This might  make the voxel  space slow in  execution: if
    	    //there  is something  in the  voxel boxes,  the following
    	    //loop is executed.
    	    if (voxboxes[i1][i2][i3].isEmpty() == false)
    	      {		
    			
    		for(int i = 0; i < num_dirs; i++)
    		  {	
    		    Vector<Double> rad_direction = new Vector<Double>(3);
    		    double iop = sky.diffuseRegionRadiationSum(i,rad_direction);
    		    PositionVector radiation_direction = new PositionVector(rad_direction.get(0),
    		    		rad_direction.get(1), rad_direction.get(2));
    		    radiation_direction.normalize();
    		    double ext = this.getBorderStandExtinction(voxboxes[i1][i2][i3].getCenterPoint(),radiation_direction);
    		    Vector<VoxelMovement> vec = new Vector<VoxelMovement>();		
    		    this.getRoute(vec, i1, i2, i3, radiation_direction);
    		    int size = vec.size();
                                           
    		    //other boxes
    		    if (size>1){
    		      for (int a=1; a<size; a++)
    			{
    			  VoxelMovement v1 = vec.get(a-1);
    			  VoxelMovement v2 = vec.get(a);	 		  
    			  ext = voxboxes[v1.x][v1.y][v1.z].extinction(v2.l); 
    			  iop = iop * ext;
    			}
    		    }
    		    //the self shading 
    		    if (size>0 && self_shading)
    		      {
    			// qin is here the value on the surface of VoxelBox
    			double qin = iop;

    			//The distance the beam travels inside this
    			//VoxelBox (from surface to middle)

    			double inner_length = vec.get(0).l;

    			//extinction coefficient on the way through VoxBox
    			double ext2 = voxboxes[i1][i2][i3].extinction(inner_length);
    							
    			//radiant intensity of the beam when it comes
    			//out of the VoxBox
    			double qout = iop * ext2;

    			//and attenuation inside the VoxBox
    			voxboxes[i1][i2][i3].addQabs(qin - qout);
    			//Now we calculate only to the mid point; the
    			//radiation the VoxBox receives is the one at
    			//the mid point.

    			ext = voxboxes[i1][i2][i3].extinction(inner_length/2.0);
    			iop = iop * ext;
    		      }
    		    //radiation coming, Qin,  to the VoxBox
    		    voxboxes[i1][i2][i3].addRadiation(iop); 
    		  }
    	      }
    	  }   
        return 0;
	}
    
    //OriginalComment:
    //diffuse is to calcluate the real diffuse from standard 1200, structureFlag is used to indicate if 
    //it is the first time light calculation after structure update
  /*  public double calculatePoplarLight(double diffuse, double structureFlag) {
		//TODO: Add Later
    	//intialize the sequence with some negative number
        int bernoulli_seed = -1;//gradseed;//-1;
      //produce the next number from sequence
        int bernoulli_sequence = 1;
        Bernoulli ber(bernoulli_seed);
        updateBoxValues();
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    int num_dirs = sky->numberOfRegions();
    	    if (voxboxes[i1][i2][i3].isEmpty() == false)
    	      {
    		//Calculate diffuse light only once during the short time step model
    	        if(structureFlag<=0)
    		  {
    		    voxboxes[i1][i2][i3].updateValues();
    		    for(int i = 0; i < num_dirs; i++)
    		      {	
    			vector<double> rad_direction(3);
    			LGMdouble iop = 
    			  sky->diffuseRegionRadiationSum(i,rad_direction);
    			PositionVector rd(rad_direction[0], rad_direction[1], rad_direction[2]);		    
    			rd.normalize();
    			LGMdouble maximum_box_project_area= fabs(Xbox*Ybox*rd.getZ())+fabs(Xbox*Zbox*rd.getY())+ fabs(Zbox*Ybox*rd.getX());
    			vector<VoxelMovement> vec;		
    			getRoute(vec, i1, i2, i3, rd);
    			//Trace the  path from one  sector towards the
    			//tree (getRoute follows the light beam from a
    			//leaf towards a sector)
    			int hits=0;

    			//stop conition i > 0 means no self shading
    			for (int i = vec.size()-1; i > 0; i--){ 
    			  VoxelMovement vm = vec[i];
    			  //cout << "VM " << i << " x " << vm.x << " y " << vm.y << " z "  << vm.z << endl;
    			  //cout << "Xn " << Xn << " Yn " << Yn << " Zn " << Zn << endl; 
    			  LGMdouble leaf_area = voxboxes[vm.x][vm.y][vm.z].getLeafArea();
    			  PositionVector big_leaf_normal=voxboxes[vm.x][vm.y][vm.z].getBigLeafNormal();
    			  LGMdouble projected_leaf_area=leaf_area*fabs(big_leaf_normal.getX()*rd.getX()
    								       + big_leaf_normal.getY()*rd.getY()
    								       + big_leaf_normal.getZ()*rd.getZ());
    			  double area_ratio = projected_leaf_area/maximum_box_project_area;
    			  if (area_ratio>0.2 && area_ratio<=0.8)
    			    area_ratio = 0.51*area_ratio +0.1;
    			  else if (area_ratio>0.8)
    			    area_ratio = 0.7*area_ratio/(area_ratio+exp(1-2.63*area_ratio));
    			  //if projected leaf area > maximum_box_project_area then several layers
    			  if (area_ratio){
    			    //there is foliage (area_ratio > 0), so if
    			    //layers == 0 after the cast to int then 1
    			    //layer, if  1 then 2 layers, if  2 then 3
    			    //layers
    			    int layers = (int)(projected_leaf_area / maximum_box_project_area) + 1; 
    			    //probability 'p' to hit one or several layers is determined by area_ratio.
    			    double p=min(area_ratio, 1.0);
    			    //....comes from the bernoulli distribution
    			    double s=ber(p,bernoulli_sequence);
    			    //cout <<  "leaf_area " << leaf_area << " area_ratio " << area_ratio 
    			    //   << " p " << p << " s " << s << endl;
    			    //there was a hit
    			    if (s == 1)
    			      hits = hits + layers;
    			    if (hits == 3)
    			      //third hit will extinguish the beam completely
    			      break;
    			  }
    			}//for (int = v.size()-1
    			//assume free path
    			double percent = 1.0;
    			if (hits == 1)
    			  //10 percent received by this focal voxel MS: section 2.3.3
    			  percent = 10.0/100.0;
    			else if (hits == 2)
    			  //1 percent received by this focal voxel if two layers of foliage
    			  percent = (10.0/100.0)*(10.0/100.0);
    			else if (hits > 2)
    			  //three layers completely shades the focal voxel
    			  percent = 0.0;
    			//set the incoming radiation from one sector;
    			//cout << "hits " << hits <<  " % " << percent << " iop " << iop <<endl<<endl;
    			//if (hits)
    			//cout << "Hits " << hits << " % "  << percent << " iop " << iop << " " 
    			//     << " Qin " << percent*iop <<endl;
    			voxboxes[i1][i2][i3].addRadiation(percent*iop);
    		      }//for (int = 0; i < num_dirs; i++)
    		  }//if (structureFlag <= 0)
    	      }//if (voxboxes[i1][i2][i3].isEmpty() == false)
    	    //cout << "Qin diffuse voxel " << i1 << " "  << i2 << " "  << i3 << " Qin " << voxboxes[i1][i2][i3].getQin() <<endl; 

    	    //Calculate the light for direct beam, this is exactly the
    	    //same as  for diffuse light  but computed each  time step
    	    //during the short time step
    	    vector<double> direct_direction(3);
    	    LGMdouble iop= sky->directRadiation(direct_direction); 
    	    PositionVector dr(direct_direction[0],direct_direction[1],direct_direction[2]);
    	    dr.normalize();
    	    LGMdouble maximum_box_project_area= fabs(Xbox*Ybox*dr.getZ())+fabs(Xbox*Zbox*dr.getY())+ fabs(Zbox*Ybox*dr.getX());
    	    vector<VoxelMovement> vec;
    	    getRoute(vec, i1, i2, i3,dr);
    	    //Trace the  path from one  sector towards the
    	    //tree (getRoute follows the light beam from a
    	    //leaf towards a sector)
    	    int hits=0;
    	    //stop condition i > 0 means no self shading
    	    for (int i = vec.size()-1; i > 0; i--){ 
    	      VoxelMovement vm = vec[i];
    	      LGMdouble leaf_area = voxboxes[vm.x][vm.y][vm.z].getLeafArea();
    	      PositionVector big_leaf_normal=voxboxes[vm.x][vm.y][vm.z].getBigLeafNormal();
    	      LGMdouble projected_leaf_area=leaf_area*fabs(big_leaf_normal.getX()*dr.getX()
    							   + big_leaf_normal.getY()*dr.getY()
    							   + big_leaf_normal.getZ()*dr.getZ());
    	      double area_ratio = projected_leaf_area/maximum_box_project_area;
    	      if (area_ratio>0.2 && area_ratio<=0.8)
    		area_ratio = 0.51*area_ratio +0.1;
    	      else if (area_ratio>0.8)
    		area_ratio = 0.7*area_ratio/(area_ratio+exp(1-2.63*area_ratio));
    	      //probability 'p' to hit a leaf....
    	      double p=min(area_ratio, 1.0);
    	      //....comes from the bernoulli distribution
    	      double s=ber(p,bernoulli_sequence);
    	      //cout <<  "Qin direct leaf_area " << leaf_area << " area_ratio " << area_ratio 
    	      //	   << " p " << p << " s " << s << endl;
    	      if (s == 1)
    		hits = hits + 1;
    	      if (hits == 3)
    		break;
    	    }//for (int = vec.size()-1
    	    //assume free path
    	    double percent = 1.0;
    	    if (hits == 1)
    	      //10 percent received by this focal voxel MS: section 2.3.3
    	      percent = 10.0/100.0;
    	    else if (hits == 2)
    	      //1 percent received by this focal voxel if two layers of foliage
    	      percent = (10.0/100.0)*(10.0/100.0);
    	    else if (hits > 2) 
    	      //three layers completely shades the focal voxel
    	      percent = 0.0;
    	    //set the incoming direct radiation from one sector;
    	    //cout << "hits " << hits <<  " % " << percent << " iop " << iop <<endl<<endl;
    	    voxboxes[i1][i2][i3].addRadiation(percent*iop);
    	    //cout << "Qin diffuse + direct voxel " << i1 << " "  << i2 << " "  << i3 << " Qin " << voxboxes[i1][i2][i3].getQin() <<endl;
    	  }//for (int i3=0; i3 < Zn; i3++)
        return 0;
	} */
    
   /* public void setLightValues() {
		//TODO: Function Declaration ? Is this function used?
	}*/
    
    /*
    public void setLight() {
    	//TODO: Function Declaration ? Is this function used?
	}*/
    
    //Original Comment:
    //
    // A function used to fill all the VoxBoxes with a initial
    // value
    //
    public void fillVoxelBoxes(double needleA, double leafA) {
    	for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    voxboxes[i1][i2][i3].setArea(needleA,leafA);
    		  }
		
	}
    
    public void fillVoxelBoxes(double inivalue, int beginZ, int endZ) {
    	for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    if (i3 >= beginZ && i3 <= endZ){
    		      voxboxes[i1][i2][i3].setArea(inivalue,inivalue);
    		    }
    		    else
    		      voxboxes[i1][i2][i3].setArea(0,0);
    		  }
	}
     
    //Original Comment:
    //First four arguments are for box.S() STAR sum
    public void FillVoxelBoxesWithNeedles(double Sf, double Wf, double Rf, double L, int beginZ, int endZ) {
    	for(int i1=0; i1<Xn; i1++)
    	      for(int i2=0; i2<Yn; i2++)
    		for(int i3=0; i3<Zn; i3++)
    		  {
    		    if (i3 >= beginZ && i3 <= endZ){
    		      voxboxes[i1][i2][i3].addNeedleArea(Sf*Wf);
    		      voxboxes[i1][i2][i3].addNeedleMass(Wf);
    		      //for-loop runs eight times (says mika).
    		      for (double phi=0; phi<PI/2.0; phi+=PI/16)
    			{
    			  //As in DumpSegment
    			  voxboxes[i1][i2][i3].addStarSum(voxboxes[i1][i2][i3].
    							  S(phi,Sf,Wf,Rf,L)/8.0);
    			  voxboxes[i1][i2][i3].updateValues();
    			}
    		      voxboxes[i1][i2][i3].increaseNumberOfSegments();
    		      voxboxes[i1][i2][i3].updateValues();
    		    }
    		  }
	}
    
    
    /*BoundingBox& searchDimensions(BoundingBox &bbox,
			  bool boolDimensionsWithNumBoxes);

	void searchDimensions(bool boolDimensionsWithNumBoxes=true){ 
	searchDimensions(bbox, boolDimensionsWithNumBoxes); 
	}
	public void dumpTrees() {
		
	}
	*/ //TODO : Where are those functions declared in cLignum?
    
    /*void writeVoxBoxesToFile(const string& filename, bool all = true);
    //Write voxel  boxes to file up to Z index
    void writeVoxBoxesToFile(const string& filename, int z);
    void writeVoxBoxesToFile2(const string& filename);
    void writeVoxelBoxesToGnuPlotFile(const string& filename, 
				      const string& sep=" ");
    void writeVoxelSpaceContents();
    void writeStarMean();
    void writeMeanDirection();*/ //TODO: Add only if needed
    
    public double getMeanFoliageAreaDensity() {
    	double meanD = 0.0;
        int nb = 0;
        double vol = Xbox*Ybox*Zbox;

        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    if(voxboxes[i1][i2][i3].isEmpty() == false)
    	      {
    		nb++;
    		meanD += (voxboxes[i1][i2][i3].getLeafArea()+
    			  voxboxes[i1][i2][i3].getNeedleArea())/vol;
    	      }
    	  }
        if(nb > 0)
          return meanD/nb;
        else
          return 0.0;
	}
    
    public void calculateMeanQabsQin() {
    	for(int i1=0; i1<Xn; i1++){
    	       for(int i2=0; i2<Yn; i2++){
    		for(int i3=0; i3<Zn; i3++){
    		  if (voxboxes[i1][i2][i3].getNumber_of_segments() > 0){
    		    double qabs_mean = 
    		      voxboxes[i1][i2][i3].getQ_abs()/voxboxes[i1][i2][i3].getNumber_of_segments();
    		    double qin_mean = 
    		      voxboxes[i1][i2][i3].getQ_in()/voxboxes[i1][i2][i3].getNumber_of_segments();
    		    voxboxes[i1][i2][i3].setQabsMean(qabs_mean);
    		    voxboxes[i1][i2][i3].setQinMean(qin_mean);
    		  }
    		}
    	       }
    	    }
	}
    
    public double getNeedleArea() {
		
    	double ret = 0.0;
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    ret += voxboxes[i1][i2][i3].getNeedleArea();
    	  }
        return ret;
	}
    
    public double getLeafArea() {
    	double ret = 0.0;
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    ret += voxboxes[i1][i2][i3].getLeafArea();
    	  }
        return ret;
	}
    
    public double getFoliageArea() {
    	double ret = 0.0;
        for(int i1=0; i1<Xn; i1++)
          for(int i2=0; i2<Yn; i2++)
    	for(int i3=0; i3<Zn; i3++)
    	  {
    	    ret += voxboxes[i1][i2][i3].getFoliageArea();
    	  }
        return ret;
	}
    
    //Original Comment:
    //Evaluate needle area of every (horizontal) layer of voxelboxes and return them in a vector
    //specifying height of the layer center and needle area index in that layer.
    //First element of vector = lowest layer.
    //Unit = leaf area / ground area (thus unitless, LAI = sum of vector)
    //Hmax and Hmin give the top and bottom of the space and n is no. of boxes in z direction
    //TODO: Use different Pair-Class. PairMinMax does not make sense here.
    public void evaluateVerticalNeedleAreaDensity(double Hmax, double Hmin, int n, Vector<PairMinMax> NAD) {
    	
    		double area = (double)Xn * (double)Yn * Xbox * Ybox;
    	    Hmax = corner2.z;
    	    Hmin = corner1.z;
    	    n = Zn;

    	    for(int i1=Zn-1; i1>=0; i1--){
    	      double na_sum = 0.0;
    	      for(int i2=0; i2<Xn; i2++)
    		for(int i3=0; i3<Yn; i3++)
    		  na_sum += voxboxes[i2][i3][i1].getNeedleArea();

    	      na_sum /= area;
    	      
    	      NAD.add(new PairMinMax(voxboxes[0][0][i1].getCenterPoint().z,na_sum)); //TODO: Test this !

    	    }
	}
    
    public void DumpCfTree(VoxelSpace s, Tree tree, int num_parts, boolean wood) {
    	
    	//Note that in case wood == false, wood variables are not set
    	DumpCFTreeFunctor f = new DumpCFTreeFunctor(num_parts, wood);
    	f.space = s;
    	tree.forEachDumpCFTreeFunctor(tree, f);  	
    	s.updateBoxValues();
	}
    
}
