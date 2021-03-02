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

package de.grogra.imp2d;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;

import de.grogra.graph.AttributeChangeEvent;
import de.grogra.graph.AttributeChangeListener;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ComponentNode;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp.ComponentViewGraphModeEventHandler;
import de.grogra.imp.ComponentViewHierarchicalGraphModeEventHandler;
import de.grogra.imp.NavigatorFactory;
import de.grogra.imp.PickList;
import de.grogra.imp.View;
import de.grogra.imp.ViewComponent;
import de.grogra.imp.ViewEventHandlerIF;
import de.grogra.imp.awt.CanvasAdapter;
import de.grogra.imp.registry.ViewComponentFactory;
import de.grogra.imp2d.graphs.GroIMPComponent;
import de.grogra.imp2d.graphs.InputSlot;
import de.grogra.imp2d.graphs.ObjectData;
import de.grogra.imp2d.graphs.OutputSlot;
import de.grogra.imp2d.layout.EdgeBasedLayout2ComponentGraph;
import de.grogra.imp2d.layout.Layout;
import de.grogra.persistence.PersistenceField;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.ObjectSelection;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.util.MimeType;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

public class ComponentView2D extends View2DIF
{
	//enh:sco

	public static final IOFlavor FLAVOR = IOFlavor.valueOf (ComponentView2D.class);

	static final String DISPLAY_PATH = "/ui/viewcomponentview2dselection";
	static final String TOOL_PATH = "/ui/tools/2d";

	public static final UIProperty DISPLAY = UIProperty.getOrCreate (DISPLAY_PATH, UIProperty.PANEL);
	public static final UIProperty TOOL = UIProperty.getOrCreate (TOOL_PATH, UIProperty.PANEL);

	// remembers the selected edge button (slot or send)
	private JButton edgeButton = null;

	Matrix3d transformation;
	//enh:field type=de.grogra.math.Matrix3dType.$TYPE setmethod=setTransformation

	Layout layout;
	//enh:field

	Matrix3d canvasTransformation = new Matrix3d ();

	private PickRayVisitor pickVisitor2DGraph, pickVisitorHierarchicalGraph;
	private PickAllVisitor visitor2DGraph, visitorHierarchicalGraph;
	static boolean isHierarchicalGraphView;

	private ViewComponent tmpViewComponent = null;
	private ViewComponent tmpViewComponent2D = null;
	private ViewComponent tmpViewComponentH = null;

	private Node pickedNode;

	private final SceneListener sceneListener = new SceneListener () {
		@Override
		public void endChange (GraphState gs) {
			super.endChange (gs);
			if(getWorkbench ()!=null) {
				if(getWorkbench ().isDeleteEvent()) {
					// refresh main rgg file when graph has changed
					updateAxiomLine();
					getWorkbench ().setDeleteEvent(false);
				}
			}
		}
	};

	private final class Listener implements AttributeChangeListener {
		@Override
		public void attributeChanged (AttributeChangeEvent event) { }
	}


	private final Command layoutCommandGraph = new Command () {
		@Override
		public void run (Object info, Context context) {
			if (info == null) {
				if (layout != null) {
					UI.executeLockedly(getGraph (), true, this, layout, context, JobManager.UPDATE_FLAGS);
					layout.setLayouted();
				} else {
					layouting = false;
				}
			} else if (info == this) {
				layouting = false;
			} else if (info instanceof Layout.Algorithm[]) {
				UI.executeLockedly (getGraph (), true, this, ((Layout.Algorithm[]) info)[0], context, JobManager.UPDATE_FLAGS);
			} else {
				Layout.Algorithm a = (info instanceof Layout.Algorithm) ? (Layout.Algorithm) info : null;
				a = layout.invoke ((View2DIF) context, a, GraphManager.COMPONENT_GRAPH);
				if (a != null) {
					getWorkbench ().getJobManager ().runLater (200, this, new Layout.Algorithm[] {a}, context);
				} else {
					UI.getJobManager (context).runLater (this, this, context, JobManager.RENDER_FLAGS);
				}
			}
		}

		@Override
		public String getCommandName () {
			return null;
		}
	};

	private final Command layoutCommandGraphH = new Command () {
		@Override
		public void run (Object info, Context context) {
			if (info == null) {
				if (layout != null) {
					UI.executeLockedly(getGraph (), true, this, layout, context, JobManager.UPDATE_FLAGS);
				} else {
					layouting = false;
				}
			} else if (info == this) {
				layouting = false;
			} else if (info instanceof Layout.Algorithm[]) {
				UI.executeLockedly (getGraph (), true, this, ((Layout.Algorithm[]) info)[0], context, JobManager.UPDATE_FLAGS);
			} else {
				Layout.Algorithm a = (info instanceof Layout.Algorithm) ? (Layout.Algorithm) info : null;
				a = layout.invoke ((View2DIF) context, a, GraphManager.COMPONENT_GRAPH);
				if (a != null) {
					getWorkbench ().getJobManager ().runLater (200, this, new Layout.Algorithm[] {a}, context);
				} else {
					UI.getJobManager (context).runLater (this, this, context, JobManager.RENDER_FLAGS);
				}
			}
		}

		@Override
		public String getCommandName () {
			return null;
		}
	};

