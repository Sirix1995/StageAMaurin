package de.grogra.ext.x3d.objects;

import de.grogra.ext.x3d.interfaces.Color;
import de.grogra.ext.x3d.interfaces.Coordinate;
import de.grogra.ext.x3d.interfaces.Normal;
import de.grogra.ext.x3d.interfaces.TextureCoordinate;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.imp3d.objects.*;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * GroIMP node class for a x3d indexed face set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DIndexedFaceSet extends MeshNode implements Value, Color, Coordinate, Normal, TextureCoordinate {

	protected X3DColor color = null;
	//enh:field hidden getter setter
	
	protected X3DCoordinate coord = null;
	//enh:field hidden getter setter
	
	protected X3DNormal normal = null;
	//enh:field hidden getter setter
	
	protected X3DTextureCoordinate texCoord = null;
	//enh:field hidden getter setter
	
	protected boolean ccw = true;
	//enh:field hidden getter setter
	
	protected int[] colorIndex = new int[]{};
	//enh:field hidden getter setter
	
	protected boolean colorPerVertex = true;
	//enh:field hidden getter setter
	
	protected boolean convex = true;
	//enh:field hidden getter setter
	
	protected int[] coordIndex = new int[]{};
	//enh:field hidden getter setter
	
	protected float creaseAngle = 0;
	//enh:field hidden getter setter
	
	protected int[] normalIndex = new int[]{};
	//enh:field hidden getter setter
	
	protected boolean normalPerVertex = true;
	//enh:field hidden getter setter
	
	protected boolean solid = true;
	//enh:field hidden getter setter
	
	protected int[] texCoordIndex = new int[]{};
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
	public X3DIndexedFaceSet() {
		super();
	}
	
	public void setValues() {
		// if no coordinates exist, no mesh is created
		if (coord == null)
			return;
		
		PolygonMesh p = new PolygonMesh();
		
		int coordIndexLength = coordIndex.length;
		
		int[] newCoordIndex = coordIndex.clone();
		float[] newCoord = coord.getPoint().clone();
		float[] newTexCoord = null;
		float[] newNormal = null;
		
		if (texCoord != null)
			newTexCoord = texCoord.getPoint().clone();
		else {
			// TODO: compute UVs manually cause PolygonMesh only generates dummy data
			// look in x3d spec
		}
		if ((normal != null) && normalPerVertex)
			newNormal = normal.getVector().clone();

		// texture coordinate indices or normal indices are given,
		// so transformation is needed because GroIMP has only one list
		// for indices of coordinates, texture coordinates and normals
		// best way: create new index list straight in ascending order
		// also convert coordinates and normals into GroIMP space
		newCoordIndex = new int[coordIndexLength];
		FloatList tmpCoord = new FloatList();
		FloatList tmpTexCoord = new FloatList();
		FloatList tmpNormal = null;
		if (normalPerVertex)
			tmpNormal = new FloatList();
		int indexCounter = 0;
		// if texture coordinates given but without texture coordinates index list
		// take coordinates index list
		int[] newTexCoordIndex = null;
		if (texCoordIndex.length > 0)
			newTexCoordIndex = texCoordIndex.clone();
		else
			newTexCoordIndex = coordIndex.clone();
		// if normals given but without normals index list
		// take coordinates index list
		int[] newNormalIndex = null;
		if (normalIndex.length > 0)
			newNormalIndex = normalIndex.clone();
		else
			newNormalIndex = coordIndex.clone();
		for (int i = 0; i < coordIndexLength; i++) {
			if (coordIndex[i] != -1) {
				newCoordIndex[i] = indexCounter++;
				tmpCoord.add( newCoord[3*coordIndex[i]+0]);
				tmpCoord.add(-newCoord[3*coordIndex[i]+2]);
				tmpCoord.add( newCoord[3*coordIndex[i]+1]);
				if (texCoord != null) {
					tmpTexCoord.add(newTexCoord[2*newTexCoordIndex[i]+0]);
					tmpTexCoord.add(newTexCoord[2*newTexCoordIndex[i]+1]);
				}
				if ((normal != null) && normalPerVertex) {
					tmpNormal.add( newNormal[3*newNormalIndex[i]+0]);
					tmpNormal.add(-newNormal[3*newNormalIndex[i]+2]);
					tmpNormal.add( newNormal[3*newNormalIndex[i]+1]);
				}
			} else {
				newCoordIndex[i] = coordIndex[i];
			}
		}
		tmpCoord.trimToSize();
		newCoord = tmpCoord.toArray();
		tmpTexCoord.trimToSize();
		newTexCoord = tmpTexCoord.toArray();
		if (normalPerVertex) {
			tmpNormal.trimToSize();
			newNormal = tmpNormal.toArray();
		}

		// set the calculated data
		// if normalPerVertex is false, GroIMP has to calculate normals
		p.setVertexData(new FloatList(newCoord));
		if (texCoord != null) {
			p.setTextureData(newTexCoord);
		}
		if ((normal != null) && normalPerVertex)
			p.setNormalData(newNormal);
		
		// transfer coordinate indices from coordIndex into int list for polygon mesh
		// polygons with more than three vertices are splitted into triangles
		// if ccw == false polygons are created in the opposite direction
		IntList coordIndexList = new IntList();
		for (int i = 0; i < coordIndexLength; i++) {
			int firstVertexIndex = i;
			coordIndexList.add(newCoordIndex[i]);
			if (ccw) {
				coordIndexList.add(newCoordIndex[i+1]);
				coordIndexList.add(newCoordIndex[i+2]);
			} else {
				coordIndexList.add(newCoordIndex[i+2]);
				coordIndexList.add(newCoordIndex[i+1]);
			}
			i+=3;
			while (( i < coordIndexLength ) && (newCoordIndex[i] != -1))  {
				coordIndexList.add(newCoordIndex[i-1]);
				if (ccw) {
					coordIndexList.add(newCoordIndex[i]);
					coordIndexList.add(newCoordIndex[firstVertexIndex]);
				} else {
					coordIndexList.add(newCoordIndex[firstVertexIndex]);
					coordIndexList.add(newCoordIndex[i]);
				}
				i++;
			}
		}
		p.setIndexData(coordIndexList);

		this.setVisibleSides(solid?0:2);
		      
		this.setPolygons(p);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field coord$FIELD;
	public static final NType.Field normal$FIELD;
	public static final NType.Field texCoord$FIELD;
	public static final NType.Field ccw$FIELD;
	public static final NType.Field colorIndex$FIELD;
	public static final NType.Field colorPerVertex$FIELD;
	public static final NType.Field convex$FIELD;
	public static final NType.Field coordIndex$FIELD;
	public static final NType.Field creaseAngle$FIELD;
	public static final NType.Field normalIndex$FIELD;
	public static final NType.Field normalPerVertex$FIELD;
	public static final NType.Field solid$FIELD;
	public static final NType.Field texCoordIndex$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DIndexedFaceSet.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 4:
					((X3DIndexedFaceSet) o).ccw = (boolean) value;
					return;
				case 6:
					((X3DIndexedFaceSet) o).colorPerVertex = (boolean) value;
					return;
				case 7:
					((X3DIndexedFaceSet) o).convex = (boolean) value;
					return;
				case 11:
					((X3DIndexedFaceSet) o).normalPerVertex = (boolean) value;
					return;
				case 12:
					((X3DIndexedFaceSet) o).solid = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 4:
					return ((X3DIndexedFaceSet) o).isCcw ();
				case 6:
					return ((X3DIndexedFaceSet) o).isColorPerVertex ();
				case 7:
					return ((X3DIndexedFaceSet) o).isConvex ();
				case 11:
					return ((X3DIndexedFaceSet) o).isNormalPerVertex ();
				case 12:
					return ((X3DIndexedFaceSet) o).isSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 9:
					((X3DIndexedFaceSet) o).creaseAngle = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 9:
					return ((X3DIndexedFaceSet) o).getCreaseAngle ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DIndexedFaceSet) o).color = (X3DColor) value;
					return;
				case 1:
					((X3DIndexedFaceSet) o).coord = (X3DCoordinate) value;
					return;
				case 2:
					((X3DIndexedFaceSet) o).normal = (X3DNormal) value;
					return;
				case 3:
					((X3DIndexedFaceSet) o).texCoord = (X3DTextureCoordinate) value;
					return;
				case 5:
					((X3DIndexedFaceSet) o).colorIndex = (int[]) value;
					return;
				case 8:
					((X3DIndexedFaceSet) o).coordIndex = (int[]) value;
					return;
				case 10:
					((X3DIndexedFaceSet) o).normalIndex = (int[]) value;
					return;
				case 13:
					((X3DIndexedFaceSet) o).texCoordIndex = (int[]) value;
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
					return ((X3DIndexedFaceSet) o).getColor ();
				case 1:
					return ((X3DIndexedFaceSet) o).getCoord ();
				case 2:
					return ((X3DIndexedFaceSet) o).getNormal ();
				case 3:
					return ((X3DIndexedFaceSet) o).getTexCoord ();
				case 5:
					return ((X3DIndexedFaceSet) o).getColorIndex ();
				case 8:
					return ((X3DIndexedFaceSet) o).getCoordIndex ();
				case 10:
					return ((X3DIndexedFaceSet) o).getNormalIndex ();
				case 13:
					return ((X3DIndexedFaceSet) o).getTexCoordIndex ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DIndexedFaceSet ());
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DColor.class), null, 0));
		$TYPE.addManagedField (coord$FIELD = new _Field ("coord", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DCoordinate.class), null, 1));
		$TYPE.addManagedField (normal$FIELD = new _Field ("normal", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DNormal.class), null, 2));
		$TYPE.addManagedField (texCoord$FIELD = new _Field ("texCoord", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (X3DTextureCoordinate.class), null, 3));
		$TYPE.addManagedField (ccw$FIELD = new _Field ("ccw", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 4));
		$TYPE.addManagedField (colorIndex$FIELD = new _Field ("colorIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 5));
		$TYPE.addManagedField (colorPerVertex$FIELD = new _Field ("colorPerVertex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 6));
		$TYPE.addManagedField (convex$FIELD = new _Field ("convex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 7));
		$TYPE.addManagedField (coordIndex$FIELD = new _Field ("coordIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 8));
		$TYPE.addManagedField (creaseAngle$FIELD = new _Field ("creaseAngle", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 9));
		$TYPE.addManagedField (normalIndex$FIELD = new _Field ("normalIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 10));
		$TYPE.addManagedField (normalPerVertex$FIELD = new _Field ("normalPerVertex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 11));
		$TYPE.addManagedField (solid$FIELD = new _Field ("solid", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.BOOLEAN, null, 12));
		$TYPE.addManagedField (texCoordIndex$FIELD = new _Field ("texCoordIndex", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (int[].class), null, 13));
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
		return new X3DIndexedFaceSet ();
	}

	public boolean isCcw ()
	{
		return ccw;
	}

	public void setCcw (boolean value)
	{
		this.ccw = (boolean) value;
	}

	public boolean isColorPerVertex ()
	{
		return colorPerVertex;
	}

	public void setColorPerVertex (boolean value)
	{
		this.colorPerVertex = (boolean) value;
	}

	public boolean isConvex ()
	{
		return convex;
	}

	public void setConvex (boolean value)
	{
		this.convex = (boolean) value;
	}

	public boolean isNormalPerVertex ()
	{
		return normalPerVertex;
	}

	public void setNormalPerVertex (boolean value)
	{
		this.normalPerVertex = (boolean) value;
	}

	public boolean isSolid ()
	{
		return solid;
	}

	public void setSolid (boolean value)
	{
		this.solid = (boolean) value;
	}

	public float getCreaseAngle ()
	{
		return creaseAngle;
	}

	public void setCreaseAngle (float value)
	{
		this.creaseAngle = (float) value;
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

	public int[] getNormalIndex ()
	{
		return normalIndex;
	}

	public void setNormalIndex (int[] value)
	{
		normalIndex$FIELD.setObject (this, value);
	}

	public int[] getTexCoordIndex ()
	{
		return texCoordIndex;
	}

	public void setTexCoordIndex (int[] value)
	{
		texCoordIndex$FIELD.setObject (this, value);
	}

//enh:end
}
