
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
import java.awt.image.BufferedImage;
import de.grogra.icon.*;
import de.grogra.pf.ui.*;
import de.grogra.xl.util.ObjectList;

public abstract class LabelRenderer implements Command
{
	final Context context;
	final int defaultHeight;
	final boolean smallIcons;
	final ObjectList nodes = new ObjectList ();


	public class LabelData implements javax.swing.Icon, Command
	{
		final Object userData;

		boolean invalid = true;
		String text = "";

		RenderedIcon renderedIcon;
		BufferedImage iconBuffer;
		int renderY;
		int stamp;
		Image image;
		Rectangle bounds;
		int width;
		int height;

		final static int NO_ICON = 0;
		final static int ICON_BUFFER = 1;
		final static int IMAGE = 2;
		final static int RENDER = 3;

		int iconType = NO_ICON;


		public LabelData (Object userData)
		{
			this.userData = userData;
		}

		
		public Object getUserData ()
		{
			return userData;
		}
		
		
		public String getText ()
		{
			return text;
		}
		
		
		public javax.swing.Icon getIcon ()
		{
			return (iconType != NO_ICON) ? this : null;
		}

		
		public void invalidate ()
		{
			invalid = true;
		}

		
		public void revalidate ()
		{
			if (invalid)
			{
				boolean exec;
				synchronized (LabelRenderer.this)
				{
					exec = nodes.isEmpty ();
					nodes.add (this);
				}
				if (exec)
				{
					UI.getJobManager (context).execute
						(LabelRenderer.this, null, context, JobManager.UPDATE_FLAGS);
				}
			}
		}


		public int getIconWidth ()
		{
			return width;
		}


		public int getIconHeight ()
		{
			return height;
		}

		
		public String getCommandName ()
		{
			return null;
		}

		
		public void run (Object info, Context c)
		{
			JobManager j = UI.getJobManager (context);
			stamp = renderedIcon.getStamp ();
			if ((renderY = renderedIcon.renderImage (j, iconBuffer, renderY))
				< height)
			{
				j.cancelQueuedJob (this);
				j.runLater (this, null, context, JobManager.RENDER_FLAGS);
			}
			invokeUpdate (new LabelData[] {this}, false);
		}
		

		public void paintIcon (Component c, Graphics g, int x, int y)
		{
			switch (iconType)
			{
				case ICON_BUFFER:
					g.drawImage (iconBuffer, x, y, null);
					break;
				case RENDER:
					if ((renderY < height)
						|| (stamp != renderedIcon.getStamp ()))
					{
						JobManager j = UI.getJobManager (context);
						j.cancelQueuedJob (this);
						j.runLater (this, null, context, JobManager.RENDER_FLAGS);
					}
					// no break
				case IMAGE:
					if (bounds != null)
					{
						g.drawImage (image, x, y, x + width, y + height,
									 bounds.x, bounds.y,
									 bounds.x + bounds.width,
									 bounds.y + bounds.height, null);
					}
					else
					{
						g.drawImage (image, x, y, width, height, null);
					}
					break;
			}
		}


		@Override
		public String toString ()
		{
			return "LabelData@" + Integer.toHexString (hashCode ()) + ':' + text;
		}
	}

	
	public LabelRenderer (Context ctx, int defaultHeight, boolean smallIcons)
	{
		this.context = ctx;
		this.defaultHeight = defaultHeight;
		this.smallIcons = smallIcons;
	}


	public synchronized void run (Object info, Context ctx)
	{
		boolean layout = false;
		int s = nodes.size;
		Object[] a = nodes.elements;
		int r = 0;
		for (int i = 0; i < s; i++)
		{
			LabelData n = (LabelData) a[i];
			if (n.invalid)
			{
				String d = getText (n);
				boolean changed = false;
				if (!d.equals (n.text))
				{
					n.text = d;
					changed = true;
				}
				IconSource is = getIconSource (n);
				n.iconType = LabelData.NO_ICON;
				if (is != null)
				{
					Dimension p = is.getPreferredIconSize (smallIcons);
					if (p == null)
					{
						p = AWTToolkitBase.MENU_ICON_SIZE;
					}
					int w = p.width;
					int h = p.height;
					Icon icon = is.getIcon (p, 0);
					changed |= icon.isMutable ();
					if (icon instanceof RenderedIcon)
					{
						RenderedIcon ri = (RenderedIcon) icon;
						h = (defaultHeight > 0) ? defaultHeight
							: de.grogra.util.Utils.getInt (UI.getOptions (ctx), "iconsize", AWTToolkitBase.MENU_ICON_SIZE.height);
						w = Math.round (h * ri.getSizeRatio ());
						BufferedImage b = n.iconBuffer;
						if ((b == null) || (b.getWidth () != w)
							|| (b.getHeight () != h))
						{
							n.iconBuffer = b = new BufferedImage
								(w, h, BufferedImage.TYPE_INT_ARGB);
						}
						n.renderY = 0;
						n.image = b;
						n.renderedIcon = (RenderedIcon) icon;
						n.iconType = LabelData.RENDER;
						n.bounds = icon.getIconBounds ();
					}
					else if ((n.image = icon.getImage (w, h)) != null)
					{
						n.iconType = LabelData.IMAGE;
						n.bounds = null;
					}
					else if ((n.image = icon.getImage ()) != null)
					{
						n.iconType = LabelData.IMAGE;
						n.bounds = icon.getIconBounds ();
					}
					else
					{
						n.iconType = LabelData.ICON_BUFFER;
						BufferedImage b = n.iconBuffer;
						if ((b == null) || (b.getWidth () != w)
							|| (b.getHeight () != h))
						{
							n.iconBuffer = b = new BufferedImage
								(w, h, BufferedImage.TYPE_INT_ARGB);
						}
						Graphics2D g = b.createGraphics ();
						icon.paintIcon (null, g, 0, 0, w, h, 0);
						g.dispose ();
					}
					if ((n.width != w) || (n.height != h))
					{
						changed = true;
						layout = true;
						n.width = w;
						n.height = h;
						n.invalidate ();
					}
				}
				n.invalid = false;
				if (changed)
				{
					a[r++] = n;
				}
			}
		}
		if (r > 0)
		{
			LabelData[] nodes = new LabelData[r];
			System.arraycopy (a, 0, nodes, 0, r);
			invokeUpdate (nodes, layout);
		}
		nodes.setSize (0);
	}
	
	
	private void invokeUpdate (final LabelData[] nodes, final boolean layout)
	{
		EventQueue.invokeLater (new Runnable ()
		{
			public void run ()
			{
				updateNodes (nodes, layout);
			}
		});
	}


	public String getCommandName ()
	{
		return null;
	}


	protected abstract String getText (LabelData node);


	protected abstract IconSource getIconSource (LabelData node);


	protected abstract void updateNodes (LabelData[] nodes, boolean layout);

}
