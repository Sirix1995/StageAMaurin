package de.grogra.grogra;

/**
 * Class is used to realize the LeafObject-command in the DTD-syntax
 * @author Jan Dérer
 */
public class LeafObject extends de.grogra.graph.impl.Node {

	/**
	 * Generated with Eclipse for the serialization
	 */
	private static final long serialVersionUID = -6440569319033690053L;
	
	/**
	 * Represents the rgg-filename for the LeafObject
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
	private LeafObject() {
		this(null, null, null, 0);
	}

	/**
	 * Standard-constructor to create a new object of that class 
	 * @param file Represents the rgg-filename for the LeafObject
	 * @param startaxiom Represents the startaxiom for derivation
	 * @param methodForDerivation Contains the methodname for Derivation
	 * @param numberOfDerivationsteps Contains the number of Derivationsteps
	 */
	public LeafObject(String file, String startaxiom, String methodForDerivation, int numberOfDerivationsteps) {
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
			super (LeafObject.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 3:
					((LeafObject) o).numberOfDerivationsteps = (int) value;
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
					return ((LeafObject) o).numberOfDerivationsteps;
			}
			return super.getInt (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((LeafObject) o).file = (String) value;
					return;
				case 1:
					((LeafObject) o).startaxiom = (String) value;
					return;
				case 2:
					((LeafObject) o).methodForDerivation = (String) value;
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
					return ((LeafObject) o).file;
				case 1:
					return ((LeafObject) o).startaxiom;
				case 2:
					return ((LeafObject) o).methodForDerivation;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new LeafObject ());
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
		return new LeafObject ();
	}

//enh:end
	
}
