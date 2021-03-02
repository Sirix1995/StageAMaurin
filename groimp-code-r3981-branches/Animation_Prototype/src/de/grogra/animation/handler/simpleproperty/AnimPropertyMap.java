package de.grogra.animation.handler.simpleproperty;

import java.util.HashMap;
import java.util.Set;
import de.grogra.persistence.*;
import de.grogra.reflect.Type;

public class AnimPropertyMap extends ShareableBase {

	//enh:sco SCOType
	
	//private HashMap<PersistenceField, AnimTimeMap> propertyMap;
	private HashMap<PersistenceField,AnimTimeMap> propertyMap;
	// enh:field
	
	public AnimPropertyMap() {
		propertyMap = new HashMap<PersistenceField,AnimTimeMap>();
	}
	
	/**
	 * This method is used to find a property in propertyMap, which is a
	 * different java object but references the same property
	 * @param property
	 * @return
	 */
	private PersistenceField getProperty(PersistenceField property) {
		Set<PersistenceField> fields = propertyMap.keySet();
		for (PersistenceField field : fields) {
			if (field.equals(property))
				return field;
		}
		return null;
	}
	
	public boolean putValue(int time, PersistenceField property, Object value) {
		// exists an entry for property ?
		PersistenceField oldProperty = getProperty(property);
		AnimTimeMap timeMap;
		if (oldProperty != null) {
			timeMap = propertyMap.get(oldProperty);
		}
		else {
			timeMap = new AnimTimeMap();
			timeMap.setInterpolationType(property.getType());
			propertyMap.put(property, timeMap);
		}
		return timeMap.putValue(time, value);
	}
	
	public void changeValue(int oldTime, int newTime, PersistenceField property, Object value) {
		PersistenceField oldProperty = getProperty(property);
		AnimTimeMap timeMap = propertyMap.get(oldProperty);
		timeMap.changeValue(oldTime, newTime, value);
	}
	
	public Object getValue(double time, PersistenceField property) {
		Object result = null;
		PersistenceField oldProperty = getProperty(property);
		if (oldProperty != null) {
			// we have an entry for the node+property
			AnimTimeMap timeMap = propertyMap.get(oldProperty);
			result = timeMap.getValue(time);
		}
		return result;
	}
	
	public void getTimes(Set<Integer> times) {
		for (AnimTimeMap timeMap : propertyMap.values()) {
			timeMap.getTimes(times);
		}
	}
	
	public void getTimesForProperty(PersistenceField property, Set<Integer> times) {
		PersistenceField oldProperty = getProperty(property);
		if (oldProperty != null) {
			AnimTimeMap timeMap = propertyMap.get(oldProperty);
			timeMap.getTimes(times);
		}
	}
	
	public void update(int time, Object node, Transaction t) {
		Set<PersistenceField> propSet = propertyMap.keySet();
		for (final PersistenceField prop : propSet) {
			// iterate over properties
			Object value = getValue(time, prop);
			if (value == null)
				// do not update if no saved value is present
				return;
			de.grogra.reflect.Type<?> type = prop.getType();
			if (type.equals(de.grogra.reflect.Type.BOOLEAN))
				prop.setBoolean(node, null, (Boolean) value, t);
			else if (type.equals(de.grogra.reflect.Type.BYTE))
				prop.setByte(node, null, (Byte) value, t);
			else if (type.equals(de.grogra.reflect.Type.SHORT))
				prop.setShort(node, null, (Short) value, t);
			else if (type.equals(de.grogra.reflect.Type.CHAR))
				prop.setChar(node, null, (Character) value, t);
			else if (type.equals(de.grogra.reflect.Type.INT))
				prop.setInt(node, null, (Integer) value, t);
			else if (type.equals(de.grogra.reflect.Type.LONG))
				prop.setLong(node, null, (Long) value, t);
			else if (type.equals(de.grogra.reflect.Type.FLOAT))
				prop.setFloat(node, null, (Float) value, t);
			else if (type.equals(de.grogra.reflect.Type.DOUBLE))
				prop.setDouble(node, null, (Double) value, t);
			else
				prop.setObject(node, null, value, t);
		}
	}
	
	@Override
	public String toString() {
		return propertyMap.toString();
	}
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field propertyMap$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AnimPropertyMap representative, de.grogra.persistence.SCOType supertype)
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
					((AnimPropertyMap) o).propertyMap = (HashMap<PersistenceField,AnimTimeMap>) value;
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
					return ((AnimPropertyMap) o).propertyMap;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AnimPropertyMap ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AnimPropertyMap.class);
		propertyMap$FIELD = Type._addManagedField ($TYPE, "propertyMap", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (HashMap.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
	
}
