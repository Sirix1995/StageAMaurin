
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

public abstract class VisitorImpl implements Visitor
{
	protected EdgePattern pattern;
	protected GraphState state;


	public void init (GraphState gs, EdgePattern pattern)
	{
		this.state = gs;
		this.pattern = pattern;
	}


	public GraphState getGraphState ()
	{
		return state;
	}


	public Object visitEnter (Path path, boolean node)
	{
		return (node || GraphUtils.matchesTerminalEdge (pattern, path)) ? null : STOP;
	}


	public boolean visitLeave (Object o, Path path, boolean node)
	{
		return true;
	}


	public Object visitInstanceEnter ()
	{
		return null;
	}


	public boolean visitInstanceLeave (Object o)
	{
		return true;
	}
}
