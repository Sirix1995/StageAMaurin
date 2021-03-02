
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

package de.grogra.pf.ui.edit;

import de.grogra.graph.impl.Node.NType;
import de.grogra.icon.IconSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.registry.expr.ObjectExpr;
import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.edit.PropertyEditorTree.Node;
import de.grogra.pf.ui.edit.PropertyEditorTree.PropertyNode;
import de.grogra.pf.ui.tree.HierarchyFlattener;
import de.grogra.pf.ui.tree.LinkResolver;
import de.grogra.pf.ui.tree.RegistryAdapter;
import de.grogra.pf.ui.tree.UITreePipeline;
import de.grogra.pf.ui.util.WidgetAdapter;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.StringMap;
import de.grogra.util.Tree;
import de.grogra.xl.lang.ObjectToBoolean;

public class PolyEditor extends PropertyEditor
{
	private static final int INCLUDE_FACTORIES_MASK = 1 << PropertyEditor.USED_BITS;
	public static final int USED_BITS = PropertyEditor.USED_BITS + 1;

	String directory;
	//enh:field

	// boolean includeFactories
	//enh:field type=bits(INCLUDE_FACTORIES_MASK)

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field directory$FIELD;
	public static final NType.Field includeFactories$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (PolyEditor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((PolyEditor) o).directory = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((PolyEditor) o).directory;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new PolyEditor ());
		$TYPE.addManagedField (directory$FIELD = new _Field ("directory", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (includeFactories$FIELD = new NType.BitField ($TYPE, "includeFactories", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, INCLUDE_FACTORIES_MASK));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new PolyEditor ();
	}

//enh:end

	public PolyEditor ()
	{
		super (null);
		bits |= INCLUDE_FACTORIES_MASK;
	}


	@Override
	public boolean isNullAllowed ()
	{
		return true;
	}

	
	@Override
	public Node createNodes (final PropertyEditorTree pt, final Property p, String label)
	{
		if (pt.isMenu ())
		{
			return null;
		}
		final UITreePipeline tree = new UITreePipeline ();
		tree.add (new LinkResolver ());
		tree.add (new HierarchyFlattener (new ObjectToBoolean ()
			{
				public boolean evaluateBoolean (Object node)
				{
					return PolyEditor.this.accept ((Item) node, p);
				}
			}, true));
		tree.initialize (new RegistryAdapter (p.getContext ()),
						 Item.resolveItem (p.getWorkbench (), directory), null);
		final WidgetAdapter w = new WidgetAdapter
			(p.getToolkit ().createTreeChoiceWidget (tree), p.getContext ());
		PropertyNode poly = pt.new PropertyNode (p, w, label)
		{
			Object oldValue = this;
			Object currentNode;
			boolean updateSubEditor = false;
			boolean updateCurrentNode = false;
			PropertyEditor currentSubEditor;
			Node subEditorNode;


			@Override
			protected ComponentWrapper createComponent ()
			{
				w.addPropertyChangeListener (this);
				w.updateValue (toWidget (p.getValue (), false));
				return w;
			}
			
			
			private void removeSubEditor ()
			{
				if (subEditorNode != null)
				{
					pt.remove (parent, pt.getIndexOfChild (this) + 1);
					subEditorNode = null;
				}
				currentSubEditor = null;
			}
			

			private void setSubEditor (final Object value)
			{
				final PropertyEditor e = PropertyEditor.findNonpolyEditor (p.getWorkbench (), value, false);
				if (e != currentSubEditor)
				{
					removeSubEditor ();
					if ((e != null) && (e != PolyEditor.this))
					{
						subEditorNode = e.createNodes (pt, p, null);
						if (subEditorNode != null)
						{
							PropertyNode g = pt.new PropertyNode (p, null, null);
							if (value instanceof IconSource)
							{
								g.icon = (IconSource) value;
								g.text = (String) e.getDescription (NAME);
							}
							else if (Reflection.equal (value.getClass (), e.getPropertyType ()))
							{
								g.described = e;
							}
							else
							{
								Item i = PropertyEditor.findMostSpecificItem
									(e, ClassAdapter.wrap (value.getClass ()));
								g.described = (i != null) ? i : e;
							}
							g.addChild (subEditorNode);
							subEditorNode = g;
							if (parent == null)
							{
								next = subEditorNode;
							}
							else
							{
								pt.insert
									(parent, pt.getIndexOfChild (this) + 1, subEditorNode, null);
							}
						}
					}
					currentSubEditor = e;
				}
			}

			
			@Override
			protected Object toWidget (Object propertyValue)
			{
				return toWidget (propertyValue, true);
			}
			
			
			private Object toWidget (final Object propertyValue, boolean set)
			{
				class Helper implements ObjectToBoolean, java.util.Comparator
				{
					public boolean evaluateBoolean (Object node)
					{
						Object i = ((UITreePipeline.Node) node).getNode ();
						return (i instanceof ObjectItem)
							&& ((ObjectItem) i).isObjectFetched ()
							&& (((ObjectItem) i).getObject () == propertyValue);
					}


					public int compare (Object o1, Object o2)
					{
						Object i = ((UITreePipeline.Node) o1).getNode ();
						if ((i instanceof ObjectExpr)
							&& !((ObjectExpr) i).isAlias ())
						{
							Type t1 = ((ObjectExpr) i).getObjectType ();
							if (t1.isInstance (propertyValue))
							{
								if (o2 == null)
								{
									return 1;
								}
								Type t2 = ((ObjectExpr)
										   ((UITreePipeline.Node) o2).getNode ())
									.getObjectType ();
								if (Reflection.equal (t1, t2))
								{
									return 0;
								}
								if (Reflection.isAssignableFrom (t2, t1))
								{
									return 1;
								}
							}
						}
						return -1;
					}
				}

				Object old = oldValue;
				oldValue = propertyValue;
				if ((old != propertyValue) || updateCurrentNode)
				{
					Helper h = new Helper ();
					Object i = Tree.findFirst (tree, tree.getRoot (), h);
					updateSubEditor = (i == null) && ((bits & INCLUDE_FACTORIES_MASK) != 0);
					if (updateSubEditor)
					{
						i = Tree.findMax (tree, tree.getRoot (), h);
					}
					else
					{
						removeSubEditor ();
					}
					updateCurrentNode = false;
					currentNode = i;
				}
				if (updateSubEditor && set)
				{
					updateSubEditor = false;
					setSubEditor (propertyValue);
				}
				return currentNode;
			}
			

			@Override
			protected Object fromWidget (Object widgetValue)
			{
				Item i = (Item) ((UITreePipeline.Node) widgetValue).getNode ();
				Object v;
				if (i instanceof ObjectExpr)
				{
					StringMap args = UI.getArgs (p.getContext (), null)
						.putObject ("oldValue", oldValue);
					v = ((ObjectExpr) i).evaluate (p.getWorkbench (), args);
					if (v == null)
					{
						return oldValue;
					}
					tree.update ();
					updateCurrentNode = true;
					updateSubEditor = true;
				}
				else
				{
					if (i instanceof ObjectItem)
					{
						v = ((ObjectItem) i).getObject ();
					}
					else
					{
						v = i;
					}
					updateSubEditor = false;
				}
				currentNode = widgetValue;
				oldValue = v;
				removeSubEditor ();
				return v;
			}
		};
		poly.toWidget (p.getValue ());
		return poly;
	}


	protected boolean accept (Item node, Property p)
	{
		return node.isDirectory ()
			|| ((node instanceof ObjectItem)
				&& Reflection.isAssignableFrom
				(p.getType (), ((ObjectItem) node).getObjectType ()))
			|| (((bits & INCLUDE_FACTORIES_MASK) != 0)
				&& (node instanceof ObjectExpr)
				&& Reflection.isAssignableFrom
				(p.getType (), ((ObjectExpr) node).getObjectType ()));
	}

}
