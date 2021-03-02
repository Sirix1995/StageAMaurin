package de.grogra.ext.x3d.objects;

import javax.vecmath.Point3f;
import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.ShadedNull;
import de.grogra.ext.x3d.interfaces.Appearance;
import de.grogra.ext.x3d.interfaces.Color;
import de.grogra.ext.x3d.interfaces.Coordinate;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.ext.x3d.objects.X3DMaterial;
import de.grogra.ext.x3d.Attributes;

/**
 * GroIMP node class for a x3d indexed line set.
 * X3D line sets are converted to one groimp line set node with
 * children. Every child is an instance of X3DLine and an extra groimp node. 
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DIndexedLineSet extends ShadedNull implements Value, Appearance, Color, Coordinate {

	protected X3DColor color = null;
	//enh:field hidden getter setter
	
	protected X3DCoordinate coord = null;
	//enh:field hidden getter setter
	
	protected int[] colorIndex = new int[]{};
	//enh:field hidden getter setter
	
	protected boolean colorPerVertex = true;
	//enh:field hidden getter setter
	
	protected int[] coordIndex = new int[]{};
	//enh:field hidden getter setter
	
	protected X3DAppearance appearance = null;
	//enh:field hidden getter setter
	
	protected boolean drawAsCylinders = false;
	//enh:field attr=Attributes.DRAW_AS_CYLINDERS getter setter
	
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
	public X3DIndexedLineSet() {
		super();
	}

	public void setValues() {
		// create lines
		int pointCount = (int) (coord.getPoint().length / 3.0f);
		// convert coordinates to GroIMP space
		Point3f[] coords = new Point3f[pointCount];
		for (int i = 0; i < pointCount; i++) {
			coords[i] = new Point3f( coord.getPoint()[3*i+0],
									-coord.getPoint()[3*i+2],
									 coord.getPoint()[3*i+1]);
		}
		
		int lineCount = 0;
		for (int i = 1; i < coordIndex.length; i++) {
			// loop over polylines in coord index
			// split every polyline to single lines of two vertices
			if ((coordIndex[i] != -1) && (coordIndex[i-1] != -1)) {
				float x = coords[coordIndex[i-1]].x;
				float y = coords[coordIndex[i-1]].y;
				float z = coords[coordIndex[i-1]].z;
				float dx = coords[coordIndex[i]].x - coords[coordIndex[i-1]].x;
				float dy = coords[coordIndex[i]].y - coords[coordIndex[i-1]].y;
				float dz = coords[coordIndex[i]].z - coords[coordIndex[i-1]].z;
				X3DLine newLine = new X3DLine(x, y, z, dx, dy, dz, lineCount);
				
				if ((color != null) && (colorPerVertex == false)) {
					// if exists a color node and color per vertex is false use colors from
					// indexedlineset node
					if (colorIndex.length == 0) {
						// if color index empty, the colours from the color node
						// are applied to each polyline  in order
						newLine.setColor(color.getColor()[3*lineCount+0],
										 color.getColor()[3*lineCount+1],
										 color.getColor()[3*lineCount+2]);
					} else {
						// use the color index to apply colors
						float r = color.getColor()[3*colorIndex[lineCount]+0];
						float g = color.getColor()[3*colorIndex[lineCount]+1];
						float b = color.getColor()[3*colorIndex[lineCount]+2];
						newLine.setColor(r, g, b);
					}
				} else {
					// use color from material node (if exists)
					if (appearance != null)
						if (appearance.getMaterial() != null) {
							X3DMaterial mat = appearance.getMaterial();
							newLine.setColor(mat.getDiffuseColor().x,
											 mat.getDiffuseColor().y,
											 mat.getDiffuseColor().z);
						}
				}
				this.addEdgeBitsTo(newLine, Graph.SUCCESSOR_EDGE, null);
				lineCount++;
			}
		}
		
	}

	//enh:insert $TYPE.addIdentityAccessor (Attributes.VIEW); 
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field coord$FIELD;
	public static final NType.Field colorIndex$FIELD;
	public static final NType.Field colorPerVertex$FIELD;
	public static final NType.Field coordIndex$FIELD;
	public static final NType.Field appearance$FIELD;
	public static final NType.Field drawAsCylinders$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DIndexedLineSet.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((X3DIndexedLineSet) o).colorPerVertex = (boolean) value;
					return;
				case 6:
					((X3DIndexedLineSet) o).drawAsCylinders = (boolean) value;
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
					return ((X3DIndexedLineSet) o).isColorPerVertex ();
				case 6:
					return ((X3DIndexedLineSet) o).isDrawAsCylinders ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DIndexedLineSet) o).color = (X3DColor) value;
					return;
				case 1:
					((X3DIndexedLineSet) o).coord = (X3DCoordinate) value;
					return;
				case 2:
					((X3DIndexedLineSet) o).colorIndex = (int[]) value;
					return;
				case 4:
					((X3DIndexedLineSet) o).coordIndex = (int[]) value;
					return;
				case 5:
					((X3DIndexedLineSet) o).appearance = (X3DAppearance) value;
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
					return ((X3DIndexedLineSet) o).getColor ();
				case 1:
					return ((X3DIndexedLineSet) o).getCoord ();
				case 2:
					return ((X3DIndexedLineSet) o).getColorIndex ();
				case 4:
					return ((X3DIndexedLineSet) o).getCoordIndex ();
				case 5:
					return ((X3DIndexedLineSet) o).getAppearance ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DIndexedLineSet ());
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DColor.class), null, 0));
		$TYPE.addManagedField (coord$FIELD = new _Field ("coord", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DCoordinate.class), null, 1));
		$TYPE.addManagedField (colorIndex$FIELD = new _Field ("colorIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 2));
		$TYPE.addManagedField (colorPerVertex$FIELD = new _Field ("colorPerVertex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (coordIndex$FIELD = new _Field ("coordIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 4));
		$TYPE.addManagedField (appearance$FIELD = new _Field ("appearance", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DAppearance.class), null, 5));
		$TYPE.addManagedField (drawAsCylinders$FIELD = new _Field ("drawAsCylinders", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 6));
		$TYPE.declareFieldAttribute (drawAsCylinders$FIELD, Attributes.DRAW_AS_CYLINDERS);
		$TYPE.addIdentityAccessor (Attributes.VIEW); 
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
		return new X3DIndexedLineSet ();
	}

	public boolean isColorPerVertex ()
	{
		return colorPerVertex;
	}

	public void setColorPerVertex (boolean value)
	{
		this.colorPerVertex = (boolean) value;
	}

	public boolean isDrawAsCylinders ()
	{
		return drawAsCylinders;
	}

	public void setDrawAsCylinders (boolean value)
	{
		this.drawAsCylinders = (boolean) value;
	}

	public X3DColor getColor ()
	{
		return color;
	}

	public void setColor (X3DColor value)
	{
		color$FIELD.setObject (this, value);
	}

	public X3DCoordinate getCoord ()
	{
		return coord;
	}

	public void setCoord (X3DCoordinate value)
	{
		coord$FIELD.setObject (this, value);
	}

	public int[] getColorIndex ()
	{
		return colorIndex;
	}

	public void setColorIndex (int[] value)
	{
		colorIndex$FIELD.setObject (this, value);
	}

	public int[] getCoordIndex ()
	{
		return coordIndex;
	}

	public void setCoordIndex (int[] value)
	{
		coordIndex$FIELD.setObject (this, value);
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
