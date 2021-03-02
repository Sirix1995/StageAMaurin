
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

package de.grogra.imp3d.objects;

import de.grogra.util.EnumerationType;
import de.grogra.graph.*;
import de.grogra.imp.objects.*;
import de.grogra.imp3d.*;
import de.grogra.imp3d.shading.*;
import de.grogra.math.Transform3D;

public class Attributes extends de.grogra.imp.objects.Attributes
{
	public static final ObjectAttribute SHAPE
		= (ObjectAttribute) new ObjectAttribute (Object.class, false, null)
		.initializeName ("de.grogra.imp3d.objects.shape");
	
	public static boolean isVisible (Object obj, boolean asNode, Graph g)
	{
		return g.getAccessor(obj, asNode, SHAPE) != null;
	}

	public static final ObjectAttribute LIGHT
		= init (new ObjectAttribute (Light.class, false, null), "light", IMP3D.I18N);

	public static final ObjectAttribute AREA_LIGHT
		= init (new ObjectAttribute (AreaLight.class, false, null), "areaLight", IMP3D.I18N);

	public static final ObjectAttribute TRANSFORM
		= init (new ObjectAttribute (Transform3D.class, false, null), "transform", IMP3D.I18N);

	public static final FloatAttribute START_POSITION
		= init (new FloatAttribute (null), "startPosition", IMP3D.I18N);

	public static final FloatAttribute END_POSITION
		= init (new FloatAttribute (null), "endPosition", IMP3D.I18N);

	public static final BooleanAttribute SCALE_V
		= init (new BooleanAttribute (), "scaleV", IMP3D.I18N);

	public static final ObjectAttribute AXIS
		= init (new Vector3fAttribute (), "axis", IMP3D.I18N);

/*		AXIS2 = new Vector3dAttribute (null, "axis2", null),
		TRANSLATION = new Vector3dAttribute (null, "translation", null),
		TRANSFORMATION = new Matrix4dAttribute (null, "transformation", null),*/

	public static final ObjectAttribute SHADER
		= init (new ObjectAttribute (Shader.class, false, null), "shader", IMP3D.I18N);

	public static final ObjectAttribute INTERIOR
		= init (new ObjectAttribute (Interior.class, false, null), "interior", IMP3D.I18N);


/*		POINT_ARRAY = new ObjectAttribute (null, "points", ClassAdapter.wrap
										   (Point3d[][].class), null, null);
*/

	public static final BooleanAttribute TREATED_AS_INFINITE
		= init (new BooleanAttribute (), "treatedAsInfinite", IMP3D.I18N);

	public static final BooleanAttribute OPEN
		= init (new BooleanAttribute (), "open", IMP3D.I18N);

	public static final BooleanAttribute BASE_OPEN
		= init (new BooleanAttribute (), "baseOpen", IMP3D.I18N);

	public static final BooleanAttribute TOP_OPEN
		= init (new BooleanAttribute (), "topOpen", IMP3D.I18N);

	public static final BooleanAttribute SHIFT_PIVOT
		= init (new BooleanAttribute (), "shiftPivot", IMP3D.I18N);

	public static final IntAttribute PER_VERTEX_SIZE
		= init (new IntAttribute (), "data.perVertexSize", IMP3D.I18N);

	public static final ObjectAttribute TRANSFORMATION
		= init (new ObjectAttribute (Transformation.class, false, null), "transformation", IMP3D.I18N);

	public static final EnumerationType EXPORTED_TRANSFORMATION_TYPE
		= new EnumerationType ("exportedTransformation", IMP3D.I18N, 3);

	public static final IntAttribute EXPORTED_TRANSFORMATION
		= init (new IntEnumerationAttribute (EXPORTED_TRANSFORMATION_TYPE),
				EXPORTED_TRANSFORMATION_TYPE.getName (), IMP3D.I18N);

	public static final int CSG_NONE = -1;
	public static final int CSG_UNION = 0;
	public static final int CSG_INTERSECTION = 1;
	public static final int CSG_DIFFERENCE = 2;
	public static final int CSG_COMPLEMENT = 3;
	public static final int CSG_UNION_TEST = 4;

	public static final EnumerationType CSG_OPERATION_TYPE
		= new EnumerationType ("csgOperation", IMP3D.I18N, 5);

	public static final IntAttribute CSG_OPERATION
		= init (new IntEnumerationAttribute (CSG_OPERATION_TYPE),
				CSG_OPERATION_TYPE.getName (), IMP3D.I18N);


	public static final int VISIBLE_SIDES_FRONT = 0;
	public static final int VISIBLE_SIDES_BACK = 1;
	public static final int VISIBLE_SIDES_BOTH = 2;

	public static final EnumerationType VISIBLE_SIDES_TYPE
		= new EnumerationType ("visibleSides", IMP3D.I18N, 3);

	public static final IntAttribute VISIBLE_SIDES
		= init (new IntEnumerationAttribute (VISIBLE_SIDES_TYPE),
				VISIBLE_SIDES_TYPE.getName (), IMP3D.I18N);

	public static final ObjectAttribute POLYGONS
		= init (new ObjectAttribute (Polygons.class, false, null),
				"polygons", IMP3D.I18N);
	
	
	// supershape
	public static final FloatAttribute A = init(new FloatAttribute(null),
			"supershape_a", IMP3D.I18N);
	public static final FloatAttribute B = init(new FloatAttribute(null),
			"supershape_b", IMP3D.I18N);
	public static final FloatAttribute M1 = init(new FloatAttribute(null),
			"supershape_m1", IMP3D.I18N);
	public static final FloatAttribute N11 = init(new FloatAttribute(null),
			"supershape_n11", IMP3D.I18N);
	public static final FloatAttribute N12 = init(new FloatAttribute(null),
			"supershape_n12", IMP3D.I18N);
	public static final FloatAttribute N13 = init(new FloatAttribute(null),
			"supershape_n13", IMP3D.I18N);
	public static final FloatAttribute M2 = init(new FloatAttribute(null),
			"supershape_m2", IMP3D.I18N);
	public static final FloatAttribute N21 = init(new FloatAttribute(null),
			"supershape_n21", IMP3D.I18N);
	public static final FloatAttribute N22 = init(new FloatAttribute(null),
			"supershape_n22", IMP3D.I18N);
	public static final FloatAttribute N23 = init(new FloatAttribute(null),
			"supershape_n23", IMP3D.I18N);

}
