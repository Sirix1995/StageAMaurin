
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

import java.io.File;
import java.util.*;

public class PathListIterator implements Iterator
{
	private final String list;
	private String next;
	private int pos = 0;


	public PathListIterator (String list)
	{
		this.list = list;
	}


	public void remove ()
	{
		throw new UnsupportedOperationException ();
	}


	public boolean hasNext()
	{
		getNext ();
		return next != null;
	}


	public Object next ()
	{
		return nextPath ();
	}


	public File nextPath ()
	{
		getNext ();
		String n = next;
		if (n == null)
		{
			throw new NoSuchElementException ();
		}
		next = null;
		return new File (n);
	}


	private void getNext ()
	{
		while ((next == null) && (list != null) && (pos < list.length ()))
		{
			int p = list.indexOf (File.pathSeparatorChar, pos);
			if (p < 0)
			{
				p = list.length ();
			}
			String n = list.substring (pos, p).trim ();
			if (n.length () > 0)
			{
				next = n;
			}
			pos = p + 1;
		}
	}

}
