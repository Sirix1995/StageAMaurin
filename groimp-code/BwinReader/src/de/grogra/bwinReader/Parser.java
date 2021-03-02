/*
 * Copyright (C) 2020 GroIMP Developer Team
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package de.grogra.bwinReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class Parser {
	
	public static File parseToTempFile(File inPFile){
		 	String inP =inPFile.getAbsolutePath();
			String output = null;
			try {
	
				output = parse(inP);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedWriter writer = null;
			File ergFile = null;
			try {
				ergFile = File.createTempFile("tmp", ".rgg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer = new BufferedWriter(new FileWriter(ergFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.write(output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ergFile;
		
	}
	public static int getOldestTree(ArrayList<Tree> liste){
		int x=0;
		for(int i=0; i<liste.size();i++){
			int tmp=Integer.parseInt(liste.get(i).data.get("Alter_Jahr"));
			if(x<tmp){
				x=tmp;
			}
		}
		return x;
	}

	public static String parse(String path) throws SAXException, IOException{
	    String output="\n";
	    ParameterHandler ph=new ParameterHandler();
		// Create XMLreader and parser
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
	    FileReader reader = new FileReader(path);
	    InputSource inputSource = new InputSource(reader);
	    XContentHandler xc=new XContentHandler();
	    xmlReader.setContentHandler(xc);
	    xmlReader.parse(inputSource);
	    //add PointerModules to output
		if(ph.getPointerMode()==1) {
			output+="module TreePointer(int code,int age);";
		}else if(ph.getPointerMode()==2) {
			output+="module Deciduous_pointer(int code, int age);\nmodule Conifer_pointer(int code, int age);";
		}
	    //add SpeciesModules to output
	    output+="// the basic Modules\n\n";
	    ArrayList<Species> typeList=xc.getAllSpecies();
	    for(int i=0; i<typeList.size();i++){
	    	output+=typeList.get(i).toString(ph)+"\n";
	    }
	    //add The Trees in groups this is necessary because for a stand larger then 2000 threes would 
	    //created init function would be to big for java to handle
	    int j=1;	//groupIndex
	    int max=100;	//groupSize
	    output+="int oldestTree="+getOldestTree(xc.getAllTrees())+";\n";
	    output+="\nprotected void init ()\n[\n\tAxiom ==>\n";
	    for(int i=0; i<xc.getAllTrees().size();i++){
	    	if(i%max==0){
	    		output+="[{init"+j+"();}];\n]\nprotected void init"+j+" ()\n[\n\tAxiom==>\n";
	    		j++;
	    	}
	    	output+="["+xc.getAllTrees().get(i).printTee(ph)+"]\n";
	    }
	    output+="; \n]\n";
		return output;
	}
}