
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

package de.grogra.graph.impl;

import de.grogra.reflect.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;

final class NodeData extends XData
{
	SharedObjectProvider sop = null;
	XClass cls;
	Node.NType ntype;

	/**
	 * The name of the node. Not necessarily unique.
	 * 
	 * @see Node#getName()
	 */
	String name = null;

	ObjectList<SharedObjectReference> refs = null;

	
	NodeData ()
	{
	}


	@Override
	public void init (XClass c)
	{
		if (cls == c)
		{
			return;
		}
		if (cls != null)
		{
			throw new IllegalStateException ("XClass has already been set.");
		}
		cls = c;
		try
		{
			ntype = (Node.NType) Reflection.findFieldInClasses (c, "$TYPE").getObject (null);
		}
		catch (Exception e)
		{
		}
		super.init (c);
	}

}
