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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.registry.MethodDescriptionContent;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;

public class FunctionBrowserManager {

	// Create the StyleContext, the document and the pane
	private static final StyleContext sc = new StyleContext ();
	private final DefaultStyledDocument doc1 = new DefaultStyledDocument (sc);
	private final JTextPane pane1 = new JTextPane (doc1);
	private final JScrollPane scrollPane1 = new JScrollPane(pane1);

	// Create and add the main document style
	private final static Style defaultStyle = sc.getStyle (StyleContext.DEFAULT_STYLE);
	private final static Style textStyle = sc.addStyle ("main", defaultStyle);
	private final static Style functionStyle = sc.addStyle ("edges", null);
	private final static Style keywordStyle = sc.addStyle ("brackets", null);

	
	private final static int FONT_SIZE = 14;
	
	static {
		StyleConstants.setFontFamily (textStyle, "serif");
		StyleConstants.setFontSize (textStyle, FONT_SIZE);
		StyleConstants.setForeground (textStyle, new Color(75, 75, 75));

		StyleConstants.setFontFamily (functionStyle, "monospaced");
		StyleConstants.setFontSize (functionStyle, FONT_SIZE);
		StyleConstants.setForeground (functionStyle, Color.black);
		StyleConstants.setItalic (functionStyle, true);

		StyleConstants.setFontFamily (keywordStyle, "monospaced");
		StyleConstants.setFontSize (keywordStyle, FONT_SIZE+1);
		StyleConstants.setForeground (keywordStyle, new Color(50, 50, 225));
	}

	private static ArrayList<String> methodNameList = null;
	private static ArrayList<MethodDescriptionContent> methodList = null;
	private final ArrayList<String> searchResults = new ArrayList<String>();
	
