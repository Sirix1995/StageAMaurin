
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

package de.grogra.imp2d;

import javax.vecmath.Point2d;

public class ClickEvent2D extends de.grogra.pf.ui.event.ClickEvent
{
	public final Point2d point;


	public ClickEvent2D (Point2d point)
	{
		this.point = new Point2d (point);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",point=" + point;
	}

	
	public View2DIF getView ()
	{
		return (View2DIF) getPanel ();
	}

}
