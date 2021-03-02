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

package de.grogra.imp;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.Path;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.EdgeImpl;
import de.grogra.graph.impl.Node;
import de.grogra.imp.awt.CanvasAdapter;
import de.grogra.imp.edit.Tool;
import de.grogra.imp.edit.ToolRoot;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.registry.ToolFactory;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.util.DisposableEventListener;
import de.grogra.util.EventListener;
import de.grogra.util.I18NBundle;
import de.grogra.util.Map;
import de.grogra.util.Utils;


public abstract class ComponentViewGraphModeEventHandler implements ViewEventHandlerIF
{
	private boolean disposed = false;
	private JobManager jm; 
	private View view;
	private DisposableEventListener navigator = null;
	private PickList list, list2;
	private PickElement pickInfo = new PickElement ();
	private final ArrayPath lastSelected = new ArrayPath ((Graph) null);

	private static final int NOTHING = 0, HIGHLIGHTED = 1, SELECTED = 2;
	private int highlightState = NOTHING, highlightIndex;

	private static final int NORMAL = 1, NAVIGATING = 2, DRAGGING = 3;
	private int state = NORMAL;

	private MouseEvent dragEvent = null, pressEvent = null;
	private int pickX, pickY, lastDragX, lastDragY, lastDragXrec, lastDragYrec;

	private final int tolerance = 8, highlightDelay = 200;

	private final int[] a2 = new int[2];
	
	private int chX = -1, chY;
	private final Point lastClickedPosition = new Point();
	
	// start node for a new connection (edge) manually inserted 
	private Node startConnectionNode;
	
	private Node pickedNode;
	// type of edge for the new edge 
	private int edgeSelected = 0;

	public Node getPickedNode()
	{
		return pickedNode;
	}

	private class Unhighlight implements Command
	{
		private boolean canceled = false;

		void cancel ()
		{
			canceled = true;
		}

		@Override
		public String getCommandName ()
		{
			return null;
		}

		@Override
		public void run (Object info, Context ctx)
		{
			if (!canceled)
			{
				resetHighlight ();
			}
		}
	}

	private Unhighlight unhighlight = null;

	public ComponentViewGraphModeEventHandler (View view, boolean allowNegativePickDist)
	{
		this.view = view;
		jm = view.getWorkbench ().getJobManager ();
		list = new PickList (10, allowNegativePickDist); 
		list2 = new PickList (10, allowNegativePickDist); 
	}