	private void createFunctionInspector(Panel panel, final Context ctx, GraphManager graph, Map params) {

		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) ctx.getWindow(), params);
		final I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();


		final JPanel searchResultPanel = new JPanel();
		searchResultPanel.setLayout(new BorderLayout());
		searchResultPanel.setBorder(new TitledBorder(" "+thisI18NBundle.getString("functionBrowser.result.panel.Name")+" "));
		
		final JTextField searchQuery = new JTextField("", 25);
		updateCommandList(searchQuery.getText(), searchResultPanel);
		KeyListener kl = new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				updateCommandList(searchQuery.getText(), searchResultPanel);

				//redraws the result panel
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						searchResultPanel.updateUI();
					}
				});
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		};
		searchQuery.addKeyListener(kl);

		// search text  Panel
		JPanel searchTextPanel = new JPanel();
		searchTextPanel.setLayout(new BorderLayout());

		JPanel p0 = new JPanel ();
		p0.setLayout (new BorderLayout());
		p0.add(new JLabel(thisI18NBundle.getString("functionBrowser.search.Name")+"  :  "), BorderLayout.WEST);
		p0.add(searchQuery, BorderLayout.CENTER);

		searchTextPanel.add(p0, BorderLayout.CENTER);
		searchTextPanel.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.WEST);
		searchTextPanel.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
		searchTextPanel.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.NORTH);
		searchTextPanel.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.SOUTH);
		
		//description panel
		scrollPane1.setPreferredSize(new Dimension(350,0));
		pane1.setEditable (false);
		scrollPane1.setViewportView(pane1);
		JPanel p1 = new JPanel ();
		p1.setLayout (new BorderLayout());
		p1.setBorder (BorderFactory.createTitledBorder (thisI18NBundle.getString ("functionBrowser.description.Name")));
		
		p1.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		p1.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		p1.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
		p1.add (scrollPane1, BorderLayout.CENTER);
		
		// search panel
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.setBorder (BorderFactory.createEmptyBorder());
		searchPanel.add(searchTextPanel, BorderLayout.NORTH);
		searchResultPanel.setPreferredSize(new Dimension(300,0));
		searchPanel.add(searchResultPanel, BorderLayout.WEST);
		searchPanel.add(p1, BorderLayout.CENTER);

		// Layout
		c.setLayout(new BorderLayout());
		c.add(searchPanel, BorderLayout.CENTER);
		c.add (Box.createHorizontalStrut (4), BorderLayout.EAST);
		c.add (Box.createHorizontalStrut (4), BorderLayout.WEST);
		c.add (Box.createVerticalStrut (5), BorderLayout.SOUTH);
	}


	private void updateCommandList(String queriedText, JPanel searchResultPanel) {
		queriedText = queriedText.toLowerCase(); //perform case-insensitive search

		//search for functions with name including the queried text
		searchResults.clear();
		list(queriedText, searchResults);
			
		//update the searchResultPanel with list of search results
		searchResultPanel.removeAll(); //this is necessary for the panel to be redrawn.
		if(searchResults.size()>0) {
			String[] stringResults = new String[searchResults.size()];
			stringResults = searchResults.toArray(stringResults);
			final JList list = new JList(stringResults);
			list.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 3, 1, 1)));
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						try {
							//clean
							doc1.remove (0, doc1.getLength ());
							//add the text to the document
							help(searchResults.get(list.getSelectedIndex()), doc1);
						} catch (BadLocationException e) {}
					}
				});

			searchResultPanel.add(new JScrollPane(list), BorderLayout.CENTER);
			searchResultPanel.add(Box.createRigidArea(new Dimension(4,0)), BorderLayout.WEST);
			searchResultPanel.add(Box.createRigidArea(new Dimension(4,0)), BorderLayout.EAST);
			searchResultPanel.add(Box.createRigidArea(new Dimension(0,4)), BorderLayout.NORTH);
			searchResultPanel.add(Box.createRigidArea(new Dimension(0,4)), BorderLayout.SOUTH);
		}
	}


	/**
	 * Prints a list of all available commands containing the specified sequence.
	 * 
	 * @param prefix of the commands
	 */
	private static void list(String key, ArrayList<String> searchResults) {
		if(key==null) return;
		Iterator<String> iter = methodNameList.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			if(name.toLowerCase().contains(key.toLowerCase())) searchResults.add(name);
		}
	}


	public void append(String s, DefaultStyledDocument doc, AttributeSet aset) {
		int len = pane1.getDocument().getLength();
		try {
			doc.insertString(len,s, aset);
		} catch (BadLocationException e) { e.printStackTrace(); }
	}

	/**
	 * Prints the JavaDoc description of all available commands starting with 
	 * the specified prefix on the XL console window.
	 * 
	 * @param prefix of the commands for those the description should be printed
	 */
	private void help(String name, DefaultStyledDocument doc) {
		if(name==null || name.length ()==0) return;
		Iterator<MethodDescriptionContent> iter = methodList.iterator();
		boolean foundOnce = false;
		while (iter.hasNext()) {
			MethodDescriptionContent method = iter.next();
			if(method.getName ().toLowerCase ().equals(name.toLowerCase ())) {
				if(foundOnce) 
					append("\n-------------------------------------------------------------------------------------\n\n",doc, functionStyle);
				foundOnce = true;

				// print out the method description
				append("Name:"+"\n",doc, keywordStyle);
				append("\t"+method.getType()+" "+method.getName()+"()"+"\n",doc, functionStyle);
				
				append("Description:"+"\n",doc, keywordStyle);
				append("\t"+method.getDescription().toString ().replace ('[', ' ').replace (']', ' ').replace ('\n', ' ').replace ("  ", " ")+"\n",doc, textStyle);

				if(method.getAnnotation().size ()>0) {
					append("Annotations:"+"\n",doc, keywordStyle);
					append("\t"+method.getAnnotation()+"\n",doc, textStyle);
				}
				if(method.getParameter().size ()>0) {
					append("Parameters:"+"\n",doc, keywordStyle);
					append("\t"+method.getParameter()+"\n",doc, textStyle);
				}
				if(method.getAttributeParameter().size ()>0) {
					append("Attributes:"+"\n",doc, keywordStyle);
					append("\t"+method.getAttributeParameter()+"\n",doc, textStyle);
				}
				if(method.getParameter().size ()>0) {
					append("return comment:"+"\n",doc, keywordStyle);
					append("\t"+method.getReturncomment()+"\n",doc, textStyle);
				}
				if(method.getSee().size ()>0) {
					append("See:"+"\n",doc, keywordStyle);
					append("\t"+method.getSee()+"\n",doc, textStyle);
				}
			}
		}
	}


	public static Panel createFunctionBrowser (Context ctx, Map params) {
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		methodList = ctx.getWorkbench().getRegistry ().getMethodList();
		methodNameList = ctx.getWorkbench().getRegistry ().getMethodNameList();
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		FunctionBrowserManager mgr = new FunctionBrowserManager();
		mgr.createFunctionInspector(p, ctx, graph, params);
		return p;
	}

}
