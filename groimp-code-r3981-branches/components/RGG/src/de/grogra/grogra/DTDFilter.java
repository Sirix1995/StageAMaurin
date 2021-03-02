/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.grogra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.grammar.Input;
import de.grogra.grammar.LexicalException;
import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.SemanticException;
import de.grogra.grammar.Token;
import de.grogra.grammar.Tokenizer;
import de.grogra.grammar.UnexpectedTokenException;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMP;
import de.grogra.imp3d.objects.Null;
import de.grogra.math.TMatrix4d;
import de.grogra.persistence.ServerConnection;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FileTypeItem;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.rgg.Library;
import de.grogra.rgg.model.RGGGraph;
import de.grogra.turtle.GD;
import de.grogra.turtle.L;
import de.grogra.turtle.MRel;
import de.grogra.turtle.Nl;
import de.grogra.turtle.OR;
import de.grogra.turtle.Ul;
import de.grogra.util.IOWrapException;
import de.grogra.vecmath.Math2;

/*
 * Vorl�ufige Kommentierung in Deutsch: Klasse soll Dateien im DTD-Format einlesen und f�r GroIMP aufbereiten.
 * Urspr�nglicher Autor: Dexu Zhao
 * �nderungen von: Jan D�rer
 * Historie: 06. 06. 07 -> Attribute und einige Methoden auf private gesetzt
 * 						-> Beginn der Kommentierung
 * 						-> Codeformatierung nach Sun
 * 						-> Entfernung nicht benutzter Imports und Attribute
 * 						-> Entfernung von doppelten Methoden / Konstanten, welche in GroIMP schon vorliegen
 * 						-> Attribute auf lokaler Ebene verteilen
 * 						-> Attribute die nur gelesen werden zu Konstanten transformiert
 * 						-> Attribute ein anderen (passenderen) Datentyp zugewiesen
 * 						-> Ersetzen von Hashtable zu HashMap
 * 						-> BUG1: Urspr�nglich wurde eine Vektordifferenz verwendet, welche eine Vektoraddition war (wurde gefixt)
 * 						-> BUG2: Farbindex wurde eingelesen aber nicht beachtet (wurde gefixt)
 * 						-> Umbenennung von Attributen (Refactoring)
 * 						-> Vorl�ufige Entnahme vom Befehl T, welche bis dato dasselbe bewirkte wie J
 * 						-> BUG3: N-Parameter wurde nicht richtig an die Struktur �bergeben (wurde gefixt)
 * 			08. 06. 07 	-> Kommentierung
 * 						-> Einige Methoden auf private gesetzt
 * 						-> Vorl�ufige Entnahme vom Befehl M, X und P
 * 						-> BUG4: Einfach DTD-Datei mit nur einem Spross, z. B. 1 L100 ## kann nicht angezeigt werden
 * 						-> Verschiedene Befehle werden noch nicht ausgewertet, z. B. Q
 * 			12. 06. 07	-> BUG5: Beim Durchmesser des Spross wurde mit dem Kernholzdurchmesser gerechnet (wurde gefixt)
 * 						-> BUG6: n-Parameter hat nur int-Werte entgegen genommen (wurde gefixt)
 * 						-> n-Parameter wurde als lokaler Turtle-Befehl eingef�gt und ist auswertbar
 * 						-> heartwoodDiameter entfernt, da es nicht ben�tigt wird
 * 			14. 06. 07	-> BUG7: q-Parameter wurde nicht richtige gesetzt, da nicht die Internodienanzahl von der Mutter ausgelesen wurden (wurde gefixt)
 * 						-> Q-Befehl wurde implementiert (q-Parameter wird direkt gesetzt und kann aktuell nicht ausgewertet werden!)
 * 			30. 07. 07	-> Der BUG4 wurde gefixt
 * 						-> Entfernen des NULL-Transformationsknoten, da F einen eigenen Transformationsknoten enth�lt
 * 						-> T, M, X, P werden gelesen aber nicht interpretiert
 * 						-> Verwendung des DTDShoot anstelle des F (DTDShoot leitet von F ab)
 * 			31. 07. 07	-> Wert�bernahme von der generativen Distanz, Ordnung und relative Position auf dem Mutterspross
 * 						-> Verarbeitung von Bl�ttern (B) und deren Positionierung mit \phyllotaxy
 * 						-> Bl�tter werden noch als rote Zylinder dargestellt
 * 						-> Verarbeitung des \leafarea-Befehls
 * 						-> Vor�bergehend wird \leaflength und \leafbreadth f�r die L�nge und den Durchmesser f�r den Zylinder verwendet
 * 						-> Verarbeitung von Fr�chten (F) und deren Positionierung
 * 						-> Fr�chte werden noch als blaue Zylinder dargestellt
 * 						-> Kommentierung
 * 			16. 07. 08	-> BUG8: Der Bezeichner in einer DTD-Zeile wird so umgewandelt (# vor jeder Zeile), dass der Tokenizer gezwungen ist den Bezeichner als String und nicht als Literal zu verarbeiten
 * 			07. 06. 09 	-> leaflength und leafbreath wurde als Parameter zur Visualisierung auskommentiert
 * 						-> Unterst�tzung von mehreren root-Objekten in DTD-Dateien
 * 			10. 06. 09	-> BUG9: Leerzeilen f�hrten zur Fehlermeldung (bedingt durch den Workaround f�r BUG8)
 */
public class DTDFilter extends FilterBase implements ObjectSource {
	private static final boolean CLASSIC_VERSION = false;			// false -> Verwende die Klasse DTDShoot, true -> verwende die normalen Turtlekommandos
	private static final char EOF = '\032';							// End Of File
	private static final char EOL = '\r';							// End Of Line
	private static final float FACTOR = 0.001f;						// Faktor zum Umrechnen in Millimeter 
	private static final float DEFAULT_SHORT_SHOOT_LENGTH = 7.07f;	// Standardwert f�r die L�nge eines Kurztriebs
	private static final int DEFAULT_DIRECTION = -5;				// Standardwert f�r die Richtung
	private static final float DEFAULT_THICKNESS = 1.001f;			// Standardwert f�r die Sprossdicke
	
	private static int leafCounter = 0;								// Z�hlt die Gessamtzahl an Bl�ttern, um einen Namen zu vergeben f�r den Shoot
	private static int fruitCounter = 0;							// Z�hlt die Gessamtzahl an Fr�chten, um einen Namen zu vergeben f�r den Shoot
	
	private float 	shootLength, distanceToMotherBasis, angle, azimuthAngle, diameter,
					nParameter, leafArea, leafLength, leafBreadth, qParameter;

	// Die wichtigen Parameter von Spross
	private int direction, order, noOfYear, internodiumCount, insertPosition, leafCount,
				fruitCount, shortShootCount, treeColor, globkug, genDistance,
				derivationSteps, minintn, phyllotax, jk;

