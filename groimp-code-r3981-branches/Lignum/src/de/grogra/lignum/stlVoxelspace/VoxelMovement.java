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

import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class VoxelMovement {

	public int x;//box indices
	public int y;
	public int z;
	public double l;//Path length in the box
	public double af;//Foliage area in the box
	public double tau;//the  extinction  caused  by  objects  in  the  box
		  //(pairwise comparison)
	public double STAR_mean;
	public double n_segs_real;
	public PositionVector  mean_direction;
	
	public VoxelMovement() {
		x = 0;
		y = 0;
		z = 0;
		l = 0.0;
		af = 0.0;
		tau = 0.0;
		STAR_mean = 0.0;
	}
}
