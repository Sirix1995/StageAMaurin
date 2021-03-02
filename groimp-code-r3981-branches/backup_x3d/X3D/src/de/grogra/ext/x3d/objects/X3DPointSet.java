package de.grogra.ext.x3d.objects;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Appearance;
import de.grogra.ext.x3d.interfaces.Color;
import de.grogra.ext.x3d.interfaces.Coordinate;
import de.grogra.ext.x3d.interfaces.Value;

/**
 * GroIMP node class for a x3d point set.
 * X3D point sets are converted to one groimp point set node with
 * children. Every child is an instance of X3DPoint and an extra groimp node. 
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DPointSet extends ShadedNull implements Value, Appearance, Color, Coordinate {

	protected X3DCoordinate coord = null;
	//enh:field hidden getter setter
	
	protected X3DColor color = null;
	//enh:field hidden getter setter
	
	protected X3DAppearance appearance = null; 
	//enh:field	hidden getter setter
	
	protected boolean drawAsSpheres = false;
	//enh:field attr=Attributes.DRAW_AS_SPHERES getter setter
	
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
	public X3DPointSet() {
		super();
	}
	
	public void setValues() {
		// if no colorNode is given create one with values of sibling appearance node
		// neccessary because Point in GroIMP doesn't inherit the color of parent
		if (color == null) {
			color = new X3DColor();
			color.setColor(new float[coord.getPoint().length]);
			Tuple3f appColor = new Color3f(0, 0, 0);
			
			if (appearance != null)
				if (appearance.getMaterial() != null)
					appColor = appearance.getMaterial().getEmissiveColor();
			
			for (int i = 0; i < coord.getPoint().length; i=i+3) {
				color.getColor()[i] = appColor.x;
				color.getColor()[i+1] = appColor.y;
				color.getColor()[i+2] = appColor.z;
			}
		}
		
		// create points, set translations
		int pointCount = coord.getPoint().length;
		for (int i = 0; i < pointCount; i=i+3) {
			X3DPoint newPoint = new X3DPoint((int) i/3);
			newPoint.setTranslation(coord.getPoint()[i], -coord.getPoint()[i+2], coord.getPoint()[i+1]);
			newPoint.setColor(color.color[i], color.color[i+1], color.color[i+2]);
			this.addEdgeBitsTo(newPoint, Graph.SUCCESSOR_EDGE, null);
		}
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field coord$FIELD;
	public static final NType.Field color$FIELD;
	public static final NType.Field appearance$FIELD;
	public static final NType.Field drawAsSpheres$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DPointSet.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((X3DPointSet) o).drawAsSpheres = (boolean) value;
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
					return ((X3DPointSet) o).isDrawAsSpheres ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DPointSet) o).coord = (X3DCoordinate) value;
					return;
				case 1:
					((X3DPointSet) o).color = (X3DColor) value;
					return;
				case 2:
					((X3DPointSet) o).appearance = (X3DAppearance) value;
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
					return ((X3DPointSet) o).getCoord ();
				case 1:
					return ((X3DPointSet) o).getColor ();
				case 2:
					return ((X3DPointSet) o).getAppearance ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DPointSet ());
		$TYPE.addManagedField (coord$FIELD = new _Field ("coord", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DCoordinate.class), null, 0));
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DColor.class), null, 1));
		$TYPE.addManagedField (appearance$FIELD = new _Field ("appearance", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DAppearance.class), null, 2));
		$TYPE.addManagedField (drawAsSpheres$FIELD = new _Field ("drawAsSpheres", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.declareFieldAttribute (drawAsSpheres$FIELD, Attributes.DRAW_AS_SPHERES);
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
		return new X3DPointSet ();
	}

	public boolean isDrawAsSpheres ()
	{
		return drawAsSpheres;
	}

	public void setDrawAsSpheres (boolean value)
	{
		this.drawAsSpheres = (boolean) value;
	}

	public X3DCoordinate getCoord ()
	{
		return coord;
	}

	public void setCoord (X3DCoordinate value)
	{
		coord$FIELD.setObject (this, value);
	}

	public X3DColor getColor ()
	{
		return color;
	}

	public void setColor (X3DColor value)
	{
		color$FIELD.setObject (this, value);
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
