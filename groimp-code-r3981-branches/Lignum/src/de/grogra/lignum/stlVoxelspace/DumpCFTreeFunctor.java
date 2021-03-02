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

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.stlLignum.CfTreeSegment;
import de.grogra.lignum.stlLignum.TreeCompartment;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class DumpCFTreeFunctor implements Mathsym {

	public double num_parts;
	public VoxelSpaceIF space;
	public boolean dumpWood;
	
	public DumpCFTreeFunctor(int n, boolean wood) {
	
		num_parts = n;
		dumpWood = wood;
	}
	
	public TreeCompartment eval(CfTreeSegment tc){
		
		boolean foliage = false;
		if(tc.getLGAWf() > R_EPSILON) foliage = true;
		
		if(foliage || dumpWood){
			Point3d p = new Point3d(tc.getPoint());
			PositionVector pv = new PositionVector(tc.getDirection());
			double length = tc.getLGAL();
			
			for (int i=1; i<(num_parts+1.0); i++)
		    {
		      Point3d p1 = new Point3d(p);
		      pv.mul(length* i/(num_parts+1));
		      p1.add(pv);
		      VoxelBox this_box = space.getVoxelBox(p1);
		      if(foliage)
			this_box.DumpCfSegmentFoliage(tc, (int)num_parts);
		      if(dumpWood) 
			this_box.DumpSegmentWood(tc, (int)num_parts);
	 	    }
		}
		return tc;
	}
	
}
