
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

public final class ConnectionTransformation extends Entry
{
	public Object source;
	public Object target;
	public Operator operator;


	public void setSource (Object source)
	{
		this.source = source;
		hashCode = (source != null) ? source.hashCode () : 0;
	}


	@Override
	protected void clear ()
	{
		source = null;
	}
	

	@Override
	protected void copyValue (Entry e)
	{
		ConnectionTransformation n = (ConnectionTransformation) e;
		target = n.target;
		operator = n.operator;
	}
	
	
	@Override
	protected boolean keyEquals (Entry e)
	{
		ConnectionTransformation n = (ConnectionTransformation) e;
		return (source == n.source) || ((source != null) && source.equals (n.source));
	}
	
	
	@Override
	public String toString ()
	{
		return "ConnTrans@" + Integer.toHexString (hashCode ())
			+ '[' + source + ',' + operator + ',' + target + ']';
	}

}
