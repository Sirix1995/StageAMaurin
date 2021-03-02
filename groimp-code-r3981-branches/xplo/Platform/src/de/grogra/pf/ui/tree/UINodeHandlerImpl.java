
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

import de.grogra.util.Utils;

public class UINodeHandlerImpl implements UINodeHandler
{
	public static final UINodeHandler SEPARATOR
		= new UINodeHandlerImpl (NT_SEPARATOR);


	protected final int type;


	public UINodeHandlerImpl (int type)
	{
		this.type = type;
	}


	public boolean nodesEqual (Object a, Object b)
	{
		return (a == b) || ((a != null) && a.equals (b));
	}


	public int getType (Object node)
	{
		return type;
	}

	
	public String getName (Object node)
	{
		return String.valueOf (node);
	}


	public Object resolveLink (Object node)
	{
		return node;
	}


	public boolean isAvailable (Object node)
	{
		return true;
	}


	public boolean isEnabled (Object node)
	{
		return true;
	}


	public Object getDescription (Object node, String type)
	{
		return Utils.isStringDescription (type)
			? getName (node) : null;
	}


	public void eventOccured (Object node, java.util.EventObject event)
	{
	}


	public Object invoke (Object node, String method, Object arg)
	{
		return null;
	}


	public boolean isLeaf (Object node)
	{
		return (getType (node) & NT_DIRECTORY_MASK) == 0;
	}
}
