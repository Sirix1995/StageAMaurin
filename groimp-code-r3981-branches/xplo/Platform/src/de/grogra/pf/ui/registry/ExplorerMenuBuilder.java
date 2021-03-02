
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

import javax.swing.tree.TreePath;
import de.grogra.xl.lang.ObjectToBoolean;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.event.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.tree.UITreePipeline.*;

public final class ExplorerMenuBuilder implements Transformer, ObjectToBoolean
{

	public void initialize (UITreePipeline pipeline)
	{
	}

	
	public void dispose ()
	{	
	}

	
	public boolean isAffectedBy (TreePath path)
	{
		return false;
	}

	
	public void transform (Node root)
	{
		Node n = root.getChild ("object");
		String s;
		final Item fdir, odir;
		final UITreePipeline p = n.getPipeline ();
		s = (String) p.getParameter ("baseName", null);
		final String base = (s != null) ? s : "Unnamed";
		final boolean objDesribes
			= Boolean.TRUE.equals (p.getParameter ("objDescribes", null));
		if ((n == null) || ((n = n.getChild ("new")) == null)
			|| ((s = (String) p.getParameter ("factoryDir", null)) == null)
			|| ((fdir = Item.resolveItem (p, s)) == null)
			|| ((s = (String) p.getParameter ("objectDir", null)) == null)
			|| ((odir = Item.resolveItem (p, s)) == null))
		{
			return;
		}
		
		class ExplorerMenu extends RegistryAdapter implements Command
		{
			ExplorerMenu ()
			{
				super (p.getContext ());
			}
			
			@Override
			public void eventOccured (Object node, java.util.EventObject event)
			{
				if (!((event instanceof ActionEditEvent)
					  && (node instanceof ItemFactory)))
				{
					return;
				}
				UI.consume (event);
				
				UI.executeLockedly (getRegistry ().getProjectGraph (),
									true, this, node, getContext (), JobManager.ACTION_FLAGS);
			}
			
			public String getCommandName ()
			{
				return null;
			}
			
			public void run (Object node, Context c)
			{
				Item i = ((ItemFactory) node).createItem (c);
				if (i == null)
				{
					return;
				}
				if (i instanceof ObjectItem)
				{
					((ObjectItem) i).setObjDescribes (objDesribes);
				}
				odir.addUserItemWithUniqueName (i, base);
			}
		}

		Node r = p.copyTree (new ExplorerMenu (), fdir, this);
		n.addChild (r.children);
		r.children = null;
		r.dispose ();
	}


	public boolean evaluateBoolean (Object node)
	{
		return ((Item) node).isDirectory () || (node instanceof ItemFactory);
	}

}
