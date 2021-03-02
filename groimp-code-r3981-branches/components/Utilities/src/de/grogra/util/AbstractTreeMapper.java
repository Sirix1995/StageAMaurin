
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

import java.util.Comparator;
import javax.swing.tree.*;
import javax.swing.event.*;

import de.grogra.xl.lang.ObjectToBoolean;

public abstract class AbstractTreeMapper
	implements TreeModelListener, Disposable
{
	protected final TreeModel source;
	protected final MutableTreeModel target;
	protected Object root;

	private final Comparator comparator;
	private ObjectToBoolean filter = null;
	private boolean disposed = false;


	public AbstractTreeMapper (TreeModel source, Object root,
							   MutableTreeModel target, Comparator comparator)
	{
		this.source = source;
		this.target = target;
		this.root = root;
		this.comparator = comparator;
	}


	public TreeModel getSourceTree ()
	{
		return source;
	}


	public void installListener ()
	{
		source.addTreeModelListener (this);
	}


	public void setFilter (ObjectToBoolean filter)
	{
		this.filter = filter;
	}


	public void map ()
	{
		Object t = createNode (root, null);
		if (!source.isLeaf (root))
		{
			int c = source.getChildCount (root);
			for (int i = 0; i < c; i++)
			{
				Object sc = source.getChild (root, i);
				if (filter (sc))
				{
					createAndInsert (sc, t, Integer.MAX_VALUE);
				}
			}
		}
		target.setRoot (t);
		targetChanged (t);
	}


	protected boolean filter (Object node)
	{
		return (filter == null) || filter.evaluateBoolean (node);
	}


	public boolean sourceNodesEqual (Object a, Object b)
	{
		return (a == b) || ((comparator == null) ? a.equals (b)
							: comparator.compare (a, b) == 0);
	}


	protected void createAndInsert (Object sourceNode, Object targetParent,
									int index)
	{
		Object t = createNode (sourceNode, targetParent);
		target.insert (targetParent, Math.min (index, target.getChildCount (targetParent)), t, null);
		if (!source.isLeaf (sourceNode))
		{
			int c = source.getChildCount (sourceNode);
			for (int i = 0; i < c; i++)
			{
				Object sc = source.getChild (sourceNode, i);
				if (filter (sc))
				{
					createAndInsert (sc, t, Integer.MAX_VALUE);
				}
			}
		}
	}


	protected Object getImage (Object sourceNode, Object targetParent)
	{
		int n = target.getChildCount (targetParent);
		for (int i = 0; i < n; i++)
		{
			Object c;
			if (isImage (sourceNode, c = target.getChild (targetParent, i)))
			{
				return c;
			}
		}
		return null;
	}


	protected abstract Object createNode (Object sourceNode,
										  Object targetParent);


	protected void update (Object sourceNode, TreePath targetPath)
	{
		target.valueForPathChanged (targetPath, sourceNode);
	}


	protected abstract boolean isImage (Object sourceNode, Object targetNode);


	protected void targetChanged (Object parent)
	{
	}


	private Object filter (TreeModelEvent e, boolean returnPath)
	{
		TreePath p = e.getTreePath ();
		Object sp = null;
		int i;
		for (i = 0; i < p.getPathCount (); i++)
		{
			if (!filter (sp = p.getPathComponent (i)))
			{
				return null;
			}
			if (sourceNodesEqual (sp, root))
			{
				break;
			}
			sp = null;
		}
		if (sp == null)
		{
			return null;
		}
		Object dp = target.getRoot ();
		TreePath rp = returnPath ? new TreePath (dp) : null;
		while (++i < p.getPathCount ())
		{
			sp = p.getPathComponent (i);
			if (!filter (sp))
			{
				return null;
			}
			dp = getImage (sp, dp);
			if (dp == null)
			{
				return null;
			}
			if (returnPath)
			{
				rp = rp.pathByAddingChild (dp);
			}
		}
		return returnPath ? rp : dp;
	}


	public void treeNodesChanged (TreeModelEvent e)
	{
		TreePath p = (TreePath) filter (e, true);
		Object[] changed = e.getChildren ();
		if (p != null)
		{
			if (changed == null)
			{
				assert p.getPathCount() == 1;
				update (root = e.getTreePath ().getLastPathComponent (), p);
			}
			else
			{
				Object dp = p.getLastPathComponent ();
				for (int k = 0; k < changed.length; k++)
				{
					Object so = changed[k], to;
					if (filter (so) && ((to = getImage (so, dp)) != null))
					{
						update (so, p.pathByAddingChild (to));
					}
				}
			}
		}
		else if (changed != null)
		{
			for (int k = 0; k < changed.length; k++)
			{
				if (sourceNodesEqual (changed[k], root))
				{
					update (root = changed[k], new TreePath (target.getRoot ()));
					targetChanged (target.getRoot ());
					break;
				}
			}
		}
	}


	public void treeNodesInserted (TreeModelEvent e)
	{
		Object dp = filter (e, false);
		if (dp != null)
		{
			Object sp = e.getTreePath ().getLastPathComponent ();
			Object[] changed = e.getChildren ();
			int[] indices = e.getChildIndices ();
			for (int k = 0; k < changed.length; k++)
			{
				Object so;
				if (filter (so = changed[k]))
				{
					Object to = null;
					int i = indices[k];
					while (--i >= 0)
					{
						if ((to = getImage (source.getChild (sp, i), dp))
							!= null)
						{
							break;
						}
					}
					if (to != null)
					{
						i = target.getIndexOfChild (dp, to) + 1;
					}
					else
					{
						i = indices[k];
						int n = source.getChildCount (sp);
						while (++i < n)
						{
							if ((to = getImage (source.getChild (sp, i), dp))
								!= null)
							{
								break;
							}
						}
						if (to != null)
						{
							i = target.getIndexOfChild (dp, to);
						}
						else
						{
							i = Integer.MAX_VALUE;
						}
					}
					createAndInsert (so, dp, i);
				}					
			}
			targetChanged (dp);
		}
	}


	public void treeNodesRemoved (TreeModelEvent e)
	{
		Object dp = filter (e, false);
		if (dp != null)
		{
			Object[] changed = e.getChildren ();
			for (int k = 0; k < changed.length; k++)
			{
				Object o = changed[k];
				if (filter (o) && ((o = getImage (o, dp)) != null))
				{
					target.remove (dp, target.getIndexOfChild (dp, o));
					nodeRemoved (o);
				}
			}
			targetChanged (dp);
		}
	}


	public void treeStructureChanged (TreeModelEvent e)
	{
		Object dp = filter (e, false);
		if (dp != null)
		{
			Object sp = e.getTreePath ().getLastPathComponent ();
			for (int k = target.getChildCount (dp) - 1; k >= 0; k--)
			{
				Object o = target.getChild (dp, k);
				target.remove (dp, k);
				nodeRemoved (o);
			}
			if (!source.isLeaf (sp))
			{
				int c = source.getChildCount (sp);
				for (int i = 0; i < c; i++)
				{
					Object sc = source.getChild (sp, i);
					if (filter (sc))
					{
						createAndInsert (sc, dp, Integer.MAX_VALUE);
					}
				}
			}
			targetChanged (dp);
		}
	}


	protected void nodeRemoved (Object node)
	{
	}


	public final void dispose ()
	{
		if (disposed)
		{
			return;
		}
		disposed = true;
		source.removeTreeModelListener (this);
		disposeImpl ();
	}


	protected void disposeImpl ()
	{
	}

}
