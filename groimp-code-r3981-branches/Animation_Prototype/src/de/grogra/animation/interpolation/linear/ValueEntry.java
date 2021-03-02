package de.grogra.animation.interpolation.linear;

import java.io.Serializable;

import de.grogra.persistence.*;

public class ValueEntry extends ShareableBase implements Serializable {
	
	//enh:sco SCOType

	private Object value = null;
	// enh:field getter setter
	
	public ValueEntry() {
	}

	@Override
	public String toString() {
		return value.toString();
	}
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field value$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ValueEntry representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ValueEntry) o).value = (Object) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ValueEntry) o).getValue ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ValueEntry ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ValueEntry.class);
		value$FIELD = Type._addManagedField ($TYPE, "value", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Object.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public Object getValue ()
	{
		return value;
	}

	public void setValue (Object value)
	{
		value$FIELD.setObject (this, value);
	}

//enh:end
		
}