	void layout () {
		if (!layouting) {
			layouting = true;
			if(isHierarchicalGraphView) {
				getWorkbench ().getJobManager ().runLater (layoutCommandGraphH, null, ComponentView2D.this, JobManager.UPDATE_FLAGS);
System.out.println("layout H "+layout.oldlayoutedList);
			} else {
				getWorkbench ().getJobManager ().runLater (layoutCommandGraph, null, ComponentView2D.this, JobManager.UPDATE_FLAGS);
System.out.println("layout 2D "+layout.oldlayoutedList);
			}
		}
	}

	public void updateView() {
		ViewComponentFactory f = ViewComponentFactory.get (this, DISPLAY_PATH);
		if (f.getCLS().equals("de.grogra.imp2d.AWTCanvas2DHierarchicalGraph"))
		{
			pickedNode = ((ComponentViewGraphModeEventHandler) eventHandler).getPickedNode();
			if (pickedNode == null)
			{
				Node rootNode = (Node)this.getGraph ().getRoot (Graph.COMPONENT_GRAPH);
				pickedNode = rootNode.getBranch ();
			}
			eventHandler = null;
			layout.oldlayoutedList.coordinateBackup();
			isHierarchicalGraphView = true;
			tmpViewComponent = tmpViewComponentH;
			setViewComponent (tmpViewComponent);
			layout.handlePickedNodeAndChildren(pickedNode);
			layout();
			setViewComponent (f.createViewComponent (this));
			addDropTargetListener();
		}
	}

	public ComponentView2D ()
	{
		transformation = new Matrix3d ();
		transformation.setIdentity ();
		transformation.m00 = 100;
		transformation.m11 = -100;
		transformation.m12 = 100;
		transformation.m22 = 1;
		layout = new EdgeBasedLayout2ComponentGraph();
	}


	@Override
	public IOFlavor getFlavor () {
		return FLAVOR;
	}


	@Override
	protected UIProperty getToolProperty () {
		return TOOL;
	}


