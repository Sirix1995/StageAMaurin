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

package de.grogra.lignum.jadt; // Like package cxxadt in cLignum

import java.io.File;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class ParametricCurve {
	
	private StringBuffer file = new StringBuffer();
	private Vector<Double> v = new Vector<Double>();
	int num_of_elements;
	
	public ParametricCurve() {
	}	//Does nothing, use method install to create ParametricCurve
	
	public ParametricCurve(String file_name){
		read_xy_file(file_name);
	}
	
	//dummy is used to overload the constructor
	public ParametricCurve(String values, int dummy){
		dummy = 0;
		v.clear();
		file.setLength(0);
		Scanner s = new Scanner(values);
		while(s.hasNext()){
			v.add(s.nextDouble ());
		}
		v.add(Double.MAX_VALUE); // Marks the last x-y value
		// I just copied this idea from cLignum however I guess that there 
		//are easier solutions.
		num_of_elements = v.size();
		//System.out.println(num_of_elements);
	}
	
	public ParametricCurve(Vector<Double> v1){
		v = v1;
		num_of_elements = v.size();
	}
	
	public ParametricCurve(ParametricCurve pc){
		file = pc.file;
		v = pc.v;
		num_of_elements=pc.num_of_elements;
	}
	
	//Original Comment: Constant function
	public ParametricCurve(double c){
		v.clear();
		v.add(0.0);
		v.add(c);
		v.add(1.0);
		v.add(c);
		v.add(Double.MAX_VALUE); //Mark the end of x-y pairs
		num_of_elements = v.size();
	}
	
	public void install(String file_name) {
		try {
			read_xy_file(file_name);
			if(v.isEmpty()) throw new EmptyStackException();
		} catch (Exception e) {
			System.err.print("Error input file does not contain xy pairs.");
		}
	}
	
	
	private void read_xy_file(String file_name){
		try {
			v.clear();
			file.append(file_name);
			Scanner s = new Scanner(new File(file_name));
			while(s.hasNext()){
				String next = s.nextLine ();
				if(!(next.startsWith ("#") || next.startsWith ("//") || next.startsWith ("%"))) {
					StringTokenizer tok = new StringTokenizer(next,"\t ");
					v.add(Double.parseDouble (tok.nextToken().replace(",", ".")));
					v.add(Double.parseDouble (tok.nextToken().replace(",", ".")));
				}
			}
			v.add(Double.MAX_VALUE); // Marks the last x-y value
			num_of_elements = v.size();		
		} catch (Exception e) {
			System.err.print("Could not open file" + file_name);
		}
	}
	
	public double eval(double x) {
		int i =0;
		for(i = 0; (v.get(i)<=x) && (v.get(i) != Double.MAX_VALUE); i+=2);
		//OriginalComment:
		//if x is out of bounds approximate according to last values
		if (i == 0)
		    i+=2;
		  else if (v.get(i) == Double.MAX_VALUE)
		    i-=2;
		//OriginalComment:
		//the evaluation of the function
		
		return v.get(i-1)+(v.get(i+1)-v.get(i-1))*((x-v.get(i-2)))/(v.get(i)-v.get(i-2));
		
	}
	

}
