package de.grogra.ext.x3d.objects;

import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.interfaces.Definable;
import de.grogra.imp.objects.ImageAdapter;

/**
 * This class saves all informations of a x3d image texture element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DImageTexture extends X3DTexture implements Definable {
	
	//enh:sco
	
	protected String[] url = null;
	//enh:field getter setter
	
	protected boolean repeatS = true;
	//enh:field getter setter
	
	protected boolean repeatT = true;
	//enh:field getter setter
	
	protected ImageAdapter img = null;
	//enh:field getter setter
	
	/**
	 * Constructor.
	 */
	public X3DImageTexture() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DImageTexture createInstance(Attributes atts) {
		X3DImageTexture newImageTexture = new X3DImageTexture();
		
		String valueString;
		
		valueString = atts.getValue("url");
		if (valueString != null)
			newImageTexture.url = Util.splitStringToArrayOfString(valueString);
		
		valueString = atts.getValue("repeatS");
		if (valueString != null)
			newImageTexture.repeatS = Boolean.valueOf(valueString);
		
		valueString = atts.getValue("repeatT");
		if (valueString != null)
			newImageTexture.repeatT = Boolean.valueOf(valueString);
		
		valueString = atts.getValue("DEF");
		newImageTexture.def = valueString;
		
		valueString = atts.getValue("USE");
		newImageTexture.use = valueString;
		
		return newImageTexture;
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field url$FIELD;
	public static final Type.Field repeatS$FIELD;
	public static final Type.Field repeatT$FIELD;
	public static final Type.Field img$FIELD;

	public static class Type extends X3DTexture.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DImageTexture representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, X3DTexture.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = X3DTexture.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = X3DTexture.Type.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((X3DImageTexture) o).repeatS = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((X3DImageTexture) o).repeatT = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((X3DImageTexture) o).isRepeatS ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((X3DImageTexture) o).isRepeatT ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((X3DImageTexture) o).url = (String[]) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((X3DImageTexture) o).img = (ImageAdapter) value;
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
					return ((X3DImageTexture) o).getUrl ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((X3DImageTexture) o).getImg ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DImageTexture ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DImageTexture.class);
		url$FIELD = Type._addManagedField ($TYPE, "url", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, Type.SUPER_FIELD_COUNT + 0);
		repeatS$FIELD = Type._addManagedField ($TYPE, "repeatS", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		repeatT$FIELD = Type._addManagedField ($TYPE, "repeatT", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		img$FIELD = Type._addManagedField ($TYPE, "img", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (ImageAdapter.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public boolean isRepeatS ()
	{
		return repeatS;
	}

	public void setRepeatS (boolean value)
	{
		this.repeatS = (boolean) value;
	}

	public boolean isRepeatT ()
	{
		return repeatT;
	}

	public void setRepeatT (boolean value)
	{
		this.repeatT = (boolean) value;
	}

	public String[] getUrl ()
	{
		return url;
	}

	public void setUrl (String[] value)
	{
		url$FIELD.setObject (this, value);
	}

	public ImageAdapter getImg ()
	{
		return img;
	}

	public void setImg (ImageAdapter value)
	{
		img$FIELD.setObject (this, value);
	}

//enh:end
}
