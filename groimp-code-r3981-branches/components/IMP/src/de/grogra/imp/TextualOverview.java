/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.imp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.ui.Context;
import de.grogra.util.I18NBundle;

public class TextualOverview {

	private final Context ctx;
	private final GraphManager graph;
	private I18NBundle thisI18NBundle;

	private final JPanel panel = new JPanel ();

	private JCheckBox idCheckBox;
	private JCheckBox qualifiedCheckBox;
	
	// Create the StyleContext, the document and the pane
	private static final StyleContext sc = new StyleContext ();
	private final DefaultStyledDocument doc1 = new DefaultStyledDocument (sc);
	private final JTextPane pane1 = new JTextPane (doc1);
	private final JScrollPane scrollPane1 = new JScrollPane(pane1);
	private final DefaultStyledDocument doc2 = new DefaultStyledDocument (sc);
	private final JTextPane pane2 = new JTextPane (doc2);
	private final JScrollPane scrollPane2 = new JScrollPane(pane2);
	private final DefaultStyledDocument doc3 = new DefaultStyledDocument (sc);
	private final JTextPane pane3 = new JTextPane (doc3);
	private final JScrollPane scrollPane3 = new JScrollPane(pane3);
	
	// Create and add the main document style
	private final static Style defaultStyle = sc.getStyle (StyleContext.DEFAULT_STYLE);
	private final static Style mainStyle = sc.addStyle ("main", defaultStyle);

	// edges
	private final static Style edgeStyle = sc.addStyle ("edges", null);
	// identifier
	private final static Style identifierStyle = sc.addStyle ("identifier", null);
	// node id's
	private final static Style idStyle = sc.addStyle ("ids", null);
	// brackets and colons
	private final static Style bracketsStyle = sc.addStyle ("brackets", null);
	// strings
	private final static Style stringStyle = sc.addStyle ("strings", null);

	
	private final static int FONT_SIZE = 14;
	
	static {
		StyleConstants.setFontFamily (mainStyle, "serif");
		StyleConstants.setFontSize (mainStyle, FONT_SIZE);
		StyleConstants.setForeground (mainStyle, new Color(50, 50, 225));

		StyleConstants.setFontFamily (edgeStyle, "monospaced");
		StyleConstants.setFontSize (edgeStyle, FONT_SIZE);
		StyleConstants.setForeground (edgeStyle, Color.black);
		StyleConstants.setItalic (edgeStyle, false);

		StyleConstants.setFontFamily (identifierStyle, "serif");
		StyleConstants.setFontSize (identifierStyle, FONT_SIZE);
		StyleConstants.setForeground (identifierStyle, Color.green);
		StyleConstants.setItalic (identifierStyle, true);
		
		StyleConstants.setFontFamily (idStyle, "serif");
		StyleConstants.setFontSize (idStyle, FONT_SIZE);
		StyleConstants.setForeground (idStyle, Color.gray);
		StyleConstants.setItalic (idStyle, true);
		
		StyleConstants.setFontFamily (bracketsStyle, "monospaced");
		StyleConstants.setFontSize (bracketsStyle, FONT_SIZE+1);
		StyleConstants.setForeground (bracketsStyle, Color.RED);

		StyleConstants.setFontFamily (stringStyle, "serif");
		StyleConstants.setFontSize (stringStyle, FONT_SIZE);
		StyleConstants.setForeground (stringStyle, Color.magenta);
		StyleConstants.setItalic (stringStyle, true);
	}

	public TextualOverview (Context ctx, GraphManager graph) {
		this.ctx = ctx;
		this.graph = graph;
		
		scrollPane1.setPreferredSize(new Dimension(10,150));
		pane1.setEditable (false);
		scrollPane1.setViewportView(pane1); 
		scrollPane2.setPreferredSize(new Dimension(10,75));
		pane2.setEditable (false);
		scrollPane2.setViewportView(pane2); 
		scrollPane3.setPreferredSize(new Dimension(10,75));
		pane3.setEditable (false);
		scrollPane3.setViewportView(pane3); 
	}

	public JPanel getPanel () {
		updateUI ();
		return panel;
	}

