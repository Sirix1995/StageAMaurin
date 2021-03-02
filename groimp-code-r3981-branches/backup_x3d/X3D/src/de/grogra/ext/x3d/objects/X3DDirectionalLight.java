package de.grogra.ext.x3d.objects;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.Util;
import de.grogra.ext.x3d.interfaces.Value;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.objects.LightNode;

/**
 * GroIMP node class for a x3d directional light.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DDirectionalLight extends LightNode implements Value {

	private float intensityFactor = 10f;
	//enh:field hidden getter setter
	
	private Vector3d upVec = new Vector3d(0, 0, 1);
	//enh:field hidden getter setter
	
	protected float x3dAmbientIntensity = 0;
	//enh:field attr=Attributes.X3DAMBIENT_INTENSITY getter setter
	
	protected Tuple3f x3dColor = new Color3f(1, 1, 1);
	//enh:field hidden getter setter
	
	protected Tuple3f x3dDirection = new Vector3f(0, 0, -1);
	//enh:field hidden getter setter
	
	protected float x3dIntensity = 1;
	//enh:field hidden getter setter
	
	protected boolean x3dOn = true;
	//enh:field attr=Attributes.X3DON getter setter

	/**
	 * Constructor.
	 */
	public X3DDirectionalLight() {
		super();
		this.setValues();
	}
	
	public void setValues() {
		DirectionalLight l = new DirectionalLight();
		light = l;
		
		// set color
		l.getColor().set(x3dColor.x, x3dColor.y, x3dColor.z);
		
		// set direction
		Vector3d dirVec = new Vector3d(x3dDirection.x, -x3dDirection.z, x3dDirection.y);
		this.setTransform(Util.vectorsToTransMatrix(upVec, dirVec));
		
		// set intensity
		l.setPowerDensity(intensityFactor * x3dIntensity);
		
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field intensityFactor$FIELD;
	public static final NType.Field upVec$FIELD;
	public static final NType.Field x3dAmbientIntensity$FIELD;
	public static final NType.Field x3dColor$FIELD;
	public static final NType.Field x3dDirection$FIELD;
	public static final NType.Field x3dIntensity$FIELD;
	public static final NType.Field x3dOn$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DDirectionalLight.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 6:
					((X3DDirectionalLight) o).x3dOn = (boolean) value;
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
					return ((X3DDirectionalLight) o).isX3dOn ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((X3DDirectionalLight) o).intensityFactor = (float) value;
					return;
				case 2:
					((X3DDirectionalLight) o).x3dAmbientIntensity = (float) value;
					return;
				case 5:
					((X3DDirectionalLight) o).x3dIntensity = (float) value;
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
					return ((X3DDirectionalLight) o).getIntensityFactor ();
				case 2:
					return ((X3DDirectionalLight) o).getX3dAmbientIntensity ();
				case 5:
					return ((X3DDirectionalLight) o).getX3dIntensity ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((X3DDirectionalLight) o).upVec = (Vector3d) value;
					return;
				case 3:
					((X3DDirectionalLight) o).x3dColor = (Tuple3f) value;
					return;
				case 4:
					((X3DDirectionalLight) o).x3dDirection = (Tuple3f) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((X3DDirectionalLight) o).getUpVec ();
				case 3:
					return ((X3DDirectionalLight) o).getX3dColor ();
				case 4:
					return ((X3DDirectionalLight) o).getX3dDirection ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DDirectionalLight ());
		$TYPE.addManagedField (intensityFactor$FIELD = new _Field ("intensityFactor", _Field.PRIVATE  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (upVec$FIELD = new _Field ("upVec", _Field.PRIVATE  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Vector3d.class), null, 1));
		$TYPE.addManagedField (x3dAmbientIntensity$FIELD = new _Field ("x3dAmbientIntensity", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (x3dColor$FIELD = new _Field ("x3dColor", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 3));
		$TYPE.addManagedField (x3dDirection$FIELD = new _Field ("x3dDirection", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.ClassAdapter.wrap (Tuple3f.class), null, 4));
		$TYPE.addManagedField (x3dIntensity$FIELD = new _Field ("x3dIntensity", _Field.PROTECTED  | _Field.SCO | _Field.HIDDEN, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (x3dOn$FIELD = new _Field ("x3dOn", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 6));
		$TYPE.declareFieldAttribute (x3dAmbientIntensity$FIELD, Attributes.X3DAMBIENT_INTENSITY);
		$TYPE.declareFieldAttribute (x3dOn$FIELD, Attributes.X3DON);
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
		return new X3DDirectionalLight ();
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

	public Vector3d getUpVec ()
	{
		return upVec;
	}

	public void setUpVec (Vector3d value)
	{
		upVec$FIELD.setObject (this, value);
	}

	public Tuple3f getX3dColor ()
	{
		return x3dColor;
	}

	public void setX3dColor (Tuple3f value)
	{
		x3dColor$FIELD.setObject (this, value);
	}

	public Tuple3f getX3dDirection ()
	{
		return x3dDirection;
	}

	public void setX3dDirection (Tuple3f value)
	{
		x3dDirection$FIELD.setObject (this, value);
	}

//enh:end
}
