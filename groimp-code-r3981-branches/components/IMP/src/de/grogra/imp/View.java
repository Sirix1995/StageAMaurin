
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

package de.grogra.imp;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import de.grogra.graph.AttributeChangeEvent;
import de.grogra.graph.AttributeChangeListener;
import de.grogra.graph.Attributes;
import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.EdgeChangeListener;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.graph.impl.Edge;
import de.grogra.imp.edit.Tool;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp.io.ImageWriter;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.Transaction;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.FileChooserResult;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.PanelDecorator;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIProperty;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.Workbench;
import de.grogra.pf.ui.edit.ObjectSelection;
import de.grogra.pf.ui.edit.Selectable;
import de.grogra.pf.ui.edit.Selection;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.event.UIPropertyEditEvent;
import de.grogra.util.Configuration;
import de.grogra.util.ConfigurationSet;
import de.grogra.util.DisposableEventListener;
import de.grogra.util.EnumerationType;
import de.grogra.util.EventListener;
import de.grogra.util.I18NBundle;
import de.grogra.util.KeyDescription;
import de.grogra.util.KeyDescriptionImpl;
import de.grogra.util.Map;
import de.grogra.util.ModifiableMap;
import de.grogra.util.StringMap;

/**
 * A <code>View</code> is a {@link de.grogra.pf.ui.Panel} which represents
 * a {@link de.grogra.graph.Graph} in geometrical-graphical way
 * (e.g., using two- or three-dimensional graphics).
 * <code>View</code> is a subclass of <code>PanelDecorator</code>:
 * As such, it implements some common operations of views, while
 * the actual GUI component is provided by the decorated panel.
 *
 * @author Ole Kniemeyer
 */
