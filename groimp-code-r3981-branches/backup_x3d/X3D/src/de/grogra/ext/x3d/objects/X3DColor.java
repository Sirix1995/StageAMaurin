package de.grogra.ext.x3d.objects;

import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * This class saves all informations of a x3d color element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DColor extends ShareableBase {
	
	//enh:sco SCOType

	/**
	 * An array of color informations. In most cases these are tuples of single r, g and b values. 
	 */
	protected float[] color = new float[]{};
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
	 * Constructor.
	 */
	public X3DColor() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DColor createInstance(Attributes atts) {
		X3DColor newColor = new X3DColor();
		
		String valueString;
		
		valueString = atts.getValue("color");
		if (valueString != null)
			newColor.color = Util.splitStringToArrayOfFloat(valueString);
		
		return newColor;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field color$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DColor representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((X3DColor) o).color = (float[]) value;
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
					return ((X3DColor) o).getColor ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DColor ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DColor.class);
		color$FIELD = Type._addManagedField ($TYPE, "color", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public float[] getColor ()
	{
		return color;
	}

	public void setColor (float[] value)
	{
		color$FIELD.setObject (this, value);
	}

//enh:end
}
