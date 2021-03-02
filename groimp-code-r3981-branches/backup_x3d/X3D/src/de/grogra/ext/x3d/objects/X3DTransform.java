package de.grogra.ext.x3d.objects;

import javax.vecmath.*;
import de.grogra.imp3d.objects.Null;
import de.grogra.math.*;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Value;

/**
 * GroIMP node class for a x3d transform element.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DTransform extends Null implements Value {
	
	protected Tuple3f x3dCenter = new Point3f(0, 0, 0);
	
	protected Tuple4f x3dRotation = new Vector4f(0, 0, 1, 0);
	
	protected Tuple3f x3dScale = new Vector3f(1, 1, 1);
	
	protected Tuple4f x3dScaleOrientation = new Vector4f(0, 0, 1, 0);
	
	protected Tuple3f x3dTranslation = new Vector3f(0, 0, 0);
	
	protected Tuple3f x3dBboxCenter = new Point3f(0, 0, 0);
	//enh:field attr=Attributes.X3DBBOX_CENTER getter setter
	
	protected Tuple3f x3dBboxSize = new Vector3f(-1, -1, -1);
	//enh:field attr=Attributes.X3DBBOX_SIZE getter setter
	
	/**
	 * Constructor.
	 */
	public X3DTransform(){
		super();
		Matrix4d m = new Matrix4d();
		m.setIdentity();
		this.setTransform(m);
		this.setValues();
	}
	
	public void setValues() {
		this.setTransform(calculateTransformations());
	}
	
	/**
	 * This method returns a transformation matrix. The matrix is calculated
	 * with the (x3d) transformations of the current object.
	 * @return
	 */
	protected TMatrix4d calculateTransformations() {
		// calculate attributes (T * C * R * SR * S * -SR * -C)
		TMatrix4d m = new TMatrix4d();
		
		m.setTranslation(new Vector3d(x3dTranslation.x, -x3dTranslation.z, x3dTranslation.y));

		TMatrix4d tmp = new TMatrix4d();
		tmp.setTranslation(new Vector3d(x3dCenter.x, -x3dCenter.z, x3dCenter.y));
		m.mul(tmp);

		tmp.setIdentity();
		tmp.set(new AxisAngle4d(x3dRotation.x, -x3dRotation.z, x3dRotation.y, x3dRotation.w));
		m.mul(tmp);		

		tmp = new TMatrix4d();
		tmp.set(new AxisAngle4d(x3dScaleOrientation.x, -x3dScaleOrientation.z, x3dScaleOrientation.y, x3dScaleOrientation.w));
		m.mul(tmp);			

		tmp.setIdentity();
		tmp.setElement(0, 0, x3dScale.x);
		tmp.setElement(1, 1, x3dScale.z);
		tmp.setElement(2, 2, x3dScale.y);
		m.mul(tmp);	

		tmp.setIdentity();
		tmp.set(new AxisAngle4d(x3dScaleOrientation.x, -x3dScaleOrientation.z, x3dScaleOrientation.y, x3dScaleOrientation.w));
		tmp.invert();
		m.mul(tmp);			

		tmp.setIdentity();
		tmp.setTranslation(new Vector3d(x3dCenter.x, -x3dCenter.z, x3dCenter.y));
		tmp.invert();
		m.mul(tmp);
		
		return m;
	}
	
	public Tuple3f getX3dCenter ()
	{
		return x3dCenter;
	}

	public void setX3dCenter (Tuple3f value)
	{
		this.x3dCenter = value;
	}

	public Tuple4f getX3dRotation ()
	{
		return x3dRotation;
	}

	public void setX3dRotation (Tuple4f value)
	{
		this.x3dRotation = value;
	}

	public Tuple3f getX3dScale ()
	{
		return x3dScale;
	}

	public void setX3dScale (Tuple3f value)
	{
		this.x3dScale = value;
	}

	public Tuple4f getX3dScaleOrientation ()
	{
		return x3dScaleOrientation;
	}

	public void setX3dScaleOrientation (Tuple4f value)
	{
		this.x3dScaleOrientation = value;
	}

	public Tuple3f getX3dTranslation ()
	{
		return x3dTranslation;
	}

	public void setX3dTranslation (Tuple3f value)
	{
		this.x3dTranslation = value;
	}
	
	//enh:insert	
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field x3dBboxCenter$FIELD;
	public static final NType.Field x3dBboxSize$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DTransform.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((X3DTransform) o).x3dBboxCenter = (Tuple3f) value;
					return;
				case 1:
					((X3DTransform) o).x3dBboxSize = (Tuple3f) value;
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
					return ((X3DTransform) o).getX3dBboxCenter ();
				case 1:
					return ((X3DTransform) o).getX3dBboxSize ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DTransform ());
		$TYPE.addManagedField (x3dBboxCenter$FIELD = new _Field ("x3dBboxCenter", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 0));
		$TYPE.addManagedField (x3dBboxSize$FIELD = new _Field ("x3dBboxSize", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 1));
		$TYPE.declareFieldAttribute (x3dBboxCenter$FIELD, Attributes.X3DBBOX_CENTER);
		$TYPE.declareFieldAttribute (x3dBboxSize$FIELD, Attributes.X3DBBOX_SIZE);
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
		return new X3DTransform ();
	}

	public Tuple3f getX3dBboxCenter ()
	{
		return x3dBboxCenter;
	}

	public void setX3dBboxCenter (Tuple3f value)
	{
		x3dBboxCenter$FIELD.setObject (this, value);
	}

	public Tuple3f getX3dBboxSize ()
	{
		return x3dBboxSize;
	}

	public void setX3dBboxSize (Tuple3f value)
	{
		x3dBboxSize$FIELD.setObject (this, value);
	}

//enh:end
}