public abstract class View extends PanelDecorator
	implements de.grogra.util.EventListener, TreeModelListener,
		ModifiableMap.Producer, Manageable
{
	public static int LOD_MIN = 0;
	public static int LOD_MAX = 3;
	public static int STEREO_MAX = 3;
	public static int PREVIEW_MAX = 6;
	public static int ANAGLYPH_MAX = 7;

	private static final int VIEW_ID = GraphState.allocatePropertyId ();
	public static final EnumerationType LOD_TYPE = new EnumerationType ("lod", IMP.I18N, LOD_MAX + 1);
	public static final EnumerationType STEREO_VIEW_TYPE = new EnumerationType ("view.stereo", IMP.I18N, STEREO_MAX + 1);
	
	public static final EnumerationType VIEW_PREVIEW1_TYPE = new EnumerationType ("view.preview", IMP.I18N, PREVIEW_MAX + 1);
	public static final EnumerationType VIEW_PREVIEW2_TYPE = new EnumerationType ("view.preview", IMP.I18N, PREVIEW_MAX + 1);
	
	public static final EnumerationType ANAGLYPH_VIEW_TYPE = new EnumerationType ("view.anaglyph", IMP.I18N, ANAGLYPH_MAX + 1);
		
	public static final UIProperty COMPONENT = UIProperty.getOrCreate ("viewComponent", UIProperty.PANEL);

	public static final I18NBundle I18N = I18NBundle.getInstance (View.class);
	
	private static final int[] EDGE_TO_ID_MAP = {Graph.SUCCESSOR_EDGE, Graph.BRANCH_EDGE, 
		Graph.REFINEMENT_EDGE, Graph.SEND_EDGE,	Graph.USES_EDGE, Graph.SLOT_EDGE, Graph.DUMMY_EDGE};
	
	GraphDescriptor graphDescriptor;
	//enh:field
	
	boolean[] visibleLayers;
	//enh:field

	boolean[] visibleEdges;
	//enh:field
	
	float epsilon;
	//enh:field setmethod=setEpsilon getter

	float epsilonSquared;

	float visualEpsilon;
	//enh:field setmethod=setVisualEpsilon getter

	float visualEpsilonSquared;

	float magnitude;
	//enh:field setmethod=setMagnitude getter

	float magnitudeSquared;


	private final Object componentLock = new Object ();


	private Graph graph;
	private GraphState state;
	private boolean disposed;
	private transient int stamp = 0;

	private ViewComponent component;

	protected ViewEventHandlerIF eventHandler;

	private Tool activeTool;
	private DisposableEventListener toolListener;

	protected final EventListener.Multicaster listeners
		= new EventListener.Multicaster ();

	
	public static View get (Context c)
	{
		Panel p = c.getPanel ().resolve ();
		while ((p != null) && !(p instanceof View))
		{
			p = p.getDecorator ();
		}
		return (View) p;
	}

	
	public static View get (GraphState gs)
	{
		return (View) gs.getUserProperty (VIEW_ID); 
	}


	public static void set (GraphState gs, View view)
	{
		gs.setUserProperty (VIEW_ID, view); 
	}

	/**
	 * Returns the view component which is used as a GUI component
	 * to perform the actual visualization of the graph. 
	 * 
	 * @return view component of the GUI
	 */
	public static ViewComponent getViewComponent (Context c)
	{
		return get (c).getViewComponent ();
	}


	public static View create (View view, Context ctx, StringMap args)
	{
		View v2 = (View) args.get ("view");
		if (v2 != null)
		{
			view = v2;
		}
		view.initialize (UIToolkit.get (ctx).createPanel (ctx, view, args), args);
		if (v2 == null)
		{
			GraphDescriptor g = (GraphDescriptor) args.get ("graph", null);
			if (g == null)
			{
				String dg = (String) args.get ("defaultGraph", null);
				if (dg != null)
				{
					Expression e = (Expression) Item.resolveItem
						(ctx.getWorkbench (), dg);
					if (e != null)
					{
						g = (GraphDescriptor) e.evaluate (ctx.getWorkbench (), args);
					}
				}
			}
			if (g == null)
			{
				g = new ProjectGraphDescriptor ();
			}
			view.setGraph (g);
		}
		else
		{
			view.setGraph ();
		}
		return view;
	}


	public static Selectable getSelectableGraph (Context ctx)
	{
		final View v = get (ctx);
		return new Selectable ()
		{
			@Override
			public Selection toSelection (Context c)
			{
				return new ObjectSelection
					(c, v, new PersistenceField[] {graphDescriptor$FIELD},
					 null, null, null, null)
				{
					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						if (field.overlaps (null, graphDescriptor$FIELD, null))
						{
							v.setGraph ();
						}
					}
				};
			}
		};
	}


	public static Selectable getSelectableLayers (Context ctx)
	{
		final View v = get (ctx);
		return new Selectable ()
		{
			@Override
			public Selection toSelection (Context c)
			{
				PersistenceField f = visibleLayers$FIELD.getArrayChain (1);
				PersistenceField[] fa = new PersistenceField[Attributes.LAYER_COUNT];
				int[][] ia = new int[Attributes.LAYER_COUNT][];
				String[] la = new String[Attributes.LAYER_COUNT];
				for (int i = 0; i < Attributes.LAYER_COUNT; i++)
				{
					fa[i] = f;
					ia[i] = new int[] {i};
					la[i] = IMP.I18N.msg ("view.display-layer", i);
				}
				return new ObjectSelection
					(c, v, fa, ia, null, la, null)
				{
					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						if (field.overlaps (null, visibleLayers$FIELD, null))
						{
							ViewComponent vc = v.getViewComponent ();
							if (vc != null)
							{
								vc.repaint (ViewComponent.ALL);
							}
						}
					}
				};
			}
		};
	}

	public static Selectable getSelectableEdges (Context ctx)
	{
		final View v = get (ctx);
		return new Selectable ()
		{
			@Override
			public Selection toSelection (Context c)
			{
				PersistenceField f = visibleEdges$FIELD.getArrayChain (1);
				PersistenceField[] fa = new PersistenceField[Attributes.EDGE_COUNT];
				int[][] ia = new int[Attributes.EDGE_COUNT][];
				String[] la = new String[Attributes.EDGE_COUNT];
				for (int i = 0; i < Attributes.EDGE_COUNT; i++)
				{
					fa[i] = f;
					ia[i] = new int[] {i};
					// TODO: replace the simple "Edge type i" by the right names
					la[i] = IMP.I18N.msg ("view.display-edge", i);
				}
				return new ObjectSelection
					(c, v, fa, ia, null, la, null)
				{
					@Override
					protected void valueChanged (PersistenceField field, Object value)
					{
						if (field.overlaps (null, visibleEdges$FIELD, null))
						{
							ViewComponent vc = v.getViewComponent ();
							if (vc != null)
							{
								vc.repaint (ViewComponent.ALL);
							}
						}
					}
				};
			}
		};
	}


	protected class SceneListener implements AttributeChangeListener,
		EdgeChangeListener, ChangeBoundaryListener
	{
		protected boolean hasChanged = false;


		public SceneListener ()
		{
		}
		
		
		public void install (Graph g)
		{
			g.addAttributeChangeListener (this);
			g.addEdgeChangeListener (this);
			g.addChangeBoundaryListener (this);
		}

		
		public void remove (Graph g)
		{
			g.removeAttributeChangeListener (this);
			g.removeEdgeChangeListener (this);
			g.removeChangeBoundaryListener (this);
		}


		@Override
		public void attributeChanged (AttributeChangeEvent e)
		{
			hasChanged = true;
			repaint (ViewComponent.SCENE | ViewComponent.CHANGED);
		}


		@Override
		public void edgeChanged (Object source, Object target, Object edgeSet,
								  GraphState gc)
		{
			hasChanged = true;
			repaint (ViewComponent.SCENE | ViewComponent.CHANGED);
		}

		
		@Override
		public void beginChange (GraphState gs)
		{
			ViewSelection s = ViewSelection.get (View.this);
			if (s != null)
			{
				s.graphModified (gs);
			}
			hasChanged = false;
		}


		@Override
		public void endChange (GraphState gs)
		{
		}

		
		@Override
		public int getPriority ()
		{
			return TOPOLOGY_PRIORITY;
		}
	}

	
	protected abstract UIProperty getToolProperty ();

	
	public abstract de.grogra.pf.io.IOFlavor getFlavor ();


	public View ()
	{
		setDimensions (1);
		visibleLayers = new boolean[Attributes.LAYER_COUNT];
		for (int i = 0; i < Attributes.LAYER_COUNT; i++)
		{
			visibleLayers[i] = true;
		}
		visibleEdges = new boolean[Attributes.EDGE_COUNT];
		for (int i = 0; i < Attributes.EDGE_COUNT; i++)
		{
			visibleEdges[i] = true;
		}
	}


	public void setGraph ()
	{
		setGraph (graphDescriptor.getGraph (this));
	}


	public void setGraph (GraphDescriptor g)
	{
		graphDescriptor = g;
		setGraph (g.getGraph (this));
	}


	private void setGraph (Graph g)
	{
		if (graph != null)
		{
			uninstall ();
		}
		if (g != null)
		{
			graph = g;
			de.grogra.imp.edit.ViewSelection.create (this);
			ViewSelection.PROPERTY.addPropertyListener (this, this);
			UIProperty.WORKBENCH_SELECTION.addPropertyListener (this, this);
			getWorkbench ().getRegistry ().addTreeModelListener (this);
			toolListener = Tool.createToolListener (this, getToolProperty ());
			installImpl ();
		}
	}
	
	
	protected void uninstall ()
	{
		if (activeTool != null)
		{
			activeTool.dispose ();
			activeTool = null;
		}
		getWorkbench ().getRegistry ().removeTreeModelListener (this);
		ViewSelection.PROPERTY.removePropertyListener (this, this);
		UIProperty.WORKBENCH_SELECTION.removePropertyListener (this, this);
		toolListener.dispose ();
		uninstallImpl ();
		graph = null;
		state = null;
	}

	public DisposableEventListener getToolListener() {
		return toolListener;
	}
	public Graph getGraph ()
	{
		return graph;
	}

	
	public final GraphState getWorkbenchGraphState ()
	{
		synchronized (this)
		{
			if (state != null)
			{
				return state;
			}
		}
		GraphState gs = GraphState.get (graph, getWorkbench ().getJobManager ().getThreadContext ());
		synchronized (this)
		{
			state = gs;
		}
		return gs;
	}

	
	public void substituteSelection (GraphState[] gs, Object[] object,
									 boolean asNode[], int index)
	{
		graphDescriptor.substituteSelection (gs, object, asNode, index);
	}

	
	public Path getPathFor (GraphState gs, Object obj, boolean node)
	{
		return graphDescriptor.getPathFor (this, gs, obj, node);
	}

	
	public ViewEventHandlerIF getEventHandler ()
	{
		return eventHandler;
	}

	
	@Override
	public void addMappings (ModifiableMap out)
	{
		out.put ("view", this);
	}

	
	protected void initialize (Panel panel, Map params)
	{
		initPanel (panel);
	}


	protected abstract void installImpl ();

	public void getToolNodes (PickList list) {}

	public abstract void pick (int x, int y, PickList list);


	public abstract boolean isToolGraph (Graph graph);


	public void addEventListener (EventListener el)
	{
		listeners.addEventListener (el);
	}


	public void removeEventListener (EventListener el)
	{
		listeners.removeEventListener (el);
	}


	@Override
	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		setGraph ((Graph) null);
		if (eventHandler != null)
		{
			eventHandler.dispose ();
			eventHandler = null;
		}
		listeners.removeAllListeners ();
	}


	protected abstract void uninstallImpl ();

	
	protected abstract ViewEventHandlerIF createEventHandler ();


	public void setViewComponent (ViewComponent component)
	{
		synchronized (componentLock)
		{
			if (eventHandler == null)
			{
				eventHandler = createEventHandler ();
			}
			component.initView (this, eventHandler);//H: eventHandler created for the whole view?
			setContent (component);
			this.component = component;
		}
		COMPONENT.setValue (this, component);
	}


	public ViewComponent getViewComponent ()
	{
		return component;
	}

	
	public final void setActiveTool (Tool tool)
	{
		if (activeTool == tool)
		{
			return;
		}
		if (activeTool != null)
		{
			activeTool.dispose ();
			activeTool = null;
		}
		activeTool = tool;
		if (tool != null)
		{
			tool.initialize (this);
		}
		repaint (ViewComponent.TOOLS | ViewComponent.CHANGED);
	}


	public final Tool getActiveTool ()
	{
		return activeTool;
	}


	@Override
	public void treeNodesChanged (TreeModelEvent e)
	{
		Object[] c = e.getChildren ();
		if (c != null)
		{
			for (int i = c.length - 1; i >= 0; i--)
			{
				if (c[i] instanceof ObjectItem)
				{
					repaint (ViewComponent.SCENE | ViewComponent.CHANGED);
				}
			}
		}
	}

	@Override
	public void treeNodesInserted (TreeModelEvent e)
	{
	}

	@Override
	public void treeNodesRemoved (TreeModelEvent e)
	{
	}

	@Override
	public void treeStructureChanged (TreeModelEvent e)
	{
	}

	@Override
	public void eventOccured (java.util.EventObject e)
	{
		if (e instanceof UIPropertyEditEvent)
		{
			UIProperty p = ((UIPropertyEditEvent) e).getProperty ();
			if (p == ViewSelection.PROPERTY)
			{
				repaint (ViewComponent.SELECTION | ViewComponent.CHANGED);
			}
			else if (p == UIProperty.WORKBENCH_SELECTION)
			{
				Object o = UIProperty.WORKBENCH_SELECTION.getValue (this);
				if (!(o instanceof Selection))
				{
					o = null;
				}
				if(o!=null) {
					//yz 20 dec 2012: selection applies only to panel which selection was made.
					ViewSelection vs = ViewSelection.get (((UIPropertyEditEvent) e).getPanel());
					if(vs!=null) vs.set ((Selection) o, false);
				}
			}
		}
	}


	protected void repaint (int flags)
	{
		synchronized (componentLock)
		{
			if (component != null)
			{
				component.repaint (flags);
			}
		}
	}


	public static void render (Item item, Object info, de.grogra.pf.ui.Context ctx)
	{
		if ((ctx.getPanel () instanceof View)
			&& (info instanceof ActionEditEvent)
			&& ((info = ((ActionEditEvent) info).getSource ())
				instanceof Expression)
			&& ((info = ((Expression) info).evaluate
				 (ctx.getWorkbench (), UI.getArgs (ctx, null)))
				instanceof Renderer))
		{
			View v = (View) ctx.getPanel ();
			ViewSelection s = ViewSelection.get (v);
			if (s != null)
			{
				s.set (ViewSelection.SELECTED, Path.PATH_0, false);
			}
			v.component.render ((Renderer) info);
		}
	}
	
	
	public static void renderToFile (Item item, Object info, de.grogra.pf.ui.Context ctx)
	{
		if ((ctx.getPanel () instanceof View)
			&& (info instanceof ActionEditEvent)
			&& ((info = ((ActionEditEvent) info).getSource ())
				instanceof Expression)
			&& ((info = ((Expression) info).evaluate
				 (ctx.getWorkbench (), UI.getArgs (ctx, null)))
				instanceof Renderer))
		{
			final View v = (View) ctx.getPanel ();
			ViewSelection s = ViewSelection.get (v);
			if (s != null)
			{
				s.set (ViewSelection.SELECTED, Path.PATH_0, false);
			}
			
			ConfigurationSet cs = new ConfigurationSet ("Render dimensions");
			KeyDescription keyWidth = new KeyDescriptionImpl("width", I18N, "rendertofile.width",
					Type.INT, null);
			KeyDescription keyHeight = new KeyDescriptionImpl("height", I18N, "rendertofile.height",
					Type.INT, null);
			Configuration c = new Configuration(new KeyDescription[] {keyWidth, keyHeight},
					new StringMap().putInt("width", 600).putInt("height", 600));
			cs.add(c);
			int width, height;
			if (Workbench.current().showConfigurationDialog(cs)) {
				width = (Integer) cs.get("width", null);
				height = (Integer) cs.get("height", null);
			}
			else
			{
				return;
			}
			
			final FileChooserResult fr = Workbench.current ().chooseFileToSave (
					I18N.msg ("rendertofile.title"), ImageWriter.RENDERED_IMAGE_FLAVOR, null);
			if (fr == null)
			{
				return;
			}
				
			Renderer renderer = (Renderer) info;
			renderer.addImageObserver(new ImageObserver()
			{
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, final int width, final int height) 
				{
					if ((infoflags & ALLBITS) == 0)
					{
						// Image was updated, cause its not ready yet.
						return true;
					} 										
					// This writes the rendered image to Disk		
					IMP.writeImage(v, (RenderedImage) img, fr.getMimeType (), fr.file);
														
					// Return if image is ready (no more image updates => false)
					return false;
				}
			});
			
			v.component.render (renderer, width, height);
		}
	}


	public static void disposeRenderer (Item item, Object info, de.grogra.pf.ui.Context ctx)
	{
		if (ctx.getPanel () instanceof View)
		{
			View v = (View) ctx.getPanel ();
			v.getViewComponent ().disposeRenderer (null);
			v.repaint (ViewComponent.ALL | ViewComponent.CHANGED);
		}
	}

	public final void setDimensions (float magnitude)
	{
		setDimensions (magnitude, magnitude * 1e-6f, magnitude * 1e-2f);
	}


	public final void setDimensions (float magnitude, float epsilon,
									 float visualEpsilon)
	{
		setEpsilon (epsilon);
		setVisualEpsilon (visualEpsilon);
		setMagnitude (magnitude);
	}


	public void setEpsilon (float epsilon)
	{
		this.epsilon = epsilon;
		epsilonSquared = epsilon * epsilon;
	}


	public void setVisualEpsilon (float visualEpsilon)
	{
		this.visualEpsilon = visualEpsilon;
		visualEpsilonSquared = visualEpsilon * visualEpsilon;
	}


	public void setMagnitude (float magnitude)
	{
		this.magnitude = magnitude;
		magnitudeSquared = magnitude * magnitude;
	}


	public float getEpsilonSquared()
	{
		return epsilonSquared;
	}


	public float getVisualEpsilonSquared ()
	{
		return visualEpsilonSquared;
	}


	public float getMagnitudeSquared ()
	{
		return magnitudeSquared;
	}


	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
	}


	@Override
	public int getStamp ()
	{
		return stamp;
	}

	
	@Override
	public Manageable manageableReadResolve ()
	{
		return this;
	}
	
	@Override
	public Object manageableWriteReplace ()
	{
		return this;
	}

	public boolean isInVisibleLayer (Object o, boolean asNode, GraphState gs)
	{
		int layer = gs.getIntDefault (o, asNode, Attributes.LAYER, 0);
		return (layer < 0) || (layer >= visibleLayers.length) || visibleLayers[layer];
	}

	
	public boolean isInVisibleEdge (Object o, boolean asNode, GraphState gs)
	{
//		if(o instanceof Node) return false;
		int edge = ((Edge)o).getEdgeBits ();
		int edgeId = -1; 
		for (int i = 0; i < EDGE_TO_ID_MAP.length; i++)
		{
			if(EDGE_TO_ID_MAP[i]==edge) edgeId = i;
		}

		return (edgeId < 0) || (edgeId >= visibleEdges.length) || visibleEdges[edgeId];
	}
	
	public Dimension getSize ()
	{
		UIToolkit ui = UIToolkit.get (this);
		return new Dimension (ui.getWidth (getViewComponent ().getComponent ()), ui.getHeight (getViewComponent ().getComponent ()));
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field graphDescriptor$FIELD;
	public static final Type.Field visibleLayers$FIELD;
	public static final Type.Field visibleEdges$FIELD;
	public static final Type.Field epsilon$FIELD;
	public static final Type.Field visualEpsilon$FIELD;
	public static final Type.Field magnitude$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (View representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 6;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					((View) o).setEpsilon (value);
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((View) o).setVisualEpsilon (value);
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((View) o).setMagnitude (value);
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					return ((View) o).getEpsilon ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((View) o).getVisualEpsilon ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((View) o).getMagnitude ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((View) o).graphDescriptor = (GraphDescriptor) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((View) o).visibleLayers = (boolean[]) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((View) o).visibleEdges = (boolean[]) value;
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
					return ((View) o).graphDescriptor;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((View) o).visibleLayers;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((View) o).visibleEdges;
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (View.class);
		graphDescriptor$FIELD = Type._addManagedField ($TYPE, "graphDescriptor", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (GraphDescriptor.class), null, Type.SUPER_FIELD_COUNT + 0);
		visibleLayers$FIELD = Type._addManagedField ($TYPE, "visibleLayers", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (boolean[].class), null, Type.SUPER_FIELD_COUNT + 1);
		visibleEdges$FIELD = Type._addManagedField ($TYPE, "visibleEdges", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (boolean[].class), null, Type.SUPER_FIELD_COUNT + 2);
		epsilon$FIELD = Type._addManagedField ($TYPE, "epsilon", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		visualEpsilon$FIELD = Type._addManagedField ($TYPE, "visualEpsilon", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 4);
		magnitude$FIELD = Type._addManagedField ($TYPE, "magnitude", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 5);
		$TYPE.validate ();
	}

	public float getEpsilon ()
	{
		return epsilon;
	}

	public float getVisualEpsilon ()
	{
		return visualEpsilon;
	}

	public float getMagnitude ()
	{
		return magnitude;
	}

//enh:end

}
