
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

package de.grogra.imp.registry;

import javax.swing.tree.TreePath;

import de.grogra.util.*;
import de.grogra.imp.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.ui.tree.*;
import de.grogra.pf.ui.tree.UITreePipeline.*;

public final class AddViewComponentMenu implements Transformer, EventListener
{
	private UITreePipeline pipeline;

	
	public void initialize (UITreePipeline pipeline)
	{
		this.pipeline = pipeline;
		View.COMPONENT.addPropertyListener (pipeline.getContext (), this);
	}

	
	public void dispose ()
	{	
		View.COMPONENT.removePropertyListener (pipeline.getContext (), this);
	}

	
	public boolean isAffectedBy (TreePath path)
	{
		return false;
	}

	
	public void transform (Node root)
	{
		UITreePipeline p = root.getPipeline ();
		ViewComponent vc = ((View) p.getContext ().getPanel ()).getViewComponent ();
		if (vc != null)
		{
			Item i = vc.getFactory ();
			if ((i != null) && ((i = i.getItem ("menu")) != null))
			{
				Node v = root.getChild ("view");
				if (v == null)
				{
					return;
				}
				v = v.getChild ("viewcomponents");
				if (v == null)
				{
					return;
				}
				p.addTree (v, -1, p.getSource (), i, null, new java.util.Comparator ()
					{
						public int compare (Object o1, Object o2)
						{
							return ((o1 instanceof Item) && (o2 instanceof Item)
									&& ((Item) o1).getName ()
										.equals (((Item) o2).getName ()))
								? 0 : 1;
						}
					});
			}
		}
	}

	
	public void eventOccured (java.util.EventObject e)
	{
		pipeline.update ();
	}

}
