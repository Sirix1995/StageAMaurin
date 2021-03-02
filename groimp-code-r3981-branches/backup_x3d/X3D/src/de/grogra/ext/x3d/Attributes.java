package de.grogra.ext.x3d;

import de.grogra.graph.*;
import de.grogra.imp.objects.Point3fAttribute;
import de.grogra.imp.objects.Vector3fAttribute;

/**
 * This class is used to identify all attributes of x3d nodes.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class Attributes extends de.grogra.imp3d.objects.Attributes {

	public static final ObjectAttribute X3D
		= (ObjectAttribute) new ObjectAttribute (Object.class, false, null)
		.initializeName ("de.grogra.ext.x3d");
	
	public static final ObjectAttribute VIEW
		= (ObjectAttribute) new ObjectAttribute (Object.class, false, null)
		.initializeName ("de.grogra.ext.x3d.view");
	
	public static final BooleanAttribute X3DSOLID
		= init (new BooleanAttribute (), "x3dSolid", X3DPlugin.I18N);
	
	public static final BooleanAttribute DRAW_AS_SPHERE
		= init (new BooleanAttribute (), "drawAsSphere", X3DPlugin.I18N);

	public static final BooleanAttribute DRAW_AS_SPHERES
		= init (new BooleanAttribute (), "drawAsSpheres", X3DPlugin.I18N);

	public static final BooleanAttribute DRAW_AS_CYLINDER
		= init (new BooleanAttribute (), "drawAsCylinder", X3DPlugin.I18N);

	public static final BooleanAttribute DRAW_AS_CYLINDERS
		= init (new BooleanAttribute (), "drawAsCylinders", X3DPlugin.I18N);
	
	public static final FloatAttribute X3DRADIUS
		= init (new FloatAttribute (null), "x3dRadius", X3DPlugin.I18N);
	
	public static final Vector3fAttribute X3DSIZE
		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dSize", X3DPlugin.I18N);

	public static final BooleanAttribute X3DSIDE
		= init (new BooleanAttribute (), "x3dSide", X3DPlugin.I18N);

	public static final FloatAttribute X3DAMBIENT_INTENSITY
		= init (new FloatAttribute (null), "x3dAmbientIntensity", X3DPlugin.I18N);

	public static final BooleanAttribute X3DON
		= init (new BooleanAttribute (), "x3dOn", X3DPlugin.I18N);

	public static final Point3fAttribute X3DBBOX_CENTER
		= (Point3fAttribute) init (new Point3fAttribute (null), "x3dBboxCenter", X3DPlugin.I18N);

	public static final Vector3fAttribute X3DBBOX_SIZE
		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dBboxSize", X3DPlugin.I18N);

	
//	public static final BooleanAttribute X3DBOTTOM
//	= init (new BooleanAttribute (), "x3dBottom", X3DPlugin.I18N);

//	public static final BooleanAttribute X3DTOP
//	= init (new BooleanAttribute (), "x3dTop", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DBOTTOM_RADIUS
//		= init (new FloatAttribute (null), "x3dBottomRadius", X3DPlugin.I18N);

//	public static final FloatAttribute X3DHEIGHT
//		= init (new FloatAttribute (null), "x3dHeight", X3DPlugin.I18N);

//	public static final FloatAttribute X3DINTENSITY
//		= init (new FloatAttribute (null), "x3dIntensity", X3DPlugin.I18N);

//	public static final Vector3fAttribute X3DDIRECTION
//		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dDirection", X3DPlugin.I18N);

//	public static final Tuple3fAttribute X3DCOLOR
//		= (Tuple3fAttribute) init (new Tuple3fAttribute (Tuple3fType.COLOR, null), "x3dColor", X3DPlugin.I18N);
	
//	public static final Point3fAttribute X3DLOCATION
//		= (Point3fAttribute) init (new Point3fAttribute (null), "x3dLocation", X3DPlugin.I18N);
	
//	public static final Vector3fAttribute X3DATTENUATION
//		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dAttenuation", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DBEAM_WIDTH
//		= init (new FloatAttribute (null), "x3dBeamWidth", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DCUT_OFF_ANGLE
//		= init (new FloatAttribute (null), "x3dCutOffAngle", X3DPlugin.I18N);

//	public static final Point3fAttribute X3DCENTER
//		= (Point3fAttribute) init (new Point3fAttribute (null), "x3dCenter", X3DPlugin.I18N);

//	public static final Tuple4fAttribute X3DROTATION
//		= (Tuple4fAttribute) init (new Tuple4fAttribute (Tuple4fType.VECTOR, null), "x3dRotation", X3DPlugin.I18N);
	
//	public static final Vector3fAttribute X3DSCALE
//		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dScale", X3DPlugin.I18N);

//	public static final Tuple4fAttribute X3DSCALE_ORIENTATION
//		= (Tuple4fAttribute) init (new Tuple4fAttribute (Tuple4fType.VECTOR, null), "x3dScaleOrientation", X3DPlugin.I18N);
	
//	public static final Vector3fAttribute X3DTRANSLATION
//		= (Vector3fAttribute) init (new Vector3fAttribute (null), "x3dTranslation", X3DPlugin.I18N);
	
//	public static final BooleanAttribute X3DCCW
//		= init (new BooleanAttribute (), "x3dCcw", X3DPlugin.I18N);
	
//	public static final BooleanAttribute X3DCOLOR_PER_VERTEX
//		= init (new BooleanAttribute (), "x3dColorPerVertex", X3DPlugin.I18N);
	
//	public static final BooleanAttribute X3DNORMAL_PER_VERTEX
//		= init (new BooleanAttribute (), "x3dNormalPerVertex", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DCREASE_ANGLE
//		= init (new FloatAttribute (null), "x3dCreaseAngle", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DX_SPACING
//		= init (new FloatAttribute (null), "x3dXSpacing", X3DPlugin.I18N);
	
//	public static final FloatAttribute X3DZ_SPACING
//		= init (new FloatAttribute (null), "x3dZSpacing", X3DPlugin.I18N);
	
//	public static final IntAttribute X3DX_DIMENSION
//		= init (new IntAttribute (), "x3dXDimension", X3DPlugin.I18N);
	
//	public static final IntAttribute X3DZ_DIMENSION
//		= init (new IntAttribute (), "x3dZDimension", X3DPlugin.I18N);
	
//	public static final StringAttribute X3DHEIGHT_ARRAY
//		= init (new StringAttribute (), "x3dHeightArray", X3DPlugin.I18N);

}
