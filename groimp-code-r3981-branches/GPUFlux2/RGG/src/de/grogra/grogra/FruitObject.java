package de.grogra.grogra;

/**
 * Class is used to realize the FruitObject-command in the DTD-syntax
 * @author Jan Dérer
 */
public class FruitObject extends de.grogra.graph.impl.Node {

	/**
	 * Generated with Eclipse for the serialization
	 */
	private static final long serialVersionUID = 2813933456442999024L;

	/**
	 * Represents the rgg-filename for the FruitObject
	 */
	public String file;
	//enh:field
	
	/**
	 * Represents the startaxiom for derivation 
	 */
	public String startaxiom;
	//enh:field
	
	/**
	 * Contains the methodname for Derivation
	 */
	public String methodForDerivation;
	//enh:field
	
	/**
	 * Contains the number of Derivationsteps
	 */
	public int numberOfDerivationsteps;
	//enh:field
	
	/**
	 * It's not allowed to construct an object by that constructor
	 */
	private FruitObject() {
		this(null, null, null, 0);
	}

	/**
	 * Standard-constructor to create a new object of that class 
	 * @param file Represents the rgg-filename for the FruitObject
	 * @param startaxiom Represents the startaxiom for derivation
	 * @param methodForDerivation Contains the methodname for Derivation
	 * @param numberOfDerivationsteps Contains the number of Derivationsteps
	 */
	public FruitObject(String file, String startaxiom, String methodForDerivation, int numberOfDerivationsteps) {
		super();
		this.file = file;
		this.startaxiom = startaxiom;
		this.methodForDerivation = methodForDerivation;
		this.numberOfDerivationsteps = numberOfDerivationsteps;
	}
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field file$FIELD;
	public static final NType.Field startaxiom$FIELD;
	public static final NType.Field methodForDerivation$FIELD;
	public static final NType.Field numberOfDerivationsteps$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FruitObject.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 3:
					((FruitObject) o).numberOfDerivationsteps = value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 3:
					return ((FruitObject) o).numberOfDerivationsteps;
			}
			return super.getInt (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((FruitObject) o).file = (String) value;
					return;
				case 1:
					((FruitObject) o).startaxiom = (String) value;
					return;
				case 2:
					((FruitObject) o).methodForDerivation = (String) value;
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
					return ((FruitObject) o).file;
				case 1:
					return ((FruitObject) o).startaxiom;
				case 2:
					return ((FruitObject) o).methodForDerivation;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new FruitObject ());
		$TYPE.addManagedField (file$FIELD = new _Field ("file", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (startaxiom$FIELD = new _Field ("startaxiom", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.addManagedField (methodForDerivation$FIELD = new _Field ("methodForDerivation", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 2));
		$TYPE.addManagedField (numberOfDerivationsteps$FIELD = new _Field ("numberOfDerivationsteps", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 3));
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
		return new FruitObject ();
	}

//enh:end
	
}
