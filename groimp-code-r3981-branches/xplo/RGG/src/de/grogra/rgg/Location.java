
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

package de.grogra.rgg;

import de.grogra.graph.impl.Node;

public final class Location
{
	public static final Location XYZ = null;


	public static final NodeToDouble X = new NodeToDouble ()
	{
		@Override
		public double evaluateDouble (Node n)
		{
			return Library.location (n).x;
		}
	};

	
	public static final NodeToDouble Y = new NodeToDouble ()
	{
		@Override
		public double evaluateDouble (Node n)
		{
			return Library.location (n).y;
		}
	};

	
	public static final NodeToDouble Z = new NodeToDouble ()
	{
		@Override
		public double evaluateDouble (Node n)
		{
			return Library.location (n).z;
		}
	};
	
}
