package de.grogra.ext.x3d.objects;

import de.grogra.imp3d.objects.Cylinder;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Value;

/**
 * GroIMP node class for a x3d cylinder.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DCylinder extends Cylinder implements Value {
	
	protected boolean x3dBottom = true;
	//enh:field hidden getter setter
	
	protected float x3dHeight = 2.0f;
	//enh:field hidden getter setter
	
	protected float x3dRadius = 1.0f;
	//enh:field hidden getter setter
	
	protected boolean x3dSide = true;
	//enh:field attr=Attributes.X3DSIDE getter setter
	
	protected boolean x3dSolid = true;
	//enh:field attr=Attributes.X3DSOLID getter setter
	
	protected boolean x3dTop = true;
	//enh:field hidden getter setter
	
	/**
	 * Constructor.
	 */
	public X3DCylinder() {
		super();
		setValues();
	}
	
	public void setValues() {
		this.setRadius(x3dRadius);
		this.setLength(x3dHeight);
		this.setStartPosition(-0.5f);
		this.setEndPosition(0);
		this.setTopOpen(!x3dTop);
		this.setBaseOpen(!x3dBottom);		
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field x3dBottom$FIELD;
	public static final NType.Field x3dHeight$FIELD;
	public static final NType.Field x3dRadius$FIELD;
	public static final NType.Field x3dSide$FIELD;
	public static final NType.Field x3dSolid$FIELD;
	public static final NType.Field x3dTop$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DCylinder.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((X3DCylinder) o).x3dBottom = (boolean) value;
					return;
				case 3:
					((X3DCylinder) o).x3dSide = (boolean) value;
					return;
				case 4:
					((X3DCylinder) o).x3dSolid = (boolean) value;
					return;
				case 5:
					((X3DCylinder) o).x3dTop = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((X3DCylinder) o).isX3dBottom ();
				case 3:
					return ((X3DCylinder) o).isX3dSide ();
				case 4:
					return ((X3DCylinder) o).isX3dSolid ();
				case 5:
					return ((X3DCylinder) o).isX3dTop ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((X3DCylinder) o).x3dHeight = (float) value;
					return;
				case 2:
					((X3DCylinder) o).x3dRadius = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((X3DCylinder) o).getX3dHeight ();
				case 2:
					return ((X3DCylinder) o).getX3dRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DCylinder ());
		$TYPE.addManagedField (x3dBottom$FIELD = new _Field ("x3dBottom", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.addManagedField (x3dHeight$FIELD = new _Field ("x3dHeight", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (x3dRadius$FIELD = new _Field ("x3dRadius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (x3dSide$FIELD = new _Field ("x3dSide", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (x3dSolid$FIELD = new _Field ("x3dSolid", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 4));
		$TYPE.addManagedField (x3dTop$FIELD = new _Field ("x3dTop", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 5));
		$TYPE.declareFieldAttribute (x3dSide$FIELD, Attributes.X3DSIDE);
		$TYPE.declareFieldAttribute (x3dSolid$FIELD, Attributes.X3DSOLID);
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
		return new X3DCylinder ();
	}

	public boolean isX3dBottom ()
	{
		return x3dBottom;
	}

	public void setX3dBottom (boolean value)
	{
		this.x3dBottom = (boolean) value;
	}

	public boolean isX3dSide ()
	{
		return x3dSide;
	}

	public void setX3dSide (boolean value)
	{
		this.x3dSide = (boolean) value;
	}

	public boolean isX3dSolid ()
	{
		return x3dSolid;
	}

	public void setX3dSolid (boolean value)
	{
		this.x3dSolid = (boolean) value;
	}

	public boolean isX3dTop ()
	{
		return x3dTop;
	}

	public void setX3dTop (boolean value)
	{
		this.x3dTop = (boolean) value;
	}

	public float getX3dHeight ()
	{
		return x3dHeight;
	}

	public void setX3dHeight (float value)
	{
		this.x3dHeight = (float) value;
	}

	public float getX3dRadius ()
	{
		return x3dRadius;
	}

	public void setX3dRadius (float value)
	{
		this.x3dRadius = (float) value;
	}

//enh:end
}
