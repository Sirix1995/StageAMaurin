
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

package de.grogra.imp.objects;

import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.IntEnumerationAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.StringAttribute;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineSurface;
import de.grogra.math.Tuple3fType;
import de.grogra.math.VertexGrid;
import de.grogra.math.VertexList;
import de.grogra.util.EnumerationType;
import de.grogra.util.Quantity;

public class Attributes extends de.grogra.graph.Attributes
{
	private static final de.grogra.util.I18NBundle I18N
		= de.grogra.imp.IMP.I18N;

	public static final StringAttribute CAPTION
		= init (new StringAttribute (), "caption", I18N);
	
	public static final StringAttribute POSITION
		= init (new StringAttribute (), "position", I18N);
	
	public static final ObjectAttribute FONT
		= init (new ObjectAttribute (FontAdapter.class, false, null), "font", I18N);

	public static final FloatAttribute DEPTH
		= init (new FloatAttribute (Quantity.LENGTH), "depth", I18N);
	
	public static final int H_ALIGN_CENTER = 0;
	public static final int H_ALIGN_LEFT = 1;
	public static final int H_ALIGN_RIGHT = 2;

	public static final EnumerationType HORIZONTAL_ALIGNMENT_TYPE
		= new EnumerationType ("horizontalAlignment", I18N, 3);

	public static final IntAttribute HORIZONTAL_ALIGNMENT
		= init (new IntEnumerationAttribute (HORIZONTAL_ALIGNMENT_TYPE),
				HORIZONTAL_ALIGNMENT_TYPE.getName (), I18N);

	public static final int V_ALIGN_CENTER = 0;
	public static final int V_ALIGN_TOP = 1;
	public static final int V_ALIGN_BOTTOM = 2;

	public static final EnumerationType VERTICAL_ALIGNMENT_TYPE
		= new EnumerationType ("verticalAlignment", I18N, 3);

	public static final IntAttribute VERTICAL_ALIGNMENT
		= init (new IntEnumerationAttribute (VERTICAL_ALIGNMENT_TYPE),
				VERTICAL_ALIGNMENT_TYPE.getName (), I18N);

	public static final FloatAttribute CONTINUOUS_VERTICAL_ALIGNMENT
		= init (new FloatAttribute (null), "continuousVerticalAlignment", I18N);

	public static final BooleanAttribute OUTLINED
		= init (new BooleanAttribute (), "outlined", I18N);

	public static final BooleanAttribute FILLED
		= init (new BooleanAttribute (), "filled", I18N);

	public static final ObjectAttribute FLOAT_DATA
		= init (new ObjectAttribute (float[].class, false, null), "data-float", I18N);

	public static final FloatAttribute RADIUS
		= init (new FloatAttribute (Quantity.LENGTH), "radius", I18N);

	public static final DoubleAttribute WIDTH
		= init (new DoubleAttribute (Quantity.LENGTH), "width", I18N);

	public static final DoubleAttribute HEIGHT
		= init (new DoubleAttribute (Quantity.LENGTH), "height", I18N);

	public static final FloatAttribute BASE_RADIUS
		= init (new FloatAttribute (Quantity.LENGTH), "baseRadius", I18N);

	public static final FloatAttribute TOP_RADIUS
		= init (new FloatAttribute (Quantity.LENGTH), "topRadius", I18N);

	public static final DoubleAttribute LENGTH
		= init (new DoubleAttribute (Quantity.LENGTH), "length", I18N);

	public static final DoubleAttribute ANGLE
		= init (new DoubleAttribute (Quantity.ANGLE), "angle", I18N);

	public static final DoubleAttribute SCALE
		= init (new DoubleAttribute (null), "scale", I18N);

	public static final DoubleAttribute VALUE
		= init (new DoubleAttribute (null), "value", I18N);

	public static final BooleanAttribute TRANSFORMING
		= init (new BooleanAttribute (), "transforming", I18N);

	public static final ObjectAttribute COLOR
		= init (new Tuple3fAttribute (Tuple3fType.COLOR, null), "color", I18N);

	public static final ObjectAttribute FILL_COLOR
		= init (new Tuple3fAttribute (Tuple3fType.COLOR, null), "fillColor", I18N);

	public static final ObjectAttribute CURVE
		= init (new ObjectAttribute (BSplineCurve.class, false, null), "curve", I18N);

	public static final ObjectAttribute SURFACE
		= init (new ObjectAttribute (BSplineSurface.class, false, null), "surface", I18N);

	public static final FloatAttribute FLATNESS
		= init (new FloatAttribute (null), "flatness", I18N);

	public static final ObjectAttribute VERTEX_LIST
		= init (new ObjectAttribute (VertexList.class, false, null), "vertexList", I18N);

	public static final ObjectAttribute VERTEX_GRID
		= init (new ObjectAttribute (VertexGrid.class, false, null), "vertexGrid", I18N);

}
