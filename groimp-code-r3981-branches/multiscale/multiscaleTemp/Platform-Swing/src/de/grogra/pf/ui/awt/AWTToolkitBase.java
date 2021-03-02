
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
import java.awt.event.*;
import javax.swing.JComponent;
import de.grogra.icon.IconAdapter;
import de.grogra.icon.IconSource;
import de.grogra.pf.ui.*;

public abstract class AWTToolkitBase extends UIToolkit
	implements WindowListener
{
	private static final int[] FONT_MASKS
		= {FONT_MONOSPACED, FONT_SANS_SERIF, FONT_SERIF, FONT_DIALOG,
		   FONT_DIALOG_INPUT};

	private static final String[] FONTS
		= {"Monospaced", "SansSerif", "Serif", "Dialog", "DialogInput"};


	protected Font getFont (int labelFlags)
	{
		int style;
		if ((labelFlags & (FONT_BOLD | FONT_ITALIC))
			== (FONT_BOLD | FONT_ITALIC))
		{
			style = Font.BOLD | Font.ITALIC;
		}
		else if ((labelFlags & FONT_BOLD) != 0)
		{
			style = Font.BOLD;
		}
		else if ((labelFlags & FONT_ITALIC) != 0)
		{
			style = Font.ITALIC;
		}
		else
		{
			style = Font.PLAIN;
		}
		return ((labelFlags & FONT_MASK) == 0) ? null
			: new Font ((String) getFirstMatching
						(labelFlags, FONT_MASKS, FONTS, null),
						style, ((labelFlags & FONT_SIZE_MASK) != 0)
						? labelFlags & FONT_SIZE_MASK : 12);
	}


	protected abstract Container getContentPane (Object container);


	@Override
	public void addComponent (final Object container, final Object component,
							  final Object constraints, final int index)
	{
		if (component != null)
		{
			if (EventQueue.isDispatchThread ())
			{
				getContentPane (container)
					.add ((Component) component, constraints, index);
			}
			else
			{
				AWTSynchronizer.staticInvokeAndWait (new Runnable ()
					{
						public void run ()
						{
							getContentPane (container).add
								((Component) component, constraints, index);
						}
					});
			}
		}
	}


	@Override
	public void removeComponent (Object component)
	{
		if (component != null)
		{
			final Component c = (Component) component;
			if (EventQueue.isDispatchThread ())
			{
				c.getParent ().remove (c);
			}
			else
			{
				AWTSynchronizer.staticInvokeAndWait (new Runnable ()
					{
						public void run ()
						{
							c.getParent ().remove (c);
						}
					});
			}
		}
	}


	@Override
	public int indexOf (Object component)
	{
		return indexOf ((Component) component);
	}


	@Override
	public int getComponentCount (Object container)
	{
		return (container instanceof Container)
			? getContentPane (container).getComponentCount ()
			: 0;
	}


	@Override
	public Object getComponent (Object container, int index)
	{
		return getContentPane (container).getComponent (index);
	}


	@Override
	public Object createLabeledComponent (Object component, Object label)
	{
		Container c = new Container ();
		c.setLayout (new BorderLayout ());
		c.add ((Component) label, BorderLayout.PAGE_START);
		c.add ((Component) component, BorderLayout.CENTER);
		return c;
	}
	

	@Override
	public Object createLabel (String text, IconSource icon, Dimension size,
							   int flags)
	{
		if ((icon != null) && ((flags & FORCE_DIMENSION) == 0))
		{
			Dimension p = icon.getPreferredIconSize (false);
			if (p != null)
			{
				size = p;
			}
		}
		IconAdapter a = IconAdapter.create (icon, size);
		if (a != null)
		{
			a.getIcon ().prepareIcon ();
		}
		Component l = createLabel (text, a, flags);
		Font f = getFont (flags);
		if (f != null)
		{
			l.setFont (f);
		}
		return l;
	}


	protected abstract Component createLabel
		(String text, javax.swing.Icon icon, int flags);
	

	protected abstract void updateLabel
		(Component label, String text, javax.swing.Icon icon);


	@Override
	public Object createContainer (int gap)
	{
		return createContainer (new BorderLayout (gap, gap));
	}


	@Override
	public Object createContainer (int rows, int cols, int gap)
	{
		return createContainer (new GridLayout (rows, cols, gap, gap));
	}


	@Override
	public Object createContainer (final float[] weights, final int gap)
	{
		return createContainer (new LayoutManager ()
		{
			public void removeLayoutComponent (Component comp)
			{
			}


			public void layoutContainer (Container parent)
			{
				Insets i = parent.getInsets ();
				int x = i.left, n = parent.getComponentCount (),
					h = parent.getHeight () - i.top - i.bottom;
				float f = 0;
				for (int k = n - 1; k >= 0; k--)
				{
					f += weights[k];
				}
				f = (parent.getWidth () - x - i.right - (n - 1) * gap) / f;
				if (f < 0)
				{
					f = 0;
				}
				float fx = x;
				for (int k = 0; k < n; k++)
				{
					Component c = parent.getComponent (k);
					fx += weights[k] * f;
					int w = (int) fx - x;
					c.setBounds (x, i.top, w, h);
					fx += gap;
					x += w + gap;
				}
			}


			public void addLayoutComponent (String name, Component comp)
			{
			}


			public Dimension minimumLayoutSize (Container parent)
			{
				Insets i = parent.getInsets ();
				Dimension d = new Dimension
					(i.left + i.right, i.top + i.bottom);
				int maxh = 0;
				for (int k = parent.getComponentCount () - 1; k >= 0; k--)
				{
					Dimension s = parent.getComponent (k).getMinimumSize ();
					d.width += s.width;
					maxh = Math.max (maxh, s.height);
					if (k > 0)
					{
						d.width += gap;
					}
				}
				d.height += maxh;
				return d;
			}


			public Dimension preferredLayoutSize (Container parent)
			{
				Insets i = parent.getInsets ();
				Dimension d = new Dimension
					(i.left + i.right, i.top + i.bottom);
				int maxh = 0;
				for (int k = parent.getComponentCount () - 1; k >= 0; k--)
				{
					Dimension s = parent.getComponent (k).getPreferredSize ();
					d.width += s.width;
					maxh = Math.max (maxh, s.height);
					if (k > 0)
					{
						d.width += gap;
					}
				}
				d.height += maxh;
				return d;
			}

		});
	}


	protected abstract Container createContainer (LayoutManager layout);


	@Override
	public Point getLocationOnScreen (Object component)
	{
		return ((Component) component).getLocationOnScreen ();
	}


	@Override
	public int getWidth (Object component)
	{
		return ((Component) component).getWidth ();
	}


	@Override
	public int getHeight (Object component)
	{
		return ((Component) component).getHeight ();
	}


	public void windowOpened (WindowEvent e)
	{
	}


	public void windowClosed (WindowEvent e)
	{
	}


	public void windowIconified (WindowEvent e)
	{
	}


	public void windowDeiconified (WindowEvent e)
	{
	}


	public void windowActivated (WindowEvent e)
	{
	}


	public void windowDeactivated (WindowEvent e)
	{
	}


	public static int indexOf (Component component)
	{
		Container p = component.getParent ();
		if (p != null)
		{
			for (int i = 0; i < p.getComponentCount (); i++)
			{
				if (component == p.getComponent (i))
				{
					return i;
				}
			}
		}
		return -1;
	}


	public static void printTree (Component root, String indent)
	{
		if (root != null)
		{
			System.out.println ('\n' + indent + root.getClass ().getName ()
								+ "\n\n" + root);
			if (root instanceof Container)
			{
				Container c = (Container) root;
				for (int i = 0; i < c.getComponentCount (); i++)
				{
					printTree (c.getComponent (i), indent + "  ");
				}
			}
		}

	}


	@Override
	public void revalidate (Object component)
	{
		Component c = (Component) component;
		c.invalidate ();
		if (!c.isDisplayable ())
		{
			return;
		}
		while (true)
		{
			if (c instanceof JComponent)
			{
				((JComponent) c).revalidate ();
				return;
			}
			else if ((c instanceof ScrollPane)
					 || (c instanceof java.awt.Window))
			{
				final Component v = c;
				EventQueue.invokeLater (new Runnable ()
				{
					public void run ()
					{
						v.validate ();
					}
				});
				return;
			}
			c = c.getParent ();
			if (c == null)
			{
				return;
			}
		}
	}


	@Override
	public void repaint (Object component)
	{
		((Component) component).repaint ();
	}

}
