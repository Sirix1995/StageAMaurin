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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XContentHandler  implements ContentHandler {
	private final ArrayList<Tree> allTrees = new ArrayList<Tree>();
	private final ArrayList<Species> allSpecies= new ArrayList<Species>();
	private String currentValue;
	private Tree tree;
	private Species species;
	private int parsingMode=0;
	int TREE=1,TYPE=2; //parsing modes
	ParameterHandler ph=new ParameterHandler();
	
	
	//getter
	public ArrayList<Tree> getAllTrees(){
			return allTrees;
	}
	public ArrayList<Species> getAllSpecies(){
			return allSpecies;
	}
//implementing ContentHandler
	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (localName.equals("Baum")) {		//starts parsing Trees
			tree=new Tree();
			parsingMode=TREE;
		 }else if(localName.equals("Baumartencode")){ //starts parsing Species
			 species=new Species();
			 parsingMode=TYPE;
		 }
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(parsingMode==TREE){
			if(!localName.equals("Baum")){
				/*
				 * In some cases the XML Parser reads in the wrong value:
				 * for example: value 3.99 becomes 99 
				 * For now the workaround is to replace an unusual height value with an average of all already written Values.
				 */
				if(localName.equals("MittlererKronenDurchmesser_m")){	
					if(Float.parseFloat(currentValue)>20){
						currentValue=""+sumMittlererKronenDurchmesser_m/countMittlererKronenDurchmesser_m;
						System.out.println(currentValue);
					}else {
						countMittlererKronenDurchmesser_m++;
						sumMittlererKronenDurchmesser_m+=Float.parseFloat(currentValue);
					}
				}
					
				tree.data.put(localName, currentValue); //add Value to hashmap
			}
			if (localName.equals("Baum")) {
				if(tree.filterAll(ph.getFilters())) {
					allTrees.add(tree); //add Tree to arrayList
				}
			}
		}else if(parsingMode==TYPE){
			if (localName.equals("Code")){
				species.setCode(Integer.parseInt(currentValue));
			}
			if(localName.equals("lateinischerName")){
				species.setLatinName(currentValue);
			}
			if (localName.equals("Baumartencode")){
				allSpecies.add(species);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		 currentValue = new String(ch, start, length);
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}
}
