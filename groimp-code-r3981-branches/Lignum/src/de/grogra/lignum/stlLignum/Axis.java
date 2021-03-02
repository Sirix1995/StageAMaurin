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

import java.util.LinkedList;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class Axis extends TreeCompartment {

	private LinkedList<TreeCompartment> tc_ls;

	public LinkedList<TreeCompartment> GetTreeCompratmentList() {
		// TODO Auto-generated method stub
		return tc_ls;
	}
	
	public Axis(Tree tree) {
		super(tree);
	}

}