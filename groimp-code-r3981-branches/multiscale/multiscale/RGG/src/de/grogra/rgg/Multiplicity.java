package de.grogra.rgg;

public final class Multiplicity extends de.grogra.graph.impl.Node
{
	protected int multiplicitySrcMin;
	protected int multiplicitySrcMax;
	protected int multiplicityTgtMin;
	protected int multiplicityTgtMax;
	
	private static final String MULTIPLICITY_ANY = "*";

	public Multiplicity()
	{
		multiplicitySrcMin = 0;
		multiplicitySrcMax = 1;
		multiplicityTgtMin = 0;
		multiplicityTgtMin = -1;
	}
	
	public Multiplicity(String srcMin, String srcMax, String tgtMin, String tgtMax)
	{
		//yong 23 mar 2012 - TODO: set contraints between min and max values, etc.
		setMultiplicitySrcMin(srcMin);
		setMultiplicitySrcMax(srcMax);
		setMultiplicityTgtMin(tgtMin);
		setMultiplicityTgtMax(tgtMax);
	}
	
	public void setMultiplicitySrcMin(int srcMin)
	{
		multiplicitySrcMin = srcMin;
	}
	
	public void setMultiplicitySrcMax(int srcMax)
	{
		multiplicitySrcMax = srcMax;
	}
	
	public void setMultiplicityTgtMin(int tgtMin)
	{
		multiplicityTgtMin = tgtMin;
	}
	
	public void setMultiplicityTgtMax(int tgtMax)
	{
		multiplicityTgtMax = tgtMax;
	}
	
	public int toIntValue(String value)
	{
		int intValue = -2;
		if(value.equals(MULTIPLICITY_ANY))
			intValue = -1;
		else
			intValue = Integer.parseInt((String)value);
		
		return intValue;
	}
	
	public void setMultiplicitySrcMin(String value)
	{
		multiplicitySrcMin = toIntValue(value);
	}
	
	public void setMultiplicitySrcMax(String value)
	{
		multiplicitySrcMax = toIntValue(value);
	}
	
	public void setMultiplicityTgtMin(String value)
	{
		multiplicityTgtMin = toIntValue(value);
	}
	
	public void setMultiplicityTgtMax(String value)
	{
		multiplicityTgtMax = toIntValue(value);
	}
	
	public int getMultiplicitySrcMin()
	{
		return multiplicitySrcMin;
	}
	
	public int getMultiplicitySrcMax()
	{
		return multiplicitySrcMax;
	}
	
	public int getMultiplicityTgtMin()
	{
		return multiplicityTgtMin;
	}
	
	public int getMultiplicityTgtMax()
	{
		return multiplicityTgtMax;
	}
	
	public static final NType $TYPE;
	public static final NType.Field multiplicitySrcMin$FIELD;
	public static final NType.Field multiplicitySrcMax$FIELD;
	public static final NType.Field multiplicityTgtMin$FIELD;
	public static final NType.Field multiplicityTgtMax$FIELD;
	
	static
	{
		$TYPE = new NType (new Multiplicity ());
		$TYPE.addManagedField (multiplicitySrcMin$FIELD = new _Field ("multiplicitySrcMin", 0 | _Field.SCO, de.grogra.reflect.Type.STRING, null, 0));
		$TYPE.addManagedField (multiplicitySrcMax$FIELD = new _Field ("multiplicitySrcMax", 0 | _Field.SCO, de.grogra.reflect.Type.STRING, null, 1));
		$TYPE.addManagedField (multiplicityTgtMin$FIELD = new _Field ("multiplicityTgtMin", 0 | _Field.SCO, de.grogra.reflect.Type.STRING, null, 2));
		$TYPE.addManagedField (multiplicityTgtMax$FIELD = new _Field ("multiplicityTgtMax", 0 | _Field.SCO, de.grogra.reflect.Type.STRING, null, 3));
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
		return new Multiplicity ();
	}
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Multiplicity.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setObjectImpl (Object object, Object value)
		{
			int intValue = -2;
			if(value.equals(MULTIPLICITY_ANY))
				intValue = -1;
			else
				intValue = Integer.parseInt((String)value);
			
			switch (id)
			{
				case 0:
					((Multiplicity) object).setMultiplicitySrcMin(intValue);
					return;
				case 1:
					((Multiplicity) object).setMultiplicitySrcMax(intValue);
					return;
				case 2:
					((Multiplicity) object).setMultiplicityTgtMin(intValue);
					return;
				case 3:
					((Multiplicity) object).setMultiplicityTgtMax(intValue);
					return;
			}
			super.setObject(object, value);
		}

		@Override
		public Object getObject (Object object)
		{
			switch (id)
			{
				case 0:
					if(((Multiplicity) object).getMultiplicitySrcMin()==-1)
						return "*";
					else
						return new Integer(((Multiplicity) object).getMultiplicitySrcMin()).toString();
				case 1:
					if(((Multiplicity) object).getMultiplicitySrcMax()==-1)
						return "*";
					else
						return new Integer(((Multiplicity) object).getMultiplicitySrcMax()).toString();
				case 2:
					if(((Multiplicity) object).getMultiplicityTgtMin()==-1)
						return "*";
					else
						return new Integer(((Multiplicity) object).getMultiplicityTgtMin()).toString();
				case 3:
					if(((Multiplicity) object).getMultiplicityTgtMax()==-1)
						return "*";
					else
						return new Integer(((Multiplicity) object).getMultiplicityTgtMax()).toString();
			}
			return super.getObject(object);
		}
	}
}
