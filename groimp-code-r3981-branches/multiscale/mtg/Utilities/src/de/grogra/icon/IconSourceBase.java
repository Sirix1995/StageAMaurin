
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

import java.awt.Dimension;
import java.util.HashMap;

public abstract class IconSourceBase implements IconSource
{
	private HashMap icons = new HashMap ();
	private static Dimension tmp = new Dimension ();


	public final Icon getIcon (Dimension size, int state)
	{
		Object o;
		int h;
		synchronized (tmp)
		{
			tmp.width = size.width;
			tmp.height = h = (size.height & 0xffff) | (state << 16);
			o = icons.get (tmp);
		}
		if (o == null)
		{
			Icon i = getIconImpl (size, state);
			icons.put (new Dimension (size.width, h),
					   (i == null) ? this : (Object) i);
			return i;
		}
		else if (o == this)
		{
			return null;
		}
		else
		{
			return (Icon) o;
		}
	}


	public Dimension getPreferredIconSize (boolean small)
	{
		return null;
	}


	protected abstract Icon getIconImpl (Dimension size, int state);
}
