package de.grogra.bwinReader;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class XContentHandler  implements ContentHandler {
	private ArrayList<Tree> allTrees = new ArrayList<Tree>();
	private ArrayList<Species> allSpecies= new ArrayList<Species>();
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
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

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

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		 currentValue = new String(ch, start, length);
		
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}
}
