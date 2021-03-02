/*
 * Copyright (C) 2020 GroIMP Developer Team
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

/**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor3d;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.vecmath.Point3f;

import de.grogra.nurbseditor3d.NURBSDisplay3D;
import de.grogra.nurbseditor3d.ObjectGeometry3D;

public class WheelListener implements MouseWheelListener{
        boolean rotation = false;
        int displayYRotation;
        int displayZRotation;
        
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		displayYRotation = NURBSDisplay3D.getRotation()[1];
    	displayZRotation = NURBSDisplay3D.getRotation()[2];
		Point p = new Point(event.getX(), event.getY());
		Point3f source = NURBSDisplay3D.pixelToCoordinate(new Point3f(p.x, p.y, 0.0f));
        int rotation = event.getWheelRotation();
        
        if(source != null) {
        	ObjectGeometry3D.mouseWheelAction(source, rotation, displayYRotation, displayZRotation);
        }
	}
}
