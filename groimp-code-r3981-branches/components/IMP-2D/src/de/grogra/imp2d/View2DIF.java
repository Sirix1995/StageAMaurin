/*
 * Copyright (C) 2012 GroIMP Developer Team
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

import javax.vecmath.Matrix3d;

import de.grogra.graph.Graph;
import de.grogra.imp.PickList;
import de.grogra.imp.View;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;

public abstract class View2DIF extends View
{

	public abstract Matrix3d getCanvasTransformation ();

	@Override
	public abstract IOFlavor getFlavor ();

	@Override
	public abstract de.grogra.persistence.ManageableType getManageableType ();

	public abstract Matrix3d getTransformation ();

	@Override
	public abstract boolean isToolGraph (Graph graph);

	@Override
	public abstract void pick (int x, int y, PickList list);

	public abstract void setTransformation (Matrix3d t);

	protected boolean layouting = false;

	public static void layout (Item item, Object info, Context ctx)
	{
		View v = get (ctx);
		if (v instanceof ComponentView2D)
		{
			((ComponentView2D) v).layout ();
		}
		if (v instanceof View2D)
		{
			((View2D) v).layout ();
		}
	}
}
