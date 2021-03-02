
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

package de.grogra.imp.edit;

import java.util.ArrayList;
import java.lang.ref.WeakReference;
import de.grogra.graph.*;
import de.grogra.pf.ui.*;
import de.grogra.imp.View;
import de.grogra.pf.ui.edit.*;

public final class ViewSelection
{
	/**
	 * Bit mask indicating that the mouse pointer is over an object
	 * in a view.
	 */
	public static final int MOUSE_OVER = 1;

	/**
	 * Bit mask indicating the single object under the mouse pointer which
	 * the user has selected out of the list of objects under the mouse pointer.
	 */
	public static final int MOUSE_OVER_SELECTED = 2;

	/**
	 * Bit mask indicating that an object is selected.
	 */
	public static final int SELECTED = 4;

	ArrayList selections = new ArrayList ();
	private final WeakReference viewRef;


	public static final UIProperty PROPERTY = UIProperty.getOrCreate
		("view.selection", UIProperty.PANEL);


	public static final class Entry
	{
		final Path path;
		int value;


		Entry (Path path, int value, ViewSelection s)
		{
			this.path = new ArrayPath (path);
			this.value = value;
		}


		Entry (Entry e)
		{
			this.path = e.path;
			this.value = e.value;
		}


		public Path getPath ()
		{
			return path;
		}


		public int getValue ()
		{
			return value;
		}
	}


	private ViewSelection (View view)
	{
		super ();
		viewRef = new WeakReference (view);
	}


	View getView ()
	{
		return (View) viewRef.get ();
	}


