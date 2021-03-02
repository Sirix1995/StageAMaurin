
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

package de.grogra.graph;

public class EdgePatternImpl implements EdgePattern
{
	public static final EdgePatternImpl TREE = new EdgePatternImpl
		(Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE, 0, true, true);

	public static final EdgePatternImpl FORWARD = new EdgePatternImpl
		(-1, 0, true, true);

	private final boolean checkDirection;
	private final boolean toTarget;
	private final int presentMask;
	private final int notPresentMask;


	public EdgePatternImpl (int presentMask, int notPresentMask,
							boolean checkDirection, boolean toTarget)
	{
		this.presentMask = presentMask;
		this.notPresentMask = notPresentMask;
		this.checkDirection = checkDirection;
		this.toTarget = toTarget;
	}


	public boolean matches (Object source, Object target, int edges,
							boolean toTarget)
	{
		return (!checkDirection || (this.toTarget == toTarget))
			&& ((edges & presentMask) != 0)
			&& ((edges & notPresentMask) == 0);
	}
}