	@Override
	public void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		if (unhighlight != null)
		{
			unhighlight.cancel ();
			unhighlight = null;
		}
		list.reset ();
		list2.reset ();
		pickInfo = null;
		if (navigator != null)
		{
			navigator.dispose ();
			navigator = null;
		}
		dragEvent = null;
		view = null;
		jm = null;
	}


	@Override
	public final View getView ()
	{
		return view;
	}


	private void setState (int newState)
	{
		if (state == newState)
		{
			return;
		}
		switch (state)
		{
			case NORMAL:
				if (unhighlight != null)
				{
					unhighlight.cancel ();
					unhighlight = null;
				}
				if (newState != DRAGGING)
				{
					resetHighlight ();
				}
				break;
			case NAVIGATING:
				navigator.dispose ();
				navigator = null;
				break;
			case DRAGGING:
				if (dragEvent != null)
				{
					mouseDragged (dragEvent, DragEvent.DRAGGING_FINISHED, 0, 0);
					handleDraggedLayout(pickedNode, lastDragX-lastDragXrec, lastDragY-lastDragYrec);
					dragEvent = null;
				}
				break;
		}
		state = newState;
	}


	@Override
	public void disposeNavigator (EventObject e)
	{
		setState (NORMAL);
		if (e != null)
		{
			eventOccured (e);
		}
	}

	protected abstract NavigatorFactory getNavigatorFactory ();
	
	protected abstract void handleDraggedLayout(Object co, int x, int y);
	
	@Override
	public void eventOccured (EventObject e) {
		try {
			View.set (GraphState.current (view.getGraph ()), view);
			NavigatorFactory nf = getNavigatorFactory();
			if (state == NAVIGATING) {
				navigator.eventOccured (e);
			} else if ((state == NORMAL) && (nf != null) && nf.isActivationEvent (e)) {
				UI.consume (e);
				setState (NAVIGATING);
				navigator = nf.createNavigator (this, e);
			} else {
				if (!(e instanceof MouseEvent)) {
					return;
				}
				MouseEvent event = (MouseEvent) e;
				switch (event.getID ()) {
					case MouseEvent.MOUSE_PRESSED:
						if (state == NORMAL) {
							pressEvent = event;
							buttonClicked (event);
							handlePress(event);
							break;
						}
						break;
					case MouseEvent.MOUSE_RELEASED:
						if (state == NORMAL) {
							buttonClicked (event);
						} else {
							setState (NORMAL);
						}
						break;
					case MouseEvent.MOUSE_CLICKED:
						if (state == NORMAL) {
							handleClick (event);
							if (!event.isConsumed ()) {
								buttonClicked (event);
							}
						}
						break;
					case MouseEvent.MOUSE_MOVED:
						if (state != NORMAL) {
							setState (NORMAL);
						}
						mouseMoved (event);
						event.consume ();
						break;
					case MouseEvent.MOUSE_DRAGGED:
						// suppress moving of slot nodes
//						String tmp = pickedNode.getClass ().getSimpleName ();
//						if(tmp.equals ("InputSlot") || tmp.equals ("OutputSlot")) {event.consume(); return; }
						
						dragEvent = event;
						if (state == NORMAL) {
							setState (DRAGGING);
						}
						if (pressEvent != null) {
							mouseDragged (pressEvent, DragEvent.DRAGGING_STARTED, 0, 0);
							lastDragX = pressEvent.getX ();
							lastDragXrec = lastDragX;
							lastDragY = pressEvent.getY ();
							lastDragYrec = lastDragY;
							pressEvent = null;
						}
						mouseDragged (event, DragEvent.DRAGGING_CONTINUED,
									  event.getX () - lastDragX,
									  event.getY () - lastDragY);
						lastDragX = event.getX ();
						lastDragY = event.getY ();
						event.consume ();
						break;
					case MouseEvent.MOUSE_EXITED:
						enqueueUnhighlight ();
						break;
				}
			}
		}
		finally
		{
			View.set (GraphState.current (view.getGraph ()), null);
		}
	}


	private Path getHighlightedPath (int shift)
	{
		if (highlightState == NOTHING)
		{
			return null;
		}
		else
		{
			list.getItem ((highlightIndex + shift + list.getSize ()) % list.getSize (), pickInfo);
			return pickInfo.path;
		}
	}


	private void handleClick (MouseEvent event)
	{
		if (event.isAltDown ())
		{
			if ((highlightState != NOTHING) && (list.getSize () > 1))
			{
				event.consume ();
				a2[0] = highlightIndex;
				if (event.isControlDown ())
				{
					if (highlightIndex == 0)
					{
						highlightIndex = list.getSize ();
					}
					highlightIndex--;
				}
				else
				{
					highlightIndex++;
					if (highlightIndex == list.getSize ())
					{
						highlightIndex = 0;
					}
				}
				a2[1] = highlightIndex;
				highlight (list, highlightIndex, a2);
				highlightState = SELECTED;
				lastSelected.set (getHighlightedPath (0));
				if (unhighlight != null)
				{
					enqueueUnhighlight ();
				}
			}
		}

		// select a node and check if should be connected to an other one on left click
		if(event.getButton () == MouseEvent.BUTTON1) {
			objectPicked ();
		}
		
		// open a popup menu on right click
		if(event.getButton () == MouseEvent.BUTTON3) {
			I18NBundle thisI18NBundle = view.getWorkbench().getRegistry()
				.getPluginDescriptor("de.grogra.imp").getI18NBundle();
			
			//if NO object picked
			if(highlightState==0) {
				return;
			}
			
			/*
			//if NO object is selected
			Object s = UIProperty.WORKBENCH_SELECTION.getValue (view);
			if (!(s instanceof Selection)) {
				return;
			}*/
			
			JPopupMenu menu = new JPopupMenu();
			// is it a node
			if (!pickInfo.path.endsInNode()) {

				if(pickInfo.path.getNodeAndEdgeCount()-1 >=0) {
					// retrieve clicked object
					Object co = pickInfo.path.getEdge(pickInfo.path.getNodeAndEdgeCount()-1);
					// was it a node
					if(co instanceof Node) {
						final Node originNode = (Node) co;
						
						//when it was the last node of a path, then it represents also the edge
						if(originNode==originNode.getTarget ()) {
							menu.add(getMenuDeleteEdge(co, thisI18NBundle));
						} else {
							JMenu menuDeleteEdge = new JMenu(thisI18NBundle.getString ("menuDeleteEdge.Name"));
							JMenuItem menuItemDeleteEdge;
							// Create and add a menu item
							StringBuffer edgeKeys = new StringBuffer();
							Edge tEdge = new EdgeImpl(originNode.getSource (), originNode.getTarget ());
							tEdge.setEdgeBits (originNode.getEdgeBits (), null);
							tEdge.getEdgeKeys (edgeKeys, true, true);
							
							menuItemDeleteEdge = new JMenuItem(thisI18NBundle.getString ("menuItemDeleteEdge.Name") + "   " +edgeKeys);
							menuItemDeleteEdge.addActionListener(new ActionListener() {
	
								@Override
								public void actionPerformed (ActionEvent event)
								{
									Workbench.delete (null, null, view);
									//repaint
									repaintAll();
								}
								
							});
							menuDeleteEdge.add (menuItemDeleteEdge);
							menu.add(menuDeleteEdge);
						}
					} else
					// was it an edge 
					if(co instanceof EdgeImpl) {
						menu.add(getMenuDeleteEdge(co, thisI18NBundle));
					}
				}
			} else {
				// retrieve clicked object
				Object co = pickInfo.path.getNode(pickInfo.path.getNodeAndEdgeCount()-1);
				// was is a node
				if(co instanceof Node) {
					final Node n = (Node) co;
					// Create and add a menu item
					JMenuItem menuItemDelete = new JMenuItem(thisI18NBundle.getString ("menuItemDelete.Name"));
					menuItemDelete.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event)
						{
							if (n.getGraph () != null)
							{
								Workbench.delete (null, null, view);
							}
							//repaint
							repaintAll();
						}
						
					});
					menu.add(menuItemDelete);
				}
			}
			
			menu.show(event.getComponent(), event.getX(), event.getY());
		}
	}

	/**
	 * repaints the view and triggers a code actualising event.
	 */
	private void repaintAll() {
		fireGraphChanedEvent();
		view.repaint (ViewComponent.SCENE | ViewComponent.CHANGED);
	}

	private JMenuItem getMenuDeleteEdge (Object co, ResourceBundle thisI18NBundle) {
		final Node originNode = (Node) co;
		JMenu menuDeleteEdge = new JMenu(thisI18NBundle.getString ("menuDeleteEdge.Name"));
		JMenuItem menuItemDeleteEdge;
		for (int i = Graph.MIN_NORMAL_BIT_INDEX; i < Graph.MAX_NORMAL_BIT_INDEX; i++) {
			final int mask = 1 << i;
			if (originNode.testEdgeBits(mask)) {

				// Create and add a menu item
				StringBuffer edgeKeys = new StringBuffer();
				Edge tEdge = new EdgeImpl(originNode.getSource (), originNode.getTarget ());
				tEdge.setEdgeBits (mask, null);
				tEdge.getEdgeKeys (edgeKeys, true, true);
				
				// iff it is not a dummy edge
				if(mask!=Graph.DUMMY_EDGE) {
					menuItemDeleteEdge = new JMenuItem(thisI18NBundle.getString ("menuItemDeleteEdge.Name") + "   " +edgeKeys);
					menuItemDeleteEdge.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event) {
//System.out.println ("s= "+originNode.getSource ()+ " t= "+ originNode.getTarget ()+"  "+originNode.getSource ().getEdgeBitsTo (originNode.getTarget ())+"  mask= "+mask);
							originNode.getSource ().removeEdgeBitsTo (originNode, mask, null);
							//check if it was the last edge connected to this node
							if(originNode.getEdgeBits ()==0) {
								// connect them with an dummy edge to the root
								connect ((Node)view.getGraph ().getRoot (Graph.COMPONENT_GRAPH), originNode, Graph.DUMMY_EDGE);
							}
							
							//repaint
							repaintAll();
						}

					});
					menuDeleteEdge.add (menuItemDeleteEdge); 
				}
			}
		}
		return menuDeleteEdge;
	}

	private void handlePress (MouseEvent event)
	{
		// select a node and check if should be connected to another one on left click
		if(event.getButton () == MouseEvent.BUTTON1) {
			objectPicked ();
		}
	}

	private void objectPicked() {
//System.out.println ("\n picked0 "+startConnectionNode.getClass ().getSimpleName ()+"    "+pickInfo+"\n    "+pickInfo.path+"    "+pickInfo.path.endsInNode());

		Object co = null;
		// retrieve clicked object
		if (!pickInfo.path.endsInNode()) {
			co = pickInfo.path.getEdge(pickInfo.path.getNodeAndEdgeCount()-1);
		} else {
			co = pickInfo.path.getNode(pickInfo.path.getNodeAndEdgeCount()-1);
		}

		if(co instanceof Node) {
			Node n = (Node) co;
//System.out.println ("picked1 "+startConnectionNode.getClass ().getSimpleName ()+"  "+n.getClass ().getSimpleName ()+"  "+edgeSelected);
			if(n.getId ()!=-1) {
				// if no edge type is selected
				pickedNode = n;
				if(edgeSelected==0) {
					// just remember the last visited node
					startConnectionNode = n;
				} else {
					if(startConnectionNode==n) {
						finishConnecting();
						// set state variables back
						edgeSelected=0;
						startConnectionNode=null;
						return;
					}
					// is a node and an edge type selected 
					if (startConnectionNode!=null && edgeSelected!=0) {
						// try to connect them
						if(connect(startConnectionNode, n, edgeSelected)) {
							// set state variables back
							edgeSelected=0;
							startConnectionNode=null;
							//repaint
							repaintAll();
						}
					}
				}
			}
		} else {
			startConnectionNode=null;
		}
	}

	
	@Override
	public void setDropTarget (ComponentDescriptor cd) {
		// if the node was dropped to an other node
		Node node = new Node();
		node.setName (cd.getIdentificationKey ());
		// connect them with a refinement edge 
		connect (startConnectionNode, node, Graph.REFINEMENT_EDGE);

		ArrayList<String> islots = cd.getInSlots();
		for (String slotName : islots) {
			addInputSlot(node, slotName);
		}
		ArrayList<String> oslots = cd.getOutSlots();
		for (String slotName : oslots) {
			addOutputSlot(node, slotName);
		}
		//repaint
		repaintAll();
	}


	/**
	 * Connect the two specified nodes according to the edge type.
	 * 
	 * @param start
	 * @param target
	 * @param edgeType
	 */
	public abstract boolean connect (Node start, Node target, int edgeType);
	
	public abstract void fireGraphChanedEvent();
	
	protected abstract void finishConnecting();
	
	public abstract void addInputSlot (Node start, String slotName);
	public abstract void addOutputSlot (Node start, String slotName);
	
	public abstract void expendComponent ();

	@Override
	public void setSelectedEdge (int edgeType) {
		edgeSelected = edgeType;
	}
	

	protected void mouseMoved (final MouseEvent event)
	{
		if ((highlightState != SELECTED)
			|| (Math.abs (event.getX () - pickX) > tolerance)
			|| (Math.abs (event.getY () - pickY) > tolerance))
		{
			UI.executeLockedly
				(view.getGraph (), false,
				 new Command ()
				 {
					@Override
					public String getCommandName ()
					{
						return null;
					}

					@Override
					public void run (Object arg, Context c)
					{
						calculateHighlight (event.getX (), event.getY ());
					}
				 }, event, view, JobManager.RENDER_FLAGS);
			// if there is a node selected for connecting it with an other one
			// and there is also an edge type selected
			if(edgeSelected!=0 && startConnectionNode!=null) {
				getViewComponentComponent().setPointForLine(
					lastClickedPosition.x, lastClickedPosition.y, event.getX (), event.getY ());
			}
		}
	}


	@Override
	public void updateHighlight ()
	{
		if (chX >= 0)
		{
			calculateHighlight (chX, chY);
		}
	}


	private void calculateHighlight (int x, int y)
	{
		chX = x;
		chY = y;
		view.pick (x, y, list2);
		if (list2.getSize () > 0)
		{
			if (unhighlight != null)
			{
				unhighlight.cancel ();
				unhighlight = null;
			}
			if (list2.equals (list))
			{
				list2.reset ();
			}
			else
			{
				resetHighlight ();
				PickList l = list;
				list = list2;
				list2 = l;
				pickX = x;
				pickY = y;
				highlightIndex = -1;
				for (int i = 0; i < list.getSize (); i++)
				{
					list.getItem (i, pickInfo);
					if (GraphUtils.equal (pickInfo.path, lastSelected))
					{
						highlightIndex = i;
						break;
					}
				}
				if (highlightIndex < 0)
				{
					lastSelected.clear (null);
					highlightIndex = 0;
				}
				// TODO: option to disable highlighting - almost untested
				Map opt = UI.getOptions (getView().getWorkbench());
				if (Utils.getBoolean(opt, "highlightOnMove", true)) {
					highlight (list, highlightIndex, null);
				}
				highlightState = HIGHLIGHTED;
//				objectPicked ();
			}
		}
		else if ((highlightState != NOTHING) && (unhighlight == null))
		{
			enqueueUnhighlight ();
		}
	}


	private void enqueueUnhighlight ()
	{
		if (unhighlight != null)
		{
			unhighlight.cancel ();
		}
		jm.runLater(highlightDelay, unhighlight = new Unhighlight (), null, view);
	}


	private void resetHighlight ()
	{
		if (unhighlight != null)
		{
			unhighlight.cancel ();
			unhighlight = null;
		}
		if (highlightState != NOTHING)
		{
			highlight (list, -1, null);
			list.reset ();
			highlightState = NOTHING;
		}
	}


	private void highlight (PickList list, int index, int[] changed)
	{
		ViewSelection s = ViewSelection.get (view);
		int i = -1, j = -1, k;
		while (true)
		{
			if (changed == null)
			{
				i++;
				if (i == list.getSize ())
				{
					break;
				}
			}
			else
			{
				j++;
				if (j == changed.length)
				{
					break;
				}
				i = changed[j];
			}
			if (index >= 0)
			{
				if (i == index)
				{
					k = ViewSelection.MOUSE_OVER_SELECTED | ViewSelection.MOUSE_OVER;
				}
				else
				{
					k = ViewSelection.MOUSE_OVER;
				}
			}
			else
			{
				k = 0;
			}
			Path p = list.getPath (i);
			if (!view.isToolGraph (p.getGraph ()))
			{
				s.removeAndAdd (ViewSelection.MOUSE_OVER_SELECTED | ViewSelection.MOUSE_OVER, k, p);
			}
		}
	}


	protected void buttonClicked (MouseEvent e)
	{
		lastClickedPosition.setLocation (e.getX (), e.getY ());
		e.consume ();
		Path path = null;
		boolean tool = false;
		for (int s = 0; s < list.getSize (); s++)
		{
			Path p = getHighlightedPath (s);
			if (p != null)
			{
				if  (view.isToolGraph (p.getGraph ()))
				{
					if (s == 0)
					{
						tool = true;
					}
				}
				else
				{
					path = p;
					break;
				}
			}
		}
		if (!tool && (e.getID () == MouseEvent.MOUSE_PRESSED)
			&& !(e.isAltDown () || e.isMetaDown ()))
		{
			ViewSelection s = ViewSelection.get (view);
			if (s != null)
			{
				if (path != null)
				{
					if (e.isControlDown ())
					{
						s.toggle (ViewSelection.SELECTED, path);
					}
					else
					{
						s.set (ViewSelection.SELECTED, new Path[] {path}, true);
					}
				}
				else
				{
					s.set (ViewSelection.SELECTED, Path.PATH_0, true);
				}
			}
		}
		if (hasListener (path))
		{
			path = new ArrayPath (path);
			ClickEvent me = createClickEvent (e);
			me.set (getView (), path);
			me.set (e);
			send (me, path);
		}
	}
	
	private static void send (EventObject e, Path p)
	{
		Object last = p.getObject (-1);
		if (last instanceof EventListener)
		{
			((EventListener) last).eventOccured (e);
		}
		if (p.getGraph () instanceof EventListener)
		{
			((EventListener) p.getGraph ()).eventOccured (e);
		}
	}

	private static boolean hasListener (Path p) {
		if (p == null) return false;
		return (p.getObject (-1) instanceof EventListener) || (p.getGraph () instanceof EventListener);
	}

	protected void mouseDragged (MouseEvent event, int dragState, int dx, int dy) {
		Path p = getHighlightedPath (0);
		if (hasListener (p)) {
			p = new ArrayPath (p);
			//move node
			mouseDragged (event, p, dragState, dx, dy);
			//move slot nodes
			moveSlotNodes(event, p.getGraph (), dragState, dx, dy);
		}
	}

	private void mouseDragged (MouseEvent event, Path p, int dragState, int dx, int dy) {
		DragEvent me = createDragEvent (event);
		me.set (getView (), p);
		me.set (event);
		me.setDragData (dragState, dx, dy);
		send (me, p);
	}

	private void moveSlotNodes(MouseEvent event, Graph g, int dragState, int dx, int dy) {
//		System.out.println (" p = "+pickedNode+"  "+pickedNode.getDirectChildCount ());
		
		PickList listOfSlotNodes = new PickList(50, true);
		listOfSlotNodes.reset(view, 0,0);
		view.getToolNodes (listOfSlotNodes);

		if(pickedNode!=null) {
			List<Node> outList = pickedNode.getOutputSlotNodes();
			if(outList!=null) {
				for (Node node : outList) {
					
					//erste art das edittool zu bekommen
					Tool et1 = getEditTool(node);
					//zweite art das edittool zu bekommen
					Tool et2 = getEditTool(listOfSlotNodes, node.getId ());
					
					moveSlotNode(et2, event, g, dragState, dx, dy);
				}
			}
			List<Node> inList = pickedNode.getInputSlotNodes();
			if(inList!=null) {
				for (Node node : inList) {
					moveSlotNode(getEditTool(node), event, g, dragState, dx, dy);
				}
			}
		}
	}

	private Tool getEditTool (Node n) {
		ToolFactory factory = (ToolFactory) view.getToolListener().getToolFactory();
		Tool t = factory.createTool (n, true);
		t.initialize (view);
		
		return t;
	}
	
	private Tool getEditTool (PickList list, long id) {
		PickElement pickInfo = new PickElement ();
		for(int i=0; i<list.getSize (); i++) {
			list.getItem (i, pickInfo);
			if(pickInfo.path.toString ().contains ("[id="+id+"]")) {
				ToolFactory factory = (ToolFactory) view.getToolListener().getToolFactory();
				Object o = (pickInfo.path != null) ? pickInfo.path.getObject (-1) : null;
				Tool t = factory.createTool (o, true);
				t.initialize (view);
				return t;
			}
		}
		return null;
	}

	private void moveSlotNode (Tool t, MouseEvent event, Graph g, int dragState, int dx, int dy) {
//		GraphManager graph1 = view.getWorkbench().getRegistry().getProjectGraph();
//		GraphManager graph2 = view.getWorkbench().getRegistry().getComponentGraph();
//		Graph graph3 = view.getGraph ();
		ArrayPath p = new ArrayPath (g);
		ToolRoot tr = t.getRoot (0);
		String s = tr.toString ();
		long l2 = Long.parseLong (s.substring (s.indexOf ("@")+1, s.length ()), 16);
		p.pushNode (tr, l2);
		DragEvent me = createDragEvent (event);
		me.set (getView (), p);
		me.set (event);
		me.setDragData (dragState, dx, dy);
		send (me, p);
	}

	@Override
	public abstract ClickEvent createClickEvent (MouseEvent event);

	@Override
	public abstract DragEvent createDragEvent (MouseEvent event);
	
	protected abstract CanvasAdapter.CanvasComponent getViewComponentComponent();


}