	public void updateUI () {
		thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();
		
		panel.removeAll ();
		panel.setLayout (new BoxLayout (panel, BoxLayout.Y_AXIS));
		panel.setBorder (BorderFactory.createEmptyBorder (2, 2, 2, 2));

		JPanel p1 = new JPanel ();
		p1.setLayout (new BorderLayout());
		p1.setBorder (BorderFactory.createTitledBorder (thisI18NBundle.getString ("textualOverview.panel1.Name") + " ("+graph.getGraphSize ()+" "+thisI18NBundle.getString ("textualOverview.panel.nodes.Name")+")"));
		final String text1 = checkIfEmpty(graph.toXLString (qualifiedCheckBox.isSelected(), Graph.MAIN_GRAPH, idCheckBox.isSelected()));
		try {
			//clean
			doc1.remove (0, doc1.getLength ());
			// Add the text to the document
			doc1.insertString (0, text1, null);

			// Apply the character attributes
			setCharacterAttributes(doc1, text1);
		} catch (BadLocationException e) {}
		p1.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		p1.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		p1.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
		p1.add (scrollPane1, BorderLayout.CENTER);
		
		JPanel p2 = new JPanel ();
		p2.setLayout (new BorderLayout());
		p2.setBorder (BorderFactory.createTitledBorder (thisI18NBundle.getString ("textualOverview.panel2.Name") + " ("+graph.getComponentGraphSize ()+" "+thisI18NBundle.getString ("textualOverview.panel.nodes.Name")+")"));
		final String text2 = checkIfEmpty(graph.toXLString (qualifiedCheckBox.isSelected(), Graph.COMPONENT_GRAPH, idCheckBox.isSelected()));
		try {
			//clean
			doc2.remove (0, doc2.getLength ());
			// Add the text to the document
			doc2.insertString (0, text2, null);

			// Apply the character attributes
			setCharacterAttributes(doc2, text2);
		} catch (BadLocationException e) {}
		p2.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		p2.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		p2.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
		p2.add (scrollPane2, BorderLayout.CENTER);
		
		JPanel p3 = new JPanel ();
		p3.setLayout (new BorderLayout());
		p3.setBorder (BorderFactory.createTitledBorder (thisI18NBundle.getString ("textualOverview.panel3.Name") + " ("+graph.getMetaGraphSize ()+" "+thisI18NBundle.getString ("textualOverview.panel.nodes.Name")+")"));
		final String text3 = checkIfEmpty(graph.toXLString (qualifiedCheckBox.isSelected(), GraphManager.META_GRAPH, idCheckBox.isSelected()));
		try {
			//clean
			doc3.remove (0, doc3.getLength ());
			// Add the text to the document
			doc3.insertString (0, text3, null);

			// Apply the character attributes
			setCharacterAttributes(doc3, text3);
		} catch (BadLocationException e) {}
		p3.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		p3.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		p3.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
		p3.add (scrollPane3, BorderLayout.CENTER);
		
		panel.add (p1);
		panel.add (Box.createVerticalStrut (6));
		panel.add (p2);
		panel.add (Box.createVerticalStrut (6));
		panel.add (p3);

		panel.validate();
		panel.repaint();
	}

	private String checkIfEmpty (String text) {
		//check if empty
		if(text==null || text.length ()==0) {
			return thisI18NBundle.getString ("textualOverview.emptyText.Name");
		}
		return text;
	}

	private void setCharacterAttributes (DefaultStyledDocument doc, String text) {
		boolean string = false;
		boolean id = false;
		//for all characters in text 
		for(int i = 0; i<text.length (); i++) {
			//check if it is a bracket
			if(text.charAt (i)=='[' || text.charAt (i)==']' || text.charAt (i)==':') {
				doc.setCharacterAttributes (i, 1, bracketsStyle, true);
			} else {
				// check if it is an edge
				if(text.charAt (i)=='>' || text.charAt (i)=='-' || text.charAt (i)=='<' ||
					text.charAt (i)=='/' || text.charAt (i)=='$' || text.charAt (i)=='<') {
					doc.setCharacterAttributes (i, 1, edgeStyle, true);
				} else {
					//check for identifier
					if(text.charAt (i)=='n' && Character.isDigit(text.charAt(i+1))) {
						doc.setCharacterAttributes (i, 1, identifierStyle, true);
						i++;
						doc.setCharacterAttributes (i, 1, identifierStyle, true);
						if(Character.isDigit(text.charAt(i+1))) {
							i++;
							doc.setCharacterAttributes (i, 1, identifierStyle, true);
							if(Character.isDigit(text.charAt(i+1))) {
								i++;
								doc.setCharacterAttributes (i, 1, identifierStyle, true);
							}
						}
					} else {
						//check for strings
						if(text.charAt (i)=='"' || string) {
							doc.setCharacterAttributes (i, 1, stringStyle, true);
							if(string && text.charAt (i)=='"') {
								string = false;
								continue;
							}
							string = true;
						} else {
							if(text.charAt (i)==')') id = false;
							if(id) {
								doc.setCharacterAttributes (i, 1, idStyle, true);
							} else {
								doc.setCharacterAttributes (i, 1, mainStyle, true);
								if(text.charAt (i)=='(') id = true;
							}
						}
					}
				}
			}
		}
	}
	
	public void setQualifiedCheckBox (JCheckBox qualifiedCheckBox) {
		this.qualifiedCheckBox = qualifiedCheckBox;
	}

	public void setIdCheckBox (JCheckBox idCheckBox) {
		this.idCheckBox = idCheckBox;
	}

}
