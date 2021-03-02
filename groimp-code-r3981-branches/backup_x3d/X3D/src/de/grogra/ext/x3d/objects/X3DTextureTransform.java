package de.grogra.ext.x3d.objects;

import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * This class saves all informations of a x3d texture transformation element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DTextureTransform extends ShareableBase {

	//enh:sco SCOType

	/**
	 * Center point for rotation and scale.
	 */
	protected Tuple2f center = new Point2f(0, 0);
	//enh:field getter setter
	
	/**
	 * Rotation of texture coordinates about center.
	 */
	protected float rotation = 0;
	//enh:field getter setter
	
	/**
	 * Scale of texture coordinates about center.
	 */
	protected Tuple2f scale = new Vector2f(1, 1);
	//enh:field getter setter
	
	/**
	 * Translation of texture coordinates.
	 */
	protected Tuple2f translation = new Vector2f(0, 0); 
	//enh:field getter setter
	
	protected String def = null;
	protected String use = null;
	
	public String getDef() {
		return def;
	}
	
	public void setDef(String def) {
		this.def = def;
	}

	public String getUse() {
		return use;
	}
	
	public void setUse(String use) {
		this.use = use;
	}
	
	/**
	 * Constructor
	 */
	public X3DTextureTransform() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DTextureTransform createInstance(Attributes atts) {
		X3DTextureTransform newTextureTransform = new X3DTextureTransform();
		
		String valueString;
		
		valueString = atts.getValue("center");
		if (valueString != null)
			newTextureTransform.center = Util.splitStringToTuple2f(new Point2f(), valueString);
		
		valueString = atts.getValue("rotation");
		if (valueString != null)
			newTextureTransform.rotation = Float.valueOf(valueString);
		
		valueString = atts.getValue("scale");
		if (valueString != null)
			newTextureTransform.scale = Util.splitStringToTuple2f(new Vector2f(), valueString);
		
		valueString = atts.getValue("translation");
		if (valueString != null)
			newTextureTransform.translation = Util.splitStringToTuple2f(new Vector2f(), valueString);		
		
		return newTextureTransform;
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field center$FIELD;
	public static final Type.Field rotation$FIELD;
	public static final Type.Field scale$FIELD;
	public static final Type.Field translation$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DTextureTransform representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((X3DTextureTransform) o).rotation = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((X3DTextureTransform) o).getRotation ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((X3DTextureTransform) o).center = (Tuple2f) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((X3DTextureTransform) o).scale = (Tuple2f) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((X3DTextureTransform) o).translation = (Tuple2f) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((X3DTextureTransform) o).getCenter ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((X3DTextureTransform) o).getScale ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((X3DTextureTransform) o).getTranslation ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DTextureTransform ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DTextureTransform.class);
		center$FIELD = Type._addManagedField ($TYPE, "center", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Tuple2f.class), null, Type.SUPER_FIELD_COUNT + 0);
		rotation$FIELD = Type._addManagedField ($TYPE, "rotation", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		scale$FIELD = Type._addManagedField ($TYPE, "scale", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Tuple2f.class), null, Type.SUPER_FIELD_COUNT + 2);
		translation$FIELD = Type._addManagedField ($TYPE, "translation", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Tuple2f.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public float getRotation ()
	{
		return rotation;
	}

	public void setRotation (float value)
	{
		this.rotation = (float) value;
	}

	public Tuple2f getCenter ()
	{
		return center;
	}

	public void setCenter (Tuple2f value)
	{
		center$FIELD.setObject (this, value);
	}

	public Tuple2f getScale ()
	{
		return scale;
	}

	public void setScale (Tuple2f value)
	{
		scale$FIELD.setObject (this, value);
	}

	public Tuple2f getTranslation ()
	{
		return translation;
	}

	public void setTranslation (Tuple2f value)
	{
		translation$FIELD.setObject (this, value);
	}

//enh:end

}
