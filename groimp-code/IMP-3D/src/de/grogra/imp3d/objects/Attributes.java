
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

import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.Graph;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.IntEnumerationAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.imp.objects.Vector3fAttribute;
import de.grogra.imp3d.IMP3D;
import de.grogra.imp3d.shading.Interior;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Transform3D;
import de.grogra.util.EnumerationType;
import de.grogra.util.Quantity;

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
	
	public static final BooleanAttribute RENDER_AS_WIREFRAME
		= init (new BooleanAttribute (), "renderAsWireframe", IMP3D.I18N);
	
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

	public static final EnumerationType CSG_OPERATION_TYPE
		= new EnumerationType ("csgOperation", IMP3D.I18N, 4);

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

	
	public static final int CIE_NORM_A = 0;
	public static final int CIE_NORM_C = 1;
	public static final int CIE_NORM_D50 = 2;
	public static final int CIE_NORM_D55 = 3;
	public static final int CIE_NORM_D65 = 4;
	public static final int CIE_NORM_D75 = 5;
	public static final int CIE_NORM_FL1 = 6;
	public static final int CIE_NORM_FL2 = 7;
	public static final int CIE_NORM_FL3 = 8;
	public static final int CIE_NORM_FL4 = 9;
	public static final int CIE_NORM_FL5 = 10;
	public static final int CIE_NORM_FL6 = 11;
	public static final int CIE_NORM_FL7 = 12;
	public static final int CIE_NORM_FL8 = 13;
	public static final int CIE_NORM_FL9 = 14;
	public static final int CIE_NORM_FL10 = 15;
	public static final int CIE_NORM_FL11 = 16;
	public static final int CIE_NORM_FL12 = 17;
	public static final int CIE_NORM_HP1 = 18;
	public static final int CIE_NORM_HP2 = 19;
	public static final int CIE_NORM_HP3 = 20;
	public static final int CIE_NORM_HP4 = 21;
	public static final int CIE_NORM_HP5 = 22;

	public static final EnumerationType CIE_NORMS_TYPE
		= new EnumerationType ("cieNorms", IMP3D.I18N, 23);

	public static final IntAttribute CIE_NORMS
		= init (new IntEnumerationAttribute (CIE_NORMS_TYPE),
				CIE_NORMS_TYPE.getName (), IMP3D.I18N);
	
	public static final ObjectAttribute POLYGONS
		= init (new ObjectAttribute (Polygons.class, false, null),
				"polygons", IMP3D.I18N);
	
	// frustum irregular
	public static final ObjectAttribute BASE_RADII
		= init (new ObjectAttribute (float[].class, false, null), "baseRadii", IMP3D.I18N);
	
	public static final ObjectAttribute TOP_RADII
		= init (new ObjectAttribute (float[].class, false, null), "topRadii", IMP3D.I18N);
	
	public static final IntAttribute SECTOR_COUNT
		= init (new IntAttribute(),"sectorCount", IMP3D.I18N);
	
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

	//sphere segment
	public static final FloatAttribute RADIUS
		= init (new FloatAttribute (Quantity.LENGTH), "radius", IMP3D.I18N);
	public static final FloatAttribute THETA1
		= init (new FloatAttribute (Quantity.ANGLE), "theta1", IMP3D.I18N);
	public static final FloatAttribute THETA2
		= init (new FloatAttribute (Quantity.ANGLE), "theta2", IMP3D.I18N);
	public static final FloatAttribute PHI
		= init (new FloatAttribute (Quantity.ANGLE), "phi", IMP3D.I18N);

	//GPP
	public static final IntAttribute N_POLYGONS
		= init (new IntAttribute (), "n_polygons", IMP3D.I18N);
	public static final FloatAttribute RADIUS_TOP
		= init (new FloatAttribute (Quantity.LENGTH), "radius_top", IMP3D.I18N);
	public static final FloatAttribute RADIUS_BASE
		= init (new FloatAttribute (Quantity.LENGTH), "radius_base", IMP3D.I18N);
	public static final FloatAttribute X_SHIFT
		= init (new FloatAttribute (Quantity.LENGTH), "x_shift", IMP3D.I18N);
	public static final FloatAttribute Y_SHIFT
		= init (new FloatAttribute (Quantity.LENGTH), "y_shift", IMP3D.I18N);

	//GWedge
	public static final FloatAttribute X1
		= init (new FloatAttribute (Quantity.LENGTH), "x1", IMP3D.I18N);
	public static final FloatAttribute X2
		= init (new FloatAttribute (Quantity.LENGTH), "x2", IMP3D.I18N);
	public static final FloatAttribute Y1
		= init (new FloatAttribute (Quantity.LENGTH), "y1", IMP3D.I18N);
	public static final FloatAttribute Y2
		= init (new FloatAttribute (Quantity.LENGTH), "y2", IMP3D.I18N);
	
	//prisms
	//prism rectangular
	public static FloatAttribute Y = init(new FloatAttribute(null),
			"prismRectangular_y", IMP3D.I18N);
	public static FloatAttribute XPOS = init(new FloatAttribute(null),
			"prismRectangular_xPos", IMP3D.I18N);
	public static FloatAttribute XNEG = init(new FloatAttribute(null),
			"prismRectangular_xNeg", IMP3D.I18N);
	public static FloatAttribute ZPOS = init(new FloatAttribute(null),
			"prismRectangular_zPos", IMP3D.I18N);
	public static FloatAttribute ZNEG = init(new FloatAttribute(null),
			"prismRectangular_zNeg", IMP3D.I18N);
	
	// lamella
	public static final FloatAttribute LAMELLA_A = init(new FloatAttribute(null),
			"lamella_a", IMP3D.I18N);
	public static final FloatAttribute LAMELLA_B = init(new FloatAttribute(null),
			"lamella_b", IMP3D.I18N);
	
	// Voronoi cell
	public static final BooleanAttribute VORONOI_NUCLEUS = init (new BooleanAttribute (), 
			"showVoronoiNucleus", IMP3D.I18N);
	public static final BooleanAttribute VORONOI_NEIGHBOURS = init (new BooleanAttribute (), 
			"showNeighbours", IMP3D.I18N);
	public static final BooleanAttribute VORONOI_DIAGRAM = init (new BooleanAttribute (), 
			"showVoronoiDiagram", IMP3D.I18N);
	public static final BooleanAttribute DELAUNAY_DIAGRAM = init (new BooleanAttribute (), 
			"showDelaunayDiagram", IMP3D.I18N);
	public static final BooleanAttribute VORONOI_FACES = init (new BooleanAttribute (), 
			"showVoronoiFaces", IMP3D.I18N);
	public static final BooleanAttribute DELAUNAY_FACES = init (new BooleanAttribute (), 
			"showDelaunayFaces", IMP3D.I18N);
	public static final BooleanAttribute VORONOI_POINTS = init (new BooleanAttribute (), 
			"showVoronoiPoints", IMP3D.I18N);
	public static final BooleanAttribute DELAUNAY_POINTS = init (new BooleanAttribute (), 
			"showDelaunayPoints", IMP3D.I18N);
}
