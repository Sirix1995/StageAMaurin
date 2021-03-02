package de.grogra.animation.handler.simpleproperty;

import java.util.Set;
import de.grogra.animation.interpolation.Interpolation;
import de.grogra.animation.interpolation.linear.LinearInterpolation;
import de.grogra.persistence.*;

public class AnimTimeMap extends ShareableBase {

	//enh:sco SCOType
	
	private Interpolation interpolation;
	// enh:field
	
	public AnimTimeMap() {
	}
	
	public void setInterpolationType(de.grogra.reflect.Type<?> type) {
		interpolation = new LinearInterpolation();
		interpolation.setInterpolationType(type);
	}
	
	public boolean putValue(int time, Object value) {
		return interpolation.putValue(time, value);
	}
	
	public void changeValue(int oldTime, int newTime, Object value) {
		interpolation.changeValue(oldTime, newTime, value);
	}
	
	public Object getValue(double time) {
		return interpolation.getValue(time);
	}
	
	public void getTimes(Set<Integer> times) {
		interpolation.getTimes(times);
	}
	
	@Override
	public String toString() {
		return interpolation.toString();
	}
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field interpolation$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AnimTimeMap representative, de.grogra.persistence.SCOType supertype)
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
					((AnimTimeMap) o).interpolation = (Interpolation) value;
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
					return ((AnimTimeMap) o).interpolation;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AnimTimeMap ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AnimTimeMap.class);
		interpolation$FIELD = Type._addManagedField ($TYPE, "interpolation", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Interpolation.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
}
