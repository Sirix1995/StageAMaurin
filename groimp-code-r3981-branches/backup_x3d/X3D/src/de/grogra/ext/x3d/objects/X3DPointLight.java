package de.grogra.ext.x3d.objects;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.imp3d.objects.LightNode;
import de.grogra.imp3d.objects.PointLight;
import de.grogra.math.TMatrix4d;

/**
 * GroIMP node class for a x3d point light.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DPointLight extends LightNode implements Value {
	
	//TODO: sicher nicht korrekt
	private float intensityFactor = 100f;
	//enh:field hidden getter setter
	
	protected float x3dAmbientIntensity = 0;
	//enh:field attr=Attributes.X3DAMBIENT_INTENSITY getter setter
	
	//TODO: match attenuation with groimp
	protected Tuple3f x3dAttenuation = new Vector3f(1, 0, 0);
	//enh:field hidden getter setter
	
	protected Tuple3f x3dColor = new Color3f(1, 1, 1);
	//enh:field hidden getter setter
	
	protected float x3dIntensity = 1;
	//enh:field hidden getter setter
	
	protected Tuple3f x3dLocation = new Point3f(0, 0, 0);
	//enh:field hidden getter setter
	
	protected boolean x3dOn = true;
	//enh:field attr=Attributes.X3DON getter setter
	
	protected float x3dRadius = 100;
	//enh:field attr=Attributes.X3DRADIUS getter setter
	
	/**
	 * Constructor.
	 */
	public X3DPointLight() {
		super();
		this.setValues();
	}
	
	public void setValues() {
		PointLight l = new PointLight ();
		light = l;
		
		// set color
		l.getColor().set(x3dColor.x, x3dColor.y, x3dColor.z);
		
		// set location
		TMatrix4d transMat = new TMatrix4d();
		transMat.setTranslation(new Vector3d(x3dLocation.x, -x3dLocation.z, x3dLocation.y));
		this.setTransform(transMat);
		
		// set intensity
		l.setPower(intensityFactor * x3dIntensity);

	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field intensityFactor$FIELD;
	public static final NType.Field x3dAmbientIntensity$FIELD;
	public static final NType.Field x3dAttenuation$FIELD;
	public static final NType.Field x3dColor$FIELD;
	public static final NType.Field x3dIntensity$FIELD;
	public static final NType.Field x3dLocation$FIELD;
	public static final NType.Field x3dOn$FIELD;
	public static final NType.Field x3dRadius$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DPointLight.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 6:
					((X3DPointLight) o).x3dOn = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 6:
					return ((X3DPointLight) o).isX3dOn ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((X3DPointLight) o).intensityFactor = (float) value;
					return;
				case 1:
					((X3DPointLight) o).x3dAmbientIntensity = (float) value;
					return;
				case 4:
					((X3DPointLight) o).x3dIntensity = (float) value;
					return;
				case 7:
					((X3DPointLight) o).x3dRadius = (float) value;
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
					return ((X3DPointLight) o).getIntensityFactor ();
				case 1:
					return ((X3DPointLight) o).getX3dAmbientIntensity ();
				case 4:
					return ((X3DPointLight) o).getX3dIntensity ();
				case 7:
					return ((X3DPointLight) o).getX3dRadius ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 2:
					((X3DPointLight) o).x3dAttenuation = (Tuple3f) value;
					return;
				case 3:
					((X3DPointLight) o).x3dColor = (Tuple3f) value;
					return;
				case 5:
					((X3DPointLight) o).x3dLocation = (Tuple3f) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 2:
					return ((X3DPointLight) o).getX3dAttenuation ();
				case 3:
					return ((X3DPointLight) o).getX3dColor ();
				case 5:
					return ((X3DPointLight) o).getX3dLocation ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DPointLight ());
		$TYPE.addManagedField (intensityFactor$FIELD = new _Field ("intensityFactor", _Field.PRIVATE  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (x3dAmbientIntensity$FIELD = new _Field ("x3dAmbientIntensity", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (x3dAttenuation$FIELD = new _Field ("x3dAttenuation", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 2));
		$TYPE.addManagedField (x3dColor$FIELD = new _Field ("x3dColor", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 3));
		$TYPE.addManagedField (x3dIntensity$FIELD = new _Field ("x3dIntensity", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (x3dLocation$FIELD = new _Field ("x3dLocation", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 5));
		$TYPE.addManagedField (x3dOn$FIELD = new _Field ("x3dOn", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 6));
		$TYPE.addManagedField (x3dRadius$FIELD = new _Field ("x3dRadius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 7));
		$TYPE.declareFieldAttribute (x3dAmbientIntensity$FIELD, Attributes.X3DAMBIENT_INTENSITY);
		$TYPE.declareFieldAttribute (x3dOn$FIELD, Attributes.X3DON);
		$TYPE.declareFieldAttribute (x3dRadius$FIELD, Attributes.X3DRADIUS);
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
		return new X3DPointLight ();
	}

	public boolean isX3dOn ()
	{
		return x3dOn;
	}

	public void setX3dOn (boolean value)
	{
		this.x3dOn = (boolean) value;
	}

	public float getIntensityFactor ()
	{
		return intensityFactor;
	}

	public void setIntensityFactor (float value)
	{
		this.intensityFactor = (float) value;
	}

	public float getX3dAmbientIntensity ()
	{
		return x3dAmbientIntensity;
	}

	public void setX3dAmbientIntensity (float value)
	{
		this.x3dAmbientIntensity = (float) value;
	}

	public float getX3dIntensity ()
	{
		return x3dIntensity;
	}

	public void setX3dIntensity (float value)
	{
		this.x3dIntensity = (float) value;
	}

	public float getX3dRadius ()
	{
		return x3dRadius;
	}

	public void setX3dRadius (float value)
	{
		this.x3dRadius = (float) value;
	}

	public Tuple3f getX3dAttenuation ()
	{
		return x3dAttenuation;
	}

	public void setX3dAttenuation (Tuple3f value)
	{
		x3dAttenuation$FIELD.setObject (this, value);
	}

	public Tuple3f getX3dColor ()
	{
		return x3dColor;
	}

	public void setX3dColor (Tuple3f value)
	{
		x3dColor$FIELD.setObject (this, value);
	}

	public Tuple3f getX3dLocation ()
	{
		return x3dLocation;
	}

	public void setX3dLocation (Tuple3f value)
	{
		x3dLocation$FIELD.setObject (this, value);
	}

//enh:end
}
