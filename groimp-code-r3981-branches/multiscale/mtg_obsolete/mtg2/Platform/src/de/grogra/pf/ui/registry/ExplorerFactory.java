
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

package de.grogra.pf.ui.registry;

import java.net.*;
import java.io.*;
import javax.swing.JSplitPane;
import javax.swing.tree.TreePath;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import de.grogra.icon.*;
import de.grogra.persistence.*;
import de.grogra.graph.impl.*;
import de.grogra.reflect.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.registry.expr.Resource;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.edit.*;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.util.ComponentWrapperImpl;
import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;

public class ExplorerFactory extends PanelFactory
{
	private static final int OBJ_DESCRIBES_MASK = 1 << PanelFactory.USED_BITS;
	public static final int USED_BITS = PanelFactory.USED_BITS + 1;

	// boolean objDescribes
	//enh:field type=bits(OBJ_DESCRIBES_MASK)

	String baseName;
	//enh:field

	String objectDir;
	//enh:field

	String factoryDir;
	//enh:field

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field objDescribes$FIELD;
	public static final NType.Field baseName$FIELD;
	public static final NType.Field objectDir$FIELD;
	public static final NType.Field factoryDir$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ExplorerFactory.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ExplorerFactory) o).baseName = (String) value;
					return;
				case 1:
					((ExplorerFactory) o).objectDir = (String) value;
					return;
				case 2:
					((ExplorerFactory) o).factoryDir = (String) value;
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
					return ((ExplorerFactory) o).baseName;
				case 1:
					return ((ExplorerFactory) o).objectDir;
				case 2:
					return ((ExplorerFactory) o).factoryDir;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ExplorerFactory ());
		$TYPE.addManagedField (objDescribes$FIELD = new NType.BitField ($TYPE, "objDescribes", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, OBJ_DESCRIBES_MASK));
		$TYPE.addManagedField (baseName$FIELD = new _Field ("baseName", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (objectDir$FIELD = new _Field ("objectDir", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.addManagedField (factoryDir$FIELD = new _Field ("factoryDir", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 2));
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
		return new ExplorerFactory ();
	}

//enh:end

	ExplorerFactory ()
	{
		this (null, null, null, null);
	}


	ExplorerFactory (String name, String baseName,
					 String factoryDir, String objectDir)
	{
		super (name, null);
		this.baseName = baseName;
		this.factoryDir = factoryDir;
		this.objectDir = objectDir;
		bits |= OBJ_DESCRIBES_MASK;
	}


	private static class ExplorerTree extends UISubTree implements Command
	{
		ExplorerTree (UITree tree, TreePath pathToRoot)
		{
			super (tree, pathToRoot);
		}

		@Override
		public void eventOccured
			(final Object node, java.util.EventObject event)
		{
			if (!(event instanceof ActionEditEvent))
			{
				super.eventOccured (node, event);
				return;
			}
			ActionEditEvent ae = (ActionEditEvent) event;
			uiTree.invoke (node, UIToolkit.EXPLORER_ACTION,
						   event);
			if (ae.isConsumed ())
			{
				return;
			}
			if (ACTION_DELETE.equals (ae.getName ())
				&& (node instanceof Item)
				&& ((Item) node).isUserItem ())
			{
				ae.consume ();
				delete ((Item) node);
			}
			else if (ACTION_OPEN.equals (ae.getName ()))
			{
				if (node instanceof Showable)
				{
					ae.consume ();
					((Showable) node).show (ae);
				}
				else if (node instanceof ObjectItem)
				{
					ae.consume ();
					select ((ObjectItem) node);
				}
			}
			else if (ACTION_RENAME.equals (ae.getName ()))
			{
				if ((node instanceof ObjectItem) && ((Item) node).isUserItem ())
				{
					ae.consume ();
					Object p = ae.getParameter ();
					if ((p instanceof String) && (((String) p).length () > 0))
					{
						rename ((Item) node, (String) p);
					}
				}
			}
		}
		
		private void rename (Item node, String newName)
		{
			UI.executeLockedly
				(node.getRegistry ().getProjectGraph (),
				 true, this, new Object[] {node, newName}, getContext (), JobManager.ACTION_FLAGS);
		}

		private void delete (Item node)
		{
			UI.executeLockedly
				(node.getRegistry ().getProjectGraph (),
				 true, this, node, getContext (), JobManager.ACTION_FLAGS);
		}

		public String getCommandName ()
		{
			return null;
		}
		
		public void run (Object arg, Context c)
		{
			if (arg instanceof Item)
			{
				Item item = (Item) arg;
				if (item.getBranch () == null)
				{
					item.deactivate ();
					item.remove ();
				}
			}
			else
			{
				Object[] args = (Object[]) arg;
				Item item = (Item) args[0];
				String name = (String) args[1];
				Node.name$FIELD.setObject (item, null, name, item.getTransaction ());
			}
		}
		
		private void select (ObjectItem node)
		{
			Object o = node.getObject ();
			Selection sel;
			SharedObjectProvider p;
			if (o instanceof Showable)
			{
				((Showable) o).show (getContext ());
				return;
			}
			else if (o instanceof Selectable)
			{
				sel = ((Selectable) o).toSelection (getContext ());
			}
			else if ((o instanceof Node)
					 && (((Node) o).getPersistenceManager () != null))
			{
				sel = new NodeSelection
					(getContext (), (Node) o,
					 new PersistenceField[] {new IndirectField (((Node) o).getNType ())},
				 	 null, null, null, node, o instanceof IconSource);
			}
			else if ((o instanceof Shareable)
					 && ((p = ((Shareable) o).getProvider ())
						 instanceof SharedObjectNode))
			{
				sel = new NodeSelection
					(getContext (), (Node) p,
					 new PersistenceField[] {SharedObjectNode.object$FIELD}, null,
					 new PropertyEditor[]
						{PropertyEditor.findEditor
						 (getContext ().getWorkbench (),
						  (o instanceof Manageable) ? ((Manageable) o).getManageableType ()
						  : (Type) ClassAdapter.wrap (o.getClass ()),
						  false)},
					 null, node, o instanceof IconSource);
			}
			else
			{
				return;
			}
			UIProperty.WORKBENCH_SELECTION.setValue (getContext (), sel);
		}
	}

	public static UITree createExplorerTree (UITree tree, TreePath pathToRoot)
	{
		return new ExplorerTree (tree, pathToRoot);
	}

	
	public static Panel createHelpExplorer (Context ctx, Map params)
	{
		final UIToolkit ui = UIToolkit.get (ctx);
		final Object viewer = ui.createTextViewer
			(null, null, null, null, true);
		Panel p = ui.createPanel (ctx, null, params);
		Item help = Item.resolveItem (ctx.getWorkbench (), "/help");
		UITree reg = new RegistryAdapter (ctx, ctx.getWorkbench ().getRegistry ())
		{
			@Override
			public Object invoke
				(Object node, String method, Object arg)
			{
				if (UIToolkit.EXPLORER_ACTION.equals (method)
					&& (node instanceof Resource))
				{
					ui.setContent (viewer, (java.net.URL) ((Resource) node)
								   .evaluate (this, new StringMap ().putBoolean ("pluginURL", true)));
					UI.consume (arg);
					return null;
				}
				return super.invoke (node, method, arg);
			}
		};
		ComponentWrapper t = ui.createTree
			(createExplorerTree (reg, new TreePath (help.getPath ())));
		Object split = ui.createSplitContainer (JSplitPane.HORIZONTAL_SPLIT);
		ui.addComponent (split, ui.createScrollPane (t.getComponent ()), null);
		ui.addComponent (split, viewer, null);
		p.setContent (new ComponentWrapperImpl (split, t));
		return p;
	}


	public static void installHelpTOCs (Registry reg)
	{
		for (Node c = reg.getPluginDirectory ().getBranch ();
			 c != null; c = c.getSuccessor ())
		{
			if (c instanceof PluginDescriptor)
			{
				URL u = (((PluginDescriptor) c).getPluginClassLoader ()).getPluginResource ("doc/toc.hhc");
				if (u != null)
				{
					try
					{
						URLConnection uc = u.openConnection ();
						uc.connect ();
						Reader in = new InputStreamReader (uc.getInputStream (), "ISO-8859-1");
						installHelpTOC (in, (PluginDescriptor) c);
					}
					catch (IOException e)
					{
						e.printStackTrace ();
					}
				}
			}
		}
	}

	
	private static void installHelpTOC (Reader in, final PluginDescriptor plugin)
		throws IOException
	{
		final Item dir = plugin.getRegistry ().getItem ("/help");
		new ParserDelegator ().parse (in, new HTMLEditorKit.ParserCallback ()
		{
			final ObjectList stack = new ObjectList ();
			private String name;
			private String address;
			private Item current;
			private boolean sitemap;

			@Override
			public void handleSimpleTag (HTML.Tag t, MutableAttributeSet a, int pos)
			{
				if (t == HTML.Tag.PARAM)
				{
					if (a.containsAttribute (HTML.Attribute.NAME, "Name"))
					{
						name = String.valueOf (a.getAttribute (HTML.Attribute.VALUE));
					}
					else if (a.containsAttribute (HTML.Attribute.NAME, "Local"))
					{
						address = String.valueOf (a.getAttribute (HTML.Attribute.VALUE));
					}
				}
			}

			@Override
			public void handleStartTag (HTML.Tag t, MutableAttributeSet a, int pos)
			{
				if (t == HTML.Tag.UL)
				{
					if (current != null)
					{
						Item d = new Directory (null, true);
						d.initPluginDescriptor (plugin);
						d.setDescription (Described.NAME, current.getDescription (Described.NAME));
						if (current.getPredecessor () != null)
						{
							current.getPredecessor ().setSuccessor (d);
						}
						else
						{
							current.getAxisParent ().setBranch (d);
						}
						d.setBranch (current);
						stack.push (d);
					}
					else
					{
						stack.push (current);
					}
				}
				else if (t == HTML.Tag.OBJECT)
				{
					name = null;
					address = null;
					sitemap = a.containsAttribute (HTML.Attribute.TYPE, "text/sitemap");
				}
			}

			@Override
			public void handleEndTag (HTML.Tag t, int pos)
			{
				if (t == HTML.Tag.UL)
				{
					current = (Item) stack.pop ();
				}
				else if ((t == HTML.Tag.OBJECT) && sitemap)
				{
					Item i = new Resource ("doc/" + address);
					i.initPluginDescriptor (plugin);
					i.setDescription (Described.NAME, name);
					i.setDescription (Described.ICON,
									  UI.I18N.getObject ("registry.file.Icon"));
					if (current == null)
					{
						dir.add (i);
					}
					else
					{
						current.setSuccessor (i);
					}
					current = i;
				}
			}
		}, true);
	}


	@Override
	protected Panel configure (Context ctx, Panel p, Item menu)
	{
		if (p == null)
		{
			Item i = Item.resolveItem (ctx.getWorkbench (), objectDir);
			if (i == null)
			{
				throw new NullPointerException (objectDir + " cannot be resolved");
			}
			p = UIToolkit.get (ctx).createPanel (ctx, null, this);
			ComponentWrapper t = UIToolkit.get (ctx).createTree
				(createExplorerTree (new RegistryAdapter (ctx, i.getRegistry ()),
									 new TreePath (i.getPath ())));
			p.setContent (new ComponentWrapperImpl (UIToolkit.get (ctx).createScrollPane (t.getComponent ()), t));
		}
		if (menu != null)
		{
			UI.setMenu (p, menu, this);
		}
		return p;
	}

}
