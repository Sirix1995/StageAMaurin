
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

package de.grogra.imp.edit;

import de.grogra.graph.Attribute;
import de.grogra.graph.AttributeAccessor;
import de.grogra.graph.AttributeChangeEvent;
import de.grogra.graph.AttributeChangeListener;
import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.ByteAttribute;
import de.grogra.graph.ChangeBoundaryListener;
import de.grogra.graph.CharAttribute;
import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.GraphUtils;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.LongAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.Path;
import de.grogra.graph.ShortAttribute;
import de.grogra.imp.View;
import de.grogra.imp.registry.ToolFactory;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.UIProperty;
import de.grogra.reflect.FieldChain;
import de.grogra.util.Disposable;
import de.grogra.util.DisposableEventListener;

/**
 * A <code>Tool</code> represents a tool for interactive manipulation
 * of objects in a 2D- or 3D-view. A tool is composed of a set of
 * tool graphs, each represented by its root node of (a subclass of) class
 * {@link de.grogra.imp.edit.ToolRoot}. Each tool graph is drawn as
 * part of the 2D- or 3D-view.
 * <p>
 * A <code>Tool</code> operates on a single object, the <i>tool target</i>.
 * 
 * @author Ole Kniemeyer
 */
public final class Tool implements Disposable, AttributeChangeListener, ChangeBoundaryListener
{
	private final ToolRoot[] roots;
	private final Object object;
	private final boolean asNode;
	private GraphState state;
	private View view;
	private int toolCount;


	/**
	 * Creates a new <code>Tool</code>.
	 * 
	 * @param object the tool target
	 * @param asNode <code>true</code> iff <code>object</code> is a node
	 * @param roots the set of tool roots
	 * @see Tool
	 */
	public Tool (Object object, boolean asNode, ToolRoot[] roots)
	{
		this.object = object;
		this.asNode = asNode;
		this.roots = roots;
	}


	public void initialize (View view)
	{
		this.view = view;
		this.state = view.getWorkbenchGraphState ();
		toolCount = roots.length;
		for (int i = toolCount - 1; i >= 0; i--)
		{
			if (roots[i] != null)
			{
				if (roots[i].initialize (this))
				{
					continue;
				}
				roots[i].dispose ();
			}
			if (i < --toolCount)
			{
				System.arraycopy (roots, i + 1, roots, i, toolCount - i);
			}
		}
		state.getGraph ().addAttributeChangeListener (object, asNode, this);
		state.getGraph ().addChangeBoundaryListener (this);
	}


	/**
	 * Returns the view in which this tool is displayed.
	 * 
	 * @return the view
	 */
	public View getView ()
	{
		return view;
	}


	/**
	 * Returns this tool's target.
	 * 
	 * @return the tool target
	 */
	public Object getObject ()
	{
		return object;
	}


	/**
	 * Determines whether the tool target is a node or an edge.
	 * 
	 * @return <code>true</code> iff the tool target is a node
	 */
	public boolean isNode ()
	{
		return asNode;
	}


	/**
	 * Returns the {@link GraphState} of the workbench.
	 * 
	 * @return the graph state of the workbench
	 */
	public GraphState getGraphState ()
	{
		return state;
	}


	/**
	 * Returns the number of tool roots. This number may be less than
	 * the length of the array used in the constructor of <code>Tool</code>,
	 * depending on the result of the initializations of each tool root. 
	 * 
	 * @return the number of tool roots
	 */
	public int getToolCount ()
	{
		return toolCount;
	}


	/**
	 * Returns the tool root at position <code>index</code> in the
	 * list of tool roots.
	 * 
	 * @param index the index into the list of tool roots
	 * @return the tool root number <code>index</code>
	 */
	public ToolRoot getRoot (int index)
	{
		return roots[index];
	}


