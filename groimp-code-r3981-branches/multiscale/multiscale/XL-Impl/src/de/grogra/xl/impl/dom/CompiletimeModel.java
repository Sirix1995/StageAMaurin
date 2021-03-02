
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

package de.grogra.xl.impl.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;

public class CompiletimeModel extends de.grogra.xl.impl.base.CompiletimeModel
{
	public CompiletimeModel ()
	{
		super (RuntimeModel.class.getName ());
	}

	public Type<?> getWrapperTypeFor (Type type)
	{
		if (Reflection.equal (Type.STRING, type))
		{
			return ClassAdapter.wrap (Text.class);
		}
		return null;
	}

	public Type<Node> getNodeType ()
	{
		return ClassAdapter.wrap (Node.class);
	}

	public Type<DOMProducer> getProducerType ()
	{
		return ClassAdapter.wrap (DOMProducer.class);
	}

}
