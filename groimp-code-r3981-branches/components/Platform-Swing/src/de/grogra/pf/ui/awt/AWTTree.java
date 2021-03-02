
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

package de.grogra.pf.ui.awt;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.tree.TreePath;
import de.grogra.util.*;
import de.grogra.icon.IconSource;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.awt.LabelRenderer.LabelData;
import de.grogra.pf.ui.tree.*;

public class AWTTree extends MappedComponentModel implements LayoutManager2
{
	// the two background colors for the shader tree display
	public static final Color color1 = new Color(0xF0F0F0);
	public static final Color color2 = new Color(0xE0E0E0);

	
	private static final class ButtonIcon implements Icon
	{
		private final int size;
		private final boolean pressed, expanded;
		private final int[] arrowX = new int[5], arrowY = new int[5];


		ButtonIcon (boolean expanded, boolean pressed)
		{
			this.expanded = expanded;
			this.pressed = pressed;
			size = 16;
		}


		public int getIconWidth ()
		{
			return size;
		}


		public int getIconHeight ()
		{
			return size;
		}


		public void paintIcon (Component c, Graphics g, int x, int y)
		{
			g.setColor (pressed ? Color.darkGray : Color.white);
			g.drawLine (x, y + size - 2, x, y);
			g.drawLine (x + 1, y, x + size - 1, y);
			g.setColor (pressed ? Color.white : Color.darkGray);
			g.drawLine (x, y + size - 1, x + size - 1, y + size - 1);
			g.drawLine (x + size - 1, y + size - 2, x + size - 1, y);
			x += size / 2 - 1;
			y += size / 2 - 1;
			if (pressed)
			{
				x++;
				y++;
			}
			int d = 3, n;
			if (expanded)
			{
				y -= 1;
				arrowX[0] = x - d; arrowY[0] = y - 1;
				arrowX[1] = x - d; arrowY[1] = y;
				arrowX[2] = x; arrowY[2] = y + d + 1;
				arrowX[3] = x + d + 1; arrowY[3] = y;
				arrowX[4] = x + d + 1; arrowY[4] = y - 1;
				n = 5;
			}
			else
			{
				x -= 2;
				arrowX[0] = x; arrowY[0] = y - d - 1;
				arrowX[1] = x + d + 2; arrowY[1] = y;
				arrowX[2] = x; arrowY[2] = y + d + 1;
				n = 3;
			}
/*			if (expanded)
			{
				arrowX[0] = x - d; arrowY[0] = y - d;
				arrowX[1] = x; arrowY[1] = y + d;
				arrowX[2] = x + d; arrowY[2] = y - d;
			}
			else
			{
				arrowX[0] = x - d; arrowY[0] = y - d;
				arrowX[1] = x + d; arrowY[1] = y;
				arrowX[2] = x - d; arrowY[2] = y + d;
			}
*/
			g.setColor (c.getForeground ());
			g.fillPolygon (arrowX, arrowY, n);
		}
	}


	protected static final Icon ICON_COLLAPSED_NORMAL = new ButtonIcon (false,
																		false);
	protected static final Icon ICON_EXPANDED_NORMAL = new ButtonIcon (true,
																	   false);
	protected static final Icon ICON_COLLAPSED_PRESSED = new ButtonIcon (false,
																		 true);
	protected static final Icon ICON_EXPANDED_PRESSED = new ButtonIcon (true,
																		true);


	protected final Insets rootInsets = new Insets (0, 0, 0, 0);


	private static final int MINIMUM = 0;
	private static final int PREFERRED = 1;
	private static final int MAXIMUM = 2;
	private static final int LAYOUT = 3;
	private static final int COMPONENT_X = 4;


	static class LayoutInfo extends Rectangle
	{
		int maxLabelWidth;
		Component label;
		LabelData renderer;
		
		Rectangle labelBounds = new Rectangle ();
	}


	abstract static class NodeBase extends Container
	{
		final Object source;
		final LayoutInfo layoutInfo = new LayoutInfo ();


		NodeBase (Object source)
		{
			super ();
			this.source = source;
		}


		void setTree (AWTTree tree)
		{
			setLayout (tree);
			tree.createLabel (this);
		}


		AWTTree getTree ()
		{
			return (AWTTree) getLayout ();
		}
	}


	static final class Leaf extends NodeBase
	{
		ComponentWrapper component;
		Rectangle compBounds = new Rectangle ();


		Leaf (Object source)
		{
			super (source);
		}
	}


	static final class Node extends NodeBase
	{
		boolean expanded = true, pressed = false;
		int depth;
		Insets layoutInsets = new Insets (0, 0, 0, 0);
		int iconLeft, iconRight, iconBottom;

