package de.grogra.ext.x3d.objects;

import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * This class saves all informations of a x3d normal element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DNormal extends ShareableBase {
	
	//enh:sco SCOType

	/**
	 * An array of informations about normal vectors. In most cases these are
	 * are single tuples of two float values. 
	 */
	private float[] vector = new float[]{};
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
	public X3DNormal() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DNormal createInstance(Attributes atts) {
		X3DNormal newNormal = new X3DNormal();
		
		String valueString;
		
		valueString = atts.getValue("vector");
		if (valueString != null)
			newNormal.vector = Util.splitStringToArrayOfFloat(valueString);
		
		return newNormal;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field vector$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DNormal representative, de.grogra.persistence.SCOType supertype)
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
					((X3DNormal) o).vector = (float[]) value;
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
					return ((X3DNormal) o).getVector ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DNormal ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DNormal.class);
		vector$FIELD = Type._addManagedField ($TYPE, "vector", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public float[] getVector ()
	{
		return vector;
	}

	public void setVector (float[] value)
	{
		vector$FIELD.setObject (this, value);
	}

//enh:end
}
