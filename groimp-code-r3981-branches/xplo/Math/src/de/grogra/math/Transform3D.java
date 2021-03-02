
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

package de.grogra.math;

import javax.vecmath.Matrix4d;

/**
 * An instance of <code>Transform3D</code> represents a coordinate
 * transformation in 3D-space.
 * 
 * @author Ole Kniemeyer
 */
public interface Transform3D extends de.grogra.persistence.Manageable
{
	/**
	 * This methods performs a local coordinate transformation.
	 * 
	 * @param in the transformation matrix of the coordinate system of the parent
	 * @param out the computed transformation is placed in here. May be the
	 * same reference as <code>in</code>
	 */
	void transform (Matrix4d in, Matrix4d out);
}