	@Override
	protected ViewEventHandlerIF createEventHandler ()
	{
		if(!isHierarchicalGraphView) {
			return new ComponentViewGraphModeEventHandler (this, true)
			{
			
				private final Point2d point = new Point2d ();

				private void getPoint (MouseEvent event)
				{
					point.set (event.getX (), event.getY ());
					de.grogra.vecmath.Math2.invTransformPoint
						(getCanvasTransformation (), point);
				}

				@Override
				public ClickEvent createClickEvent (MouseEvent e)
				{
					getPoint (e);
					return new ClickEvent2D (point);
				}

				@Override
				protected void handleDraggedLayout(Object co, int dx, int dy)
				{
					final Point2d pointori = new Point2d ();
					//prepare to write dragged coordinate into layout
					if (layout.oldlayoutedList!=null && layout.oldlayoutedList.nodeSearch(co, pointori))
					{
						de.grogra.vecmath.Math2.transformPoint(getCanvasTransformation (), pointori);
						pointori.x += dx;
						pointori.y += dy;
						de.grogra.vecmath.Math2.invTransformPoint(getCanvasTransformation (), pointori);
						layout.oldlayoutedList.nodeCoordSet(co, pointori);
					}
				}
			
				@Override
				public DragEvent createDragEvent (MouseEvent e)
				{
					getPoint (e);
					return new DragEvent2D (point);
				}

				@Override
				public DragEvent createDragEvent (Point2d point)
				{
					return new DragEvent2D (point);
				}

				@Override
				protected NavigatorFactory getNavigatorFactory ()
				{
					return new Navigator2DFactory ();
				}
			
				@Override
				public void expendComponent() {
					goDown();
				}

				/**
				 * Connect the two specified nodes according to the edge type.
				 * 
				 * @param start
				 * @param target
				 * @param edgeType
				 */
				@Override
				public boolean connect (Node start, Node target, int edgeType) {
					if(target==null) return false;

					// isn't it the same node; loops from a component to itself are not allowed
					if(start == target) return false;

					// check conditions for valid connections
					if(!(
						// 1) from output to input slots, with slot edge, where the target slot (input slot) has no other incoming edges
						(start instanceof OutputSlot && target instanceof InputSlot && 
						// and only if it is an edge 
						edgeType==Graph.SLOT_EDGE && target.getDirectPredecessorCount()==1) 
					||
						// 2) from component to another component, only when a refinement edge is selected
						(start instanceof GroIMPComponent && target instanceof GroIMPComponent && 
						// is it a refinement edge 
						 edgeType==Graph.REFINEMENT_EDGE)
					)) return false;
					
					if(start!=null && !(start instanceof ComponentNode) && !start.getClass ().getSimpleName ().contains ("ComponentRoot")) {
						start.addEdgeBitsTo(target, edgeType, null);
					} else {
						Node rootNode = (Node) getGraph().getRoot (GraphManager.COMPONENT_GRAPH);
						Node rootNode1 = rootNode.getBranch ();
						Node rootNode2 = rootNode1.getSuccessor ();
						if(rootNode2!=null && rootNode2 instanceof GroIMPComponent) {
							rootNode2.addEdgeBitsTo(target, edgeType, null);
						} else {
							if(rootNode1!=null) {
								rootNode1.addEdgeBitsTo(target, edgeType, null);
							}
						}
					}
					finishConnecting();
					return true;
				}

				@Override
				public void fireGraphChanedEvent() {
					// refresh main rgg file when graph has changed
					updateAxiomLine();
				}

				@Override
				public void addInputSlot (Node start, String slotName) {
					if(start==null) return;
					start.addEdgeBitsTo(new InputSlot(slotName), Graph.COMPONENT_INPUT_SLOT_EDGE, null);
				}

				@Override
				public void addOutputSlot (Node start, String slotName) {
					if(start==null) return;
					start.addEdgeBitsTo(new OutputSlot(slotName), Graph.COMPONENT_OUTPUT_SLOT_EDGE, null);
				}

				@Override
				protected CanvasAdapter.CanvasComponent getViewComponentComponent() {
					return (CanvasAdapter.CanvasComponent) getViewComponent().getComponent();
				}
				
				@Override
				protected void finishConnecting() {
					finishConnectingA(getViewComponentComponent());
				}
			};
		}

		return new ComponentViewHierarchicalGraphModeEventHandler (this, true)
		{
			private final Point2d point = new Point2d ();

			private void getPoint (MouseEvent event)
			{
				point.set (event.getX (), event.getY ());
				de.grogra.vecmath.Math2.invTransformPoint
					(getCanvasTransformation (), point);
			}

			@Override
			public ClickEvent createClickEvent (MouseEvent e)
			{
				getPoint (e);
				return new ClickEvent2D (point);
			}

			@Override
			protected boolean handleDragLimit(Object co) {
				ViewComponent vc1 = getViewComponent ();
				GraphState state = ((AWTCanvas2DHierarchicalGraph)vc1).getGraphState ();
			
				// get the position of the current node
				ObjectData od = (ObjectData)state.getObjectDefault(co, true, de.grogra.imp2d.objects.Attributes.TRANSFORM, null);
			
				Point2d pointori = new Point2d (od.x, od.y);
	
				//get the size of the box
				Point2d x0y0 = new Point2d (0,0);
				Point2d wh = new Point2d (0,0);
				((AWTCanvas2DHierarchicalGraph)vc1).getRectangleDimension(x0y0, wh);
 
				// calculate max x and y out of the original size in pixels 
				double xMax = wh.x /100d -1.4;
				double yMax = -((wh.y+100) /100d -1.4);
			
				//tell the layout the size of the current boy
				layout.setBoxSize(xMax, yMax);
			
				boolean b = true;
		//System.out.println ("H1     "+(pointori.x < 0.034)+" "+(pointori.x > xMax)+" "+(pointori.y > 0)+" "+(pointori.y < yMax)+"  MAX "+xMax+":"+yMax);
				// check if the borders are touched
				{
					if (pointori.x < 0.05 || pointori.x > xMax || pointori.y > -0.05 || pointori.y < yMax) {
	//					if (pointori.x < 0) pointori.x = 0.035;
	//					if (pointori.x > xMax) pointori.x = xMax-0.01;
	//					if (pointori.y > 0) pointori.y = -0.01;
	//					if (pointori.y < yMax) pointori.y = yMax+0.01;
						//layout.oldlayoutedList.nodeCoordSet(co, pointori);
						b = false; // mark that the border was crossed
					}
				}
				
				//System.out.println ("H2    "+pointori+"   "+b+"\n");
				return b;
			}
		
			@Override
			public DragEvent createDragEvent (MouseEvent e)
			{
				getPoint (e);
				return new DragEvent2D (point);
			}

			@Override
			public DragEvent createDragEvent (Point2d point)
			{
				return new DragEvent2D (point);
			}
		
			@Override
			protected NavigatorFactory getNavigatorFactory ()
			{
				return new Navigator2DFactory ();
			}

			@Override
			public void expendComponent() {
				goDown();
			}

			/**
			 * Connect the two specified nodes according to the edge type.
			 * 
			 * @param start
			 * @param target
			 * @param edgeType
			 */
			@Override
			public boolean connect (Node start, Node target, int edgeType)
			{
				if(target==null) return false;
			
				if(start!=null) {
					start.addEdgeBitsTo(target, edgeType, null);
				} else {
					Node rootNode = (Node) getGraph().getRoot (GraphManager.COMPONENT_GRAPH);
					rootNode = rootNode.getBranch ();
					if(rootNode!=null) {
						rootNode.addEdgeBitsTo(target, edgeType, null);
					}
				}
				finishConnecting();
				return true;
			}
			
			@Override
			public void fireGraphChanedEvent() {
				// refresh main rgg file when graph has changed
				updateAxiomLine();
			}
			
			@Override
			public void addInputSlot (Node start, String slotName) {
				if(start==null) return;
				start.addEdgeBitsTo(new InputSlot(slotName), Graph.COMPONENT_INPUT_SLOT_EDGE , null);
			}
		
			@Override
			public void addOutputSlot (Node start, String slotName) {
				if(start==null) return;
				start.addEdgeBitsTo(new OutputSlot(slotName), Graph.COMPONENT_OUTPUT_SLOT_EDGE, null);
			}
		
			@Override
			protected CanvasAdapter.CanvasComponent getViewComponentComponent() {
				return (CanvasAdapter.CanvasComponent) getViewComponent().getComponent();
			}

			@Override
			protected void finishConnecting() {
				finishConnectingA(getViewComponentComponent());
			}
		};
	}
	
