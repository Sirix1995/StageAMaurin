
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

import java.util.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.*;

public class DockManager extends WindowAdapter
	implements DragGestureListener
{
	public static final ResourceBundle RES_BUNDLE
		= ResourceBundle.getBundle ("de.grogra.docking.Resources");


	final RootInfo mainInfo;
	private Dockable selected = null;	


	private static final class DnDGlassPane extends JComponent
		implements DockPositionList
	{
		RootInfo info;
		boolean dndActive = false;

		private Point absPoint = new Point ();
		private int minXTol, minYTol, stepsBack;
		private TreeSet horzPositions = new TreeSet (),
			vertPositions = new TreeSet ();
		DockPosition position = null, newPosition, tabPosition;


		DnDGlassPane (RootInfo info)
		{
			super ();
			setOpaque (false);
			this.info = info;
		}


		@Override
		public final void paint (Graphics g)
		{
			DockPosition l = position;
			if (dndActive && (l != null))
			{
				l.paintDockShape (g);
			}
		}


		void setDropPosition (DockPosition pos)
		{
			if (pos == null)
			{
				if (position != null)
				{
					position = null;
					repaint ();
				}
			}
			else
			{
				if ((position == null) || !pos.dockShapeEquals (position))
				{
					position = pos;
					repaint ();
				}
			}
		}


		void drag (DropTargetDragEvent e)
		{
			Point p = e.getLocation ();
			absPoint.setLocation (p);
			p.translate (getX (), getY ());

			minXTol = Integer.MAX_VALUE;
			minYTol = Integer.MAX_VALUE;
			stepsBack = 0;
			horzPositions.clear ();
			vertPositions.clear ();
			tabPosition = null;
			newPosition = null;
			findDockPositions (info.dockRoot, p, getParent ());
			TreeSet s = (minXTol < minYTol) ? vertPositions : horzPositions;
			if (!s.isEmpty ())
			{
				choosePosition (s.iterator (), null);
				if (Math.abs (newPosition.getDragDelta ())
					>= DockPosition.TOLERANCE)
				{
					newPosition = null;
				}
			}
			if (newPosition == null)
			{
				newPosition = tabPosition;
			}
			setDropPosition (newPosition);
		}


		private void choosePosition (Iterator i, DockPosition prev)
		{
			DockPosition dl = null;
			if (i.hasNext ())
			{
				dl = (DockPosition) i.next ();
				if (newPosition == null)
				{
					newPosition = dl;
				}
				if ((prev == null) || (dl.getLength () > prev.getLength ()))
				{
					choosePosition (i, dl);
					if (--stepsBack == 0)
					{
						newPosition = dl;
					}
					return;
				}
			}
			int n = prev.getDragDelta () >> 1;
			if ((n == 0) || (n == -1))
			{
				newPosition = prev;
			}
			else if (n < 0)
			{
				++n;
				while (dl != null)
				{
					newPosition = dl;
					if ((++n == 0) || !i.hasNext ())
					{
						break;
					}
					dl = (DockPosition) i.next ();
				}
			}
			else
			{
				stepsBack = n + 1;
			}
		}


		private void findDockPositions (DockContainer dc, Point p,
										Container parent)
		{
			int x = p.x, y = p.y;
			translate (p, parent, (Component) dc);
			dc.findDockPositions (this, p);
			int n = dc.getDockComponentCount ();
			for (int i = 0; i < n; i++)
			{
				DockComponent c = dc.getDockComponent (i);
				if (c instanceof DockContainer)
				{
					findDockPositions ((DockContainer) c, p, (Container) dc);
				}
			}
			p.x = x;
			p.y = y;
		}


		public void addDockPosition (DockComponent c, int position,
									 Point relDrag, DockShape shape)
		{
			DockPosition dp = DockPosition.testDockPosition
				(c, position, absPoint, relDrag, shape);
			if (dp != null)
			{
				if ((position & DockPosition.VERTICAL_MASK) != 0)
				{
					minXTol = Math.min (minXTol, dp.getDragDelta ());
					vertPositions.add (dp);
				}
				else if ((position & DockPosition.HORIZONTAL_MASK) != 0)
				{
					minYTol = Math.min (minYTol, dp.getDragDelta ());
					horzPositions.add (dp);
				}
				else if ((position & DockPosition.TAB_MASK) != 0)
				{
					tabPosition = dp;
				}
			}
		}

	}


	private static final class RootInfo
	{
		DockManager manager;
		JRootPane root;
		DockContentPane dockRoot;

		DnDGlassPane dndGlassPane;
		DropTarget dropTarget;

		Rectangle fwBounds;
		String fwTitle;


		RootInfo (DockManager manager, JRootPane root,
					   DockContentPane dockRoot)
		{
			this.manager = manager;
			this.root = root;
			this.dockRoot = dockRoot;
			dndGlassPane = new DnDGlassPane (this);
			dndGlassPane.setVisible (false);
			root.setGlassPane (dndGlassPane);
		}


		Container getWindow ()
		{
			return root.getParent ();
		}
/*
		public void mousePressed (MouseEvent e)
		{
			Component c = e.getComponent ();
			while (c != null)
			{
				if (c instanceof DockableComponent)
				{
					manager.select (((DockableComponent) c).getDockable ());
					return;
				}
				c = c.getParent ();
			}
		}
*/

		ArrayList dispose (boolean invokeClosed)
		{
			if (dropTarget != null)
			{
				dropTarget.setComponent (null);
				dropTarget = null;
			}

			ArrayList list = release (invokeClosed);
			dockRoot = null;
			return list;
		}


		ArrayList release (boolean invokeClosed)
		{
			ArrayList list = new ArrayList (10);
			getDockables (list, null);
			for (int i = list.size () - 1; i >= 0; i--)
			{
				Dockable d = (Dockable) list.get (i);
				d.getComponent ().getParent ()
					.remove ((Component) d.getComponent ());
				if (invokeClosed)
				{
					d.dockableClosed ();
				}
			}
			dockRoot.removeAll ();
			return list;
		}


		void setDnD (boolean dnd)
		{
			if (dnd != dndGlassPane.dndActive)
			{
				if (dnd && (dropTarget == null))
				{
					dropTarget = new DropTarget
						(dndGlassPane, DnDConstants.ACTION_MOVE,
						 manager.targetListener);
				}
				dndGlassPane.dndActive = dnd;
				dndGlassPane.setVisible (dnd);
				dndGlassPane.repaint ();
			}
		}


		void getDockables (ArrayList list, DockableFilter filter)
		{
			getDockables (dockRoot, list, filter);
		}


		private static void getDockables
			(DockComponent c, ArrayList list, DockableFilter filter)
		{
			if (c instanceof DockableComponent)
			{
				if ((filter == null)
					|| filter.accept (((DockableComponent) c).getDockable ()))
				{
					list.add (((DockableComponent) c).getDockable ());
				}
			}
			else if (c instanceof DockContainer)
			{
				DockContainer dc = (DockContainer) c;
				int n = dc.getDockComponentCount ();
				for (int i = 0; i < n; i++)
				{
					getDockables (dc.getDockComponent (i), list, filter);
				}
			}
		}

	}


	private DragSource dragSource;
	Hashtable roots = new Hashtable (4);


	private DragSourceAdapter sourceListener = new DragSourceAdapter ()
	{
		@Override
		public void dragEnter (DragSourceDragEvent e)
		{
			e.getDragSourceContext ().setCursor (DragSource.DefaultMoveDrop);
		}


		@Override
		public void dragOver (DragSourceDragEvent e)
		{
			e.getDragSourceContext ().setCursor (DragSource.DefaultMoveDrop);
		}


		@Override
		public void dragExit (DragSourceEvent e)
		{
			e.getDragSourceContext ().setCursor (DragSource.DefaultMoveNoDrop);
		}


		@Override
		public void dragDropEnd (DragSourceDropEvent e) 
		{
			setDnD (false);
		}
	};


	DnDGlassPane check (DropTargetDragEvent e)
	{
		return e.isDataFlavorSupported (DragDockableContext.getFlavor ())
			? checkImpl (e) : null;
	}


	DnDGlassPane check (DropTargetDropEvent e)
	{
		return e.isDataFlavorSupported (DragDockableContext.getFlavor ())
			? checkImpl (e) : null;
	}


	private DnDGlassPane checkImpl (DropTargetEvent e)
	{
		Component c = e.getDropTargetContext ().getComponent ();
		return ((c instanceof DnDGlassPane) && ((DnDGlassPane) c).dndActive)
			? (DnDGlassPane) c : null;
	}


	DropTargetAdapter targetListener = new DropTargetAdapter ()
	{
		private DnDGlassPane current = null;


		@Override
		public void dragEnter (DropTargetDragEvent e)
		{
			if (current != null)
			{
				current.setDropPosition (null);
				current = null;
			}
			DnDGlassPane c;
			if ((c = check (e)) != null)
			{
				current = c;
				c.drag (e);
				e.acceptDrag (DnDConstants.ACTION_MOVE);
			}
			else
			{
				e.rejectDrag ();
			}
		}


		@Override
		public void dragExit (DropTargetEvent e)
		{
			if (current != null)
			{
				current.setDropPosition (null);
				current = null;
			}
		}


		@Override
		public void dragOver (DropTargetDragEvent e)
		{
			if (current != null)
			{
				if (check (e) == current)
				{
					current.drag (e);
				}
				else
				{
					current.setDropPosition (null);
					current = null;
				}
			}
			if (current == null)
			{
				e.rejectDrag ();
			}
			else
			{
				e.acceptDrag (DnDConstants.ACTION_MOVE);
			}
		}


		public void drop (DropTargetDropEvent e)
		{
			boolean reject = true;
			if (current != null)
			{
				if (e.isLocalTransfer () && (check (e) == current))
				{
					DockPosition loc = current.position;
					if (loc != null)
					{
						reject = false;
						e.acceptDrop (DnDConstants.ACTION_MOVE);
						DragDockableContext d = null;
						try
						{
							d = (DragDockableContext) e.getTransferable ()
								.getTransferData (DragDockableContext
												  .getFlavor ());
							if (getInfo ((Component) d.getDockableComponent ())
								== null)
							{
								d = null;
							}
						}
						catch (java.io.IOException ex)
						{
						}
						catch (java.awt.datatransfer
							   .UnsupportedFlavorException ex)
						{
						}
						catch (ClassCastException ex)
						{
						}
						if (d != null)
						{
							loc.drop (d);
							e.dropComplete (true);
						}
						else
						{
							e.dropComplete (false);
						}
					}
				}
			}
			current.setDropPosition (null);
			current = null;
			if (reject)
			{
				e.rejectDrop ();
			}
		}

	};

/*
	Component replace (Component old, Component n)
	{
		if (old.getParent () instanceof DockTabbedPane)
		{
			throw new AssertionError ();
		}
		else
		{
			if (n instanceof DockableComponent)
			{
				n = createFrameComponent ((DockableComponent) n);
			}
			replace0 (old, n);
			return n;
		}
	}
*/

	static void replace0 (Component old, Component n)
	{
		Container p = old.getParent ();
		for (int i = p.getComponentCount () - 1; i >= 0; i--)
		{
			if (p.getComponent (i) == old)
			{
				p.remove (i);
				if (p instanceof JComponent)
				{
					((JComponent) p).revalidate ();
				}
				p.add (n, i);
				return;
			}
		}
	}		


	static void translate (Point p, Container parent, Component child)
	{
		while (child != parent)
		{
			p.translate (-child.getX (), -child.getY ());
			child = child.getParent ();
		}
	}


	public LayoutConsumer setLayout ()
	{
		return setLayout (null);
	}


	public LayoutConsumer setLayout (final FloatingWindow window)
	{
		return new LayoutConsumer ()
		{
			private final Stack stack = new Stack ();
			private final HashSet toClose = new HashSet ();
			private RootInfo currentInfo, windowInfo;

			
			public void startLayout ()
			{
				RootInfo[] info = (RootInfo[]) roots.values ()
					.toArray (new RootInfo[roots.size ()]);
				RootInfo ti = null;
				for (int i = 0; i < info.length; i++)
				{
					ti = info[i];
					if ((window == null) || (window == ti.getWindow ()))
					{
						toClose.addAll (ti.release (false));
						if (window == null)
						{
							if (ti != mainInfo)
							{
								((FloatingWindow) ti.getWindow ()).dispose ();
							}
						}
						else
						{
							break;
						}
					}
				}
				currentInfo = windowInfo = (window == null) ? mainInfo : ti;
				if (ti != null)
				{
					stack.push (ti.dockRoot);
				}
			}

			
			public void endLayout ()
			{
				for (Iterator it = toClose.iterator (); it.hasNext (); )
				{
					((Dockable) it.next ()).dockableClosed ();
				}
				windowInfo.dockRoot.revalidate ();
			}

			
			public void startMainWindow ()
			{
				currentInfo = mainInfo;
				stack.push (currentInfo.dockRoot);
			}


			public void endMainWindow ()
			{
				stack.pop ();
				currentInfo = null;
			}


			public void startFloatingWindow (String title,
											 int width, int height)
			{
				stack.push (((width <= 0) || (height <= 0)) ? null
							: new Dimension (width, height));
				FloatingWindow w = createFloatingWindow (title, true);
				stack.push (w.getDockRoot ());
				currentInfo = (RootInfo) roots.get (w.getRootPane ());
			}


			public void endFloatingWindow ()
			{
				FloatingWindow f = (FloatingWindow) SwingUtilities
					.getWindowAncestor ((Component) stack.pop ());
				Object d = stack.pop ();
				if (d != null)
				{
					f.setSize ((Dimension) d);
					f.setLocationRelativeTo (mainInfo.dockRoot);
				}
				else
				{
					pack (f);
				}
				f.setVisible (true);
				currentInfo = null;
			}


			public void startSplit (int orientation, float location)
			{
				stack.push ((location < 0) ? null : new Float (location));
				stack.push (new DockSplitPane (DockManager.this,
											   orientation));
			}


			public void endSplit ()
			{
				DockSplitPane p = (DockSplitPane) stack.pop ();
				Object l = stack.pop ();
				switch (p.getDockComponentCount ())
				{
					case 0:
						break;
					case 1:
						DockComponent c = p.getDockComponent (0);
						p.remove (c);
						((DockContainer) stack.peek ()).add (-1, c);
						break;
					default:
						((DockContainer) stack.peek ()).add (-1, p);
						if (p.isEnabled ())
						{
							if (l != null)
							{
								p.setProportionalDividerLocation
									(((Float) l).floatValue ());
							}
							else
							{
								p.resetToPreferredSizes ();
							}
						}
						break;
				}
			}


			public void startTabbed (int selectedIndex)
			{
				stack.push ((selectedIndex < 0) ? null
							: Integer.valueOf (selectedIndex));
				stack.push (new DockTabbedPane (DockManager.this));
			}


			public void endTabbed ()
			{
				DockTabbedPane p = (DockTabbedPane) stack.pop ();
				Integer selected = (Integer) stack.pop ();
				switch (p.getDockComponentCount ())
				{
					case 0:
						break;
					case 1:
						DockComponent c = p.getDockComponent (0);
						p.remove (c);
						((DockContainer) stack.peek ()).add (-1, c);
						break;
					default:
						p.setSelectedIndex
							((selected == null) ? 0
							 : Math.min (selected.intValue (),
										 p.getTabCount () - 1));
						((DockContainer) stack.peek ()).add (-1, p);
						break;
				}
			}


			public void addDockable (Dockable dockable)
			{
				DockContainer c = (DockContainer) stack.peek ();
				dockable.setManager (DockManager.this);
				c.add (-1, dockable.getComponent ());
				toClose.remove (dockable);
			}
		};
	}


	public void supply (LayoutConsumer lc)
	{
		lc.startLayout ();
		lc.startMainWindow ();
		supply (lc, mainInfo.dockRoot);
		lc.endMainWindow ();
		for (Enumeration e = roots.elements (); e.hasMoreElements (); )
		{
			RootInfo i = (RootInfo) e.nextElement ();
			if (i != mainInfo)
			{
				FloatingWindow w = (FloatingWindow) i.getWindow ();
				lc.startFloatingWindow (w.getTitle (), w.getWidth (),
										w.getHeight ());
				supply (lc, i.dockRoot);
				lc.endFloatingWindow ();
			}
		}
		lc.endLayout ();
	}


	private static void supply (LayoutConsumer lc, Component c)
	{
		if (c instanceof DockableComponent)
		{
			lc.addDockable (((DockableComponent) c).getDockable ());
		}
		else if (c instanceof DockContainer)
		{
			DockContainer dc = (DockContainer) c;
			if (dc instanceof DockSplitPane)
			{
				DockSplitPane s = (DockSplitPane) dc;
				lc.startSplit (s.getOrientation (),
							   (Math.abs (s.getResizeWeight () - 0.5) > 0.4)
							   ? -1 : s.getProportionalDividerLocation ());
			}
			else if (dc instanceof DockTabbedPane)
			{
				lc.startTabbed (((DockTabbedPane) dc).getSelectedIndex ());
			}
			else if (!(dc instanceof DockContentPane))
			{
				throw new IllegalStateException ();
			}
			int n = dc.getDockComponentCount ();
			for (int i = 0; i < n; i++)
			{
				supply (lc, (Component) dc.getDockComponent (i));
			}
			if (dc instanceof DockSplitPane)
			{
				lc.endSplit ();
			}
			else if (dc instanceof DockTabbedPane)
			{
				lc.endTabbed ();
			}
		}
	}

/*
	public void add (Dockable dockable, int position, Dockable rel)
	{
		Component adjacent;
		if (rel == null)
		{
			adjacent = null;
		}
		else
		{
			adjacent = (Component) rel;
			Component c;
			for (c = adjacent.getParent (); c != null; c = c.getParent ())
			{
				if (c instanceof DockableComponent)
				{
					adjacent = (Component) c;
				}
				if (c instanceof DockContainer)
				{
					if (c instanceof DockTabbedPane)
					{
						position = DockPosition.TAB;
					}
					break;
				}
			}
			if (c == null)
			{
				adjacent = null;
			}
		}
		if (adjacent == null)
		{
			mainInfo.dockRoot.add (dockable);
		}
		else
		{
			addImpl (dockable, position, adjacent);
		}
	}
*/

/*
	void initialize (Dockable dockable, int position)
	{
		if (dockable instanceof DockableToolBar)
		{
			((DockableToolBar) dockable).setOrientation
				(((position & DockPosition.VERTICAL_MASK) != 0)
				 ? JToolBar.VERTICAL : JToolBar.HORIZONTAL);
		}
		dockable.setManager (this);
	}
*/

	void addImpl (Dockable dockable, int position, DockComponent adjacent)
	{
		dockable.setManager (this);
		if (adjacent instanceof Dockable)
		{
			adjacent = ((Dockable) adjacent).getComponent ();
		}
		DockContainer dc;
		if (adjacent instanceof DockContentPane)
		{
			dc = (DockContentPane) adjacent;
		}
		else if ((position & DockPosition.EDGE) != 0)
		{
			DockSplitPane sp = new DockSplitPane
				(this,
				 ((position & DockPosition.VERTICAL_MASK) != 0)
				 ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
			replace0 ((Component) adjacent, sp);
			sp.add (DockPosition.CENTER, adjacent);
			sp.resetToPreferredSizes ();
			dc = sp;
		}
		else if ((position & DockPosition.TAB_MASK) != 0)
		{
			DockTabbedPane p;
			if (adjacent instanceof DockTabbedPane)
			{
				p = (DockTabbedPane) adjacent;
				position = -1;
			}
			else if (adjacent.getParent () instanceof DockTabbedPane)
			{
				p = (DockTabbedPane) adjacent.getParent ();
				position = p.indexOfComponent ((Component) adjacent);
			}
			else
			{
				p = new DockTabbedPane (this);
				replace0 ((Component) adjacent, p);
				p.add (0, adjacent);
				position = ((position & DockPosition.CENTER) != 0) ? 1 : 0;
			}
			dc = p;
		}
		else
		{
			throw new AssertionError (position);
		}
		dc.add (position, dockable.getComponent ());
		select (dockable, false, null);
	}

/*
	Component getDockableComponent (Dockable d)
	{
		if ((d instanceof DockableToolBar) || (d instanceof DockablePanel))
		{
			return (Component) d;
		}
		else
		{
			return DockedFrame.createInstance (null, this, d);
		}
	}

/*
	public static void remove (Dockable d)
	{
		getDockContainer (d).remove (d);
	}


	public static DockContainer getDockContainer (Dockable d)
	{
		return getDockContainer ((Component) d);
	}
*/
//
//	public static void removeWrapper (Dockable d)
//	{
//		if (d.getComponent () instanceof DockableWrapper)
//		{
//			((DockableWrapper) d.getComponent ()).releaseDockable ();
//			d.setWrapper (null);
//		}
//	}
//

	public DockComponent wrap (DockComponent c, boolean wrap)
	{
		if (c instanceof Dockable)
		{
			Dockable d = (Dockable) c;
			if (wrap)
			{
				if (d.needsWrapper ())
				{
					if ((c = d.getComponent ()) == d)
					{
						c = new DockedFrame (this, d);
						d.setWrapper ((DockableWrapper) c);
					}
				}
			}
			else
			{
				if (d.getComponent () != d)
				{
					((DockableWrapper) d.getComponent ()).releaseDockable ();
					d.setWrapper (null);
				}
			}
		}
		else if ((c instanceof DockableWrapper) && !wrap)
		{
			DockableWrapper w = (DockableWrapper) c;
			c = w.getDockable ();
			w.releaseDockable ();
			((Dockable) c).setWrapper (null);
		}
		return c;
	}


	public static DockContainer getDockParent (DockComponent dc)
	{
		for (Component c = dc.getParent (); c != null; c = c.getParent ())
		{
			if (c instanceof DockContainer)
			{
				return (DockContainer) c;
			}
		}
		return null;
	}

/*
	public static Component getDockChild (Dockable d)
	{
		Component dc = (Component) d;
		for (Container c = dc.getParent (); c != null; c = c.getParent ())
		{
			if (c instanceof DockContainer)
			{
				return dc;
			}
			dc = c;
		}
		return null;
	}
*/

	public DockManager (JRootPane root, DockContentPane dockRoot)
	{
		super ();
		dragSource = new DragSource ();
		mainInfo = registerRoot (root, dockRoot);
	}


	private RootInfo registerRoot (JRootPane root, DockContentPane dockRoot)
	{
		dockRoot.setDockManager (this);
		RootInfo i = new RootInfo (this, root, dockRoot);
		roots.put (root, i);
		return i;
	}


	void unregisterRoot (JRootPane root)
	{
		RootInfo i = (RootInfo) roots.remove (root);
		if (i != null)
		{
			i.dispose (true);
		}
		if (root.getParent () instanceof FloatingWindow)
		{
			((FloatingWindow) root.getParent ()).removeWindowListener (this);
		}
	}


	private FloatingWindow createFloatingWindow (String title, boolean register)
	{
		Window mw = SwingUtilities.getWindowAncestor (mainInfo.dockRoot);
		FloatingWindow w = (mw instanceof Frame)
			? new FloatingWindow ((Frame) mw)
			: new FloatingWindow ((Dialog) mw);
		if (register)
		{
			registerRoot (w.getRootPane (), w.getDockRoot ());
		}
		w.addWindowListener (this);
		w.setTitle (title);
		return w;
	}


	@Override
	public void windowClosing (final WindowEvent e)
	{
		RootInfo i = (RootInfo) roots.get
			(((RootPaneContainer) e.getWindow ()).getRootPane ());
		final ArrayList list = new ArrayList ();
		i.getDockables (list, null);
		new Runnable ()
		{
			public void run ()
			{
				if (list.isEmpty ())
				{
					e.getWindow ().dispose ();
				}
				else
				{
					((Dockable) list.remove (0)).checkClose (this);
				}
			}
		}.run ();
	}


	public FloatingWindow createFloatingWindow (Dockable dockable)
	{
		FloatingWindow w = createFloatingWindow (dockable.getPanelTitle (), true);
		dockable.setManager (this);
		w.getDockRoot ().add (-1, dockable.getComponent ());
		pack (w);
		return w;
	}


	void pack (FloatingWindow w)
	{
		w.pack ();
		if ((w.getWidth () < 500) || (w.getHeight () < 300))
		{
			w.setSize (Math.max (w.getWidth (), 500),
					   Math.max (w.getHeight (), 300));
		}
		w.setLocationRelativeTo (mainInfo.dockRoot);
	}


	public void hideFloatingWindows ()
	{
		for (Enumeration w = roots.elements (); w.hasMoreElements (); )
		{
			RootInfo r = (RootInfo) w.nextElement ();
			if (r != mainInfo)
			{
				FloatingWindow f = (FloatingWindow) r.root.getParent ();
				if (f != null)
				{
					r.fwBounds = f.getBounds ();
					r.fwTitle = f.getTitle ();
					f.setVisible (false);
					f.setRootPane0 (null);
					f.superDispose ();
				}
			}
		}
	}


	public void showFloatingWindows ()
	{
		for (Enumeration w = roots.elements (); w.hasMoreElements (); )
		{
			RootInfo r = (RootInfo) w.nextElement ();
			if ((r != mainInfo) && (r.root.getParent () == null))
			{
				FloatingWindow f = createFloatingWindow (r.fwTitle, false);
				f.setRootPane0 (r.root);
				r.dockRoot.windowChanged ();
				f.pack ();
				f.setBounds (r.fwBounds);
				f.setVisible (true);
			}
		}
	}


	private Dockable curKeepInFront;

	public void select (Dockable d, boolean moveToFront, Dockable keepInFront)
	{
		Dockable ckif = curKeepInFront;
		if (keepInFront == null)
		{
			keepInFront = ckif;
		}
		if (keepInFront == d)
		{
			keepInFront = null;
		}
		if (keepInFront != null)
		{
			curKeepInFront = keepInFront;
		}
		try
		{
			if (d != null)
			{
				if (d != selected)
				{
					if ((keepInFront != null)
						&& (keepInFront.getDockParent () == d.getDockParent ()))
					{
						return;
					}
					if (d.isSelectable ())
					{
						if (selected != null)
						{
							selected.setSelected (false);
						}
						d.setSelected (true);
						selected = d;
					}
					d.getDockParent ().toFront (d.getComponent ());
				}
				if ((keepInFront != null) || !moveToFront)
				{
					return;
				}
				Window w = SwingUtilities.getWindowAncestor ((Component) d);
				if (w != null)
				{
					w.toFront ();
				}
			}
			else if (selected != null)
			{
				selected.setSelected (false);
				selected = null;
			}
		}
		finally
		{
			curKeepInFront = ckif;
		}
	}


	public void floatDockable (Dockable d)
	{
		DockableComponent dc = d.getComponent ();
		for (Component c = dc.getParent (); c != null; c = c.getParent ())
		{
			if (c instanceof DockableWrapper)
			{
				dc = (DockableWrapper) c;
			}
			if (c instanceof DockContainer)
			{
				((DockContainer) c).remove (dc);
				break;
			}
		}
		FloatingWindow w = createFloatingWindow (d);
		pack (w);
		w.setVisible (true);
	}


	public void closeDockable (Dockable d)
	{
		DockableComponent dc = d.getComponent ();
		for (Component c = dc.getParent (); c != null; c = c.getParent ())
		{
			if (c instanceof DockableWrapper)
			{
				dc = (DockableWrapper) c;
			}
			if (c instanceof DockContainer)
			{
				((DockContainer) c).remove (dc);
				break;
			}
		}
		d.dockableClosed ();
	}


	public Dockable[] dispose ()
	{
		ArrayList l = new ArrayList ();
		for (Enumeration w = roots.elements (); w.hasMoreElements (); )
		{
			RootInfo i = (RootInfo) w.nextElement ();
			l.addAll (i.dispose (false));
			if (i.getWindow () instanceof FloatingWindow)
			{
				((FloatingWindow) i.getWindow ()).superDispose ();
			}
		}
		roots.clear ();
		return (Dockable[]) l.toArray (new Dockable[l.size ()]);
	}


	public Dockable[] closeDockables ()
	{
		ArrayList l = new ArrayList ();
		for (Enumeration w = roots.elements (); w.hasMoreElements (); )
		{
			RootInfo i = (RootInfo) w.nextElement ();
			if (i.getWindow () instanceof FloatingWindow)
			{
				l.addAll (i.dispose (false));
				((FloatingWindow) i.getWindow ()).superDispose ();
			}
			else
			{
				assert i == mainInfo;
				l.addAll (i.release (false));
			}
		}
		roots.clear ();
		roots.put (mainInfo.root, mainInfo);
		return (Dockable[]) l.toArray (new Dockable[l.size ()]);
	}


	public Dockable[] getDockables (DockableFilter filter)
	{
		ArrayList l = new ArrayList ();
		for (Enumeration w = roots.elements (); w.hasMoreElements (); )
		{
			((RootInfo) w.nextElement ()).getDockables (l, filter);
		}
		return (Dockable[]) l.toArray (new Dockable[l.size ()]);
	}


	public void beginDraggingFrame (JComponent f)
	{
	}


	public void dragFrame (JComponent f, int newX, int newY)
	{
	}


	public void endDraggingFrame (JComponent f)
	{
	}


	void setDnD (boolean dnd)
	{
		synchronized (roots)
		{
			for (Enumeration w = roots.elements (); w.hasMoreElements (); )
			{
				((RootInfo) w.nextElement ()).setDnD (dnd);
			}
		}
	}


	static void uninstallListeners (Object[] a)
	{
		if (a == null)
		{
			return;
		}
		if (a[0] instanceof DragGestureRecognizer)
		{
			((DragGestureRecognizer) a[0]).setComponent (null);
		}
		a[0] = null;
		if (a[1] instanceof MouseListener)
		{
			((Component) a[2]).removeMouseListener ((MouseListener) a[1]);
		}
		a[1] = null;
		a[2] = null;
	}


	Object[] installListeners (Component c, DragGestureListener dgl,
							   Dockable d)
	{
		return installListeners (c, dgl, new DockMenuListener (this, d));
	}


	Object[] installListeners (Component c, DragGestureListener dgl,
							   MouseListener ml)
	{
		Object[] a = new Object[3];
		try
		{
			DragGestureRecognizer dgr
				= dragSource.createDefaultDragGestureRecognizer
					(c, DnDConstants.ACTION_MOVE, null);
			dgr.addDragGestureListener (dgl);
			a[0] = dgr;
		}
		catch (TooManyListenersException e)
		{
			throw new AssertionError (e);
		}
		c.addMouseListener (ml);
		a[1] = ml;
		a[2] = c;
		return a;
	}


	public void dragGestureRecognized (DragGestureEvent e)
	{
		dragGestureRecognized (e, ((DragSourceComponent) e.getComponent ())
							   .createContext (e));
	}


	void dragGestureRecognized (DragGestureEvent e, DragDockableContext ctx)
	{
		if (ctx == null)
		{
			return;
		}
//		Icon i = DragSource.isDragImageSupported ()
//			? ctx.getDockable ().getIcon () : null;
		setDnD (true);
		e.startDrag (DragSource.DefaultMoveNoDrop,
					 null/*de.grogra.icon.IconUtils.getImage (e.getComponent (),
															 i)*/,
					 new Point (0, 0), ctx, sourceListener);
	}


	RootInfo getInfo (Component c)
	{
		while (c != null)
		{
			if (c instanceof JRootPane)
			{
				Object i = roots.get (c);
				if (i != null)
				{
					return (RootInfo) i;
				}
			}
			if (c instanceof Window)
			{
				return null;
			}
			c = c.getParent ();
		}
		return null;
	}

}
