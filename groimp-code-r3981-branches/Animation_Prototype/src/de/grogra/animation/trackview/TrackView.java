package de.grogra.animation.trackview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.grogra.animation.AnimManager;
import de.grogra.animation.handler.Handler;
import de.grogra.animation.timeline.TimeContext;
import de.grogra.animation.util.Debug;
import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.ObjectInspector;
import de.grogra.imp.ObjectInspector.TreeNode;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.ui.Workbench;

public class TrackView extends JSplitPane implements ActionListener, TreeSelectionListener {

	final private Component nodesPanel;
	final private Component editorPanel;
	final private Workbench wb;
	final private GraphManager graph;
	
	private ObjectInspector oi;
	private JTree objectTree;
	
	CanvasPanel canvasPanel;
	JButton fitButton;
	JToggleButton moveKeysButton;
	JToggleButton addKeysButton;
	
	private boolean needUpdate = false;
	
	public TrackView(Workbench wb, GraphManager graph, AnimManager animManager, TimeContext timeContext) {
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		
		this.wb = wb;
		this.graph = graph;
		
		nodesPanel = createNodesPanel();
		editorPanel = createEditorPanel(animManager, timeContext);
		
		this.setLeftComponent(nodesPanel);
		this.setRightComponent(editorPanel);
	}
	
	public Component createNodesPanel() {
		oi = new NodePropertyInspector(wb, graph);
		objectTree = new JTree(oi);
		
		objectTree.setDragEnabled(false);
		objectTree.setEditable(false);
		objectTree.setExpandsSelectedPaths(true);
		objectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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
		
		// event when clicking in tree
		objectTree.addTreeSelectionListener(this);
		
		// Tree		
		JScrollPane scrollPane = new JScrollPane(objectTree);
		
		return scrollPane;
	}
	
	public Component createEditorPanel(AnimManager animManager, TimeContext timeContext) {
		JPanel editorPanel = new JPanel(new BorderLayout());
		
		// buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fitButton = new JButton("Fit");
		fitButton.addActionListener(this);
		buttonsPanel.add(fitButton);
		
		ButtonGroup controlButtonsGroup = new ButtonGroup();
		moveKeysButton = new JToggleButton("Move Keys");
		moveKeysButton.addActionListener(this);
		addKeysButton = new JToggleButton("Add Keys");
		addKeysButton.addActionListener(this);
		moveKeysButton.setSelected(true);
		controlButtonsGroup.add(moveKeysButton);
		controlButtonsGroup.add(addKeysButton);
		buttonsPanel.add(moveKeysButton);
		buttonsPanel.add(addKeysButton);
		editorPanel.add(buttonsPanel, BorderLayout.NORTH);
		
		// canvas
		canvasPanel = new CanvasPanel(animManager, timeContext);
		editorPanel.add(canvasPanel, BorderLayout.CENTER);
		
		return editorPanel;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == fitButton) {
			canvasPanel.fitView();
		}
		else if (source == moveKeysButton) {
			canvasPanel.setKeyTool(CanvasPanel.TOOL_MOVE_KEYS);
		}
		else if (source == addKeysButton) {
			canvasPanel.setKeyTool(CanvasPanel.TOOL_ADD_KEYS);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		refresh();
		canvasPanel.fitView();
	}
	
	/**
	 * Updates all objects of canvas panel and repaint them.
	 */
	public void refresh() {
		canvasPanel.clearGraphContext();
		
		TreePath selectionPath = objectTree.getSelectionPath();
		if (selectionPath != null) {
			TreeNode treeNode = (TreeNode)  selectionPath.getLastPathComponent();
			Object selection = treeNode.getObject();
			if (selection instanceof PersistenceField) {
				PersistenceField field = (PersistenceField) selection;
				TreeNode parentTreeNode = treeNode.getParent();
				Object p = parentTreeNode.getObject();
				// TODO: sicherstellen, dass man den wirklichen Knoten,
				//also das sichtbare Objekt, hier findet
				if (p instanceof Node) {
					Node node = (Node) p;
					canvasPanel.addGraphContext(node, field);
				}
			}
		}
		canvasPanel.repaint();
	}
	
	/**
	 * Repaints the canvas panel.
	 */
	public void redraw() {
		canvasPanel.repaint();
	}
	
	
}
