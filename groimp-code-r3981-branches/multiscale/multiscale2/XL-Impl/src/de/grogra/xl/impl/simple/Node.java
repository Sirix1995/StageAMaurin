
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package de.grogra.xl.impl.simple;

import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.query.EdgeDirection;

public abstract class Node
{
	int index = -1;

	public abstract void getAdjacentNodes (ObjectConsumer<? super Node> cons, boolean outgoing);

	public abstract void addEdgeBitsTo (Node target, int bits);

	public abstract void removeEdgeBitsTo (Node target, int bits);

	public abstract int getEdgeBitsTo (Node target);
}
