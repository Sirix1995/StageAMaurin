package de.grogra.ext.x3d.objects;

import javax.vecmath.Color3f;
import de.grogra.ext.x3d.interfaces.Appearance;
import de.grogra.ext.x3d.interfaces.FontStyle;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.imp3d.objects.TextLabel;

/**
 * GroIMP node class for a x3d text element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DText extends ShadedNull implements Value, Appearance, FontStyle {

	protected X3DFontStyle fontStyle = null;
	//enh:field hidden getter setter
	
	protected float[] length = new float[]{};
	//enh:field hidden getter setter
	
	protected float maxExtent = 0;
	//enh:field hidden getter setter
	
	protected boolean solid = false;
	//enh:field hidden getter setter
	
	protected String[] string = new String[]{};
	//enh:field hidden getter setter
	
	protected X3DAppearance appearance = null;
	//enh:field hidden getter setter
	
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
	public X3DText() {
		super();
	}

	public void setValues() {
		// TextLabel erzeugen
		String text = new String("");
		for (int i = 0; i < string.length; i++) {
			text += string [fontStyle.topToBottom?i:string.length-i-1] + "\n";
		}
		TextLabel label = new TextLabel();
		label.setCaption(text);
		if (appearance != null)
			if (appearance.getMaterial() != null)
				label.setColor((Color3f) appearance.getMaterial().getDiffuseColor());
		
		// Text ausrichten
		int ha = 1;
		int va = fontStyle.topToBottom?1:2;
		if (fontStyle.justify.length > 0) {
			if (fontStyle.justify[0].toUpperCase().equals("MIDDLE"))
				ha = 0;
			else if (fontStyle.justify[0].toUpperCase().equals("END"))
				ha = 2;
			else
				ha = 1;
			if (fontStyle.justify.length > 1) {
				if (fontStyle.justify[1].toUpperCase().equals("MIDDLE"))
					va = 0;
				else if (fontStyle.justify[1].toUpperCase().equals("END"))
					va = fontStyle.topToBottom?2:1;
				else
					va = fontStyle.topToBottom?1:2;
			}
		}
		label.setAlignment(ha, va);
		
		// TODO: Text formatieren
		// nicht moeglich, da FontAdapter nicht ansprechbar
		
		this.getOrCreateEdgeTo(label).addEdgeBits(Graph.SUCCESSOR_EDGE, null);
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field fontStyle$FIELD;
	public static final NType.Field length$FIELD;
	public static final NType.Field maxExtent$FIELD;
	public static final NType.Field solid$FIELD;
	public static final NType.Field string$FIELD;
	public static final NType.Field appearance$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DText.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((X3DText) o).solid = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 3:
					return ((X3DText) o).isSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 2:
					((X3DText) o).maxExtent = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 2:
					return ((X3DText) o).getMaxExtent ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DText) o).fontStyle = (X3DFontStyle) value;
					return;
				case 1:
					((X3DText) o).length = (float[]) value;
					return;
				case 4:
					((X3DText) o).string = (String[]) value;
					return;
				case 5:
					((X3DText) o).appearance = (X3DAppearance) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((X3DText) o).getFontStyle ();
				case 1:
					return ((X3DText) o).getLength ();
				case 4:
					return ((X3DText) o).getString ();
				case 5:
					return ((X3DText) o).getAppearance ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DText ());
		$TYPE.addManagedField (fontStyle$FIELD = new _Field ("fontStyle", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DFontStyle.class), null, 0));
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 1));
		$TYPE.addManagedField (maxExtent$FIELD = new _Field ("maxExtent", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (solid$FIELD = new _Field ("solid", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (string$FIELD = new _Field ("string", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 4));
		$TYPE.addManagedField (appearance$FIELD = new _Field ("appearance", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DAppearance.class), null, 5));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new X3DText ();
	}

	public boolean isSolid ()
	{
		return solid;
	}

	public void setSolid (boolean value)
	{
		this.solid = (boolean) value;
	}

	public float getMaxExtent ()
	{
		return maxExtent;
	}

	public void setMaxExtent (float value)
	{
		this.maxExtent = (float) value;
	}

	public X3DFontStyle getFontStyle ()
	{
		return fontStyle;
	}

	public void setFontStyle (X3DFontStyle value)
	{
		fontStyle$FIELD.setObject (this, value);
	}

	public float[] getLength ()
	{
		return length;
	}

	public void setLength (float[] value)
	{
		length$FIELD.setObject (this, value);
	}

	public String[] getString ()
	{
		return string;
	}

	public void setString (String[] value)
	{
		string$FIELD.setObject (this, value);
	}

	public X3DAppearance getAppearance ()
	{
		return appearance;
	}

	public void setAppearance (X3DAppearance value)
	{
		appearance$FIELD.setObject (this, value);
	}

//enh:end
}
