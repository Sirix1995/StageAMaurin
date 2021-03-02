
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

package de.grogra.pf.ui.tree;

import javax.swing.tree.*;
import de.grogra.pf.ui.tree.UITreePipeline.Node;

public class LinkResolver implements UITreePipeline.Transformer
{

	public void initialize (UITreePipeline pipeline)
	{	
	}


	public void dispose ()
	{	
	}


	public void transform (Node root)
	{
		transformImpl (root);
	}


	private void transformImpl (Node root)
	{
		if (root.getType () == UINodeHandler.NT_LINK)
		{
			Object o = root.getNodeHandler ().resolveLink (root.getNode ());
			if ((o != null) && root.getPipeline ().getSource ().isAvailable (o))
			{
				Node r = root.getPipeline ().copyTree
					(root.getPipeline ().getSource (), o, null);
				root.replace (r, true);
				root.dispose ();
				root = r;
			}
			else
			{
				root.remove ();
				root.dispose ();
			}
		}
		Node rr;
		for (Node r = (Node) root.children; r != null; r = rr)
		{
			rr = (Node) r.next;
			transformImpl (r);
		}
	}


	public boolean isAffectedBy (TreePath path)
	{
		return false;
	}
}
