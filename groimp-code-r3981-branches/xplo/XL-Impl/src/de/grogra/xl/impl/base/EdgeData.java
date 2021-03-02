
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

package de.grogra.xl.impl.base;

import de.grogra.xl.util.EHashMap.Entry;

public final class EdgeData extends Entry
{
	public Object source;
	public Object target;
	
	int add;
	int undirectedAdd;
	int delete;
	int bits;
 

	public void set (Object source, Object target)
	{
		this.source = source;
		this.target = target;
		hashCode = source.hashCode () * 31 + target.hashCode ();
	}
	
	
	@Override
	protected void clear ()
	{
		source = null;
		target = null;
	}
	

	@Override
	protected void copyValue (Entry e)
	{
		EdgeData n = (EdgeData) e;
		if (((add & RuntimeModel.SPECIAL_MASK) != 0)
			&& ((n.add & RuntimeModel.SPECIAL_MASK) != 0)
			&& (((add ^ n.add) & RuntimeModel.SPECIAL_MASK) != 0))
		{
			throw new RuntimeException ();
		}
		add |= n.add;
		if (((undirectedAdd & RuntimeModel.SPECIAL_MASK) != 0)
			&& ((n.undirectedAdd & RuntimeModel.SPECIAL_MASK) != 0)
			&& (((undirectedAdd ^ n.undirectedAdd) & RuntimeModel.SPECIAL_MASK) != 0))
		{
			throw new RuntimeException ();
		}
		undirectedAdd |= n.undirectedAdd;
	}
	
	
	@Override
	protected boolean keyEquals (Entry e)
	{
		EdgeData n = (EdgeData) e;
		return (source == n.source) && (target == n.target);
	}

	
	@Override
	public String toString ()
	{
		return source + " -" + bits + "-> " + target + " +" + add
			+ " -" + delete;
	}
	
}
