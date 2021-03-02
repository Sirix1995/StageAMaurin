
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

package de.grogra.util;

import java.util.Comparator;

public class TreeMapper extends AbstractTreeMapper
{
	private MappedTreeModel mtarget;


	public TreeMapper (javax.swing.tree.TreeModel source, Object root,
					   MappedTreeModel target, Comparator comparator)
	{
		super (source, root, target, comparator);
		this.mtarget = target;
	}


	@Override
	protected Object createNode (Object sourceNode, Object targetParent)
	{
		return mtarget.createNode (sourceNode, targetParent);
	}


	@Override
	protected boolean isImage (Object sourceNode, Object targetNode)
	{
		return mtarget.isImage (sourceNode, targetNode);
	}


	@Override
	protected void nodeRemoved (Object node)
	{
		disposeTree (node);
	}


	@Override
	protected void disposeImpl ()
	{
		super.disposeImpl ();
		disposeTree (target.getRoot ());
	}


	protected void disposeTree (Object node)
	{
		for (int i = target.getChildCount (node) - 1; i >= 0; i--)
		{
			disposeTree (target.getChild (node, i));
		}
		mtarget.disposeNode (node);
	}


	@Override
	protected void targetChanged (Object parent)
	{
		mtarget.treeChanged (parent);
	}

}
