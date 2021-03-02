
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

package de.grogra.imp2d.objects;

import de.grogra.graph.*;
import de.grogra.math.Transform2D;
import de.grogra.util.Quantity;

public class Attributes extends de.grogra.imp.objects.Attributes
{
	private static final de.grogra.util.I18NBundle I18N
		= de.grogra.imp2d.IMP2D.getInstance ().getI18NBundle ();

	public static final ObjectAttribute SHAPE
		= (ObjectAttribute) new ObjectAttribute (Object.class, false, null)
		.initializeName ("de.grogra.imp2d.objects.shape");

	public static final ObjectAttribute TRANSFORM
		= init (new ObjectAttribute (Transform2D.class, false, null), "transform", I18N);

	public static final ObjectAttribute TRANSFORMATION
		= init (new ObjectAttribute (Transformation.class, false, null), "transformation", I18N);

	public static final ObjectAttribute STROKE
		= init (new ObjectAttribute (StrokeAdapter.class, false, null), "stroke", I18N);

	public static final ObjectAttribute SHAPE_2D
		= init (new ObjectAttribute (java.awt.Shape.class, false, null), "shape", I18N);

	public static final ObjectAttribute START_ARROW
		= init (new ObjectAttribute (Arrow.class, false, null), "startArrow", I18N);

	public static final ObjectAttribute END_ARROW
		= init (new ObjectAttribute (Arrow.class, false, null), "endArrow", I18N);

	public static final ObjectAttribute RUNTIMESTATUS
		= init (new ObjectAttribute (java.awt.Shape.class, false, null), "runtimestatus", I18N);
	
	public static final DoubleAttribute SHAPEWIDTH 
		= init (new DoubleAttribute (Quantity.LENGTH), "shapewidth",I18N);

	public static final DoubleAttribute SHAPEHEIGHT 
		= init (new DoubleAttribute (Quantity.LENGTH), "shapeheight",I18N);

	public static final FloatAttribute EDGEWEIGHTED 
		= init (new FloatAttribute (Quantity.LENGTH), "edgeweighted",I18N);

}
