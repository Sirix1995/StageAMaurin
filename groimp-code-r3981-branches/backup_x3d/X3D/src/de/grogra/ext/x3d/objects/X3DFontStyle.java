package de.grogra.ext.x3d.objects;

import org.xml.sax.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;

/**
 * GroIMP node class for a x3d font style element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DFontStyle extends ShareableBase {
	
	//enh:sco SCOType

	protected String[] family = new String[]{"SERIF"};
	//enh:field getter setter
	
	protected boolean horizontal = true;
	//enh:field getter setter
	
	protected String[] justify = new String[]{"BEGIN"};
	//enh:field getter setter
	
	protected String language = new String("");
	//enh:field getter setter
	
	protected boolean leftToRight = true;
	//enh:field getter setter
	
	protected float size = 1;
	//enh:field getter setter
	
	protected float spacing = 1;
	//enh:field getter setter
	
	protected String style = new String("PLAIN");
	//enh:field getter setter
	
	protected boolean topToBottom = true;
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
 	public X3DFontStyle() {
		super();
	}
	
	/**
	 * Creates a new instance of this class. X3D attributes are read and set in
	 * corresponding class attributes.
	 * @param atts
	 * @return
	 */
	public static X3DFontStyle createInstance(Attributes atts) {
		X3DFontStyle newFontStyle = new X3DFontStyle();
		
		String valueString;
		
		valueString = atts.getValue("family");
		if (valueString != null)
			newFontStyle.family = Util.splitStringToArrayOfString(valueString);
		
		valueString = atts.getValue("horizontal");
		if (valueString != null)
			newFontStyle.horizontal = Boolean.valueOf(valueString);
		
		valueString = atts.getValue("justify");
		if (valueString != null)
			newFontStyle.justify = Util.splitStringToArrayOfString(valueString);
		
		valueString = atts.getValue("language");
		if (valueString != null)
			newFontStyle.language = String.valueOf(valueString);
		
		valueString = atts.getValue("leftToRight");
		if (valueString != null)
			newFontStyle.leftToRight = Boolean.valueOf(valueString);
		
		valueString = atts.getValue("size");
		if (valueString != null)
			newFontStyle.size = Float.valueOf(valueString);
		
		valueString = atts.getValue("spacing");
		if (valueString != null)
			newFontStyle.spacing = Float.valueOf(valueString);
		
		valueString = atts.getValue("style");
		if (valueString != null)
			newFontStyle.style = String.valueOf(valueString);
		
		valueString = atts.getValue("topToBottom");
		if (valueString != null)
			newFontStyle.topToBottom = Boolean.valueOf(valueString);
		
		return newFontStyle;
	}

	public String[] getFamily() {
		return family;
	}

	public void setFamily(String[] family) {
		this.family = family;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public String[] getJustify() {
		return justify;
	}

	public void setJustify(String[] justify) {
		this.justify = justify;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isLeftToRight() {
		return leftToRight;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field topToBottom$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (X3DFontStyle representative, de.grogra.persistence.SCOType supertype)
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
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((X3DFontStyle) o).topToBottom = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((X3DFontStyle) o).isTopToBottom ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new X3DFontStyle ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (X3DFontStyle.class);
		topToBottom$FIELD = Type._addManagedField ($TYPE, "topToBottom", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public boolean isTopToBottom ()
	{
		return topToBottom;
	}

	public void setTopToBottom (boolean value)
	{
		this.topToBottom = (boolean) value;
	}

//enh:end
}