	public void finishConnectingA(CanvasAdapter.CanvasComponent viewComponentComponent) {
		// deselect the used button
		if(edgeButton!=null) edgeButton.setSelected (false);
		// turn off the line
		viewComponentComponent.setLineOff ();
	}

	@Override
	protected void installImpl ()
	{
		pickVisitor2DGraph = new PickRayVisitor ();
		pickVisitorHierarchicalGraph = new PickRayVisitor ();
		visitor2DGraph = new PickAllVisitor ();
		visitorHierarchicalGraph = new PickAllVisitor ();
		getGraph ().addAttributeChangeListener(new Listener());

		// refresh main rgg file when graph has changed
//		getGraph().addChangeBoundaryListener(new ChangeBoundaryListener() {
//			@Override
//			public void beginChange(GraphState gs) {}
//
//			@Override
//			public void endChange(GraphState gs) {
//				if (!needUpdate) return;
//				writeAxiomLine(generateAxiomLine(null), null);
//System.out.println ("ComponentView: graph changed listener");
//				needUpdate = false;
//			}
//
//			@Override
//			public int getPriority() {
//				return 0;
//			}
//		});
	
//		getGraph().addEdgeChangeListener(new EdgeChangeListener() {
//			@Override
//			public void edgeChanged(Object source, Object target, Object edge, GraphState gs) {
//				needUpdate = true;
//			}
//		});
	
	
		sceneListener.install (getGraph ());
		if (getViewComponent () == null) {
			ViewComponentFactory f = ViewComponentFactory.get (this, DISPLAY_PATH);
			String tmp = f.getCLS();
			f.setCLS("de.grogra.imp2d.AWTCanvas2D");
			tmpViewComponent2D = f.createViewComponent (this);
		
			f.setCLS("de.grogra.imp2d.AWTCanvas2DHierarchicalGraph");
			tmpViewComponentH = f.createViewComponent (this);
			f.setCLS(tmp);

			isHierarchicalGraphView = false;
			tmpViewComponent = tmpViewComponent2D;
			setViewComponent (tmpViewComponent);
			Node rootNode = (Node)this.getGraph ().getRoot (Graph.COMPONENT_GRAPH);
			pickedNode = rootNode.getBranch ();
		
			DISPLAY.setValue (this, f);
			DISPLAY.addPropertyListener (this, this);
		
			layout ();
			repaint(ViewComponent.SCENE);
			addDropTargetListener();
		} else {
			layout ();
		}
	}
	

	private void addDropTargetListener ()
	{
		ViewComponent vc = getViewComponent ();
		if(vc!= null) {
			if(vc instanceof AWTCanvas2D) {
				addDropTargetListenerGraph(vc);
			} else if(vc instanceof AWTCanvas2DHierarchicalGraph) {
				addDropTargetListenerHierarchicalGraph(vc);
			}
		}
	}

