
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

package de.grogra.imp;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.vecmath.Point2d;

import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.util.Disposable;
import de.grogra.util.EventListener;

public interface ViewEventHandlerIF extends Disposable, EventListener
{

	abstract ClickEvent createClickEvent (MouseEvent event);

	abstract DragEvent createDragEvent (MouseEvent event);

	abstract DragEvent createDragEvent (Point2d point);

	public void disposeNavigator (EventObject me);

	public View getView ();

	public void updateHighlight ();

	public void setSelectedEdge (int edgeType);
	
	public void setDropTarget (ComponentDescriptor node);

}
