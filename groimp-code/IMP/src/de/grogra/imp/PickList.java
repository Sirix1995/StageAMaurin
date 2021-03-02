
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

public final class PickList extends de.grogra.math.Pool
{
	private int currentLayer;

	private Path path = null;
	private boolean removeNode;

	private PickElement[] info = new PickElement[0];
	private int size = 0, maxLength, groupSize = 0, x, y;
	private View view;
	private double minDist;
	private GraphState state;
	private final boolean allowNegativeDist;


	public PickList (int maxLength, boolean allowNegativeDist)
	{
		this.maxLength = maxLength;
		this.allowNegativeDist = allowNegativeDist;
	}


	public GraphState getGraphState ()
	{
		return state;
	}


	public View getView ()
	{
		return view;
	}


	public int getViewX ()
	{
		return x;
	}


	public int getViewY ()
	{
		return y;
	}


	public void reset (View view, int x, int y)
	{
		this.view = view;
		this.state = view.getWorkbenchGraphState ();
		this.x = x;
		this.y = y;
		reset ();
	}


	public void reset ()
	{
		int i;
		for (i = 0; i < size; i++)
		{
			info[i].set (null, 0);
		}
		size = 0;
		groupSize = 0;
	}


	public void beginNewGroup ()
	{
		groupSize = 0;
	}

	
	public void setCurrentLayer (int layer)
	{
		currentLayer = layer;
	}


	public void begin (Path p, boolean removeNode)
	{
		path = p;
		this.removeNode = removeNode;
		minDist = Double.POSITIVE_INFINITY;
	}


	public void add (double distance)
	{
		if ((allowNegativeDist || (distance >= 0)) && (distance < minDist))
		{
			minDist = distance;
		}
	}


	public void add ()
	{
		if (currentLayer < minDist)
		{
			minDist = currentLayer;
		}
	}


	public boolean containsCurrent ()
	{
		return minDist != Double.POSITIVE_INFINITY;
	}


	public void end ()
	{
		if (minDist == Double.POSITIVE_INFINITY)
		{
			return;
		}
		int i, j;
		Object c = path.getObject (removeNode ? -2 : -1);
		boolean node = !removeNode;
		if (size == info.length)
		{
			System.arraycopy (info, 0, info = new PickElement[size + 10], 0, size);
			for (i = size; i < info.length; i++)
			{
				info[i] = new PickElement ();
			}
		}
		for (i = 0; i < groupSize; i++)
		{
			if (minDist < info[i].distance)
			{
				break;
			}
			if ((info[i].path.endsInNode () == node)
				&& (info[i].path.getObject (-1) == c))
			{
				return;
			}
		}
		PickElement p;
		if (i < size)
		{
			p = info[size];
			System.arraycopy (info, i, info, i + 1, size - i);
			info[i] = p;
		}
		else if (size == maxLength)
		{
			return;
		}
		info[i].set (path, minDist);
		if (removeNode)
		{
			info[i].path.popNode ();
		}
		for (j = i + 1; j <= size; j++)
		{
			p = info[j];
			if ((p.path.endsInNode () == node) && (p.path.getObject (-1) == c))
			{
				p.set (null, 0);
				if (j < size)
				{
					System.arraycopy (info, j + 1, info, j, size - j);
					info[size] = p;
				}
				return;
			}
		}
		size++;
		groupSize++;
	}


	public double getMaxDistance ()
	{
		return (size == 0) ? Double.NEGATIVE_INFINITY
						   : info[size - 1].distance;
	}


	public void getItem (int index, PickElement p)
	{
		p.set (info[index]);
	}


	public void getPath (int index, ArrayPath p)
	{
		p.set (info[index].path);
	}


	public ArrayPath getPath (int index)
	{
		return new ArrayPath (info[index].path);
	}


	public int getSize ()
	{
		return size;
	}


	@Override
	public boolean equals (Object object)
	{
		if (object instanceof PickList)
		{
			PickList l = (PickList) object;
			if (l.size == size)
			{
				for (int i = 0; i < size; i++)
				{
					if (!GraphUtils.equal (info[i].path, l.info[i].path))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer (super.toString ())
			.append ("[size=").append (size).append (":\n");
		for (int i = 0; i < size; i++)
		{
			b.append (info[i]);
			b.append ('\n');
		}
		return b.append (']').toString ();
	}

}
