package de.grogra.ext.x3d.objects;

import de.grogra.imp3d.objects.MeshNode;
import de.grogra.imp3d.objects.PolygonMesh;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.ext.x3d.interfaces.Color;
import de.grogra.ext.x3d.interfaces.Normal;
import de.grogra.ext.x3d.interfaces.TextureCoordinate;
import de.grogra.ext.x3d.interfaces.Value;

/**
 * GroIMP node class for a x3d elevation grid.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DElevationGrid extends MeshNode implements Value, Color, Normal, TextureCoordinate {

	protected X3DColor color = null;
	//enh:field hidden getter setter
	
	protected X3DNormal normal = null;
	//enh:field hidden getter setter
	
	protected X3DTextureCoordinate texCoord = null;
	//enh:field hidden getter setter
	
	protected boolean x3dCcw = true;
	//enh:field hidden getter setter
	
	protected boolean x3dColorPerVertex = true;
	//enh:field hidden getter setter
	
	protected float x3dCreaseAngle = 0;
	//enh:field hidden getter setter
	
	protected float[] x3dHeight = new float[]{};
	//enh:field hidden getter setter
	
	protected boolean x3dNormalPerVertex = true;
	//enh:field hidden getter setter
	
	protected boolean x3dSolid = true;
	//enh:field hidden getter setter
	
	protected int x3dXDimension = 0;
	//enh:field hidden getter setter
	
	protected float x3dXSpacing = 1;
	//enh:field hidden getter setter
	
	protected int x3dZDimension = 0;
	//enh:field hidden getter setter
	
	protected float x3dZSpacing = 1;
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
	public X3DElevationGrid() {
		super();
	}
	
	public void setValues() {
		PolygonMesh p = new PolygonMesh();
		
		float[] vertArray = new float[x3dXDimension * x3dZDimension * 3];
		for (int j = 0; j < x3dZDimension; j++) {
			for (int i = 0; i < x3dXDimension; i++) {
				vertArray[3 * (i + j * x3dXDimension) + 0] = x3dXSpacing * i;
				vertArray[3 * (i + j * x3dXDimension) + 1] = -x3dZSpacing * j;
				vertArray[3 * (i + j * x3dXDimension) + 2] = x3dHeight[i + j * x3dXDimension];				
			}
		}
		
		IntList coordIndexList = new IntList();
		for (int j = 0; j < x3dZDimension - 1; j++) {
			for (int i = 0; i < x3dXDimension - 1; i++) {
				if (x3dCcw) {
					coordIndexList.add(i + 0 + (j + 0) * x3dXDimension);
					coordIndexList.add(i + 0 + (j + 1) * x3dXDimension);
					coordIndexList.add(i + 1 + (j + 0) * x3dXDimension);
					
					coordIndexList.add(i + 1 + (j + 1) * x3dXDimension);
					coordIndexList.add(i + 1 + (j + 0) * x3dXDimension);
					coordIndexList.add(i + 0 + (j + 1) * x3dXDimension);
				}
				else {
					coordIndexList.add(i + 0 + (j + 0) * x3dXDimension);
					coordIndexList.add(i + 1 + (j + 0) * x3dXDimension);
					coordIndexList.add(i + 0 + (j + 1) * x3dXDimension);
					
					coordIndexList.add(i + 1 + (j + 1) * x3dXDimension);
					coordIndexList.add(i + 0 + (j + 1) * x3dXDimension);					
					coordIndexList.add(i + 1 + (j + 0) * x3dXDimension);
				}
			}
		}
		
		float[] textureData = null;
		if (texCoord != null) {
			// use texture coordinates from node 
			textureData = texCoord.getPoint();
		}
		else {
			// generate own texture coordinates
			textureData = new float[x3dXDimension * x3dZDimension * 2];
			for (int j = 0; j < x3dZDimension; j++) {
				for (int i = 0; i < x3dXDimension; i++) {
					textureData[2 * (i + j * x3dXDimension) + 0] = (float) i / (x3dXDimension - 1);
					textureData[2 * (i + j * x3dXDimension) + 1] = (float) j / (x3dZDimension - 1);
				}
			}
		}

		float[] normalData = null;
		if ((normal != null) && (x3dNormalPerVertex)) {
			// use normal vectors from node
			normalData = normal.getVector();
		}
		else {
			// generate own normal vectors (in consideration of crease angle)
		}
		
		p.setIndexData(coordIndexList);
		p.setVertexData(new FloatList(vertArray));
		p.setTextureData(textureData);
		p.setNormalData(normalData);
		
		this.setPolygons(p);
		this.setVisibleSides(x3dSolid?0:2);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field normal$FIELD;
	public static final NType.Field texCoord$FIELD;
	public static final NType.Field x3dCcw$FIELD;
	public static final NType.Field x3dColorPerVertex$FIELD;
	public static final NType.Field x3dCreaseAngle$FIELD;
	public static final NType.Field x3dHeight$FIELD;
	public static final NType.Field x3dNormalPerVertex$FIELD;
	public static final NType.Field x3dSolid$FIELD;
	public static final NType.Field x3dXDimension$FIELD;
	public static final NType.Field x3dXSpacing$FIELD;
	public static final NType.Field x3dZDimension$FIELD;
	public static final NType.Field x3dZSpacing$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DElevationGrid.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 3:
					((X3DElevationGrid) o).x3dCcw = (boolean) value;
					return;
				case 4:
					((X3DElevationGrid) o).x3dColorPerVertex = (boolean) value;
					return;
				case 7:
					((X3DElevationGrid) o).x3dNormalPerVertex = (boolean) value;
					return;
				case 8:
					((X3DElevationGrid) o).x3dSolid = (boolean) value;
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
					return ((X3DElevationGrid) o).isX3dCcw ();
				case 4:
					return ((X3DElevationGrid) o).isX3dColorPerVertex ();
				case 7:
					return ((X3DElevationGrid) o).isX3dNormalPerVertex ();
				case 8:
					return ((X3DElevationGrid) o).isX3dSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 9:
					((X3DElevationGrid) o).x3dXDimension = (int) value;
					return;
				case 11:
					((X3DElevationGrid) o).x3dZDimension = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 9:
					return ((X3DElevationGrid) o).getX3dXDimension ();
				case 11:
					return ((X3DElevationGrid) o).getX3dZDimension ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 5:
					((X3DElevationGrid) o).x3dCreaseAngle = (float) value;
					return;
				case 10:
					((X3DElevationGrid) o).x3dXSpacing = (float) value;
					return;
				case 12:
					((X3DElevationGrid) o).x3dZSpacing = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 5:
					return ((X3DElevationGrid) o).getX3dCreaseAngle ();
				case 10:
					return ((X3DElevationGrid) o).getX3dXSpacing ();
				case 12:
					return ((X3DElevationGrid) o).getX3dZSpacing ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DElevationGrid) o).color = (X3DColor) value;
					return;
				case 1:
					((X3DElevationGrid) o).normal = (X3DNormal) value;
					return;
				case 2:
					((X3DElevationGrid) o).texCoord = (X3DTextureCoordinate) value;
					return;
				case 6:
					((X3DElevationGrid) o).x3dHeight = (float[]) value;
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
					return ((X3DElevationGrid) o).getColor ();
				case 1:
					return ((X3DElevationGrid) o).getNormal ();
				case 2:
					return ((X3DElevationGrid) o).getTexCoord ();
				case 6:
					return ((X3DElevationGrid) o).getX3dHeight ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DElevationGrid ());
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DColor.class), null, 0));
		$TYPE.addManagedField (normal$FIELD = new _Field ("normal", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DNormal.class), null, 1));
		$TYPE.addManagedField (texCoord$FIELD = new _Field ("texCoord", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DTextureCoordinate.class), null, 2));
		$TYPE.addManagedField (x3dCcw$FIELD = new _Field ("x3dCcw", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (x3dColorPerVertex$FIELD = new _Field ("x3dColorPerVertex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 4));
		$TYPE.addManagedField (x3dCreaseAngle$FIELD = new _Field ("x3dCreaseAngle", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (x3dHeight$FIELD = new _Field ("x3dHeight", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 6));
		$TYPE.addManagedField (x3dNormalPerVertex$FIELD = new _Field ("x3dNormalPerVertex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 7));
		$TYPE.addManagedField (x3dSolid$FIELD = new _Field ("x3dSolid", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 8));
		$TYPE.addManagedField (x3dXDimension$FIELD = new _Field ("x3dXDimension", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.INT, null, 9));
		$TYPE.addManagedField (x3dXSpacing$FIELD = new _Field ("x3dXSpacing", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 10));
		$TYPE.addManagedField (x3dZDimension$FIELD = new _Field ("x3dZDimension", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.INT, null, 11));
		$TYPE.addManagedField (x3dZSpacing$FIELD = new _Field ("x3dZSpacing", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 12));
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
		return new X3DElevationGrid ();
	}

	public boolean isX3dCcw ()
	{
		return x3dCcw;
	}

	public void setX3dCcw (boolean value)
	{
		this.x3dCcw = (boolean) value;
	}

	public boolean isX3dColorPerVertex ()
	{
		return x3dColorPerVertex;
	}

	public void setX3dColorPerVertex (boolean value)
	{
		this.x3dColorPerVertex = (boolean) value;
	}

	public boolean isX3dNormalPerVertex ()
	{
		return x3dNormalPerVertex;
	}

	public void setX3dNormalPerVertex (boolean value)
	{
		this.x3dNormalPerVertex = (boolean) value;
	}

	public boolean isX3dSolid ()
	{
		return x3dSolid;
	}

	public void setX3dSolid (boolean value)
	{
		this.x3dSolid = (boolean) value;
	}

	public int getX3dXDimension ()
	{
		return x3dXDimension;
	}

	public void setX3dXDimension (int value)
	{
		this.x3dXDimension = (int) value;
	}

	public int getX3dZDimension ()
	{
		return x3dZDimension;
	}

	public void setX3dZDimension (int value)
	{
		this.x3dZDimension = (int) value;
	}

	public float getX3dCreaseAngle ()
	{
		return x3dCreaseAngle;
	}

	public void setX3dCreaseAngle (float value)
	{
		this.x3dCreaseAngle = (float) value;
	}

	public float getX3dXSpacing ()
	{
		return x3dXSpacing;
	}

	public void setX3dXSpacing (float value)
	{
		this.x3dXSpacing = (float) value;
	}

	public float getX3dZSpacing ()
	{
		return x3dZSpacing;
	}

	public void setX3dZSpacing (float value)
	{
		this.x3dZSpacing = (float) value;
	}

	public X3DColor getColor ()
	{
		return color;
	}

	public void setColor (X3DColor value)
	{
		color$FIELD.setObject (this, value);
	}

	public X3DNormal getNormal ()
	{
		return normal;
	}

	public void setNormal (X3DNormal value)
	{
		normal$FIELD.setObject (this, value);
	}

	public X3DTextureCoordinate getTexCoord ()
	{
		return texCoord;
	}

	public void setTexCoord (X3DTextureCoordinate value)
	{
		texCoord$FIELD.setObject (this, value);
	}

	public float[] getX3dHeight ()
	{
		return x3dHeight;
	}

	public void setX3dHeight (float[] value)
	{
		x3dHeight$FIELD.setObject (this, value);
	}

//enh:end
}