	private void addDropTargetListenerGraph (ViewComponent vc)
	{
		Component c = (Component) vc.getComponent();
		c.setDropTarget (new DropTarget(c, new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				event.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
			}

			@Override
			public void dragExit(DropTargetEvent event) {
				event.getDropTargetContext ().getComponent ().setCursor (new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void dragOver(DropTargetDragEvent event) {
			}

			@Override
			public void drop(DropTargetDropEvent event) {
				if(event==null) return;
				Transferable transferable = event.getTransferable();
				try	{
					if (event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						Object transferDataKey = transferable.getTransferData(DataFlavor.stringFlavor);
						if (transferDataKey instanceof String) {
							event.getDropTargetContext().dropComplete(true);

							String componentIdentificationKey = (String)transferable.getTransferData(transferable.getTransferDataFlavors ()[0]);
							ComponentDescriptor cd = getWorkbench ().getRegistry ().getComponent (componentIdentificationKey);
							if(cd == null) return;
							// add this component as node to the graph
							eventHandler.setDropTarget(cd);

							// add the code of the component
							String[] className = addComponentCodeToProject(cd);
							// update the main rgg file
							updateMainRggFile(className, cd);
							event.dropComplete(true);
							event.getDropTargetContext ().getComponent ().setCursor (new Cursor(Cursor.DEFAULT_CURSOR));
						}
					} else {
						event.rejectDrop();
						event.dropComplete(false); 
					}
				}
				catch (UnsupportedFlavorException e) {
					event.rejectDrop();
				}
				catch (IOException e) {
					event.rejectDrop();
				}
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent arg0) {
			}
		}));
	}

	private void addDropTargetListenerHierarchicalGraph (ViewComponent vc)
	{
		Component c = (Component) vc.getComponent();
		c.setDropTarget (new DropTarget(c, new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetDragEvent event) {
				event.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
			}

			@Override
			public void dragExit(DropTargetEvent event) {
				event.getDropTargetContext ().getComponent ().setCursor (new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void dragOver(DropTargetDragEvent event) {
				event.getDropTargetContext ().getComponent ().setCursor (DragSource.DefaultCopyDrop);
			}

			@Override
			public void drop(DropTargetDropEvent event) {
				Transferable transferable = event.getTransferable();
				try	{
					if (event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						Object transferDataKey = transferable.getTransferData(DataFlavor.stringFlavor);
						if (transferDataKey instanceof String) {
							event.getDropTargetContext().dropComplete(true);

							String componentIdentificationKey = (String)transferable.getTransferData(transferable.getTransferDataFlavors ()[0]);
							ComponentDescriptor cd = getWorkbench ().getRegistry ().getComponent (componentIdentificationKey);
							// add this component as node to the graph
							eventHandler.setDropTarget(cd);
						
							// add the code of the component
							String[] className = addComponentCodeToProject(cd);
							// update the main rgg file
							updateMainRggFile(className, cd);
							event.dropComplete(true);
							event.getDropTargetContext ().getComponent ().setCursor (new Cursor(Cursor.DEFAULT_CURSOR));
						}
					} else {
						event.rejectDrop();
						event.dropComplete(false); 
					}
				}
				catch (UnsupportedFlavorException e) {
					event.rejectDrop();
				}
				catch (IOException e) {
					event.rejectDrop();
				}
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent arg0) {
			}
		}));
	}


	/**
	 * Adds the code of the added component as new file to the project.
	 * Deletes any existing file with the targetFileNeme from project file system and project listing before.
	 * 
	 * @param component descriptor
	 * @return list of all rgg files of the added rgg file
	 */
	private String[] addComponentCodeToProject(ComponentDescriptor cd) {
		String[] rggFileList = {""};
		try {
			//use current registry's file system
			Registry registry = getWorkbench().getRegistry ();
			rggFileList = cd.getRggFileList ();
			FileSystem fs = registry.getFileSystem();

			if(fs instanceof MemoryFileSystem) {
				//for all rgg files included inside the component
				for(String item:rggFileList) {
					//if file already exists in registry, delete it and remove it from file explorer list
					removedFileFromFileExpl(item);
//					deleteFile(item);

					//generate new file
					Object generatedFile = fs.create (fs.getRoot(), item, false);

					//copy the file content
					InputStream inStream = cd.getRggInputStream (item);

					FileSource fsrc = new FileSource(fs, generatedFile,IO.toSystemId (fs, generatedFile), 
						new MimeType ("text/x-grogra-rgg", null), registry, null);
					OutputStream fsrcOut = fsrc.getOutputStream(false);
					BufferedWriter generatedWriter = new BufferedWriter(new OutputStreamWriter(fsrcOut));

					int read = 0;
					StringBuffer buf = new StringBuffer();
					while ((read = inStream.read()) != -1) {
						buf.append ((char)read);
						generatedWriter.write(read);
					}

					//yong 11 jan 2013 - get module name from description content.
					cd.loadModuleName();
					//flush and end process
					generatedWriter.flush();
					fsrcOut.flush();

					//if file system is memory file system, must close in order for data to be written in.
					((MemoryFileSystem) fs).closeQuiet(generatedFile);

					//add generated file to 'file explorer' in project (list of files for project)
					final SourceFile sf = new SourceFile (IO.toSystemId (registry.getFileSystem(), generatedFile), 
						new MimeType ("text/x-grogra-rgg", null));
					registry.getDirectory ("/project/objects/files", null).addUserItem (sf);
				}
			}
		}
		catch(Throwable t) {}
		return rggFileList;
	}

