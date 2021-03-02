package de.grogra.imp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.ObjectInspector.TreeNode;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.GraphSelection;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.swing.PanelSupport;
import de.grogra.pf.ui.swing.SwingPanel;
import de.grogra.pf.ui.swing.SwingToolkit;
import de.grogra.pf.ui.swing.WindowSupport;
import de.grogra.util.EventListener;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;

public class ObjectInspectorManager {

	final static HashMap<GraphManager, JTree> hierarchicalObjectInspectors = new HashMap<GraphManager, JTree>();
	final static HashMap<GraphManager, JTree> flatObjectInspectors = new HashMap<GraphManager, JTree>();
	
	boolean needUpdate = false;
	
	private void createObjectInspector(Panel panel, final Context ctx, GraphManager graph,
			Map params, HashMap<GraphManager, JTree> objectInspectors, final ObjectInspector oi) {
		Container c = ((SwingPanel) panel.getComponent()).getContentPane();
		((PanelSupport)panel).initialize((WindowSupport) ctx.getWindow(), params);
				
		final JTree objectTree;
		
		if (objectInspectors.containsKey(graph)) {
			objectTree = objectInspectors.get(graph);
		}
		else {
			objectTree = new JTree(oi);
			objectInspectors.put(graph, objectTree);
			
			// refresh view when graph changes
			graph.addChangeBoundaryListener(new ChangeBoundaryListener() {
				public void beginChange(GraphState gs) {}
				public void endChange(GraphState gs) {
					if (!needUpdate)
						return;
					oi.buildTree();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							objectTree.updateUI();
						}
					});
					needUpdate = false;
				}
				public int getPriority() {
					return 0;
				}
			});
			
			graph.addEdgeChangeListener(new EdgeChangeListener() {
				public void edgeChanged(Object source, Object target,
						Object edge, GraphState gs) {
					needUpdate = true;
				}
			});
					
			objectTree.setDragEnabled(false);
			objectTree.setEditable(false);
			objectTree.setExpandsSelectedPaths(true);
			objectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			objectTree.addTreeSelectionListener(oi);
			
			// change selection in objectree when selection changes in GroIMP
			UIProperty.WORKBENCH_SELECTION.addPropertyListener(ctx, new EventListener() {
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
							public void run() {
								objectTree.updateUI();
							}
						});
					}
				}
			});
			
				
		}
		
		I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();
		
		// Buttons
		JButton setFilterButton = new JButton(thisI18NBundle.getString ("setFilterButton.Name"));
		setFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oi.setFilter(objectTree.getSelectionPaths(), false);
				oi.buildTree();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});
		
		JButton setHierarchicFilterButton = new JButton(thisI18NBundle.getString ("setHierarchicFilterButton.Name"));
		setHierarchicFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oi.setFilter(objectTree.getSelectionPaths(), true);
				oi.buildTree();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});

		JButton removeFilterButton = new JButton(thisI18NBundle.getString ("removeFilterButton.Name"));
		removeFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oi.removeFilter();
				oi.buildTree();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});
		
		JButton expandAllButton = new JButton(thisI18NBundle.getString ("expandAllButton.Name"));
		expandAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    expandAll(objectTree);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});
		
		JButton collapseAllButton = new JButton(thisI18NBundle.getString ("collapseAllButton.Name"));
		collapseAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				expandAll(objectTree);
				collapseAll(objectTree);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						objectTree.updateUI();
					}
				});
			}
		});

		
		// Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(setFilterButton);
		buttonPanel.add(setHierarchicFilterButton);
		buttonPanel.add(removeFilterButton);
		buttonPanel.add(expandAllButton);
		buttonPanel.add(collapseAllButton);
		
		// Tree		
		JScrollPane scrollPane = new JScrollPane(objectTree);
		
		// Layout
		c.setLayout(new BorderLayout());
		c.add(buttonPanel, BorderLayout.NORTH);	
		c.add(scrollPane, BorderLayout.CENTER);
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
	
	public static Panel createHierarchicalObjectInspector (Context ctx, Map params) {
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		ObjectInspectorManager mgr = new ObjectInspectorManager();
		mgr.createObjectInspector(p, ctx, graph, params, hierarchicalObjectInspectors, new HierarchicalObjectInspector(ctx, graph));
		return p;
	}
	
	public static Panel createFlatObjectInspector (Context ctx, Map params) {
		GraphManager graph = ctx.getWorkbench().getRegistry().getProjectGraph();
		UIToolkit ui = UIToolkit.get(ctx);
		Panel p = ui.createPanel(ctx, null, params);
		ObjectInspectorManager mgr = new ObjectInspectorManager();
		mgr.createObjectInspector(p, ctx, graph, params, flatObjectInspectors, new FlatObjectInspector(ctx, graph));
		return p;
	}
	
}