		private Object[] icons = new Object[4];
		private long releaseTime = 0;

		Node (Object source, int depth)
		{
			super (source);
			this.depth = depth;
			enableEvents (AWTEvent.MOUSE_EVENT_MASK
						  | AWTEvent.MOUSE_MOTION_EVENT_MASK);
			
			// force setting of background color
			setExpanded(true);
		}


		public void setExpanded (boolean expanded)
		{
			// set background color depending on depth and expanded state
			setBackground(expanded ^ (depth & 1) == 1 ? color1 : color2);
			
			if (this.expanded != expanded)
			{
				this.expanded = expanded;
				for (int i = 1, n = getComponentCount (); i < n; i++)
				{
					getComponent (i).setVisible (expanded);
				}
				AWTTree t = getTree ();
				if (t != null)
				{
					t.revalidate (this);
				}
			}
		}


		public void expandAndCollapseOthers ()
		{
			Container node = this;
			AWTTree t = getTree ();
			Container r = (t != null) ? t.root : null;
			while (true)
			{
				Container c = node.getParent ();
				if (c == null)
				{
					return;
				}
				for (int i = 0, n = c.getComponentCount (); i < n; i++)
				{
					Component child = c.getComponent (i);
					if (child instanceof Node)
					{
						((Node) child).setExpanded (child == node);
					}
				}
				if (c == r)
				{
					return;
				}
				node = c;
			}
		}


		public boolean isExpanded ()
		{
			return expanded;
		}


		private boolean isOverButton (MouseEvent event)
		{
			return (0 <= event.getX ()) && (event.getX () < iconRight)
				&& (0 <= event.getY ()) && (event.getY () < iconBottom);
		}


		private void setPressed (boolean pressed)
		{
			if (this.pressed != pressed)
			{
				this.pressed = pressed;
				AWTTree t = getTree ();
				if (t != null)
				{
					t.root.repaint ();
				}
			}
		}


		@Override
		protected void processMouseEvent (MouseEvent event)
		{
			switch (event.getID ())
			{
				case MouseEvent.MOUSE_PRESSED:
					if (isOverButton (event))
					{
						setPressed (true);
					}
					break;
				case MouseEvent.MOUSE_RELEASED:
					if (pressed)
					{
						setPressed (false);
						if (event.getWhen () - releaseTime < 300)
						{
							expandAndCollapseOthers ();
						}
						else
						{
							setExpanded (!expanded);
						}
						releaseTime = event.getWhen ();
					}
					break;
			}
		}


		@Override
		protected void processMouseMotionEvent (MouseEvent event)
		{
			setPressed (false);
		}


		Icon getButtonIcon ()
		{
			int i = (expanded ? 1 : 0) | (pressed ? 2 : 0);
			Object o = icons[i];
			if (o == this)
			{
				return null;
			}
			else if (o != null)
			{
				return (Icon) o;
			}
			AWTTree t = getTree ();
			if (t == null)
			{
				return null;
			}
			o = t.getButtonIcon (depth, expanded, pressed);
			icons[i] = (o == null) ? this : o;
			return (Icon) o;
		}

	}


	private Disposable toDispose;
	private final LabelRenderer renderer = new LabelRenderer (sourceTree.getContext (), 0, false)
	{
		private Object src (LabelData n)
		{
			return getSourceOf (((LayoutInfo) n.getUserData ()).label.getParent ());
		}

		@Override
		protected String getText (LabelData n)
		{
			return (String) sourceTree.getDescription
				(src (n), Described.NAME);
		}
			
		@Override
		protected IconSource getIconSource (LabelData n)
		{
			return (IconSource) sourceTree.getDescription
				(src (n), Described.ICON);
		}

		@Override
		protected void updateNodes (LabelData[] nodes, boolean layout)
		{
			if (layout)
			{
				for (int i = nodes.length - 1; i >= 0; i--)
				{
					LayoutInfo li = (LayoutInfo) nodes[i].getUserData ();
					li.label.invalidate ();
				}
				root.validate ();
			}
			for (int i = nodes.length - 1; i >= 0; i--)
			{
				LayoutInfo li = (LayoutInfo) nodes[i].getUserData ();
				getToolkit ().updateLabel
					(li.label, nodes[i].getText(), nodes[i].getIcon ());
				li.label.repaint ();
			}
		}
	};


	private LayoutInfo rootInfo = new LayoutInfo ();


	public AWTTree (UITree sourceTree, Disposable toDispose, Container root)
	{
		super (sourceTree);
		this.toDispose = toDispose;
		this.root = root;
		root.setLayout (this);
	}


