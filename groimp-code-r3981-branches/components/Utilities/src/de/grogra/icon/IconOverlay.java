
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

package de.grogra.icon;

import java.awt.*;

public class IconOverlay extends IconSourceBase
{
	protected IconSource a;
	protected IconSource b;


	public IconOverlay (IconSource a, IconSource b)
	{
		this.a = a;
		this.b = b;
	}


	@Override
	protected Icon getIconImpl (Dimension size, int state)
	{
		final Icon ai = a.getIcon (size, state);
		final Icon bi = b.getIcon (size, state);
		return new Icon ()
		{
			public void paintIcon (Component c, Graphics2D g, int x, int y,
								   int w, int h, int state)
			{
				if (ai != null)
				{
					ai.paintIcon (c, g, x, y, w, h, state);
				}
				if (bi != null)
				{
					bi.paintIcon (c, g, x, y, w, h, state);
				}
			}


			public void prepareIcon ()
			{
				if (ai != null)
				{
					ai.prepareIcon ();
				}
				if (bi != null)
				{
					bi.prepareIcon ();
				}
			}


			public boolean isMutable ()
			{
				return ((ai != null) && ai.isMutable ())
					|| ((bi != null) && bi.isMutable ());
			}


			public IconSource getIconSource ()
			{
				return IconOverlay.this;
			}


			public Image getImage ()
			{
				return null;
			}


			public Image getImage (int w, int h)
			{
				return null;
			}


			public java.net.URL getImageSource ()
			{
				return null;
			}


			public Rectangle getIconBounds ()
			{
				return null;
			}
		};
	}


	@Override
	public Dimension getPreferredIconSize (boolean small)
	{
		Dimension d = a.getPreferredIconSize (small);
		return (d != null) ? d : b.getPreferredIconSize (small);
	}
}
