/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.grogra.components.ComponentInspector.TreeNode;
import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.GraphSelection;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.EventListener;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;

public class ComponentInspectorManager {

	final static HashMap<GraphManager, JTree> hierarchicalComponentInspectors = new HashMap<GraphManager, JTree>();
	
	boolean needUpdate = false;
	
	private ComponentInspectorTreeDragSource dragSource;
	
	ArrayList<String> searchResults;
		
	private void createComponentInspector(Panel panel, final Context ctx, GraphManager graph,
			Map params, HashMap<GraphManager, JTree> objectInspectors, final ComponentInspector oi) {
		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) ctx.getWindow(), params);
				
		final JTree objectTree;
		
		if (objectInspectors.containsKey(graph)) {
			objectTree = objectInspectors.get(graph);
		}
		else {
			objectTree = new JTree(oi);
			objectInspectors.put(graph, objectTree);
			objectTree.setCellRenderer(new ComponentInspectorTreeIconRenderer());
			
			 MouseListener ml = new MouseAdapter() {
				 @Override
			     public void mousePressed(MouseEvent event) {
			         int selRow = objectTree.getRowForLocation(event.getX(), event.getY());
			    	 
			         if(event.getButton ()==MouseEvent.BUTTON2 && selRow==0) {
			    		 toTEXFile();
			    	 }
			         
			         TreePath selPath = objectTree.getPathForLocation(event.getX(), event.getY());
			         if(selRow != -1) {
			             if(event.getClickCount() == 1 && event.getButton ()==MouseEvent.BUTTON3) {
			                 showMenue(event, selRow, selPath, ctx);
			             }
			         }
			     }
			 };
			 objectTree.addMouseListener (ml);

			 // add the drag source 
			 dragSource = new ComponentInspectorTreeDragSource(objectTree, DnDConstants.ACTION_MOVE);
			 
			
			// refresh view when graph changes
			graph.addChangeBoundaryListener(new ChangeBoundaryListener() {
				@Override
				public void beginChange(GraphState gs) {}
				@Override
				public void endChange(GraphState gs) {
					if (!needUpdate)
						return;
					oi.buildTree();
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							objectTree.updateUI();
						}
					});
					needUpdate = false;
				}
				@Override
				public int getPriority() {
					return 0;
				}
			});
			
			graph.addEdgeChangeListener(new EdgeChangeListener() {
				@Override
				public void edgeChanged(Object source, Object target,
						Object edge, GraphState gs) {
					needUpdate = true;
				}
			});
					
			objectTree.setDragEnabled(true);
			objectTree.setEditable(true);
			objectTree.setExpandsSelectedPaths(true);
			objectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			objectTree.addTreeSelectionListener(oi);
			
			
			
			// change selection in object tree when selection changes in GroIMP
			UIProperty.WORKBENCH_SELECTION.addPropertyListener(ctx, new EventListener() {
				@Override
				public void eventOccured(EventObject event) {
					if (event instanceof UIPropertyEditEvent) {
						UIPropertyEditEvent uipee = (UIPropertyEditEvent) event;
						if (oi.isActiveTreeSelection()) {
							oi.setActiveTreeSelection(false);
							return;
						}
						if (uipee.getNewValue() instanceof GraphSelection) {
							GraphSelection gs = (GraphSelection) uipee.getNewValue();
							int selectCount = gs.size();
							TreePath[] selectionPaths = new TreePath[selectCount];
							
							for (int i = 0; i < selectCount; i++) {
								if (gs.getObject(i) instanceof Node) {
									TreeNode treeNode = oi.getTreeNodeForNode((Node) gs.getObject(i));
									if (treeNode != null) {
										LinkedList<TreeNode> path = new LinkedList<TreeNode>();
										oi.getPathToTreeNode(treeNode, path);
										TreePath selectionPath = new TreePath(path.toArray());
										selectionPaths[i] = selectionPath;
									}
								}
							}
							oi.setActiveGISelection(true);
							objectTree.setSelectionPaths(selectionPaths);
							oi.setActiveGISelection(false);
						}
						else if ((uipee.getOldValue() instanceof GraphSelection)
								&& (uipee.getNewValue() == null)) {
							oi.setActiveGISelection(true);
							objectTree.clearSelection();
							oi.setActiveGISelection(false);
						}
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								objectTree.updateUI();
							}
						});
					}
				}
			});
			
				
		}
		
		I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.components").getI18NBundle();

		// Buttons
		JButton expandAllButton = new JButton(thisI18NBundle.getString("expandAllButton.Name")); 
		expandAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			    expandAll(objectTree);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});
		
		JButton collapseAllButton = new JButton(thisI18NBundle.getString("collapseAllButton.Name"));
		collapseAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expandAll(objectTree);
				collapseAll(objectTree);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});


		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(expandAllButton);
		buttonPanel.add(collapseAllButton);
		
		// Tree
		JScrollPane scrollPane = new JScrollPane(objectTree);
		
		// hierarchical view
		JPanel hirachicalPanel = new JPanel();
		hirachicalPanel.setLayout(new BorderLayout());
		hirachicalPanel.setBorder(new TitledBorder(""));
		hirachicalPanel.add(buttonPanel, BorderLayout.NORTH);	
		hirachicalPanel.add(scrollPane, BorderLayout.CENTER);
		hirachicalPanel.add(Box.createRigidArea(new Dimension(3,0)), BorderLayout.WEST);
		hirachicalPanel.add(Box.createRigidArea(new Dimension(3,0)), BorderLayout.EAST);
		hirachicalPanel.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.SOUTH);

		/*********** search panel *************/
		// search result Panel
		final JPanel searchResultPanel = new JPanel();
		searchResultPanel.setLayout(new BorderLayout());
		searchResultPanel.setBorder(new TitledBorder(" "+thisI18NBundle.getString("searchResultPanel.Name")+" "));
		
		final JTextField searchQuery = new JTextField(thisI18NBundle.getString("searchQuery.Name"), 25);
		KeyListener kl = new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				String queriedText = searchQuery.getText();
				if(queriedText.length()>0)
				{
					queriedText = queriedText.toLowerCase(); //perform case-insensitive search
					
					//search for components with name including the queried text
					searchResults.clear();
					oi.searchWithComponentName((ComponentInspector.TreeNode)oi.getRoot(), queriedText, searchResults);
					
					//update the searchResultPanel with list of search results
					searchResultPanel.removeAll(); //this is necessary for the panel to be redrawn.
					if(searchResults.size()>0)
					{
						String[] stringResults = new String[searchResults.size()];
						stringResults = searchResults.toArray(stringResults);
						final JList list = new JList(stringResults);
						list.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 3, 1, 1)));
						list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						list.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

							@Override
							public void valueChanged(ListSelectionEvent arg0) {
								int selectedIndex = list.getSelectedIndex();
								String selectedComponentName = searchResults.get(selectedIndex);
								Object selectedComponent = oi.getComponentDescriptor((ComponentInspector.TreeNode)oi.getRoot(), selectedComponentName);
								oi.valueChanged(selectedComponent);
							}
							
							
						});
						searchResultPanel.add(list, BorderLayout.CENTER);
						searchResultPanel.add(Box.createRigidArea(new Dimension(4,0)), BorderLayout.WEST);
						searchResultPanel.add(Box.createRigidArea(new Dimension(4,0)), BorderLayout.EAST);
						searchResultPanel.add(Box.createRigidArea(new Dimension(0,4)), BorderLayout.NORTH);
						searchResultPanel.add(Box.createRigidArea(new Dimension(0,4)), BorderLayout.SOUTH);
					}
					
					//redraws the result panel
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							searchResultPanel.updateUI();
						}
					});
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		};
		searchQuery.addKeyListener(kl);

		// search text  Panel
		JPanel searchTextPanel = new JPanel();
		searchTextPanel.setLayout(new BorderLayout()); //31 Dec 2012 yong: if flow layout is used here, will introduce overlapping swing components in children
		searchTextPanel.add(new JLabel(thisI18NBundle.getString("searchTextPanel.Name")+" : "), BorderLayout.NORTH);
		searchTextPanel.add(searchQuery, BorderLayout.CENTER);
		searchTextPanel.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.WEST);
		searchTextPanel.add(Box.createRigidArea(new Dimension(5,0)), BorderLayout.EAST);
		searchTextPanel.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.NORTH);
		searchTextPanel.add(Box.createRigidArea(new Dimension(0,3)), BorderLayout.SOUTH);
		
		// search panel
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(searchTextPanel, BorderLayout.NORTH);
		searchPanel.add(searchResultPanel, BorderLayout.CENTER);
		
		// tabbed pane
		JTabbedPane viewTabbedPane = new JTabbedPane();
		viewTabbedPane.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(1, 3, 1, 1)));
		viewTabbedPane.add(thisI18NBundle.getString("hirachicalTab.Name"), hirachicalPanel);
		viewTabbedPane.add(thisI18NBundle.getString("searchTab.Name"), searchPanel);
		
		// main Layout (window)
		c.setLayout(new BorderLayout());
		c.add(viewTabbedPane, BorderLayout.CENTER);
	}

	private void expandAll(JTree objectTree) {
		int row = 0;
		while (row < objectTree.getRowCount()) {
			objectTree.expandRow(row);
			row++;
		}
	}

	private void collapseAll(JTree objectTree) {
	    int row = objectTree.getRowCount() - 1;
	    while (row >= 0) {
	    	objectTree.collapseRow(row);
	    	row--;
	    }
	}
	
	private void toTEXFile ()
	{
		System.out.println("Generate library documentation to latex file.");
	}

	private void showMenue (MouseEvent event, int selRow, TreePath selPath, final Context ctx)
	{
		System.out.println(" menue "+ selRow+ " + " + selPath);
		I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.components").getI18NBundle();
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItemOpen = new JMenuItem(thisI18NBundle.getString ("menuItemOpen.Name")+" "+selPath.getPath ()[selPath.getPath ().length-1]);
		menuItemOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed (ActionEvent event)
			{
				System.out.println("Selected: " + event.getActionCommand());
			}
			
		});
		menu.add(menuItemOpen);
		menu.addSeparator ();	
		JMenuItem menuItemTest = new JMenuItem(thisI18NBundle.getString ("menuItemTest.Name"));
		menuItemTest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed (ActionEvent event)
			{
				System.out.println("Selected: " + event.getActionCommand());
			}
			
		});
		menu.add(menuItemTest);
		menu.show(event.getComponent(), event.getX(), event.getY());
	}	
	
	public void initSearchResultList()
	{
		this.searchResults = new ArrayList<String>();
	}
	
	public static Panel createHierarchicalComponentInspector (Context ctx, Map params) {
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		ComponentInspectorManager mgr = new ComponentInspectorManager();
		mgr.initSearchResultList();
		mgr.createComponentInspector(p, ctx, graph, params, hierarchicalComponentInspectors, new HierarchicalComponentInspector(ctx, graph));
		return p;
	}
	
}
