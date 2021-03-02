package de.grogra.ext.x3d.objects;

import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import de.grogra.imp3d.objects.Box;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Value;

/**
 * GroIMP node class for a x3d box.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DBox extends Box implements Value {
	
	protected Tuple3f x3dSize = new Vector3f(2, 2, 2);
	//enh:field hidden getter setter
	
	protected boolean x3dSolid = true;
	//enh:field attr=Attributes.X3DSOLID getter setter
	
	/**
	 * Constructor.
	 */
	public X3DBox() {
		super();
		setValues();
	}
	
	public void setValues() {
		this.setLength(x3dSize.y);
		this.setWidth(x3dSize.x);
		this.setHeight(x3dSize.z);
		
		this.setShiftPivot(false);
		this.setStartPosition(-0.5f);
		this.setEndPosition(0);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field x3dSize$FIELD;
	public static final NType.Field x3dSolid$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DBox.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((X3DBox) o).x3dSolid = (boolean) value;
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
					return ((X3DBox) o).isX3dSolid ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DBox) o).x3dSize = (Tuple3f) value;
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
					return ((X3DBox) o).getX3dSize ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DBox ());
		$TYPE.addManagedField (x3dSize$FIELD = new _Field ("x3dSize", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 0));
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
		return new X3DBox ();
	}

	public boolean isX3dSolid ()
	{
		return x3dSolid;
	}

	public void setX3dSolid (boolean value)
	{
		this.x3dSolid = (boolean) value;
	}

	public Tuple3f getX3dSize ()
	{
		return x3dSize;
	}

	public void setX3dSize (Tuple3f value)
	{
		x3dSize$FIELD.setObject (this, value);
	}

//enh:end
	
}
