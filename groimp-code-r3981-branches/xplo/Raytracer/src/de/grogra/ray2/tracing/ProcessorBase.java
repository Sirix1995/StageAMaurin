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

package de.grogra.ray2.tracing;

import java.util.ArrayList;

public abstract class ProcessorBase implements Cloneable
{
	private final ArrayList<ProcessorBase> instances;

	public ProcessorBase ()
	{
		instances = new ArrayList<ProcessorBase> ();
		instances.add (this);
	}

	@Override
	protected Object clone ()
	{
		try
		{
			ProcessorBase p = (ProcessorBase) super.clone ();
			instances.add (p);
			return p;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}

	protected void initLocals ()
	{
	}

	public void appendStatistics (StringBuffer stats)
	{
		for (int i = 0; i < instances.size (); i++)
		{
			ProcessorBase p = instances.get (i);
			if (p != this)
			{
				mergeStatistics (p);
			}
		}
		appendStatisticsImpl (stats);
	}

	protected void mergeStatistics (ProcessorBase src)
	{
	}

	protected abstract void appendStatisticsImpl (StringBuffer stats);

}
