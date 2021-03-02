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
import static java.lang.Math.min;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Mathsym;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class BoundingBox implements Mathsym {

	public BoundingBox() {}
	
	private final Point3d minxyz = new Point3d(R_HUGE, R_HUGE, R_HUGE);
	private final Point3d maxxyz = new Point3d(-R_HUGE, -R_HUGE, -R_HUGE);
	
	//Original Comment:
	//Addition of a BoundingBox to this. It finds the (smallest) big one containing
    //both of them (mathematically: intersection of all BoundingBoxes that contain
    //this and the other BoundingBox).
	
	public Point3d getMin() { return minxyz; }
	public Point3d getMax() { return maxxyz; }
	public void setMin(Point3d p){minxyz.set(p);}
	public void setMax(Point3d p){maxxyz.set(p);}
	public void setMinX(double x) { minxyz.x = x; }
	public void setMinY(double y) { minxyz.y = y; }
	public void setMinZ(double z) { minxyz.z = z; }
	public void setMaxX(double x) { maxxyz.x = x; }
	public void setMaxY(double y) { maxxyz.y = y; }
	public void setMaxZ(double z) { maxxyz.z = z; }
	
	public BoundingBox add(BoundingBox add_this) {
		 minxyz.x = (min(minxyz.x,add_this.getMin().x));
	     minxyz.y = (min(minxyz.y,add_this.getMin().y));
	     minxyz.z = (min(minxyz.z,add_this.getMin().z));
	     maxxyz.x = (max(maxxyz.x,add_this.getMax().x));
	     maxxyz.y = (max(maxxyz.y,add_this.getMax().y));
	     maxxyz.z = (max(maxxyz.z,add_this.getMax().z));
	     return this;
	}
	
	public void accumulateFindCfBoundingBox(BoundingBox b_box, CfTreeSegment ts) {
		
		/*
		// TODO
		if (foliage) {
			if (ts.getLGAWf() < R_EPSILON)
				return b_box;
		}
		*/
		
		Point3d base = ts.getPoint();
		Point3d top = ts.getEndPoint();
		double rSh = ts.getLGARf(); //max dist of needle tip from woody part
	     
		if(b_box.getMin().x > base.x-rSh) b_box.setMinX(base.x-rSh);
		if(b_box.getMin().y > base.y-rSh) b_box.setMinY(base.y-rSh);
		if(b_box.getMin().z > base.z-rSh) b_box.setMinZ(base.z-rSh);
		if(b_box.getMin().x > top.x-rSh) b_box.setMinX(top.x-rSh);
		if(b_box.getMin().y > top.y-rSh) b_box.setMinY(top.y-rSh);
		if(b_box.getMin().z > top.z-rSh) b_box.setMinZ(top.z-rSh);
		if(b_box.getMax().x < base.x+rSh) b_box.setMaxX(base.x+rSh);
		if(b_box.getMax().y < base.y+rSh) b_box.setMaxY(base.y+rSh);
		if(b_box.getMax().z < base.z+rSh) b_box.setMaxZ(base.z+rSh);
		if(b_box.getMax().x < top.x+rSh) b_box.setMaxX(top.x+rSh);
		if(b_box.getMax().y < top.y+rSh) b_box.setMaxY(top.y+rSh);
		if(b_box.getMax().z < top.z+rSh) b_box.setMaxZ(top.z+rSh);
	}
	
	
	/*
	inline friend ostream& operator << (ostream& os,
			const BoundingBox& bb) {
os << "BoundingBox:" << '\n' << "Min -  x: " << bb.minxyz.getX() 
<<" y: " << bb.minxyz.getY()  << " z: " << bb.minxyz.getZ()
<< '\n' << flush;
os << "Max -  x: " << bb.maxxyz.getX() <<" y: " <<
bb.maxxyz.getY()  << " z: " << bb.maxxyz.getZ() << '\n' << flush;
return os;
} */ //TODO: Is this necessary ?
}
