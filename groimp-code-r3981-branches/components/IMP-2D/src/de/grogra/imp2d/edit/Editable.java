
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

package de.grogra.imp2d.edit;

import javax.vecmath.Point2d;
import javax.vecmath.Matrix3d;
import de.grogra.imp2d.*;
import de.grogra.graph.*;

public interface Editable
{
	ObjectAttribute ATTRIBUTE
		= (ObjectAttribute) new ObjectAttribute (Editable.class, false, null)
		.initializeName ("de.grogra.imp2d.edit.editable");


	void pickTool (Point2d point, Matrix3d transformation, de.grogra.imp.PickList list,
				   EditTool tool);

	void drawTool (AWTCanvas2DIF canvas, Matrix3d transformation, EditTool tool);

	void toolEventOccured (de.grogra.pf.ui.event.EditEvent event,
						   EditTool tool);
}
