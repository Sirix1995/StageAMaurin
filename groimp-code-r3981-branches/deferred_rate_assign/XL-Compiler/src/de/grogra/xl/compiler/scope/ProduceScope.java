
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

package de.grogra.xl.compiler.scope;

import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.ExpressionFactory;
import de.grogra.xl.util.ObjectList;

public class ProduceScope extends InstanceScope
{
	private ObjectList<ExpressionFactory> producerStack = new ObjectList<ExpressionFactory> ();


	public ProduceScope (BlockScope enclosing, Local producer)
	{
		super (enclosing, new Block (), producer, Members.FIELD | Members.METHOD, false);
	}

	
	public static ProduceScope get (Scope s)
	{
		while (!(s instanceof ProduceScope))
		{
			if (!(s instanceof BlockScope))
			{
				return null;
			}
			s = s.getEnclosingScope ();
		}
		return (ProduceScope) s;
	}


	public void push ()
	{
		producerStack.push (getInstance ());
	}


	public void pop ()
	{
		setInstance (producerStack.pop ());
	}
	
}
