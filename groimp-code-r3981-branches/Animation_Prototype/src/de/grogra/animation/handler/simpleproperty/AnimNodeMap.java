package de.grogra.animation.handler.simpleproperty;

import java.util.HashMap;
import java.util.Set;
import de.grogra.persistence.*;

public class AnimNodeMap extends ShareableBase {

	//enh:sco SCOType
	
	//private HashMap<Object, AnimPropertyMap> nodeMap;
	private HashMap<Object,AnimPropertyMap> nodeMap;
	// enh:field

	public AnimNodeMap() {
		nodeMap = new HashMap<Object,AnimPropertyMap>();
	}

	public boolean putValue(int time, Object node, PersistenceField property,
			Object value) {
		// exists an entry for node ?
		AnimPropertyMap propMap;
		if (nodeMap.containsKey(node)) {
			propMap = nodeMap.get(node);
		} else {
			propMap = new AnimPropertyMap();
			nodeMap.put(node, propMap);
		}
		return propMap.putValue(time, property, value);
	}
	
	public void changeValue(int oldTime, int newTime, Object node,
			PersistenceField property, Object value) {
		AnimPropertyMap propMap = nodeMap.get(node);
		propMap.changeValue(oldTime, newTime, property, value);
	}
	
	public Object getValue(double time, Object node, PersistenceField property) {
		// exists an entry for node ?
		AnimPropertyMap propMap;
		if (nodeMap.containsKey(node)) {
			propMap = nodeMap.get(node);
			return propMap.getValue(time, property);
		}
		return null;
	}

	public void getTimes(Object node, Set<Integer> times) {
		if (nodeMap.containsKey(node)) {
			AnimPropertyMap propMap = nodeMap.get(node);
			propMap.getTimes(times);
		}
	}
	
	public void getTimesForProperty(Object node, PersistenceField property, Set<Integer> times) {
		if (nodeMap.containsKey(node)) {
			AnimPropertyMap propMap = nodeMap.get(node);
			propMap.getTimesForProperty(property, times);
		}
	}
	
	public void update(int time, Transaction t) {
		Set<Object> objectSet = nodeMap.keySet();
		for (final Object node : objectSet) {
			// iterate over objects
			AnimPropertyMap propMap = nodeMap.get(node);
			propMap.update(time, node, t);
		}
	}
	
	public void clearValues() {
		nodeMap.clear();
	}
	
	@Override
	public String toString() {
		return nodeMap.toString();
	}
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field nodeMap$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AnimNodeMap representative, de.grogra.persistence.SCOType supertype)
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
					((AnimNodeMap) o).nodeMap = (HashMap<Object,AnimPropertyMap>) value;
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
					return ((AnimNodeMap) o).nodeMap;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AnimNodeMap ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AnimNodeMap.class);
		nodeMap$FIELD = Type._addManagedField ($TYPE, "nodeMap", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (HashMap.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

}