	@Override
	protected void disposeImpl ()
	{
		if (toDispose != null)
		{
			toDispose.dispose ();
			toDispose = null;
		}
	}

	
	Object getSourceOf (Container comp)
	{
		return (comp == root) ? sourceRoot : ((NodeBase) comp).source;
	}


	public void addLayoutComponent (String name, Component comp)
	{
		addLayoutComponent (comp, name);
	}

	
	private LayoutInfo getLayoutInfo (Container c)
	{
		return (c == root) ? rootInfo : ((NodeBase) c).layoutInfo;
	}


	public void addLayoutComponent (Component comp, Object constraints)
	{
		if (comp instanceof NodeBase)
		{
			((NodeBase) comp).setLayout (this);
		}
		else
		{
			LayoutInfo i = getLayoutInfo (comp.getParent ());
			if (comp == i.label)
			{
				int w = comp.getPreferredSize ().width;
				if (w > i.maxLabelWidth)
				{
					i.maxLabelWidth = w;
				}
			}
			else if (comp instanceof Leaf)
			{
				LayoutInfo l = ((Leaf) comp).layoutInfo;
				if (l.maxLabelWidth > i.maxLabelWidth)
				{
					i.maxLabelWidth = l.maxLabelWidth;
				}
			}
		}
	}


	public void removeLayoutComponent (Component comp)
	{
	}


	public void layoutContainer (Container parent)
	{
		synchronized (parent.getTreeLock ())
		{
			if (parent == root)
			{
				layout (LAYOUT);
			}
			applyLayout (parent);
		}
	}


	private void applyLayout (Container parent)
	{
		for (int i = 0, n = parent.getComponentCount (); i < n; i++)
		{
			Component c = parent.getComponent (i);
			if (c instanceof NodeBase)
			{
				NodeBase node = (NodeBase) c;
				node.setBounds (node.layoutInfo);
			}
			else
			{
				LayoutInfo li = getLayoutInfo (parent);
				c.setBounds ((li.label == c) ? li.labelBounds
							 : ((Leaf) parent).compBounds);
			}
		}
	}


	public void invalidateLayout (Container parent)
	{
	}


	public float getLayoutAlignmentX (Container target)
	{
		return Component.CENTER_ALIGNMENT;
	}


	public float getLayoutAlignmentY (Container target)
	{
		return Component.CENTER_ALIGNMENT;
	}


	private Dimension layoutSize (Container parent, int size)
	{
		if (parent != root)
		{
			return parent.getSize ();
		}
		layout (size);
		return rootInfo.getSize ();
	}


	public Dimension minimumLayoutSize (Container parent)
	{
		return layoutSize (parent, MINIMUM);
	}


	public Dimension preferredLayoutSize (Container parent)
	{
		return layoutSize (parent, PREFERRED);
	}


	public Dimension maximumLayoutSize (Container parent)
	{
		return layoutSize (parent, MAXIMUM);
	}


	public void paint (Graphics g)
	{
		paintRoot (g);
		paintChildren (root, 0, g);
	}


	private void paintChildren (Container c, int depth, Graphics g)
	{
		for (int i = 0, count = c.getComponentCount (); i < count; i++)
		{
			Component child = c.getComponent (i);
			if (child instanceof NodeBase)
			{
				g.translate (child.getX (), child.getY ());
				if (child instanceof Node)
				{
					Node n = (Node) child;				
					paintNode (n, depth, n.expanded, n.pressed, g);
					if (n.expanded)
					{
						paintChildren (n, depth + 1, g);
					}
				}
				else
				{
					paintChildren ((Container) child, depth + 1, g);
				}
				g.translate (-child.getX (), -child.getY ());
			}
		}
	}


	protected void paintRoot (Graphics g)
	{
		g.setColor(color2);
		g.fillRect(0, 0, root.getWidth(), root.getHeight());
	}

	private static Color lighten(Color color)
	{
		// convert color to HSB
		float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		// increase brightness
		hsb[2] = (float)Math.min(hsb[2] + 0.1, 1.0);
		// return new color based on hsb
		return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
	}

