
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

import java.awt.Component;

import javax.swing.JSplitPane;

import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Panel;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.OptionsSelection;
import de.grogra.pf.ui.event.ActionEditEvent;
import de.grogra.pf.ui.tree.HierarchyFlattener;
import de.grogra.pf.ui.tree.RegistryAdapter;
import de.grogra.pf.ui.tree.UITreePipeline;
import de.grogra.pf.ui.tree.UITreePipeline.Node;
import de.grogra.pf.ui.util.ComponentWrapperImpl;
import de.grogra.util.DisposableWrapper;

public class OptionExplorerFactory extends PanelFactory
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new OptionExplorerFactory ());
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
		return new OptionExplorerFactory ();
	}

//enh:end

	OptionExplorerFactory ()
	{
		super ();
	}


	@Override
	protected Panel configure (Context ctx, Panel p, Item menu)
	{
		if (p == null)
		{
			Registry rr = ctx.getWorkbench ().getRegistry ().getRootRegistry ();
			UITreePipeline tp = new UITreePipeline ();
			tp.add (new HierarchyFlattener (null, false)
			{
				@Override
				protected boolean hasContent (Node node)
				{
					return ((Item) node.getNode ()).hasEditableOptions ();
				}

				@Override
				protected boolean flattenGroup (Node node)
				{
					return (node.parent != null)
						&& !((Item) node.getNode ()).isOptionCategory ();
				}
			});
			final UIToolkit ui = UIToolkit.get (ctx);
			final Object split = ui.createSplitContainer (JSplitPane.HORIZONTAL_SPLIT);
			final Object container = ui.createContainer (1, 1, 1);
			final DisposableWrapper dw = new DisposableWrapper ();
			
			class OptionTree extends RegistryAdapter
			{
				private Object editorComponent;
				
				OptionTree (Context c, Registry r)
				{
					super (c, r);
				}

				@Override
				public int getType (Object node)
				{
					int t = super.getType (node);
					return (t == NT_UNDEFINED) ? NT_ITEM : t;
				}

				@Override
				public void eventOccured (final Object node,
										  java.util.EventObject event)
				{
					if (!(event instanceof ActionEditEvent))
					{
						super.eventOccured (node, event);
						return;
					}
					Item item = (Item) node;
					if (((ActionEditEvent) event).isConsumed ()
						|| !item.hasEditableOptions ())
					{
						return;
					}
					((ActionEditEvent) event).consume ();
					setItem (item);
				}
				
				void setItem (Item item)
				{
					if (dw.disposable != null)
					{
						ui.removeComponent (editorComponent);
						dw.dispose ();
						editorComponent = null;
					}
					ComponentWrapper w = new OptionsSelection
						 (getContext (), item, true)
						 .createPropertyEditorComponent ();
					if (w != null)
					{
						dw.disposable = w;
						ui.addComponent (container, editorComponent = w.getComponent (), null);
					}
					ui.revalidate (container);
				}
			}

			tp.initialize (new OptionTree (ctx, rr), rr.getRoot (), this);

			p = ui.createPanel (ctx, dw, this);
			ComponentWrapper t = ui.createTreeInSplit (tp, split);
//			ComponentWrapper t = ui.createTree (tp);
			ui.addComponent (split, ui.createScrollPane ((Component) t.getComponent()), null);
			ui.addComponent (split, container, null);
			p.setContent (new ComponentWrapperImpl (split, t));
		}
		if (menu != null)
		{
			UI.setMenu (p, menu, this);
		}
		return p;
	}
	
}