	@Override
	public void dispose ()
	{
		state.getGraph ().removeAttributeChangeListener (object, asNode, this);
		state.getGraph ().removeChangeBoundaryListener (this);
		for (int i = toolCount - 1; i >= 0; i--)
		{
			roots[i].dispose ();
		}
	}


	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer ("Tool[object=");
		b.append (object).append (",roots={");
		for (int i = 0; i < toolCount; i++)
		{
			if (i > 0)
			{
				b.append (',');
			}
			b.append (roots[i]);
		}
		return b.append ("}]").toString ();
	}


	@Override
	public void beginChange (GraphState gs)
	{
	}


	@Override
	public void endChange (GraphState gs)
	{
		if (!gs.containsInTree (object, asNode))
		{
			view.setActiveTool (null);
		}
	}


	@Override
	public int getPriority ()
	{
		return UPDATE_PRIORITY;
	}


	@Override
	public void attributeChanged (AttributeChangeEvent e)
	{
		for (int i = 0; i < toolCount; i++)
		{
			roots[i].attributeChanged (e.getDependentAttributes ());
		}
	}


	public static DisposableEventListener createToolListener
		(final View view, final UIProperty tool)
	{
		if (tool.getValue (view) == null)
		{
			Item i = Item.resolveItem (view.getWorkbench (), tool.getName ());
			if (i != null)
			{
				tool.setValue (view, i.getBranch ());
			}
		}

		DisposableEventListener el = new DisposableEventListener ()
		{
			private Object object;
			private boolean asNode;
			private ToolFactory factory;

			@Override
			public Object getToolFactory() {
				return factory;
			}
			
			@Override
			public void eventOccured (java.util.EventObject event)
			{
				Path p = GraphUtils.cutToGraph
					(ViewSelection.get (view).getFirstPath (ViewSelection.SELECTED),
					 view.getGraph ());
				Object o = (p != null) ? p.getObject (-1) : null;
				boolean n = (p != null) && ((p.getNodeAndEdgeCount () & 1) != 0);
				if (!view.getWorkbenchGraphState ().containsInTree (o, n))
				{
					o = null;
				}
				Object f = tool.getValue (view);
				if ((o != object) || (n != asNode) || (f != factory))
				{
					object = o;
					asNode = n;
					factory = (f instanceof ToolFactory) ? (ToolFactory) f
						: null;
					if ((o != null) && (factory != null))
					{
						view.setActiveTool (factory.createTool (o, n));
					}
					else
					{
						view.setActiveTool (null);
					}
				}
			}


			@Override
			public void dispose ()
			{
				ViewSelection.PROPERTY.removePropertyListener (view, this);
				tool.removePropertyListener (view, this);
			}
		};

		ViewSelection.PROPERTY.addPropertyListener (view, el);
		tool.addPropertyListener (view, el);
		return el;
	}
	
	
	public AttributeAccessor getAccessorOfObject (Attribute a)
	{
		return state.getGraph ().getAccessor (object, asNode, a);
	}
	
	
	public boolean isWritable (Attribute a)
	{
		AttributeAccessor acc = getAccessorOfObject (a);
		return (acc != null) && acc.isWritable (object, state);
	}
	
/*!!
#foreach ($type in $types)
$pp.setType($type)

	public void set${pp.Type}OfObject (${pp.Type}Attribute a, $type value)
	{
		a.set${pp.Type} (object, asNode, value, state);
	}


	public $type get${pp.Type}OfObject (${pp.Type}Attribute a, $type defaultValue)
	{
		return state.get${pp.Type}Default (object, asNode, a, defaultValue);
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public void setBooleanOfObject (BooleanAttribute a, boolean value)
	{
		a.setBoolean (object, asNode, value, state);
	}
// generated
// generated
	public boolean getBooleanOfObject (BooleanAttribute a, boolean defaultValue)
	{
		return state.getBooleanDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setByteOfObject (ByteAttribute a, byte value)
	{
		a.setByte (object, asNode, value, state);
	}
// generated
// generated
	public byte getByteOfObject (ByteAttribute a, byte defaultValue)
	{
		return state.getByteDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setShortOfObject (ShortAttribute a, short value)
	{
		a.setShort (object, asNode, value, state);
	}
// generated
// generated
	public short getShortOfObject (ShortAttribute a, short defaultValue)
	{
		return state.getShortDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setCharOfObject (CharAttribute a, char value)
	{
		a.setChar (object, asNode, value, state);
	}
// generated
// generated
	public char getCharOfObject (CharAttribute a, char defaultValue)
	{
		return state.getCharDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setIntOfObject (IntAttribute a, int value)
	{
		a.setInt (object, asNode, value, state);
	}
// generated
// generated
	public int getIntOfObject (IntAttribute a, int defaultValue)
	{
		return state.getIntDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setLongOfObject (LongAttribute a, long value)
	{
		a.setLong (object, asNode, value, state);
	}
// generated
// generated
	public long getLongOfObject (LongAttribute a, long defaultValue)
	{
		return state.getLongDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setFloatOfObject (FloatAttribute a, float value)
	{
		a.setFloat (object, asNode, value, state);
	}
// generated
// generated
	public float getFloatOfObject (FloatAttribute a, float defaultValue)
	{
		return state.getFloatDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setDoubleOfObject (DoubleAttribute a, double value)
	{
		a.setDouble (object, asNode, value, state);
	}
// generated
// generated
	public double getDoubleOfObject (DoubleAttribute a, double defaultValue)
	{
		return state.getDoubleDefault (object, asNode, a, defaultValue);
	}
// generated
// generated
	public void setObjectOfObject (ObjectAttribute a, Object value)
	{
		a.setObject (object, asNode, value, state);
	}
// generated
// generated
	public Object getObjectOfObject (ObjectAttribute a, Object defaultValue)
	{
		return state.getObjectDefault (object, asNode, a, defaultValue);
	}
//!! *# End of generated code

	public void setSubfield (ObjectAttribute a, FieldChain field, int[] indices, Object value)
	{
		a.setSubfield (object, asNode, field, indices, value, state);
	}
}