	/**
	 * Removes the file with the fileName from project file list (file explorer)
	 * 
	 * @param src
	 */
	private void removedFileFromFileExpl(String fileName) {
		//get list of entries in GroIMP's file explorer
		Item dir = getWorkbench().getRegistry().getDirectory ("/project/objects/files", null);
		for(Node n = dir.getBranch(); n != null; n = n.getSuccessor()) {
			if(n instanceof SourceFile) {
				SourceFile sf = (SourceFile) n;
		
				//if generated file is found in file explorer, remove it from file explorer
				if(sf.getName().equals("pfs:"+fileName)) {
					sf.remove();
				}
			}
		}
	}


	/**
	 * Deletes generated xl file from file system (local or memory file system)
	 * @param src
	 */
	private void deleteGeneratedFile(String fileName) {
		try {
			//get file system - could be LocalFileSystem or MemoryFileSystem
			FileSystem fs = getWorkbench().getRegistry().getFileSystem();
	
			//get parent path of file
//			Object parent = fs.getParent(src.getFile());
	
			//attempt to get generated file from file system
			Object file = null; //fs.getFile(parent, fileName);
	
			//if generated file exists, delete it
			if(file!=null)
				fs.delete(file);
		} 
		catch (Throwable e) {
			//e.printStackTrace();
			return;
		}
	}

	/**
	 * Updates the main rgg file (adds the imports and writes an new axiom component line).
	 * 
	 * steps: 
	 * - Parses the component graph and generates a new axiom line
	 * - and write it to the main project file
	 * 
	 */
	private void updateMainRggFile(String[] nameOfNewClasses, ComponentDescriptor cd) {
		String axiomLine = generateAxiomLine(cd);
		// generate connections
		axiomLine = generateConnections(axiomLine);
		writeAxiomLine(axiomLine, nameOfNewClasses);
		
	}

	/**
	 * Parses the component graph and generates a new axiom line
	 *
	 * @return 
	 */
	private String generateAxiomLine(ComponentDescriptor cd) {
		String s = "errror";
		Workbench w = getWorkbench ();
		if(w!=null) {
			GraphManager gm = w.getRegistry ().getProjectGraph ();
			if(gm!=null) {
				s = gm.getComponentGraphString();
			}
		}
		if(cd!=null && s.length ()>4) s=s.replace ("Node", cd.getModuleName ());
		return "    AxiomComponent ==> "+s+";";
	}

	private void updateAxiomLine() {
		// refresh main rgg file when graph has changed
		writeAxiomLine(generateAxiomLine(null), null);
	}
	
