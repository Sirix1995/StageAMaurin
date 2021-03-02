
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

package de.grogra.docking;

import java.awt.*;

public final class DockPosition implements Comparable
{
	public static final int TOLERANCE = 10;
	public static final int TOLERANCE2 = 30;

	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int TOP = 4;
	public static final int BOTTOM = 8;
	public static final int CENTER = 16;
	public static final int TAB = 32;

	public static final int VERTICAL_MASK = LEFT | RIGHT;
	public static final int HORIZONTAL_MASK = TOP | BOTTOM;
	public static final int TAB_MASK = CENTER | TAB;

	public static final int FIRST_EDGE = LEFT | TOP;
	public static final int SECOND_EDGE = RIGHT | BOTTOM;

	public static final int EDGE = FIRST_EDGE | SECOND_EDGE;


	private static int nextId = 0;

	private static synchronized int getNextId ()
	{
		return nextId++;
	}


	private final int id;

	private DockComponent adjacent;
	private final int position;

	private final int absX;
	private final int absY;

	private final int dragX;
	private final int dragY;

	private final DockShape shape;


	public static void addDockPositions (DockPositionList list, DockComponent c,
										 int positions, Point relDrag,
										 Container coordParent,
										 DockShape shape)
	{
		int x = relDrag.x, y = relDrag.y;
		if (coordParent != null)
		{
			DockManager.translate (relDrag, coordParent, (Component) c);
		}
		for (int i = LEFT; i <= CENTER; i <<= 1)
		{
			if (((positions & i) != 0)
				&& !((i == CENTER) && (c instanceof DockSplitPane)))
			{
				list.addDockPosition (c, i, relDrag, shape);
			}
		}
		relDrag.x = x;
		relDrag.y = y;
	}


	DockPosition (DockComponent adjacent,
				  int position, int absX, int absY, int dragX, int dragY,
				  DockShape shape)
	{
		this.id = getNextId ();
		this.adjacent = adjacent;
		this.position = position;
		this.absX = absX;
		this.absY = absY;
		this.dragX = dragX;
		this.dragY = dragY;
		this.shape = shape;
	}


	DockPosition (DockComponent adjacent,
				  int position, Point absDrag, Point relDrag, DockShape shape)
	{
		this (adjacent, position,
			  absDrag.x - relDrag.x, absDrag.y - relDrag.y,
			  relDrag.x, relDrag.y, shape);
	}


	public DockComponent getAdjacent ()
	{
		return adjacent;
	}


	public int getPosition ()
	{
		return position;
	}


	public int getLength ()
	{
		if ((position & VERTICAL_MASK) != 0)
		{
			return adjacent.getHeight ();
		}
		else if ((position & HORIZONTAL_MASK) != 0)
		{
			return adjacent.getWidth ();
		}
		else
		{
			return 0;
		}
	}


	public int getDragDelta ()
	{
		switch (position)
		{
			case LEFT:
				return -dragX;
			case RIGHT:
				return adjacent.getWidth () - dragX;
			case TOP:
				return -dragY;
			case BOTTOM:
				return adjacent.getHeight () - dragY;
			default:
				return 0;
		}
	}


	public int getAbsX ()
	{
		return absX;
	}


	public int getAbsY ()
	{
		return absY;
	}