	protected void paintNode (Node n, int depth, boolean expanded,
							  boolean pressed, Graphics g)
	{
		// get background color of the displayed node 
		Color color = n.getBackground();
		
		// obtain drawable area
		int w = n.getWidth ();
		int h = n.getHeight ();
		int x0 = 1;
		int x1 = w - 3;
		int y0 = 1;
		int y1 = h - 3;

		// clear background
		g.setColor(color);
		g.fillRect(x0, y0, w - 3, h - 3);
		
		// draw the button
		Icon i = n.getButtonIcon ();
		if (i != null)
		{
			i.paintIcon (root, g, n.iconLeft, 4);
		}
		
		// draw a border if area is expanded
		if (expanded)
		{
			g.setColor (Color.darkGray);
			g.drawRect (x0 + 1, y0 + 1, x1 - x0, y1 - y0);
			g.setColor (Color.white);
			g.drawRect (x0, y0, x1 - x0, y1 - y0);
		}
	}


	private final Insets layoutInsets = new Insets (0, 0, 0, 0);
	private static final Insets nullInsets = new Insets (0, 0, 0, 0);
	private int globalComponentX = 0;

	private void layout (int size)
	{
		Insets i = root.getInsets ();
		layoutInsets.left = i.left;
		layoutInsets.right = i.right;
		layoutInsets.top = i.top;
		layoutInsets.bottom = i.bottom;

		i = layoutInsets;
		i.left += rootInsets.left;
		i.right += rootInsets.right;
		i.top += rootInsets.top;
		i.bottom += rootInsets.bottom;

		layoutChildren (root, i, COMPONENT_X, i);
		layoutChildren (root, i, size, i);
	}


	private void layoutChildren (Container c, Insets ci, int size,
								 Insets global)
	{
		int y = ci.top;
		int cw = 0;
		LayoutInfo li = getLayoutInfo (c);
		if (c instanceof Leaf)
		{
			Leaf l = (Leaf) c;
			int index;
			if (li.label != null)
			{
				li.labelBounds.height = li.label.getPreferredSize ().height;
				li.labelBounds.x = ci.left;
				li.labelBounds.y = y;
				li.labelBounds.width = globalComponentX - getLabelGap ()
					- global.left;
				index = 1;
			}
			else
			{
				li.labelBounds.height = 0;
				index = 0;
			}
			Component child = (c.getComponentCount () == index) ? null
				: c.getComponent (index);
			if ((child != null) && (size != COMPONENT_X))
			{
				l.compBounds.y = y;
				Dimension s;
				switch (size)
				{
					case MINIMUM:
						s = child.getMinimumSize ();
						break;
					case PREFERRED:
						s = child.getPreferredSize ();
						break;
					case MAXIMUM:
						s = child.getMaximumSize ();
						break;
					default:
						s = null;
						break;
				}
				if (s != null)
				{
					l.compBounds.setSize (s);
					int w = s.width;
					if (li.label != null)
					{
						w += globalComponentX - global.left;
					}
					cw = Math.max (cw, w);
				}
				else
				{
					l.compBounds.x = (li.label != null)
						? globalComponentX - global.left + ci.left
						: ci.left;
					s = child.getPreferredSize ();
					l.compBounds.height = s.height;
					l.compBounds.width = Math.min
						(root.getWidth () - global.right - global.left
						 - l.compBounds.x + ci.left,
						 (s.width + 127) & ~127);
				}
				y += Math.max (li.labelBounds.height, l.compBounds.height);
			}
			else
			{
				y += li.labelBounds.height;
			}
		}
		else
		{
			int x2 = ci.left;
			int y2 = y;
			if (c instanceof Node)
			{
				Icon i = ((Node) c).getButtonIcon ();
				if (i != null)
				{
					x2 += i.getIconWidth () + getIconGap ();
					y2 += i.getIconHeight () + getIconGap ();
				}
			}
			if (li.label != null)
			{
				li.labelBounds.x = x2;
				li.labelBounds.y = y;
				li.labelBounds.setSize (li.label.getPreferredSize ());
				y2 = Math.max (y2, y + li.labelBounds.height + 4);
			}
			if (c instanceof Node)
			{
				((Node) c).iconLeft = ci.left;
				((Node) c).iconBottom = y2;
				((Node) c).iconRight = x2;
			}
			y = y2;
			if (!(c instanceof Node) || ((Node) c).expanded)
			{
				for (int i = (li.label != null) ? 1 : 0,
					 count = c.getComponentCount (); i < count; i++)
				{
					NodeBase n = (NodeBase) c.getComponent (i);
					n.layoutInfo.x = ci.left;
					n.layoutInfo.y = y;
					if (size == LAYOUT)
					{
						n.layoutInfo.width = root.getWidth ()
							- global.left - global.right;
					}

					Insets ni;
					if (n instanceof Node)
					{
						ni = ((Node) n).layoutInsets;
						ni.left = 4;
						ni.right = 4;
						ni.top = 4;
						ni.bottom = 4;
					}
					else
					{
						ni = nullInsets;
					}
					global.left += ni.left;
					global.right += ni.right;
					global.top += ni.top;
					global.bottom += ni.bottom;
					if (size == COMPONENT_X)
					{
						int x = global.left + n.layoutInfo.maxLabelWidth + getLabelGap ();
						if (x > globalComponentX)
						{
							globalComponentX = x;
						}
					}
					layoutChildren (n, ni, size, global);
					global.left -= ni.left;
					global.right -= ni.right;
					global.top -= ni.top;
					global.bottom -= ni.bottom;
					y += n.layoutInfo.height;
					cw = Math.max (cw, n.layoutInfo.width);
				}
			}
		}
		cw += ci.left + ci.right;
		li.height = y + ci.bottom;
		if (size != LAYOUT)
		{
			li.width = cw;
		}
	}