	/**
	 * Writes the code of the axiom line to the main rgg project file.
	 * Steps:
	 * - read in the rgg file until the AxiomComponent line is found
	 * - if it was found, replace it with the new component axiom line
	 * - if not search for the axiom line and write behind the component axiom line. 
	 *
	 * @param text of the axiom line
	 */
	private void writeAxiomLine(String axiomComponentLine, String[] nameOfNewClasses) {
		Registry registry = getWorkbench().getRegistry();
		MemoryFileSystem fs = (MemoryFileSystem) registry.getFileSystem();

		//
		Object mainRGG = registry.getMainRGG();
		if(mainRGG==null) return;
		String mainRGGFileName = mainRGG.toString ().substring (0, mainRGG.toString ().indexOf ("[id="));

		// read input file
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.getInputStream(fs.getFile(mainRGGFileName+".rgg"))));
		StringBuilder sb=new StringBuilder();
		String read = "";
		try {
			read = br.readLine();
			while(read != null) {
				sb.append(read+"\n");
				read = br.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//split input text
		String[] part = splitInputFile(sb.toString());

		// add/check imports
		if(nameOfNewClasses!=null) part[0] = addImports(part[0], nameOfNewClasses);

		//writing it back
		Object mainFile = fs.getFile(mainRGGFileName+".rgg");
		OutputStream out = fs.getOutputStream(mainFile, false);
		try {
			out.write(part[0].getBytes());
			out.write(axiomComponentLine.getBytes());
			out.write(part[1].getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//close file (will automatically compiled)
		fs.close(mainFile);

		//refresh JEdit text editor
		Workbench.refreshJEdit(getWorkbench (), mainRGGFileName+".rgg");
	}


	/**
	 * Parses the axiom line and generates the connections (get and set function) 
	 * to connect the components.
	 *
	 * @return 
	 */
	private String generateConnections(String axiomLine) {
		//TODO
		return axiomLine;
	}

	private String[] splitInputFile(String inputText) {
		String top="", tail="";
		// check and if there is a AxiomComponent line
		// iff remove it
		if(inputText.contains ("AxiomComponent")) {
			int at = inputText.indexOf("AxiomComponent");
			top = inputText.substring (0, at).trim () + "\n";
			while(inputText.charAt (at)!=';') at++;
			tail = inputText.substring (at+1, inputText.length ());
			return new String[] {top, tail};
		}

		// if there is an init function - what has to be for the main rgg file
		if(inputText.contains ("void init")) {
			// if it has a axiom rule
			if(inputText.contains ("Axiom==>") || inputText.contains ("Axiom ==>")) {
				int at = inputText.indexOf("Axiom==>");
				at = at==-1 ? inputText.indexOf("Axiom ==>") : at;
				top = inputText.substring (0, at);
				while(inputText.charAt (at)!=';') {
					top += inputText.charAt (at);
					at++;
				}
				top += ";\n";
				tail = inputText.substring (at+1, inputText.length ());
			} else {
				int at = inputText.indexOf ("void init")+8;
				boolean b = true;
				while(inputText.charAt (at)!='{' && (b=inputText.charAt (at)!='[')) at++;
				top = inputText.substring (0, at+1);
				top += "\n";
				if(b)top += "\t[\n";

				if(b)tail += "\t]\n";
				tail += inputText.substring (at+1, inputText.length ());
			}
		} else {
			top += "protected void init() [\n";
	
			tail += "]\n";
			tail += inputText;
		}

		return new String[] {top, tail};
	}


	private String addImports(String inputText, String[] nameOfNewClasses) {
		StringBuffer out = new StringBuffer();
		//check if the world component is already added
		if(!inputText.contains("import de.grogra.imp2d.graphs.World;")) {
			addImport(out, inputText, "import de.grogra.imp2d.graphs.World;\n");
			inputText = out.toString ();
		}
		out = new StringBuffer();
		//check if the InputSlot is already added
		if(!inputText.contains("import de.grogra.imp2d.graphs.InputSlot;")) {
			addImport(out, inputText, "import de.grogra.imp2d.graphs.InputSlot;\n");
			inputText = out.toString ();
		}
		out = new StringBuffer();
		//check if the OutputSlot is already added
		if(!inputText.contains("import de.grogra.imp2d.graphs.OutputSlot;")) {
			addImport(out, inputText, "import de.grogra.imp2d.graphs.OutputSlot;\n");
			inputText = out.toString ();
		}
		out = new StringBuffer();

		for(String item:nameOfNewClasses) {
			item = item.replace (".rgg", "");
			//if the input text already contains the new class
			//TODO: will fail, if there is the word of variable <item> in one of the comments above the real import part
			if(inputText.contains(item)) continue;
			addImport(out, inputText, "import static "+item+".*;\n");
		}
		return out.toString ();
	}


	//TODO: will fail, if there is the word "import" in one of the comments above the real import part
	private void addImport(StringBuffer out, String inputText, String item) {
		//check if there exists at least one import line
		if(inputText.contains("import")) {
			int x = inputText.indexOf("import");
			out.append (inputText.substring (0, x));
			out.append (item);
			out.append (inputText.substring(x));
		} else {
			out.append (item);
			out.append (inputText);
		}
	}


	@Override
	protected void uninstallImpl ()
	{
		sceneListener.remove (getGraph ());
	}

	@Override
	public void eventOccured (java.util.EventObject e) {
		if (e instanceof UIPropertyEditEvent) {
			UIPropertyEditEvent pe = (UIPropertyEditEvent) e;
			Object o = pe.getNewValue ();
			if (o!=null && pe.getProperty () == DISPLAY) {
				if (o instanceof ViewComponentFactory) {
					if (((ViewComponentFactory) o).getCLS().equals("de.grogra.imp2d.AWTCanvas2D")) {
						eventHandler = null;
						isHierarchicalGraphView = false;
						if (layout.isLayouted()) {
							layout.oldlayoutedList.coordinateRecover();
						} else { 
							layouting = false; 
						}
						tmpViewComponent = tmpViewComponent2D;
						setViewComponent (tmpViewComponent);
					} else {
						pickedNode = ((ComponentViewGraphModeEventHandler) eventHandler).getPickedNode();
						if (pickedNode == null) {
							Node rootNode = (Node)this.getGraph ().getRoot (Graph.COMPONENT_GRAPH);
							pickedNode = rootNode.getBranch ().getSuccessor ();
						}
						eventHandler = null;
						if (layout.isLayouted()) {
							layout.oldlayoutedList.coordinateBackup();
						}
						isHierarchicalGraphView = true;
						tmpViewComponent = tmpViewComponentH;
						setViewComponent (tmpViewComponent);
						if (layout.isLayouted()) {
							layout.handlePickedNodeAndChildren(pickedNode);
						} else { 
							layouting = false; 
						}
					}
					layout();
					setViewComponent (((ViewComponentFactory) o).createViewComponent (this));
					addDropTargetListener();
				}
				return;
			}
		}
		super.eventOccured (e);
	}

	public static Selectable getSelectableLayout (Context ctx)
	{
		final ComponentView2D v = (ComponentView2D) get (ctx);
		return new Selectable ()
		{
			@Override
			public Selection toSelection (Context c)
			{
				return new ObjectSelection
					(c, v, new PersistenceField[] {layout$FIELD},
					 null, null, null, null)
				{
					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						//if (field.overlaps (null, layout$FIELD, null))
						//{
						//	v.layout ();
						//}
					}
				};
			}
		};
	}


	@Override
	public void pick (int x, int y, PickList list) {
		if (isHierarchicalGraphView) {
			pickVisitorHierarchicalGraph.pick (this, x, y, list);
		} else {
			pickVisitor2DGraph.pick (this, x, y, list);
		}
	}

	@Override
	public void getToolNodes (PickList list) {
		if (isHierarchicalGraphView) {
			visitorHierarchicalGraph.pick (this, list);
		} else {
			visitor2DGraph.pick (this, list);
		}
	}

	@Override
	public boolean isToolGraph (Graph graph)
	{
		return graph == de.grogra.graph.impl.GraphManager.STATIC;
	}


	@Override
	public void setTransformation (Matrix3d t)
	{
		transformation.set (t);
		repaint (ViewComponent.ALL | ViewComponent.CHANGED);
	}

	@Override
	public final Matrix3d getTransformation ()
	{
		return transformation;
	}

	@Override
	public Matrix3d getCanvasTransformation ()
	{
		canvasTransformation.set (transformation);
		UIToolkit ui = UIToolkit.get (this);
		canvasTransformation.m02 += ui.getWidth (getViewComponent ().getComponent ()) >> 1;
		canvasTransformation.m12 += ui.getHeight (getViewComponent ().getComponent ()) >> 1;
		return canvasTransformation;
	}

	public Matrix3d getInvCanvasTransformation (Matrix3d inTransformation)
	{
		Matrix3d outTransformation = new Matrix3d(inTransformation);
		UIToolkit ui = UIToolkit.get (this);
		outTransformation.m02 -= ui.getWidth (getViewComponent ().getComponent ()) >> 1;
		outTransformation.m12 -= ui.getHeight (getViewComponent ().getComponent ()) >> 1;
		return outTransformation;
	}

	public void goUp() {
		if(pickedNode != null) {
			if(pickedNode.getClass().getSimpleName().equals("World")) return;
			Node p = layout.handleGoUp(pickedNode);
			if(p != null){
				pickedNode = p;
			}
			layout();
			addDropTargetListener();
		}
	}

	public void goDown() {
		pickedNode = ((ComponentViewHierarchicalGraphModeEventHandler) eventHandler).getPickedNode();
		if(pickedNode != null) {
			layout.handlePickedNodeAndChildren(pickedNode);
			layout();
			addDropTargetListener();
		}
	}


	private void selectButton(JButton edgeButton) {
		if(edgeButton==null) return;
		if(this.edgeButton!=null) 
			this.edgeButton.setSelected (false);
		this.edgeButton = edgeButton;
		this.edgeButton.setSelected (true);
	}


	public void addHasPartEdge(JButton edgeButton) {
		selectButton(edgeButton);
		if(eventHandler instanceof ComponentViewGraphModeEventHandler ) {
			eventHandler.setSelectedEdge(Graph.REFINEMENT_EDGE);
		}
	}


	public void addUsesEdge(JButton edgeButton) {
		selectButton(edgeButton);
		if(eventHandler instanceof ComponentViewGraphModeEventHandler ) {
			eventHandler.setSelectedEdge(Graph.USES_EDGE);
		}
	}


	public void addSendEdge(JButton edgeButton) {
		selectButton(edgeButton);
		if(eventHandler instanceof ComponentViewGraphModeEventHandler ) {
			eventHandler.setSelectedEdge(Graph.SEND_EDGE);
		}
	}

	public void addSlotEdge(JButton edgeButton) {
		selectButton(edgeButton);
		if(eventHandler instanceof ComponentViewGraphModeEventHandler ) {
			eventHandler.setSelectedEdge(Graph.SLOT_EDGE);
		}
	}



//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field transformation$FIELD;
	public static final Type.Field layout$FIELD;

	public static class Type extends View.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ComponentView2D representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, View.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = View.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = View.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ComponentView2D) o).setTransformation ((Matrix3d) value);
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					{
						((ComponentView2D) o).layout = (Layout) value;
					}
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ComponentView2D) o).transformation;
				case Type.SUPER_FIELD_COUNT + 1:
				{ 

					return ((ComponentView2D) o).layout;
				}
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ComponentView2D ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ComponentView2D.class);
		transformation$FIELD = Type._addManagedField ($TYPE, "transformation", 0 | Type.Field.SCO, de.grogra.math.Matrix3dType.$TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		layout$FIELD = Type._addManagedField ($TYPE, "layout", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Layout.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}