	public static DockPosition testDockPosition (DockComponent c, int position,
												 Point absDrag, Point relDrag,
												 DockShape shape)
	{
		boolean ok;
		switch (position)
		{
			case LEFT:
				ok = (Math.abs (relDrag.x) < TOLERANCE2)
					&& (-TOLERANCE2 < relDrag.y)
					&& (relDrag.y < c.getHeight () + TOLERANCE2);
				break;
			case RIGHT:
				ok = (Math.abs (relDrag.x - c.getWidth () + 1) < TOLERANCE2)
					&& (-TOLERANCE2 < relDrag.y)
					&& (relDrag.y < c.getHeight () + TOLERANCE2);
				break;
			case TOP:
				ok = (Math.abs (relDrag.y) < TOLERANCE2)
					&& (-TOLERANCE2 < relDrag.x)
					&& (relDrag.x < c.getWidth () + TOLERANCE2);
				break;
			case BOTTOM:
				ok = (Math.abs (relDrag.y - c.getHeight () + 1) < TOLERANCE2)
					&& (-TOLERANCE2 < relDrag.x)
					&& (relDrag.x < c.getWidth () + TOLERANCE2);
				break;
			case CENTER:
				ok = (relDrag.x >= 0) && (relDrag.y >= 0) 
					&& (relDrag.x < c.getWidth ())
					&& (relDrag.y < c.getHeight ());
				break;
			case TAB:
				ok = true;
				break;
			default:
				ok = false;
				break;
		}
		if (ok)
		{
			return new DockPosition (c, position, absDrag, relDrag, shape);
		}
		return null;
	}


	private static int compare (int a, int b)
	{
		return (a < b) ? -1 : (a > b) ? 1 : 0;
	}


	private int compareImpl (DockPosition o)
	{
		if (position < o.position)
		{
			return 1;
		}
		else if (position > o.position)
		{
			return -1;
		}
		int c = compare (getDragDelta (), o.getDragDelta ());
		if (c != 0)
		{
			return c;
		}
		return compare (getLength (), o.getLength ())
			* (((position & SECOND_EDGE) != 0) ? 1 : -1);
	}


	public int compareTo (Object o)
	{
		int c = compareImpl ((DockPosition) o);
		return (c != 0) ? c : compare (id, ((DockPosition) o).id);
	}


	@Override
	public String toString ()
	{
		return "DockPosition[" + position + ',' + getDragDelta () + ','
			+ getLength () + ',' + adjacent + ']';
	}


	void paintDockShape (Graphics g)
	{
		g.setColor (Color.darkGray);
		if (shape != null)
		{
			shape.paintDockShape (this, g);
		}
		else
		{
			for (int i = (position == CENTER) ? LEFT : position;
				 i <= ((position == CENTER) ? BOTTOM : position); i <<= 1)
			{
				switch (i)
				{
					case LEFT:
						g.fillRect (absX - 2, absY - 2,
									5, adjacent.getHeight () + 4);
						break;
					case RIGHT:
						g.fillRect (absX + adjacent.getWidth () - 2, absY - 2,
									5, adjacent.getHeight () + 4);
						break;
					case TOP:
						g.fillRect (absX - 2, absY - 2,
									adjacent.getWidth () + 4, 5);
						break;
					case BOTTOM:
						g.fillRect (absX - 2, absY + adjacent.getHeight () - 2,
									adjacent.getWidth () + 4, 5);
						break;
				}
			}
		}
	}


	boolean dockShapeEquals (DockPosition p)
	{
		if (shape != null)
		{
			return shape.equals (p.shape);
		}
		else if (p.shape != null)
		{
			return false;
		}
		else
		{
			return (position == p.position)
				&& ((adjacent == p.adjacent)
					|| ((absX == p.absX) && (absY == p.absY)
						&& (adjacent.getWidth () == p.adjacent.getWidth ())
						&& (adjacent.getHeight () == p.adjacent.getHeight ())));
		}
	}


	void drop (DragDockableContext ctx)
	{
		DockableComponent d = ctx.getDockableComponent ();
		Dockable dockable = ctx.getDockable ();
		if (adjacent == d)
		{
			return;
		}
		DockComponent a = (adjacent instanceof DockableComponent)
			? ((DockableComponent) adjacent).getDockable () : adjacent;
		DockContainer dc = ctx.getDockContainer ();
		DockComponent c = dc.remove (d);
		if ((c != dc) && (a == dc))
		{
			a = c;
		}
		ctx.getManager ().addImpl (dockable, position, a);
	}
}