	public Object createNode (Object sourceNode, Object targetParent)
	{
		if (isSourceRoot (sourceNode))
		{
			createLabel (root);
			return root;
		}
		else
		{
			NodeBase n;
			if (sourceTree.isLeaf (sourceNode))
			{
				Leaf leaf = new Leaf (sourceNode);
				leaf.component = (ComponentWrapper) sourceTree.invoke
					(sourceNode, UIToolkit.CREATE_COMPONENT_WRAPPER_METHOD, null);
				if (leaf.component != null)
				{
					setEnabled ((Component) leaf.component.getComponent (),
								sourceTree.isEnabled (sourceNode));
					leaf.add ((Component) leaf.component.getComponent (), null);
				}
				n = leaf;
			}
			else
			{
				n = new Node
					(sourceNode, (targetParent == root)
					 ? 0 : ((Node) targetParent).depth + 1);
			}
			n.setTree (this);
			return n;
		}
	}


	public void valueForPathChanged (TreePath path, Object newValue)
	{
		Container c = (Container) path.getLastPathComponent ();
		LabelData r = getLayoutInfo (c).renderer;
		if (r != null)
		{
			r.invalidate ();
			r.revalidate ();
		}
		if (c instanceof Leaf)
		{
			Leaf leaf = (Leaf) c;
			if (leaf.component != null)
			{
				sourceTree.invoke
					(leaf.source, UIToolkit.UPDATE_COMPONENT_WRAPPER_METHOD,
					 leaf.component);
				setEnabled ((Component) leaf.component.getComponent (),
							sourceTree.isEnabled (leaf.source));
			}
		}
	}


	void createLabel (Container node)
	{
		Object src = getSourceOf (node);
		String s = (String) sourceTree.getDescription (src, Described.NAME);
		IconSource i = (IconSource) sourceTree.getDescription (src, Described.ICON);
		if ((s == null) && (i == null))
		{
			return;
		}
		LayoutInfo info = getLayoutInfo (node);
		LabelData ld = renderer.new LabelData (info);
		info.renderer = ld;
		info.label = getToolkit ().createLabel
			(s, ld, (node instanceof Leaf) ? UIToolkit.ALIGNMENT_TRAILING : UIToolkit.ALIGNMENT_LEADING);
		node.add (info.label, 0);
		ld.revalidate ();
	}


	public void disposeNode (Object targetNode)
	{
		if (targetNode instanceof Leaf)
		{
			((Leaf) targetNode).component.dispose ();
		}
	}


	@Override
	public boolean isLeaf (Object node)
	{
		return node instanceof Leaf;
	}


	public boolean isImage (Object sourceNode, Object targetNode)
	{
		return isSourceRoot (sourceNode) ? root == targetNode
			: ((NodeBase) targetNode).source == sourceNode;
	}


	protected Icon getButtonIcon (int depth, boolean expanded, boolean pressed)
	{
		return expanded
			? (pressed ? ICON_EXPANDED_PRESSED : ICON_EXPANDED_NORMAL)
			: (pressed ? ICON_COLLAPSED_PRESSED : ICON_COLLAPSED_NORMAL);
	}


	protected Icon getIcon (Object node, int depth, boolean expanded)
	{
		return de.grogra.icon.IconAdapter.create
			((de.grogra.icon.IconSource) sourceTree.getDescription
			 (node, Described.ICON), AWTToolkitBase.MENU_ICON_SIZE);
	}


	protected String getText (Object node, int depth, boolean expanded)
	{
		return (String) sourceTree.getDescription
			(node, Described.NAME);
	}


	protected int getIconGap ()
	{
		return 4;
	}


	protected int getLabelGap ()
	{
		return 4;
	}


	@Override
	protected int getPrefixComponentCount (Container c)
	{
		return (getLayoutInfo (c).label != null) ? 1 : 0;
	}

}