	public Entry getEntry (Path path)
	{
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if (GraphUtils.equal (path, e.path))
				{
					return e;
				}
			}
		}
		return null;
	}


	public Entry getFirstEntry (Object object, boolean asNode)
	{
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if ((((e.path.getNodeAndEdgeCount () & 1) != 0) == asNode)
					&& (object == e.path.getObject (-1)))
				{
					return e;
				}
			}
		}
		return null;
	}


	public int get (Path path)
	{
		Entry e = getEntry (path);
		return e == null ? 0 : e.value;
	}


	public int get (Object object, boolean asNode)
	{
		int c = 0;
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if ((((e.path.getNodeAndEdgeCount () & 1) != 0) == asNode)
					&& (object == e.path.getObject (-1)))
				{
					c |= e.value;
				}
			}
		}
		return c;
	}


	public boolean isSelected (Object object, boolean asNode)
	{
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if (((e.value & SELECTED) != 0)
					&& (((e.path.getNodeAndEdgeCount () & 1) != 0) == asNode)
					&& (object == e.path.getObject (-1)))
				{
					return true;
				}
			}
		}
		return false;
	}


	public Path getFirstPath (int type)
	{
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if ((e.value & type) != 0)
				{
					return e.path;
				}
			}
		}
		return null;
	}


	public Entry[] getAll (int type)
	{
		ArrayList l = new ArrayList ();
		synchronized (selections)
		{
			for (int i = 0; i < selections.size (); i++)
			{
				Entry e = (Entry) selections.get (i);
				if ((e.value & type) != 0)
				{
					l.add (new Entry (e));
				}
			}
		}
		return (Entry[]) l.toArray (new Entry[l.size ()]);
	}


	public int count (int type)
	{
		int c = 0;
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if ((e.value & type) != 0)
				{
					c++;
				}
			}
		}
		return c;
	}


	public int add (int type, Path path)
	{
		boolean t = (type & SELECTED) != 0;
		Entry e;
		synchronized (selections)
		{
			e = getEntry (path);
			if (e == null)
			{
				selections.add (e = new Entry (path, type, this));
			}
			else
			{
				if ((e.value & type) != type)
				{
					type = (e.value |= type);
				}
				else
				{
					e = null;
				}
			}
		}
		notify (e, t);
		return type;
	}


	public void remove (int type, Path path)
	{
		boolean t = false;
		Entry e;
		synchronized (selections)
		{
			e = getEntry (path);
			if ((e != null) && ((e.value & type) != 0))
			{
				t = (e.value & type & SELECTED) != 0;
				type = (e.value &= ~type);
				if (type == 0)
				{
					selections.remove (e);
				}
			}
			else
			{
				e = null;
			}
		}
		notify (e, t);
	}


	public int removeAndAdd (int remove, int add, Path path)
	{
		boolean t = false;
		Entry e;
		synchronized (selections)
		{
			e = getEntry (path);
			if (e == null)
			{
				if (add != 0)
				{
					t = (add & SELECTED) != 0;
					selections.add (e = new Entry (path, add, this));
				}
			}
			else if ((add = (e.value & ~remove) | add) != e.value)
			{
				t = ((e.value ^ add) & SELECTED) != 0;
				e.value = add;
				if (add == 0)
				{
					selections.remove (e);
				}
			}
			else
			{
				e = null;
			}
		}
		notify (e, t);
		return add;
	}

	
	public void set (Selection sel, boolean updateWorkbenchSelection)
	{
		if (!(sel instanceof GraphSelection))
		{
			set (SELECTED, Path.PATH_0, false);
			return;
		}
		GraphSelection g = (GraphSelection) sel;
		ArrayList paths = new ArrayList ();
		for (int i = g.size () - 1; i >= 0; i--)
		{
			Path p = getView ().getPathFor
				(g.getGraphState (i), g.getObject (i), g.isNode (i));
			if (p != null)
			{
				paths.add (p);
			}
		}
		set (SELECTED, (Path[]) paths.toArray (new Path[paths.size ()]),
			 updateWorkbenchSelection);
	}


	public Path[] set (int type, Path[] paths, boolean updateWorkbenchSelection)
	{
		ArrayList toRemove, changed;
		synchronized (selections)
		{
			toRemove = new ArrayList (selections.size ());
			changed = new ArrayList (selections.size () + 1);
			boolean[] found = new boolean[paths.length];

		checkSelections:
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				for (int j = 0; j < paths.length; j++)
				{
					if (!found[j] && GraphUtils.equal (paths[j], e.path))
					{
						if ((e.value & type) != type)
						{
							changed.add ((((e.value ^ type) & SELECTED) != 0)
										 ? this : null);
							changed.add (e);
							e.value |= type;
						}
						found[j] = true;
						continue checkSelections;
					}
				}
				if ((e.value & type) != 0)
				{
					changed.add (((e.value & type & SELECTED) != 0) ? this : null);
					changed.add (e);
					if ((e.value &= ~type) == 0)
					{
						toRemove.add (e);
					}
				}
			}
			for (int i = toRemove.size () - 1; i >= 0; i--)
			{
				selections.remove (toRemove.get (i));
			}
			for (int j = 0; j < paths.length; j++)
			{
				if (!found[j])
				{
					Entry e = new Entry (paths[j], type, this);
					selections.add (e);
					changed.add (((type & SELECTED) != 0) ? this : null);
					changed.add (e);
				}
			}
		}
		Path[] a = new Path[changed.size () >> 1];
		for (int i = changed.size () - 2; i >= 0; i -= 2)
		{
			Entry e = (Entry) changed.get (i + 1);
			notify (e, updateWorkbenchSelection && (changed.get (i) != null));
			a[i >> 1] = e.path;
		}
		return a;
	}


	public int toggle (int type, Path path)
	{
		boolean t = (type & SELECTED) != 0;
		Entry e;
		synchronized (selections)
		{
			e = getEntry (path);
			if (e == null)
			{
				selections.add (e = new Entry (path, type, this));
			}
			else
			{
				type = (e.value ^= type);
				if (type == 0)
				{
					selections.remove (e);
				}
			}
		}
		notify (e, t);
		return type;
	}

	
	public void graphModified (GraphState gs)
	{
		ArrayList removed = null;
		synchronized (selections)
		{
			for (int i = selections.size () - 1; i >= 0; i--)
			{
				Entry e = (Entry) selections.get (i);
				if (gs.getGraph ().getLifeCycleState (e.path.getObject (-1), (e.path.getNodeAndEdgeCount () & 1) != 0) != Graph.PERSISTENT)
				{
					removed = new ArrayList (selections);
					selections.clear ();
					break;
				}
			}
		}
		if (removed != null)
		{
			for (int i = 0; i < removed.size (); i++)
			{
				notify ((Entry) removed.get (i), false);
			}
		}
	}


	private void notify (Entry e, boolean updateWorkbenchSelection)
	{
		if (e != null)
		{
			View view = getView ();
			PROPERTY.firePropertyChange
				(view, null, null,
				 new ViewSelectionChanged (view, e.path));
			if (updateWorkbenchSelection)
			{
				Entry[] a = getAll (SELECTED);
				if (a.length == 0)
				{
					UIProperty.WORKBENCH_SELECTION.setValue (view, null);
				}
				else
				{
					Object[] objects = new Object[a.length];
					boolean[] nodes = new boolean[a.length];
					GraphState[] states = new GraphState[a.length];
					GraphState gs = view.getWorkbenchGraphState ();
					for (int i = a.length - 1; i >= 0; i--)
					{
						int l = GraphUtils.lastIndexOfGraph (a[i].getPath (), gs.getGraph ());
						assert l >= 0;
						states[i] = gs;
						objects[i] = a[i].getPath ().getObject (l);
						nodes[i] = (l & 1) == 0;
						view.substituteSelection (states, objects, nodes, i);
					}
					UIProperty.WORKBENCH_SELECTION.setValue
						(view, new GraphSelectionImpl (view, states, objects, nodes));
				}
			}
		}
	}


	public static void create (View view)
	{
		PROPERTY.setValue (view, new ViewSelection (view));
	}


	public static ViewSelection get (de.grogra.pf.ui.Context ctx)
	{
		return (ViewSelection) PROPERTY.getValue (ctx);
	}


	public static int getColor (int baseColor, int state, boolean showSel)
	{
		int r, g, b;
		if (showSel && ((state & SELECTED) != 0))
		{
			r = g = b = 255;
		}
		else if ((state & MOUSE_OVER_SELECTED) != 0)
		{
			r = 255;
			g = 200;
			b = 0;
		}
		else if ((state & MOUSE_OVER) != 0)
		{
			r = 255;
			g = 0;
			b = 0;
		}
		else
		{
			return baseColor;
		}
		if (Math.abs (r - ((baseColor >> 16) & 255))
			+ Math.abs (g - ((baseColor >> 8) & 255))
			+ Math.abs (b - (baseColor & 255)) < 60)
		{
			if (r + b + g > 600)
			{
				r >>= 1;
				g >>= 1;
				b >>= 1;
			}
			else
			{
				r = 128 + (r >> 1);
				g = 128 + (g >> 1);
				b = 128 + (b >> 1);
			}
		}
		return (r << 16) + (g << 8) + b + 0xff000000;
	}

}
