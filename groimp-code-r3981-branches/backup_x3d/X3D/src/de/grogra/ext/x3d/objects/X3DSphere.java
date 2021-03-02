package de.grogra.ext.x3d.objects;

import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.imp3d.objects.Sphere;

/**
 * GroIMP node class for a x3d sphere.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DSphere extends Sphere implements Value {
	
	protected float x3dRadius = 1.0f;
	//enh:field hidden getter setter
	
	protected boolean x3dSolid = true;
	//enh:field attr=Attributes.X3DSOLID getter setter

	/**
	 * Constructor.
	 */
	public X3DSphere() {
		super();
		setValues();
	}

	public void setValues() {
		this.setRadius(x3dRadius);
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field x3dRadius$FIELD;
	public static final NType.Field x3dSolid$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DSphere.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((X3DSphere) o).x3dSolid = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((X3DSphere) o).isX3dSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((X3DSphere) o).x3dRadius = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((X3DSphere) o).getX3dRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DSphere ());
		$TYPE.addManagedField (x3dRadius$FIELD = new _Field ("x3dRadius", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (x3dSolid$FIELD = new _Field ("x3dSolid", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
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
		return new X3DSphere ();
	}

	public boolean isX3dSolid ()
	{
		return x3dSolid;
	}

	public void setX3dSolid (boolean value)
	{
		this.x3dSolid = (boolean) value;
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