	private boolean isDollar, isExtension, isBud, leafObjectAvailable, fruitObjectAvailable;

	private String nam, mnam, startaxiom, filename, currentLeafObject, currentFruitObject;
	
	public DTDFilter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(IOFlavor.NODE);
	}

	public DTDFilter(FileSource source) {
		super(null, source);
		setFlavor(IOFlavor.NODE);
	}
	
	public Object getObject() throws IOException {
		DTDTokenizer tokenizer = new DTDTokenizer();
		initDTDParameter();
		tokenizer.setSource(((ReaderSource) source).getReader(), source.getSystemId());
		
		// Erstellen des String für die DTD-Datei (Pfad und Dateiname)
		String dtdFile = source.toString().substring(source.toString().indexOf("[")+1, source.toString().length()-1);
		if(dtdFile.charAt(0) == '~')
			dtdFile = System.getProperty("user.home") +  dtdFile.substring(1);
		
		return getObjectImpl(tokenizer, dtdFile);
	}

	public Object getObject(FileSource fs) throws IOException {
		DTDTokenizer tokenizer = new DTDTokenizer();
		initDTDParameter();
		tokenizer.setSource(fs.getReader(), fs.getSystemId());
		
		// Erstellen des String für die DTD-Datei (Pfad und Dateiname)
		String dtdFile = System.getProperty("user.home") + System.getProperty ("file.separator") + fs.getSystemId ().substring (4);
		File tmpFile = createTmpFile(dtdFile, ((ByteArrayOutputStream) fs.getFile ()).toString ());
		Object o = getObjectImpl(tokenizer, dtdFile);
		deleteTmpFile(tmpFile);
		return o;
	}
	
	/**
	 * creates a temporal copy of the actually opent file (opend just at MemoryFileSystem)
	 * but unfortunately this parser works only on fils on the file system ...
	 * 	  
	 * @param dtdFile
	 * @param content
	 * @return
	 * @throws IOException
	 */
	private File createTmpFile(String dtdFile, String content) throws IOException {
		File file = new File(dtdFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.append(content);
		writer.flush ();
		writer.close ();
		return file;
	}

	/**
	 * deletes a file from the fiel system.
	 * normally it is only called from getObject(FileSource fs) to delet the by createTmpFile created file.
	 * 
	 * @param file
	 */
	private void deleteTmpFile(File file) {
		String fileName = file.getName ();
	    // Make sure the file or directory exists and isn't write protected
	    if (!file.exists())
	      throw new IllegalArgumentException("Delete: no such file or directory: " + fileName);
	    if (!file.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: " + fileName);

	    // Attempt to delete it
	    if (!file.delete())
	      throw new IllegalArgumentException("Delete: deletion failed");
	}
	
	public Object getObjectImpl(DTDTokenizer tokenizer, String dtdFile) throws IOException {	
		HashMap<String, ShootInfo> shoots = new HashMap<String, ShootInfo>(500);
		ShootInfo info = null;

		leafCounter = 0;
		fruitCounter = 0;
		
		Input in = tokenizer.getInput();
		try {
			// Sichern der original Datei
			createBackUpFile(dtdFile);
			// Ver�ndern der Datei so, dass vor jedem Bezeichner ein # steht
			// somit wird der Bezeichner nie als Literal verarbeitet
			preparationOfTheDTDFile(dtdFile);
			int newLine = 1;
			int linenr = 1;
			boolean isHeadAvailable;
			while (!in.isClosed()) {
				Token token = tokenizer.getToken();
				if (token.getType() == DTDTokenizer.LAUBPARA) {
					getLeafParameter(tokenizer, token);
				} else if (newLine == 1) {
					initDTDShootParameter();
					info = new ShootInfo();
					if (token.getType() == DTDTokenizer.COMM) {
						if (token.getText().compareTo("{") == 0) {
							boolean endD = false;
							while (!endD) {
								token = tokenizer.getToken();
								if (token.getText().compareTo("}") == 0)
									endD = true;
							}

						} else if (token.getText().compareTo("<") == 0) {
							boolean endD = false;
							while (!endD) {
								token = tokenizer.getToken();
								if (token.getText().compareTo(">") == 0)
									endD = true;
							}
						}
						token = tokenizer.getToken();
					}
					isHeadAvailable = getShootHead(in, token, tokenizer);
					newLine = isStartNewLine(in);
					
					// Wurde ein Kopf (Name, L�nge/Kurztrieb und Muttername) gefunden und die Zeile/Datei ist zu Ende,
					// dann f�hre schon jetzt die Sprossanalyse durch und f�ge den Spross hinzu
					if(isHeadAvailable && (newLine == -1 || newLine == 1)) {
						analyseShoot(shoots);
						linenr++;
					}
				} else if (newLine == 0) {
					getShootPart(token, tokenizer, info);
					newLine = isStartNewLine(in);
					if (newLine == 1 || newLine == -1) {
						analyseShoot(shoots);
						linenr++;
					}
				}
			}
			
			ShootInfo root = null;
			Matrix3f m = new Matrix3f();
			Matrix4d m4d = new Matrix4d();
			m4d.setIdentity();
			Iterator enu = shoots.entrySet().iterator();
			
			// Knoten, welcher in den Graph eingef�gt wird
			// Ist selbst ein Knoten ohne Visualisierungskomponente um mehrere root-Objekte aufzunehmen
			Node returnNode = new Null();
			
			while (enu.hasNext()) {
				ShootInfo child = (ShootInfo) ((Map.Entry) enu.next()).getValue();
				ShootInfo parent = (ShootInfo) shoots.get(child.mName);
				if (parent == null) {
					
					// Wegen Unterst�tzung von mehreren root-Objekten auskommentiert
					// System.out.println(child.mName);
//					if (root != null) {
//						throw new SemanticException("Invalid parent").set(tokenizer);
//					}
					root = child;
					m.setIdentity();
				} else {
					m.transpose(parent.xf);
					child.sub(parent.tip);
				}

				m.transform(child);
				m.mul(m, child.xf);
				m4d.setRotation(m);
				m4d.m03 = child.x;
				m4d.m13 = child.y;
				m4d.m23 = child.z;
				child.shoot.setTransform(new TMatrix4d(m4d));
				child.shoot.internodeCount = child.izahl;
				
				// Realisiert eine lineare Verbindung zwischen den Knoten
				Node connector = child.shoot;
				
				if(CLASSIC_VERSION) {
				
					// Wurde ein n-Parameter gesetzt? -> Dann erzeuge einen Nl-Turtlebefehl f�r die Wert�bernahme (lokale Auswirkung)
					if(child.nad != 0.0f) {
						Nl nl = new Nl(child.nad);
						nl.addEdgeBitsTo(connector, Graph.SUCCESSOR_EDGE, null);
						connector = nl;
					}
					// Wurde die Ordnung gesetzt? -> Dann erzeuge einen OR-Turtlebefehl f�r die Wert�bernahme
					if(child.or != -1) {
						OR or = new OR(child.or);
						or.addEdgeBitsTo(connector, Graph.SUCCESSOR_EDGE, null);
						connector = or;
					}
					// Wurde die generative Distanz gesetzt? -> Dann erzeuge einen GD-Turtlebefehl f�r die Wert�bernahme
					if(child.gen != -1) {
						GD gd = new GD(child.gen);
						gd.addEdgeBitsTo(connector, Graph.SUCCESSOR_EDGE, null);
						connector = gd;
					}
					// Wurde die Internodienzahl gesetzt? -> Dann erzeuge einen Ul-Turtlebefehl f�r die Wert�bernahme
					if(child.izahl != 0) {
						Ul ul = new Ul(child.izahl);
						ul.addEdgeBitsTo(connector, Graph.SUCCESSOR_EDGE, null);
						connector = ul;
					}
					// Wurde der q-Wert gesetzt? -> Dann erzeuge einen MRel-Turtlebefehl f�r die Wert�bernahme
					// und setze beim Mutterspross einen L-Turtlebefehl ein (MRel bezieht sich auf die L�nge, welche mit L vergeben wird)
					if(child.q != 0.0f) {
						MRel mrel = new MRel(child.q);
						mrel.addEdgeBitsTo(connector, Graph.SUCCESSOR_EDGE, null);
						connector = mrel;
						if(parent != null) {
							L l = new L(parent.laenge);
							l.addEdgeBitsTo(parent.shoot, Graph.SUCCESSOR_EDGE, null);
						}
					}
				
				} else {
					child.shoot.parameter = child.nad;
					child.shoot.order = child.or;
					child.shoot.generativeDistance = child.gen;
					child.shoot.internodeCount = child.izahl;
					child.shoot.relPosition = child.q;
				}
				if(parent != null) {
					// Ist Blatt?
//					if(child.izahl == -1 && leafObjectAvailable) {
//						Instance instance = new Instance(currentLeafObject);
//						parent.shoot.addEdgeBitsTo(instance, Graph.SUCCESSOR_EDGE, null);
						
					// Ist Frucht?
//					} else if(child.izahl == -2 && fruitObjectAvailable) {
//						Instance instance = new Instance(currentFruitObject);
//						parent.shoot.addEdgeBitsTo(instance, Graph.SUCCESSOR_EDGE, null);
//						
//					} else {
						parent.shoot.addEdgeBitsTo(connector,
								(child.or == parent.or) ? Graph.SUCCESSOR_EDGE
										: Graph.BRANCH_EDGE, null);
//					}
				}
				if(child == root)
					returnNode.addEdgeBitsTo(root.shoot, Graph.SUCCESSOR_EDGE, null);
					//returnNode = connector;
			}
			if (root == null) {
				throw new SemanticException("No root").set(tokenizer);
			}

			return returnNode;
		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		} finally {
			// Herstellen der urspr�nglichen DTD-Datei
			restoreBackUpFile(dtdFile);
		}
	}

	private String getString(Tokenizer tokenizer) throws IOException,
			LexicalException {
		Input in = tokenizer.getInput();
		String getS = "";
		char trennz = ' ';
		try {
			while (trennz == ' ') {
				trennz = (char) in.getChar();
			}
			if (trennz == EOL || trennz == EOF) {
				in.ungetChar();
				return null;
			}
			getS = getS + trennz;
			trennz = (char) in.getChar();
			while ((trennz != ' ') && (trennz != EOF) && (trennz != EOL)
					&& (trennz != '\t') && (trennz != '\n')) {
				getS = getS + trennz;
				trennz = (char) in.getChar();
			} // end while
			in.ungetChar();
			return getS;
		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		}
	}

	private boolean isWhiteSpace(Tokenizer tokenizer) throws IOException,
			LexicalException {
		Input in = tokenizer.getInput();
		try {
			char trennz = (char) in.getChar();
			if (trennz == ' ' || trennz == '\t')
				return true;
			else {
				in.ungetChar();
				return false;
			}

		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		}
	}

	private int isStartNewLine(Input in) throws IOException, LexicalException {
		if (in.isClosed())
			return -1;
		char mark = ' ';
		try {
			while (mark == ' ') {
				mark = (char) in.getChar();
			}
			if (mark == EOL || mark == '\n')
				return 1;
			else if (mark == EOF)
				return -1;
			else
				in.ungetChar();
		} catch (RecognitionException e) {
			throw new IOWrapException(e);

		}
		return 0;
	}

	private void getLeafParameter(Tokenizer tokenizer, Token readingToken)
			throws IOException, LexicalException {
		try {
			boolean nocomma = true;
			if (readingToken.getText().compareTo("\\leafarea") == 0) {
				leafArea = tokenizer.getFloat();
			}// end if
			else if (readingToken.getText().compareTo("\\leaflength") == 0) {
				leafLength = tokenizer.getFloat();
			} else if (readingToken.getText().compareTo("\\leafbreadth") == 0) {
				leafBreadth = tokenizer.getFloat();
			} // end if
			else if (readingToken.getText().compareTo("\\leafobject") == 0) {
				// Parameter laden
				String methodname = null;
				leafObjectAvailable = true;
				filename = getString(tokenizer);
				startaxiom = getString(tokenizer);
				derivationSteps = tokenizer.getInt();
				methodname = getString(tokenizer);
				
				loadRGGFileToProject();
			
				// LeafObject als Wurzel
				LeafObject leaf = new LeafObject(filename, startaxiom, methodname, derivationSteps);
				
				currentLeafObject = supplyToObjectExplorer(methodname, leaf);
				nocomma = false;
			} // end if
			else if (readingToken.getText().compareTo("\\fruitobject") == 0) {
				// Parameter laden
				String methodname = null;
				fruitObjectAvailable = true;
				filename = getString(tokenizer);
				startaxiom = getString(tokenizer);
				derivationSteps = tokenizer.getInt();
				methodname = getString(tokenizer);
				
				loadRGGFileToProject();
				
				// Ausgabe der Registry in der Konsole
				//de.grogra.util.Utils.dumpTree(de.grogra.pf.ui.Workbench.current().getRegistry());
				
				// FruitObject als Wurzel
				FruitObject fruit = new FruitObject(filename, startaxiom, methodname, derivationSteps);
				
				currentFruitObject = supplyToObjectExplorer(methodname, fruit);
				nocomma = false;
			}// end if
			else if (readingToken.getText().compareTo("\\phyllotaxy") == 0) {
				String phyllotaxy = getString(tokenizer);
				if (phyllotaxy.compareTo("alternate,") == 0) {
					phyllotax = 0;
				}// end if
				else if (phyllotaxy.compareTo("spiral,") == 0) {
					phyllotax = 1;
				}// end if
				else if (phyllotaxy.compareTo("opposite,") == 0) {
					phyllotax = 2;
				}
				nocomma = false;
			} else if (readingToken.getText().compareTo("\\min_intn") == 0) {
				minintn = tokenizer.getInt();
			}// end if
			if (nocomma) {
				String comma = tokenizer.getToken().getText();
				if (comma.compareTo(",") != 0) {
					throw new UnexpectedTokenException(comma, ",")
							.set(tokenizer);
				}
			}
		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		}

	}

	private String supplyToObjectExplorer(String methodname, Node node) {
		String nameInObjectExplorer = null;
		
		try {
			
			// Klasse suchen
			Class classToInstanciated = Workbench.current().getRegistry().classForName(filename.substring(0, filename.length()-4)+"$"+startaxiom);
//			Class classToInstanciated = Workbench.current().getRegistry().classForName(filename.substring(0, filename.length()-4));
//			Class[] ac = classToInstanciated.getDeclaredClasses();
//			System.out.println(ac.length);
//			for(Class c : ac) {
//				System.out.println(c.toString());
//			}
//			
//			System.out.println(filename);
//			Class classToInstanciated = Class.forName(filename.substring(0, filename.length()-4));
			
			// Instanz von der Klasse erzeugen
			Object newInstance = classToInstanciated.newInstance();
			
			// LeafObject mit RGG-Datei verbinden
			((Node)newInstance).addEdgeBitsTo(node, Graph.SUCCESSOR_EDGE, null);
			
			// Zweiten Graph erzeugen
			GraphManager virtualGraph = new GraphManager(new ServerConnection(null), null, false, false);
			
			// Wurzel mit dem LeafObject setzen
			virtualGraph.setRoot(GraphManager.MAIN_GRAPH, node);
			
			// Sichern des aktuellen Graphs
			RGGGraph originalGraph = de.grogra.rgg.model.Runtime.INSTANCE.currentGraph();
			
			// Den virtuellen Graph setzen
			de.grogra.rgg.model.Runtime.INSTANCE.setCurrentGraph(virtualGraph);
			
			// i-mal Ableiten
			for(int i = 0; i < derivationSteps; i++) {
				Method method = newInstance.getClass().getMethod(methodname);
				method.invoke(newInstance);
				Library.derive();
			}
			
			// Original Graphen wieder einsetzen
			de.grogra.rgg.model.Runtime.INSTANCE.setCurrentGraph(originalGraph);
			
			// LeafObject in den ObjectExplorer einh�ngen
			nameInObjectExplorer = filename+"_"+startaxiom+"_"+derivationSteps+"_"+methodname;
			ObjectItem oItem = ObjectItem.createReference(Workbench.current(), node, nameInObjectExplorer);
			Workbench.current().getRegistry().getDirectory("/project/objects/objects", null).addUserItem(oItem);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return nameInObjectExplorer;
	}

	private void loadRGGFileToProject() {
		boolean rggFileIsInProject = false;
		
		// Lade die RGG-Datei in das Projekt rein
		// Suche, ob die RGG-Datei schon im Projekt ist
		Item dir = Workbench.current().getRegistry().getDirectory ("/project/objects/files", null);
		for(Node n = dir.getBranch(); n != null; n = n.getSuccessor()) {
			if(n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
				if(sf.getName().equals(filename)) {
					rggFileIsInProject = true;
				}
			}
		}
		// Ist die RGG-Datei nicht im Projekt, dann f�ge sie ein
		if(!rggFileIsInProject) {
			File file = new File(filename);
			FileTypeItem i = FileTypeItem.get(Workbench.current(), file.getName ());
			if (i == null) {
				throw new UnsupportedOperationException ("unsupported format of " + file);
			}
			IMP.addSourceFile(file, i.getMimeType(), Workbench.current()); 
		}
	}

	private void initDTDParameter() {
		// Default Values for Leaves:
		leafArea = 0;
		leafLength = 5;
		leafBreadth = 1;
		leafObjectAvailable = fruitObjectAvailable = false;
		phyllotax = 0; // default: alternate phyllotaxy
		minintn = 0; // = off
		derivationSteps = 0;
	}

	private void initDTDShootParameter() {
		diameter = DEFAULT_THICKNESS;
		direction = DEFAULT_DIRECTION;
		angle = 0.f;
		azimuthAngle = -360.f;
		nParameter = 0.f;
		insertPosition = internodiumCount = 0;
		leafCount = 0;
		fruitCount = 0;
		shortShootCount = 0;
		treeColor = 10; // Hellgr�n
		isDollar = false;
		globkug = 0;
		distanceToMotherBasis = -1;
		order = -1;
		genDistance = -1;
		noOfYear = -1;
		isExtension = false;
		isBud = false;
	}

	private boolean getShootHead(Input in, Token tokenf, Tokenizer tokenizer)
			throws IOException, LexicalException {
		try {
			if (in.isClosed())
				return false;
			boolean lstart = isWhiteSpace(tokenizer);
			if (!lstart) {
				nam = getString(tokenizer);
				if (tokenf.getText() != null)
					nam = tokenf.getText() + nam;
			} else
				nam = tokenf.getText();
			if(nam.charAt(0) == '#')
				nam = nam.substring(1);

			Token token = tokenizer.getToken();
			if (token.getType() == DTDTokenizer.DTDINDENT) {
				if (token.getText().compareTo("Q") == 0
						|| token.getText().compareTo("q") == 0) {
					// Kurztriebkette liegt vor
					shortShootCount = tokenizer.getInt();
					shootLength = DEFAULT_SHORT_SHOOT_LENGTH;
				} else if (token.getText().compareTo("L") == 0
						|| token.getText().compareTo("l") == 0) {
					boolean smark = isWhiteSpace(tokenizer);
					if (!smark)
						shootLength = tokenizer.getFloat();
					else
						shootLength = 0.f;
				} else {
					throw new UnexpectedTokenException(token.getText(), "Q,L").set(tokenizer);
				}
			}
			token = tokenizer.getToken(); // get # mother token
			if (token.getText().compareTo("#") == 0) {
				mnam = getString(tokenizer);
			} else {
				throw new UnexpectedTokenException(token.getText(), "#").set(tokenizer);
			}

		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		}
		
		return true;
	}

	private void getShootPart(Token token, Tokenizer tokenizer, ShootInfo info)
			throws IOException, LexicalException {
		if (info == null || tokenizer == null) {
			return;
		}
		try {
			if (token.getText().compareTo("A") == 0
					|| token.getText().compareTo("a") == 0) {
				boolean smark = isWhiteSpace(tokenizer);
				if (!smark)
					distanceToMotherBasis = tokenizer.getFloat(); // Abstand wird gelesen
			} else if (token.getText().compareTo("W") == 0
					|| token.getText().compareTo("w") == 0) {
				angle = tokenizer.getFloat(); // Winkel wird gelesen
			} else if (token.getText().compareTo("O") == 0
					|| token.getText().compareTo("o") == 0) {
				order = tokenizer.getInt(); // order wird gelesen
			} else if (token.getText().compareTo("G") == 0
					|| token.getText().compareTo("g") == 0) {
				genDistance = tokenizer.getInt(); // generative distance of the shoot
			} else if (token.getText().compareTo("J") == 0
					|| token.getText().compareTo("j") == 0) {
				noOfYear = tokenizer.getInt(); // Angabe des Alters der WE
			} else if (token.getText().compareTo("R") == 0
					|| token.getText().compareTo("r") == 0) {
				direction = tokenizer.getInt(); // Richtungangabe
			} else if (token.getText().compareTo("S") == 0
					|| token.getText().compareTo("s") == 0) {
				azimuthAngle = tokenizer.getFloat(); // Richtungangabe
			} else if (token.getText().compareTo("E") == 0
					|| token.getText().compareTo("e") == 0) {
				internodiumCount = tokenizer.getInt(); // Anzahl der Internodien der WE
			} else if (token.getText().compareTo("I") == 0
					|| token.getText().compareTo("i") == 0) {
				insertPosition = tokenizer.getInt();// Index des Insertionskonotens an
											// der Mutter-WE
			} else if (token.getText().compareTo("N") == 0
					|| token.getText().compareTo("n") == 0) {
				nParameter = tokenizer.getFloat(); // Nadel-oder Blattparameter
			} else if (token.getText().compareTo("D") == 0
					|| token.getText().compareTo("d") == 0) {
				boolean smark = isWhiteSpace(tokenizer);
				if (!smark)
					diameter = tokenizer.getFloat(); // Durchmesser im mm
			} else if (token.getText().compareTo("C") == 0
					|| token.getText().compareTo("c") == 0) {
				treeColor = tokenizer.getInt();// Farbindex fuer die graphische
											// Darstellung
			} else if (token.getText().compareTo("B") == 0
					|| token.getText().compareTo("b") == 0) {
				leafCount = tokenizer.getInt();// Blattzahl
			} else if (token.getText().compareTo("F") == 0
					|| token.getText().compareTo("f") == 0) {
				fruitCount = tokenizer.getInt();// Blattzahl
			} else if (token.getText().compareTo("T") == 0
					|| token.getText().compareTo("t") == 0) {
				tokenizer.getFloat();// dummy
				noOfYear = 0;
			} else if (token.getText().compareTo("M") == 0
					|| token.getText().compareTo("m") == 0) {
				tokenizer.getFloat();// dummy
			} else if (token.getText().compareTo("P") == 0
					|| token.getText().compareTo("p") == 0) {
				tokenizer.getFloat();// dummy
			} else if (token.getText().compareTo("X") == 0
					|| token.getText().compareTo("x") == 0) {
				tokenizer.getFloat();// dummy
			} else if (token.getText().compareTo("V") == 0
					|| token.getText().compareTo("v") == 0) {
				isExtension = true; // Verlaengerungs-WE der Mutter-WE
			} else if (token.getText().compareTo("K") == 0
					|| token.getText().compareTo("k") == 0) {
				isBud = true; // Markierung als Knospe					// wird nicht ausgewertet !!!!!!!!!!!!!!!!!!!!!
			} else if (token.getText().compareTo("-") == 0) {
				direction = 7; // Richtung nach rechts
			} else if (token.getText().compareTo("+") == 0) {
				direction = 3; // Richtung nach rechts
			} else if (token.getText().compareTo("$") == 0) {
				isDollar = true; // Richtung nach rechts
			} else if (token.getText().compareTo(".") == 0) {
				globkug = 1; // Richtung nach rechts
			} else if (token.getText().compareTo("(") == 0) {
				String dtrennz = tokenizer.getToken().getText();
				if ((dtrennz.compareTo("D") == 0)
						|| (dtrennz.compareTo("d") == 0)) {
					diameter = tokenizer.getFloat();
				}
				boolean endD = false;
				while (!endD) {
					Token endca = tokenizer.getToken();
					if (endca.getText().compareTo(")") == 0)
						endD = true;
				}
			// Kommentare in DTD-Datei auslassen
			} else if (token.getText().compareTo("{") == 0) {
				boolean endD = false;
				while (!endD) {
					Token endca = tokenizer.getToken();
					if (endca.getText().compareTo("}") == 0)
						endD = true;
				}
			// Kommentare in DTD-Datei auslassen
			} else if (token.getText().compareTo("<") == 0) {
				boolean endD = false;
				while (!endD) {
					Token endca = tokenizer.getToken();
					if (endca.getText().compareTo(">") == 0)
						endD = true;
				}
			}
		} catch (RecognitionException e) {
			throw new IOWrapException(e);
		}
	}

	private void analyseShoot(HashMap<String, ShootInfo> shoots) {
		boolean vertif;
		float motherLength;
		ShootInfo ms = searchMotherShoot(shoots, mnam);
		if (distanceToMotherBasis >= 0) { // es wurde eine A-Angabe eingelesen
			if (ms != null) {
				motherLength = ms.laenge;
			} else
				motherLength = 0;
			if (motherLength > 0)
				qParameter = 1.f - (distanceToMotherBasis / motherLength);
			else
				qParameter = 0.f;
			if (qParameter < 0.f)
				qParameter = 0.f;
			if (qParameter > 1.f)
				qParameter = 1.f;
		} else {
			int internodiumCountOfMother = 0;
			if(ms != null)
				internodiumCountOfMother = ms.izahl;
			if ((insertPosition > 0) && (internodiumCountOfMother > 0)) // aequidist. Unterteilung
				qParameter = ((float) insertPosition) / (float) internodiumCountOfMother;
			else
				qParameter = 0.f;
		}

		if (order == -1) { // O-Angabe wurde nicht gemacht
			if (ms != null) {
				if (isExtension)
					order = ms.or;
				else {
					order = (ms.or) + 1;
				}
			} else
				order = 0;
		}

		if (azimuthAngle == -360) // R-Angabe ist zu verwenden
			azimuthAngle = (direction - 1) * 45;

		if (genDistance == (-1)) { // G-Angabe wurde nicht gemacht
			if (noOfYear == (-1)) { // J-Angabe wurde nicht gemacht
				if (ms != null)
					genDistance = (ms.gen) + 1;
				else
					genDistance = 0;
			} else {
				/* vorher: genn = 11 - jnr; SPEZIFISCH FUER DIE SOFI! */
				genDistance = -1 - noOfYear; /*
									 * in einem zweiten Durchlauf zu
									 * korrigieren; dieser Fall wird nur im
									 * Modus spycher == 1 korrekt behandelt!
									 */
				if (ms != null) { /*
									 * Beeinflussung der Gen.-nr. der Vorgaenger
									 * gleicher Ordnung
									 */
					jk = 1;
				}
			}
		}// end if (gen)

		// determination of number of (pseudo-) internodes:
		if (internodiumCount <= 0) {
			// use artificial internode number
			if (((leafCount > 0) || (fruitCount > 0)) && (minintn > 0)) {
				if (leafCount + 1 > minintn)
					internodiumCount = leafCount + 1;
				else {
					if (fruitCount + 1 > minintn)
						internodiumCount = fruitCount + 1;
					else
						internodiumCount = minintn;
				}
			} else {
				if ((leafCount > 0) || (fruitCount > 0))
					internodiumCount = 1;
			}
		}

		
		if (order <= 1)
			vertif = true;
		else
			vertif = false; /* Annahme: Plagiotropie */
		if (isDollar || (globkug != 0))
			vertif = true;
		
		processingShoot(vertif, ms, shoots);
	}

	private void processingShoot(boolean vertif, ShootInfo motherShoot, HashMap<String, ShootInfo> shoots) {
		Vector3f spanf = new Vector3f();
		Vector3f spend = new Vector3f();
		Vector3f ssh = new Vector3f();
		Vector3f ssl = new Vector3f();
		Vector3f ssu = new Vector3f();
		Vector3f hv = new Vector3f();
		Vector3f vertik = new Vector3f();
		Vector3f temp1, temp2, manf, mend, mh, ml, mu;
		float f1 = 0.f, f2 = 0.f, f3 = 0.f;
		double bphi, btheta;
		vertik.x = vertik.y = 0.f;
		vertik.z = 1.f;
		bphi = Math.PI * (double) (azimuthAngle / 180);
		btheta = Math.PI * (double) (angle / 180);
		if (globkug == 0) {
			f1 = (float) Math.cos(bphi) * (float) Math.sin(btheta);
			f2 = (float) Math.sin(bphi) * (float) Math.sin(btheta);
			f3 = (float) Math.cos(btheta);
		} else {
			f1 = (float) Math.cos(btheta) * (float) Math.cos(bphi);
			f2 = (float) Math.cos(btheta) * (float) Math.sin(bphi);
			f3 = (float) Math.sin(btheta);
		}
		if (motherShoot != null) {
			manf = motherShoot.panf;
			mend = motherShoot.pend;
			mh = motherShoot.sh;
			ml = motherShoot.sl;
			mu = motherShoot.su;
		} else {
			manf = new Vector3f();
			mend = new Vector3f();
			mh = new Vector3f();
			mu = new Vector3f();
			ml = new Vector3f();
			mh.z = 1.f;
			mu.y = -1.f; // beachte die Konsistenz
			ml.x = -1.f; // mit nullsprinit
		}
		spanf = (Vector3f) manf.clone();
		temp1 = (Vector3f) mend.clone();
		spanf.scale(qParameter);
		temp1.scale(1.0f - qParameter);
		spanf.add(temp1);
		if (globkug == 0) {
			hv = (Vector3f) mu.clone();
			temp1 = (Vector3f) ml.clone();
			temp2 = (Vector3f) mh.clone();
			hv.scale(f1);
			temp1.scale(f2);
			hv.sub(temp1);
			temp2.scale(f3);
			hv.add(temp2);
		} else {
			hv.x = f1;
			hv.y = f2;
			hv.z = f3;
		}
		hv.scale(shootLength);
		spend.add(spanf, hv);
		if (hv.length() < Math2.EPSILON)
			ssh = mh;
		else {
			ssh = (Vector3f) hv.clone();
			Math2.normalize(ssh);
		}
		if (vertif) {
			hv.cross(vertik, ssh);
			if (hv.length() < Math2.EPSILON) {
				ssl.x = -1.f; // Konsistenz mit nullsprinit
				ssl.y = ssl.z = 0.f; // beachte: keine Vorzeichenkontr.
			} // modif. 26.03.2000
			else {
				ssl = (Vector3f) hv.clone();
				Math2.normalize(ssl);
			}
			ssu.cross(ssh, ssl);
		} else {
			hv.cross(ml, ssh);
			if (hv.length() < Math2.EPSILON)
				ssu = mu;
			else {
				ssu = (Vector3f) hv.clone();
				Math2.normalize(ssu);
			}
			if (ssu.dot(mu) < 0)
				ssu.scale(-1.0f);
			ssl.cross(ssu, ssh);
		}
		ShootInfo shootinfo = createShoot(spanf, spend, ssh, ssl, ssu);
		
		// Erzeuge Kurztriebkette -> Verwende den gerade erzeugten Spross als erstes Glied in der Kette
		if(shootinfo.akurztr > 0) {
			String basisName = shootinfo.name;		// Zwischenspeichern des Basisnamens f�r die Gliednamen 
			shootinfo.name += ".1";					// Erstes Glied mit Name.1 bezeichnen ... Glied n = Name.n
			shoots.put((nam+".1"), shootinfo);		// Ablegen in die Map
			ShootInfo clone = shootinfo;			// Initialisierung
			ShootInfo mother = shootinfo;			// Initialisierung
			Vector3f beginShoot = (Vector3f) shootinfo.pend.clone();	// Anfangspunkt f�r das n�chste Glied, ist der Endpunkt des vorherigen Glieds
			
			// Erzeuge so viele Glieder wie n hoch ist
			for(int i = 2; i <= shootinfo.akurztr; i++) {
				Vector3f endShoot = new Vector3f();		// Neuer Endpunkt
				clone = (ShootInfo) shootinfo.clone();	// Kurztrieb erzeugen durch klonen des vorherigen
				clone.name = basisName+"."+i;			// Name vergeben
				clone.mName = mother.name;				// Name des Mutterspross setzen
				clone.panf = beginShoot;				// Neuen Anfangspunkt aus alten Endpunkt setzen
				clone.set(beginShoot);
				clone.scale(FACTOR);
				endShoot.scale(clone.shoot.length, beginShoot);	// Neuen Endpunkt berechnet durch Anfangspunkt * Vektorl�nge
				clone.pend = endShoot;					// Neuen Endpunkt setzen
				clone.tip.set(endShoot);
				clone.tip.scale(FACTOR);
				shoots.put((nam+"."+i), clone);			// Ablegen in die Map
				mother = clone;							// Neue Mutter setzen
				beginShoot = (Vector3f) endShoot.clone();	// Neuen Anfangspunkt bestimmen
			}
		} else
			shoots.put(nam, shootinfo);				// Ablegen in die Map
		
		// Liegen Bl�tter vor (und daf�r entsprechende Einf�gepositionen)?
		if(internodiumCount > 0 && leafCount > 0) {
			analyseLeaf(vertif, shoots, shootinfo);
		}
		
		// Liegen Fr�chte vor (und daf�r entsprechende Einf�gepositionen)?
		if(internodiumCount > 0 && fruitCount > 0) {
			analyseFruit(vertif, shoots, shootinfo);
		}
	}

	// Verarbeitung und Positionierung von Fr�chten
	private void analyseFruit(boolean vertif, HashMap<String, ShootInfo> shoots, ShootInfo shootinfo) {
		float bwink = 50.0f;	// Tempor�re Variable, um die Fr�chte rossettenartig am Sprossanfang zu Positionieren
								// (wenn keine Einf�geposition mehr vorhanden ist)
		azimuthAngle = 90.0f;	// Initialisierung der Positionierung
		
		// Initialisierung der Parameter f�r ein Frucht
		diameter = DEFAULT_THICKNESS;
		nParameter = 0.0f;
		shortShootCount = 0;
		treeColor = 4;
		shootLength = 10.0f;
		
		// Durchlaufe so oft, wie Fr�chte angegeben wurde
		for(int i = 1; i <= fruitCount; i++) {
			
			// Positionierung der Frucht
			azimuthAngle += 180.0f;
			if(azimuthAngle > 360.0f)
				azimuthAngle = 90.0f;
			
			// Existiert noch eine "freie" Einf�geposition, dann berechne daf�r den q-Wert
			// -> Ansonsten bilde an Sprossanfang eine Rossette
			if(i <= internodiumCount) {
				qParameter = ((float) i) / (float) internodiumCount;
			} else {
				qParameter = 1.0f;
				bwink += 5.0f;
				azimuthAngle = bwink;
			}
			
			// Korrekte Namensvergabe
			fruitCounter++;
			mnam = shootinfo.name;
			nam = "fruit"+fruitCounter;
			
			// Verarbeite die Frucht und f�ge es in die Map ein
			processingLeaf(vertif, shootinfo, shoots, i, true);
		}
	}

	// Verarbeitung und Positionierung von Bl�ttern
	private void analyseLeaf(boolean vertif, HashMap<String, ShootInfo> shoots, ShootInfo shootinfo) {
		float bwink = 70.0f;	// Tempor�re Variable, um die Bl�tter rossettenartig am Sprossanfang zu Positionieren
								// (wenn keine Einf�geposition mehr vorhanden ist)
		azimuthAngle = 90.0f;	// Initialisierung der Positionierung
		int oppositeCounter = 0;// Tempor�re Variable f�r die Positionierung bei der Phyllotaxe Option "opposite"
		
		// Initialisierung der Parameter f�r ein Blatt
		// diameter = leafBreadth > 0.0f ? leafBreadth : DEFAULT_THICKNESS;
		diameter = DEFAULT_THICKNESS;
		nParameter = leafArea;
		shortShootCount = 0;
		treeColor = 15;
		// shootLength = leafLength > 0.0f ? leafLength : 10.0f;
		shootLength = 10.0f;
		angle = 45.0f;
		
		// Durchlaufe so oft, wie Bl�tter angegeben wurde
		for(int i = 0; i < leafCount; i++) {
			
			// Positionierung anhand der Phyllotaxe-Option
			switch(phyllotax) {
				case 0:		azimuthAngle += 180.0f;				// alternate
							insertPosition++;
							break;
				case 1:		azimuthAngle += 137.5f;				// spiral
							insertPosition++;
							break;
				case 2:		switch(oppositeCounter) {			// opposite
								case 0: insertPosition++;
										azimuthAngle = 270.0f;
										break;
								case 1: azimuthAngle = 90.0f;
										break;
								case 2: insertPosition++;
										azimuthAngle = 180.0f;
										break;
								case 3: azimuthAngle = 0.0f;
										oppositeCounter = -1;
										break;
							}
							oppositeCounter++;
							break;
				default:	insertPosition = i-1;
			}
			
			// Existiert noch eine "freie" Einf�geposition, dann berechne daf�r den q-Wert
			// -> Ansonsten bilde an Sprossanfang eine Rossette
			if(insertPosition <= internodiumCount) {
				qParameter = ((float) insertPosition) / (float) internodiumCount;
			} else {
				qParameter = 1.0f;
				bwink += 5.0f;
				azimuthAngle = bwink;
			}
			
			// Korrekte Namensvergabe
			leafCounter++;
			mnam = shootinfo.name;
			nam = "leaf"+leafCounter;
			
			// Verarbeite das Blatt und f�ge es in die Map ein
			processingLeaf(vertif, shootinfo, shoots, i, false);
		}
	}

	// Analog wie processingShoot nur ohne Kurztriebkette oder Bl�tter/Fr�chte
	private void processingLeaf(boolean vertif, ShootInfo motherShoot, HashMap<String, ShootInfo> shoots, int leafNr, boolean fruit) {
		Vector3f spanf = new Vector3f();
		Vector3f spend = new Vector3f();
		Vector3f ssh = new Vector3f();
		Vector3f ssl = new Vector3f();
		Vector3f ssu = new Vector3f();
		Vector3f hv = new Vector3f();
		Vector3f vertik = new Vector3f();
		Vector3f temp1, temp2, manf, mend, mh, ml, mu;
		float f1 = 0.f, f2 = 0.f, f3 = 0.f;
		double bphi, btheta;
		vertik.x = vertik.y = 0.f;
		vertik.z = 1.f;
		bphi = Math.PI * (double) (azimuthAngle / 180);
		btheta = Math.PI * (double) (angle / 180);
		if (globkug == 0) {
			f1 = (float) Math.cos(bphi) * (float) Math.sin(btheta);
			f2 = (float) Math.sin(bphi) * (float) Math.sin(btheta);
			f3 = (float) Math.cos(btheta);
		} else {
			f1 = (float) Math.cos(btheta) * (float) Math.cos(bphi);
			f2 = (float) Math.cos(btheta) * (float) Math.sin(bphi);
			f3 = (float) Math.sin(btheta);
		}
		if (motherShoot != null) {
			manf = motherShoot.panf;
			mend = motherShoot.pend;
			mh = motherShoot.sh;
			ml = motherShoot.sl;
			mu = motherShoot.su;
		} else {
			manf = new Vector3f();
			mend = new Vector3f();
			mh = new Vector3f();
			mu = new Vector3f();
			ml = new Vector3f();
			mh.z = 1.f;
			mu.y = -1.f; // beachte die Konsistenz
			ml.x = -1.f; // mit nullsprinit
		}
		spanf = (Vector3f) manf.clone();
		temp1 = (Vector3f) mend.clone();
		spanf.scale(qParameter);
		temp1.scale(1.0f - qParameter);
		spanf.add(temp1);
		if (globkug == 0) {
			hv = (Vector3f) mu.clone();
			temp1 = (Vector3f) ml.clone();
			temp2 = (Vector3f) mh.clone();
			hv.scale(f1);
			temp1.scale(f2);
			hv.sub(temp1);
			temp2.scale(f3);
			hv.add(temp2);
		} else {
			hv.x = f1;
			hv.y = f2;
			hv.z = f3;
		}
		hv.scale(shootLength);
		spend.add(spanf, hv);
		if (hv.length() < Math2.EPSILON)
			ssh = mh;
		else {
			ssh = (Vector3f) hv.clone();
			Math2.normalize(ssh);
		}
		if (vertif) {
			hv.cross(vertik, ssh);
			if (hv.length() < Math2.EPSILON) {
				ssl.x = -1.f; // Konsistenz mit nullsprinit
				ssl.y = ssl.z = 0.f; // beachte: keine Vorzeichenkontr.
			} // modif. 26.03.2000
			else {
				ssl = (Vector3f) hv.clone();
				Math2.normalize(ssl);
			}
			ssu.cross(ssh, ssl);
		} else {
			hv.cross(ml, ssh);
			if (hv.length() < Math2.EPSILON)
				ssu = mu;
			else {
				ssu = (Vector3f) hv.clone();
				Math2.normalize(ssu);
			}
			if (ssu.dot(mu) < 0)
				ssu.scale(-1.0f);
			ssl.cross(ssu, ssh);
		}
		ShootInfo shootinfo = createShoot(spanf, spend, ssh, ssl, ssu);
		
		// Bl�tter/Fr�chte haben keine Internodien, zur Erkennung eines Blatt wird Anzahl auf -1 gesetzt und f�r Fr�chte auf -2
		if(fruit)
			shootinfo.izahl = -2;
		else
			shootinfo.izahl = -1;
		
		shootinfo.or = motherShoot.or + 1;
		
		// Korrigiere den n-Wert, wenn explizit ein n-Wert angegeben wurde
		if(!fruit && nParameter > 0.0f)
			shootinfo.nad = nParameter / (float) leafNr;
		shoots.put(nam, shootinfo);
	}
	
	private ShootInfo createShoot(Vector3f spanf, Vector3f spend, Vector3f ssh, Vector3f ssl, Vector3f ssu) {
		ShootInfo shootinfo = new ShootInfo();
		shootinfo.akurztr = shortShootCount;
		shootinfo.name = nam;
		shootinfo.mName = mnam;
		shootinfo.laenge = Math2.abstpp(new Vector3d(spanf), new Vector3d(spend));
		shootinfo.edur = diameter;
		shootinfo.nad = nParameter;
		shootinfo.izahl = internodiumCount;
		shootinfo.farbe = treeColor;
		shootinfo.or = order;
		shootinfo.gen = genDistance;
		shootinfo.q = qParameter;
		shootinfo.panf = spanf;
		shootinfo.pend = spend;
		shootinfo.sh = ssh;
		shootinfo.sl = ssl;
		shootinfo.su = ssu;
		DTGShoot shoot = new DTGShoot();
		shootinfo.shoot = shoot;
		shoot.diameter = diameter * FACTOR;
		shoot.color = treeColor;
		shootinfo.set(spanf);      
		shootinfo.scale(FACTOR);
		shootinfo.tip.set(spend);
		shootinfo.tip.scale(FACTOR);
		shoot.length = shootinfo.laenge * FACTOR;
		shootinfo.xf.setColumn(2, ssh);
		shootinfo.xf.setColumn(0, ssl);
		shootinfo.xf.setColumn(1, ssu);
		return shootinfo;
	}
	
	// Kopiert den Inhalt einer DTD-Datei in eine .tmp Datei
	private void createBackUpFile(String dtdFile) {
		// Erzeugung der Backup Datei
		File f = new File(dtdFile + ".tmp");
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			f.createNewFile();
			
			// Original Datei zum Lesen und Backup-Datei zum Schreiben �ffnen
			in = new BufferedReader(new FileReader(dtdFile));
			out = new BufferedWriter(new FileWriter(f));
			
			// Inhalt kopieren
			String s = null;
			while((s = in.readLine()) != null) {
				out.write(s);
				out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Streams schlie�en (sofern ge�ffnet)
				if(in != null)
					in.close();
				if(out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// L�scht die dtd-Datei und �ndert den Namen der Backup-Datei in den urspr�nglichen Namen um
	private void restoreBackUpFile(String dtdFile) {
		File manipulatedFile = new File(dtdFile);
		File backupFile = new File(dtdFile + ".tmp");
		
		// Wenn die manipulierte Datei existiert, dann L�sche diese
		// und �ndern den Namen der Backup-Datei um
		if(manipulatedFile.exists() && backupFile.exists()) {
			manipulatedFile.delete();
			backupFile.renameTo(manipulatedFile);
		// Wenn nur die Backup-Datei existert, dann nur den Namen �ndern
		} else if(!manipulatedFile.exists() && backupFile.exists()) {
			backupFile.renameTo(manipulatedFile);
		}
	}
	
	// Setzt in jede Zeile die mit einem Bezeichner f�r ein Ast-St�ck anf�ngt
	// ein # vor den Bezeichner. Damit erkennt der Tokenizer das Objekt immer als String und nicht als Literal
	private void preparationOfTheDTDFile(String dtdFile) {
		// Zugriff auf Datei
		File f = new File(dtdFile);
		File g = new File(dtdFile + ".prep");
		BufferedReader in = null;
		BufferedWriter out = null;
		final String sharp = "#";
		
		try {
			// Datei zum Lesen und zum Schreiben �ffnen
			in = new BufferedReader(new FileReader(f));
			out = new BufferedWriter(new FileWriter(g));
			
			// Inhalt manipulieren
			String s = null;
			char t;
			while((s = in.readLine()) != null) {
				s = s.trim();
				if(s.length() != 0) {
					t = s.charAt(0);
					// Ist es kein Kommentar oder generelles Kommando
					if(t != '<' && t != '\\' && t != '{')
						out.write(sharp+s);
					else
						out.write(s);
				}
				out.newLine();
			}
			in.close();
			out.close();
			
			// Inhalte kopieren in die DTD Datei
			in = new BufferedReader(new FileReader(g));
			out = new BufferedWriter(new FileWriter(f, false));
			
			s = null;
			while((s = in.readLine()) != null) {
				out.write(s);
				out.newLine();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Streams schlie�en (sofern ge�ffnet)
				if(in != null)
					in.close();
				if(out != null)
					out.close();
				if(g.exists())
					g.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	private ShootInfo searchMotherShoot(HashMap<String, ShootInfo> shoots, String motherName) {
		return shoots.get(motherName);
	}// end DTDShoot

}
