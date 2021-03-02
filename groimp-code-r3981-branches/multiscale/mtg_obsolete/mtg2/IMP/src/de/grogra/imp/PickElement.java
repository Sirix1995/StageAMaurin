
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

package de.grogra.imp;

import de.grogra.graph.*;

public final class PickElement
{
	public final ArrayPath path = new ArrayPath ((Graph) null);
	public double distance;


	public PickElement ()
	{
		super ();
	}


	public void set (Path path, double distance)
	{
		if (path != null)
		{
			this.path.set (path);
		}
		else
		{
			this.path.clear (null);
		}
		this.distance = distance;
	}


	public void set (PickElement info)
	{
		this.path.set (info.path);
		this.distance = info.distance;
	}


	public Path getPath ()
	{
		return path;
	}


	@Override
	public String toString ()
	{
		return String.valueOf (path) + ",distance=" + distance;
	}

}
