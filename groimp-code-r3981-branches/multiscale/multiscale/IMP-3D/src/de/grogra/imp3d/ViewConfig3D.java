
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

package de.grogra.imp3d;

import javax.vecmath.Matrix4d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.shading.Light;
import de.grogra.pf.ui.Workbench;

public interface ViewConfig3D
{
	boolean isInVisibleLayer (Object o, boolean asNode, GraphState gs);

	Graph getGraph ();
	
	Workbench getWorkbench ();

	float getEpsilon ();

	Camera getCamera ();

	/**
	 * Computes a default light to use when a scene contains no lights.
	 * 
	 * @param lightToWorld the computed transformation for the light is placed in here
	 * @return a default light
	 */
	Light getDefaultLight (Matrix4d lightToWorld);

}
